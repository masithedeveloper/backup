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

package com.barclays.absa.banking.manage.devices;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.barclays.absa.banking.R;

import styleguide.buttons.OptionActionButtonView;

class ReasonDeviceNotAvailableAdapter extends RecyclerView.Adapter<ReasonDeviceNotAvailableAdapter.ViewHolder> {

    private final String[] reasonsDeviceNotAvailable;
    private ReasonDeviceNotAvailableView view;

    ReasonDeviceNotAvailableAdapter(String[] reasonsDeviceNotAvailable, ReasonDeviceNotAvailableView view) {
        this.reasonsDeviceNotAvailable = reasonsDeviceNotAvailable;
        this.view = view;
    }

    @NonNull
    @Override
    public ReasonDeviceNotAvailableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_not_available_reason_list_item, parent, false);
        return new ReasonDeviceNotAvailableAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReasonDeviceNotAvailableAdapter.ViewHolder holder, int position) {
        String reasonText = reasonsDeviceNotAvailable[position];
        holder.optionActionButtonView.setupCaptionImageTextIcon(reasonText, -1);

        holder.optionActionButtonView.setOnClickListener((View v) -> view.onReasonSelected(position));

        holder.optionActionButtonView.setClickable(true);
    }

    @Override
    public int getItemCount() {
        return reasonsDeviceNotAvailable == null ? 0 : reasonsDeviceNotAvailable.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        OptionActionButtonView optionActionButtonView;

        public ViewHolder(View itemView) {
            super(itemView);
            optionActionButtonView = itemView.findViewById(R.id.deviceReasonOptionActionButtonView);
        }
    }
}