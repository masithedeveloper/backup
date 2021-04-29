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

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.Transaction;
import com.barclays.absa.utils.CommonUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import styleguide.cards.TransactionView;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionsViewHolder> {
    private final SimpleDateFormat dateFormatWithMonthString = new SimpleDateFormat("dd MMM yyyy", CommonUtils.getCurrentApplicationLocale());
    private List<Transaction> transactions;
    private List<Transaction> allTransactions;
    private TransactionHistoryFragment context;
    private String keyword;

    TransactionsAdapter(TransactionHistoryFragment context, List<Transaction> allTransactions) {
        this.allTransactions = allTransactions;
        this.context = context;
        if (allTransactions != null) {
            transactions = new ArrayList<>();
            transactions.addAll(allTransactions);
        }
    }

    @NonNull
    @Override
    public TransactionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context.getContext()).inflate(R.layout.account_activity_transaction_item, parent, false);
        return new TransactionsAdapter.TransactionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsViewHolder holder, int position) {
        styleguide.cards.Transaction transactionItem;
        Transaction transaction = transactions.get(position);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", CommonUtils.getCurrentApplicationLocale());
        if (transaction != null) {
            transactionItem = new styleguide.cards.Transaction();
            transactionItem.setTransaction(transaction.getDescription());

            if (transaction.getCreditAmount() != null && !"0.00".equals(transaction.getCreditAmount().getAmount())) {
                transactionItem.setAmount(transaction.getCreditAmount().toString());
            } else if (transaction.getDebitAmount() != null) {
                transactionItem.setAmount(transaction.getDebitAmount().toString());
            }

            if (transaction.getTransactionDate() != null) {
                try {
                    Calendar yesterdayDate = Calendar.getInstance();
                    yesterdayDate.add(Calendar.DATE, -1);

                    Date dateFormatted = formatter.parse(transaction.getTransactionDate());
                    String transactionDate = dateFormatWithMonthString.format(dateFormatted);
                    transactionItem.setDate(transactionDate);
                    if (transaction.getTransactionDate().equals(formatter.format(Calendar.getInstance().getTime()))) {
                        holder.dateTextView.setText(R.string.credit_card_hub_today);
                    } else if (transaction.getTransactionDate().equals(formatter.format(yesterdayDate.getTime()))) {
                        holder.dateTextView.setText(R.string.credit_card_hub_yesterday);
                    } else {
                        holder.dateTextView.setText(transactionDate);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                transactionItem.setDate("");
            }

            if (transaction.isUnclearedTransaction() || (transaction.getTransactionCategory() != null && transaction.getTransactionCategory().equals("1"))) {
                holder.transactionView.setUnclearedLabelText(context.getString(R.string.uncleared));
                transactionItem.setUncleared(true);
            } else {
                holder.transactionView.setUnclearedLabelText(null);
                transactionItem.setUncleared(false);

                if (transaction.getCreditAmount().getAmountDouble() > 0) {
                    holder.transactionView.setIncomingColor();
                }
            }

            if (position > 0 && transactions.get(position - 1).getTransactionDate().equals(transaction.getTransactionDate())) {
                holder.dateTextView.setVisibility(View.GONE);
            } else {
                holder.dateTextView.setVisibility(View.VISIBLE);
            }

            if (TextUtils.isEmpty(keyword)) {
                holder.transactionView.setTransaction(transactionItem);
            } else {
                holder.transactionView.highlight(transactionItem, keyword);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    void refreshItemList(List<Transaction> transactions) {
        this.transactions.clear();
        this.transactions.addAll(transactions);
        context.setNoTransactionsVisible(transactions);
        notifyDataSetChanged();
    }

    public void filter(String keyword) {
        this.keyword = keyword;
        List<Transaction> filteredTransactionItems = new ArrayList<>();
        if (keyword.isEmpty()) {
            refreshItemList(allTransactions);
        } else {
            for (Transaction transactionItem : allTransactions) {
                String description = transactionItem.getDescription();
                if (!TextUtils.isEmpty(description) && description.toLowerCase().contains(keyword)) {
                    filteredTransactionItems.add(transactionItem);
                }
            }
            refreshItemList(filteredTransactionItems);
        }
    }

    @Override
    public int getItemCount() {
        return transactions == null ? 0 : transactions.size();
    }

    class TransactionsViewHolder extends RecyclerView.ViewHolder {
        TransactionView transactionView;
        TextView dateTextView;

        TransactionsViewHolder(View itemView) {
            super(itemView);
            transactionView = itemView.findViewById(R.id.cardTransactionView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}