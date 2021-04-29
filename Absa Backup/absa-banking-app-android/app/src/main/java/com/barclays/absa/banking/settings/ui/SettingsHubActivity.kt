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
package com.barclays.absa.banking.settings.ui

import android.annotation.TargetApi
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricPrompt
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import com.barclays.absa.banking.R
import com.barclays.absa.banking.biometric.BiometricHelper
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.boundary.model.UserProfile
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.express.biometricAuthentication.BiometricAuthenticationToggler
import com.barclays.absa.banking.express.languageUpdate.LanguageUpdateViewModel
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants.*
import com.barclays.absa.banking.framework.app.ScreenshotHelper
import com.barclays.absa.banking.framework.app.ScreenshotHelper.registerShakeListener
import com.barclays.absa.banking.framework.app.ScreenshotHelper.setupShakeDetection
import com.barclays.absa.banking.framework.app.ScreenshotHelper.unregisterShakeListener
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.home.ui.IHomeCacheService
import com.barclays.absa.banking.passcode.PasscodeActivity
import com.barclays.absa.banking.passcode.PasscodeForResultActivity
import com.barclays.absa.banking.settings.ui.customisation.CustomiseLoginActivity
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog.dismissAlertDialog
import com.barclays.absa.banking.shared.BaseAlertDialog.showAlertDialog
import com.barclays.absa.banking.shared.BaseAlertDialog.showYesNoDialog
import com.barclays.absa.utils.*
import com.barclays.absa.utils.imageHelpers.ImageHelper
import com.barclays.absa.utils.imageHelpers.ProfileViewImageHelper
import com.barclays.absa.utils.imageHelpers.ProfileViewImageHelper.PROFILE_IMAGE_REQUEST
import com.barclays.absa.utils.imageHelpers.ProfileViewImageHelper.PROFILE_IMAGE_REQUEST_AFTER_CROP
import com.barclays.absa.utils.key.KeyTools
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.settings_hub_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import styleguide.bars.ToggleView
import styleguide.content.Profile
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import java.io.IOException
import java.lang.ref.WeakReference
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException

class SettingsHubActivity : BaseActivity(R.layout.settings_hub_activity), SettingsHubView {

    companion object {
        private val TAG = SettingsHubActivity::class.java.simpleName
        private const val RECREATED_EXTRA = "recreated"
        private const val PUSH_NOTIFICATIONS_ENABLED = "pushNotificationEnabled"
        private const val FINGERPRINT_ENABLED = "fingerprintEnabled"
        private const val SOUND_ENABLED = "soundEnabled"
        private const val SCREENSHOT_ENABLED = "screenshotEnabled"
        private const val DARK_MODE_ENABLED = "darkModeEnabled"

        private const val DEFAULT_BACKGROUND_IMAGE = "1"
        private const val AFRIKAANS_LANGUAGE_INDEX = 0
        private const val ENGLISH_LANGUAGE_INDEX = 1

        private const val REQUESTCODE_SECURITY_SETTINGS: Int = 5001
        private const val REQUESTCODE_BIOMETRIC_ENROLLMENT: Int = 5002
        private const val REQUESTCODE_PASSCODE_TO_ENABLE_BIOMETRICS: Int = 5003
    }

    private lateinit var biometricHelper: BiometricHelper
    private lateinit var presenter: SettingsHubPresenter
    private lateinit var profileImageHelper: ProfileViewImageHelper

    private val profileManager: ProfileManager = ProfileManager.getInstance()
    private var customerProfileInstance = CustomerProfileObject.instance

    private var cipher: Cipher? = null
    private var languageCode: Char = ' '
    private var languageUpdated = false
    private var shouldDoBiometricsOnResume = true
    private lateinit var newRandomUserId: ByteArray
    private val homeCacheService: IHomeCacheService = getServiceInterface()
    private val userSettingsManager: IUserSettingsManager = UserSettingsManager

    private val onPushNotificationCheckedListener = ToggleView.OnCustomCheckChangeListener { _, isChecked ->
        run {
            if (isChecked) {
                togglePushNotifications(isChecked)
                userSettingsManager.setNotificationServicesEnabled(true)
            } else {
                showPushNotificationWarningDialog(isChecked)
            }
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSiteSection = SETTINGS_CONST
        mScreenName = mSiteSection
        AnalyticsUtils.getInstance().trackCustomScreenView(SETTINGS_CONST, SETTINGS_CONST, TRUE_CONST)

        presenter = SettingsHubPresenter(WeakReference(this))
        setToolBarBack(R.string.navigation_menu_app_preferences)

        populateUIComponents()

        if (intent.getBooleanExtra(RECREATED_EXTRA, false)) {
            AbsaCacheManager.getInstance().setAccountsCacheStatus(false)
            languageUpdated = true
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            initialiseBiometrics()
        } else {
            fingerprintLogInToggleView.visibility = View.GONE
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun initialiseBiometrics() {
        biometricHelper = BiometricHelper(this, object : BiometricHelper.Callback {
            override fun onAuthenticated(cryptoObject: BiometricPrompt.CryptoObject?) {
                profileManager.activeUserProfile.let {
                    try {
                        newRandomUserId = profileManager.generateRandomUserId().toByteArray()
                        BMBLogger.d("SettingsHubActivity", "SH newRandomUserId (not encrypted): " + String(newRandomUserId))
                        BMBLogger.i("SettingsHub::newRandomUserId", String(newRandomUserId))
                        cryptoObject?.cipher?.iv?.let { iv -> SharedPreferenceService.setKeyToolsIv(iv) }
                        it.fingerprintId = cryptoObject?.cipher?.doFinal(newRandomUserId)
                        BMBLogger.i("SettingsHub::initialiseBiometrics", String(it.fingerprintId!!))
                        BMBLogger.d("SettingsHubActivity", "SH profile-assigned randomUserId (encrypted): " + String(it.fingerprintId!!))
                        it.randomAliasId = it.fingerprintId
                        it.migrationVersion = UserProfile.CURRENT_PROFILE_MIGRATION_VERSION
                        profileManager.updateProfile(it, object : ProfileManager.OnProfileUpdateListener {
                            override fun onProfileUpdated(userProfile: UserProfile?) {
                                shouldDoBiometricsOnResume = false
                                startActivityForResult(Intent(this@SettingsHubActivity, PasscodeForResultActivity::class.java), REQUESTCODE_PASSCODE_TO_ENABLE_BIOMETRICS)
                            }

                            override fun onProfileUpdateFailed() {
                                showGenericErrorMessageThenFinish()
                            }
                        })
                    } catch (e: BadPaddingException) {
                        BMBLogger.e(e.message)
                    } catch (e: IllegalBlockSizeException) {
                        BMBLogger.e(e.message)
                    }
                }
            }

            override fun onError(errorCode: Int) {
                if (!userSettingsManager.isFingerprintActive()) {
                    fingerprintLogInToggleView.isChecked = false
                }
            }

            override fun onAuthenticationFailed() {
                if (!userSettingsManager.isFingerprintActive()) {
                    fingerprintLogInToggleView.isChecked = false
                }
            }
        })
    }

    private fun registerNewBiometrics() {
        BMBLogger.i("SettingsHub::registerNewBiometrics", String(newRandomUserId))
        BiometricAuthenticationToggler.enableBiometrics(
                this@SettingsHubActivity,
                newRandomUserId, {
            userSettingsManager.setFingerprintActive(true)
            logoutAndGoToStartScreen()
        }, {
            userSettingsManager.setFingerprintActive(false)
            fingerprintLogInToggleView.isChecked = false
        })
    }

    private fun populateUIComponents() {
        initializeProfileImageViewHelper()
        setProfileView()
        setLanguage()
        setPushNotification()
        setAppSounds()
        setDarkMode()
        setScreenshotsAllowed()
        setCustomiseLoginScreen()
    }

    private fun initializeProfileImageViewHelper() {
        //TODO: Check with BMG as this does not work in UAT-i/Prod 12:00 6 June 2018
        profileImageHelper = ProfileViewImageHelper(this, currentProfileView).apply {
            setOnImageActionListener { ImageHelper.onImageActionListener { sendProfileSetupRequest() } }
        }
    }

    override fun onResume() {
        super.onResume()
        if (shouldDoBiometricsOnResume) {
            setFingerprintToggleView()
        }
        // this method is placed here as the listener needs to be invoked after the restore (recreate)
        pushNotificationToggleView.setOnCustomCheckChangeListener(onPushNotificationCheckedListener)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        pushNotificationToggleView.isChecked = savedInstanceState.getBoolean(PUSH_NOTIFICATIONS_ENABLED)
        fingerprintLogInToggleView.isChecked = savedInstanceState.getBoolean(FINGERPRINT_ENABLED)
        playSoundsToggleView.isChecked = savedInstanceState.getBoolean(SOUND_ENABLED)
        takeScreenshotsToggleView.isChecked = savedInstanceState.getBoolean(SCREENSHOT_ENABLED)
        darkModeToggleView.isChecked = savedInstanceState.getBoolean(DARK_MODE_ENABLED)

        if (customerProfileInstance.languageCode == "E") {
            languageInputView.text = getString(R.string.english)
        }
    }

    @Synchronized
    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putBoolean(PUSH_NOTIFICATIONS_ENABLED, pushNotificationToggleView.isChecked)
            putBoolean(FINGERPRINT_ENABLED, pushNotificationToggleView.isChecked)
            putBoolean(SOUND_ENABLED, playSoundsToggleView.isChecked)
            putBoolean(SCREENSHOT_ENABLED, takeScreenshotsToggleView.isChecked)
            putBoolean(DARK_MODE_ENABLED, darkModeToggleView.isChecked)
        }
        super.onSaveInstanceState(outState)
    }

    private fun setLanguage() {
        val customerLanguageCode = customerProfileInstance.languageCode
        val newSelectedIndex = if (AFRIKAANS_LANGUAGE.toString().equals(customerLanguageCode, ignoreCase = true))
            AFRIKAANS_LANGUAGE_INDEX else ENGLISH_LANGUAGE_INDEX

        val stringItems = SelectorList<StringItem>().apply {
            add(StringItem(getString(R.string.afrikaansLanguage)))
            add(StringItem(getString(R.string.englishLanguage)))
        }

        languageInputView.apply {
            setItemSelectionInterface { index ->
                languageCode = if (AFRIKAANS_LANGUAGE_INDEX == index) AFRIKAANS_LANGUAGE else ENGLISH_LANGUAGE
                if (!customerLanguageCode.equals(languageCode.toString(), true) && !isFinishing) {
                    showProgressDialog()
                    Handler(Looper.getMainLooper()).postDelayed({ presenter.requestLanguageUpdate(languageCode) }, 250)
                }
            }
            setList(stringItems, getString(R.string.settings_language))
            selectedIndex = newSelectedIndex
        }
    }

    override fun responseLanguageUpdate(isSuccess: Boolean) {
        pushNotificationToggleView.setOnCustomCheckChangeListener(null)
        val locale = if (languageCode == ENGLISH_LANGUAGE) ENGLISH_CODE else AFRIKAANS_CODE
        BMBApplication.getInstance().updateLanguage(this, locale)
        profileManager.activeUserProfile.let {
            it.languageCode = languageCode.toString()
            profileManager.updateProfile(it, null)
        }

        dismissProgressDialog()
        intent.putExtra(RECREATED_EXTRA, true)
        recreate()
    }

    private fun setProfileView() {
        val customerName = customerProfileInstance.customerName
        val customerAccountType = ProfileViewHelper.getCustomerAccountType(this)
        currentProfileView.setProfile(Profile(customerName, customerAccountType, profileImageHelper.imageBitmap))
    }

    private fun setPushNotification() {
        val notificationsDisabled = !NotificationManagerCompat.from(this).areNotificationsEnabled()
        pushNotificationToggleView.apply {
            isChecked = userSettingsManager.isNotificationServicesEnabled()
            if (notificationsDisabled) {
                isChecked = false
                isEnabled = false
                FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                    presenter.switchOffPushNotifications(it.token)
                }
            }
        }
    }

    private fun setAppSounds() {
        playSoundsToggleView.apply {
            isChecked = userSettingsManager.isSoundEnabled()
            setOnCustomCheckChangeListener { compoundButton, isChecked ->
                if (compoundButton.isPressed) {
                    userSettingsManager.setSoundEnabled(isChecked)
                }
            }
        }
    }

    private fun setScreenshotsAllowed() {
        val featureSwitching = FeatureSwitchingCache.featureSwitchingToggles
        featureSwitching.let {
            if (it.screenshots == FeatureSwitchingStates.ACTIVE.key && ScreenshotHelper.hasAccelerometer()) {
                takeScreenshotsToggleView.apply {
                    visibility = View.VISIBLE
                    isChecked = userSettingsManager.isScreenshotsAllowed()
                    setOnCustomCheckChangeListener { compoundButton, enabled ->
                        if (!compoundButton.isPressed) {
                            return@setOnCustomCheckChangeListener
                        }
                        if (enabled) {
                            showMessage(getString(R.string.screenshot), getString(R.string.screenshot_hint_text)) { _, _ ->
                                setupShakeDetection()
                                registerShakeListener()
                            }

                            setBottomMessage(getString(R.string.screenshot_hint_text))
                        } else {
                            unregisterShakeListener()
                            removeBottomMessage()
                        }
                        userSettingsManager.setScreenshotsAllowed(enabled)
                    }
                }
            } else {
                takeScreenshotsToggleView.visibility = View.GONE
            }
        }
    }

    private fun setCustomiseLoginScreen() {
        if (!CustomiseLoginActivity.isCustomisedLoginDisabled()) {
            dividerView.visibility = View.VISIBLE
            with(customiseLoginScreenOptionActionButtonView) {
                visibility = View.VISIBLE
                setOnClickListener {
                    startActivity(Intent(context, CustomiseLoginActivity::class.java))
                }
            }
        }
    }

    private fun setDarkMode() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            darkModeToggleView.visibility = View.GONE
            return
        }
        darkModeToggleView.apply {
            visibility = View.VISIBLE
            val darkModeSetting = userSettingsManager.getDarkModeSetting()
            isChecked = darkModeSetting == AppCompatDelegate.MODE_NIGHT_YES || darkModeSetting == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM && CommonUtils.isDarkMode(context)
            setOnCustomCheckChangeListener { compoundButton, enabled ->
                if (!compoundButton.isPressed) {
                    return@setOnCustomCheckChangeListener
                }
                val mode = if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                userSettingsManager.setDarkModeSetting(mode)
                AppCompatDelegate.setDefaultNightMode(mode)
                recreate()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PROFILE_IMAGE_REQUEST -> profileImageHelper.cropThumbnail(data)
                PROFILE_IMAGE_REQUEST_AFTER_CROP -> {
                    profileImageHelper.retrieveThumbnail(data)
                    sendProfileSetupRequest()
                }
                REQUESTCODE_SECURITY_SETTINGS -> checkForFingerPrintAndAuthenticate()
                REQUESTCODE_BIOMETRIC_ENROLLMENT -> checkForFingerPrintAndAuthenticate()
                REQUESTCODE_PASSCODE_TO_ENABLE_BIOMETRICS -> {
                    appCacheService.setPasscode(data?.getStringExtra(PasscodeActivity.SET_PASSCODE) ?: "")
                    registerNewBiometrics()
                }
            }
        }
    }

    private fun checkForFingerPrintAndAuthenticate() {
        if (!BiometricUtil.noFingerPrintHardwareDetected() && !BiometricUtil.noEnrolledFingerPrints()) {
            cipher = KeyTools.newInstance(BMBApplication.getInstance())?.cipherNew
            cipher?.let { biometricHelper.authenticate(it) }
        }
    }

    private fun sendProfileSetupRequest() = CustomerProfileObject.instance.let {
        presenter.fireProfileSetupRequest(it.languageCode, it.customerName, DEFAULT_BACKGROUND_IMAGE, profileImageHelper)
    }

    override fun switchOffPushNotification(successResponse: TransactionResponse) {
        BMBLogger.d(SettingsHubActivity.TAG, "Push toggle changed: $successResponse")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        setPushNotificationsEnabled(false)
        toastLong(R.string.settings_push_notifications_switched_off_message)
    }

    override fun policyListUpdated() {
        loadAccountsAndGoHome()
    }

    override fun onProfileImageViewInvalidate(imageName: String) {
        mScreenName = EDIT_PHOTO_CONST
        mSiteSection = SETTINGS_CONST
        AnalyticsUtils.getInstance().trackCustomScreenView(mScreenName, mSiteSection, TRUE_CONST)

        setProfileView()
    }

    private fun togglePushNotifications(isPushNotificationsEnabled: Boolean) {
        mSiteSection = SETTINGS_CONST
        mScreenName = mSiteSection

        AnalyticsUtils.getInstance().trackCustomScreenView(mScreenName, "PUSH_SETTINGS_TOGGLE", TRUE_CONST)

        if (!userSettingsManager.shouldRegisterPushNotificationIdWithBackend()) {
            FirebaseInstanceId.getInstance().let { firebaseInstanceId ->
                if (isPushNotificationsEnabled) {
                    setPushNotificationsEnabled(true)
                } else {
                    firebaseInstanceId.instanceId.addOnSuccessListener {
                        presenter.switchOffPushNotifications(it.token)
                    }
                }
            }
        }
    }

    private fun showPushNotificationWarningDialog(isPushNotificationsEnabled: Boolean) {
        showYesNoDialog(AlertDialogProperties.Builder()
                .title(getString(R.string.disable_notification_dialog_title))
                .message(getString(R.string.disable_notification_dialog_message))
                .positiveDismissListener { _, _ -> togglePushNotifications(isPushNotificationsEnabled) }
                .negativeDismissListener { _, _ ->
                    pushNotificationToggleView.apply {
                        setOnCustomCheckChangeListener(null)
                        isChecked = true
                        setOnCustomCheckChangeListener(onPushNotificationCheckedListener)
                    }
                })
    }

    private fun setFingerprintToggleView() {
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            PermissionHelper.requestFingerprintPermission(this) {
                enableBiometricSettings()
            }
        } else {
            disableFingerprintLogInToggleView(getString(R.string.unsupported_prompt_text))
        }
    }

    private fun disableFingerprintLogInToggleView(message: String) {
        fingerprintLogInToggleView.apply {
            isChecked = false
            setBottomMessage(message)
            setOnCustomCheckChangeListener { compoundButton, isChecked ->
                if (isChecked && compoundButton.isPressed) {
                    showYesNoDialog(AlertDialogProperties.Builder()
                            .message(getString(R.string.text_turnon_fingerprint_authentication_on_phone))
                            .positiveDismissListener { _, _ ->
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    startFingerprintEnrollment()
                                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    gotoSecuritySettings()
                                }
                            }.negativeDismissListener { _, _ ->
                                fingerprintLogInToggleView.isChecked = false
                                dismissAlertDialog()
                            })
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private fun startFingerprintEnrollment() {
        try {
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(Intent(Settings.ACTION_FINGERPRINT_ENROLL), REQUESTCODE_BIOMETRIC_ENROLLMENT)
            } else {
                gotoSecuritySettings()
            }
        } catch (e: Exception) {
            BMBLogger.e(e.toString())
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun gotoSecuritySettings() {
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(Intent(Settings.ACTION_SECURITY_SETTINGS), REQUESTCODE_SECURITY_SETTINGS)
        }
    }

    private fun enableBiometricSettings() {
        when {
            BiometricUtil.noFingerPrintHardwareDetected() -> fingerprintLogInToggleView.visibility = View.GONE
            BiometricUtil.noEnrolledFingerPrints() -> setViewNoFingerPrintEnrolled()
            else -> {
                val profileCount = profileManager.profileCount
                try {
                    cipher = KeyTools.newInstance(BMBApplication.getInstance())?.encryptionCipher
                } catch (e: KeyTools.KeyToolsException) {
                    BMBLogger.e(LOGTAG, "Failed to initialize key tools")
                }

                when {
                    cipher == null -> {
                        BiometricAuthenticationToggler.disableBiometrics(this) {
                            userSettingsManager.setFingerprintActive(false)
                            dismissProgressDialog()
                            enableBiometricsIfNecessary()
                        }
                    }
                    profileCount == 1 -> {
                        fingerprintLogInToggleView.apply {
                            isChecked = userSettingsManager.isFingerprintActive()
                            setOnCustomCheckChangeListener { compoundButton, isChecked ->
                                if (!compoundButton.isPressed) {
                                    return@setOnCustomCheckChangeListener
                                }
                                if (profileManager.isFingerprintRegistered) {
                                    userSettingsManager.setFingerprintActive(isChecked)
                                }
                                toggleBiometricUsage(isChecked)
                            }
                        }
                        PermissionHelper.requestFingerprintPermission(this) { fingerprintNotSupported() }
                    }
                    profileCount > 1 -> {
                        disableFingerprintLogInToggleView(getString(R.string.fingerprint_disabled_prompt_text))
                        fingerprintLogInToggleView.isEnabled = false
                    }
                }
            }
        }
    }

    private fun enableBiometricsIfNecessary() {
        fingerprintLogInToggleView.apply {
            isChecked = userSettingsManager.isFingerprintActive()
            setOnCustomCheckChangeListener { compoundButton, isChecked ->
                if (isChecked && compoundButton.isPressed) {
                    displayFingerPrintEnablingDialog()
                }
            }
        }
        PermissionHelper.requestFingerprintPermission(this) { fingerprintNotSupported() }
    }

    private fun setViewNoFingerPrintEnrolled() = disableFingerprintLogInToggleView(getString(R.string.text_turnon_fingerprint_authentication_on_phone))

    private fun fingerprintNotSupported() {
        if (BiometricUtil.noFingerPrintHardwareDetected()) {
            disableFingerprintLogInToggleView(getString(R.string.unsupported_prompt_text))
        }
    }

    private fun toggleBiometricUsage(isFingerprintCheckBoxChecked: Boolean) {
        val randomAliasId = profileManager.activeUserProfile.randomAliasId
        if (isFingerprintCheckBoxChecked) {
            if (randomAliasId == null || randomAliasId.isEmpty()) {
                showFingerprintSetupWithoutAliasIdDialog()
            } else if (!profileManager.isFingerprintRegistered && profileManager.profileCount == 1) {
                showFingerprintSetupWithAliasIdDialog()
            } else {
                showFingerprintSetupWithoutAliasIdDialog()
            }
        } else if (randomAliasId != null && randomAliasId.isNotEmpty()) {
            BiometricAuthenticationToggler.disableBiometrics(this) {
                shouldDoBiometricsOnResume = true
                dismissProgressDialog()
            }
        }
    }

    private fun showFingerprintSetupWithoutAliasIdDialog() {
        showAlertDialog(AlertDialogProperties.Builder()
                .title(getString(R.string.fingerprint_setup_dialog_title))
                .message(getString(R.string.fingerprint_setup_dialog_message))
                .negativeButton(getString(R.string.cancel))
                .positiveDismissListener { _, _ ->
                    val keyTools = KeyTools.newInstance(this@SettingsHubActivity)
                    cipher = keyTools.encryptionCipher ?: keyTools.cipherNew
                    cipher?.let { biometricHelper.authenticate(it) }
                }
                .negativeDismissListener { _, _ ->
                    fingerprintLogInToggleView.isChecked = false
                    userSettingsManager.setFingerprintActive(false)
                    dismissAlertDialog()
                }
                .build())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) = profileImageHelper.handlePermissionResults(requestCode, grantResults)

    private fun showFingerprintSetupWithAliasIdDialog() {
        // TODO: Implement Register fingerprint service once it works.
        displayFingerPrintEnablingDialog()
    }

    private fun setPushNotificationsEnabled(isEnabled: Boolean) {
        userSettingsManager.setShouldRegisterPushNotificationIdWithBackend(isEnabled)
        userSettingsManager.setNotificationServicesEnabled(isEnabled)
    }

    override fun onBackPressed() {
        if (languageUpdated) {
            homeCacheService.setInsurancePolicies(emptyList())

            val languageUpdateViewModel: LanguageUpdateViewModel = viewModel()
            val language = if (customerProfileInstance.languageCode == "A") "af" else "en"

            languageUpdateViewModel.updateLanguage(language)

            languageUpdateViewModel.languageUpdateLiveData.observe(this, {
                presenter.fetchAccountsAndPolicies()
            })

            languageUpdateViewModel.failureLiveData.observe(this, {
                logoutAndGoToStartScreen()
            })
        } else {
            finish()
        }
    }

    private fun displayFingerPrintEnablingDialog() {
        showYesNoDialog(AlertDialogProperties.Builder()
                .title(getString(R.string.new_fingerprints))
                .message(getString(R.string.new_fingerprints_added) + "\n" + getString(R.string.new_fingerprints_are_you_sure))
                .positiveDismissListener { _: DialogInterface, _: Int ->
                    cipher = KeyTools.newInstance(BMBApplication.getInstance())?.cipherNew
                    cipher?.let { biometricHelper.authenticate(it) }
                }.negativeDismissListener { _: DialogInterface?, _: Int ->
                    fingerprintLogInToggleView.isChecked = false
                    dismissAlertDialog()
                })
    }
}