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

import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.ExtendedResponseListener

interface CashSendPlusService {

    companion object {
        const val OP2170_REGISTER_FOR_CASH_SEND_PLUS = "OP2170"
        const val OP2171_UPDATE_CASH_SEND_PLUS_LIMIT = "OP2171"
        const val OP2172_CANCEL_CASH_SEND_PLUS_REGISTRATION = "OP2172"
        const val OP2173_CHECK_CASH_SEND_PLUS_REGISTRATION_STATUS = "OP2173"
        const val OP2196_VALIDATE_CASH_SEND_PLUS_SEND_MULTIPLE = "OP2196"
        const val OP2194_CASH_SEND_PLUS_SEND_MULTIPLE = "OP2194"
    }

    fun sendCashSendPlusRegistration(cashSendPlusLimitAmount: String, cashSendPlusEmailAddress: String, responseListener: ExtendedResponseListener<CashSendPlusRegistrationResponse>)

    fun sendUpdateCashSendPlusLimit(newCashSendPlusLimitAmount: String, cashSendPlusLimitAmount: String, responseListener: ExtendedResponseListener<UpdateCashSendPlusLimitResponse>)

    fun sendCashSendPlusRegistrationCancellation(responseListener: ExtendedResponseListener<CancelCashSendPlusRegistrationResponse>)

    fun sendCheckCashSendPlusRegistration(responseListener: ExtendedResponseListener<CheckCashSendPlusRegistrationStatusResponse>)

    fun sendCheckCashSendPlusValidateSendMultiple(cashSendPlusBeneficiaries: String, responseListener: ExtendedResponseListener<CashSendPlusSendMultipleResponse>)

    fun sendCheckCashSendPlusSendMultiple(cashSendPlusBeneficiaries: String, responseListener: ExtendedResponseListener<CashSendPlusSendMultipleResponse>)

    fun updateClientAgreementDetails(responseListener: ExtendedResponseListener<TransactionResponse>)
}