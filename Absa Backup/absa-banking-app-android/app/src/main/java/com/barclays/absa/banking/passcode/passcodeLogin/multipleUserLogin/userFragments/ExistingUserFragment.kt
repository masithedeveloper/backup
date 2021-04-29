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
package com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.userFragments

import android.annotation.TargetApi
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.constraintlayout.widget.Barrier
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.biometric.BiometricHelper
import com.barclays.absa.banking.boundary.model.UserProfile
import com.barclays.absa.banking.express.data.isBusiness
import com.barclays.absa.banking.framework.BuildConfigHelper
import com.barclays.absa.banking.framework.PermissionFacade
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.passcode.passcodeLogin.ForgotPasscodeActivity
import com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.MultipleUsersFragmentAdapter
import com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.SimplifiedLoginActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.utils.*
import com.barclays.absa.utils.key.KeyTools
import styleguide.buttons.FloatingActionButtonView
import styleguide.forms.KeypadView
import styleguide.forms.OnPasscodeChangeListener
import styleguide.utils.extensions.extractTwoLetterAbbreviation
import styleguide.widgets.RoundedImageView
import java.util.*

class ExistingUserFragment : UserFragment() {

    companion object {
        private const val MINIMUM_IMAGE_BYTE_SIZE = 100

        fun newInstance(pageNumber: Int): ExistingUserFragment = ExistingUserFragment().apply {
            arguments = Bundle().apply {
                putInt(PAGE_NUMBER, pageNumber)
            }
        }
    }

    private lateinit var profilePictureImageView: RoundedImageView
    private lateinit var initialsTextView: TextView
    private lateinit var userMessageTextView: TextView
    private lateinit var arrowUpShowShortcutsImageView: ImageView
    private lateinit var userShortcutViews: List<FloatingActionButtonView>
    private lateinit var loginActionView: FloatingActionButtonView
    private lateinit var fingerprintLoginButton: Button
    private lateinit var arrowDownShowKeypadImageView: ImageView

    private lateinit var passcodeConstraintLayout: ConstraintLayout
    private var constraintPasscodeLayoutHeight: Int = 0

    private lateinit var shortcutList: List<Shortcut>
    private lateinit var currentUserProfile: UserProfile

    private lateinit var customLoginConstraintLayout: ConstraintLayout
    private lateinit var shortcutsConstraintLayout: ConstraintLayout

    private var profileBadge: Bitmap? = null

    lateinit var instructionTextView: TextView
    lateinit var numericKeypad: KeypadView

    private var rootViewGroup: ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.existing_user_fragment, container, false)
        rootViewGroup = container

        currentUserProfile = ProfileManager.getInstance().getSelectedUser(position)
        shortcutList = (AppShortcutsHandler as IAppShortcutsHandler).getCachedUserShortcutList(currentUserProfile.userId).filter { shortcut -> (AppShortcutsHandler as IAppShortcutsHandler).isFeatureVisible(shortcut.featureName) }

        setupViewReferences(rootView)

        if (isFingerprintSupported() && ProfileManager.getInstance().profileCount == 1) {
            useFingerprint = true
            initialiseBiometrics()
        } else {
            disableFingerprint()
        }

        setupExistingUserView()
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setViewClickListeners()
        if (!getSimplifiedLoginActivity().currentShortcutId.isNullOrBlank()) {
            hideShortcutsActiveView()
        }
    }

    private fun setupViewReferences(rootView: View) {
        with(rootView) {
            customLoginConstraintLayout = findViewById(R.id.customLoginConstraintLayout)
            shortcutsConstraintLayout = findViewById(R.id.shortcutViewConstraintLayout)
            profilePictureImageView = findViewById(R.id.profilePictureRoundedImageView)
            initialsTextView = findViewById(R.id.initialsTextView)
            userMessageTextView = findViewById(R.id.userMessageTextView)
            instructionTextView = findViewById(R.id.instructionTextView)
            numericKeypad = findViewById(R.id.numericKeypad)
            arrowUpShowShortcutsImageView = findViewById(R.id.arrowUpShowShortcutsImageView)
            userShortcutViews = listOf(
                    findViewById(R.id.action1FloatingActionButtonView), findViewById(R.id.action2FloatingActionButtonView), findViewById(R.id.action3FloatingActionButtonView),
                    findViewById(R.id.action4FloatingActionButtonView), findViewById(R.id.action5FloatingActionButtonView), findViewById(R.id.action6FloatingActionButtonView),
                    findViewById(R.id.action7FloatingActionButtonView), findViewById(R.id.action8FloatingActionButtonView), findViewById(R.id.action9FloatingActionButtonView),
                    findViewById(R.id.action10FloatingActionButtonView), findViewById(R.id.action11FloatingActionButtonView), findViewById(R.id.action12FloatingActionButtonView))
            loginActionView = findViewById(R.id.actionLoginFloatingActionButtonView)
            passcodeConstraintLayout = findViewById(R.id.passcodeConstraintLayout)
            fingerprintLoginButton = findViewById(R.id.fragmentFingerprintLoginButton)
            arrowDownShowKeypadImageView = findViewById(R.id.arrowDownShowKeypadImageView)

            // Get the constraint height available for the passcodeConstraintLayout
            val topBarrier: Barrier = findViewById(R.id.topBarrier)
            topBarrier.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    constraintPasscodeLayoutHeight = (rootViewGroup?.height ?: 0) - topBarrier.top
                    passcodeConstraintLayout.layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, constraintPasscodeLayoutHeight).apply {
                        topToBottom = R.id.arrowDownShowKeypadImageView
                    }
                    topBarrier.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        numericKeypad.clearPasscode()
    }

    override fun onStop() {
        super.onStop()
        if (isFingerprintSupported()) {
            deactivateFingerprintSensor()
        }
        numericKeypad.clearPasscode()
    }

    private fun setupExistingUserView() {
        MultipleUsersFragmentAdapter.userProfiles.elementAtOrNull(position)?.let { userProfile ->
            val clientType = userProfile.clientType
            var name = userProfile.customerName
            if (clientType != null && clientType.isBusiness()) {
                name = userProfile.customerName
            } else if (name != null && name.contains(" ")) {
                name = name.split(" ").toTypedArray().first()
            }

            profileBadge = getProfileBadge(userProfile)
            profilePictureImageView.contentDescription = userProfile.customerName
            profilePictureImageView.visibility = View.VISIBLE
            initialsTextView.text = userProfile.customerName.extractTwoLetterAbbreviation()
            userMessageTextView.text = if (ProfileManager.getInstance().isLegacyUser) DateUtils.setTimeOfDayGreetingMessage(requireContext()) else name

            handleProfileBadge(profileBadge)
            setPasscodeInstruction()

            with(numericKeypad) {
                setOnPasscodeChangeListener(object : OnPasscodeChangeListener {
                    override fun onCompleted(passcode: String) = onPasscodeEntered(passcode)
                    override fun onChangedPasscode(currentPasscode: String) {}
                    override fun onKeyEntered(currentPasscode: String) {}
                })
                changeForgotPasscodeText(getString(R.string.forgot_passcode))
                setForgotPasscodeOnclickListener { navigateForgotPasscodeActivity() }
                setClearPasscodeOnclickListener { setPasscodeInstruction() }
            }
            setupShortcutViews()
        }
    }

    private fun setupShortcutViews() = showShortcutsActiveView()

    private fun getProfileBadge(userProfile: UserProfile): Bitmap? {
        val imageBytes: ByteArray? = userProfile.imageName?.takeIf { it.isNotEmpty() }?.let { imageName ->
            Base64.decode(imageName, Base64.URL_SAFE)
        }
        return if (imageBytes == null || imageBytes.size < MINIMUM_IMAGE_BYTE_SIZE) null else BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private fun handleProfileBadge(profileBadge: Bitmap?) {
        if (profileBadge == null) {
            initialsTextView.visibility = View.VISIBLE
            profilePictureImageView.visibility = View.GONE
        } else {
            initialsTextView.visibility = View.GONE
            profilePictureImageView.visibility = View.VISIBLE
            ImageUtils.setImageFromBitmap(profilePictureImageView, profileBadge)
        }
    }

    private fun setPasscodeInstruction() = setPasscodeInstruction(getString(R.string.enter_passcode_description))

    private fun setPasscodeInstruction(instruction: String?) {
        view?.findViewById<TextView>(R.id.instructionTextView)?.let { instructionTextView ->
            instructionTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.graphite_light_theme_item_color))
            instructionTextView.text = instruction
        }
    }

    private fun showShortcutsActiveView() {
        val noActiveShortcuts = (shortcutList.isEmpty() || shortcutList.all { shortcut -> !shortcut.isEnabled })
        if (noActiveShortcuts) {
            hideShortcutsActiveView()
            arrowUpShowShortcutsImageView.visibility = View.GONE
            return
        }

        userShortcutViews.forEachIndexed { index, floatingActionButtonView ->
            val shortcut = shortcutList.elementAtOrNull(index)
            if (shortcut != null && shortcut.isEnabled) {
                with(floatingActionButtonView) {
                    setIcon(shortcut.getDrawableResId())
                    setTitleText(shortcut.getNameResId())
                    visibility = View.VISIBLE
                    isEnabled = !AppShortcutsHandler.isFeatureDisabled(shortcut.featureName)
                    setOnClickListener {
                        if (AppShortcutsHandler.isFeatureDisabled(shortcut.featureName)) {
                            startActivity(IntentFactory.outsideAppCapabilityUnavailable(requireActivity(), R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(shortcut.getNameResId()))))
                        } else {
                            getSimplifiedLoginActivity().currentShortcutId = getString(shortcut.getNameResId())
                            hideShortcutsActiveView()
                        }
                    }
                }
            } else {
                floatingActionButtonView.visibility = View.INVISIBLE
            }
        }
    }

    private fun clearShortcutView() {
        getSimplifiedLoginActivity().currentShortcutId = null
        hideShortcutsActiveView()
    }

    private fun hideShortcutsActiveView() {
        if (isFingerprintActivated()) {
            fingerprintLoginButton.visibility = View.VISIBLE
            activateFingerprintSensorIfPossible()
            fingerprintLoginButton.setOnClickListener {
                activateFingerprintSensorIfPossible()
            }
        }
        updateConstraints(R.layout.include_existing_user_view_show_keypad, R.layout.include_shortcuts_off_screen_view)
    }

    private fun updateConstraints(@LayoutRes loginLayoutId: Int, @LayoutRes shortcutsLayoutId: Int) {
        val loginLayoutConstraintSet = ConstraintSet()
        val shortcutLayoutConstraintSet = ConstraintSet()

        loginLayoutConstraintSet.clone(activity, loginLayoutId)
        shortcutLayoutConstraintSet.clone(activity, shortcutsLayoutId)

        loginLayoutConstraintSet.applyTo(customLoginConstraintLayout)
        shortcutLayoutConstraintSet.applyTo(shortcutsConstraintLayout)

        passcodeConstraintLayout.layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, constraintPasscodeLayoutHeight).apply {
            topToBottom = R.id.arrowDownShowKeypadImageView
        }

        val transition = ChangeBounds().apply {
            duration = 500
        }

        rootViewGroup?.let { TransitionManager.beginDelayedTransition(it, transition) }
        handleProfileBadge(profileBadge)
    }

    private fun setViewClickListeners() {
        arrowDownShowKeypadImageView.setOnClickListener {
            clearShortcutView()
            updateConstraints(R.layout.include_existing_user_view_show_keypad, R.layout.include_shortcuts_off_screen_view)
            setActiveShortcutsVisible()
        }

        loginActionView.setOnClickListener {
            clearShortcutView()
            updateConstraints(R.layout.include_existing_user_view_show_keypad, R.layout.include_shortcuts_off_screen_view)
            setActiveShortcutsVisible()
        }

        arrowUpShowShortcutsImageView.setOnClickListener {
            numericKeypad.clearPasscode()
            updateConstraints(R.layout.include_existing_user_view_show_shortcuts, R.layout.include_shortcuts_on_screen_view)
            setActiveShortcutsVisible()
        }
    }

    private fun setActiveShortcutsVisible() {
        userShortcutViews.forEachIndexed { index, floatingActionButtonView ->
            val shortcut = shortcutList.elementAtOrNull(index)
            if (shortcut != null && shortcut.isEnabled) {
                floatingActionButtonView.visibility = View.VISIBLE
            } else {
                floatingActionButtonView.visibility = View.INVISIBLE
            }
        }
    }

    override fun onPageScrollSettled() {

    }

    private fun handleLoginOnDeviceStatePermission() {
        if (useFingerprint) {
            requestFingerprintIfAvailable()
        } else {
            getSimplifiedLoginActivity().executeLoginRequest()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val permissionStatus = grantResults.firstOrNull() ?: return
        when (requestCode) {
            PermissionHelper.PermissionCode.ACCESS_DEVICE_STATE.value -> {
                when (permissionStatus) {
                    PackageManager.PERMISSION_GRANTED -> handleLoginOnDeviceStatePermission()
                    PackageManager.PERMISSION_DENIED -> PermissionFacade.requestDeviceStatePermission(getSimplifiedLoginActivity(), PermissionHelper.OnPermissionGrantListener { handleLoginOnDeviceStatePermission() })
                    else -> {
                    }
                }
            }
            PermissionHelper.PermissionCode.ACCESS_FINGERPRINT.value -> {
                when (permissionStatus) {
                    PackageManager.PERMISSION_GRANTED -> showFingerprintPrompt()
                    PackageManager.PERMISSION_DENIED -> PermissionHelper.requestFingerprintPermission(getSimplifiedLoginActivity()) { showFingerprintPrompt() }
                    else -> {
                    }
                }
            }
        }
    }

    private fun onPasscodeEntered(passcode: String) {
        getSimplifiedLoginActivity().enteredPasscode = passcode
        if (getSimplifiedLoginActivity().isGenerateTokenOffline) {
            val otp = getSimplifiedLoginActivity().generateOfflineToken()
            getSimplifiedLoginActivity().showMessage(getString(R.string.surecheck_token), getString(R.string.use_this_surecheck_token) + "\n\n" + otp) { _: DialogInterface?, _: Int -> getSimplifiedLoginActivity().finish() }
        } else {
            appCacheService.setPasscode(passcode)
            getSimplifiedLoginActivity().userSecret = passcode
            performAuthentication()
        }
    }

    private fun performAuthentication() {
        SharedPreferenceService.setCanUpdateFuneralCoverDisplayCount(true)
        SharedPreferenceService.setCanAnimateFuneralTile(true)
        PermissionFacade.requestDeviceStatePermission(getSimplifiedLoginActivity(), PermissionHelper.OnPermissionGrantListener {
            if (!BuildConfigHelper.STUB && !NetworkUtils.isNetworkConnected()) {
                getSimplifiedLoginActivity().navigateNoConnectionGenerateTokenActivity()
            } else {
                getSimplifiedLoginActivity().expressLogin()
            }
        })
    }

    private fun navigateForgotPasscodeActivity() = startActivity(Intent(activity, ForgotPasscodeActivity::class.java).apply {
        putExtra(SimplifiedLoginActivity.USER_POSITION, position)
    })

    // Fingerprint
    // -----------------------------------------------------------------------------------------------------------------
    @TargetApi(Build.VERSION_CODES.M)
    private fun initialiseBiometrics() {
        biometricHelper = BiometricHelper(this, object : BiometricHelper.Callback {
            override fun onAuthenticated(cryptoObject: BiometricPrompt.CryptoObject?) {
                BMBLogger.d(SimplifiedLoginActivity.TAG + "x-f -  fingerprint authentication succeeded", "@" + Date().time)
                if (cryptoObject == null) {
                    getSimplifiedLoginActivity().showGenericErrorMessageThenFinish()
                    return
                }

                PermissionFacade.requestDeviceStatePermission(getSimplifiedLoginActivity(), PermissionHelper.OnPermissionGrantListener {
                    if (!BuildConfigHelper.STUB && !NetworkUtils.isNetworkConnected()) {
                        getSimplifiedLoginActivity().navigateNoConnectionGenerateTokenActivity()
                    } else {
                        getSimplifiedLoginActivity().isPerformingFingerprintLogin = true
                        getSimplifiedLoginActivity().enteredPasscode = ""
                        try {
                            val userProfile = profileManager.activeUserProfile
                            val zeroKeyEncryptedAlias = getSimplifiedLoginActivity().symmetricCryptoHelper.retrieveAlias(userProfile.userId)
                            val decryptedAlias = getSimplifiedLoginActivity().symmetricCryptoHelper.decryptAliasWithZeroKey(zeroKeyEncryptedAlias)
                            if (decryptedAlias != null) {
                                if (userProfile.migrationVersion > 1) {
                                    val biometricCipherEncryptedRandomAliasId = userProfile.randomAliasId ?: userProfile.fingerprintId
                                    if (biometricCipherEncryptedRandomAliasId != null) {
                                        BMBLogger.d(SimplifiedLoginActivity.TAG, "SLA biometricCipherEncryptedRandomAliasId: " + String(biometricCipherEncryptedRandomAliasId))
                                        val decryptedRandomAliasId = cryptoObject.cipher?.doFinal(biometricCipherEncryptedRandomAliasId) ?: ByteArray(0)
                                        getSimplifiedLoginActivity().userSecret = String(decryptedRandomAliasId)
                                    }
                                } else {
                                    getSimplifiedLoginActivity().userSecret = String(userProfile.randomAliasId ?: userProfile.fingerprintId ?: "".toByteArray())
                                }
                                useFingerprint = false
                                performAuthentication()
                            }
                        } catch (e: Exception) {
                            BMBLogger.e(SimplifiedLoginActivity.TAG, e.message)
                            getSimplifiedLoginActivity().showGenericErrorMessageThenFinish()
                        }
                    }
                })
            }

            override fun onError(errorCode: Int) {
                BMBLogger.d(SimplifiedLoginActivity.TAG + "x-f -  we have an authentication error", "@" + Date().time + "retries " + fingerPrintRetries)
                if (errorCode == BiometricPrompt.ERROR_LOCKOUT) {
                    showFingerprintLockedState()
                }
            }

            override fun onAuthenticationFailed() {
                BMBLogger.d(SimplifiedLoginActivity.TAG + "x-f -  authentication failed", "@" + Date().time + "retries " + fingerPrintRetries)
                --fingerPrintRetries
                if (fingerPrintRetries == 0) {
                    showFingerprintLockedState()
                }
            }
        })
    }

    private fun isFingerprintSupported(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getSimplifiedLoginActivity().packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)

    private fun activateFingerprintSensorIfPossible() {
        if (!getSimplifiedLoginActivity().isFinishing) {
            activateFingerprintSensor()
        }
    }

    private fun activateFingerprintSensor() {
        if (isFingerprintSupported() && isFingerprintActivated()) {
            try {
                val cipher = getSimplifiedLoginActivity().simplifiedAuthenticationHelper.cipher
                if (cipher == null) {
                    deactivateFingerprintSensor()
                    if (getSimplifiedLoginActivity().userProfiles.isEmpty()) {
                        profileManager.loadAllUserProfiles(object : ProfileManager.OnProfileLoadListener {
                            override fun onAllProfilesLoaded(userProfiles: List<UserProfile>) {
                                getSimplifiedLoginActivity().userProfiles = userProfiles
                                userProfiles.elementAtOrNull(position)?.let { userProfile -> profileManager.activeUserProfile = userProfile }
                            }

                            override fun onProfilesLoadFailed() {}
                        })
                    }
                } else {
                    BMBLogger.d("x-f", "activate finger print")
                    biometricHelper?.authenticate(cipher)
                }
            } catch (e: KeyTools.KeyToolsException) {
                BMBLogger.d("x-f", "KeyToolsException")
                if (BuildConfig.DEBUG) {
                    Log.e(BMBConstants.LOGTAG, "Failed to initialise the fingerprint manager crypto object", e)
                }
                getSimplifiedLoginActivity().showGenericErrorMessageThenFinish()
            } catch (e: Exception) {
                BMBLogger.e("x-f", e.message)
                getSimplifiedLoginActivity().showGenericErrorMessageThenFinish()
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun requestFingerprintIfAvailable() {
        if (isFingerprintSupported()) {
            if (getSimplifiedLoginActivity().isSingleUserProfile()) {
                PermissionHelper.requestFingerprintPermission(getSimplifiedLoginActivity()) { showFingerprintPrompt() }
            } else {
                disableFingerprint()
            }
        }
    }

    private fun showFingerprintLockedState() {
        getSimplifiedLoginActivity().toastLong(R.string.too_many_attempts)
        fingerprintLoginButton.visibility = View.GONE
        deactivateFingerprintSensor()
    }

    private fun disableFingerprint() {
        useFingerprint = false
        fingerprintLoginButton.visibility = View.GONE
    }

    private fun showFingerprintPrompt() {
        if (isFingerprintActivated() && !getSimplifiedLoginActivity().isGenerateTokenOffline) {
            useFingerprint = true
        } else {
            useFingerprint = false
            fingerprintLoginButton.visibility = View.GONE
        }
    }

    private fun isFingerprintActivated(): Boolean = UserSettingsManager.isFingerprintActive() && BiometricManager.from(getSimplifiedLoginActivity()).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
}
