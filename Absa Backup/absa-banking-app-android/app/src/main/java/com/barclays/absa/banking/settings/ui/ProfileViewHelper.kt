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

package com.barclays.absa.banking.settings.ui

import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity

object ProfileViewHelper {
    fun getCustomerAccountType(context: BaseActivity): String = context.getString(if (context.isBusinessAccount) R.string.business_banking_title else R.string.personal_banking_title)
}
