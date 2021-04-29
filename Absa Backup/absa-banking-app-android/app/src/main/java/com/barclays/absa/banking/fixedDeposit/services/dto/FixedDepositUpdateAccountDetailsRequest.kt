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

import com.barclays.absa.banking.fixedDeposit.FixedDepositPayoutDetailsData
import com.barclays.absa.banking.fixedDeposit.services.dto.FixedDepositService.Companion.OP2161_UPDATE_ACCOUNT_DETAILS
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams

class FixedDepositUpdateAccountDetailsRequest<T>(payoutDetailsData: FixedDepositPayoutDetailsData, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder(OP2161_UPDATE_ACCOUNT_DETAILS)
                .put("accountType", payoutDetailsData.accountType)
                .put("accountNo", payoutDetailsData.accountNumber)
                .put("productCode", payoutDetailsData.productCode)
                .put("liqEffDate", payoutDetailsData.liqEffDate)
                .put("perEffDate", payoutDetailsData.perEffDate)
                .put("AutomaticReinvestment", payoutDetailsData.automaticReinvestment)
                .put("reinvestCapInt", payoutDetailsData.reinvestCapInt)
                .put("trgAcc", payoutDetailsData.targetAccountNumber)
                .put("trgAccType", payoutDetailsData.targetAccountType)
                .put("trgClrCode", payoutDetailsData.targetBranchCode)
                .put("trgInstCode", payoutDetailsData.targetInstCode)
                .put("trgStmtRef", payoutDetailsData.targetAccountRef)
                .put("intExtBenInd", payoutDetailsData.intExtBeneficiaryIndicator)
                .put("trgAccountHolderName", payoutDetailsData.targetAccountHolderName)
                .put("termCapDay", payoutDetailsData.termCapDay)
                .put("termNextCapDate", payoutDetailsData.termNextCapDate)
                .put("termCapFrequency", payoutDetailsData.termCapFrequency)
                .put("addPaymentInstruction", payoutDetailsData.addPaymentInstruction)
                .put("addCapitalizationInfo", payoutDetailsData.addCapitalizationInfo)
                .put("addDebitOrderInstruction", payoutDetailsData.addDebitOrderInstruction)
                .build()

        mockResponseFile = "fixed_deposit/op2161_fixed_deposit_update_account_details.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = FixedDepositUpdateAccountDetailsResponse::class.java as Class<T>

    override fun isEncrypted(): Boolean = true
}