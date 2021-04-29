/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.framework

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.view.*
import android.view.accessibility.AccessibilityManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ContentFrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.NavHostFragment
import androidx.viewbinding.ViewBinding
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.account.services.AccountInteractor
import com.barclays.absa.banking.account.services.AccountService
import com.barclays.absa.banking.boundary.analytics.AnalyticsService
import com.barclays.absa.banking.boundary.analytics.AnalyticsView
import com.barclays.absa.banking.boundary.analytics.FirebaseAnalyticsInteractor
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.boundary.model.CustomerProfileObject.Companion.instance
import com.barclays.absa.banking.boundary.model.UserProfile
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor
import com.barclays.absa.banking.express.data.ClientTypeGroup
import com.barclays.absa.banking.express.data.ClientTypePrefix
import com.barclays.absa.banking.express.data.ResidenceType
import com.barclays.absa.banking.express.data.isBusiness
import com.barclays.absa.banking.express.getAllBalances.CacheHeader
import com.barclays.absa.banking.express.identificationAndVerification.dto.BiometricStatus.Companion.shouldAllowIdentifyFlow
import com.barclays.absa.banking.express.logout.LogoutRepository
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.app.BMBConstants.HMAC_401_UNAUTHORIZED
import com.barclays.absa.banking.framework.app.ScreenshotHelper.saveImage
import com.barclays.absa.banking.framework.app.ScreenshotHelper.screenShot
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitching
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktDelegate
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktHandler
import com.barclays.absa.banking.framework.utils.AlertInfo
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.framework.utils.IntentUtil
import com.barclays.absa.banking.home.services.HomeScreenInteractor
import com.barclays.absa.banking.home.services.HomeScreenService
import com.barclays.absa.banking.home.ui.FetchPolicyAuthorizationsFromPasscodeLogin
import com.barclays.absa.banking.home.ui.HomeContainerActivity
import com.barclays.absa.banking.linking.ui.LinkingActivity
import com.barclays.absa.banking.login.ui.RootTamperActivity
import com.barclays.absa.banking.manage.devices.ManageDeviceConstants.DEVICE_OBJECT
import com.barclays.absa.banking.manage.devices.ManageDeviceConstants.ID_NUMBER
import com.barclays.absa.banking.manage.devices.services.dto.Device
import com.barclays.absa.banking.manage.devices.services.dto.DeviceListResponse
import com.barclays.absa.banking.passcode.passcodeLogin.ExpressAuthenticationHelper
import com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.SimplifiedLoginActivity
import com.barclays.absa.banking.payments.international.InternationalBankingActivity
import com.barclays.absa.banking.presentation.connectivity.NoConnectivityActivity
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.banking.presentation.sessionTimeout.SessionTimeOutDialogActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.presentation.shared.datePickerUtils.RebuildUtils
import com.barclays.absa.banking.presentation.sureCheckV2.SureCheckDevicePasscode2faActivity
import com.barclays.absa.banking.presentation.transactions.AccountRefreshInterface
import com.barclays.absa.banking.presentation.utils.ToolBarUtils
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.barclays.absa.banking.shared.BaseAlertDialog.showAlertDialog
import com.barclays.absa.banking.shared.OnBackPressedInterface
import com.barclays.absa.crypto.SecureUtils
import com.barclays.absa.integration.DeviceProfilingInteractor
import com.barclays.absa.utils.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.newrelic.agent.android.NewRelic
import com.trusteer.tas.atas
import styleguide.forms.BaseInputView
import styleguide.forms.CheckBoxView
import za.co.absa.networking.ExpressNetworkingConfig
import za.co.absa.networking.error.ApplicationError
import za.co.absa.networking.hmac.service.ApiService.Companion.httpErrorLiveData
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class BaseActivity(layout: Int) : AppCompatActivity(layout), BMBConstants, BaseView, AnalyticsView {

    override val baseActivity: BaseActivity
        get() = this

    // Services should not be used directly in views, but instead be injected in managers and viewModels as required
    // These dependencies will reside here only in the interim
    val appCacheService: IAppCacheService
        get() = getServiceInterface()

    val absaCacheService: IAbsaCacheService
        get() = getServiceInterface()

    constructor() : this(R.layout.barclays_main)

    @JvmField
    protected var transaktHandler: TransaktHandler? = null

    @JvmField
    protected var transaktAuthDelegate: TransaktDelegate? = null

    @JvmField
    protected val TAG = BaseActivity::class.java.simpleName

    private var isHiddenToolbar = false
    var toolBar: ActionBar? = null
        private set

    @MenuRes
    private var menuItem = 0
    private var requestHomeRetry = 0

    private var isFragmentCommitAllowed = false
    private var lottieProgressDialog: LottieAnimationProgressDialog? = null
    private var analyticsService: AnalyticsService? = null
    private var tempFragmentTransaction: FragmentTransaction? = null
    private var fragmentManager: FragmentManager? = null
    private var isBackPressEnabled = true
    private lateinit var featureSwitching: FeatureSwitching
    private val progressLock = Any()

    override var homeScreenService: HomeScreenService? = null
        get() {
            if (field == null) {
                field = HomeScreenInteractor(this)
            }
            return field
        }

    private var accountService: AccountService? = null
        get() {
            if (field == null) {
                field = AccountInteractor()
            }
            return field
        }

    protected fun loadAnimation(resid: Int): Animation {
        return AnimationUtils.loadAnimation(this, resid)
    }

    fun animate(view: View, resid: Int): Animation {
        val animation = loadAnimation(resid)
        view.startAnimation(animation)
        return animation
    }

    fun setToolBar(title: String?, listener: View.OnClickListener?) { // Without back button
        toolBar = ToolBarUtils.setToolBarNoBack(this, title)
    }

    fun setToolBar(@StringRes titleResourceId: Int, listener: View.OnClickListener?) {
        setToolBar(getString(titleResourceId), listener)
    }

    fun setToolBarBack(title: String?, listener: View.OnClickListener?) {
        toolBar = ToolBarUtils.setToolBarBack(this, title, listener, false)
    }

    fun setToolBarBack(@StringRes titleResourceId: Int, listener: View.OnClickListener) {
        setToolBarBack(getString(titleResourceId), listener)
    }

    fun setToolBarBack(title: String) {
        setToolBarBack(title) { onBackPressed() }
    }

    fun setToolBarBack(@StringRes titleResourceId: Int) {
        setToolBarBack(getString(titleResourceId))
    }

    fun setToolBarBack(@StringRes titleResourceId: Int, overrideTitleCase: Boolean?) {
        toolBar = ToolBarUtils.setToolBarBack(this, getString(titleResourceId), { onBackPressed() }, overrideTitleCase!!)
    }

    fun setToolBarBack(title: String?, overrideTitleCase: Boolean?) {
        toolBar = ToolBarUtils.setToolBarBack(this, title, { onBackPressed() }, overrideTitleCase!!)
    }

    fun setToolBarNoBackButton(@StringRes title: Int) {
        RebuildUtils.setupToolBar(this, getString(title), -1, false) { onBackPressed() }
    }

    fun setToolBarWithNoBackButton(title: String?) {
        RebuildUtils.setupToolBar(this, title, -1, false) { onBackPressed() }
    }

    fun setToolBarBackWithMenu(title: String?, @MenuRes menu: Int) {
        setupRequestedMenu(menu)
        setToolBarBack(title) { onBackPressed() }
    }

    fun hideToolBar() {
        toolBar?.hide()
        isHiddenToolbar = true
    }

    fun showToolBar() {
        if (isHiddenToolbar) {
            toolBar?.show()
            isHiddenToolbar = false
        }
    }

    fun setToolBarBackFragment(fragmentManager: FragmentManager, title: String?) {
        showToolBar()
        setToolBarBack(title) { fragmentManager.popBackStack() }
    }

    fun setToolBarBackFragment(title: String?, onClickListener: View.OnClickListener?) {
        if (isHiddenToolbar) {
            toolBar?.show()
            isHiddenToolbar = false
        }
        setToolBarBack(title, onClickListener)
    }

    protected fun setLocale2() {
        setLocale(BMBApplication.getApplicationLocale())
    }

    private fun setLocale(locale: Locale) {
        val config = resources.configuration
        config.setLocale(locale)
        val metrics = resources.displayMetrics
        resources.updateConfiguration(config, metrics)
    }

    protected fun setLocaleToEnglish() {
        setLocale(Locale(BMBConstants.ENGLISH_CODE))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NewRelic.setInteractionName(javaClass.simpleName)
        if (SessionTimeOutDialogActivity.shouldShow) {
            startActivity(IntentFactory.getLogOutDialog(this))
        }
        isFragmentCommitAllowed = true
        analyticsService = FirebaseAnalyticsInteractor(FirebaseAnalytics.getInstance(this))

        featureSwitching = FeatureSwitchingCache.featureSwitchingToggles

        Thread.setDefaultUncaughtExceptionHandler(CrashHandler())
        setLocale2()
        if (BuildConfig.PRD || BuildConfig.UAT_PEN_TESTING) {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }
        if (ArxanProtection.isDeviceRooted()) {
            startActivity(Intent(this, RootTamperActivity::class.java))
            finish()
            return
        }
        try { // Oreo: Only fullscreen activities can request orientation
            @SuppressLint("SourceLockedOrientationActivity")
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } catch (e: IllegalStateException) {
            BMBLogger.d(e)
        }
    }

    override fun onResume() {
        super.onResume()

        if (!appCacheService.isCacheAvailable()) {
            appCacheService.setCacheAvailable(true)
            goToLaunchScreen(this)
            return
        }

        httpErrorLiveDataSetup()

        BMBLogger.d("x-class:", javaClass.simpleName)
        NewRelic.recordBreadcrumb(javaClass.simpleName)
        isFragmentCommitAllowed = true
        if (tempFragmentTransaction != null) {
            tempFragmentTransaction!!.commit()
            tempFragmentTransaction = null
        }
    }

    private fun httpErrorLiveDataSetup() {
        httpErrorLiveData.removeObservers(this)
        httpErrorLiveData = MutableLiveData()
        httpErrorLiveData.observe(this, {
            if (it.actualMessage.isNotEmpty()) {
                MonitoringInteractor().logExpressHttpErrorEvent(this::class.simpleName, it.serviceName, it.actualMessage)
            }

            dismissProgressDialog()
            if (it.actualMessage == HMAC_401_UNAUTHORIZED && ExpressNetworkingConfig.isLoggedIn) {
                showRelogRequiredScreen()
            } else if (LogoutRepository::class.java.simpleName != it.serviceName) {
                showMessageError(it)
            }
            httpErrorLiveDataSetup()
        })
    }

    protected fun findPrimaryDevice(deviceList: DeviceListResponse): Device? {
        var primaryDevice: Device? = null
        var isPrimary = false
        if (deviceList.deviceList != null) {
            for (device in deviceList.deviceList!!) {
                if (device.isPrimarySecondFactorDevice) {
                    primaryDevice = device
                    isPrimary = primaryDevice.imei.equals(SecureUtils.getDeviceID(), ignoreCase = true)
                    break
                }
            }
        }
        appCacheService.setPrimarySecondFactorDevice(isPrimary)
        primaryDevice?.let { appCacheService.setCurrentPrimaryDevice(it) }
        return primaryDevice
    }

    override fun onSearchRequested(): Boolean = false

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return keyCode == KeyEvent.KEYCODE_SEARCH || super.onKeyDown(keyCode, event)
    }

    override fun showMessageError(message: String?) {
        dismissProgressDialog()
        BaseAlertDialog.showErrorAlertDialog(message)
    }

    override fun showMessageError(error: ApplicationError) {
        when (error.errorCode) {
            ApplicationError.UNCLASSIFIED_ERROR -> {
                MonitoringInteractor().logTechnicalEvent(javaClass.simpleName, error.serviceName, "UNCLASSIFIED_ERROR: " + error.actualMessage)
                BaseAlertDialog.showGenericErrorDialog()
            }
            ApplicationError.CERT_PINNING_ERROR -> {
                MonitoringInteractor().logTechnicalEvent(javaClass.simpleName, error.serviceName, "CERT_PINNING_ERROR: " + error.actualMessage)
                BaseAlertDialog.showErrorAlertDialog(getString(R.string.cert_pinning_error_message))
            }
            else -> BaseAlertDialog.showErrorAlertDialog(error.friendlyMessage)
        }
    }

    override fun showGenericErrorMessageThenFinish() {
        BaseAlertDialog.showGenericErrorDialog { _, _ -> finish() }
    }

    open fun showMessageError(message: String, positiveOnClickListener: DialogInterface.OnClickListener) {
        dismissProgressDialog()
        BaseAlertDialog.showErrorAlertDialog(message, positiveOnClickListener)
    }

    override fun showMessage(title: String?, message: String?, onDismissListener: DialogInterface.OnClickListener?) {
        dismissProgressDialog()
        showAlertDialog(AlertDialogProperties.Builder()
                .title(title)
                .message(message)
                .positiveDismissListener(onDismissListener)
                .build())
    }

    override fun showCustomAlertDialog(alertInfo: AlertInfo) {
        showAlertDialog(AlertDialogProperties.Builder()
                .title(alertInfo.title)
                .message(alertInfo.message)
                .positiveButton(alertInfo.positiveButtonText)
                .positiveDismissListener { _, _ -> alertInfo.positiveDismissListener }
                .negativeButton(alertInfo.negativeButtonText)
                .negativeDismissListener { _, _ -> alertInfo.negativeDismissListener }
                .build())
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        BMBApplication.getInstance().setCurrentTime(System.currentTimeMillis())
    }

    protected fun saveInstanceState(key: String?, value: String?) {
        key?.let { SharedPreferenceService.setString(it, value) }
    }

    protected fun saveInstanceState(key: String?, value: Boolean) {
        key?.let { SharedPreferenceService.setBoolean(it, value) }
    }

    protected fun restoreInstanceState(bundle: Bundle, key: String?, baseInputView: BaseInputView<*>) {
        bundle.getString(key)?.let { baseInputView.selectedValue = it }
    }

    protected fun restoreInstanceState(key: String?, baseInputView: BaseInputView<*>) {
        if (key != null) {
            SharedPreferenceService.getString(key)?.let { baseInputView.selectedValue = it }
        }
    }

    protected fun restoreInstanceState(key: String?, checkBoxView: CheckBoxView) {
        key?.let { checkBoxView.isChecked = SharedPreferenceService.getBoolean(it) }
    }

    fun toastLong(string: String?) {
        SingleToast.show(this, string, Toast.LENGTH_LONG)
    }

    fun toastLong(@StringRes resId: Int) {
        toastLong(getString(resId))
    }

    fun toastShort(message: String?) {
        SingleToast.show(this, message, Toast.LENGTH_SHORT)
    }

    fun toastShort(resid: Int) {
        toastShort(getString(resid))
    }

    protected val isOperator: Boolean
        get() = appCacheService.getSecureHomePageObject()?.accessPrivileges?.isOperator ?: false

    fun shouldBlockUser(): Boolean {
        return if (appCacheService.getSecureHomePageObject() == null) {
            logoutAndGoToStartScreen()
            false
        } else {
            try {
                val authIndicator = CustomerProfileObject.instance.numberOfAuthorisations.toInt()
                when {
                    isOperator && authIndicator > 0 -> return true
                    !isOperator && authIndicator > 1 -> return true
                    !isOperator && authIndicator <= 1 -> return false
                }
            } catch (e: NumberFormatException) {
                BMBLogger.d(e)
            }
            false
        }
    }

    fun isIntentAvailable(intent: Intent?): Boolean = IntentUtil.isIntentAvailable(this, intent)

    override fun startActivityIfAvailable(intent: Intent?): Boolean {
        return if (isIntentAvailable(intent)) {
            startActivity(intent)
            true
        } else false
    }

    fun startActivityIfAvailable(intent: Intent?, requestCode: Int): Boolean {
        return if (isIntentAvailable(intent)) {
            startActivityForResult(intent, requestCode)
            true
        } else false
    }

    override fun showNoPrimaryDeviceScreen() {
        if (featureSwitching.biometricVerification == FeatureSwitchingStates.ACTIVE.key && shouldAllowIdentifyFlow(instance.biometricStatus)) {
            GenericResultActivity.topOnClickListener = View.OnClickListener {
                appCacheService.setChangePrimaryDeviceFromNoPrimaryDeviceScreen(true)
                Intent(this, LinkingActivity::class.java).apply {
                    putExtra(ID_NUMBER, instance.idNumber)
                    startActivity(this)
                }
            }
        } else {
            GenericResultActivity.topOnClickListener = View.OnClickListener { navigateToPreviousDevicePasscodeEntryScreen() }
        }

        Intent(this, GenericResultActivity::class.java).apply {
            putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_cross_gray)
            putExtra(GenericResultActivity.IS_ERROR, true)
            putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.no_primary_device)
            putExtra(GenericResultActivity.SUB_MESSAGE, R.string.your_primary_device_is_de_linked)
            putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.make_this_my_primary)
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, -1)
            putExtra(GenericResultActivity.CALL_US_CONTACT_NUMBER, getString(R.string.support_center_number))
            flags = Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            startActivity(this)
        }
    }

    override fun showRelogRequiredScreen() {
        showGenericFailureScreen(getString(R.string.something_went_wrong), getString(R.string.relog_required))
    }

    override fun showGenericFailureScreen(title: String, message: String) {
        GenericResultActivity.bottomOnClickListener = View.OnClickListener { logoutAndGoToStartScreen() }
        Intent(this, GenericResultActivity::class.java).apply {
            putExtra(GenericResultActivity.IS_ERROR, true)
            putExtra(GenericResultActivity.NOTICE_MESSAGE_STRING, title)
            putExtra(GenericResultActivity.SUB_MESSAGE_STRING, message)
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.ok)
            startActivity(this)
        }
    }

    override fun navigateToPreviousDevicePasscodeEntryScreen() {
        val device = Device.createDevice()
        device.imei = SecureUtils.getDeviceID()
        Intent(this@BaseActivity, SureCheckDevicePasscode2faActivity::class.java).apply {
            putExtra(SureCheckDevicePasscode2faActivity.FILLER_TEXT_KEY, "old verification")
            putExtra(SureCheckDevicePasscode2faActivity.DELINK_REASON_KEY, "no verification device")
            putExtra(DEVICE_OBJECT, device)
            startActivity(this)
        }
    }

    override fun checkDeviceState() {
        if (NetworkUtils.isNetworkConnected()) {
            showMaintenanceError()
        } else {
            showConnectivityError()
        }
    }

    private fun showMaintenanceError() {
        GenericResultActivity.bottomOnClickListener = View.OnClickListener {
            if (appCacheService.getUserLoggedInStatus()) {
                logoutAndGoToStartScreen()
            } else {
                BMBApplication.getInstance().topMostActivity.finish()
            }
        }
        Intent(this@BaseActivity, GenericResultActivity::class.java).apply {
            putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.unplanned_downtime_title)
            putExtra(GenericResultActivity.SUB_MESSAGE, R.string.unplanned_downtime_message)
            putExtra(GenericResultActivity.IS_ERROR, true)
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.close)
            startActivityIfAvailable(this)
        }
    }

    private fun showConnectivityError() {
        appCacheService.setHasErrorResponse(false)
        Intent(this, NoConnectivityActivity::class.java).apply {
            putExtra(NoConnectivityActivity.INSTRUCTION, getString(R.string.connectivity_maintenance_instruction))
            putExtra(NoConnectivityActivity.MESSAGE, getString(R.string.connectivity_maintenance_message))
            putExtra(NoConnectivityActivity.CONNECTIVITY_TYPE, NoConnectivityActivity.CONNECTIVITY_MAINTENANCE)
            startActivity(this)
        }
    }

    fun showNoNetworkError() {
        appCacheService.setHasErrorResponse(false)
        Intent(this, NoConnectivityActivity::class.java).apply {
            putExtra(NoConnectivityActivity.CONNECTIVITY_TYPE, NoConnectivityActivity.CONNECTIVITY_DATA_SIGNAL)
            startActivity(this)
        }
    }

    override fun loadAccountsAndShowHomeScreenWithAccountsList() {
        loadAccountsAndShowHomeScreen(CacheHeader.BALANCE, null, true)
    }

    override fun loadAccountsClearingAccountProfileAndShowHomeScreen() {
        loadAccountsAndShowHomeScreen(CacheHeader.PROFILE, null, true)
    }

    override fun loadAccountsClearingAccountProfileAndGoHomeWithShortcut(shortcutId: String) {
        loadAccountsAndShowHomeScreen(CacheHeader.PROFILE, shortcutId, true)
    }

    override fun loadAccountsClearingRewardsBalanceAndShowHomeScreen() {
        loadAccountsAndShowHomeScreen(CacheHeader.REWARDS, null, true)
    }

    private fun loadAccountsAndShowHomeScreen(cacheHeader: CacheHeader, shortcut: String?, showAccountList: Boolean) {
        absaCacheService.clear()
        AbsaCacheManager.getInstance().clearCache()

        if (!BMBApplication.getInstance().userLoggedInStatus) {
            goToLaunchScreen(this)
            return
        }

        homeScreenService?.refreshHomeScreenAccountsAndBalances(object : AccountRefreshInterface {
            override fun onSuccess() {
                val expressAuthenticationHelper = ExpressAuthenticationHelper(BMBApplication.getInstance().topMostActivity as BaseActivity)
                expressAuthenticationHelper.getAllBalances(cacheHeader, object : ExpressAuthenticationHelper.BalanceCallBack {
                    override fun callComplete() {
                        homeScreenService?.refreshAccountList()
                        FetchPolicyAuthorizationsFromPasscodeLogin().fetch(shortcut, showAccountList)
                    }

                    override fun callFailed(failureMessage: String) {
                        handleHomeScreenDataFailure(cacheHeader, failureMessage, true)
                    }
                })
            }

            override fun onFailure() {
                handleHomeScreenDataFailure(cacheHeader, getString(R.string.something_went_wrong), true)
            }
        })
    }

    private object SingleToast {
        private var mToast: Toast? = null

        fun show(activity: Activity, text: String?, duration: Int) {
            if (!activity.isFinishing) {
                mToast?.cancel()
                mToast = Toast.makeText(activity, text, duration)
                mToast?.show()
            }
        }
    }

    override val isBusinessAccount: Boolean
        get() {
            val type = CustomerProfileObject.instance.clientTypeGroup
            return type.isBusiness() || BMBConstants.BUSINESS_CUSTOMER_CODE.equals(type, ignoreCase = true)
        }

    override val isSouthAfricanResident: Boolean
        get() = when (CustomerProfileObject.instance.clientType) {
            "${ClientTypePrefix.PRIVATE_INDIVIDUAL.clientTypeCode}${ResidenceType.SA_RESIDENT.residenceCode}",
            "${ClientTypePrefix.STAFF.clientTypeCode}${ResidenceType.SA_RESIDENT.residenceCode}" -> true
            else -> false
        }

    override val isSoleProprietor: Boolean
        get() {
            val clientType = CustomerProfileObject.instance.clientTypeGroup
            return clientType.equals(ClientTypeGroup.SOLE_TRADER_CLIENT.value, ignoreCase = true) || clientType.equals(ClientTypePrefix.SOLE_PROPRIETOR.clientTypeCode, ignoreCase = true) || clientType.equals(ClientTypePrefix.FARMER.clientTypeCode, ignoreCase = true)
        }

    override val isSwiftAllowedForClientType: Boolean
        get() {
            InternationalBankingActivity.SWIFT_ALLOWED_CUSTOMER_CODES.let {
                return CustomerProfileObject.instance.clientType?.padStart(5, '0') ?: "" in it
            }
        }

    protected fun isSoleProprietor(clientType: String): Boolean {
        return clientType.equals(ClientTypeGroup.SOLE_TRADER_CLIENT.value, true) || clientType.equals(ClientTypePrefix.SOLE_PROPRIETOR.clientTypeCode, ignoreCase = true) || clientType.equals(ClientTypePrefix.FARMER.clientTypeCode)
    }

    protected fun isBusinessUserProfile(userProfile: UserProfile?): Boolean {
        val clientType = userProfile?.clientType
        return clientType != null && (clientType.isBusiness() || userProfile.clientType.equals(BMBConstants.BUSINESS_CUSTOMER_CODE, ignoreCase = true))
    }

    fun performAnalytics(screenName: String, channel: String) {
        mScreenName = screenName
        mSiteSection = channel
        trackScreenView(channel, screenName)
    }

    fun numberOfNoneWimiAccounts(): Int = appCacheService.getSecureHomePageObject()?.accounts?.size ?: 0

    val isStandaloneCreditCardAccount: Boolean
        get() {
            var isStandaloneCreditCardAccount = false
            appCacheService.getSecureHomePageObject()?.let { secureHomePageObject ->
                var accounts: List<AccountObject?> = secureHomePageObject.accounts

                if (accounts.isEmpty() && AbsaCacheManager.getInstance().accountsList != null) {
                    accounts = AbsaCacheManager.getInstance().accountsList.customerAccounts
                }

                if (numberOfNoneWimiAccounts() == 1 && "creditCard".equals(accounts.firstOrNull()?.accountType, ignoreCase = true)) {
                    isStandaloneCreditCardAccount = true
                }
            }

            return isStandaloneCreditCardAccount
        }

    override fun showGenericErrorMessage() {
        showMessageError(getString(R.string.generic_error))
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        if (menuItem != 0) {
            inflater.inflate(menuItem, menu)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun setupRequestedMenu(@MenuRes menuItem: Int) {
        this.menuItem = menuItem
    }

    //TODO as part of the refactoring process, this callback must be removed from all sub activities that overrides it in favor of onPermissionGranted callback
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionHelper.handlePermissionsResponse(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && requestCode == REQUEST_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            saveImage(screenShot(window.decorView.rootView))
        }
    }

    override fun navigateToHomeScreenAndShowAccountsList() {
        val intent = buildHomeScreenIntent()
        intent.putExtra(HomeContainerActivity.DISPLAY_ACCOUNTS_LIST, true)
        startActivity(intent)
    }

    override fun navigateToHomeScreenSelectingHomeIcon() {
        val intent = buildHomeScreenIntent().apply { putExtra(HomeContainerActivity.SELECT_HOME_ICON, true) }
        startActivity(intent)
    }

    override fun navigateToHomeScreenWithoutReloadingAccounts() {
        startActivity(buildHomeScreenIntent())
    }

    override fun loadAccountsAndGoHome() {
        loadAccountsAndShowHomeScreen(CacheHeader.BALANCE, null, false)
    }

    override fun loadAccountsAndGoHomeWithShortcut(shortcutId: String) {
        loadAccountsAndShowHomeScreen(CacheHeader.BALANCE, shortcutId, false)
    }

    private fun buildHomeScreenIntent(): Intent {
        Intent(this, HomeContainerActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            return this
        }
    }

    override fun handleHomeScreenDataFailure(cacheHeader: CacheHeader, failureMessage: String, shouldShowAccountsList: Boolean) {
        if (requestHomeRetry++ < REQUEST_HOME_RETRY_MAX) {
            dismissProgressDialog()
            loadAccountsAndShowHomeScreen(cacheHeader, null, shouldShowAccountsList)
        } else {
            if (!isFinishing) {
                dismissProgressDialog()
                showMessageError(failureMessage) { _, _ ->
                    toastLong(getString(R.string.sessionExpiredMessage))
                    logoutAndGoToStartScreen()
                }
            }
        }
    }

    override fun getDeviceProfilingInteractor(): DeviceProfilingInteractor = BMBApplication.getInstance().deviceProfilingInteractor

    override fun onBackPressed() {
        if (isBackPressEnabled && (currentFragment !is OnBackPressedInterface || !(currentFragment as OnBackPressedInterface).onBackPressed())) {
            isBackPressEnabled = false
            super.onBackPressed()
            val halfSecondDelay = 500
            Handler(Looper.getMainLooper()).postDelayed({ isBackPressEnabled = true }, halfSecondDelay.toLong())
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) {
            atas.CheckOverlay(this)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onRestart() {
        super.onRestart()
        isFragmentCommitAllowed = true
    }

    override fun onStart() {
        super.onStart()
        isFragmentCommitAllowed = true
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        isFragmentCommitAllowed = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        isFragmentCommitAllowed = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        isFragmentCommitAllowed = false
        super.onSaveInstanceState(outState)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        isFragmentCommitAllowed = false
        super.onSaveInstanceState(outState, outPersistentState)
    }

    fun commitFragmentSafely(fragmentTransaction: FragmentTransaction?): Boolean {
        if (isFragmentCommitAllowed && fragmentTransaction != null) {
            try {
                fragmentTransaction.commit()
                return true
            } catch (e: IllegalStateException) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }
        }
        return false
    }

    private fun showProgress() {
        synchronized(progressLock) {
            try {
                @Suppress("ControlFlowWithEmptyBody")
                if (lottieProgressDialog == null) {
                    lottieProgressDialog = LottieAnimationProgressDialog.newInstance()
                    lottieProgressDialog!!.isCancelable = false
                    lottieProgressDialog!!.show(supportFragmentManager, FRAGMENT_COMMIT_TAG)
                } else {

                }
            } catch (e: NullPointerException) {
                BMBLogger.d(e)
            } catch (e: IllegalStateException) {
                BMBLogger.d(e)
            }
        }
    }

    override fun showProgressDialog() {
        showProgress()
    }

    override fun dismissProgressDialog() {
        synchronized(progressLock) {
            try {
                val progressIndicator = supportFragmentManager.findFragmentByTag(FRAGMENT_COMMIT_TAG)
                if (progressIndicator != null) {
                    val lottieAnimationProgressDialog = progressIndicator as LottieAnimationProgressDialog
                    lottieAnimationProgressDialog.dismiss()
                }

                lottieProgressDialog?.dismiss()
                lottieProgressDialog = null
            } catch (e: NullPointerException) {
                BMBLogger.d("x-e", ":" + e.toString() + ":" + e.message)
                BMBLogger.d(e)
            } catch (e: IllegalStateException) {
                BMBLogger.d("x-e", ":" + e.toString() + ":" + e.message)
                BMBLogger.d(e)
            }
        }
    }

    override fun trackScreenView(channel: String, screenName: String) {
        analyticsService?.trackScreenView(channel, screenName)
    }

    override fun trackButtonClick(buttonName: String) {
        analyticsService?.trackButtonClick(buttonName)
    }

    override fun trackCustomAction(actionName: String) {
        analyticsService?.trackCustomAction(actionName)
    }

    override fun trackLogout() {
        trackCustomAction("logout")
    }

    override fun trackUserLogin() {
        trackCustomAction("login")
    }

    override fun trackCustomScreenView(screenName: String, siteSection: String, customScreen: String) {
        analyticsService?.trackCustomScreenView(screenName, siteSection, customScreen)
    }

    override fun trackLogoutPopUpYes(screenName: String) {
        analyticsService?.trackLogoutPopUpYes(screenName)
    }

    override fun trackLogoutPopUpNo(screenName: String) {
        analyticsService?.trackLogoutPopUpNo(screenName)
    }

    enum class AnimationType {
        NONE, FADE, FADE_IN_ONLY, SLIDE_IN_ONLY, SLIDE_UP, SLIDE
    }

    fun startFragment(fragment: Fragment, replaceFrag: Boolean, animate: AnimationType?, tag: String?) {
        startFragment(fragment, R.id.fragmentContainer, replaceFrag, animate, false, tag)
    }

    fun startFragment(fragment: Fragment, replaceFrag: Boolean, animate: AnimationType?) {
        startFragment(fragment, R.id.fragmentContainer, replaceFrag, animate, true, "")
    }

    fun startFragment(fragment: Fragment, container: Int, replaceFrag: Boolean, animate: AnimationType?, addToBackStack: Boolean, tag: String?) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        when (animate) {
            AnimationType.FADE -> fragmentTransaction.setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout)
            AnimationType.FADE_IN_ONLY -> fragmentTransaction.setCustomAnimations(R.anim.fadein, R.anim.nothing, R.anim.fadein, R.anim.nothing)
            AnimationType.SLIDE_UP -> {
            }
            else -> {
            }
        }
        if (replaceFrag) {
            if (!tag.isNullOrEmpty()) {
                fragmentTransaction.replace(container, fragment, tag)
            } else {
                fragmentTransaction.replace(container, fragment)
            }
        } else {
            if (!tag.isNullOrEmpty()) {
                fragmentTransaction.add(container, fragment, tag)
            } else {
                fragmentTransaction.add(container, fragment)
            }
        }
        if (!BMBApplication.getInstance().isInForeground && this !is HomeContainerActivity) { //ignore this for
            tempFragmentTransaction = fragmentTransaction
        } else {
            if (addToBackStack) {
                fragmentTransaction.addToBackStack(fragment.javaClass.name)
            }
            BMBLogger.d("x-class:Fragment", fragment.javaClass.simpleName)
            commitFragmentSafely(fragmentTransaction)
        }
    }

    open val currentFragment: Fragment?
        get() {
            fragmentManager = fragmentManager ?: supportFragmentManager
            fragmentManager?.let { fragmentManager ->
                val fragmentList = fragmentManager.fragments

                if (fragmentList.isNullOrEmpty()) {
                    return null
                }

                val currentFragment = fragmentList.last()
                val navHostFragment = fragmentList.firstOrNull { it is NavHostFragment } ?: return currentFragment
                val navHostFragmentList = navHostFragment.childFragmentManager.fragments
                return if (navHostFragmentList.isNotEmpty()) navHostFragmentList.last() else currentFragment
            }
            return null
        }

    val currentFragmentName: String
        get() {
            val currentFragment = currentFragment
            return currentFragment?.javaClass?.name ?: ""
        }

    fun removeFragments(fragmentCount: Int) {
        if (fragmentCount <= 0) {
            return
        }
        if (fragmentManager == null) {
            fragmentManager = supportFragmentManager
        }
        for (i in 0 until fragmentCount) {
            if (fragmentManager!!.backStackEntryCount > 0) {
                fragmentManager!!.popBackStackImmediate()
            }
        }
    }

    override fun logoutAndGoToStartScreen() {
        BMBApplication.getInstance().logoutAndGoToStartScreen()
    }

    protected fun goToLaunchScreen(activity: Activity) {
        retry = 0
        ProfileManager.getInstance().loadAllUserProfiles(object : ProfileManager.OnProfileLoadListener {
            override fun onAllProfilesLoaded(userProfiles: List<UserProfile>) {
                goToLaunchScreen(activity, userProfiles.isNotEmpty())
            }

            override fun onProfilesLoadFailed() {
                if (retry++ < 1) goToLaunchScreen(activity) else goToLaunchScreen(activity, false)
            }
        })
    }

    companion object {
        @JvmField
        var mSiteSection = BMBConstants.BANKING_HOME_CONST

        @JvmField
        var mScreenName = BMBConstants.HOME_CONST

        private var retry = 0
        private const val REQUEST_EXTERNAL_STORAGE = 101
        private const val REQUEST_HOME_RETRY_MAX = 1
        private const val DELAY_IN_MILLISECONDS = 2000
        private const val FRAGMENT_COMMIT_TAG = "ProgressIndicatorDialogFragment"

        @JvmStatic
        fun preventDoubleClick(view: View) {
            preventDoubleClick(view, DELAY_IN_MILLISECONDS)
        }

        @JvmStatic
        fun preventDoubleClick(view: View, duration: Int) {
            view.isClickable = false
            view.postDelayed({ view.isClickable = true }, duration.toLong())
        }

        @JvmStatic
        val isAccessibilityEnabled: Boolean
            get() {
                val accessibilityManager = BMBApplication.getInstance().getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager?
                accessibilityManager?.let {
                    val isAccessibilityEnabled = accessibilityManager.isEnabled
                    val isExploreByTouchEnabled = accessibilityManager.isTouchExplorationEnabled
                    return isAccessibilityEnabled && isExploreByTouchEnabled
                }
                return false
            }

        @JvmStatic
        fun goToLaunchScreen(activity: Activity, shouldGoToPassCodeLogin: Boolean) {
            val intent = Intent()
            val nextActivity: Class<*> = if (shouldGoToPassCodeLogin) SimplifiedLoginActivity::class.java else WelcomeActivity::class.java
            intent.setClass(activity, nextActivity)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            activity.finish()
        }
    }

    class ActivityViewInflatingDelegate<T : ViewBinding>(val activity: AppCompatActivity, val viewBindingFactory: (LayoutInflater) -> T) : ReadOnlyProperty<AppCompatActivity, T> {
        private var binding: T? = null

        init {
            activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onCreate(owner: LifecycleOwner) {
                    if (binding == null) {
                        binding = viewBindingFactory(activity.layoutInflater).also { activity.setContentView(it.root) }
                    }
                }
            })
        }

        override fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T = binding ?: viewBindingFactory(activity.layoutInflater).also {
            thisRef.setContentView(it.root)
            this.binding = it
        }
    }

    class ActivityViewBindingDelegate<T : ViewBinding>(val activity: AppCompatActivity, val viewBindingFactory: (View) -> T) : ReadOnlyProperty<AppCompatActivity, T> {
        private var binding: T? = null

        init {
            activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onCreate(owner: LifecycleOwner) {
                    if (binding == null) {
                        binding = viewBindingFactory(getRootView())
                    }
                }
            })
        }

        fun getRootView(): View = activity.findViewById<ContentFrameLayout>(android.R.id.content).getChildAt(0)

        override fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T = binding ?: viewBindingFactory(getRootView()).also { this.binding = it }
    }

    fun <T : ViewBinding> AppCompatActivity.viewBinding(viewBindingFactory: (LayoutInflater) -> T) = ActivityViewInflatingDelegate(this, viewBindingFactory)

    fun <T : ViewBinding> AppCompatActivity.viewBinding(viewBindingFactory: (View) -> T) = ActivityViewBindingDelegate(this, viewBindingFactory)
}