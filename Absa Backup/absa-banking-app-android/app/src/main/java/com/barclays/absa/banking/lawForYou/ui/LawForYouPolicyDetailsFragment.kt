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
package com.barclays.absa.banking.lawForYou.ui

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import android.widget.DatePicker
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.funeralCover.services.dto.RetailAccount
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskProfileDetails
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskProfileResponse
import com.barclays.absa.banking.shared.services.dto.LookupItem
import com.barclays.absa.banking.ultimateProtector.ui.DayPickerDialogFragment
import com.barclays.absa.banking.ultimateProtector.ui.DayPickerDialogFragment.Companion.newInstance
import com.barclays.absa.utils.*
import com.barclays.absa.utils.extensions.toSelectorList
import kotlinx.android.synthetic.main.law_for_you_policy_details_fragment.*
import styleguide.forms.NormalInputView
import styleguide.forms.SelectorInterface
import styleguide.forms.SelectorList
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import styleguide.utils.extensions.toFormattedAccountNumber
import styleguide.utils.extensions.toSentenceCase
import styleguide.utils.extensions.toTitleCase
import java.util.*

class LawForYouPolicyDetailsFragment : LawForYouBaseFragment(R.layout.law_for_you_policy_details_fragment) {

    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var lawForYouPolicyViewModel: LawForYouPolicyDetailsViewModel
    private var sortedOccupationStatusSelectorList = SelectorList<LookupItem>()
    private var sortedOccupationSelectorList = SelectorList<LookupItem>()
    private var sortedSourceOfFundsSelectorList = SelectorList<LookupItem>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lawForYouPolicyViewModel = viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.details)
        lawForYouActivity.setProgressStep(3)

        continueButton.setOnClickListener { navigateOnTermsAgreement() }
        CommonUtils.makeTextClickable(context, R.string.law_for_you_agree_debit_order_terms,
                R.string.law_for_you_debit_order_terms, iAgreePolicyDetailsCheckBoxView.checkBoxTextView,
                R.color.graphite, object : ClickableSpan() {
            override fun onClick(widget: View) {
                AnalyticsUtil.trackAction("Law For You", "LawForYou_DebitOrderTCPDF_ScreenDisplayed")
                PdfUtil.showPDFInApp(lawForYouActivity, lawForYouViewModel.applyLawForYou.debitOrderAuthorizationTermsAndConditionsPdfUrl)
            }
        })

        debitDayNormalInputView.selectedValue = lawForYouViewModel.lawForYouDetails.dayOfDebit
        policyDateNormalInputView.addRequiredValidationHidingTextWatcher()
        debitDayNormalInputView.addRequiredValidationHidingTextWatcher()
        accountSelectNormalInputView.addRequiredValidationHidingTextWatcher()
        occupationStatusNormalInputView.addRequiredValidationHidingTextWatcher()
        occupationNormalInputView.addRequiredValidationHidingTextWatcher()
        sourceOfFundsNormalInputView.addRequiredValidationHidingTextWatcher()

        debitDayNormalInputView.setOnClickListener { showDayPicker() }

        lawForYouPolicyViewModel.retailAccountListMutableLiveData.observe(viewLifecycleOwner, {
            setupAccountSelector(it)
        })

        lawForYouPolicyViewModel.occupationStatusListMutableLiveData.observe(viewLifecycleOwner, {
            setupOccupationStatusSelector(it)
        })

        lawForYouPolicyViewModel.occupationListMutableLiveData.observe(viewLifecycleOwner, {
            setOccupationSelector(it)
        })

        lawForYouPolicyViewModel.sourceOfFundsListMutableLiveData.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            setupSourceOfFundsSelector(it)
        })

        lawForYouViewModel.riskProfileResponseMutableLiveData.observe(viewLifecycleOwner, {
            lawForYouViewModel.riskProfileResponseMutableLiveData = MutableLiveData()
            dismissProgressDialog()
            navigateOnRiskProfile(it)
        })

        occupationStatusNormalInputView.setItemSelectionInterface {
            checkOccupationStatus()
        }

        setupDateSelector(policyDateNormalInputView)
        policyDateNormalInputView.setOnClickListener { datePickerDialog.show() }

        AnalyticsUtil.trackAction("Law For You", "LawForYou_Details_ScreenDisplayed")
    }

    override fun onDestroyView() {
        lawForYouViewModel.riskProfileResponseMutableLiveData.removeObservers(viewLifecycleOwner)
        lawForYouPolicyViewModel.getMutableLiveDataList().forEach { it.removeObservers(viewLifecycleOwner) }
        super.onDestroyView()
    }

    private fun showDayPicker() {
        val dayPickerDialogFragment = newInstance(emptyArray())
        dayPickerDialogFragment.onDateItemSelectionListener = object : DayPickerDialogFragment.OnDateItemSelectionListener {
            override fun onDateItemSelected(day: String) {
                debitDayNormalInputView.selectedValue = day
            }
        }
        dayPickerDialogFragment.show(childFragmentManager, "dialog")
    }

    private fun setupDateSelector(inputView: NormalInputView<*>) {
        datePickerDialog = DatePickerDialog(lawForYouActivity, R.style.DatePickerDialogTheme, { _: DatePicker?, year: Int, month: Int, day: Int ->
            lawForYouPolicyViewModel.selectedPolicyDate[year, month] = day
            inputView.selectedValue = DateUtils.format(lawForYouPolicyViewModel.selectedPolicyDate.time, DateUtils.DATE_DISPLAY_PATTERN)
        }, lawForYouPolicyViewModel.selectedPolicyDate[Calendar.YEAR], lawForYouPolicyViewModel.selectedPolicyDate[Calendar.MONTH], lawForYouPolicyViewModel.selectedPolicyDate[Calendar.DAY_OF_MONTH])

        datePickerDialog.apply {
            datePicker.minDate = Calendar.getInstance().timeInMillis
            datePicker.maxDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 60) }.timeInMillis
        }
    }

    private fun setupAccountSelector(retailAccountList: List<RetailAccount>) {
        val accountSelectorList = retailAccountList.toSelectorList { account -> AccountItem(account) }
        accountSelectNormalInputView.setList(accountSelectorList, getString(R.string.account_to_debit))

        accountSelectNormalInputView.setItemSelectionInterface {
            val selectedAccount = retailAccountList[it]
            accountSelectNormalInputView.selectedValue =
                    "${selectedAccount.accountDescription.toSentenceCase()} (${selectedAccount.accountNumber})"
        }

        if (retailAccountList.size == 1) {
            accountSelectNormalInputView.selectedIndex = 0
            val selectedAccount = retailAccountList[0]
            accountSelectNormalInputView.selectedValue = "${selectedAccount.accountDescription.toSentenceCase()} (${selectedAccount.accountNumber})"
        }
    }

    private fun setOccupationSelector(listItems: List<LookupItem>) {
        val selectedOccupationCode = if (lawForYouViewModel.selectedOccupation?.defaultLabel.isNullOrBlank()) {
            lawForYouViewModel.personalInformationResponseMutableLiveData.value?.customerInformation?.employmentInformation?.occupation.toString()
        } else {
            lawForYouViewModel.selectedOccupation?.itemCode ?: ""
        }
        setupSelectorList(listItems, selectedOccupationCode, occupationNormalInputView, R.string.law_for_you_occupation)
    }

    private fun setupOccupationStatusSelector(listItems: List<LookupItem>) {
        val selectedOccupationStatusCode = if (lawForYouViewModel.selectedOccupationStatus?.defaultLabel.isNullOrBlank()) {
            lawForYouViewModel.personalInformationResponseMutableLiveData.value?.customerInformation?.employmentInformation?.occupationStatus.toString()
        } else {
            lawForYouViewModel.selectedOccupationStatus?.itemCode ?: ""
        }
        setupSelectorList(listItems, selectedOccupationStatusCode, occupationStatusNormalInputView, R.string.law_for_you_employment_status_title)
        checkOccupationStatus()
    }

    private fun setupSourceOfFundsSelector(listItems: List<LookupItem>) {
        val selectedSourceOfFundsCode = if (lawForYouViewModel.selectedSourceOfFunds?.defaultLabel.isNullOrBlank()) {
            lawForYouViewModel.personalInformationResponseMutableLiveData.value?.customerInformation?.sourceOfIncome ?: ""
        } else {
            lawForYouViewModel.selectedSourceOfFunds?.itemCode ?: ""
        }
        setupSelectorList(listItems, selectedSourceOfFundsCode, sourceOfFundsNormalInputView, R.string.law_for_you_source_of_funds_title)
    }

    private fun setupSelectorList(listItems: List<LookupItem>, selectedItemCode: String, normalInputView: NormalInputView<*>, @StringRes toolbarTitleResId: Int) {
        val toolbarTitle = getString(toolbarTitleResId)
        val sortedList = listItems.sortedBy { item -> item.defaultLabel }
        val selectorList: SelectorList<LookupItem> = sortedList.toSelectorList { item -> LookupItem(item.itemCode, item.groupCode, item.defaultLabel.toTitleCase(), item.cmsKey) }
        normalInputView.setList(selectorList, toolbarTitle)
        normalInputView.selectedIndex = sortedList.indexOfFirst { lookupItem -> lookupItem.itemCode.equals(selectedItemCode) }

        when (normalInputView) {
            occupationStatusNormalInputView -> sortedOccupationStatusSelectorList = selectorList
            occupationNormalInputView -> sortedOccupationSelectorList = selectorList
            sourceOfFundsNormalInputView -> sortedSourceOfFundsSelectorList = selectorList
        }
    }

    private fun navigateOnTermsAgreement() {
        if (!hasValidFields()) return

        if (iAgreePolicyDetailsCheckBoxView.isChecked) {
            lawForYouViewModel.apply {
                selectedOccupation = if (occupationNormalInputView.isVisible) {
                    sortedOccupationSelectorList[occupationNormalInputView.selectedIndex]
                } else {
                    null
                }
                selectedSourceOfFunds = sortedSourceOfFundsSelectorList[sourceOfFundsNormalInputView.selectedIndex]
                selectedOccupationStatus = sortedOccupationStatusSelectorList[occupationStatusNormalInputView.selectedIndex]
                selectedRetailAccount = lawForYouPolicyViewModel.retailAccountListMutableLiveData.value!![accountSelectNormalInputView.selectedIndex]
                val riskProfileDetails = RiskProfileDetails(casaReference, selectedOccupation?.itemCode ?: "",
                        selectedOccupationStatus?.itemCode ?: "", "BLIFE",
                        "UL01", "096", selectedSourceOfFunds?.itemCode ?: "")
                lawForYouDetails.apply {
                    inceptionDate = DateUtils.formatDate(policyDateNormalInputView.selectedValue, DateUtils.DATE_DISPLAY_PATTERN, DateUtils.DASHED_DATE_PATTERN)
                    dayOfDebit = debitDayNormalInputView.selectedValue
                    accountToBeDebited = "${selectedRetailAccount.accountType} - ${selectedRetailAccount.accountNumber}"
                    businessSourceIndicator = selectedSourceOfFunds?.displayValue.toString()
                }

                lawForYouViewModel.requestRiskProfile(riskProfileDetails)
            }
        } else {
            iAgreePolicyDetailsCheckBoxView.setErrorMessage(getString(R.string.please_accept_terms_and_conditions))
        }
    }

    private fun hasValidFields(): Boolean {
        when {
            policyDateNormalInputView.selectedValue.isEmpty() -> policyDateNormalInputView.setError(R.string.law_for_you_please_select_date)
            debitDayNormalInputView.selectedValue.isEmpty() -> debitDayNormalInputView.setError(R.string.law_for_you_please_select_debit_day)
            accountSelectNormalInputView.selectedValue.isEmpty() -> accountSelectNormalInputView.setError(R.string.law_for_you_please_select_account_to_debit)
            occupationStatusNormalInputView.selectedValue.isEmpty() -> occupationStatusNormalInputView.setError(R.string.law_for_you_please_select_employment_status)
            occupationNormalInputView.selectedValue.isEmpty() && occupationNormalInputView.isVisible -> occupationNormalInputView.setError(R.string.law_for_you_please_select_occupation)
            sourceOfFundsNormalInputView.selectedValue.isEmpty() -> sourceOfFundsNormalInputView.setError(R.string.law_for_you_please_select_source_of_funds)
            else -> return true
        }
        return false
    }

    private fun checkOccupationStatus() {
        val occupationStatusItemCode = sortedOccupationStatusSelectorList.get(occupationStatusNormalInputView.selectedIndex).itemCode ?: "-1"
        val hasOccupation = !occupationStatusItemCode.contains(Regex("04|05|06|07|10"))
        occupationNormalInputView.visibility = if (hasOccupation) {
            View.VISIBLE
        } else {
            occupationNormalInputView.clear()
            View.GONE
        }
    }

    private fun navigateOnRiskProfile(riskProfileResponse: RiskProfileResponse) {
        val isAcceptableRisk = riskProfileResponse.riskRating.toLowerCase(Locale.getDefault()).contains(Regex("l|vl|m"))
        if (isAcceptableRisk) {
            navigate(LawForYouPolicyDetailsFragmentDirections.actionLawForYouPolicyDetailsFragmentToLawForYouConfirmationFragment())
        } else {
            navigateRBAError()
        }
    }

    private fun navigateRBAError() =
            startActivity(IntentFactory.getUnableToContinueScreen(lawForYouActivity,
                    R.string.unable_to_continue, R.string.risk_based_approach_failure_message))

    class AccountItem(private val retailAccount: RetailAccount) : SelectorInterface {
        override val displayValue: String?
            get() = retailAccount.accountDescription.toSentenceCase()

        override val displayValueLine2: String?
            get() = retailAccount.accountNumber.toFormattedAccountNumber()
    }
}