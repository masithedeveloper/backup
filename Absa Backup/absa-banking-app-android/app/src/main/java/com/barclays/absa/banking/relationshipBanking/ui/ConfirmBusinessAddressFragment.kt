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
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.BusinessBankingConfirmAddressFragmentBinding
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.newToBank.NewToBankActivity
import com.barclays.absa.banking.newToBank.NewToBankPresenter.TECHNICAL_ERROR
import com.barclays.absa.banking.newToBank.NewToBankView
import com.barclays.absa.banking.newToBank.services.dto.AddressDetails
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.shared.ExtendedFragment
import com.barclays.absa.utils.viewModel
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.utils.extensions.toTitleCase

class ConfirmBusinessAddressFragment : ExtendedFragment<BusinessBankingConfirmAddressFragmentBinding>() {

    private lateinit var newToBankBusinessView: NewToBankView
    private lateinit var viewModel: NewToBankBusinessAccountViewModel

    override fun getToolbarTitle(): String = getString(R.string.relationship_banking_about_your_business)

    override fun getLayoutResourceId(): Int = R.layout.business_banking_confirm_address_fragment

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.let {
            viewModel = (context as NewToBankActivity).viewModel()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        newToBankBusinessView = activity as NewToBankView
        newToBankBusinessView.trackSoleProprietorCurrentFragment("SoleProprietor_AboutYourBusinessAddressScreen_ScreenDisplayed")
        newToBankBusinessView.setToolbarBackTitle(toolbarTitle)
        initViews()
        populateRadioButton()

        viewModel.validateAddressTransactionMessage.observe(this, { validateAddressTransactionMessage ->
            newToBankBusinessView.dismissProgressDialog()
            if (TECHNICAL_ERROR.equals(validateAddressTransactionMessage, ignoreCase = true)) {
                newToBankBusinessView.navigateToGenericResultFragment(false, false, TECHNICAL_ERROR, ResultAnimations.generalFailure)
            } else {
                newToBankBusinessView.newToBankTempData.customerDetails.nationalityCode?.let { viewModel.performCasaScreening(it) }
            }
        })

        viewModel.casaScreeningTransactionStatus.observe(this, { casaScreeningTransactionStatus ->
            newToBankBusinessView.dismissProgressDialog()
            if (BMBConstants.SUCCESS.equals(casaScreeningTransactionStatus, ignoreCase = true)) {
                newToBankBusinessView.navigateToSelectPreferredBusinessBankBranchFragment()
            } else {
                newToBankBusinessView.navigateToCasaFailureScreen(false)
            }
        })
    }

    private fun populateRadioButton() {
        val selectorList = SelectorList<StringItem>().apply {
            add(StringItem(getString(R.string.new_to_bank_yes)))
            add(StringItem(getString(R.string.new_to_bank_no)))
        }
        binding.confirmAddressRadioButtonView.setDataSource(selectorList)
        binding.confirmAddressRadioButtonView.selectedIndex = 0
    }

    private fun initViews() {
        val addressDetails: AddressDetails? = newToBankBusinessView.newToBankTempData.addressDetails
        addressDetails?.let {
            val addressLine2 = if (it.addressLine2.isNotBlank()) it.addressLine2.toTitleCase() + ", " else ""
            val residentialAddress = StringBuilder()
                    .append(it.addressLine1.toTitleCase()).append(", ")
                    .append(addressLine2)
                    .append(it.suburb.toTitleCase()).append(", ")
                    .append(it.town.toTitleCase()).append(", ")
                    .append(it.postalCode)
            binding.businessAddressTextView.text = residentialAddress.toString()
        }
        binding.nextButton.setOnClickListener {
            newToBankBusinessView.trackSoleProprietorCurrentFragment("SoleProprietor_AboutYourBusinessAddressScreen_NextButtonClicked")
            when (binding.confirmAddressRadioButtonView.selectedIndex) {
                0 -> {
                    newToBankBusinessView.trackSoleProprietorCurrentFragment("SoleProprietor_AboutYourBusinessAddressScreen_YesClicked")
                    viewModel.validateCustomerBusinessAddress(newToBankBusinessView.newToBankTempData.addressDetails)
                }
                1 -> {
                    newToBankBusinessView.trackSoleProprietorCurrentFragment("SoleProprietor_AboutYourBusinessAddressScreen_NoClicked")
                    newToBankBusinessView.navigateToAddBusinessAddressFragment()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.cancel_menu, menu)
    }

    override fun onDetach() {
        super.onDetach()
        viewModel.validateAddressTransactionMessage.removeObservers(this)
        viewModel.casaScreeningTransactionStatus.removeObservers(this)
    }
}