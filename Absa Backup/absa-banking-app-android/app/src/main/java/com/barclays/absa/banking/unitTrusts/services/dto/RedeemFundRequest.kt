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

class RedeemFundRequest<T>(fundRedemptionInfo: FundRedemptionInfo, extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {

    init {
        val redemptionAccountDetail = fundRedemptionInfo.redemptionAccountDetail
        val fund = fundRedemptionInfo.unitTrustFund

        params = RequestParams.Builder(UnitTrustService.OP2096_REDEEM_FUND)
                .put(UnitTrustService.UnitTrustParams.UNIT_TRUST_ACCOUNT_NUMBER.key, fundRedemptionInfo.unitTrustAccountNumber)
                .put(UnitTrustService.UnitTrustParams.UNIT_TRUST_ACCOUNT_HOLDER.key, fundRedemptionInfo.unitTrustAccountHolder)
                .put(UnitTrustService.UnitTrustParams.FUND_NAME.key, fund.fundName)
                .put(UnitTrustService.UnitTrustParams.UNIT_TRUST_FUND_CODE.key, fund.fundCode)
                .put(UnitTrustService.UnitTrustParams.AMOUNT_AVAILABLE_FOR_REDEMPTION.key, fund.fundAvailableBalance)
                .put(UnitTrustService.UnitTrustParams.UNITS_AVAILABLE_FOR_REDEMPTION.key, fund.fundAvailablelUnits)
                .put(UnitTrustService.UnitTrustParams.REDEMPTION_TYPE.key, fundRedemptionInfo.redemptionType)
                .put(UnitTrustService.UnitTrustParams.REDEMPTION_AMOUNT.key, fundRedemptionInfo.redeemAmount)
                .put(UnitTrustService.UnitTrustParams.FUND_UNITS.key, fundRedemptionInfo.fundUnits)
                .put(UnitTrustService.UnitTrustParams.ALL_UNITS.key, fundRedemptionInfo.allUnits)
                .put(UnitTrustService.UnitTrustParams.REDEEM_ALL.key, fundRedemptionInfo.redeemAll)
                .put(UnitTrustService.UnitTrustParams.REDEMPTION_ACCOUNT_NUMBER.key, redemptionAccountDetail.redemptionAccountNumber)
                .put(UnitTrustService.UnitTrustParams.REDEMPTION_ACCOUNT_HOLDER.key, redemptionAccountDetail.redemptionAccountHolder)
                .put(UnitTrustService.UnitTrustParams.ACCOUNT_TYPE.key, redemptionAccountDetail.redemptionAccountType)
                .put(UnitTrustService.UnitTrustParams.REDEMPTION_ACCOUNT_BANK_NAME.key, redemptionAccountDetail.redemptionAccountBankName)
                .put(UnitTrustService.UnitTrustParams.REDEMPTION_ACCOUNT_BANK_CODE.key, redemptionAccountDetail.redemptionAccountBankCode)
                .put(UnitTrustService.UnitTrustParams.CANCEL_DEBIT_ORDER.key, fundRedemptionInfo.cancelDebitOrder)
                .build()

        mockResponseFile = "switch_and_redeem/op2096_redeem_unit_trust_fund_result.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = RedeemFundResponse::class.java as Class<T>

    override fun isEncrypted(): Boolean = true
}