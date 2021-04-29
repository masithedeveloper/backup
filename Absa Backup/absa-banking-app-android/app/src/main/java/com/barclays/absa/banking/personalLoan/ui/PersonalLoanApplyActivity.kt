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

package com.barclays.absa.banking.personalLoan.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import kotlinx.android.synthetic.main.personal_loan_apply_activity.*

class PersonalLoanApplyActivity : BaseActivity() {
    private lateinit var navHostFragment: Fragment

    companion object {
        const val IS_FROM_BANNER = "is_from_banner"
        const val MAXIMUM_CREDIT_LIMIT_LIST = "maximum_credit_limit_list"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.personal_loan_apply_activity)
        setSupportActionBar(toolbar as Toolbar?)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.personalLoansVclNavHostFragment)!!
    }

    fun setToolBar(title: String) = setToolBarBack(title)

    fun showToolbar() {
        toolbar.visibility = View.VISIBLE
    }

    fun hideToolbar() {
        toolbar.visibility = View.GONE
    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.personalLoansVclNavHostFragment)
        if (navController.currentDestination?.id != R.id.personalLoanGenericResultScreenFragment) {
            super.onBackPressed()
        }
    }
}