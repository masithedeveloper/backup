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
package com.barclays.absa.banking.registration.services;

import com.barclays.absa.banking.boundary.model.RegisterProfileDetail;
import com.barclays.absa.banking.framework.BuildConfigHelper;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;
import com.barclays.absa.banking.registration.services.dto.AtmPinRegistrationRequest;
import com.barclays.absa.banking.registration.services.dto.Create2faAliasRequest;
import com.barclays.absa.banking.registration.services.dto.Create2faAliasResponse;
import com.barclays.absa.banking.registration.services.dto.CreatePasswordRequest;
import com.barclays.absa.banking.registration.services.dto.CreatePasswordResult;
import com.barclays.absa.banking.registration.services.dto.DecoupleRegistrationRequest;
import com.barclays.absa.banking.registration.services.dto.ExtendedRegisterUserRequest;
import com.barclays.absa.banking.registration.services.dto.RegisterAOLProfileResponse;
import com.barclays.absa.banking.registration.services.dto.RegisterCredentialsRequest;
import com.barclays.absa.banking.registration.services.dto.RegisterCredentialsResponse;
import com.barclays.absa.banking.registration.services.dto.TermsAndConditionObject;
import com.barclays.absa.banking.registration.services.dto.TermsAndConditions2faRequest;

public class RegistrationInteractor implements RegistrationService {

    @Override
    public void getCustomerProfileDetails(String cardNumber, String atmPin, String serverPath, ExtendedResponseListener<RegisterProfileDetail> responseListener) {
        AtmPinRegistrationRequest<RegisterProfileDetail> loginRequest = new AtmPinRegistrationRequest<>(cardNumber, serverPath, atmPin, responseListener);
        ServiceClient serviceClient = new ServiceClient(BuildConfigHelper.INSTANCE.getNCipherServerPath(), loginRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void fetchTermsOfUserDetails(String clientType, ExtendedResponseListener<TermsAndConditionObject> responseListener) {
        TermsAndConditions2faRequest termsAndConditionsRequest = new TermsAndConditions2faRequest<>(BuildConfigHelper.INSTANCE.getNCipherServerPath(), clientType, responseListener);
        ServiceClient serviceClient = new ServiceClient(termsAndConditionsRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void create2faAlias(ExtendedResponseListener<Create2faAliasResponse> responseListener) {
        Create2faAliasRequest<Create2faAliasResponse> create2faAliasRequest = new Create2faAliasRequest<>(responseListener);
        ServiceClient serviceClient = new ServiceClient(create2faAliasRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void registerCredentials(String passcode, String randomAliasId, ExtendedResponseListener<RegisterCredentialsResponse> responseListener) {
        RegisterCredentialsRequest<RegisterCredentialsResponse> registerCredentialsRequest = new RegisterCredentialsRequest<>(passcode, randomAliasId, responseListener);
        ServiceClient serviceClient = new ServiceClient(registerCredentialsRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void passcodeReset(String passcode, ExtendedResponseListener<RegisterCredentialsResponse> responseListener) {
        PasscodeResetRequest<RegisterCredentialsResponse> registerCredentialsRequest = new PasscodeResetRequest<>(passcode, responseListener);
        ServiceClient serviceClient = new ServiceClient(registerCredentialsRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void registerOnlineProfile(RegisterProfileDetail registerProfileDetail, String password, ExtendedResponseListener<RegisterAOLProfileResponse> responseListener) {
        String selectedAccessAccount = registerProfileDetail != null ? registerProfileDetail.getSelectedAccessAccountNo() : null;
        String selectedBillingAccount = registerProfileDetail != null ? registerProfileDetail.getSelectedBillingAccountNo() : null;
        String accessAccount = "";
        String billingAccount = "";
        assert selectedAccessAccount != null;
        String[] accessAccountStrings = selectedAccessAccount.split("\\n");
        assert selectedBillingAccount != null;
        String[] billingAccountStrings = selectedBillingAccount.split("\\n");
        if (accessAccountStrings.length > 1) {
            accessAccount = accessAccountStrings[1];
        }
        if (billingAccountStrings.length > 1) {
            billingAccount = billingAccountStrings[1];
        }
        ExtendedRegisterUserRequest<RegisterAOLProfileResponse> registerOnlineProfileRequest = new ExtendedRegisterUserRequest<>(registerProfileDetail, accessAccount, billingAccount, password, responseListener);
        ServiceClient serviceClient = new ServiceClient(BuildConfigHelper.INSTANCE.getNCipherServerPath(), registerOnlineProfileRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void createPassword(String password, ExtendedResponseListener<CreatePasswordResult> registrationResponseLister) {
        CreatePasswordRequest<CreatePasswordResult> createPasswordRequest = new CreatePasswordRequest<>(password, registrationResponseLister);
        ServiceClient serviceClient = new ServiceClient(createPasswordRequest);
        serviceClient.submitRequest();
    }

    public void decoupleRegistration(RegisterProfileDetail registerProfileDetail, ExtendedResponseListener<RegisterAOLProfileResponse> registrationResultListener) {
        DecoupleRegistrationRequest<RegisterAOLProfileResponse> decoupleRegistrationRequest = new DecoupleRegistrationRequest<>(registerProfileDetail, registrationResultListener);
        new ServiceClient(BuildConfigHelper.INSTANCE.getNCipherServerPath(), decoupleRegistrationRequest).submitRequest();
    }
}