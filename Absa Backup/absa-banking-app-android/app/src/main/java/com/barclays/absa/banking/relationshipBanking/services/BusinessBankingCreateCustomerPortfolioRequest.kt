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

package com.barclays.absa.banking.relationshipBanking.services

import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.NewToBankParams
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.newToBank.dto.CustomerPortfolioInfo
import com.barclays.absa.banking.newToBank.services.NewToBankService.Companion.OP2023_CREATE_CUSTOMER_PORTFOLIO
import com.barclays.absa.banking.newToBank.services.dto.CreateCustomerPortfolioAccountResponse
import com.barclays.absa.banking.relationshipBanking.services.dto.BusinessCustomerPortfolio

class BusinessBankingCreateCustomerPortfolioRequest<T>(customerPortfolioInfo: CustomerPortfolioInfo, businessCustomerPortfolio: BusinessCustomerPortfolio, extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {

    init {
        val newToBankRequest = RequestParams.Builder()
                .put(OP2023_CREATE_CUSTOMER_PORTFOLIO)
                .put(TransactionParams.Transaction.SERVICE_CHANNEL_IND, "S")
                .put(NewToBankParams.PRODUCT_TYPE.key, customerPortfolioInfo.productType)
                .put(NewToBankParams.SOURCE_OF_INCOME.key, customerPortfolioInfo.sourceOfIncome)
                .put(NewToBankParams.SOURCE_OF_FUNDS.key, customerPortfolioInfo.sourceOfFunds)
                .put(NewToBankParams.EMPLOYMENT_SECTOR.key, customerPortfolioInfo.employmentSector)
                .put(NewToBankParams.MONTHLY_INCOME_RANGE.key, customerPortfolioInfo.monthlyIncomeRange)
                .put(NewToBankParams.OCCUPATION_CODE.key, customerPortfolioInfo.occupationCode)
                .put(NewToBankParams.MEDICAL_OCCUPATION_CODE.key, customerPortfolioInfo.medicalOccupationCode)
                .put(NewToBankParams.OCCUPATION_STATUS.key, customerPortfolioInfo.occupationStatus)
                .put(NewToBankParams.OCCUPATION_LEVEL.key, customerPortfolioInfo.occupationLevel)
                .put(NewToBankParams.REGISTERED_FOR_TAX.key, customerPortfolioInfo.registeredForTax.toString())
                .put(NewToBankParams.FOREIGN_NATIONAL_TAX_RESIDENT.key, customerPortfolioInfo.foreignNationalTaxResident.toString())
                .put(NewToBankParams.FOREIGN_NATIONAL_RESIDENT_COUNTRY.key, customerPortfolioInfo.foreignNationalResidentCountry)
                .put(NewToBankParams.PERSONAL_CLIENT_AGREEMENT_ACCEPTED.key, customerPortfolioInfo.personalClientAgreementAccepted.toString())
                .put(NewToBankParams.CREDIT_CHECK_CONSENT.key, customerPortfolioInfo.creditCheckConsent.toString())
                .put(NewToBankParams.UNDER_DEBT_COUNSELING.key, customerPortfolioInfo.underDebtCounseling.toString())
                .put(NewToBankParams.MARKETING_INDICATOR.key, customerPortfolioInfo.marketingIndicator.toString())
                .put(NewToBankParams.MAIL_MARKETING_INDICATOR.key, customerPortfolioInfo.mailMarketingIndicator.toString())
                .put(NewToBankParams.EMAIL_MARKETING_INDICATOR.key, customerPortfolioInfo.emailMarketingIndicator.toString())
                .put(NewToBankParams.SMS_MARKETING_INDICATOR.key, customerPortfolioInfo.smsMarketingIndicator.toString())
                .put(NewToBankParams.TELE_MARKETING_INDICATOR.key, customerPortfolioInfo.teleMarketingIndicator.toString())
                .put(NewToBankParams.FEES_LINK_DISPLAYED.key, customerPortfolioInfo.feesLinkDisplayed.toString())
                .put(NewToBankParams.ACCOUNT_FEATURE_DISPLAYED.key, customerPortfolioInfo.accountFeatureDisplayed.toString())
                .put(NewToBankParams.TOTAL_MONTHLY_INCOME.key, customerPortfolioInfo.totalMonthlyIncome)
                .put(NewToBankParams.TOTAL_MONTHLY_EXPENSES.key, customerPortfolioInfo.totalMonthlyExpenses)
                .put(NewToBankParams.MARKET_PROPERTY_VALUE.key, customerPortfolioInfo.marketPropertyValue)
                .put(NewToBankParams.PREFERRED_MARKETING_COMMUNICATION.key, customerPortfolioInfo.preferredMarketingCommunication)
                .put(NewToBankParams.ENABLE_REWARDS.key, customerPortfolioInfo.enableRewards.toString())
                .put(NewToBankParams.REWARDS_TERMS_ACCEPTED.key, customerPortfolioInfo.rewardsTermsAccepted.toString())
                .put(NewToBankParams.REWARDS_DAY_OF_DEBIT_ORDER.key, customerPortfolioInfo.rewardsDayOfDebitOrder)
                .put(NewToBankParams.RESIDENTIAL_ADDRESS_SINCE.key, customerPortfolioInfo.residentialAddressSince)
                .put(NewToBankParams.CURRENT_EMPLOYMENT_SINCE.key, customerPortfolioInfo.currentEmploymentSince)
                .put(NewToBankParams.RESIDENTIAL_STATUS.key, customerPortfolioInfo.residentialStatus)
                .put(NewToBankParams.OVERDRAFT_REQUIRED.key, customerPortfolioInfo.overdraftRequired.toString())
                .put(NewToBankParams.E_STATEMENT_REQUIRED.key, customerPortfolioInfo.eStatementRequired.toString())
                .put(NewToBankParams.NOTIFY_ME_REQUIRED.key, customerPortfolioInfo.notifyMeRequired.toString())
                .put(NewToBankParams.REWARDS_MARKETING_CONSENT.key, customerPortfolioInfo.rewardsMarketingConsent.toString())
                .put(NewToBankParams.SIC_CODE.key, businessCustomerPortfolio.sicCode)
                .put(NewToBankParams.PHYSICAL_ADDRESS_COUNTRY.key, businessCustomerPortfolio.physicalAddressCountry)
                .put(NewToBankParams.BUSINESS_BANKER_SITE.key, businessCustomerPortfolio.preferredBranch.siteCode)
                .put(NewToBankParams.TAX_NUMBER.key, businessCustomerPortfolio.taxNumber)
                .put(NewToBankParams.VAT_NUMBER.key, businessCustomerPortfolio.vatNumber)
                .put(NewToBankParams.TRADING_NAME.key, businessCustomerPortfolio.tradingName)
                .put(NewToBankParams.FOREIGN_TRADING.key, businessCustomerPortfolio.foreignTrading.toString())
                .put(NewToBankParams.SHORT_AND_MEDIUM_TERM_FUNDING_REWARDS_VALUE_ADDED_SERVICE.key, customerPortfolioInfo.shortAndMediumTermFundingRewardsValueAddedService)
                .put(NewToBankParams.ASSET_FINANCE_REWARDS_VALUE_ADDED_SERVICE.key, customerPortfolioInfo.assetFinanceRewardsValueAddedService)
                .put(NewToBankParams.MAKE_AND_RECEIVE_PAYMENTS_REWARDS_VALUE_ADDED_SERVICE.key, customerPortfolioInfo.makeAndReceivePaymentsRewardsValueAddedService3)
                .put(NewToBankParams.SAVING_AND_INVESTMENT_PRODUCTS_REWARDS_VALUE_ADDED_SERVICE.key, customerPortfolioInfo.savingAndInvestmentProductsRewardsValueAddedService4)

        if (BuildConfig.TOGGLE_DEF_SELFIE_PRIVACY_CONSENT_ENABLED || FeatureSwitchingCache.featureSwitchingToggles.newToBankSelfiePrivacyIndicator == FeatureSwitchingStates.ACTIVE.key) {
            newToBankRequest.put(NewToBankParams.USESELFIE.key, customerPortfolioInfo.useSelfie.toString())
        }

        params = newToBankRequest.build()

        mockResponseFile = "new_to_bank/op2023_create_customer_profile.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = CreateCustomerPortfolioAccountResponse::class.java as Class<T>

    override fun isEncrypted(): Boolean? = true
}