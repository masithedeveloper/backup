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
package com.barclays.absa.banking.card.ui.creditCard.hub;

import com.barclays.absa.banking.boundary.model.Transaction;
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardResponseObject;
import com.barclays.absa.banking.card.services.card.dto.creditCard.VCLParcelableModel;
import com.barclays.absa.banking.framework.BaseView;

import java.util.List;

public interface CreditCardHubView extends BaseView {

    void updateTransactionsList(List<Transaction> transactions);

    void onSearchRequestCompleted(List<Transaction> transactions);

    void updateVCLModel(VCLParcelableModel dataModel);

    void updateCreditCardInformation(CreditCardResponseObject successResponse);
}
