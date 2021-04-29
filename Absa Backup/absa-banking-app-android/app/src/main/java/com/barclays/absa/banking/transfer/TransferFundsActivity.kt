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
package com.barclays.absa.banking.transfer

import android.os.Bundle
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCard
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.transfer.TransferConstants.Companion.CREDIT_CARD
import com.barclays.absa.banking.transfer.TransferConstants.Companion.FROM_ACCOUNT
import com.barclays.absa.banking.transfer.TransferConstants.Companion.HOME_LOAN_ACCOUNT_OBJECT
import com.barclays.absa.banking.transfer.TransferConstants.Companion.IS_FROM_REWARDS
import com.barclays.absa.banking.transfer.TransferConstants.Companion.MINIMUM_PAYABLE_AMOUNT
import com.barclays.absa.utils.viewModel

class TransferFundsActivity : BaseActivity(R.layout.transfer_funds_activity) {

    private lateinit var transferViewModel: TransferViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transferViewModel = viewModel()

        intent?.apply {
            transferViewModel.fromAccount = getSerializableExtra(FROM_ACCOUNT) as? AccountObject
            transferViewModel.minimumAmount = getSerializableExtra(MINIMUM_PAYABLE_AMOUNT) as? String
            transferViewModel.isFromRewards = getBooleanExtra(IS_FROM_REWARDS, false)

            val homeLoanAccount = getSerializableExtra(HOME_LOAN_ACCOUNT_OBJECT) as? AccountObject
            homeLoanAccount.let { transferViewModel.toAccount = it }

            val avafAccountObject = getSerializableExtra(TransferConstants.AVAF_ACCOUNT_OBJECT) as? AccountObject
            avafAccountObject?.let {
                transferViewModel.isAvafTransfer = true
                transferViewModel.toAccount = avafAccountObject
            }

            val creditCardInformation = getSerializableExtra(CREDIT_CARD) as? CreditCard
            creditCardInformation?.let { it ->
                transferViewModel.toAccount = AccountObject().apply {
                    accountType = "creditCard"
                    accountNumber = it.accountNo
                    description = if (it.accountName.isNullOrEmpty()) it.accountTypeDescription else it.accountName
                    availableBalance = it.availableBalance
                }
            }

        }
    }
}