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

package com.barclays.absa.banking.fixedDeposit

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.fixedDeposit.responseListeners.FixedDepositViewModel
import com.barclays.absa.banking.fixedDeposit.services.dto.FixedDeposit
import com.barclays.absa.banking.fixedDeposit.services.dto.FixedDepositAccountDetailsResponse
import com.barclays.absa.banking.shared.ItemPagerFragment
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.DateUtils.DASHED_DATE_PATTERN
import com.barclays.absa.utils.DateUtils.DATE_DISPLAY_PATTERN
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.fixed_deposit_details_fragment.*
import styleguide.utils.extensions.toFormattedAccountNumber
import styleguide.utils.extensions.toTitleCaseRemovingCommas
import java.util.*

class FixedDepositDetailsFragment : ItemPagerFragment(R.layout.fixed_deposit_details_fragment) {

    private lateinit var fixedDepositHubActivity: FixedDepositHubActivity
    private lateinit var fixedDepositDetails: FixedDeposit
    private lateinit var accountDetailsResponse: FixedDepositAccountDetailsResponse

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fixedDepositHubActivity = activity as FixedDepositHubActivity
        val fixedDepositViewModel: FixedDepositViewModel = fixedDepositHubActivity.viewModel()
        val accountNumber = fixedDepositHubActivity.accountObject.accountNumber
        accountDetailsResponse = fixedDepositViewModel.accountDetailsResponse.value ?: FixedDepositAccountDetailsResponse()
        fixedDepositDetails = accountDetailsResponse.fixedDeposit ?: FixedDeposit()

        accountNumberPrimaryContentAndLabelView.setContentText(accountNumber.toFormattedAccountNumber())
        if (fixedDepositViewModel.isIslamicAccount()) {
            setIslamicLabels()
        } else {
            setTermDepositLabels()
        }
    }

    private fun setIslamicLabels() {
        detailsOptionActionButtonView.visibility = View.GONE
        dividerView.visibility = View.GONE
        infoTextView.visibility = View.GONE
        interestPaymentFrequencyLineItemView.visibility = View.VISIBLE

        investmentTermLineItemView.setLineItemViewLabel(getString(R.string.fixed_deposit_opened_on))
        interestRateLineItemView.setLineItemViewLabel(getString(R.string.fixed_deposit_maturity_date))
        activationDateLineItemView.setLineItemViewLabel(getString(R.string.fixed_deposit_profit_share_rate))
        interestPaymentFrequencyLineItemView.setLineItemViewLabel(getString(R.string.fixed_deposit_profit_share_payment_frequency))
        maturityDateLineItemView.setLineItemViewLabel(getString(R.string.fixed_deposit_profit_share_payment_day))

        investmentTermLineItemView.setLineItemViewContent(DateUtils.formatDate(DateUtils.formatDate(fixedDepositDetails.dateOpened, DASHED_DATE_PATTERN, DATE_DISPLAY_PATTERN)))
        interestRateLineItemView.setLineItemViewContent(DateUtils.formatDate(fixedDepositDetails.maturityDate, DASHED_DATE_PATTERN, DATE_DISPLAY_PATTERN))
        activationDateLineItemView.setLineItemViewContent("${fixedDepositDetails.currentInterestRate}%")
        interestPaymentFrequencyLineItemView.setLineItemViewContent(fixedDepositDetails.capFrequency.toTitleCaseRemovingCommas())
        maturityDateLineItemView.setLineItemViewContent(DateUtils.formatDate(fixedDepositDetails.nextCapDate, DASHED_DATE_PATTERN, DATE_DISPLAY_PATTERN))
    }

    private fun setTermDepositLabels() {
        setInvestmentTerm()
        interestRateLineItemView.setLineItemViewContent("${fixedDepositDetails.currentInterestRate}%")
        activationDateLineItemView.setLineItemViewContent(DateUtils.formatDate(fixedDepositDetails.dateOpened, DASHED_DATE_PATTERN, DATE_DISPLAY_PATTERN))
        maturityDateLineItemView.setLineItemViewContent(DateUtils.formatDate(fixedDepositDetails.maturityDate, DASHED_DATE_PATTERN, DATE_DISPLAY_PATTERN))
        infoTextView.text = getString(R.string.fixed_deposit_account_info, maturityDateLineItemView.getContentTextView().text)

        val autoReinvestDate = DateUtils.getCalendarObj(fixedDepositDetails.maturityDate).apply { add(Calendar.DAY_OF_MONTH, 5) }.timeInMillis
        if (GregorianCalendar().timeInMillis > autoReinvestDate && fixedDepositHubActivity.accountObject.availableBalance.amountDouble < 1000) {
            detailsOptionActionButtonView.visibility = View.GONE
            dividerView.visibility = View.GONE
            infoTextView.visibility = View.GONE
            hideInterestRateFields()
        } else {
            setUpOptionItemButton()
        }
    }

    private fun setInvestmentTerm() = if (fixedDepositDetails.term.toInt() > 31) {
        val monthsBetween = when (fixedDepositDetails.term.toInt()) {
            in 32..59 -> (32..59).getMonthFromDayRange(0)
            in 60..364 -> (60..364).getMonthFromDayRange(1)
            in 365..577 -> (365..577).getMonthFromDayRange(11)
            in 578..729 -> (578..729).getMonthFromDayRange(18)
            in 730..1094 -> (730..1094).getMonthFromDayRange(23)
            in 1095..1459 -> (1095..1459).getMonthFromDayRange(35)
            in 1460..1825 -> (1460..1825).getMonthFromDayRange(47)
            else -> 60
        }
        val label = if (monthsBetween == 1) getString(R.string.fixed_deposit_month) else getString(R.string.fixed_deposit_months)
        investmentTermLineItemView.setLineItemViewContent("$monthsBetween $label")
    } else {
        investmentTermLineItemView.setLineItemViewContent("${fixedDepositDetails.term} ${getString(R.string.fixed_deposit_days)}")
    }

    private fun IntRange.getMonthFromDayRange(startMonth: Int): Int {
        var monthTerm = startMonth
        (this step 30).forEach { day ->
            if (fixedDepositDetails.term.toInt() >= day) {
                monthTerm++
            }
        }
        return monthTerm
    }

    private fun setUpOptionItemButton() {
        val flow = if ("matured".equals(fixedDepositDetails.accountStatus, true) || "verval".equals(fixedDepositDetails.accountStatus, true)) {
            infoTextView.visibility = View.GONE
            detailsOptionActionButtonView.setIcon(R.drawable.ic_fixed_deposit_reinvest)
            detailsOptionActionButtonView.setCaptionText(getString(R.string.fixed_deposit_new_investment_instruction))
            hideInterestRateFields()
            FixedDepositFlow.REINVEST
        } else {
            detailsOptionActionButtonView.setIcon(R.drawable.ic_my_accounts)
            detailsOptionActionButtonView.setCaptionText(getString(R.string.fixed_deposit_update_payout_details))
            FixedDepositFlow.CHANGE_PAYOUT_DETAILS
        }

        detailsOptionActionButtonView.setOnClickListener {
            val analyticsTag = if (flow == FixedDepositFlow.CHANGE_PAYOUT_DETAILS) "UpdatePayoutDetailsButtonClicked" else "NewInvestmentInstructionButtonClicked"
            AnalyticsUtil.trackAction(FixedDepositActivity.FIXED_DEPOSIT, "FixedTermDepositAccount_HubScreen_${analyticsTag}")
            startActivity(Intent(fixedDepositHubActivity, FixedDepositMaintenanceActivity::class.java).apply {
                putExtra(FIXED_DEPOSIT_ACCOUNT_DETAILS, accountDetailsResponse as Parcelable)
                putExtra(FixedDepositFlow::class.java.name, flow)
                putExtra(FIXED_DEPOSIT_ACCOUNT_OBJECT, fixedDepositHubActivity.accountObject)
            })
        }
    }

    private fun hideInterestRateFields() {
        investmentTermLineItemView.visibility = View.GONE
        interestRateLineItemView.visibility = View.GONE
        activationDateLineItemView.visibility = View.GONE
        interestPaymentFrequencyLineItemView.visibility = View.GONE
        maturityDateLineItemView.visibility = View.GONE
    }

    override fun getTabDescription(): String = arguments?.getString(TAB_DESCRIPTION_KEY) ?: ""

    companion object {
        fun newInstance(description: String): FixedDepositDetailsFragment {
            return FixedDepositDetailsFragment().apply { arguments = Bundle().apply { putString(TAB_DESCRIPTION_KEY, description) } }
        }

        const val FIXED_DEPOSIT_ACCOUNT_DETAILS = "FIXED_DEPOSIT_ACCOUNT_DETAILS"
        const val FIXED_DEPOSIT_ACCOUNT_OBJECT = "FIXED_DEPOSIT_ACCOUNT_OBJECT"
    }

    enum class FixedDepositFlow {
        CHANGE_PAYOUT_DETAILS,
        REINVEST
    }
}