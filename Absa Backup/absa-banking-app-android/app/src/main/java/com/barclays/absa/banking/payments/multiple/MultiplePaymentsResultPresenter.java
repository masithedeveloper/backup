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

import com.barclays.absa.banking.payments.services.multiple.dto.PaymentDetailResult;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.barclays.absa.banking.framework.app.BMBConstants.SUCCESS;

class MultiplePaymentsResultPresenter {
    private WeakReference<MultiplePaymentsResultView> weakReference;

    MultiplePaymentsResultPresenter(MultiplePaymentsResultView multiplePaymentPartialSuccessView) {
        weakReference = new WeakReference<>(multiplePaymentPartialSuccessView);
    }

    void onHomeButtonClicked() {
        MultiplePaymentsResultView view = weakReference.get();
        if (view != null) {
            view.loadAccountsAndGoHome();
        }
    }

    List<PaymentDetailResult> filterBySuccessfulPayments(List<PaymentDetailResult> paymentDetailResultsList) {
        List<PaymentDetailResult> resultArrayList = new ArrayList<>();
        for (PaymentDetailResult paymentDetailResult : paymentDetailResultsList) {
            if (SUCCESS.equalsIgnoreCase(paymentDetailResult.getResult())) {
                resultArrayList.add(paymentDetailResult);
            }
        }
        return resultArrayList;
    }

    List<PaymentDetailResult> filterByFailedPayments(List<PaymentDetailResult> paymentDetailResultsList) {
        List<PaymentDetailResult> resultArrayList = new ArrayList<>();
        for (PaymentDetailResult paymentDetailResult : paymentDetailResultsList) {
            if (!SUCCESS.equalsIgnoreCase(paymentDetailResult.getResult())) {
                resultArrayList.add(paymentDetailResult);
            }
        }
        return resultArrayList;
    }

    List<PaymentDetailResult> getFilteredBeneficiaryPaymentList(List<PaymentDetailResult> paymentList, String paymentType) {
        List<PaymentDetailResult> filteredPaymentList = new ArrayList<>();
        for (PaymentDetailResult paymentDetailResult : paymentList) {
            if (paymentType.equalsIgnoreCase(paymentDetailResult.getNowFlag())) {
                filteredPaymentList.add(paymentDetailResult);
            }
        }
        return filteredPaymentList;
    }

    void onImportantNoticeClicked() {
        MultiplePaymentsResultView view = weakReference.get();
        if (view != null) {
            view.navigateToImportantNoticeScreen();
        }
    }
}