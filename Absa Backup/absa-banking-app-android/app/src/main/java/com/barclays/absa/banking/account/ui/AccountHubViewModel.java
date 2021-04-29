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
package com.barclays.absa.banking.account.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.barclays.absa.banking.boundary.model.AccountDetail;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.OverdraftGadgets;
import com.barclays.absa.banking.boundary.model.OverdraftStatus;
import com.barclays.absa.banking.boundary.model.Transaction;
import com.barclays.absa.banking.businessBanking.services.AccountInteractor;
import com.barclays.absa.banking.businessBanking.services.AccountService;
import com.barclays.absa.banking.card.ui.TransactionHistoryComparison;
import com.barclays.absa.banking.framework.BuildConfigHelper;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitching;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.overdraft.services.OverdraftInteractor;
import com.barclays.absa.banking.overdraft.services.OverdraftService;
import com.barclays.absa.utils.CommonUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

//Note for older API ViewModel must be public to avoid : java.lang.IllegalAccessException: access to class not allowed
public class AccountHubViewModel extends ViewModel {
    private String fromDate, toDate;
    private AccountService accountInteractor;
    private OverdraftService overdraftService = new OverdraftInteractor();
    private AccountDetail accountDetailResponse;
    private final MutableLiveData<AccountDetail> accountDetailLiveData = new MutableLiveData<>();
    private final MutableLiveData<AccountObject> accountObjectLiveData = new MutableLiveData<>();
    private final MutableLiveData<OverdraftGadgets> overdraftGadgetsObjectLiveData = new MutableLiveData<>();

    private final List<Transaction> originalTransactionList = new ArrayList<>();
    private AccountHubView view;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    private final ExtendedResponseListener<AccountDetail> clearedTransactionsExtendedResponseListener = new ExtendedResponseListener<AccountDetail>() {
        @Override
        public void onSuccess(AccountDetail clearedTransactionsAccountDetail) {
            accountDetailResponse = clearedTransactionsAccountDetail;
            originalTransactionList.clear();
            originalTransactionList.addAll(clearedTransactionsAccountDetail.getTransactions());
            accountInteractor.fetchDateBasedUnclearedAccountTransactionHistory(
                    getAccountObjectLiveData().getValue(), fromDate, toDate, unclearedTransactionsResponseListener);
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            if (AppConstants.RESPONSE_CODE_ACCOUNT_CLOSED.equals(failureResponse.getResponseCode())) {
                view.dismissProgressDialog();
                view.navigateToAccountClosedErrorScreen();
            } else {
                accountInteractor.fetchDateBasedUnclearedAccountTransactionHistory(
                        getAccountObjectLiveData().getValue(), fromDate, toDate, unclearedTransactionsResponseListener);
            }
        }
    };

    private final ExtendedResponseListener<OverdraftStatus> overdraftStatusExtendedResponseListener = new ExtendedResponseListener<OverdraftStatus>() {

        @Override
        public void onSuccess(OverdraftStatus overdraftStatus) {
            if (overdraftStatus != null) {
                if (overdraftStatus.getTransactionStatus() != null && overdraftStatus.getOverdraftGadgets() != null) {
                    if (BMBConstants.SUCCESS.equalsIgnoreCase(overdraftStatus.getTransactionStatus())) {
                        OverdraftGadgets gadgets = overdraftStatus.getOverdraftGadgets();
                        if (gadgets != null) {
                            overdraftGadgetsObjectLiveData.setValue(gadgets);
                        }
                    }
                }
            }
            view.dismissProgressDialog();
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            overdraftGadgetsObjectLiveData.setValue(null);
            view.dismissProgressDialog();
        }
    };

    private final ExtendedResponseListener<AccountDetail> unclearedTransactionsResponseListener = new ExtendedResponseListener<AccountDetail>() {

        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(final AccountDetail unclearedTransactionsAccountDetail) {
            accountDetailResponse = unclearedTransactionsAccountDetail;
            if (unclearedTransactionsAccountDetail != null) {
                unclearedTransactionsAccountDetail.getTransactions();
                AccountObject accountObject = unclearedTransactionsAccountDetail.getAccountObject();
                if (accountObject != null) {
                    if (!accountObject.isSavingAccount() && !unclearedTransactionsAccountDetail.getTransactions().isEmpty()) {
                        for (Transaction transactionItem : unclearedTransactionsAccountDetail.getTransactions()) {
                            transactionItem.setUnclearedTransaction(true);
                            originalTransactionList.add(transactionItem);
                        }
                    }
                }
            }
            List<Transaction> filteredTransactions = filterTransactionsByDate(originalTransactionList, fromDate, toDate);
            updateTransactionsList(filteredTransactions, unclearedTransactionsAccountDetail);
            FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
            if (featureSwitchingToggles.getOverdraftVCL() == FeatureSwitchingStates.DISABLED.getKey()) {
                view.dismissProgressDialog();
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            updateTransactionsList(originalTransactionList, accountDetailResponse);
            FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
            if (featureSwitchingToggles.getOverdraftVCL() == FeatureSwitchingStates.DISABLED.getKey()) {
                view.dismissProgressDialog();
            }
        }
    };

    private void updateTransactionsList(List<Transaction> transactionsList, AccountDetail successResponse) {
        if (successResponse != null) {
            if (transactionsList != null) {
                Collections.sort(transactionsList, new TransactionHistoryComparison());
                successResponse.setTransactions(transactionsList);
            }
            appCacheService.setFilteredCreditCardTransactions(successResponse);
            accountDetailLiveData.setValue(successResponse);
            view.applyFilter();
        }
    }

    public MutableLiveData<AccountDetail> getAccountDetailLiveData() {
        return accountDetailLiveData;
    }

    private List<Transaction> filterTransactionsByDate(List<Transaction> transactionsList, String from, String to) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", CommonUtils.getCurrentApplicationLocale());
        List<Transaction> resultList = new ArrayList<>();
        if (BuildConfigHelper.STUB) {
            resultList.addAll(transactionsList);
        } else
            try {
                Date fromDate = dateFormat.parse(from);
                Date toDate = dateFormat.parse(to);
                for (Transaction transaction : transactionsList) {
                    Date transactionDate = dateFormat.parse(transaction.getTransactionDate());
                    boolean fromDateIsLessThanOrEqualToTransactionDate = false;
                    if (fromDate != null) {
                        fromDateIsLessThanOrEqualToTransactionDate = fromDate.before(transactionDate) || fromDate.equals(transactionDate);
                    }
                    boolean transactionDateIsLessThanOrEqualToTransactionDate = false;
                    if (transactionDate != null) {
                        transactionDateIsLessThanOrEqualToTransactionDate = transactionDate.equals(toDate) || transactionDate.before(toDate);
                    }
                    if (fromDateIsLessThanOrEqualToTransactionDate && transactionDateIsLessThanOrEqualToTransactionDate) {
                        resultList.add(transaction);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
                resultList = transactionsList;
            }
        return resultList;
    }

    // Note for lower API constructor must be public to avoid : java.lang.IllegalAccessException: access to constructor not allowed
    public AccountHubViewModel() {
    }

    public void init(AccountHubView view) {
        this.view = view;
        accountInteractor = new AccountInteractor();
        clearedTransactionsExtendedResponseListener.setView(view);
        unclearedTransactionsResponseListener.setView(view);
    }

    public void setAccountObject(AccountObject accountObject, String fromDate, String toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        getAccountObjectLiveData().setValue(accountObject);
    }

    public MutableLiveData<AccountObject> getAccountObjectLiveData() {
        return accountObjectLiveData;
    }

    public AccountObject getAccountObject() {
        return accountObjectLiveData.getValue();
    }

    public AccountDetail getAccountDetail() {
        return accountDetailLiveData.getValue();
    }

    public String getAccountNumber() {
        return (getAccountDetail() == null || getAccountDetail().getAccountObject() == null) ?
                null : getAccountDetail().getAccountObject().getAccountNumber();
    }

    void fetchOverdraftStatus() {
        overdraftStatusExtendedResponseListener.setView(view);
        overdraftService.fetchOverdraftStatus(overdraftStatusExtendedResponseListener);
    }

    MutableLiveData<OverdraftGadgets> getOverdraftGadgetsObjectLiveData() {
        return overdraftGadgetsObjectLiveData;
    }
}
