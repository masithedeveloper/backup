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

package com.barclays.absa.banking.boundary.model.overdraft

import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import java.io.Serializable

class OverdraftQuoteSummary : TransactionResponse() {
    var quoteDetails: QuoteSummaryDetails? = null

    class QuoteSummaryDetails : Serializable {
        var product: Product? = null
        var quote: Quote? = null
    }

    class Product : Serializable {
        var language: String? = null
        var agreementType: String? = null
        var corpCode: String? = null
    }

    class Quote : Serializable {
        var totalCostOfCredit: String? = "0.00"
        var discContIntRate: String? = "0"
        var contAnnIntRate: String? = "0"
        var creditLifeAggregaged: String? = "0.00"
        var interestAggregated: String? = "0.00"
        var annualMonthlyFee: String? = "0.00"
        var serviceFeeAggragated: String? = "0.00"
        var creditCostMultiple: String? = "0"
        var annualInterestRate: String? = "0"
        var installmentIncludingFees: String? = "0.00"
        var creditAdvancedValueOfGoods: String? = "0.00"
        var initiationFee: String? = "0.00"
        var creditLifeInsuranse: String? = "0.00"
    }

    fun getOverdraftCreditLimit(): String? {
        return quoteDetails?.quote?.creditAdvancedValueOfGoods
    }

    fun getAnnualInterestRate(): String? {
        return quoteDetails?.quote?.annualInterestRate
    }

    fun getContractualAnnualInterestRate(): String? {
        return quoteDetails?.quote?.contAnnIntRate
    }

    fun getDiscountContractualAnnualInterestRate(): String? {
        return quoteDetails?.quote?.discContIntRate
    }

    fun getInitiationFee(): String? {
        return quoteDetails?.quote?.initiationFee
    }

    fun getMonthlyServiceFee(): String? {
        return quoteDetails?.quote?.annualMonthlyFee
    }

    fun getMonthlyRepaymentFee(): String? {
        return quoteDetails?.quote?.installmentIncludingFees
    }

    fun getTotalOverdraftCost(): String? {
        return quoteDetails?.quote?.totalCostOfCredit
    }

    fun getCreditLimit(): String? {
        return quoteDetails?.quote?.creditAdvancedValueOfGoods
    }

    fun getAggregratedInterest(): String? {
        return quoteDetails?.quote?.interestAggregated
    }

    fun getAggregatedMonthlyServiceFee(): String? {
        return quoteDetails?.quote?.serviceFeeAggragated
    }

    fun getAggregatedCreditProtection(): String? {
        return quoteDetails?.quote?.creditLifeAggregaged
    }

    fun getCreditCostMultiple(): String? {
        return quoteDetails?.quote?.creditCostMultiple
    }

    fun getCreditLifeInsurance(): String? {
        return quoteDetails?.quote?.creditLifeInsuranse
    }
}