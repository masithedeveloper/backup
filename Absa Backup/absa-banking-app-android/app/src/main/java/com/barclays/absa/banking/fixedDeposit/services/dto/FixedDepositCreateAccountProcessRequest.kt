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
 */
package com.barclays.absa.banking.fixedDeposit.services.dto

import com.barclays.absa.banking.fixedDeposit.FixedDepositData
import com.barclays.absa.banking.fixedDeposit.services.dto.FixedDepositService.Companion.OP2073_CREATE_ACCOUNT_PROCESS
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams

class FixedDepositCreateAccountProcessRequest<T>(fixedDepositData: FixedDepositData, sureCheckAccepted: Boolean,
                                                 extendedResponseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(extendedResponseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP2073_CREATE_ACCOUNT_PROCESS)
                .put(TransactionParams.Transaction.PRODUCT_CODE, "03040")
                .put(FixedDepositParameters.CATEGORY_CODE, "aoctd002")
                .put(FixedDepositParameters.TYPE, "Investment")
                .put(FixedDepositParameters.SURE_CHECK_ACCEPTED, sureCheckAccepted.toString())
                .put(FixedDepositParameters.NAME, fixedDepositData.name)
                .put(FixedDepositParameters.MINIMUM_DEPOSIT, fixedDepositData.minimumDeposit)
                .put(FixedDepositParameters.AMOUNT, fixedDepositData.amount.amountValue.toString())
                .put(FixedDepositParameters.FROM_ACCOUNT, fixedDepositData.fromAccount)
                .put(FixedDepositParameters.FROM_DESCRIPTION, fixedDepositData.fromReference)
                .put(FixedDepositParameters.TO_DESCRIPTION, fixedDepositData.toReference)
                .put(FixedDepositParameters.SOURCE_OF_FUNDS, fixedDepositData.sourceOfFunds)
                .put(FixedDepositParameters.CAP_FREQUENCY, fixedDepositData.capFrequencyCode)
                .put(FixedDepositParameters.CAP_DAY, fixedDepositData.interestPaymentDay)
                .put(FixedDepositParameters.NEXT_CAP_DATE, fixedDepositData.nextCapDate)
                .put(FixedDepositParameters.MATURITY_DATE, fixedDepositData.maturityDate)
                .put(FixedDepositParameters.AUTOMATIC_RENEWAL, "N")
                .put(FixedDepositParameters.MONTHLY_INTEREST_PAYMENTS, "N")
                .put(FixedDepositParameters.INTEREST_TO_ACCOUNT_NO, fixedDepositData.interestToAccountNumber)
                .put(FixedDepositParameters.INTEREST_TO_BANK_NAME, fixedDepositData.bankName)
                .put(FixedDepositParameters.INTEREST_TO_BRANCH_CODE, fixedDepositData.branchCode)
                .put(FixedDepositParameters.INTEREST_TO_ACCOUNT_TYPE, fixedDepositData.interestToAccountType)
                .build()
        mockResponseFile = "fixed_deposit/op0273_fixed_deposit_create_account_process.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T>? = FixedDepositCreateAccountProcessResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}