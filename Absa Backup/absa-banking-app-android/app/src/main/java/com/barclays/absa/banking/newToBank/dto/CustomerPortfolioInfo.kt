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

package com.barclays.absa.banking.newToBank.dto

data class CustomerPortfolioInfo(
        var productType: String = "",
        var sourceOfIncome: String = "",
        var sourceOfFunds: String = "",
        var employmentSector: String = "",
        var monthlyIncomeRange: String = "",
        var occupationCode: String = "",
        var medicalOccupationCode: String = "",
        var occupationStatus: String = "",
        var occupationLevel: String = "",
        var registeredForTax: Boolean = false,
        var foreignNationalTaxResident: Boolean = false,
        var foreignNationalResidentCountry: String = "",
        var personalClientAgreementAccepted: Boolean = true,
        var creditCheckConsent: Boolean = true,
        var underDebtCounseling: Boolean = false,
        var marketingIndicator: Boolean = false,
        var mailMarketingIndicator: Boolean = false,
        var emailMarketingIndicator: Boolean = false,
        var smsMarketingIndicator: Boolean = false,
        var teleMarketingIndicator: Boolean = false,
        var voiceMarketingIndicator: Boolean = false,
        var foreignTrading: Boolean = false,
        var feesLinkDisplayed: Boolean = true,
        var accountFeatureDisplayed: Boolean = true,
        var totalMonthlyIncome: String = "",
        var totalMonthlyExpenses: String = "",
        var marketPropertyValue: String = "",
        var preferredMarketingCommunication: String = "",
        var enableRewards: Boolean = false,
        var rewardsTermsAccepted: Boolean = false,
        var rewardsDayOfDebitOrder: String = "",
        var residentialAddressSince: String = "",
        var currentEmploymentSince: String = "",
        var residentialStatus: String = "",
        var overdraftRequired: Boolean = false,
        var eStatementRequired: Boolean = true,
        var notifyMeRequired: Boolean = true,
        var rewardsMarketingConsent: Boolean = false,
        var useSelfie: Boolean = false,
        var shortAndMediumTermFundingRewardsValueAddedService : String = "",
        var assetFinanceRewardsValueAddedService: String = "",
        var makeAndReceivePaymentsRewardsValueAddedService3: String = "",
        var savingAndInvestmentProductsRewardsValueAddedService4: String = "")