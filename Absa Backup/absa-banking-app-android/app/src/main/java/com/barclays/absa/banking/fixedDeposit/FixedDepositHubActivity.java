/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.fixedDeposit;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.account.ui.AccountHubView;
import com.barclays.absa.banking.account.ui.AccountHubViewModel;
import com.barclays.absa.banking.boundary.model.AccountDetail;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.boundary.model.Transaction;
import com.barclays.absa.banking.card.ui.creditCard.TransactionHistoryView;
import com.barclays.absa.banking.card.ui.creditCard.hub.FilteringOptions;
import com.barclays.absa.banking.card.ui.creditCard.hub.TransactionHistoryFilterFragment;
import com.barclays.absa.banking.card.ui.creditCard.hub.TransactionHistoryFragment;
import com.barclays.absa.banking.databinding.FixedDepositHubActivityBinding;
import com.barclays.absa.banking.express.transactionHistory.TransactionHistoryViewModel;
import com.barclays.absa.banking.express.transactionHistory.dto.AccountHistoryLines;
import com.barclays.absa.banking.express.transactionHistory.dto.HistoryRequest;
import com.barclays.absa.banking.fixedDeposit.responseListeners.FixedDepositViewModel;
import com.barclays.absa.banking.fixedDeposit.services.dto.FixedDepositAccountDetailsResponse;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.utils.AnalyticsUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import styleguide.bars.CollapsingAppBarView;
import styleguide.bars.FragmentPagerItem;

public class FixedDepositHubActivity extends BaseActivity implements TransactionHistoryFilterFragment.UpdateFilteringOptions, TransactionHistoryView, AccountHubView {

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", BMBApplication.getApplicationLocale());
    public static final String ACCOUNT_NUMBER = "ACCOUNT_NUMBER";
    public static final String CREDIT = "Credit";
    public static final String CARD_NUMBER = "CARD_NUMBER";
    public static final String ALL_TRANSACTIONS = "A";

    private AccountHubViewModel viewModel;
    private AccountDetail accountDetail;
    private String toDate;
    private String fromDate;

    private FixedDepositHubActivityBinding binding;
    private FixedDepositHeaderFragment headerFragment;
    private TransactionHistoryFragment transactionHistoryFragment;

    private List<Transaction> transactions;

    private FixedDepositViewModel fixedDepositViewModel;
    private FilteringOptions filteringOptions;
    private FixedDepositAccountDetailsResponse fixedDepositAccountDetailsResponse;
    private TransactionHistoryViewModel transactionHistoryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.fixed_deposit_hub_activity);
        AnalyticsUtil.INSTANCE.trackAction(FixedDepositActivity.FIXED_DEPOSIT, "FixedTermDepositAccount_HubScreen_ScreenDisplayed");

        final AccountObject accountObject = (AccountObject) getIntent().getSerializableExtra(IntentFactory.ACCOUNT_OBJECT);
        transactionHistoryViewModel = new ViewModelProvider(this).get(TransactionHistoryViewModel.class);

        viewModel = new ViewModelProvider(this).get(AccountHubViewModel.class);
        viewModel.init(this);

        viewModel.getAccountDetailLiveData().observe(this, accountDetail -> {
            this.accountDetail = accountDetail;
            if (accountDetail != null) {
                getAppCacheService().setTransactions(accountDetail);
                transactions = accountDetail.getTransactions();
            }

            if (fixedDepositAccountDetailsResponse == null) {
                fixedDepositViewModel = new ViewModelProvider(this).get(FixedDepositViewModel.class);

                fixedDepositViewModel.getAccountDetailsResponse().observe(this, fixedDepositAccountDetailsResponse -> {
                    this.fixedDepositAccountDetailsResponse = fixedDepositAccountDetailsResponse;
                    headerFragment.setupFragment(getAccountObject(), fixedDepositAccountDetailsResponse);
                    setupTabView(accountDetail);
                    dismissProgressDialog();
                });

                fixedDepositViewModel.fetchAccountDetails(getAccountObject().getAccountNumber());
            } else {
                dismissProgressDialog();
            }
        });

        filteringOptions = new FilteringOptions();
        filteringOptions.setFilterType(ALL_TRANSACTIONS);
        headerFragment = FixedDepositHeaderFragment.newInstance();
        binding.collapsingAppbarView.addHeaderView(headerFragment);
        binding.collapsingAppbarView.setBackground(R.drawable.gradient_orange_dark_orange);


        if (accountObject != null) {
            setToolBarBack(accountObject.getDescription());
            viewModel.getAccountObjectLiveData().setValue(accountObject);
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accountDetail == null) {
            requestTransactionHistory();
        }
    }

    public void setupTabView(AccountDetail accountDetail) {
        SparseArray<FragmentPagerItem> tabs = new SparseArray<>();
        String transactionsDescription = getString(R.string.credit_card_hub_transactions_tab);
        String detailsDescription = fixedDepositViewModel.isIslamicAccount() ? getString(R.string.details) : getString(R.string.fixed_deposit_tab_label);

        if (accountDetail != null) {
            filteringOptions.setToDate(accountDetail.getToDate());
            filteringOptions.setFromDate(accountDetail.getFromDate());
        }

        transactionHistoryFragment = TransactionHistoryFragment.newInstance(transactionsDescription, "Fixed deposit hub");
        tabs.append(0, transactionHistoryFragment);
        tabs.append(1, FixedDepositDetailsFragment.Companion.newInstance(detailsDescription));

        binding.collapsingAppbarView.setUpTabs(this, tabs);

        attachSearchViewCallbacks();

        binding.collapsingAppbarView.setOnPageSelectionListener((description, position) -> {
            if (getString(R.string.credit_card_hub_transactions_tab).equalsIgnoreCase(description)) {
                attachSearchViewCallbacks();
            } else {
                detachSearchViewCallbacks();
            }
        });
    }

    @NonNull
    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void showBottomBarMenu() {
        TransactionHistoryFilterFragment filterBottomDialogFragment = TransactionHistoryFilterFragment.newInstance(this, filteringOptions, "Fixed deposit hub");
        filterBottomDialogFragment.show(getSupportFragmentManager(), "filter_dialog_fragment");
    }

    public void updateFilteringOptions(FilteringOptions filteringOption) {
        if ((filteringOption.getFromDate() != null && filteringOption.getFromDate().equals(filteringOption.getFromDate())) || filteringOption.getToDate() != null && filteringOption.getToDate().equals(filteringOption.getToDate())) {
            requestTransactionHistory(filteringOption.getFromDate(), filteringOption.getToDate());
        } else {
            transactionHistoryFragment.updateFilter(filteringOption);
        }

        this.filteringOptions = filteringOption;
    }

    @NonNull
    public FilteringOptions getFilterOptions() {
        return filteringOptions;
    }

    public void collapseAppBar() {
        binding.collapsingAppbarView.collapseAppBar();
    }

    private void attachSearchViewCallbacks() {
        binding.collapsingAppbarView.setOnViewPropertiesChangeListener((state) -> {
            if (state == CollapsingAppBarView.State.EXPANDED) {
                if (transactionHistoryFragment != null) {
                    transactionHistoryFragment.showCalendarFilterBar();
                }
                binding.collapsingAppbarView.hideSearchView();
            }
        });

        binding.collapsingAppbarView.setOnSearchQueryListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                transactionHistoryFragment.searchTransactionHistory(newText);
                return true;
            }
        });
    }

    private void detachSearchViewCallbacks() {
        if (transactionHistoryFragment != null) {
            transactionHistoryFragment.showCalendarFilterBar();
        }
        binding.collapsingAppbarView.hideSearchView();
        binding.collapsingAppbarView.setOnViewPropertiesChangeListener(null);
        binding.collapsingAppbarView.setOnSearchQueryListener(null);
    }

    public void collapseAppBarView() {
        binding.collapsingAppbarView.showSearchView();
        binding.collapsingAppbarView.collapseAppBar();
    }

    public void navigateToPreviousScreen() {
        if (binding.collapsingAppbarView.getSearchView().getVisibility() == View.VISIBLE && !binding.collapsingAppbarView.getSearchView().isIconified()) {
            binding.collapsingAppbarView.getSearchView().setIconified(true);
            binding.collapsingAppbarView.expandAppBar();
            binding.collapsingAppbarView.hideSearchView();
        } else {
            finish();
        }
    }

    @Override
    public void applyFilter() {
        if (transactionHistoryFragment != null) {
            transactionHistoryFragment.updateTransaction(transactions, filteringOptions);
        }
    }

    @Override
    public void navigateToAccountClosedErrorScreen() {
        Intent errorIntent = IntentFactory.getGenericResultFailureBuilder(this)
                .setGenericResultHeaderMessage(R.string.fixed_deposit_account_closed)
                .setGenericResultSubMessage(R.string.fixed_deposit_account_closed_sub_message)
                .setGenericResultBottomButton(R.string.ok, view -> loadAccountsAndGoHome())
                .build();
        startActivity(errorIntent);
    }

    public AccountObject getAccountObject() {
        return viewModel.getAccountObjectLiveData().getValue();
    }

    private void requestTransactionHistory() {
        BMBLogger.d("x-x", "AccountActivity.requestTransactionHistory()");

        Calendar calendar = Calendar.getInstance();
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+02:00"));

        toDate = formatter.format(calendar.getTime());
        filteringOptions.setToDate(toDate);

        calendar.add(Calendar.DAY_OF_MONTH, -7);

        fromDate = formatter.format(calendar.getTime());
        filteringOptions.setFromDate(fromDate);

        viewModel.setAccountObject(getAccountObject(), fromDate, toDate);

        requestHistory();
    }

    private void requestHistory() {
        HistoryRequest historyRequest = new HistoryRequest();
        historyRequest.setFromAccountNumber(getAccountObject().getAccountNumber());
        historyRequest.setFromDate(fromDate);
        historyRequest.setToDate(toDate);
        historyRequest.setAccountType(getAccountObject().getAccountType());
        transactionHistoryViewModel.fetchTransactionHistory(historyRequest);
        setUpTransactionHistoryModel();
    }

    private void setUpTransactionHistoryModel() {
        transactionHistoryViewModel.transactionHistoryLiveData.observe(this, transactionHistoryResponse -> {
            AccountDetail account = new AccountDetail();

            List<Transaction> transactions = new ArrayList<>();

            for (AccountHistoryLines accountHistoryLine : transactionHistoryResponse.getTransactionHistory().getAccountHistoryLines()) {
                Transaction transaction = new Transaction();
                transaction.setBalance(new Amount(accountHistoryLine.getBalanceAmount()));
                transaction.setDescription(accountHistoryLine.getTransactionDescription());
                transaction.setReferenceNumber(accountHistoryLine.getTransactionDescription());
                transaction.setTransactionCategory(String.valueOf(accountHistoryLine.getTransactionCategory()));
                transaction.setTransactionDate(accountHistoryLine.getTransactionDate());
                transaction.setUnclearedTransaction(accountHistoryLine.getTransactionCategory() == 1);
                Amount amount = new Amount(accountHistoryLine.getTransactionAmount());

                if (amount.getAmountDouble() > 0) {
                    transaction.setCreditAmount(amount);
                } else {
                    transaction.setCreditAmount(new Amount("0.00"));
                    transaction.setDebitAmount(amount);
                }

                transactions.add(transaction);
            }

            account.setFromDate(fromDate);
            account.setToDate(toDate);
            account.setTransactions(transactions);

            getAppCacheService().setFilteredCreditCardTransactions(account);
            viewModel.getAccountDetailLiveData().setValue(account);
            applyFilter();
        });
    }

    @Override
    public void onBackPressed() {
        AnalyticsUtil.INSTANCE.trackAction(FixedDepositActivity.FIXED_DEPOSIT, "FixedTermDeposit_HubScreen_BackButtonClicked");
        if (binding.collapsingAppbarView.getSearchView().getVisibility() == View.VISIBLE && !binding.collapsingAppbarView.getSearchView().isIconified()) {
            binding.collapsingAppbarView.getSearchView().setIconified(true);
            binding.collapsingAppbarView.expandAppBar();
            binding.collapsingAppbarView.hideSearchView();
            transactionHistoryFragment.showCalendarFilterBar();
        } else {
            finish();
        }
    }

    private void requestTransactionHistory(String fromDate, String toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        viewModel.setAccountObject(getAccountObject(), fromDate, toDate);
        requestHistory();
    }
}