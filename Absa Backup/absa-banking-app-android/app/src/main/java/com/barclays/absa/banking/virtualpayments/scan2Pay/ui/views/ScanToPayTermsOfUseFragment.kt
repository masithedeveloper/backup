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
package com.barclays.absa.banking.virtualpayments.scan2Pay.ui.views

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import kotlinx.android.synthetic.main.fragment_scan_to_pay_terms_of_use.*

class ScanToPayTermsOfUseFragment : ScanToPayBaseFragment(R.layout.fragment_scan_to_pay_terms_of_use) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setToolBarNoBack(R.string.scan_to_pay_empty)
        continueButton.setOnClickListener {
            scanToPayViewModel.isTermsOfUseAccepted = true
            findNavController().navigateUp()
        }

        scanToPayActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = navigateToHomeScreenWithoutReloadingAccounts()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_close_dark_mode, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuClose) {
            navigateToHomeScreenWithoutReloadingAccounts()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}