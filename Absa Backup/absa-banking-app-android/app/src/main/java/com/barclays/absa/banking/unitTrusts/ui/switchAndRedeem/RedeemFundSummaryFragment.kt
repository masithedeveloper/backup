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

package com.barclays.absa.banking.unitTrusts.ui.switchAndRedeem

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.debiCheck.ui.DebiCheckContracts
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.banking.unitTrusts.ui.view.ViewUnitTrustViewModel
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.sell_funds_summary_fragment.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toFormattedAmountZeroDefault
import styleguide.utils.extensions.toSentenceCase
import styleguide.utils.extensions.toTitleCase

class RedeemFundSummaryFragment : BaseFragment(R.layout.sell_funds_summary_fragment) {

    private lateinit var viewUnitTrustViewModel: ViewUnitTrustViewModel
    private lateinit var sureCheckDelegate: SureCheckDelegate
    private lateinit var hostActivity: RedeemFundHostActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as RedeemFundHostActivity
        viewUnitTrustViewModel = hostActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.summary)
        setUpUI()
        setUpProgressIndicator()
        initialiseSureCheckDelegate()
        setupObservers()
        hostActivity.trackEvent("UTRedeemFund_SummaryScreen_ScreenDisplayed")
    }

    private fun setUpUI() {
        val redemptionTypeNumberOfUnits = getString(R.string.switch_redemption_value_number_of_units)
        val redemptionTypeRandValue = getString(R.string.switch_redemption_type_rand_value)
        val redemptionTypeAllUnits = getString(R.string.switch_redemption_type_all_units)

        val redemptionAccountDetail = viewUnitTrustViewModel.unitTrustRedemptionAccountData.value?.redemptionAccountDetail
        val unitTrustFund = viewUnitTrustViewModel.unitTrustRedemptionAccountData.value?.unitTrustFund
        val cancelDebitOrder = if (viewUnitTrustViewModel.unitTrustRedemptionAccountData.value?.cancelDebitOrder.equals("Yes", ignoreCase = true)) getString(R.string.yes) else getString(R.string.no)
        val redemptionType = viewUnitTrustViewModel.unitTrustRedemptionAccountData.value?.redemptionType
        val redeemUnits = viewUnitTrustViewModel.unitTrustRedemptionAccountData.value?.fundUnits
        val redeemAmount = viewUnitTrustViewModel.unitTrustRedemptionAccountData.value?.redeemAmount

        selectedFundTitleAndDescriptionView.title = unitTrustFund?.fundName.toTitleCase()
        debitOrderAccountContentAndLabelView.setContentText(redemptionAccountDetail?.redemptionAccountType.toTitleCase() + " - " + redemptionAccountDetail?.redemptionAccountNumber)
        accountHolderContentAndLabelView.setContentText(redemptionAccountDetail?.redemptionAccountHolder.toTitleCase())
        accountNumberContentAndLabelView.setContentText(redemptionAccountDetail?.redemptionAccountBankName + " - " + redemptionAccountDetail?.redemptionAccountBankCode)
        cancelDebitOrderContentAndLabelView.setContentText(cancelDebitOrder)

        confirmButton.setOnClickListener {
            viewUnitTrustViewModel.redeemFund()
        }

        when {
            redeemUnits != "0.00" -> {
                valueContentAndLabelView.setContentText(redeemUnits.toFormattedAmountZeroDefault())
                valueContentAndLabelView.visibility = View.VISIBLE
            }
            else -> {
                valueContentAndLabelView.setContentText(redeemAmount.toFormattedAmountZeroDefault())
                valueContentAndLabelView.visibility = View.VISIBLE
            }
        }

        when (redemptionType) {
            "V" -> redemptionTypeContentAndLabelView.setContentText(redemptionTypeRandValue)

            "U" -> redemptionTypeContentAndLabelView.setContentText(redemptionTypeNumberOfUnits)

            "A" -> redemptionTypeContentAndLabelView.setContentText(redemptionTypeAllUnits)
        }
    }

    private fun initialiseSureCheckDelegate() {
        sureCheckDelegate = object : SureCheckDelegate(hostActivity) {
            override fun onSureCheckProcessed() {
                Handler(Looper.getMainLooper()).postDelayed({
                    viewUnitTrustViewModel.redeemFund()
                }, DebiCheckContracts.DEFAULT_DELAY.toLong())
            }

            override fun onSureCheckFailed() {
                super.onSureCheckFailed()
                dismissProgressDialog()
            }
        }
    }

    private fun setupObservers() {
        viewUnitTrustViewModel.redeemFundResponseLiveData.observe(this, {
            if (it?.transactionStatus.equals(BMBApplication.CONST_SUCCESS, true)) {
                sureCheckDelegate.processSureCheck(hostActivity, it) {
                    navigateToSuccessResultScreen(viewUnitTrustViewModel.unitTrustRedemptionAccountData.value?.unitTrustFund?.fundName)
                    viewUnitTrustViewModel.redeemFundResponseLiveData.removeObservers(this)
                }
            } else {
                navigateToFailureResultScreen()
                viewUnitTrustViewModel.redeemFundResponseLiveData.removeObservers(this)
            }
            dismissProgressDialog()
        })
    }

    private fun setUpProgressIndicator() {
        hostActivity.showProgressIndicator()
        hostActivity.progressIndicatorStep(2)
    }

    private fun navigateToSuccessResultScreen(fundName: String?) {
        hostActivity.trackEvent("UTRedeemFund_SuccessScreen_ScreenDisplayed")
        hideToolBar()
        hostActivity.hideProgressIndicator()
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.succes_text))
                .setDescription(getString(R.string.redeem_fund_successful_message, fundName.toTitleCase()))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick { loadAccountsAndGoHome() }
        navigate(RedeemFundSummaryFragmentDirections.actionSellFundSummaryFragmentToGenericResultScreenFragment(resultScreenProperties))
    }

    private fun navigateToFailureResultScreen() {
        hostActivity.trackEvent("UTRedeemFund_FailureScreen_ScreenDisplayed")
        hideToolBar()
        hostActivity.hideProgressIndicator()
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.status_unsuccessful).toSentenceCase())
                .setDescription(getString(R.string.buy_unit_trust_unsuccessful_description))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(false)

        GenericResultScreenFragment.setPrimaryButtonOnClick { loadAccountsAndGoHome() }
        navigate(RedeemFundSummaryFragmentDirections.actionSellFundSummaryFragmentToGenericResultScreenFragment(resultScreenProperties))
    }
}