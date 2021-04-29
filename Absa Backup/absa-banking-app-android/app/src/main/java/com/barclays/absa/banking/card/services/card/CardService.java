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
package com.barclays.absa.banking.card.services.card;

import com.barclays.absa.banking.boundary.model.Card;
import com.barclays.absa.banking.boundary.model.CardAccountList;
import com.barclays.absa.banking.boundary.model.CardAccountListOld;
import com.barclays.absa.banking.boundary.model.CardPin;
import com.barclays.absa.banking.boundary.model.ManageCardLimitDetails;
import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse;
import com.barclays.absa.banking.card.services.card.dto.EnquirePauseState;
import com.barclays.absa.banking.card.services.card.dto.ManageCardResponseObject;
import com.barclays.absa.banking.card.services.card.dto.PauseStates;
import com.barclays.absa.banking.card.services.card.dto.TravelUpdateModel;
import com.barclays.absa.banking.card.services.card.dto.UpdatePauseCard;
import com.barclays.absa.banking.card.services.card.dto.VirtualCardDetailsResponse;
import com.barclays.absa.banking.framework.ExtendedResponseListener;

public interface CardService {
    String OP0120_CARD_LIST = "OP0120";
    String OP2019_ENQUIRE_PAUSE_CARD_STATES = "OP2019";
    String OP2020_UPDATE_PAUSE_CARD_STATES = "OP2020";
    String OP2054_MANAGE_CARDS = "OP2054";
    String OP2056_UPDATE_TRAVEL_NOTIFICATION = "OP2056";
    String OP2132_VIEW_CARD_DETAILS = "OP2132";
    String OP2193_UPDATE_SECONDARY_CARD = "OP2193";

    void fetchCardLimits(Card card, ExtendedResponseListener<ManageCardLimitDetails> responseListener);
    void fetchListOfCardsWithChangeableLimits(ExtendedResponseListener<CardAccountList> extendedResponseListener);
    void fetchCardList(ExtendedResponseListener<CardAccountListOld> cardAccountListResponseListener);
    void fetchLinkedCards(ExtendedResponseListener<CardAccountList> cardAccountListResponseListener);
    void fetchPIN(String cardNumber, String cardIndex, ExtendedResponseListener<CardPin> pinRetrievalListener);
    void fetchPauseCardStates(String cardNumber, ExtendedResponseListener<EnquirePauseState> pauseRetrievalListener);
    void updatePauseCardStates(PauseStates pauseCardStates, ExtendedResponseListener<UpdatePauseCard> responseListener);
    void fetchManageCardsData(ExtendedResponseListener<ManageCardResponseObject> responseListener);
    void updateTravelNotification(TravelUpdateModel travelUpdateModel, ExtendedResponseListener<SureCheckResponse> responseListener);
    void fetchCardDetails(String cardNumber, ExtendedResponseListener<VirtualCardDetailsResponse> responseListener);
    void updateSecondaryCardMandates(String secondaryCards, ExtendedResponseListener<SureCheckResponse> responseListener);
}