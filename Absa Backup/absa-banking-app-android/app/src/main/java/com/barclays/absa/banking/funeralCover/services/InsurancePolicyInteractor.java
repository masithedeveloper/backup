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

import com.barclays.absa.banking.account.services.dto.HomeLoanAccountListRequest;
import com.barclays.absa.banking.boundary.model.AccountDetail;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.AllPerils;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.boundary.model.CallMeOverview;
import com.barclays.absa.banking.boundary.model.CallMeResult;
import com.barclays.absa.banking.boundary.model.PolicyClaim;
import com.barclays.absa.banking.boundary.model.notification.ClaimNotification;
import com.barclays.absa.banking.boundary.model.notification.SubmitClaim;
import com.barclays.absa.banking.boundary.model.policy.PolicyClaimTypes;
import com.barclays.absa.banking.boundary.model.policy.PolicyDetail;
import com.barclays.absa.banking.boundary.model.policy.PolicyList;
import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse;
import com.barclays.absa.banking.framework.AbstractInteractor;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.funeralCover.services.dto.AllPerilsRequest;
import com.barclays.absa.banking.funeralCover.services.dto.BeneficiaryNotificationDetailRequest;
import com.barclays.absa.banking.funeralCover.services.dto.ChangeExergyPolicyPaymentRequest;
import com.barclays.absa.banking.funeralCover.services.dto.ChangePolicyPaymentRequest;
import com.barclays.absa.banking.funeralCover.services.dto.ClaimNotificationRequest;
import com.barclays.absa.banking.funeralCover.services.dto.PolicyClaimTypesRequest;
import com.barclays.absa.banking.funeralCover.services.dto.PolicyDetailRequest;
import com.barclays.absa.banking.funeralCover.services.dto.PolicyListRequest;
import com.barclays.absa.banking.funeralCover.services.dto.SubmitClaimNotificationRequest;
import com.barclays.absa.banking.funeralCover.ui.ChangePaymentDetails;
import com.barclays.absa.banking.funeralCover.ui.PolicyClaimItem;
import com.barclays.absa.banking.home.services.clickToCall.dto.CallMeOverviewRequest;
import com.barclays.absa.banking.home.services.clickToCall.dto.CallMeSubmitTransactionRequest;

public class InsurancePolicyInteractor extends AbstractInteractor implements InsurancePolicyService {

    @Override
    public void fetchInsurancePolicies(ExtendedResponseListener<PolicyList> policyListResponseListener) {
        PolicyListRequest<PolicyList> policyListRequest = new PolicyListRequest<>(policyListResponseListener);
        ServiceClient serviceClient = new ServiceClient(policyListRequest);
        serviceClient.submitRequest();
    }

    public void fetchPolicyDetails(String policyType, String policyNumber, ExtendedResponseListener<PolicyDetail> listener) {
        PolicyDetailRequest<PolicyDetail> policyDetailExtendedRequest = new PolicyDetailRequest<>(policyType, policyNumber, listener);
        ServiceClient serviceClient = new ServiceClient(policyDetailExtendedRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void changePolicyPaymentDetails(ChangePaymentDetails changePaymentDetails, ExtendedResponseListener<SureCheckResponse> responseListener) {
        ChangePolicyPaymentRequest<SureCheckResponse> changePolicyPaymentRequest = new ChangePolicyPaymentRequest<>(changePaymentDetails, responseListener);
        ServiceClient serviceClient = new ServiceClient(changePolicyPaymentRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void changeExergyPolicyPaymentDetails(ChangePaymentDetails changePaymentDetails, ExtendedResponseListener<SureCheckResponse> responseListener) {
        ChangeExergyPolicyPaymentRequest<SureCheckResponse> changeExergyPolicyPaymentRequest = new ChangeExergyPolicyPaymentRequest<>(changePaymentDetails, responseListener);
        ServiceClient serviceClient = new ServiceClient(changeExergyPolicyPaymentRequest);
        serviceClient.submitRequest();
    }

    public void requestCustomerDetails(ExtendedResponseListener<BeneficiaryDetailObject> extendedResponseListener) {
        BeneficiaryNotificationDetailRequest<BeneficiaryDetailObject> accountsRequest = new BeneficiaryNotificationDetailRequest<>(extendedResponseListener);
        ServiceClient serviceClient = new ServiceClient(accountsRequest);
        serviceClient.submitRequest();
    }

    public void invokeCallMeService(String phoneNumber, String emailId, ExtendedResponseListener<CallMeOverview> callMeOverviewResponseListener) {
        CallMeOverviewRequest<CallMeOverview> registerCallMeRequest = new CallMeOverviewRequest<>(phoneNumber, emailId, callMeOverviewResponseListener);
        ServiceClient serviceClient = new ServiceClient(registerCallMeRequest);
        serviceClient.submitRequest();
    }

    public void invokeSubmitTransactionReferenceCallMe(ResponseObject transactionRefNumber, ExtendedResponseListener<CallMeResult> callMeResultListener) {
        CallMeSubmitTransactionRequest<CallMeResult> registerCallMeRequest = new CallMeSubmitTransactionRequest<>(transactionRefNumber, callMeResultListener);
        ServiceClient serviceClient = new ServiceClient(registerCallMeRequest);
        serviceClient.submitRequest();
    }

    public void requestPolicyClaimNotification(PolicyClaimItem policyClaimItem, ExtendedResponseListener<ClaimNotification> notificationResponseListener) {
        ClaimNotificationRequest<ClaimNotification> claimNotificationClaimNotificationRequest = new ClaimNotificationRequest<>(policyClaimItem, notificationResponseListener);
        ServiceClient serviceClient = new ServiceClient(claimNotificationClaimNotificationRequest);
        serviceClient.submitRequest();
    }

    public void submitInsurancePolicyClaim(String reference, ExtendedResponseListener<SubmitClaim> claimResponseListener) {
        SubmitClaimNotificationRequest<SubmitClaim> claimNotificationRequest = new SubmitClaimNotificationRequest<>(reference, claimResponseListener);
        ServiceClient serviceClient = new ServiceClient(claimNotificationRequest);
        serviceClient.submitRequest();
    }

    public void submitPerilsClaim(PolicyClaim policyClaim, String duplicateClaim, ExtendedResponseListener<AllPerils> allPerilsResponseListener) {
        AllPerilsRequest<AllPerils> policyClaimAllPerilsRequest = new AllPerilsRequest<>(policyClaim, duplicateClaim, allPerilsResponseListener);
        ServiceClient serviceClient = new ServiceClient(policyClaimAllPerilsRequest);
        serviceClient.submitRequest();
    }

    public void fetchHomeLoanAccountHistory(String fromDate, String toDate, AccountObject accountObject, ExtendedResponseListener<AccountDetail> responseListener) {
        HomeLoanAccountListRequest<AccountDetail> homeLoanAccountListRequest = new HomeLoanAccountListRequest<>(fromDate, toDate, accountObject, responseListener);
        ServiceClient serviceClient = new ServiceClient(homeLoanAccountListRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void fetchPolicyClaimTypes(String policyNumber, ExtendedResponseListener<PolicyClaimTypes> responseListener) {
        PolicyClaimTypesRequest<PolicyClaimTypes> policyClaimTypesRequest = new PolicyClaimTypesRequest<>(policyNumber, responseListener);
        submitRequest(policyClaimTypesRequest);
    }
}
