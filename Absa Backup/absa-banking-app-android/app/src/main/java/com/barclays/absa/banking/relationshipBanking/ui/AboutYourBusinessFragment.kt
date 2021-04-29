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
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.newToBank.NewToBankActivity
import com.barclays.absa.banking.newToBank.NewToBankView
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.relationshipBanking.services.dto.FilteredListObject
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.fragment_business_banking_about_business.*
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher

class AboutYourBusinessFragment : BaseFragment(R.layout.fragment_business_banking_about_business), View.OnClickListener {

    companion object {
        private const val REQUEST_SIC_CODE = 1000
        private const val REQUEST_CATEGORY_SIC_CODE = 4000
        private const val CATEGORY_AGRICULTURE = "Agriculture hunting forestry and fishing"
    }

    private var categorySicCode: String = ""
    private var subCategorySicCode: String = ""
    private lateinit var newToBankBusinessView: NewToBankView
    private lateinit var newToBankAccountViewModel: NewToBankBusinessAccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newToBankAccountViewModel.fetchSicCodesList("CATEGORY_LIST")
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        newToBankAccountViewModel = (context as NewToBankActivity).viewModel()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        newToBankBusinessView = activity as NewToBankView
        newToBankBusinessView.trackSoleProprietorCurrentFragment("SoleProprietor_AboutYourBusinessScreen_ScreenDisplayed")
        handleToolbar()
        initViews()
    }

    private fun attachDataObservers() {
        newToBankAccountViewModel.sicCodesListLiveData.observe(this, { sicCodeList ->
            newToBankBusinessView.newToBankTempData.sicCodesLookUpDetailsList = newToBankAccountViewModel.buildSicCodesSelectorList(sicCodeList)
        })

        newToBankAccountViewModel.categorySicCodeListLiveData.observe(this, { categorySicCodesList ->
            newToBankBusinessView.newToBankTempData.categorySicCodeDetailList = newToBankAccountViewModel.buildSicCodesSelectorList(categorySicCodesList)

            if (CATEGORY_AGRICULTURE.equals(categorySicCode, true)) {
                FilteredListObject(newToBankAccountViewModel.buildSicCodesSelectorList(categorySicCodesList)).apply {
                    titleText = getString(R.string.relationship_banking_nature_of_business)
                    description = String.format(getString(R.string.relationship_banking_please_select_type, categorySicCode))
                    normalInputViewRequestCode = REQUEST_CATEGORY_SIC_CODE
                    callingFragment = this@AboutYourBusinessFragment
                    hasOptionsMenu = true
                    newToBankBusinessView.navigateToFilteredList(this)
                }
            }
        })
    }

    private fun initViews() {
        CommonUtils.setInputFilter(tradingNormalInputView.editText)
        countryNormalInputView.selectedValue = getString(R.string.relationship_banking_south_africa)
        businessNormalInputView.apply {
            setOnClickListener(this@AboutYourBusinessFragment)
            addRequiredValidationHidingTextWatcher()
        }
        populateRadioButton()

        nextButton.setOnClickListener {
            when (yesNoOptionRadioButtonView.selectedIndex) {
                0 -> newToBankBusinessView.navigateToGenericResultFragment(false, false, getString(R.string.relationship_banking_trading_with_other_country_error), ResultAnimations.generalFailure, getString(R.string.new_to_bank_unsuccessful))
                1 -> {
                    if (validateBusinessIndustry()) {
                        newToBankBusinessView.apply {
                            newToBankTempData.businessCustomerPortfolio.tradingName = tradingNormalInputView.selectedValue
                            navigateToConfirmBusinessAddressFragment()
                        }
                    }
                }
            }
        }
    }

    private fun validateBusinessIndustry() = if (businessNormalInputView.selectedValue.isEmpty()) {
        businessNormalInputView.setError(getString(R.string.relationship_banking_please_select_business_industry))
        false
    } else {
        true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.cancel_menu, menu)
    }

    override fun onResume() {
        super.onResume()
        setBusinessCategoryView()
    }

    private fun setBusinessCategoryView() {
        if (categorySicCode.isNotEmpty()) {
            businessNormalInputView.selectedValue = categorySicCode
            newToBankBusinessView.newToBankTempData.categorySicCodeDetailList.forEach {
                if (businessNormalInputView.selectedValue == it.displayValue) {
                    newToBankBusinessView.newToBankTempData.businessCustomerPortfolio.sicCode = it.itemCode
                    return@forEach
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        handleToolbar()
        if (resultCode == Activity.RESULT_OK) {
            data?.let {
                when (requestCode) {
                    REQUEST_SIC_CODE -> {
                        categorySicCode = it.getStringExtra(BusinessBankingFilteredListFragment.SELECTED_ITEM) ?: ""
                        newToBankAccountViewModel.fetchCategorySicCodesList(categorySicCode.replace(" ", "_").toUpperCase(BMBApplication.getApplicationLocale()))
                    }
                    REQUEST_CATEGORY_SIC_CODE -> {
                        subCategorySicCode = it.getStringExtra(BusinessBankingFilteredListFragment.SELECTED_ITEM) ?: ""
                    }
                }
                it
            }
        }
    }

    private fun handleToolbar() {
        newToBankBusinessView.setToolbarBackTitle(getString(R.string.relationship_banking_about_your_business))
        newToBankBusinessView.showProgressIndicator()
    }

    override fun onDetach() {
        super.onDetach()
        newToBankAccountViewModel.sicCodesListLiveData.removeObservers(this)
        newToBankAccountViewModel.categorySicCodeListLiveData.removeObservers(this)
    }

    private fun populateRadioButton() {
        SelectorList<StringItem>().apply {
            add(StringItem(getString(R.string.new_to_bank_yes)))
            add(StringItem(getString(R.string.new_to_bank_no)))
        }.also {
            yesNoOptionRadioButtonView.apply {
                setDataSource(it)
                selectedIndex = 1
            }
        }
    }

    override fun onClick(view: View?) {
        if (!(newToBankAccountViewModel.sicCodesListLiveData.hasObservers() && newToBankAccountViewModel.categorySicCodeListLiveData.hasObservers())) {
            attachDataObservers()
        }

        FilteredListObject(newToBankBusinessView.newToBankTempData.sicCodesLookUpDetailsList).apply {
            titleText = getString(R.string.relationship_banking_nature_of_business)
            description = getString(R.string.relationship_banking_select_nature_of_business)
            normalInputViewRequestCode = REQUEST_SIC_CODE
            callingFragment = this@AboutYourBusinessFragment
            hasOptionsMenu = true
            newToBankBusinessView.navigateToFilteredList(this)
        }
    }
}