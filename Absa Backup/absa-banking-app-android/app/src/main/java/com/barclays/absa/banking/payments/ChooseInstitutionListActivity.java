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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.Institution;
import com.barclays.absa.banking.boundary.model.Institutions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChooseInstitutionListActivity extends BaseExpandableListView<Institution> {

    private final String BENEFICIARY_NAME = "benName";
    private final String INSTITUTION_CODE = "institutionCode";
    private final String ACCOUNT_NUMBER = "accountNo";
    private List<Institution> childItemSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void getIntentExtraData(BaseExpandableListView activity) {
        ChooseInstitutionListActivity chooseInstitutionListActivity = (ChooseInstitutionListActivity) activity;
        Institutions institutions = (Institutions) chooseInstitutionListActivity.getIntent().getSerializableExtra(RESULT);
        if (institutions != null) {
            childItemSource = institutions.getList();
            if (childItemSource != null && childItemSource.size() > 0) {
                AsyncTask.execute(() -> Collections.sort(childItemSource, (lhs, rhs) -> {
                    if (lhs.getBeneficiaryName() != null && rhs != null && rhs.getBeneficiaryName() != null) {
                        return lhs.getBeneficiaryName().compareToIgnoreCase(rhs.getBeneficiaryName());
                    }
                    return 0;
                }));
            }
        }
    }

    @Override
    public void onItemClick(Institution item) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(BENEFICIARY_NAME, item.getBeneficiaryName());
        returnIntent.putExtra(INSTITUTION_CODE, item.getInstitutionCode());
        returnIntent.putExtra(ACCOUNT_NUMBER, item.getAccountNumber());
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public List<Institution> onListItemSearch(String searchString) {
        List<Institution> tempInstitutions = new ArrayList<>();
        for (Institution institution : childItemSource) {
            Runnable filterRunnable = () -> {
                if (institution.getBeneficiaryName() != null && institution.getBeneficiaryName().toLowerCase().contains(searchString.toLowerCase()) ||
                        institution.getInstitutionCode() != null && institution.getInstitutionCode().toLowerCase().contains(searchString.toLowerCase())) {
                    tempInstitutions.add(institution);
                }
            };
            AsyncTask.execute(filterRunnable);
        }
        return tempInstitutions;
    }

    @Override
    public List<Institution> getChildItemSource() {
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
    public String getNavigationTitle() {
        return getString(R.string.sel_an_institution);
    }

    @Override
    public String getFirstLetter(Institution institution) {
        if (institution == null) {
            return null;
        }
        if (institution.getBeneficiaryName() == null) {
            return null;
        }
        return institution.getBeneficiaryName().substring(0, 1);
    }

    @Override
    public String getPrimaryData(Institution institution) {
        if (institution == null) {
            return null;
        }
        return institution.getBeneficiaryName();
    }

    @Override
    public String getSecondaryData(Institution institution) {
        if (institution == null) {
            return null;
        }
        return institution.getInstitutionCode();
    }

    @Override
    public boolean isSecondaryLabelRequired() {
        return true;
    }
}
