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

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
import com.barclays.absa.banking.databinding.MultipleBeneficiarySelectedItemBinding;
import com.barclays.absa.banking.framework.app.BMBApplication;

import java.util.List;

import styleguide.utils.extensions.StringExtensions;

@Deprecated
class SelectedBeneficiaryAdapter extends RecyclerView.Adapter<SelectedBeneficiaryAdapter.BeneficiaryItemHolder> {
    private final SelectBeneficiaryPresenterInterface presenter;
    private final List<BeneficiaryObject> selectedBeneficiaryList;

    SelectedBeneficiaryAdapter(List<BeneficiaryObject> selectedBeneficiaryList, SelectBeneficiaryPresenterInterface presenter) {
        this.selectedBeneficiaryList = selectedBeneficiaryList;
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public SelectedBeneficiaryAdapter.BeneficiaryItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new SelectedBeneficiaryAdapter.BeneficiaryItemHolder(null);
    }

    @Override
    public void onBindViewHolder(@NonNull BeneficiaryItemHolder bindingHolder, int position) {
        MultipleBeneficiarySelectedItemBinding multipleBeneficiarySelectedItemBinding = bindingHolder.multipleBeneficiarySelectedItemBinding;
        multipleBeneficiarySelectedItemBinding.selectedBeneficiaryNameTextView.setText(selectedBeneficiaryList.get(position).getBeneficiaryName());
        multipleBeneficiarySelectedItemBinding.selectedBeneficiaryAccountNumberTextView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);

        String name = selectedBeneficiaryList.get(position).getBeneficiaryName();
        String beneficiaryAccountNumber = selectedBeneficiaryList.get(position).getBeneficiaryAccountNumber();
        beneficiaryAccountNumber = StringExtensions.toMaskedAccountNumber(beneficiaryAccountNumber);
        multipleBeneficiarySelectedItemBinding.selectedBeneficiaryAccountNumberTextView.setText(beneficiaryAccountNumber);
        String talkBackAccountNumber = beneficiaryAccountNumber.replace("", ",");
        multipleBeneficiarySelectedItemBinding.selectedBeneficiaryNameTextView.setContentDescription(BMBApplication.getInstance().getString(R.string.talkback_multipay_beneficiary_list_item, name, talkBackAccountNumber));
        multipleBeneficiarySelectedItemBinding.initialsTextView.setText(StringExtensions.extractTwoLetterAbbreviation(selectedBeneficiaryList.get(position).getBeneficiaryName()));
    }

    @Override
    public int getItemCount() {
        return selectedBeneficiaryList.size();
    }

    class BeneficiaryItemHolder extends RecyclerView.ViewHolder {
        private final MultipleBeneficiarySelectedItemBinding multipleBeneficiarySelectedItemBinding;

        BeneficiaryItemHolder(final MultipleBeneficiarySelectedItemBinding multipleBeneficiarySelectedItemBinding) {
            super(multipleBeneficiarySelectedItemBinding.selectedBeneficiaryContainerConstraintLayout);
            this.multipleBeneficiarySelectedItemBinding = multipleBeneficiarySelectedItemBinding;
            this.multipleBeneficiarySelectedItemBinding.selectedBeneficiaryContainerConstraintLayout.setOnClickListener(view -> presenter.onSelectedBeneficiaryRemoveIconClicked(getAdapterPosition(), selectedBeneficiaryList));
        }
    }
}