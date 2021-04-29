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

package com.barclays.absa.banking.overdraft.ui.fiveSteps;

import com.barclays.absa.banking.framework.AbstractPresenter;
import com.barclays.absa.banking.framework.BaseView;
import com.barclays.absa.banking.overdraft.ui.OverdraftContracts;

import java.lang.ref.WeakReference;

public class OverdraftMarketingConsentPresenter extends AbstractPresenter implements OverdraftContracts.MarketingConsentPresenter {

    OverdraftMarketingConsentPresenter(WeakReference<? extends BaseView> marketingConsentView) {
        super(marketingConsentView);
    }

    @Override
    public void onNextButtonClicked() {
        OverdraftContracts.MarketingConsentView view = (OverdraftContracts.MarketingConsentView) viewWeakReference.get();
        if (view != null) {
            view.navigateToOverdraftSetupConfirmationScreen();
        }
    }

    @Override
    public void marketingConsentChecked() {
        OverdraftContracts.MarketingConsentView view = (OverdraftContracts.MarketingConsentView) viewWeakReference.get();
        if (view != null) {
            view.showMarketingConsentChannelOptions();
        }
    }

    @Override
    public void marketingConsentNotChecked() {
        OverdraftContracts.MarketingConsentView view = (OverdraftContracts.MarketingConsentView) viewWeakReference.get();
        if (view != null) {
            view.hideMarketingConsentChannelOptions();
        }
    }
}