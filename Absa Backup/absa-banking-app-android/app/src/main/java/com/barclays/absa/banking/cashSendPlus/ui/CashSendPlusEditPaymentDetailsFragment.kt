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
package com.barclays.absa.banking.cashSendPlus.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.bankConfirmationLetter.ui.AccountTypesObjectWrapper
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.cashSendPlus.services.CashSendPlusSendMultiplePaymentDetails
import com.barclays.absa.banking.cashSendPlus.services.CheckCashSendPlusRegistrationStatusResponse
import com.barclays.absa.banking.cashSendPlus.ui.CashSendPlusSendMultipleActivity.Companion.ACCESS_PIN_LENGTH
import com.barclays.absa.banking.cashSendPlus.ui.CashSendPlusSendMultipleActivity.Companion.MAX_AMOUNT_TO_SEND
import com.barclays.absa.banking.cashSendPlus.ui.CashSendPlusSendMultipleActivity.Companion.MIN_AMOUNT_TO_SEND
import com.barclays.absa.banking.express.getAllBalances.dto.AccountTypesBMG
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.utils.AbsaCacheManager
import com.barclays.absa.utils.TextFormatUtils
import kotlinx.android.synthetic.main.cash_send_plus_edit_payment_details_fragment.*
import styleguide.content.BeneficiaryListItem
import styleguide.forms.SelectorList
import styleguide.forms.validation.FieldRequiredValidationRule
import styleguide.forms.validation.addValidationRule
import kotlin.math.roundToInt

class CashSendPlusEditPaymentDetailsFragment : BaseFragment(R.layout.cash_send_plus_edit_payment_details_fragment) {
    private val cashSendPlusViewModel by activityViewModels<CashSendPlusViewModel>()
    private val cachedCashSendPlusData: CheckCashSendPlusRegistrationStatusResponse? = appCacheService.getCashSendPlusRegistrationStatus()
    private val accountTypesList = SelectorList<AccountTypesObjectWrapper>()
    private val selectedBeneficiaryPaymentDetails: MutableList<CashSendPlusSendMultiplePaymentDetails> = appCacheService.getCashSendPlusSendMultipleBeneficiariesPaymentDetails().toMutableList()
    private var newSelectedAccount: AccountObject? = null
    private var currentAccessPin: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.cash_send_plus_multiple)

        val beneficiaryPaymentDetails = selectedBeneficiaryPaymentDetails[cashSendPlusViewModel.selectedBeneficiaryPosition]

        beneficiaryPaymentDetails.let { paymentDetails ->
            with(paymentDetails.beneficiaryInfo) {
                val beneficiaryName = "$beneficiaryName $beneficiarySurname"
                selectedBeneficiaryView.setBeneficiary(BeneficiaryListItem(beneficiaryName, beneficiaryAccountNumber, ""))
            }

            cachedCashSendPlusData?.cashSendPlusResponseData?.let {
                amountNormalInputView.contentDescription = getString(R.string.account_available_balance, TextFormatUtils.formatBasicAmount(it.cashSendPlusLimitAmountAvailable))
            }

            with(paymentDetails) {
                amountNormalInputView.text = if (amount.isNotEmpty() && amount != "0") amount else ""
                accountNormalInputView.text = "${accountDetail.accountType} (${accountDetail.accountNumberFormatted})"
                referenceNormalInputView.text = reference
                accessPinNormalInputView.text = accessPin
                currentAccessPin = accessPin
            }
        }

        val accountList = AbsaCacheManager.getInstance().accountsList
        accountList.accountsList.filter { it.accountType == AccountTypesBMG.savingsAccount.name || it.accountType == AccountTypesBMG.currentAccount.name }.forEach { accountObject ->
            accountTypesList.add(AccountTypesObjectWrapper(accountObject.description, accountObject.accountNumber))
        }

        accountNormalInputView.apply {
            setList(accountTypesList, getString(R.string.select_account_hint))
            setItemSelectionInterface { index ->
                accountList.accountsList.find { it.accountNumber == accountTypesList[index].displayValueLine2 }?.let {
                    selectedValue = "${it.accountType} ${it.accountNumberFormatted}"
                    newSelectedAccount = it
                }
            }

            setOnClickListener { triggerListActivity() }
        }

        addValidationRules()

        doneButton.setOnClickListener {
            if (validateAmount() && referenceNormalInputView.validate() && validateAccessPinLength() && accessPinNormalInputView.validate()) {
                beneficiaryPaymentDetails.apply {
                    amount = amountNormalInputView.selectedValueUnmasked
                    reference = referenceNormalInputView.selectedValue
                    accessPin = accessPinNormalInputView.selectedValue
                    newSelectedAccount?.let {
                        accountDetail = it
                    }
                }

                val cashedBeneficiaryData = appCacheService.getCashSendPlusSendMultipleBeneficiariesPaymentDetails().toMutableList()
                cashedBeneficiaryData[cashSendPlusViewModel.selectedBeneficiaryPosition] = beneficiaryPaymentDetails
                appCacheService.setCashSendPlusSendMultipleBeneficiariesPaymentDetails(cashedBeneficiaryData)
                findNavController().navigateUp()
            }
        }
        accessPinNormalInputView.isEnabled = !cashSendPlusViewModel.useSameAccessPinForAllBeneficiary
    }

    private fun validateAmount(): Boolean {
        val amountSelectedValue = amountNormalInputView.selectedValueUnmasked
        var isValidAmount = false
        val amount = amountSelectedValue.toDouble().roundToInt()

        val isMultipleOfTen = amount % 10 == 0
        when {
            isMultipleOfTen && amount in MIN_AMOUNT_TO_SEND..MAX_AMOUNT_TO_SEND -> {
                isValidAmount = true
                amountNormalInputView.clearError()
            }
            !isMultipleOfTen -> {
                amountNormalInputView.setError(getString(R.string.cash_send_plus_amount_can_only_be_in_r10_increment))
                isValidAmount = false
            }
            isMultipleOfTen && amount < MIN_AMOUNT_TO_SEND -> {
                amountNormalInputView.setError(getString(R.string.cash_send_plus_amount_must_be_equal_or_more_than_r20))
                isValidAmount = false
            }
            isMultipleOfTen && amount > MAX_AMOUNT_TO_SEND -> {
                amountNormalInputView.setError(getString(R.string.cash_send_plus_amount_cannot_be_more_than_r3000))
                isValidAmount = false
            }
            isMultipleOfTen && amount < cachedCashSendPlusData?.cashSendPlusResponseData?.cashSendPlusLimitAmountAvailable?.toInt() ?: 0 -> {
                amountNormalInputView.setError(getString(R.string.cash_send_plus_amount_exceeds_available_amount))
                isValidAmount = false
            }
        }

        return isValidAmount
    }

    private fun validateAccessPinLength(): Boolean {
        val isValidPinLength = accessPinNormalInputView.selectedValue.length == ACCESS_PIN_LENGTH
        if (!isValidPinLength) {
            accessPinNormalInputView.setError(getString(R.string.access_pin_errormessage))
        }
        return isValidPinLength
    }

    private fun addValidationRules() {
        referenceNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.cash_send_plus_enter_reference_fo_my_statement))
        accessPinNormalInputView.addValidationRule(FieldRequiredValidationRule(R.string.cash_send_plus_enter_access_pin))
    }
}