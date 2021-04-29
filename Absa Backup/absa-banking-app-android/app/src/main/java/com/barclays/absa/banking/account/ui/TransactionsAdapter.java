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

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.boundary.model.Transaction;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import styleguide.cards.TransactionView;

import static com.barclays.absa.banking.framework.app.BMBConstants.ACCOUNT_SUMMERY_CONST;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolderBase> implements Filterable {

    private final TransactionsView transactionsView;
    protected ArrayList<Transaction> transactions = new ArrayList<>(), originalList = new ArrayList<>();
    protected String searchText;
    private final CustomFilter customFilter;
    private int searchInvokationCount = 0;

    public TransactionsAdapter(TransactionsView transactionsView, List<Transaction> transactionList) {
        this.transactionsView = transactionsView;
        if (transactionList != null) {
            originalList.addAll(transactionList);
            transactions.addAll(transactionList);
        }
        customFilter = new CustomFilter();
    }

    @NonNull
    @Override
    public ViewHolderBase onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_activity_transaction_item, parent, false);
        return new ViewHolderItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderBase viewHolderBase, int position) {
        if (viewHolderBase instanceof ViewHolderItem) {
            ViewHolderItem holder = (ViewHolderItem) viewHolderBase;
            if (position < transactions.size()) {
                Transaction transaction = transactions.get(position);
                holder.setDate(transaction, position);
                holder.updateView(transaction);
            }
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    protected static class ViewHolderBase extends RecyclerView.ViewHolder {
        protected ViewHolderBase(View itemView) {
            super(itemView);
        }
    }

    protected class ViewHolderItem extends ViewHolderBase {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", CommonUtils.getCurrentApplicationLocale());
        TextView dateHeaderTextView;
        TransactionView transactionView;

        ViewHolderItem(View view) {
            super(view);

            transactionView = view.findViewById(R.id.cardTransactionView);
            dateHeaderTextView = view.findViewById(R.id.dateTextView);
        }

        void setDate(@NonNull Transaction transaction, int position) {
            String date = "";
            if (transaction.getTransactionDate() != null) {
                Calendar yesterdayDate = Calendar.getInstance();
                yesterdayDate.add(Calendar.DATE, -1);

                date = DateUtils.getDateWithMonthNameFromHyphenatedString(transaction.getTransactionDate());

                if (transaction.getTransactionDate().equals(formatter.format(Calendar.getInstance().getTime()))) {
                    date = transactionsView.getString(R.string.credit_card_hub_today);
                } else if (transaction.getTransactionDate().equals(formatter.format(yesterdayDate.getTime()))) {
                    date = transactionsView.getString(R.string.credit_card_hub_yesterday);
                }
            }

            if (position > 0 && transactions.get(position - 1).getTransactionDate().equals(transaction.getTransactionDate())) {
                this.dateHeaderTextView.setVisibility(View.GONE);
            } else {
                this.dateHeaderTextView.setVisibility(View.VISIBLE);
                this.dateHeaderTextView.setText(date);
            }
        }

        void updateView(@NonNull Transaction transaction) {
            this.transactionView.setVisibility(View.VISIBLE);

            String description = transaction.getDescription();
            if (!TextUtils.isEmpty(searchText)) {
                int startPosition = description.toLowerCase().indexOf(searchText);
                int endPosition = startPosition + searchText.length();
                if (startPosition >= 0) {
                    Spannable spannable = new SpannableString(description);
                    ColorStateList highlightColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{ContextCompat.getColor(transactionView.getContext(), R.color.filter_color)});
                    TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, highlightColor, null);
                    spannable.setSpan(highlightSpan, startPosition, endPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    transactionView.setDescriptionText(spannable);
                } else {
                    transactionView.setDescriptionText(description);
                }
            } else if (transaction.getDescription() != null) {
                transactionView.setDescriptionText(transaction.getDescription());
            }

            if (transaction.getCreditAmount() != null && transaction.getCreditAmount().getAmount() != null && !transaction.getCreditAmount().getAmount().equals("0.00")) {
                transactionView.setAmountText(transaction.getCreditAmount().toString());
                if (transaction.isUnclearedTransaction()) {
                    transactionView.setUnclearedLabelText(transactionsView.getString(R.string.uncleared));
                } else {
                    transactionView.setUnclearedLabelText(null);

                    if (transaction.getCreditAmount().getAmountDouble() > 0) {
                        transactionView.setIncomingColor();
                    }
                }
            } else if (transaction.getDebitAmount() != null) {
                Amount debitAmount = transaction.getDebitAmount();
                if (transaction.isUnclearedTransaction()) {
                    transactionView.setAmountText(debitAmount.toString().replace("-", ""));
                    transactionView.setUnclearedLabelText(transactionsView.getString(R.string.uncleared));
                } else {
                    transactionView.setUnclearedLabelText(null);
                    transactionView.setAmountText(debitAmount.toString());
                }
            }

            transactionView.setDateText(DateUtils.getDateWithMonthNameFromHyphenatedString(transaction.getTransactionDate()));
        }
    }

    public void search(String text) {
        getFilter().filter(text);
        if (searchInvokationCount++ < 1) {
            transactionsView.trackScreenView(ACCOUNT_SUMMERY_CONST, "Field search screen");
        }
    }

    @Override
    public Filter getFilter() {
        return customFilter;
    }

    class CustomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            final FilterResults results = new FilterResults();
            searchText = charSequence.toString().toLowerCase().trim();
            List<Transaction> filteredList = new ArrayList<>();
            if (searchText.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                for (Transaction transactionItem : originalList) {
                    if (!(transactionItem instanceof TransactionLabel)) {
                        String smallCaseFilter = transactionItem.getDescription().toLowerCase();
                        if (smallCaseFilter.contains(searchText)) {
                            filteredList.add(transactionItem);
                        }
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            transactions.clear();
            transactions.addAll((List<Transaction>) filterResults.values);
            transactionsView.setNoTransactions(transactions.isEmpty());
            notifyDataSetChanged();
        }
    }
}