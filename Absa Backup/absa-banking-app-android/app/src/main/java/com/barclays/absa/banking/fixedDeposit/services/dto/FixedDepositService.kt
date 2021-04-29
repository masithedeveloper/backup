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
 */

package com.barclays.absa.banking.fixedDeposit.services.dto

import com.barclays.absa.banking.fixedDeposit.FixedDepositData
import com.barclays.absa.banking.fixedDeposit.FixedDepositPayoutDetailsData
import com.barclays.absa.banking.fixedDeposit.FixedDepositRenewalInstructionData
import com.barclays.absa.banking.framework.ExtendedResponseListener

interface FixedDepositService {
    companion object {
        const val OP2070_RETRIEVE_INTEREST_RATE_INFO = "OP2070"
        const val OP2071_CREATE_ACCOUNT = "OP2071"
        const val OP2072_CREATE_ACCOUNT_CONFIRM = "OP2072"
        const val OP2073_CREATE_ACCOUNT_PROCESS = "OP2073"
        const val OP2076_GET_ACCOUNT_DETAIL = "OP2076"
        const val OP2160_FIXED_DEPOSIT_RENEWAL_INSTRUCTION = "OP2160"
        const val OP2161_UPDATE_ACCOUNT_DETAILS = "OP2161"
        const val OP2162_CONFIRM_FIXED_DEPOSIT_RENEWAL_INSTRUCTION = "OP2162"
        const val OP2163_CREATE_FIXED_DEPOSIT_RENEWAL_INSTRUCTION = "OP2163"
    }

    fun fetchInterestRateInfo(fetchInterestRateInfoResponseListener: ExtendedResponseListener<FixedDepositInterestRateInfoResponse>)
    fun fetchAccountDetail(accountNumber: String, accountDetailResponseListener: ExtendedResponseListener<FixedDepositAccountDetailsResponse>)
    fun createAccount(createAccountResponseListener: ExtendedResponseListener<FixedDepositCreateAccountResponse>)
    fun createAccountConfirm(createAccountConfirmResponseListener: ExtendedResponseListener<FixedDepositCreateAccountConfirmResponse>)
    fun createAccountProcess(fixedDepositData: FixedDepositData, sureCheckAccepted: Boolean, createAccountProcessResponseListener: ExtendedResponseListener<FixedDepositCreateAccountProcessResponse>)
    fun requestAccountUpdate(fixedDepositPayoutDetailsData: FixedDepositPayoutDetailsData, updateAccountDetailsResponseListener: ExtendedResponseListener<FixedDepositUpdateAccountDetailsResponse>)
    fun requestRenewalInstruction(accountNumber: String, renewalInstructionResponseListener: ExtendedResponseListener<FixedDepositRenewalInstructionResponse>)
    fun requestRenewalInstructionConfirmation(renewalInstructionData: FixedDepositRenewalInstructionData, confirmRenewalInstructionResponseListener: ExtendedResponseListener<FixedDepositConfirmRenewalInstructionResponse>)
    fun createRenewalInstruction(renewalInstructionData: FixedDepositRenewalInstructionData, createRenewalInstructionResponseListener: ExtendedResponseListener<FixedDepositCreateRenewalInstructionResponse>)
}