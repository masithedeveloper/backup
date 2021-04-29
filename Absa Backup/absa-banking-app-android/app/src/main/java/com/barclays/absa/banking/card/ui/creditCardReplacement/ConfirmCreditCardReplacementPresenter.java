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

import com.barclays.absa.banking.boundary.model.creditCardStopAndReplace.CreditCardReplacement;
import com.barclays.absa.banking.boundary.model.creditCardStopAndReplace.CreditCardReplacementConfirmation;
import com.barclays.absa.banking.card.services.card.dto.CreditCardService;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBConstants;

import java.lang.ref.WeakReference;

import static com.barclays.absa.banking.framework.app.BMBConstants.FAILURE;
import static com.barclays.absa.banking.framework.app.BMBConstants.SUCCESS;

class ConfirmCreditCardReplacementPresenter {
    private CreditCardService creditCardInteractor;
    private WeakReference<ConfirmCreditCardReplacementView> weakReferenceView;
    private ConfirmCreditCardReplacementView view;
    private static final String AFRIKAANS_FACE_TO_FACE = "Persoonlike aflewering";
    private static final String AFRIKAANS_BRANCH_COLLECTION = "Haal af by tak";

    ConfirmCreditCardReplacementPresenter(ConfirmCreditCardReplacementView view, CreditCardService creditCardInteractor) {
        this.weakReferenceView = new WeakReference<>(view);
        this.creditCardInteractor = creditCardInteractor;
    }

    private ExtendedResponseListener<CreditCardReplacementConfirmation> cardReplacementConfirmationResponseListener = new ExtendedResponseListener<CreditCardReplacementConfirmation>() {
        @Override
        public void onSuccess(CreditCardReplacementConfirmation successResponse) {
            view = weakReferenceView.get();
            if (SUCCESS.equalsIgnoreCase(successResponse.getTransactionStatus())) {
                if (view != null) {
                    view.navigateToWarningScreen();
                }
            } else if (FAILURE.equalsIgnoreCase(successResponse.getTransactionStatus())) {
                view.showMessageError(successResponse.getErrorMessage());
            }
        }
    };

    void validateReplacementInvoked(CreditCardReplacement cardDetails) {
        if (cardDetails != null) {
            CreditCardReplacementConfirmation replacementConfirmation = new CreditCardReplacementConfirmation();
            replacementConfirmation.setCreditCardNumber(cardDetails.getCreditCardnumber());
            replacementConfirmation.setCardDeliveryMethod(getCardDeliveryMethod(cardDetails.getDeliveryMethod()));
            String TWENTY_FIRST_CENTURY = "20";
            if (TWENTY_FIRST_CENTURY.equals(StopAndReplaceDateUtils.extractFirstDigitsOfDate(cardDetails.getLastUsedDate()))) {
                replacementConfirmation.setDateLastUse("1" + StopAndReplaceDateUtils.extractLastUsedDate(cardDetails.getLastUsedDate()).replace("/", ""));
                replacementConfirmation.setDateLoss("1" + StopAndReplaceDateUtils.extractIncidentDate(cardDetails.getIncidentDate()).replace("/", ""));
            } else {
                replacementConfirmation.setDateLastUse("0" + StopAndReplaceDateUtils.extractLastUsedDate(cardDetails.getLastUsedDate()).replace("/", ""));
                replacementConfirmation.setDateLoss("0" + StopAndReplaceDateUtils.extractIncidentDate(cardDetails.getIncidentDate()).replace("/", ""));
            }
            replacementConfirmation.setContactNumber(cardDetails.getContactNumber());
            if (cardDetails.getReasonForReplacement() != null) {
                replacementConfirmation.setReplacementReason(cardDetails.getReasonForReplacement().toUpperCase());
            }
            replacementConfirmation.setDeliveryBranchCode(cardDetails.getSelectedBranchCode());
            creditCardInteractor.validateStopAndReplaceCreditCard(replacementConfirmation, cardReplacementConfirmationResponseListener);
        } else {
            view.showTechnicalDifficultiesErrorMessage();
        }
    }

    void openAssistanceDialogInvoked() {
        view = weakReferenceView.get();
        if (view != null) {
            view.openAssistanceDialog();
        }
    }

    private String getCardDeliveryMethod(String method) {
        if (BMBConstants.FACE_TO_FACE_DELIVERY_METHOD.equalsIgnoreCase(method) || AFRIKAANS_FACE_TO_FACE.equalsIgnoreCase(method)) {
            return "SP";
        } else if (BMBConstants.COLLECT_FROM_BRANCH.equalsIgnoreCase(method) || AFRIKAANS_BRANCH_COLLECTION.equalsIgnoreCase(method)) {
            return "CB";
        }
        throw new RuntimeException();
    }
}
