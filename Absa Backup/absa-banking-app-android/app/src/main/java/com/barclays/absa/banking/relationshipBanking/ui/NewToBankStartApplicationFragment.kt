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
package com.barclays.absa.banking.relationshipBanking.ui

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.newToBank.NewToBankActivity
import com.barclays.absa.banking.newToBank.NewToBankView
import kotlinx.android.synthetic.main.new_to_bank_business_banking_start_application_fragment.*

class NewToBankStartApplicationFragment(val newToBankView: NewToBankView) : BaseFragment(if (newToBankView.isBusinessFlow) R.layout.new_to_bank_business_banking_start_application_fragment else R.layout.new_to_bank_student_account_application_fragment) {

    private lateinit var hostActivity: NewToBankActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as NewToBankActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        newToBankView.hideProgressIndicator()
        newToBankView.apply {
            if (newToBankView.isBusinessFlow) {
                trackSoleProprietorCurrentFragment(if (hostActivity.isFromBusinessAccountActivity) "SoleProprietor_StartApplicationScreen_PostLoginScreenDisplayed" else "SoleProprietor_StartApplicationScreen_PreLoginScreenDisplayed")
            } else {
                trackStudentAccount("StudentAccount_StartApplicationScreen_ScreenDisplayed")
            }

            if (hostActivity.isFromBusinessAccountActivity) {
                toolbarTitle = getString(R.string.relationship_banking_application)
            } else {
                setToolbarBackTitle(getString(R.string.relationship_banking_application))
            }
            showToolbar()
        }

        startApplicationButton.setOnClickListener {
            if (newToBankView.isBusinessFlow || newToBankView.isStudentFlow) {
                newToBankView.apply {
                    if (newToBankView.isBusinessFlow) {
                        trackSoleProprietorCurrentFragment("SoleProprietor_StartApplicationScreen_StartApplicationButtonClicked")
                    } else {
                        trackStudentAccount("StudentAccount_StartApplicationScreen_StartApplicationButtonClicked")
                    }
                    navigateToVerifyIdentityFragment()
                }
            } else {
                newToBankView.navigateSelectCurrentLocationFragment()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.cancel_menu, menu)
        menu.findItem(R.id.cancel_menu_item).isVisible = hostActivity.isFromBusinessAccountActivity
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.cancel) {
            hostActivity.showApplicationCancellationDialog()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}