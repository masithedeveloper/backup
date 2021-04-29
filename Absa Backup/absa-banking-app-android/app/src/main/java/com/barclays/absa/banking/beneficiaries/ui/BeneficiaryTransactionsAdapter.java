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
package com.barclays.absa.banking.beneficiaries.ui;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.TransactionObject;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import styleguide.cards.Transaction;
import styleguide.cards.TransactionView;

public class BeneficiaryTransactionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private final Context context;
    private final List<TransactionObject> transactionObjectList;

    BeneficiaryTransactionsAdapter(Context context, List<TransactionObject> transactionObjectList) {
        this.context = context;
        this.transactionObjectList = transactionObjectList;
    }

    @Override
    public int getItemCount() {
        return transactionObjectList == null ? 0 : transactionObjectList.size();
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new BeneficiaryTransactionsAdapter.BeneficiaryTransactionListItemViewHolder(layoutInflater.inflate(R.layout.beneficiary_transaction_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, final int position) {

        BeneficiaryTransactionsAdapter.BeneficiaryTransactionListItemViewHolder historyHolder = (BeneficiaryTransactionsAdapter.BeneficiaryTransactionListItemViewHolder) holder;
        Transaction transaction = new Transaction();
        final TransactionObject transactionObject = transactionObjectList.get(position);
        transaction.setAmount(Objects.requireNonNull(transactionObject.getAmount().toString()));
        transaction.setDate(Objects.requireNonNull(transactionObject.getDate()));

        historyHolder.transactionView.setTransaction(transaction);
        historyHolder.transactionView.setOnClickListener(view -> ((BeneficiaryDetailsActivity) context).viewTransactionDetails(transactionObject));
    }

    @Override
    public void onClick(View view) {

    }

    private static class BeneficiaryTransactionListItemViewHolder extends RecyclerView.ViewHolder {

        private final TransactionView transactionView;

        private BeneficiaryTransactionListItemViewHolder(View listItemView) {
            super(listItemView);
            transactionView = listItemView.findViewById(R.id.beneficiary_transaction_view);
        }

    }
}
