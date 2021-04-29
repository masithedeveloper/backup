/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 *
 */
package com.barclays.absa.banking.registration;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccessPrivileges;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.ErrorResponseObject;
import com.barclays.absa.banking.boundary.model.RegisterProfileDetail;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.databinding.Activity2faRegistrationResultBinding;
import com.barclays.absa.banking.deviceLinking.ui.AccountLoginActivity;
import com.barclays.absa.banking.deviceLinking.ui.LinkingPasswordValidationActivity;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.PermissionFacade;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.home.ui.HomeContainerActivity;
import com.barclays.absa.banking.linking.ui.LinkingActivity;
import com.barclays.absa.banking.login.services.LoginInteractor;
import com.barclays.absa.banking.preLogin.views.RegisterResultView;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity;
import com.barclays.absa.banking.registration.services.dto.RegisterAOLProfileResponse;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.PermissionHelper;
import com.barclays.absa.utils.SharedPreferenceService;

import styleguide.utils.IdentityDocumentValidationUtil;
import styleguide.utils.extensions.StringExtensions;

public class RegistrationResultActivity extends BaseActivity implements RegisterResultView {

    private RegisterAOLProfileResponse registrationResultObj;
    private RegisterProfileDetail registerProfileDetail;
    private Bundle bundle;
    private Activity2faRegistrationResultBinding binding;
    private LoginInteractor loginInteractor;
    public final String SECONDARY_CARD_REGISTRATION_FAILURE_CODE = "H0823 062";
    public static final String ALREADY_REGISTERED_FAILURE_CODE = "H0600 H0222 061";
    private ResponseObject responseObject;

    private final ExtendedResponseListener<SecureHomePageObject> loginResponseListener = new ExtendedResponseListener<SecureHomePageObject>() {
        @Override
        public void onSuccess(final SecureHomePageObject homeObject) {
            dismissProgressDialog();
            if (homeObject.getResponseId().equals(BMBConstants.RESPONSE_ID_DEVICE_NOT_LINKED)) {
                if (AccessPrivileges.getInstance().isOperator()) {
                    BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                            .title(getString(R.string.main_user_title))
                            .message(getString(R.string.main_user_text))
                            .build());
                } else {

                    if (passwordNotSet(homeObject)) {
                        Intent createPassIntent = new Intent(RegistrationResultActivity.this, RegisterCreatePasswordActivity.class);
                        createPassIntent.putExtra(BMBConstants.NAVIGATED_FROM, "DS2");
                        if (CustomerProfileObject.getInstance().getReachedDeviceMaxLimit())
                            createPassIntent.putExtra(BMBConstants.DEVICE_DECOUPLE_OBJ, homeObject.getSerializableDeviceList());
                        startActivity(createPassIntent);
                    } else {
                        if ("0".equalsIgnoreCase(homeObject.getPasswordLength()) && homeObject.getPasswordDigits() == null) {
                            CommonUtils.detailNeedsToBeUpdated(RegistrationResultActivity.this);
                        } else if (!passwordNotSet(homeObject)) {
                            Intent linkIntent = new Intent(RegistrationResultActivity.this, LinkingPasswordValidationActivity.class);
                            linkIntent.putExtra(AppConstants.RESULT, homeObject);
                            startActivity(linkIntent);
                        } else {
                            Intent homeScreenIntent = new Intent(RegistrationResultActivity.this, HomeContainerActivity.class);
                            homeScreenIntent.putExtra(AppConstants.RESULT, homeObject);
                            startActivity(homeScreenIntent);
                        }
                    }
                }
            } else {
                getAppCacheService().setSecureHomePageObject(homeObject);
                if (shouldAllowIdentificationFlow()) {
                    Intent intent = new Intent(RegistrationResultActivity.this, LinkingActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else if (homeObject.getCustomerProfile() != null && "true".equalsIgnoreCase(homeObject.getCustomerProfile().getLimitsNotSet())) {
                    Intent homeScreenIntent = new Intent(RegistrationResultActivity.this, HomeContainerActivity.class);
                    startActivity(homeScreenIntent);
                } else {
                    goToLoginScreen();
                }
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            dismissProgressDialog();
            Intent intent = new Intent(RegistrationResultActivity.this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    };

    private boolean passwordNotSet(SecureHomePageObject secureHomePageObject) {
        // Because isAuthPassNotRegistered comes back as null , this is the solution that works
        return secureHomePageObject.getPasswordDigits() == null || secureHomePageObject.getPasswordDigits().isEmpty();
    }

    private void goToLoginScreen() {
        if (shouldAllowIdentificationFlow()) {
            Intent intent = new Intent(RegistrationResultActivity.this, LinkingActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (registrationResultObj == null) {
            BaseAlertDialog.INSTANCE.showGenericErrorDialog((dialog, which) -> logoutAndGoToStartScreen());
        } else {
            String onlinePin = bundle.getString(BMBConstants.ONLINE_PIN);
            Intent accountLoginIntent = new Intent(RegistrationResultActivity.this, AccountLoginActivity.class);
            accountLoginIntent.putExtra(AccountLoginActivity.SOURCE_ACTIVITY, BMBConstants.REGISTER_CONST);
            accountLoginIntent.putExtra(AccountLoginActivity.ACCESS_ACCOUNT_KEY, registrationResultObj.getAccessAccount());
            accountLoginIntent.putExtra(AccountLoginActivity.ACCESS_PIN_KEY, onlinePin);
            accountLoginIntent.putExtra(AccountLoginActivity.USER_NO_KEY, registrationResultObj.getUserNumber());
            startActivity(accountLoginIntent);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_2fa_registration_result, null, false);
        setContentView(binding.getRoot());

        setToolBarNoBackButton(R.string.register);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            responseObject = (ResponseObject) extras.getSerializable(AppConstants.RESULT);
            if (responseObject == null) {
                showFailureScreen();
            }
        }
        if (responseObject instanceof RegisterProfileDetail) {
            registerProfileDetail = (RegisterProfileDetail) responseObject;
        } else if (responseObject instanceof RegisterAOLProfileResponse) {
            registrationResultObj = (RegisterAOLProfileResponse) responseObject;
        } else if (responseObject instanceof ErrorResponseObject) {
            ErrorResponseObject error = (ErrorResponseObject) responseObject;

            if (error.getErrorMessage() != null) {
                if (error.getErrorMessage().contains(ALREADY_REGISTERED_FAILURE_CODE)) {
                    showAlreadyRegisteredFailureScreen();
                } else if (error.getErrorMessage().contains(SECONDARY_CARD_REGISTRATION_FAILURE_CODE)) {
                    startActivity(IntentFactory.getSecondaryCardRegistrationFailureScreen(this));
                }
                return;
            } else {
                showFailureScreen();
            }
            BMBLogger.d("x-response", "ErrorMessage " + error.getErrorMessage());
            BMBLogger.d("x-response", "ResponseMessage" + error.getResponseMessage());
        }
        initViews();
        loginInteractor = new LoginInteractor();
        loginResponseListener.setView(this);
    }

    private void initViews() {
        bundle = getIntent().getExtras();
        binding.loginButton.setOnClickListener(view -> {
            if (shouldAllowIdentificationFlow()) {
                Intent intent = new Intent(RegistrationResultActivity.this, LinkingActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                goToLoginScreen();
            }
        });

        binding.accountLimitsActionButtonView.setOnClickListener(view -> showAccountLimitsScreen());

        boolean isPartiallyRegistered = SharedPreferenceService.INSTANCE.isPartialRegistration();

        if (registrationResultObj == null && registerProfileDetail == null) {
            showFailureScreen();
        } else {
            if (isPartiallyRegistered) {
                binding.congratulationsText.setText(getString(R.string.congratulations_register_partial_text));
                showPartiallyRegisteredSuccessScreen();
            } else {
                if (registrationResultObj != null) {
                    if (registrationResultObj.getSuccess()) {
                        showRegisteredSuccessScreen();
                    } else {
                        final String ALREADY_REGISTERED_FAILURE_CODE = "H0600 H0222 061";
                        if (registrationResultObj.getFailureMessage() != null && registrationResultObj.getFailureMessage().contains(ALREADY_REGISTERED_FAILURE_CODE)) {
                            showAlreadyRegisteredFailureScreen();
                        } else {
                            showFailureScreen();
                        }
                    }
                } else {
                    showFailureScreen();
                }
            }
        }
    }

    private void performLogin() {
        if (shouldAllowIdentificationFlow()) {
            Intent intent = new Intent(RegistrationResultActivity.this, LinkingActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (registrationResultObj != null) {
            getAppCacheService().setAccessAccountLogin(true);
            loginInteractor.performAccessAccountLogin(registrationResultObj.getAccessAccount(), bundle.getString(BMBConstants.ONLINE_PIN), registrationResultObj.getUserNumber(), loginResponseListener);
        } else {
            showMessageError(getString(R.string.technical_difficulty));
        }
    }

    @Override
    public void showRegisteredSuccessScreen() {
        setRegisteredDetails();
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.REGISTER_CONST, BMBConstants.REGISTER_SUCCESS_CONST, BMBConstants.TRUE_CONST);
    }

    @Override
    public void showPartiallyRegisteredSuccessScreen() {
        setRegisteredDetails();
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.REGISTER_CONST, BMBConstants.PARTIAL_REGISTER_SUCCESS_CONST, BMBConstants.TRUE_CONST);
    }

    private void setRegisteredDetails() {
        if (registrationResultObj != null) {
            binding.accountNumberView.setContentText(StringExtensions.toFormattedAccountNumber(registrationResultObj.getAccessAccount()));
            binding.userNumberView.setContentText(registrationResultObj.getUserNumber());
        } else if (registerProfileDetail != null) {
            binding.accountNumberView.setContentText(StringExtensions.toFormattedAccountNumber(registerProfileDetail.getSelectedAccessAccountNo()));
            binding.userNumberView.setContentText(registerProfileDetail.getUserNumber());
        }

        String surePhrase = bundle.getString(BMBConstants.SURE_PHRASE);

        if (!TextUtils.isEmpty(surePhrase)) {
            binding.surephraseView.setContentText(surePhrase);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            if (requestCode == PermissionHelper.PermissionCode.ACCESS_DEVICE_STATE.value) {
                int permissionStatus = grantResults[0];
                switch (permissionStatus) {
                    case PackageManager.PERMISSION_GRANTED:
                        performLogin();
                        break;
                    case PackageManager.PERMISSION_DENIED:
                        PermissionFacade.requestDeviceStatePermission(this, this::performLogin);
                        break;
                }
            }
        }
    }

    @Override
    public void showFailureScreen() {
        int resultTitle = R.string.register_result_unsuccess_msg;
        int resultMessage = R.string.register_result_error_msg_sub_title;
        if (getAppCacheService().isPasswordResetFlow()) {
            resultTitle = R.string.password_reset_failed;
            resultMessage = R.string.password_reset_failed_explanation;
        }
        Intent failureIntent = IntentFactory.getPreLoginFailureScreen(RegistrationResultActivity.this, resultTitle, resultMessage);
        startActivity(failureIntent);
    }

    public void showAlreadyRegisteredFailureScreen() {
        Intent failureIntent = IntentFactory.getPreLoginFailureScreen(RegistrationResultActivity.this, R.string.register_result_already_registered_title, R.string.register_result_already_registered_message);
        startActivity(failureIntent);
    }

    public void showAccountLimitsScreen() {
        Intent showAccountLimitsScreenIntent = new Intent(this, RegisterAccountLimitsActivity.class);
        showAccountLimitsScreenIntent.putExtra(getString(R.string.register_profile_detail_obj), registrationResultObj);
        startActivity(showAccountLimitsScreenIntent);
    }

    @Override
    public void onBackPressed() {
        CommonUtils.callWelcomeActivity(this);
    }

    private Boolean shouldAllowIdentificationFlow() {
        return BuildConfig.TOGGLE_DEF_BIOMETRIC_VERIFICATION_ENABLED && FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles().getBiometricVerification() == FeatureSwitchingStates.ACTIVE.getKey() && IdentityDocumentValidationUtil.INSTANCE.isValidIdNumber(getAppCacheService().getCustomerIdNumber());
    }
}
