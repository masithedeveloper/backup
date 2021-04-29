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

package com.barclays.absa.banking.presentation.homeLoan;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccountDetail;
import com.barclays.absa.banking.boundary.model.Transaction;
import com.barclays.absa.banking.databinding.HomeLoanAccountHistoryFragmentBinding;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.home.ui.IHomeCacheService;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.shared.ItemPagerFragment;
import com.barclays.absa.banking.shared.genericTransactionHistory.ui.DatePickerFragment;
import com.barclays.absa.utils.DateUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeLoanAccountHistoryFragment extends ItemPagerFragment implements HomeLoanPerilsAccountHistoryView {

    private IHomeCacheService homeCacheService = DaggerHelperKt.getServiceInterface(IHomeCacheService.class);
    private HomeLoanAccountHistoryFragmentBinding binding;
    private HomeLoanAccountHistoryAdapter homeLoanAccountHistoryAdapter;
    private HomeLoanPerilsAccountHistoryPresenter homeLoanPerilsAccountHistoryPresenter;
    private String fromDate;
    private String toDate;

    public HomeLoanAccountHistoryFragment() {
    }

    public static HomeLoanAccountHistoryFragment newInstance(String description, AccountDetail accountDetail) {
        HomeLoanAccountHistoryFragment homeLoanAccountHistoryFragment = new HomeLoanAccountHistoryFragment();
        Bundle arguments = new Bundle();
        arguments.putString(Companion.getTAB_DESCRIPTION_KEY(), description);
        arguments.putSerializable(HomeLoanPerilsHubActivity.ACCOUNT_DETAIL, accountDetail);
        homeLoanAccountHistoryFragment.setArguments(arguments);
        return homeLoanAccountHistoryFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_loan_account_history_fragment, container, false);
        binding.transactionsFilterAndSearch.hideSearchView();
        AccountDetail accountDetail;

        if (getArguments() != null) {
            accountDetail = (AccountDetail) getArguments().getSerializable(HomeLoanPerilsHubActivity.ACCOUNT_DETAIL);
            if (accountDetail != null) {
                displayTransactionHistory(accountDetail);
                Date today = new Date();
                Calendar todayCalendar = Calendar.getInstance();
                todayCalendar.add(Calendar.DAY_OF_YEAR, -30);
                Date fromDay = todayCalendar.getTime();
                fromDate = !TextUtils.isEmpty(accountDetail.getFromDate()) ? accountDetail.getFromDate() : DateUtils.hyphenateDate(fromDay);
                toDate = !TextUtils.isEmpty(accountDetail.getToDate()) ? accountDetail.getToDate() : DateUtils.hyphenateDate(today);
                String searchText = DateUtils.getDateWithMonthNameFromHyphenatedString(fromDate) + " - " + DateUtils.getDateWithMonthNameFromHyphenatedString(toDate);
                binding.transactionsFilterAndSearch.setSearchText(searchText);
            }
        } else {
            startActivity(IntentFactory.getFailureResultScreen(getActivity(), R.string.claim_error_text, R.string.try_later_text));
        }
        homeLoanPerilsAccountHistoryPresenter = new HomeLoanPerilsAccountHistoryPresenter(this);
        binding.transactionsFilterAndSearch.setEditable(false);
        binding.transactionsFilterAndSearch.setOnCalendarLayoutClickListener(v -> filterTransactionsUsingDateRange());
        binding.transactionsFilterAndSearch.setOnSearchClickListener(v -> filterTransactionsUsingDateRange());
        return binding.getRoot();
    }

    private void displayTransactionHistory(AccountDetail accountDetail) {
        displayTransactions(accountDetail.getTransactions());
    }

    private void displayTransactions(List<Transaction> transactionList) {
        if (transactionList != null && !transactionList.isEmpty()) {
            binding.noResultsToDisplayHeadingView.setVisibility(View.GONE);
            binding.transactionHistoryRecyclerView.setVisibility(View.VISIBLE);
            homeLoanAccountHistoryAdapter = new HomeLoanAccountHistoryAdapter(transactionList);
            binding.transactionHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.transactionHistoryRecyclerView.setAdapter(homeLoanAccountHistoryAdapter);
        } else {
            binding.noResultsToDisplayHeadingView.setVisibility(View.VISIBLE);
            binding.transactionHistoryRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showFailureScreen() {
        startActivity(IntentFactory.getFailureResultScreen(getActivity(), R.string.claim_error_text, R.string.try_later_text));
    }

    @Override
    public void displayFetchedTransactionHistory(AccountDetail successResponse) {
        displayTransactions(successResponse.getTransactions());
    }

    @Override
    public void dismissProgressDialog() {
        if (getActivity() != null) {
            ((HomeLoanPerilsHubActivity) getActivity()).dismissProgressDialog();
        }
    }

    private void filterTransactionsUsingDateRange() {
        DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(fromDate, toDate);
        datePickerFragment.setOnDateRangeSelectionListener((startDate, endDate) -> {
            String date = DateUtils.format(startDate, DateUtils.DATE_DISPLAY_PATTERN) + " - " + DateUtils.format(endDate, DateUtils.DATE_DISPLAY_PATTERN);
            binding.transactionsFilterAndSearch.setSearchText(date);
            String startDateParam = DateUtils.format(startDate, DateUtils.DASHED_DATE_PATTERN);
            String endDateParam = DateUtils.format(endDate, DateUtils.DASHED_DATE_PATTERN);
            final AccountDetail accountDetail = homeCacheService.getHomeLoanAccountHistoryCleared();
            homeLoanPerilsAccountHistoryPresenter.fetchHomeLoanTransactionHistory(startDateParam, endDateParam, accountDetail.getAccountObject());
            fromDate = startDateParam;
            toDate = endDateParam;
        });
        datePickerFragment.show(getChildFragmentManager(), "datePickerFragment");
    }

    @NotNull
    @Override
    protected String getTabDescription() {
        return getArguments().getString(Companion.getTAB_DESCRIPTION_KEY());
    }
}

