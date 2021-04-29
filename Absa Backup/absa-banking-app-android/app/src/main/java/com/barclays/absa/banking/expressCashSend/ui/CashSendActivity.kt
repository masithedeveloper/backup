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
 */
package com.barclays.absa.banking.expressCashSend.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.beneficiaries.ui.BeneficiaryLandingActivity
import com.barclays.absa.banking.beneficiaries.ui.BeneficiaryType
import com.barclays.absa.banking.cashSendPlus.ui.CashSendPlusCancellationActivity
import com.barclays.absa.banking.cashSendPlus.ui.CashSendPlusLimitsActivity
import com.barclays.absa.banking.databinding.CashSendActivityBinding
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.presentation.shared.bottomSheet.BottomSheet


class CashSendActivity : BaseActivity() {
    private val binding by viewBinding(CashSendActivityBinding::inflate)
    private val cashSendViewModel by viewModels<CashSendViewModel>()

    companion object {
        const val IS_CASH_SEND_PLUS = "isCashSendPlus"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cashSendViewModel.isCashSendPlus.value = intent.extras?.getBoolean(IS_CASH_SEND_PLUS, false) ?: false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu_dark, menu)
        val searchViewItem = menu.findItem(R.id.action_search)
        (searchViewItem.actionView as SearchView).apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    cashSendViewModel.searchString.value = query
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    cashSendViewModel.searchString.value = newText
                    return false
                }
            })
        }

        with(menu.findItem(R.id.action_more)) {
            isVisible = cashSendViewModel.isCashSendPlus.value == true
            setOnMenuItemClickListener {
                openBottomSheet()
                false
            }
        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun navigateToCashSendPlusManageLimits() {
        startActivity(Intent(this, CashSendPlusLimitsActivity::class.java))
    }

    private fun navigateToCashSendBeneficiaries() {
        val intent = Intent(this, BeneficiaryLandingActivity::class.java).apply {
            putExtra(BeneficiaryLandingActivity.TAB_TO_SELECT_EXTRA, BeneficiaryType.BENEFICIARY_TYPE_CASHSEND)
        }
        startActivity(intent)
    }

    private fun navigateToCashSendPlusCancellation() {
        startActivity(Intent(this, CashSendPlusCancellationActivity::class.java))
    }

    private fun openBottomSheet() {
        BottomSheet.Builder(this, R.style.BottomSheet_StyleDialog)
                .title(R.string.manage_cash_send_service)
                .sheet(R.menu.cash_send_plus_bottom_sheet_menu)
                .listener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.manageBeneficiaryMenuItem -> navigateToCashSendBeneficiaries()
                        R.id.manageCashSendPlusLimitMenuItem -> navigateToCashSendPlusManageLimits()
                        R.id.cancelCashSendPlusItemMenu -> navigateToCashSendPlusCancellation()
                        R.id.cancelBottomSheetItemMenu -> {
                        }
                    }
                    false
                }
                .build()
                .show()
    }
}