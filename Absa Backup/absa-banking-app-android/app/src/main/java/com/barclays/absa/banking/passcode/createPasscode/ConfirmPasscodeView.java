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

import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.BaseView;

public interface ConfirmPasscodeView extends BaseView {
    void showPasscodeErrorMessage(String passcodeError);
    void showDeviceLinkingFailedScreen(String failureMessage);
    void showDeviceLinkingFailedScreen();
    void showPasscodeResetSuccessMessage();
    void goToUseFingerprintScreen();
    boolean userIsEligibleForBiometrics();
    void navigateToPrimaryScreens();
    void showOperatorLinkingSuccessScreen();

    BaseActivity getActivity();
    void showEncryptionFailureErrorMessageAndGiveUserAnotherChanceToCreatePasscode();
    void showErrorMessage(String error);
    void performExpressLogin(UserProfile userProfile);
    void loginComplete();
}
