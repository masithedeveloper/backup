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

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBApplication
import kotlinx.android.synthetic.main.cash_send_plus_limits_fragment.*
import styleguide.utils.extensions.toRandAmount

class CashSendPlusLimitsFragment : BaseFragment(R.layout.cash_send_plus_limits_fragment) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.manage_cash_send_plus_limit)

        val cachedCashSendPlusData = appCacheService.getCashSendPlusRegistrationStatus()
        cachedCashSendPlusData?.cashSendPlusResponseData?.let {
            cashSendPlusLimitTitleAndDescriptionView.title = it.cashSendPlusLimitAmount.toRandAmount()
            dailyCashSendPlusAvailableTextView.text = "${it.cashSendPlusLimitAmountAvailable.toRandAmount()} ${getString(R.string.available_lower)}"
            dailyCashSendPlusUsedTextView.text = "${it.cashSendPlusLimitAmountUsed.toRandAmount()} ${getString(R.string.used).toLowerCase(BMBApplication.getApplicationLocale())}"
            cashSendPlusLimitProgressIndicatorView.steps = it.cashSendPlusLimitAmount.toIntOrNull() ?: 0
            cashSendPlusLimitProgressIndicatorView.setNextStep(it.cashSendPlusLimitAmountAvailable.toIntOrNull() ?: 0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.text_edit_menu, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_edit) {
            navigate(CashSendPlusLimitsFragmentDirections.actionCashSendPlusLimitsFragmentToEditCashSendPlusLimitsFragment())
        }
        return super.onOptionsItemSelected(item)
    }
}