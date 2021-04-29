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
package com.barclays.absa.banking.overdraft.ui.fiveSteps;

import com.barclays.absa.banking.boundary.model.AccountList;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftAccountObject;
import com.barclays.absa.banking.framework.AbstractPresenter;
import com.barclays.absa.banking.framework.BaseView;
import com.barclays.absa.banking.overdraft.ui.OverdraftContracts;
import com.barclays.absa.banking.riskBasedApproach.services.RiskBasedApproachInteractor;
import com.barclays.absa.banking.riskBasedApproach.services.dto.PersonalInformationResponse;
import com.barclays.absa.utils.AbsaCacheManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import styleguide.forms.SelectorList;

public class OverdraftSetupPresenter extends AbstractPresenter implements OverdraftContracts.OverdraftSetupPresenter {
    private PersonalInformationExtendedResponseListener personalInformationExtendedResponseListener;

    OverdraftSetupPresenter(WeakReference<? extends BaseView> overdraftSetupView) {
        super(overdraftSetupView);
        personalInformationExtendedResponseListener = new PersonalInformationExtendedResponseListener(this);
        loadCurrentAccountsFromCache();
    }

    @Override
    public void loadCurrentAccountsFromCache() {
        AccountList accountList = AbsaCacheManager.getInstance().getAccountsList();
        ArrayList<AccountObject> chequeAccounts = new ArrayList<>();
        if (accountList != null) {
            for (AccountObject accountObject : accountList.getAccountsList()) {
                if ("currentAccount".equalsIgnoreCase(accountObject.getAccountType())) {
                    chequeAccounts.add(accountObject);
                }
            }
            addListOfChequeAccounts(chequeAccounts);
        }
    }

    private void addListOfChequeAccounts(ArrayList<AccountObject> chequeAccounts) {
        OverdraftContracts.OverdraftSetupView view = (OverdraftContracts.OverdraftSetupView) viewWeakReference.get();
        SelectorList<OverdraftAccountObject> accounts = new SelectorList<>();
        for (AccountObject accountObject : chequeAccounts) {
            accounts.add(new OverdraftAccountObject(accountObject));
        }
        dismissProgressIndicator();
        if (view != null) {
            if (chequeAccounts.size() == 1) {
                AccountObject chequeAccount = chequeAccounts.get(0);
                view.setChequeAccount(chequeAccount.getAccountInformation(), chequeAccount.getAccountNumber(), chequeAccount.getAvailableBalanceFormated());
            }
            view.populateAccountList(accounts);
        }
    }

    @Override
    public void fetchMarketingConsentMethods() {
        showProgressIndicator();
        new RiskBasedApproachInteractor().fetchPersonalInformation(personalInformationExtendedResponseListener);
    }

    @Override
    public void onFailureResponse() {
        OverdraftContracts.OverdraftSetupView view = (OverdraftContracts.OverdraftSetupView) viewWeakReference.get();
        dismissProgressIndicator();
        if (view != null) {
            view.showGenericErrorMessage();
        }
    }

    public void marketingMethodsReceived(PersonalInformationResponse marketingMethodResponse) {
        OverdraftContracts.OverdraftSetupView view = (OverdraftContracts.OverdraftSetupView) viewWeakReference.get();
        dismissProgressIndicator();
        if (marketingMethodResponse != null) {
            if (view != null) {
                view.setMarketingResponse(marketingMethodResponse);
            }
        } else {
            if (view != null) {
                view.showGenericErrorMessage();
            }
        }
    }
}