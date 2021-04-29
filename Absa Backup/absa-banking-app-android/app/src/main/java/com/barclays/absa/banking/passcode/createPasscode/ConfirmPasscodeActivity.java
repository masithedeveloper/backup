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
package com.barclays.absa.banking.passcode.createPasscode;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccessPrivileges;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.express.authentication.login.AuthenticationStatusCodes;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.PermissionFacade;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.login.ui.passcode.PasscodeSuccessUseFingerprintActivity;
import com.barclays.absa.banking.login.ui.passcode.SimplifiedAuthenticationHelper;
import com.barclays.absa.banking.manage.devices.VerificationDeviceNominationActivity;
import com.barclays.absa.banking.passcode.PasscodeActivity;
import com.barclays.absa.banking.passcode.passcodeLogin.ExpressAuthenticationHelper;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.sureCheckV2.SureCheckConfirmation2faActivity;
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity;
import com.barclays.absa.banking.presentation.whatsNew.WhatsNewHelper;
import com.barclays.absa.banking.presentation.whatsNew.WhatsNewLottieActivity;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.PermissionHelper;
import com.barclays.absa.utils.ProfileManager;
import com.barclays.absa.utils.UserSettingsManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ConfirmPasscodeActivity extends PasscodeActivity implements ConfirmPasscodeView {
    private static final int RETRY_TRESHOLD = 3;
    private static final int VIBRATE_MS = 200;
    private boolean passcodeMatched = false;
    private int mRetryCount = 0;
    private int profileCount;
    private ConfirmPasscodePresenter presenter;
    private ExpressAuthenticationHelper expressAuthenticationHelper;

    ExtendedResponseListener<SecureHomePageObject> confirmPasscodeExtendedResponseListener = new ExtendedResponseListener<SecureHomePageObject>() {
        @Override
        public void onSuccess(final SecureHomePageObject successResponse) {
            dismissProgressDialog();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolBarBack(R.string.passcode);

        findViewById(R.id.termsAndConditionsTextView).setVisibility(View.INVISIBLE);
        findViewById(R.id.visitOnlineTextView).setVisibility(View.INVISIBLE);

        findViewById(R.id.createOrConfirmLayout).setVisibility(View.VISIBLE);
        enterOrConfirmPasscode.setVisibility(View.VISIBLE);
        enterOrConfirmPasscode.setText(R.string.reenter_passcode);
        enterOrConfirmPasscodeInstrution.setVisibility(View.VISIBLE);
        enterOrConfirmPasscodeInstrution.setText(R.string.linking_reenter_your_five_digit_app_passcode);
        hideOldInstruction();

        AsyncTask.execute(() -> {
            SimplifiedAuthenticationHelper simplifiedAuthenticationHelper = new SimplifiedAuthenticationHelper(ConfirmPasscodeActivity.this);
            simplifiedAuthenticationHelper.setExtendedResponseListener(confirmPasscodeExtendedResponseListener);
            confirmPasscodeExtendedResponseListener.setView(ConfirmPasscodeActivity.this);
        });

        mScreenName = BMBConstants.RE_ENTER_PASSCODE_CONST;
        mSiteSection = BMBConstants.SIMPLIFIED_LOGIN_CONST;
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.RE_ENTER_PASSCODE_CONST, BMBConstants.SIMPLIFIED_LOGIN_CONST, BMBConstants.TRUE_CONST);
        ProfileManager profileManager = ProfileManager.getInstance();
        profileCount = profileManager.getProfileCount();

        setBottomLeftKeyText("");

        presenter = new ConfirmPasscodePresenter(this);
    }

    @Override
    public BaseActivity getActivity() {
        return this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showPasscodeResetSuccessMessage() {
        showMessage(getString(R.string.passcode), getString(R.string.passcode_reset_successful), (dialog, which) -> logoutAndGoToStartScreen());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionHelper.PermissionCode.ACCESS_DEVICE_STATE.value) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkIfPasscodeMatched();
            } else {
                PermissionFacade.requestDeviceStatePermission(this, this::checkIfPasscodeMatched);
            }
        } else if (requestCode == PermissionHelper.PermissionCode.ACCESS_FINGERPRINT.value) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                navigateToNextScreen();
            } else {
                navigateToPrimaryScreens();
            }
        }
    }

    @Override
    protected void doEarlyValidation() {
        // ignore
    }

    @Override
    protected void onPasscodeEntered() {
        Intent intent = getIntent();
        String passcode = intent.getStringExtra(SET_PASSCODE);
        String confirmPasscode = this.enteredPasscode;
        if (passcode != null && passcode.equals(confirmPasscode)) {
            passcodeMatched = true;
            PermissionFacade.requestDeviceStatePermission(this, this::checkIfPasscodeMatched);
        } else {
            showPasscodeMismatchError();
        }
    }

    private void showPasscodeMismatchError() {
        setPasscodeError(getString(R.string.passcodes_notmatch_error_message));
        mScreenName = BMBConstants.PASSCODE_DO_NOT_MATCH_CONST;
        mSiteSection = BMBConstants.SIMPLIFIED_LOGIN_CONST;
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.PASSCODE_DO_NOT_MATCH_CONST, BMBConstants.SIMPLIFIED_LOGIN_CONST,
                BMBConstants.TRUE_CONST);
        mRetryCount++;
        resetPasscodeIndicator();
        shakeForIncorrect();
        if (mRetryCount == RETRY_TRESHOLD) {
            showFailedResultScreen();
        }
    }

    private void showFailedResultScreen() {
        GenericResultActivity.bottomOnClickListener = v -> navigateToCreatePasscodeActivity();

        Intent intent = new Intent(ConfirmPasscodeActivity.this, GenericResultActivity.class);
        intent.putExtra(GenericResultActivity.IS_FAILURE, true);
        intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.passcodes_not_match);
        intent.putExtra(GenericResultActivity.SUB_MESSAGE, R.string.incorrect_passcode_match);
        intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.create_passcode);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void navigateToCreatePasscodeActivity() {
        Intent intent = new Intent(ConfirmPasscodeActivity.this, CreatePasscodeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void checkIfPasscodeMatched() {
        if (passcodeMatched) {
            presenter.passcodeEntered(enteredPasscode);
        } else {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(VIBRATE_MS);
            }
        }
    }

    @Override
    protected void onBackSpaceClicked() {
        passcodeMatched = false;
    }

    @Override
    protected void onBottomLeftKeyClicked() {

    }

    @Override
    protected void resetPasscodeInstruction() {
        setPasscodeInstruction(null);
    }

    @Override
    public void onBackPressed() {
        Intent goBackIntent = new Intent(this, CreatePasscodeActivity.class);
        goBackIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goBackIntent);
        finish();
    }

    @Override
    public void showPasscodeErrorMessage(String passcodeError) {
        setPasscodeInstruction(passcodeError);
    }

    private void navigateToNextScreen() {
        PermissionHelper.requestFingerprintPermission(this, () -> {
            if (userIsEligibleForBiometrics()) {
                goToUseFingerprintScreen();
            } else {
                navigateToPrimaryScreens();
            }
        });
    }

    @Override
    public void goToUseFingerprintScreen() {
        Intent enableFingerPrintIntent = new Intent(ConfirmPasscodeActivity.this, PasscodeSuccessUseFingerprintActivity.class);
        enableFingerPrintIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(enableFingerPrintIntent);
    }

    @Override
    public boolean userIsEligibleForBiometrics() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
            return profileCount < 1 && fingerprintManager != null && fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints();
        } else {
            return false;
        }
    }

    @Override
    public void navigateToPrimaryScreens() {
        if (getAppCacheService().isSecondaryDevice() && !getAppCacheService().isPasscodeResetFlow() && !AccessPrivileges.getInstance().isOperator() && CustomerProfileObject.getInstance().isTransactionalUser()) {
            Intent surecheck2SuccessIntent = new Intent(ConfirmPasscodeActivity.this, VerificationDeviceNominationActivity.class);
            surecheck2SuccessIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(surecheck2SuccessIntent);
            UserSettingsManager.INSTANCE.setFingerprintActive(false);
        } else {
            Intent surecheck2SuccessIntent = new Intent(ConfirmPasscodeActivity.this, SureCheckConfirmation2faActivity.class);
            surecheck2SuccessIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            surecheck2SuccessIntent.putExtra(VerificationDeviceNominationActivity.MAKE_SURECHECK, "yes");
            startActivity(surecheck2SuccessIntent);
            UserSettingsManager.INSTANCE.setFingerprintActive(false);
        }
    }

    @Override
    public void showOperatorLinkingSuccessScreen() {
        if (!WhatsNewHelper.INSTANCE.getEnabledWhatsScreens().isEmpty()) {
            Intent whatsNewIntent = new Intent(this, WhatsNewLottieActivity.class);
            whatsNewIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(whatsNewIntent);
        } else {
            navigateToPrimaryScreens();
        }
        finish();
    }

    @Override
    public void showEncryptionFailureErrorMessageAndGiveUserAnotherChanceToCreatePasscode() {
        showMessageError("Error creating passcode. Please try again.", (dialog, which) -> finish());
    }

    @Override
    public void showErrorMessage(String error) {
        showMessageError(error);
    }

    @Override
    public void loginComplete() {
        presenter.fetchAuthorizations();
    }

    @Override
    public void performExpressLogin(UserProfile userProfile) {
        expressAuthenticationHelper = new ExpressAuthenticationHelper(getActivity());
        expressAuthenticationHelper.performLogin(enteredPasscode, new ExpressAuthenticationHelper.LoginCallBack() {

            @Override
            public void loginCallComplete() {
                expressAuthenticationHelper.getUserProfile(new ExpressAuthenticationHelper.UserProfileCallBack() {
                    @Override
                    public void userProfileCallComplete(@NotNull ArrayList<AccountObject> expressAccountList) {
                        AbsaCacheManager.getInstance().appendAccountList(expressAccountList);
                        getAppCacheService().setPasscodeResetFlow(false);
                        presenter.performAOlLogin();
                    }

                    @Override
                    public void userProfileCallFailed(@NotNull String failureMessage) {
                        showMessageError(failureMessage);
                    }
                });
            }

            @Override
            public void loginCallFailure(AuthenticationStatusCodes authenticationStatusCodes, @NotNull String message) {
                dismissProgressDialog();
                showMessageError(message, (dialog, buttonSelected) -> goToLaunchScreen(ConfirmPasscodeActivity.this, false));
            }
        }, false);
    }

    public void showDeviceLinkingFailedScreen(String failureMessage) {
        GenericResultActivity.bottomOnClickListener = v -> logoutAndGoToStartScreen();
        Intent deviceLinkingFailedIntent = new Intent(ConfirmPasscodeActivity.this, GenericResultActivity.class);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.IS_FAILURE, true);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.device_linking_error);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.SUB_MESSAGE_STRING, failureMessage);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.ok);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.CALL_US_CONTACT_NUMBER, getString(R.string.support_center_number));
        deviceLinkingFailedIntent.putExtra(BMBConstants.PRE_LOGIN_LAYOUT, true);
        startActivity(deviceLinkingFailedIntent);
    }

    @Override
    public void showDeviceLinkingFailedScreen() {
        GenericResultActivity.bottomOnClickListener = v -> {
            startActivity(new Intent(ConfirmPasscodeActivity.this, WelcomeActivity.class));
            finish();
        };
        Intent deviceLinkingFailedIntent = new Intent(ConfirmPasscodeActivity.this, GenericResultActivity.class);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.IS_FAILURE, true);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.device_linking_error);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.SUB_MESSAGE, R.string.device_linking_error_explanation);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.cancel);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.CALL_US_CONTACT_NUMBER, "08600 08600");
        deviceLinkingFailedIntent.putExtra(BMBConstants.PRE_LOGIN_LAYOUT, true);
        startActivity(deviceLinkingFailedIntent);
    }
}
