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

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.beneficiaries.ui.BeneficiaryUtils;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
import com.barclays.absa.banking.databinding.BeneficiaryListItemBinding;
import com.barclays.absa.banking.databinding.BeneficiaryListSectionHeaderItemBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import styleguide.content.BeneficiaryListItem;

public class GenericGroupingBeneficiaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_HEADER = 0;
    private final int VIEW_TYPE_ITEM = 1;
    private final BeneficiarySelectionInterface beneficiarySelectionInterface;

    private int sectionNormalizedPosition = 0; //or sectionAwarePosition
    private ArrayList<Integer> sectionHeaderPositions = new ArrayList<>();
    private ArrayList<BeneficiaryObject> allItemsList = new ArrayList<>();
    private ArrayList<BeneficiaryObject> originalAllItemsList = new ArrayList<>();
    private LinkedHashMap<String, ArrayList<BeneficiaryObject>> sectionedList;

    private String filterText;

    public GenericGroupingBeneficiaryAdapter(List<BeneficiaryObject> beneficiaries, BeneficiarySelectionInterface beneficiarySelectionInterface) {
        initializeListData(beneficiaries);
        this.beneficiarySelectionInterface = beneficiarySelectionInterface;
    }

    public GenericGroupingBeneficiaryAdapter(List<BeneficiaryObject> beneficiaryListFiltered, String filter, BeneficiarySelectionInterface beneficiarySelectionInterface) {
        initializeListData(beneficiaryListFiltered);
        this.filterText = filter;
        this.beneficiarySelectionInterface = beneficiarySelectionInterface;
    }

    private void initializeListData(List<BeneficiaryObject> beneficiaries) {
        sectionedList = sortAndGroup(beneficiaries);
        originalAllItemsList = new ArrayList<>();
        originalAllItemsList.addAll(beneficiaries);
    }

    private LinkedHashMap<String, ArrayList<BeneficiaryObject>> sortAndGroup(List<BeneficiaryObject> beneficiaries) {
        ArrayList<BeneficiaryObject> sortedList = sort(beneficiaries);
        return group(sortedList);
    }

    private LinkedHashMap<String, ArrayList<BeneficiaryObject>> group(ArrayList<BeneficiaryObject> beneficiaries) {
        String firstLetterOfCurrentBeneficiary;
        LinkedHashMap<String, ArrayList<BeneficiaryObject>> groupedBeneficiaries = new LinkedHashMap<>();
        for (BeneficiaryObject beneficiary : beneficiaries) {
            String beneficiaryName = beneficiary.getBeneficiaryName();
            firstLetterOfCurrentBeneficiary = (beneficiaryName != null && !beneficiaryName.isEmpty()) ? beneficiaryName.toUpperCase().substring(0, 1) : "";
            String currentSectionName = firstLetterOfCurrentBeneficiary;
            ArrayList<BeneficiaryObject> beneficiariesForCurrentSection = groupedBeneficiaries.get(currentSectionName);
            if (beneficiariesForCurrentSection != null) {
                beneficiariesForCurrentSection.add(beneficiary);
                allItemsList.add(beneficiary);
                sectionNormalizedPosition++;
            } else {
                sectionHeaderPositions.add(sectionNormalizedPosition);
                //this means we are in a new section
                beneficiariesForCurrentSection = new ArrayList<>();
                BeneficiaryObject sectionBeneficiary = new BeneficiaryObject();
                sectionBeneficiary.setBeneficiaryName(currentSectionName);
                beneficiariesForCurrentSection.add(sectionBeneficiary);
                allItemsList.add(sectionBeneficiary);
                ++sectionNormalizedPosition;

                beneficiariesForCurrentSection.add(beneficiary);
                allItemsList.add(beneficiary);
                groupedBeneficiaries.put(currentSectionName, beneficiariesForCurrentSection);
                sectionNormalizedPosition++;
            }
        }
        return groupedBeneficiaries;
    }

    private ArrayList<BeneficiaryObject> sort(List<BeneficiaryObject> beneficiaries) {
        return (ArrayList<BeneficiaryObject>) BeneficiaryUtils.sortBeneficiariesByFirstName(beneficiaries);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            BeneficiaryListSectionHeaderItemBinding beneficiaryListSectionHeaderItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.beneficiary_list_section_header_item, parent, false);
            return new BeneficiarySectionItemViewHolder(beneficiaryListSectionHeaderItemBinding);
        } else {
            BeneficiaryListItemBinding beneficiaryListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.beneficiary_list_item, parent, false);
            return new BeneficiaryListItemViewHolder(beneficiaryListItemBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final BeneficiaryObject beneficiary = allItemsList.get(position);

        if (holder instanceof BeneficiarySectionItemViewHolder) {
            TextView sectionNameTextView = ((BeneficiarySectionItemViewHolder) holder).beneficiaryListSectionHeaderItemBinding.sectionNameTextview;
            sectionNameTextView.setText(beneficiary.getBeneficiaryName());
        } else if (holder instanceof BeneficiaryListItemViewHolder) {
            TypedValue outValue = new TypedValue();
            holder.itemView.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            holder.itemView.setBackgroundResource(outValue.resourceId);

            Amount lastTransactionAmount = beneficiary.getLastTransactionAmount();
            String lastPaymentDetail = null;
            String placeholderText = "";
            if (lastTransactionAmount != null) {
                lastPaymentDetail = holder.itemView.getContext().getString(R.string.last_transaction_beneficiary, lastTransactionAmount.toString(), placeholderText, beneficiary.getLastTransactionDate());
            }
            BeneficiaryListItem displayItem = new BeneficiaryListItem(beneficiary.getBeneficiaryName(), beneficiary.getBeneficiaryAccountNumber(), lastPaymentDetail);
            BeneficiaryListItemViewHolder beneficiaryListItemViewHolder = (BeneficiaryListItemViewHolder) holder;
            beneficiaryListItemViewHolder.beneficiaryListItemBinding.beneficiaryView.setBeneficiary(displayItem);

            applyFilter(beneficiaryListItemViewHolder.beneficiaryListItemBinding, beneficiary);

            beneficiaryListItemViewHolder.beneficiaryListItemBinding.beneficiaryView.setOnClickListener(v -> {
                if (beneficiarySelectionInterface != null) {
                    beneficiarySelectionInterface.onBeneficiarySelected(beneficiary);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (sectionedList != null) {
            for (ArrayList<BeneficiaryObject> section : sectionedList.values()) {
                count += section != null ? section.size() : 0;
            }
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeaderPositions.contains(position) ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    private void applyFilter(BeneficiaryListItemBinding viewHolder, BeneficiaryObject beneficiaryObject) {
        if (!TextUtils.isEmpty(filterText)) {
            final String beneficiaryName = beneficiaryObject.getBeneficiaryName();
            Spannable searchBeneficiary = new SpannableString(beneficiaryName);
            final String beneficiaryAccountNumber = beneficiaryObject.getBeneficiaryAccountNumber();
            Spannable searchAccount = new SpannableString(beneficiaryAccountNumber);

            int startBeneficiaryPosition = beneficiaryName.toLowerCase().indexOf(filterText.toLowerCase());
            int startAccountPosition = beneficiaryAccountNumber.toLowerCase().indexOf(filterText.toLowerCase());

            int endBeneficiaryPosition = startBeneficiaryPosition + filterText.length();
            int endAccountPosition = startAccountPosition + filterText.length();

            boolean found = false;

            if (endBeneficiaryPosition <= searchBeneficiary.length() && startBeneficiaryPosition != -1) {
                found = true;
                searchBeneficiary.setSpan(new ForegroundColorSpan(ContextCompat.getColor(viewHolder.beneficiaryView.getContext(), R.color.dark_red)), startBeneficiaryPosition, endBeneficiaryPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder.beneficiaryView.getNameTextView().setText(searchBeneficiary);
            }

            if (endAccountPosition <= searchAccount.length() && startAccountPosition != -1) {
                found = true;
                searchAccount.setSpan(new ForegroundColorSpan(ContextCompat.getColor(viewHolder.beneficiaryView.getContext(), R.color.dark_red)), startAccountPosition, endAccountPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder.beneficiaryView.getAccountNumberTextView().setText(searchAccount);
            }

            viewHolder.beneficiaryView.setVisibility(!found ? View.GONE : View.VISIBLE);
        } else {
            viewHolder.beneficiaryView.setVisibility(View.VISIBLE);
        }
    }

    public void setDataSetAndFilterText(@NotNull List<BeneficiaryObject> beneficiaryList, @NotNull String query) {
        allItemsList.clear();
        sectionHeaderPositions.clear();
        sectionNormalizedPosition = 0;
        originalAllItemsList.clear();
        initializeListData(beneficiaryList);
        this.filterText = query;
    }

    private class BeneficiaryListItemViewHolder extends RecyclerView.ViewHolder {
        private final BeneficiaryListItemBinding beneficiaryListItemBinding;

        BeneficiaryListItemViewHolder(BeneficiaryListItemBinding beneficiaryListItemBinding) {
            super(beneficiaryListItemBinding.getRoot());
            this.beneficiaryListItemBinding = beneficiaryListItemBinding;
        }
    }

    private class BeneficiarySectionItemViewHolder extends RecyclerView.ViewHolder {
        private final BeneficiaryListSectionHeaderItemBinding beneficiaryListSectionHeaderItemBinding;

        BeneficiarySectionItemViewHolder(BeneficiaryListSectionHeaderItemBinding beneficiaryListSectionHeaderItemBinding) {
            super(beneficiaryListSectionHeaderItemBinding.getRoot());
            this.beneficiaryListSectionHeaderItemBinding = beneficiaryListSectionHeaderItemBinding;
        }
    }
}