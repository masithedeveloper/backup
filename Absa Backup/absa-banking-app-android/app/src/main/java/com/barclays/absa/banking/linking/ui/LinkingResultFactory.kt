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
 */

package com.barclays.absa.banking.linking.ui

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.annotation.StringRes
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.linking.ui.IdentificationAndVerificationConstants.ID_AND_V_LINK_DEVICE
import com.barclays.absa.banking.manage.devices.DeviceListActivity
import com.barclays.absa.banking.newToBank.NewToBankActivity
import com.barclays.absa.banking.newToBank.NewToBankConstants
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.sureCheckV2.SureCheckReTriggerHandler
import com.barclays.absa.utils.AnalyticsUtil
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties

class LinkingResultFactory(val fragment: BaseFragment) {
    private val appCacheService: IAppCacheService = getServiceInterface()

    fun sureCheckFailed(message: String? = ""): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            clearCache()
            fragment.activity?.finish()
        }

        buildScreens(fragment.baseActivity as LinkingActivity)
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.surecheck_failed))
                .setDescription(message)
                .setPrimaryButtonLabel(getString(R.string.done))
                .setShouldAnimateOnlyOnce(true)
                .build(true)
    }

    fun showSureCheckPinRevokedResultScreen(): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            clearCache()
            fragment.activity?.finish()
        }

        buildScreens(fragment.baseActivity as LinkingActivity)
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.linking_pin_revoked_title))
                .setDescription(getString(R.string.linking_pin_revoked_message))
                .setPrimaryButtonLabel(getString(R.string.done))
                .setShouldAnimateOnlyOnce(true)
                .build(true)
    }

    fun showPermissionsFailure(action: () -> Unit): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            action.invoke()
        }

        buildScreens(fragment.baseActivity)
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.linking_error_camera_permission_title))
                .setDescription(getString(R.string.linking_error_camera_permission_message))
                .setPrimaryButtonLabel(getString(R.string.linking_error_open_settings))
                .setShouldAnimateOnlyOnce(true)
                .build(true)
    }

    fun showFailureScreenForLiveliness(message: String?, primaryClick: () -> Unit): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            primaryClick.invoke()
        }

        buildScreens(fragment.baseActivity)
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.something_went_wrong))
                .setDescription(message)
                .setPrimaryButtonLabel(getString(R.string.try_again))
                .setPrimaryButtonContentDescription(getString(R.string.try_again))
                .build(true)
    }

    fun showFailureScreenForLivelinessNoRetry(message: String?, primaryClick: () -> Unit): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            primaryClick.invoke()
        }

        buildScreens(fragment.baseActivity)
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.something_went_wrong))
                .setDescription(message)
                .setPrimaryButtonLabel(getString(R.string.ok))
                .setPrimaryButtonContentDescription(getString(R.string.ok))
                .build(true)
    }

    fun showFailureForNoActiveAccounts(): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_AccountNotFoundScreen_OpenAnAccountButtonClicked")
            NewToBankConstants.ON_NEW_TO_BANK_FLOW = true
            clearCache()
            startActivity(Intent(fragment.activity, NewToBankActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
        GenericResultScreenFragment.setSecondaryButtonOnClick {
            AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_AccountNotFoundScreen_NotNowButtonClicked")
            clearCache()
            fragment.activity?.finish()
        }

        buildScreens(fragment.baseActivity)
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.linking_you_dont_have_an_active_account_title))
                .setDescription(getString(R.string.linking_you_dont_have_an_active_account_content))
                .setPrimaryButtonLabel(getString(R.string.linking_open_an_account))
                .setPrimaryButtonContentDescription(getString(R.string.try_again))
                .setSecondaryButtonLabel(getString(R.string.linking_not_now))
                .build(true)
    }

    fun showFailureScreenFragment(title: String, message: String, button: String, action: () -> Unit): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            clearCache()
            AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_AccountVerificationFailedScreen_OKButtonClicked")
            action.invoke()
        }

        buildScreens(fragment.baseActivity)
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(title)
                .setDescription(message)
                .setPrimaryButtonLabel(button)
                .setContactViewContactName(getString(R.string.call_centre))
                .setContactViewContactNumber(getString(R.string.support_center_number))
                .build(false)
    }

    fun showFailureScreenFragmentWithNoContactDetails(@StringRes title: Int, @StringRes message: Int, @StringRes button: Int, action: () -> Unit): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            clearCache()
            AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_AccountVerificationFailedScreen_OKButtonClicked")
            action.invoke()
        }

        buildScreens(fragment.baseActivity)
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(title))
                .setDescription(if (message != -1) getString(message) else "")
                .setPrimaryButtonLabel(getString(button))
                .build(false)
    }

    fun showFailureScreenFragment(@StringRes title: Int, @StringRes message: Int, @StringRes button: Int, action: () -> Unit): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            clearCache()
            action.invoke()
        }

        buildScreens(fragment.baseActivity)
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(title))
                .setDescription(if (message != -1) getString(message) else "")
                .setPrimaryButtonLabel(getString(button))
                .build(false)
    }

    fun showFailureScreenFragmentForVerification(@StringRes title: Int, @StringRes message: Int, error: String, @StringRes button: Int, action: () -> Unit): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            clearCache()
            action.invoke()
        }

        buildScreens(fragment.baseActivity)
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(title))
                .setDescription("${getString(message)}\n\n$error")
                .setPrimaryButtonLabel(getString(button))
                .build(false)
    }

    fun showPrimaryDeviceChangedSuccessfullyResultFragment(): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            fragment.hideToolBar()
            clearCache()
            val deviceSelectIntent = Intent(fragment.activity, DeviceListActivity::class.java)
            deviceSelectIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            fragment.startActivity(deviceSelectIntent)
        }

        buildScreens(fragment.baseActivity)
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.linking_verification_device_updated))
                .setDescription(getString(R.string.linking_this_is_now_your_verification_device))
                .setPrimaryButtonLabel(getString(R.string.manage_devices))
                .build(false)
    }

    fun showPrimaryDeviceChangedSuccessfullyDuringSureCheckFragment(): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            Handler(Looper.getMainLooper()).postDelayed({
                SureCheckReTriggerHandler.reTriggerAction?.let {
                    appCacheService.getLastSureCheckDelegateBeforeChangingPrimary()?.let {
                        appCacheService.setSureCheckDelegate(it)
                    }

                    BMBApplication.getInstance().topMostActivity.finish()
                    SureCheckReTriggerHandler.reTriggerAction?.performAction()
                    SureCheckReTriggerHandler.reTriggerAction = null
                    appCacheService.setChangePrimaryDeviceFromNoPrimaryDeviceScreen(false)
                }
            }, 250)

            fragment.baseActivity.finish()
        }

        buildScreens(fragment.baseActivity)
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.this_is_now_your_surecheck_2_0_device))
                .setDescription(getString(R.string.surecheck_2_0_instruction))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(false)
    }

    fun showSuccessFragment(@StringRes title: Int, @StringRes message: Int, @StringRes buttonTitle: Int): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick { fragment.baseActivity.finish() }

        buildScreens(fragment.baseActivity)
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(title))
                .setDescription(if (message != -1) getString(message) else "")
                .setPrimaryButtonLabel(getString(buttonTitle))
                .build(false)
    }

    fun showConnectionErrorScreen(@StringRes title: Int, @StringRes message: Int, @StringRes buttonTitle: Int, action: () -> Unit): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick { action.invoke() }

        buildScreens(fragment.baseActivity)
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalError)
                .setTitle(getString(title))
                .setDescription(if (message != -1) getString(message) else "")
                .setPrimaryButtonLabel(getString(buttonTitle))
                .build(false)
    }

    fun showConnectionErrorScreenWithCancelButton(@StringRes title: Int, @StringRes message: Int, @StringRes buttonTitle: Int, action: () -> Unit): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick { action.invoke() }
        GenericResultScreenFragment.setSecondaryButtonOnClick { fragment.baseActivity.finish() }

        buildScreens(fragment.baseActivity)
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalError)
                .setTitle(getString(title))
                .setDescription(if (message != -1) getString(message) else "")
                .setPrimaryButtonLabel(getString(buttonTitle))
                .setSecondaryButtonLabel(getString(R.string.cancel))
                .build(false)
    }

    private fun buildScreens(activity: BaseActivity) {
        activity.dismissProgressDialog()
        activity.hideToolBar()
    }

    private fun getString(resourceId: Int): String = fragment.getString(resourceId)

    private fun startActivity(intent: Intent) {
        fragment.startActivity(intent)
    }

    fun clearCache() {
        appCacheService.setIsIdentificationAndVerificationLinkingFlow(false)
        appCacheService.setChangePrimaryDeviceFlow(false)
        appCacheService.setChangePrimaryDeviceFlowFromSureCheck(false)
        appCacheService.setIsBioAuthenticated(false)
    }
}