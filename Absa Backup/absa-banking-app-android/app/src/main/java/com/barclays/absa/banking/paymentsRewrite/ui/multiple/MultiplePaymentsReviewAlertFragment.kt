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
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.MultiplePaymentsReviewAlertFragmentBinding
import com.barclays.absa.banking.paymentsRewrite.ui.multiple.dto.ValidationAlert
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import za.co.absa.networking.dto.ResultMessage

class MultiplePaymentsReviewAlertFragment : MultiplePaymentsBaseFragment(R.layout.multiple_payments_review_alert_fragment) {

    private val binding by viewBinding(MultiplePaymentsReviewAlertFragmentBinding::bind)
    private var sequenceNumber: Int = 0
    private var validationAlert: ValidationAlert? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupClickListeners()
    }

    private fun setupViews() {
        MultiplePaymentsReviewAlertFragmentArgs.fromBundle(requireArguments()).let { args ->
            sequenceNumber = args.sequenceNumber
            validationAlert = multiplePaymentsViewModel.getValidationAlertForSequenceNumber(sequenceNumber)
        }

        binding.allowPaymentRadioButtonView.setDataSource(SelectorList<StringItem>().apply {
            add(StringItem(getString(R.string.yes)))
            add(StringItem(getString(R.string.no)))
        })

        if (validationAlert?.isResolved == true) {
            binding.allowPaymentRadioButtonView.selectedIndex = 0
        } else if (validationAlert?.isRemoved == true) {
            binding.allowPaymentRadioButtonView.selectedIndex = 1
        }

        binding.allowPaymentRadioButtonView.setItemCheckedInterface { index ->
            binding.removedWarningTextView.visibility = if (index == 0) View.GONE else View.VISIBLE
        }

        validateMultiplePaymentViewModel.validateMultiplePaymentResponse.value?.validateMultiplePaymentResponseList?.find { it.paymentSequenceNumber == sequenceNumber }?.let {
            binding.areYouSureTextView.text = getString(R.string.payments_are_you_sure, validationAlert?.beneficiaryName)

            it.warningMessageList.forEach { resultMessage ->
                binding.alertsLinearLayout.addView(createWarningTextView(resultMessage))
            }
        }
    }

    private fun setupClickListeners() {
        binding.saveButton.setOnClickListener {
            when (binding.allowPaymentRadioButtonView.selectedIndex) {
                -1 -> binding.allowPaymentRadioButtonView.setErrorMessage(getString(R.string.payments_select_option))
                0 -> {
                    multiplePaymentsViewModel.validationAlertResolved(validationAlert)
                    activity?.onBackPressed()
                }
                else -> {
                    multiplePaymentsViewModel.validationAlertRemoved(validationAlert)
                    activity?.onBackPressed()
                }
            }
        }
    }

    private fun createWarningTextView(validationMessageList: ResultMessage): TextView {
        return TextView(context).apply {
            setTextAppearance(R.style.NormalTextRegularDark)
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, resources.getDimensionPixelSize(R.dimen.normal_space), 0, 0)
            }
            text = validationMessageList.responseMessage
        }
    }
}