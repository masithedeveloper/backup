/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.card.ui.creditCard.vcl;

import com.barclays.absa.banking.boundary.model.BankBranches;
import com.barclays.absa.banking.boundary.model.BankDetails;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.payments.services.PaymentsInteractor;

import java.lang.ref.WeakReference;

class CreditCardVCLIncomeSourcePresenter {
    private WeakReference<CreditCardVCLIncomeSourceView> weakReference;
    private CreditCardVCLIncomeSourceView view;
    private PaymentsInteractor paymentsService;

    private ExtendedResponseListener<BankDetails> bankExtendedResponseListener = new ExtendedResponseListener<BankDetails>() {
        @Override
        public void onSuccess(BankDetails successResponse) {
            view = weakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                view.navigateToBankListLayout(successResponse);
            }
        }
    };

    private ExtendedResponseListener<BankBranches> branchesExtendedResponseListener = new ExtendedResponseListener<BankBranches>() {
        @Override
        public void onSuccess(BankBranches successResponse) {
            view = weakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                view.navigateToBranchList(successResponse);
            }
        }
    };

    CreditCardVCLIncomeSourcePresenter(CreditCardVCLIncomeSourceView view, PaymentsInteractor paymentsService) {
        weakReference = new WeakReference<>(view);
        this.paymentsService = paymentsService;
        bankExtendedResponseListener.setView(view);
        branchesExtendedResponseListener.setView(view);
    }

    public void onBankSelectorClicked() {
        paymentsService.fetchBankList(bankExtendedResponseListener);
    }

    public void onBranchSelectorClicked(String bankName) {
        paymentsService.fetchBranchList(bankName, branchesExtendedResponseListener);
    }

    public void onAccountTypeSelectorClick() {
        view = weakReference.get();
        if (view != null) {
            view.navigateToAccountTypeSelector();
        }
    }

    void onContinueToAdjustCreditLimitScreen() {
        view = weakReference.get();
        if (view != null) {
            view.navigateToAdjustCreditLimitScreen();
        }
    }
}
