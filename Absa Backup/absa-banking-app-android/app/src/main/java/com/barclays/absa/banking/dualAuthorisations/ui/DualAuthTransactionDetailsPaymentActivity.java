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
import com.barclays.absa.banking.databinding.AuthorisationPaymentDetailsActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.utils.extensions.AccountUtils;

import styleguide.utils.extensions.StringExtensions;

import static com.barclays.absa.banking.dualAuthorisations.ui.DualAuthTransactionDetailsCashSendActivity.TRANSACTION_DETAILS;

public class DualAuthTransactionDetailsPaymentActivity extends BaseActivity {

    private DualAuthorisationHandler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AuthorisationPaymentDetailsActivityBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.authorisation_payment_details_activity, null, false);
        setContentView(binding.getRoot());

        AuthorisationTransactionDetails transactionDetails = (AuthorisationTransactionDetails) getIntent().getSerializableExtra(TRANSACTION_DETAILS);
        handler = new DualAuthorisationHandler(this, transactionDetails);
        setToolBarBack(getString(R.string.transaction_overview));
        binding.beneficiaryNameContentView.setContentText(transactionDetails.getBeneficiaryName());
        binding.accountNumberContentView.setContentText(StringExtensions.toFormattedAccountNumber(transactionDetails.getBeneficiaryAccount()));
        binding.amountContentView.setContentText(transactionDetails.getTransactionAmount().toString());
        String accountDescription = AccountUtils.getAccountDisplayDescriptionWithFormattedAccountNumber(transactionDetails.getFromAccount());
        binding.fromAccountContentView.setContentText(accountDescription);
        binding.theirReferenceContentView.setContentText(transactionDetails.getBeneficiaryReference());
        binding.myReferenceContentView.setContentText(transactionDetails.getMyReference());
        binding.beneficiaryNotificationContentView.setContentText(handler.beneficiaryNoticeOfPayment());
        binding.myNotificationContentView.setContentText(handler.myNoticeOfPayment());
        binding.transactionTypeView.setContentText(transactionDetails.getTransactionType());
        binding.branchCodeView.setContentText(transactionDetails.getBankCode());
        binding.iipReferenceContentView.setVisibility(handler.setIIPReferenceVisibility());
        binding.iipReferenceContentView.setContentText(transactionDetails.getIipReference());
        binding.initiatedByContentView.setContentText(transactionDetails.getOperatorName());
        binding.initiatedOnContentView.setContentText(handler.getDebitDate());

        if (AccessPrivileges.getInstance().isOperator()) {
            binding.cancelButton.setVisibility(View.VISIBLE);
            binding.rejectButton.setVisibility(View.GONE);
            binding.approveButton.setVisibility(View.GONE);
        }

        binding.rejectButton.setOnClickListener(v -> handler.onRejectionClick(v));
        binding.approveButton.setOnClickListener(v -> handler.onConfirmationClick(v));
        binding.cancelButton.setOnClickListener(v -> handler.onCancelClick(v));
    }

    @Override
    public void onBackPressed() {
        handler.onBackPressed();
    }
}