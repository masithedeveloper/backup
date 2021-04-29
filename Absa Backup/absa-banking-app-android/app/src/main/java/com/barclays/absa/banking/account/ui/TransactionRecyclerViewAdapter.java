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


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccountDetail;
import com.barclays.absa.banking.boundary.model.Transaction;
import com.barclays.absa.banking.card.ui.TransactionHistoryComparison;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

class TransactionRecyclerViewAdapter extends TransactionsAdapter {
    private final SimpleDateFormat dateFormatDateSearch = new SimpleDateFormat("dd MMM yyyy", BMBApplication.getApplicationLocale());
    private final AccountActivity accountActivity;
    private static final int TYPE_HEADER = 0, TYPE_ITEM = 1;
    private final int HEADER_OFFSET = 1;
    private AccountDetail accountDetail;
    private TextView tvDateRange;

    TransactionRecyclerViewAdapter(TransactionsView transactionView, AccountActivity accountActivity) {
        super(transactionView, null);
        this.accountActivity = accountActivity;
    }

    public void setData(@Nullable AccountDetail accountDetail) {
        if (accountDetail != null) {
            this.accountDetail = accountDetail;
            setTransactions(accountDetail.getTransactions());
            notifyDataSetChanged();
        }
    }

    private void setTransactions(List<Transaction> transactions) {
        if (transactions != null) {
            Collections.sort(transactions, new TransactionHistoryComparison());
            this.transactions.clear();
            originalList.clear();
            String dateLabel = "";
            for (Transaction transaction : transactions) {
                if (dateLabel.isEmpty() || !dateLabel.equals(transaction.getTransactionDate())) {
                    originalList.add(new TransactionLabel(transaction.getTransactionDate()));
                    originalList.add(transaction);

                    dateLabel = transaction.getTransactionDate();
                } else {
                    originalList.add(transaction);
                }
            }
            this.transactions.addAll(originalList);
        }
    }

    private void animate(View view, int resid) {
        if (view != null) {
            view.startAnimation(AnimationUtils.loadAnimation(accountActivity, resid));
        }
    }

    @NonNull
    @Override
    public TransactionsAdapter.ViewHolderBase onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_recycleview_header, parent, false);
                return new ViewHolderHeader(view);
            case TYPE_ITEM:
                return super.onCreateViewHolder(parent, viewType);
            default:
                accountActivity.showGenericErrorMessageThenFinish();
                View viewDefault = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_recycleview_header, parent, false);
                return new ViewHolderHeader(viewDefault);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderBase holderBase, int position) {
        if (holderBase instanceof ViewHolderItem) {
            if (position - HEADER_OFFSET < transactions.size()) {
                super.onBindViewHolder(holderBase, position - HEADER_OFFSET);
            }
        } else if (holderBase instanceof ViewHolderHeader) {
            ViewHolderHeader holder = ((ViewHolderHeader) holderBase);
            holder.setSelectedDateRange();
        }
    }

    @Override
    public int getItemCount() {
        return HEADER_OFFSET + transactions.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }

    class ViewHolderHeader extends ViewHolderBase implements View.OnClickListener {
        View ivCalendar, ivSearch;

        private void setSelectedDateRange() {
            if (accountDetail != null)
                try {
                    final SimpleDateFormat dateFormatWithHyphen = new SimpleDateFormat("yyyy-MM-dd", CommonUtils.getCurrentApplicationLocale());
                    String fromDateSelected = DateUtils.getFormattedDate(accountDetail.getFromDate(), dateFormatWithHyphen, dateFormatWithHyphen);
                    String toDateSelected = DateUtils.getFormattedDate(accountDetail.getToDate(), dateFormatWithHyphen, dateFormatWithHyphen);
                    Date from = dateFormatWithHyphen.parse(fromDateSelected);
                    Date to = dateFormatWithHyphen.parse(toDateSelected);
                    if (tvDateRange != null) {
                        String dateInfo = dateFormatDateSearch.format(from) + " - " + dateFormatDateSearch.format(to);
                        tvDateRange.setText(dateInfo);
                    }
                } catch (ParseException e) {
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace();
                    }
                }
        }

        ViewHolderHeader(View view) {
            super(view);
            tvDateRange = view.findViewById(R.id.et_account_date_range);
            ivCalendar = view.findViewById(R.id.iv_calender);
            ivSearch = view.findViewById(R.id.iv_search);
            ivSearch.setOnClickListener(this);
            tvDateRange.setOnClickListener(this);
            ivCalendar.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.requestAccessTextView:
                    animate(view, R.anim.click);
                    BaseAlertDialog.INSTANCE.showRequestAccessAlertDialog(accountActivity.getString(R.string.account_balance));
                    break;
                case R.id.iv_calender:
                case R.id.et_account_date_range:
                    animate(ivCalendar, R.anim.click);
                    animate(tvDateRange, R.anim.click);
                    accountActivity.showBottomBarMenu();
                    break;
                case R.id.iv_search:
                    animate(view, R.anim.click);
                    accountActivity.startActivity(IntentFactory.getAccountSearch(accountActivity, transactions));
                    break;
            }
        }
    }

    void refreshItemList(List<Transaction> transactions) {
        this.transactions.clear();
        this.transactions.addAll(transactions);
        notifyDataSetChanged();
    }
}