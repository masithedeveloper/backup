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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.activityViewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.BusinessBankingBranchFragmentBinding
import com.barclays.absa.banking.newToBank.NewToBankView
import com.barclays.absa.banking.presentation.shared.ExtendedFragment
import com.barclays.absa.banking.relationshipBanking.services.dto.BusinessBankingBranchesSelector
import com.barclays.absa.banking.relationshipBanking.services.dto.FilteredListObject
import kotlinx.android.synthetic.main.business_banking_branch_fragment.*
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher

class SelectBusinessBankingBranchFragment : ExtendedFragment<BusinessBankingBranchFragmentBinding>() {
    private lateinit var selectedBranch: BusinessBankingBranchesSelector
    private lateinit var newToBusinessBankView: NewToBankView
    private val viewModel by activityViewModels<NewToBankBusinessAccountViewModel>()

    override fun getToolbarTitle(): String = getString(R.string.relationship_banking_about_your_business)

    override fun getLayoutResourceId(): Int = R.layout.business_banking_branch_fragment

    override fun onAttach(context: Context) {
        super.onAttach(context)
        newToBusinessBankView = context as NewToBankView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.fetchBusinessBankingBranches()
    }

    override fun onResume() {
        super.onResume()
        if (::selectedBranch.isInitialized) {
            preferredBranchNormalInputView.selectedValue = selectedBranch.displayValue ?: ""
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        handleToolbar()
        preferredBranchNormalInputView.addRequiredValidationHidingTextWatcher()

        viewModel.filteredSiteLiveData.observe(viewLifecycleOwner, { branches ->
            dismissProgressDialog()
            branches?.let {
                newToBusinessBankView.newToBankTempData.businessBankingSiteCodeDetails = viewModel.buildBranchesSelectorList(it)
                val filteredListObject = FilteredListObject(viewModel.buildBranchesSelectorList(it)).apply {
                    titleText = getString(R.string.relationship_banking_business_banking_branch)
                    description = getString(R.string.relationship_banking_please_select_preferred_branch)
                    callingFragment = this@SelectBusinessBankingBranchFragment
                    hasOptionsMenu = true
                }
                preferredBranchNormalInputView.setOnClickListener { newToBusinessBankView.navigateToFilteredList(filteredListObject) }
            }
        })

        nextButton.setOnClickListener {
            if (validateBranch()) {
                newToBusinessBankView.apply {
                    trackSoleProprietorCurrentFragment("SoleProprietor_PreferredBranchScreen_NextButtonClicked")
                    navigateToFinancialDetailsFragment()
                }
            }
        }
        newToBusinessBankView.trackSoleProprietorCurrentFragment("SoleProprietor_PreferredBranchScreen_ScreenDisplayed")
    }

    private fun validateBranch() = if (preferredBranchNormalInputView.selectedValue.isEmpty()) {
        preferredBranchNormalInputView.setError(getString(R.string.relationship_banking_please_select_a_branch))
        false
    } else {
        true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.cancel_menu, menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        handleToolbar()
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data?.let {
                val selectedBranchText = data.getStringExtra(BusinessBankingFilteredListFragment.SELECTED_ITEM)
                newToBusinessBankView.newToBankTempData.businessBankingSiteCodeDetails.forEach {
                    if (selectedBranchText == it.displayValue) {
                        selectedBranch = it
                    }
                }
                newToBusinessBankView.newToBankTempData.businessCustomerPortfolio.preferredBranch = selectedBranch.siteFilteredDetails
            }
        }
    }

    private fun handleToolbar() {
        newToBusinessBankView.apply {
            showToolbar()
            setToolbarBackTitle(getString(R.string.relationship_banking_about_your_business))
            showProgressIndicator()
        }
    }
}