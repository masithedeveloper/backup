/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */

package styleguide.forms;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import styleguide.content.TertiaryContentAndLabelView;
import za.co.absa.presentation.uilib.R;
import za.co.absa.presentation.uilib.databinding.MultiSelectItemBinding;

public class AdditionalDamagesItemAdapter<T> extends RecyclerView.Adapter<AdditionalDamagesItemAdapter.AdditionalDamagesItemViewHolder> {

    private List<MultiSelectItem<T>> multiSelectItemList;
    private AdditionalItemCheckListener additionalItemCheckListener;

    AdditionalDamagesItemAdapter(List<MultiSelectItem<T>> multiSelectItemList, AdditionalItemCheckListener additionalItemCheckListener) {
        this.multiSelectItemList = multiSelectItemList;
        this.additionalItemCheckListener = additionalItemCheckListener;
    }

    @NonNull
    @Override
    public AdditionalDamagesItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MultiSelectItemBinding multiSelectItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.multi_select_item, parent, false);
        return new AdditionalDamagesItemViewHolder(multiSelectItemBinding, additionalItemCheckListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdditionalDamagesItemViewHolder holder, int position) {
        MultiSelectItem multiSelectItem = multiSelectItemList.get(position);
        holder.additionalDamagesItemView.setContentText(multiSelectItem.getItem().toString());
        holder.additionalDamagesItemView.setTertiaryCheckBoxChecked(multiSelectItem.isItemSelected());
        holder.setSelectedPosition(position);
    }

    @Override
    public int getItemCount() {
        return multiSelectItemList == null ? 0 : multiSelectItemList.size();
    }

    interface AdditionalItemCheckListener {
        void onItemChecked(int position, boolean isChecked);
    }

    static class AdditionalDamagesItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, OnCheckedListener {
        TertiaryContentAndLabelView additionalDamagesItemView;
        AdditionalItemCheckListener additionalItemCheckListener;
        private int position;

        AdditionalDamagesItemViewHolder(MultiSelectItemBinding multiSelectItemBinding, AdditionalItemCheckListener additionalItemCheckListener) {
            super(multiSelectItemBinding.getRoot());
            additionalDamagesItemView = multiSelectItemBinding.additionalDamagesItemView;
            this.additionalItemCheckListener = additionalItemCheckListener;
            this.additionalDamagesItemView.setOnClickListener(this);
            this.additionalDamagesItemView.getTertiaryCheckBox().setClickable(false);
        }

        void setSelectedPosition(int position) {
            this.position = position;
        }

        int getSelectedPosition() {
            return position;
        }

        @Override
        public void onClick(View v) {
            boolean isChecked = additionalDamagesItemView.getTertiaryCheckBox().isChecked();
            if (isChecked) {
                additionalDamagesItemView.getTertiaryCheckBox().setChecked(false);
            } else {
                additionalDamagesItemView.getTertiaryCheckBox().setChecked(true);
            }
            additionalItemCheckListener.onItemChecked(getSelectedPosition(), additionalDamagesItemView.getTertiaryCheckBox().isChecked());
        }

        @Override
        public void onChecked(boolean isChecked) {
            additionalItemCheckListener.onItemChecked(getSelectedPosition(), isChecked);
        }
    }

    void retainPreviouslyCheckedItems(List<Integer> previouslyCheckedItemsList) {
        if (previouslyCheckedItemsList != null && !previouslyCheckedItemsList.isEmpty()) {
            for (int index = 0; index < previouslyCheckedItemsList.size(); index++) {
                multiSelectItemList.get(previouslyCheckedItemsList.get(index)).setItemSelected(true);
            }
        }
    }
}
