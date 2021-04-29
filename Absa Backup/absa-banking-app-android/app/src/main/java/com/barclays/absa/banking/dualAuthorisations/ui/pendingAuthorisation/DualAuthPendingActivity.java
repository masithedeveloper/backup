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
package com.barclays.absa.banking.dualAuthorisations.ui.pendingAuthorisation;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.ActivityDualAuthTransactionPendingBinding;
import com.barclays.absa.banking.framework.BaseActivity;

public abstract class DualAuthPendingActivity extends BaseActivity {

    protected ActivityDualAuthTransactionPendingBinding view;

    abstract String getAuthTitle();

    abstract String getAuthExpiryMessage();

    abstract String getAuthContactMessage();

    abstract String getAuthPrimaryButtonText();

    abstract void onPrimaryButtonClicked();

    String transactionType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_dual_auth_transaction_pending, null, false);
        setContentView(view.getRoot());
        view.getRoot().setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(TRANSACTION_TYPE)) {
            transactionType = getIntent().getStringExtra(TRANSACTION_TYPE);
        }
        view.tvAuthTitle.setText(getAuthTitle());
        view.tvAuthExpiryMessage.setText(getAuthExpiryMessage());
        view.tvAuthContactMessage.setText(getAuthContactMessage());
        view.btnAuthPrimary.setText(getAuthPrimaryButtonText());
        view.btnAuthPrimary.setOnClickListener(v -> onPrimaryButtonClicked());
        view.btnHomeOption.setOnClickListener(v -> loadAccountsAndGoHome());
    }

    @Override
    public void onBackPressed() {
    }

}
