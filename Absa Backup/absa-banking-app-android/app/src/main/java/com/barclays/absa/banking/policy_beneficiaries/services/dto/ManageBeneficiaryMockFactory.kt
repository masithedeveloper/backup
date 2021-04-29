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

package com.barclays.absa.banking.policy_beneficiaries.services.dto

import com.barclays.absa.banking.ultimateProtector.services.dto.CallType

object ManageBeneficiaryMockFactory {
    fun addBeneficiary(callType: CallType): String {
        return when (callType) {
            CallType.SURECHECKV2_REQUIRED -> "insurance/op0909_add_beneficiary_surecheck_required.json"
            CallType.SURECHECKV2_PASSED -> "insurance/op0909_add_beneficiary_surecheck_passed.json"
            else -> ""
        }
    }

    fun changeBeneficiary(callType: CallType): String {
        return when (callType) {
            CallType.SURECHECKV2_REQUIRED -> "insurance/op0910_change_beneficiary_surecheck_required.json"
            CallType.SURECHECKV2_PASSED -> "insurance/op0910_change_beneficiary_surecheck_passed.json"
            else -> ""
        }
    }

    fun removeBeneficiary(callType: CallType): String {
        return when (callType) {
            CallType.SURECHECKV2_REQUIRED -> "insurance/op0914_remove_beneficiary_surecheck_required.json"
            CallType.SURECHECKV2_PASSED -> "insurance/op0914_remove_beneficiary_surecheck_passed.json"
            else -> ""
        }
    }
}