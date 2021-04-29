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

package com.barclays.absa.banking.cashSend.ui;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.boundary.model.TransactionUnredeem;
import com.barclays.absa.banking.databinding.UnredeemCardviewViewBinding;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.utils.AccessibilityUtils;
import com.barclays.absa.utils.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import styleguide.utils.extensions.StringExtensions;

@Deprecated
public class UnredeemedRecyclerViewAdapter extends RecyclerView.Adapter<UnredeemedRecyclerViewAdapter.UnredeemedViewHolder> implements BMBConstants {

    private ArrayList<TransactionUnredeem> transactionsList;
    private ArrayList<TransactionUnredeem> originalList;
    private Context context;

    private final UnredeemedItemClickListener unredeemedItemClickListener;
    private String filterText;

    public void setTransactionsList(ArrayList<TransactionUnredeem> transactionsList) {
        this.transactionsList = transactionsList;
        this.originalList = this.transactionsList;
    }

    public UnredeemedRecyclerViewAdapter(Context context, List<TransactionUnredeem> objects, UnredeemedItemClickListener listener) {
        this.context = context;
        this.transactionsList = (ArrayList<TransactionUnredeem>) objects;
        this.originalList = this.transactionsList;
        this.unredeemedItemClickListener = listener;
    }

    @NonNull
    @Override
    public UnredeemedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UnredeemCardviewViewBinding binding = UnredeemCardviewViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UnredeemedViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UnredeemedViewHolder holder, int position) {
        final TransactionUnredeem transaction = this.transactionsList.get(position);
        holder.bind(transaction, unredeemedItemClickListener);

        if (transaction != null) {
            if (transaction.getBeneficiaryNickname() != null && !"null".equalsIgnoreCase(transaction.getBeneficiaryNickname())) {
                holder.binding.unredeemedBeneficiaryTextView.setText(transaction.getBeneficiaryNickname());
            } else {
                holder.binding.unredeemedBeneficiaryTextView.setText("");
            }

            final String cellphoneNumber = transaction.getCellphoneNumber();
            if (cellphoneNumber != null && !cellphoneNumber.startsWith("0")) {
                String phoneNumber = StringExtensions.toFormattedCellphoneNumber("0".concat(cellphoneNumber));
                holder.binding.unredeemedBeneficiaryTextView.append(" (" + phoneNumber + ")");
            } else {
                String phoneNumber = StringExtensions.toFormattedCellphoneNumber(cellphoneNumber);
                holder.binding.unredeemedBeneficiaryTextView.append(" (" + phoneNumber + ")");
            }

            if (!TextUtils.isEmpty(filterText)) {
                String textToSearch = holder.binding.unredeemedBeneficiaryTextView.getText().toString();
                Spannable searchText = new SpannableString(textToSearch);

                int startPosition = textToSearch.toLowerCase().indexOf(filterText.toLowerCase());
                int endPosition = startPosition + filterText.length();
                if (endPosition <= searchText.length() && startPosition != -1) {
                    searchText.setSpan(new ForegroundColorSpan(Color.RED), startPosition, endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.binding.unredeemedBeneficiaryTextView.setText(searchText);
                }
            }

            String formattedDate = "";
            String timeLapse = "";
            if ("".equals(transaction.getTransactionDateTime())) {
                formattedDate = DateUtils.formatDate(transaction.getTransactionDateTime(), "MM/dd/yyyy", "dd MMM yyyy");

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", BMBApplication.getApplicationLocale());
                timeLapse = calculateDateTime(formattedDate, simpleDateFormat);
            }

            if (transaction.getTransactionDateTime() != null && position > 0 && transaction.getTransactionDateTime().equals(transactionsList.get(position - 1).getTransactionDateTime())) {
                holder.binding.dateHeaderTextView.setVisibility(View.GONE);
            } else {
                holder.binding.dateHeaderTextView.setText(timeLapse);
                holder.binding.dateHeaderTextView.setVisibility(View.VISIBLE);
            }

            holder.binding.unredeemedDateTextView.setText(formattedDate);
            final Amount amount = transaction.getAmount();
            holder.binding.unredeemedAmountTextView.setText(amount != null ? amount.toString() : "R 0.00");

            if (AccessibilityUtils.isExploreByTouchEnabled()) {
                String[] beneficiaryDetails = holder.binding.unredeemedBeneficiaryTextView.getText().toString().split(Pattern.quote("("));
                String contactName = beneficiaryDetails[0];
                String contactNumber = beneficiaryDetails[1];
                String transactionDate = holder.binding.unredeemedDateTextView.getText().toString();
                String transactionAmount = AccessibilityUtils.getTalkBackRandValueFromString(holder.binding.unredeemedAmountTextView.getText().toString());
                String dateHeader = holder.binding.dateHeaderTextView.getText().toString();
                if (holder.binding.dateHeaderTextView.getVisibility() == View.VISIBLE) {
                    holder.binding.dateHeaderTextView.setContentDescription(BMBApplication.getInstance().getString(R.string.talkback_unredeemed_cashsends_date_view, dateHeader));
                }
                holder.binding.cardView.setContentDescription(BMBApplication.getInstance().getString(R.string.talkback_unredeemed_cashsends_card_info, contactName, contactNumber, transactionDate, transactionAmount));
            }
        }
    }

    private String calculateDateTime(String transactionDate, SimpleDateFormat simpleDateFormat) {
        Date dateTime;
        try {
            dateTime = DateUtils.getDate(transactionDate, simpleDateFormat);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateTime);
            Calendar today = Calendar.getInstance();
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DATE, -1);

            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                return context.getResources().getString(R.string.today);
            } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
                return context.getResources().getString(R.string.yesterday);
            } else {
                return transactionDate;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return transactionsList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void filterText(String filter) {
        this.filterText = filter;
        transactionsList = new ArrayList<>();

        if (TextUtils.isEmpty(filter)) {
            transactionsList.addAll(originalList);
            notifyDataSetChanged();
            ((UnredeemedTransactionActivity) context).hideNoResultsView();
            return;
        }

        for (TransactionUnredeem transactionUnredeem : originalList) {
            String searchString = String.format("%s (%s)", transactionUnredeem.getBeneficiaryNickname(), StringExtensions.toFormattedCellphoneNumber(transactionUnredeem.getCellphoneNumber()));
            if (searchString.toLowerCase().contains(filter.toLowerCase())) {
                transactionsList.add(transactionUnredeem);
            }
        }

        if (transactionsList.size() > 0) {
            notifyDataSetChanged();
            ((UnredeemedTransactionActivity) context).hideNoResultsView();
        } else {
            ((UnredeemedTransactionActivity) context).showNoResultsView();
        }
    }

    static class UnredeemedViewHolder extends RecyclerView.ViewHolder {
        UnredeemCardviewViewBinding binding;

        UnredeemedViewHolder(UnredeemCardviewViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final TransactionUnredeem transaction, final UnredeemedItemClickListener listener) {
            binding.cardView.setOnClickListener(v -> listener.onItemClick(transaction));
        }
    }
}