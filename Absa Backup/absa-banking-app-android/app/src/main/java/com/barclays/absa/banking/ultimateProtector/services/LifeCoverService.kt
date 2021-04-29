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
package com.barclays.absa.banking.ultimateProtector.services

import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.funeralCover.services.dto.RetailAccountsResponse
import com.barclays.absa.banking.ultimateProtector.services.dto.CallType
import com.barclays.absa.banking.ultimateProtector.services.dto.LifeCoverApplicationResult
import com.barclays.absa.banking.ultimateProtector.services.dto.LifeCoverInfo
import com.barclays.absa.banking.ultimateProtector.services.dto.Quotation

interface LifeCoverService {
    companion object {
        const val OP2078_RETRIEVE_QUOTE = "OP2078"
        const val OP2079_APPLY_FOR_ULTIMATE_PROTECTOR = "OP2079"
    }

    enum class UltimateProtectorParams(val key: String) {
        PRODUCT_NAME("productName"),
        BENEFIT_CODE("benefitCode"),
        MONTHLY_PREMIUM("monthlyPremium"),
        MEDICAL_QUESTION_ONE("medicalQuestionOne"),
        MEDICAL_QUESTION_TWO("medicalQuestionTwo"),
        MEDICAL_QUESTION_THREE("medicalQuestionThree"),
        PAY_TO("payTo"),
        BENEFICIARY_TITLE("beneficiaryTitle"),
        BENEFICIARY_INITIALS("beneficiaryInitials"),
        BENEFICIARY_FIRSTNAME("beneficiaryFirstName"),
        BENEFICIARY_LASTNAME("beneficiarySurName"),
        BENEFICIARY_RELATIONSHIP("beneficiaryRelationship"),
        BENEFICIARY_DATE_OF_BIRTH("beneficiaryDateOfBirth"),
        INSURANCE_PRODUCT_TYPE("insuranceProductType"),
        SOURCE_OF_FUND("sourceOfFund"),
        DEFAULT_INSURANCE_PRODUCT_TYPE("ultimateProtector"),
        COVER_AMOUNT("coverAmount"),
        ACCOUNT_NUMBER("accountNumber"),
        ACCOUNT_TYPE("accountType"),
        DAY_OF_DEBIT("dayOfDebit"),
    }

    fun fetchLifeCoverQuotation(benefitCode: String, quotationExtendedResponseListener: ExtendedResponseListener<Quotation>)
    fun fetchRetailAccount(retailAccountsExtendedResponseListener: ExtendedResponseListener<RetailAccountsResponse>)
    fun applyForLifeCover(lifeCoverInfo: LifeCoverInfo, lifeCoverApplicationExtendedResponseListener: ExtendedResponseListener<LifeCoverApplicationResult>, callType: CallType)
}