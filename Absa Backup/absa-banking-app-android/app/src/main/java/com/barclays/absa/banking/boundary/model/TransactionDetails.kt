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
 *
 */
package com.barclays.absa.banking.boundary.model

import com.barclays.absa.banking.framework.data.ResponseObject

data class TransactionDetails(var accountObj: AccountObject? = null,
                              var chequeNotYetClearedDate: String? = null,
                              var viewType: String? = null,
                              var fromDate: String? = null,
                              var toDate: String? = null,
                              var startBalance: Amount? = null,
                              var finishBalance: Amount? = null,
                              var moneyOut: Amount? = null,
                              var withdrawals: Amount? = null,
                              var purchases: Amount? = null,
                              var payments: Amount? = null,
                              var scheduledPayments: Amount? = null,
                              var otherAmounts: Amount? = null,
                              var otherInAmounts: Amount? = null,
        //Other Out
                              var otherOutAmounts: Amount? = null,
                              var moneyIn: Amount? = null,
                              var transfers: Amount? = null,
                              var deposits: Amount? = null,
                              var returnedTrans: Amount? = null,
                              var withdrawalsTransactionList: List<Transaction>? = null,
                              var purchasesTransactionList: List<Transaction>? = null,
                              var paymentsTransactionList: List<Transaction>? = null,
                              var scheduledPaymentsTransactionList: List<Transaction>? = null,
                              var otherInAmountsTransactionList: List<Transaction>? = null,
                              var otherOutAmountsTransactionList: List<Transaction>? = null,
                              var transfersTransactionList: List<Transaction>? = null,
                              var depositsTransactionList: List<Transaction>? = null,
                              var returnedTransactionList: List<Transaction>? = null) : ResponseObject()