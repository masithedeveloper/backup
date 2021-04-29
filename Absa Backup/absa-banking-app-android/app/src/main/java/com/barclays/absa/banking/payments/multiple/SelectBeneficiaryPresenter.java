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
package com.barclays.absa.banking.payments.multiple;

import androidx.annotation.NonNull;

import com.barclays.absa.banking.boundary.model.AccountList;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
import com.barclays.absa.banking.boundary.model.ClientAgreementDetails;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.payments.services.multiple.MultipleBeneficiaryPaymentInteractor;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.FilterAccountList;

import java.util.ArrayList;
import java.util.List;

import static com.barclays.absa.banking.paymentsRewrite.ui.multiple.MultiplePaymentsViewModel.MAX_ALLOWED_BUSINESS_BENEFICIARIES;
import static com.barclays.absa.banking.paymentsRewrite.ui.multiple.MultiplePaymentsViewModel.MAX_ALLOWED_RETAIL_BENEFICIARIES;

public class SelectBeneficiaryPresenter implements SelectBeneficiaryPresenterInterface {
    private MultipleBeneficiaryPaymentInteractor interactor;
    private AccountObject accountObject;
    private final MultipleBeneficiarySelectionView view;
    private final int maximumAllowedBeneficiaries;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);
    private final ExtendedResponseListener<AccountList> accountListExtendedResponseListener = new ExtendedResponseListener<AccountList>() {

        @Override
        public void onSuccess(AccountList response) {
            if (view != null) {
                view.dismissProgressDialog();
                AbsaCacheManager.getInstance().updateAccountList(response.getAccountsList());
                ArrayList<AccountObject> accountObjectList = AbsaCacheManager.getInstance().filterFromAccountList(FilterAccountList.REQ_TYPE_PAYMENT);
                ArrayList<AccountObject> transactionalAndCreditAccounts = FilterAccountList.getTransactionalAndCreditAccounts(accountObjectList);
                if (accountObjectList != null && transactionalAndCreditAccounts.size() > 1) {
                    view.openFromAccountChooserActivity();
                } else {
                    if (accountObjectList != null && !accountObjectList.isEmpty()) {
                        accountObject = accountObjectList.get(0);
                    }
                    interactor.clientAgreementDetails(clientAgreementDetailsExtendedResponseListener);
                }
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            if (view != null) {
                view.dismissProgressDialog();
                if (appCacheService.hasErrorResponse()) {
                    view.checkDeviceState();
                }
            }
        }
    };

    private ExtendedResponseListener<ClientAgreementDetails> clientAgreementDetailsExtendedResponseListener = new ExtendedResponseListener<ClientAgreementDetails>() {
        @Override
        public void onSuccess(final ClientAgreementDetails successResponse) {
            if (view != null) {
                view.dismissProgressDialog();
                view.autoPopulateSingleAccount(successResponse, accountObject);
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            if (view != null) {
                view.dismissProgressDialog();
                if (appCacheService.hasErrorResponse()) {
                    view.checkDeviceState();
                }
            }
        }
    };

    public SelectBeneficiaryPresenter(@NonNull MultipleBeneficiarySelectionView view) {
        this.view = view;
        accountListExtendedResponseListener.setView(view);
        clientAgreementDetailsExtendedResponseListener.setView(view);
        interactor = new MultipleBeneficiaryPaymentInteractor();
        maximumAllowedBeneficiaries = view.isBusinessAccount() ? MAX_ALLOWED_BUSINESS_BENEFICIARIES : MAX_ALLOWED_RETAIL_BENEFICIARIES;
    }

    @Override
    public void onBeneficiaryClicked(int selectedPosition, List<BeneficiaryObject> selectedBeneficiaryList) {
        if (view != null) {
            BeneficiaryObject beneficiaryObject = view.getSectionListBeneficiary(selectedPosition);
            if (beneficiaryObject != null) {
                if (!selectedBeneficiaryList.contains(beneficiaryObject)) {
                    if (selectedBeneficiaryList.size() < maximumAllowedBeneficiaries) {
                        selectedBeneficiaryList.add(beneficiaryObject);
                    }
                    view.notifyOnItemSelection();
                } else {
                    int position = selectedBeneficiaryList.indexOf(beneficiaryObject);
                    selectedBeneficiaryList.remove(beneficiaryObject);
                    view.notifyOnItemDeselection(position);
                }
                toggleContinueButton(selectedBeneficiaryList, view);
            }
        }
    }

    @Override
    public void onSelectedBeneficiaryRemoveIconClicked(int position, List<BeneficiaryObject> selectedBeneficiaryList) {
        if (0 <= position && position < selectedBeneficiaryList.size()) {
            if (view != null) {
                BeneficiaryObject beneficiaryObject = selectedBeneficiaryList.get(position);
                if (selectedBeneficiaryList.contains(beneficiaryObject)) {
                    selectedBeneficiaryList.remove(beneficiaryObject);
                    view.notifyOnItemDeselection(position);
                }
            }
            toggleContinueButton(selectedBeneficiaryList, view);
        }
    }

    private void toggleContinueButton(List<BeneficiaryObject> selectedBeneficiaryList, MultipleBeneficiarySelectionView multipleBeneficiarySelectionView) {
        if (isAnyBeneficiarySelected(selectedBeneficiaryList)) {
            multipleBeneficiarySelectionView.toggleContinueButton(true);
        } else {
            multipleBeneficiarySelectionView.toggleContinueButton(false);
        }
    }

    private boolean isAnyBeneficiarySelected(List<BeneficiaryObject> selectedBeneficiaryList) {
        return selectedBeneficiaryList.size() > 0;
    }

    @Override
    public void getAccountList() {
        if (view != null) {
            ArrayList<AccountObject> accountObjectList = AbsaCacheManager.getInstance().filterFromAccountList(FilterAccountList.REQ_TYPE_PAYMENT);
            ArrayList<AccountObject> transactionalAndCreditAccounts = FilterAccountList.getTransactionalAndCreditAccounts(accountObjectList);
            if (transactionalAndCreditAccounts != null && transactionalAndCreditAccounts.size() > 1) {
                getAccountObject(accountObjectList);
                view.openFromAccountChooserActivity();
            } else {
                getAccountObject(accountObjectList);
                interactor.clientAgreementDetails(clientAgreementDetailsExtendedResponseListener);
            }
        }
    }

    private void getAccountObject(List<AccountObject> accountObjectList) {
        if (accountObjectList != null && !accountObjectList.isEmpty()) {
            accountObject = accountObjectList.get(0);
        }
    }

    @Override
    public void onBeneficiaryListFiltered(List<BeneficiaryObject> paymentBeneficiaryList) {
        if (view != null) {
            if (paymentBeneficiaryList.isEmpty()) {
                view.showNoBeneficiaryContainer();
            } else {
                view.showBeneficiaryContainer();
            }
        }
    }

}