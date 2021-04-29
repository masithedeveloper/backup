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

package com.barclays.absa.banking.presentation.shared;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
import com.barclays.absa.banking.databinding.LineItemRecentPurchaseBinding;

import java.util.ArrayList;
import java.util.List;

import styleguide.content.BeneficiaryListItem;

public class GenericRecentBeneficiaryAdapter extends RecyclerView.Adapter<GenericRecentBeneficiaryAdapter.ViewHolder> {

    private final BeneficiarySelectionInterface beneficiarySelectionInterface;
    private List<BeneficiaryObject> beneficiaryObjectList;
    private List<BeneficiaryObject> originalBeneficiaryObjectList;
    private String filterText;

    public GenericRecentBeneficiaryAdapter(List<BeneficiaryObject> beneficiaryList, BeneficiarySelectionInterface beneficiarySelectionInterface) {
        this.beneficiaryObjectList = beneficiaryList;
        this.originalBeneficiaryObjectList = beneficiaryList;
        this.beneficiarySelectionInterface = beneficiarySelectionInterface;
    }

    @NonNull
    @Override
    public GenericRecentBeneficiaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LineItemRecentPurchaseBinding lineItemSelectBeneficiaryBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.line_item_recent_purchase, parent, false);
        return new GenericRecentBeneficiaryAdapter.ViewHolder(lineItemSelectBeneficiaryBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull GenericRecentBeneficiaryAdapter.ViewHolder holder, int position) {
        final BeneficiaryObject beneficiaryObject = beneficiaryObjectList.get(position);

        Amount lastTransactionAmount = beneficiaryObject.getLastTransactionAmount();
        String lastPaymentDetail = null;
        if (lastTransactionAmount != null) {
            final Context context = holder.itemView.getContext();
            lastPaymentDetail = context.getString(R.string.last_transaction_beneficiary, lastTransactionAmount.toString(), context.getString(R.string.purchased), beneficiaryObject.getLastTransactionDate());
        }
        holder.lineItemRecentPurchaseBinding.recentPurchaseBeneficiaryView.setBeneficiary(new BeneficiaryListItem(beneficiaryObject.getBeneficiaryName(), beneficiaryObject.getBeneficiaryAccountNumber(), lastPaymentDetail));

        applyFilter(holder.lineItemRecentPurchaseBinding, beneficiaryObject);

        holder.lineItemRecentPurchaseBinding.recentPurchaseBeneficiaryView.setOnClickListener(v -> {
            if (beneficiarySelectionInterface != null) {
                beneficiarySelectionInterface.onBeneficiarySelected(beneficiaryObject);
            }
        });
    }

    private void applyFilter(LineItemRecentPurchaseBinding viewHolder, BeneficiaryObject beneficiaryObject) {
        if (!TextUtils.isEmpty(filterText)) {
            final String beneficiaryName = beneficiaryObject.getBeneficiaryName();
            Spannable searchBeneficiary = new SpannableString(beneficiaryName);
            final String beneficiaryAccountNumber = beneficiaryObject.getBeneficiaryAccountNumber();
            Spannable searchAccount = new SpannableString(beneficiaryAccountNumber);

            int startBeneficiaryPosition = beneficiaryName != null ? beneficiaryName.toLowerCase().indexOf(filterText.toLowerCase()) : -1;
            int startAccountPosition = beneficiaryAccountNumber != null ? beneficiaryAccountNumber.toLowerCase().indexOf(filterText.toLowerCase()) : -1;

            int endBeneficiaryPosition = startBeneficiaryPosition + filterText.length();
            int endAccountPosition = startAccountPosition + filterText.length();

            if (endBeneficiaryPosition <= searchBeneficiary.length() && startBeneficiaryPosition != -1) {
                searchBeneficiary.setSpan(new ForegroundColorSpan(Color.RED), startBeneficiaryPosition, endBeneficiaryPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder.recentPurchaseBeneficiaryView.getNameTextView().setText(searchBeneficiary);
            }

            if (endAccountPosition <= searchAccount.length() && startAccountPosition != -1) {
                searchAccount.setSpan(new ForegroundColorSpan(Color.RED), startAccountPosition, endAccountPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder.recentPurchaseBeneficiaryView.getAccountNumberTextView().setText(searchAccount);
            }
        }
    }

    @Override
    public int getItemCount() {
        return beneficiaryObjectList != null ? beneficiaryObjectList.size() : 0;
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void filter(String filter) {
        this.filterText = filter;

        beneficiaryObjectList = new ArrayList<>();
        if (TextUtils.isEmpty(filter)) {
            beneficiaryObjectList.addAll(originalBeneficiaryObjectList);
        } else {
            for (BeneficiaryObject beneficiary : originalBeneficiaryObjectList) {
                final String beneficiaryName = beneficiary.getBeneficiaryName();
                final String beneficiaryAccountNumber = beneficiary.getBeneficiaryAccountNumber();
                if (beneficiaryName != null && beneficiaryName.toLowerCase().contains(filter.toLowerCase()) || beneficiaryAccountNumber != null && beneficiaryAccountNumber.toLowerCase().contains(filter.toLowerCase())) {
                    beneficiaryObjectList.add(beneficiary);
                }
            }
        }

        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LineItemRecentPurchaseBinding lineItemRecentPurchaseBinding;

        public ViewHolder(LineItemRecentPurchaseBinding lineItemRecentPurchaseBinding) {
            super(lineItemRecentPurchaseBinding.getRoot());
            this.lineItemRecentPurchaseBinding = lineItemRecentPurchaseBinding;
        }
    }
}
