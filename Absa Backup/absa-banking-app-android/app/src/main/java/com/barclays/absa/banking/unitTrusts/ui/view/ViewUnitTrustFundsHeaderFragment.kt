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

package com.barclays.absa.banking.unitTrusts.ui.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.unitTrusts.services.dto.BuyMoreUnitsInfo
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustFund
import com.barclays.absa.banking.unitTrusts.ui.switchAndRedeem.RedeemFundHostActivity
import com.barclays.absa.banking.unitTrusts.ui.switchAndRedeem.SwitchFundHostActivity
import com.barclays.absa.banking.unitTrusts.ui.view.ViewUnitTrustAccountsFragment.Companion.UNIT_TRUST_ACCOUNT_HOLDER
import com.barclays.absa.banking.unitTrusts.ui.view.ViewUnitTrustAccountsFragment.Companion.UNIT_TRUST_ACCOUNT_NUMBER
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.TextFormatUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.view_unit_trust_header_fragment.*

class ViewUnitTrustFundsHeaderFragment : BaseFragment(R.layout.view_unit_trust_header_fragment) {

    private lateinit var viewUnitTrustViewModel: ViewUnitTrustViewModel
    private lateinit var hostActivity: ViewUnitTrustFundBaseActivity

    companion object {
        private const val FUND_PARCEL_KEY = "fundParcel"
        private const val REDEMPTION_ACCOUNT_PARCEL_KEY = "redemptionAccountParcel"
        private const val FUNDISA = "Fundisa"

        fun newInstance(unitTrustFund: UnitTrustFund, accountHolder: String, accountNumber: String): ViewUnitTrustFundsHeaderFragment {
            val viewUnitTrustFundsHeaderFragment = ViewUnitTrustFundsHeaderFragment()
            val arguments = Bundle()
            arguments.putParcelable(FUND_PARCEL_KEY, unitTrustFund)
            arguments.putString(UNIT_TRUST_ACCOUNT_NUMBER, accountNumber)
            arguments.putString(UNIT_TRUST_ACCOUNT_HOLDER, accountHolder)
            viewUnitTrustFundsHeaderFragment.arguments = arguments
            return viewUnitTrustFundsHeaderFragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as ViewUnitTrustFundBaseActivity
        viewUnitTrustViewModel = hostActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val featureSwitchingToggles = FeatureSwitchingCache.featureSwitchingToggles
        val fund = arguments?.getParcelable(FUND_PARCEL_KEY) as UnitTrustFund?

        if (!fund?.fundName.toString().contains(FUNDISA, true)) {
            if (featureSwitchingToggles.wimiBuyMoreUnitTrusts == FeatureSwitchingStates.ACTIVE.key) {
                buyMoreUnitsImageView.visibility = View.VISIBLE
                buyTextView.visibility = View.VISIBLE
            }

            if (featureSwitchingToggles.wimiRedeemUnitTrusts == FeatureSwitchingStates.ACTIVE.key) {
                redeemFundsImageView.visibility = View.VISIBLE
                redeemTextView.visibility = View.VISIBLE
            }

            if (featureSwitchingToggles.wimiSwitchUnitTrusts == FeatureSwitchingStates.ACTIVE.key) {
                switchFundImageView.visibility = View.VISIBLE
                switchTextView.visibility = View.VISIBLE
            }
        }

        val accountNumber = arguments?.getString(UNIT_TRUST_ACCOUNT_NUMBER)
        val accountHolder = arguments?.getString(UNIT_TRUST_ACCOUNT_HOLDER)

        fund?.let {
            balanceTextView.text = TextFormatUtils.formatBasicAmount(String.format("R %s", TextFormatUtils.formatBasicAmount(it.fundAvailableBalance)))
            coverAmountTitleTextView.text = getString(R.string.available_balance)
            viewUnitTrustViewModel.buyMoreUnitsData.value = BuyMoreUnitsInfo()
            viewUnitTrustViewModel.buyMoreUnitsData.value?.fund = fund
            viewUnitTrustViewModel.buyMoreUnitsData.value?.accountHolder = accountHolder.orEmpty()
            viewUnitTrustViewModel.buyMoreUnitsData.value?.accountNumber = accountNumber.orEmpty()
        }

        redeemFundsImageView.setOnClickListener {
            viewUnitTrustViewModel.buyUnitTrustActions = UnitTrustFundActions.REDEEM
            viewUnitTrustViewModel.fetchBuyMoreUnitsCappedStatus()
        }

        switchFundImageView.setOnClickListener {
            viewUnitTrustViewModel.buyUnitTrustActions = UnitTrustFundActions.SWITCH
            viewUnitTrustViewModel.fetchBuyMoreUnitsCappedStatus()
        }

        buyMoreUnitsImageView.setOnClickListener {
            viewUnitTrustViewModel.buyUnitTrustActions = UnitTrustFundActions.BUY
            viewUnitTrustViewModel.fetchBuyMoreUnitsCappedStatus()
        }

        viewUnitTrustViewModel.buyMoreUnitsCappedLiveData.observe(this, { buyMoreUnitsCappedResponse ->
            buyMoreUnitsCappedResponse?.let {
                if (it.status) {
                    AnalyticsUtil.trackAction(ViewUnitTrustHostActivity.BUY_MORE_UNITS_CHANNEL, "WIMI_UT_MoreUnits_CapError")
                    startActivity(IntentFactory.getUnableToContinueScreenWithPrimaryButton(activity, R.string.unable_to_continue, R.string.buy_more_unit_error))
                } else {
                    when (viewUnitTrustViewModel.buyUnitTrustActions) {
                        UnitTrustFundActions.BUY -> {
                            val intent = Intent(activity, ViewUnitTrustHostActivity::class.java).apply {
                                putExtra(FUND_PARCEL_KEY, fund)
                                putExtra(UNIT_TRUST_ACCOUNT_NUMBER, accountNumber)
                                putExtra(UNIT_TRUST_ACCOUNT_HOLDER, accountHolder)
                            }
                            startActivity(intent)
                        }
                        UnitTrustFundActions.SWITCH -> {
                            val intent = Intent(activity, SwitchFundHostActivity::class.java).apply {
                                putExtra(FUND_PARCEL_KEY, fund)
                                putExtra(UNIT_TRUST_ACCOUNT_NUMBER, accountNumber)
                                putExtra(UNIT_TRUST_ACCOUNT_HOLDER, accountHolder)
                            }
                            startActivity(intent)
                        }

                        UnitTrustFundActions.REDEEM -> {
                            viewUnitTrustViewModel.fetchUnitTrustAccountRedemptionStatus()
                        }
                    }
                }
            }
            viewUnitTrustViewModel.buyMoreUnitsCappedLiveData.removeObservers(hostActivity)
            dismissProgressDialog()
        })

        viewUnitTrustViewModel.unitTrustRedemptionAccountLiveData.observe(this, { unitTrustAccountRedemptionResponse ->
            if (unitTrustAccountRedemptionResponse.successMessage) {
                val intent = Intent(activity, RedeemFundHostActivity::class.java).apply {
                    putExtra(REDEMPTION_ACCOUNT_PARCEL_KEY, unitTrustAccountRedemptionResponse.redemptionAccountDetails)
                    putExtra(FUND_PARCEL_KEY, fund)
                    putExtra(UNIT_TRUST_ACCOUNT_NUMBER, accountNumber)
                    putExtra(UNIT_TRUST_ACCOUNT_HOLDER, accountHolder)
                }
                startActivity(intent)
            } else {
                startActivity(IntentFactory.getUnableToContinueScreenWithPrimaryButton(activity, R.string.unable_to_continue, R.string.switch_fund_no_redemption_account_error_message))
            }
        })
    }
}