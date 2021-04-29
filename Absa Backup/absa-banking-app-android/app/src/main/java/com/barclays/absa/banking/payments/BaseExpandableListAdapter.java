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
package com.barclays.absa.banking.payments;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.barclays.absa.banking.R;

import java.util.HashMap;
import java.util.List;

import styleguide.content.PrimaryContentAndLabelView;

public class BaseExpandableListAdapter<T> extends android.widget.BaseExpandableListAdapter implements SectionIndexer {

    private final String HEADER_NOT_REQUIRED = "header_not_required";
    private final String ALL_HEADERS = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private Activity activity;
    private BaseExpandableListView<T> baseExpandableListView;
    private int childTemplateId;
    private int headerTemplateId;
    private List<T> headerItemSource;
    private HashMap<String, List<T>> childItemSource;
    private String[] sections;

    public BaseExpandableListAdapter(Activity activity, int childTemplateId, int headerTemplateId, HashMap<String, List<T>> childItemSource, List<T> headerItemSource) {
        this.childTemplateId = childTemplateId;
        this.headerTemplateId = headerTemplateId;
        this.childItemSource = childItemSource;
        this.headerItemSource = headerItemSource;
        this.activity = activity;
        if (activity != null && activity instanceof BaseExpandableListView) {
            baseExpandableListView = (BaseExpandableListView<T>) activity;
        }
        createSectionArray();
    }

    private void createSectionArray() {
        sections = new String[ALL_HEADERS.length()];
        for (int i = 0; i < ALL_HEADERS.length(); i++) {
            sections[i] = String.valueOf(ALL_HEADERS.charAt(i));
        }
    }

    @Override
    public int getGroupCount() {
        return headerItemSource != null ? headerItemSource.size() : 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<T> ts = childItemSource.get(headerItemSource.get(groupPosition));
        if (childItemSource != null && ts != null) {
            return ts.size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return headerItemSource.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childItemSource.get(headerItemSource.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (headerTitle.equals(HEADER_NOT_REQUIRED)) {
            return new View(parent.getContext());
        } else {
            LayoutInflater layoutInflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(headerTemplateId, null);
            switch (headerTemplateId) {
                case R.layout.section_view:
                    TextView textView = convertView.findViewById(R.id.sectionListAlphabetView);
                    textView.setText(headerTitle);
                    break;
                default:
                    break;
            }
            return convertView;
        }
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final T item = (T) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(childTemplateId, null);
        }
        if (childTemplateId == R.layout.primary_content_and_label_row) {
            PrimaryContentAndLabelView primaryContentAndLabelView = (PrimaryContentAndLabelView) convertView;
            primaryContentAndLabelView.setContentText(baseExpandableListView.getPrimaryData(item));
            primaryContentAndLabelView.setContentTextStyle(R.style.LargeTextRegularDark);
            if (baseExpandableListView.isSecondaryLabelRequired()) {
                primaryContentAndLabelView.setLabelText(baseExpandableListView.getSecondaryData(item));
                primaryContentAndLabelView.setLabelTextStyle(R.style.SmallTextRegularDark);
            } else {
                primaryContentAndLabelView.getLabelTextView().setVisibility(View.GONE);
            }
            final CheckBox checkBox = convertView.findViewById(R.id.primary_content_check_box);
            checkBox.setOnClickListener(view -> baseExpandableListView.onItemClick(item));
            primaryContentAndLabelView.setOnClickListener(view -> {
                checkBox.setChecked(true);
                baseExpandableListView.onItemClick(item);
            });
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void expandGroup(ExpandableListView expandableListView) {
        for (int i = 0; i < this.getGroupCount(); i++) {
            expandableListView.expandGroup(i);
        }
    }

    @Override
    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int section) {
        String sectionItem = sections[section];
        return headerItemSource.indexOf(sectionItem);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }
}
