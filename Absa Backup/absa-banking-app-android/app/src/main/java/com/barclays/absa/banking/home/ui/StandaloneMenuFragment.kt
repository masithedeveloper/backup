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
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.manage.devices.DeviceListActivity
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.contactUs.ContactUsActivity
import com.barclays.absa.banking.presentation.contactUs.ContactUsContainerActivity
import com.barclays.absa.banking.presentation.help.FeedbackActivity
import com.barclays.absa.banking.presentation.help.HelpActivity
import com.barclays.absa.banking.presentation.security.SecurityActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.settings.ui.SettingsHubActivity
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog.showAlertDialog
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.PermissionHelper
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.standalone_menu_fragment.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import java.util.*

class StandaloneMenuFragment : BaseFragment(R.layout.standalone_menu_fragment) {

    private lateinit var hostActivity: StandaloneHomeActivity
    private lateinit var menuViewModel: MenuViewModel

    companion object {
        const val VIEWPAGER_ANIMATION_DURATION_MILLISECONDS = 280
        const val LANGUAGE_RESULT_CODE = 1

        fun newInstance(): MenuFragment {
            return MenuFragment()
        }
    }

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as StandaloneHomeActivity
        menuViewModel = hostActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFeatureSwitchingVisibilityToggles()
        initViews()
    }

    private fun setupFeatureSwitchingVisibilityToggles() {
        featureSwitchingToggles.let {
            if (it.clickToCall == FeatureSwitchingStates.GONE.key) {
                callMeBackOptionActionButtonView.visibility = View.GONE
            }

            if (it.branchLocator == FeatureSwitchingStates.GONE.key) {
                branchLocatorActionButtonView.visibility = View.GONE
            }
        }
    }

    private fun initViews() {
        setOnClickListeners()
        lastLoginTextView.text = lastLoginTime
    }

    private fun setOnClickListeners() {
        val featureSwitchingToggles = featureSwitchingToggles
        val atmBranchLocator = featureSwitchingToggles.branchLocator

        if (!BuildConfig.TOGGLE_DEF_BRANCH_LOCATOR_ENABLED || atmBranchLocator == FeatureSwitchingStates.GONE.key) {
            branchLocatorActionButtonView.visibility = View.GONE
        } else {
            branchLocatorActionButtonView.setOnClickListener {
                if (atmBranchLocator == FeatureSwitchingStates.DISABLED.key) {
                    startActivity(IntentFactory.capabilityUnavailable(hostActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_atm_branch_locator))))
                } else {
                    AnalyticsUtil.trackAction("ATM Branch Locator", "ATMAndBranchLocator_MenuScreen_ATMAndBranchLocatorClicked")
                    BaseActivity.preventDoubleClick(branchLocatorActionButtonView)
                    requestPermissions()
                }
            }
        }

        contactUsActionButtonView.setOnClickListener { startActivity(Intent(hostActivity, ContactUsActivity::class.java)) }
        helpActionButtonView.setOnClickListener { startActivity(Intent(hostActivity, HelpActivity::class.java)) }
        manageDevicesActionButtonView.setOnClickListener { startActivity(Intent(hostActivity, DeviceListActivity::class.java)) }
        securityActionButtonView.setOnClickListener { startActivity(Intent(hostActivity, SecurityActivity::class.java)) }
        feedbackActionButtonView.setOnClickListener { startActivity(Intent(hostActivity, FeedbackActivity::class.java)) }

        val clickToCall = featureSwitchingToggles.clickToCall
        val clickToCallEnabled = clickToCall == FeatureSwitchingStates.DISABLED.key
        if (clickToCallEnabled) {
            callMeBackOptionActionButtonView.visibility = View.GONE
        } else {
            callMeBackOptionActionButtonView.setOnClickListener { startActivity(Intent(hostActivity, ContactUsContainerActivity::class.java)) }
        }

        settingsActionButtonView.setOnClickListener {
            val settingIntent = Intent(hostActivity, SettingsHubActivity::class.java)
            startActivity(settingIntent)
            hostActivity.finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        dismissProgressDialog()
    }

    override fun onResume() {
        super.onResume()

        if (menuViewModel.justReturnedFromSettings) {
            menuViewModel.justReturnedFromSettings = false
            requestPermissions()
        }
        menuConstraintLayout.animate().scaleY(1f).setDuration(VIEWPAGER_ANIMATION_DURATION_MILLISECONDS.toLong()).setListener(null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LANGUAGE_RESULT_CODE) {
            val activity = activity as HomeContainerActivity?
            activity?.recreate()
        }
    }

    private fun requestPermissions() {
        if (checkIfNetworkAndGPSIsEnabled()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(hostActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                PermissionHelper.requestLocationAccessPermission(hostActivity) {
                    startActivity(Intent(hostActivity, AtmBranchLocatorActivity::class.java))
                }
            } else {
                if (ActivityCompat.checkSelfPermission(hostActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    showResultScreen(isSuccess = false, isPermissionError = true)
                } else {
                    if (hostActivity.currentFragment is GenericResultScreenFragment) {
                        parentFragmentManager.popBackStack()
                    }
                    startActivity(Intent(baseActivity, AtmBranchLocatorActivity::class.java))
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PermissionHelper.PermissionCode.ACCESS_LOCATION.value && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (checkIfNetworkAndGPSIsEnabled()) {
                startActivity(Intent(hostActivity, AtmBranchLocatorActivity::class.java))
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showResultScreen(isSuccess: Boolean?, isPermissionError: Boolean) {

        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
        val resultsBundle = Bundle()

        if (isPermissionError) {
            resultScreenProperties.setResultScreenAnimation(ResultAnimations.generalFailure)
                    .setTitle(getString(R.string.location_access_denied_title))
                    .setDescription(getString(R.string.location_access_denied_description))
                    .setPrimaryButtonLabel(getString(R.string.ok))
                    .setSecondaryButtonLabel(getString(R.string.branch_help_camera_open_settings))
        }
        val primaryOnClick = View.OnClickListener {
            parentFragmentManager.popBackStack()
            hostActivity.setBottomNavigationMenuVisibility(View.VISIBLE)
            hostActivity.showToolBar()
        }
        val secondaryOnClick = View.OnClickListener {
            menuViewModel.justReturnedFromSettings = true
            val uri = Uri.parse("package:" + baseActivity.packageName)
            val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
            startActivity(settingsIntent)
        }

        resultsBundle.putSerializable(GenericResultScreenFragment.GENERIC_RESULT_PROPERTIES_KEY, resultScreenProperties.build(isSuccess!!))

        val genericResultsScreenFragment = GenericResultScreenFragment.newInstance(resultScreenProperties.build(true), false, primaryOnClick, secondaryOnClick)
        hostActivity.startFragment(genericResultsScreenFragment, R.id.content_frame_layout, false, BaseActivity.AnimationType.FADE, true, "")
        hostActivity.hideToolBar()
        hostActivity.setBottomNavigationMenuVisibility(View.GONE)
    }

    private fun checkIfNetworkAndGPSIsEnabled(): Boolean {
        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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

}