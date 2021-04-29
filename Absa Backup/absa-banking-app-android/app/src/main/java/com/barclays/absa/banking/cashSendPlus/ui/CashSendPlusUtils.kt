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
package com.barclays.absa.banking.cashSendPlus.ui

import com.barclays.absa.banking.cashSendPlus.services.CashSendPlusResponseData
import com.barclays.absa.banking.cashSendPlus.services.CashSendPlusSendMultipleResponse
import com.barclays.absa.banking.framework.app.BMBConstants

object CashSendPlusUtils {
    const val CSP_RETURN_CODE_SUCCESS = "0"
    const val CSP_RETURN_CODE_PENDING_ONLINE = "313"
    const val CSP_RETURN_CODE_DUPLICATE = "317"
    const val CSP_RETURN_CODE_NOT_FOUND = "318"
    const val CSP_RETURN_CODE_PENDING_BRANCH = "319"
    const val CSP_RETURN_CODE_FAILURE = "999"

    const val CSP_AUTH_NOT_OUTSTANDING = "0"
    const val CSP_AUTH_OUTSTANDING = "1"

    fun isSuccess(responseData: CashSendPlusResponseData): Boolean {
        return CSP_RETURN_CODE_SUCCESS.equals(responseData.returnCode, true) && CSP_AUTH_NOT_OUTSTANDING.equals(responseData.cashSendPlusOutstanding, true)
    }

    fun isPendingAuthorization(responseData: CashSendPlusResponseData): Boolean {
        return CSP_RETURN_CODE_SUCCESS.equals(responseData.returnCode, true) && CSP_AUTH_OUTSTANDING.equals(responseData.cashSendPlusOutstanding, true)
    }

    fun cancelInProgress(responseData: CashSendPlusResponseData): Boolean {
        return (CSP_RETURN_CODE_PENDING_ONLINE.equals(responseData.returnCode, true) && CSP_AUTH_NOT_OUTSTANDING.equals(responseData.cashSendPlusOutstanding, true))
    }

    fun inProgress(responseData: CashSendPlusResponseData): Boolean {
        return (CSP_RETURN_CODE_SUCCESS.equals(responseData.returnCode, true) && CSP_AUTH_OUTSTANDING.equals(responseData.cashSendPlusOutstanding, true)) || (CSP_RETURN_CODE_PENDING_ONLINE.equals(responseData.returnCode, true) || CSP_RETURN_CODE_PENDING_BRANCH.equals(responseData.returnCode, true) || CSP_RETURN_CODE_DUPLICATE.equals(responseData.returnCode, true))
    }

    fun isNotFound(responseData: CashSendPlusResponseData): Boolean {
        return CSP_RETURN_CODE_NOT_FOUND.equals(responseData.returnCode, true)
    }

    fun isFailure(responseData: CashSendPlusResponseData): Boolean {
        return CSP_RETURN_CODE_FAILURE.equals(responseData.returnCode, true)
    }

    fun isSendMultipleSuccess(responseData: CashSendPlusSendMultipleResponse): Boolean {
        return BMBConstants.SUCCESS.equals(responseData.transactionStatus, true) && BMBConstants.SUCCESS.equals(responseData.transactionMessage, true)
    }

    fun isSendMultiplePendingAuthorization(responseData: CashSendPlusSendMultipleResponse): Boolean {
        return BMBConstants.SUCCESS.equals(responseData.transactionStatus, true) && BMBConstants.AUTHORISATION_OUTSTANDING_TRANSACTION.equals(responseData.transactionMessage, true)
    }

    fun isSendMultipleFailure(responseData: CashSendPlusSendMultipleResponse): Boolean {
        return BMBConstants.FAILURE.equals(responseData.transactionStatus, true)
    }
}