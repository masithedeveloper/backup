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
package com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.boundary.model.SecureHomePageObject
import com.barclays.absa.banking.boundary.model.UserProfile
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor
import com.barclays.absa.banking.boundary.monitoring.MonitoringService
import com.barclays.absa.banking.boundary.pushNotifications.PushNotificationInteractor
import com.barclays.absa.banking.boundary.shared.dto.SecondFactorState
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.deviceLinking.ui.TermsAndConditionsSelectorActivity
import com.barclays.absa.banking.express.authentication.login.AuthenticationStatusCodes
import com.barclays.absa.banking.framework.*
import com.barclays.absa.banking.framework.BuildConfigHelper.activateStub
import com.barclays.absa.banking.framework.BuildConfigHelper.activeUat2
import com.barclays.absa.banking.framework.BuildConfigHelper.activeUatI
import com.barclays.absa.banking.framework.BuildConfigHelper.activeUatR
import com.barclays.absa.banking.framework.SessionManager.resetSession
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants.*
import com.barclays.absa.banking.framework.app.ScreenshotHelper
import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.banking.framework.push.CustomTags
import com.barclays.absa.banking.framework.push.PushMessageListener
import com.barclays.absa.banking.framework.push.PushMessageListener.IN_APP_NOTIFICATION_IDENTIFIER
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktDelegate.CREDENTIAL_TYPE_5_DIGIT_PASSCODE
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktDelegate.CREDENTIAL_TYPE_BIOMETRIC
import com.barclays.absa.banking.framework.utils.AppConstants
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.framework.utils.TelephoneUtil
import com.barclays.absa.banking.linking.ui.LinkingActivity
import com.barclays.absa.banking.login.ui.RootTamperActivity
import com.barclays.absa.banking.login.ui.passcode.SimplifiedAuthenticationHelper
import com.barclays.absa.banking.passcode.PasscodeLockedActivity
import com.barclays.absa.banking.passcode.passcodeLogin.ExpressAuthenticationHelper
import com.barclays.absa.banking.passcode.passcodeLogin.ExpressAuthenticationHelper.LoginCallBack
import com.barclays.absa.banking.passcode.passcodeLogin.ExpressAuthenticationHelper.UserProfileCallBack
import com.barclays.absa.banking.passcode.passcodeLogin.PasscodeLoginView
import com.barclays.absa.banking.passcode.passcodeLogin.RemoveAccountConfirmationActivity
import com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.userFragments.ExistingUserFragment
import com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.userFragments.UserFragment
import com.barclays.absa.banking.passcode.transformer.ZoomPageTransformer
import com.barclays.absa.banking.presentation.contactUs.ContactUsActivity
import com.barclays.absa.banking.presentation.generateTokens.NoConnectionGenerateTokenActivity
import com.barclays.absa.banking.presentation.generateTokens.OfflineOtpGenerator
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.banking.presentation.launch.SplashActivity
import com.barclays.absa.banking.presentation.multipleUsers.MultipleUsersListActivity
import com.barclays.absa.banking.presentation.sessionTimeout.SessionTimeOutDialogActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity
import com.barclays.absa.banking.presentation.whatsNew.WhatsNewHelper.getEnabledWhatsScreens
import com.barclays.absa.banking.presentation.whatsNew.WhatsNewLottieActivity
import com.barclays.absa.banking.registration.RegisterCreatePasswordActivity
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog.showAlertDialog
import com.barclays.absa.crypto.SecureUtils.getDeviceID
import com.barclays.absa.crypto.SymmetricCryptoHelper
import com.barclays.absa.crypto.SymmetricCryptoHelper.KeyStoreEntryAccessException
import com.barclays.absa.utils.*
import com.barclays.absa.utils.ProfileManager.OnProfileCreateListener
import com.barclays.absa.utils.ProfileManager.OnProfileLoadListener
import com.google.firebase.iid.FirebaseInstanceId
import com.newrelic.agent.android.NewRelic
import kotlinx.android.synthetic.main.activity_simplified_login.*
import styleguide.forms.ItemCheckedInterface
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import za.co.absa.networking.ExpressNetworkingConfig
import java.util.*
import kotlin.collections.ArrayList

class SimplifiedLoginActivity : BaseActivity(), PasscodeLoginView {

    companion object {
        val TAG: String = SimplifiedLoginActivity::class.java.simpleName
        private const val ALIAS_NOT_FOUND_RESPONSE_CODE = "9003"
        private const val DELETE_USER_REQUEST_CODE = 1
        private const val CLOSE_APP = 999
        const val MAXIMUM_PER_DEVICE_USER_PROFILES = 9999
        const val USER_POSITION = "userPosition"
        const val USER_PROFILE = "userProfile"
        const val SHORTCUT = "shortcut"
        const val ACCOUNT_ERROR_CODE = "E305"
        const val INVALID_CREDENTIALS = "Security/LogontoAbsaOnline/Error/InvalidCredentails"
    }

    private val profileManager: ProfileManager = ProfileManager.getInstance()
    private val bmbApplication: BMBApplication = BMBApplication.getInstance()
    val symmetricCryptoHelper: SymmetricCryptoHelper = SymmetricCryptoHelper.getInstance()
    private val monitoringInteractor = MonitoringInteractor()

    lateinit var simplifiedAuthenticationHelper: SimplifiedAuthenticationHelper
    private lateinit var multipleUsersFragmentAdapter: MultipleUsersFragmentAdapter
    private var toolbar: Toolbar? = null

    var enteredPasscode = ""
    private var retryLoadProfiles = 0
    private var callsComplete = 0
    private var isFromPushNotification = false
    private var isPushAppStart = false
    private var selectedUserProfile: UserProfile? = null
    var loginAnimationProgressDialog: LoginAnimationProgressDialog? = null
    private var userId: String = ""
    var userSecret: String = ""
    private var expressAccountList: ArrayList<AccountObject> = ArrayList()

    private var actionBar: ActionBar? = null
    private var userPosition = 0
    var userProfiles: List<UserProfile> = emptyList()

    private var aliasID: String = ""
    private var profileCount = 0
    var isGenerateTokenOffline = false
    var isPerformingFingerprintLogin = false
    var currentShortcutId: String? = null
    private var customTags: CustomTags? = null

    // These services will eventually be injected in the viewModel for Simplified login
    private val userSettingsManager: IUserSettingsManager = UserSettingsManager
    private val sharedPreferenceService: ISharedPreferenceService = SharedPreferenceService
    private val tag = javaClass.simpleName

    private val pushNotificationToggleResponseListener: ExtendedResponseListener<TransactionResponse> = object : ExtendedResponseListener<TransactionResponse>() {
        override fun onRequestStarted() {}
        override fun onSuccess(successResponse: TransactionResponse) {
            BMBLogger.d(Companion.TAG, "Push toggle changed: $successResponse")
            userSettingsManager.setShouldRegisterPushNotificationIdWithBackend(false)
            userSettingsManager.setNotificationServicesEnabled(true)
            toastLong("Push notifications successfully switched on")
        }

        override fun onFailure(failureResponse: ResponseObject) {
            BMBLogger.e(Companion.TAG, "Push toggle change failure: $failureResponse")
        }
    }

    private val loginExtendedResponseListener: ExtendedResponseListener<SecureHomePageObject> = object : ExtendedResponseListener<SecureHomePageObject>() {
        override fun onSuccess(response: SecureHomePageObject) = loginSuccess(response)

        override fun onFailure(response: ResponseObject) {
            dismissProgressDialog()
            val errorMessage = response.errorMessage
            val errorCode = response.responseCode
            val eventMap = HashMap<String, Any?>()
            BMBLogger.d(LOGTAG, "response:$response")
            eventMap[MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_OPCODE] = response.opCode
            eventMap[MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_ERROR_MESSAGE] = errorMessage
            NewRelic.recordCustomEvent("monitoringEventTypeCustom", eventMap)
            monitoringInteractor.logMonitoringEvent(MonitoringService.MONITORING_EVENT_NAME_INCORRECT_PASSCODE, eventMap)
            resetPasscodeIndicator()
            when {
                getString(R.string.auth_failure).equals(errorMessage, true) -> authenticationFailed()
                getString(R.string.auth_revoked).equals(errorMessage, true) -> navigatePasscodeLockedActivity()
                INVALID_CREDENTIALS.equals(errorCode, true) -> showMessageError(errorMessage)
                ALIAS_NOT_FOUND_RESPONSE_CODE.equals(errorCode, true) -> deleteAliasLocally(getString(R.string.device_not_linked))
                errorMessage.contains(ACCOUNT_ERROR_CODE) -> navigateAccountErrorScreen()
                AppConstants.PIN_LOCKED.equals(errorCode, true) -> navigateAccountLockedScreen()
                AppConstants.RESPONSE_CODE_SERVICE_NOT_ACTIVE.equals(response.responseCode, true) -> showAccountSuspendedScreen()
                AppConstants.RESPONSE_CODE_FRAUD_HOLD.equals(response.responseCode, true) -> navigateFraudLockScreen()
                AppConstants.RESPONSE_CODE_NO_ACCOUNTS.equals(response.responseCode, true) -> {
                    BMBLogger.d("EXPRESS", "AOL threw an error when there were no accounts. Handling it differently...")
                    val secureHomePageObject = SecureHomePageObject()
                    secureHomePageObject.responseId = SIMPLIFIED_LOGON_SUCCESS_RESPONSE_ID
                    loginSuccess(secureHomePageObject)
                }
                else -> {
                    monitoringInteractor.logEvent(MonitoringService.MONITORING_EVENT_NAME_UNEXPECTED_ERROR_AT_LOGIN_LINKING, response)
                    if (errorMessage.contains("routine maintenance")) {
                        checkDeviceState()
                    } else {
                        showMessageError(errorMessage)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        SessionTimeOutDialogActivity.shouldShow = false
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simplified_login)
        if (isDeviceRootHandled()) return

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        actionBar = supportActionBar

        visitOnlineTextView.setOnClickListener { navigateAbsaOnline() }
        termsAndConditionsTextView.setOnClickListener { navigateTermsAndConditionsSelectorActivity() }

        mScreenName = LOGIN_CONST
        mSiteSection = SIMPLIFIED_LOGIN_CONST
        AnalyticsUtils.getInstance().trackCustomScreenView(LOGIN_CONST, SIMPLIFIED_LOGIN_CONST, TRUE_CONST)

        @Suppress("ConstantConditionIf")
        if (!BuildConfig.PRD && !BuildConfig.PRD_BETA) {
            setUpEnvironmentToggle()
        }

        appCacheService.setUserLoggedInStatus(false)
        setLocaleToEnglish()
        pushNotificationToggleResponseListener.setView(this)
        loginExtendedResponseListener.setView(this)

        bmbApplication.userLoggedInStatus = false
        bmbApplication.updateLanguage(this, ENGLISH_CODE)

        simplifiedAuthenticationHelper = SimplifiedAuthenticationHelper(this)
        simplifiedAuthenticationHelper.setExtendedResponseListener(loginExtendedResponseListener)

        appCacheService.setPasscodeResetFlow(false)
        isPushAppStart = intent.getBooleanExtra(PUSH_NOTIFICATION_APP_START, false)

        if (intent.hasExtra(SHORTCUT)) {
            currentShortcutId = intent.getStringExtra(SHORTCUT)
            intent.removeExtra(SHORTCUT)
        }

        if (isPushAppStart) {
            appCacheService.setCacheAvailable(true);
            appLaunchedFromPush();
        } else {
            continueWithInitialisation();
        }

        val pushMillis = intent.getLongExtra(PUSH_NOTIFICATION_TIME, -1L)
        if (pushMillis != -1L) {
            val currentMillis = Calendar.getInstance().timeInMillis
            val diffSeconds = (currentMillis - pushMillis) / 1000
            if (diffSeconds > 55) {
                isPushAppStart = false
            }
        }
        ScreenshotHelper.setupShakeDetection()
        DontKeepActivitiesHelper.checkForDontKeepActivities(this)
    }

    private fun continueWithInitialisation() {
        profileCount = profileManager.profileCount
        userProfiles = profileManager.userProfiles
        // If there is no user profiles for instance when the app process gets killed by the operating system while the app is minimised we navigate to splash and allow the whole process to execute a new.
        if (ProfileManager.getInstance().userProfiles.isNullOrEmpty()) {
            navigateSplashActivity()
            return
        }
        initViews()
        setUserProfiles()
        onNewIntent(intent)
    }

    private fun appLaunchedFromPush() {
        ProfileManager.getInstance().loadAllUserProfiles(object : OnProfileLoadListener {
            override fun onAllProfilesLoaded(userProfiles: List<UserProfile?>?) {
                continueWithInitialisation()
            }

            override fun onProfilesLoadFailed() {}
        })
    }

    override fun onResume() {
        super.onResume()

        resetSession()
        ExpressNetworkingConfig.tokenExpireTime = null
        ExpressNetworkingConfig.isLoggedIn = false

        if (isGenerateTokenOffline) {
            BMBLogger.d(Companion.TAG, "Proceeding to generate offline token...")
        } else if (!BuildConfigHelper.STUB && !NetworkUtils.isNetworkConnected()) {
            navigateNoConnectionGenerateTokenActivity()
        }

        ScreenshotHelper.registerShakeListener()

        if (profileManager.activeUserProfile == null) {
            setActiveUserProfile()
        }

        if (bmbApplication.hasPendingAuth()) {
            bmbApplication.clearPendingAuth()
            bmbApplication.showVerificationScreen()
            return
        }
        BMBLogger.d("x-show:", "isPushAppStart $isPushAppStart")
        if (isPushAppStart) {
            isPushAppStart = false
            showProgressDialog()
        } else {
            dismissProgressDialog()
        }
    }

    private fun authenticationFailed() {
        val passcodeTries = sharedPreferenceService.countRetries(userId)
        BMBLogger.d(Companion.TAG, "Retries remaining AFTER decrement -> $passcodeTries")
        when {
            passcodeTries < 0 -> showMessageError("negative retries")
            passcodeTries == 0 -> {
                BMBLogger.d(Companion.TAG, "Retries now zero, gonna show profile blocked screen -> $passcodeTries")
                navigatePasscodeLockedActivity()
            }
            else -> {
                BMBLogger.d(Companion.TAG, "Retries remaining not zero but ->$passcodeTries")
                val tryAgain = getString(R.string.try_again_newline)
                val attemptsMessage = if (passcodeTries == 1) getString(R.string.attempt_remaining) else getString(R.string.attempts_remaining)
                getExistingUserFragment()?.instructionTextView?.text = "$tryAgain$passcodeTries $attemptsMessage"
            }
        }
    }

    // Because isAuthPassNotRegistered comes back as null , this is the solution that works
    private fun passwordNotSet(secureHomePageObject: SecureHomePageObject): Boolean {
        val passwordDigits = secureHomePageObject.passwordDigits
        return passwordDigits.isNullOrEmpty()
    }

    private fun setLoginResponseData() {
        val secureHomePageObject = appCacheService.getSecureHomePageObject()
        if (secureHomePageObject == null) {
            showMessageError(getString(R.string.relog_required));
            return
        }

        val customerProfile = CustomerProfileObject.instance
        createDeviceProfilingPostLoginSession(customerProfile)
        combineAolAndExpressAccounts(secureHomePageObject)

        userProfiles.elementAtOrNull(userPosition)?.let { userProfile ->
            val userName = customerProfile.customerName ?: userProfile.customerName
            if (userProfile.customerName != null && !userProfile.customerName.equals(userName, ignoreCase = true)) {
                userProfile.customerName = userName
            }
            profileManager.updateProfile(userProfile, null)
            profileManager.activeUserProfile = userProfile
            sharedPreferenceService.resetRetries(userId)
        }

        customerProfile.cellNumber?.let { appCacheService.setCellphoneNumber(it) }

        if (customerProfile.isTransactionalUser) {
            appCacheService.setIsTransactionalUser(customerProfile.isTransactionalUser)
        }

        setLanguage(secureHomePageObject)
        appCacheService.setUserLoggedInStatus(true)

        val activeProfile = profileManager.activeUserProfile
        if (activeProfile != null) {
            val profileClientType = activeProfile.clientType
            if (profileClientType == null) {
                activeProfile.clientType = customerProfile.clientTypeGroup
            }
        }

        val secondFactorState = SecondFactorState.fromValue(customerProfile.secondFactorState)
        if (SecondFactorState.SURECHECKV2_NOPRIMARYDEVICE == secondFactorState) {
            appCacheService.setNoPrimaryDeviceState(true)
        }
        appCacheService.setPrimarySecondFactorDevice(secureHomePageObject.isPrimarySecondFactorDevice)
        setLanguageForAppAndProfile(secureHomePageObject, activeProfile)
    }

    private fun combineAolAndExpressAccounts(secureHomePageObject: SecureHomePageObject) {
        val absaCacheManager = AbsaCacheManager.getInstance()
        absaCacheManager.appendAccountList(expressAccountList)
        val accountsList = absaCacheManager.accountsList.accountsList
        with(secureHomePageObject) {
            fromAccounts = absaCacheManager.filterFromAccountList(accountsList, "")
            toAccounts = absaCacheManager.filterToAccountList(accountsList)
            accounts = accountsList
        }
    }

    private fun loginSuccess(secureHomePageObject: SecureHomePageObject) {
        if (userSettingsManager.isNotificationServicesEnabled() && userSettingsManager.shouldRegisterPushNotificationIdWithBackend()) {
            BMBLogger.d("$tag - (push interactor)", "@" + Date().time)
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                PushNotificationInteractor().addPushNotificationRecord(it.token, pushNotificationToggleResponseListener)
            }
        }

        if (SIMPLIFIED_LOGON_SUCCESS_RESPONSE_ID == secureHomePageObject.responseId || AppConstants.RESPONSE_CODE_EMS_FAILED == secureHomePageObject.responseCode) {
            BMBLogger.d("$tag -  was success response", "@" + Date().time)
            appCacheService.setAuthCredential(userSecret)
            appCacheService.setAuthCredentialType(if (enteredPasscode.isNotBlank()) CREDENTIAL_TYPE_5_DIGIT_PASSCODE else CREDENTIAL_TYPE_BIOMETRIC)

            val lastLoginVersion = sharedPreferenceService.getLastLoginVersion()
            val versionCode = BuildConfig.VERSION_CODE
            val isFirstLoginForVersion = versionCode > lastLoginVersion
            sharedPreferenceService.setFirstLoginStatus(isFirstLoginForVersion)
            if (isZeroUserProfile() && profileManager.isLegacyUser) {
                BMBLogger.d("$tag -  legacy profile migration", "@" + Date().time)
                try {
                    val userProfile: UserProfile = profileManager.initialiseUserProfile("", secureHomePageObject)
                    profileManager.deleteLegacyAlias(object : ProfileManager.SimpleCallback {
                        override fun onSuccess() {
                            profileManager.addProfile(userProfile, object : OnProfileCreateListener {
                                override fun onProfileCreated(savedUserProfile: UserProfile) = loginCallComplete()
                                override fun onProfileCreateFailed() {}
                            })
                        }

                        override fun onFailure() {}
                    })
                } catch (e: Exception) {
                    BMBLogger.e(Companion.TAG, e.message)
                }
            } else {
                if (userProfiles.isEmpty()) {
                    BMBLogger.d("$tag -  loading all user profiles", "@" + Date().time)
                    profileManager.loadAllUserProfiles(object : OnProfileLoadListener {
                        override fun onAllProfilesLoaded(userProfiles: List<UserProfile>) {
                            this@SimplifiedLoginActivity.userProfiles = userProfiles
                            loginCallComplete()
                        }

                        override fun onProfilesLoadFailed() {}
                    })
                } else {
                    loginCallComplete()
                }
            }
        } else if (RESPONSE_ID_DEVICE_NOT_LINKED == secureHomePageObject.responseId) {
            appCacheService.setAuthCredential(userSecret)
            appCacheService.setAuthCredentialType(if (enteredPasscode.isNotBlank()) CREDENTIAL_TYPE_5_DIGIT_PASSCODE else CREDENTIAL_TYPE_BIOMETRIC)
            if (passwordNotSet(secureHomePageObject)) {
                appCacheService.setIsPasswordResetFlow(true)
                dismissProgressDialog()
                navigateRegisterCreatePasswordActivity()

            } else {
                BMBLogger.d("$tag -  backend does not consider device as linked, deleting it...", "@" + Date().time)
                dismissProgressDialog()
                deleteProfile()
            }
        }
    }

    private fun createDeviceProfilingPostLoginSession(customerProfile: CustomerProfileObject?) {
        if (bmbApplication.isDeviceProfilingActive && customerProfile != null) {
            BMBLogger.d(Companion.TAG, "Creating post login session...")
            getDeviceProfilingInteractor().createPostLoginSession(customerProfile.customerSessionId, customerProfile.permanentUserId, customerProfile.userId)
        } else {
            getDeviceProfilingInteractor().disable()
        }
    }

    private fun setUpEnvironmentToggle() {
        val environmentOptions = SelectorList<StringItem>().apply {
            add(StringItem("I"))
            add(StringItem("R"))
            add(StringItem("2"))
            add(StringItem("S"))
        }
        val buildFlavor = BuildConfig.FLAVOR.toLowerCase(Locale.ENGLISH)
        val buildFlavorIndex = when {
            buildFlavor.contains("uat_i") -> 0
            buildFlavor.contains("uat_r") -> 1
            buildFlavor.contains("uat_2") -> 2
            buildFlavor.contains("stub") -> 3
            else -> -1
        }
        with(environmentRadioButtonView) {
            getRadioGroup()?.layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setDataSource(environmentOptions)
            if (buildFlavorIndex != -1) {
                visibility = View.VISIBLE
                selectedIndex = buildFlavorIndex
                setItemCheckedInterface(ItemCheckedInterface { index: Int ->
                    when (index) {
                        0 -> activeUatI()
                        1 -> activeUatR()
                        2 -> activeUat2()
                        3 -> activateStub()
                    }
                })
            } else {
                visibility = View.GONE
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val bundle = intent?.extras ?: return

        isGenerateTokenOffline = bundle.getBoolean(IS_GENERATE_TOKEN, false)
        customTags = try {
            bundle.getParcelable(IN_APP_NOTIFICATION_IDENTIFIER) as? CustomTags
        } catch (e: Exception) {
            BMBLogger.d(e)
            null
        }
        if (customTags != null) {
            isFromPushNotification = true
            userProfiles.find { userProfile -> userProfile.mailboxId == customTags?.mailboxId }.let { userProfile ->
                selectedUserProfile = userProfile
                userPosition = userProfiles.indexOf(userProfile)
            }
            currentShortcutId = PushMessageListener.IN_APP_SHORTCUT
        } else {
            selectedUserProfile = bundle.getSerializable(IntentFactory.EXTRA_USER_PROFILE) as? UserProfile
            if (navigateToSelectedProfile()) {
                return
            }
        }

        selectedUserProfile?.let { loadUserProfiles() }
    }

    private fun navigateToSelectedProfile(): Boolean {
        if (selectedUserProfile != null) {
            val userPositionFound = userProfiles.indexOfFirst { userProfile -> userProfile.userId != null && userProfile.userId.equals(selectedUserProfile?.userId, ignoreCase = true) }
            if (userPositionFound != -1) {
                userPosition = userPositionFound
                profilePictureViewPager.setCurrentItem(userPosition, true)
                return true
            }
        }
        return false
    }

    private fun initViews() {
        profilePictureViewPager.visibility = View.INVISIBLE
        profilePictureViewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(page: Int) {
                if (page in 0..userProfiles.lastIndex) {
                    onPageScrolled(page)
                }
                isFromPushNotification = false
            }

            override fun onPageScrollStateChanged(i: Int) {}
        })
    }

    private fun setUserProfiles() {
        val cachedProfiles = profileManager.userProfiles
        if (!cachedProfiles.isNullOrEmpty()) {
            onProfilesLoaded(cachedProfiles, false)
        } else {
            userPosition = intent.getIntExtra(USER_POSITION, 0)
            loadUserProfiles()
        }
    }

    private fun loadUserProfiles() {
        profileManager.loadAllUserProfiles(object : OnProfileLoadListener {
            override fun onAllProfilesLoaded(userProfiles: List<UserProfile>) = onProfilesLoaded(userProfiles, true)
            override fun onProfilesLoadFailed() = goToLaunchScreen(this@SimplifiedLoginActivity)
        })
    }

    private fun onProfilesLoaded(userProfiles: List<UserProfile>, animate: Boolean) {
        this.userProfiles = userProfiles
        profileCount = userProfiles.size

        multipleUsersFragmentAdapter = MultipleUsersFragmentAdapter(supportFragmentManager, this.userProfiles as ArrayList<UserProfile>)

        with(profilePictureViewPager) {
            val padding = resources.getDimensionPixelSize(liblotto.lotto.R.dimen.medium_space)
            val margin = resources.getDimensionPixelSize(liblotto.lotto.R.dimen.normal_space)

            adapter = multipleUsersFragmentAdapter
            clipToPadding = false

            setPadding(padding, 0, padding, 0)
            pageMargin = -margin
            setPageTransformer(true, ZoomPageTransformer(this))
        }

        if (profileCount <= 5) {
            userPagerDotsTabLayout.visibility = View.VISIBLE
            userPagerDotsTabLayout.setupWithViewPager(profilePictureViewPager, true)
        } else {
            userPagerDotsTabLayout.visibility = View.GONE
        }

        if (animate) {
            profilePictureViewPager.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fadein_quick))
        } else {
            profilePictureViewPager.visibility = View.VISIBLE
        }

        if (selectedUserProfile != null) {
            val userPositionFound = userProfiles.indexOfFirst { userProfile -> userProfile.userId != null && userProfile.userId.equals(selectedUserProfile?.userId, ignoreCase = true) }
            if (userPositionFound != -1) {
                userPosition = userPositionFound
            }
            profilePictureViewPager.setCurrentItem(userPosition, true)
            profileManager.activeUserProfile = selectedUserProfile
        } else if (userPosition < userProfiles.size) {
            profileManager.activeUserProfile = userProfiles[userPosition]
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.delete_menu_multiple, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_delete -> {
            navigateRemoveAccountConfirmationActivity()
            true
        }
        R.id.users_list -> {
            selectedUserProfile = null
            navigateMultipleUsersListActivity()
            super.onOptionsItemSelected(item)
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DELETE_USER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            retryLoadProfiles = 0
            loadProfiles()
        } else if (requestCode == CLOSE_APP) {
            finish()
        }
    }

    private fun loadProfiles() {
        profileManager.loadAllUserProfiles(object : OnProfileLoadListener {
            override fun onAllProfilesLoaded(userProfiles: List<UserProfile>) {
                userPosition = 0
                onProfilesLoaded(userProfiles, false)
            }

            override fun onProfilesLoadFailed() {
                dismissProgressDialog()
                if (retryLoadProfiles++ < 1) {
                    loadProfiles()
                } else {
                    MonitoringInteractor().logTechnicalEvent(SimplifiedLoginActivity::class.java.simpleName, "", "Failed to Load Profiles")
                    showMessageError(getString(R.string.generic_error))
                }
            }
        })
    }

    private fun extractAlias(): String {
        try {
            val userProfile = userProfiles.elementAtOrNull(userPosition) ?: return aliasID
            val existingUserAliasKey = "alias_key"
            var aliasEncryptedWithZeroKey = symmetricCryptoHelper.retrieveAlias(existingUserAliasKey)
            aliasEncryptedWithZeroKey = if (aliasEncryptedWithZeroKey != null) {
                // Legacy code: no multiple users feature
                symmetricCryptoHelper.decryptAliasWithZeroKey(aliasEncryptedWithZeroKey)
            } else {
                // new App with multiple users feature
                symmetricCryptoHelper.retrieveAlias(userProfile.userId)
            }
            symmetricCryptoHelper.decryptAliasWithZeroKey(aliasEncryptedWithZeroKey)?.let { aliasBytes ->
                aliasID = String(aliasBytes)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return aliasID
    }

    fun generateOfflineToken(): String {
        val alias = extractAlias()
        val pin = enteredPasscode
        val deviceID = getDeviceID()
        val otpSeed = symmetricCryptoHelper.retrieveOtpSeed()
        val gen = OfflineOtpGenerator(alias, pin, deviceID, otpSeed)

        val otp = try {
            gen.generateOTP()
        } catch (e: Exception) {
            e.printStackTrace()
            "000000"
        }

        profileManager.activeUserProfile?.userId?.let { userId ->
            sharedPreferenceService.setOfflineOtpLongNumber(userId, gen.longCredentials)
        }
        return otp
    }

    private fun isZeroUserProfile(): Boolean = profileCount == 0

    fun isSingleUserProfile(): Boolean = profileCount == 1

    fun executeLoginRequest() {
        sharedPreferenceService.enableLaunchProfileSetUp()
        val passcode = enteredPasscode
        if (profileCount > 0) {
            if (userPosition < profileCount) {
                performPasscodeAuthentication(passcode)
            } else {
                dismissProgressDialog()
            }
        }
    }

    fun expressLogin() {
        profileManager.activeUserProfile?.userId?.let { userId = it }

        appCacheService.clear()
        absaCacheService.clear()
        AbsaCacheManager.getInstance().clearCache()

        callsComplete = 0
        val expressAuthenticationHelper = ExpressAuthenticationHelper(this)
        expressAuthenticationHelper.performLogin(userSecret, object : LoginCallBack {
            override fun loginCallComplete() {
                expressAuthenticationHelper.getUserProfile(object : UserProfileCallBack {
                    override fun userProfileCallComplete(expressAccountList: ArrayList<AccountObject>) {
                        this@SimplifiedLoginActivity.expressAccountList = expressAccountList
                        this@SimplifiedLoginActivity.loginCallComplete()
                    }

                    override fun userProfileCallFailed(failureMessage: String) = showMessageError(failureMessage)
                })
                if (isPerformingFingerprintLogin) {
                    performAolFingerprintAuthentication()
                } else {
                    executeLoginRequest()
                }
            }

            override fun loginCallFailure(authenticationStatusCodes: AuthenticationStatusCodes?, failureMessage: String) {
                dismissProgressDialog()
                resetPasscodeIndicator()
                if (authenticationStatusCodes != null) {
                    when (authenticationStatusCodes) {
                        AuthenticationStatusCodes.AUTHENTICATION_FAILED, AuthenticationStatusCodes.AUTHENTICATION_FAILED_IDP -> authenticationFailed()
                        AuthenticationStatusCodes.ALIAS_ID_NOT_FOUND -> deleteAliasLocally(getString(R.string.device_not_linked))
                        AuthenticationStatusCodes.ACCOUNT_LOCKED -> navigateAccountLockedScreen()
                        AuthenticationStatusCodes.SERVICE_SUSPENDED,
                        AuthenticationStatusCodes.SERVICE_CANCELLED -> showAccountCancelledScreen()
                        AuthenticationStatusCodes.SERVICE_SUSPENDED_UNPAID_FEES -> showAccountSuspendedScreen()
                        AuthenticationStatusCodes.REVOKED -> navigatePasscodeLockedActivity()
                        AuthenticationStatusCodes.FRAUD_HOLD -> navigateFraudLockScreen()
                        else -> showGenericFailureScreen(getString(R.string.something_went_wrong), String.format("%s (%s)", getString(R.string.auth_failure), authenticationStatusCodes.statusCode))
                    }
                } else {
                    showMessageError(failureMessage)
                }
            }
        }, isPerformingFingerprintLogin)
    }

    private fun callHomeScreen() {
        bmbApplication.userLoggedInStatus = true
        setLanguage(appCacheService.getSecureHomePageObject())

        sharedPreferenceService.setFirstLoginStatus(BuildConfig.VERSION_CODE > sharedPreferenceService.getLastLoginVersion())
        BannerManager.incrementBannerViews()
        if (getEnabledWhatsScreens().isNotEmpty() && sharedPreferenceService.getFirstLoginStatus() && appCacheService.isTransactionalUser()) {
            onLoginCompleted()
            navigateWhatsNewLottieActivity(currentShortcutId)
        } else {
            BMBLogger.d("$tag -  requesting home screen", "@" + Date().time)
            homeScreenService?.loadPoliciesAndAuthorizations(currentShortcutId, customTags, this)
        }
    }

    private fun setLanguage(secureHomePageObject: SecureHomePageObject?) {
        val langCode = if (ENGLISH_LANGUAGE.toString() == secureHomePageObject?.customerProfile?.languageCode) ENGLISH_CODE else AFRIKAANS_CODE
        bmbApplication.updateLanguage(this, langCode)
    }

    override fun onLoginCompleted() {
        loginAnimationProgressDialog?.onLoginCompleted()
    }

    private fun loginCallComplete() {
        callsComplete++
        if (callsComplete > 1) {
            setLoginResponseData()
            callHomeScreen()
        }
    }

    private fun setLanguageForAppAndProfile(secureHomePageObject: SecureHomePageObject, profile: UserProfile?) {
        BMBLogger.d("$tag - setting user's language", "@" + Date().time)
        bmbApplication.userLoggedInStatus = true
        val currentLanguage = secureHomePageObject.langCode
        val currentLanguageCode = if (ENGLISH_LANGUAGE.toString() == currentLanguage) ENGLISH_CODE else AFRIKAANS_CODE
        bmbApplication.updateLanguage(this, currentLanguageCode)

        if (profile == null) return
        currentLanguage?.let { profile.languageCode = it }
        profileManager.updateProfile(profile, null)
    }

    private fun deleteProfile() {
        val selectedUser = profileManager.getSelectedUser(userPosition)
        profileManager.deleteProfile(selectedUser, object : ProfileManager.SimpleCallback {
            override fun onSuccess() = showDeviceNotLinkedMessage()
            override fun onFailure() {}
        })
    }

    override fun showDeviceNotLinkedMessage() {
        showAlertDialog(AlertDialogProperties.Builder()
                .title(getString(R.string.link_dialog_title))
                .message(getString(R.string.device_not_linked))
                .positiveDismissListener { _: DialogInterface?, _: Int ->
                    try {
                        symmetricCryptoHelper.clearKeys()
                    } catch (e: KeyStoreEntryAccessException) {
                        e.printStackTrace()
                    }
                    navigateWelcomeActivity()
                }
                .build())
    }

    private fun alertBoxSecurityCode(message: String) {
        if (bmbApplication.isInForeground) {
            showMessageError(message) { _: DialogInterface?, _: Int -> navigateAccountLoginActivity() }
        } else {
            finish()
        }
    }

    private fun deleteAliasLocally(errorMessage: String) {
        val userProfile = profileManager.getSelectedUser(userPosition)
        if (userProfile != null) {
            profileManager.deleteProfile(userProfile, object : ProfileManager.SimpleCallback {
                override fun onSuccess() {
                    dismissProgressDialog()
                    alertBoxSecurityCode(errorMessage)
                }

                override fun onFailure() {
                    dismissProgressDialog()
                    alertBoxSecurityCode(errorMessage)
                }
            })
        } else {
            dismissProgressDialog()
        }
    }

    private fun onPageScrolled(position: Int) {
        resetPasscodeIndicator()
        if (selectedUserProfile == null && !isFromPushNotification) {
            userPosition = position
        }
        setActiveUserProfile()
        getUserFragment()?.onPageScrollSettled()
        val lastPosition = multipleUsersFragmentAdapter.count - 1
        if (!(userPosition == lastPosition && profileCount != MAXIMUM_PER_DEVICE_USER_PROFILES)) {
            setupToolbar()
        }
    }

    private fun getUserFragment(): UserFragment? = supportFragmentManager.fragments.elementAtOrNull(userPosition) as? UserFragment

    private fun getExistingUserFragment(): ExistingUserFragment? = getUserFragment() as? ExistingUserFragment

    private fun setupToolbar() {
        val pageIndicator = String.format(getString(R.string.page_of_page), userPosition + 1, profileCount)
        actionBar?.setHomeButtonEnabled(true)
        if (toolbar != null) {
            toolbar?.setTitle(R.string.contact_us_heading)
            val paddingTopBottom = resources.getDimensionPixelSize(R.dimen.normal_space)
            val title = (toolbar?.getChildAt(0) as? TextView) ?: return
            title.setPadding(0, paddingTopBottom, 0, paddingTopBottom)
            title.setOnClickListener { startActivity(Intent(this, ContactUsActivity::class.java)) }
        } else if (actionBar != null) {
            actionBar?.title = if (profileCount == 1) null else pageIndicator
        }
    }

    private fun setActiveUserProfile() {
        if (userPosition < profileCount && userPosition < MAXIMUM_PER_DEVICE_USER_PROFILES) {
            profileManager.activeUserProfile = profileManager.userProfiles?.elementAtOrNull(userPosition) ?: return
        }
    }

    private fun performAolFingerprintAuthentication() {
        try {
            val userProfile = userProfiles.elementAtOrNull(userPosition) ?: return
            val zeroKeyEncryptedAlias = symmetricCryptoHelper.retrieveAlias(userProfile.userId)
            symmetricCryptoHelper.decryptAliasWithZeroKey(zeroKeyEncryptedAlias)?.let { decryptedAlias ->
                val alias = String(decryptedAlias)
                simplifiedAuthenticationHelper.performAuthentication(alias, userSecret, true)
            }
        } catch (e: Exception) {
            BMBLogger.e(Companion.TAG, e.message)
            showGenericErrorMessageThenFinish()
        }
    }

    private fun performPasscodeAuthentication(passcode: String) {
        isPerformingFingerprintLogin = false
        try {
            val userProfile = userProfiles.elementAtOrNull(userPosition) ?: return
            val zeroKeyEncryptedAlias = symmetricCryptoHelper.retrieveAlias(userProfile.userId)
            if (zeroKeyEncryptedAlias == null) {
                dismissProgressDialog()
                showMessageError("This profile is lost! you should delete and recreate this profile")
                resetPasscodeIndicator()
            } else {
                symmetricCryptoHelper.decryptAliasWithZeroKey(zeroKeyEncryptedAlias)?.let { decryptedAlias ->
                    val alias = String(decryptedAlias)
                    simplifiedAuthenticationHelper.performAuthentication(alias, passcode, false)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun showMessageError(message: String?) {
        super.showMessageError(message)
        resetPasscodeIndicator()
    }

    override fun showMessageError(message: String, positiveOnClickListener: DialogInterface.OnClickListener) {
        super.showMessageError(message, positiveOnClickListener)
        resetPasscodeIndicator()
    }

    @Synchronized
    override fun showProgressDialog() {
        try {
            if (loginAnimationProgressDialog == null) {
                loginAnimationProgressDialog = LoginAnimationProgressDialog.newInstance()
                loginAnimationProgressDialog?.isCancelable = false
                loginAnimationProgressDialog?.show(supportFragmentManager, Companion.TAG)
            }
        } catch (e: Exception) {
            BMBLogger.d(e)
        }
    }

    @Synchronized
    override fun dismissProgressDialog() {
        try {
            loginAnimationProgressDialog?.dismiss()
            loginAnimationProgressDialog = null
        } catch (e: Exception) {
            BMBLogger.d(e)
        }
    }

    private fun resetPasscodeIndicator() {
        (getUserFragment() as? ExistingUserFragment)?.numericKeypad?.clearPasscode()
    }

    // Navigating from login screen
    // -----------------------------------------------------------------------------------------------------------------
    private fun navigatePasscodeLockedActivity() = startActivity(Intent(this, PasscodeLockedActivity::class.java).apply {
        putExtra(USER_POSITION, userPosition)
    })

    private fun navigateAccountLoginActivity() {
        AbsaCacheManager.getInstance().clearCache()
        absaCacheService.clear()
        appCacheService.clear()
        startActivity(Intent(this, LinkingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    private fun navigateWelcomeActivity() = startActivity(Intent(this, WelcomeActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    })

    private fun navigateWhatsNewLottieActivity(currentShortcutId: String?) = startActivity(Intent(this, WhatsNewLottieActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        putExtra("shortcut", currentShortcutId)
        putExtra(WhatsNewLottieActivity.IS_FROM_LOGIN, true)
    })

    private fun navigateRemoveAccountConfirmationActivity() = startActivityForResult(Intent(this, RemoveAccountConfirmationActivity::class.java).apply {
        putExtra(USER_PROFILE, profileManager.activeUserProfile)
    }, DELETE_USER_REQUEST_CODE)

    private fun navigateMultipleUsersListActivity() = startActivityForResult(Intent(this, MultipleUsersListActivity::class.java).apply {
        putExtra("userProfiles", ArrayList(userProfiles))
    }, DELETE_USER_REQUEST_CODE)

    fun navigateNoConnectionGenerateTokenActivity() = startActivity(Intent(this, NoConnectionGenerateTokenActivity::class.java))

    private fun navigateRegisterCreatePasswordActivity() = startActivity(Intent(this, RegisterCreatePasswordActivity::class.java).apply {
        putExtra(NAVIGATED_FROM, "DS2")
        putExtra(PASSCODE_STRING, enteredPasscode)
        putExtra(USER_POSITION, userPosition)
    })

    private fun navigateTermsAndConditionsSelectorActivity() = startActivity(Intent(this, TermsAndConditionsSelectorActivity::class.java))

    private fun isDeviceRootHandled(): Boolean {
        val isDeviceRooted = ArxanProtection.isDeviceRooted()
        if (isDeviceRooted) {
            navigateRootTamperActivity()
        }
        return isDeviceRooted
    }

    private fun navigateRootTamperActivity() {
        startActivity(Intent(this, RootTamperActivity::class.java))
        finish()
    }

    private fun navigateAbsaOnline() {
        startActivityIfAvailable(Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.absa.co.za")))
    }

    private fun navigateAccountErrorScreen() {
        GenericResultActivity.bottomOnClickListener = View.OnClickListener {
            bmbApplication.topMostActivity.finish()
            finish()
        }
        startActivity(Intent(this, GenericResultActivity::class.java).apply {
            putExtra(GenericResultActivity.NOTICE_MESSAGE_STRING, getString(R.string.account_error_title))
            putExtra(GenericResultActivity.SUB_MESSAGE_STRING, getString(R.string.account_error_description))
            putExtra(GenericResultActivity.IS_ERROR, true)
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.close)
        })
    }

    private fun navigateFraudLockScreen() {
        appCacheService.setLinkingFlow(false)
        GenericResultActivity.bottomOnClickListener = View.OnClickListener { bmbApplication.topMostActivity.finish() }
        GenericResultActivity.topOnClickListener = View.OnClickListener { TelephoneUtil.call(this, TelephoneUtil.FRAUD_NUMBER) }
        startActivity(IntentFactory.getFailureResultScreen(this, R.string.login_error_account_locked_title, R.string.login_error_fraud_suspended).apply {
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.done)
            putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.call)
        })
    }

    private fun navigateAccountLockedScreen() {
        appCacheService.setLinkingFlow(false)
        GenericResultActivity.bottomOnClickListener = View.OnClickListener { bmbApplication.topMostActivity.finish() }
        GenericResultActivity.topOnClickListener = View.OnClickListener { TelephoneUtil.call(this, "tel:0860111123") }
        val subMessage = String.format("%s\n\n%s", getString(R.string.unlock_digital_profile), getString(R.string.contact_for_assistance))
        startActivity(IntentFactory.getFailureResultScreen(this, R.string.security_problems, subMessage).apply {
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.done)
            putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.call)
        })
    }

    private fun showAccountCancelledScreen() {
        appCacheService.setLinkingFlow(false)
        startActivity(IntentFactory.getFailureResultScreen(this, R.string.login_error_account_locked_title, R.string.login_error_service_cancelled) {
            BMBApplication.getInstance().topMostActivity.finish()
        })
    }

    private fun showAccountSuspendedScreen() {
        appCacheService.setLinkingFlow(false)
        startActivity(IntentFactory.getFailureResultScreen(this, R.string.login_error_account_locked_title, R.string.login_error_unpaid_suspended) {
            bmbApplication.topMostActivity.finish()
        })
    }

    private fun navigateSplashActivity() {
        finishAffinity()
        startActivity(Intent(this, SplashActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }
}