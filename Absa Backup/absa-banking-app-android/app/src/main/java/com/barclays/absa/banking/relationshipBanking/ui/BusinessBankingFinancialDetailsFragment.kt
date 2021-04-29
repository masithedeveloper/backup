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

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.BusinessBankingFinancialDetailsFragmentBinding
import com.barclays.absa.banking.newToBank.NewToBankView
import com.barclays.absa.banking.newToBank.dto.CustomerPortfolioInfo
import com.barclays.absa.banking.newToBank.services.dto.CodesLookupDetailsSelector
import com.barclays.absa.banking.presentation.shared.ExtendedFragment
import com.barclays.absa.banking.relationshipBanking.services.dto.FilteredListObject
import com.barclays.absa.banking.relationshipBanking.ui.BusinessBankingFilteredListFragment.Companion.SELECTED_ITEM
import kotlinx.android.synthetic.main.business_banking_financial_details_fragment.*
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher

class BusinessBankingFinancialDetailsFragment : ExtendedFragment<BusinessBankingFinancialDetailsFragmentBinding>() {

    private var selectedSourceOfFund: String = ""
    private lateinit var newToBankBusinessView: NewToBankView
    private var selectedSourceOfIncome: String = ""
    private var selectedEmploymentType: String = ""
    private var selectedOccupationalType: String = ""
    private var selectedMedicalSpecialisation: String = ""
    private var occupationCode: String = "0"
    private var medicalSpecialisationCode = ""

    companion object {
        const val SOURCE_OF_INCOME_CODE: Int = 1000
        const val EMPLOYMENT_TYPE_CODE: Int = 2000
        const val OCCUPATIONAL_TYPE_CODE: Int = 3000
        const val MEDICAL_SPECIALISATION_TYPE_CODE = 4000
        const val BUSINESS_FUNDING_CODE = 5000
    }

    override fun getToolbarTitle(): String = getString(R.string.relationship_banking_about_your_business)

    override fun getLayoutResourceId(): Int = R.layout.business_banking_financial_details_fragment

    override fun onAttach(context: Context) {
        super.onAttach(context)
        newToBankBusinessView = activity as NewToBankView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        newToBankBusinessView.trackSoleProprietorCurrentFragment("SoleProprietor_AboutYourBusinessFinancialDetailsScreen_ScreenDisplayed")
        setUpViews()
        attachEventHandlers()
    }

    private fun attachEventHandlers() {
        newToBankBusinessView.apply {
            sourceOfIncomeNormalInputView.apply {
                addRequiredValidationHidingTextWatcher()
                setOnClickListener { navigateToFilteredList(onClickSourceOfIncome()) }
            }

            employmentTypeNormalInputView.apply {
                addRequiredValidationHidingTextWatcher()
                setOnClickListener { navigateToFilteredList(onClickEmploymentType()) }
            }

            occupationNormalInputView.apply {
                addRequiredValidationHidingTextWatcher()
                setOnClickListener { navigateToFilteredList(onClickOccupationalType()) }
            }

            medicalSpecialisationNormalInputView.apply {
                addRequiredValidationHidingTextWatcher()
                setOnClickListener { navigateToFilteredList(onClickMedicalSpecialisation()) }
            }

            businessFundedNormalInputView.apply {
                addRequiredValidationHidingTextWatcher()
                setOnClickListener { navigateToFilteredList(onClickBusinessFunding()) }
            }

            taxNumberNormalInputView.addRequiredValidationHidingTextWatcher()
            taxRegisteredRadioButtonView.setItemCheckedInterface { index -> taxNumberNormalInputView.visibility = if (index == 0) View.VISIBLE else View.GONE }
            foreignTaxRegisteredRadioButtonView.setItemCheckedInterface { index -> countryNormalInputView.visibility = if (index == 0) View.VISIBLE else View.GONE }

            nextButton.setOnClickListener {
                if (validateFields()) {
                    val customerPortfolioInfo = newToBankTempData.newToBankIncomeDetails?.run {
                        occupation = if (occupationNormalInputView.visibility == View.VISIBLE) occupationCode else "0"
                        medicalCode = if (medicalSpecialisationNormalInputView.visibility == View.VISIBLE) medicalSpecialisationCode else ""
                        registeredForTax = taxRegisteredRadioButtonView.selectedIndex == 0
                        foreignTaxResident = foreignTaxRegisteredRadioButtonView.selectedIndex == 0
                        foreignCountry = if (foreignTaxRegisteredRadioButtonView.selectedIndex == 0) (countryNormalInputView.selectedItem as CodesLookupDetailsSelector).itemCode else ""

                        CustomerPortfolioInfo().apply {
                            occupationStatus = employmentTypeCode
                            occupationCode = occupation
                            sourceOfIncome = sourceOfIncomeCode
                            productType = newToBankTempData.selectedBusinessEvolveProduct.identifier
                        }
                    }
                    newToBankTempData.businessCustomerPortfolio.taxNumber = taxNumberNormalInputView.selectedValue
                    requestCasaRiskStatus(customerPortfolioInfo)
                    trackSoleProprietorCurrentFragment("SoleProprietor_AboutYourBusinessFinancialDetailsScreen_NextButtonClicked")
                }
            }
        }
    }

    private fun onClickSourceOfIncome() =
            FilteredListObject(newToBankBusinessView.newToBankTempData.sourceOfIncomeList).apply {
                titleText = getString(R.string.relationship_banking_income)
                description = getString(R.string.relationship_banking_where_does_income_come_from)
                callingFragment = this@BusinessBankingFinancialDetailsFragment
                normalInputViewRequestCode = SOURCE_OF_INCOME_CODE
                hasOptionsMenu = false
            }

    override fun onResume() {
        super.onResume()
        sourceOfIncomeNormalInputView.selectedValue = selectedSourceOfIncome

        if (selectedEmploymentType.isNotEmpty()) {
            medicalSpecialisationNormalInputView.visibility = View.GONE
            newToBankBusinessView.newToBankTempData.newToBankIncomeDetails?.employmentTypeCode?.let {
                toggleOccupationVisibility(it)
            }
            employmentTypeNormalInputView.selectedValue = selectedEmploymentType
        }

        if (selectedOccupationalType.isNotEmpty()) {
            toggleMedicalSpecialisationVisibility()
            occupationNormalInputView.selectedValue = selectedOccupationalType
        }

        medicalSpecialisationNormalInputView.selectedValue = selectedMedicalSpecialisation
        businessFundedNormalInputView.selectedValue = selectedSourceOfFund
    }

    private fun onClickEmploymentType() =
            FilteredListObject(newToBankBusinessView.newToBankTempData.employmentStatusList).apply {
                titleText = getString(R.string.relationship_banking_occupational_status)
                description = getString(R.string.relationship_banking_select_occupational_status)
                callingFragment = this@BusinessBankingFinancialDetailsFragment
                normalInputViewRequestCode = EMPLOYMENT_TYPE_CODE
                hasOptionsMenu = false
            }


    private fun onClickOccupationalType() =
            FilteredListObject(newToBankBusinessView.newToBankTempData.occupationCodeList).apply {
                titleText = getString(R.string.relationship_banking_occupation)
                description = getString(R.string.relationship_banking_what_is_occupational_status)
                callingFragment = this@BusinessBankingFinancialDetailsFragment
                normalInputViewRequestCode = OCCUPATIONAL_TYPE_CODE
                hasOptionsMenu = false

            }

    private fun onClickMedicalSpecialisation() =
            FilteredListObject(newToBankBusinessView.newToBankTempData.medicalOccupationList).apply {
                titleText = getString(R.string.relationship_banking_my_specialisation)
                callingFragment = this@BusinessBankingFinancialDetailsFragment
                normalInputViewRequestCode = MEDICAL_SPECIALISATION_TYPE_CODE
                hasOptionsMenu = false
            }


    private fun onClickBusinessFunding() =
            FilteredListObject(newToBankBusinessView.newToBankTempData.sourceOfFundsList).apply {
                titleText = getString(R.string.relationship_banking_income)
                description = getString(R.string.relationship_banking_how_your_business_funded)
                callingFragment = this@BusinessBankingFinancialDetailsFragment
                normalInputViewRequestCode = BUSINESS_FUNDING_CODE
                hasOptionsMenu = false
            }

    private fun setUpViews() {
        handleToolbar()
        val selectorList = SelectorList<StringItem>().apply {
            add(StringItem(getString(R.string.new_to_bank_yes)))
            add(StringItem(getString(R.string.new_to_bank_no)))
        }

        taxRegisteredRadioButtonView.apply {
            setDataSource(selectorList)
            selectedIndex = 1
        }

        foreignTaxRegisteredRadioButtonView.apply {
            setDataSource(selectorList)
            selectedIndex = 1
        }
        countryNormalInputView.setList(newToBankBusinessView.newToBankTempData.nationalityList, getString(R.string.relationship_banking_country_registered_for_tax))
    }

    fun validateFields(): Boolean {
        when {
            sourceOfIncomeNormalInputView.selectedValue.isBlank() -> sourceOfIncomeNormalInputView.setError(getString(R.string.relationship_banking_please_select_income))
            businessFundedNormalInputView.selectedValue.isBlank() -> businessFundedNormalInputView.setError(getString(R.string.relationship_banking_please_select_business_funding))
            employmentTypeNormalInputView.selectedValue.isBlank() -> employmentTypeNormalInputView.setError(getString(R.string.relationship_banking_please_select_employment_type))
            occupationNormalInputView.visibility == View.VISIBLE && occupationNormalInputView.selectedValue.isBlank() -> occupationNormalInputView.setError(getString(R.string.relationship_banking_please_select_occupation))
            medicalSpecialisationNormalInputView.visibility == View.VISIBLE && medicalSpecialisationNormalInputView.selectedValue.isBlank() -> medicalSpecialisationNormalInputView.setError(getString(R.string.relationship_banking_please_select_occupation))
            taxRegisteredRadioButtonView.selectedIndex == 0 && taxNumberNormalInputView.selectedValue.length < 10 -> taxNumberNormalInputView.setError(getString(R.string.relationship_banking_valid_tax_number))
            foreignTaxRegisteredRadioButtonView.selectedIndex == 0 && countryNormalInputView.selectedValue.isBlank() -> countryNormalInputView.setError(getString(R.string.relationship_banking_please_select_country))
            else -> return true
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        handleToolbar()
        if (resultCode == RESULT_OK) {
            with(newToBankBusinessView.newToBankTempData) {
                data?.getStringExtra(SELECTED_ITEM)?.let { selectedItem ->
                    when (requestCode) {
                        SOURCE_OF_INCOME_CODE -> {
                            with(sourceOfIncomeList.first { it.engCodeDescription == selectedItem }) {
                                selectedSourceOfIncome = engCodeDescription
                                newToBankIncomeDetails?.sourceOfIncomeCode = itemCode
                            }
                        }
                        EMPLOYMENT_TYPE_CODE -> {
                            with(employmentStatusList.first { it.engCodeDescription == selectedItem }) {
                                selectedEmploymentType = engCodeDescription
                                newToBankIncomeDetails?.employmentTypeCode = itemCode
                            }
                        }
                        OCCUPATIONAL_TYPE_CODE -> {
                            with(occupationCodeList.first { it.engCodeDescription == selectedItem }) {
                                selectedOccupationalType = engCodeDescription
                                occupationCode = itemCode
                            }
                        }
                        MEDICAL_SPECIALISATION_TYPE_CODE -> {
                            with(medicalOccupationList.first { it.engCodeDescription == selectedItem }) {
                                selectedMedicalSpecialisation = engCodeDescription
                                medicalSpecialisationCode = itemCode
                            }
                        }
                        BUSINESS_FUNDING_CODE -> {
                            with(sourceOfFundsList.first { it.engCodeDescription == selectedItem }) {
                                selectedSourceOfFund = engCodeDescription
                                sourceOfFundsCode = itemCode
                            }
                        }
                    }
                }
            }
        }
    }

    private fun toggleMedicalSpecialisationVisibility() {
        with(occupationCode) {
            medicalSpecialisationNormalInputView.visibility = if ("99" == this) View.VISIBLE else View.GONE
        }
    }

    private fun toggleOccupationVisibility(employmentTypeCode: String) {
        occupationNormalInputView.visibility = if (employmentTypeCode in arrayOf("01", "02", "03", "08", "09")) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun handleToolbar() {
        newToBankBusinessView.showProgressIndicator()
        setToolBar(getString(R.string.relationship_banking_about_your_business))
    }
}