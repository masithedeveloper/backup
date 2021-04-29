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

package com.barclays.absa.banking.payments.international;

import android.content.Intent;
import android.os.Bundle;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.payments.BaseExpandableListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InternationalPaymentsCitySelectorActivity extends BaseExpandableListView<String> {

    private ArrayList<String> childItemSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void getIntentExtraData(BaseExpandableListView baseExpandableListView) {
        InternationalPaymentsCitySelectorActivity chooseBranchListActivity = (InternationalPaymentsCitySelectorActivity) baseExpandableListView;
        ArrayList<String> cities = (ArrayList<String>) chooseBranchListActivity.getIntent().getSerializableExtra(InternationalPaymentsConstants.INTERNATIONAL_CITY_LIST);
        if (cities != null) {
            childItemSource = cities;
            if (childItemSource.size() > 0) {
                Collections.sort(childItemSource, (leftHandSide, rightHandSide) -> {
                    if (leftHandSide != null && rightHandSide != null) {
                        return leftHandSide.compareToIgnoreCase(rightHandSide);
                    }
                    return 0;
                });
            }
        }
    }

    @Override
    public void onItemClick(String item) {
        Intent intent = new Intent();
        intent.putExtra(InternationalPaymentsConstants.SELECTED_CITY, item);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public List<String> onListItemSearch(String searchString) {
        List<String> tempCityList = new ArrayList<>();
        for (String city : childItemSource) {
            if (city != null && city.toLowerCase().contains(searchString.toLowerCase()) ||
                    city != null && city.toLowerCase().contains(searchString.toLowerCase())) {
                tempCityList.add(city);
            }
        }
        return tempCityList;
    }

    @Override
    public boolean isSearchRequired() {
        return true;
    }

    @Override
    public String getPrimaryData(String item) {
        return item;
    }

    @Override
    public String getFirstLetter(String item) {
        if (item == null) {
            return "";
        }
        return item.substring(0, 1);
    }

    @Override
    public String getNavigationTitle() {
        return getString(R.string.select_city);
    }

    @Override
    public List<String> getChildItemSource() {
        return childItemSource;
    }

    @Override
    public int getChildTemplateId() {
        return R.layout.primary_content_and_label_row;
    }

    @Override
    public int getHeaderTemplateId() {
        return R.layout.section_view;
    }

    @Override
    public boolean isSecondaryLabelRequired() {
        return false;
    }
}
