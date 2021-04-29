/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.saveAndInvest

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.SaveAndInvestPersonalInformationFragmentBinding
import com.barclays.absa.banking.express.invest.validatePersonalDetails.PersonalDetailsValidationViewModel
import com.barclays.absa.banking.express.shared.getLookupInfo.LookupViewModel
import com.barclays.absa.banking.express.shared.getLookupInfo.dto.LookupItem
import com.barclays.absa.banking.express.shared.getLookupInfo.dto.LookupRequest
import com.barclays.absa.banking.express.shared.getRiskRating.RiskRatingViewModel
import com.barclays.absa.banking.express.shared.getRiskRating.dto.RiskRatingRequest
import com.barclays.absa.banking.express.shared.updateCustomerDetails.CustomerDetailsUpdateViewModel
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.saveAndInvest.SaveAndInvestRiskBasedHelper.DEFAULT_NO_OCCUPATION_CODE
import com.barclays.absa.banking.shared.services.dto.CIFGroupCode
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.ProfileManager
import com.barclays.absa.utils.extensions.toSelectorList
import com.barclays.absa.utils.extensions.viewBinding
import com.barclays.absa.utils.viewModel
import styleguide.forms.NormalInputView
import styleguide.forms.SelectorInterface
import styleguide.forms.StringItem
import styleguide.forms.validation.FieldRequiredValidationRule
import styleguide.forms.validation.addValidationRule
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toSentenceCase

abstract class SaveAndInvestPersonalInformationFragment : SaveAndInvestBaseFragment(R.layout.save_and_invest_personal_information_fragment) {

    private lateinit var personalDetailsValidationViewModel: PersonalDetailsValidationViewModel
    private lateinit var customerDetailsUpdateViewModel: CustomerDetailsUpdateViewModel
    private lateinit var riskRatingViewModel: RiskRatingViewModel
    private lateinit var lookupViewModel: LookupViewModel
    private lateinit var language: String

    private val binding by viewBinding(SaveAndInvestPersonalInformationFragmentBinding::bind)
    private var sourceOfFundsString = ""
    private var employmentStatusList = listOf<LookupItem>()
    private var occupationList = listOf<LookupItem>()
    private var sourceOfIncomeList = listOf<LookupItem>()
    private var detailsChanged = false
    private var sourceOfFundsCodes = ""

    abstract fun navigateOnSuccess()
    abstract fun navigateOnRiskBasedFailure()
    abstract fun navigateOnFailure()
    abstract fun navigateToSourceOfFunds()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lookupViewModel = viewModel()
        personalDetailsValidationViewModel = viewModel()
        customerDetailsUpdateViewModel = viewModel()
        riskRatingViewModel = viewModel()

        with(saveAndInvestViewModel) {
            selectedOccupationStatus = LookupItem()
            selectedOccupation = LookupItem()
            selectedSourceOfIncome = LookupItem()
            selectedSourceOfFunds = mutableListOf()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.depositor_plus_personal_information)

        language = ProfileManager.getInstance().activeUserProfile?.languageCode.toString()

        if (sourceOfFundsString.isEmpty()) {
            fetchLookupData()
        } else {
            setupSelectorData(CIFGroupCode.OCCUPATION_STATUS, employmentStatusList)
        }

        binding.nextButton.setOnClickListener {
            if (hasValidFields()) {
                if (detailsChanged) {
                    updateCustomerDetails()
                } else {
                    validatePersonalInfo()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupSourceOfFundsSelector()
    }

    private fun fetchLookupData(groupCode: CIFGroupCode = CIFGroupCode.OCCUPATION_STATUS) {
        lookupViewModel.fetchLookupItems(LookupRequest(groupCode.key, language))
        lookupViewModel.lookupItemsLiveData.observe(viewLifecycleOwner, { lookupItems ->
            lookupViewModel.lookupItemsLiveData.removeObservers(viewLifecycleOwner)
            if (!lookupItems.isNullOrEmpty()) {
                setupSelectorData(groupCode, lookupItems)
            } else {
                navigateOnFailure()
                dismissProgressDialog()
            }
        })
    }

    private fun setupSelectorData(groupCode: CIFGroupCode, lookupItems: List<LookupItem>) {
        when (groupCode) {
            CIFGroupCode.OCCUPATION_STATUS -> {
                employmentStatusList = lookupItems.sortedBy { it.value }
                binding.employmentStatusNormalInputView.setupSelector(employmentStatusList, R.string.depositor_plus_employment_status, R.string.depositor_plus_please_select_employment_status) {
                    saveAndInvestViewModel.selectedOccupationStatus = it
                    setOccupationVisibility()
                }

                if (saveAndInvestViewModel.selectedOccupationStatus.code.isBlank()) {
                    saveAndInvestViewModel.selectedOccupationStatus = employmentStatusList.find { lookupItem -> lookupItem.code.trimStart('0') == saveAndInvestViewModel.customerDetails.employmentInfo.occupationStatus } ?: LookupItem()
                    binding.employmentStatusNormalInputView.selectedIndex = employmentStatusList.indexOfFirst { lookupItem -> lookupItem == saveAndInvestViewModel.selectedOccupationStatus }
                }

                setOccupationVisibility()

                if (occupationList.isNullOrEmpty()) {
                    fetchLookupData(CIFGroupCode.OCCUPATION)
                } else {
                    setupSelectorData(CIFGroupCode.OCCUPATION, occupationList)
                }
            }
            CIFGroupCode.OCCUPATION -> {
                occupationList = lookupItems.sortedBy { it.value }
                binding.occupationNormalInputView.setupSelector(occupationList, R.string.depositor_plus_occupation, R.string.depositor_plus_please_select_occupation) {
                    saveAndInvestViewModel.selectedOccupation = it
                }

                if (saveAndInvestViewModel.selectedOccupation.code.isBlank()) {
                    saveAndInvestViewModel.selectedOccupation = occupationList.find { lookupItem -> lookupItem.code.trimStart('0') == saveAndInvestViewModel.customerDetails.employmentInfo.occupation } ?: LookupItem()
                    binding.occupationNormalInputView.selectedIndex = occupationList.indexOfFirst { lookupItem -> lookupItem == saveAndInvestViewModel.selectedOccupation }
                }

                if (saveAndInvestViewModel.sourceOfFundsList.isEmpty()) {
                    fetchLookupData(CIFGroupCode.SOURCE_OF_FUNDS_I)
                } else {
                    setupSelectorData(CIFGroupCode.SOURCE_OF_FUNDS_I, saveAndInvestViewModel.sourceOfFundsList)
                }
            }
            CIFGroupCode.SOURCE_OF_FUNDS_I -> {
                saveAndInvestViewModel.sourceOfFundsList = lookupItems
                setupSourceOfFundsSelector()

                if (sourceOfIncomeList.isNullOrEmpty()) {
                    fetchLookupData(CIFGroupCode.SOURCE_OF_INCOME_I)
                } else {
                    setupSelectorData(CIFGroupCode.SOURCE_OF_INCOME_I, sourceOfIncomeList)
                }
            }
            CIFGroupCode.SOURCE_OF_INCOME_I -> {
                sourceOfIncomeList = lookupItems.sortedBy { it.value }
                binding.sourceOfIncomeNormalInputView.setupSelector(sourceOfIncomeList, R.string.depositor_plus_source_of_income, R.string.depositor_plus_please_select_source_of_income) {
                    saveAndInvestViewModel.selectedSourceOfIncome = it
                }

                if (saveAndInvestViewModel.selectedSourceOfIncome.code.isBlank()) {
                    saveAndInvestViewModel.selectedSourceOfIncome = sourceOfIncomeList.find { lookupItem -> lookupItem.code.trimStart('0') == saveAndInvestViewModel.customerDetails.financeDetails.sourceOfIncome } ?: LookupItem()
                    binding.sourceOfIncomeNormalInputView.selectedIndex = sourceOfIncomeList.indexOfFirst { lookupItem -> lookupItem == saveAndInvestViewModel.selectedSourceOfIncome }
                }
                dismissProgressDialog()
            }
            else -> dismissProgressDialog()
        }
    }

    private fun setupSourceOfFundsSelector() {
        if (!saveAndInvestViewModel.selectedSourceOfFunds.isNullOrEmpty()) {
            sourceOfFundsString = ""
            sourceOfFundsCodes = ""
            saveAndInvestViewModel.selectedSourceOfFunds.forEach {
                sourceOfFundsString += "${it.value.toSentenceCase()}, "
                sourceOfFundsCodes += "${it.code},"
            }
        }

        with(binding.fundsNormalInputView) {
            binding.fundsNormalInputView.selectedValue = sourceOfFundsString.trimEnd(',', ' ')
            addValidationRule(FieldRequiredValidationRule(R.string.depositor_plus_please_select_source_of_funds))
            setOnClickListener {
                detailsChanged = true
                navigateToSourceOfFunds()
            }
        }
    }

    private fun setOccupationVisibility() {
        binding.occupationNormalInputView.visibility = if (saveAndInvestViewModel.selectedOccupationStatus.code.contains(Regex("01|02|03|08|09"))) View.VISIBLE else View.GONE
    }

    private fun <T : SelectorInterface> NormalInputView<T>.setupSelector(lookupItems: List<LookupItem>, @StringRes title: Int, @StringRes errorMessage: Int, selection: (LookupItem) -> Unit) {
        with(this) {
            setList(lookupItems.toSelectorList { lookupData -> StringItem(lookupData.value) }, getString(title))
            addValidationRule(FieldRequiredValidationRule(errorMessage))
            setItemSelectionInterface {
                detailsChanged = true
                selection(lookupItems[it])
                selectedValue = lookupItems[it].value.toSentenceCase()
            }
        }
    }

    private fun hasValidFields(): Boolean = binding.employmentStatusNormalInputView.validate() && binding.occupationNormalInputView.validateIfVisible() && binding.fundsNormalInputView.validate()

    private fun updateCustomerDetails() {
        val customerDetailsUpdateRequest = SaveAndInvestRiskBasedHelper.buildCustomerDetailsUpdateRequest(saveAndInvestViewModel.customerDetails, saveAndInvestActivity.productType).apply {
            financeInfo.sourceOfFunds = sourceOfFundsCodes.trimEnd(',')
            financeInfo.sourceOfIncome = saveAndInvestViewModel.selectedSourceOfIncome.code
            employmentDetails.occupationCode = if (binding.occupationNormalInputView.isVisible) saveAndInvestViewModel.selectedOccupation.code else DEFAULT_NO_OCCUPATION_CODE
            employmentDetails.occupationStatus = saveAndInvestViewModel.selectedOccupationStatus.code
            personalDetails.casaReferenceNumber = saveAndInvestViewModel.casaReference
        }

        with(customerDetailsUpdateViewModel) {
            updateCustomerDetails(customerDetailsUpdateRequest)
            customerDetailsUpdateResultLiveData.observe(viewLifecycleOwner, { isUpdated ->
                customerDetailsUpdateResultLiveData.removeObservers(viewLifecycleOwner)
                if (isUpdated) {
                    validatePersonalInfo()
                }
            })
        }
    }

    private fun validatePersonalInfo() {
        with(personalDetailsValidationViewModel) {
            validatePersonalDetails(SaveAndInvestRiskBasedHelper.buildPersonalDetailsValidationRequest(saveAndInvestViewModel.customerDetails))
            personalDetailsValidationResultLiveData.observe(viewLifecycleOwner, {
                personalDetailsValidationResultLiveData.removeObservers(viewLifecycleOwner)
                saveAndInvestViewModel.personalDetails.referenceNumber = it.referenceNumber
                saveExistingCustomerDetails()
            })
        }
    }

    private fun saveExistingCustomerDetails() {
        saveApplicationDetails()
        customerDetailsSaveViewModel.customerDetailsSaveLiveData.observe(viewLifecycleOwner, {
            customerDetailsSaveViewModel.customerDetailsSaveLiveData.removeObservers(viewLifecycleOwner)
            performRiskRating()
        })
    }

    private fun performRiskRating() {
        riskRatingViewModel.fetchRiskRating(RiskRatingRequest().apply {
            with(saveAndInvestViewModel) {
                occupationStatus = selectedOccupationStatus.code
                occupation = if (binding.occupationNormalInputView.isVisible) selectedOccupation.code else DEFAULT_NO_OCCUPATION_CODE
                cifKey = customerDetails.cifKey
                casaReferenceNumber = casaReference
                sourceOfIncome = selectedSourceOfIncome.code
                productCode = saveAndInvestActivity.productType.productCode
            }
        })

        riskRatingViewModel.riskRatingLiveData.observe(viewLifecycleOwner, {
            riskRatingViewModel.riskRatingLiveData.removeObservers(viewLifecycleOwner)
            saveAndInvestViewModel.riskRatingResponse = it
            saveEmploymentDetails()
            when {
                it.riskRating.isBlank() || it.riskRating == "H" || it.riskRating == "VH" || it.riskRatingServiceStatus == BMBConstants.FAILURE -> navigateOnRiskBasedFailure()
                else -> navigateOnSuccess()
            }
            dismissProgressDialog()
        })

        riskRatingViewModel.failureLiveData.observe(viewLifecycleOwner, {
            riskRatingViewModel.failureLiveData.removeObservers(viewLifecycleOwner)
            dismissProgressDialog()
            navigateOnFailure()
        })
    }

    private fun saveEmploymentDetails() {
        with(saveAndInvestViewModel) {
            employmentDetails = SaveAndInvestSaveApplicationHelper.buildEmploymentDetailsInfo(customerDetails).apply {
                employmentStatusCode = selectedOccupationStatus.code
                employmentStatusValue = selectedOccupationStatus.value
                occupationCode = if (binding.occupationNormalInputView.isVisible) selectedOccupation.code else DEFAULT_NO_OCCUPATION_CODE
                occupationValue = if (binding.occupationNormalInputView.isVisible) selectedOccupation.value else ""
                sourceOfIncomeCode = selectedSourceOfIncome.code
                sourceOfIncomeValue = selectedSourceOfIncome.value
                sourceOfFundsCode = sourceOfFundsCodes.trimEnd(',')
                sourceOfFundsValue = sourceOfFundsString
            }
        }
    }

    protected fun getRiskedBasedFailureScreenProperties(): GenericResultScreenProperties {
        val isPrivateBanker = CommonUtils.isPrivateBanker()
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            trackAnalyticsAction(if (isPrivateBanker) "PrivateBankerRBAFailureScreen_DoneButtonClicked" else "RetailBankerRBAFailureScreen_DoneButtonClicked")
            navigateToHomeScreenSelectingHomeIcon()
        }
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalAlert)
                .setTitle(getString(R.string.depositor_plus_unable_to_process))
                .setDescription(if (isPrivateBanker) getString(R.string.depositor_plus_contact_private_banker) else getString(R.string.depositor_plus_visit_nearest_branch))
                .setContactViewContactName(getString(R.string.depositor_plus_call_centre))
                .setContactViewContactNumber(if (isPrivateBanker) getString(R.string.depositor_plus_private_call_centre_number) else getString(R.string.support_contact_number))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(false)
    }
}