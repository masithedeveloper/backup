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
import androidx.annotation.LayoutRes
import androidx.fragment.app.activityViewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.express.payments.payMultipleBeneficiary.PayMultipleBeneficiaryViewModel
import com.barclays.absa.banking.express.payments.payMultipleBeneficiary.dto.PayMultipleBeneficiaryRequest
import com.barclays.absa.banking.express.payments.validateMultiplePayment.ValidateMultiplePaymentViewModel
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.paymentsRewrite.ui.PaymentsBaseFragment
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties

open class MultiplePaymentsBaseFragment(@LayoutRes layout: Int) : PaymentsBaseFragment(layout) {
    val payMultipleBeneficiaryViewModel by activityViewModels<PayMultipleBeneficiaryViewModel>()
    val multiplePaymentsViewModel by activityViewModels<MultiplePaymentsViewModel>()
    val validateMultiplePaymentViewModel by activityViewModels<ValidateMultiplePaymentViewModel>()

    open fun performPayment() {
        payMultipleBeneficiaryViewModel.failureLiveData.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            navigateToFailureScreen()
        })

        val payMultipleBeneficiaryRequestList = mutableListOf<PayMultipleBeneficiaryRequest>()

        validateMultiplePaymentViewModel.validateMultiplePaymentResponse.value?.validateMultiplePaymentResponseList?.forEach { validateMultiplePayment ->
            val validationAlertForSequenceNumber = multiplePaymentsViewModel.getValidationAlertForSequenceNumber(validateMultiplePayment.paymentSequenceNumber)
            if (validationAlertForSequenceNumber == null || !validationAlertForSequenceNumber.isRemoved) {
                payMultipleBeneficiaryRequestList.add(PayMultipleBeneficiaryRequest().apply {
                    realTimePaymentReferenceNumber = validateMultiplePayment.realTimePaymentReferenceNumber
                    paymentSequenceNumber = validateMultiplePayment.paymentSequenceNumber
                    targetAccountNumber = validateMultiplePayment.targetAccountNumber
                    paymentToken = validateMultiplePayment.paymentToken
                })
            }
        }

        payMultipleBeneficiaryViewModel.payBeneficiaries(payMultipleBeneficiaryRequestList, multiplePaymentsViewModel.totalPaymentAmount)
    }

    fun navigateToFailureScreen(resultScreenProperties: GenericResultScreenProperties? = null) {
        val genericResultScreenProperties = resultScreenProperties ?: genericFailureScreenProperties()

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            baseActivity.navigateToHomeScreenWithoutReloadingAccounts()
        }
        navigate(R.id.genericResultScreenFragment, Bundle().apply { putSerializable(GenericResultScreenFragment.GENERIC_RESULT_PROPERTIES_KEY, genericResultScreenProperties) })
    }

    private fun genericFailureScreenProperties(): GenericResultScreenProperties {
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.something_went_wrong))
                .setDescription(getString(R.string.multiple_payments_failure))
                .setPrimaryButtonLabel(getString(R.string.home))
                .build(true)
    }
}