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
package com.barclays.absa.banking.login.ui.passcode

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.biometric.BiometricPrompt
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.biometric.BiometricHelper
import com.barclays.absa.banking.boundary.model.AccessPrivileges.Companion.instance
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.boundary.model.UserProfile
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor
import com.barclays.absa.banking.express.biometricAuthentication.BiometricAuthenticationToggler
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.services.AppCacheService
import com.barclays.absa.banking.linking.ui.IdentificationAndVerificationConstants.ID_AND_V_LINK_DEVICE
import com.barclays.absa.banking.manage.devices.VerificationDeviceNominationActivity
import com.barclays.absa.banking.newToBank.NewToBankConstants
import com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.SimplifiedLoginActivity
import com.barclays.absa.banking.presentation.sureCheckV2.SureCheckConfirmation2faActivity
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog.dismissAlertDialog
import com.barclays.absa.banking.shared.BaseAlertDialog.showYesNoDialog
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.ProfileManager
import com.barclays.absa.utils.SharedPreferenceService
import com.barclays.absa.utils.UserSettingsManager
import com.barclays.absa.utils.key.KeyTools
import kotlinx.android.synthetic.main.passcode_success_use_fingerprint_activity.*
import styleguide.forms.RadioButtonView
import styleguide.forms.SelectorList
import styleguide.forms.StringItem

class PasscodeSuccessUseFingerprintActivity : BaseActivity(R.layout.passcode_success_use_fingerprint_activity) {

    private lateinit var biometricHelper: BiometricHelper
    private val keyTools: KeyTools by lazy { KeyTools.newInstance(BMBApplication.getInstance()) }

    companion object {
        private val TAG = PasscodeSuccessUseFingerprintActivity::class.java.simpleName
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initKeyTools(this)
        if (NewToBankConstants.ON_NEW_TO_BANK_FLOW) {
            setToolBar(getString(R.string.linking_passcode_toolbar_header), null)
        } else {
            setToolBarBack(getString(R.string.linking_passcode_toolbar_header))
        }
        fingerprintOptionSelection()

        fingerprintAuthProceedButton.setOnClickListener {
            when (fingerprintAuthRadioButtonView.selectedIndex) {
                0 -> {
                    AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_PasscodeCreatedSuccessfullyScreen_EnableBiometricAuthenticationButtonClicked")
                    showFingerprintDialog()
                }
                1 -> {
                    AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_PasscodeCreatedSuccessfullyScreen_DisableBiometricAuthenticationButtonClicked")
                    UserSettingsManager.setFingerprintActive(false)
                    navigateToPrimaryScreen(false)
                }
                else -> {
                }
            }
        }

        biometricHelper = BiometricHelper(this, object : BiometricHelper.Callback {
            override fun onAuthenticated(cryptoObject: BiometricPrompt.CryptoObject?) {
                cryptoObject?.cipher?.iv?.let { iv -> SharedPreferenceService.setKeyToolsIv(iv) }
                val profileManager = ProfileManager.getInstance()
                profileManager.activeUserProfile?.let {
                    val newRandomUserId = profileManager.generateRandomUserId().toByteArray()
                    it.fingerprintId = cryptoObject?.cipher?.doFinal(newRandomUserId)
                    it.randomAliasId = it.fingerprintId
                    profileManager.updateProfile(it, object : ProfileManager.OnProfileUpdateListener {
                        override fun onProfileUpdated(userProfile: UserProfile?) {
                            BiometricAuthenticationToggler.enableBiometrics(this@PasscodeSuccessUseFingerprintActivity, newRandomUserId, {
                                UserSettingsManager.setFingerprintActive(true)
                                navigateToPrimaryScreen(true)
                            }, {
                                UserSettingsManager.setFingerprintActive(false)
                                navigateToPrimaryScreen(true)
                            })
                        }

                        override fun onProfileUpdateFailed() {
                            MonitoringInteractor().logTechnicalEvent(this::class.simpleName, "onProfileUpdateFailed", "Profile update failed")
                            showGenericErrorMessageThenFinish()
                        }
                    })
                }

            }

            override fun onError(errorCode: Int) {
                dismissProgressDialog()
            }

            override fun onAuthenticationFailed() {}
        })
    }

    private fun initKeyTools(context: Context) {
        val packageManager = context.packageManager
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            try {
                keyTools.loadKeys()
            } catch (e: KeyTools.KeyToolsException) {
                if (BuildConfig.DEBUG) {
                    Log.e(PasscodeSuccessUseFingerprintActivity.TAG, "Failed to initialise keytools", e)
                }
            }
        }
    }

    private fun showFingerprintDialog() {
        try {
            val userAuthCipher = keyTools.encryptionCipher ?: keyTools.cipherNew
            userAuthCipher?.let { biometricHelper.authenticate(it) }
        } catch (e: KeyTools.KeyToolsException) {
            if (BuildConfig.DEBUG) {
                Log.e(Companion.TAG, "Failed to set the crypto object", e)
            }
            showGenericErrorMessageThenFinish()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun fingerprintOptionSelection() {
        SelectorList<StringItem>().apply {
            add(StringItem(resources.getString(R.string.yes)))
            add(StringItem(resources.getString(R.string.no)))
            (fingerprintAuthRadioButtonView as RadioButtonView<StringItem>).setDataSource(this)
        }
        fingerprintAuthRadioButtonView.selectedIndex = 0
    }

    private fun navigateToPrimaryScreen(isFingerPrintAuthenticationSuccessful: Boolean) {
        if (appCacheService.isSecondaryDevice() && !appCacheService.isPasscodeResetFlow() && !instance.isOperator) {
            Intent(this, VerificationDeviceNominationActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra("isFingerPrintAuthenticationEnabled", isFingerPrintAuthenticationSuccessful)
                startActivity(this)
            }
        } else {
            Intent(this, SureCheckConfirmation2faActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(VerificationDeviceNominationActivity.MAKE_SURECHECK, "yes")
                startActivity(this)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.cancel_menu_item) {
            AnalyticsUtil.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_PasscodeCreatedSuccessfully_CancelButtonClicked")
            showYesNoDialog(AlertDialogProperties.Builder()
                    .title(getString(R.string.are_you_sure_stop_setup))
                    .message(getString(R.string.this_will_end_session))
                    .positiveDismissListener { _, _ ->
                        AnalyticsUtils.getInstance().trackCancelButton(mScreenName, mSiteSection)
                        dismissAlertDialog()
                        val intent = Intent(this, WelcomeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        CustomerProfileObject.updateCustomerProfileObject(CustomerProfileObject())
                        startActivity(intent)
                        finish()
                    })
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (NewToBankConstants.ON_NEW_TO_BANK_FLOW) {
            showEndSessionDialogPrompt()
        } else {
            showYesNoDialog(AlertDialogProperties.Builder()
                    .message(getString(R.string.are_you_sure_stop_setup))
                    .positiveDismissListener { _, _ ->
                        AnalyticsUtils.getInstance().trackCancelButton(mScreenName, mSiteSection)
                        CustomerProfileObject.updateCustomerProfileObject(CustomerProfileObject())
                        val intent = Intent(this, SimplifiedLoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    })
        }
    }

    private fun showEndSessionDialogPrompt() {
        showYesNoDialog(AlertDialogProperties.Builder()
                .title(getString(R.string.new_to_bank_are_you_sure))
                .message(getString(R.string.new_to_bank_if_you_go_back))
                .positiveDismissListener { _, _ ->
                    val intent = Intent(this@PasscodeSuccessUseFingerprintActivity, WelcomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    finish()
                })
    }
}