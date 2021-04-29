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

import com.barclays.absa.banking.boundary.model.BranchDeliveryDetailsList;
import com.barclays.absa.banking.boundary.model.CardReplacementAndFetchFees;
import com.barclays.absa.banking.boundary.model.ClientContactInformationList;
import com.barclays.absa.banking.boundary.model.debitCard.DebitCardProductType;
import com.barclays.absa.banking.boundary.model.debitCard.DebitCardProductTypeList;
import com.barclays.absa.banking.boundary.model.debitCard.DebitCardReplacementConfirmation;
import com.barclays.absa.banking.boundary.model.debitCard.DebitCardReplacementDetailsConfirmation;
import com.barclays.absa.banking.boundary.model.debitCard.DebitCardReplacementReasonList;
import com.barclays.absa.banking.boundary.model.debitCard.DebitCardTypeList;
import com.barclays.absa.banking.card.services.card.dto.BranchDeliveryDetailsListRequest;
import com.barclays.absa.banking.card.services.card.dto.ClientContactInformationRequest;
import com.barclays.absa.banking.card.services.card.dto.DebitCardCardTypeListRequest;
import com.barclays.absa.banking.card.services.card.dto.DebitCardProductTypeListRequest;
import com.barclays.absa.banking.card.services.card.dto.DebitCardReplacementDetailsConfirmationRequest;
import com.barclays.absa.banking.card.services.card.dto.DebitCardReplacementFeesRequest;
import com.barclays.absa.banking.card.services.card.dto.DebitCardReplacementReasonListRequest;
import com.barclays.absa.banking.card.ui.debitCard.services.DebitCardService;
import com.barclays.absa.banking.framework.AbstractInteractor;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;

import java.util.List;

public class DebitCardInteractor extends AbstractInteractor implements DebitCardService {

    @Override
    public void fetchDebitCardData(List<ExtendedRequest> requests) {
        submitQueuedRequests(requests);
    }

    @Override
    public void fetchClientInformation(ExtendedResponseListener clientContactInformationListResponseListener) {
        ClientContactInformationRequest<ClientContactInformationList> clientContactInformationRequest =
                new ClientContactInformationRequest<>(clientContactInformationListResponseListener);
        submitRequest(clientContactInformationRequest);
    }

    @Override
    public void fetchReplacementReasonList(ExtendedResponseListener debitCardReplacementReasonsResponseListener) {
        DebitCardReplacementReasonListRequest<DebitCardReplacementReasonList> debitCardReplacementReasonListRequest =
                new DebitCardReplacementReasonListRequest<>(debitCardReplacementReasonsResponseListener);
       submitRequest(debitCardReplacementReasonListRequest);
    }

    @Override
    public void fetchProductTypeList(ExtendedResponseListener<DebitCardProductTypeList> debitCardProductTypeListResponseListener) {
        DebitCardProductTypeListRequest<DebitCardProductTypeList> debitCardProductTypeListRequest =
                new DebitCardProductTypeListRequest<>(debitCardProductTypeListResponseListener);
        submitRequest(debitCardProductTypeListRequest);
    }

    @Override
    public void fetchCardTypeList(DebitCardProductType productType,
                                  ExtendedResponseListener<DebitCardTypeList> debitCardTypeListResponseListener) {
        DebitCardCardTypeListRequest<DebitCardTypeList> debitCardCardTypeListRequest
                = new DebitCardCardTypeListRequest<>(productType, debitCardTypeListResponseListener);
       submitRequest(debitCardCardTypeListRequest);
    }

    @Override
    public void fetchBranchList(ExtendedResponseListener<BranchDeliveryDetailsList> branchDeliveryDetailsListResponseListener) {
        BranchDeliveryDetailsListRequest<BranchDeliveryDetailsList> branchDeliveryDetailsListRequest
                = new BranchDeliveryDetailsListRequest<>(branchDeliveryDetailsListResponseListener);
       submitRequest(branchDeliveryDetailsListRequest);
    }

    @Override
    public void fetchReplacementFee(DebitCardReplacementDetailsConfirmation debitCardReplacementDetailsConfirmation,
                                    ExtendedResponseListener<CardReplacementAndFetchFees> cardReplacementAndFetchFeesResponseListener) {
        DebitCardReplacementFeesRequest<CardReplacementAndFetchFees> debitCardReplacementFeesRequest
                = new DebitCardReplacementFeesRequest<>(debitCardReplacementDetailsConfirmation, cardReplacementAndFetchFeesResponseListener);
       submitRequest(debitCardReplacementFeesRequest);
    }

    @Override
    public void replaceCard(DebitCardReplacementDetailsConfirmation debitCardReplacementDetailsConfirmation, ExtendedResponseListener<DebitCardReplacementConfirmation> debitCardReplacementConfirmationResponseListener) {
        DebitCardReplacementDetailsConfirmationRequest<DebitCardReplacementConfirmation> extendedRequest = new DebitCardReplacementDetailsConfirmationRequest<>(debitCardReplacementDetailsConfirmation, debitCardReplacementConfirmationResponseListener);
        ServiceClient serviceClient = new ServiceClient(extendedRequest);
        serviceClient.submitRequest();
    }
}
