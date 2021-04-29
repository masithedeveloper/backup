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
package com.barclays.absa.banking.card.ui.creditCardReplacement;

import androidx.annotation.NonNull;

import com.barclays.absa.banking.boundary.model.creditCardStopAndReplace.CreditCardReplacement;
import com.barclays.absa.banking.boundary.model.creditCardStopAndReplace.CreditCardReplacementConfirmation;
import com.barclays.absa.banking.card.services.card.dto.CreditCardInteractor;
import com.barclays.absa.banking.card.services.card.dto.CreditCardService;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

class CreditCardStopAndReplaceWarningPresenter {
    private WeakReference<StopAndReplaceCreditCardWarningView> weakReferenceView;
    private StopAndReplaceCreditCardWarningView view;
    private CreditCardService creditCardInteractor;
    private SureCheckDelegate replaceCardSurecheckDelegate;
    private static final String AFRIKAANS_FACE_TO_FACE = "Persoonlike aflewering";
    private static final String AFRIKAANS_COLLECT_FROM_BRANCH = "Haal af by tak";

    CreditCardStopAndReplaceWarningPresenter(StopAndReplaceCreditCardWarningView view, SureCheckDelegate sureCheckDelegate) {
        this.weakReferenceView = new WeakReference<>(view);
        this.creditCardInteractor = new CreditCardInteractor();
        replaceCardSurecheckDelegate = sureCheckDelegate;
        replacementConfirmationExtendedResponseListener.setView(view);
    }

    private ExtendedResponseListener<CreditCardReplacementConfirmation> replacementConfirmationExtendedResponseListener = new ExtendedResponseListener<CreditCardReplacementConfirmation>() {
        @Override
        public void onSuccess(final CreditCardReplacementConfirmation successResponse) {
            view = weakReferenceView.get();
            if (view != null) {
                view.dismissProgressDialog();
                replaceCardSurecheckDelegate.processSureCheck(view, successResponse, () -> {
                    if (BMBConstants.SUCCESS.equalsIgnoreCase(successResponse.getTransactionStatus())) {
                        view.showSuccessScreen();
                    } else {
                        view.showErrorScreen();
                    }
                });
            }
        }
    };

    void cancelReplacementInvoked() {
        view = weakReferenceView.get();
        if (view != null) {
            view.showCancelConfirmationScreen();
        }
    }

    void stopCardInvoked(@NotNull CreditCardReplacement cardDetails) {
        CreditCardReplacementConfirmation cardReplacementConfirmation = new CreditCardReplacementConfirmation();
        if (cardDetails.getCreditCardnumber() != null && cardDetails.getDeliveryMethod() != null) {
            cardReplacementConfirmation.setCreditCardNumber(cardDetails.getCreditCardnumber());
            cardReplacementConfirmation.setCardDeliveryMethod(getCardDeliveryMethod(cardDetails.getDeliveryMethod()));
            String TWENTY_FIRST_CENTURY = "20";
            if (TWENTY_FIRST_CENTURY.equals(StopAndReplaceDateUtils.extractFirstDigitsOfDate(cardDetails.getLastUsedDate()))) {
                cardReplacementConfirmation.setDateLastUse("1" + StopAndReplaceDateUtils.extractLastUsedDate(cardDetails.getLastUsedDate()).replace("/", ""));
                cardReplacementConfirmation.setDateLoss("1" + StopAndReplaceDateUtils.extractIncidentDate(cardDetails.getIncidentDate()).replace("/", ""));
            } else {
                cardReplacementConfirmation.setDateLastUse("0" + StopAndReplaceDateUtils.extractLastUsedDate(cardDetails.getLastUsedDate()).replace("/", ""));
                cardReplacementConfirmation.setDateLoss("0" + StopAndReplaceDateUtils.extractIncidentDate(cardDetails.getIncidentDate()).replace("/", ""));
            }
            cardReplacementConfirmation.setContactNumber(cardDetails.getContactNumber());
            if (cardDetails.getReasonForReplacement() != null) {
                cardReplacementConfirmation.setReplacementReason(cardDetails.getReasonForReplacement().toUpperCase());
            }
            cardReplacementConfirmation.setDeliveryBranchCode(cardDetails.getSelectedBranchCode());
            creditCardInteractor.confirmStopAndReplaceCreditCard(cardReplacementConfirmation, replacementConfirmationExtendedResponseListener);
        } else {
            view.showMessageError();
        }
    }

    private String getCardDeliveryMethod(@NonNull String method) {
        if (BMBConstants.FACE_TO_FACE_DELIVERY_METHOD.equalsIgnoreCase(method) || AFRIKAANS_FACE_TO_FACE.equalsIgnoreCase(method)) {
            return "SP";
        } else if (BMBConstants.COLLECT_FROM_BRANCH.equalsIgnoreCase(method) || AFRIKAANS_COLLECT_FROM_BRANCH.equalsIgnoreCase(method)) {
            return "CB";
        }
        throw new RuntimeException();
    }
}