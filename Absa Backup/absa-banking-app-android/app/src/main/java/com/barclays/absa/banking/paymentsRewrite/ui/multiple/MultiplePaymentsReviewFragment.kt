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

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.MultiplePaymentsReviewFragmentBinding
import com.barclays.absa.banking.paymentsRewrite.ui.multiple.dto.ValidationAlert
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.sureCheck.ExpressSureCheckHandler
import com.barclays.absa.banking.sureCheck.ProcessSureCheck
import com.barclays.absa.utils.AnimationHelper
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.utils.extensions.toFormattedAccountNumber
import styleguide.utils.extensions.toRandAmount
import za.co.absa.networking.hmac.service.ExpressBaseRepository

class MultiplePaymentsReviewFragment : MultiplePaymentsBaseFragment(R.layout.multiple_payments_review_fragment) {

    private var currentAlertCount: Int = 0
    private val binding by viewBinding(MultiplePaymentsReviewFragmentBinding::bind)
    private lateinit var validationAlertList: List<ValidationAlert>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        validationAlertList = multiplePaymentsViewModel.createValidationAlertList(validateMultiplePaymentViewModel.validateMultiplePaymentResponse.value?.validateMultiplePaymentResponseList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupClickListeners()
    }

    private fun setupSureCheckHandler() {
        ExpressSureCheckHandler.processSureCheck = object : ProcessSureCheck {
            override fun onSureCheckProcessed() {
                performPayment()
            }

            override fun onSureCheckFailed() {
                startActivity(IntentFactory.getPaymentTransactionFailedResultScreen(baseActivity))
            }
        }
    }

    override fun performPayment() {
        super.performPayment()

        setupSureCheckHandler()

        payMultipleBeneficiaryViewModel.payMultipleBeneficiaryResponse.observe(viewLifecycleOwner, {
            dismissProgressDialog()

            if (it.header.statuscode == ExpressBaseRepository.AUTHORISATION_RESPONSE_CODE) {
                paymentsViewModel.dualAuthorisationRequired = true
            }
            // TODO : Handle new Response Codes
            navigate(MultiplePaymentsReviewFragmentDirections.actionMultiplePaymentsReviewFragmentToMultiplePaymentsResultFragment())
        })
    }

    private fun setupViews() {
        currentAlertCount = 0
        validationAlertList.forEach { validationAlerts ->
            if (!validationAlerts.isResolved) {
                val multiplePaymentsAlertItemView = MultiplePaymentsAlertItemView(requireContext()).apply {
                    setBeneficiaryTitle(validationAlerts.beneficiaryName)
                    setAmountTitle(validationAlerts.amount.toRandAmount())
                    setOnClickListener {
                        beneficiaryAlertClicked(validationAlerts.paymentSequenceNumber)
                    }

                    if (validationAlerts.isRemoved) {
                        setAmountDescription(getString(R.string.payments_transaction_removed))
                        disableView()
                    } else {
                        currentAlertCount++
                    }
                }

                binding.alertsLinearLayout.addView(multiplePaymentsAlertItemView)
            }
        }

        if (currentAlertCount > 0) {
            binding.alertTitleSecondaryContentAndLabelView.setContentText(getString(R.string.payments_number_of_alerts, currentAlertCount))
            binding.alertTitleSecondaryContentAndLabelView.labelTextView.visibility = View.VISIBLE
            binding.alertsLinearLayout.visibility = View.VISIBLE
        } else {
            binding.alertTitleSecondaryContentAndLabelView.setContentText(getString(R.string.payments_all_alerts_reviewed))
            binding.alertTitleSecondaryContentAndLabelView.labelTextView.visibility = View.GONE
            if (validationAlertList.isEmpty() || validationAlertList.none { it.isRemoved }) {
                binding.alertsLinearLayout.visibility = View.GONE
            }
        }

        binding.totalAmountTitleAndDescriptionView.title = multiplePaymentsViewModel.totalPaymentAmount.toRandAmount()
        binding.fromAccountSecondaryContentAndLabelView.setContentText(paymentsViewModel.selectedSourceAccount.accountNumber.toFormattedAccountNumber())

        val normalPaymentsList = multiplePaymentsViewModel.getNormalPaymentsList()
        val immediatePaymentsList = multiplePaymentsViewModel.getImmediatePaymentsList()
        val futurePaymentsList = multiplePaymentsViewModel.getFuturePaymentsList()

        if (normalPaymentsList.isNotEmpty() && multiplePaymentsViewModel.isValidPaymentList(normalPaymentsList)) {
            setUpPaymentLinearLayout(binding.normalPaymentsLinearLayout, normalPaymentsList)
        } else {
            binding.normalPaymentsGroup.visibility = View.GONE
        }

        if (immediatePaymentsList.isNotEmpty() && multiplePaymentsViewModel.isValidPaymentList(immediatePaymentsList)) {
            setUpPaymentLinearLayout(binding.immediatePaymentsLinearLayout, immediatePaymentsList)
        } else {
            binding.immediatePaymentsGroup.visibility = View.GONE
        }

        if (futurePaymentsList.isNotEmpty() && multiplePaymentsViewModel.isValidPaymentList(futurePaymentsList)) {
            setUpPaymentLinearLayout(binding.futureDatedPaymentsLinearLayout, futurePaymentsList)
        } else {
            binding.futureDatedPaymentsGroup.visibility = View.GONE
        }
    }

    private fun setUpPaymentLinearLayout(linearLayout: LinearLayout, paymentList: List<MultiplePaymentBeneficiaryWrapper>) {
        paymentList.forEach {
            val validationAlertForBeneficiary = multiplePaymentsViewModel.getValidationAlertForBeneficiary(it)

            if (validationAlertForBeneficiary == null || validationAlertForBeneficiary.isResolved) {
                val multiplePaymentsAlertItemView = MultiplePaymentsAlertItemView(requireContext()).apply {
                    setBeneficiaryTitle(it.regularBeneficiary.beneficiaryDetails.beneficiaryName)
                    setAmountTitle(it.paymentAmount.toRandAmount())

                    if (validationAlertForBeneficiary?.isResolved == true) {
                        setAmountDescription(getString(R.string.payments_alert_reviewed))
                        setArrowIcon(R.drawable.ic_arrow_next)

                        setOnClickListener {
                            beneficiaryAlertClicked(validationAlertForBeneficiary.paymentSequenceNumber)
                        }
                    } else {
                        hideArrow()
                        setAmountDescription(getString(R.string.payments_no_alert))
                    }

                    setTitleTextColor(R.color.multiple_payment_alert_title_color)
                    setDescriptionTextColor(R.color.multiple_payment_alert_description_color)
                    resetBorder()
                }

                linearLayout.addView(multiplePaymentsAlertItemView)
            }
        }
    }

    private fun beneficiaryAlertClicked(paymentSequenceNumber: Int) {
        navigate(MultiplePaymentsReviewFragmentDirections.actionMultiplePaymentsReviewFragmentToMultiplePaymentsReviewAlertFragment(paymentSequenceNumber))
    }

    private fun setupClickListeners() {
        binding.cancelButton.setOnClickListener {
            activity?.finish()
        }

        binding.continueButton.setOnClickListener {
            if (currentAlertCount > 0) {
                binding.scrollView.post { binding.scrollView.scrollTo(0, 0) }
                AnimationHelper.shakeShakeAnimate(binding.alertTitleSecondaryContentAndLabelView)
            } else {
                performPayment()
            }
        }
    }
}