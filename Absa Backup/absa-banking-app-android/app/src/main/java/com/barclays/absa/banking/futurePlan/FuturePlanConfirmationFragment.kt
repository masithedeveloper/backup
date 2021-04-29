/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank Limited
 *
 */

package com.barclays.absa.banking.futurePlan

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.express.invest.createAndLinkAccountForExistingCustomer.dto.AccountCreateAndLinkRequest
import com.barclays.absa.banking.express.invest.createAndLinkAccountForExistingCustomer.dto.AccountCreateAndLinkStatus
import com.barclays.absa.banking.express.invest.createAndLinkAccountForExistingCustomer.dto.InvestmentAccount
import com.barclays.absa.banking.futurePlan.FuturePlanActivity.Companion.FUTURE_PLAN
import com.barclays.absa.banking.futurePlan.FuturePlanActivity.Companion.FUTURE_PLAN_UPPER_CASE
import com.barclays.absa.banking.saveAndInvest.SaveAndInvestConfirmationFragment
import com.barclays.absa.banking.saveAndInvest.SaveAndInvestRiskBasedHelper
import com.barclays.absa.banking.saveAndInvest.YES
import com.barclays.absa.banking.shared.TermsAndConditionsInfo
import com.barclays.absa.utils.DateTimeHelper
import com.barclays.absa.utils.DateTimeHelper.NO_SPACED_PATTERN_MMMM
import com.barclays.absa.utils.DateTimeHelper.SLASH_PATTERN_YYYY_MM_DD
import java.util.*

class FuturePlanConfirmationFragment : SaveAndInvestConfirmationFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolBar(R.string.summary)
        saveAndInvestActivity.showToolbar()
        saveAndInvestActivity.hideProgressIndicator()

        setupViews()
    }

    override fun navigateToPersonalClientAgreement(termsAndConditionsInfo: TermsAndConditionsInfo) {
        navigate(FuturePlanConfirmationFragmentDirections.actionFuturePlanConfirmationFragmentToFuturePlanTermsAndConditionsFragment(termsAndConditionsInfo))
    }

    private fun setupViews() {
        with(binding) {
            investmentTermSecondaryContentAndLabelView.visibility = View.VISIBLE
            investmentTermSecondaryContentAndLabelView.setContentText(saveAndInvestViewModel.investmentTerm.value)
            maturityDateSecondaryContentAndLabelView.visibility = View.VISIBLE
            maturityDateSecondaryContentAndLabelView.setContentText(saveAndInvestViewModel.maturityDate)
            interestPaymentAccountSecondaryContentAndLabelView.setContentText(getString(R.string.future_plan_this_future_plan_account))
            interestPayoutDateSecondaryContentAndLabelView.visibility = View.VISIBLE
            interestPayoutDateSecondaryContentAndLabelView.setContentText(getString(R.string.future_plan_interest_payout_date_value))
            nextInterestPayoutDateSecondaryContentAndLabelView.visibility = View.VISIBLE
            nextInterestPayoutDateSecondaryContentAndLabelView.setContentText(getString(R.string.future_plan_next_payout_date_value, getNextPayoutMonth()))
        }
    }

    private fun getNextPayoutMonth(): String {
       with(GregorianCalendar()) {
           if (get(Calendar.DAY_OF_MONTH) > 7) {
               add(Calendar.MONTH, 1)
           }
           return DateTimeHelper.formatDate(time, NO_SPACED_PATTERN_MMMM)
       }
    }

    override fun buildAccountCreateAndLinkRequest(): AccountCreateAndLinkRequest =
            AccountCreateAndLinkRequest().apply {
                with(saveAndInvestViewModel) {
                    investmentAccount = InvestmentAccount().apply {
                        frequencyCode = 1
                        accountName = FUTURE_PLAN_UPPER_CASE
                        cifKey = customerDetails.cifKey
                        productCode = saveAndInvestActivity.productType.productCode
                        productName = FUTURE_PLAN
                        sourceOfFunds = sourceOfFundsCodes()
                        initialInvestmentAmount = initialDepositAmount
                        creditRatePlanCode = saveAndInvestActivity.productType.creditRatePlanCode
                        riskBasedServiceStatus = riskRatingResponse.riskRatingServiceStatus
                        riskRating = riskRatingResponse.riskRating
                        investmentTerm = investmentTermInDays
                    }
                    onceOffAmount = initialDepositAmount
                    changedAccountName = accountName
                    savingsFrequencyType = SaveAndInvestRiskBasedHelper.INITIAL
                    initiatePaymentAccount = initialDepositAccount.accountNumber.toLong()
                    initiatePaymentReference = initialDepositReference.trim()

                    if (recurringPaymentAccount.accountNumber.isNotBlank()) {
                        monthlyInstallmentAmount = recurringPaymentAmount
                        monthlyDebitAccount = recurringPaymentAccount.accountNumber.toLong()
                        startDate = DateTimeHelper.formatDate(recurringPaymentStartDate, SLASH_PATTERN_YYYY_MM_DD)
                        endDate = DateTimeHelper.formatDate(recurringPaymentEndDate, SLASH_PATTERN_YYYY_MM_DD)
                        nextPaymentDate = DateTimeHelper.formatDate(recurringPaymentStartDate, SLASH_PATTERN_YYYY_MM_DD)
                        monthlyDebitReference = recurringPaymentReference.trim()
                    }
                }
            }

    override fun navigateOnCreateAccountSuccessResponse(accountCreateAndLinkStatus: AccountCreateAndLinkStatus) {
        val properties = when {
            !accountCreateAndLinkStatus.accountCreated || accountCreateAndLinkStatus.accountNumber.toString().isBlank() -> buildFailureResultScreenProperties { trackAnalyticsAction("FailureScreen_DoneButtonClicked") }
            !accountCreateAndLinkStatus.accountLinked -> buildLinkAccountResultScreenProperties(accountCreateAndLinkStatus) { trackAnalyticsAction("SuccessScreen_DoneButtonClicked") }
            accountCreateAndLinkStatus.payOutIndicator != YES -> buildFundAccountResultScreenProperties(accountCreateAndLinkStatus) { trackAnalyticsAction("SuccessScreen_DoneButtonClicked") }
            else -> buildSuccessResultScreenProperties(accountCreateAndLinkStatus) { trackAnalyticsAction("SuccessScreen_DoneButtonClicked") }
        }
        saveAndInvestActivity.hideProgressIndicatorAndToolbar()
        navigate(FuturePlanConfirmationFragmentDirections.actionFuturePlanConfirmationFragmentToGenericResultScreenFragment(properties))
    }

    override fun navigateOnCreateAccountFailureResponse() {
        saveAndInvestActivity.hideProgressIndicatorAndToolbar()
        navigate(FuturePlanConfirmationFragmentDirections.actionFuturePlanConfirmationFragmentToGenericResultScreenFragment(buildFailureResultScreenProperties { trackAnalyticsAction("FailureScreen_DoneButtonClicked") }))
    }
}