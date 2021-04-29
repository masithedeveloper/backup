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

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.Transaction;
import com.barclays.absa.banking.databinding.HomeLoanPerilsTransactionHistoryItemBinding;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.utils.DateUtils;
import com.barclays.absa.utils.TextFormatUtils;
import styleguide.content.SecondaryContentAndLabelView;
import styleguide.utils.extensions.StringExtensions;

import java.util.List;


public class HomeLoanAccountHistoryAdapter extends RecyclerView.Adapter<HomeLoanAccountHistoryAdapter.HomeLoanAccountViewHolder> {

    private List<Transaction> transactionList;

    public HomeLoanAccountHistoryAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public HomeLoanAccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        HomeLoanPerilsTransactionHistoryItemBinding perilsTransactionHistoryItemBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.home_loan_perils_transaction_history_item, parent, false);
        return new HomeLoanAccountViewHolder(perilsTransactionHistoryItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeLoanAccountViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        if (transaction.isTransactionCreditType()) {
            holder.secondaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmount(transaction.getCreditAmount()));
            holder.secondaryContentAndLabelView.setLabelText(String.format(BMBApplication.getApplicationLocale(), "%s %s", StringExtensions.toTitleCase(transaction.getDescription()), DateUtils.getDateWithMonthNameFromHyphenatedString(transaction.getTransactionDate())));
        } else {
            holder.secondaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmount(transaction.getDebitAmount()));
            holder.secondaryContentAndLabelView.setLabelText(String.format(BMBApplication.getApplicationLocale(), "%s %s", StringExtensions.toTitleCase(transaction.getDescription()), DateUtils.getDateWithMonthNameFromHyphenatedString(transaction.getTransactionDate())));
        }
        if ((position + 1) % 3 == 0) {
            holder.dividerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return transactionList == null ? 0 : transactionList.size();
    }

    static class HomeLoanAccountViewHolder extends RecyclerView.ViewHolder {
        SecondaryContentAndLabelView secondaryContentAndLabelView;
        View dividerView;

        HomeLoanAccountViewHolder(HomeLoanPerilsTransactionHistoryItemBinding perilsTransactionHistoryItemBinding) {
            super(perilsTransactionHistoryItemBinding.getRoot());
            secondaryContentAndLabelView = perilsTransactionHistoryItemBinding.homeLoanTransactionHistoryLabelView;
            dividerView = perilsTransactionHistoryItemBinding.dividerView;
        }
    }
}
