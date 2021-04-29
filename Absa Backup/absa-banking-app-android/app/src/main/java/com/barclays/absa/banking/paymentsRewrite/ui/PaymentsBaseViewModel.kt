/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */
package com.barclays.absa.banking.paymentsRewrite.ui

import com.barclays.absa.banking.express.payments.listBranchCodesForBank.dto.BranchCodes
import com.barclays.absa.banking.express.shared.dto.InstructionType
import com.barclays.absa.banking.presentation.shared.beneficiaries.BeneficiariesBaseViewModel

open class PaymentsBaseViewModel : BeneficiariesBaseViewModel() {

    var defaultToFutureDated = false
    var isMultiplePayment = false
    var isBillPayment = false
    var isOnceOffPayment = false
    var isIipAllowed = false
    var hasIIPBeneficiary = false

    fun checkIfIipIsAllowed(selectedBranch: BranchCodes) {
        val finBondEpeBranchCode = "591000"
        isIipAllowed = if (selectedBranch.bankCode == finBondEpeBranchCode) false else selectedBranch.IIPEligible
    }

    fun resetAllStates() {
        isMultiplePayment = false
        isBillPayment = false
        isOnceOffPayment = false
        isIipAllowed = false
        hasIIPBeneficiary = false
    }

    fun updateToBeneficiaryFlow() {
        isOnceOffPayment = false
        selectedBeneficiary.beneficiaryDetails.instructionType = InstructionType.NORMAL
    }

    fun updateToOnceOffFlow() {
        isOnceOffPayment = true
        selectedBeneficiary.beneficiaryDetails.instructionType = InstructionType.ONCE_OFF_PAYMENT
    }
}