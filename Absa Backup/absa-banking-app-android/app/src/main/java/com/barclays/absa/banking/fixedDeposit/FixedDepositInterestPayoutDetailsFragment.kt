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

package com.barclays.absa.banking.fixedDeposit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.PayBeneficiaryPaymentObject
import com.barclays.absa.banking.card.ui.creditCard.vcl.BaseVCLFragment.REQUEST_CODE_FOR_BANK_NAME
import com.barclays.absa.banking.card.ui.creditCard.vcl.BaseVCLFragment.REQUEST_CODE_FOR_BRANCH_NAME
import com.barclays.absa.banking.fixedDeposit.FixedDepositOpenAccountFragment.Companion.FIXED_DEPOSIT_PRODUCT_CODE
import com.barclays.absa.banking.fixedDeposit.FixedDepositReinvestInstructionsFragment.Companion.NO_INDICATOR
import com.barclays.absa.banking.fixedDeposit.FixedDepositReinvestInstructionsFragment.Companion.YES_INDICATOR
import com.barclays.absa.banking.fixedDeposit.services.dto.FixedDeposit
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.data.ApplicationFlowType
import com.barclays.absa.banking.payments.ChooseBankListActivity
import com.barclays.absa.banking.payments.ChooseBranchListActivity
import com.barclays.absa.banking.payments.PaymentsConstants
import com.barclays.absa.utils.AbsaCacheManager
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.TextFormatUtils
import com.barclays.absa.utils.extensions.getAccountDisplayDescriptionFromAccountNumber
import com.barclays.absa.utils.extensions.toSelectorList
import kotlinx.android.synthetic.main.fixed_deposit_interest_payout_details_fragment.*
import kotlinx.android.synthetic.main.fixed_deposit_maintenance_activity.*
import styleguide.forms.ItemSelectionInterface
import styleguide.forms.SelectorInterface
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import styleguide.utils.extensions.removeSpaces
import styleguide.utils.extensions.toSentenceCase
import styleguide.utils.extensions.toTitleCase

class FixedDepositInterestPayoutDetailsFragment : FixedDepositBaseFragment(R.layout.fixed_deposit_interest_payout_details_fragment) {

    companion object {
        const val ABSA_BRANCH_CODE = "632005"
    }

    private var isOtherBankAccount = false
    private lateinit var accountSelectorList: SelectorList<FixedDepositAccountItem>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.fixed_deposit_interest_payout_details)
        fixedDepositMaintenanceActivity.progressIndicatorView.visibility = View.GONE

        fixedDepositViewModel.accountDetailsResponse.value?.fixedDeposit?.let { fixedDeposit ->
            nextPayoutLineItemView.setLineItemViewContent(DateUtils.formatDate(fixedDeposit.nextCapDate, DateUtils.DASHED_DATE_PATTERN, DateUtils.DATE_DISPLAY_PATTERN))
            capitalisationFrequencyLineItemView.setLineItemViewContent(fixedDeposit.capFrequency.toTitleCase())
            setupData(fixedDeposit)
        }

        setupAccountSelector()
        setupObservers()
        setupAccountTypeSelector()
        setupClickListeners()
        setupTextWatchers()
        setupCurrentPaymentDetails()

        if (isOtherBankAccount) {
            otherBankDetailsConstraintLayout.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null && resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_FOR_BANK_NAME -> {
                    bankNormalInputView.clearError()
                    (data.getStringExtra(PaymentsConstants.BANK_NAME))?.let {
                        branchNormalInputView.clear()
                        bankNormalInputView.selectedValue = it.toTitleCase()
                        fixedDepositViewModel.fixedDepositPayoutDetailsData.targetInstCode = it
                        fixedDepositViewModel.fetchBranchList(fixedDepositViewModel.fixedDepositPayoutDetailsData.targetInstCode)
                        showProgressDialog()
                    }
                }
                REQUEST_CODE_FOR_BRANCH_NAME -> {
                    branchNormalInputView.clearError()
                    branchNormalInputView.selectedValue = data.getStringExtra(PaymentsConstants.BRANCH_CODE) ?: ""
                    fixedDepositViewModel.fixedDepositPayoutDetailsData.targetBranchCode = branchNormalInputView.selectedValue
                }
            }
        }
    }

    private fun setupData(fixedDeposit: FixedDeposit) {
        if (fixedDepositViewModel.fixedDepositPayoutDetailsData.targetAccountNumber.isNotEmpty()) {
            return
        }
        fixedDepositViewModel.fixedDepositPayoutDetailsData.apply {
            termCapDay = fixedDeposit.nextCapDate.takeLast(2)
            termNextCapDate = fixedDeposit.nextCapDate
            termCapFrequency = fixedDeposit.capFrequency
            accountType = fixedDeposit.accountType
            accountNumber = fixedDeposit.accountNumber
            productCode = FIXED_DEPOSIT_PRODUCT_CODE
            automaticReinvestment = false.toString()
            addPaymentInstruction = YES_INDICATOR
            addCapitalizationInfo = YES_INDICATOR
            addDebitOrderInstruction = NO_INDICATOR
        }
    }

    private fun setupAccountSelector() {
        val responseObject = PayBeneficiaryPaymentObject()
        AbsaCacheManager.getInstance().getModelForAccounts(responseObject, ApplicationFlowType.ACCT_SUMMARY)
        val accounts: MutableList<AccountObject> = (responseObject.fromAccounts?.toMutableList() ?: mutableListOf()).apply {
            add(AccountObject().apply { description = getString(R.string.another_absa_account) })
            add(AccountObject().apply { description = getString(R.string.fixed_deposit_other_bank) })
        }
        accountSelectorList = accounts.toSelectorList { accountObject -> FixedDepositAccountItem(accountObject, getString(R.string.fixed_deposit_enter_details_manually)) }
        payInterestIntoNormalInputView.setList(accountSelectorList, getString(R.string.fixed_deposit_select_to_account))

        payInterestIntoNormalInputView.setItemSelectionInterface {
            clearAccountDetails()
            var analyticsTag = "AbsaAccountSelected"
            val selectedAccount = accounts[it]
            otherBankDetailsConstraintLayout.visibility = when (selectedAccount.description) {
                getString(R.string.fixed_deposit_other_bank) -> {
                    isOtherBankAccount = true
                    bankNormalInputView.isEnabled = true
                    branchNormalInputView.isEnabled = true
                    payInterestIntoNormalInputView.selectedValue = selectedAccount.description.toTitleCase()
                    fixedDepositViewModel.fixedDepositPayoutDetailsData.intExtBeneficiaryIndicator = BeneficiaryIndicator.EXTERNAL.indicator
                    fixedDepositViewModel.fetchBankList()
                    analyticsTag = "OtherBankSelected"
                    View.VISIBLE
                }
                getString(R.string.another_absa_account) -> {
                    isOtherBankAccount = true
                    bankNormalInputView.selectedValue = BMBConstants.ABSA
                    bankNormalInputView.isEnabled = false
                    branchNormalInputView.selectedValue = ABSA_BRANCH_CODE
                    branchNormalInputView.isEnabled = false
                    fixedDepositViewModel.fixedDepositPayoutDetailsData.apply {
                        intExtBeneficiaryIndicator = BeneficiaryIndicator.INTERNAL.indicator
                        targetInstCode = BMBConstants.ABSA
                        targetBranchCode = ABSA_BRANCH_CODE
                    }
                    View.VISIBLE
                }
                else -> {
                    isOtherBankAccount = false
                    payInterestIntoNormalInputView.selectedValue = TextFormatUtils.formatAccountNumberAndDescription(selectedAccount.description.toTitleCase(), selectedAccount.accountNumber)
                    fixedDepositViewModel.fixedDepositPayoutDetailsData.apply {
                        targetAccountNumber = selectedAccount.accountNumber
                        targetAccountDescription = selectedAccount.description
                        targetInstCode = BMBConstants.ABSA
                        targetBranchCode = ABSA_BRANCH_CODE
                        targetAccountType = selectedAccount.accountType
                        intExtBeneficiaryIndicator = BeneficiaryIndicator.INTERNAL.indicator
                    }
                    View.GONE
                }
            }
            AnalyticsUtil.trackAction(FixedDepositActivity.FIXED_DEPOSIT, "FixedTermDeposit_InterestPayoutDetailsScreen_${analyticsTag}")
        }
    }

    private fun setupAccountTypeSelector() {
        val accountTypeList = SelectorList<StringItem>().apply {
            add(StringItem(getString(R.string.fixed_deposit_cheque_account)))
            add(StringItem(getString(R.string.savings_account).toSentenceCase()))
            add(StringItem(getString(R.string.credit_card).toSentenceCase()))
        }
        accountTypeNormalInputView.setList(accountTypeList, getString(R.string.select_account_type))
        accountTypeNormalInputView.setItemSelectionInterface({
            val selectedAccount = accountTypeList[it].displayValue ?: ""
            accountTypeNormalInputView.selectedValue = selectedAccount
            fixedDepositViewModel.fixedDepositPayoutDetailsData.targetAccountType = selectedAccount
            fixedDepositViewModel.fixedDepositPayoutDetailsData.targetAccountDescription = selectedAccount
        })
    }

    private fun clearAccountDetails() {
        bankNormalInputView.clear()
        branchNormalInputView.clear()
        accountTypeNormalInputView.clear()
        accountNumberNormalInputView.clear()
    }

    private fun setupObservers() {
        fixedDepositViewModel.bankDetails.observe(viewLifecycleOwner, { banks ->
            bankNormalInputView.setOnClickListener {
                if (!bankNormalInputView.isEnabled) {
                    return@setOnClickListener
                }
                val chooseBankIntent = Intent(fixedDepositMaintenanceActivity, ChooseBankListActivity::class.java)
                chooseBankIntent.putExtra(PaymentsConstants.BANK_LIST, banks)
                startActivityForResult(chooseBankIntent, REQUEST_CODE_FOR_BANK_NAME)
            }
            dismissProgressDialog()
        })

        fixedDepositViewModel.bankBranches.observe(viewLifecycleOwner, { branches ->
            branches.branchList?.let {
                if (it.size == 1) {
                    branchNormalInputView.selectedValue = it.first().branchCode ?: ""
                    fixedDepositViewModel.fixedDepositPayoutDetailsData.targetBranchCode = branchNormalInputView.selectedValue
                }
            }
            dismissProgressDialog()
        })
    }

    private fun setupClickListeners() {
        branchNormalInputView.setOnClickListener {
            if (!branchNormalInputView.isEnabled) {
                return@setOnClickListener
            }

            if (bankNormalInputView.selectedValue.isNotEmpty() && fixedDepositViewModel.bankBranches.value?.branchList.isNullOrEmpty()) {
                fixedDepositViewModel.fetchBranchList(fixedDepositViewModel.fixedDepositPayoutDetailsData.targetInstCode)
                showProgressDialog()
            }
            val chooseBranchIntent = Intent(fixedDepositMaintenanceActivity, ChooseBranchListActivity::class.java)
            chooseBranchIntent.putExtra(BMBConstants.RESULT, fixedDepositViewModel.bankBranches.value)
            startActivityForResult(chooseBranchIntent, REQUEST_CODE_FOR_BRANCH_NAME)
        }
        nextButton.setOnClickListener { navigateToConfirmationScreen() }
    }

    private fun setupTextWatchers() {
        payInterestIntoNormalInputView.addRequiredValidationHidingTextWatcher()
        accountNumberNormalInputView.addRequiredValidationHidingTextWatcher()
    }

    private fun setupCurrentPaymentDetails() {
        if (fixedDepositViewModel.fixedDepositPayoutDetailsData.targetAccountNumber.isNotEmpty()) {
            return
        }
        fixedDepositViewModel.accountDetailsResponse.value?.interestPaymentInstruction?.let { paymentInstruction ->
            fixedDepositViewModel.fixedDepositPayoutDetailsData.apply {
                targetAccountDescription = when {
                    paymentInstruction.targetInstitutionCode.isEmpty() -> ""
                    paymentInstruction.targetInstitutionCode.contains(BMBConstants.ABSA, true) -> {
                        val accountDescription = paymentInstruction.targetAccount.trimStart('0').getAccountDisplayDescriptionFromAccountNumber().ifEmpty {
                            when (paymentInstruction.targetAccountType) {
                                InternalAccountType.CHEQUE.code -> getString(R.string.current_account)
                                InternalAccountType.SAVINGS.code -> getString(R.string.savings_account)
                                InternalAccountType.CREDIT_CARD.code -> getString(R.string.credit_card)
                                else -> ""
                            }
                        }
                        payInterestIntoNormalInputView.selectedValue = TextFormatUtils.formatAccountNumberAndDescription(accountDescription, paymentInstruction.targetAccount.trimStart('0'))
                        targetAccountType = paymentInstruction.targetAccountType
                        accountDescription
                    }
                    else -> {
                        payInterestIntoNormalInputView.onItemClicked(accountSelectorList.lastIndex)
                        bankNormalInputView.selectedValue = paymentInstruction.targetInstitutionCode
                        branchNormalInputView.selectedValue = paymentInstruction.targetBranchCode
                        accountNumberNormalInputView.selectedValue = paymentInstruction.targetAccount.trimStart('0')
                        accountTypeNormalInputView.selectedValue = when (paymentInstruction.targetAccountType) {
                            ExternalAccountType.CHEQUE.code -> getString(R.string.fixed_deposit_cheque_account)
                            ExternalAccountType.SAVINGS.code -> getString(R.string.savings_account)
                            ExternalAccountType.CREDIT_CARD.code -> getString(R.string.credit_card)
                            else -> ""
                        }
                        targetAccountType = accountTypeNormalInputView.selectedValue.toSentenceCase()
                        accountTypeNormalInputView.selectedValue
                    }
                }
                targetInstCode = paymentInstruction.targetInstitutionCode
                targetBranchCode = paymentInstruction.targetBranchCode
                targetAccountNumber = paymentInstruction.targetAccount.trimStart('0')
                toAccountReferenceNormalInputView.selectedValue = paymentInstruction.targetAccountReference
            }
        }
    }

    private fun navigateToConfirmationScreen() {
        if (hasValidFields()) {
            fixedDepositViewModel.fixedDepositPayoutDetailsData.targetAccountRef = toAccountReferenceNormalInputView.selectedValue
            if (otherBankDetailsConstraintLayout.isVisible) {
                fixedDepositViewModel.fixedDepositPayoutDetailsData.targetAccountNumber = accountNumberNormalInputView.selectedValue.removeSpaces()
            }
            navigate(FixedDepositInterestPayoutDetailsFragmentDirections.actionFixedDepositInterestPayoutDetailsFragmentToFixedDepositInterestPayoutConfirmationFragment())
        }
    }

    private fun hasValidFields(): Boolean {
        when {
            payInterestIntoNormalInputView.selectedValue.isEmpty() -> payInterestIntoNormalInputView.setError(R.string.fixed_deposit_pay_interest_into_error)
            otherBankDetailsConstraintLayout.isVisible -> when {
                bankNormalInputView.selectedValue.isEmpty() -> bankNormalInputView.setError(R.string.fixed_deposit_please_choose_a_bank)
                branchNormalInputView.selectedValue.isEmpty() -> branchNormalInputView.setError(R.string.fixed_deposit_please_choose_a_branch)
                accountTypeNormalInputView.selectedValue.isEmpty() -> accountTypeNormalInputView.setError(R.string.fixed_deposit_please_choose_an_account_type)
                accountNumberNormalInputView.selectedValue.isEmpty() -> accountNumberNormalInputView.setError(R.string.fixed_deposit_please_enter_an_account_number)
                else -> return true
            }
            else -> return true
        }
        return false
    }

    override fun onDestroyView() {
        fixedDepositViewModel.bankBranches.removeObservers(viewLifecycleOwner)
        fixedDepositViewModel.bankDetails.removeObservers(viewLifecycleOwner)
        super.onDestroyView()
    }

    internal class FixedDepositAccountItem(private val accountObject: AccountObject, private val emptyMessage: String) : SelectorInterface {
        override val displayValue: String?
            get() = accountObject.description

        override val displayValueLine2: String?
            get() = if (accountObject.accountNumber.isNotEmpty()) "${accountObject.accountNumber} (${accountObject.availableBalance?.toString()})" else emptyMessage
    }

    enum class BeneficiaryIndicator(val indicator: String) {
        INTERNAL("I"),
        EXTERNAL("E")
    }

    enum class ExternalAccountType(val code: String) {
        CHEQUE("01"),
        SAVINGS("02"),
        CREDIT_CARD("07")
    }

    enum class InternalAccountType(val code: String) {
        CHEQUE("CQ"),
        SAVINGS("SA"),
        CREDIT_CARD("CA")
    }
}