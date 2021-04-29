/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.cashSend.ui

import com.barclays.absa.banking.R
import com.barclays.absa.banking.express.shared.dto.SourceAccount
import com.barclays.absa.banking.framework.app.BMBApplication
import styleguide.forms.SelectorInterface
import styleguide.utils.extensions.toFormattedAccountNumber
import styleguide.utils.extensions.toRandAmount

class CashSendSelectedAccountWrapper(var sourceAccount: SourceAccount) : SelectorInterface {
    override val displayValue: String
        get() = sourceAccount.accountName
    override val displayValueLine2: String
        get() = "${sourceAccount.accountNumber.toFormattedAccountNumber()}\n${BMBApplication.getInstance().topMostActivity.getString(R.string.account_to_pay_from_avlbl_blnc)} : ${sourceAccount.availableBalance.toRandAmount()}"
}