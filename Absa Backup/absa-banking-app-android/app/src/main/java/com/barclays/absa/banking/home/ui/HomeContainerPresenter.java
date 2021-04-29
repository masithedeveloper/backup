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

package com.barclays.absa.banking.home.ui;

import com.barclays.absa.banking.boundary.model.AccountDetail;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.policy.Policy;
import com.barclays.absa.banking.boundary.model.policy.PolicyList;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.funeralCover.services.InsurancePolicyInteractor;
import com.barclays.absa.banking.home.services.HomeScreenInteractor;
import com.barclays.absa.banking.home.services.HomeScreenService;
import com.barclays.absa.banking.home.services.clickToCall.dto.CallBackResponse;
import com.barclays.absa.banking.home.services.clickToCall.dto.CallBackVerificationDataModel;
import com.barclays.absa.banking.home.services.clickToCall.dto.CallBackVerificationResponse;
import com.barclays.absa.banking.home.services.dto.AccountHistoryRequest;
import com.barclays.absa.banking.home.services.dto.PolicyListRequest;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

class HomeContainerPresenter {
    private WeakReference<HomeContainerView> view;
    private HomeScreenService homeScreenInteractor;
    private PolicyExtendedResponseListener policyInformationResponseListener;
    private IHomeCacheService homeCacheService = DaggerHelperKt.getServiceInterface(IHomeCacheService.class);

    private ExtendedResponseListener<CallBackResponse> callBackResponseListener = new ExtendedResponseListener<CallBackResponse>() {
        @Override
        public void onSuccess(CallBackResponse successResponse) {
            super.onSuccess();
            HomeContainerView homeContainerView = view.get();
            if (homeContainerView != null) {
                homeContainerView.navigateToCallMeBackFragment(null, successResponse.getUniqueReferenceNumber());
                homeContainerView.dismissProgressDialog();
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            super.onFailure(failureResponse);
            HomeContainerView homeContainerView = view.get();
            if (homeContainerView != null) {
                homeContainerView.dismissProgressDialog();
                homeContainerView.navigateToCallMeBackFailureScreen();
            }
        }
    };

    private ExtendedResponseListener<CallBackVerificationResponse> callBackVerificationResponseResponseListener = new ExtendedResponseListener<CallBackVerificationResponse>() {
        @Override
        public void onSuccess(CallBackVerificationResponse successResponse) {
            super.onSuccess();
            HomeContainerView homeContainerView = view.get();
            if (homeContainerView != null) {
                homeContainerView.dismissProgressDialog();
                homeContainerView.navigateToCallMeBackSuccessScreen();
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            super.onFailure(failureResponse);
            HomeContainerView homeContainerView = view.get();
            if (homeContainerView != null) {
                homeContainerView.dismissProgressDialog();
                homeContainerView.navigateToCallMeBackFailureScreen();
            }
        }
    };

    public void requestCallBack(String secretCode, String callBackDateTime) {
        HomeContainerView homeContainerView = view.get();
        if (homeContainerView != null) {
            homeContainerView.showProgressDialog();
            homeScreenInteractor.requestCallBack(secretCode, callBackDateTime, callBackResponseListener);
        }
    }

    public void requestVerificationCallBack(CallBackVerificationDataModel callBackVerificationDataModel) {
        HomeContainerView homeContainerView = view.get();
        if (homeContainerView != null) {
            homeContainerView.showProgressDialog();
        }
        homeScreenInteractor.verifyCallBack(callBackVerificationDataModel, callBackVerificationResponseResponseListener);
    }

    HomeContainerPresenter(HomeContainerView view) {
        this.view = new WeakReference<>(view);
        homeScreenInteractor = new HomeScreenInteractor();
        insurancePolicyInteractor = new InsurancePolicyInteractor();

        policyInformationResponseListener = new PolicyExtendedResponseListener();
        policyInformationResponseListener.setView(view);
        callBackVerificationResponseResponseListener.setView(view);
        callBackResponseListener.setView(view);
        accountHistoryClearedResponseListener.setView(view);
        accountHistoryUnclearedResponseListener.setView(view);
        insurancePolicyListResponseListener.setView(view);
    }

    private void showErrorDialog(ResponseObject response) {
        HomeContainerView homeContainerView = view.get();
        if (homeContainerView != null) {
            homeContainerView.showErrorDialog(ResponseObject.extractErrorMessage(response));
        }
    }

    void fetchPolicyInformation(Policy policy) {
        policyInformationResponseListener.setPolicy(policy);
        homeScreenInteractor.fetchPolicyInformation(policy.getType(), policy.getNumber(), policyInformationResponseListener);
    }

    void creditCardRequest(AccountObject accountObject) {
        final HomeContainerView homeContainerView = view.get();
        if (homeContainerView != null && accountObject != null) {
            homeContainerView.onCreditCardInformationFetched(accountObject);
        }
    }

    private ExtendedResponseListener<AccountDetail> accountHistoryClearedResponseListener = new ExtendedResponseListener<AccountDetail>() {

        @Override
        public void onSuccess(final AccountDetail accountDetail) {
            homeCacheService.setHomeLoanAccountHistoryCleared(accountDetail);
        }

        @Override
        public void onFailure(final ResponseObject response) {
        }
    };

    private ExtendedResponseListener<AccountDetail> accountHistoryUnclearedResponseListener = new ExtendedResponseListener<AccountDetail>() {

        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(final AccountDetail accountDetail) {
            homeCacheService.setHomeLoanAccountHistoryUncleared(accountDetail);
        }

        @Override
        public void onFailure(final ResponseObject response) {
        }
    };

    private InsurancePolicyInteractor insurancePolicyInteractor;
    private ExtendedResponseListener<PolicyList> insurancePolicyListResponseListener = new ExtendedResponseListener<PolicyList>() {

        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(final PolicyList policyList) {
            final HomeContainerView homeContainerView = view.get();
            List<Policy> policies = new ArrayList<>();
            if (policyList != null && policyList.getPolicies() != null) {
                policies = policyList.getPolicies();
            }
            homeCacheService.setInsurancePolicies(policies);
            if (InsuranceUtils.hasHomeOwnerPolicy()) {
                if (homeContainerView != null) {
                    Policy homeOwnerPolicy = InsuranceUtils.getHomeOwnerPolicy();
                    if (homeOwnerPolicy != null) {
                        policyInformationResponseListener.setPolicy(homeOwnerPolicy);
                        insurancePolicyInteractor.fetchPolicyDetails(homeOwnerPolicy.getType(), homeOwnerPolicy.getNumber(), policyInformationResponseListener);
                    } else {
                        homeContainerView.showGenericErrorMessage();
                    }
                }
            } else {
                if (homeContainerView != null) {
                    //pull out home loan summary services
                    final AccountDetail homeLoanAccountHistoryCleared = homeCacheService.getHomeLoanAccountHistoryCleared();
                    if (homeLoanAccountHistoryCleared != null) {
                        homeContainerView.dismissProgressDialog();
                        homeContainerView.navigateToHomeLoanAccountHub(homeLoanAccountHistoryCleared);
                    }
                }
            }
        }

        @Override
        public void onFailure(final ResponseObject response) {
            // onFailure is called when there is no insurance account
            // with message: For Absa idirect motor and household insurance, SMS "insure" to 43755.
            // For life and funeral insurance, SMS 'Life' to 31513.
            // so we ignore it
            final HomeContainerView homeContainerView = view.get();
            if (homeContainerView != null) {
                if (response.getErrorMessage() != null && response.getErrorMessage().toLowerCase().contains("idirect")) {
                    final AccountDetail homeLoanAccountHistoryCleared = homeCacheService.getHomeLoanAccountHistoryCleared();
                    if (homeLoanAccountHistoryCleared != null) {
                        homeContainerView.dismissProgressDialog();
                        homeContainerView.navigateToHomeLoanAccountHub(homeLoanAccountHistoryCleared);
                    }
                } else {
                    homeContainerView.dismissProgressDialog();
                    showErrorDialog(response);
                }
                BMBLogger.d("x-policy", response.getErrorMessage());
            }
        }
    };

    void onHomeLoanAccountCardClicked(AccountObject account) {
        if (account != null) {
            homeCacheService.setSelectedHomeLoanAccount(account);
        }
        AccountHistoryRequest<AccountDetail> accountHistoryClearedTransactionsRequest = new AccountHistoryRequest<>(account, accountHistoryClearedResponseListener, false);
        AccountHistoryRequest<AccountDetail> accountHistoryUnclearedTransactionsRequest = new AccountHistoryRequest<>(account, accountHistoryUnclearedResponseListener, true);
        PolicyListRequest<PolicyList> policyListRequest = new PolicyListRequest<>(insurancePolicyListResponseListener);
        homeScreenInteractor.fetchHomeLoanAccountData(accountHistoryClearedTransactionsRequest, accountHistoryUnclearedTransactionsRequest, policyListRequest);
    }
}