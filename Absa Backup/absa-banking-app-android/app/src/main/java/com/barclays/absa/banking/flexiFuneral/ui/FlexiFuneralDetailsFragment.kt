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

package com.barclays.absa.banking.flexiFuneral.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountList
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.flexiFuneral.services.dto.FlexiFuneralBeneficiaryDetails
import com.barclays.absa.banking.newToBank.services.dto.CodesLookupDetailsSelector
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.riskBasedApproach.services.dto.PersonalInformationResponse
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskBasedApproachViewModel
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskProfileDetails
import com.barclays.absa.banking.shared.services.SharedViewModel
import com.barclays.absa.banking.shared.services.dto.CIFGroupCode
import com.barclays.absa.banking.transfer.AccountListItem
import com.barclays.absa.banking.ultimateProtector.ui.DayPickerDialogFragment
import com.barclays.absa.utils.*
import kotlinx.android.synthetic.main.flexi_funeral_details_fragment.*
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import styleguide.utils.extensions.toTitleCaseRemovingCommas

class FlexiFuneralDetailsFragment : FlexiFuneralBaseFragment(R.layout.flexi_funeral_details_fragment) {
    private var isBeneficiarySelected: Boolean = true
    private lateinit var riskBasedApproachViewModel: RiskBasedApproachViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private var customerInformation = PersonalInformationResponse.CustomerInformation()

    private lateinit var flexiFuneralAccountList: SelectorList<AccountListItem>

    private var employmentSelectorStatusList: SelectorList<CodesLookupDetailsSelector>? = null
    private var occupationStatusSelectorList: SelectorList<CodesLookupDetailsSelector>? = null
    private var sourceOfFundsSelectorList: SelectorList<CodesLookupDetailsSelector>? = null
    private lateinit var transactionalAccountsList: List<AccountObject>

    companion object {
        const val ESTATE_LATE = "Estate_Late"
        const val UNKNOWN = "UNKNOWN"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        riskBasedApproachViewModel = hostActivity.viewModel()
        sharedViewModel = hostActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.flexi_funeral_details))
        hostActivity.setStep(2)
        setYesAndNoRadioButtons()
        setPolicyPayeeRadioButtons()
        setUpOnClickListeners()
        debitDayNormalInputView.setValueEditable(false)
        nameOfInsurerNormalInputView.addRequiredValidationHidingTextWatcher()

        val accounts: AccountList = AbsaCacheManager.getInstance().cachedAccountListObject
        setAccountsList(accounts)

        if (sharedViewModel.codesLiveData.value == null || employmentSelectorStatusList.isNullOrEmpty()) {
            debitDayNormalInputView.selectedValue = "1"
            sharedViewModel.getCIFCodes(CIFGroupCode.OCCUPATION_STATUS)
            attachObservers()
        } else {
            initViews()
        }

        flexiFuneralViewModel.addBeneficiaryStatus = MutableLiveData()
        flexiFuneralViewModel.addBeneficiaryStatus.observe(viewLifecycleOwner, {
            if (it.addBeneficiaryStatus && it.transactionStatus.equals("Success", true)) {
                navigate(FlexiFuneralDetailsFragmentDirections.actionFlexiFuneralDebitOrderDetailsFragmentToFlexiFuneralConfirmationFragment())
            }
            flexiFuneralViewModel.addBeneficiaryStatus.removeObservers(this)
        })

        AnalyticsUtil.trackAction("WIMI_FlexiFuneral", "FlexiFuneral_Details_ScreenDisplayed")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (!employmentSelectorStatusList.isNullOrEmpty() && employmentStatusNormalInputView.selectedIndex > -1) {
            val codesLookupDetailsSelector = employmentSelectorStatusList?.get(employmentStatusNormalInputView.selectedIndex)
            checkEmploymentStatus(codesLookupDetailsSelector?.itemCode.toString())
        }
    }

    private fun initViews() {
        debitDayNormalInputView.selectedValue = flexiFuneralViewModel.applyForFlexiFuneralData.dayOfDebit
        employmentStatusNormalInputView.selectedValue = flexiFuneralViewModel.applyForFlexiFuneralData.employmentStatus?.engCodeDescription.toString()
        occupationNormalInputView.selectedValue = flexiFuneralViewModel.applyForFlexiFuneralData.occupation?.engCodeDescription.toString()
        sourceOfFundsNormalInputView.selectedValue = flexiFuneralViewModel.applyForFlexiFuneralData.sourceOfFund?.engCodeDescription.toString()
    }

    private fun setYesAndNoRadioButtons() {
        val radioButtonLabels = SelectorList<StringItem>().apply {
            add(StringItem(getString(R.string.yes)))
            add(StringItem(getString(R.string.no)))
        }
        terminatedPolicyRadioButtonView.apply {
            setDataSource(radioButtonLabels)
            selectedIndex = 1
            setItemCheckedInterface {
                nameOfInsurerNormalInputView.isVisible = it == 0
            }
        }
    }

    private fun setPolicyPayeeRadioButtons() {
        val radioButtonLabels = SelectorList<StringItem>().apply {
            add(StringItem(getString(R.string.insurance_beneficiary)))
            add(StringItem(getString(R.string.insurance_deceased_estate)))
        }
        payeeRadioButtonView.apply {
            setDataSource(radioButtonLabels)
            selectedIndex = 0
            setItemCheckedInterface {
                isBeneficiarySelected = it == 0
            }
        }
    }

    private fun setAccountsList(accountList: AccountList?) {
        flexiFuneralAccountList = SelectorList()
        if (accountList != null) {
            transactionalAccountsList = FilterAccountList.getFlexiFuneralAccounts(accountList.accountsList)
            transactionalAccountsList.forEach { item ->
                val accountListItem = AccountListItem()
                accountListItem.apply {
                    accountNumber = item.accountNumber
                    accountType = item.description
                    accountBalance = item.availableBalanceFormated
                }
                flexiFuneralAccountList.add(accountListItem)
            }

            if (flexiFuneralAccountList.size == 1) {
                accountNormalInputView.selectedIndex = 0
                accountNormalInputView.selectedValue = flexiFuneralAccountList.first().accountType.toString()
                flexiFuneralViewModel.applyForFlexiFuneralData.apply {
                    accountDescription = flexiFuneralAccountList.first().accountType.toString()
                    accountToBeDebited = transactionalAccountsList.first().accountType.toString()
                    accountNumber = flexiFuneralAccountList.first().accountNumber.toString()
                }
            }
            accountNormalInputView.setList(flexiFuneralAccountList, getString(R.string.select_account_toolbar_title))
        }
    }

    private fun attachObservers() {
        sharedViewModel.codesLiveData = MutableLiveData()
        sharedViewModel.codesLiveData.observe(viewLifecycleOwner, {

            if (!it?.items.isNullOrEmpty()) {
                when (it?.items?.first()?.groupCode) {
                    CIFGroupCode.OCCUPATION_STATUS.key -> {
                        sharedViewModel.getCIFCodes(CIFGroupCode.OCCUPATION)
                        employmentSelectorStatusList = SelectorList()
                        for (lookupDetails in it.items) {
                            employmentSelectorStatusList?.add(CodesLookupDetailsSelector(lookupDetails.defaultLabel.toTitleCaseRemovingCommas(), lookupDetails.itemCode.toString(), lookupDetails.groupCode.toString()))
                        }
                        employmentSelectorStatusList?.sortBy { employmentSelectorStatusList ->
                            employmentSelectorStatusList.engCodeDescription
                        }
                        employmentStatusNormalInputView.setList(employmentSelectorStatusList, getString(R.string.risk_based_approach_select_employment_status))
                    }
                    CIFGroupCode.OCCUPATION.key -> {
                        sharedViewModel.getCIFCodes(CIFGroupCode.SOURCE_OF_FUNDS)
                        occupationStatusSelectorList = SelectorList()
                        for (lookupDetails in it.items) {
                            occupationStatusSelectorList?.add(CodesLookupDetailsSelector(lookupDetails.defaultLabel.toTitleCaseRemovingCommas(), lookupDetails.itemCode.toString(), lookupDetails.groupCode.toString()))
                        }
                        occupationStatusSelectorList?.sortBy { occupationStatusSelectorList ->
                            occupationStatusSelectorList.engCodeDescription
                        }
                        occupationNormalInputView.setList(occupationStatusSelectorList, getString(R.string.risk_based_approach_select_occupation))
                    }
                    CIFGroupCode.SOURCE_OF_FUNDS.key -> {
                        sharedViewModel.sourceOfFundsResponse.value = it
                        sourceOfFundsSelectorList = SelectorList()
                        for (lookupDetails in it.items) {
                            sourceOfFundsSelectorList?.add(CodesLookupDetailsSelector(lookupDetails.defaultLabel.toTitleCaseRemovingCommas(), lookupDetails.itemCode.toString(), lookupDetails.groupCode.toString()))
                        }

                        sourceOfFundsSelectorList?.sortBy { sourceOfFundsSelectorList ->
                            sourceOfFundsSelectorList.engCodeDescription
                        }
                        sourceOfFundsNormalInputView.setList(sourceOfFundsSelectorList, getString(R.string.risk_based_approach_select_source_of_funds))
                        riskBasedApproachViewModel.fetchPersonalInformation()
                    }
                }
            } else {
                dismissProgressDialog()
                showGenericErrorMessage()
            }
        })

        riskBasedApproachViewModel.personalInformationResponse = MutableLiveData()
        riskBasedApproachViewModel.personalInformationResponse.observe(viewLifecycleOwner, { personalInformationResponse ->

            personalInformationResponse?.customerInformation?.let { customerInformation = it }

            if (!employmentSelectorStatusList.isNullOrEmpty()) {
                val occupationStatus = personalInformationResponse.customerInformation?.employmentInformation?.occupationStatus.toString()
                val matchingLookupIndex = sharedViewModel.getMatchingLookupIndex(occupationStatus, employmentSelectorStatusList)
                employmentStatusNormalInputView.selectedIndex = matchingLookupIndex
                checkEmploymentStatus(occupationStatus)
            }

            if (!occupationStatusSelectorList.isNullOrEmpty()) {
                val matchingLookupIndex = sharedViewModel.getMatchingLookupIndex(personalInformationResponse?.customerInformation?.employmentInformation?.occupation.toString(), occupationStatusSelectorList)
                occupationNormalInputView.selectedIndex = matchingLookupIndex
            }

            if (!sourceOfFundsSelectorList.isNullOrEmpty()) {
                val matchingLookupIndex = sharedViewModel.getMatchingLookupIndex(personalInformationResponse?.customerInformation?.sourceOfIncome.toString(), sourceOfFundsSelectorList)
                sourceOfFundsNormalInputView.selectedIndex = matchingLookupIndex
            }
            dismissProgressDialog()
        })

        flexiFuneralViewModel.getRetailAccounts.observe(viewLifecycleOwner, {
            dismissProgressDialog()
        })
    }

    private fun setUpOnClickListeners() {
        debitDayNormalInputView.setCustomOnClickListener {
            showDayPicker()
        }

        accountNormalInputView.setItemSelectionInterface { index ->
            flexiFuneralViewModel.applyForFlexiFuneralData.apply {
                accountDescription = flexiFuneralAccountList[index].accountType.toString()
                accountToBeDebited = transactionalAccountsList[index].accountType.toString()
                accountNumber = flexiFuneralAccountList[index].accountNumber.toString()
            }
        }

        employmentStatusNormalInputView.setItemSelectionInterface { index ->
            checkEmploymentStatus(employmentSelectorStatusList?.get(index)?.itemCode.toString())
            flexiFuneralViewModel.applyForFlexiFuneralData.employmentStatus = employmentStatusNormalInputView.selectedItem as CodesLookupDetailsSelector
        }

        occupationNormalInputView.setItemSelectionInterface {
            flexiFuneralViewModel.applyForFlexiFuneralData.occupation = occupationNormalInputView.selectedItem as CodesLookupDetailsSelector
        }

        sourceOfFundsNormalInputView.setItemSelectionInterface {
            flexiFuneralViewModel.applyForFlexiFuneralData.sourceOfFund = sourceOfFundsNormalInputView.selectedItem as CodesLookupDetailsSelector
        }

        continueButton.setOnClickListener {
            if (isValidInput()) {
                val occupation = occupationNormalInputView.selectedItem as CodesLookupDetailsSelector?
                val employment = employmentStatusNormalInputView.selectedItem as CodesLookupDetailsSelector?
                val sourceOfFunds = sourceOfFundsNormalInputView.selectedItem as CodesLookupDetailsSelector?
                val occupationItem = occupation?.itemCode ?: ""
                val employmentItem = employment?.itemCode ?: ""
                val sourceOfFundsItem = sourceOfFunds?.itemCode ?: ""

                flexiFuneralViewModel.applyForFlexiFuneralData.employmentStatus = employment
                flexiFuneralViewModel.applyForFlexiFuneralData.occupation = occupation
                flexiFuneralViewModel.applyForFlexiFuneralData.sourceOfFund = sourceOfFunds

                val riskProfileDetails = RiskProfileDetails(flexiFuneralViewModel.flexiFuneralData.casaReference, occupationItem, employmentItem, "27", "AFFP", "096", sourceOfFundsItem)
                riskBasedApproachViewModel.riskProfileResponse = MutableLiveData()
                riskBasedApproachViewModel.fetchRiskProfile(riskProfileDetails)

                riskBasedApproachViewModel.riskProfileResponse.observe(viewLifecycleOwner, {
                    AnalyticsUtil.trackAction("RBAStatus", "RBAStatus " + it.riskRating)
                    if (it?.riskRating.isNullOrEmpty()) {
                        showGenericErrorMessageThenFinish()
                    } else if (it?.riskRating == "H" || it?.riskRating == "VH") {
                        AnalyticsUtil.trackAction("WIMI_FlexiFuneral", "FlexiFuneral_RBAError_ScreenDisplayed")
                        startActivity(IntentFactory.getFailureResultScreen(activity, R.string.unable_to_continue, R.string.risk_based_approach_failure_message))
                    } else {

                        if (isBeneficiarySelected) {
                            navigate(FlexiFuneralDetailsFragmentDirections.actionFlexiFuneralDebitOrderDetailsFragmentToFlexiFuneralBeneficiaryDetailsFragment())
                        } else {
                            flexiFuneralViewModel.addBeneficiary(getCustomerDetails())
                        }
                    }
                    dismissProgressDialog()
                    riskBasedApproachViewModel.riskProfileResponse.removeObservers(this)
                })
            }
            saveDebitDetails()
        }
    }

    private fun checkEmploymentStatus(occupationStatusCode: String) {
        if (occupationStatusCode.contains(Regex("01|02|03|08|09"))) {
            occupationNormalInputView.visibility = View.VISIBLE
        } else {
            flexiFuneralViewModel.applyForFlexiFuneralData.occupation?.engCodeDescription = ""
            occupationNormalInputView.visibility = View.GONE
        }
    }

    private fun isValidInput(): Boolean {
        when {
            debitDayNormalInputView.selectedValue.isEmpty() -> debitDayNormalInputView.setError(getString(R.string.flexi_funeral_debit_day_error))
            accountNormalInputView.selectedValue.isEmpty() -> accountNormalInputView.setError(getString(R.string.flexi_funeral_account_to_debit_error))
            employmentStatusNormalInputView.selectedValue.isEmpty() -> employmentStatusNormalInputView.setError(R.string.risk_based_approach_employment_status_error_message)
            occupationNormalInputView.visibility == View.VISIBLE && occupationNormalInputView.selectedValue.isEmpty() -> occupationNormalInputView.setError(R.string.risk_based_approach_occupation_error_message)
            sourceOfFundsNormalInputView.selectedValue.isEmpty() -> sourceOfFundsNormalInputView.setError(R.string.risk_based_approach_source_of_funds_error_message)
            nameOfInsurerNormalInputView.visibility == View.VISIBLE && nameOfInsurerNormalInputView.selectedValue.isEmpty() -> nameOfInsurerNormalInputView.setError(getString(R.string.flexi_funeral_name_of_insurer_error))
            else -> return true
        }
        return false
    }

    private fun showDayPicker() {
        val datePickerDialogFragment = DayPickerDialogFragment.newInstance(emptyArray())
        datePickerDialogFragment.onDateItemSelectionListener = object : DayPickerDialogFragment.OnDateItemSelectionListener {
            override fun onDateItemSelected(day: String) {
                debitDayNormalInputView.selectedValue = day
            }
        }
        datePickerDialogFragment.show(childFragmentManager, "dialog")
    }

    private fun getCustomerDetails(): FlexiFuneralBeneficiaryDetails {
        val deceasedEstateDetails = FlexiFuneralBeneficiaryDetails().apply {
            titleInEnglish = ESTATE_LATE
            title = ESTATE_LATE
            firstName = customerInformation.firstName.toString()
            surname = customerInformation.lastName.toString()
            relationship = UNKNOWN
            initials = customerInformation.initials.toString()
            dateOfBirth = DateUtils.formatDate(customerInformation.dateOfBirth.toString(), "yyyyMMdd", DateUtils.DASHED_DATE_PATTERN)
        }
        flexiFuneralViewModel.flexiFuneralBeneficiaryDetails = deceasedEstateDetails
        return deceasedEstateDetails
    }

    private fun saveDebitDetails() {
        flexiFuneralViewModel.applyForFlexiFuneralData.apply {
            dayOfDebit = debitDayNormalInputView.selectedValue
            planCode = flexiFuneralViewModel.multipleDependentsDetails.planCode
            isReplacement = if (terminatedPolicyRadioButtonView.selectedIndex == 0) TRUE else FALSE
            company = if (nameOfInsurerNormalInputView.isVisible) nameOfInsurerNormalInputView.selectedValue else ""
        }
    }
}