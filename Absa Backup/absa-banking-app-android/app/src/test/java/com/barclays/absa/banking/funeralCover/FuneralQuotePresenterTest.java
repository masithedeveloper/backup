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
package com.barclays.absa.banking.funeralCover;

import com.barclays.absa.banking.boundary.model.FuneralCoverQuotesListObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public final class FuneralQuotePresenterTest {

    @Mock
    private FuneralCoverQuotesListObject funeralCoverQuotesListObject;

    @Before
    public void setup() {
        funeralCoverQuotesListObject = new FuneralCoverQuotesListObject();
    }

    @Test
    public void shouldDisplayMainMemberTotalCoverAmount() {

        String coverAmount = "R500 000.00";

        funeralCoverQuotesListObject.setCoverAmount(coverAmount);

        String getCoverAmount = funeralCoverQuotesListObject.getCoverAmount();
        Assert.assertEquals("R500 000.00", getCoverAmount);
    }

    @Test
    public void shouldDisplayMainMemberMonthlyPremiumAmount() {
        String premiumAmount = "R25.68";

        funeralCoverQuotesListObject.setPremiumAmount(premiumAmount);

        String getPremiumAmount = funeralCoverQuotesListObject.getPremiumAmount();
        Assert.assertEquals("R25.68", getPremiumAmount);
    }

    @Test
    public void shouldDisplaySpouseCoverPremiumAmount() {
        String spousePremiumAmount = "R19.88";

        funeralCoverQuotesListObject.setSpouseBenefitPremium(spousePremiumAmount);

        String getSpouseBenefitPremium = funeralCoverQuotesListObject.getSpouseBenefitPremium();
        Assert.assertEquals("R19.88", getSpouseBenefitPremium);
    }

    @Test
    public void shouldDisplaySpouseTotalCoverAmount() {
        String spouseCoverAmount = "R200 000.00";

        funeralCoverQuotesListObject.setSpouseCoverAmount(spouseCoverAmount);

        String spouseTotalCoverAmount = funeralCoverQuotesListObject.getSpouseCoverAmount();
        Assert.assertEquals("R200 000.00", spouseTotalCoverAmount);
    }

}
