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

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccountDetail;
import com.barclays.absa.banking.boundary.model.Transaction;
import com.barclays.absa.banking.card.ui.creditCard.hub.CreditCardHubActivity;
import com.barclays.absa.banking.card.ui.creditCard.hub.FilteringOptions;
import com.barclays.absa.banking.databinding.AccountActivityTransactionsFragmentBinding;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.shared.ItemPagerFragment;

import java.util.ArrayList;
import java.util.List;

public class AccountActivityTransactionsFragment extends ItemPagerFragment implements LifecycleOwner, TransactionsView {
    private final String TAG = AccountActivityTransactionsFragment.class.getSimpleName();
    private AccountActivityTransactionsFragmentBinding binding;
    private TransactionRecyclerViewAdapter adapter;

    public static AccountActivityTransactionsFragment newInstance(String description) {
        AccountActivityTransactionsFragment accountActivityTransactionsFragment = new AccountActivityTransactionsFragment();
        Bundle arguments = new Bundle();
        arguments.putString(Companion.getTAB_DESCRIPTION_KEY(), description);
        accountActivityTransactionsFragment.setArguments(arguments);
        return accountActivityTransactionsFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        AccountHubViewModel viewModel = new ViewModelProvider(getAccountActivity()).get(AccountHubViewModel.class);
        viewModel.getAccountDetailLiveData().observe(this, this::updateData);
    }

    private void updateData(AccountDetail accountDetail) {
        boolean hasNoTransaction = accountDetail == null || accountDetail.getTransactions().isEmpty();
        setNoTransactions(hasNoTransaction);
        adapter.setData(accountDetail);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        BMBLogger.d("x-c", "onCreateView");
        BMBLogger.d(TAG, "onCreateView");
        binding = DataBindingUtil.inflate(inflater, R.layout.account_activity_transactions_fragment, container, false);
        binding.accountRecycleView.setLayoutManager(new LinearLayoutManager(getAccountActivity()));

        adapter = new TransactionRecyclerViewAdapter(this, getAccountActivity());
        binding.accountRecycleView.setAdapter(adapter);

        return binding.getRoot();
    }

    void updateFilter(FilteringOptions filteringOptions) {
        AccountActivity accountActivity = getAccountActivity();
        if (accountActivity != null && accountActivity.getAccountDetail() != null) {
            List<Transaction> transactions = accountActivity.getAccountDetail().getTransactions();
            if (filteringOptions != null) {
                if (adapter != null) {
                    switch (filteringOptions.getFilterType()) {
                        case CreditCardHubActivity.ALL_TRANSACTIONS:
                            adapter.refreshItemList(transactions);
                            setNoTransactionsVisible(transactions);
                            break;
                        case CreditCardHubActivity.MONEY_IN:
                            adapter.refreshItemList(moneyInFilter(transactions));
                            break;
                        case CreditCardHubActivity.MONEY_OUT:
                            adapter.refreshItemList(moneyOutFilter(transactions));
                            break;
                        case CreditCardHubActivity.UNCLEARED:
                            adapter.refreshItemList(unclearedTransactionsFilter(transactions));
                            break;
                    }
                }
            }
        }
    }

    private List<Transaction> moneyInFilter(List<Transaction> transactions) {
        List<Transaction> listTransactions = new ArrayList<>();

        for (Transaction item : transactions) {
            if (item.getCreditAmount() != null && !"0.00".equals(item.getCreditAmount().getAmount()) && !item.isUnclearedTransaction()) {
                listTransactions.add(item);
            }
        }
        setNoTransactionsVisible(listTransactions);
        return listTransactions;
    }

    private List<Transaction> moneyOutFilter(List<Transaction> transactions) {
        List<Transaction> listTransactions = new ArrayList<>();

        for (Transaction item : transactions) {
            if (item.getDebitAmount() != null && !"0.00".equals(item.getDebitAmount().getAmount()) && !item.isUnclearedTransaction()) {
                listTransactions.add(item);
            }
        }
        setNoTransactionsVisible(listTransactions);
        return listTransactions;
    }

    private List<Transaction> unclearedTransactionsFilter(List<Transaction> transactions) {
        List<Transaction> listTransactions = new ArrayList<>();

        for (Transaction item : transactions) {
            if (item.isUnclearedTransaction()) {
                listTransactions.add(item);
            }
        }
        setNoTransactionsVisible(listTransactions);
        return listTransactions;
    }

    private void setNoTransactionsVisible(List<Transaction> listTransactions) {
        if (listTransactions.isEmpty()) {
            binding.noTransactionsTextView.setVisibility(View.VISIBLE);
        } else {
            binding.noTransactionsTextView.setVisibility(View.GONE);
        }
    }

    private AccountActivity getAccountActivity() {
        return (AccountActivity) getActivity();
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
    public void setNoTransactions(boolean hasNoTransactions) {
        binding.noTransactionsTextView.setVisibility(hasNoTransactions ? View.VISIBLE : View.GONE);
    }

    @Override
    public void trackScreenView(String channel, String screenName) {
        AccountActivity accountActivity = getAccountActivity();
        if (accountActivity != null) {
            accountActivity.trackScreenView(channel, screenName);
        }
    }
}