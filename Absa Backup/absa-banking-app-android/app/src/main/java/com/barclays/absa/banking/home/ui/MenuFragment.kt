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

package com.barclays.absa.banking.home.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.atmAndBranchLocator.ui.AtmBranchLocatorActivity
import com.barclays.absa.banking.beneficiaries.services.IBeneficiaryCacheService
import com.barclays.absa.banking.beneficiaries.ui.BeneficiaryLandingActivity
import com.barclays.absa.banking.boundary.model.AccessPrivileges
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.card.ui.creditCard.hub.CardListActivity
import com.barclays.absa.banking.debiCheck.ui.DebiCheckActivity
import com.barclays.absa.banking.debiCheck.ui.DebiCheckWhatsNewActivity
import com.barclays.absa.banking.dualAuthorisations.ui.AuthorisationHubActivity
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.framework.utils.AppConstants
import com.barclays.absa.banking.manage.devices.DeviceListActivity
import com.barclays.absa.banking.manage.profile.ui.ManageProfileActivity
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants
import com.barclays.absa.banking.payments.international.InternationalBankingActivity
import com.barclays.absa.banking.presentation.contactUs.ContactUsActivity
import com.barclays.absa.banking.presentation.contactUs.ContactUsContainerActivity
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.banking.presentation.help.FeedbackActivity
import com.barclays.absa.banking.presentation.help.HelpActivity
import com.barclays.absa.banking.presentation.security.SecurityActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.presentation.whatsApp.WhatsAppOfferActivity
import com.barclays.absa.banking.presentation.whatsNew.WhatsNewHelper
import com.barclays.absa.banking.presentation.whatsNew.WhatsNewLottieActivity
import com.barclays.absa.banking.settings.ui.ManageAccountsActivity
import com.barclays.absa.banking.settings.ui.ManageDigitalLimitsActivity
import com.barclays.absa.banking.settings.ui.ProfileViewHelper
import com.barclays.absa.banking.settings.ui.SettingsHubActivity
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.barclays.absa.banking.shared.BaseAlertDialog.showAlertDialog
import com.barclays.absa.utils.*
import com.barclays.absa.utils.imageHelpers.ProfileViewImageHelper
import kotlinx.android.synthetic.main.menu_fragment.*
import styleguide.content.Profile
import styleguide.screens.GenericResultScreenFragment
import java.util.*

class MenuFragment : BaseFragment(R.layout.menu_fragment), HomeMenuView {

    private lateinit var menuViewModel: MenuViewModel

    private lateinit var homeContainerActivity: HomeContainerActivity
    private lateinit var profileImageHelper: ProfileViewImageHelper
    private var shouldBlockUser = false

    private var showSwift = true
    private var showWesternUnion = true

    private val lastLoginTime: String
        get() {
            val lastLogin = String.format(getString(R.string.last_login_text), CustomerProfileObject.instance.lastLoginTime)
            var versionCode = ""
            if (!BuildConfig.PRD) {
                versionCode = String.format(Locale.ENGLISH, " (%d)", BuildConfig.VERSION_CODE)
            }

            val versionName = if (BuildConfig.IS_HUAWEI_BUILD) BuildConfig.VERSION_NAME + "H" else BuildConfig.VERSION_NAME
            return getString(R.string.app_version, versionName, versionCode, lastLogin)
        }

    private val beneficiaryCacheService:IBeneficiaryCacheService = getServiceInterface()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeContainerActivity = context as HomeContainerActivity
        menuViewModel = homeContainerActivity.viewModel()
    }

    private fun setProfileImage() {
        val customerProfileObject = CustomerProfileObject.instance

        profileImageHelper = ProfileViewImageHelper(homeContainerActivity)
        profileView.apply {
            setBackgroundImageVisible()
            setProfile(Profile(customerProfileObject.customerName, ProfileViewHelper.getCustomerAccountType(homeContainerActivity), profileImageHelper.imageBitmap))
            if (!homeContainerActivity.isBusinessAccount && !AccessPrivileges.instance.isOperator && BuildConfig.TOGGLE_DEF_MANAGE_PROFILE_ENABLED) {
                setSecondaryImage(R.drawable.ic_more_alt_dark)
                setSecondaryImageVisible()
                setOnClickListener {
                    if (featureSwitchingToggles.manageProfile == FeatureSwitchingStates.ACTIVE.key) {
                        AnalyticsUtil.trackAction(ManageProfileConstants.ANALYTICS_TAG, "ManageProfile_MenuScreen_ManageProfileButtonClicked")
                        startActivity(Intent(homeContainerActivity, ManageProfileActivity::class.java))
                    } else {
                        startActivity(IntentFactory.capabilityUnavailable(homeContainerActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_manage_profile))))
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shouldBlockUser = savedInstanceState?.getBoolean(SHOULD_BLOCK_USER, false) ?: false
        menuViewModel.fetchCustomerDetails()

        if (homeContainerActivity.shouldBlockUser()) {
            cardsActionButtonView.visibility = View.GONE
        }

        beneficiaryCacheService.setTabPositionToReturnTo("")
        beneficiaryCacheService.setBeneficiaryAdded(false)
        initViews()
    }

    private fun initViews() {
        setupFeatureSwitchingVisibilityToggles()
        setOnClickListeners()
        setProfileImage()
        lastLoginTextView.text = lastLoginTime
        if (homeContainerActivity.isBusinessAccount && !OperatorPermissionUtils.isMainUser()) {
            debitOrdersActionButtonView.visibility = View.GONE
        }
        if (homeContainerActivity.isBusinessAccount) {
            whatsAppActionButtonView.visibility = View.GONE
            internationalPaymentsActionButton.visibility = View.GONE
        }

        if (WhatsNewHelper.getEnabledWhatsScreens().isEmpty()) {
            whatsNewActionButtonView.visibility = View.GONE
        }

        menuViewModel.isInternationalPaymentsOptionVisible.observe(viewLifecycleOwner, { clientTypeResponse ->
            if (clientTypeResponse.identityType == IdTypes.IDENTITY_DOCUMENT.typeCode) {
                ValidationUtils.isOlderThan18(clientTypeResponse.identityNumber).let {
                    showSwift = it
                    showWesternUnion = it
                }
            }
            showWesternUnion = showWesternUnion && clientTypeResponse.isInternationalPaymentsOptionVisible
            showSwift = showSwift && isSwiftAllowedForClientType && BuildConfig.TOGGLE_DEF_SWIFT_ENABLED

            dismissProgressDialog()
            toggleInternationalPaymentsMenuItem(showWesternUnion && !isBusinessAccount)
        })

        menuViewModel.failureResponse.observe(viewLifecycleOwner, {
            dismissProgressDialog()
        })
    }

    override fun toggleInternationalPaymentsMenuItem(isVisible: Boolean) {
        showWesternUnion = isVisible && OperatorPermissionUtils.isMainUser()

        showWesternUnion = showWesternUnion && !homeContainerActivity.isStandaloneCreditCardAccount
        if (showSwift || showWesternUnion) {
            internationalPaymentsActionButton.visibility = View.VISIBLE
        } else {
            internationalPaymentsActionButton.visibility = View.GONE
        }
    }

    private fun setupFeatureSwitchingVisibilityToggles() {
        if (featureSwitchingToggles.debitOrderHubWithMinimumValue == FeatureSwitchingStates.GONE.key && featureSwitchingToggles.debiCheck == FeatureSwitchingStates.GONE.key) {
            debitOrdersActionButtonView.visibility = View.GONE
        }

        if (featureSwitchingToggles.businessBankingAuthorizations == FeatureSwitchingStates.GONE.key) {
            authorisationsActionButtonView.visibility = View.GONE
        }

        if (featureSwitchingToggles.clickToCall == FeatureSwitchingStates.GONE.key) {
            callMeBackOptionActionButtonView.visibility = View.GONE
        }

        if (featureSwitchingToggles.internationalPayments == FeatureSwitchingStates.GONE.key) {
            internationalPaymentsActionButton.visibility = View.GONE
        }

        if (featureSwitchingToggles.manageAccounts == FeatureSwitchingStates.GONE.key) {
            manageAccountsActionButtonView.visibility = View.GONE
        }

        if (featureSwitchingToggles.branchLocator == FeatureSwitchingStates.GONE.key) {
            branchLocatorActionButtonView.visibility = View.GONE
        }

        if (isBusinessAccount && featureSwitchingToggles.businessBankingManageCards == FeatureSwitchingStates.GONE.key) {
            cardsActionButtonView.visibility = View.GONE
        }
    }

    private fun setOnClickListeners() {
        if (!BuildConfig.TOGGLE_DEF_BRANCH_LOCATOR_ENABLED || featureSwitchingToggles.branchLocator == FeatureSwitchingStates.GONE.key) {
            branchLocatorActionButtonView.visibility = View.GONE
        } else {
            branchLocatorActionButtonView.setOnClickListener {
                if (featureSwitchingToggles.branchLocator == FeatureSwitchingStates.DISABLED.key) {
                    startActivity(IntentFactory.capabilityUnavailable(homeContainerActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_atm_branch_locator))))
                } else {
                    AnalyticsUtil.trackAction("ATM Branch Locator", "ATMAndBranchLocator_MenuScreen_ATMAndBranchLocatorClicked")
                    BaseActivity.preventDoubleClick(branchLocatorActionButtonView)
                    requestPermissions()
                }
            }
        }

        cardsActionButtonView.setOnClickListener { startActivity(Intent(homeContainerActivity, CardListActivity::class.java)) }
        if (AccessPrivileges.instance.isOperator) {
            debitOrdersActionButtonView.visibility = View.GONE
        } else {
            debitOrdersActionButtonView.setOnClickListener {
                val debiCheck = featureSwitchingToggles.debiCheck
                val debitOrder = featureSwitchingToggles.debitOrderHubWithMinimumValue
                val isDebitOrderHubDisabled = debiCheck == FeatureSwitchingStates.DISABLED.key && debitOrder == FeatureSwitchingStates.DISABLED.key
                if (isDebitOrderHubDisabled) {
                    startActivity(IntentFactory.capabilityUnavailable(homeContainerActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_debit_orders))))
                } else {
                    AnalyticsUtil.trackAction("Debit Orders", "DebitOrders_MenuScreen_DebitOrderButtonClicked")
                    if (SharedPreferenceService.getDebiCheckFirstVisitDone()) {
                        startActivity(Intent(homeContainerActivity, DebiCheckActivity::class.java))
                    } else {
                        startActivity(Intent(homeContainerActivity, DebiCheckWhatsNewActivity::class.java))
                    }
                }
            }
        }
        contactUsActionButtonView.setOnClickListener { startActivity(Intent(homeContainerActivity, ContactUsActivity::class.java)) }
        helpActionButtonView.setOnClickListener { startActivity(Intent(homeContainerActivity, HelpActivity::class.java)) }
        manageDevicesActionButtonView.setOnClickListener { startActivity(Intent(homeContainerActivity, DeviceListActivity::class.java)) }
        securityActionButtonView.setOnClickListener { startActivity(Intent(homeContainerActivity, SecurityActivity::class.java)) }
        feedbackActionButtonView.setOnClickListener { startActivity(Intent(homeContainerActivity, FeedbackActivity::class.java)) }
        manageDigitalLimitsActionButtonView.setOnClickListener { startActivity(Intent(homeContainerActivity, ManageDigitalLimitsActivity::class.java)) }
        authorisationsActionButtonView.setOnClickListener {
            val businessBankingAuthorizations = featureSwitchingToggles.businessBankingAuthorizations
            val businessBankingAuthorizationsEnabled = businessBankingAuthorizations == FeatureSwitchingStates.DISABLED.key
            if (businessBankingAuthorizationsEnabled) {
                startActivity(IntentFactory.capabilityUnavailable(homeContainerActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_authorisations))))
            } else {
                startActivity(Intent(homeContainerActivity, AuthorisationHubActivity::class.java))
            }
        }

        val clickToCall = featureSwitchingToggles.clickToCall
        val clickToCallEnabled = clickToCall == FeatureSwitchingStates.DISABLED.key
        if (clickToCallEnabled) {
            callMeBackOptionActionButtonView.visibility = View.GONE
        } else {
            callMeBackOptionActionButtonView.setOnClickListener { startActivity(Intent(homeContainerActivity, ContactUsContainerActivity::class.java)) }
        }

        internationalPaymentsActionButton.setOnClickListener {
            startActivity(Intent(homeContainerActivity, InternationalBankingActivity::class.java)
                    .putExtra(SHOW_WESTERN_UNION_INTENT_KEY, showWesternUnion)
                    .putExtra(SHOW_SWIFT_INTENT_KEY, showSwift))
        }

        documentsActionButton.setOnClickListener {
            startActivity(Intent(baseActivity, DocumentsActivity::class.java))
        }

        whatsAppActionButtonView.setOnClickListener {
            startActivity(Intent(homeContainerActivity, WhatsAppOfferActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false)
                putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_AIRTIME)
            })
        }

        settingsActionButtonView.setOnClickListener {
            startActivity(Intent(homeContainerActivity, SettingsHubActivity::class.java))
        }

        whatsNewActionButtonView.setOnClickListener {
            startActivity(Intent(homeContainerActivity, WhatsNewLottieActivity::class.java))
        }

        beneficiaryActionButtonView.setOnClickListener {
            if (AccessPrivileges.instance.isOperator) {
                if (!AccessPrivileges.instance.beneficiaryPaymentAllowed && !AccessPrivileges.instance.isPrepaidAllowed && !AccessPrivileges.instance.isPrepaidElectricityAllowed && !AccessPrivileges.instance.cashSendAllowed) {
                    BaseAlertDialog.showRequestAccessAlertDialog(getString(R.string.manage_beneficiary_title))
                } else if (!AccessPrivileges.instance.beneficiaryPaymentAllowed && AccessPrivileges.instance.isPrepaidAllowed) {
                    navigateToBeneficiaryScreen(BMBConstants.PASS_AIRTIME)
                } else if (!AccessPrivileges.instance.beneficiaryPaymentAllowed && !AccessPrivileges.instance.isPrepaidAllowed && AccessPrivileges.instance.cashSendAllowed) {
                    navigateToBeneficiaryScreen(BMBConstants.PASS_CASHSEND)
                } else if (AccessPrivileges.instance.beneficiaryPaymentAllowed) {
                    navigateToBeneficiaryScreen(BMBConstants.PASS_PAYMENT)
                }
            } else {
                navigateToBeneficiaryScreen(BMBConstants.PASS_PAYMENT)
            }
        }

        if (BuildConfig.TOGGLE_DEF_MANAGE_ACCOUNTS_ENABLED && !AccessPrivileges.instance.isOperator) {
            manageAccountsActionButtonView.visibility = View.VISIBLE
            manageAccountsActionButtonView.setOnClickListener {
                val manageAccounts = featureSwitchingToggles.manageAccounts
                if (manageAccounts == FeatureSwitchingStates.DISABLED.key) {
                    startActivity(IntentFactory.capabilityUnavailable(homeContainerActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_manage_accounts))))
                } else {
                    startActivity(Intent(homeContainerActivity, ManageAccountsActivity::class.java))
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SHOULD_BLOCK_USER, shouldBlockUser)
        dismissProgressDialog()
    }

    override fun onResume() {
        super.onResume()

        if (menuViewModel.justReturnedFromSettings) {
            menuViewModel.justReturnedFromSettings = false
            requestPermissions()
        }

        profileView.animate().scaleX(1f).scaleY(1f).setDuration(VIEWPAGER_ANIMATION_DURATION_MILLISECONDS.toLong()).setListener(null)
        menuConstraintLayout.animate().scaleY(1f).setDuration(VIEWPAGER_ANIMATION_DURATION_MILLISECONDS.toLong()).setListener(null)
        profileImageHelper.profileView(profileView)
    }

    private fun navigateToBeneficiaryScreen(accessType: String) {
        val manageBeneficiaryFullIntent = Intent(homeContainerActivity, BeneficiaryLandingActivity::class.java)
        manageBeneficiaryFullIntent.putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, accessType)
        startActivity(manageBeneficiaryFullIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LANGUAGE_RESULT_CODE) {
            homeContainerActivity.recreate()
        }
    }

    private fun requestPermissions() {
        if (checkIfNetworkAndGPSIsEnabled()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(homeContainerActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                PermissionHelper.requestLocationAccessPermission(homeContainerActivity) {
                    startActivity(Intent(homeContainerActivity, AtmBranchLocatorActivity::class.java))
                }
            } else {
                if (ActivityCompat.checkSelfPermission(homeContainerActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    showResultScreen()
                } else {
                    if (homeContainerActivity.currentFragment is GenericResultScreenFragment) {
                        parentFragmentManager.popBackStack()
                    }
                    startActivity(Intent(homeContainerActivity, AtmBranchLocatorActivity::class.java))
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PermissionHelper.PermissionCode.ACCESS_LOCATION.value && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (checkIfNetworkAndGPSIsEnabled()) {
                startActivity(Intent(homeContainerActivity, AtmBranchLocatorActivity::class.java))
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showResultScreen() {
        GenericResultActivity.topOnClickListener = View.OnClickListener {
            menuViewModel.justReturnedFromSettings = true
            val uri = Uri.parse("package:" + baseActivity.packageName)
            val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
            startActivity(settingsIntent)
            BMBApplication.getInstance().topMostActivity.finish()
        }

        GenericResultActivity.bottomOnClickListener = View.OnClickListener {
            BMBApplication.getInstance().topMostActivity.finish()
        }

        val genericResultActivity = Intent(homeContainerActivity, GenericResultActivity::class.java).apply {
            putExtra(GenericResultActivity.IS_FAILURE, true)
            putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.location_access_denied_title)
            putExtra(GenericResultActivity.SUB_MESSAGE, R.string.location_access_denied_description)
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.ok)
            putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.branch_help_camera_open_settings)
        }

        homeContainerActivity.startActivity(genericResultActivity)
        homeContainerActivity.hideToolBar()
    }

    private fun checkIfNetworkAndGPSIsEnabled(): Boolean {
        val locationManager = BMBApplication.getInstance().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnabled = false

        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
        }

        if ((!gpsEnabled && !networkEnabled) && context != null) {
            showAlertDialog(AlertDialogProperties.Builder()
                    .message(getString(R.string.gps_network_not_enabled))
                    .positiveButton(getString(R.string.action_settings))
                    .negativeButton(getString(R.string.cancel))
                    .positiveDismissListener { _, _ -> context?.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                    .build())
        }
        return gpsEnabled || networkEnabled
    }

    companion object {
        const val VIEWPAGER_ANIMATION_DURATION_MILLISECONDS = 280
        const val LANGUAGE_RESULT_CODE = 1
        const val SHOULD_BLOCK_USER = "shouldBlockUser"

        const val SHOW_WESTERN_UNION_INTENT_KEY = "showWesternUnion"
        const val SHOW_SWIFT_INTENT_KEY = "showSwift"

        fun newInstance(): MenuFragment {
            return MenuFragment()
        }
    }

    private enum class IdTypes(val typeCode: String) {
        IDENTITY_DOCUMENT("01"),
        PASSPORT("03"),
        REGISTRATION_NUMBER("05")
    }
}