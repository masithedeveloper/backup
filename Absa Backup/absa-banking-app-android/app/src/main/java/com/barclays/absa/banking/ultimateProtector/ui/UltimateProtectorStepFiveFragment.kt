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

package com.barclays.absa.banking.ultimateProtector.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.UltimateProtectorStepFiveFragmentBinding
import com.barclays.absa.banking.framework.AbsaBaseFragment
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.funeralCover.services.dto.RetailAccount
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.riskBasedApproach.services.dto.PersonalInformationResponse
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskBasedApproachViewModel
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskProfileDetails
import com.barclays.absa.banking.shared.services.SharedViewModel
import com.barclays.absa.banking.shared.services.dto.CIFGroupCode
import com.barclays.absa.banking.shared.services.dto.LookupItem
import com.barclays.absa.banking.shared.services.dto.LookupResult
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.viewModel
import styleguide.forms.SelectorList

class UltimateProtectorStepFiveFragment : AbsaBaseFragment<UltimateProtectorStepFiveFragmentBinding>() {

    private lateinit var ultimateProtectorViewModel: UltimateProtectorViewModel
    private lateinit var riskBasedApproachViewModel: RiskBasedApproachViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var lookupItemObserver: Observer<LookupResult>
    private lateinit var personalInformationObserver: Observer<PersonalInformationResponse>
    private lateinit var retailAccountsObserver: Observer<List<RetailAccount>?>
    private lateinit var hostActivity: UltimateProtectorHostActivity

    override fun getLayoutResourceId(): Int = R.layout.ultimate_protector_step_five_fragment

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as UltimateProtectorHostActivity
        ultimateProtectorViewModel = hostActivity.viewModel()
        riskBasedApproachViewModel = hostActivity.viewModel()
        sharedViewModel = hostActivity.viewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ultimateProtectorViewModel.retailAccountsLiveData.removeObserver(retailAccountsObserver)
        riskBasedApproachViewModel.personalInformationResponse.removeObserver(personalInformationObserver)
        sharedViewModel.codesLiveData.removeObserver(lookupItemObserver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.debit_order_details))
        hostActivity.setStep(5)
        populateWithData()
        attachViewCallBackHandlers()
        AnalyticsUtil.trackAction(UltimateProtectorHostActivity.ULTIMATE_PROTECTOR_CHANNEL, "WIMI_Life_UP_Step5")
    }

    private fun populateWithData() {
        var personalInformation: PersonalInformationResponse? = null

        retailAccountsObserver = Observer {
            it?.let { retailAccounts ->
                if (retailAccounts.isNotEmpty()) {
                    binding.accountToDebitNormalInputView.setList(ultimateProtectorViewModel.buildAccountsSelectorList(retailAccounts), getString(R.string.account_to_debit))
                }
            }
            riskBasedApproachViewModel.fetchPersonalInformation()
        }

        personalInformationObserver = Observer { personalInformationResponse ->
            personalInformation = personalInformationResponse
            sharedViewModel.codesLiveData.observe(this, lookupItemObserver)
            sharedViewModel.getCIFCodes(CIFGroupCode.OCCUPATION_STATUS)
        }

        lookupItemObserver = Observer { lookupResult ->
            val lookupItems = lookupResult?.items
            val employmentInformation = personalInformation?.customerInformation?.employmentInformation
            val sortedSelectorList = lookupItems?.let { sharedViewModel.buildSortedSelectorList(it) } ?: SelectorList()

            when (lookupItems?.get(0)?.groupCode) {
                CIFGroupCode.OCCUPATION_STATUS.key -> {
                    binding.employmentStatusNormalInputView.setList(sortedSelectorList, getString(R.string.risk_based_approach_employment_status))
                    employmentInformation?.occupationStatus?.let {
                        binding.employmentStatusNormalInputView.selectedIndex = sharedViewModel.getMatchingLookupIndex(it, sortedSelectorList)
                        binding.occupationNormalInputView.visibility = if (ultimateProtectorViewModel.shouldShowOccupation(sortedSelectorList[binding.employmentStatusNormalInputView.selectedIndex].itemCode)) View.VISIBLE else View.GONE
                        ultimateProtectorViewModel.ultimateProtectorInfo.employmentStatus = binding.employmentStatusNormalInputView.selectedItem as LookupItem
                    }
                    sharedViewModel.getCIFCodes(CIFGroupCode.OCCUPATION)
                }
                CIFGroupCode.OCCUPATION.key -> {
                    binding.occupationNormalInputView.setList(sortedSelectorList, getString(R.string.risk_based_approach_occupation))
                    employmentInformation?.occupation?.let {
                        binding.occupationNormalInputView.selectedIndex = sharedViewModel.getMatchingLookupIndex(it, sortedSelectorList)
                        ultimateProtectorViewModel.ultimateProtectorInfo.occupation = binding.occupationNormalInputView.selectedItem as? LookupItem
                    }
                    sharedViewModel.getCIFCodes(CIFGroupCode.SOURCE_OF_FUNDS)
                }
                CIFGroupCode.SOURCE_OF_FUNDS.key -> {
                    binding.sourceOfFundsNormalInputView.setList(sortedSelectorList, getString(R.string.source_of_funds))
                    binding.sourceOfFundsNormalInputView.selectedIndex = sharedViewModel.getMatchingLookupIndex("20", sortedSelectorList)
                    ultimateProtectorViewModel.ultimateProtectorInfo.sourceOfFund = binding.sourceOfFundsNormalInputView.selectedItem as? LookupItem
                    dismissProgressDialog()
                }
            }
        }

        ultimateProtectorViewModel.retailAccountsLiveData.observe(this, retailAccountsObserver)
        riskBasedApproachViewModel.personalInformationResponse.observe(this, personalInformationObserver)
        ultimateProtectorViewModel.loadRetailAccounts()
        val selectorList = ultimateProtectorViewModel.buildOptionsSelectorList(getString(R.string.beneficiary), getString(R.string.deceased_estate))
        binding.beneficiaryRadioButtonView.setDataSource(selectorList)
        binding.beneficiaryRadioButtonView.selectedIndex = 0
    }

    private fun attachViewCallBackHandlers() {
        binding.debitDayNormalInputView.setCustomOnClickListener {
            showDayPicker()
        }

        binding.accountToDebitNormalInputView.setItemSelectionInterface {
            val retailAccount = ultimateProtectorViewModel.retailAccountsLiveData.value?.get(it)
            binding.accountToDebitNormalInputView.selectedValue = retailAccount?.accountDescription + " - " + retailAccount?.accountNumber
            ultimateProtectorViewModel.ultimateProtectorInfo.accountInfo = retailAccount
        }

        binding.employmentStatusNormalInputView.setItemSelectionInterface {
            val lookupItem = binding.employmentStatusNormalInputView.selectedItem as LookupItem
            val shouldShowOccupation = ultimateProtectorViewModel.shouldShowOccupation(lookupItem.itemCode)

            ultimateProtectorViewModel.ultimateProtectorInfo.employmentStatus = lookupItem
            binding.occupationNormalInputView.visibility = if (shouldShowOccupation) View.VISIBLE else View.GONE
        }

        binding.occupationNormalInputView.setItemSelectionInterface {
            ultimateProtectorViewModel.ultimateProtectorInfo.occupation = binding.occupationNormalInputView.selectedItem as LookupItem
        }

        binding.sourceOfFundsNormalInputView.setItemSelectionInterface {
            ultimateProtectorViewModel.ultimateProtectorInfo.sourceOfFund = binding.sourceOfFundsNormalInputView.selectedItem as LookupItem
        }

        binding.beneficiaryRadioButtonView.setItemCheckedInterface {
            ultimateProtectorViewModel.ultimateProtectorInfo.isBeneficiarySelected = it == 0
        }

        binding.stepFiveNextButton.setOnClickListener {
            if (!hasError()) {
                riskBasedApproachViewModel.riskProfileResponse.observe(this, { riskProfile ->
                    dismissProgressDialog()
                    if (riskProfile?.transactionStatus.equals(BMBConstants.FAILURE, true)) {
                        startActivity(IntentFactory.getUnableToContinueScreen(activity, R.string.unable_to_continue, R.string.risk_based_approach_failure_message))
                    } else {
                        if (riskProfile?.riskRating.equals("L") ||
                                riskProfile?.riskRating.equals("VL") ||
                                riskProfile?.riskRating.equals("M")) {
                            val isBeneficiarySelected = ultimateProtectorViewModel.ultimateProtectorInfo.isBeneficiarySelected
                            isBeneficiarySelected?.let {
                                if (it) {
                                    ultimateProtectorViewModel.ultimateProtectorInfo.payTo = "beneficiary"
                                    navigate(UltimateProtectorStepFiveFragmentDirections.actionUltimateProtectorStepFiveFragmentToUltimateProtectorStepFiveBeneficiaryFragment())
                                } else {
                                    ultimateProtectorViewModel.ultimateProtectorInfo.payTo = "myEstate"
                                    navigate(UltimateProtectorStepFiveFragmentDirections.actionUltimateProtectorStepFiveFragmentToUltimateProtectorStepSixFragment())
                                }
                            }
                        } else {
                            AnalyticsUtil.trackAction(UltimateProtectorHostActivity.ULTIMATE_PROTECTOR_CHANNEL, "WIMI_Life_UP_RBAHigh_Vhigh")
                            startActivity(IntentFactory.getUnableToContinueScreen(activity, R.string.unable_to_continue, R.string.risk_based_approach_failure_message))
                        }
                    }
                })

                val riskProfileDetails = RiskProfileDetails(ultimateProtectorViewModel.ultimateProtectorData.casaReference
                        ?: "",
                        ultimateProtectorViewModel.ultimateProtectorInfo.occupation?.itemCode ?: "",
                        ultimateProtectorViewModel.ultimateProtectorInfo.employmentStatus?.itemCode
                                ?: "",
                        "BLIFE",
                        "UL01",
                        "096",
                        ultimateProtectorViewModel.ultimateProtectorInfo.sourceOfFund?.itemCode
                                ?: "")
                riskBasedApproachViewModel.fetchRiskProfile(riskProfileDetails)
            }
        }
        if (binding.beneficiaryRadioButtonView.selectedIndex == 0) {
            ultimateProtectorViewModel.ultimateProtectorInfo.isBeneficiarySelected = true
        }
        binding.stepFiveNextButton.isEnabled = binding.beneficiaryRadioButtonView.selectedIndex > -1
    }

    private fun showDayPicker() {
        val datePickerDialogFragment = DayPickerDialogFragment.newInstance(arrayOf("17", "18", "19"))
        datePickerDialogFragment.onDateItemSelectionListener = object : DayPickerDialogFragment.OnDateItemSelectionListener {
            override fun onDateItemSelected(day: String) {
                ultimateProtectorViewModel.ultimateProtectorInfo.dayOfDebit = day
                binding.debitDayNormalInputView.selectedValue = day
            }
        }
        datePickerDialogFragment.show(childFragmentManager, "dialog")
    }

    private fun hasError(): Boolean {
        if (binding.debitDayNormalInputView.selectedValue.isEmpty()) {
            binding.debitDayNormalInputView.setError(R.string.select_day_of_debit)
            return true
        } else {
            if (binding.debitDayNormalInputView.errorTextView?.text.toString().isNotEmpty()) {
                binding.debitDayNormalInputView.clearError()
            }
        }

        if (binding.accountToDebitNormalInputView.selectedValue.isEmpty()) {
            binding.accountToDebitNormalInputView.setError(R.string.please_select_account)
            return true
        } else {
            if (binding.accountToDebitNormalInputView.errorTextView?.text.toString().isNotEmpty()) {
                binding.accountToDebitNormalInputView.clearError()
            }
        }
        return false
    }
}