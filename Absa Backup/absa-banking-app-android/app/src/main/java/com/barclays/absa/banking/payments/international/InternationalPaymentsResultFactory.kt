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

package com.barclays.absa.banking.payments.international

import android.os.Bundle
import androidx.navigation.Navigation
import com.barclays.absa.banking.R
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.utils.AnalyticsUtil
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties

class InternationalPaymentsResultFactory {

    fun buildInternationalPaymentProcessingBundle(activity: InternationalPaymentsPaymentsActivity, dateAndTime: String, referenceNumber: String): GenericResultScreenProperties {
        AnalyticsUtil.trackAction(InternationalPaymentsConstants.INTERNATIONAL_PAYMENTS, "International Payments Payment Successful Screen")
        val resultScreenProperties: GenericResultScreenProperties
        val propertiesBuilder = GenericResultScreenProperties.PropertiesBuilder()

        GenericResultScreenFragment.setPrimaryButtonOnClick { Navigation.findNavController(activity, R.id.international_nav_host_fragment).navigate(R.id.internationalPaymentHubFragment) }
        activity.isExistingBeneficiary = false
        activity.clearCachedData()

        if (!activity.hasSecurityQuestion) {
            val originalString: String = activity.getString(R.string.international_payment_result_transaction_successfully_submitted, dateAndTime, referenceNumber)
            val textToMakeBold: Array<String> = arrayOf(activity.getString(R.string.international_payment_result_transaction_successfully_string_to_make_bold), referenceNumber)
            resultScreenProperties = propertiesBuilder
                    .setResultScreenAnimation(ResultAnimations.generalSuccess)
                    .setTitle(activity.getString(R.string.international_payment_result_payment_processing))
                    .setDescription(originalString, textToMakeBold)
                    .setPrimaryButtonLabel(activity.getString(R.string.done))
                    .build(true)
        } else {
            val originalString: String = activity.getString(R.string.international_payment_result_transaction_successfully_submitted_with_security_question, dateAndTime, referenceNumber)
            val textToMakeBold: Array<String> = arrayOf(activity.getString(R.string.international_payment_result_transaction_successfully_submitted_with_security_question_text_to_bold), referenceNumber)
            resultScreenProperties = propertiesBuilder
                    .setResultScreenAnimation(ResultAnimations.generalSuccess)
                    .setTitle(activity.getString(R.string.international_payment_result_payment_processing))
                    .setDescription(originalString, textToMakeBold)
                    .setPrimaryButtonLabel(activity.getString(R.string.done))
                    .build(true)
        }
        return resultScreenProperties
    }

    fun buildInternationalPaymentPleaseNoteBundle(activity: InternationalPaymentsPaymentsActivity): GenericResultScreenProperties {
        AnalyticsUtil.trackAction(InternationalPaymentsConstants.INTERNATIONAL_PAYMENTS, "International Payments Outside Office Hours Screen")
        GenericResultScreenFragment.setPrimaryButtonOnClick { activity.finish() }

        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalAlert)
                .setTitle(activity.getString(R.string.international_payment_result_please_note))
                .setDescription(activity.getString(R.string.international_payment_result_payment_notice_times))
                .setPrimaryButtonLabel(activity.getString(R.string.home))
                .build(true)
    }

    fun buildInternationalPaymentLastPaymentPendingBundle(activity: InternationalPaymentsPaymentsActivity): GenericResultScreenProperties {
        AnalyticsUtil.trackAction(InternationalPaymentsConstants.INTERNATIONAL_PAYMENTS, "International Payments Payment Pending Screen")
        GenericResultScreenFragment.setPrimaryButtonOnClick { Navigation.findNavController(activity, R.id.international_nav_host_fragment).navigate(R.id.internationalPaymentHubFragment) }

        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalAlert)
                .setTitle(activity.getString(R.string.international_payment_result_last_payment_pending))
                .setDescription(activity.getString(R.string.international_payment_result_last_payment_pending_description))
                .setPrimaryButtonLabel(activity.getString(R.string.done))
                .build(true)
    }

    fun buildInternationalPaymentFailureBundle(activity: InternationalPaymentsPaymentsActivity, errorText: String): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick { Navigation.findNavController(activity, R.id.international_nav_host_fragment).navigate(R.id.internationalPaymentHubFragment) }
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(activity.getString(R.string.payment_failure_message))
                .setDescription(errorText)
                .setPrimaryButtonLabel(activity.getString(R.string.done))
                .build(false)
    }

    fun buildInternationalPaymentSureCheckFailureBundle(activity: InternationalPaymentsPaymentsActivity): GenericResultScreenProperties {
        AnalyticsUtil.trackAction(InternationalPaymentsConstants.INTERNATIONAL_PAYMENTS, "International Payments Payment Unsuccessful Screen")
        GenericResultScreenFragment.setPrimaryButtonOnClick { Navigation.findNavController(activity, R.id.international_nav_host_fragment).navigate(R.id.internationalPaymentHubFragment) }
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(activity.getString(R.string.payment_failure_message))
                .setDescription(activity.getString(R.string.surecheck_error_unabletocomplete))
                .setPrimaryButtonLabel(activity.getString(R.string.done))
                .build(false)

    }

    fun buildInternationalPaymentBeneficiaryNotSavedBundle(activity: InternationalPaymentsPaymentsActivity, error: String): GenericResultScreenProperties {
        AnalyticsUtil.trackAction(InternationalPaymentsConstants.INTERNATIONAL_PAYMENTS, "International Payments Beneficiary Not Saved Screen")
        GenericResultScreenFragment.setPrimaryButtonOnClick { Navigation.findNavController(activity, R.id.international_nav_host_fragment).navigate(R.id.internationalPaymentHubFragment) }
        activity.clearCachedData()
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(activity.getString(R.string.international_payment_result_beneficiary_not_saved))
                .setDescription(error)
                .setPrimaryButtonLabel(activity.getString(R.string.done))
                .build(false)
    }

    fun buildInternationalPaymentCannotCalculateBundle(activity: InternationalPaymentsPaymentsActivity): GenericResultScreenProperties {
        AnalyticsUtil.trackAction(InternationalPaymentsConstants.INTERNATIONAL_PAYMENTS, "International Payments Cannot Calculate Screen")
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            Navigation.findNavController(activity, R.id.international_nav_host_fragment).navigateUp()
        }
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(activity.getString(R.string.international_payments_unable_to_calculate_amount))
                .setDescription(activity.getString(R.string.international_payments_unable_to_calculate_description))
                .setPrimaryButtonLabel(activity.getString(R.string.international_try_again))
                .build(false)
    }

    fun buildInternationalPaymentCannotCalculateFailedBundle(activity: InternationalPaymentsPaymentsActivity): GenericResultScreenProperties {
        AnalyticsUtil.trackAction(InternationalPaymentsConstants.INTERNATIONAL_PAYMENTS, "International Payments Cannot Calculate Screen")
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            Navigation.findNavController(activity, R.id.international_nav_host_fragment).navigate(R.id.internationalPaymentHubFragment)
            activity.clearCachedData()
            activity.calculateRetryCount = 0
        }
        return  GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(activity.getString(R.string.international_payments_unable_to_calculate_amount))
                .setDescription(activity.getString(R.string.international_payments_unable_to_calculate_extended_description))
                .setPrimaryButtonLabel(activity.getString(R.string.done))
                .build(false)
    }

    fun buildFicaFailureBundle(activity: InternationalPaymentsPaymentsActivity): GenericResultScreenProperties {
        AnalyticsUtil.trackAction(InternationalPaymentsConstants.INTERNATIONAL_PAYMENTS, "International Payments FICA error Screen")
        GenericResultScreenFragment.setPrimaryButtonOnClick { Navigation.findNavController(activity, R.id.international_nav_host_fragment).navigate(R.id.internationalPaymentHubFragment) }
        activity.clearCachedData()

        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(activity.getString(R.string.international_payments_payment_unsuccessful))
                .setDescription(activity.getString(R.string.international_payments_fica_error))
                .setPrimaryButtonLabel(activity.getString(R.string.done))
                .build(false)
    }

    fun buildFicaFailureBundle(activity: InternationalPaymentsPaymentsActivity, title: String, error: String): GenericResultScreenProperties {
        AnalyticsUtil.trackAction(InternationalPaymentsConstants.INTERNATIONAL_PAYMENTS, "International Payments General Failure Screen")
        GenericResultScreenFragment.setPrimaryButtonOnClick { Navigation.findNavController(activity, R.id.international_nav_host_fragment).navigate(R.id.internationalPaymentHubFragment) }
        activity.clearCachedData()

        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(title)
                .setDescription(error)
                .setPrimaryButtonLabel(activity.getString(R.string.done))
                .build(false)
    }

    fun buildGeneralFailureBundle(activity: InternationalPaymentsPaymentsActivity, error: String): Bundle {
        val resultScreenProperties: GenericResultScreenProperties
        val propertiesBuilder = GenericResultScreenProperties.PropertiesBuilder()

        GenericResultScreenFragment.setPrimaryButtonOnClick { Navigation.findNavController(activity, R.id.international_nav_host_fragment).navigate(R.id.internationalPaymentHubFragment) }
        activity.clearCachedData()

        resultScreenProperties = propertiesBuilder
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(activity.getString(R.string.error))
                .setDescription(error)
                .setPrimaryButtonLabel(activity.getString(R.string.done))
                .build(false)
        return buildBundle(resultScreenProperties)
    }

    private fun buildBundle(resultScreenProperties: GenericResultScreenProperties): Bundle {
        val bundle = Bundle()
        bundle.putSerializable(GenericResultScreenFragment.GENERIC_RESULT_PROPERTIES_KEY, resultScreenProperties)
        bundle.putBoolean(GenericResultScreenFragment.SHOULD_ONLY_ANIMATE_ONCE, true)
        return bundle
    }
}