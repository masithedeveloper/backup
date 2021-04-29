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
package com.barclays.absa.banking.dualAuthorisations.ui;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AccessPrivileges;
import com.barclays.absa.banking.boundary.model.authorisations.AuthorisationTransactionDetails;
import com.barclays.absa.banking.databinding.AuthorisationTransferDetailsActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;

import static com.barclays.absa.banking.dualAuthorisations.ui.DualAuthTransactionDetailsCashSendActivity.TRANSACTION_DETAILS;

public class DualAuthTransactionDetailsTransferActivity extends BaseActivity {

    private DualAuthorisationHandler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AuthorisationTransferDetailsActivityBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.authorisation_transfer_details_activity, null, false);
        setContentView(binding.getRoot());

        AuthorisationTransactionDetails transactionDetails = (AuthorisationTransactionDetails) getIntent().getSerializableExtra(TRANSACTION_DETAILS);
        handler = new DualAuthorisationHandler(this, transactionDetails);
        setToolBarBack(R.string.authorisation_transfer_title);
        binding.typeContentView.setContentText(transactionDetails.getTransactionType());
        binding.amountContentView.setContentText(transactionDetails.getTransactionAmount().toString());
        binding.fromAccountContentView.setContentText(handler.getFromAccountTypeAndNumber());
        binding.toAccountContentView.setContentText(handler.getToAccountTypeAndNumber());
        binding.initiatedByContentView.setContentText(transactionDetails.getOperatorName());
        binding.initiatedOnContentView.setContentText(handler.getDebitDate());

        binding.rejectButton.setOnClickListener(v -> handler.onRejectionClick(v));
        binding.approveButton.setOnClickListener(v -> handler.onAuthoriseClick(v));
        binding.cancelButton.setOnClickListener(v -> handler.onCancelClick(v));

        if (AccessPrivileges.getInstance().isOperator()) {
            binding.cancelButton.setVisibility(View.VISIBLE);
            binding.rejectButton.setVisibility(View.GONE);
            binding.approveButton.setVisibility(View.GONE);
        }

    }

    @Override
    public void onBackPressed() {
        handler.onBackPressed();
    }
}