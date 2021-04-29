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

package com.barclays.absa.banking.funeralCover.ui;

import com.barclays.absa.banking.boundary.model.BankBranches;
import com.barclays.absa.banking.boundary.model.BankDetails;
import com.barclays.absa.banking.boundary.model.ExergyBankListResponse;
import com.barclays.absa.banking.boundary.model.ExergyBranchListResponse;
import com.barclays.absa.banking.framework.AbstractPresenter;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.funeralCover.services.FuneralCoverQuoteInteractor;
import com.barclays.absa.banking.funeralCover.services.dto.RetailAccount;
import com.barclays.absa.banking.funeralCover.services.dto.RetailAccountsResponse;
import com.barclays.absa.banking.funeralCover.ui.responseListeners.BankBranchDetailsResponseListener;
import com.barclays.absa.banking.funeralCover.ui.responseListeners.EditPolicyPaymentDetailsExtendedResponseListener;
import com.barclays.absa.banking.funeralCover.ui.responseListeners.ExergyBankListResponseListener;
import com.barclays.absa.banking.funeralCover.ui.responseListeners.ExergyBranchListResponseListener;
import com.barclays.absa.banking.funeralCover.ui.responseListeners.RetailAccountsExtendedResponseListener;
import com.barclays.absa.banking.funeralCover.ui.responseListeners.SourceOfFundsExtendedResponseListener;
import com.barclays.absa.banking.payments.services.PaymentsInteractor;
import com.barclays.absa.banking.payments.services.PaymentsService;
import com.barclays.absa.banking.shared.services.SharedInteractor;
import com.barclays.absa.banking.shared.services.SharedService;
import com.barclays.absa.banking.shared.services.dto.CIFGroupCode;
import com.barclays.absa.banking.shared.services.dto.LookupResult;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class EditPolicyPaymentDetailsPresenter extends AbstractPresenter implements RetailAccountsBasePresenter, SourceFundsBasePresenter {
    private PaymentsService paymentsService;
    private FuneralCoverQuoteInteractor funeralCoverQuoteInteractor;
    private SharedService sharedService;
    private EditPolicyPaymentDetailsExtendedResponseListener extendedResponseListener;

    private RetailAccountsExtendedResponseListener retailAccountsExtendedResponseListener;
    private SourceOfFundsExtendedResponseListener sourceOfFundsExtendedResponseListener;
    private BankBranchDetailsResponseListener bankBranchDetailsResponseListener;

    EditPolicyPaymentDetailsPresenter(@NotNull WeakReference<EditPolicyPaymentDetailsView> viewWeakReference) {
        super(viewWeakReference);
        paymentsService = new PaymentsInteractor();
        extendedResponseListener = new EditPolicyPaymentDetailsExtendedResponseListener(this);
        funeralCoverQuoteInteractor = new FuneralCoverQuoteInteractor();
        retailAccountsExtendedResponseListener = new RetailAccountsExtendedResponseListener(this);
        sourceOfFundsExtendedResponseListener = new SourceOfFundsExtendedResponseListener(this);
        bankBranchDetailsResponseListener = new BankBranchDetailsResponseListener(this);
        sharedService = new SharedInteractor();
    }

    void onBankNameInputViewClicked(boolean isExergyPolicy) {
        if (isExergyPolicy) {
            paymentsService.fetchExergyBankList(new ExergyBankListResponseListener(this));
        } else {
            paymentsService.fetchBankList(extendedResponseListener);
        }
    }

    public void onBankListReceived(BankDetails successResponse) {
        EditPolicyPaymentDetailsView paymentDetailsView = (EditPolicyPaymentDetailsView) viewWeakReference.get();
        if (paymentDetailsView != null) {
            if (successResponse != null) {
                paymentDetailsView.displayListOfBankAccounts(successResponse);
            } else {
                paymentDetailsView.showSomethingWentWrongScreen();
            }
            paymentDetailsView.dismissProgressDialog();
        }
    }

    public void onExergyBankListReceived(ExergyBankListResponse successResponse) {
        EditPolicyPaymentDetailsView paymentDetailsView = (EditPolicyPaymentDetailsView) viewWeakReference.get();
        if (paymentDetailsView != null) {
            if (successResponse != null) {
                paymentDetailsView.displayListOfExergyBanks(successResponse);
            } else {
                paymentDetailsView.showSomethingWentWrongScreen();
            }
            paymentDetailsView.dismissProgressDialog();
        }
    }

    public void onFailedToLoadBankList() {
        EditPolicyPaymentDetailsView paymentDetailsView = (EditPolicyPaymentDetailsView) viewWeakReference.get();
        if (paymentDetailsView != null) {
            paymentDetailsView.showSomethingWentWrongScreen();
            paymentDetailsView.dismissProgressDialog();
        }
    }

    void loadRetailsAccounts() {
        funeralCoverQuoteInteractor.fetchRetailAccounts(retailAccountsExtendedResponseListener);
    }

    @Override
    public void onFailedToLoadSourceOfFunds() {
        dismissProgressIndicator();
        EditPolicyPaymentDetailsView view = (EditPolicyPaymentDetailsView) viewWeakReference.get();
        if (view != null) {
            view.showSomethingWentWrongScreen();
        }
    }

    @Override
    public void onRetailAccountsLoaded(RetailAccountsResponse retailAccountsResponse) {
        dismissProgressIndicator();
        EditPolicyPaymentDetailsView paymentDetailsView = (EditPolicyPaymentDetailsView) viewWeakReference.get();
        if (paymentDetailsView != null) {
            if (retailAccountsResponse != null) {
                ArrayList<RetailAccount> retailAccountsList = retailAccountsResponse.getRetailAccountsList();
                if (retailAccountsList != null && !retailAccountsList.isEmpty()) {
                    paymentDetailsView.displayRetailAccounts(retailAccountsList);
                }
            } else {
                paymentDetailsView.showSomethingWentWrongScreen();
            }
        }

        sharedService.performLookup(CIFGroupCode.SOURCE_OF_FUNDS, sourceOfFundsExtendedResponseListener);
    }

    @Override
    public void onSourceOfFundsLoaded(LookupResult lookupResult) {
        EditPolicyPaymentDetailsView paymentDetailsView = (EditPolicyPaymentDetailsView) viewWeakReference.get();
        if (paymentDetailsView != null) {
            if (BMBConstants.SUCCESS.equalsIgnoreCase(lookupResult.getTransactionStatus())) {
                paymentDetailsView.displaySourceOfFunds(lookupResult.getItems());
            } else if (BMBConstants.FAILURE.equalsIgnoreCase(lookupResult.getTransactionStatus())) {
                paymentDetailsView.showSomethingWentWrongScreen();
            }
            paymentDetailsView.dismissProgressDialog();
        }
    }

    @Override
    public void onFailedToLoadRetailAccounts() {
        dismissProgressIndicator();
        EditPolicyPaymentDetailsView paymentDetailsView = (EditPolicyPaymentDetailsView) viewWeakReference.get();
        if (paymentDetailsView != null) {
            paymentDetailsView.showSomethingWentWrongScreen();
        }
    }

    public void onBranchInputViewClicked(String bankName, Boolean isExergyPolicy) {
        if (isExergyPolicy) {
            paymentsService.fetchExergyBranchList(bankName, new ExergyBranchListResponseListener(this));
        } else {
            paymentsService.fetchBranchList(bankName, bankBranchDetailsResponseListener);
        }
    }

    public void onBankBranchListReceived(BankBranches bankBranches) {
        EditPolicyPaymentDetailsView paymentDetailsView = (EditPolicyPaymentDetailsView) viewWeakReference.get();
        if (paymentDetailsView != null) {
            if (bankBranches != null) {
                paymentDetailsView.displayListOfBankBranches(bankBranches);
            }
            paymentDetailsView.dismissProgressDialog();
        }
    }

    public void onExergyBankBranchesReceived(ExergyBranchListResponse successResponse) {
        EditPolicyPaymentDetailsView paymentDetailsView = (EditPolicyPaymentDetailsView) viewWeakReference.get();
        if (paymentDetailsView != null) {
            if (successResponse != null) {
                paymentDetailsView.displayListOfExergyBankBranches(successResponse);
            }
            paymentDetailsView.dismissProgressDialog();
        }
    }
}
