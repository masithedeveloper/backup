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

import com.barclays.absa.banking.boundary.model.PayBeneficiaryPaymentConfirmationObject;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.payments.services.multiple.MultipleBeneficiaryPaymentInteractor;
import com.barclays.absa.banking.payments.services.multiple.dto.BeneficiaryDetails;
import com.barclays.absa.banking.payments.services.multiple.dto.MultiplePaymentsBeneficiaryList;
import com.barclays.absa.banking.payments.services.multiple.dto.PaymentDetailResult;
import com.barclays.absa.banking.payments.services.multiple.dto.PaymentResult;
import com.barclays.absa.banking.payments.services.multiple.dto.ValidateMultipleBeneficiariesPayment;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;
import com.barclays.absa.utils.AbsaCacheManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.barclays.absa.banking.framework.app.BMBConstants.AUTHORISATION_OUTSTANDING_TRANSACTION;
import static com.barclays.absa.banking.framework.app.BMBConstants.CONN_TIME_OUT;
import static com.barclays.absa.banking.framework.app.BMBConstants.PASS_PAYMENT;

public class MultiplePaymentOverviewPresenter {
    private final WeakReference<MultiplePaymentOverviewView> weakReference;
    private final MultipleBeneficiaryPaymentInteractor interactor;
    private MultiplePaymentOverviewView view;
    private final SureCheckDelegate sureCheckDelegate;

    private final ExtendedResponseListener<PaymentResult> multipleBeneficiaryPerformPaymentListener = new ExtendedResponseListener<PaymentResult>() {

        @Override
        public void onSuccess(final PaymentResult successResponse) {
            view = weakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                sureCheckDelegate.processSureCheck(view, successResponse, () -> {
                    if (BMBConstants.SUCCESS.equalsIgnoreCase(successResponse.getTransactionStatus())) {
                        AbsaCacheManager.getInstance().setBeneficiaryCacheStatus(false, PASS_PAYMENT);
                        launchResultScreen(successResponse);
                    } else if ((BMBConstants.FAILURE.equalsIgnoreCase(successResponse.getTransactionStatus()))) {
                        launchResultScreen(successResponse);
                    }
                });
            }
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            view = weakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                if (CONN_TIME_OUT.equals(failureResponse.getErrorMessage())) {
                    view.navigateToTimeOutFailureScreen();
                } else {
                    view.showMessageError(ResponseObject.extractErrorMessage(failureResponse));
                }
            }
        }
    };

    private final ExtendedResponseListener<ValidateMultipleBeneficiariesPayment> validateMultiplePaymentResponseListener = new ExtendedResponseListener<ValidateMultipleBeneficiariesPayment>() {

        @Override
        public void onSuccess(final ValidateMultipleBeneficiariesPayment successResponse) {
            view = weakReference.get();
            if (view != null) {
                if ("Failure".equalsIgnoreCase(successResponse.getTransactionStatus())) {
                    if (successResponse.getErrors() != null && successResponse.getErrors().size() > 0) {
                        view.showMessageError(successResponse.getErrors().get(0));
                    } else {
                        view.showGenericErrorMessage();
                    }
                } else {
                    view.validateSuccess(successResponse.getTransactionReferenceId());
                }
            }
        }
    };

    MultiplePaymentOverviewPresenter(MultiplePaymentOverviewView multiplePaymentOverviewView, SureCheckDelegate sureCheckDelegate) {
        weakReference = new WeakReference<>(multiplePaymentOverviewView);
        interactor = new MultipleBeneficiaryPaymentInteractor();
        view = multiplePaymentOverviewView;
        this.sureCheckDelegate = sureCheckDelegate;
        multipleBeneficiaryPerformPaymentListener.setView(multiplePaymentOverviewView);
    }

    void onPayButtonClicked(String transactionReferenceId) {
        interactor.multipleBeneficiaryPerformPayment(transactionReferenceId, multipleBeneficiaryPerformPaymentListener);
    }

    void validateMultiplePayment(MultiplePaymentsBeneficiaryList multiplePaymentsBeneficiaryList, Map<String, PayBeneficiaryPaymentConfirmationObject> beneficiaryPaymentConfirmationObjectMap) {
        interactor.validateMultipleBeneficiaryPayment(multiplePaymentsBeneficiaryList, beneficiaryPaymentConfirmationObjectMap, validateMultiplePaymentResponseListener);
    }

    List<BeneficiaryDetails> getFilteredBeneficiaryPaymentList(List<BeneficiaryDetails> beneficiaryDetailsList, String paymentType) {
        List<BeneficiaryDetails> FilteredBeneficiaryPaymentList = new ArrayList<>();
        for (BeneficiaryDetails paymentConfirmationObject : beneficiaryDetailsList) {
            if (paymentType.equalsIgnoreCase(paymentConfirmationObject.getNowFlg())) {
                FilteredBeneficiaryPaymentList.add(paymentConfirmationObject);
            }
        }
        return FilteredBeneficiaryPaymentList;
    }

    private void launchResultScreen(PaymentResult paymentResult) {
        List<PaymentDetailResult> paymentResultList = paymentResult.getPaymentResultList();
        view = weakReference.get();
        if (view != null) {
            if (paymentResultList != null && !paymentResultList.isEmpty()) {
                view.navigateToResultScreen(paymentResultList, checkForAuthorisationPending(paymentResultList));
            }
        }
    }

    private boolean checkForAuthorisationPending(List<PaymentDetailResult> paymentDetailResultsList) {
        for (PaymentDetailResult paymentDetailResult : paymentDetailResultsList) {
            if (AUTHORISATION_OUTSTANDING_TRANSACTION.equalsIgnoreCase(paymentDetailResult.getWarningMessage())) {
                return true;
            }
        }
        return false;
    }
}