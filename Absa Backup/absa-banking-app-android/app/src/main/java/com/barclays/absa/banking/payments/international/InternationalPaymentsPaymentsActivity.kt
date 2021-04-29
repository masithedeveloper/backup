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

package com.barclays.absa.banking.payments.international

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.INTERNATIONAL_PAYMENTS
import com.barclays.absa.banking.payments.international.data.BeneficiaryEnteredDetails
import com.barclays.absa.banking.payments.international.services.IInternationalPaymentCacheService
import com.barclays.absa.banking.payments.international.services.dto.WesternUnionBeneficiaryDetails
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.ProfileManager
import kotlinx.android.synthetic.main.international_payments_activity.*

class InternationalPaymentsPaymentsActivity : BaseActivity(R.layout.international_payments_activity) {

    private val internationalPaymentCacheService: IInternationalPaymentCacheService = getServiceInterface()
    var westernUnionBeneficiaryDetails: WesternUnionBeneficiaryDetails? = null
    lateinit var flowTypeString: String
    lateinit var flowHintTypeString: String
    var isExistingBeneficiary: Boolean = false
    var isOnceOffPayment: Boolean = false
    var calculateRetryCount = 0
    var hasSecurityQuestion = false
    var showOnceOffDialog = true
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_HubScreen_ScreenDisplayed")

        navController = Navigation.findNavController(this, R.id.international_nav_host_fragment)
        if (isFirstTimeInternationalPayments()) {
            navController.navigate(R.id.internationalPaymentsFirstTimeFragment)
            setFirstTimeInternationalPayments(false)
        }
        flowTypeString = getString(R.string.international_payments_beneficiary)
        flowHintTypeString = getString(R.string.international_payments_beneficiaries)
    }

    override fun onBackPressed() {
        when (navController.currentDestination?.id) {
            R.id.internationalPaymentsFirstTimeFragment,
            R.id.internationalPaymentHubFragment -> finish()
            R.id.internationalPaymentsConfirmPaymentFragment -> {
                BaseAlertDialog.showYesNoDialog(AlertDialogProperties.Builder()
                        .message(getString(R.string.international_are_you_sure_you_want_to_cancel))
                        .positiveDismissListener { _, _ ->
                            AnalyticsUtils.getInstance().trackCancelButton(mScreenName, mSiteSection)
                            BaseAlertDialog.dismissAlertDialog()
                            finish()
                        })
                return
            }
            R.id.internationalPaymentsCalculateFragment -> {
                BaseAlertDialog.showYesNoDialog(AlertDialogProperties.Builder()
                        .message(getString(R.string.international_are_you_sure_you_want_to_cancel))
                        .positiveDismissListener { _, _ ->
                            AnalyticsUtils.getInstance().trackCancelButton(mScreenName, mSiteSection)
                            BaseAlertDialog.dismissAlertDialog()
                            finish()
                        })
                return
            }
            R.id.genericResultScreenFragment -> finish()
            else -> super.onBackPressed()
        }
    }

    fun setProgressStep(step: Int) = progressView.setNextStep(step)

    fun showProgressIndicator() {
        progressView.visibility = View.VISIBLE
    }

    fun hideProgressIndicator() {
        progressView.visibility = View.GONE
    }

    fun updateBeneficiaryBeneficiaryDetailsDataModel(beneficiaryDetails: BeneficiaryEnteredDetails) = internationalPaymentCacheService.setEnteredBeneficiaryDetails(beneficiaryDetails)

    fun fetchBeneficiaryBeneficiaryDetailsDataModel(): BeneficiaryEnteredDetails = internationalPaymentCacheService.getEnteredBeneficiaryDetails()

    private fun isFirstTimeInternationalPayments(): Boolean {
        val userProfileKey = buildProfileIdAndKey()
        val sharedPreferences = getSharedPreferences(userProfileKey, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(userProfileKey, true)
    }

    private fun setFirstTimeInternationalPayments(isFirstTime: Boolean) {
        val userProfileKey = buildProfileIdAndKey()
        val sharedPreferences = getSharedPreferences(userProfileKey, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(userProfileKey, isFirstTime)
        editor.apply()
    }

    private fun buildProfileIdAndKey(): String =
            (ProfileManager.getInstance().activeUserProfile?.userId ?: "") + "FIRST_TIME_INTERNATIONAL_PAYMENTS"

    fun clearCachedData() {
        westernUnionBeneficiaryDetails = WesternUnionBeneficiaryDetails()
    }
}