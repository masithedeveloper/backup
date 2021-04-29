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
package com.barclays.absa.banking.beneficiaries.ui;

import com.barclays.absa.banking.beneficiaries.services.BeneficiariesInteractor;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryCashSendConfirmationObject;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryPaymentObject;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryResult;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.boundary.model.MeterNumberObject;
import com.barclays.absa.banking.boundary.model.beneficiary.BeneficiaryRemove;
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse;
import com.barclays.absa.banking.buy.services.airtime.PrepaidInteractor;
import com.barclays.absa.banking.cashSend.services.CashSendInteractor;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.payments.services.PaymentsInteractor;
import com.barclays.absa.banking.payments.services.PaymentsService;

import java.lang.ref.WeakReference;

import static com.barclays.absa.banking.framework.app.BMBConstants.EDIT_BENEFICIARY_CONST;
import static com.barclays.absa.banking.framework.app.BMBConstants.MANAGE_CASHSEND_BENEFICIARIES_CONST;
import static com.barclays.absa.banking.framework.app.BMBConstants.SUCCESS;
import static com.barclays.absa.banking.framework.app.BMBConstants.TRUE_CONST;

class EditBeneficiaryPresenter {

    private WeakReference<EditBeneficiaryView> weakReference;
    private EditBeneficiaryView view;
    private PaymentsService paymentsService = new PaymentsInteractor();
    private BeneficiaryDetailObject beneficiaryObject;

    private ExtendedResponseListener<AddBeneficiaryPaymentObject> addBeneficiaryExtendedResponseListener = new ExtendedResponseListener<AddBeneficiaryPaymentObject>() {
        @Override
        public void onSuccess(AddBeneficiaryPaymentObject successResponse) {
            view = weakReference.get();
            if (view != null) {
                view.paymentUpdateConfirmation(successResponse);
            }
        }
    };

    private ExtendedResponseListener<AddBeneficiaryPaymentObject> addBeneficiaryConfirmationExtendedResponseListener = new ExtendedResponseListener<AddBeneficiaryPaymentObject>() {
        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(final AddBeneficiaryPaymentObject successResponse) {
            view = weakReference.get();
            if (view != null && successResponse != null) {
                view.dismissProgressDialog();
                view.showResultActivity("", SUCCESS.equalsIgnoreCase(successResponse.getStatus()));
            }
        }
    };

    private ExtendedResponseListener<BeneficiaryRemove> deleteBeneficiaryExtendedResponseListener = new ExtendedResponseListener<BeneficiaryRemove>() {
        @Override
        public void onSuccess(final BeneficiaryRemove successResponse) {
            view = weakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                view.deleteBeneficiary(successResponse);
            }
        }
    };

    private ExtendedResponseListener<AddBeneficiaryCashSendConfirmationObject> addCashSendBeneficiaryExtendedResponseListener = new ExtendedResponseListener<AddBeneficiaryCashSendConfirmationObject>() {
        @Override
        public void onSuccess(AddBeneficiaryCashSendConfirmationObject successResponse) {
            new CashSendInteractor().performAddCashSendBeneficiary(true, successResponse.getTxnRefNo(), "N", addCashSendBeneficiaryConfirmationExtendedResponseListener);
        }
    };

    private ExtendedResponseListener<AddBeneficiaryCashSendConfirmationObject> addCashSendBeneficiaryConfirmationExtendedResponseListener = new ExtendedResponseListener<AddBeneficiaryCashSendConfirmationObject>() {

        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(AddBeneficiaryCashSendConfirmationObject successResponse) {
            view = weakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                if (BMBConstants.SUCCESS.equalsIgnoreCase(successResponse.getStatus())) {
                    AnalyticsUtils.getInstance().trackActionSuccess(BMBConstants.EDIT_CASHSEND_BENEFICIARY_CONST, MANAGE_CASHSEND_BENEFICIARIES_CONST, EDIT_BENEFICIARY_CONST, TRUE_CONST);
                    view.showResultActivity("", true);
                } else {
                    view.showResultActivity(successResponse.getMsg(), false);
                }
            }
        }
    };

    private ExtendedResponseListener<AddBeneficiaryResult> addPrepaidBeneficiaryExtendedResponseListener = new ExtendedResponseListener<AddBeneficiaryResult>() {

        @Override
        public void onSuccess(AddBeneficiaryResult addBeneficiaryResult) {
            view = weakReference.get();
            if (view != null) {
                onPrepaidEditRequestCompleted(addBeneficiaryResult.getTransactionReferenceNumber());
            }
        }
    };

    private ExtendedResponseListener<AddBeneficiaryResult> prepaidBeneficiaryConfirmationExtendedResponseListener = new ExtendedResponseListener<AddBeneficiaryResult>() {

        @Override
        public void onSuccess(AddBeneficiaryResult addBeneficiaryResult) {
            view = weakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                view.showResultActivity("", true);
            }
        }

        @Override
        public void onRequestStarted() {
        }
    };

    private ExtendedResponseListener<TransactionResponse> editPrepaidElectricityBeneficiaryExtendedResponseListener = new ExtendedResponseListener<TransactionResponse>() {
        @Override
        public void onSuccess(TransactionResponse successResponse) {
            view = weakReference.get();
            if (view != null) {
                if (BMBConstants.SUCCESS.equalsIgnoreCase(successResponse.getTransactionStatus())) {
                    if (beneficiaryObject.getHasImage()) {
                        new BeneficiariesInteractor().updateBeneficiaryImage(beneficiaryObject, editPrepaidElectricityBeneficiaryPictureExtendedResponseListener);
                    } else {
                        view.dismissProgressDialog();
                        view.showEditBeneficiarySuccessfulScreen();
                    }
                } else if (BMBConstants.FAILURE.equalsIgnoreCase(successResponse.getTransactionStatus())) {
                    view.showEditBeneficiaryFailureScreen(successResponse.getTransactionMessage());
                }
            }
        }
    };

    EditBeneficiaryPresenter(EditBeneficiaryView view) {
        weakReference = new WeakReference<>(view);
        addBeneficiaryExtendedResponseListener.setView(view);
        addBeneficiaryConfirmationExtendedResponseListener.setView(view);
        deleteBeneficiaryExtendedResponseListener.setView(view);
        addCashSendBeneficiaryExtendedResponseListener.setView(view);
        prepaidBeneficiaryConfirmationExtendedResponseListener.setView(view);
        addPrepaidBeneficiaryExtendedResponseListener.setView(view);
        editPrepaidElectricityBeneficiaryExtendedResponseListener.setView(view);
    }

    void onEditPaymentBeneficiarySaveClick(BeneficiaryDetailObject beneficiaryDetailObject, TransactionParams.Transaction notificationMethodDetails) {
        updatePaymentBeneficiary(beneficiaryDetailObject, notificationMethodDetails);
    }

    private void updatePaymentBeneficiary(BeneficiaryDetailObject beneficiaryDetailObject, TransactionParams.Transaction notificationMethodDetails) {
        paymentsService.editBeneficiaryConfirmation(beneficiaryDetailObject, notificationMethodDetails, addBeneficiaryExtendedResponseListener);
    }

    void updateCashSendBeneficiary(BeneficiaryDetailObject beneficiaryDetailObject) {
        new CashSendInteractor().confirmEditCashSendBeneficiary(beneficiaryDetailObject, addCashSendBeneficiaryExtendedResponseListener);
    }

    void updatePrepaidBeneficiary(BeneficiaryDetailObject beneficiaryDetailObject) {
        new PrepaidInteractor().editAirtimeBeneficiaryConfirm(beneficiaryDetailObject.getBeneficiaryId(),
                beneficiaryDetailObject.getBeneficiaryName(), beneficiaryDetailObject.getActNo(),
                beneficiaryDetailObject.getNetworkProviderName(), beneficiaryDetailObject.getNetworkProviderCode(),
                beneficiaryDetailObject.getImageName(), addPrepaidBeneficiaryExtendedResponseListener);
    }

    void updatePrepaidElectricityBeneficiary(String beneficiaryName, BeneficiaryDetailObject beneficiaryDetailObject, MeterNumberObject meterNumberObject) {
        this.beneficiaryObject = beneficiaryDetailObject;
        new BeneficiariesInteractor().updatePrepaidElectricityBeneficiaryDetails(beneficiaryName, beneficiaryDetailObject, meterNumberObject, editPrepaidElectricityBeneficiaryExtendedResponseListener);
    }

    private void onPrepaidEditRequestCompleted(String txnRef) {
        new PrepaidInteractor().editAirtimeBeneficiaryResult(txnRef, "N", prepaidBeneficiaryConfirmationExtendedResponseListener);
    }

    void onPaymentEditRequestCompleted(String txnRef) {
        paymentsService.editBeneficiaryResult(txnRef, "N", addBeneficiaryConfirmationExtendedResponseListener);
    }

    void onDeleteBeneficiaryDeleted(String beneficiaryId, String beneficiaryType) {
        new BeneficiariesInteractor().removeBeneficiaryRequest(beneficiaryId, beneficiaryType, deleteBeneficiaryExtendedResponseListener);
    }

    void requestCashSendUpdate(AddBeneficiaryCashSendConfirmationObject successResponse) {
        new CashSendInteractor().performAddCashSendBeneficiary(true, successResponse.getTxnRefNo(), "N", addCashSendBeneficiaryExtendedResponseListener);
    }

    private ExtendedResponseListener<AddBeneficiaryObject> editPrepaidElectricityBeneficiaryPictureExtendedResponseListener = new ExtendedResponseListener<AddBeneficiaryObject>() {
        @Override
        public void onSuccess(AddBeneficiaryObject successResponse) {
            view = weakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                view.showResultActivity("", true);
            }
        }
    };

}
