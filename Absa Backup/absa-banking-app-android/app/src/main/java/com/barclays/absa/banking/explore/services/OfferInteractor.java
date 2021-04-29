/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.explore.services;

import com.barclays.absa.banking.explore.services.dto.OffersRequest;
import com.barclays.absa.banking.explore.services.dto.OffersResponseObject;
import com.barclays.absa.banking.framework.AbstractInteractor;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;

public class OfferInteractor extends AbstractInteractor implements  OfferService{
    @Override
    public void requestOffers(ExtendedResponseListener<OffersResponseObject> responseListener) {
        OffersRequest<OffersResponseObject> extendedCreditCardHistoryRequest = new OffersRequest<>(responseListener);
        ServiceClient serviceClient = new ServiceClient(extendedCreditCardHistoryRequest);
        serviceClient.submitRequest();
    }
}
