/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.explore.services.dto

import android.os.Parcel
import android.os.Parcelable
import com.barclays.absa.banking.boundary.model.FuneralCoverQuotesListObject
import com.barclays.absa.banking.boundary.model.OverdraftGadgets
import com.barclays.absa.banking.businessBanking.services.BusinessBankingAccountData
import com.barclays.absa.banking.card.services.card.dto.ManageCardResponseObject
import com.barclays.absa.banking.card.services.card.dto.creditCard.BureauDataApplicationResponse
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardGadget
import com.barclays.absa.banking.card.services.card.dto.creditCard.VCLParcelableModel
import com.barclays.absa.banking.directMarketing.services.dto.MarketingIndicators
import com.barclays.absa.banking.flexiFuneral.services.dto.FlexiFuneralData
import com.barclays.absa.banking.framework.KParcelable
import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.banking.freeCover.ui.FreeCoverData
import com.barclays.absa.banking.lawForYou.services.dto.ApplyLawForYou
import com.barclays.absa.banking.personalLoan.services.CreditLimitsResponse
import com.barclays.absa.banking.ultimateProtector.services.dto.UltimateProtectorData
import com.barclays.absa.banking.unitTrusts.services.dto.FuneralData
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustData
import com.fasterxml.jackson.annotation.JsonProperty

class OffersResponseObject() : ResponseObject(), KParcelable {
    var cliLimitsAndIncomeExpense: ManageCardResponseObject.CLIIncomeData? = null
    var bureauData: BureauDataApplicationResponse? = BureauDataApplicationResponse()
    var displayCreditCardGadget: CreditCardGadget? = null
    var overdraftGadgets: OverdraftGadgets? = null
    var applyUnitTrustData: UnitTrustData? = null
    var applyFuneralData: FuneralData? = FuneralData()
    var policyFee: String? = ""
    var hasUnitTrustAccount: Boolean? = true
    var allFundsMinLumpSumAmt: Double? = 0.0
    var allFundsMinDebitOrderAmt: Double? = 0.0
    var validateClientStatus: Boolean? = true
    var insurancePolicyHeaders: List<FuneralCoverQuotesListObject?>? = ArrayList()
    var applyUltimateProtectorData: UltimateProtectorData? = UltimateProtectorData()
    var marketingIndicators: MarketingIndicators? = MarketingIndicators()
    var personalLoanData: CreditLimitsResponse = CreditLimitsResponse()
    var showApplyCreditCard: Boolean? = false
    var applyLawForYou: ApplyLawForYou = ApplyLawForYou()
    var applyFlexiFuneralData: FlexiFuneralData = FlexiFuneralData()

    @JsonProperty("freeCover")
    var freeCoverData: FreeCoverData = FreeCoverData()

    @JsonProperty("applyBusinessBankAccountData")
    var businessAccountData: BusinessBankingAccountData = BusinessBankingAccountData()
    var applyBusinessBankOverdraft: BusinessBankOverdraftData = BusinessBankOverdraftData()

    constructor(parcel: Parcel) : this() {
        cliLimitsAndIncomeExpense = parcel.readParcelable(ManageCardResponseObject.CLIIncomeData::class.java.classLoader)
        bureauData = parcel.readParcelable(BureauDataApplicationResponse::class.java.classLoader)
        displayCreditCardGadget = parcel.readParcelable(CreditCardGadget::class.java.classLoader)
        overdraftGadgets = parcel.readParcelable(OverdraftGadgets::class.java.classLoader)
        applyUnitTrustData = parcel.readParcelable(UnitTrustData::class.java.classLoader)
        applyFuneralData = parcel.readParcelable(FuneralData::class.java.classLoader)
        policyFee = parcel.readString()
        hasUnitTrustAccount = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        allFundsMinLumpSumAmt = parcel.readValue(Int::class.java.classLoader) as? Double
        allFundsMinDebitOrderAmt = parcel.readValue(Int::class.java.classLoader) as? Double
        validateClientStatus = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        insurancePolicyHeaders = parcel.createTypedArrayList(FuneralCoverQuotesListObject)
        applyUltimateProtectorData = parcel.readParcelable(UltimateProtectorData::class.java.classLoader)
        marketingIndicators = parcel.readParcelable(MarketingIndicators::class.java.classLoader)
        parcel.readParcelable<CreditLimitsResponse>(CreditLimitsResponse::class.java.classLoader)?.let { personalLoanData = it }
        showApplyCreditCard = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        parcel.readParcelable<ApplyLawForYou>(ApplyLawForYou::class.java.classLoader)?.let { applyLawForYou = it }
        parcel.readParcelable<FlexiFuneralData>(FlexiFuneralData::class.java.classLoader)?.let {
            applyFlexiFuneralData = it
        }
        parcel.readParcelable<FreeCoverData>(FreeCoverData::class.java.classLoader)?.let { freeCoverData = it }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.apply {
            writeParcelable(cliLimitsAndIncomeExpense, flags)
            writeParcelable(bureauData, flags)
            writeParcelable(displayCreditCardGadget, flags)
            writeParcelable(overdraftGadgets, flags)
            writeParcelable(applyUnitTrustData, flags)
            writeParcelable(applyFuneralData, flags)
            writeString(policyFee)
            writeValue(hasUnitTrustAccount)
            writeValue(allFundsMinLumpSumAmt)
            writeValue(allFundsMinDebitOrderAmt)
            writeValue(validateClientStatus)
            writeTypedList(insurancePolicyHeaders)
            writeParcelable(applyUltimateProtectorData, flags)
            writeParcelable(marketingIndicators, flags)
            writeParcelable(personalLoanData, flags)
            writeValue(showApplyCreditCard)
            writeParcelable(applyLawForYou, flags)
            writeParcelable(applyFlexiFuneralData, flags)
            writeParcelable(freeCoverData, flags)
        }
    }

    companion object CREATOR : Parcelable.Creator<OffersResponseObject> {
        override fun createFromParcel(parcel: Parcel): OffersResponseObject {
            return OffersResponseObject(parcel)
        }

        override fun newArray(size: Int): Array<OffersResponseObject?> {
            return arrayOfNulls(size)
        }
    }

    fun buildVCLModel(): VCLParcelableModel {
        val vclParcelableModel = VCLParcelableModel()
        vclParcelableModel.populateInstalmentAmounts()
        vclParcelableModel.creditCardVCLGadget = displayCreditCardGadget
        vclParcelableModel.isFreshBureauDataAvailable = displayCreditCardGadget?.isBureauDataAvailableFromCams
        vclParcelableModel.creditCardNumber = displayCreditCardGadget?.creditCardNumber
        vclParcelableModel.currentCreditLimit = displayCreditCardGadget?.existingCreditCardLimit
        vclParcelableModel.newCreditLimitAmount = displayCreditCardGadget?.creditLimitIncreaseAmount
        vclParcelableModel.incomeAndExpense = displayCreditCardGadget?.incomeAndExpenses
        return vclParcelableModel
    }
}