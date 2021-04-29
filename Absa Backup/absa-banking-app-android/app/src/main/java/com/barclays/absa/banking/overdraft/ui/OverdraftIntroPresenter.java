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
package com.barclays.absa.banking.overdraft.ui;


import com.barclays.absa.banking.framework.AbstractPresenter;
import com.barclays.absa.banking.framework.BaseView;

import java.lang.ref.WeakReference;

public class OverdraftIntroPresenter extends AbstractPresenter implements OverdraftContracts.OverdraftIntroPresenter {

    OverdraftIntroPresenter(WeakReference<? extends BaseView> weakReference) {
        super(weakReference);
    }

    @Override
    public void applyNowButtonClicked() {
        OverdraftContracts.OverdraftIntroView view = getView();
        if (view != null) {
            view.navigateToApplyOverdraftStep1();
        }
    }

    @Override
    public void onAbsaWebsiteClicked() {
        OverdraftContracts.OverdraftIntroView view = getView();
        if (view != null) {
            view.navigateToAbsaWebsite();
        }
    }

    public OverdraftContracts.OverdraftIntroView getView() {
        return (OverdraftContracts.OverdraftIntroView) viewWeakReference.get();
    }
}
