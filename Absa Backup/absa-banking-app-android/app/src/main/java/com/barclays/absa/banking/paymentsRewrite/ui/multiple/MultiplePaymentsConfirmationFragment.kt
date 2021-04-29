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

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.MultiplePaymentsConfirmationFragmentBinding
import com.barclays.absa.banking.databinding.WidgetMultiplePaymentOverviewBinding
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.presentation.shared.IntentFactoryGenericResult
import com.barclays.absa.banking.sureCheck.ExpressSureCheckHandler
import com.barclays.absa.banking.sureCheck.ProcessSureCheck
import com.barclays.absa.utils.AbsaCacheManager
import com.barclays.absa.utils.AccessibilityUtils
import com.barclays.absa.utils.DateTimeHelper.SPACED_PATTERN_DD_MMM_YYYY
import com.barclays.absa.utils.DateTimeHelper.toFormattedString
import com.barclays.absa.utils.TextFormatUtils.formatAccountNumberAndDescription
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toFormattedAccountNumber
import styleguide.utils.extensions.toRandAmount
import za.co.absa.networking.hmac.service.ExpressBaseRepository.Companion.AUTHORISATION_RESPONSE_CODE
import java.util.*

class MultiplePaymentsConfirmationFragment : MultiplePaymentsBaseFragment(R.layout.multiple_payments_confirmation_fragment) {

    private val binding by viewBinding(MultiplePaymentsConfirmationFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.confirm_payment))
        setupViews()
        setupTalkBack()
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

    private fun setupTalkBack() {
        with(binding) {
            val totalAmountPayable = AccessibilityUtils.getTalkBackRandValueFromTextView(totalAmountTitleAndDescriptionView.titleTextView)
            totalAmountTitleAndDescriptionView.contentDescription = getString(R.string.talkback_multipay_beneficiary_total_amount, totalAmountPayable)
            normalPaymentsTextView.contentDescription = getString(R.string.talkback_multipay_normal_payments_header)
            normalPaymentsTotalLineItemView.getLabelTextView().contentDescription = getString(R.string.talkback_multipay_normal_payment_total_label)
            normalPaymentsTotalLineItemView.getContentTextView().contentDescription = getString(R.string.talkback_multipay_normal_payment_total_content, AccessibilityUtils.getTalkBackRandValueFromTextView(normalPaymentsTotalLineItemView.getContentTextView()))
            futureDatedPaymentsTextView.contentDescription = getString(R.string.talkback_multipay_future_dated_payments_label)
            futureDatedPaymentsTotalLineItemView.getLabelTextView().contentDescription = getString(R.string.talkback_multipay_future_dated_amount_label)
            futureDatedPaymentsTotalLineItemView.getContentTextView().contentDescription = getString(R.string.talkback_multipay_future_dated_payment_total, AccessibilityUtils.getTalkBackRandValueFromTextView(futureDatedPaymentsTotalLineItemView.getContentTextView()))
            payButton.contentDescription = getString(R.string.talkback_multipay_beneficiary_pay_button)
        }
    }

    private fun setupViews() {
        val futureDatedPaymentsList: List<MultiplePaymentBeneficiaryWrapper> = multiplePaymentsViewModel.getFuturePaymentsList()
        val normalPaymentsList: List<MultiplePaymentBeneficiaryWrapper> = multiplePaymentsViewModel.getNormalPaymentsList()
        val immediatePaymentsList: List<MultiplePaymentBeneficiaryWrapper> = multiplePaymentsViewModel.getImmediatePaymentsList()

        populateNormalPayments(normalPaymentsList)
        populateFutureDatedPayments(futureDatedPaymentsList)
        populateImmediatePayments(immediatePaymentsList)

        binding.totalAmountTitleAndDescriptionView.title = multiplePaymentsViewModel.totalPaymentAmount.toRandAmount()
        binding.fromAccountSecondaryContentAndLabelView.setContentText(formatAccountNumberAndDescription(paymentsViewModel.selectedSourceAccount.accountName, paymentsViewModel.selectedSourceAccount.accountNumber))
        binding.payButton.setOnClickListener {
            validatePayments()

            validateMultiplePaymentViewModel.validateMultiplePaymentResponse.observe(viewLifecycleOwner, { validateMultiplePaymentResponse ->
                val hasWarnings = validateMultiplePaymentResponse.validateMultiplePaymentResponseList.any { it.warningMessageList.isNotEmpty() }
                when {
                    hasWarnings -> {
                        dismissProgressDialog()
                        navigate(MultiplePaymentsConfirmationFragmentDirections.actionMultiplePaymentsConfirmationFragmentToMultiplePaymentsReviewFragment())
                    }
                    validateMultiplePaymentResponse.errorsDetected -> {
                        dismissProgressDialog()
                        showGenericErrorMessageThenFinish() // TODO: Update this to handle exact errors
                    }
                    else -> performPayment()
                }
            })
        }
    }

    override fun performPayment() {
        super.performPayment()

        setupSureCheckHandler()

        payMultipleBeneficiaryViewModel.payMultipleBeneficiaryResponse.observe(viewLifecycleOwner, {
            dismissProgressDialog()

            // TODO : Handle new Response Codes

            if (it.header.statuscode == AUTHORISATION_RESPONSE_CODE) {
                paymentsViewModel.dualAuthorisationRequired = true
            }

            navigate(MultiplePaymentsConfirmationFragmentDirections.actionMultiplePaymentsConfirmationFragmentToMultiplePaymentsResultFragment())
        })
    }

    private fun validatePayments() {
        with(multiplePaymentsViewModel) {
            validateMultiplePaymentsRequestList.clear()
            selectedBeneficiaryList.forEachIndexed { index, regularBeneficiary ->
                val createValidateMultiplePaymentRequest = createValidateMultiplePaymentRequest(regularBeneficiary).apply {
                    paymentSequenceNumber = index
                    paymentSourceAccountNumber = paymentsViewModel.selectedSourceAccount.accountNumber

                    multiplePaymentsViewModel.getBeneficiaryWrapper(regularBeneficiary)?.let { beneficiaryWrapper ->
                        paymentAmount = beneficiaryWrapper.paymentAmount.toString()
                        useTime = beneficiaryWrapper.useTime
                        immediatePayment = beneficiaryWrapper.isImmediate
                        paymentTransactionDateAndTime = if (useTime) beneficiaryWrapper.paymentTransactionDateAndTime else Date()
                    }
                }

                validateMultiplePaymentsRequestList.add(createValidateMultiplePaymentRequest)
            }

            validateMultiplePaymentViewModel.validatePayments(validateMultiplePaymentsRequestList)
        }
    }

    private fun populateFutureDatedPayments(futureDatedBeneficiaryPaymentList: List<MultiplePaymentBeneficiaryWrapper>) {
        if (futureDatedBeneficiaryPaymentList.isNotEmpty()) {
            futureDatedBeneficiaryPaymentList.forEach {
                val rowBinding = createRowBinding(it, binding.futureDatedPaymentsLinearLayout).apply {
                    futureDatedSecondaryContentAndLabelView.visibility = View.VISIBLE
                    futureDatedSecondaryContentAndLabelView.setContentText(it.regularBeneficiary.transactionDate.toFormattedString(SPACED_PATTERN_DD_MMM_YYYY))
                    futureDatedSecondaryContentAndLabelView.setLabelText(getString(R.string.payment_date))
                }
                binding.futureDatedPaymentsLinearLayout.addView(rowBinding.root)
            }
            binding.futureDatedPaymentsTotalLineItemView.setLineItemViewContent(multiplePaymentsViewModel.totalFutureDatedPaymentAmount.toRandAmount())
        } else {
            binding.futureDatedPaymentsGroup.visibility = View.GONE
        }
    }

    private fun populateNormalPayments(normalPaymentsBeneficiaryList: List<MultiplePaymentBeneficiaryWrapper>) {
        if (normalPaymentsBeneficiaryList.isNotEmpty()) {
            normalPaymentsBeneficiaryList.forEach {
                val rowBinding = createRowBinding(it, binding.normalPaymentsLinearLayout).apply {
                    futureDatedSecondaryContentAndLabelView.visibility = View.GONE
                }
                binding.normalPaymentsLinearLayout.addView(rowBinding.root)
            }
            binding.normalPaymentsTotalLineItemView.setLineItemViewContent(multiplePaymentsViewModel.totalNormalPaymentAmount.toRandAmount())
        } else {
            binding.normalPaymentsGroup.visibility = View.GONE
        }
    }

    private fun populateImmediatePayments(immediatePaymentsBeneficiaryList: List<MultiplePaymentBeneficiaryWrapper>) {
        if (immediatePaymentsBeneficiaryList.isNotEmpty()) {
            immediatePaymentsBeneficiaryList.forEach {
                val rowBinding = createRowBinding(it, binding.immediatePaymentsLinearLayout).apply {
                    futureDatedSecondaryContentAndLabelView.visibility = View.GONE
                }
                binding.immediatePaymentsLinearLayout.addView(rowBinding.root)
            }
            binding.immediatePaymentsTotalLineItemView.setLineItemViewContent(multiplePaymentsViewModel.totalImmediatePaymentAmount.toRandAmount())
        } else {
            binding.immediatePaymentsGroup.visibility = View.GONE
        }
    }

    private fun createRowBinding(it: MultiplePaymentBeneficiaryWrapper, linearLayout: LinearLayout): WidgetMultiplePaymentOverviewBinding {
        return WidgetMultiplePaymentOverviewBinding.inflate(LayoutInflater.from(context), linearLayout, false).apply {
            amountLineItemView.setLineItemViewContent(it.paymentAmount.toString().toRandAmount())
            beneficiaryNameView.title = it.regularBeneficiary.beneficiaryDetails.beneficiaryName
            beneficiaryReferenceSecondaryContentAndLabelView.setLabelText(getString(R.string.ben_reference))
            beneficiaryReferenceSecondaryContentAndLabelView.setContentText(it.regularBeneficiary.beneficiaryDetails.targetAccountReference)
            accountNumberSecondaryContentAndLabelView.setContentText(it.regularBeneficiary.beneficiaryDetails.targetAccountNumber.toFormattedAccountNumber())

            setTalkBackForRow()
        }
    }

    private fun WidgetMultiplePaymentOverviewBinding.setTalkBackForRow() {
        beneficiaryNameView.contentDescription = getString(R.string.talkback_multipay_beneficiary_name, beneficiaryNameView.title)
        beneficiaryReferenceSecondaryContentAndLabelView.contentDescription = getString(R.string.talkback_multipay_beneficiary_reference, beneficiaryReferenceSecondaryContentAndLabelView.contentTextView.text.toString())
        futureDatedSecondaryContentAndLabelView.contentDescription = getString(R.string.talkback_multipay_future_dated_payments_scheduled, futureDatedSecondaryContentAndLabelView.contentTextViewValue)
        amountLineItemView.getLabelTextView().contentDescription = getString(R.string.talkback_multipay_amountview_label_text)
        amountLineItemView.getContentTextView().contentDescription = AccessibilityUtils.getTalkBackRandValueFromTextView(amountLineItemView.getContentTextView())

        if (beneficiaryNameView.titleTextView != null || beneficiaryNameView.descriptionTextView != null) {
            beneficiaryNameView.titleTextView.contentDescription = getString(R.string.talkback_multipay_beneficiary_name, beneficiaryNameView.title)
            beneficiaryNameView.descriptionTextView.contentDescription = getString(R.string.talkback_multipay_beneficiary_reference, beneficiaryNameView.description)
        }
    }

    fun navigateToResultScreen() {
        // TODO : Result Fragment
/*        val successIntent = Intent(baseActivity, MultiplePaymentResultActivity::class.java)
        successIntent.putExtra(MultiplePaymentResultActivity.BENEFICIARY_LIST, paymentResultList as Serializable)
        successIntent.putExtra(BMBConstants.AUTHORISATION_OUTSTANDING_TRANSACTION, isAuthorisationOutstanding)
        successIntent.putExtra(MultiplePaymentResultActivity.HAS_IMMEDIATE_PAYMENT, hasImmediatePayment)
        startActivityIfAvailable(successIntent)*/
    }

    fun navigateToFailureScreen() {
        AbsaCacheManager.getInstance().setAccountsCacheStatus(false)
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.your_payments_were_unsuccessful))
                .setPrimaryButtonLabel(getString(R.string.home))
                .build(false)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            navigateToHomeScreenWithoutReloadingAccounts()
        }
        navigate(MultiplePaymentsConfirmationFragmentDirections.actionMultiplePaymentsConfirmationFragmentToGenericResultScreenFragment(resultScreenProperties))
    }

    //TODO: Replace with Fragment
    fun navigateToTimeOutFailureScreen() {
        val intent = IntentFactoryGenericResult.getFailureResultBuilder(context)
                .setGenericResultHeaderMessage(R.string.payments_timeout_title)
                .setGenericResultSubMessage(getString(R.string.payments_timeout_message))
                .setGenericResultDoneButton(activity) { loadAccountsAndGoHome() }
                .build()
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}