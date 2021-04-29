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

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBConstants.YES
import com.barclays.absa.utils.TextFormatUtils
import kotlinx.android.synthetic.main.switch_unit_trusts_funds_summary_fragment.*
import styleguide.utils.extensions.toTitleCase

class SwitchFundSummaryFragment : SwitchFundBaseFragment(R.layout.switch_unit_trusts_funds_summary_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.summary)
        setUpProgressIndicator()
        displayData()

        confirmButton.setOnClickListener {
            navigate(SwitchFundSummaryFragmentDirections.actionSwitchFundSummaryFragmentToSwitchFundTermsAndConditionsFragment())
        }
    }

    private fun setUpProgressIndicator() {
        hostActivity.showProgressIndicator()
        hostActivity.progressIndicatorStep(2)
    }

    private fun displayData() {
        viewUnitTrustViewModel.unitTrustSwitchAccountData.value?.apply {
            val switchOptionAllUnits = getString(R.string.switch_redemption_type_all_units)
            val switchOptionNumberOfUnits = getString(R.string.switch_redemption_value_number_of_units)
            val switchOptionRandValue = getString(R.string.switch_redemption_type_rand_value)

            valueContentAndLabelView.visibility = if (switchOption.equals(SwitchFundDetailFragment.ALL_UNITS, true)) View.GONE else View.VISIBLE

            when (switchOption) {
                SwitchFundDetailFragment.RAND_VALUE -> redemptionTypeContentAndLabelView.setContentText(switchOptionRandValue)
                SwitchFundDetailFragment.NUMBER_OF_UNITS -> redemptionTypeContentAndLabelView.setContentText(switchOptionNumberOfUnits)
                SwitchFundDetailFragment.ALL_UNITS -> redemptionTypeContentAndLabelView.setContentText(switchOptionAllUnits)
            }

            if (YES.equals(newDebitOrder, true)) {
                debitAccountContentAndLabelView.visibility = View.VISIBLE
                debitDayContentAndLabelView.visibility = View.VISIBLE
                debitOrderAmountContentAndLabelView.visibility = View.VISIBLE
                debitOrderYearlyIncreaseContentAndLabelView.visibility = View.VISIBLE

                debitAccountContentAndLabelView.setContentText("${debitOrderAccountType.toTitleCase()} - $debitOrderAccount")
                debitDayContentAndLabelView.setContentText(debitOrderDay)
                debitOrderAmountContentAndLabelView.setContentText("${TextFormatUtils.formatBasicAmountAsRand(debitOrderAmount)} ${getString(R.string.switch_per_month)}")
                debitOrderYearlyIncreaseContentAndLabelView.setContentText("$debitOrderPercentageIncrease%")
            } else {
                debitAccountContentAndLabelView.visibility = View.GONE
                debitDayContentAndLabelView.visibility = View.GONE
                debitOrderAmountContentAndLabelView.visibility = View.GONE
                debitOrderYearlyIncreaseContentAndLabelView.visibility = View.GONE
            }

            if ("Y".equals(incomeDistributionIndicator, true)) {
                accountContentAndLabelView.visibility = View.VISIBLE
                payIntoAccountContentAndLabelView.visibility = View.VISIBLE
                payIntoAccountContentAndLabelView.setContentText(getString(R.string.switch_pay_into_my_account))
                accountContentAndLabelView.setContentText("${incomeDistributionAccountType.toTitleCase()} - $incomeDistributionBankAccount")
            } else {
                accountContentAndLabelView.visibility = View.GONE
                payIntoAccountContentAndLabelView.visibility = View.GONE
            }

            selectedFundContentAndLabelView.setContentText(toFundName.toTitleCase())
            valueContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmount(unitsToSwitch))
            accountHolderContentAndLabelView.setContentText(unitTrustAccountHolderName)
            accountNumberContentAndLabelView.setContentText(unitTrustAccountNumber)
        }
    }
}