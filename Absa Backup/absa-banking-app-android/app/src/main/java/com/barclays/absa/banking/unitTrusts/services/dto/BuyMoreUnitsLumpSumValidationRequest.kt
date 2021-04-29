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
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.unitTrusts.services.UnitTrustService
import com.barclays.absa.banking.unitTrusts.services.UnitTrustService.UnitTrustParams

class BuyMoreUnitsLumpSumValidationRequest<T>(lumpSumInfo: BuyMoreUnitsInfo, extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(UnitTrustService.OP2068_VALIDATE_LUMP_SUM)
                .put(UnitTrustParams.LUMP_SUM_ACCOUNT_HOLDER.key, lumpSumInfo.buyMoreUnitsLumpSumInfo.accountInfo?.accountHolderName)
                .put(UnitTrustParams.LUMP_SUM_ACCOUNT_NUMBER.key, lumpSumInfo.buyMoreUnitsLumpSumInfo.accountInfo?.accountNumber)
                .put(UnitTrustParams.LUMP_SUM_ACCOUNT_TYPE.key, lumpSumInfo.buyMoreUnitsLumpSumInfo.accountInfo?.accountType)
                .put(UnitTrustParams.LUMP_SUM_AMOUNT.key, lumpSumInfo.buyMoreUnitsLumpSumInfo.amount)
                .put(UnitTrustParams.LUMP_SUM_INDICATOR.key, lumpSumInfo.buyMoreUnitsLumpSumInfo.indicator)
                .build()
        mockResponseFile = "unit_trust/op2068_validate_lump_sum.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = ValidationStatus::class.java as Class<T>
    override fun isEncrypted(): Boolean = true
}
