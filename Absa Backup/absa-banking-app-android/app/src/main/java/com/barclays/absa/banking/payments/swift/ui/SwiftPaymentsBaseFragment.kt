/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.payments.swift.ui

import android.content.Context
import android.view.View
import androidx.annotation.LayoutRes
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.utils.TelephoneUtil
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.payments.swift.services.response.dto.SwiftBopErrorResponse
import com.barclays.absa.banking.payments.swift.ui.SwiftTransactionsActivity.Companion.SWIFT
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.viewModel
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import java.util.*

abstract class SwiftPaymentsBaseFragment(@LayoutRes contentLayoutId: Int) : BaseFragment(contentLayoutId) {

    protected lateinit var swiftPaymentViewModel: SwiftPaymentsViewModel
    protected lateinit var swiftPaymentsActivity: SwiftPaymentsActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        swiftPaymentsActivity = context as SwiftPaymentsActivity
        swiftPaymentViewModel = swiftPaymentsActivity.viewModel()
    }

    protected fun getUnableToProcessBopGenericResultScreenProperties(errors: List<SwiftBopErrorResponse>): GenericResultScreenProperties {
        val dateTime = getDisplayDateTime()
        val errorMessageBuilder = StringBuilder(getString(R.string.swift_unable_to_process_message).format(dateTime[0], dateTime[1]))
        errors.forEach {
            val field = if (it.field.contains("ABSABOPCUS.FINSURV.OriginalTransaction.", true)) {
                it.field.substringAfter("ABSABOPCUS.FINSURV.OriginalTransaction.")
            } else {
                it.field
            }
            errorMessageBuilder.append(it.code).append("\n").append(field).append("\n").append(it.text).append("\n\n")
        }

        setupDefaultsBeforeGenericResultScreen()
        GenericResultScreenFragment.setPrimaryButtonOnClick { findNavController().navigateUp() }
        GenericResultScreenFragment.setSecondaryButtonOnClick { swiftPaymentsActivity.finish() }
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.swift_unable_to_process))
                .setDescription(errorMessageBuilder.toString())
                .setPrimaryButtonLabel(getString(R.string.swift_edit))
                .setSecondaryButtonLabel(getString(R.string.swift_cancel))
                .build(true)
    }

    protected fun getUnableToProcessTransactionGenericResultScreenProperties(errorMessage: String): GenericResultScreenProperties {
        val dateTime = getDisplayDateTime()
        val errorMessageBuilder = StringBuilder(getString(R.string.swift_unable_to_process_message).format(dateTime[0], dateTime[1]))
                .append("\n${errorMessage}")
        setupDefaultsBeforeGenericResultScreen()
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            AnalyticsUtil.trackAction(SWIFT, "Swift_UnableToProcessScreen_DoneButtonClicked")
            swiftPaymentsActivity.finish()
        }
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.swift_unable_to_process))
                .setDescription(errorMessageBuilder.toString())
                .setPrimaryButtonLabel(getString(R.string.swift_cancel))
                .build(true)
    }

    protected fun getPaymentProcessingResultScreenProperties(reference: String): GenericResultScreenProperties {
        setupDefaultsBeforeGenericResultScreen()
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            AnalyticsUtil.trackAction(SWIFT, "Swift_SuccessScreen_DoneButtonClicked")
            swiftPaymentsActivity.loadAccountsAndGoHome()
        }
        val dateTime = getDisplayDateTime()
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.swift_payment_processing))
                .setDescription(getString(R.string.swift_payment_processing_message).format(dateTime[0], dateTime[1], reference))
                .setPrimaryButtonLabel(getString(R.string.swift_done))
                .build(true)
    }

    protected fun getPaymentProcessedGenericResultScreenProperties(reference: String): GenericResultScreenProperties {
        setupDefaultsBeforeGenericResultScreen()
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            AnalyticsUtil.trackAction(SWIFT, "Swift_AlreadyProcessedScreen_DoneButtonClicked")
            swiftPaymentsActivity.finish()
        }
        GenericResultScreenFragment.setSecondaryButtonOnClick { TelephoneUtil.call(swiftPaymentsActivity, TelephoneUtil.FRAUD_NUMBER) }
        val dateTime = getDisplayDateTime()
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalAlert)
                .setTitle(getString(R.string.swift_payment_already_processed))
                .setDescription(getString(R.string.swift_payment_already_processed_message).format(dateTime[0], dateTime[1], reference))
                .setPrimaryButtonLabel(getString(R.string.swift_done))
                .setSecondaryButtonLabel(getString(R.string.swift_fraud_hotline))
                .build(true)
    }

    private fun getDisplayDateTime(): List<String> {
        val date = Calendar.getInstance().time
        val displayDate = DateUtils.format(date, DateUtils.DATE_DISPLAY_PATTERN, Locale.ENGLISH)
        val displayTime = DateUtils.format(date, "HH:mm:ss")
        return listOf(displayDate, displayTime)
    }

    private fun setupDefaultsBeforeGenericResultScreen() {
        hideToolBar()
        swiftPaymentsActivity.setProgressIndicatorVisibility(View.GONE)
    }
}