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
import com.barclays.absa.banking.framework.app.BMBConstants.YES
import com.barclays.absa.banking.unitTrusts.services.UnitTrustService

class SwitchUnitTrustFundRequest<T>(fundSwitchInfo: FundSwitchInfo, extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {

    init {
        val builder = RequestParams.Builder("OP2097")
                .put(UnitTrustService.UnitTrustParams.SWITCH_UNIT_TRUST_ACCOUNT_NUMBER.key, fundSwitchInfo.unitTrustAccountNumber)
                .put(UnitTrustService.UnitTrustParams.SWITCH_UNIT_TRUST_ACCOUNT_HOLDER_NAME.key, fundSwitchInfo.unitTrustAccountHolderName)
                .put(UnitTrustService.UnitTrustParams.SWITCH_FROM_FUND_CODE.key, fundSwitchInfo.fromFundCode)
                .put(UnitTrustService.UnitTrustParams.SWITCH_TO_FUND_CODE.key, fundSwitchInfo.toFundCode)
                .put(UnitTrustService.UnitTrustParams.SWITCH_FROM_FUND_NAME.key, fundSwitchInfo.fromFundName)
                .put(UnitTrustService.UnitTrustParams.SWITCH_TO_FUND_NAME.key, fundSwitchInfo.toFundName)
                .put(UnitTrustService.UnitTrustParams.SWITCH_OPTION.key, fundSwitchInfo.switchOption)
                .put(UnitTrustService.UnitTrustParams.UNITS_TO_SWITCH.key, fundSwitchInfo.unitsToSwitch)
                .put(UnitTrustService.UnitTrustParams.SWITCH_INCOME_DISTRIBUTION_INDICATOR.key, fundSwitchInfo.incomeDistributionIndicator)

        if ("Y".equals(fundSwitchInfo.incomeDistributionIndicator, true)) {
            builder.put(UnitTrustService.UnitTrustParams.SWITCH_INCOME_DISTRIBUTION_ACCOUNT_NAME.key, fundSwitchInfo.incomeDistributionAccountName)
                    .put(UnitTrustService.UnitTrustParams.SWITCH_INCOME_DISTRIBUTION_BANK_NAME.key, fundSwitchInfo.incomeDistributionBankname)
                    .put(UnitTrustService.UnitTrustParams.SWITCH_INCOME_DISTRIBUTION_BANK_CODE.key, fundSwitchInfo.incomeDistributionBankCode)
                    .put(UnitTrustService.UnitTrustParams.SWITCH_INCOME_DISTRIBUTION_BANK_ACCOUNT.key, fundSwitchInfo.incomeDistributionBankAccount)
                    .put(UnitTrustService.UnitTrustParams.SWITCH_INCOME_DISTRIBUTION_ACCOUNT_TYPE.key, fundSwitchInfo.incomeDistributionAccountType)
        }

        if (YES.equals(fundSwitchInfo.newDebitOrder, true)) {
            builder.put(UnitTrustService.UnitTrustParams.SWITCH_DEBIT_ORDER_OPERATION.key, fundSwitchInfo.debitOrderOperation)
                    .put(UnitTrustService.UnitTrustParams.SWITCH_DEBIT_ORDER_ACCOUNT.key, fundSwitchInfo.debitOrderAccount)
                    .put(UnitTrustService.UnitTrustParams.SWITCH_DEBIT_ORDER_ACCOUNT_TYPE.key, fundSwitchInfo.debitOrderAccountType)
                    .put(UnitTrustService.UnitTrustParams.SWITCH_DEBIT_ORDER_ACCOUNT_NAME.key, fundSwitchInfo.debitOrderAccountName)
                    .put(UnitTrustService.UnitTrustParams.SWITCH_DEBIT_ORDER_BANK.key, fundSwitchInfo.debitOrderBank)
                    .put(UnitTrustService.UnitTrustParams.SWITCH_DEBIT_ORDER_BANK_CODE.key, fundSwitchInfo.debitOrderBankCode)
                    .put(UnitTrustService.UnitTrustParams.SWITCH_DEBIT_ORDER_AMOUNT.key, fundSwitchInfo.debitOrderAmount)
                    .put(UnitTrustService.UnitTrustParams.SWITCH_DEBIT_ORDER_START_MONTH.key, fundSwitchInfo.debitOrderStartMonth)
                    .put(UnitTrustService.UnitTrustParams.SWITCH_DEBIT_ORDER_DAY.key, fundSwitchInfo.debitOrderDay)
                    .put(UnitTrustService.UnitTrustParams.SWITCH_DEBIT_ORDER_PERCENTAGE_INCREASE.key, fundSwitchInfo.debitOrderPercentageIncrease)
                    .put(UnitTrustService.UnitTrustParams.SWITCH_DEBIT_ORDER_INCREASE_EFFECTIVE_DATE.key, fundSwitchInfo.debitOrderIncreaseEffectiveDate)
        }

        params = builder.build()
        mockResponseFile = "switch_and_redeem/op2097_switch_unit_trust_fund.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = SwitchFundResponse::class.java as Class<T>

    override fun isEncrypted(): Boolean? = true
}