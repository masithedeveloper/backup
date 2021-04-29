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

package com.barclays.absa.banking.cashSendPlus.services

import com.barclays.absa.banking.beneficiaries.services.dto.UpdateClientAgreementDetailsRequest
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.AbstractInteractor
import com.barclays.absa.banking.framework.ExtendedResponseListener

class CashSendPlusInteractor : AbstractInteractor(), CashSendPlusService {

    override fun sendCashSendPlusRegistration(cashSendPlusLimitAmount: String, cashSendPlusEmailAddress: String, responseListener: ExtendedResponseListener<CashSendPlusRegistrationResponse>) {
        submitRequest(CashSendPlusRegistrationRequest(cashSendPlusLimitAmount, cashSendPlusEmailAddress, responseListener))
    }

    override fun sendUpdateCashSendPlusLimit(newCashSendPlusLimitAmount: String, cashSendPlusLimitAmount: String, responseListener: ExtendedResponseListener<UpdateCashSendPlusLimitResponse>) {
        submitRequest(UpdateCashSendPlusLimitRequest(newCashSendPlusLimitAmount, cashSendPlusLimitAmount, responseListener))
    }

    override fun sendCashSendPlusRegistrationCancellation(responseListener: ExtendedResponseListener<CancelCashSendPlusRegistrationResponse>) {
        submitRequest(CancelCashSendPlusRegistrationRequest(responseListener))
    }

    override fun sendCheckCashSendPlusRegistration(responseListener: ExtendedResponseListener<CheckCashSendPlusRegistrationStatusResponse>) {
        submitRequest(CheckCashSendPlusRegistrationStatusRequest(responseListener))
    }

    override fun sendCheckCashSendPlusValidateSendMultiple(cashSendPlusBeneficiaries: String, responseListener: ExtendedResponseListener<CashSendPlusSendMultipleResponse>) {
        submitRequest(ValidateCashSendPlusSendMultipleRequest(cashSendPlusBeneficiaries, responseListener))
    }

    override fun sendCheckCashSendPlusSendMultiple(cashSendPlusBeneficiaries: String, responseListener: ExtendedResponseListener<CashSendPlusSendMultipleResponse>) {
        submitRequest(CashSendPlusSendMultipleRequest(cashSendPlusBeneficiaries, responseListener))
    }

    override fun updateClientAgreementDetails(responseListener: ExtendedResponseListener<TransactionResponse>) {
        submitRequest(UpdateClientAgreementDetailsRequest(responseListener))
    }
}