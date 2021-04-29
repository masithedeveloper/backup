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

package com.barclays.absa.banking.cashSendPlus.ui

import android.content.Context
import androidx.annotation.LayoutRes
import androidx.fragment.app.activityViewModels
import com.barclays.absa.banking.framework.BaseFragment

abstract class CashSendPlusBaseFragment(@LayoutRes layout: Int) : BaseFragment(layout) {
    lateinit var cashSendPlusActivity: CashSendPlusRegistrationActivity
    val cashSendPlusViewModel by activityViewModels<CashSendPlusViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        cashSendPlusActivity = context as CashSendPlusRegistrationActivity
    }
}