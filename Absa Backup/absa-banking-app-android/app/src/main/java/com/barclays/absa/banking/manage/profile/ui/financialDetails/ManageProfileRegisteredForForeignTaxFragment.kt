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

import android.view.View
import androidx.core.view.size
import com.barclays.absa.banking.R
import com.barclays.absa.banking.manage.profile.ManageProfileFlow
import com.barclays.absa.banking.manage.profile.services.dto.FinancialInformation
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.NO
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.YES
import com.barclays.absa.banking.manage.profile.ui.models.ForeignTaxDisplayValues
import com.barclays.absa.banking.manage.profile.ui.models.OriginalForeignTaxDisplayValues
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.manage_profile_registered_for_tax_fragment.*
import styleguide.forms.ItemSelectionInterface

class ManageProfileRegisteredForForeignTaxFragment : ManageProfileRegisteredForTaxBaseFragment(), ItemSelectionInterface {
    private lateinit var manageProfileAdapter: ManageProfileCountryAdapter<ForeignTaxDisplayValues>
    private lateinit var financialDetails: FinancialInformation
    private lateinit var unmodifiedForeignTaxDisplayValues: ArrayList<OriginalForeignTaxDisplayValues>
    private lateinit var currentForeignTaxDisplayValues: ArrayList<ForeignTaxDisplayValues>

    companion object {
        const val POSITION = "position"
    }

    override fun setToolbar() = setToolBar(R.string.manage_profile_registered_for_tax_foreign_tax_toolbar_title)

    override fun populateData() {
        continueButton.isEnabled = false
        manageProfileViewModel.manageProfileFlow = ManageProfileFlow.FOREIGN_TAX
        registeredForTaxHeading.setHeadingTextView(getString(R.string.manage_profile_financial_details_are_you_registered_title))

        if (manageProfileViewModel.foreignTaxDetails.value == null) {
            manageProfileViewModel.populateEmptyForeignDetails()
        }

        manageProfileViewModel.customerInformation.value?.customerInformation?.financialInformation?.let { financialDetails = it }

        initRecyclerView()
        initOnClickListeners()

        if (manageProfileViewModel.originalDisplayForeignTaxDetails.size == 4) {
            addCountryOptionActionButton.visibility = View.GONE
        }
    }

    override fun isRegisteredForTax() {
        addCountryOptionActionButton.visibility = View.VISIBLE
        taxCountriesRecyclerView.visibility = View.VISIBLE
        continueButton.isEnabled = (financialDetails.areYouRegisteredForeignTax != if (registeredForTaxRadioGroup.selectedIndex == 0) YES else NO) && taxCountriesRecyclerView.size > 0
    }

    override fun isNotRegisteredForTax() {
        addCountryOptionActionButton.visibility = View.GONE
        taxCountriesRecyclerView.visibility = View.GONE
        continueButton.isEnabled = financialDetails.areYouRegisteredForeignTax != if (registeredForTaxRadioGroup.selectedIndex == 0) YES else NO
    }

    private fun initRecyclerView() {
        manageProfileViewModel.foreignTaxDetails.observe(viewLifecycleOwner, { foreignTaxDetails ->
            currentForeignTaxDisplayValues = foreignTaxDetails
            manageProfileAdapter = ManageProfileCountryAdapter(foreignTaxDetails, this@ManageProfileRegisteredForForeignTaxFragment)
            when {
                foreignTaxDetails.size == 4 -> {
                    showAndPopulateRecyclerView()
                    addCountryOptionActionButton.visibility = View.GONE
                }
                foreignTaxDetails.size > 0 -> showAndPopulateRecyclerView()
                else -> registeredForTaxRadioGroup.selectedIndex = 1
            }
            manageProfileViewModel.foreignTaxDetails.removeObservers(viewLifecycleOwner)
            unmodifiedForeignTaxDisplayValues = manageProfileViewModel.originalDisplayForeignTaxDetails

            validateInputs()
        })
    }

    private fun showAndPopulateRecyclerView() {
        registeredForTaxRadioGroup.selectedIndex = 0
        taxCountriesRecyclerView.adapter = manageProfileAdapter
    }

    override fun onItemClicked(position: Int) {
        navigate(ManageProfileRegisteredForForeignTaxFragmentDirections.actionManageProfileRegisteredForForeignTaxFragmentToManageProfileEditForeignTaxDetailsFragment(position))
    }

    override fun continueButtonClicked() {
        var isTaxCountryListEmpty = false
        currentForeignTaxDisplayValues.forEach {
            isTaxCountryListEmpty = it.taxNumber.isBlank() && it.taxCountryCode.isBlank()
        }

        AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_ForeignTaxScreen_ContinueButtonClicked")
        if (registeredForTaxRadioGroup.selectedIndex == 0 && !isTaxCountryListEmpty) {
            manageProfileViewModel.manageProfileFinancialDetailsMetaData.isRegisteredForForeignTax = YES
        } else {
            manageProfileViewModel.manageProfileFinancialDetailsMetaData.isRegisteredForForeignTax = NO
        }
    }

    private fun initOnClickListeners() {
        addCountryOptionActionButton.setOnClickListener {
            navigate(ManageProfileRegisteredForForeignTaxFragmentDirections.actionManageProfileRegisteredForForeignTaxFragmentToManageProfileEditForeignTaxDetailsFragment(-1))
        }
    }

    override fun validateInputs() {
        for ((index, foreignTaxDisplayValues) in currentForeignTaxDisplayValues.withIndex()) {
            if (index < unmodifiedForeignTaxDisplayValues.size) {
                when {
                    foreignTaxDisplayValues.taxNumber != unmodifiedForeignTaxDisplayValues[index].taxNumber -> continueButton.isEnabled = true
                    foreignTaxDisplayValues.reasonForNoTaxNumberCode != unmodifiedForeignTaxDisplayValues[index].reasonForNoTaxNumberCode -> continueButton.isEnabled = true
                    foreignTaxDisplayValues.taxCountryCode != unmodifiedForeignTaxDisplayValues[index].taxCountryCode -> continueButton.isEnabled = true
                }
            } else {
                continueButton.isEnabled = true
            }
        }
        if (unmodifiedForeignTaxDisplayValues.size != currentForeignTaxDisplayValues.size) {
            continueButton.isEnabled = true
        }
    }
}