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

package com.barclays.absa.banking.explore.ui;

import com.barclays.absa.banking.boundary.model.FuneralCoverQuotes;
import com.barclays.absa.banking.explore.services.dto.OffersResponseObject;
import com.barclays.absa.banking.framework.BaseView;

public interface ExploreHubOffersView extends BaseView {
    void showFailureScreen(FuneralCoverQuotes successResponse);
    void buildOffers(OffersResponseObject successResponse);
}