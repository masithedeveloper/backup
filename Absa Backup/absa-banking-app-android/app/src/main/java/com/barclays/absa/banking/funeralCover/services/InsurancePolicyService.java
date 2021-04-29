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

package com.barclays.absa.banking.funeralCover.services;

import com.barclays.absa.banking.boundary.model.policy.PolicyClaimTypes;
import com.barclays.absa.banking.boundary.model.policy.PolicyDetail;
import com.barclays.absa.banking.boundary.model.policy.PolicyList;
import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.funeralCover.ui.ChangePaymentDetails;

public interface InsurancePolicyService {
    String OP0848_CHANGE_PAYMENT_DETAILS_OPCODE = "OP0848";
    String OP2205_FETCH_POLICY_CLAIM_TYPES = "OP2205";
    String OP2184_CHANGE_EXERGY_PAYMENT_DETAILS_OPCODE = "OP2184";

    void fetchInsurancePolicies(ExtendedResponseListener<PolicyList> policyListResponseListener);
    void fetchPolicyDetails(String policyType, String policyNumber, ExtendedResponseListener<PolicyDetail> listener);
    void changePolicyPaymentDetails(ChangePaymentDetails changePaymentDetails, ExtendedResponseListener<SureCheckResponse> responseListener);
    void fetchPolicyClaimTypes(String policyNumber, ExtendedResponseListener<PolicyClaimTypes> responseListener);
    void changeExergyPolicyPaymentDetails(ChangePaymentDetails changePaymentDetails, ExtendedResponseListener<SureCheckResponse> responseListener);

}
