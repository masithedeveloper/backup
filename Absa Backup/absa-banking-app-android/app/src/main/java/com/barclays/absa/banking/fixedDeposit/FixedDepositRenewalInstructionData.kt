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

package com.barclays.absa.banking.fixedDeposit

data class FixedDepositRenewalInstructionData(var accountNumber: String = "",
                                              var eventNo: String = "",
                                              var amount: String = "",
                                              var term: String = "",
                                              var startDate: String = "",
                                              var endDate: String = "",
                                              var interestCapFreq: String = "",
                                              var capDay: String = "",
                                              var nextCapDate: String = "",
                                              var productCode: String = "",
                                              var islamicIndicator: String = "",
                                              var fromAccount: String = "",
                                              var fromAccountStatementDescription: String = "",
                                              var toAccountStatementDescription: String = "",
                                              var fundAmount: String = "")