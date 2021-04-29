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

import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.boundary.model.ValidatePasswordResponse;
import com.barclays.absa.banking.deviceLinking.services.ValidatePasswordRequest;
import com.barclays.absa.banking.framework.BuildConfigHelper;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;
import com.barclays.absa.banking.login.services.dto.AccessAccountLoginRequest;
import com.barclays.absa.banking.login.services.dto.AuthenticateAliasRequest;
import com.barclays.absa.banking.login.services.dto.AuthenticateAliasResponse;
import com.barclays.absa.banking.login.services.dto.SecureHomePageParser;
import com.barclays.absa.banking.login.services.dto.SimplifiedLoginRequest;

public class LoginInteractor implements LoginService {

    @Override
    public void performAccessAccountLogin(String accountNumber, String pin, String userNumber, ExtendedResponseListener<SecureHomePageObject> responseListener) {
        AccessAccountLoginRequest<SecureHomePageObject> loginRequest = new AccessAccountLoginRequest<>(accountNumber, pin, userNumber, responseListener);
        loginRequest.setResponseParser(new SecureHomePageParser());
        ServiceClient serviceClient = new ServiceClient(BuildConfigHelper.INSTANCE.getNCipherServerPath(), loginRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void performPasscodeLogin(UserProfile userProfile, String passCode, ExtendedResponseListener<SecureHomePageObject> responseListener) {
        SimplifiedLoginRequest<SecureHomePageObject> loginRequest = new SimplifiedLoginRequest<>(userProfile, passCode, responseListener);
        submit(loginRequest);
    }

    @Override
    public void validatePassword(String value1, String value2, String value3, ExtendedResponseListener<ValidatePasswordResponse> responseListener) {
        ValidatePasswordRequest<ValidatePasswordResponse> validatePasswordRequest = new ValidatePasswordRequest<>(value1, value2, value3, responseListener);
        submit(validatePasswordRequest);
    }

    @Override
    public void authenticateAliasWithPasscode(UserProfile userProfile, String passcode, ExtendedResponseListener<AuthenticateAliasResponse> responseListener) {
        AuthenticateAliasRequest<AuthenticateAliasResponse> request = new AuthenticateAliasRequest<>(userProfile, passcode, responseListener);
        submit(request);

    }

    @Override
    public void authenticateAliasWithBiometrics(UserProfile userProfile, ExtendedResponseListener<AuthenticateAliasResponse> responseListener) {
        AuthenticateAliasRequest<AuthenticateAliasResponse> request = new AuthenticateAliasRequest<>(userProfile, responseListener);
        submit(request);
    }

    @Override
    public void performBiometricLogin(String encryptedAlias, String encryptedCredential, String encryptedSymmetricKey, ExtendedResponseListener<SecureHomePageObject> extendedResponseListener) {
        SimplifiedLoginRequest<SecureHomePageObject> request = new SimplifiedLoginRequest<>(encryptedAlias, encryptedCredential, encryptedSymmetricKey, extendedResponseListener, true);
        submit(request);
    }

    @Override
    public void performPasscodeLogin(String encryptedAlias, String encryptedCredential, String encryptedSymmetricKey, ExtendedResponseListener<SecureHomePageObject> extendedResponseListener) {
        SimplifiedLoginRequest<SecureHomePageObject> request = new SimplifiedLoginRequest<>(encryptedAlias, encryptedCredential, encryptedSymmetricKey, extendedResponseListener, false);
        submit(request);
    }

    @SuppressWarnings("rawtypes")
    private void submit(ExtendedRequest request) {
        ServiceClient serviceClient = new ServiceClient(request);
        serviceClient.submitRequest();
    }

}