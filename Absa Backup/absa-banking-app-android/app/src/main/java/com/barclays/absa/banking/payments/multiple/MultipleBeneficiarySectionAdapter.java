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
package com.barclays.absa.banking.payments.multiple;

import android.content.Context;
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

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
import com.barclays.absa.banking.databinding.MultipleBeneficiarySelectionItemBinding;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.paymentsRewrite.ui.multiple.MultipleBeneficiarySectionListItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class MultipleBeneficiarySectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private static final int ITEM_TYPE_BENEFICIARY = 0;
    private static final int ITEM_TYPE_ALPHABET = 1;
    private final List<BeneficiaryObject> originalBeneficiaryList;
    private MultipleBeneficiarySectionListItem[] beneficiarySectionedList;
    private List<BeneficiaryObject> adapterBackingBeneficiaryList;
    private List<MultipleBeneficiarySectionListItem> listItems = new ArrayList<>();
    private MultipleBeneficiarySelectionView multipleBeneficiarySelectionView;
    private StringBuilder beneficiaryDetailsBuilder = new StringBuilder();
    private final int maximumAllowedBeneficiaries = 0;
    private String searchTerm;
    private final Context context;

    public static final class AlphabetViewHolder extends RecyclerView.ViewHolder {
        final TextView tvAlphabet;

        AlphabetViewHolder(View view) {
            super(view);
            tvAlphabet = view.findViewById(R.id.sectionListAlphabetView);
        }
    }

    public MultipleBeneficiarySectionAdapter(Context context, MultipleBeneficiarySelectionView selectionView, List<BeneficiaryObject> beneficiaryObjectArrayList) {
        multipleBeneficiarySelectionView = selectionView;
        //maximumAllowedBeneficiaries = this.multipleBeneficiarySelectionView.isBusinessAccount() ? MAX_ALLOWED_BUSINESS_BENEFICIARIES : MAX_ALLOWED_RETAIL_BENEFICIARIES;
        originalBeneficiaryList = beneficiaryObjectArrayList;
        this.context = context;
        adapterBackingBeneficiaryList = originalBeneficiaryList;
        if (adapterBackingBeneficiaryList != null) {
            //   beneficiarySectionedList = CommonUtils.createMultiplePaymentsSectionedList(adapterBackingBeneficiaryList);
        }
        RecyclerView.AdapterDataObserver dataSetObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                updateSessionCache();
            }
        };
        registerAdapterDataObserver(dataSetObserver);
        updateSessionCache();
    }

    @Override
    public int getItemViewType(int position) {
        if (listItems.get(position).isSection()) {
            return ITEM_TYPE_ALPHABET;
        } else {
            return ITEM_TYPE_BENEFICIARY;
        }
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ITEM_TYPE_ALPHABET) {
            return new AlphabetViewHolder(inflater.inflate(R.layout.section_view, parent, false));
        }
        MultipleBeneficiarySelectionItemBinding multipleBeneficiarySelectionItemBinding = MultipleBeneficiarySelectionItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BeneficiaryItemHolder(multipleBeneficiarySelectionItemBinding);
    }

    @Override
    public void onBindViewHolder(@NotNull final RecyclerView.ViewHolder viewHolder, int position) {
        final int itemType = getItemViewType(position);
        BMBLogger.d("POSITION", "" + position);
        switch (itemType) {
            case ITEM_TYPE_BENEFICIARY:
                bindBeneficiaryView(viewHolder, position);
                break;
            case ITEM_TYPE_ALPHABET:
                bindAlphabetView((AlphabetViewHolder) viewHolder, position);
                break;
        }
    }

    private void bindBeneficiaryView(RecyclerView.ViewHolder viewHolder, int position) {
        MultipleBeneficiarySectionListItem multipleBeneficiarySectionListItem = listItems.get(position);
        BeneficiaryObject beneficiaryObject = ((BeneficiaryObject) multipleBeneficiarySectionListItem.item);
        MultipleBeneficiarySelectionItemBinding multipleBeneficiarySelectionItemBinding = ((BeneficiaryItemHolder) viewHolder).multipleBeneficiarySelectionItemBinding;

        String beneficiaryName = beneficiaryObject.getBeneficiaryName();
        multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView.setContentText(beneficiaryName);

        beneficiaryDetailsBuilder.append(beneficiaryObject.getBankName());
        if (!TextUtils.isEmpty(beneficiaryObject.getBeneficiaryAccountNumber())) {

            beneficiaryDetailsBuilder.append(" | ").append(beneficiaryObject.getBeneficiaryAccountNumber());
        }
        multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView.setLabelText(beneficiaryDetailsBuilder.toString());

        String accountNumber = beneficiaryObject.getBeneficiaryAccountNumber().replace("", ",");
        multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView.setContentDescription(context.getString(R.string.talkback_multipay_choose_beneficiary, beneficiaryName, accountNumber));

        if (null != multipleBeneficiarySelectionView.getSelectedBeneficiaries() && multipleBeneficiarySelectionView.getSelectedBeneficiaries().contains(beneficiaryObject)) {
            multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView.setSecondaryCheckBoxChecked(true);
        } else {
            multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView.setSecondaryCheckBoxChecked(false);
        }

        if (!TextUtils.isEmpty(searchTerm)) {
            if (beneficiaryName != null) {
                int startPosition = beneficiaryName.toLowerCase().indexOf(searchTerm);
                int endPosition = startPosition + searchTerm.length();
                if (startPosition != -1) {
                    Spannable spannable = new SpannableString(beneficiaryName);
                    ColorStateList highlightColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{ContextCompat.getColor(context, R.color.dark_orange)});
                    TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, highlightColor, null);
                    spannable.setSpan(highlightSpan, startPosition, endPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView.getContentTextView().setText(spannable);
                } else {
                    multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView.setContentText(beneficiaryName);
                }
            }
        } else {
            multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView.setContentText(beneficiaryName);
        }

        final boolean isDisabled = multipleBeneficiarySelectionView.getSelectedBeneficiaries().size() == maximumAllowedBeneficiaries
                && !multipleBeneficiarySelectionView.getSelectedBeneficiaries().contains(beneficiaryObject);

        multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView.setAlpha(isDisabled ? 0.5f : 1f);
        multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView.showCheckBox(isDisabled);
        beneficiaryDetailsBuilder = new StringBuilder();
    }

    private void bindAlphabetView(AlphabetViewHolder viewHolder, int position) {
        viewHolder.tvAlphabet.setText(listItems.get(position).section);
        viewHolder.tvAlphabet.setPadding(0, 0, 0, 0);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    private synchronized void updateSessionCache() {
        listItems.clear();
        MultipleBeneficiarySectionListItem previousItem = null;
        final int count = adapterBackingBeneficiaryList.size();

        for (int position = 0; position < count; position++) {
            MultipleBeneficiarySectionListItem currentItem = getSectionListItem(position);
            if (currentItem != null) {
                if (previousItem == null || isNotTheSame(previousItem.section, currentItem.section)) {
                    MultipleBeneficiarySectionListItem listItem = new MultipleBeneficiarySectionListItem(new Object(), currentItem.section);
                    listItem.setSection(true);
                    currentItem.setBeginningOfSection(true);
                    listItems.add(listItem);
                }
                MultipleBeneficiarySectionListItem nextItem = getSectionListItem(position + 1);
                if (nextItem != null && isNotTheSame(currentItem.section, nextItem.section)) {
                    currentItem.setEndOfSection(true);
                }
                if (position == count - 1) {
                    currentItem.setEndOfSection(true);
                }
                listItems.add(currentItem);
                previousItem = currentItem;
            }
        }
    }

    private boolean isNotTheSame(final String previousSection, final String newSection) {
        return (previousSection == null || !previousSection.equals(newSection));
    }

    private MultipleBeneficiarySectionListItem getSectionListItem(int linkedItemPosition) {
        MultipleBeneficiarySectionListItem sectionListItem = null;
        if (linkedItemPosition >= 0) {
            if (beneficiarySectionedList != null && beneficiarySectionedList.length > 0 && beneficiarySectionedList.length > linkedItemPosition) {
                sectionListItem = beneficiarySectionedList[linkedItemPosition];
            }
        }
        return sectionListItem;
    }

    public BeneficiaryObject getSectionListBeneficiary(int position) {
        BeneficiaryObject beneficiaryObject = null;
        MultipleBeneficiarySectionListItem sectionListItem = getSectionListItem(position);
        if (sectionListItem != null && !sectionListItem.isSection()) {
            beneficiaryObject = (BeneficiaryObject) sectionListItem.item;
        }
        return beneficiaryObject;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    searchTerm = constraint.toString().toLowerCase();
                }
                final FilterResults filterResults = new FilterResults();
                List<BeneficiaryObject> results = new ArrayList<>();
                if (constraint != null) {
                    if (originalBeneficiaryList != null && originalBeneficiaryList.size() > 0) {
                        for (final BeneficiaryObject beneficiaryObject : originalBeneficiaryList) {
                            beneficiaryObject.getBeneficiaryName();
                            if (beneficiaryObject.getBeneficiaryName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                results.add(beneficiaryObject);
                            }
                        }
                    }
                    filterResults.values = results;
                } else {
                    searchTerm = "";
                    filterResults.values = originalBeneficiaryList;
                }
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null) {
                    adapterBackingBeneficiaryList = (List<BeneficiaryObject>) results.values;
                    multipleBeneficiarySelectionView.onBeneficiaryListFiltered(adapterBackingBeneficiaryList);
                    //    beneficiarySectionedList = CommonUtils.createMultiplePaymentsSectionedList(adapterBackingBeneficiaryList);
                    updateSessionCache();
                    notifyDataSetChanged();
                }
            }
        };
    }

    class BeneficiaryItemHolder extends RecyclerView.ViewHolder {
        private final MultipleBeneficiarySelectionItemBinding multipleBeneficiarySelectionItemBinding;

        BeneficiaryItemHolder(MultipleBeneficiarySelectionItemBinding multipleBeneficiarySelectionItemBinding) {
            super(multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView);
            this.multipleBeneficiarySelectionItemBinding = multipleBeneficiarySelectionItemBinding;
            this.multipleBeneficiarySelectionItemBinding.getRoot().setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (0 <= position && position < listItems.size()) {
                    MultipleBeneficiarySectionListItem listItem = listItems.get(position);
                    if (!listItem.isSection()) {
                        BeneficiaryObject beneficiary = (BeneficiaryObject) listItem.item;
                        int index = adapterBackingBeneficiaryList.lastIndexOf(beneficiary);
                        multipleBeneficiarySelectionView.onBeneficiaryClicked(index);
                        multipleBeneficiarySelectionView.stopSearch();
                    }
                }
            });

            multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView.getSecondaryCheckBox().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (0 <= position && position < listItems.size()) {
                    MultipleBeneficiarySectionListItem listItem = listItems.get(position);
                    if (!listItem.isSection()) {
                        BeneficiaryObject beneficiary = (BeneficiaryObject) listItem.item;
                        int index = adapterBackingBeneficiaryList.lastIndexOf(beneficiary);
                        multipleBeneficiarySelectionView.onBeneficiaryClicked(index);
                        multipleBeneficiarySelectionView.stopSearch();
                    }
                }
            });
        }
    }
}