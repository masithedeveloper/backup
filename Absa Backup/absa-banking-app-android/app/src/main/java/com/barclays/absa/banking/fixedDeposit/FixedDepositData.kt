/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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

import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.shared.BaseModel

data class FixedDepositData(var name: String = "",
                            var minimumDeposit: String = "",
                            var investmentTerm: String = "",
                            var interestRate: String = "",
                            var interestFrequency: String = "",
                            var interestPaymentDay: String = "",
                            var nextCapDate: String = "",
                            var capFrequencyCode: String = "",
                            var maturityDate: String = "",
                            var accountDescription: String = "",
                            var amount: Amount = Amount(),
                            var fromAccount: String = "",
                            var fromReference: String = "",
                            var toReference: String = "",
                            var payInterestInto: String = "",
                            var sourceOfFunds: String = "",
                            var interestToAccountNumber: String = "",
                            var paymentReference: String = "",
                            var bankName: String = "",
                            var branchCode: String = "",
                            var accountType: String = "",
                            var interestToAccountType: String = "",
                            var displayMaturityDate: String = "",
                            var selectedAbsaAccountType: Int = 0) : BaseModel