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

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.payments.BaseExpandableListView;
import com.barclays.absa.banking.payments.international.data.InternationalCountryList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.COUNTRY_CODE;
import static com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.COUNTRY_DESCRIPTION;
import static com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.INTERNATIONAL_COUNTRY_LIST;
import static com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.SHOULD_DISPLAY_SECURITY_QUESTION;

public class InternationalPaymentsCountrySelectorActivity extends BaseExpandableListView<InternationalCountryList> {
    private ArrayList<InternationalCountryList> childItemSource;

    @SuppressWarnings("unchecked")
    @Override
    public void getIntentExtraData(BaseExpandableListView baseExpandableListView) {
        InternationalPaymentsCountrySelectorActivity chooseCountryActivity = (InternationalPaymentsCountrySelectorActivity) baseExpandableListView;
        ArrayList<InternationalCountryList> bankBranches = (ArrayList<InternationalCountryList>) chooseCountryActivity.getIntent().getSerializableExtra(INTERNATIONAL_COUNTRY_LIST);
        if (bankBranches != null) {
            childItemSource = bankBranches;
            if (childItemSource.size() > 0) {
                Collections.sort(childItemSource, (leftHandSide, rightHandSide) -> {
                    if (leftHandSide.getCountryDescription() != null && rightHandSide.getCountryDescription() != null) {
                        return leftHandSide.getCountryDescription().compareToIgnoreCase(rightHandSide.getCountryDescription());
                    }
                    return 0;
                });
            }
        }
    }

    @Override
    public void onItemClick(InternationalCountryList item) {
        Intent intent = new Intent();
        intent.putExtra(COUNTRY_DESCRIPTION, item.getCountryDescription());
        intent.putExtra(COUNTRY_CODE, item.getCountryCode());
        intent.putExtra(SHOULD_DISPLAY_SECURITY_QUESTION, item.getRequiresSecurityQuestion());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public List<InternationalCountryList> onListItemSearch(String searchString) {
        List<InternationalCountryList> tempCountryList = new ArrayList<>();
        for (InternationalCountryList country : childItemSource) {
            if (country.getCountryDescription() != null && country.getCountryDescription().toLowerCase().contains(searchString.toLowerCase()) ||
                    country.getCountryDescription() != null && country.getCountryDescription().toLowerCase().contains(searchString.toLowerCase())) {
                tempCountryList.add(country);
            }
        }
        return tempCountryList;
    }

    @Override
    public boolean isSearchRequired() {
        return true;
    }

    @Override
    public String getPrimaryData(InternationalCountryList item) {
        return item.getCountryDescription();
    }

    @Override
    public String getFirstLetter(InternationalCountryList item) {
        if (item == null) {
            return "";
        }
        if (item.getCountryDescription() == null) {
            return null;
        }
        return item.getCountryDescription().substring(0, 1);
    }

    @Override
    public String getNavigationTitle() {
        return getString(R.string.select_country);
    }

    @Override
    public List<InternationalCountryList> getChildItemSource() {
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
