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
package com.barclays.absa.banking.moneyMarket.service

import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.AbstractInteractor
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.data.ResponseObject

interface MoneyMarketService {
    fun snoozeMoneyMarketBannerStatus(accountNumber: String, responseListener: ExtendedResponseListener<TransactionResponse>)
    fun convertMoneyMarketAccount(accountNumber: String, responseListener: ExtendedResponseListener<SureCheckResponse>)
    fun logMoneyMarketAction(action: String, accountNumber: String, responseListener: ExtendedResponseListener<ResponseObject>)
    fun withdrawMoneyMarketFunds(sourceAccountNumber: String, destinationAccountNumber: String, responseListener: ExtendedResponseListener<SureCheckResponse>)
}

class MoneyMarketInteractor : AbstractInteractor(), MoneyMarketService {
    companion object {
        const val OP2199_GET_ORBIT_STATUS = "OP2199"
        const val OP2200_SNOOZE_ORBIT_SERVICE = "OP2200"
        const val OP2201_CONVERT_ORBIT = "OP2201"
        const val OP2202_LOG_ORBIT = "OP2202"
        const val OP2203_WITHDRAW_ORBIT = "OP2203"
    }

    override fun snoozeMoneyMarketBannerStatus(accountNumber: String, responseListener: ExtendedResponseListener<TransactionResponse>) = submitRequest(MoneyMarketSnoozeRequest(accountNumber, responseListener))

    override fun convertMoneyMarketAccount(accountNumber: String, responseListener: ExtendedResponseListener<SureCheckResponse>) = submitRequest(MoneyMarketConvertAccountRequest(accountNumber, responseListener))

    override fun logMoneyMarketAction(action: String, accountNumber: String, responseListener: ExtendedResponseListener<ResponseObject>) = submitRequest(MoneyMarketLogActionRequest(action, accountNumber, responseListener))

    override fun withdrawMoneyMarketFunds(sourceAccountNumber: String, destinationAccountNumber: String, responseListener: ExtendedResponseListener<SureCheckResponse>) = submitRequest(MoneyMarketWithdrawRequest(sourceAccountNumber, destinationAccountNumber, responseListener))
}