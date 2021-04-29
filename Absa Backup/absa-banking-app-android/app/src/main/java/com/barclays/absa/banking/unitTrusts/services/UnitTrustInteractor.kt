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

package com.barclays.absa.banking.unitTrusts.services

import com.barclays.absa.banking.framework.AbstractInteractor
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.unitTrusts.services.dto.*
import com.barclays.absa.banking.unitTrusts.ui.buy.UnitTrustFundsAvailableToBuyExtendedResponseListener
import com.barclays.absa.banking.unitTrusts.ui.buy.UnitTrustFundsExtendedResponseListener
import com.barclays.absa.banking.unitTrusts.ui.view.BuyNewFundResponseListener

class UnitTrustInteractor : AbstractInteractor(), UnitTrustService {
    override fun switchUnitTrustFund(fundSwitchInfo: FundSwitchInfo, extendedResponseListener: ExtendedResponseListener<SwitchFundResponse>) {
        val switchUnitTrustFundsRequest = SwitchUnitTrustFundRequest(fundSwitchInfo, extendedResponseListener)
        submitRequest(switchUnitTrustFundsRequest)
    }

    override fun fetchSwitchUnitTrustFunds(extendedResponseListener: ExtendedResponseListener<SwitchFundsListResponse>) {
        val unitTrustFundsRequest = SwitchUnitTrustFundsListRequest(extendedResponseListener)
        submitRequest(unitTrustFundsRequest)
    }

    override fun fetchRedeemFundStatus(fundRedemptionInfo: FundRedemptionInfo, extendedResponseListener: ExtendedResponseListener<RedeemFundResponse>) {
        val redeemFundRequest = RedeemFundRequest(fundRedemptionInfo, extendedResponseListener)
        submitRequest(redeemFundRequest)
    }

    override fun fetchBuyMoreUnitsStatus(buyMoreUnitsInfo: BuyMoreUnitsInfo, sureCheckAccepted: Boolean, extendedResponseListener: ExtendedResponseListener<BuyMoreFundsResponse>) {
        val buyMoreUnitsRequest = BuyMoreUnitsRequest(buyMoreUnitsInfo, extendedResponseListener)
        submitRequest(buyMoreUnitsRequest)
    }

    override fun fetchUnitTrustRedemptionAccountStatus(accountNumber: String, extendedResponseListener: ExtendedResponseListener<UnitsTrustRedemptionAccountResponse>) {
        val unitTrustFundRedemptionRequest = UnitTrustFundRedemptionRequest(accountNumber, extendedResponseListener)
        submitRequest(unitTrustFundRedemptionRequest)
    }

    override fun buyMoreUnitsValidateLumpSumAmount(lumpSumInfo: BuyMoreUnitsInfo, extendedResponseListener: ExtendedResponseListener<ValidationStatus>) {
        val validateLumpSumAmount = BuyMoreUnitsLumpSumValidationRequest(lumpSumInfo, extendedResponseListener)
        submitRequest(validateLumpSumAmount)
    }

    override fun fetchUnitTrustFunds(extendedResponseListener: UnitTrustFundsExtendedResponseListener) {
        val unitTrustFundsRequest = UnitTrustFundsRequest(extendedResponseListener)
        submitRequest(unitTrustFundsRequest)
    }

    override fun fetchUnitTrustFundsAvailableToBuy(unitTrustAccountNumber: String, extendedResponseListener: UnitTrustFundsAvailableToBuyExtendedResponseListener) {
        val availableUnitTrustFundsRequest = UnitTrustFundsAvailableToBuyRequest(unitTrustAccountNumber, extendedResponseListener)
        submitRequest(availableUnitTrustFundsRequest)
    }

    override fun fetchLinkedAccounts(extendedResponseListener: ExtendedResponseListener<LinkedAccountsWrapper>) {
        val linkedAccountsRequest = LinkedAccountsRequest(extendedResponseListener)
        submitRequest(linkedAccountsRequest)
    }

    override fun validateLumpSumAmount(lumpSumInfo: LumpSumInfo, extendedResponseListener: ExtendedResponseListener<ValidationStatus>) {
        val lumpSumValidationRequest = LumpSumValidationRequest(lumpSumInfo, extendedResponseListener)
        submitRequest(lumpSumValidationRequest)
    }

    override fun fetchDebitDays(extendedResponseListener: ExtendedResponseListener<DebitPeriod>) {
        val debitDaysRequest = DebitPeriodRequest(extendedResponseListener)
        submitRequest(debitDaysRequest)
    }

    override fun createUnitTrustAccount(unitTrustAccountInfo: UnitTrustAccountInfo, extendedResponseListener: ExtendedResponseListener<UnitTrustAccountCreationResult>) {
        val unitTrustAccountCreationRequest = UnitTrustAccountCreationRequest(unitTrustAccountInfo, extendedResponseListener)
        submitRequest(unitTrustAccountCreationRequest)
    }

    override fun fetchBuyMoreUnitsCappedStatus(fundCode: String, extendedResponseListener: ExtendedResponseListener<BuyMoreUnitsCappedResponse>) {
        val buyMoreUnitCappedRequest = BuyMoreUnitCappedRequest(fundCode, extendedResponseListener)
        submitRequest(buyMoreUnitCappedRequest)
    }

    override fun fetchBuyMoreUnitsLinkedAccounts(extendedResponseListener: ExtendedResponseListener<BuyMoreUnitsLinkedAccountsResponse>) {
        val linkedAccountsRequest = BuyMoreUnitsLinkedAccountsRequest(extendedResponseListener)
        submitRequest(linkedAccountsRequest)
    }

    override fun buyNewFund(unitTrustAccountInfo: UnitTrustAccountInfo, extendedResponseListener: BuyNewFundResponseListener) {
        val buyNewFundRequest = BuyNewFundRequest(unitTrustAccountInfo, extendedResponseListener)
        submitRequest(buyNewFundRequest)
    }
}
