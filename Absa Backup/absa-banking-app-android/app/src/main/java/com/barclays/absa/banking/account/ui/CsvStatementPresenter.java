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

import androidx.annotation.NonNull;

import com.barclays.absa.banking.account.services.AccountInteractor;
import com.barclays.absa.banking.account.services.AccountService;
import com.barclays.absa.banking.boundary.model.AccountDetail;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.Transaction;
import com.barclays.absa.banking.card.ui.TransactionHistoryComparison;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.data.ResponseObject;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CsvStatementPresenter implements StatementContract.CsvPresenter {
    private WeakReference<StatementContract.CsvView> weakReference;
    private AccountService accountService;
    private AccountObject accountObject;
    private List<Transaction> transactions = new ArrayList<>();
    private String fromDate, toDate;

    private ExtendedResponseListener<AccountDetail> clearedTransactionsListener = new ExtendedResponseListener<AccountDetail>() {
        @Override
        public void onSuccess(AccountDetail clearedAccountDetail) {
            transactions.clear();
            transactions.addAll(clearedAccountDetail.getTransactions());
            final StatementContract.CsvView view = weakReference.get();
            if (view != null) {
                unclearedTransactionsListener.setView(view);
                accountService.fetchDateBasedUnclearedAccountTransactionHistory(clearedAccountDetail.getAccountObject(), fromDate, toDate, unclearedTransactionsListener);
            }
        }

        @Override
        public void onFailure(ResponseObject response) {
            final StatementContract.CsvView view = weakReference.get();
            if (view != null)
                view.showMessageError(ResponseObject.extractErrorMessage(response));
            generateCsv();
        }
    };
    private ExtendedResponseListener<AccountDetail> unclearedTransactionsListener = new ExtendedResponseListener<AccountDetail>() {

        @Override
        public void onRequestStarted() {
            // do nothing progress indicator is already showing
        }

        @Override
        public void onSuccess(final AccountDetail unclearedAccountDetail) {
            final StatementContract.CsvView view = weakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                if (!accountObject.isSavingAccount()) {
                    for (Transaction transactionItem : unclearedAccountDetail.getTransactions()) {
                        transactionItem.setUnclearedTransaction(true);
                        transactions.add(transactionItem);
                    }
                }
                generateCsv();
            }
        }

        @Override
        public void onFailure(final ResponseObject response) {
            final StatementContract.CsvView view = weakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                view.showMessageError(ResponseObject.extractErrorMessage(response));
            }
        }
    };


    private void generateCsv() {
        Collections.sort(transactions, new TransactionHistoryComparison());
        final StatementContract.CsvView view = weakReference.get();
        StringBuilder lines = view != null ? view.createCSV() :
                new StringBuilder(String.format(BMBApplication.getApplicationLocale(), "%s, %s, %s, %s", "Date", "Description", "Amount", "Balance"));

        BigDecimal zero = new BigDecimal("0");
        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            if (transaction != null) {
                BigDecimal credit = transaction.getCreditAmount().getAmountValue();
                BigDecimal debit = transaction.getDebitAmount().getAmountValue();
                boolean isNegatif = debit.compareTo(zero) != 0;
                BigDecimal amount = debit.compareTo(zero) != 0 ? debit : credit;
                if (transaction.isUnclearedTransaction()) {
                    isNegatif = false;
                }

                BigDecimal balance = transaction.getBalance().getAmountValue();
                String currency = isNegatif ? transaction.getDebitAmount().getCurrency() : transaction.getCreditAmount().getCurrency();
                String amountPrefix = (isNegatif ? "-" : "") + currency;
                String balancePrefix = (balance.compareTo(zero) < 0 ? "-" : "") + currency;
                String description = transaction.getDescription() == null ? "" :
                        transaction.getDescription().replace(",", " ");

                lines.append(String.format(BMBApplication.getApplicationLocale(), "\n%s, %s, %s, %s",
                        transaction.getTransactionDate(), description,
                        amountPrefix + " " + amount.abs(), balancePrefix + " " + balance.abs()));
            }
        }
        if (view != null) {
            view.showCsv(lines.toString());
        }
    }

    private CsvStatementPresenter(StatementContract.CsvView view, AccountService accountService) {
        this.weakReference = new WeakReference<>(view);
        this.accountService = accountService;
        init(view);
    }

    CsvStatementPresenter(StatementContract.CsvView view) {
        this(view, new AccountInteractor());
        init(view);
    }

    private void init(StatementContract.CsvView view) {
        clearedTransactionsListener.setView(view);
        unclearedTransactionsListener.setView(view);
    }

    @Override
    public void generateCsv(@NonNull AccountObject accountObject, String fromDate, String toDate) {
        this.accountObject = accountObject;
        this.fromDate = fromDate;
        this.toDate = toDate;

        final StatementContract.CsvView view = weakReference.get();
        if (view != null) {
            clearedTransactionsListener.setView(view);
            accountService.fetchDateBasedClearedAccountTransactionHistory(accountObject, fromDate, toDate, clearedTransactionsListener);
        }
    }
}