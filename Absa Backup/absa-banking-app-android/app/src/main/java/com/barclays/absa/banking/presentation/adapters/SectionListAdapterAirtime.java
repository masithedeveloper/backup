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

import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
import com.barclays.absa.banking.buy.ui.airtime.BuyPrepaidActivity;
import com.barclays.absa.banking.presentation.shared.FilterResultInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Adapter for sections.
 */
public class SectionListAdapterAirtime extends BaseAdapter implements ListAdapter, Filterable, SectionIndexer, OnItemClickListener {
    private ArrayList<BeneficiaryObject> orig;
    private final ListAdapter linkedAdapter;
    private final Map<Integer, String> sectionPositions = new LinkedHashMap<>();
    private final Map<Integer, Integer> itemPositions = new LinkedHashMap<>();
    private final Map<View, String> currentViewSections = new HashMap<>();
    private int viewTypeCount;
    private final LayoutInflater inflater;
    private View transparentSectionView;
    private OnItemClickListener linkedListener;
    private FilterResultInterface filterResultInterface;

    public void setFilterInterface(FilterResultInterface filterResultInterface) {
        this.filterResultInterface = filterResultInterface;
    }

    public SectionListAdapterAirtime(final LayoutInflater inflater, final ListAdapter linkedAdapter) {
        this.linkedAdapter = linkedAdapter;
        this.inflater = inflater;
        DataSetObserver dataSetObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                updateSessionCache();
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
                updateSessionCache();
            }
        };
        this.linkedAdapter.registerDataSetObserver(dataSetObserver);
        updateSessionCache();
    }

    private boolean isTheSame(final String previousSection, final String newSection) {
        if (previousSection == null) {
            return newSection == null;
        } else {
            return previousSection.equals(newSection);
        }
    }

    public synchronized void updateSessionCache() {
        int currentPosition = 0;
        sectionPositions.clear();
        itemPositions.clear();
        viewTypeCount = linkedAdapter.getViewTypeCount() + 1;
        String currentSection = null;
        final int count = linkedAdapter.getCount();
        for (int i = 0; i < count; i++) {
            final SectionListItem item = (SectionListItem) linkedAdapter.getItem(i);
            if (item != null && !isTheSame(currentSection, item.section)) {
                sectionPositions.put(currentPosition, item.section);
                currentSection = item.section;
                currentPosition++;
            }
            itemPositions.put(currentPosition, i);
            currentPosition++;
        }
    }

    @Override
    public synchronized int getCount() {
        return sectionPositions.size() + itemPositions.size();
    }

    @Override
    public synchronized Object getItem(final int position) {
        if (isSection(position)) {
            return sectionPositions.get(position);
        } else {
            final int linkedItemPosition = getLinkedPosition(position);
            return linkedAdapter.getItem(linkedItemPosition);
        }
    }

    private synchronized boolean isSection(final int position) {
        return sectionPositions.containsKey(position);
    }

    private synchronized String getSectionName(final int position) {
        if (isSection(position)) {
            return sectionPositions.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(final int position) {
        if (isSection(position)) {
            return sectionPositions.get(position).hashCode();
        } else {
            return linkedAdapter.getItemId(getLinkedPosition(position));
        }
    }

    private Integer getLinkedPosition(final int position) {
        return itemPositions.get(position);
    }

    @Override
    public int getItemViewType(final int position) {
        if (isSection(position)) {
            return viewTypeCount - 1;
        }
        return linkedAdapter.getItemViewType(getLinkedPosition(position));
    }

    private View getSectionView(final View convertView, final String section) {
        View theView = convertView;
        if (theView == null) {
            theView = createNewSectionView();
        }
        setSectionText(section, theView);
        replaceSectionViewsInMaps(section, theView);
        return theView;
    }

    private void setSectionText(final String section, final View sectionView) {
        final TextView textView = sectionView
                .findViewById(R.id.sectionListAlphabetView);
        textView.setText(section);
    }

    private synchronized void replaceSectionViewsInMaps(final String section, final View theView) {
        currentViewSections.remove(theView);
        currentViewSections.put(theView, section);
    }

    private View createNewSectionView() {
        return inflater.inflate(R.layout.section_view, null);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        if (isSection(position)) {
            return getSectionView(convertView, sectionPositions.get(position));
        }
        return linkedAdapter.getView(getLinkedPosition(position), convertView, parent);
    }

    @Override
    public int getViewTypeCount() {
        return viewTypeCount;
    }

    @Override
    public boolean hasStableIds() {
        return linkedAdapter.hasStableIds();
    }

    @Override
    public boolean isEmpty() {
        return linkedAdapter.isEmpty();
    }

    @Override
    public void registerDataSetObserver(final DataSetObserver observer) {
        linkedAdapter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(final DataSetObserver observer) {
        linkedAdapter.unregisterDataSetObserver(observer);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return linkedAdapter.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(final int position) {
        return isSection(position) || linkedAdapter.isEnabled(getLinkedPosition(position));
    }

    public void makeSectionInvisibleIfFirstInList(final int firstVisibleItem) {
        final String section = getSectionName(firstVisibleItem);
        // only make invisible the first section with that name in case there  are more with the same name
        boolean alreadySetFirstSectionInvisible = false;
        for (final Entry<View, String> itemView : currentViewSections.entrySet()) {
            if (itemView.getValue().equals(section) && !alreadySetFirstSectionInvisible) {
                itemView.getKey().setVisibility(View.INVISIBLE);
                alreadySetFirstSectionInvisible = true;
            } else {
                itemView.getKey().setVisibility(View.VISIBLE);
            }
        }
        for (final Entry<Integer, String> entry : sectionPositions.entrySet()) {
            if (entry.getKey() > firstVisibleItem + 1) {
                break;
            }
            getTransparentSectionView().setVisibility(View.GONE);
        }
    }

    public synchronized View getTransparentSectionView() {
        if (transparentSectionView == null) {
            transparentSectionView = createNewSectionView();
        }
        return transparentSectionView;
    }

    private void sectionClicked(final String section) {
        // do nothing
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        if (isSection(position)) {
            sectionClicked(getSectionName(position));
        } else if (linkedListener != null) {
            linkedListener.onItemClick(parent, view, getLinkedPosition(position), id);
        }
    }

    public void setOnItemClickListener(final OnItemClickListener linkedListener) {
        this.linkedListener = linkedListener;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                ArrayList<BeneficiaryObject> results = new ArrayList<>();
                if (orig == null)
                    orig = (ArrayList<BeneficiaryObject>) ((BeneficiaryCustomSectionListAdapterPrepaid) linkedAdapter).getBeneficiaryListData();
                if (constraint != null) {
                    if (orig != null && orig.size() > 0) {
                        for (final BeneficiaryObject g : orig) {
                            if (g.getBeneficiaryName().toLowerCase().contains(constraint.toString().toLowerCase()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ((BeneficiaryCustomSectionListAdapterPrepaid) linkedAdapter).setBeneficiaryListData((ArrayList<BeneficiaryObject>) results.values);
                ((BeneficiaryCustomSectionListAdapterPrepaid) linkedAdapter).setItems(BuyPrepaidActivity.createSectionedList(((BeneficiaryCustomSectionListAdapterPrepaid) linkedAdapter).getBeneficiaryListData()));
                updateSessionCache();
                ((BeneficiaryCustomSectionListAdapterPrepaid) linkedAdapter).notifyDataSetChanged();
                notifyDataSetChanged();

                if (filterResultInterface != null) {
                    if (((ArrayList<BeneficiaryObject>) results.values).isEmpty()) {
                        filterResultInterface.filterIsEmpty();
                    } else {
                        filterResultInterface.filterIsNotEmpty();
                    }
                }
            }
        };

    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int i) {
        List<Integer> indexes = new ArrayList<>(sectionPositions.keySet());
        return indexes.get(i);
    }

    @Override
    public int getSectionForPosition(int i) {
        return 0;
    }
}
