/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.manage.profile.ui

import android.content.Intent
import android.net.Uri
import androidx.navigation.Navigation
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor
import com.barclays.absa.banking.boundary.monitoring.MonitoringService.Companion.MONITORING_EVENT_MANAGE_PROFILE_FAILURE_REASONS
import com.barclays.absa.banking.boundary.monitoring.MonitoringService.Companion.MONITORING_EVENT_MANAGE_PROFILE_VALIDATION_ERROR_MESSAGE
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.manage.profile.ManageProfileFileUtils
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.banking.manage.profile.ui.addressDetails.ManageProfileAddressDetailsViewModel
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.ProfileManager
import com.barclays.absa.utils.viewModel
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import java.util.*

class ManageProfileResultFactory(val fragment: BaseFragment) {

    fun updatedOtherPersonalInformationSuccess(message: String? = "", analyticsTag: String? = ""): GenericResultScreenProperties {
        buildScreens(fragment.baseActivity as ManageProfileActivity, analyticsTag)
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(fragment.getString(R.string.manage_profile_your_profile_changes_saved))
                .setDescription(message)
                .setPrimaryButtonLabel(fragment.getString(R.string.done))
                .setShouldAnimateOnlyOnce(true)
                .build(true)
    }

    fun updatedOtherPersonalInformationFailure(message: String? = "", analyticsTag: String? = ""): GenericResultScreenProperties {
        buildScreens(fragment.baseActivity as ManageProfileActivity, analyticsTag)

        val map = LinkedHashMap<String, Any?>()
        map[MONITORING_EVENT_MANAGE_PROFILE_VALIDATION_ERROR_MESSAGE] = message
        MonitoringInteractor().logMonitoringEvent(MONITORING_EVENT_MANAGE_PROFILE_FAILURE_REASONS, map)

        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(fragment.getString(R.string.manage_profile_unable_to_update_your_details))
                .setDescription(fragment.getString(R.string.manage_profile_generic_error_message))
                .setPrimaryButtonLabel(fragment.getString(R.string.done))
                .setShouldAnimateOnlyOnce(true)
                .build(true)
    }

    fun uploadedDocumentSuccessScreen(analyticsTag: String?): GenericResultScreenProperties {
        buildScreens(fragment.baseActivity as ManageProfileActivity, analyticsTag)
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(fragment.getString(R.string.manage_profile_doc_upload_successful_title))
                .setDescription(fragment.getString(R.string.manage_profile_doc_upload_successful_description))
                .setPrimaryButtonLabel(fragment.getString(R.string.done))
                .setShouldAnimateOnlyOnce(true)
                .build(true)
    }

    fun uploadedDocumentFailureScreen(analyticsTag: String): GenericResultScreenProperties {
        buildScreens(fragment.baseActivity as ManageProfileActivity, analyticsTag)

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            Intent(Intent.ACTION_DIAL).apply {
                AnalyticsUtil.trackAction(ANALYTICS_TAG, analyticsTag)
                data = Uri.parse(fragment.getString(R.string.manage_profile_fic_center_number))
                fragment.startActivity(this)
            }
        }

        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(fragment.getString(R.string.manage_profile_doc_upload_failure_title))
                .setDescription(fragment.getString(R.string.manage_profile_doc_upload_failure_description))
                .setSecondaryButtonLabel(fragment.getString(R.string.manage_profile_doc_upload_failure_screen_call_fic_center))
                .setPrimaryButtonLabel(fragment.getString(R.string.done))
                .setShouldAnimateOnlyOnce(true)
                .build(true)
    }

    private fun buildScreens(activity: ManageProfileActivity, analyticsTag: String?) {
        activity.dismissProgressDialog()
        activity.hideToolBar()

        GenericResultScreenFragment.setPrimaryButtonOnClick {
            if (!analyticsTag.isNullOrEmpty()) {
                AnalyticsUtil.trackAction(ANALYTICS_TAG, analyticsTag)
            }
            activity.viewModel<ManageProfileViewModel>().clearData()
            activity.viewModel<ManageProfileAddressDetailsViewModel>().clearData()
            ManageProfileFileUtils.removePreviewedFiles(activity, ProfileManager.getInstance().activeUserProfile.userId.toString())
            Navigation.findNavController(activity, R.id.manage_profile_nav_host_fragment).navigate(R.id.manageProfileHubFragment)
        }
    }
}
