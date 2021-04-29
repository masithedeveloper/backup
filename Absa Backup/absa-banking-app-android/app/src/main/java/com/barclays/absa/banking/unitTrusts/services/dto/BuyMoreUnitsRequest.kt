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

package com.barclays.absa.banking.unitTrusts.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.unitTrusts.services.UnitTrustService.Companion.OP2059_BUY_MORE_UNITS
import com.barclays.absa.banking.unitTrusts.services.UnitTrustService.UnitTrustParams


class BuyMoreUnitsRequest<T>(unitTrustAccountInfo: BuyMoreUnitsInfo, extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {
    companion object {
        const val NO = "N"
        const val YES = "Y"
    }

    init {
        val unitTrustFund = unitTrustAccountInfo.fund
        val lumpSumInfo = unitTrustAccountInfo.buyMoreUnitsLumpSumInfo

        params = RequestParams.Builder()
                .put(OpCodeParams.OPCODE_KEY, OP2059_BUY_MORE_UNITS)
                .put(UnitTrustParams.INCOME_DISTRIBUTION_INDICATOR.key, NO)
                .put(UnitTrustParams.TRANSFER_IDENTIFIER_INCOME_DISTRIBUTION.key, YES)
                .put(UnitTrustParams.TRANSFER_IDENTIFIER_BUY_UNIT_TRUST.key, "Buy-More")
                .put(UnitTrustParams.FUND_CODE.key, unitTrustFund.fundCode)
                .put(UnitTrustParams.FUND_NAME.key, unitTrustFund.fundName)
                .put(UnitTrustParams.FUND_NUMBER.key, unitTrustAccountInfo.accountNumber)
                .put(UnitTrustParams.LUMP_SUM_ACCOUNT_HOLDER.key, lumpSumInfo.accountInfo?.accountHolderName)
                .put(UnitTrustParams.LUMP_SUM_ACCOUNT_NUMBER.key, lumpSumInfo.accountInfo?.accountNumber)
                .put(UnitTrustParams.LUMP_SUM_ACCOUNT_TYPE.key, lumpSumInfo.accountInfo?.accountType)
                .put(UnitTrustParams.LUMP_SUM_AMNT.key, lumpSumInfo.amount)
                .put(UnitTrustParams.UNIT_TRUST_ACCOUNT_NAME.key, unitTrustAccountInfo.accountHolder)
                .put(UnitTrustParams.LUMP_SUM_AUTHORISED.key, YES)
                .build()

        mockResponseFile = "unit_trust/op2067_buy_unit_trust_result.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = BuyMoreFundsResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean = true
}

