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
package com.barclays.absa.banking.ultimateProtector.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.MockFactory
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.ultimateProtector.services.LifeCoverService.Companion.OP2079_APPLY_FOR_ULTIMATE_PROTECTOR
import com.barclays.absa.banking.ultimateProtector.services.LifeCoverService.UltimateProtectorParams.*
import com.barclays.absa.banking.ultimateProtector.services.dto.CallType.SURECHECKV2_PASSED
import com.barclays.absa.banking.ultimateProtector.services.dto.CallType.SURECHECKV2_REQUIRED

class LifeCoverApplicationRequest<T>(lifeCoverInfo: LifeCoverInfo, responseListener: ExtendedResponseListener<T>, callType: CallType) : ExtendedRequest<T>(responseListener) {
    companion object {
        const val YES = "Y"
        const val NO = "N"
    }

    init {
        val beneficiaryInfo = lifeCoverInfo.beneficiaryInfo
        val accountInfo = lifeCoverInfo.accountInfo
        val sourceOfFund = lifeCoverInfo.sourceOfFund
        val builder = RequestParams.Builder()
                .put(OP2079_APPLY_FOR_ULTIMATE_PROTECTOR)
                .put(MONTHLY_PREMIUM.key, lifeCoverInfo.monthlyPremium)
                .put(PAY_TO.key, lifeCoverInfo.payTo)
                .put(INSURANCE_PRODUCT_TYPE.key, DEFAULT_INSURANCE_PRODUCT_TYPE.key)
                .put(SOURCE_OF_FUND.key, sourceOfFund?.itemCode + "-" + sourceOfFund?.defaultLabel)
                .put(COVER_AMOUNT.key, lifeCoverInfo.coverAmount)
                .put(ACCOUNT_NUMBER.key, accountInfo?.accountNumber)
                .put(ACCOUNT_TYPE.key, accountInfo?.accountType)
                .put(DAY_OF_DEBIT.key, lifeCoverInfo.dayOfDebit)
                .put(BENEFIT_CODE.key, lifeCoverInfo.benefitCode)

        lifeCoverInfo.medicalQuestionOne?.let {
            builder.put(MEDICAL_QUESTION_ONE.key, if (it) YES else NO)
        }

        lifeCoverInfo.medicalQuestionTwo?.let {
            builder.put(MEDICAL_QUESTION_TWO.key, if (it) YES else NO)
        }

        lifeCoverInfo.medicalQuestionThree?.let {
            builder.put(MEDICAL_QUESTION_THREE.key, if (it) YES else NO)
        }

        lifeCoverInfo.isBeneficiarySelected?.let {
            if (it) {
                builder.put(BENEFICIARY_TITLE.key, beneficiaryInfo?.title?.code)
                builder.put(BENEFICIARY_INITIALS.key, beneficiaryInfo?.initials)
                builder.put(BENEFICIARY_FIRSTNAME.key, beneficiaryInfo?.firstName)
                builder.put(BENEFICIARY_LASTNAME.key, beneficiaryInfo?.surname)
                builder.put(BENEFICIARY_RELATIONSHIP.key, beneficiaryInfo?.relationship?.code)
                builder.put(BENEFICIARY_DATE_OF_BIRTH.key, beneficiaryInfo?.dateOfBirth)
            }
        }

        mockResponseFile = when (callType) {
            SURECHECKV2_REQUIRED -> MockFactory.lifeCoverApplicationResult(0)
            SURECHECKV2_PASSED -> MockFactory.lifeCoverApplicationResult(1)
            else -> MockFactory.lifeCoverApplicationResult(-1)
        }

        params = builder.build()
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = LifeCoverApplicationResult::class.java as Class<T>
    override fun isEncrypted(): Boolean = true
}