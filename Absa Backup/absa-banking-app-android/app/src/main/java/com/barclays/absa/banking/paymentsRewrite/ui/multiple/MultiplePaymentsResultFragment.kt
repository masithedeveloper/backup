/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */
package com.barclays.absa.banking.paymentsRewrite.ui.multiple

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.LineItemMultiplePaymentSuccessBinding
import com.barclays.absa.banking.databinding.MultiplePaymentsResultFragmentBinding
import com.barclays.absa.banking.express.payments.payMultipleBeneficiary.dto.PayMultipleBeneficiaryResponse
import com.barclays.absa.banking.express.payments.payMultipleBeneficiary.dto.PaymentResponse
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.utils.DateTimeHelper.SPACED_PATTERN_DD_MMM_YYYY
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.utils.extensions.toRandAmount

class MultiplePaymentsResultFragment : MultiplePaymentsBaseFragment(R.layout.multiple_payments_result_fragment) {
    private val binding by viewBinding(MultiplePaymentsResultFragmentBinding::bind)

    private lateinit var paymentMultipleBeneficiaryResponse: PayMultipleBeneficiaryResponse

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideToolBar()
        payMultipleBeneficiaryViewModel.payMultipleBeneficiaryResponse.value?.let {
            paymentMultipleBeneficiaryResponse = it
        }
        binding.root.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        setUpViews()
        setUpClickListeners()
    }

    private fun setUpViews() {
        binding.contactView.setContact(getString(R.string.contact_support_title), getString(R.string.support_contact_number))

        if (paymentsViewModel.dualAuthorisationRequired) {
            binding.noticeMessageTextView.text = getString(R.string.auth_title_payments_pending, getString(R.string.payment))
            binding.subMessageTextView.visibility = View.GONE
        }

        val date = DateUtils.getTodaysDate(SPACED_PATTERN_DD_MMM_YYYY)
        val time = DateUtils.getCurrentTime()
        binding.subMessageTextView.text = getString(R.string.payment_successful_on, date, time)

        val normalPaymentsList = multiplePaymentsViewModel.filterNormalPaymentsResponseList(paymentMultipleBeneficiaryResponse.successResponseList)
        val immediatePaymentsList = multiplePaymentsViewModel.filterImmediatePaymentsResponseList(paymentMultipleBeneficiaryResponse.successResponseList)
        val futureDatedPaymentsList = multiplePaymentsViewModel.filterFuturePaymentsResponseList(paymentMultipleBeneficiaryResponse.successResponseList)

        populateSuccessNormalPayments(normalPaymentsList)
        populateSuccessfulImmediatePayments(immediatePaymentsList)
        populateSuccessfulFutureDatedPayments(futureDatedPaymentsList)

        if (paymentMultipleBeneficiaryResponse.failureResponseList.isNotEmpty()) {
            binding.noticeMessageTextView.setText(R.string.payment_failure_title)
            binding.resultLottieAnimationView.setAnimation(ResultAnimations.generalFailure)
            binding.unsuccessfulNormalPaymentsTextView.visibility = View.VISIBLE
            binding.unsuccessfulNormalPaymentsLinearLayout.visibility = View.VISIBLE
        }

        val unsuccessfulNormalPaymentsList = multiplePaymentsViewModel.filterNormalPaymentsResponseList(paymentMultipleBeneficiaryResponse.failureResponseList)
        val unsuccessfulImmediatePaymentsList = multiplePaymentsViewModel.filterImmediatePaymentsResponseList(paymentMultipleBeneficiaryResponse.failureResponseList)
        val unsuccessfulFutureDatedPaymentsList = multiplePaymentsViewModel.filterFuturePaymentsResponseList(paymentMultipleBeneficiaryResponse.failureResponseList)

        populateUnsuccessfulPayments(unsuccessfulNormalPaymentsList)
        populateUnsuccessfulImmediatePayments(unsuccessfulImmediatePaymentsList)
        populateUnsuccessfulFutureDatedPayments(unsuccessfulFutureDatedPaymentsList)
    }

    private fun setUpClickListeners() {
        binding.importantNoticeOptionActionButtonView.setOnClickListener {
            navigate(MultiplePaymentsResultFragmentDirections.actionMultiplePaymentsResultFragmentToImportantNoticeFragment())
        }

        binding.doneButton.setOnClickListener {
            loadAccountsAndGoHome()
        }
    }

    private fun populateSuccessNormalPayments(normalPaymentsList: List<PaymentResponse>) {
        if (normalPaymentsList.isNotEmpty()) {
            addAndBindView(normalPaymentsList, binding.normalPaymentsLinearLayout)
        } else {
            binding.normalPaymentsTextView.visibility = View.GONE
            binding.normalPaymentsLinearLayout.visibility = View.GONE
            binding.normalPaymentsDividerView.visibility = View.GONE
        }
    }

    private fun populateSuccessfulImmediatePayments(immediatePaymentsList: List<PaymentResponse>) {
        if (immediatePaymentsList.isNotEmpty()) {
            addAndBindView(immediatePaymentsList, binding.immediatePaymentsLinearLayout)
        } else {
            binding.immediatePaymentsTextView.visibility = View.GONE
            binding.immediatePaymentsLinearLayout.visibility = View.GONE
            binding.immediatePaymentsDividerView.visibility = View.GONE
        }
    }

    private fun populateSuccessfulFutureDatedPayments(futureDatedPaymentsList: List<PaymentResponse>) {
        if (futureDatedPaymentsList.isNotEmpty()) {
            addAndBindView(futureDatedPaymentsList, binding.futureDatedPaymentsLinearLayout)
        } else {
            binding.futureDatedPaymentTextView.visibility = View.GONE
            binding.futureDatedPaymentsLinearLayout.visibility = View.GONE
            binding.futureDatedPaymentDividerView.visibility = View.GONE
        }
    }

    private fun populateUnsuccessfulPayments(unsuccessfulNormalPaymentsList: List<PaymentResponse>) {
        if (unsuccessfulNormalPaymentsList.isNotEmpty()) {
            addAndBindView(unsuccessfulNormalPaymentsList, binding.unsuccessfulNormalPaymentsLinearLayout)
        } else {
            binding.unsuccessfulNormalPaymentsTextView.visibility = View.GONE
            binding.unsuccessfulNormalPaymentsLinearLayout.visibility = View.GONE
            binding.unsuccessfulNormalPaymentsDividerView.visibility = View.GONE
        }
    }

    private fun populateUnsuccessfulImmediatePayments(unsuccessfulImmediatePaymentsList: List<PaymentResponse>) {
        if (unsuccessfulImmediatePaymentsList.isNotEmpty()) {
            addAndBindView(unsuccessfulImmediatePaymentsList, binding.unsuccessfulImmediatePaymentsLinearLayout)
        } else {
            binding.unsuccessfulImmediatePaymentsTextView.visibility = View.GONE
            binding.unsuccessfulImmediatePaymentsLinearLayout.visibility = View.GONE
            binding.unsuccessfulImmediatePaymentsDividerView.visibility = View.GONE
        }
    }

    private fun populateUnsuccessfulFutureDatedPayments(unsuccessfulFutureDatedPaymentsList: List<PaymentResponse>) {
        if (unsuccessfulFutureDatedPaymentsList.isNotEmpty()) {
            addAndBindView(unsuccessfulFutureDatedPaymentsList, binding.unsuccessfulFutureDatedPaymentsLinearLayout)
        } else {
            binding.unsuccessfulFutureDatedPaymentsTextView.visibility = View.GONE
            binding.unsuccessfulFutureDatedPaymentsLinearLayout.visibility = View.GONE
            binding.unsuccessfulFutureDatedPaymentsDividerView.visibility = View.GONE
        }
    }

    private fun addAndBindView(paymentList: List<PaymentResponse>, linearLayout: LinearLayout) {
        paymentList.forEach { payMultipleBeneficiary ->
            multiplePaymentsViewModel.validateMultiplePaymentsRequestList.find { it.paymentSequenceNumber == payMultipleBeneficiary.paymentSequenceNumber }?.let {
                linearLayout.addView(inflateView(it.beneficiaryName, it.paymentAmount.toRandAmount()))
            }
        }
    }

    private fun inflateView(beneficiaryName: String, paymentAmount: String) = LineItemMultiplePaymentSuccessBinding.inflate(LayoutInflater.from(context), binding.contentConstraintLayout, false).apply {
        lineItemView.setLineItemViewLabel(beneficiaryName)
        lineItemView.setLineItemViewContent(paymentAmount)
    }.root

    fun onBackPressed() {}
}