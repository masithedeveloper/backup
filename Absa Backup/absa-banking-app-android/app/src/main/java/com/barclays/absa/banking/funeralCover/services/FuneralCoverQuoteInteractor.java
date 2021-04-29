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
package com.barclays.absa.banking.funeralCover.services;

import com.barclays.absa.banking.boundary.model.FuneralCoverQuotes;
import com.barclays.absa.banking.boundary.model.funeralCover.FamilyMemberCoverDetails;
import com.barclays.absa.banking.boundary.model.funeralCover.FuneralCoverDetails;
import com.barclays.absa.banking.boundary.model.funeralCover.RolePlayerDetails;
import com.barclays.absa.banking.card.services.card.dto.creditCard.FuneralQuoteServiceRequest;
import com.barclays.absa.banking.card.services.card.dto.creditCard.RetailAccountRequest;
import com.barclays.absa.banking.framework.AbstractInteractor;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.MockFactory;
import com.barclays.absa.banking.framework.ServiceClient;
import com.barclays.absa.banking.funeralCover.services.dto.FuneralCoverDetailsRequest;
import com.barclays.absa.banking.funeralCover.services.dto.RetailAccountsResponse;
import com.barclays.absa.banking.funeralCover.services.dto.RolePlayerDetailsRequest;
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustAccountsRequest;
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustAccountsWrapper;

public class FuneralCoverQuoteInteractor extends AbstractInteractor implements FuneralCoverQuoteService {

    @Override
    public void pullFuneralCoverQuotes(ExtendedResponseListener<FuneralCoverQuotes> responseListener) {
        FuneralQuoteServiceRequest<FuneralCoverQuotes> funeralQuoteServiceRequest = new FuneralQuoteServiceRequest<>(responseListener);
        ServiceClient serviceClient = new ServiceClient(funeralQuoteServiceRequest);
        funeralQuoteServiceRequest.setMockResponseFile(MockFactory.funeralCoverQuotes());
        serviceClient.submitRequest();
    }

    @Override
    public void pullFuneralPlanApplication(ExtendedResponseListener<FuneralCoverDetails> responseListener, FuneralCoverDetails funeralCoverDetails) {
        FuneralCoverDetailsRequest acceptFuneralQuoteExtendedRequest = new FuneralCoverDetailsRequest(funeralCoverDetails, responseListener);
        ServiceClient serviceClient = new ServiceClient(acceptFuneralQuoteExtendedRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void pullCoverAmountForRelationApplyFuneralPlan(ExtendedResponseListener<RolePlayerDetails> responseListener, FuneralCoverDetails funeralCoverDetails, FamilyMemberCoverDetails rolePlayerDetails) {
        RolePlayerDetailsRequest funeralCoverForRelativeExtendedRequest = new RolePlayerDetailsRequest(funeralCoverDetails, rolePlayerDetails, responseListener);
        ServiceClient serviceClient = new ServiceClient(funeralCoverForRelativeExtendedRequest);
        serviceClient.submitRequest();
    }

    public void fetchRetailAccounts(ExtendedResponseListener<RetailAccountsResponse> responseListener) {
        String accountTypes = "savingsAccount|currentAccount|creditCard|chequeAccount";
        RetailAccountRequest<RetailAccountsResponse> retailAccountRequest = new RetailAccountRequest<>(responseListener, accountTypes);
        submitRequest(retailAccountRequest);
    }

    @Override
    public void fetchUnitTrustAccounts(ExtendedResponseListener<UnitTrustAccountsWrapper> unitTrustAccountExtendedResponseListener) {
        UnitTrustAccountsRequest<UnitTrustAccountsWrapper> unitTrustAccountsRequest = new UnitTrustAccountsRequest<>(unitTrustAccountExtendedResponseListener);
        ServiceClient serviceClient = new ServiceClient(unitTrustAccountsRequest);
        serviceClient.submitRequest();
    }
}
