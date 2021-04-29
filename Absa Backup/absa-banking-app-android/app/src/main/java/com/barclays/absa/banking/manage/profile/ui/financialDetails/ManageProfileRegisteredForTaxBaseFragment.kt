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
 */

package com.barclays.absa.banking.manage.profile.ui.financialDetails

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.manage.profile.ManageProfileFlow
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import kotlinx.android.synthetic.main.manage_profile_registered_for_tax_fragment.*
import styleguide.forms.SelectorList
import styleguide.forms.StringItem

abstract class ManageProfileRegisteredForTaxBaseFragment : ManageProfileBaseFragment(R.layout.manage_profile_registered_for_tax_fragment) {
    abstract fun populateData()
    abstract fun setToolbar()
    abstract fun isRegisteredForTax()
    abstract fun isNotRegisteredForTax()
    abstract fun validateInputs()
    abstract fun continueButtonClicked()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manageProfileViewModel.manageProfileFlow = ManageProfileFlow.UPDATE_FINANCIAL_DETAILS
        setToolbar()
        populateData()

        initData()
        setOnChangeListeners()
        initOnClickListeners()
    }

    private fun setOnChangeListeners() {
        registeredForTaxRadioGroup.setItemCheckedInterface { index ->
            if (index == 0) {
                isRegisteredForTax()
            } else {
                isNotRegisteredForTax()
            }
        }
    }

    private fun initData() {
        val radioGroupOptions: SelectorList<StringItem> = SelectorList()
        radioGroupOptions.add(StringItem(getString(R.string.yes)))
        radioGroupOptions.add(StringItem(getString(R.string.no)))

        registeredForTaxRadioGroup.setDataSource(radioGroupOptions)
    }

    private fun initOnClickListeners() {
        continueButton.setOnClickListener {
            continueButtonClicked()
            if (manageProfileViewModel.manageProfileFlow == ManageProfileFlow.FOREIGN_TAX) {
                navigate(ManageProfileRegisteredForForeignTaxFragmentDirections.actionManageProfileRegisteredForForeignTaxFragmentToMangeProfileTaxDetailsConfirmationFragment2())
            } else {
                navigate(ManageProfileRegisteredForLocalTaxFragmentDirections.actionManageProfileRegisteredForLocalTaxFragmentToMangeProfileTaxDetailsConfirmationFragment())
            }
        }
    }
}