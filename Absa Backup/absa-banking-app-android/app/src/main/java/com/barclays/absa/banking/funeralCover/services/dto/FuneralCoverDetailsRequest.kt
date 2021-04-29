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
package com.barclays.absa.banking.funeralCover.services.dto

import com.barclays.absa.banking.boundary.model.funeralCover.FuneralCoverDetails
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.funeralCover.services.FuneralCoverQuoteService.*

class FuneralCoverDetailsRequest<T>(funeralCoverDetails: FuneralCoverDetails, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {

        val builder = RequestParams.Builder()
                .put(OP0862_APPLY_FOR_FUNERAL_PLAN)
                .put(ACCOUNT_NUMBER, funeralCoverDetails.accountNumber)
                .put(ACC_TYPE, funeralCoverDetails.accountType)
                .put(ANNUAL_ESCALATION_OF_PREMIUM, funeralCoverDetails.yearlyIncrease)
                .put(DAY_OF_DEBIT, funeralCoverDetails.debitDate)
                .put(FAMILY_SELECTED, funeralCoverDetails.familySelected)
                .put(PLAN_CODE, funeralCoverDetails.planCode)
                .put(SELF_COVER_AMOUNT, funeralCoverDetails.mainMemberCover)
                .put(SELF_PREMIUM, funeralCoverDetails.mainMemberPremium)
                .put(SOURCE_OF_FUNDS, funeralCoverDetails.sourceOfFunds.toUpperCase())
                .put(SPOUSE_COVER_AMOUNT, funeralCoverDetails.spouseCover)
                .put(SPOUSE_PLAN_CODE, funeralCoverDetails.spousePlanCode)
                .put(SPOUSE_PREMIUM, funeralCoverDetails.spousePremium)
                .put(TOTAL_COVER_AMOUNT, funeralCoverDetails.totalCoverAmount)
                .put(TOTAL_PREMIUM, funeralCoverDetails.totalMonthlyPremium)
                .put(FAMILY_INITIALS, funeralCoverDetails.familyInitials)
                .put(FAMILY_SURNAME, funeralCoverDetails.familySurname)
                .put(FAMILY_GENDER, funeralCoverDetails.familyGender)
                .put(FAMILY_DATE_OF_BIRTH, funeralCoverDetails.familyDateOfBirth)
                .put(FAMILY_COVER_AMOUNT, funeralCoverDetails.familyCoverAmount)
                .put(FAMILY_PREMIUM, funeralCoverDetails.familyPremium)
                .put(FAMILY_RELATIONSHIP_CODE, funeralCoverDetails.familyRelationshipCode)
                .put(FAMILY_BENEFIT_CODE, funeralCoverDetails.familyBenefitCode)

        funeralCoverDetails.beneficiaryInfo?.let { beneficiaryInfo ->
            builder.put(PAY_TO_TYPE, "Beneficiary")
                    .put(BENEFICIARY_TITLE, beneficiaryInfo.title?.code)
                    .put(BENEFICIARY_FIRST_NAME, beneficiaryInfo.firstName)
                    .put(BENEFICIARY_SURNAME, beneficiaryInfo.surname)
                    .put(BENEFICIARY_DATE_OF_BIRTH, beneficiaryInfo.dateOfBirth)
                    .put(BENEFICIARY_RELATIONSHIP, beneficiaryInfo.relationship?.code)
                    .put(BENEFICIARY_INITIALS, beneficiaryInfo.initials)
        }
        params = builder.build()
        mockResponseFile = "funeral_cover/op0862_apply_for_funeral_plan.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = FuneralCoverDetails::class.java as Class<T>

    override fun isEncrypted(): Boolean? = true
}