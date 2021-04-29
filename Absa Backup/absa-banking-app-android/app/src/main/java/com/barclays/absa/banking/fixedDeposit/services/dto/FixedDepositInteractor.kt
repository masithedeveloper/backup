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
import com.barclays.absa.banking.framework.AbstractInteractor
import com.barclays.absa.banking.framework.ExtendedResponseListener

class FixedDepositInteractor : AbstractInteractor(), FixedDepositService {

    override fun fetchAccountDetail(accountNumber: String, accountDetailResponseListener: ExtendedResponseListener<FixedDepositAccountDetailsResponse>) {
        val accountDetailsRequest = FixedDepositGetAccountDetailRequest(accountNumber, accountDetailResponseListener)
        submitRequest(accountDetailsRequest)
    }

    override fun fetchInterestRateInfo(fetchInterestRateInfoResponseListener: ExtendedResponseListener<FixedDepositInterestRateInfoResponse>) {
        val interestRateInfoRequest = FixedDepositInterestRateInfoRequest(fetchInterestRateInfoResponseListener)
        submitRequest(interestRateInfoRequest)
    }

    override fun createAccount(createAccountResponseListener: ExtendedResponseListener<FixedDepositCreateAccountResponse>) {
        val createAccountRequest = FixedDepositCreateAccountRequest(createAccountResponseListener)
        submitRequest(createAccountRequest)
    }

    override fun createAccountConfirm(createAccountConfirmResponseListener: ExtendedResponseListener<FixedDepositCreateAccountConfirmResponse>) {
        val createAccountConfirmRequest = FixedDepositCreateAccountConfirmRequest(createAccountConfirmResponseListener)
        submitRequest(createAccountConfirmRequest)
    }

    override fun createAccountProcess(fixedDepositData: FixedDepositData, sureCheckAccepted: Boolean, createAccountProcessResponseListener: ExtendedResponseListener<FixedDepositCreateAccountProcessResponse>) {
        val createAccountProcessRequest = FixedDepositCreateAccountProcessRequest(fixedDepositData, sureCheckAccepted, createAccountProcessResponseListener)
        submitRequest(createAccountProcessRequest)
    }

    override fun requestAccountUpdate(fixedDepositPayoutDetailsData: FixedDepositPayoutDetailsData, updateAccountDetailsResponseListener: ExtendedResponseListener<FixedDepositUpdateAccountDetailsResponse>) {
        submitRequest(FixedDepositUpdateAccountDetailsRequest(fixedDepositPayoutDetailsData, updateAccountDetailsResponseListener))
    }

    override fun requestRenewalInstruction(accountNumber: String, renewalInstructionResponseListener: ExtendedResponseListener<FixedDepositRenewalInstructionResponse>) {
        submitRequest(FixedDepositRenewalInstructionRequest(accountNumber, renewalInstructionResponseListener))
    }

    override fun requestRenewalInstructionConfirmation(renewalInstructionData: FixedDepositRenewalInstructionData, confirmRenewalInstructionResponseListener: ExtendedResponseListener<FixedDepositConfirmRenewalInstructionResponse>) {
        submitRequest(FixedDepositConfirmRenewalInstructionRequest(renewalInstructionData, confirmRenewalInstructionResponseListener))
    }

    override fun createRenewalInstruction(renewalInstructionData: FixedDepositRenewalInstructionData, createRenewalInstructionResponseListener: ExtendedResponseListener<FixedDepositCreateRenewalInstructionResponse>) {
        submitRequest(FixedDepositCreateRenewalInstructionRequest(renewalInstructionData, createRenewalInstructionResponseListener))
    }
}