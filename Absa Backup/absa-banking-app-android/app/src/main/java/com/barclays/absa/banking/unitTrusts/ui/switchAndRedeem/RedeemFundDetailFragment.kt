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
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBConstants.NO
import com.barclays.absa.banking.framework.app.BMBConstants.YES
import com.barclays.absa.banking.unitTrusts.ui.view.ViewUnitTrustViewModel
import com.barclays.absa.utils.TextFormatUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.sell_fund_details_fragment.*
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import styleguide.utils.extensions.toTitleCase

class RedeemFundDetailFragment : BaseFragment(R.layout.sell_fund_details_fragment) {

    private lateinit var viewUnitTrustViewModel: ViewUnitTrustViewModel
    private lateinit var hostActivity: RedeemFundHostActivity
    private lateinit var redemptionTypeNumberOfUnits: String
    private lateinit var redemptionTypeRandValue: String
    private lateinit var redemptionTypeAllUnits: String

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as RedeemFundHostActivity
        viewUnitTrustViewModel = hostActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.redeem_fund_redemption_details_header)
        setUpProgressIndicator()
        setUpUI()
        hostActivity.trackEvent("UTRedeemFund_RedeemDetailsScreen_ScreenDisplayed")
    }

    private fun setUpUI() {
        redemptionTypeNumberOfUnits = getString(R.string.switch_redemption_value_number_of_units)
        redemptionTypeRandValue = getString(R.string.switch_redemption_type_rand_value)
        redemptionTypeAllUnits = getString(R.string.switch_redemption_type_all_units)

        sellValueInputView.showDescription(false)

        viewUnitTrustViewModel.unitTrustRedemptionAccountData.value?.unitTrustFund?.let {
            selectedFundTitleAndDescriptionView.title = it.fundName.toTitleCase()
            availableAmountLineItemView.setLineItemViewContent(TextFormatUtils.formatBasicAmountAsRand(it.fundAvailableBalance))
            availableUnitsLineItemView.setLineItemViewContent(TextFormatUtils.formatBasicAmount(it.fundAvailablelUnits))
        }

        redemptionTypeInputView.setList(buildRedemptionTypeOptions(), getString(R.string.redeem_fund_redemption_type).toTitleCase())

        cancelDebitOrderForFundCheckBox.setOnCheckedListener { isChecked ->
            viewUnitTrustViewModel.unitTrustRedemptionAccountData.value?.cancelDebitOrder = if (isChecked) YES else NO
        }

        nextButton.setOnClickListener {
            validateFields()
        }

        sellValueInputView.addRequiredValidationHidingTextWatcher { redemptionValue ->
            viewUnitTrustViewModel.unitTrustRedemptionAccountData.value?.unitTrustFund?.let {
                val redemptionType = redemptionTypeInputView.selectedValue
                val fundAvailableUnits = it.fundAvailablelUnits
                val fundAvailableBalance = it.fundAvailableBalance
                if (!fundAvailableBalance.isNullOrEmpty() && !fundAvailableUnits.isNullOrEmpty()) {
                    var availableValue = 0.0
                    when (redemptionType) {
                        redemptionTypeNumberOfUnits -> availableValue = fundAvailableUnits.toDouble()
                        redemptionTypeRandValue -> availableValue = fundAvailableBalance.toDouble()
                    }

                    val isRedemptionValueGreaterThanSellingThreshold = redemptionValue.toDouble() > availableValue.times(SELLING_THRESHOLD)
                    sellValueInputView.showDescription(isRedemptionValueGreaterThanSellingThreshold)
                    if (redemptionValue.toDouble() > availableValue) {
                        sellValueInputView.setError(getString(R.string.redeem_fund_redemption_value_error))
                    } else {
                        sellValueInputView.clearError()
                    }
                }
            }
        }

        redemptionTypeInputView.addRequiredValidationHidingTextWatcher {
            if (it.equals(redemptionTypeAllUnits, true)) {
                sellValueInputView.visibility = View.GONE
                redeemAll(true)
            } else {
                sellValueInputView.visibility = View.VISIBLE
            }
            sellValueInputView.selectedValue = ""
            sellValueInputView.clearError()
            sellValueInputView.clearDescription()
        }
    }

    private fun validateFields() {
        val redemptionType = redemptionTypeInputView.selectedValue
        val redemptionValue = sellValueInputView.selectedValue
        viewUnitTrustViewModel.unitTrustRedemptionAccountData.value?.unitTrustFund?.let {
            val fundAvailableUnits = it.fundAvailablelUnits
            val fundAvailableBalance = it.fundAvailableBalance
            if (!fundAvailableBalance.isNullOrEmpty() && !fundAvailableUnits.isNullOrEmpty()) {
                when {
                    redemptionType == redemptionTypeAllUnits -> navigate(RedeemFundDetailFragmentDirections.actionSellFundDetailFragmentToSellFundSummaryFragment())
                    redemptionType.isEmpty() -> redemptionTypeInputView.setError(getString(R.string.redeem_fund_redemption_type_required))
                    redemptionValue.isEmpty() -> if (redemptionType != redemptionTypeAllUnits) sellValueInputView.setError(getString(R.string.redeem_fund_redemption_value_required))
                    redemptionType == redemptionTypeNumberOfUnits -> {
                        if (!sellValueInputView.hasError()) {
                            setRedeemDetails(redemptionValue, redemptionType)
                            navigate(RedeemFundDetailFragmentDirections.actionSellFundDetailFragmentToSellFundSummaryFragment())
                        }
                    }

                    redemptionType == redemptionTypeRandValue -> {
                        if (redemptionValue.toDouble() > fundAvailableBalance.toDouble()) {
                            sellValueInputView.setError(getString(R.string.redeem_fund_redemption_value_error))
                        } else {
                            setRedeemDetails(redemptionValue, redemptionType)
                            navigate(RedeemFundDetailFragmentDirections.actionSellFundDetailFragmentToSellFundSummaryFragment())
                        }
                    }
                }
            }
        }
    }

    private fun setRedeemDetails(redemptionValue: String, redeemType: String) {
        viewUnitTrustViewModel.unitTrustRedemptionAccountData.value?.let {
            val unitTrustFund = it.unitTrustFund
            val fundAvailableUnits = unitTrustFund.fundAvailablelUnits
            val fundAvailableBalance = unitTrustFund.fundAvailableBalance
            val redemptionTypeRandValue = getString(R.string.switch_redemption_type_rand_value)

            it.apply {
                if (!fundAvailableBalance.isNullOrBlank() && !fundAvailableUnits.isNullOrBlank()) {
                    if (redeemType == redemptionTypeRandValue) {
                        redemptionType = "V"
                        fundUnits = "0.00"
                        redeemAmount = if (isPercentageGreaterThanBalance(redemptionValue, redeemType)) fundAvailableBalance else redemptionValue
                    } else {
                        redemptionType = "U"
                        redeemAmount = "0.00"
                        fundUnits = if (isPercentageGreaterThanBalance(redemptionValue, redeemType)) fundAvailableUnits else redemptionValue
                    }
                    redeemAll(false)
                }
            }
        }
    }

    private fun redeemAll(redeemAll: Boolean) {
        viewUnitTrustViewModel.unitTrustRedemptionAccountData.value?.apply {
            if (redeemAll) {
                redemptionType = "A"
                allUnits = "Y"
                this.redeemAll = "Y"
                fundUnits = viewUnitTrustViewModel.unitTrustRedemptionAccountData.value?.unitTrustFund?.fundAvailablelUnits
                        ?: "0"
                redeemAmount = "0"
            } else {
                allUnits = "N"
                this.redeemAll = "N"
            }
        }
    }

    private fun buildRedemptionTypeOptions(): SelectorList<StringItem> {
        val redemptionOptions = resources.getStringArray(R.array.redemptionTypeOptions)
        val redemptionTypeOptions = SelectorList<StringItem>()
        for (option in redemptionOptions) {
            redemptionTypeOptions.add(StringItem(option))
        }
        return redemptionTypeOptions
    }

    private fun setUpProgressIndicator() {
        val hostActivity = activity as RedeemFundHostActivity
        hostActivity.showProgressIndicator()
        hostActivity.progressIndicatorStep(1)
    }

    private fun isPercentageGreaterThanBalance(amount: String, redemptionType: String): Boolean {
        var validAmount = false
        val redemptionTypeRandValue = getString(R.string.switch_redemption_type_rand_value)

        viewUnitTrustViewModel.unitTrustRedemptionAccountData.value?.unitTrustFund?.let {
            val fundAvailableUnits = it.fundAvailablelUnits
            val fundAvailableBalance = it.fundAvailableBalance
            if (!fundAvailableBalance.isNullOrEmpty() && redemptionType == redemptionTypeRandValue) {
                validAmount = amount.toDouble() > fundAvailableBalance.toDouble().times(SELLING_THRESHOLD)
            } else {
                fundAvailableUnits?.let {
                    validAmount = amount.toDouble() > fundAvailableUnits.toDouble().times(SELLING_THRESHOLD)
                }
            }
        }

        return validAmount
    }

    companion object {
        private const val SELLING_THRESHOLD = 0.95
    }
}