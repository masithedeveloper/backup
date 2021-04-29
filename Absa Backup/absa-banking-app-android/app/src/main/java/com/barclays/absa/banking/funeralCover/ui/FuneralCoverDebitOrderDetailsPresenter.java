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

package com.barclays.absa.banking.funeralCover.ui;

import com.barclays.absa.banking.framework.AbstractPresenter;
import com.barclays.absa.banking.funeralCover.services.FuneralCoverQuoteInteractor;
import com.barclays.absa.banking.funeralCover.services.dto.RetailAccount;
import com.barclays.absa.banking.funeralCover.services.dto.RetailAccountsResponse;
import com.barclays.absa.banking.funeralCover.ui.responseListeners.RetailAccountsExtendedResponseListener;
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskProfileDetails;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class FuneralCoverDebitOrderDetailsPresenter extends AbstractPresenter implements RetailAccountsBasePresenter {
    private final FuneralCoverQuoteInteractor funeralCoverQuoteInteractor;
    private final RetailAccountsExtendedResponseListener retailAccountsExtendedResponseListener;
    private RiskProfileDetails riskProfileDetails = new RiskProfileDetails();

    FuneralCoverDebitOrderDetailsPresenter(WeakReference<FuneralCoverDebitOrderDetailsView> weakReference) {
        super(weakReference);
        funeralCoverQuoteInteractor = new FuneralCoverQuoteInteractor();
        retailAccountsExtendedResponseListener = new RetailAccountsExtendedResponseListener(this);
    }

    public void loadRetailAccounts() {
        funeralCoverQuoteInteractor.fetchRetailAccounts(retailAccountsExtendedResponseListener);
    }

    @Override
    public void onRetailAccountsLoaded(RetailAccountsResponse retailAccountsResponse) {
        FuneralCoverDebitOrderDetailsView view = (FuneralCoverDebitOrderDetailsView) viewWeakReference.get();
        if (view != null) {
            if (retailAccountsResponse != null) {
                ArrayList<RetailAccount> retailAccountsList = retailAccountsResponse.getRetailAccountsList();
                if (retailAccountsList != null) {
                    view.displayRetailAccounts(retailAccountsList);
                }
            } else {
                view.navigateToSomethingWentWrongScreen();
            }
        }
    }

    private void showFailureMessage() {
        FuneralCoverDebitOrderDetailsView view = (FuneralCoverDebitOrderDetailsView) viewWeakReference.get();
        if (view != null) {
            view.navigateToSomethingWentWrongScreen();
            dismissProgressIndicator();
        }
    }

    public RiskProfileDetails getRiskProfileDetails() {
        return riskProfileDetails;
    }

    public boolean shouldShowOccupation(String itemCode) {
        String[] occupationStatusBlackList = {"04", "07", "10", "05", "06"};
        for (String occupationStatus : occupationStatusBlackList) {
            if (occupationStatus.equals(itemCode)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onFailedToLoadRetailAccounts() {
        showFailureMessage();
    }
}
