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
package com.barclays.absa.banking.funeralCover.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.*
import com.barclays.absa.banking.boundary.model.CustomerProfileObject.Companion.instance
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants.EXERGY_POLICY_TYPE
import com.barclays.absa.banking.funeralCover.services.dto.RetailAccount
import com.barclays.absa.banking.payments.ChooseBankListActivity
import com.barclays.absa.banking.payments.ChooseBranchListActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.shared.services.SharedViewModel
import com.barclays.absa.banking.shared.services.dto.LookupItem
import com.barclays.absa.banking.ultimateProtector.ui.DayPickerDialogFragment.Companion.newInstance
import com.barclays.absa.banking.ultimateProtector.ui.DayPickerDialogFragment.OnDateItemSelectionListener
import com.barclays.absa.utils.AnalyticsUtil.trackAction
import com.barclays.absa.utils.KeyboardUtils.hideKeyboard
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.fragment_edit_policy_payment_details.*
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.forms.validation.FieldRequiredValidationRule
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import styleguide.forms.validation.addValidationRule
import styleguide.utils.extensions.toTitleCase
import java.lang.ref.WeakReference

class EditPolicyPaymentDetailsFragment : BaseFragment(R.layout.fragment_edit_policy_payment_details), EditPolicyPaymentDetailsView {

    private lateinit var editPolicyPaymentDetailsPresenter: EditPolicyPaymentDetailsPresenter
    private lateinit var changePaymentDetails: ChangePaymentDetails
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var bankDetails: BankDetails
    private lateinit var bankBranches: BankBranches
    private lateinit var accountTypesList: SelectorList<StringItem>
    private var retailAccountsList: List<RetailAccount>? = null
    private var memberName: String = ""
    private var selectedBankName: String? = null
    private var selectedIndex = 0
    private var isExergyPolicy = false
    private var differentBankSelected = false
    private var bankList = ArrayList<String>()
    private var branchList: ArrayList<Branch> = ArrayList()
    private var exergyBankListResponse: ExergyBankListResponse? = null

    companion object {
        const val ABSA_BANK_NAME = "Absa Bank Limited"
        const val BANK_LIST = "bank_list"
        const val BANK_NAME = "bankName"
        const val ABSA_BANK = "ABSA BANK"
        const val ABSA = "ABSA"
        const val ABSA_BRANCH_CODE = "632005"
        const val BRANCH_NAME = "branchName"
        const val BRANCH_CODE = "branchCode"
        const val RESULT = "RESULT"
        const val REQUEST_CODE_FOR_BANK_NAME = 3000
        const val REQUEST_CODE_FOR_BRANCH_NAME = 4000
        const val IS_EXERGY_POLICY = "is_exergy_policy"
        private const val DEFAULT_SOURCE_OF_FUNDS = "20"

        @JvmStatic
        fun newInstance() = EditPolicyPaymentDetailsFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedViewModel = viewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changePaymentDetails = ChangePaymentDetails()
        val policyDetail = appCacheService.getPolicyDetail()
        isExergyPolicy = EXERGY_POLICY_TYPE == policyDetail?.policy?.type

        policyDetail?.accountInfo?.let {
            with(changePaymentDetails) {
                accountType = it.accountType.toString()
                accountNumber = it.accountNumber.toString()
                bankName = it.bankName.toString()
                nextPremiumDate = it.nextPremiumDate.toString()
                branchCode = it.branchCode.toString()
                branchName = it.branchName.toString()
                itemCode = it.sourceOfFund.toString()
                sourceOfFund = it.sourceOfFund.toString()
                bankId = it.bankId
                branchId = it.branchId
            }
        }
    }

    override val baseActivity = activity as? InsuranceManagePaymentDetailsActivity ?: BMBApplication.getInstance().topMostActivity as InsuranceManagePaymentDetailsActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(baseActivity) {
            setToolBarBack(getString(R.string.edit_payment_details))
            adjustProgressIndicator(0, 1)
            progressIndicatorView.visibility = View.VISIBLE
        }

        accountTypesList = SelectorList<StringItem>().apply { addAll(resources.getStringArray(R.array.account_types).map { StringItem(it) }) }

        memberName = instance.customerName ?: ""
        editPolicyPaymentDetailsPresenter = EditPolicyPaymentDetailsPresenter(WeakReference(this))
        editPolicyPaymentDetailsPresenter.loadRetailsAccounts()
        populateBankDetails()
        addValidationRules()
        setUpOnClickListeners()
        setUpItemSelectionInterfaces()
        externalBankAccountToBeDebitedEditText.addRequiredValidationHidingTextWatcher()
        accountHolderInputView.addRequiredValidationHidingTextWatcher()
        trackAction("Insurance_Hub", InsurancePolicyClaimsBaseActivity.policyType.value + "_EditPaymentDetailsScreen_ScreenDisplayed")
    }

    private fun setUpOnClickListeners() {
        bankNameInputView.setOnClickListener {
            if (bankList.isEmpty()) {
                editPolicyPaymentDetailsPresenter.onBankNameInputViewClicked(isExergyPolicy)
            } else {
                navigateToChooseBankListActivity()
            }
            bankNameInputView.hideError()
            trackAction("Insurance_Hub", InsurancePolicyClaimsBaseActivity.policyType.value + "_EditPaymentDetailsScreen_BankClicked")
        }

        editPaymentContinueButton.setOnClickListener {
            validatePaymentDetailsInputData()
            trackAction("Insurance_Hub", InsurancePolicyClaimsBaseActivity.policyType.value + "_EditPaymentDetailsScreen_ContinueButtonClicked")
        }

        dayOfDebitInputView.setOnClickListener { showDayPicker() }

        branchCodeInputView.setOnClickListener {
            if (branchList.isEmpty() || differentBankSelected) {
                differentBankSelected = false
                editPolicyPaymentDetailsPresenter.onBranchInputViewClicked(selectedBankName, isExergyPolicy)
            } else {
                navigateToChooseBranchListActivity()
            }
            trackAction("Insurance_Hub", InsurancePolicyClaimsBaseActivity.policyType.value + "_EditPaymentDetailsScreen_BranchClicked")
        }
    }

    private fun setUpItemSelectionInterfaces() {
        accountToBeDebitedInputView.setItemSelectionInterface { index: Int -> setAccountToDebit(index) }
        accountTypeInputView.setItemSelectionInterface { setAccountType() }

        sourceOfFundsInputView.setItemSelectionInterface {
            val lookupItem = sourceOfFundsInputView.selectedItem as LookupItem
            selectedIndex = sourceOfFundsInputView.selectedIndex
            changePaymentDetails.sourceOfFund = lookupItem.defaultLabel.toString()
            changePaymentDetails.itemCode = lookupItem.itemCode.toString()
            sourceOfFundsInputView.clearError()
        }
    }

    private fun setAccountToDebit(index: Int) {
        val selectedAccount = retailAccountsList?.get(index)
        with(changePaymentDetails) {
            accountType = selectedAccount?.accountType.toString()
            accountNumber = selectedAccount?.accountNumber.toString()
            accountHolderName = memberName
        }
        accountToBeDebitedInputView.setValueEditable(true)
        accountToBeDebitedInputView.clearError()
    }

    private fun setAccountType() {
        setAccountTypeFlag(accountTypeInputView.selectedValue)
        accountTypeInputView.clearError()
    }

    private fun validatePaymentDetailsInputData() {
        appCacheService.getPolicyDetail()?.policy?.let {
            prepareChangedPaymentData(it.number)
            if (isAbsaBankAccount(selectedBankName) && isValidAbsaBankData() || !isAbsaBankAccount(selectedBankName) && isValidExternalBankData()) {
                hideKeyboard(baseActivity)
                navigateToConfirmationScreen()
            }
        }
    }

    private fun prepareChangedPaymentData(policyNumber: String?) {
        with(changePaymentDetails) {
            accountHolderName = memberName
            dayOfDebit = dayOfDebitInputView.text.toString()
            this.policyNumber = policyNumber.toString()
        }

        if (!isAbsaBankAccount(selectedBankName)) {
            changePaymentDetails.accountNumber = externalBankAccountToBeDebitedEditText.text.toString()
            setAccountTypeFlag(accountTypeInputView.text)
            changePaymentDetails.accountHolderName = accountHolderInputView.text.toString()
            changePaymentDetails.bankName = bankNameInputView.text.toString()
        } else {
            setAbsaBranchName()
        }
        appCacheService.setChangePaymentDetails(changePaymentDetails)
    }

    private fun isAbsaBankAccount(bankName: String?) = ABSA_BANK.equals(bankName, ignoreCase = true) || ABSA.equals(bankName, ignoreCase = true) || ABSA_BANK_NAME.equals(bankName, ignoreCase = true)

    private fun populateBankDetails() {
        selectedBankName = changePaymentDetails.bankName
        if (isAbsaBankAccount(changePaymentDetails.bankName)) {
            populateInternalBankingView()
        } else {
            populateExternalBankingView()
        }
    }

    private fun populateExternalBankingView() {
        bankNameInputView.text = changePaymentDetails.bankName
        val branchLabel = "${changePaymentDetails.branchCode} - ${changePaymentDetails.branchName}"
        branchCodeInputView.text = branchLabel.toTitleCase()
        val accountType = baseActivity.accountDescription[changePaymentDetails.accountType] ?: ""
        accountTypeInputView.text = accountType
        accountHolderInputView.text = memberName
        accountToBeDebitedInputView.text = changePaymentDetails.accountNumber
        accountTypeInputView.setList(accountTypesList, getString(R.string.select_account_type))
        accountToBeDebitedInputView.visibility = View.GONE
        externalBankConstraintLayout.visibility = View.VISIBLE
        externalBankAccountToBeDebitedEditText.visibility = View.VISIBLE
        externalBankAccountToBeDebitedEditText.text = changePaymentDetails.accountNumber
        dayOfDebitInputView.text = getDayOfDebit(changePaymentDetails.nextPremiumDate)
    }

    private fun populateInternalBankingView() {
        hideViewsForExternalBank()
        if (changePaymentDetails.accountType.isNotEmpty() && changePaymentDetails.accountNumber.isNotEmpty()) {
            val accountNumberDisplayLabel = "${baseActivity.accountDescription[changePaymentDetails.accountType]} - ${changePaymentDetails.accountNumber}"
            accountToBeDebitedInputView.text = accountNumberDisplayLabel
        } else {
            accountHolderInputView.clear()
        }
        bankNameInputView.text = changePaymentDetails.bankName.toTitleCase()
        dayOfDebitInputView.text = getDayOfDebit(changePaymentDetails.nextPremiumDate)
    }

    private fun getDayOfDebit(premiumDate: String): String = if (premiumDate.length >= 2) premiumDate.substring(premiumDate.length - 2) else ""

    private fun navigateToConfirmationScreen() {
        baseActivity.startFragment(PolicyPaymentDetailsConfirmationFragment.newInstance(changePaymentDetails), true, BaseActivity.AnimationType.FADE)
    }

    override fun displayListOfBankAccounts(successResponse: BankDetails) {
        bankList = successResponse.bankList
        bankDetails = successResponse
        if (bankList.isNotEmpty()) {
            navigateToChooseBankListActivity()
        }
    }

    override fun displayListOfExergyBanks(successResponse: ExergyBankListResponse) {
        bankList = successResponse.bankNameList
        exergyBankListResponse = successResponse
        if (bankList.isNotEmpty()) {
            navigateToChooseBankListActivity()
        }
    }

    private fun navigateToChooseBankListActivity() {
        val chooseBankIntent = Intent(baseActivity, ChooseBankListActivity::class.java).apply {
            putExtra(BANK_LIST, if (isExergyPolicy) exergyBankListResponse else bankDetails)
            putExtra(IS_EXERGY_POLICY, isExergyPolicy)
        }
        startActivityForResult(chooseBankIntent, REQUEST_CODE_FOR_BANK_NAME)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_FOR_BANK_NAME -> {
                    differentBankSelected = selectedBankName != data?.getStringExtra(BANK_NAME)
                    selectedBankName = data?.getStringExtra(BANK_NAME)
                    accountToBeDebitedInputView.clear()
                    bankNameInputView.text = selectedBankName
                    bankNameInputView.hideError()
                    if (isAbsaBankAccount(selectedBankName)) {
                        setAbsaBranchName()
                        hideViewsForExternalBank()
                    } else {
                        changePaymentDetails.bankName = selectedBankName.toString()
                        showViewsForExternalBank()
                    }
                }
                REQUEST_CODE_FOR_BRANCH_NAME -> if (data != null) {
                    val branchName = data.getStringExtra(BRANCH_NAME)
                    val branchCode = data.getStringExtra(BRANCH_CODE)
                    branchCodeInputView.text = "$branchName - $branchCode"
                    branchCodeInputView.hideError()
                    with(changePaymentDetails) {
                        this.branchName = branchName.toString()
                        this.branchCode = branchCode.toString()
                        bankId = data.getStringExtra(ChangePolicyPaymentRequestParameters.BANK_ID).toString()
                        branchId = data.getStringExtra(ChangePolicyPaymentRequestParameters.BRANCH_ID).toString()
                    }
                }
            }
        }
    }

    private fun setAbsaBranchName() {
        with(changePaymentDetails) {
            branchName = ABSA_BRANCH_CODE
            bankName = ABSA
            branchCode = ABSA_BRANCH_CODE
        }
    }

    private fun showViewsForExternalBank() {
        externalBankConstraintLayout.visibility = View.VISIBLE
        accountToBeDebitedInputView.visibility = View.GONE
        accountToBeDebitedInputView.clear()
        branchCodeInputView.text = ""
        accountTypeInputView.setList(accountTypesList, getString(R.string.select_account_type))
    }

    private fun hideViewsForExternalBank() {
        externalBankConstraintLayout.visibility = View.GONE
        accountToBeDebitedInputView.visibility = View.VISIBLE
    }

    override fun showSomethingWentWrongScreen() {
        startActivity(IntentFactory.getSomethingWentWrongScreen(baseActivity, R.string.claim_error_text, R.string.connectivity_maintenance_message))
    }

    override fun displaySourceOfFunds(lookupItems: List<LookupItem>) {
        if (lookupItems.isNotEmpty()) {
            val sortedSelectorList = sharedViewModel.buildSortedSelectorList(lookupItems)
            sourceOfFundsInputView.setList(sortedSelectorList, getString(R.string.source_of_funds))
            val sourceOfFundsItem = if (changePaymentDetails.sourceOfFund.isEmpty()) DEFAULT_SOURCE_OF_FUNDS else changePaymentDetails.itemCode
            selectedIndex = sharedViewModel.getMatchingLookupIndex(sourceOfFundsItem, sortedSelectorList)
            sourceOfFundsInputView.selectedIndex = selectedIndex
            val lookupItem = sourceOfFundsInputView.selectedItem as LookupItem?
            changePaymentDetails.sourceOfFund = lookupItem?.defaultLabel.toString()
            changePaymentDetails.itemCode = lookupItem?.itemCode.toString()
        } else {
            showSomethingWentWrongScreen()
        }
    }

    override fun displayRetailAccounts(retailAccountsList: ArrayList<RetailAccount>) {
        val accountToDebitItems = SelectorList<StringItem>()
        if (retailAccountsList.isNotEmpty()) {
            this.retailAccountsList = retailAccountsList
            retailAccountsList.forEach {
                accountToDebitItems.add(StringItem().apply { item = "${it.accountDescription} - ${it.accountNumber}" })
            }
            accountToBeDebitedInputView.setList(accountToDebitItems, getString(R.string.account_to_debit))
        }
    }

    private fun showDayPicker() {
        val disabledDates = if (!isExergyPolicy) arrayOf("17", "18", "19") else arrayOf()
        val dayPickerDialogFragment = newInstance(disabledDates)
        dayPickerDialogFragment.onDateItemSelectionListener = object : OnDateItemSelectionListener {
            override fun onDateItemSelected(day: String) {
                dayOfDebitInputView.text = day
                dayOfDebitInputView.clearError()
                changePaymentDetails.dayOfDebit = day
            }
        }
        dayPickerDialogFragment.show(childFragmentManager, "dialog")
    }

    override fun displayListOfBankBranches(bankBranches: BankBranches) {
        branchList = bankBranches.branchList as ArrayList<Branch>
        this.bankBranches = bankBranches
        if (!branchList.isNullOrEmpty()) {
            navigateToChooseBranchListActivity()
        }
    }

    override fun displayListOfExergyBankBranches(successResponse: ExergyBranchListResponse) {
        val exergyBranchDetailsList: List<ExergyBranchDetails> = successResponse.branchList
        bankBranches = BankBranches()
        branchList = ArrayList()
        for (exergyBranchDetails in exergyBranchDetailsList) {
            val branch = Branch().apply {
                branchName = exergyBranchDetails.branchName
                branchCode = exergyBranchDetails.branchCode
                immediatePaymentAllowed = ""
                bankId = exergyBranchDetails.bankId
                branchId = exergyBranchDetails.branchId
            }
            branchList.add(branch)
        }
        if (exergyBranchDetailsList.isNotEmpty()) {
            bankBranches.bankName = successResponse.branchList.first().bankName
            bankBranches.branchList = branchList
            navigateToChooseBranchListActivity()
        }
    }

    private fun navigateToChooseBranchListActivity() {
        Intent(baseActivity, ChooseBranchListActivity::class.java).apply {
            this.putExtra(RESULT, bankBranches)
            this.putExtra(IS_EXERGY_POLICY, isExergyPolicy)
            startActivityForResult(this, REQUEST_CODE_FOR_BRANCH_NAME)
        }
    }

    private fun setAccountTypeFlag(accountType: String?) {
        val accountFlag = when {
            getString(R.string.current_cheque_account).equals(accountType, ignoreCase = true) || getString(R.string.chequeAccount).equals(accountType, ignoreCase = true) -> "1"
            getString(R.string.savings_account).equals(accountType, ignoreCase = true) -> "2"
            getString(R.string.transmission_account).equals(accountType, ignoreCase = true) -> "3"
            getString(R.string.bond_account).equals(accountType, ignoreCase = true) -> "4"
            getString(R.string.credit_card_account).equals(accountType, ignoreCase = true) -> "5"
            else -> "0"
        }
        changePaymentDetails.accountType = accountFlag
    }

    private fun addValidationRules() {
        bankNameInputView.addValidationRule(FieldRequiredValidationRule(R.string.payment_required_field_error))
        accountToBeDebitedInputView.addValidationRule(FieldRequiredValidationRule(R.string.payment_required_field_error))
        dayOfDebitInputView.addValidationRule(FieldRequiredValidationRule(R.string.payment_required_field_error))
        sourceOfFundsInputView.addValidationRule(FieldRequiredValidationRule(R.string.payment_required_field_error))
        branchCodeInputView.addValidationRule(FieldRequiredValidationRule(R.string.payment_required_field_error))
        accountTypeInputView.addValidationRule(FieldRequiredValidationRule(R.string.payment_required_field_error))
        accountHolderInputView.addValidationRule(FieldRequiredValidationRule(R.string.payment_required_field_error))
        externalBankAccountToBeDebitedEditText.addValidationRule(FieldRequiredValidationRule(R.string.payment_required_field_error))
    }

    private fun isValidAbsaBankData(): Boolean = bankNameInputView.validate() && accountToBeDebitedInputView.validate() && dayOfDebitInputView.validate() && sourceOfFundsInputView.validate()

    private fun isValidExternalBankData(): Boolean = bankNameInputView.validate() && branchCodeInputView.validate() && accountTypeInputView.validate() && accountHolderInputView.validate() && externalBankAccountToBeDebitedEditText.validate() && dayOfDebitInputView.validate() && sourceOfFundsInputView.validate()

}