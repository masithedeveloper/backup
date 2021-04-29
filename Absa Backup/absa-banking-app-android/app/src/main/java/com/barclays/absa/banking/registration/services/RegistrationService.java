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
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.registration.services.dto.Create2faAliasResponse;
import com.barclays.absa.banking.registration.services.dto.CreatePasswordResult;
import com.barclays.absa.banking.registration.services.dto.RegisterAOLProfileResponse;
import com.barclays.absa.banking.registration.services.dto.RegisterCredentialsResponse;
import com.barclays.absa.banking.registration.services.dto.TermsAndConditionObject;

public interface RegistrationService {
    String OP0815_CREATE_2FA_ALIAS_UNAUTHENTICATED_OPCODE = "OP0815";
    String OP0821_CREATE_2FA_ALIAS_OPCODE = "OP0821";

    void getCustomerProfileDetails(String cardNumber, String atmPin, String serverPath, ExtendedResponseListener<RegisterProfileDetail> responseListener);
    void passcodeReset(String passcode, ExtendedResponseListener<RegisterCredentialsResponse> responseListener);
    void create2faAlias(ExtendedResponseListener<Create2faAliasResponse> responseListener);
    void registerCredentials(String passcode, String randomId, ExtendedResponseListener<RegisterCredentialsResponse> responseListener);
    void fetchTermsOfUserDetails(String clientType, ExtendedResponseListener<TermsAndConditionObject> responseListener);
    void registerOnlineProfile(RegisterProfileDetail registerProfileDetail, String password, ExtendedResponseListener<RegisterAOLProfileResponse> responseListener);
    void createPassword(String password, ExtendedResponseListener<CreatePasswordResult> registrationResponseLister);
}
