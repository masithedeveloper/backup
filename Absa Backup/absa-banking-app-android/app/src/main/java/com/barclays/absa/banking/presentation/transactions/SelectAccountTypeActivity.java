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
package com.barclays.absa.banking.presentation.transactions;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ListView;

import androidx.appcompat.widget.Toolbar;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccountType;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.presentation.adapters.AccountTypeAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.barclays.absa.banking.payments.PaymentsConstants.ACCOUNT_TYPE;

public class SelectAccountTypeActivity extends BaseActivity {
    private Toolbar toolbar;
    private ListView listView;
    private List<AccountType> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_account_type);

        initialiseUIElements();
        setToolBarBack(getString(R.string.select_account_type));
        data = getData();
        AccountTypeAdapter adapter = new AccountTypeAdapter(this, data);
        listView.setAdapter(adapter);
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.SELECT_TO_ACCOUNT_TYPE_CONST, BMBConstants.PAYMENTS_CONST, BMBConstants.TRUE_CONST);
        setListeners();
    }

    private void setListeners() {

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(ACCOUNT_TYPE, data.get(position));
            setResult(RESULT_OK, returnIntent);
            finish();
        });

        toolbar.setNavigationOnClickListener(v -> {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        });
    }

    private void initialiseUIElements() {
        toolbar = findViewById(R.id.toolbar);
        listView = findViewById(R.id.rv);
    }

    public List<AccountType> getData() {
        List<AccountType> data = new ArrayList<>();

        String[] titles = {getString(R.string.chequeAccount), getString(R.string.savingAccount)};
        int index = 0;
        for (String title : titles) {
            AccountType current = new AccountType();
            current.setName(title);
            current.setBackendAccountType(getEnglishVersionOfAccountType(index));
            data.add(current);
            index++;
        }
        return data;
    }

    private String getEnglishVersionOfAccountType(int accountTypeIndex) {
        Configuration conf = getResources().getConfiguration();
        conf.locale = new Locale("en");
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Resources resources = new Resources(getAssets(), metrics, conf);
        int[] accountTypes = {R.string.chequeAccount, R.string.savingAccount};
        int stringResourceToUse = accountTypes[accountTypeIndex];
        conf.setLocale(BMBApplication.getApplicationLocale());
        return resources.getString(stringResourceToUse);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }
}
