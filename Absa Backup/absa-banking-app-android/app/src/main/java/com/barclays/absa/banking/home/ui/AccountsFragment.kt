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
package com.barclays.absa.banking.home.ui

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccessPrivileges.Companion.instance
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.buy.ui.airtime.BuyPrepaidActivity
import com.barclays.absa.banking.buy.ui.electricity.PrepaidElectricityActivity
import com.barclays.absa.banking.cashSendPlus.services.CashSendPlusResponseData
import com.barclays.absa.banking.cashSendPlus.services.CheckCashSendPlusRegistrationStatusResponse
import com.barclays.absa.banking.cashSendPlus.ui.CashSendPlusUtils.isNotFound
import com.barclays.absa.banking.cashSendPlus.ui.CashSendPlusViewModel
import com.barclays.absa.banking.covid.CovidAlertActivity
import com.barclays.absa.banking.debiCheck.ui.DebiCheckActivity
import com.barclays.absa.banking.debiCheck.ui.DebiCheckWhatsNewActivity
import com.barclays.absa.banking.dualAuthorisations.ui.AuthorisationHubActivity
import com.barclays.absa.banking.express.data.ClientTypeGroup
import com.barclays.absa.banking.expressCashSend.ui.CashSendActivity
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitching
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.framework.utils.AppConstants
import com.barclays.absa.banking.home.AccountAdapter
import com.barclays.absa.banking.lotto.services.dto.LottoGameRules
import com.barclays.absa.banking.lotto.ui.LottoActivity
import com.barclays.absa.banking.lotto.ui.LottoUtils.isEligibleToPlayLotto
import com.barclays.absa.banking.lotto.ui.LottoViewModel
import com.barclays.absa.banking.manage.profile.ui.ManageProfileActivity
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants
import com.barclays.absa.banking.paymentsRewrite.ui.PaymentsActivity
import com.barclays.absa.banking.personalLoan.services.CreditLimitsResponse
import com.barclays.absa.banking.personalLoan.ui.PersonalLoanApplyActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog.showAlertDialog
import com.barclays.absa.banking.shared.BaseAlertDialog.showRequestAccessAlertDialog
import com.barclays.absa.banking.solidarityFund.SolidarityFundActivity
import com.barclays.absa.banking.transfer.TransferFundsActivity
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.ScanToPayActivity
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.ScanToPayViewModel
import com.barclays.absa.utils.*
import com.barclays.absa.utils.AnalyticsUtil.trackAction
import com.barclays.absa.utils.imageHelpers.ProfileViewImageHelper
import kotlinx.android.synthetic.main.accounts_fragment.*
import styleguide.cards.Alert
import styleguide.utils.AnimationHelper
import java.util.*

open class AccountsFragment : BaseFragment(R.layout.accounts_fragment) {
    private lateinit var homeContainerActivity: HomeContainerActivity
    private lateinit var profileImageHelper: ProfileViewImageHelper
    private lateinit var cashSendPlusViewModel: CashSendPlusViewModel
    private lateinit var verticalPagerAdapter: AccountAdapter
    private val homeCacheService: IHomeCacheService = getServiceInterface()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeContainerActivity = context as HomeContainerActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFloatingActionButtonsClickListeners()
        profileImageHelper = ProfileViewImageHelper(homeContainerActivity)
        if (!homeContainerActivity.isBusinessAccount && !instance.isOperator && BuildConfig.TOGGLE_DEF_MANAGE_PROFILE_ENABLED) {
            profileView.setSecondaryImage(R.drawable.ic_more_alt_dark)
            profileView.setSecondaryImageVisible()
        }

        setupFeatureSwitchingVisibilityToggles()
        val clientTypeGroup = CustomerProfileObject.instance.clientTypeGroup

        if (!homeContainerActivity.isBusinessAccount && BannerManager.shouldShowSolidarityBanner()) {
            showSolidarityFundBanner()
        } else if (featureSwitchingToggles.covidBanner == FeatureSwitchingStates.ACTIVE.key && BannerManager.shouldShowCovidBanner()) {
            showCovidBanner()
        } else if (BuildConfig.TOGGLE_DEF_PERSONAL_LOANS_ENABLED && featureSwitchingToggles.personalLoan == FeatureSwitchingStates.ACTIVE.key && !homeContainerActivity.isBusinessAccount && !instance.isOperator && (clientTypeGroup == ClientTypeGroup.INDIVIDUAL_CLIENT.value || clientTypeGroup == ClientTypeGroup.SOLE_TRADER_CLIENT.value)) {
            val personalLoanCreditLimitsResponse = homeCacheService.getPersonalLoanResponse()
            if (personalLoanCreditLimitsResponse != null && personalLoanCreditLimitsResponse.creditLimits.isNotEmpty() && BannerManager.shouldShowPersonalLoanBanner()) {
                setPersonalLoanVclBannerVisibility(personalLoanCreditLimitsResponse)
            }
        }

        if (!BuildConfig.TOGGLE_DEF_CASH_SEND_PLUS_ENABLED || featureSwitchingToggles.businessBankingCashSendPlus != FeatureSwitchingStates.ACTIVE.key || !isBusinessMainUser()) {
            cashSendPlusFloatingActionButtonView.visibility = View.GONE
            cashSendFloatingActionButtonView.visibility = View.VISIBLE
        } else {
            cashSendPlusViewModel = ViewModelProvider(homeContainerActivity).get(CashSendPlusViewModel::class.java)
        }

        profileView.setOnClickListener {
            if (!homeContainerActivity.isBusinessAccount && !instance.isOperator && BuildConfig.TOGGLE_DEF_MANAGE_PROFILE_ENABLED) {
                if (featureSwitchingToggles.manageProfile == FeatureSwitchingStates.DISABLED.key) {
                    startActivity(IntentFactory.capabilityUnavailable(homeContainerActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_manage_profile))))
                } else if (featureSwitchingToggles.manageProfile == FeatureSwitchingStates.ACTIVE.key) {
                    trackAction(ManageProfileConstants.ANALYTICS_TAG, "ManageProfile_HomeScreen_ManageProfileButtonClicked")
                    startActivity(Intent(homeContainerActivity, ManageProfileActivity::class.java))
                }
            }
        }

        verticalPagerAdapter = AccountAdapter(homeContainerActivity)
        verticalRecyclerViews.adapter = verticalPagerAdapter
        verticalRecyclerViews?.let {
            it.visibility = View.VISIBLE
            it.animate().scaleY(1f).alpha(1f).setDuration(250).setListener(null).start()
        }
    }

    private fun setProfileImage() {
        profileImageHelper.profileView(profileView)
    }

    private fun isBusinessMainUser(): Boolean = homeContainerActivity.isBusinessAccount && OperatorPermissionUtils.isMainUser() && instance.cashSendAllowed

    override fun onResume() {
        super.onResume()
        setDebiCheckFloatingActionButton()
        setProfileImage()
        updateCashSendPlusAvailability()
    }

    private fun setupFeatureSwitchingVisibilityToggles() {
        val featureSwitchingToggles = featureSwitchingToggles
        if (featureSwitchingToggles.prepaidAirtime == FeatureSwitchingStates.GONE.key) {
            buyAirtimeFloatingActionButtonView.visibility = View.GONE
        }
        if (featureSwitchingToggles.prepaidElectricity == FeatureSwitchingStates.GONE.key) {
            buyElectricityFloatingActionButtonView.visibility = View.GONE
        }
        if (featureSwitchingToggles.fixedDepositApplication == FeatureSwitchingStates.GONE.key) {
            bannerAlertView.visibility = View.GONE
        }
        if (featureSwitchingToggles.debiCheck == FeatureSwitchingStates.GONE.key) {
            debitOrdersFloatingActionButtonView.visibility = View.GONE
        }

        if (ScanToPayViewModel.scanToPayGone()) {
            qrScanToPayButtonView.visibility = View.GONE
        }
        if (ScanToPayViewModel.scanToPayDisabled()) {
            qrScanToPayButtonView.isEnabled = false
        }
    }

    private fun setFloatingActionButtonsClickListeners() {
        debitOrdersFloatingActionButtonView.setOnClickListener { navigateToWhatsNewOrDebicheckScreen(featureSwitchingToggles) }
        buyElectricityFloatingActionButtonView.isEnabled = !instance.isOperator
        authorizationsFloatingActionButtonView.setOnClickListener {
            startActivity(Intent(homeContainerActivity, AuthorisationHubActivity::class.java))
        }
        payFloatingActionButtonView.setOnClickListener {
            if (instance.isOperator && !instance.beneficiaryPaymentAllowed) {
                showRequestAccessAlertDialog(getString(R.string.payment))
            } else {
                startActivity(Intent(homeContainerActivity, PaymentsActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false)
                    putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_PAYMENT)
                })
            }
        }

        qrScanToPayButtonView.setOnClickListener {
            trackAction(ScanToPayViewModel.FEATURE_NAME, "ScanToPay_HomeScreen_QRPaymentsClicked")
            if (ScanToPayViewModel.scanToPayDisabled()) {
                startActivity(IntentFactory.featureUnavailable(homeContainerActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.scan_to_pay))))
            } else {
                val intent = Intent(activity, ScanToPayActivity::class.java)
                startActivity(intent)
            }
        }

        transferFloatingActionButtonView.setOnClickListener {
            if (!transferFloatingActionButtonView.isEnabled) {
                showAlertDialog(AlertDialogProperties.Builder()
                        .message(getString(R.string.transfer_unavailable))
                        .build())
                return@setOnClickListener
            }
            if (instance.isOperator && !instance.interAccountTransferAllowed) {
                showRequestAccessAlertDialog(getString(R.string.transfer))
            } else {
                startActivity(Intent(homeContainerActivity, TransferFundsActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                })
            }
        }
        cashSendFloatingActionButtonView.setOnClickListener {
            if (!cashSendFloatingActionButtonView.isEnabled) {
                showAlertDialog(AlertDialogProperties.Builder()
                        .message(getString(R.string.you_need_a_transactional_account_warning))
                        .build())
                return@setOnClickListener
            }
            if (instance.isOperator && !instance.cashSendAllowed) {
                showRequestAccessAlertDialog(getString(R.string.cashsend))
            } else {
                startActivity(Intent(homeContainerActivity, CashSendActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false)
                    putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_CASHSEND)
                })
            }
        }
        buyAirtimeFloatingActionButtonView.setOnClickListener {
            if (!buyAirtimeFloatingActionButtonView.isEnabled) {
                showAlertDialog(AlertDialogProperties.Builder()
                        .message(getString(R.string.you_need_a_transactional_account_warning))
                        .build())
                return@setOnClickListener
            }
            if (featureSwitchingToggles.prepaidAirtime == FeatureSwitchingStates.DISABLED.key) {
                startActivity(IntentFactory.capabilityUnavailable(homeContainerActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_prepaid_airtime))))
            } else if (instance.isOperator && !instance.isPrepaidAllowed) {
                showRequestAccessAlertDialog(getString(R.string.prepaid))
            } else {
                startActivity(Intent(homeContainerActivity, BuyPrepaidActivity::class.java).apply {
                    putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_AIRTIME)
                })
            }
        }
        buyElectricityFloatingActionButtonView.setOnClickListener {
            if (!buyElectricityFloatingActionButtonView.isEnabled) {
                showAlertDialog(AlertDialogProperties.Builder()
                        .message(getString(R.string.you_need_a_transactional_account_warning))
                        .build())
                return@setOnClickListener
            }
            if (featureSwitchingToggles.prepaidElectricity == FeatureSwitchingStates.DISABLED.key) {
                startActivity(IntentFactory.capabilityUnavailable(homeContainerActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_prepaid_electricity))))
            } else if (instance.isOperator && !instance.isPrepaidElectricityAllowed) {
                showAlertDialog(AlertDialogProperties.Builder()
                        .message(getString(R.string.limited_access_message))
                        .build())
            } else {
                startActivity(Intent(homeContainerActivity, PrepaidElectricityActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false)
                    putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_PREPAID_ELECTRICITY)
                })
            }
        }

        checkLottoAvailable()
        disableTransferButton()
        setAuthorizationsButton()
        disableOptionsForStandaloneCreditCardUsers()
    }

    private fun navigateToWhatsNewOrDebicheckScreen(featureSwitchingToggles: FeatureSwitching) {
        if (featureSwitchingToggles.debiCheck == FeatureSwitchingStates.DISABLED.key) {
            startActivity(IntentFactory.capabilityUnavailable(homeContainerActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_debit_orders))))
        } else if (instance.isOperator) {
            showAlertDialog(AlertDialogProperties.Builder()
                    .message(getString(R.string.limited_access_message))
                    .build())
        } else {
            trackAction("Debit Orders", "DebitOrders_HomeScreen_DebitOrderFloatingActionButtonClicked")
            if (SharedPreferenceService.getDebiCheckFirstVisitDone()) {
                startActivity(Intent(homeContainerActivity, DebiCheckActivity::class.java))
            } else {
                startActivity(Intent(homeContainerActivity, DebiCheckWhatsNewActivity::class.java))
            }
        }
    }

    private fun checkLottoAvailable() {
        if (BuildConfig.TOGGLE_DEF_LOTTO_ENABLED && isEligibleToPlayLotto()) {
            val featureSwitchingToggles = featureSwitchingToggles
            val lottoState = featureSwitchingToggles.lottoAndPowerball
            val isLottoDisabled = lottoState == FeatureSwitchingStates.DISABLED.key
            val isLottoGone = lottoState == FeatureSwitchingStates.DISABLED.key
            lottoActionButtonView.visibility = if (isLottoGone) View.GONE else View.VISIBLE
            if (isLottoDisabled) {
                startActivity(IntentFactory.capabilityUnavailable(homeContainerActivity, R.string.feature_unavailable, getString(R.string.lotto_not_available_header, getString(R.string.lotto_not_available_content))))
            } else {
                lottoActionButtonView.setOnClickListener {
                    val viewModel = ViewModelProvider(this).get(LottoViewModel::class.java)
                    trackAction("Lotto", "Lotto_HomeScreen_DashboardLottoFloatingActionButtonClicked")
                    viewModel.lottoGameRulesListLiveData = MutableLiveData()
                    viewModel.lottoGameRulesListLiveData.observe(viewLifecycleOwner, { lottoGameRulesList: List<LottoGameRules?>? ->
                        viewModel.lottoGameRulesListLiveData.removeObservers(this)
                        homeContainerActivity.dismissProgressIndicator()
                        startActivity(Intent(homeContainerActivity, LottoActivity::class.java).apply {
                            putParcelableArrayListExtra(LottoActivity.LOTTO_GAME_RULES_EXTRA, lottoGameRulesList as ArrayList<out Parcelable?>?)
                        })
                    })
                    viewModel.failureResponse = MutableLiveData()
                    viewModel.failureResponse.observe(viewLifecycleOwner, {
                        viewModel.lottoGameRulesListLiveData.removeObservers(this)
                        homeContainerActivity.dismissProgressIndicator()
                        homeContainerActivity.showGenericErrorMessage()
                    })
                    viewModel.fetchGameRules()
                }
            }
        } else {
            lottoActionButtonView.visibility = View.GONE
        }
    }

    private fun disableTransferButton() {
        transferFloatingActionButtonView.isEnabled = homeContainerActivity.numberOfNoneWimiAccounts() > 1
    }

    private fun updateCashSendPlusAvailability() {
        homeContainerActivity.dismissProgressIndicator()
        if (BuildConfig.TOGGLE_DEF_CASH_SEND_PLUS_ENABLED && featureSwitchingToggles.businessBankingCashSendPlus == FeatureSwitchingStates.ACTIVE.key && isBusinessMainUser()) {
            checkCashSendPlusIsAvailable()
        }
    }

    private fun disableOptionsForStandaloneCreditCardUsers() {
        buyAirtimeFloatingActionButtonView.isEnabled = !homeContainerActivity.isStandaloneCreditCardAccount
        buyElectricityFloatingActionButtonView.isEnabled = !homeContainerActivity.isStandaloneCreditCardAccount
        cashSendFloatingActionButtonView.isEnabled = !homeContainerActivity.isStandaloneCreditCardAccount
        cashSendPlusFloatingActionButtonView.isEnabled = !homeContainerActivity.isStandaloneCreditCardAccount
    }

    private fun setAuthorizationsButton() {
        authorizationsFloatingActionButtonView.visibility = if (homeCacheService.hasAuthorizations()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun setDebiCheckFloatingActionButton() {
        val numberOfPendingMandates = homeCacheService.getNumberOfPendingMandates()
        if (numberOfPendingMandates > 0) {
            debitOrdersFloatingActionButtonView.visibility = View.VISIBLE
            debitOrdersFloatingActionButtonView.setTitleText("$numberOfPendingMandates ${if (numberOfPendingMandates == 1) getString(R.string.debicheck_debit_order_count) else getString(R.string.debicheck_debit_orders_count)}")
            floatingActionButtonLinearLayout.removeView(debitOrdersFloatingActionButtonView)
            floatingActionButtonLinearLayout.addView(debitOrdersFloatingActionButtonView, 0)
            if (homeContainerActivity.isBusinessAccount && !OperatorPermissionUtils.isMainUser()) {
                debitOrdersFloatingActionButtonView.visibility = View.GONE
            }
        } else {
            debitOrdersFloatingActionButtonView.visibility = View.GONE
        }
    }

    private fun checkCashSendPlusIsAvailable() {
        val cashSendPlusRegistrationStatus = appCacheService.getCashSendPlusRegistrationStatus()
        if (cashSendPlusRegistrationStatus == null) {
            cashSendPlusViewModel.sendCheckCashSendPlusRegistration()
            cashSendPlusViewModel.checkCashSendPlusRegistrationStatusResponse.observe(homeContainerActivity, { registrationStatusResponse: CheckCashSendPlusRegistrationStatusResponse ->
                homeContainerActivity.dismissProgressIndicator()
                appCacheService.setCashSendPlusRegistrationStatus(registrationStatusResponse)
                cashSendPlusViewModel.checkCashSendPlusRegistrationStatusResponse.removeObservers(homeContainerActivity)
                setUpCashSendPlus(registrationStatusResponse.cashSendPlusResponseData)
            })
            cashSendPlusViewModel.checkCashSendPlusRegistrationStatusFailedResponse.observe(viewLifecycleOwner, {
                homeContainerActivity.dismissProgressIndicator()
                cashSendPlusFloatingActionButtonView?.visibility = View.GONE
                cashSendFloatingActionButtonView?.visibility = View.VISIBLE
                cashSendPlusViewModel.checkCashSendPlusRegistrationStatusFailedResponse.removeObservers(homeContainerActivity)
            })
        } else {
            setUpCashSendPlus(cashSendPlusRegistrationStatus.cashSendPlusResponseData)
        }
        cashSendPlusFloatingActionButtonView?.setOnClickListener {
            if (!cashSendPlusFloatingActionButtonView.isEnabled) {
                showAlertDialog(AlertDialogProperties.Builder()
                        .message(getString(R.string.you_need_a_transactional_account_warning))
                        .build())
                return@setOnClickListener
            }
            startActivity(Intent(homeContainerActivity, CashSendActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(CashSendActivity.IS_CASH_SEND_PLUS, true)
                putExtra(AppConstants.SUB_ACTIVITY_INDICATOR, false)
                putExtra(BMBConstants.PASS_BENEFICAIRY_TYPE, BMBConstants.PASS_CASHSEND)
            })
        }
    }

    private fun setUpCashSendPlus(statusResponse: CashSendPlusResponseData) {
        if (isNotFound(statusResponse)) {
            cashSendPlusFloatingActionButtonView?.visibility = View.GONE
            cashSendFloatingActionButtonView?.visibility = View.VISIBLE
        } else {
            cashSendPlusFloatingActionButtonView?.isEnabled = true
            cashSendPlusFloatingActionButtonView?.visibility = View.VISIBLE
            cashSendFloatingActionButtonView?.visibility = View.GONE
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setPersonalLoanVclBannerVisibility(creditLimitsResponse: CreditLimitsResponse) {
        val maximumCreditLimitIndex = creditLimitsResponse.creditLimits.size - 1
        val maximumCreditLimit = creditLimitsResponse.creditLimits[maximumCreditLimitIndex].amount
        val alert = Alert(getString(R.string.personal_loan_title), getString(R.string.personal_loan_preapproved_amount_description, TextFormatUtils.formatBasicAmountAsRand(maximumCreditLimit)))
        with(bannerAlertView) {
            setImage(R.drawable.personal_loan_vcl_banner_background)
            setAlert(alert)
            hideRightImage()
            visibility = View.VISIBLE
            setOnTouchListener { _: View, event: MotionEvent ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> AnimationHelper.scaleInTouchAnimation(this)
                    MotionEvent.ACTION_UP -> {
                        trackAction("Personal Loans", "PersonalLoans_HomeScreen_BannerClicked")
                        BannerManager.hidePersonalLoanBanner()
                        startActivity(Intent(homeContainerActivity, PersonalLoanApplyActivity::class.java).apply {
                            putExtra(PersonalLoanApplyActivity.IS_FROM_BANNER, true)
                            putParcelableArrayListExtra(PersonalLoanApplyActivity.MAXIMUM_CREDIT_LIMIT_LIST, creditLimitsResponse.creditLimits as ArrayList<out Parcelable?>)
                        })
                        animate().scaleX(0f).scaleY(0f).alpha(0f).setDuration(300).setListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator?) {}

                            override fun onAnimationEnd(animation: Animator?) {
                                this@with.visibility = View.GONE
                            }

                            override fun onAnimationCancel(animation: Animator?) {}

                            override fun onAnimationRepeat(animation: Animator?) {}
                        }).start()
                    }
                    MotionEvent.ACTION_CANCEL -> AnimationHelper.scaleOutTouchAnimation(this)
                }
                true
            }
        }
    }

    fun updateAccountCards() {
        verticalPagerAdapter.notifyDataSetChanged()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showCovidBanner() {
        val alert = Alert(getString(R.string.covid_banner_heading), getString(R.string.covid_banner_message))
        with(bannerAlertView) {
            setImage(R.drawable.ic_covid_banner)
            setAlert(alert)
            hideRightImage()
            visibility = View.VISIBLE
            setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> AnimationHelper.scaleInTouchAnimation(this)
                    MotionEvent.ACTION_UP -> {
                        trackAction("Covid Alert", "COVIDAlertSA_HomeScreenBanner_Clicked")
                        BannerManager.hideCovidBanner()
                        startActivity(Intent(homeContainerActivity, CovidAlertActivity::class.java))
                        animate().scaleX(0f).scaleY(0f).alpha(0f).setDuration(300).setListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator?) {}

                            override fun onAnimationEnd(animation: Animator?) {
                                this@with.visibility = View.GONE
                            }

                            override fun onAnimationCancel(animation: Animator?) {}

                            override fun onAnimationRepeat(animation: Animator?) {}
                        }).start()
                    }
                    MotionEvent.ACTION_CANCEL -> AnimationHelper.scaleOutTouchAnimation(this)
                }
                true
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showSolidarityFundBanner() {
        val alert = Alert(getString(R.string.solidarity_fund_banner_heading), getString(R.string.solidarity_fund_banner_message))
        with(bannerAlertView) {
            setImage(R.drawable.ic_solidarity_banner)
            setAlert(alert)
            hideRightImage()
            visibility = View.VISIBLE
            setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> AnimationHelper.scaleInTouchAnimation(this)
                    MotionEvent.ACTION_UP -> {
                        trackAction("Solidarity Alert", "SolidarityAlert_HomeScreenBanner_Clicked")
                        BannerManager.hideSolidarityBanner()
                        startActivity(Intent(homeContainerActivity, SolidarityFundActivity::class.java))
                        animate().scaleX(0f).scaleY(0f).alpha(0f).setDuration(300).setListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator?) {}

                            override fun onAnimationEnd(animation: Animator?) {
                                this@with.visibility = View.GONE
                            }

                            override fun onAnimationCancel(animation: Animator?) {}

                            override fun onAnimationRepeat(animation: Animator?) {}
                        }).start()
                    }
                    MotionEvent.ACTION_CANCEL -> AnimationHelper.scaleOutTouchAnimation(this)
                }
                true
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): AccountsFragment = AccountsFragment()
    }
}