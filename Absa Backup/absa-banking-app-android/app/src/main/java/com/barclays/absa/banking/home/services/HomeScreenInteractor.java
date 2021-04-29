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
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject;
import com.barclays.absa.banking.boundary.model.Entry;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.boundary.model.policy.PolicyDetail;
import com.barclays.absa.banking.boundary.model.policy.PolicyList;
import com.barclays.absa.banking.framework.AbstractInteractor;
import com.barclays.absa.banking.framework.BaseView;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.MockFactory;
import com.barclays.absa.banking.framework.ServiceClient;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.data.cache.IRewardsCacheService;
import com.barclays.absa.banking.framework.push.CustomTags;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.home.services.clickToCall.dto.CallBackRequest;
import com.barclays.absa.banking.home.services.clickToCall.dto.CallBackResponse;
import com.barclays.absa.banking.home.services.clickToCall.dto.CallBackVerificationDataModel;
import com.barclays.absa.banking.home.services.clickToCall.dto.CallBackVerificationRequest;
import com.barclays.absa.banking.home.services.clickToCall.dto.CallBackVerificationResponse;
import com.barclays.absa.banking.home.services.dto.AccountHistoryRequest;
import com.barclays.absa.banking.home.services.dto.PolicyInformationRequest;
import com.barclays.absa.banking.home.services.dto.PolicyListRequest;
import com.barclays.absa.banking.home.services.dto.ProfileImageRequest;
import com.barclays.absa.banking.home.ui.FetchPolicyAuthorizationsFromPasscodeLogin;
import com.barclays.absa.banking.home.ui.IHomeCacheService;
import com.barclays.absa.banking.model.SecureHomePageRequest;
import com.barclays.absa.banking.passcode.passcodeLogin.PasscodeLoginView;
import com.barclays.absa.banking.presentation.transactions.AccountRefreshInterface;
import com.barclays.absa.banking.settings.services.ProfileImageDownloadParser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeScreenInteractor extends AbstractInteractor implements HomeScreenService {

    private static final String TAG = HomeScreenInteractor.class.getSimpleName();
    private BaseView view;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);
    private final IHomeCacheService homeCacheService = DaggerHelperKt.getServiceInterface(IHomeCacheService.class);
    private final IRewardsCacheService rewardsCacheService = DaggerHelperKt.getServiceInterface(IRewardsCacheService.class);

    public HomeScreenInteractor() {

    }

    public HomeScreenInteractor(BaseView baseView) {
        view = baseView;
    }

    @Override
    public void fetchHomeScreenData(List<ExtendedRequest> requests) {
        submitQueuedRequests(requests);
    }

    @Override
    public void fetchProfileImage(String timestamp, String mimeType, ExtendedResponseListener<AddBeneficiaryObject> profileImageResponseListener) {
        BMBLogger.d(TAG + "x-req - fetching profile image", "@" + new Date().getTime());
        ProfileImageRequest<AddBeneficiaryObject> profileImageRequest = new ProfileImageRequest<>(timestamp, mimeType, profileImageResponseListener);
        profileImageRequest.setResponseParser(new ProfileImageDownloadParser());
        submitRequest(profileImageRequest);
    }

    @Override
    public void fetchPolicyInformation(String policyType, String policyNumber, ExtendedResponseListener<PolicyDetail> policyInformationResponseListener) {
        BMBLogger.d(TAG + " -  fetching policy information", "@" + new Date().getTime());
        PolicyInformationRequest<PolicyDetail> policyInformationRequest = new PolicyInformationRequest<>(policyType, policyNumber, policyInformationResponseListener);
        submitRequest(policyInformationRequest);
    }

    @Override
    public void requestCallBack(String secretCode, String callBackDateTime, ExtendedResponseListener<CallBackResponse> callBackRequestExtendedResponseListener) {
        CallBackRequest<CallBackResponse> request = new CallBackRequest<>(secretCode, callBackDateTime, callBackRequestExtendedResponseListener);
        submitRequest(request, MockFactory.requestCallBack());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void verifyCallBack(CallBackVerificationDataModel callBackVerificationDataModel, ExtendedResponseListener<CallBackVerificationResponse> callBackRequestExtendedResponseListener) {
        CallBackVerificationRequest request = new CallBackVerificationRequest(callBackVerificationDataModel, callBackRequestExtendedResponseListener);
        submitRequest(request, MockFactory.verifyCallBackCode());
    }

    @Override
    public void fetchHomeLoanAccountData(AccountHistoryRequest<AccountDetail> accountHistoryClearedTransactionsRequest, AccountHistoryRequest<AccountDetail> accountHistoryUnclearedTransactionsRequest, PolicyListRequest<PolicyList> policyListRequest) {
        submitQueuedRequests(accountHistoryClearedTransactionsRequest, accountHistoryUnclearedTransactionsRequest, policyListRequest);
    }

    private void fetchSecureHomePageData(ExtendedResponseListener<SecureHomePageObject> homePageResponseListener) {
        SecureHomePageRequest<SecureHomePageObject> secureHomePageRequest = new SecureHomePageRequest<>(homePageResponseListener);
        ServiceClient serviceClient = new ServiceClient(secureHomePageRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void refreshHomeScreenAccountsAndBalances(AccountRefreshInterface accountRefreshInterface) {
        ExtendedResponseListener<SecureHomePageObject> homePageResponseListener = new ExtendedResponseListener<SecureHomePageObject>() {

            @Override
            public void onSuccess(final SecureHomePageObject secureHomePageObject) {
                accountRefreshInterface.onSuccess();
            }

            @Override
            public void onFailure(final ResponseObject response) {
                accountRefreshInterface.onFailure();
            }
        };

        fetchSecureHomePageData(homePageResponseListener);
    }

    @Override
    public void loadPoliciesAndAuthorizations(String shortcut, CustomTags customTags, PasscodeLoginView passcodeLoginView) {
        new FetchPolicyAuthorizationsFromPasscodeLogin().fetch(shortcut, customTags, false, passcodeLoginView);
    }

    @Override
    public void refreshAccountList() {
        SecureHomePageObject secureHomePageObject = appCacheService.getSecureHomePageObject();
        if (secureHomePageObject == null) {
            view.showRelogRequiredScreen();
            return;
        }

        final List<Entry> entries = new ArrayList<>();
        final List<Entry> filteredEntries = new ArrayList<>();

        List<AccountObject> accounts = secureHomePageObject.getAccounts();

        if (!accounts.isEmpty()) {
            entries.clear();
            entries.addAll(accounts);
        }

        filteredEntries.clear();
        for (Entry entry : entries) {
            boolean isValidAccount = false;
            switch (entry.getEntryType()) {
                case Entry.HEADER:
                    break;
                case Entry.ACCOUNT:
                    isValidAccount = true;
                    break;
                case Entry.POLICY:
                    isValidAccount = true;
                    break;
                case Entry.OFFERS:
                    break;
                default:
                    isValidAccount = false;
            }

            if (isValidAccount) {
                AccountObject accountObject = (AccountObject) entry;
                if ("absaReward".equalsIgnoreCase(accountObject.getAccountType())) {
                    appCacheService.setHasRewardsAccount(true);
                    rewardsCacheService.setRewardsAccount(accountObject);
                }
                filteredEntries.add(entry);
            }
        }
        homeCacheService.setFilteredAccounts(filteredEntries);
    }

}