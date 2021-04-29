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

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import za.co.absa.presentation.uilib.R;

public class GenericAdapter<T extends SelectorInterface> extends RecyclerView.Adapter<GenericAdapter.ViewHolder> implements Filterable {

    private final ItemSelectionInterface itemSelectionInterface;
    private CheckBox lastSelected;
    private ArrayList<T> items;
    private int selectedIndex = -1;
    private boolean isInteractionBlocked;
    private ArrayList<T> original;
    private String filterText = "";

    GenericAdapter(ArrayList<T> items, ItemSelectionInterface itemSelectionInterface) {
        this.items = items;
        this.itemSelectionInterface = itemSelectionInterface;
    }

    public GenericAdapter(ArrayList<T> items, int selectedIndex, ItemSelectionInterface itemSelectionInterface) {
        this.items = items;
        this.selectedIndex = selectedIndex;
        this.itemSelectionInterface = itemSelectionInterface;
    }

    @NonNull
    @Override
    public GenericAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.selector_list_item_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final GenericAdapter.ViewHolder holder, final int position) {
        final T item = items.get(position);

        if (item.getDisplayValue() == null || item.getDisplayValue().isEmpty()) {
            holder.contextTextView.setVisibility(View.GONE);
        } else {
            Spannable displayValue = new SpannableString(item.getDisplayValue());
            highlightTextInTextView(holder.contextTextView, displayValue);
        }

        if (item.getDisplayValueLine2() == null || item.getDisplayValueLine2().isEmpty()) {
            holder.labelTextView.setVisibility(View.GONE);
        } else {
            Spannable displayValueLine2 = new SpannableString(item.getDisplayValueLine2());
            highlightTextInTextView(holder.labelTextView, displayValueLine2);
        }

        holder.checkBox.setButtonDrawable(R.drawable.round_check_box_view_button);
        holder.checkBox.setOnCheckedChangeListener(null);

        if (position == selectedIndex) {
            lastSelected = holder.checkBox;
            lastSelected.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }

        holder.itemView.setOnClickListener(v -> {
            if (isInteractionBlocked) {
                return;
            }
            isInteractionBlocked = true;
            if (lastSelected != null) {
                lastSelected.setChecked(false);
            }
            holder.checkBox.setChecked(true);
            itemSelectionInterface.onItemClicked(holder.getAdapterPosition());
        });

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (isInteractionBlocked) {
                    return;
                }
                isInteractionBlocked = true;
                if (lastSelected != null && lastSelected != holder.checkBox) {
                    lastSelected.setChecked(false);
                }
                itemSelectionInterface.onItemClicked(holder.getAdapterPosition());
            }
        });
    }

    private void highlightTextInTextView(@NonNull TextView textView, Spannable displayValue) {
        int startTextPosition = displayValue.toString().toLowerCase(Locale.getDefault()).indexOf(filterText.toLowerCase(Locale.getDefault()));
        int endTextPosition = startTextPosition + filterText.length();

        if (endTextPosition <= displayValue.length() && startTextPosition != -1) {
            displayValue.setSpan(new ForegroundColorSpan(Color.RED), startTextPosition, endTextPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setText(displayValue);
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

    public void updateList(ArrayList<T> items, String query) {
        selectedIndex = -1;
        this.items = items;
        notifyDataSetChanged();
        filterText = query;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults filterResults = new FilterResults();
                ArrayList<T> results = new ArrayList<>();
                if (original == null) {
                    original = items;
                }
                if (constraint != null) {
                    if (original != null && original.size() > 0) {
                        for (final T lineItem : original) {
                            if (lineItem.getDisplayValue().toLowerCase(Locale.getDefault()).contains(constraint.toString())) {
                                results.add(lineItem);
                            }
                        }
                    }
                    filterResults.values = results;
                }
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                if (((List<T>) results.values).isEmpty()) {
                    //TODo: add empty message
                }
                items = (ArrayList<T>) results.values;
                notifyDataSetChanged();
            }
        };

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView contextTextView;
        TextView labelTextView;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            contextTextView = itemView.findViewById(R.id.content_text_view);
            labelTextView = itemView.findViewById(R.id.label_text_view);
            checkBox = itemView.findViewById(R.id.primary_content_check_box);
        }
    }
}
