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

package com.barclays.absa.banking.newToBank;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.barclays.absa.banking.R;

import java.util.ArrayList;

import styleguide.content.TertiaryContentAndLabelView;

public class ShowBranchListAdapter extends RecyclerView.Adapter<ShowBranchListAdapter.ViewHolder> implements Filterable {

    private ArrayList<String> items;
    private ItemClickListener itemClickListener;
    private BranchListFilter branchListFilter;

    ShowBranchListAdapter(@NonNull ArrayList<String> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_city_list_item, parent, false);
        return new ShowBranchListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String item = items.get(position);

        holder.tertiaryContentAndLabelView.setContentText(item);
        holder.tertiaryContentAndLabelView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        } else {
            return 0;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TertiaryContentAndLabelView tertiaryContentAndLabelView;

        public ViewHolder(View itemView) {
            super(itemView);
            tertiaryContentAndLabelView = itemView.findViewById(R.id.tertiaryContentAndLabelView);
        }
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public Filter getFilter() {
        if (branchListFilter == null) {
            branchListFilter = new BranchListFilter(items, this);
        }

        return branchListFilter;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }
}