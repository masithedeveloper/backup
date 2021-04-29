/*
 *  Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */package com.barclays.absa.banking.rewards.behaviouralRewards.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.express.behaviouralRewards.fetchPersonalDetails.BehaviouralRewardsFetchPersonalDetailsForCustomerViewModel
import com.barclays.absa.banking.express.behaviouralRewards.fetchPersonalDetails.dto.PersonalDetailsResponse
import com.barclays.absa.banking.express.behaviouralRewards.getProgressOfChallenge.BehaviouralRewardsProgressOfChallengeViewModel
import com.barclays.absa.banking.express.behaviouralRewards.shared.Challenge
import com.barclays.absa.banking.express.behaviouralRewards.updatePersonalDetails.BehaviouralRewardsUpdatePersonalDetailsViewModel
import com.barclays.absa.banking.express.behaviouralRewards.updatePersonalDetails.dto.UpdateContactInformation
import com.barclays.absa.banking.express.behaviouralRewards.updatePersonalDetails.dto.UpdatePersonalDetails
import com.barclays.absa.banking.express.codesLookup.CodesLookupTypes
import com.barclays.absa.banking.express.codesLookup.CodesLookupViewModel
import com.barclays.absa.banking.newToBank.services.dto.CodesLookupDetailsSelector
import com.barclays.absa.banking.shared.OnBackPressedInterface
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.behavioural_rewards_update_personal_details_fragment.*
import styleguide.forms.SelectorList
import styleguide.forms.validation.*
import styleguide.utils.extensions.toTenDigitPhoneNumber

class BehaviouralRewardsUpdatePersonalDetailsFragment : BehaviouralRewardsBaseFragment(R.layout.behavioural_rewards_update_personal_details_fragment), OnBackPressedInterface {

    private lateinit var challenge: Challenge
    private lateinit var codesLookUpViewModel: CodesLookupViewModel
    private lateinit var fetchPersonalDetailsForCustomerViewModel: BehaviouralRewardsFetchPersonalDetailsForCustomerViewModel
    private lateinit var updatePersonalDetailsForCustomerViewModel: BehaviouralRewardsUpdatePersonalDetailsViewModel
    private lateinit var behaviouralRewardsProgressOfChallengeViewModel: BehaviouralRewardsProgressOfChallengeViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        behaviouralRewardsProgressOfChallengeViewModel = viewModel()
        updatePersonalDetailsForCustomerViewModel = viewModel()
        fetchPersonalDetailsForCustomerViewModel = viewModel()
        codesLookUpViewModel = viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.behavioural_rewards_personal_details_title))
        showToolBar()

        arguments?.let {
            challenge = BehaviouralRewardsUpdatePersonalDetailsFragmentArgs.fromBundle(it).behaviouralRewardsChallenge
        }

        addValidationRules()
        setUpOnClickListeners()

        fetchPersonalDetailsForCustomerViewModel.fetchPersonalDetails()
        fetchPersonalDetailsForCustomerViewModel.personalDetailsResponseLiveData.observe(viewLifecycleOwner, { personalDetailsResponse ->
            populateViews(personalDetailsResponse)

            codesLookUpViewModel.fetchCodesForType(CodesLookupTypes.MONTHLY_INCOME_RANGE)
            codesLookUpViewModel.codesLookupLiveData.observe(viewLifecycleOwner, {
                val monthlyIncomeList = SelectorList<CodesLookupDetailsSelector>().apply {
                    addAll(it.codesLookupList.map { CodesLookupDetailsSelector(it.engCodeDescription, it.itemCode, it.codesLookupType) })
                }

                monthlyIncomeNormalInputView.setList(monthlyIncomeList, getString(R.string.behavioural_rewards_update_monthly_income))
                var monthlyIncomeCode = personalDetailsResponse.personalDetails.financialInformation.monthlyIncome
                if (monthlyIncomeCode.length == 1) {
                    monthlyIncomeCode = "0$monthlyIncomeCode"
                }

                monthlyIncomeNormalInputView.selectedIndex = monthlyIncomeList.indexOfFirst { it.itemCode == monthlyIncomeCode }

                dismissProgressDialog()
            })
        })

        contactNumberNormalInputView.addRequiredValidationHidingTextWatcher()
        emailAddressNormalInputView.addRequiredValidationHidingTextWatcher()
        monthlyIncomeNormalInputView.addRequiredValidationHidingTextWatcher()

        trackAnalytics("UpdatePersonalDetails_ScreenDisplayed")
    }

    private fun populateViews(personalDetailsResponse: PersonalDetailsResponse) {
        contactNumberNormalInputView.text = personalDetailsResponse.personalDetails.contactInformation.cellphoneNumber.toTenDigitPhoneNumber()
        emailAddressNormalInputView.text = personalDetailsResponse.personalDetails.contactInformation.clientEmailAddress
    }

    private fun setUpOnClickListeners() {
        confirmButton.setOnClickListener {
            if (isInputValid()) {
                val personalDetails = UpdatePersonalDetails().apply {
                    contactInformation = UpdateContactInformation().apply {
                        cellphoneNumber = contactNumberNormalInputView.selectedValueUnmasked
                        clientEmailAddress = emailAddressNormalInputView.selectedValue
                    }
                    monthlyIncomeRange = (monthlyIncomeNormalInputView.selectedItem as CodesLookupDetailsSelector).itemCode
                }
                updatePersonalDetailsForCustomerViewModel.updatePersonalDetails(personalDetails)
                updatePersonalDetailsForCustomerViewModel.updatePersonalDetailsLiveData.observe(viewLifecycleOwner, {
                    dismissProgressDialog()
                    navigate(BehaviouralRewardsUpdatePersonalDetailsFragmentDirections.actionBehaviouralRewardsUpdatePersonalDetailsFragmentToBehaviouralRewardsResultFragment(challenge, getString(R.string.behavioural_rewards_success_description)))
                })
            }

            trackAnalytics("UpdatePersonalDetails_ConfirmButtonClicked")
        }
    }

    private fun addValidationRules() {
        contactNumberNormalInputView.addValidationRules(FieldRequiredValidationRule(R.string.behavioural_rewards_empty_cellphone_number_error), SouthAfricaCellphoneNumberValidation(R.string.behavioural_rewards_invalid_cellphone_number_error))
        emailAddressNormalInputView.addValidationRules(FieldRequiredValidationRule(R.string.behavioural_rewards_empty_email_error), EmailValidationRule(R.string.behavioural_rewards_invalid_email_error))
        monthlyIncomeNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.behavioural_rewards_empty_annual_income_error))
    }

    private fun isInputValid(): Boolean {
        return contactNumberNormalInputView.validate() &&
                emailAddressNormalInputView.validate() &&
                monthlyIncomeNormalInputView.validate()
    }

    override fun onBackPressed(): Boolean {
        trackAnalytics("UpdatePersonalDetails_BackButtonClicked")
        return false
    }
}