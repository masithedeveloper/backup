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
package com.barclays.absa.banking.deviceLinking.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.boundary.shared.dto.SecondFactorState;
import com.barclays.absa.banking.databinding.AccountLoginActivityBinding;
import com.barclays.absa.banking.deviceLinking.ui.verifyAlias.VerifyAliasDetails2faActivity;
import com.barclays.absa.banking.framework.ConnectivityMonitorActivity;
import com.barclays.absa.banking.framework.PermissionFacade;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.framework.utils.TelephoneUtil;
import com.barclays.absa.banking.linking.ui.LinkingActivity;
import com.barclays.absa.banking.manage.devices.linking.ForgotPassword2faActivity;
import com.barclays.absa.banking.newToBank.NewToBankConstants;
import com.barclays.absa.banking.passcode.passcodeLogin.ExpressAuthenticationHelper;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.sessionTimeout.SessionTimeOutDialogActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.presentation.sureCheckV2.SecurityCodeActivity;
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity;
import com.barclays.absa.banking.registration.RegisterCreatePasswordActivity;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AccessibilityUtils;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.AnimationHelper;
import com.barclays.absa.utils.DeviceUtils;
import com.barclays.absa.utils.KeyboardUtils;
import com.barclays.absa.utils.ProfileManager;
import com.barclays.absa.utils.SharedPreferenceService;

import java.util.List;

import kotlin.Unit;
import styleguide.forms.validation.ValidationExtensions;
import za.co.absa.networking.ExpressNetworkingConfig;

import static com.barclays.absa.banking.linking.ui.IdentificationAndVerificationConstants.ID_AND_V_LINK_DEVICE;

public class AccountLoginActivity extends ConnectivityMonitorActivity implements View.OnClickListener, AccountLoginView {
    public static final String ACCESS_ACCOUNT_KEY = "ACCESS_ACCOUNT";
    public static final String ACCESS_PIN_KEY = "ACCESS_PIN";
    public static final String USER_NO_KEY = "USER_NO";
    public static final String SOURCE_ACTIVITY = "FROM_ACTIVITY";
    private static final String DELIMITER = " ";
    public static final String ACCOUNT_LOCKED = "ACC_LOCKED";

    private final DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
        switch (which) {
            case DialogInterface.BUTTON_NEUTRAL:
                break;
            default:
                break;
        }
    };
    private String accessAccountNumber;
    private String accessPin;
    private String userNumber = DEFAULT_USER_NO;
    private AccountLoginActivityBinding binding;
    private AccountLoginPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SessionTimeOutDialogActivity.shouldShow = false;
        super.onCreate(savedInstanceState);
        SharedPreferenceService.INSTANCE.setIsPartialRegistration(false);
        ExpressNetworkingConfig.INSTANCE.setLoggedIn(false);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.account_login_activity, null, false);
        setContentView(binding.getRoot());

        setToolBarBack(R.string.login_text);
        presenter = new AccountLoginPresenter(this);

        if (!getAppCacheService().isPasscodeResetFlow()) {
            ProfileManager.getInstance().setActiveUserProfile(null);
        }

        if (getAppCacheService().getCustomerSessionId() == null) {
            ExpressAuthenticationHelper expressAuthenticationHelper = new ExpressAuthenticationHelper(this);
            expressAuthenticationHelper.performHello(() -> {
                dismissProgressDialog();
                showCsid();
                return Unit.INSTANCE;
            });
        } else {
            showCsid();
        }

        binding.userNumberInputView.setSelectedValue("1");
        binding.termsConditionsTextView.setOnClickListener(this);
        binding.visitWebsiteTextView.setOnClickListener(this);
        binding.loginButton.setOnClickListener(this);

        configureViews();
        setupTalkBack();
        prepopulateViews();

        binding.accountNumberInputView.requestFocus();

        mScreenName = BMBConstants.FIRST_LOGIN_CONST;
        mSiteSection = BMBConstants.SIMPLIFIED_LOGIN_CONST;

        getAppCacheService().setAnalyticsScreenName(BMBConstants.FIRST_LOGIN_CONST);
        getAppCacheService().setAnalyticsAppSection(BMBConstants.SIMPLIFIED_LOGIN_CONST);

        AnalyticsUtils.getInstance().trackCustomScreenView(getAppCacheService().getAnalyticsScreenName(), getAppCacheService().getAnalyticsAppSection(), BMBConstants.TRUE_CONST);

        final BMBApplication app = BMBApplication.getInstance();
        app.setUserLoggedInStatus(false);
        getAppCacheService().setUserLoggedInStatus(false);

        app.updateLanguage(this);
        app.LOGGED_IN_START_TIME = 0;
    }

    private void showCsid() {
        String customerSessionId = getAppCacheService().getCustomerSessionId();
        if (BuildConfig.UAT) {
            binding.csidTextView.setVisibility(View.VISIBLE);
            binding.csidTextView.setText(String.format("CSID: %s", customerSessionId));
        }
    }

    private void setupTalkBack() {
        if (isAccessibilityEnabled()) {
            binding.accountNumberInputView.setContentDescription(getString(R.string.talkback_account_number_input));
            binding.pinCodeInputView.setContentDescription(getString(R.string.talkback_pincode_input));
            binding.userNumberInputView.setContentDescription(getString(R.string.talkback_usernumber_inputview));
            binding.loginButton.setContentDescription(getString(R.string.talkback_login_button));
        }
    }

    @Override
    public void clearLoginDetails() {
        binding.accountNumberInputView.setSelectedValue("");
        binding.pinCodeInputView.setSelectedValue("");
        binding.userNumberInputView.setSelectedValue(DEFAULT_USER_NO);
        binding.accountNumberInputView.requestFocus();
    }

    @Override
    public void launchCreatePasswordScreen(SecureHomePageObject secureHomePageObject) {
        getAppCacheService().setIsPasswordResetFlow(true);
        CustomerProfileObject customerProfile = secureHomePageObject.getCustomerProfile();
        if (customerProfile.getSecondFactorState() == SecondFactorState.SURECHECKV2_NOPRIMARYDEVICE.getValue()) {
            getAppCacheService().setReturnToScreen(LinkingPasswordValidationActivity.class);
        }
        Intent createPassIntent = new Intent(AccountLoginActivity.this, RegisterCreatePasswordActivity.class);
        createPassIntent.putExtra(BMBConstants.NAVIGATED_FROM, "DS2");
        if (CustomerProfileObject.getInstance().getReachedDeviceMaxLimit()) {
            createPassIntent.putExtra(BMBConstants.DEVICE_DECOUPLE_OBJ, secureHomePageObject.getSerializableDeviceList());
        }
        startActivity(createPassIntent);
    }

    @Override
    public void navigateToForgotPasscodeScreen() {
        Intent accountLockedIntent = new Intent(this, ForgotPassword2faActivity.class);
        accountLockedIntent.putExtra(ACCOUNT_LOCKED, true);
        startActivity(accountLockedIntent);
    }

    @Override
    public void goToSecurityCodeRevokedScreen() {
        goToSecurityCodeScreen(R.string.reason_passcode_revoked);
    }

    @Override
    public void goToSecurityCodeExpiredScreen() {
        goToSecurityCodeScreen(R.string.security_code_expired);
    }

    private void goToSecurityCodeScreen(@StringRes int heading) {
        Intent intent = IntentFactory.getFailureResultScreen(this, heading, R.string.passcode_revoked_description);
        intent = new IntentFactory.IntentBuilder(intent).setGenericResultBottomButton(R.string.passcode_revoked_enter_security_code,
                view -> startActivity(new Intent(this, SecurityCodeActivity.class))).build();

        GenericResultActivity.topOnClickListener = view -> BMBApplication.getInstance().getTopMostActivity().finish();
        startActivity(intent);
    }

    @Override
    public void goToNoPrimaryDeviceScreen(SecureHomePageObject secureHomePageObject) {
        navigateToEnterPasswordScreen(true, secureHomePageObject);
    }

    @Override
    public void goToAccountLockedScreen() {
        getAppCacheService().setLinkingFlow(false);
        String subMessage = String.format("%s\n\n%s", getString(R.string.unlock_digital_profile), getString(R.string.contact_for_assistance));
        Intent intent = IntentFactory.getFailureResultScreen(this, R.string.security_problems, subMessage);
        intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.done);
        intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.call);
        GenericResultActivity.bottomOnClickListener = v -> BMBApplication.getInstance().getTopMostActivity().finish();
        GenericResultActivity.topOnClickListener = v -> TelephoneUtil.call(AccountLoginActivity.this, "tel:0860111123");

        startActivity(intent);
        clearLoginDetails();
    }

    private void prepopulateViews() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(SOURCE_ACTIVITY)) {
            String sourceActivityName = bundle.getString(SOURCE_ACTIVITY);
            if (BMBConstants.REGISTER_CONST.equalsIgnoreCase(sourceActivityName)) {
                if (NewToBankConstants.ON_NEW_TO_BANK_FLOW) {
                    setToolBar(getString(R.string.login_text), null);
                }
                accessAccountNumber = bundle.getString(ACCESS_ACCOUNT_KEY);
                accessPin = bundle.getString(ACCESS_PIN_KEY);
                userNumber = bundle.getString(USER_NO_KEY);
                binding.accountNumberInputView.setSelectedValue(accessAccountNumber);
                binding.pinCodeInputView.setSelectedValue(accessPin);
                binding.userNumberInputView.setSelectedValue(userNumber);
                binding.loginButton.performClick();
            }
        }
    }

    private String getAccountNumber() {
        return binding.accountNumberInputView.getSelectedValue().replace(DELIMITER, "").trim();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginButton:
                preventDoubleClick(view);
                AnalyticsUtil.INSTANCE.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_AccessAccountNumberAndPIN_LogInButtonClicked");
                binding.accountNumberInputView.showError(false);
                binding.pinCodeInputView.showError(false);
                binding.userNumberInputView.showError(false);
                if (TextUtils.isEmpty(binding.accountNumberInputView.getSelectedValue().trim())) {
                    AnimationHelper.shakeShakeAnimate(binding.accountNumberInputView);
                    binding.accountNumberInputView.showError(true);
                    binding.accountNumberInputView.setError(getString(R.string.access_account_number_2fa_errormessage));
                    announceError(binding.accountNumberInputView.getErrorTextView());
                } else if (TextUtils.isEmpty(binding.pinCodeInputView.getSelectedValue().trim())) {
                    AnimationHelper.shakeShakeAnimate(binding.pinCodeInputView);
                    binding.pinCodeInputView.showError(true);
                    binding.pinCodeInputView.setError(getString(R.string.access_pin_errormessage));
                    announceError(binding.pinCodeInputView.getErrorTextView());
                } else {
                    presenter.login();
                }
                break;
            case R.id.termsConditionsTextView:
                presenter.termsAndConditionsInvoked();
                break;
            case R.id.visitWebsiteTextView:
                presenter.onlineBankingViewInvoked();
                break;
            default:
                break;
        }
    }

    private void loginWithAccessAccount() {
        SharedPreferenceService.INSTANCE.enableLaunchProfileSetUp();
        KeyboardUtils.hideSoftKeyboard(binding.accountNumberInputView);
        accessAccountNumber = getAccountNumber();
        accessPin = binding.pinCodeInputView.getSelectedValue();
        userNumber = binding.userNumberInputView.getSelectedValue();
        presenter.loginInvoked(accessAccountNumber, accessPin, userNumber);
    }

    private void configureViews() {
        binding.accountNumberInputView.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        binding.pinCodeInputView.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        binding.userNumberInputView.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.accountNumberInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.pinCodeInputView);

        binding.accountNumberInputView.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && !TextUtils.isEmpty(binding.pinCodeInputView.getText())) {
                    binding.loginButton.setEnabled(true);
                } else if (s.length() == 0 || TextUtils.isEmpty(binding.pinCodeInputView.getText())) {
                    binding.loginButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.pinCodeInputView.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && !TextUtils.isEmpty(binding.accountNumberInputView.getText())) {
                    binding.loginButton.setEnabled(true);
                } else if (s.length() == 0 || TextUtils.isEmpty(binding.accountNumberInputView.getText())) {
                    binding.loginButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.accountNumberInputView.clearFocus();
        binding.pinCodeInputView.clearFocus();
        binding.userNumberInputView.clearFocus();
    }

    public void showError(int errorMessage) {
        showError(getString(errorMessage));
    }

    public void showError(String errorMessage) {
        super.showMessageError(errorMessage);
    }

    public void announceError(TextView textView) {
        if (isAccessibilityEnabled()) {
            AccessibilityUtils.announceRandValueTextFromView(textView);
        }
    }

    @Override
    public void showErrorDialog(int title, int errorMessage) {
        BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                .title(getString(title))
                .message(getString(errorMessage))
                .positiveButton(getString(R.string.retry))
                .positiveDismissListener(dialogClickListener)
                .build());
    }

    @Override
    public void navigateToTermsAndConditionsScreen() {
        startActivity(new Intent(this, TermsAndConditionsSelectorActivity.class));
    }

    @Override
    public void navigateToOnlineView() {
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.absa.co.za"));
        startActivityIfAvailable(intent);
    }

    private void navigateToEnterPasswordScreen(boolean isNoPrimaryState, SecureHomePageObject secureHomePageObject) {
        Intent linkIntent = new Intent(AccountLoginActivity.this, LinkingPasswordValidationActivity.class);
        linkIntent.putExtra(LinkingPasswordValidationActivity.IS_NO_PRIMARY_STATE, isNoPrimaryState);
        if (secureHomePageObject != null) {
            getAppCacheService().setSecureHomePageObject(secureHomePageObject);
        }
        startActivity(linkIntent);
    }

    @Override
    public void navigateToEnterPasswordScreen(SecureHomePageObject secureHomePageObject) {
        navigateToEnterPasswordScreen(false, secureHomePageObject);
    }

    @Override
    public void showGoToBranchForSecurityCodeMessage() {
        GenericResultActivity.bottomOnClickListener = v -> {
            Intent intent = new Intent(AccountLoginActivity.this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        };
        Intent intent = new Intent(AccountLoginActivity.this, GenericResultActivity.class);
        intent.putExtra(GenericResultActivity.IS_FAILURE, true);
        intent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_cross);
        intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.no_primary_device);
        intent.putExtra(GenericResultActivity.SUB_MESSAGE, R.string.go_to_a_branch);
        intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, -1);
        intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.close);
        startActivity(intent);
    }

    @Override
    public void setDefaultUserNumber() {
        binding.userNumberInputView.setSelectedValue(BMBConstants.DEFAULT_USER_NO);
    }

    @Override
    public void login() {
        ProfileManager.getInstance().loadAllUserProfiles(new ProfileManager.OnProfileLoadListener() {
            @Override
            public void onAllProfilesLoaded(List<UserProfile> userProfiles) {
                if (userProfiles.size() == 0) {
                    DeviceUtils.getDeviceUuid();
                }
                PermissionFacade.requestDeviceStatePermission(AccountLoginActivity.this, () -> loginWithAccessAccount());
            }

            @Override
            public void onProfilesLoadFailed() {
                BaseAlertDialog.INSTANCE.showGenericErrorDialog();
            }
        });
    }

    @Override
    public void onBackPressed() {
        AnalyticsUtil.INSTANCE.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_AccessAccountNumberAndPIN_CloseButtonClicked");

        if (NewToBankConstants.ON_NEW_TO_BANK_FLOW) {
            showEndSessionDialogPrompt();
        } else {
            super.onBackPressed();
        }
    }

    private void showEndSessionDialogPrompt() {
        BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.new_to_bank_are_you_sure))
                .message(getString(R.string.new_to_bank_if_you_go_back))
                .positiveDismissListener((dialog, which) -> {
                    Intent intent = new Intent(AccountLoginActivity.this, WelcomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }));
    }

    @Override
    public void showAccountSuspendedScreen() {
        getAppCacheService().setLinkingFlow(false);
        Intent intent = IntentFactory.getFailureResultScreen(this, R.string.login_error_account_locked_title, R.string.login_error_unpaid_suspended, v -> BMBApplication.getInstance().getTopMostActivity().finish());
        startActivity(intent);
    }

    @Override
    public void showFraudLockScreen() {
        getAppCacheService().setLinkingFlow(false);
        Intent intent = IntentFactory.getFailureResultScreen(this, R.string.login_error_account_locked_title, R.string.login_error_fraud_suspended);
        intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.done);
        intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.call);
        GenericResultActivity.bottomOnClickListener = v -> BMBApplication.getInstance().getTopMostActivity().finish();
        GenericResultActivity.topOnClickListener = v -> TelephoneUtil.call(AccountLoginActivity.this, TelephoneUtil.FRAUD_NUMBER);

        startActivity(intent);
    }

    @Override
    public void showPleaseUseBiometricAuthenticationScreen() {
        getAppCacheService().setLinkingFlow(false);
        Intent intent = IntentFactory.getFailureResultScreen(this, R.string.linking_please_try_again, R.string.linking_use_bio_authentication);

        GenericResultActivity.topOnClickListener = v -> {
            Intent linkingActivityIntent = new Intent(this, LinkingActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(linkingActivityIntent);
        };

        startActivity(intent);
    }

    @Override
    public void showCurrentlyOfflineScreen(String message) {
        startActivity(IntentFactory.getGenericResultFailureBuilder(this)
                .setGenericResultIconToError()
                .setGenericResultHeaderMessage(R.string.something_went_wrong)
                .setGenericResultSubMessage(message).setFinishActivityOnDone(this)
                .build());
    }

    @Override
    public void navigateToAliasVerificationScreen(SecureHomePageObject secureHomePageObject) {
        Intent linkIntent = new Intent(this, VerifyAliasDetails2faActivity.class);
        linkIntent.putExtra(AppConstants.RESULT, secureHomePageObject);
        linkIntent.putExtra(VerifyAliasDetails2faActivity.VERIFY_ALIAS_FROM_SCREEN, VerifyAliasDetails2faActivity.ACCOUNT_LOGIN_SCREEN);
        startActivity(linkIntent);
    }
}