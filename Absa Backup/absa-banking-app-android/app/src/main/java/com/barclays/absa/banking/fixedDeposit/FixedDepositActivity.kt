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

package com.barclays.absa.banking.fixedDeposit

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.databinding.FixedDepositActivityBinding
import com.barclays.absa.banking.fixedDeposit.responseListeners.FixedDepositViewModel
import com.barclays.absa.banking.fixedDeposit.services.dto.InterestRateTable
import com.barclays.absa.banking.fixedDeposit.services.dto.TermDepositInterestRateDayTable
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskBasedApproachViewModel
import com.barclays.absa.utils.KeyboardUtils
import com.barclays.absa.utils.ViewAnimation
import com.barclays.absa.utils.viewModel
import styleguide.widgets.OfferBannerView.ANIMATION_DURATION

class FixedDepositActivity : BaseActivity() {
    private val binding by viewBinding(FixedDepositActivityBinding::inflate)

    private lateinit var fixedDepositViewModel: FixedDepositViewModel
    private lateinit var riskBasedViewModel: RiskBasedApproachViewModel
    private lateinit var navHostFragment: Fragment
    private lateinit var toolbarAnimation: ViewAnimation

    companion object {
        const val FIXED_DEPOSIT = "FixedDeposit"

        fun getMonthToDays(dayList: java.util.ArrayList<Int>, month: Int): Int {
            return when {
                dayList.isNullOrEmpty() -> 0
                month < dayList.size -> dayList[month]
                else -> dayList[dayList.size - 1]
            }
        }

        fun getMonthFromDays(interestRateTable: List<InterestRateTable>?, days: Int): Int {
            interestRateTable?.firstOrNull()?.termDepositAmountRangeMinInterestTable?.let {
                var position = 0
                it.forEach { dayTable ->
                    val daysFrom = dayTable.daysFrom?.toInt() ?: 0
                    val daysTo = dayTable.daysTo?.toInt() ?: 0

                    if (daysTo - daysFrom > 35) {
                        for (i in daysFrom..daysTo - 28 step 30) {
                            position++
                            when {
                                i >= days -> return position
                            }
                        }
                    } else {
                        position++
                    }
                    when {
                        days >= daysFrom && days <= daysTo -> return position - 1
                    }
                }
            }
            return 1
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fixedDepositViewModel = viewModel()
        riskBasedViewModel = viewModel()

        fixedDepositViewModel.failureResponse.observe(this, {
            dismissProgressDialog()
            if (isBusinessAccount) {
                startActivity(IntentFactory.getFailureResultScreen(this, R.string.fixed_deposit_business_account_error_title, R.string.fixed_deposit_business_account_error_message) {
                    navigateToHomeScreenWithoutReloadingAccounts()
                })
            } else {
                showGenericErrorMessageThenFinish()
            }
        })

        fixedDepositViewModel.ficaCheckResponse.observe(this, {
            if (it?.ficaAndCIFStatus != null && "Y".equals(it.ficaAndCIFStatus!!, ignoreCase = true)) {
                riskBasedViewModel.fetchCasaStatus()
            } else {
                showProfileUpdateScreen()
            }
        })

        riskBasedViewModel.failureResponse.observe(this, {
            dismissProgressDialog()
            if (it.transactionMessage != null && it.transactionMessage!!.contains("casa", ignoreCase = true)) {
                showProfileUpdateScreen()
            } else {
                showGenericErrorMessageThenFinish()
            }
        })

        riskBasedViewModel.casaStatusResponse.observe(this, {
            if (it?.casaApproved != null && it.casaApproved) {
                fixedDepositViewModel.fetchInterestRateInfo()
            } else {
                showProfileUpdateScreen()
            }
        })

        fixedDepositViewModel.interestRateInfo.observe(this, {
            setUpDateTable()

            if (!absaCacheService.isPersonalClientAgreementAccepted()) {
                fixedDepositViewModel.fetchPersonalClientAgreementDetails()

                fixedDepositViewModel.successResponse = MutableLiveData()
                fixedDepositViewModel.successResponse.observe(this, {
                    dismissProgressDialog()
                })
            } else {
                dismissProgressDialog()
            }
        })

        fixedDepositViewModel.performFicaCheck()

        setUpToolbar()
        navHostFragment = supportFragmentManager.findFragmentById(R.id.fixedDepositNavHostFragment)!!
    }

    private fun setUpDateTable() {
        fixedDepositViewModel.dayTableList = ArrayList()

        if (fixedDepositViewModel.interestRateInfo.value?.interestRateTable != null && fixedDepositViewModel.interestRateInfo.value?.interestRateTable?.isNotEmpty()!!) {
            for (dayTable: TermDepositInterestRateDayTable? in fixedDepositViewModel.interestRateInfo.value?.interestRateTable!![0].termDepositAmountRangeMinInterestTable!!) {
                val daysFrom = dayTable?.daysFrom?.toInt()
                val daysTo = dayTable?.daysTo?.toInt()

                if (daysFrom != null && daysTo != null && daysTo - daysFrom > 35) {
                    for (i in daysFrom..daysTo - 28 step 30) {
                        fixedDepositViewModel.dayTableList.add(i)
                    }
                } else {
                    fixedDepositViewModel.dayTableList.add(daysFrom!!)
                }
            }
        }
    }

    private fun showProfileUpdateScreen() {
        dismissProgressDialog()

        if (CustomerProfileObject.instance.customerType == "E") {
            startActivity(IntentFactory.getAlertResultScreenPrimaryButton(this, R.string.unable_to_continue, R.string.fixed_deposit_profile_needs_to_be_updated_content_private))
        } else {
            startActivity(IntentFactory.getAlertResultScreenPrimaryButton(this, R.string.fixed_deposit_profile_needs_to_be_updated_title, R.string.fixed_deposit_profile_needs_to_be_updated_content))
        }
    }

    private fun setUpToolbar() {
        binding.toolbar.toolbar.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                toolbarAnimation = ViewAnimation(binding.toolbar.toolbar)
                toolbarAnimation.collapseView(0)
                binding.toolbar.toolbar.viewTreeObserver?.removeOnGlobalLayoutListener(this)
            }
        })

        setSupportActionBar(binding.toolbar as Toolbar?)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        KeyboardUtils.hideKeyboard(this)
        val navController = findNavController(R.id.fixedDepositNavHostFragment)
        if (navController.currentDestination?.id == R.id.fixedDepositOpenAccountFragment) {
            hideToolbar()
        }
    }

    fun showToolbarBackArrow() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun hideToolBackArrow() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    fun hideToolbar() {
        if (binding.toolbar.toolbar.visibility != View.GONE) {
            toolbarAnimation.collapseView(ANIMATION_DURATION)
        }
    }

    fun showToolbar() {
        if (binding.toolbar.toolbar.visibility != View.VISIBLE) {
            toolbarAnimation.expandView(ANIMATION_DURATION)
        }
        showToolbarBackArrow()
    }

    fun setToolbarTitle(title: String) {
        setToolBarBack(title) { onBackPressed() }
    }
}