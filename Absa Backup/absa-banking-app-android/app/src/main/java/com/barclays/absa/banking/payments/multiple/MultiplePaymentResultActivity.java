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
package com.barclays.absa.banking.payments.multiple;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.LineItemMultiplePaymentSuccessBinding;
import com.barclays.absa.banking.databinding.MultiplePaymentsSuccessActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.payments.services.multiple.MultipleBeneficiaryPaymentService;
import com.barclays.absa.banking.payments.services.multiple.dto.PaymentDetailResult;
import com.barclays.absa.banking.paymentsRewrite.ui.ImportantNoticeFragment;
import com.barclays.absa.utils.DateUtils;

import java.util.List;

@Deprecated
public class MultiplePaymentResultActivity extends BaseActivity implements MultiplePaymentsResultView {

    static final String DATE_FORMAT = "dd MMM yyyy";
    public static final String HAS_IMMEDIATE_PAYMENT = "hasImmediatePayment";
    private boolean hasImmediatePayment;
    private MultiplePaymentsSuccessActivityBinding binding;
    private MultiplePaymentsResultPresenter presenter;
    private List<PaymentDetailResult> paymentDetailResultsList;
    private boolean isAuthorisationOutstanding;
    public static final String BENEFICIARY_LIST = "beneficairyList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.multiple_payments_success_activity, null, false);
        setContentView(binding.getRoot());
        binding.getRoot().setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setUpViews();
    }

    @SuppressWarnings("unchecked")
    private void setUpViews() {
        String CONTACT_SUPPORT = "08600 08600";
        binding.contactView.setContact(getString(R.string.contact_support_title), CONTACT_SUPPORT);
        presenter = new MultiplePaymentsResultPresenter(this);
        if (getIntent().getExtras() != null) {
            hasImmediatePayment = getIntent().getExtras().getBoolean(HAS_IMMEDIATE_PAYMENT, false);
            paymentDetailResultsList = (List<PaymentDetailResult>) getIntent().getSerializableExtra(BENEFICIARY_LIST);
            isAuthorisationOutstanding = getIntent().getBooleanExtra(AUTHORISATION_OUTSTANDING_TRANSACTION, false);
        }
        if (isAuthorisationOutstanding) {
            binding.noticeMessageTextView.setText(getString(R.string.auth_title_payments_pending, getString(R.string.payment)));
            binding.subMessageTextView.setVisibility(View.GONE);
        }

        if (paymentDetailResultsList != null && !paymentDetailResultsList.isEmpty()) {
            List<PaymentDetailResult> successPaymentList = presenter.filterBySuccessfulPayments(paymentDetailResultsList);
            if (!successPaymentList.isEmpty()) {
                binding.successfulPaymentsView.setVisibility(View.VISIBLE);
                binding.successfulPaymentTextView.setVisibility(View.VISIBLE);
            }
            List<PaymentDetailResult> normalAndIipPayments = presenter.getFilteredBeneficiaryPaymentList(successPaymentList, NOW);
            List<PaymentDetailResult> futureDatedPayments = presenter.getFilteredBeneficiaryPaymentList(successPaymentList, OWN);
            List<PaymentDetailResult> failurePaymentList = presenter.filterByFailedPayments(paymentDetailResultsList);
            if (!failurePaymentList.isEmpty()) {
                binding.noticeMessageTextView.setText(R.string.payment_failure_title);
                binding.resultImageView.setAnimation("general_failure.json");
                binding.unsuccessfulPaymentTextView.setVisibility(View.VISIBLE);
                binding.unsuccessfulPaymentsView.setVisibility(View.VISIBLE);
            }
            List<PaymentDetailResult> unsuccessPaymentsList = presenter.getFilteredBeneficiaryPaymentList(failurePaymentList, NOW);
            List<PaymentDetailResult> unsuccessFutureDatedPaymentsList = presenter.getFilteredBeneficiaryPaymentList(failurePaymentList, OWN);
            String date = DateUtils.getTodaysDate(DATE_FORMAT);
            String time = DateUtils.getCurrentTime();
            binding.subMessageTextView.setText(getString(R.string.payment_successful_on, date, time));

            populateSuccessPayments(presenter.getFilteredBeneficiaryPaymentList(successPaymentList, NOW));
            populateSuccessFutureDatedPayments(futureDatedPayments);
            populateUnsuccessfulPayments(unsuccessPaymentsList);
            populateUnsuccessFutureDatedPayments(unsuccessFutureDatedPaymentsList);
        }
        binding.importantNoticeView.setOnClickListener(view -> presenter.onImportantNoticeClicked());
        binding.doneButton.setOnClickListener(view -> presenter.onHomeButtonClicked());
    }

    private void populateSuccessPayments(List<PaymentDetailResult> normalAndIipPayments) {
        for (PaymentDetailResult paymentDetailResult : normalAndIipPayments) {
            trackCustomScreenView("Normal payment successful", MultipleBeneficiaryPaymentService.ANALYTICS_CHANNEL_NAME, BMBConstants.TRUE_CONST);
            binding.successfulPaymentsView.addView(bindViews(paymentDetailResult.getBeneficiaryName(), paymentDetailResult.getTransactionAmount().toString()).getRoot());
        }
    }

    private void populateSuccessFutureDatedPayments(List<PaymentDetailResult> paymentDetailResults) {
        if (paymentDetailResults.size() > 0) {
            for (int i = 0; i < paymentDetailResults.size(); i++) {
                binding.futureDatedSuccessfulPaymentsView.addView(bindViews(paymentDetailResults.get(i).getBeneficiaryName(), paymentDetailResults.get(i).getTransactionAmount().toString()).getRoot());
            }
        } else {
            binding.futureDatedSuccessfulPaymentTextView.setVisibility(View.GONE);
            binding.futureDatedSuccessfulPaymentsView.setVisibility(View.GONE);
            binding.divider1.setVisibility(View.GONE);
        }
    }

    private void populateUnsuccessfulPayments(List<PaymentDetailResult> unsuccessPaymentsList) {
        if (unsuccessPaymentsList.size() > 0) {
            for (int i = 0; i < unsuccessPaymentsList.size(); i++) {
                binding.unsuccessfulPaymentsView.addView(bindViews(unsuccessPaymentsList.get(i).getBeneficiaryName(), unsuccessPaymentsList.get(i).getTransactionAmount().toString()).getRoot());
            }
        } else {
            binding.unsuccessfulPaymentTextView.setVisibility(View.GONE);
            binding.unsuccessfulPaymentsView.setVisibility(View.GONE);
            binding.divider2.setVisibility(View.GONE);
        }
    }

    private void populateUnsuccessFutureDatedPayments(List<PaymentDetailResult> unsuccessFutureDatedPaymentsList) {
        if (unsuccessFutureDatedPaymentsList.size() > 0) {
            for (int i = 0; i < unsuccessFutureDatedPaymentsList.size(); i++) {
                binding.unsuccessfulFutureDatedPaymentsView.addView(bindViews(unsuccessFutureDatedPaymentsList.get(i).getBeneficiaryName(), unsuccessFutureDatedPaymentsList.get(i).getTransactionAmount().toString()).getRoot());
            }
        } else {
            binding.unsuccessfulFutureDatedPaymentTextView.setVisibility(View.GONE);
            binding.unsuccessfulFutureDatedPaymentsView.setVisibility(View.GONE);
            binding.divider3.setVisibility(View.GONE);
        }
    }

    private LineItemMultiplePaymentSuccessBinding bindViews(String name, String amount) {
        LineItemMultiplePaymentSuccessBinding successRowBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.line_item_multiple_payment_success, binding.successfulPaymentsView, false);
        successRowBinding.lineItemView.setLineItemViewLabel(name);
        successRowBinding.lineItemView.setLineItemViewContent(amount);
        return successRowBinding;
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void navigateToImportantNoticeScreen() {
        Intent importantNoticeIntent = new Intent(this, ImportantNoticeFragment.class);
        importantNoticeIntent.putExtra(IIP_CONST, hasImmediatePayment);
        startActivity(importantNoticeIntent);
    }
}