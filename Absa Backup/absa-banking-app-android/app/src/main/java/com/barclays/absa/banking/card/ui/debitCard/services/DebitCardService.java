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

package com.barclays.absa.banking.card.ui.debitCard.services;

import com.barclays.absa.banking.boundary.model.BranchDeliveryDetailsList;
import com.barclays.absa.banking.boundary.model.CardReplacementAndFetchFees;
import com.barclays.absa.banking.boundary.model.ClientContactInformationList;
import com.barclays.absa.banking.boundary.model.debitCard.DebitCardProductType;
import com.barclays.absa.banking.boundary.model.debitCard.DebitCardProductTypeList;
import com.barclays.absa.banking.boundary.model.debitCard.DebitCardReplacementConfirmation;
import com.barclays.absa.banking.boundary.model.debitCard.DebitCardReplacementDetailsConfirmation;
import com.barclays.absa.banking.boundary.model.debitCard.DebitCardReplacementReasonList;
import com.barclays.absa.banking.boundary.model.debitCard.DebitCardTypeList;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;

import java.util.List;

public interface DebitCardService {

    void fetchClientInformation(ExtendedResponseListener<ClientContactInformationList> creditCardInfoResponseListener);

    void fetchReplacementReasonList(ExtendedResponseListener<DebitCardReplacementReasonList> debitCardReplacementReasonsResponseListener);

    void fetchProductTypeList(ExtendedResponseListener<DebitCardProductTypeList> debitCardProductTypeListResponseListener);

    void fetchCardTypeList(DebitCardProductType productType,
                           ExtendedResponseListener<DebitCardTypeList> debitCardTypeListResponseListener);

    void fetchBranchList(ExtendedResponseListener<BranchDeliveryDetailsList> branchDeliveryDetailsListResponseListener);

    void fetchReplacementFee(DebitCardReplacementDetailsConfirmation debitCardReplacementDetailsConfirmation, ExtendedResponseListener<CardReplacementAndFetchFees> cardReplacementAndFetchFeesResponseListener);

    void replaceCard(DebitCardReplacementDetailsConfirmation debitCardReplacementDetailsConfirmation, ExtendedResponseListener<DebitCardReplacementConfirmation> debitCardReplacementConfirmationResponseListener);

    void fetchDebitCardData(List<ExtendedRequest> requests);

}
