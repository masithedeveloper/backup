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

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.NO
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.YES
import com.barclays.absa.banking.manage.profile.ui.financialDetails.ManageProfileRegisteredForForeignTaxFragment.Companion.POSITION
import com.barclays.absa.banking.manage.profile.ui.models.ForeignTaxDisplayValues
import com.barclays.absa.banking.shared.services.dto.LookupItem
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.manage_profile_edit_foreign_tax_details_fragment.*
import styleguide.forms.SelectorList
import styleguide.forms.StringItem

class ManageProfileEditForeignTaxDetailsFragment : ManageProfileBaseFragment(R.layout.manage_profile_edit_foreign_tax_details_fragment) {
    private var foreignTaxDisplayValues: ForeignTaxDisplayValues? = ForeignTaxDisplayValues()
    private var index = -1
    private var indexOfReason = 0
    private var selectedReason = ""
    private lateinit var countryList: SelectorList<LookupItem>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.manage_profile_tax_country_toolbar_title)
        index = arguments?.getInt(POSITION) ?: -1
        countryList = manageProfileViewModel.retrieveCountryList()

        countryList.removeAll { it.defaultLabel == getString(R.string.south_africa) }

        initData()
        initCheckBox()
        initButtonClickListeners()
    }

    private fun initData() {
        taxCountryNormalInputView.setList(countryList, getString(R.string.manage_profile_tax_country_tax_country))

        if (index >= 0) {
            foreignTaxDisplayValues = manageProfileViewModel.foreignTaxDetails.value?.get(index)

            taxCountryNormalInputView.selectedValue = foreignTaxDisplayValues?.taxCountry.toString()
            val indexOfCountry = countryList.indexOfFirst { foreignTaxDisplayValues?.taxCountry.equals(it.defaultLabel, true) }
            if (indexOfCountry >= 0) {
                foreignTaxDisplayValues?.taxCountryCode = countryList[indexOfCountry].itemCode.toString()
                taxCountryNormalInputView.selectedIndex = indexOfCountry
            }
            taxNumberNormalInputView.selectedValue = foreignTaxDisplayValues?.taxNumber.toString()
        }

        val noTaxNumberReasonList = SelectorList<StringItem>()
        manageProfileViewModel.taxReasons.forEach {
            noTaxNumberReasonList.add(StringItem(it.reasonDescription))
        }

        reasonNormalInputView.setList(noTaxNumberReasonList, getString(R.string.manage_profile_tax_country_select_no_tax_reason))
        indexOfReason = manageProfileViewModel.taxReasons.indexOfFirst { foreignTaxDisplayValues?.reasonForNoTaxNumber.equals(it.reasonDescription, true) }
        reasonNormalInputView.selectedIndex = indexOfReason
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (index == -1 || manageProfileViewModel.displayForeignTaxDetails.size <= 1) {
            menu.clear()
        } else {
            inflater.inflate(R.menu.remove_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_remove) {
            manageProfileViewModel.displayForeignTaxDetails[index].apply {
                taxNumber = " "
                isTaxNumberAvailable = ""
                reasonForNoTaxNumberCode = ""
                taxCountryCode = " "
                taxCountry = ""
            }

            navigate(ManageProfileEditForeignTaxDetailsFragmentDirections.actionManageProfileEditForeignTaxDetailsFragmentPop())
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initCheckBox() {
        hasForeignTaxNumberCheckboxView.setOnCheckedListener { isChecked ->
            if (isChecked) {
                reasonNormalInputView.visibility = View.VISIBLE
                taxNumberNormalInputView.visibility = View.GONE
            } else {
                reasonNormalInputView.visibility = View.GONE
                taxNumberNormalInputView.visibility = View.VISIBLE
            }
        }

        hasForeignTaxNumberCheckboxView.isChecked = foreignTaxDisplayValues?.isTaxNumberAvailable == "N"
    }

    private fun initButtonClickListeners() {
        doneButton.setOnClickListener {
            if (validateInputs()) {
                foreignTaxDisplayValues?.taxCountry = taxCountryNormalInputView.selectedValueUnmasked
                foreignTaxDisplayValues?.taxNumber = if (taxNumberNormalInputView.visibility == View.VISIBLE) taxNumberNormalInputView.selectedValueUnmasked else ""
                selectedReason = if (reasonNormalInputView.visibility == View.VISIBLE) reasonNormalInputView.selectedValue else ""

                val indexOfCountry = countryList.indexOfFirst { foreignTaxDisplayValues?.taxCountry.equals(it.defaultLabel, true) }
                if (indexOfCountry >= 0) {
                    foreignTaxDisplayValues?.taxCountryCode = countryList[indexOfCountry].itemCode.toString()
                }
                if (reasonNormalInputView.visibility == View.VISIBLE) {
                    val indexOfReason = manageProfileViewModel.taxReasons.indexOfFirst { selectedReason.equals(it.reasonDescription, true) }
                    if (indexOfReason >= 0) {
                        foreignTaxDisplayValues?.reasonForNoTaxNumberCode = manageProfileViewModel.taxReasons[indexOfReason].reasonCode
                        foreignTaxDisplayValues?.reasonForNoTaxNumber = manageProfileViewModel.taxReasons[indexOfReason].reasonDescription
                    }
                } else {
                    foreignTaxDisplayValues?.reasonForNoTaxNumberCode = ""
                    foreignTaxDisplayValues?.reasonForNoTaxNumber = ""
                }

                foreignTaxDisplayValues?.isTaxNumberAvailable = if (hasForeignTaxNumberCheckboxView.isChecked) NO else YES

                if (index != -1) {
                    manageProfileViewModel.displayForeignTaxDetails.removeAt(index)
                }
                foreignTaxDisplayValues?.let { it1 -> manageProfileViewModel.displayForeignTaxDetails.add(it1) }

                AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_TaxCountryInformationScreen_DoneButtonClicked")
                navigate(ManageProfileEditForeignTaxDetailsFragmentDirections.actionManageProfileEditForeignTaxDetailsFragmentPop())
            }
        }
    }

    private fun validateInputs(): Boolean {
        when {
            taxCountryNormalInputView.visibility == View.VISIBLE && taxCountryNormalInputView.selectedValueUnmasked.isEmpty() -> taxCountryNormalInputView.setError(getString(R.string.manage_profile_foreign_tax_enter_valid_tax_country_error))
            taxNumberNormalInputView.visibility == View.VISIBLE && taxNumberNormalInputView.selectedValueUnmasked.isEmpty() -> taxNumberNormalInputView.setError(getString(R.string.mange_profile_foreign_tax_enter_tax_number_error))
            reasonNormalInputView.visibility == View.VISIBLE && reasonNormalInputView.selectedValueUnmasked.isEmpty() -> reasonNormalInputView.setError(getString(R.string.manage_profile_foreign_tax_select_reason_for_no_tax_number_error))
            else -> return true
        }
        return false
    }
}