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
 */

package com.barclays.absa.banking.fixedDeposit.services.dto

class FixedDepositParameters {
    companion object {
        const val CATEGORY_CODE = "categoryCode"
        const val TYPE = "type"
        const val NAME = "name"
        const val MINIMUM_DEPOSIT = "minimumDeposit"
        const val AMOUNT = "amount"
        const val FROM_ACCOUNT = "fromAccount"
        const val FROM_DESCRIPTION = "fromDescription"
        const val TO_DESCRIPTION = "toDescription"
        const val SOURCE_OF_FUNDS = "sourceOfFunds"
        const val CAP_FREQUENCY = "capFrequency"
        const val CAP_DAY = "capDay"
        const val NEXT_CAP_DATE = "nextCapDate"
        const val MATURITY_DATE = "maturityDate"
        const val AUTOMATIC_RENEWAL = "automaticRenewal"
        const val MONTHLY_INTEREST_PAYMENTS = "monthlyInterestPayments"
        const val INTEREST_TO_ACCOUNT_NO = "interestToAccountNo"
        const val INTEREST_TO_BANK_NAME = "interestToBankName"
        const val INTEREST_TO_BRANCH_NAME = "interestToBranchName"
        const val INTEREST_TO_BRANCH_CODE = "interestToBranchCode"
        const val INTEREST_TO_ACCOUNT_TYPE = "interestToAccountType"
        const val ACCOUNT_NUMBER = "accountNumber"
        const val SURE_CHECK_ACCEPTED = "sureCheckAccepted"
    }
}