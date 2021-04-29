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

package com.barclays.absa.banking.depositorPlus.ui

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.express.invest.createAndLinkAccountForExistingCustomer.dto.AccountCreateAndLinkRequest
import com.barclays.absa.banking.express.invest.createAndLinkAccountForExistingCustomer.dto.AccountCreateAndLinkStatus
import com.barclays.absa.banking.express.invest.createAndLinkAccountForExistingCustomer.dto.InvestmentAccount
import com.barclays.absa.banking.saveAndInvest.SaveAndInvestConfirmationFragment
import com.barclays.absa.banking.saveAndInvest.SaveAndInvestRiskBasedHelper
import com.barclays.absa.banking.saveAndInvest.YES
import com.barclays.absa.banking.shared.TermsAndConditionsInfo
import com.barclays.absa.utils.DateUtils

class DepositorPlusConfirmationFragment : SaveAndInvestConfirmationFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolBar(R.string.depositor_plus_summary)
        saveAndInvestActivity.showToolbar()
        saveAndInvestActivity.hideProgressIndicator()
    }

    override fun navigateToPersonalClientAgreement(termsAndConditionsInfo: TermsAndConditionsInfo) {
        navigate(DepositorPlusConfirmationFragmentDirections.actionDepositorPlusConfirmationFragmentToTermsAndConditionsFragment(termsAndConditionsInfo))
    }

    override fun buildAccountCreateAndLinkRequest(): AccountCreateAndLinkRequest =
            AccountCreateAndLinkRequest().apply {
                with(saveAndInvestViewModel) {
                    investmentAccount = InvestmentAccount().apply {
                        frequencyCode = 1
                        accountName = DepositorPlusActivity.DEPOSITOR_PLUS_UPPER_CASE
                        cifKey = customerDetails.cifKey
                        productCode = saveAndInvestActivity.productType.productCode
                        productName = DepositorPlusActivity.DEPOSITOR_PLUS
                        sourceOfFunds = sourceOfFundsCodes()
                        initialInvestmentAmount = initialDepositAmount
                        creditRatePlanCode = saveAndInvestActivity.productType.creditRatePlanCode
                        riskBasedServiceStatus = riskRatingResponse.riskRatingServiceStatus
                        riskRating = riskRatingResponse.riskRating
                    }
                    onceOffAmount = initialDepositAmount
                    changedAccountName = accountName
                    savingsFrequencyType = SaveAndInvestRiskBasedHelper.INITIAL
                    initiatePaymentAccount = initialDepositAccount.accountNumber.toLong()
                    initiatePaymentReference = initialDepositReference.trim()

                    if (interestPaymentAccount != getString(R.string.depositor_plus_account_title)) {
                        interestPayoutAccount = interestAccountNumber
                        interestPayoutReference = interestReference.trim()
                        interestPayoutBranchCode = interestBranchCode
                        interestPayoutAccountType = interestAccountTypeCode
                    }
                    if (recurringPaymentAccount.accountNumber.isNotBlank()) {
                        monthlyInstallmentAmount = recurringPaymentAmount
                        monthlyDebitAccount = recurringPaymentAccount.accountNumber.toLong()
                        startDate = DateUtils.formatDate(recurringPaymentStartDate, DateUtils.DATE_DISPLAY_PATTERN, DateUtils.SLASH_DATE_PATTERN)
                        endDate = DateUtils.formatDate(recurringPaymentEndDate, DateUtils.DATE_DISPLAY_PATTERN, DateUtils.SLASH_DATE_PATTERN)
                        nextPaymentDate = DateUtils.formatDate(recurringPaymentStartDate, DateUtils.DATE_DISPLAY_PATTERN, DateUtils.SLASH_DATE_PATTERN)
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
        navigate(DepositorPlusConfirmationFragmentDirections.actionDepositorPlusConfirmationFragmentToDepositorPlusGenericResultScreenFragment(properties))
    }

    override fun navigateOnCreateAccountFailureResponse() {
        saveAndInvestActivity.hideProgressIndicatorAndToolbar()
        navigate(DepositorPlusConfirmationFragmentDirections.actionDepositorPlusConfirmationFragmentToDepositorPlusGenericResultScreenFragment(buildFailureResultScreenProperties { trackAnalyticsAction("FailureScreen_DoneButtonClicked") }))
    }
}