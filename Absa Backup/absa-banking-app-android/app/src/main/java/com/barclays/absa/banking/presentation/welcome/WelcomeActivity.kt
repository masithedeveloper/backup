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
package com.barclays.absa.banking.presentation.welcome

import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.StringRes
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.WelcomeActivityBinding
import com.barclays.absa.banking.deviceLinking.ui.AccountLoginActivity
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.framework.services.AppCacheService
import com.barclays.absa.banking.linking.ui.IdentificationAndVerificationConstants.ID_AND_V_LINK_DEVICE
import com.barclays.absa.banking.linking.ui.LinkingActivity
import com.barclays.absa.banking.newToBank.NewToBankActivity
import com.barclays.absa.banking.newToBank.NewToBankConstants
import com.barclays.absa.banking.passcode.passcodeLogin.ExpressAuthenticationHelper
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.banking.presentation.sessionTimeout.SessionTimeOutDialogActivity
import com.barclays.absa.banking.registration.RegisterAtmValidationActivity
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.CameraUtil
import com.barclays.absa.utils.NetworkUtils
import com.barclays.absa.utils.SharedPreferenceService

class WelcomeActivity : BaseActivity() {
    private val binding by viewBinding(WelcomeActivityBinding::inflate)
    private val welcomeViewModel by viewModels<WelcomeViewModel>()

    companion object {
        const val SKIP_HELLO_CONFIG = "skipHelloConfiguration"
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        SessionTimeOutDialogActivity.shouldShow = false

        if (intent.getBooleanExtra(BMBApplication.SESSION_EXPIRE_EXTRA, false)) {
            intent.removeExtra(BMBApplication.SESSION_EXPIRE_EXTRA)
            moveTaskToBack(true)
        }

        val canSkipHello = intent.getBooleanExtra(SKIP_HELLO_CONFIG, false)
        if (!canSkipHello) {
            val expressAuthenticationHelper = ExpressAuthenticationHelper(this)
            expressAuthenticationHelper.performHello {
                dismissProgressDialog()
            }
        }

        super.onCreate(savedInstanceState)

        registerListeners()
        appCacheService.setPasscodeResetFlow(false)
        SharedPreferenceService.setIsPartialRegistration(false)
        if (featureSwitchingToggles.ntbRegistration == FeatureSwitchingStates.GONE.key) {
            binding.openAccountNowButton.visibility = View.GONE
        }
    }

    private fun registerListeners() {
        binding.openAccountNowButton.setOnClickListener {
            AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_WelcomeToAbsaScreen_OpenAnAccountButtonClicked")
            if (NetworkUtils.isNetworkConnected()) {
                try {
                    if (CameraUtil.isFrontCameraAvailable(BMBApplication.getInstance().applicationContext)) {
                        continueWithNavigation()
                        performAnalytics(NewToBankConstants.OPEN_ACCOUNT_ACTION, NewToBankConstants.ANALYTICS_CHANNEL)
                    } else {
                        capabilityUnavailable(R.string.new_to_bank_generic_header_unable_to_continue, R.string.new_to_bank_front_facing_camera_error)
                        trackScreenView(NewToBankConstants.ANALYTICS_CHANNEL, NewToBankConstants.SELFIE_NO_FRONT_CAMERA_ERROR)
                    }
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                    capabilityUnavailable(R.string.new_to_bank_generic_header_unable_to_continue, R.string.new_to_bank_camera_access_error)
                } catch (e: Exception) {
                    BMBApplication.getInstance().logCaughtException(e)
                }
            } else {
                capabilityUnavailable(R.string.new_to_bank_generic_header_unable_to_continue, R.string.connectivity_turn_on_connection)
            }
        }

        binding.linkDeviceButton.setOnClickListener {
            AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_WelcomeToAbsaScreen_LinkThisDeviceButtonClicked")
            welcomeViewModel.fetchIvalAndNval()
            if (BuildConfig.TOGGLE_DEF_BIOMETRIC_VERIFICATION_ENABLED && featureSwitchingToggles.biometricVerification == FeatureSwitchingStates.ACTIVE.key) {
                launchIdNumberInputScreen()
            } else {
                launchAccessAccountLoginScreen()
            }
        }

        binding.registerButton.setOnClickListener {
            AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_WelcomeToAbsaScreen_RegisterForDigitalBankingButtonClicked")
            launchRegistrationAtmValidationScreen()
        }

        populateAnimatedPages()
        appCacheService.setPasscodeResetFlow(false)
        SharedPreferenceService.setIsPartialRegistration(false)
        if (featureSwitchingToggles.ntbRegistration == FeatureSwitchingStates.GONE.key) {
            binding.openAccountNowButton.visibility = View.GONE
        }
    }

    private fun launchIdNumberInputScreen() {
        startActivity(Intent(this, LinkingActivity::class.java))
    }

    private fun configureTalkBack() {
        if (isAccessibilityEnabled) {
            binding.openAccountNowButton.contentDescription = getString(R.string.talkback_landing_new_absa_client)
        }
    }

    override fun onResume() {
        super.onResume()

        appCacheService.clearAllIdentificationAndVerificationCacheValues()

        setToolBarBack(R.string.all_new_absa) {
            AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_WelcomeToAbsaScreen_CloseButtonClicked")
            super.onBackPressed()
        }
    }

    private fun populateAnimatedPages() {
        configureTalkBack()
    }

    private fun launchAccessAccountLoginScreen() {
        startActivity(Intent(this, AccountLoginActivity::class.java))
    }

    private fun launchRegistrationAtmValidationScreen() {
        startActivity(Intent(this, RegisterAtmValidationActivity::class.java))
    }

    fun capabilityUnavailable(@StringRes noticeMessage: Int, @StringRes subMessage: Int) {
        dismissProgressDialog()
        with(Intent(this, GenericResultActivity::class.java)) {
            putExtra(GenericResultActivity.IS_ERROR, true)
            putExtra(GenericResultActivity.NOTICE_MESSAGE, noticeMessage)
            putExtra(GenericResultActivity.SUB_MESSAGE, subMessage)
            putExtra(GenericResultActivity.BOTTOM_BUTTON_TYPE_SECONDARY, true)
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.close)
            GenericResultActivity.bottomOnClickListener = View.OnClickListener { navigateToWelcomePage() }
            startActivity(this)
        }
    }

    private fun navigateToWelcomePage() {
        with(Intent(this, WelcomeActivity::class.java)) {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(this)
        }
    }

    private fun continueWithNavigation() {
        if (featureSwitchingToggles.ntbRegistration == FeatureSwitchingStates.DISABLED.key) {
            capabilityUnavailable(R.string.new_to_bank_generic_header_unable_to_continue, R.string.new_to_bank_registration_maintenance_error)
        } else {
            NewToBankConstants.ON_NEW_TO_BANK_FLOW = true
            startActivity(Intent(this, NewToBankActivity::class.java))
        }
    }
}