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

package com.barclays.absa.banking.presentation.homeLoan;

import com.barclays.absa.banking.boundary.model.AccountDetail;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.funeralCover.services.InsurancePolicyInteractor;

import java.lang.ref.WeakReference;

public class HomeLoanPerilsAccountHistoryPresenter {
    private WeakReference<HomeLoanPerilsAccountHistoryView> historyViewWeakReference;
    private InsurancePolicyInteractor insurancePolicyInteractor;

    private ExtendedResponseListener<AccountDetail> accountDetailExtendedResponseListener = new ExtendedResponseListener<AccountDetail>() {
        @Override
        public void onSuccess(final AccountDetail successResponse) {
            HomeLoanPerilsAccountHistoryView perilsAccountHistoryView = historyViewWeakReference.get();
            if (perilsAccountHistoryView != null) {
                perilsAccountHistoryView.dismissProgressDialog();
                if (successResponse != null) {
                    perilsAccountHistoryView.displayFetchedTransactionHistory(successResponse);
                } else {
                    perilsAccountHistoryView.showFailureScreen();
                }
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            HomeLoanPerilsAccountHistoryView perilsAccountHistoryView = historyViewWeakReference.get();
            perilsAccountHistoryView.dismissProgressDialog();
            perilsAccountHistoryView.showFailureScreen();
        }
    };

    HomeLoanPerilsAccountHistoryPresenter(HomeLoanPerilsAccountHistoryView homeLoanPerilsAccountHistoryView) {
        historyViewWeakReference = new WeakReference<>(homeLoanPerilsAccountHistoryView);
        insurancePolicyInteractor = new InsurancePolicyInteractor();
    }

    void fetchHomeLoanTransactionHistory(String dateFrom, String dateTo, AccountObject accountObject) {
        insurancePolicyInteractor.fetchHomeLoanAccountHistory(dateFrom, dateTo, accountObject, accountDetailExtendedResponseListener);
    }
}