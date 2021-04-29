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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
import com.barclays.absa.banking.boundary.model.Entry;
import com.barclays.absa.banking.boundary.model.HeaderBeneficiary;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.utils.AccessibilityUtils;

import java.util.ArrayList;
import java.util.List;

import styleguide.content.BeneficiaryListItem;
import styleguide.content.BeneficiaryView;

import static com.barclays.absa.banking.framework.app.BMBConstants.PASS_AIRTIME;
import static com.barclays.absa.banking.framework.app.BMBConstants.PASS_CASHSEND;
import static com.barclays.absa.banking.framework.app.BMBConstants.PASS_PAYMENT;
import static com.barclays.absa.banking.framework.app.BMBConstants.PASS_PREPAID;
import static com.barclays.absa.banking.framework.app.BMBConstants.PASS_PREPAID_ELECTRICITY;

public class BeneficiarySectionedRecyclerAdapter extends RecyclerView.Adapter {
    private List<BeneficiaryObject> sectionedBeneficiaries;
    private List<BeneficiaryObject> originalBeneficiaries;
    private final Context context;
    private boolean shouldHideRecents;
    private String beneficiaryType;
    private String filterText;
    private BeneficiaryCallback callback;

    BeneficiarySectionedRecyclerAdapter(Context context,BeneficiaryCallback callback,  List<BeneficiaryObject> originalBeneficiaries, boolean shouldHideRecents) {
        this.context = context;
        this.shouldHideRecents = shouldHideRecents;
        this.originalBeneficiaries = originalBeneficiaries;
        this.sectionedBeneficiaries = new ArrayList<>();
        this.callback = callback;
        computeSections();
    }

    public void update(List<BeneficiaryObject> beneficiaries, String filterText) {
        this.filterText = filterText;
        originalBeneficiaries = beneficiaries;
        sectionedBeneficiaries.clear();
        computeSections();
        notifyDataSetChanged();
    }

    public void setBeneficiaryType(String beneficiaryType) {
        this.beneficiaryType = beneficiaryType;
    }

    void setShouldHideRecents(boolean shouldHideRecents) {
        this.shouldHideRecents = shouldHideRecents;
    }

    private void computeSections() {
        if (!shouldHideRecents) {
            List<BeneficiaryObject> shortenedRecents = BeneficiaryUtils.computeRecents(originalBeneficiaries);
            HeaderBeneficiary headerBeneficiary = new HeaderBeneficiary();
            headerBeneficiary.setBeneficiaryName(BMBApplication.getInstance().getString(R.string.recent));
            shortenedRecents.add(0, headerBeneficiary);
            sectionedBeneficiaries.addAll(shortenedRecents);
        }
        List<BeneficiaryObject> beneficiariesByName = BeneficiaryUtils.sortBeneficiariesByFirstName(originalBeneficiaries);
        sectionedBeneficiaries.addAll(buildSections(beneficiariesByName));
    }

    private List<BeneficiaryObject> buildSections(List<BeneficiaryObject> beneficiaries) {
        char firstLetterOfBeneficiaryName;
        char sectionHeader = ' ';

        List<BeneficiaryObject> groupedBeneficiaries = new ArrayList<>();

        for (BeneficiaryObject beneficiary : beneficiaries) {
            String beneficiaryName = beneficiary.getBeneficiaryName();

            firstLetterOfBeneficiaryName = (beneficiaryName != null && !beneficiaryName.isEmpty()) ? beneficiaryName.toUpperCase().charAt(0) : ' ';

            if (firstLetterOfBeneficiaryName != sectionHeader) {
                sectionHeader = firstLetterOfBeneficiaryName;
                HeaderBeneficiary headerBeneficiary = new HeaderBeneficiary();
                headerBeneficiary.setBeneficiaryName(String.valueOf(sectionHeader));
                groupedBeneficiaries.add(headerBeneficiary);
            }
            groupedBeneficiaries.add(beneficiary);

        }
        return groupedBeneficiaries;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case Entry.HEADER:
                final View headerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.beneficiary_list_section_header_item, parent, false);
                return new BeneficiarySectionHeaderViewHolder(headerView);
            case Entry.BENEFICIARY:
                final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.beneficiary_list_item, parent, false);
                return new BeneficiarySectionItemViewHolder(itemView);
            default:
                throw new RuntimeException("Unexpected View Type");
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final BeneficiaryObject beneficiary = sectionedBeneficiaries.get(position);
        switch (beneficiary.getEntryType()) {
            case Entry.HEADER:
                BeneficiarySectionHeaderViewHolder beneficiarySectionHeaderViewHolder = (BeneficiarySectionHeaderViewHolder) holder;
                beneficiarySectionHeaderViewHolder.headerTextView.setText(beneficiary.getBeneficiaryName());
                break;
            case Entry.BENEFICIARY:
                BeneficiarySectionItemViewHolder beneficiarySectionItemViewHolder = (BeneficiarySectionItemViewHolder) holder;
                String accountDescription = "";
                String placeholderText = context.getString(R.string.purchased);
                if (PASS_PAYMENT.equalsIgnoreCase(beneficiaryType)) {
                    accountDescription = beneficiary.getBankName() + " | " + beneficiary.getBeneficiaryAccountNumber();
                    placeholderText = context.getString(R.string.paid);
                } else if (PASS_PREPAID.equalsIgnoreCase(beneficiaryType) || PASS_AIRTIME.equalsIgnoreCase(beneficiaryType)) {
                    accountDescription = beneficiary.getMyReference() + " | " + beneficiary.getBeneficiaryAccountNumber();
                    placeholderText = context.getString(R.string.purchased);
                } else if (PASS_CASHSEND.equalsIgnoreCase(beneficiaryType)) {
                    accountDescription = beneficiary.getBeneficiaryAccountNumber();
                    String formattedAccountNumber = AccessibilityUtils.getTalkBackAccountNumberFromString(beneficiary.getBeneficiaryAccountNumber());
                    String beneficiaryName = beneficiary.getBeneficiaryName();
                    ((BeneficiarySectionItemViewHolder) holder).beneficiaryView.setContentDescription(context.getString(R.string.talkback_cashsend_recent_sends, beneficiaryName, formattedAccountNumber));
                    placeholderText = "CashSend";
                } else if (PASS_PREPAID_ELECTRICITY.equalsIgnoreCase(beneficiaryType)) {
                    accountDescription = beneficiary.getBeneficiaryAccountNumber();
                    try {
                        byte[] decodedString = Base64.decode(beneficiary.getImageName(), Base64.URL_SAFE);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ((BeneficiarySectionItemViewHolder) holder).beneficiaryView.setImage(decodedByte);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    placeholderText = context.getString(R.string.purchased);
                }

                String lastPaymentDetail = null;
                Amount lastTransactionAmount = beneficiary.getLastTransactionAmount();
                if (lastTransactionAmount != null) {
                    lastPaymentDetail = context.getString(R.string.last_transaction_beneficiary, lastTransactionAmount.toString(), placeholderText, beneficiary.getLastTransactionDate());
                }
                BeneficiaryListItem displayItem = new BeneficiaryListItem(beneficiary.getBeneficiaryName(), accountDescription, lastPaymentDetail);
                beneficiarySectionItemViewHolder.beneficiaryView.setBeneficiary(displayItem);
                BeneficiaryUtils.applyFilter(beneficiarySectionItemViewHolder, beneficiary, filterText);

                beneficiarySectionItemViewHolder.beneficiaryView.setOnClickListener(v -> callback.onBeneficiaryClicked(beneficiary, beneficiaryType));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return sectionedBeneficiaries.size();
    }

    @Override
    public int getItemViewType(int position) {
        return sectionedBeneficiaries.get(position).getEntryType();
    }

    private static class BeneficiarySectionHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTextView;

        BeneficiarySectionHeaderViewHolder(View itemView) {
            super(itemView);
            headerTextView = itemView.findViewById(R.id.section_name_textview);
        }
    }

    static class BeneficiarySectionItemViewHolder extends RecyclerView.ViewHolder {
        BeneficiaryView beneficiaryView;

        BeneficiarySectionItemViewHolder(View itemView) {
            super(itemView);
            beneficiaryView = itemView.findViewById(R.id.beneficiary_view);
        }
    }
}