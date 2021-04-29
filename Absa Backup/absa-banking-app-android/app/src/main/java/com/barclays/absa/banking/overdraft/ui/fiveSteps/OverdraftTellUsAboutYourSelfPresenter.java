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

public class OverdraftTellUsAboutYourSelfPresenter extends AbstractPresenter implements OverdraftContracts.TellUsAboutYourSelfPresenter {
    private OverdraftContracts.TellUsAboutYourSelfView view;

    public OverdraftTellUsAboutYourSelfPresenter(WeakReference<? extends BaseView> weakReference) {
        super(weakReference);
    }

    @Override
    public void onDebtCounsellingOrPendingDebtReviewOrInsolventNoSelected() {
        view = (OverdraftContracts.TellUsAboutYourSelfView) viewWeakReference.get();
        if (view != null) {
            view.showInsolventAndDebtReviewInThePastOptions();
        }
    }

    @Override
    public void onDebtCounsellingOrPendingDebtReviewOrInsolventYesSelected() {
        view = (OverdraftContracts.TellUsAboutYourSelfView) viewWeakReference.get();
        if (view != null) {
            view.hideInsolventAndDebtReviewInThePastOptions();
            view.hideDebtReviewReasonsOptions();
            view.hideInsolventDateSelector();
        }
    }

    @Override
    public void onInsolventOrUnderDebtReviewYesOptionSelected() {
        view = (OverdraftContracts.TellUsAboutYourSelfView) viewWeakReference.get();
        if (view != null) {
            view.showInsolventAndDebtReviewReasons();
            view.showInsolventDateSelector();
        }
    }

    @Override
    public void onInsolventOrUnderDebtReviewNoOptionSelected() {
        view = (OverdraftContracts.TellUsAboutYourSelfView) viewWeakReference.get();
        if (view != null) {
            view.hideDebtReviewReasonsOptions();
            view.hideInsolventDateSelector();
        }
    }

    @Override
    public void onNextButtonClicked() {
        view = (OverdraftContracts.TellUsAboutYourSelfView) viewWeakReference.get();
        if (view != null) {
            view.navigateToNextScreen();
        }
    }
}