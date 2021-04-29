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
package com.barclays.absa.banking.transfer

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.DatePicker
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.boundary.model.rewards.RewardsAccountDetails
import com.barclays.absa.banking.boundary.model.rewards.RewardsRedeemConfirmation
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.shared.DigitalLimitState
import com.barclays.absa.banking.shared.DigitalLimitsHelper
import com.barclays.absa.banking.shared.DigitalLimitsHelper.Companion.checkTransferAmount
import com.barclays.absa.banking.transfer.TransferFundsContract.TransferFundsView
import com.barclays.absa.utils.*
import kotlinx.android.synthetic.main.transfer_funds_fragment.*
import styleguide.forms.SelectorList
import styleguide.forms.SelectorType
import styleguide.forms.StringItem
import styleguide.forms.validation.FieldRequiredValidationRule
import styleguide.forms.validation.addValidationRule
import styleguide.utils.AnimationHelper
import styleguide.utils.extensions.removeCurrency
import java.math.BigDecimal
import java.text.ParseException
import java.util.*

open class TransferFundsFragment : BaseFragment(R.layout.transfer_funds_fragment), TransferFundsView, TransferConstants {

    private lateinit var transferConfirmationData: TransferConfirmationData

    private var shouldCopyToReference = true
    private var shouldCopyFromReference = true
    private var availableBalance = BigDecimal(0)
    private var isFutureDatedTransfer = false
    private var lastFutureDate: Date? = null

    private lateinit var transferViewModel: TransferViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        transferViewModel = (context as TransferFundsActivity).viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupTalkBack()

        setToolBar(getString(R.string.transfer)) { baseActivity.finish() }

        AccessibilityUtils.announceTextFromGenericView(view, getString(R.string.talkback_transfer_home_fragment_label))

        transferViewModel.transferConfirmationData = TransferConfirmationData()
        transferConfirmationData = transferViewModel.transferConfirmationData

        attachObservers()
        initOnClickListeners()
        initItemSelector()
        populateViews()

        if (transferViewModel.fromAccountList.isEmpty() || transferViewModel.toAccountList.isEmpty()) {
            transferViewModel.minimumAmount?.let {
                setMinimumAmount(it)
            }
            val flowType = if (transferViewModel.isAvafTransfer) TransferType.AVAF_INTERACCOUNT_TRANSFER else TransferType.INTERACCOUNT_TRANSFER
            transferViewModel.fetchAccountList(flowType)
        }

        if (transferViewModel.isRewardsTransfer) {
            hideFutureDatedTransferTypeAndReferenceFields()
        }

        if (transferViewModel.isAvafTransfer) {
            AnalyticsUtil.trackAction("AVAFInterAccountTransfer_TransferScreen_ScreenDisplayed")
            fromAccountReferenceInputView.addValidationRule(FieldRequiredValidationRule(R.string.transfer_enter_valid_reference))
            toAccountReferenceInputView.addValidationRule(FieldRequiredValidationRule(R.string.transfer_enter_valid_reference))
            transferDelayHeaderTextView.visibility = View.VISIBLE
            showFutureDatedTransferTypeAndReferenceFields()
            with(toSelectAccountView) {
                setValueEditable(false)
                setSelectorViewType(SelectorType.NONE)
            }
        }
    }

    private fun attachObservers() {
        transferViewModel.fromAccountItemList = MutableLiveData()
        transferViewModel.fromAccountItemList.observe(viewLifecycleOwner, Observer {
            transferViewModel.fromAccountItemList.removeObservers(this)
            dismissProgressDialog()
            if (it.isEmpty()) {
                showMessage(getString(R.string.something_went_wrong), getString(R.string.no_from_accounts)) { _, _ -> activity?.finish() }
                return@Observer
            }

            populateFromAccountList(it)

            when {
                transferViewModel.isFromRewards -> {
                    transferViewModel.fetchRewardsToAccountList()
                    return@Observer
                }
                transferViewModel.fromAccount != null -> {
                    val index = it.indexOfFirst { accountListItem -> accountListItem.accountNumber == transferViewModel.fromAccount?.accountNumber }
                    if (index != -1) {
                        fromAccountChanged(index)
                    }
                }
                transferViewModel.toAccount != null -> {
                    val index = transferViewModel.toAccountList.indexOfFirst { accountListItem -> accountListItem.accountNumber == transferViewModel.toAccount?.accountNumber }
                    if (index != -1) {
                        setToAccount(index)
                        transferViewModel.isItemSelectedByUser = false
                    }
                }
            }
        })

        transferViewModel.toAccountItemList = MutableLiveData()
        transferViewModel.toAccountItemList.observe(viewLifecycleOwner, Observer {
            dismissProgressDialog()
            if (it.isEmpty()) {
                showMessage(getString(R.string.something_went_wrong), getString(R.string.no_to_accounts)) { _, _ -> activity?.finish() }
                return@Observer
            }
            populateToAccountList(it)

            if (transferViewModel.toAccount != null) {
                val index = it.indexOfFirst { accountListItem -> accountListItem.accountNumber == transferViewModel.toAccount?.accountNumber }
                if (index != -1) {
                    setToAccount(index)
                }
            }

            if (!TransferConstants.ABSA_REWARDS.equals(transferViewModel.fromAccount?.accountType, ignoreCase = true)) {
                showFutureDatedTransferTypeAndReferenceFields()
            }
        })

        transferViewModel.rewardsAccountListLiveData = MutableLiveData()
        transferViewModel.rewardsAccountListLiveData.observe(viewLifecycleOwner, { rewardsToList ->
            transferViewModel.rewardsAccountListLiveData.removeObservers(this)
            val listOfToAccounts = buildRewardsToAccountList(rewardsToList)

            if (transferViewModel.toAccount != null) {
                val index = listOfToAccounts.indexOfFirst { account -> account.accountNumber == transferViewModel.toAccount?.accountNumber }
                if (index != -1) {
                    setToAccount(index)
                }
            } else if (transferViewModel.isFromRewards) {
                val index = transferViewModel.fromAccountList.indexOfFirst { account -> TransferConstants.ABSA_REWARDS.equals(account.accountType, ignoreCase = true) }
                if (index != -1) {
                    fromAccountChanged(index)
                }
            }

            populateToAccountList(listOfToAccounts)
            hideFutureDatedTransferTypeAndReferenceFields()

            dismissProgressDialog()
        })

        transferViewModel.rewardsRedeemConfirmationLiveData = MutableLiveData()
        transferViewModel.rewardsRedeemConfirmationLiveData.observe(viewLifecycleOwner, {
            transferViewModel.rewardsRedeemConfirmationLiveData.removeObservers(this)
            dismissProgressDialog()
            navigateToRewardsTransferConfirmationScreen(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        transferViewModel.toAccountItemList.removeObservers(this)
    }

    private fun initOnClickListeners() {
        continueButton.setOnClickListener {
            if (transferViewModel.isAvafTransfer) {
                AnalyticsUtil.trackAction("AVAFInterAccountTransfer_TransferScreen_NextButtonClicked")
            }
            if (amountInputView.errorTextView?.visibility == View.VISIBLE) {
                AnimationHelper.shakeShakeAnimate(amountInputView)
                return@setOnClickListener
            }

            if (isAllFieldsValid) {
                if (isFutureDatedTransfer && transferDateView.selectedValue.isEmpty()) {
                    transferDateView.setError(R.string.select_transfer_future_date)
                    return@setOnClickListener
                } else if (!isFutureDatedTransfer) {
                    val date = Date()
                    transferConfirmationData.transactionDate = date
                }

                checkTransferAmount(activity as BaseActivity, Amount(amountInputView.selectedValue.removeCurrency()))
                DigitalLimitsHelper.digitalLimitState.observe(viewLifecycleOwner, { digitalLimitState: DigitalLimitState ->
                    if (digitalLimitState === DigitalLimitState.CANCELLED) {
                        dismissProgressDialog()
                    } else if (digitalLimitState === DigitalLimitState.CHANGED || digitalLimitState === DigitalLimitState.UNCHANGED) {
                        if (transferViewModel.fromAccount != null && TransferConstants.ABSA_REWARDS.equals(transferViewModel.fromAccount!!.accountType, ignoreCase = true)) {
                            transferViewModel.validateRewardsRedemption(amountInputView.selectedValue.removeCurrency())
                        } else {
                            navigateToConfirmationScreenNormalTransfer()
                        }
                    }
                })
            }
        }

        if (transferViewModel.fromAccount == null) {
            toSelectAccountView.setCustomOnClickListener { showFromAccountNeededError() }
        }

        amountInputView.setValueViewFocusChangedListener { _, hasFocus ->
            if (hasFocus && amountInputView.selectedValue.isNotEmpty() && amountInputView.selectedValueUnmasked.toDouble() == 0.00) {
                amountInputView.selectedValue = ""
            }
        }

        amountInputView.addValueViewTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateTransferAmount()
            }
        })
    }

    private fun validateTransferAmount() {
        val selectedValueUnmasked: String = amountInputView.selectedValueUnmasked
        if (selectedValueUnmasked.isNotEmpty() && BigDecimal(selectedValueUnmasked) > availableBalance) {
            amountInputView.setError(getString(R.string.transfer_amount_exceeds_balance, availableBalance))
            return
        }
        amountInputView.setDescription(getString(R.string.transfer_available, TextFormatUtils.formatBasicAmount(availableBalance)))
    }

    private fun populateViews() {
        val transferTypes = SelectorList<StringItem>()
        transferTypes.add(StringItem(getString(R.string.normal_transfer)))
        transferTypes.add(StringItem(getString(R.string.future_dated_transfer)))
        transferTypeRadioButtonView.setDataSource(transferTypes)

        transferTypeRadioButtonView.selectedIndex = 0
        transferTypeRadioButtonView.setItemCheckedInterface { checkTransferTypeState() }
        transferDateView.setOnClickListener { showDatePickerDialog() }
        populateReference()
    }

    private fun checkTransferTypeState() {
        if (transferTypeRadioButtonView.selectedIndex == 0) {
            transferDateView.visibility = View.GONE
            isFutureDatedTransfer = false
            transferConfirmationData.isFutureDatedTransfer = false
        } else if (transferTypeRadioButtonView.selectedIndex == 1) {
            isFutureDatedTransfer = true
            transferDateView.visibility = View.VISIBLE
            transferConfirmationData.isFutureDatedTransfer = true
            if (lastFutureDate != null) {
                transferConfirmationData.transactionDate = lastFutureDate as Date
            }
        }
    }

    private fun populateReference() {
        CommonUtils.setInputFilter(fromAccountReferenceInputView.editText)
        CommonUtils.setInputFilter(toAccountReferenceInputView.editText)

        fromAccountReferenceInputView.editText?.onFocusChangeListener = View.OnFocusChangeListener { _: View?, _: Boolean ->
            shouldCopyFromReference = false
        }
        toAccountReferenceInputView.editText?.onFocusChangeListener = View.OnFocusChangeListener { _: View?, _: Boolean ->
            shouldCopyToReference = false
        }

        fromAccountReferenceInputView.addValueViewTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (shouldCopyToReference && s.toString().isNotEmpty()) {
                    toAccountReferenceInputView.editText?.text = s
                }
            }
        })

        toAccountReferenceInputView.addValueViewTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (shouldCopyFromReference && s.toString().isNotEmpty()) {
                    fromAccountReferenceInputView.editText?.text = s
                }
            }
        })
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        context?.let {
            val datePickerDialog = DatePickerDialog(it, R.style.DatePickerDialogTheme, { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                calendar[year, month] = dayOfMonth
                lastFutureDate = null
                try {
                    calendar.apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 1)
                        set(Calendar.SECOND, 0)
                    }

                    lastFutureDate = calendar.time
                    transferConfirmationData.isFutureDatedTransfer = true
                    transferConfirmationData.transactionDate = lastFutureDate as Date
                    transferDateView.selectedValue = DateUtils.format(lastFutureDate!!, DateUtils.DATE_DISPLAY_PATTERN_FULL)
                    transferDateView.clearError()
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
            val nextYear = Calendar.getInstance()
            val tomorrow = Calendar.getInstance()
            try {
                tomorrow.add(Calendar.DAY_OF_YEAR, 1)
                datePickerDialog.datePicker.minDate = tomorrow.timeInMillis
                nextYear.add(Calendar.YEAR, 1)
                datePickerDialog.datePicker.maxDate = nextYear.timeInMillis
            } catch (e: IllegalArgumentException) {
                BMBLogger.d(e)
            }
            datePickerDialog.show()
        }
    }

    private fun buildRewardsToAccountList(rewardsToList: List<RewardsAccountDetails>): SelectorList<AccountListItem> {
        val listOfToAccounts = SelectorList<AccountListItem>()
        rewardsToList.forEach { rewardAccount ->
            AccountListItem().apply {
                accountNumber = rewardAccount.accountNumber
                accountType = rewardAccount.description
                accountBalance = rewardAccount.availableBalance.toString()
                listOfToAccounts.add(this)
            }
        }
        return listOfToAccounts
    }

    private val isAllFieldsValid: Boolean
        get() {
            fromSelectAccountView.hideError()
            toSelectAccountView.hideError()
            amountInputView.hideError()
            transferDateView.hideError()
            toAccountReferenceInputView.hideError()
            fromAccountReferenceInputView.hideError()
            return (fromAccountValidationRequested() && toAccountValidationRequested() && validationOfTransferAmount(amountInputView.selectedValue) && fromAccountReferenceInputView.validate() && toAccountReferenceInputView.validate())
        }

    private fun fromAccountValidationRequested(): Boolean {
        return if (fromSelectAccountView.selectedValue.isNotEmpty()) {
            true
        } else {
            showFromAccountNeededError()
            false
        }
    }

    private fun toAccountValidationRequested(): Boolean {
        return if (toSelectAccountView.selectedValue.isNotEmpty()) {
            true
        } else {
            showToAccountNeededError()
            false
        }
    }

    open fun validationOfTransferAmount(transactionAmount: String): Boolean {
        val userAmount = Amount(transactionAmount.removeCurrency())
        if (userAmount.amountDouble > 0.00) {
            return true
        } else if (userAmount.amountDouble <= 0.00) {
            showMinimumAmountError()
            return false
        }
        return false
    }

    private fun initItemSelector() {
        fromSelectAccountView.setItemSelectionInterface { index: Int -> this.fromAccountChanged(index) }
        toSelectAccountView.setItemSelectionInterface { index: Int -> this.setToAccount(index) }
    }

    open fun fromAccountChanged(selectedAccountIndex: Int) {
        if (selectedAccountIndex < transferViewModel.fromAccountList.size) {
            transferViewModel.fromAccount = transferViewModel.fromAccountList[selectedAccountIndex]
            transferViewModel.fromAccount?.let { it ->
                if (it.accountNumber == transferViewModel.toAccount?.accountNumber) {
                    transferViewModel.isItemSelectedByUser = true
                    clearToField()
                }

                setFromAccount(it.accountInformation, it.availableBalanceFormated, selectedAccountIndex)
                validateTransferAmount()
                clearToField()
                if (TransferConstants.ABSA_REWARDS.equals(it.accountType, ignoreCase = true)) {
                    if (transferViewModel.rewardsAccountListLiveData.value == null) {
                        showProgressDialog()
                        transferViewModel.fetchRewardsToAccountList()
                    } else {
                        val rewardsToAccountList = transferViewModel.rewardsAccountListLiveData.value
                        rewardsToAccountList?.let { rewardsAccountList ->
                            transferViewModel.populateListOfToAccountsForRewards()
                            populateToAccountList(buildRewardsToAccountList(rewardsAccountList))
                            hideFutureDatedTransferTypeAndReferenceFields()
                        }
                    }
                } else {
                    transferViewModel.populateToAccountList()
                }
            }
        }
    }

    private fun clearToField() {
        if (transferViewModel.isItemSelectedByUser && !transferViewModel.isAvafTransfer) {
            transferViewModel.toAccount = null
            setToAccount("", "", -1)
        }
    }

    private fun setToAccount(index: Int) {
        transferViewModel.isItemSelectedByUser = true
        if (index < transferViewModel.toAccountList.size) {
            transferViewModel.toAccount = transferViewModel.toAccountList[index]
            setToAccount(transferViewModel.toAccount!!.accountInformation, transferViewModel.toAccount!!.availableBalanceFormated, index)
        }
    }

    override fun populateToAccountList(listOfToAccounts: SelectorList<AccountListItem>?) {
        if (listOfToAccounts == null) {
            return
        }
        val contentDescriptions: MutableList<String> = ArrayList()
        listOfToAccounts.forEach { accountListItem ->
            val accountType = accountListItem.accountType
            val accountNumber = AccessibilityUtils.getTalkBackAccountNumberFromString(accountListItem.accountNumber)
            val formattedRandValue = AccessibilityUtils.getTalkBackRandValueFromString(accountListItem.accountBalance)
            contentDescriptions.add(getString(R.string.talkback_accessibility_list_items, accountType, accountNumber, formattedRandValue))
        }
        toSelectAccountView.setList(listOfToAccounts, getString(R.string.account_to_pay_from), contentDescriptions)
        toSelectAccountView.setCustomOnClickListener(null)
    }

    override fun populateFromAccountList(listOfFromAccounts: SelectorList<AccountListItem>?) {
        if (fromSelectAccountView.itemList != null || listOfFromAccounts == null) {
            return
        }
        val contentDescriptions: MutableList<String> = ArrayList()
        listOfFromAccounts.forEach { accountListItem ->
            val accountType = accountListItem.accountType
            val formattedRandValue = AccessibilityUtils.getTalkBackRandValueFromString(accountListItem.accountBalance)
            val accountNumber = AccessibilityUtils.getTalkBackAccountNumberFromString(accountListItem.accountNumber)
            contentDescriptions.add(getString(R.string.talkback_accessibility_list_items, accountType, accountNumber, formattedRandValue))
        }
        fromSelectAccountView.setList(listOfFromAccounts, getString(R.string.account_to_pay_to), contentDescriptions)
    }

    override fun setFromAccount(fromAccount: String, availableBalance: String, index: Int) {
        fromSelectAccountView.selectedIndex = index
        fromSelectAccountView.selectedValue = fromAccount
        fromSelectAccountView.contentDescription = getString(R.string.talkback_transfer_chosen_source_account, fromAccount)
        fromSelectAccountView.setDescription(String.format("%s %s", context?.getString(R.string.available_balance), availableBalance))
        amountInputView.setDescription(String.format("%s %s", context?.getString(R.string.available_balance), availableBalance))
        this.availableBalance = BigDecimal(availableBalance.removeCurrency())
    }

    override fun setToAccount(toAccount: String, availableBalance: String, index: Int) {
        toSelectAccountView.selectedIndex = index
        toSelectAccountView.selectedValue = toAccount
        val baseContext: Context = baseActivity
        toSelectAccountView.contentDescription = context?.getString(R.string.talkback_transfer_chosen_destination_account, toAccount)
        toSelectAccountView.setDescription(String.format("%s %s", baseContext.getString(R.string.available_balance), availableBalance))
    }

    override fun changeAmount(newAmount: String?) {
        amountInputView.contentDescription = AccessibilityUtils.getTalkBackAccountNumberFromString(newAmount)
        amountInputView.editText?.contentDescription = newAmount
    }

    override fun navigateToConfirmationScreenNormalTransfer() {
        dismissProgressDialog()
        var fromAccountNumber = ""
        var toAccountNumber = ""

        if (fromSelectAccountView.selectedValue.isNotEmpty()) {
            fromAccountNumber = (fromSelectAccountView.selectedItem as AccountListItem).accountNumber.toString()
        }
        if (toSelectAccountView.selectedValue.isNotEmpty()) {
            toAccountNumber = (toSelectAccountView.selectedItem as AccountListItem).accountNumber.toString()
        }

        val toReference = if (toAccountReferenceInputView.selectedValue.isNotBlank()) toAccountReferenceInputView.selectedValue else getString(R.string.transfer_reference_default, fromAccountNumber, toAccountNumber)
        val fromReference = if (fromAccountReferenceInputView.selectedValue.isNotBlank()) fromAccountReferenceInputView.selectedValue else getString(R.string.transfer_reference_default, fromAccountNumber, toAccountNumber)

        transferConfirmationData.apply {
            amountToTransfer = amountInputView.selectedValue
            fromAccountDescription = fromSelectAccountView.selectedValue
            toAccountDescription = toSelectAccountView.selectedValue
            this.fromAccountNumber = fromAccountNumber.replace(" ", "")
            this.toAccountNumber = toAccountNumber.replace(" ", "")
            fromAccountReference = fromReference
            toAccountReference = toReference
            useTime = isFutureDatedTransfer
        }

        transferViewModel.isRewardsTransfer = false
        navigate(TransferFundsFragmentDirections.actionTransferFundsFragmentToTransferFundsConfirmationFragment())
    }

    override fun navigateToRewardsTransferConfirmationScreen(successResponse: RewardsRedeemConfirmation) {
        transferViewModel.rewardsRedeemConfirmation = successResponse

        transferConfirmationData.apply {
            this.amountToTransfer = amountInputView.selectedValue
            this.fromAccountDescription = fromSelectAccountView.selectedValue
            this.toAccountDescription = toSelectAccountView.selectedValue
        }

        transferViewModel.isRewardsTransfer = true
        navigate(TransferFundsFragmentDirections.actionTransferFundsFragmentToTransferFundsConfirmationFragment())
    }

    override fun setMinimumAmount(minimumAmount: String) {
        amountInputView.selectedValue = minimumAmount
    }

    override fun showMinimumAmountError() {
        amountInputView.setError(R.string.please_enter_amount_to_transfer)
        amountInputView.errorTextView?.let { AccessibilityUtils.announceErrorFromTextWidget(it) }
    }

    override fun showFromAccountNeededError() {
        fromSelectAccountView.setError(R.string.please_select_a_from_account)
        fromSelectAccountView.errorTextView?.let { AccessibilityUtils.announceErrorFromTextWidget(it) }
    }

    override fun showToAccountNeededError() {
        toSelectAccountView.setError(R.string.please_select_a_to_account)
        toSelectAccountView.errorTextView?.let { AccessibilityUtils.announceErrorFromTextWidget(it) }
    }

    override fun hideFutureDatedTransferTypeAndReferenceFields() {
        fromAccountReferenceInputView.visibility = View.GONE
        toAccountReferenceInputView.visibility = View.GONE
        transferTypeHeadingView.visibility = View.GONE
        transferTypeRadioButtonView.visibility = View.GONE
        transferDateView.visibility = View.GONE
    }

    private fun showFutureDatedTransferTypeAndReferenceFields() {
        fromAccountReferenceInputView.visibility = View.VISIBLE
        toAccountReferenceInputView.visibility = View.VISIBLE

        if(transferViewModel.isAvafTransfer) {
            when (FeatureSwitchingCache.featureSwitchingToggles.vehicleFinanceFutureDatedTransfer) {
                FeatureSwitchingStates.GONE.key -> {
                    transferTypeHeadingView.visibility = View.GONE
                    transferTypeRadioButtonView.visibility = View.GONE
                }
                FeatureSwitchingStates.DISABLED.key -> {
                    transferTypeHeadingView.visibility = View.VISIBLE
                    transferTypeRadioButtonView.visibility = View.VISIBLE
                    transferTypeRadioButtonView.disableRadioGroup()
                }
                else -> {
                    transferTypeHeadingView.visibility = View.VISIBLE
                    transferTypeRadioButtonView.visibility = View.VISIBLE
                }
            }
        } else {
            when (FeatureSwitchingCache.featureSwitchingToggles.futureDatedTransfers) {
                FeatureSwitchingStates.GONE.key -> {
                    transferTypeHeadingView.visibility = View.GONE
                    transferTypeRadioButtonView.visibility = View.GONE
                }
                FeatureSwitchingStates.DISABLED.key -> {
                    transferTypeHeadingView.visibility = View.VISIBLE
                    transferTypeRadioButtonView.visibility = View.VISIBLE
                    transferTypeRadioButtonView.disableRadioGroup()
                }
                else -> {
                    transferTypeHeadingView.visibility = View.VISIBLE
                    transferTypeRadioButtonView.visibility = View.VISIBLE
                }
            }
        }

        checkTransferTypeState()
    }

    private fun setupTalkBack() {
        if (AccessibilityUtils.isExploreByTouchEnabled()) {
            fromSelectAccountView.setHintText(null)
            toSelectAccountView.setHintText(null)
            amountInputView.setHintText(null)
            fromSelectAccountView.contentDescription = getString(R.string.talkback_transfer_select_source_account)
            fromSelectAccountView.editText?.contentDescription = getString(R.string.talkback_transfer_select_source_account)
            toSelectAccountView.contentDescription = getString(R.string.talkback_transfer_select_destination_account)
            toSelectAccountView.editText?.contentDescription = getString(R.string.talkback_transfer_select_destination_account)
            amountInputView.contentDescription = getString(R.string.talkback_transfer_enter_amount_to_pay)
            amountInputView.editText?.contentDescription = getString(R.string.talkback_transfer_enter_amount_to_pay)
        }
    }
}