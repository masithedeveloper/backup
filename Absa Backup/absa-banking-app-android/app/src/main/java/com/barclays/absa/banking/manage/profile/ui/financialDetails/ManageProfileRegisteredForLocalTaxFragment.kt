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

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.manage.profile.ManageProfileFlow
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.NO
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.YES
import com.barclays.absa.banking.manage.profile.ui.models.SouthAfricanTaxDetails
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.manage_profile_registered_for_tax_fragment.*
import styleguide.forms.SelectorList
import styleguide.forms.StringItem

class ManageProfileRegisteredForLocalTaxFragment : ManageProfileRegisteredForTaxBaseFragment() {
    private lateinit var southAfricanTaxDetails: SouthAfricanTaxDetails
    private var reasonForNoTaxNumberCode = ""
    private var reasonForNoTaxNumber = ""

    override fun setToolbar() = setToolBar(R.string.manage_profile_registered_for_tax_south_african_tax_toolbar_title)

    override fun populateData() {
        manageProfileViewModel.manageProfileFlow = ManageProfileFlow.LOCAL_TAX
        registeredForTaxHeading.setHeadingTextView(getString(R.string.manage_profile_registered_for_tax_registered_for_registered_tax_heading))

        initChangeListener()
        initValueChangeListeners()
        initReasons()
        if (manageProfileViewModel.southAfricanTax.value == null) {
            manageProfileViewModel.retrieveSouthAfricanTaxDetails()
        }

        manageProfileViewModel.southAfricanTax.observe(this, {
            southAfricanTaxDetails = it
            populateData(it)
            manageProfileViewModel.southAfricanTax.removeObservers(this)
        })
    }

    private fun initValueChangeListeners() {
        taxNumberNormalInputView.addValueViewTextWatcher(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                continueButton.isEnabled = s?.length == 10 && s.toString() != southAfricanTaxDetails.taxNumber
            }
        })
    }

    private fun populateData(southAfricanTaxDetails: SouthAfricanTaxDetails) {
        if (YES.equals(southAfricanTaxDetails.areYouRegisteredForSouthAfricanTax, true)) {
            registeredForTaxRadioGroup.disableRadioGroup()

            registeredForTaxRadioGroup.selectedIndex = 0
            hasTaxNumberCheckboxView.isChecked = !YES.equals(southAfricanTaxDetails.isTaxNumberAvailable, true)

            if (hasTaxNumberCheckboxView.isChecked && southAfricanTaxDetails.taxNumber.isEmpty() && !manageProfileViewModel.southAfricanTax.value?.reasonForNoTaxNumberCode.isNullOrEmpty()) {
                val indexOfSelectedReasonsForNoTaxNumber = manageProfileViewModel.taxReasons.indexOfFirst { it.reasonCode == manageProfileViewModel.southAfricanTax.value?.reasonForNoTaxNumberCode }
                if (indexOfSelectedReasonsForNoTaxNumber != -1) {
                    reasonNormalInputView.selectedIndex = indexOfSelectedReasonsForNoTaxNumber
                    reasonNormalInputView.selectedValue = manageProfileViewModel.taxReasons[indexOfSelectedReasonsForNoTaxNumber].reasonDescription
                }
            } else if (southAfricanTaxDetails.taxNumber.isNotEmpty()) {
                taxNumberNormalInputView.selectedValue = southAfricanTaxDetails.taxNumber
            }
        } else {
            registeredForTaxRadioGroup.selectedIndex = 1
        }
    }

    private fun initReasons() {
        val noTaxNumberReasonList = SelectorList<StringItem>()
        manageProfileViewModel.taxReasons.forEach {
            noTaxNumberReasonList.add(StringItem(it.reasonDescription))
        }

        reasonNormalInputView.setList(noTaxNumberReasonList, getString(R.string.manage_profile_tax_country_select_no_tax_reason))

        reasonNormalInputView.setItemSelectionInterface { index ->
            reasonForNoTaxNumberCode = manageProfileViewModel.taxReasons[index].reasonCode
            reasonForNoTaxNumber = manageProfileViewModel.taxReasons[index].reasonDescription
            validateInputs()
        }
    }

    private fun initChangeListener() {
        hasTaxNumberCheckboxView.setOnCheckedListener { isChecked ->
            if (isChecked) {
                AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_SouthAfricanTaxScreen_NoTaxNumberCheckBoxChecked")
                reasonNormalInputView.visibility = View.VISIBLE
                taxNumberNormalInputView.visibility = View.GONE
            } else {
                reasonNormalInputView.visibility = View.GONE
                taxNumberNormalInputView.visibility = View.VISIBLE
            }
            validateInputs()
        }
    }

    override fun isRegisteredForTax() {
        AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_SouthAfricanTaxScreen_AreRegisteredForTaxYESRadioButtonChecked")
        hasTaxNumberCheckboxView.visibility = View.VISIBLE
        taxNumberNormalInputView.visibility = View.VISIBLE
        validateInputs()
    }

    override fun isNotRegisteredForTax() {
        AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_SouthAfricanTaxScreen_AreRegisteredForTaxNORadioButtonChecked")
        taxNumberNormalInputView.visibility = View.GONE
        hasTaxNumberCheckboxView.visibility = View.GONE
        reasonNormalInputView.visibility = View.GONE
        continueButton.isEnabled = true
    }

    override fun continueButtonClicked() {
        AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_SouthAfricanTaxScreen_ContinueButtonClicked")
        val reasonForNoTaxNumberLocal = if (reasonNormalInputView.visibility == View.VISIBLE) reasonNormalInputView.selectedValue else ""
        val indexOfReason: Int
        manageProfileViewModel.manageProfileFinancialDetailsMetaData.apply {
            isRegisteredForSouthAfricanTax = if (registeredForTaxRadioGroup.selectedIndex == 0) YES else NO
            saIncomeTaxNumber = if (taxNumberNormalInputView.visibility == View.VISIBLE) taxNumberNormalInputView.selectedValue else ""
            isSaTaxNumberAvailable = if (hasTaxNumberCheckboxView.isChecked) NO else YES

            indexOfReason = manageProfileViewModel.taxReasons.indexOfFirst { reasonForNoTaxNumberLocal.equals(it.reasonDescription, true) }
            if (indexOfReason != -1) {
                reasonForNoTaxNumberCode = manageProfileViewModel.taxReasons[indexOfReason].reasonCode
                reasonForNoTaxNumber = manageProfileViewModel.taxReasons[indexOfReason].reasonDescription
            } else if (saIncomeTaxNumber.isNotEmpty()) {
                reasonForNoTaxNumberCode = ""
                reasonForNoTaxNumber = ""
            }

            manageProfileViewModel.southAfricanTaxDetails.let {
                it.areYouRegisteredForSouthAfricanTax = isRegisteredForSouthAfricanTax
                it.taxNumber = saIncomeTaxNumber
                it.reasonForNoTaxNumber = reasonForNoTaxNumber
                it.reasonForNoTaxNumberCode = if (reasonForNoTaxNumber.isNotEmpty()) reasonForNoTaxNumberCode else ""
            }
        }

        manageProfileViewModel.modifySouthAfricanTaxDetails()
    }

    override fun validateInputs() {
        when {
            taxNumberNormalInputView.visibility == View.VISIBLE && taxNumberNormalInputView.selectedValue != southAfricanTaxDetails.taxNumber -> continueButton.isEnabled = true
            reasonNormalInputView.visibility == View.VISIBLE && reasonForNoTaxNumberCode != southAfricanTaxDetails.reasonForNoTaxNumberCode && reasonNormalInputView.selectedValue.isNotEmpty() -> continueButton.isEnabled = true
            else -> continueButton.isEnabled = false
        }
    }
}