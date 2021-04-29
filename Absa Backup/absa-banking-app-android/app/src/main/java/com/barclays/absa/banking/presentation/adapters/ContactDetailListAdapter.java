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
package com.barclays.absa.banking.presentation.adapters;

import android.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.DialogContactDetailListItemBinding;
import com.barclays.absa.banking.presentation.contactDetail.ContactDetailListItemViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ContactDetailListAdapter extends RecyclerView.Adapter<ContactDetailListAdapter.BindingHolder> {

    private AlertDialog alertDialog;
    private List<Pair<String, String>> contactDetail;
    private ContactDetailSelection contactDetailSelection;

    public interface ContactDetailSelection {
        void onContactDetailForSelection(AlertDialog alertDialog, String contactDetail);
    }

    public ContactDetailListAdapter(AlertDialog alertDialog, List<Pair<String, String>> contactDetail, ContactDetailSelection contactDetailSelection) {
        this.alertDialog = alertDialog;
        this.contactDetail = contactDetail;
        this.contactDetailSelection = contactDetailSelection;
    }

    @NotNull
    @Override
    public ContactDetailListAdapter.BindingHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        DialogContactDetailListItemBinding contactDetailListItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.dialog_contact_detail_list_item, parent, false
        );
        return new BindingHolder(contactDetailListItemBinding);
    }

    @Override
    public void onBindViewHolder(BindingHolder bindingHolder, int position) {
        DialogContactDetailListItemBinding dialogContactDetailListItemBinding = bindingHolder.dialogContactDetailListItemBinding;
        dialogContactDetailListItemBinding.setContactDetailViewModel(new ContactDetailListItemViewModel(this.contactDetail.get(position)));
    }

    @Override
    public int getItemCount() {
        return contactDetail.size();
    }

    class BindingHolder extends RecyclerView.ViewHolder {

        private DialogContactDetailListItemBinding dialogContactDetailListItemBinding;

        BindingHolder(DialogContactDetailListItemBinding dialogContactDetailListItemBinding) {
            super(dialogContactDetailListItemBinding.contactDetailConstraintLayout);
            this.dialogContactDetailListItemBinding = dialogContactDetailListItemBinding;
            this.dialogContactDetailListItemBinding.contactDetailConstraintLayout.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (0 <= position && position < contactDetail.size()) {
                    contactDetailSelection.onContactDetailForSelection(alertDialog, contactDetail.get(position).second);
                }
            });
        }
    }
}

