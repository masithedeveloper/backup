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

package com.barclays.absa.banking.home.ui

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.utils.EmailUtil
import com.barclays.absa.banking.framework.utils.TelephoneUtil
import com.barclays.absa.banking.home.services.clickToCall.dto.CallBackVerificationDataModel
import com.barclays.absa.banking.presentation.contactUs.ContactUsContracts
import com.barclays.absa.banking.presentation.contactUs.ContactUsFragment
import com.barclays.absa.banking.presentation.contactUs.EnterSecurityCodeFragment
import com.barclays.absa.banking.presentation.contactUs.ReportFraudFragment
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog.dismissAlertDialog
import com.barclays.absa.banking.shared.BaseAlertDialog.showAlertDialog
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.PasswordGenerator
import com.barclays.absa.utils.imageHelpers.ProfileViewImageHelper
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.standalone_container_activity.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties

class StandaloneHomeActivity : BaseActivity(), ContactUsContracts.ContactUsView, ContactUsContracts.ReportFraudView {
    private lateinit var profileViewImageHelper: ProfileViewImageHelper
    private lateinit var secretCode: String
    private lateinit var callMeBackUniqueReferenceNumber: String
    private lateinit var homeContainerViewModel: HomeContainerViewModel
    private lateinit var logoutMenuItem: MenuItem
    private lateinit var fragment: Fragment
    private val application by lazy { BMBApplication.getInstance() }
    private var headingFadeType = HeadingFadeType.None
    private lateinit var colorStateList: ColorStateList

    companion object {
        private const val HEADING_TRANSITION_DURATION_MILLISECONDS = 500
    }

    enum class HeadingFadeType {
        None,
        FadeFromContactUs,
        FadeToContactUs
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.standalone_container_activity)
        setToolbar(this)
        homeContainerViewModel = viewModel()
        homeContainerViewModel.stopListeningForAuth()
        profileViewImageHelper = ProfileViewImageHelper(this)
        profileViewImageHelper.profileView(profileView)
        fragment = StandaloneAccountFragment()
        startFragment(fragment)
        attachEventHandlers()
        attachDataObservers()
        homeContainerViewModel.setupSession()
        CommonUtils.isOddActivityTransition = false
        performAnalytics()
        checkSecureHomePageObjectExists()

        val stateArray = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf(-android.R.attr.state_checked))
        val colorArray = intArrayOf(ContextCompat.getColor(this, R.color.smile), ContextCompat.getColor(this, R.color.bottom_navigation_icon_color))
        colorStateList = ColorStateList(stateArray, colorArray)
        bottomNavigationView.itemIconTintList = colorStateList
    }

    private fun attachEventHandlers() {
        var isHeadingDark = true
        bottomNavigationView.setOnNavigationItemSelectedListener { p0 ->
            var shouldReplace = true
            when (p0.itemId) {
                R.id.bottom_nav_home -> {
                    if (fragment is StandaloneAccountFragment) {
                        shouldReplace = false
                    } else {
                        shouldReplace = true
                        isHeadingDark = true
                        fragment = StandaloneAccountFragment()
                        profileView.visibility = View.VISIBLE
                    }

                }
                R.id.bottom_nav_contactus -> {
                    if (fragment is ContactUsFragment) {
                        shouldReplace = false
                    } else {
                        shouldReplace = true
                        isHeadingDark = false
                        fragment = ContactUsFragment()
                        profileView.visibility = View.GONE
                    }
                }
                R.id.bottom_nav_menu -> {
                    if (fragment is StandaloneMenuFragment) {
                        shouldReplace = false
                    } else {
                        shouldReplace = true
                        isHeadingDark = true
                        content_frame_layout.alpha = 0.0f
                        fragment = StandaloneMenuFragment()
                        profileView.visibility = View.VISIBLE
                    }
                }
            }

            if (shouldReplace) {
                setHeadingStyle(fragment, isHeadingDark)
                startFragment(fragment)
            }

            true
        }
    }

    private fun attachDataObservers() {
        homeContainerViewModel.callBackLiveData.observe(this, {
            dismissProgressDialog()
            homeContainerViewModel.callBackLiveData.removeObservers(this)
            callMeBackUniqueReferenceNumber = it
            val enterSecurityCodeFragment = EnterSecurityCodeFragment.newInstance(null, secretCode)
            bottomNavigationView.visibility = View.VISIBLE
            setHeadingStyle(enterSecurityCodeFragment, false)
            startFragment(enterSecurityCodeFragment)
        })

        homeContainerViewModel.callBackVerificationLiveData.observe(this, {
            dismissProgressDialog()
            homeContainerViewModel.callBackVerificationLiveData.removeObservers(this)
            navigateToCallMeBackSuccessScreen()
        })

        homeContainerViewModel.failureLiveData.observe(this, {
            dismissProgressDialog()
            homeContainerViewModel.failureLiveData.removeObservers(this)
            when (it.first) {
                FailureType.CALLBACK, FailureType.CALLBACK_VERIFICATION -> {
                    navigateToCallMeBackFailureScreen()
                }
                FailureType.POLICY_LIST -> {
                    showMessageError(it.second)
                }
                FailureType.POLICY_DETAILS -> {
                    startActivity(IntentFactory.getSomethingWentWrongScreen(this, R.string.claim_error_text, R.string.connectivity_maintenance_message))
                }
            }
        })
    }

    private fun performAnalytics() {
        trackUserLogin()
        mScreenName = "Standalone Screen"
        mSiteSection = "Standalone"
        AnalyticsUtil.trackAction(mSiteSection, mScreenName)
    }

    private fun checkSecureHomePageObjectExists() {
        if (appCacheService.getSecureHomePageObject() == null) {
            toastLong(R.string.sessionExpiredMessage)
            logoutAndGoToStartScreen()
        }
    }

    override fun onBackPressed() {
        if (fragment !is StandaloneAccountFragment) {
            bottomNavigationView.selectedItemId = R.id.bottom_nav_home
        }
    }

    override fun logoutAndGoToStartScreen() {
        application.logoutAndGoToStartScreen()
    }

    private fun setToolbar(activity: AppCompatActivity) {
        setToolBarBack("") { onBackPressed() }
        activity.supportActionBar?.apply { setDisplayHomeAsUpEnabled(false) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_container_activity_menu, menu)
        logoutMenuItem = menu.findItem(R.id.logout_menu_item)
        setMenuItemTextColor(true)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout_menu_item) {
            showLogoutDialog()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun setBottomNavigationMenuVisibility(visibility: Int) {
        bottomNavigationView.visibility = visibility
    }

    fun startFragment(fragment: Fragment) {
        startFragment(fragment, R.id.content_frame_layout, true, AnimationType.NONE, true, fragment.javaClass.name)
        if (findViewById<View>(R.id.content_frame_layout).alpha == 0f) {
            findViewById<View>(R.id.content_frame_layout).animate().alpha(1f).duration = 500
        }
    }

    override fun emailAppSupportOrGeneralEnquiry(emailAddress: String) {
        EmailUtil.email(this, emailAddress)
    }

    override fun call(phoneNumber: String) {
        TelephoneUtil.call(this, phoneNumber)
    }

    override fun initView() {}

    override fun isCallMeBackSessionAvailable(): Boolean {
        return secretCode.isNotEmpty()
    }

    override fun navigateToContactUsFragment() {
        showActionBarForContactUs()
        val contactUsFragment = ContactUsFragment()
        bottomNavigationView.visibility = View.GONE
        setHeadingStyle(contactUsFragment, false)
        startFragment(contactUsFragment)
    }

    override fun navigateToReportFraudFragment() {
        showActionBarForContactUs()
        val reportFraudFragment = ReportFraudFragment()
        bottomNavigationView.visibility = View.VISIBLE
        setHeadingStyle(reportFraudFragment, true)
        startFragment(reportFraudFragment)
    }

    override fun navigateToCallMeBackFragment(description: String?) {
        val enterSecurityCodeFragment = EnterSecurityCodeFragment.newInstance(description, secretCode)
        bottomNavigationView.visibility = View.VISIBLE
        setHeadingStyle(enterSecurityCodeFragment, true)
        startFragment(enterSecurityCodeFragment)
    }

    private fun navigateToCallMeBackSuccessScreen() {
        val onClickListener = View.OnClickListener { onBackPressed() }

        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation("general_success.json")
                .setDescription(getString(R.string.your_call_is_secure))
                .setTitle(getString(R.string.call_verified))
                .setPrimaryButtonLabel(getString(R.string.done_action))
                .build(true)

        val genericResultScreenFragment = GenericResultScreenFragment.newInstance(resultScreenProperties, false, onClickListener, null)
        startFragment(genericResultScreenFragment)
        bottomNavigationView.visibility = View.GONE
    }

    private fun navigateToCallMeBackFailureScreen() {
        val onClickListener = View.OnClickListener { navigateToReportFraudFragment() }

        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation("general_failure.json")
                .setDescription(getString(R.string.your_call_is_not_secure_description))
                .setTitle(getString(R.string.your_call_is_not_secure))
                .setSecondaryButtonLabel(getString(R.string.try_again))
                .build(false)

        val genericResultScreenFragment = GenericResultScreenFragment.newInstance(resultScreenProperties, false, null, onClickListener)
        startFragment(genericResultScreenFragment)
        bottomNavigationView.visibility = View.GONE
    }

    override fun requestCall() {
        secretCode = generateSecretCode()
        val callBackDateTime = "NOW"

        homeContainerViewModel.requestCallBack(secretCode, callBackDateTime)
    }

    private fun generateSecretCode(): String {
        val passwordBuilder = PasswordGenerator.PasswordGeneratorBuilder()
                .useDigits(true)
                .build()

        return passwordBuilder.generate(6)
    }

    private fun showActionBarForContactUs() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.contact_absa)
        }
    }

    private fun setHeadingStyle(fragment: Fragment, isDark: Boolean) {
        if (fragment is ContactUsFragment) {
            headingFadeType = HeadingFadeType.FadeToContactUs
        } else if (headingFadeType == HeadingFadeType.FadeToContactUs) {
            headingFadeType = HeadingFadeType.FadeFromContactUs
        }

        when (headingFadeType) {
            HeadingFadeType.None -> toolbar.setBackgroundColor(Color.TRANSPARENT)
            HeadingFadeType.FadeFromContactUs -> invokeHeadingTransition(false, R.drawable.home_container_tranparent_warm_purple_transition)
            HeadingFadeType.FadeToContactUs -> invokeHeadingTransition(true, R.drawable.home_container_warm_purple_transparent_transition)
        }
        setMenuTextAppearance(isDark)
    }

    private fun setMenuTextAppearance(isDark: Boolean) {
        val toolBar = toolbar as Toolbar
        val toolbarStyle = if (isDark) {
            za.co.absa.presentation.uilib.R.style.ToolbarDarkTheme_TitleTextStyle
        } else {
            za.co.absa.presentation.uilib.R.style.ToolbarLiteTheme_TitleTextStyle
        }

        toolBar.setTitleTextAppearance(this, toolbarStyle)
        setMenuItemTextColor(isDark)
    }

    private fun setMenuItemTextColor(isDark: Boolean) {
        if (isDark) {
            setMenuItemTextColor(logoutMenuItem, ContextCompat.getColor(this, za.co.absa.presentation.uilib.R.color.graphite_light_theme_item_color))
        } else {
            setMenuItemTextColor(logoutMenuItem, Color.WHITE)
        }
    }

    private fun setMenuItemTextColor(menuItem: MenuItem?, colorId: Int) {
        val menuItemString = SpannableString(menuItem?.title.toString())
        menuItemString.setSpan(ForegroundColorSpan(colorId), 0, menuItemString.length, 0)
        menuItem?.title = menuItemString
    }

    private fun invokeHeadingTransition(isReverseTransition: Boolean, transitionDrawableId: Int) {
        toolbar.setBackgroundResource(transitionDrawableId)
        ContextCompat.getDrawable(this, transitionDrawableId)?.let {
            val transition = TransitionDrawable(arrayOf(it))
            if (isReverseTransition) {
                transition.reverseTransition(HEADING_TRANSITION_DURATION_MILLISECONDS)
            } else {
                transition.startTransition(HEADING_TRANSITION_DURATION_MILLISECONDS)
            }
        }
    }

    override fun verifyCallBack(secretCode: String) {
        homeContainerViewModel.requestVerificationCallBack(CallBackVerificationDataModel().apply {
            this.secretCode = secretCode
            secretCodeVerified = true
            uniqueRefNo = callMeBackUniqueReferenceNumber
        })
    }

    override fun clearSecretCode() {
        secretCode = ""
    }

    private fun showLogoutDialog() {
        showAlertDialog(AlertDialogProperties.Builder()
                .title(getString(R.string.home_page_logout_option))
                .message(getString(R.string.logout_dialog))
                .positiveButton(getString(R.string.home_page_logout_option))
                .negativeButton(getString(R.string.cancel))
                .positiveDismissListener { _, _ ->
                    application.is_RVN_checked = false
                    logoutAndGoToStartScreen()
                    trackLogoutPopUpYes(mScreenName)
                    appCacheService.setPrimarySecondFactorDevice(false)
                }.negativeDismissListener { _, _ ->
                    trackLogoutPopUpNo(mScreenName)
                    dismissAlertDialog()
                }
                .build())
    }

    override fun showConfirmCallDialog() {
        val cellNumber = CustomerProfileObject.instance.cellNumber
        showAlertDialog(AlertDialogProperties.Builder()
                .title(getString(R.string.confirm_call_back))
                .message(if (cellNumber == null) getString(R.string.cellphone_number_ending_no_number) else getString(R.string.cellphone_number_ending, cellNumber))
                .positiveButton(getString(R.string.ok))
                .negativeButton(getString(R.string.cancel))
                .positiveDismissListener { _, _ -> requestCall() }
                .build())
    }

    override fun showMaxAttemptsDialog(attemptCount: Int, listener: DialogInterface.OnClickListener?) {
        showAlertDialog(AlertDialogProperties.Builder()
                .title(getString(R.string.confirm_call_back))
                .message(getString(R.string.contact_us_attempts, attemptCount))
                .positiveDismissListener(listener ?: DialogInterface.OnClickListener { _, _ -> dismissAlertDialog() })
                .build())
    }
}