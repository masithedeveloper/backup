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
package com.barclays.absa.banking.card.ui.creditCard.hub;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.Transaction;
import com.barclays.absa.banking.card.ui.creditCard.TransactionHistoryView;
import com.barclays.absa.banking.databinding.TransactionHistoryFragmentBinding;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.shared.ItemPagerFragment;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.DateUtils;
import com.barclays.absa.utils.ViewAnimation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryFragment extends ItemPagerFragment {

    private static final String ANALYTICS_FEATURE = "ANALYTICS_FEATURE";
    private static final String SRC_DATE_PATTERN = "yyyy-MM-dd";
    private static final String DEST_DATE_PATTERN = "dd MMM yyyy";
    private TransactionHistoryFragmentBinding binding;
    private TransactionsAdapter transactionsAdapter = null;
    private ViewAnimation viewAnimation;
    private AppCompatActivity activity;
    private List<Transaction> transactions;
    private String featureName = "";

    public TransactionHistoryFragment() {
    }

    public static TransactionHistoryFragment newInstance(String description, String featureName) {
        Bundle args = new Bundle();
        args.putString(Companion.getTAB_DESCRIPTION_KEY(), description);
        args.putString(ANALYTICS_FEATURE, featureName);
        TransactionHistoryFragment fragment = new TransactionHistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.transaction_history_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TransactionHistoryView transactionHistoryView = (TransactionHistoryView) getActivity();
        if (transactionHistoryView != null && savedInstanceState == null) {
            transactions = transactionHistoryView.getTransactions();
        } else {
            transactions = (List<Transaction>) savedInstanceState.getSerializable("transactions");
        }
        setupTransactionsList();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
    }

    private void setupTransactionsList() {
        TransactionHistoryView transactionHistoryView = (TransactionHistoryView) this.activity;
        if (transactionHistoryView == null) {
            BMBApplication.getInstance().forceSignOut();
            return;
        }

        binding.transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.transactionsRecyclerView.setHasFixedSize(true);

        viewAnimation = new ViewAnimation(binding.transactionsFilterAndSearchView);
        binding.transactionsFilterAndSearchView.setOnSearchClickListener(v -> {
            hideCalendarFilterBar();
            transactionHistoryView.collapseAppBarView();
        });

        if (transactions != null && !transactions.isEmpty()) {
            setUpAdapter();
        } else {
            binding.transactionsRecyclerView.setVisibility(View.GONE);
            binding.noTransactionsTextView.setVisibility(View.VISIBLE);
        }

        if (getArguments() != null) {
            featureName = getArguments().getString(ANALYTICS_FEATURE);
        }

        AnalyticsUtil.INSTANCE.trackAction("Transaction history tab", featureName);

        binding.transactionsFilterAndSearchView.setOnCalendarLayoutClickListener(v -> {
            transactionHistoryView.trackCustomAction(String.format(" %s - Search button", featureName));
            transactionHistoryView.collapseAppBar();
            transactionHistoryView.showBottomBarMenu();
        });

        updateDateSearchFields(transactionHistoryView.getFilterOptions());
    }

    private void setUpAdapter() {
        transactionsAdapter = new TransactionsAdapter(this, transactions);
        binding.transactionsRecyclerView.setAdapter(transactionsAdapter);
    }

    public void invalidateAdapter() {
        transactionsAdapter = null;
    }

    @NonNull
    @Override
    protected String getTabDescription() {
        String tabDescription = "";
        Bundle arguments = getArguments();
        if (arguments != null) {
            tabDescription = arguments.getString(Companion.getTAB_DESCRIPTION_KEY());
        }

        return tabDescription != null ? tabDescription : "";
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("transactions", (Serializable) transactions);
    }

    public void updateFilter(FilteringOptions filteringOptions) {
        updateDateSearchFields(filteringOptions);
        if (transactionsAdapter != null) {
            switch (filteringOptions.getFilterType()) {
                case CreditCardHubActivity.ALL_TRANSACTIONS:
                    if (transactions != null) {
                        transactionsAdapter.refreshItemList(transactions);
                        setNoTransactionsVisible(transactions);
                    }
                    break;
                case CreditCardHubActivity.MONEY_IN:
                    transactionsAdapter.refreshItemList(moneyInFilter());
                    break;
                case CreditCardHubActivity.MONEY_OUT:
                    transactionsAdapter.refreshItemList(moneyOutFilter());
                    break;
                case CreditCardHubActivity.UNCLEARED:
                    transactionsAdapter.refreshItemList(unclearedTransactionsFilter());
                    break;
            }
        }
    }

    private void updateDateSearchFields(FilteringOptions filteringOptions) {
        if (filteringOptions != null) {
            String date = DateUtils.formatDate(filteringOptions.getFromDate(), SRC_DATE_PATTERN, DEST_DATE_PATTERN) + " - " + DateUtils.formatDate(filteringOptions.getToDate(), SRC_DATE_PATTERN, DEST_DATE_PATTERN);
            binding.transactionsFilterAndSearchView.setSearchText(date);
        }
    }

    private List<Transaction> moneyInFilter() {
        List<Transaction> listTransactions = new ArrayList<>();

        for (Transaction item : transactions) {
            if (item.getCreditAmount() != null && !"0.00".equals(item.getCreditAmount().getAmount()) && !item.isUnclearedTransaction()) {
                listTransactions.add(item);
            }
        }
        setNoTransactionsVisible(listTransactions);
        return listTransactions;
    }

    private List<Transaction> moneyOutFilter() {
        List<Transaction> listTransactions = new ArrayList<>();

        for (Transaction item : transactions) {
            if (item.getDebitAmount() != null && !"0.00".equals(item.getDebitAmount().getAmount()) && !item.isUnclearedTransaction()) {
                listTransactions.add(item);
            }
        }
        setNoTransactionsVisible(listTransactions);
        return listTransactions;
    }

    private List<Transaction> unclearedTransactionsFilter() {
        List<Transaction> listTransactions = new ArrayList<>();

        for (Transaction item : transactions) {
            if (item.isUnclearedTransaction()) {
                listTransactions.add(item);
            }
        }
        setNoTransactionsVisible(listTransactions);
        return listTransactions;
    }

    void setNoTransactionsVisible(List<Transaction> listTransactions) {
        if (listTransactions.isEmpty()) {
            binding.noTransactionsTextView.setVisibility(View.VISIBLE);
            binding.transactionsRecyclerView.setVisibility(View.GONE);
        } else {
            binding.noTransactionsTextView.setVisibility(View.GONE);
            binding.transactionsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void searchTransactionHistory(String query) {
        if (transactionsAdapter != null) {
            transactionsAdapter.filter(query);
        }
    }

    public void showCalendarFilterBar() {
        if (binding != null && binding.transactionsFilterAndSearchView.getVisibility() == View.GONE) {
            viewAnimation.expandView(500);
        }
    }

    private void hideCalendarFilterBar() {
        if (binding.transactionsFilterAndSearchView.getVisibility() == View.VISIBLE) {
            viewAnimation.collapseView(500);
        }
    }

    public void updateTransaction(List<Transaction> transactions, FilteringOptions filteringOptions) {
        this.transactions = transactions;
        if (transactionsAdapter == null) {
            setUpAdapter();
        }
        transactionsAdapter.refreshItemList(transactions);
        updateFilter(filteringOptions);
    }
}