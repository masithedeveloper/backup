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
package com.barclays.absa.banking.home.services;

import com.barclays.absa.banking.boundary.model.AccountDetail;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject;
import com.barclays.absa.banking.boundary.model.policy.PolicyDetail;
import com.barclays.absa.banking.boundary.model.policy.PolicyList;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.push.CustomTags;
import com.barclays.absa.banking.home.services.clickToCall.dto.CallBackResponse;
import com.barclays.absa.banking.home.services.clickToCall.dto.CallBackVerificationDataModel;
import com.barclays.absa.banking.home.services.clickToCall.dto.CallBackVerificationResponse;
import com.barclays.absa.banking.home.services.dto.AccountHistoryRequest;
import com.barclays.absa.banking.home.services.dto.PolicyListRequest;
import com.barclays.absa.banking.passcode.passcodeLogin.PasscodeLoginView;
import com.barclays.absa.banking.presentation.transactions.AccountRefreshInterface;

import java.util.List;

public interface HomeScreenService {
    String OP0849_LINKED_POLICIES = "OP0849";
    String OP0915_REDEEM_REWARDS_TRANSACTION_HISTORY = "OP0915";
    String OP0993_ACCOUNT_REORDERING = "OP0993";
    String OP1301_ACCOUNT_DETAILS = "OP1301";

    void fetchProfileImage(String timestamp, String mimeType, ExtendedResponseListener<AddBeneficiaryObject> profileImageResponse);

    void fetchPolicyInformation(String policyType, String policyNumber, ExtendedResponseListener<PolicyDetail> policyInformationResponseListener);

    void fetchHomeScreenData(List<ExtendedRequest> requests);

    void requestCallBack(String secretCode, String callBackDateTime, ExtendedResponseListener<CallBackResponse> callBackRequestExtendedResponseListener);

    void verifyCallBack(CallBackVerificationDataModel callBackVerificationDataModel, ExtendedResponseListener<CallBackVerificationResponse> callBackRequestExtendedResponseListener);

    void fetchHomeLoanAccountData(AccountHistoryRequest<AccountDetail> accountHistoryClearedTransactionsRequest,
                                  AccountHistoryRequest<AccountDetail> accountHistoryUnclearedTransactionsRequest,
                                  PolicyListRequest<PolicyList> policyListRequest);

    void refreshHomeScreenAccountsAndBalances(AccountRefreshInterface accountRefreshInterface);

    void loadPoliciesAndAuthorizations(String shortcut, CustomTags customTags, PasscodeLoginView passcodeLoginView);

    void refreshAccountList();
}
