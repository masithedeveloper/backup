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
package com.barclays.absa.banking.paymentsRewrite.ui

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.fragment.app.activityViewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.PaymentDetailsFragmentBinding
import com.barclays.absa.banking.express.beneficiaries.dto.BeneficiaryType
import com.barclays.absa.banking.express.beneficiaries.dto.RegularBeneficiary
import com.barclays.absa.banking.paymentsRewrite.ui.multiple.MultiplePaymentsViewModel
import com.barclays.absa.banking.presentation.shared.NotificationMethodSelectionActivity
import com.barclays.absa.banking.shared.OnBackPressedInterface
import com.barclays.absa.banking.shared.SourceAccountSelector
import com.barclays.absa.utils.DateTimeHelper
import com.barclays.absa.utils.DateTimeHelper.SPACED_PATTERN_DD_MMMM_YYYY
import com.barclays.absa.utils.DateTimeHelper.toDate
import com.barclays.absa.utils.extensions.toSelectorList
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.content.BeneficiaryListItem
import styleguide.forms.Form
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.forms.notificationmethodview.NotificationMethodData
import styleguide.forms.validation.MinimumAmountValidationRule
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import styleguide.forms.validation.addValidationRule
import styleguide.utils.extensions.formatAmountAsRand
import styleguide.utils.extensions.toRandAmount
import java.math.BigDecimal
import java.util.*

class PaymentDetailsFragment : PaymentsBaseFragment(R.layout.payment_details_fragment), OnBackPressedInterface {

    private val binding by viewBinding(PaymentDetailsFragmentBinding::bind)

    private lateinit var selectedBeneficiary: RegularBeneficiary

    private val multiplePaymentsViewModel by activityViewModels<MultiplePaymentsViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        with(paymentsViewModel) {
            validatePaymentRequest = if (isOnceOffPayment) createOnceOffValidatePaymentRequest() else createValidatePaymentRequest()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedBeneficiary = paymentsViewModel.selectedBeneficiary

        setupPaymentTypes()
        populateAccountSelectorAndDefaultToFirstAccount()
        setupBeneficiaryView()
        populateDetails()
        setUpClickListeners()

        with(binding) {
            amountLargeInputView.addRequiredValidationHidingTextWatcher(R.string.validation_error_payment_amount)
            amountLargeInputView.addValidationRule(MinimumAmountValidationRule(0.0, false, errorMessage = getString(R.string.cash_send_minimum_amount)))
            beneficiaryReferenceNormalInputView.addRequiredValidationHidingTextWatcher(R.string.validation_error_reference)
            myReferenceNormalInputView.addRequiredValidationHidingTextWatcher(R.string.validation_error_reference)
            paymentDateNormalInputView.addRequiredValidationHidingTextWatcher(R.string.payments_please_select_date)
            myPaymentNotificationNormalInputView.addRequiredValidationHidingTextWatcher()
            beneficiaryPaymentNotificationNormalInputView.addRequiredValidationHidingTextWatcher()

            if (selectedBeneficiary.beneficiaryDetails.typeOfBeneficiary == BeneficiaryType.INSTITUTIONAL) {
                beneficiaryPaymentNotificationNormalInputView.visibility = View.GONE
            }
        }
    }

    private fun populateAccountSelectorAndDefaultToFirstAccount() {
        binding.accountSelectorView.setHint(R.string.select_account_hint)

        paymentsViewModel.paymentsSourceAccounts.let { sourceAccountList ->
            val accountSelectorList = sourceAccountList.toSelectorList {
                SourceAccountSelector().apply {
                    accountName = it.accountName
                    accountNumber = it.accountNumber
                    availableBalance = it.availableBalance
                }
            }

            binding.accountSelectorView.setList(accountSelectorList, getString(R.string.select_account_hint))
            binding.accountSelectorView.setItemSelectionInterface {
                paymentsViewModel.selectedSourceAccount = sourceAccountList[it]
                if (accountFundsSufficient()) {
                    binding.amountLargeInputView.clearError()
                } else {
                    binding.amountLargeInputView.setError(R.string.amount_exceeds_available)
                }
                binding.accountSelectorView.setDescription(getString(R.string.amount_available, accountSelectorList[it].availableBalance.toRandAmount()))

                checkIIPOptionForCreditCard()
            }

            if (paymentsViewModel.isSourceAccountInitialised()) {
                binding.accountSelectorView.selectedIndex = sourceAccountList.indexOfFirst { it.accountNumber == paymentsViewModel.selectedSourceAccount.accountNumber }
                binding.accountSelectorView.setDescription(getString(R.string.amount_available, paymentsViewModel.selectedSourceAccount.availableBalance.toRandAmount()))
            } else if (accountSelectorList.isNotEmpty()) {
                paymentsViewModel.selectedSourceAccount = sourceAccountList.first()
                binding.accountSelectorView.selectedIndex = 0
                binding.accountSelectorView.setDescription(getString(R.string.amount_available, accountSelectorList.first().availableBalance.toRandAmount()))
            }

            checkIIPOptionForCreditCard()
        }
    }

    private fun checkIIPOptionForCreditCard() {
        with(binding.paymentTypeRadioView) {
            if (paymentsViewModel.shouldDisableIIPForCreditCard()) {
                clearSelection()
                disableRadioItem(1)
                setErrorMessage(context.getString(R.string.payments_iip_credit_card_error), false)
            } else {
                enableRadioGroup()
                hideError()
            }
        }
    }

    private fun populateDetails() {
        with(selectedBeneficiary.beneficiaryDetails) {
            with(binding) {
                beneficiaryReferenceNormalInputView.text = targetAccountReference
                myReferenceNormalInputView.text = sourceAccountReference

                beneficiaryPaymentNotificationNormalInputView.text = getNotificationMethodDetails(beneficiaryNotification)
                myPaymentNotificationNormalInputView.text = getNotificationMethodDetails(ownNotification)

                if (paymentsViewModel.isBillPayment) {
                    beneficiaryReferenceNormalInputView.visibility = View.GONE
                }
            }
        }

        if (paymentsViewModel.isMultiplePayment) {
            binding.amountLargeInputView.selectedValue = multiplePaymentsViewModel.getBeneficiaryWrapper(selectedBeneficiary)?.paymentAmount.toString()
        }

        binding.paymentDateNormalInputView.setOnClickListener { showDatePickerDialog() }
    }

    private fun setUpClickListeners() {
        binding.beneficiaryPaymentNotificationNormalInputView.setOnClickListener {
            val intent = Intent(baseActivity, NotificationMethodSelectionActivity::class.java).apply {
                putExtra(NotificationMethodSelectionActivity.TOOLBAR_TITLE, getString(R.string.beneficiary_notification))
                putExtra(NotificationMethodSelectionActivity.SHOW_BENEFICIARY_NOTIFICATION_METHOD, true)
                putExtra(NotificationMethodSelectionActivity.HIDE_NOTIFICATION_TITLE, true)
                putExtra(NotificationMethodSelectionActivity.LAST_SELECTED_TYPE_FOR_BENEFICIARY, selectedBeneficiary.beneficiaryDetails.beneficiaryNotification.notificationMethod.name)
                putExtra(NotificationMethodSelectionActivity.LAST_SELECTED_VALUE_FOR_BENEFICIARY, getNotificationMethodDetails(selectedBeneficiary.beneficiaryDetails.beneficiaryNotification))
            }
            startActivityForResult(intent, NotificationMethodSelectionActivity.NOTIFICATION_METHOD_BENEFICIARY_REQUEST_CODE)
        }

        binding.myPaymentNotificationNormalInputView.setOnClickListener {
            val intent = Intent(baseActivity, NotificationMethodSelectionActivity::class.java).apply {
                putExtra(NotificationMethodSelectionActivity.TOOLBAR_TITLE, getString(R.string.my_notification))
                putExtra(NotificationMethodSelectionActivity.SHOW_SELF_NOTIFICATION_METHOD, true)
                putExtra(NotificationMethodSelectionActivity.HIDE_NOTIFICATION_TITLE, true)
                putExtra(NotificationMethodSelectionActivity.LAST_SELECTED_TYPE_FOR_SELF, selectedBeneficiary.beneficiaryDetails.ownNotification.notificationMethod.name)
                putExtra(NotificationMethodSelectionActivity.LAST_SELECTED_VALUE_FOR_SELF, getNotificationMethodDetails(selectedBeneficiary.beneficiaryDetails.ownNotification))
            }
            startActivityForResult(intent, NotificationMethodSelectionActivity.NOTIFICATION_METHOD_SELF_REQUEST_CODE)
        }

        binding.nextButton.setOnClickListener {
            if (Form(binding.parentConstraintLayout).isValid()) {
                when {
                    !accountFundsSufficient() -> binding.amountLargeInputView.setError(R.string.amount_exceeds_available)
                    binding.paymentTypeRadioView.selectedIndex == -1 -> binding.paymentTypeRadioView.setErrorMessage(getString(R.string.validation_error_payment_type))
                    paymentsViewModel.isMultiplePayment -> setDataForMultiplePayments()
                    else -> {
                        setBeneficiaryReferences()
                        paymentsViewModel.paymentAmount = binding.amountLargeInputView.selectedValueUnmasked
                        navigate(PaymentDetailsFragmentDirections.actionPaymentDetailsFragmentToPaymentConfirmationFragment())
                    }
                }
            }
        }
    }

    private fun setDataForMultiplePayments() {
        setBeneficiaryReferences()

        with(paymentsViewModel) {
            paymentsSourceAccounts.find { it.accountNumber == (binding.accountSelectorView.selectedItem as SourceAccountSelector).accountNumber }?.let {
                selectedSourceAccount = it
            }

            multiplePaymentsViewModel.getBeneficiaryWrapper(selectedBeneficiary)?.apply {
                regularBeneficiary = selectedBeneficiary
                paymentAmount = BigDecimal(binding.amountLargeInputView.selectedValueUnmasked)
                isImmediate = validatePaymentRequest.immediatePayment
                useTime = validatePaymentRequest.useTime
                paymentTransactionDateAndTime = validatePaymentRequest.paymentTransactionDateAndTime
            }

            hasIIPBeneficiary = multiplePaymentsViewModel.hasImmediatePayment()
        }

        baseActivity.onBackPressed()
    }

    private fun setBeneficiaryReferences() {
        selectedBeneficiary.beneficiaryDetails.apply {
            sourceAccountReference = binding.myReferenceNormalInputView.selectedValue
            targetAccountReference = binding.beneficiaryReferenceNormalInputView.selectedValue
        }
    }

    private fun accountFundsSufficient(): Boolean {
        val availableAmount = BigDecimal(paymentsViewModel.selectedSourceAccount.availableBalance)
        if (binding.amountLargeInputView.selectedValueUnmasked.isNotEmpty()) {
            return availableAmount >= BigDecimal(binding.amountLargeInputView.selectedValueUnmasked)
        }
        return true
    }

    private fun setupBeneficiaryView() {
        var lastPaymentDetail = ""
        selectedBeneficiary.processedTransactions.firstOrNull()?.apply {
            lastPaymentDetail = getString(R.string.last_transaction_beneficiary, paymentAmount.formatAmountAsRand(), getString(R.string.paid), DateTimeHelper.formatDate(paymentTransactionDateAndTime, DateTimeHelper.DASHED_PATTERN_YYYY_MM_DD_HH_MM_SS))
        }

        val beneficiaryListItem = BeneficiaryListItem(selectedBeneficiary.beneficiaryDetails.beneficiaryName, selectedBeneficiary.beneficiaryDetails.targetAccountNumber, lastPaymentDetail)
        binding.beneficiaryView.setBeneficiary(beneficiaryListItem)
    }

    private fun setupPaymentTypes() {
        binding.paymentTypeRadioView.disableItemCheckInterface()
        val paymentTypeOptions = SelectorList<StringItem>().apply {
            add(StringItem(getString(R.string.normal_24_48)))
            if (selectedBeneficiary.beneficiaryDetails.realTimePaymentAllowed) {
                add(StringItem(getString(R.string.iip)))
            }
            add(StringItem(getString(R.string.future_dated_payment)))
        }

        binding.paymentTypeRadioView.setDataSource(paymentTypeOptions)

        if (paymentsViewModel.isMultiplePayment) {
            multiplePaymentsViewModel.getBeneficiaryWrapper(selectedBeneficiary)?.let {
                when {
                    it.useTime -> {
                        binding.paymentTypeRadioView.selectedIndex = paymentTypeOptions.size - 1
                        binding.paymentDateNormalInputView.visibility = View.VISIBLE
                    }
                    it.isImmediate -> binding.paymentTypeRadioView.selectedIndex = 1
                    else -> binding.paymentTypeRadioView.selectedIndex = 0
                }
            }
        } else {
            when {
                paymentsViewModel.validatePaymentRequest.useTime -> {
                    binding.paymentTypeRadioView.selectedIndex = paymentTypeOptions.size - 1
                    binding.paymentDateNormalInputView.visibility = View.VISIBLE
                }
                paymentsViewModel.validatePaymentRequest.immediatePayment -> binding.paymentTypeRadioView.selectedIndex = 1
                else -> binding.paymentTypeRadioView.selectedIndex = 0
            }
        }

        binding.paymentTypeRadioView.setItemCheckedInterface { selectedIndex ->
            binding.paymentTypeRadioView.hideError()
            val baseValidatePaymentRequest = paymentsViewModel.validatePaymentRequest
            when (selectedIndex) {
                1 -> {
                    if (selectedBeneficiary.beneficiaryDetails.realTimePaymentAllowed) {
                        binding.paymentDateNormalInputView.visibility = View.GONE
                        baseValidatePaymentRequest.immediatePayment = true
                        baseValidatePaymentRequest.useTime = false
                    } else {
                        showDatePickerDialog()
                        baseValidatePaymentRequest.immediatePayment = false
                        baseValidatePaymentRequest.useTime = true
                        binding.paymentDateNormalInputView.visibility = View.VISIBLE
                    }
                }
                2 -> {
                    showDatePickerDialog()
                    baseValidatePaymentRequest.immediatePayment = false
                    baseValidatePaymentRequest.useTime = true
                    binding.paymentDateNormalInputView.visibility = View.VISIBLE
                }
                else -> {
                    binding.paymentDateNormalInputView.visibility = View.GONE
                    baseValidatePaymentRequest.immediatePayment = false
                    baseValidatePaymentRequest.useTime = false
                }
            }
        }

        if (paymentsViewModel.defaultToFutureDated) {
            binding.paymentTypeRadioView.selectedIndex = paymentTypeOptions.size - 1
            paymentsViewModel.defaultToFutureDated = false
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        binding.paymentDateNormalInputView.clearError()
        DatePickerDialog(baseActivity, R.style.DatePickerDialogTheme, { _: DatePicker?, year: Int, month: Int, day: Int ->
            val selectedDateMonth = if (month < 9) "0${month + 1}" else month + 1
            val selectedDateDay = if (day < 10) "0${day}" else day
            paymentsViewModel.validatePaymentRequest.paymentTransactionDateAndTime = "$year/${selectedDateMonth}/$selectedDateDay".toDate()
            binding.paymentDateNormalInputView.text = DateTimeHelper.formatDate(paymentsViewModel.validatePaymentRequest.paymentTransactionDateAndTime, SPACED_PATTERN_DD_MMMM_YYYY)
        }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]).apply {
            val nextYear = Calendar.getInstance().apply { add(Calendar.YEAR, 1) }
            val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }

            datePicker.minDate = tomorrow.timeInMillis
            datePicker.maxDate = nextYear.timeInMillis
        }.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                NotificationMethodSelectionActivity.NOTIFICATION_METHOD_SELF_REQUEST_CODE -> {
                    val selfNotificationMethod = data?.getParcelableExtra<NotificationMethodData>(NotificationMethodSelectionActivity.SHOW_SELF_NOTIFICATION_METHOD)
                    selfNotificationMethod?.let {
                        updateNotificationMethodDetails(selectedBeneficiary.beneficiaryDetails.ownNotification, it, binding.myPaymentNotificationNormalInputView)
                    }
                }
                NotificationMethodSelectionActivity.NOTIFICATION_METHOD_BENEFICIARY_REQUEST_CODE -> {
                    val beneficiaryNotificationMethod = data?.getParcelableExtra<NotificationMethodData>(NotificationMethodSelectionActivity.SHOW_BENEFICIARY_NOTIFICATION_METHOD)
                    beneficiaryNotificationMethod?.let {
                        updateNotificationMethodDetails(selectedBeneficiary.beneficiaryDetails.beneficiaryNotification, beneficiaryNotificationMethod, binding.beneficiaryPaymentNotificationNormalInputView)
                    }
                }
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        binding.paymentTypeRadioView.enableItemCheckInterface()
    }

    override fun onBackPressed(): Boolean {
        if (paymentsViewModel.beneficiaryAdded) {
            navigate(PaymentDetailsFragmentDirections.actionPaymentDetailsFragmentToPaymentHubFragment())
            return true
        }
        return false
    }
}