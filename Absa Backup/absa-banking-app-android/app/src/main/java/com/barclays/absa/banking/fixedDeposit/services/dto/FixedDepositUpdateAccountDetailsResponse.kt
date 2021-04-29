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

import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.barclays.absa.banking.shared.BaseModel

class FixedDepositUpdateAccountDetailsResponse : SureCheckResponse() {
    var product: Product = Product()
}

data class Product(
        var accountNo: String = "",
        var accountTypeDescription: String = "",
        var accountTypeId: String = "",
        var accountTypeName: String = "",
        var automaticReinvestment: Boolean = false,
        var capitalizationInfo: CapitalizationInfo? = CapitalizationInfo(),
        var debitOrderInst: DebitOrderInst? = DebitOrderInst(),
        var interestPaymentInst: InterestPaymentInst? = InterestPaymentInst(),
        var liqEffDate: String = "",
        var perEffDate: String = "",
        var productCode: String = "",
        var reinvestCapInt: String = "") : BaseModel

data class CapitalizationInfo(
        var noticeCapDay: String = "",
        var noticeCapFrequency: String = "",
        var noticeNextCapDate: String = "",
        var noticeNoticePeriod: String = "",
        var noticeinterestRate: String = "",
        var noticewithdrawalPerc: String = "",
        var savingCapDay: String = "",
        var savingCapFrequency: String = "",
        var savingNextCapDate: String = "",
        var termCapDay: String = "",
        var termCapFrequency: String = "",
        var termNextCapDate: String = "") : BaseModel

data class DebitOrderInst(
        var amount: String = "",
        var endDate: String = "",
        var freq: String = "",
        var intExtBenInd: String = "",
        var nextPaymentDate: String = "",
        var payDay: String = "",
        var remainingPayments: String = "",
        var srcAcc: String = "",
        var srcAccType: String = "",
        var srcClrCode: String = "",
        var srcInstCode: String = "",
        var srcStmtRef: String = "",
        var startDate: String = "",
        var trgAcc: String = "",
        var trgAccountHolderName: String = "",
        var trgClrCode: String = "",
        var trgInstCode: String = "",
        var trgStmtRef: String = "") : BaseModel

data class InterestPaymentInst(
        var intExtBenInd: String = "",
        var trgAcc: String = "",
        var trgAccType: String = "",
        var trgAccountHolderName: String = "",
        var trgClrCode: String = "",
        var trgInstCode: String = "",
        var trgStmtRef: String = "") : BaseModel