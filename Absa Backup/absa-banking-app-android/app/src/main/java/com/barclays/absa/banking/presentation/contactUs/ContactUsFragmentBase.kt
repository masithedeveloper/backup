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
package com.barclays.absa.banking.presentation.contactUs

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.atmAndBranchLocator.ui.AtmBranchLocatorActivity
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog.showAlertDialog
import com.barclays.absa.banking.shared.ItemPagerFragment
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.PermissionHelper
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.contact_us_fragment.*
import styleguide.buttons.OptionActionButtonView
import styleguide.content.Contact
import styleguide.content.ContactView
import styleguide.screens.GenericResultScreenFragment
import java.io.Serializable

abstract class ContactUsFragmentBase : ItemPagerFragment(), View.OnClickListener {

    companion object {
        protected const val TAB_TITLE = "tabTitle"

        fun Fragment.putTabTitle(tabTitle: String) {
            arguments = Bundle().apply { putString(TAB_TITLE, tabTitle) }
        }
    }

    private lateinit var inflatedView: View
    private lateinit var contactUsViewModel: ContactUsViewModel
    private lateinit var contactUsView: ContactUsContracts.ContactUsView

    protected var baseActivity: BaseActivity? = null

    protected lateinit var generalLayout: LinearLayout
    protected lateinit var emergencyLayout: LinearLayout

    protected enum class ContactUsEnum(val phone: String) : Serializable {
        LocalFraudHotline("0860 557 557"),
        LocalLostStolenCard("0800 111 155"),
        LocalBankingAppSupportPhone("0860 1111 23"),
        BankingAppSupportEmail("bankingapp@absa.co.za"),
        LocalMainSwitchBoard("08600 08600"),
        InternationalFraudHotLine("+27 11 501 5089"),
        InternationalMainSwitchBoard("+27 11 501 5019")
    }

    override fun getTabDescription(): String = arguments?.getString(TAB_TITLE) ?: ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contactUsViewModel = requireActivity().viewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inflatedView = inflater.inflate(R.layout.contact_us_fragment, container, false)
        inflatedView.findViewById<View>(R.id.absaWebActionButtionView).setOnClickListener { navigateToAbsaTalkToUs() }
        generalLayout = inflatedView.findViewById(R.id.generalLayout)
        emergencyLayout = inflatedView.findViewById(R.id.emergencyLayout)

        contactUsView = activity as ContactUsContracts.ContactUsView
        baseActivity = activity as? BaseActivity
        return inflatedView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val atmBranchLocator = featureSwitchingToggles.branchLocator
        if (!BuildConfig.TOGGLE_DEF_BRANCH_LOCATOR_ENABLED || atmBranchLocator == FeatureSwitchingStates.GONE.key) {
            hideAtmAndBranchLocator()
        } else {
            view.findViewById<OptionActionButtonView>(R.id.branchLocatorActionButtonView).setOnClickListener {
                val isAtmBranchLocatorDisabled = atmBranchLocator == FeatureSwitchingStates.DISABLED.key
                if (isAtmBranchLocatorDisabled) {
                    startActivity(IntentFactory.capabilityUnavailable(baseActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_atm_branch_locator))))
                } else {
                    AnalyticsUtil.trackAction("ATM Branch Locator", "ATMAndBranchLocator_ContactUsScreen_ATMAndBranchLocatorClicked")
                    requestPermissions()
                    BaseActivity.preventDoubleClick(view.findViewById(R.id.branchLocatorActionButtonView))
                }
            }
        }
        view.findViewById<OptionActionButtonView>(R.id.absaWebActionButtionView).setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.contact_us_link)))) }
    }

    override fun onResume() {
        super.onResume()
        if (contactUsViewModel.justReturnedFromSettings) {
            contactUsViewModel.justReturnedFromSettings = false
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        if (activity != null && checkIfNetworkAndGPSIsEnabled()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                PermissionHelper.requestLocationAccessPermission(activity) {
                    startActivity(Intent(baseActivity, AtmBranchLocatorActivity::class.java))
                }
            } else {
                if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    showResultScreen()
                } else {
                    if ((activity as BaseActivity).currentFragment is GenericResultScreenFragment) {
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
                startActivity(Intent(baseActivity, AtmBranchLocatorActivity::class.java))
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showResultScreen() {
        GenericResultActivity.topOnClickListener = View.OnClickListener {
            contactUsViewModel.justReturnedFromSettings = true
            val uri = Uri.parse("package:" + baseActivity?.packageName)
            val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
            startActivity(settingsIntent)
            BMBApplication.getInstance().topMostActivity.finish()

        }

        GenericResultActivity.bottomOnClickListener = View.OnClickListener {
            BMBApplication.getInstance().topMostActivity.finish()
        }

        val genericResultActivity = Intent(context, GenericResultActivity::class.java).apply {
            putExtra(GenericResultActivity.IS_FAILURE, true)
            putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.location_access_denied_title)
            putExtra(GenericResultActivity.SUB_MESSAGE, R.string.location_access_denied_description)
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.ok)
            putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.branch_help_camera_open_settings)
        }

        baseActivity?.startActivity(genericResultActivity)
        baseActivity?.hideToolBar()
    }

    private fun checkIfNetworkAndGPSIsEnabled(): Boolean {
        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnabled = false

        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
        }

        try {
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

    protected fun setItem(container: LinearLayout, vararg contactUsEnums: ContactUsEnum) {
        for (contactUsEnum in contactUsEnums) {
            setItem(container, contactUsEnum)
        }
    }

    private fun setItem(container: LinearLayout, contactUsEnum: ContactUsEnum) {
        container.addView(ContactView(activity).apply {
            tag = contactUsEnum
            setContact(createContact(contactUsEnum))
            setOnClickListener(this@ContactUsFragmentBase)
            setIcon(if (contactUsEnum == ContactUsEnum.BankingAppSupportEmail) R.drawable.ic_email_new else R.drawable.ic_call_dark)
        })
    }

    private fun createContact(contactUsEnum: ContactUsEnum): Contact {
        val name = when (contactUsEnum) {
            ContactUsEnum.LocalFraudHotline,
            ContactUsEnum.InternationalFraudHotLine -> getString(R.string.fraud_hotline)
            ContactUsEnum.LocalLostStolenCard -> getString(R.string.lost_or_stolen_title)
            ContactUsEnum.LocalBankingAppSupportPhone -> getString(R.string.banking_app_support_title)
            ContactUsEnum.BankingAppSupportEmail -> getString(R.string.banking_app_support_title)
            ContactUsEnum.LocalMainSwitchBoard, ContactUsEnum.InternationalMainSwitchBoard -> getString(R.string.main_switch_board)
        }
        return Contact(name, contactUsEnum.phone)
    }

    override fun onClick(view: View) {
        (view.tag as? ContactUsEnum)?.let {
            baseActivity?.animate(view, R.anim.expand_horizontal)
            when (it) {
                ContactUsEnum.LocalFraudHotline,
                ContactUsEnum.LocalLostStolenCard,
                ContactUsEnum.LocalBankingAppSupportPhone,
                ContactUsEnum.LocalMainSwitchBoard,
                ContactUsEnum.InternationalFraudHotLine,
                ContactUsEnum.InternationalMainSwitchBoard -> contactUsView.call(it.phone)
                ContactUsEnum.BankingAppSupportEmail -> contactUsView.emailAppSupportOrGeneralEnquiry(getString(R.string.banking_app_support_email))
            }
        }
    }

    private fun navigateToAbsaTalkToUs() = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.contact_us_link))))

    fun hideAtmAndBranchLocator() {
        branchLocatorLayout.visibility = View.GONE
        branchLocatorActionButtonView.visibility = View.GONE
        divider3.visibility = View.GONE
    }
}