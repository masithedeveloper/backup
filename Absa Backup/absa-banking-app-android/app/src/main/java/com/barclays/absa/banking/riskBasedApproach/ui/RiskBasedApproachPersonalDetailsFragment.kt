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

package com.barclays.absa.banking.riskBasedApproach.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.RiskBasedApproachPersonalDetailsFragmentBinding
import com.barclays.absa.banking.fixedDeposit.FixedDepositActivity
import com.barclays.absa.banking.framework.AbsaBaseFragment
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.newToBank.services.dto.CodesLookupDetailsSelector
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskBasedApproachViewModel
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskProfileDetails
import com.barclays.absa.banking.shared.services.SharedViewModel
import com.barclays.absa.banking.shared.services.dto.CIFGroupCode
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.viewModel
import styleguide.forms.SelectorList
import styleguide.utils.extensions.toTitleCaseRemovingCommas

class RiskBasedApproachPersonalDetailsFragment : AbsaBaseFragment<RiskBasedApproachPersonalDetailsFragmentBinding>() {

    private lateinit var riskBasedApproachViewModel: RiskBasedApproachViewModel
    private lateinit var sharedViewModel: SharedViewModel

    private var employmentSelectorStatusList: SelectorList<CodesLookupDetailsSelector>? = null
    private var occupationStatusSelectorList: SelectorList<CodesLookupDetailsSelector>? = null

    private var destinationFragment: Int = -1
    private var productCode: String = ""

    override fun getLayoutResourceId(): Int = R.layout.risk_based_approach_personal_details_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as FixedDepositActivity).setToolbarTitle(getString(R.string.risk_based_approach_personal_details_toolbar_title))
        (activity as FixedDepositActivity).showToolbar()

        arguments?.let {
            RiskBasedApproachPersonalDetailsFragmentArgs.fromBundle(it).apply {
                destinationFragment = destinationExtra
                productCode = productCodeExtra
            }
        }

        setUpViewModels()
        setUpOnClickListeners()

        if (employmentSelectorStatusList.isNullOrEmpty()) {
            setUpObservers()
            sharedViewModel.getCIFCodes(CIFGroupCode.OCCUPATION_STATUS)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (!employmentSelectorStatusList.isNullOrEmpty() && binding.employmentStatusNormalInputView.selectedIndex > -1) {
            val codesLookupDetailsSelector = employmentSelectorStatusList?.get(binding.employmentStatusNormalInputView.selectedIndex)
            checkEmploymentStatus(codesLookupDetailsSelector!!.itemCode)
        }
    }

    private fun setUpViewModels() {
        riskBasedApproachViewModel = baseActivity.viewModel()
        sharedViewModel = baseActivity.viewModel()
    }

    private fun setUpObservers() {

        sharedViewModel.codesLiveData = MutableLiveData()
        sharedViewModel.codesLiveData.observe(this, {

            when {
                it?.items?.get(0)?.groupCode == CIFGroupCode.OCCUPATION_STATUS.key -> {
                    sharedViewModel.getCIFCodes(CIFGroupCode.OCCUPATION)

                    employmentSelectorStatusList = SelectorList()
                    for (lookupDetails in it.items) {
                        employmentSelectorStatusList?.add(CodesLookupDetailsSelector(lookupDetails.defaultLabel.toTitleCaseRemovingCommas(), lookupDetails.itemCode!!, lookupDetails.groupCode!!))
                    }

                    binding.employmentStatusNormalInputView.setList(employmentSelectorStatusList, getString(R.string.risk_based_approach_select_employment_status))
                }
                it?.items?.get(0)?.groupCode == CIFGroupCode.OCCUPATION.key -> {
                    sharedViewModel.getCIFCodes(CIFGroupCode.SOURCE_OF_FUNDS)

                    occupationStatusSelectorList = SelectorList()
                    for (lookupDetails in it.items) {
                        occupationStatusSelectorList?.add(CodesLookupDetailsSelector(lookupDetails.defaultLabel.toTitleCaseRemovingCommas(), lookupDetails.itemCode!!, lookupDetails.groupCode!!))
                    }
                    binding.occupationNormalInputView.setList(occupationStatusSelectorList, getString(R.string.risk_based_approach_select_occupation))

                }
                it?.items?.get(0)?.groupCode == CIFGroupCode.SOURCE_OF_FUNDS.key -> {
                    sharedViewModel.sourceOfFundsResponse.value = it
                    riskBasedApproachViewModel.fetchPersonalInformation()
                }
            }
        })

        riskBasedApproachViewModel.personalInformationResponse = MutableLiveData()
        riskBasedApproachViewModel.personalInformationResponse.observe(this, {

            if (!employmentSelectorStatusList.isNullOrEmpty()) {
                val occupationStatus = it?.customerInformation?.employmentInformation?.occupationStatus.toString()
                val matchingLookupIndex = sharedViewModel.getMatchingLookupIndex(occupationStatus, employmentSelectorStatusList!!)
                binding.employmentStatusNormalInputView.selectedIndex = matchingLookupIndex
                checkEmploymentStatus(occupationStatus)
            }

            if (!occupationStatusSelectorList.isNullOrEmpty()) {
                val matchingLookupIndex = sharedViewModel.getMatchingLookupIndex(it?.customerInformation?.employmentInformation?.occupation.toString(), occupationStatusSelectorList!!)
                binding.occupationNormalInputView.selectedIndex = matchingLookupIndex
            }

            dismissProgressDialog()
        })

        sharedViewModel.selectedSourceOfFunds = MutableLiveData()
        sharedViewModel.selectedSourceOfFunds.observe(this, {

            if (!it.isNullOrEmpty()) {
                val sourceOfFunds = StringBuilder()
                val sourceOfFundsCode = StringBuilder()
                for (item in it) {
                    sourceOfFunds.append(item.defaultLabel.toTitleCaseRemovingCommas()).append(", ")
                    sourceOfFundsCode.append(item.itemCode).append("|")
                }
                sourceOfFunds.delete(sourceOfFunds.length - 2, sourceOfFunds.length)
                sourceOfFundsCode.deleteCharAt(sourceOfFundsCode.length - 1)

                binding.sourceOfFundsNormalInputView.selectedValue = sourceOfFunds.toString()
            } else {
                binding.sourceOfFundsNormalInputView.selectedValue = ""
            }

        })
    }

    private fun checkEmploymentStatus(occupationStatusCode: String) {
        if (occupationStatusCode == "01" || occupationStatusCode == "02" || occupationStatusCode == "03" || occupationStatusCode == "08" || occupationStatusCode == "09") {
            binding.occupationNormalInputView.visibility = View.VISIBLE
        } else {
            binding.occupationNormalInputView.visibility = View.GONE
        }
    }

    private fun setUpOnClickListeners() {
        binding.employmentStatusNormalInputView.setItemSelectionInterface({
            checkEmploymentStatus(employmentSelectorStatusList?.get(it)?.itemCode.toString())
        })

        binding.sourceOfFundsNormalInputView.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_riskBasedApproachPersonalDetailsFragment_to_fixedDepositSourceOfFundsFragment)
        }

        binding.nextButton.setOnClickListener { view ->

            if (isValidInput()) {
                val occupation = binding.occupationNormalInputView.selectedItem as CodesLookupDetailsSelector?
                val employment = binding.employmentStatusNormalInputView.selectedItem as CodesLookupDetailsSelector?
                var sourceOfFundsCodes = ""
                for (item in sharedViewModel.selectedSourceOfFunds.value!!) {
                    sourceOfFundsCodes = item.itemCode.toString() + "|"
                }
                sourceOfFundsCodes = sourceOfFundsCodes.substring(0, sourceOfFundsCodes.length - 1)
                val occupationItem = occupation?.itemCode ?: ""
                val employmentItem = employment?.itemCode ?: ""

                val riskProfileDetails = RiskProfileDetails(riskBasedApproachViewModel.casaStatusResponse.value?.casaReference.toString(), occupationItem, employmentItem, productCode, "", "", sourceOfFundsCodes)
                riskBasedApproachViewModel.riskProfileResponse = MutableLiveData()
                riskBasedApproachViewModel.fetchRiskProfile(riskProfileDetails)
                riskBasedApproachViewModel.riskProfileResponse.observe(this, {
                    AnalyticsUtil.trackAction("RBAStatus", "RBAStatus " + it?.riskRating)
                    if (it?.riskRating.isNullOrEmpty()) {
                        showGenericErrorMessageThenFinish()
                    } else if (it?.riskRating == "H" || it?.riskRating == "VH") {
                        startActivity(IntentFactory.getAlertResultScreenPrimaryButton(activity, R.string.unable_to_continue, R.string.risk_based_approach_unable_to_continue))
                    } else {
                        Navigation.findNavController(view).navigate(destinationFragment)
                    }
                    if (BuildConfig.DEBUG && BuildConfig.UAT) {
                        (activity as BaseActivity).toastLong("" + it?.riskRating)
                    }
                    dismissProgressDialog()
                    riskBasedApproachViewModel.riskProfileResponse.removeObservers(this)
                })
            }
        }
    }

    private fun isValidInput(): Boolean {
        when {
            binding.employmentStatusNormalInputView.selectedValue.isEmpty() -> binding.employmentStatusNormalInputView.setError(R.string.risk_based_approach_employment_status_error_message)
            binding.occupationNormalInputView.visibility == View.VISIBLE && binding.occupationNormalInputView.selectedValue.isEmpty() -> binding.occupationNormalInputView.setError(R.string.risk_based_approach_occupation_error_message)
            binding.sourceOfFundsNormalInputView.selectedValue.isEmpty() -> binding.sourceOfFundsNormalInputView.setError(R.string.risk_based_approach_source_of_funds_error_message)
            else -> return true
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedViewModel.codesLiveData.removeObservers(this)
        sharedViewModel.selectedSourceOfFunds.removeObservers(this)
        riskBasedApproachViewModel.personalInformationResponse.removeObservers(this)
    }
}