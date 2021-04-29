/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.fixedDeposit.services.dto

import com.barclays.absa.banking.fixedDeposit.FixedDepositRenewalInstructionData
import com.barclays.absa.banking.fixedDeposit.services.dto.FixedDepositService.Companion.OP2162_CONFIRM_FIXED_DEPOSIT_RENEWAL_INSTRUCTION
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams

class FixedDepositConfirmRenewalInstructionRequest<T>(renewalInstructionData: FixedDepositRenewalInstructionData, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder(OP2162_CONFIRM_FIXED_DEPOSIT_RENEWAL_INSTRUCTION)
                .put("accountNumber", renewalInstructionData.accountNumber)
                .put("eventNo", renewalInstructionData.eventNo)
                .put("amount", renewalInstructionData.amount)
                .put("term", renewalInstructionData.term)
                .put("startDate", renewalInstructionData.startDate)
                .put("endDate", renewalInstructionData.endDate)
                .put("interestCapFreq", renewalInstructionData.interestCapFreq)
                .put("capDay", renewalInstructionData.capDay)
                .put("nextCapDate", renewalInstructionData.nextCapDate)
                .put("productCode", renewalInstructionData.productCode)
                .put("islamicIndicator", renewalInstructionData.islamicIndicator)
                .put("fromAccount", renewalInstructionData.fromAccount)
                .put("fromAccountStatementDescription", renewalInstructionData.fromAccountStatementDescription)
                .put("toAccountStatementDescription", renewalInstructionData.toAccountStatementDescription)
                .put("fundAmount", renewalInstructionData.fundAmount)
                .build()

        mockResponseFile = "fixed_deposit/op2162_fixed_deposit_confirm_renewal_instruction.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = FixedDepositConfirmRenewalInstructionResponse::class.java as Class<T>

    override fun isEncrypted(): Boolean = true
}