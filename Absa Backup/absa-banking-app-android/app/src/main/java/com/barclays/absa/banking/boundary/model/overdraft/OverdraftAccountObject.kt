/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.boundary.model.overdraft

import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.framework.app.BMBApplication
import styleguide.forms.SelectorInterface

class OverdraftAccountObject(var accountObject: AccountObject?) : SelectorInterface {

    override val displayValue: String?
        get() = accountObject?.accountInformation ?: ""

    override val displayValueLine2: String?
        get() = String.format("%s\n%s: %s", accountObject?.accountNumberFormatted, BMBApplication.getInstance().topMostActivity.getString(R.string.account_to_pay_from_avlbl_blnc), accountObject?.availableBalance.toString())

}