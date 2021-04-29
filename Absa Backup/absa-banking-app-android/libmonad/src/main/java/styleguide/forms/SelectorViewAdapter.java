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

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import za.co.absa.presentation.uilib.R;

public class SelectorViewAdapter extends RecyclerView.Adapter<SelectorViewAdapter.ViewHolder> {

    private ItemSelectionInterface selectorViewAdapterInterface;
    private List<String> items;
    private int selectedIndex = -1;
    private List<String> contentDescriptions;

    SelectorViewAdapter(List<String> items, ItemSelectionInterface selectorViewAdapterInterface) {
        this.items = items;
        this.selectorViewAdapterInterface = selectorViewAdapterInterface;
    }

    SelectorViewAdapter(List<String> items, ItemSelectionInterface selectorViewAdapterInterface, List<String> contentDescriptions) {
        this.items = items;
        this.selectorViewAdapterInterface = selectorViewAdapterInterface;
        this.contentDescriptions = contentDescriptions;
    }

    SelectorViewAdapter(List<String> items, ItemSelectionInterface selectorViewAdapterInterface, int selectedIndex) {
        this.items = items;
        this.selectedIndex = selectedIndex;
        this.selectorViewAdapterInterface = selectorViewAdapterInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selector_view_item, parent, false);
        SelectorViewAdapter.ViewHolder holder = new SelectorViewAdapter.ViewHolder(view);
        holder.descriptionTextView.setFocusable(true);
        return holder;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Context context = holder.itemView.getContext();
        String item = items.get(position);

        if (contentDescriptions != null && contentDescriptions.size() > 0 && contentDescriptions.size() == getItemCount()) {
            holder.descriptionTextView.setContentDescription(contentDescriptions.get(position));
        }

        holder.descriptionTextView.setText(item);
        holder.descriptionTextView.setFocusableInTouchMode(false);

        if (selectedIndex == holder.getAdapterPosition()) {
            holder.descriptionTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.silver));
        } else {
            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            holder.descriptionTextView.setBackgroundResource(outValue.resourceId);
        }

        holder.descriptionTextView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                holder.descriptionTextView.setFocusableInTouchMode(true);
                selectedIndex = holder.getAdapterPosition();

                TypedValue outValue = new TypedValue();
                context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                holder.descriptionTextView.setBackgroundResource(outValue.resourceId);
                selectorViewAdapterInterface.onItemClicked(selectedIndex);
            }
            return false;
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

    public int getSelectedIndex() {
        return selectedIndex;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView descriptionTextView;

        ViewHolder(View itemView) {
            super(itemView);
            descriptionTextView = itemView.findViewById(R.id.description_text_view);
        }
    }
}
