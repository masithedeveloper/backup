/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.freeCover.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.freeCover.ui.FreeCoverActivity.Companion.FREE_COVER_ANALYTICS_TAG
import com.barclays.absa.banking.manage.profile.ui.OccupationStatus
import com.barclays.absa.banking.newToBank.services.dto.CodesLookupDetailsSelector
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.riskBasedApproach.services.dto.PersonalInformationResponse
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskBasedApproachViewModel
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskProfileDetails
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskProfileResponse
import com.barclays.absa.banking.shared.services.SharedViewModel
import com.barclays.absa.banking.shared.services.dto.CIFGroupCode
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.free_cover_employment_details_fragment.*
import styleguide.forms.ItemSelectionInterface
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.forms.validation.FieldRequiredValidationRule
import styleguide.forms.validation.addValidationRule
import styleguide.utils.extensions.toTitleCaseRemovingCommas

class FreeCoverEmploymentDetailsFragment : FreeCoverBaseFragment(R.layout.free_cover_employment_details_fragment) {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var riskBasedApproachViewModel: RiskBasedApproachViewModel
    private var customerInformation = PersonalInformationResponse.CustomerInformation()

    private var employmentStatusSelectorStatusList: SelectorList<CodesLookupDetailsSelector> = SelectorList()
    private var occupationSelectorList: SelectorList<CodesLookupDetailsSelector> = SelectorList()
    private var sourceOfFundsSelectorList: SelectorList<CodesLookupDetailsSelector> = SelectorList()

    private var employmentStatusChanged: Boolean = false
    private var occupationStatusChanged: Boolean = false

    companion object {
        const val PRODUCT_CODE = "FAOL"
        const val SUB_PRODUCT_CODE = "FAOL"
        const val SUB_BUSINESS_UNIT = "096"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedViewModel = freeCoverInterface.sharedViewModel()
        riskBasedApproachViewModel = freeCoverInterface.riskBasedApproachViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.free_cover_details))
        freeCoverInterface.setStep(2)

        if (employmentStatusSelectorStatusList.isNullOrEmpty()) {
            sharedViewModel.getCIFCodes(CIFGroupCode.OCCUPATION_STATUS)
            attachObservers()
        } else {
            initViews()
        }

        setUpRadioButtonView()
        addValidationRules()
        setUpOnClickListener()

        AnalyticsUtil.trackAction(FREE_COVER_ANALYTICS_TAG, "FreeCover_DetailsScreen_ScreenDisplayed")
    }

    private fun initViews() {
        with(freeCoverViewModel.applyFreeCoverData.employmentStatus) {
            employmentStatusNormalInputView.selectedValue = engCodeDescription
            checkEmploymentStatus(itemCode)
        }
    }

    private fun attachObservers() {
        sharedViewModel.codesLiveData = MutableLiveData()
        sharedViewModel.codesLiveData.observe(viewLifecycleOwner, Observer { lookupResult ->

            if (!lookupResult.items.isNullOrEmpty()) {
                when (lookupResult.items.first().groupCode) {
                    CIFGroupCode.OCCUPATION_STATUS.key -> {
                        sharedViewModel.getCIFCodes(CIFGroupCode.OCCUPATION)
                        employmentStatusSelectorStatusList = SelectorList()
                        lookupResult.items.forEach { lookupItem ->
                            employmentStatusSelectorStatusList.add(CodesLookupDetailsSelector(lookupItem.defaultLabel.toTitleCaseRemovingCommas(), lookupItem.itemCode.toString(), lookupItem.groupCode.toString()))
                        }
                        employmentStatusSelectorStatusList.sortBy { employmentSelectorStatusList ->
                            employmentSelectorStatusList.engCodeDescription
                        }
                        employmentStatusNormalInputView.setList(employmentStatusSelectorStatusList, getString(R.string.free_cover_employment_status))
                    }

                    CIFGroupCode.OCCUPATION.key -> {
                        sharedViewModel.getCIFCodes(CIFGroupCode.SOURCE_OF_FUNDS)
                        occupationSelectorList = SelectorList()
                        lookupResult.items.forEach { lookupItem ->
                            occupationSelectorList.add(CodesLookupDetailsSelector(lookupItem.defaultLabel.toTitleCaseRemovingCommas(), lookupItem.itemCode.toString(), lookupItem.groupCode.toString()))
                        }
                        occupationSelectorList.sortBy { occupationStatusSelectorList ->
                            occupationStatusSelectorList.engCodeDescription
                        }
                        freeCoverOccupationNormalInputView.setList(occupationSelectorList, getString(R.string.free_cover_occupation))
                    }

                    CIFGroupCode.SOURCE_OF_FUNDS.key -> {
                        sharedViewModel.sourceOfFundsResponse.value = lookupResult
                        sourceOfFundsSelectorList = SelectorList()
                        lookupResult.items.forEach { lookupItem ->
                            sourceOfFundsSelectorList.add(CodesLookupDetailsSelector(lookupItem.defaultLabel.toTitleCaseRemovingCommas(), lookupItem.itemCode.toString(), lookupItem.groupCode.toString()))
                        }
                        riskBasedApproachViewModel.fetchPersonalInformation()
                    }
                }
            } else {
                dismissProgressDialog()
                showGenericErrorMessage()
            }

            riskBasedApproachViewModel.personalInformationResponse = MutableLiveData()
            riskBasedApproachViewModel.personalInformationResponse.observe(viewLifecycleOwner, Observer { personalInformationResponse ->

                personalInformationResponse.customerInformation?.let { customerInformation = it }
                if (!employmentStatusSelectorStatusList.isNullOrEmpty()) {
                    val employmentStatus = personalInformationResponse.customerInformation?.employmentInformation?.occupationStatus.toString()
                    val matchingLookupIndex = sharedViewModel.getMatchingLookupIndex(employmentStatus, employmentStatusSelectorStatusList)
                    employmentStatusNormalInputView.selectedIndex = matchingLookupIndex
                    checkEmploymentStatus(employmentStatus)
                }

                if (!occupationSelectorList.isNullOrEmpty()) {
                    val matchingLookupIndex = sharedViewModel.getMatchingLookupIndex(personalInformationResponse.customerInformation?.employmentInformation?.occupation.toString(), occupationSelectorList)
                    freeCoverOccupationNormalInputView.selectedIndex = matchingLookupIndex
                }
                freeCoverViewModel.selectedEmploymentStatusIndex = employmentStatusNormalInputView.selectedIndex
                freeCoverViewModel.selectedOccupationStatusIndex = freeCoverOccupationNormalInputView.selectedIndex
                dismissProgressDialog()
            })
        })
    }

    private fun isNotEmployed(occupationCode: String): Boolean = listOf(OccupationStatus.HOUSEWIFE, OccupationStatus.STUDENT, OccupationStatus.UNEMPLOYED, OccupationStatus.PENSIONER, OccupationStatus.SCHOLAR)
            .any { it.occupationCode == occupationCode }

    private fun checkEmploymentStatus(employmentStatusCode: String) {
        freeCoverOccupationNormalInputView.visibility = if (isNotEmployed(employmentStatusCode)) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun setUpRadioButtonView() {
        val radioButtonLabels = SelectorList<StringItem>().apply {
            add(StringItem(getString(R.string.insurance_beneficiary)))
            add(StringItem(getString(R.string.insurance_deceased_estate)))
        }
        with(payeeRadioButtonView) {
            setDataSource(radioButtonLabels)
            selectedIndex = 0
        }
    }

    private fun setUpOnClickListener() {
        employmentStatusNormalInputView.setItemSelectionInterface(ItemSelectionInterface { index ->
            checkEmploymentStatus(employmentStatusSelectorStatusList[index].itemCode)
            employmentStatusChanged = index != freeCoverViewModel.selectedEmploymentStatusIndex
        })

        freeCoverOccupationNormalInputView.setItemSelectionInterface(ItemSelectionInterface { index ->
            occupationStatusChanged = index != freeCoverViewModel.selectedOccupationStatusIndex
        })

        continueButton.setOnClickListener {
            if (areAllFieldsValid()) {
                freeCoverViewModel.applyFreeCoverData.apply {
                    employmentStatus = employmentStatusNormalInputView.selectedItem as CodesLookupDetailsSelector
                    occupation = if (freeCoverOccupationNormalInputView.isVisible) {
                        freeCoverOccupationNormalInputView.selectedItem as CodesLookupDetailsSelector
                    } else {
                        CodesLookupDetailsSelector("", "", "")
                    }
                    sourceOfFund = riskBasedApproachViewModel.personalInformationResponse.value?.customerInformation?.sourceOfIncome ?: ""

                    val riskProfileDetails = RiskProfileDetails(freeCoverViewModel.casaReference, occupation.itemCode, employmentStatus.itemCode, PRODUCT_CODE, SUB_PRODUCT_CODE, SUB_BUSINESS_UNIT, sourceOfFund)

                    if (employmentStatusChanged || occupationStatusChanged) {
                        employmentStatusChanged = false
                        occupationStatusChanged = false
                        freeCoverViewModel.selectedEmploymentStatusIndex = employmentStatusNormalInputView.selectedIndex
                        freeCoverViewModel.selectedOccupationStatusIndex = freeCoverOccupationNormalInputView.selectedIndex
                        riskBasedApproachViewModel.riskProfileResponse = MutableLiveData()
                    }
                    riskBasedApproachViewModel.fetchRiskProfile(riskProfileDetails)
                }

                with(riskBasedApproachViewModel) {
                    riskProfileResponse.observe(viewLifecycleOwner, Observer {
                        navigateOnRiskProfile(it)
                        dismissProgressDialog()
                        riskBasedApproachViewModel.riskProfileResponse.removeObservers(viewLifecycleOwner)
                    })
                }
            }
        }
    }

    private fun navigateOnRiskProfile(riskProfileResponse: RiskProfileResponse) {
        val isAcceptableRisk = riskProfileResponse.riskRating.toLowerCase(BMBApplication.getApplicationLocale()).contains(Regex("l|vl|m"))
        if (isAcceptableRisk) {
            if (payeeRadioButtonView.selectedIndex == 0) {
                freeCoverViewModel.isDeceasedEstate = false
                AnalyticsUtil.trackAction(FREE_COVER_ANALYTICS_TAG, "FreeCover_DetailsScreen_BeneficiaryButtonClicked")
                navigate(FreeCoverEmploymentDetailsFragmentDirections.actionFreeCoverEmploymentDetailsFragmentToFreeCoverBeneficiaryDetailsFragment())
            } else {
                freeCoverViewModel.isDeceasedEstate = true
                AnalyticsUtil.trackAction(FREE_COVER_ANALYTICS_TAG, "FreeCover_DetailsScreen_DeceasedEstateButtonClicked")
                navigate(FreeCoverEmploymentDetailsFragmentDirections.actionFreeCoverEmploymentDetailsFragmentToFreeCoverConfirmationFragment())
            }
        } else {
            startActivity(IntentFactory.getUnableToContinueScreen(hostActivity, R.string.unable_to_continue, R.string.risk_based_approach_failure_message))
        }
    }

    private fun areAllFieldsValid(): Boolean {
        return if (freeCoverOccupationNormalInputView.isVisible) {
            employmentStatusNormalInputView.validate() && freeCoverOccupationNormalInputView.validate()
        } else {
            employmentStatusNormalInputView.validate()
        }
    }

    private fun addValidationRules() {
        employmentStatusNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.risk_based_approach_employment_status_error_message))
        freeCoverOccupationNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.risk_based_approach_occupation_error_message))
    }

    override fun onDestroyView() {
        freeCoverViewModel.coverAmountApplyFreeCoverResponse.removeObservers(this)
        super.onDestroyView()
    }
}