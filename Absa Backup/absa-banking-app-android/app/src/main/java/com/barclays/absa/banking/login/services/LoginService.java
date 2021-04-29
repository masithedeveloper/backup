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
package com.barclays.absa.banking.login.services;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.boundary.model.ValidatePasswordResponse;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.login.services.dto.AuthenticateAliasResponse;

public interface LoginService {
    String OP0817_AUTHENTICATE_ALIAS = "OP0817";
    String APP_VERSION = "2FA_APP_" + BuildConfig.VERSION_NAME;

    void performAccessAccountLogin(String accountNumber, String userNumber, String pin, ExtendedResponseListener<SecureHomePageObject> responseListener);
    void performPasscodeLogin(UserProfile userProfile, String passCode, ExtendedResponseListener<SecureHomePageObject> responseListener);
    void validatePassword(String value1, String value2, String value3, ExtendedResponseListener<ValidatePasswordResponse> responseListener);
    void authenticateAliasWithPasscode(UserProfile userProfile, String passcode, ExtendedResponseListener<AuthenticateAliasResponse> responseListener);
    void authenticateAliasWithBiometrics(UserProfile userProfile, ExtendedResponseListener<AuthenticateAliasResponse> responseListener);
    void performBiometricLogin(String encryptedAlias, String encryptedCredential, String encryptedSymmetricKey, ExtendedResponseListener<SecureHomePageObject> extendedResponseListener);
    void performPasscodeLogin(String encryptedAlias, String encryptedCredential, String encryptedSymmetricKey, ExtendedResponseListener<SecureHomePageObject> extendedResponseListener);
}