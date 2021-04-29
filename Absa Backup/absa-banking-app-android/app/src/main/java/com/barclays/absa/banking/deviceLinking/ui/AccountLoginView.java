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

import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.framework.BaseView;

interface AccountLoginView extends BaseView {

    void navigateToTermsAndConditionsScreen();
    void navigateToOnlineView();
    void navigateToEnterPasswordScreen(SecureHomePageObject secureHomePageObject);
    void setDefaultUserNumber();
    void showError(int errorMessage);
    void showError(String errorMessage);
    void showErrorDialog(int title, int errorMessage);
    void clearLoginDetails();
    void launchCreatePasswordScreen(SecureHomePageObject secureHomePageObject);
    void navigateToForgotPasscodeScreen();
    void goToSecurityCodeRevokedScreen();
    void goToSecurityCodeExpiredScreen();
    void goToNoPrimaryDeviceScreen(SecureHomePageObject secureHomePageObject);
    void showGoToBranchForSecurityCodeMessage();
    void goToAccountLockedScreen();
    void login();
    void showAccountSuspendedScreen();
    void showFraudLockScreen();
    void showPleaseUseBiometricAuthenticationScreen();
    void showCurrentlyOfflineScreen(String message);
    void navigateToAliasVerificationScreen(SecureHomePageObject secureHomePageObject);
}