/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.card.services.card;

import com.barclays.absa.banking.boundary.model.Card;
import com.barclays.absa.banking.boundary.model.CardAccountList;
import com.barclays.absa.banking.boundary.model.CardAccountListOld;
import com.barclays.absa.banking.boundary.model.CardPin;
import com.barclays.absa.banking.boundary.model.ManageCardLimitDetails;
import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse;
import com.barclays.absa.banking.card.services.card.dto.CardAccountListOldRequest;
import com.barclays.absa.banking.card.services.card.dto.CardAccountListRequest;
import com.barclays.absa.banking.card.services.card.dto.CardDetailsRequest;
import com.barclays.absa.banking.card.services.card.dto.CardLimitDetailsRequest;
import com.barclays.absa.banking.card.services.card.dto.EnquirePauseState;
import com.barclays.absa.banking.card.services.card.dto.LinkedCardsRequest;
import com.barclays.absa.banking.card.services.card.dto.ManageCardResponseObject;
import com.barclays.absa.banking.card.services.card.dto.ManageCardsRequest;
import com.barclays.absa.banking.card.services.card.dto.PauseCardLockRequest;
import com.barclays.absa.banking.card.services.card.dto.PauseCardLockStatesRequest;
import com.barclays.absa.banking.card.services.card.dto.PauseStates;
import com.barclays.absa.banking.card.services.card.dto.PinRetrievalRequest;
import com.barclays.absa.banking.card.services.card.dto.TravelUpdateModel;
import com.barclays.absa.banking.card.services.card.dto.TravelUpdateRequest;
import com.barclays.absa.banking.card.services.card.dto.UpdatePauseCard;
import com.barclays.absa.banking.card.services.card.dto.UpdateSecondaryCardMandate;
import com.barclays.absa.banking.card.services.card.dto.VirtualCardDetailsResponse;
import com.barclays.absa.banking.framework.AbstractInteractor;
import com.barclays.absa.banking.framework.BuildConfigHelper;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;

public class CardInteractor extends AbstractInteractor implements CardService {

    @Override
    public void fetchCardLimits(Card card, ExtendedResponseListener<ManageCardLimitDetails> responseListener) {
        CardLimitDetailsRequest<ManageCardLimitDetails> cardLimitDetailsRequest
                = new CardLimitDetailsRequest<>(card, responseListener);
        submitRequest(cardLimitDetailsRequest);
    }

    @Override
    public void fetchListOfCardsWithChangeableLimits(ExtendedResponseListener<CardAccountList> extendedResponseListener) {
        CardAccountListRequest<CardAccountList> cardAccountListRequest = new CardAccountListRequest<>(extendedResponseListener);
        submitRequest(cardAccountListRequest);
    }

    @Override
    public void fetchLinkedCards(ExtendedResponseListener<CardAccountList> cardAccountListResponseListener) {
        LinkedCardsRequest<CardAccountList> cardAccountListRequest = new LinkedCardsRequest<>(cardAccountListResponseListener);
        submitRequest(cardAccountListRequest);
    }

    @Override
    public void fetchCardList(ExtendedResponseListener<CardAccountListOld> cardAccountListResponseListener) {
        CardAccountListOldRequest<CardAccountListOld> cardAccountListRequest = new CardAccountListOldRequest<>(cardAccountListResponseListener);
        submitRequest(cardAccountListRequest);
    }

    @Override
    public void fetchPIN(String cardNumber, String cardIndex, ExtendedResponseListener<CardPin> pinRetrievalListener) {
        PinRetrievalRequest<CardPin> pinPinRetrievalRequest = new PinRetrievalRequest<>(cardNumber, cardIndex, pinRetrievalListener);
        ServiceClient serviceClient = new ServiceClient(BuildConfigHelper.INSTANCE.getPinRetrievalServerPath(), pinPinRetrievalRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void fetchPauseCardStates(String cardNumber, ExtendedResponseListener<EnquirePauseState> pauseRetrievalListener) {
        PauseCardLockStatesRequest<EnquirePauseState> pauseCardRequest = new PauseCardLockStatesRequest<>(cardNumber, pauseRetrievalListener);
        submitRequest(pauseCardRequest);
    }

    @Override
    public void updatePauseCardStates(PauseStates pauseCardStates, ExtendedResponseListener<UpdatePauseCard> responseListener) {
        PauseCardLockRequest<UpdatePauseCard> pauseCardLockRequest = new PauseCardLockRequest<>(pauseCardStates, responseListener);
        submitRequest(pauseCardLockRequest);
    }

    @Override
    public void fetchManageCardsData(ExtendedResponseListener<ManageCardResponseObject> responseListener) {
        ManageCardsRequest<ManageCardResponseObject> request = new ManageCardsRequest<>(responseListener);
        submitRequest(request);
    }

    @Override
    public void updateTravelNotification(TravelUpdateModel travelUpdateModel, ExtendedResponseListener<SureCheckResponse> responseListener) {
        TravelUpdateRequest<SureCheckResponse> request = new TravelUpdateRequest<>(travelUpdateModel, responseListener);
        submitRequest(request);
    }

    @Override
    public void fetchCardDetails(String cardNumber, ExtendedResponseListener<VirtualCardDetailsResponse> responseListener) {
        submitRequest(new CardDetailsRequest<>(cardNumber, responseListener));
    }

    @Override
    public void updateSecondaryCardMandates(String secondaryCards, ExtendedResponseListener<SureCheckResponse> responseListener) {
        submitRequest(new UpdateSecondaryCardMandate<>(secondaryCards, responseListener));
    }
}