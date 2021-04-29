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
 */
package com.barclays.absa.banking.expressCashSend.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.buy.ui.airtime.AirtimeHelper
import com.barclays.absa.banking.cashSend.ui.CashSendSelectedAccountWrapper
import com.barclays.absa.banking.databinding.CashSendDetailsFragmentBinding
import com.barclays.absa.banking.express.cashSend.cashSendGetSourceAccounts.CashSendFetchSourceAccountsViewModel
import com.barclays.absa.banking.express.cashSend.cashSendListBeneficiaries.dto.CashSendBeneficiary
import com.barclays.absa.banking.express.cashSend.dto.CashSendType
import com.barclays.absa.banking.express.cashSend.validateCashSend.CashSendValidationDataModel
import com.barclays.absa.banking.express.cashSend.validateCashSend.CashSendValidationViewModel
import com.barclays.absa.banking.express.shared.dto.CashSendInstructionType
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.utils.ContactDialogOptionListener
import com.barclays.absa.utils.*
import com.barclays.absa.utils.extensions.toRandAmount
import com.barclays.absa.utils.extensions.toSelectorList
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.content.BeneficiaryListItem
import styleguide.forms.Form
import styleguide.forms.validation.*
import styleguide.utils.extensions.*
import java.util.*

class CashSendDetailsFragment : CashSendBaseFragment(R.layout.cash_send_details_fragment) {
    private val binding by viewBinding(CashSendDetailsFragmentBinding::bind)
    private val sourceAccountViewModel by activityViewModels<CashSendFetchSourceAccountsViewModel>()
    private val validateCashSendViewModel by activityViewModels<CashSendValidationViewModel>()
    private val absaCacheService: IAbsaCacheService = getServiceInterface()

    private lateinit var contactUri: Uri
    private var cashSendValidationDataModel = CashSendValidationDataModel()

    companion object {
        private const val SELECT_CONTACT_NO_REQUEST_CODE = 3
        private const val MINIMUM_CASH_SEND_AMOUNT = 50.0
        private const val MAXIMUM_CASH_SEND_AMOUNT = 3000.00
        private const val CASH_SEND_INCREMENT_AMOUNT = 10
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.cashsend, true)
        setUpObservers()
        cashSendViewModel.clientAgreement = ClientAgreementHelper.fetchClientType(baseActivity)

        setUpValidationRules()
        populateViews()
        setupTalkBack()

        sourceAccountViewModel.fetchSourceAccounts()
        sourceAccountViewModel.cashSendFetchSourceAccountsResponse.observe(viewLifecycleOwner) { sourceAccounts ->
            if (sourceAccounts.sourceAccountList.isNotEmpty()) {
                cashSendViewModel.sourceAccount = sourceAccounts.sourceAccountList.first()
                setSelectedAccount()
                val accounts = sourceAccounts.sourceAccountList.toSelectorList { CashSendSelectedAccountWrapper(it) }
                binding.accountSelectorView.setList(accounts, getString(R.string.select_account_toolbar_title))
                binding.accountSelectorView.setItemSelectionInterface { index: Int ->
                    cashSendViewModel.sourceAccount = accounts[index].sourceAccount
                    setSelectedAccount()
                }
            }

            if (absaCacheService.isPersonalClientAgreementAccepted()) {
                ClientAgreementHelper.updatePersonalClientAgreementContainer(baseActivity, true, binding.termsAndConditionsCheckBoxView, cashSendViewModel.clientAgreement)
                dismissProgressDialog()
            } else {
                cashSendViewModel.fetchClientAgreementDetails()
            }
        }

        binding.nextButton.setOnClickListener {
            cashSendValidationDataModel.sourceAccount = cashSendViewModel.sourceAccount.accountNumber
            val existingBeneficiary = cashSendViewModel.beneficiaryList.find { it.targetAccountNumber.removeSpaces() == binding.numberNormalInputView.selectedValue.removeSpaces() }
            if (existingBeneficiary != null) {
                cashSendViewModel.beneficiaryDetail = existingBeneficiary
                getOnceOffDataFromViews()
                navigate(CashSendDetailsFragmentDirections.actionCashSendDetailFragmentToCashSendExistingBeneficiaryFragment())
            } else {
                requestCashSendValidation()
            }
        }
        binding.numberNormalInputView.setImageViewOnTouchListener(ContactDialogOptionListener(binding.numberNormalInputView.editText, R.string.selFrmPhoneBookMsg, baseActivity, SELECT_CONTACT_NO_REQUEST_CODE, this))
    }

    fun setupTalkBack() {
        if (AccessibilityUtils.isExploreByTouchEnabled()) {
            with(binding.nameNormalInputView) {
                contentDescription = getString(R.string.talkback_cashsend_onceoff_enter_name)
                setEditTextContentDescription(getString(R.string.talkback_cashsend_onceoff_recipient_name))
            }
            with(binding.numberNormalInputView) {
                setIconImageViewDescription(getString(R.string.talkback_cashsend_choose_contact_from_phone))
                setEditTextContentDescription(getString(R.string.talkback_cashsend_onceoff_send_mobile_number))
                contentDescription = getString(R.string.talkback_cashsend_onceoff_send_mobile_number)
            }
            with(binding.surnameNormalInputView) {
                contentDescription = getString(R.string.talkback_cashsend_onceoff_enter_recipient_surname)
                setEditTextContentDescription(getString(R.string.talkback_cashsend_onceoff_enter_recipient_surname))
            }

            val minValue = getString(R.string.talkback_cashsend_beneficiary_minimum_value)
            val maxValue = getString(R.string.talkback_cashsend_beneficiary_maximum_value)
            binding.sendRangeTextView.contentDescription = getString(R.string.talkback_cashsend_min_max, AccessibilityUtils.getTalkBackRandValueFromString(minValue), AccessibilityUtils.getTalkBackRandValueFromString(maxValue))
        }
    }

    private fun populateViews() {
        when (cashSendViewModel.cashSendFlow) {
            CashSendFlow.CASH_SEND_TO_SELF -> binding.myReferenceInputView.text = CustomerProfileObject.instance.customerName
            CashSendFlow.ONCE_OFF_CASH_SEND -> {
                getDeviceProfilingInteractor().notifyTransaction()
                getOnceOffDataFromViews()
                binding.newCashSendGroup.visibility = View.VISIBLE
            }
            CashSendFlow.CASH_SEND_TO_BENEFICIARY -> {
                val cashSendBeneficiary = cashSendViewModel.beneficiaryDetail
                prepareValidationData(cashSendBeneficiary)
                binding.beneficiaryView.visibility = View.VISIBLE
                val beneficiaryName = "${cashSendBeneficiary.beneficiaryDetails.beneficiaryName} ${cashSendBeneficiary.beneficiaryDetails.beneficiarySurname}"
                binding.beneficiaryView.setBeneficiary(BeneficiaryListItem(beneficiaryName, cashSendBeneficiary.targetAccountNumber.toFormattedCellphoneNumber(), getString(R.string.last_transaction_beneficiary, cashSendBeneficiary.beneficiaryDetails.lastPaymentAmount.toRandAmount(), getString(R.string.paid), DateTimeHelper.formatDate(cashSendBeneficiary.beneficiaryDetails.lastPaymentDateAndTime))))
                binding.myReferenceInputView.text = cashSendBeneficiary.beneficiaryDetails.statementReference
            }
        }
    }

    private fun prepareValidationData(cashSendBeneficiary: CashSendBeneficiary) {
        with(cashSendValidationDataModel) {
            cashSendBeneficiary.beneficiaryDetails.let {
                recipientCellphoneNumber = it.recipientCellphoneNumber
                instructionType = CashSendInstructionType.CASHSEND_BENEFICIARY.value
                beneficiaryName = it.beneficiaryName
                beneficiarySurname = it.beneficiarySurname
                beneficiaryShortName = it.beneficiaryShortName
                statementReference = it.statementReference
                transactionDateTime = DateTimeHelper.formatDate(Date(), DateTimeHelper.DASHED_PATTERN_YYYY_MM_DD_HH_MM)
                cifKey = it.cifKey
                tieBreaker = it.tieBreaker
                beneficiaryNumber = it.beneficiaryNumber.toString()
                cashSendType = CashSendType.SINGLE.value
            }
        }
    }

    private fun getOnceOffDataFromViews() {
        with(cashSendValidationDataModel) {
            sourceAccount = cashSendViewModel.sourceAccount.accountNumber
            tieBreaker = ""
            cifKey = ""
            statementReference = binding.myReferenceInputView.selectedValue
            beneficiaryShortName = binding.nameNormalInputView.selectedValue
            beneficiarySurname = binding.surnameNormalInputView.selectedValue
            beneficiaryName = binding.nameNormalInputView.selectedValue
            recipientCellphoneNumber = binding.numberNormalInputView.selectedValue.removeSpaces()
            paymentAmount = binding.amountLargeInputView.selectedValue.removeCurrency()
            pin = binding.accessPinNormalInputView.selectedValue
            instructionType = CashSendInstructionType.CASHSEND_ONCE_OFF.value
            transactionDateTime = DateTimeHelper.formatDate(Date(), DateTimeHelper.DASHED_PATTERN_YYYY_MM_DD_HH_MM)
            beneficiaryNumber = ""
            cashSendType = CashSendType.ONCE_OFF.value
        }
        cashSendViewModel.cashSendValidationDataModel = cashSendValidationDataModel
    }

    private fun setUpValidationRules() {
        with(binding) {
            amountLargeInputView.addValidationRules(FieldRequiredValidationRule(R.string.law_for_you_enter_address_error), MinimumAmountValidationRule(MINIMUM_CASH_SEND_AMOUNT, true, errorMessage = getString(R.string.cash_send_invalid_less_error, MINIMUM_CASH_SEND_AMOUNT.toRandAmount())), MaximumAmountValidationRule(MAXIMUM_CASH_SEND_AMOUNT, false, errorMessage = getString(R.string.depositor_plus_maximum_amount_error, MAXIMUM_CASH_SEND_AMOUNT.toRandAmount())), RemainderValueValidationRule(CASH_SEND_INCREMENT_AMOUNT, true, R.string.card_limit_mul_of_10_validation))
            accessPinNormalInputView.addValidationRules(FieldRequiredValidationRule(R.string.cash_send_pin_error), MinimumLengthValidationRule(6, R.string.cash_send_invalid_pin_error))
            myReferenceInputView.addValidationRule(FieldRequiredValidationRule(R.string.cash_send_reference_error))
            nameNormalInputView.addValidationRules(FieldRequiredWhenVisibleValidationRule(R.string.cash_send_invalid_name_error))
            surnameNormalInputView.addValidationRules(FieldRequiredWhenVisibleValidationRule(R.string.cash_send_invalid_surname_error))
            numberNormalInputView.addValidationRules(CellphoneNumberValidationRule(R.string.cash_send_invalid_number_error))
        }
    }

    private fun setUpObservers() {
        cashSendViewModel.updateClientAgreementDetailsLiveData.observe(viewLifecycleOwner) { transactionResponse ->
            if (BMBConstants.SUCCESS.equals(transactionResponse.transactionStatus, ignoreCase = true)) {
                absaCacheService.setPersonalClientAgreementAccepted(true)
            }
        }

        cashSendViewModel.clientAgreementDetailsLiveData.observe(viewLifecycleOwner) {
            ClientAgreementHelper.updatePersonalClientAgreementContainer(requireContext(), !BMBConstants.ALPHABET_N.equals(it.clientAgreementAccepted, ignoreCase = true), binding.termsAndConditionsCheckBoxView, cashSendViewModel.clientAgreement)
            dismissProgressDialog()
        }
    }

    private fun setSelectedAccount() {
        binding.accountSelectorView.selectedValue = "${cashSendViewModel.sourceAccount.accountName} (${cashSendViewModel.sourceAccount.accountNumber.toFormattedAccountNumber()})"
        binding.amountLargeInputView.setDescription(getString(R.string.account_available_balance, cashSendViewModel.sourceAccount.availableBalance.toRandAmount()))
    }

    private fun requestCashSendValidation() {
        if (Form(binding.formConstraintLayout).isValid()) {
            KeyboardUtils.hideKeyboard(this)
            binding.amountLargeInputView.text?.let { cashSendValidationDataModel.paymentAmount = it.removeCurrency() }
            binding.accessPinNormalInputView.text?.let {
                cashSendValidationDataModel.pin = it
                cashSendViewModel.accessPin = it
            }
            when {
                !cashSendViewModel.accountFundsSufficient(binding.amountLargeInputView.selectedValueUnmasked.toDouble()) -> binding.amountLargeInputView.setError(getString(R.string.amount_exceeds_available))
                absaCacheService.isPersonalClientAgreementAccepted() -> {
                    if (cashSendViewModel.cashSendFlow == CashSendFlow.ONCE_OFF_CASH_SEND) {
                        getOnceOffDataFromViews()
                    }
                    validateCashSendViewModel.validateCashSend(cashSendValidationDataModel)
                    validateCashSendViewModel.cashSendValidationResponseLiveData.observe(viewLifecycleOwner, {
                        dismissProgressDialog()
                        cashSendViewModel.cashSendValidationResponse = it
                        cashSendViewModel.cashSendValidationDataModel = cashSendValidationDataModel
                        navigate(CashSendDetailsFragmentDirections.actionCashSendDetailFragmentToCashSendOnceOffConfirmFragment(cashSendValidationDataModel.pin))
                    })
                }
                else -> cashSendViewModel.updateClientAgreementDetails()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == SELECT_CONTACT_NO_REQUEST_CODE) {
            contactUri = data?.data ?: Uri.EMPTY
            PermissionHelper.requestContactsReadPermission(baseActivity) { readContact() }
            binding.numberNormalInputView.clearError()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == PermissionHelper.PermissionCode.LOAD_CONTACTS.value && grantResults.isNotEmpty()) {
            when (grantResults.first()) {
                PackageManager.PERMISSION_GRANTED -> readContact()
                PackageManager.PERMISSION_DENIED -> PermissionHelper.requestContactsReadPermission(baseActivity) { readContact() }
            }
        }
    }

    private fun readContact() {
        binding.numberNormalInputView.text = CommonUtils.getContact(baseActivity, contactUri).phoneNumbers?.mobile
        AirtimeHelper.validateMobileNumber(baseActivity, binding.numberNormalInputView)
    }
}
