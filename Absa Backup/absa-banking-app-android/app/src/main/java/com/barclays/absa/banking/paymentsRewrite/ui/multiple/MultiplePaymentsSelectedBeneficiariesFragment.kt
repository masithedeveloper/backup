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
package com.barclays.absa.banking.paymentsRewrite.ui.multiple

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.ClientAgreementDetails
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.databinding.MultiplePaymentsSelectedBeneficiariesFragmentBinding
import com.barclays.absa.banking.databinding.WidgetSelectedBeneficiaryDetailsBinding
import com.barclays.absa.banking.databinding.WidgetSelectedBeneficiaryFooterDetailsBinding
import com.barclays.absa.banking.express.beneficiaries.dto.RegularBeneficiary
import com.barclays.absa.banking.express.shared.dto.SourceAccount
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.payments.services.multiple.MultipleBeneficiaryPaymentService
import com.barclays.absa.banking.shared.*
import com.barclays.absa.utils.*
import com.barclays.absa.utils.extensions.toSelectorList
import com.barclays.absa.utils.extensions.viewBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import styleguide.content.Profile
import styleguide.utils.extensions.removeCommasAndDots
import styleguide.utils.extensions.removeCurrency
import styleguide.utils.extensions.toFormattedAccountNumber
import styleguide.utils.extensions.toRandAmount
import java.math.BigDecimal
import java.util.*

class MultiplePaymentSelectedBeneficiariesFragment : MultiplePaymentsBaseFragment(R.layout.multiple_payments_selected_beneficiaries_fragment), MultipleBeneficiaryDetailsView, OnBackPressedInterface {

    private val binding by viewBinding(MultiplePaymentsSelectedBeneficiariesFragmentBinding::bind)
    private var isClientAgreementAccepted: Boolean = false

    private lateinit var footer: FooterView
    private lateinit var selectedBeneficiaries: List<RegularBeneficiary>
    private lateinit var clientAgreementDetails: ClientAgreementDetails

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsUtils.getInstance().trackCustomScreenView("Selected beneficiaries", MultipleBeneficiaryPaymentService.ANALYTICS_CHANNEL_NAME, BMBConstants.TRUE_CONST)

        if (!paymentsViewModel.isSourceAccountInitialised()) {
            paymentsViewModel.selectedSourceAccount = paymentsViewModel.paymentsSourceAccounts.first()
        }
        selectedBeneficiaries = multiplePaymentsViewModel.selectedBeneficiaryList

        footer = FooterView(this, binding.bodyLinearLayout)
        addBodyItemsAndFooter()

        setUpClientAgreementDetails()

        binding.nextButton.setOnClickListener {
            validatePayment()
        }
    }

    private fun setClientAgreementAccepted(isClientAgreementAccepted: Boolean) {
        binding.personalClientAgreementTertiaryContentAndLabelView.showCheckBox(!isClientAgreementAccepted)
        this.isClientAgreementAccepted = isClientAgreementAccepted
    }

    private fun setUpClientAgreementDetails() {
        if (!::clientAgreementDetails.isInitialized) {
            paymentsViewModel.fetchClientAgreementDetails()
            paymentsViewModel.clientAgreementDetailsLiveData = MutableLiveData()
            paymentsViewModel.clientAgreementDetailsLiveData.observe(viewLifecycleOwner, {
                dismissProgressDialog()
                updateClientAgreementData(it)
            })
        } else {
            setClientAgreementAccepted("Y".equals(clientAgreementDetails.clientAgreementAccepted, ignoreCase = true))
        }
    }

    private fun addBodyItemsAndFooter() {
        val isNewList = multiplePaymentsViewModel.multiplePaymentBeneficiaryWrapperList.isEmpty()
        selectedBeneficiaries.forEach { addViewToBody(it, isNewList) }
        addFooter()
    }

    private fun addViewToBody(regularBeneficiary: RegularBeneficiary, isNewList: Boolean) {
        MainScope().launch {
            val beneficiaryViewItem = BeneficiaryViewItem(this@MultiplePaymentSelectedBeneficiariesFragment, binding.bodyLinearLayout, regularBeneficiary)
            if (isNewList) {
                multiplePaymentsViewModel.multiplePaymentBeneficiaryWrapperList[regularBeneficiary.beneficiaryDetails.beneficiaryNumber] = MultiplePaymentBeneficiaryWrapper().apply {
                    this.regularBeneficiary = regularBeneficiary
                    this.beneficiaryViewItem = beneficiaryViewItem
                }
            } else {
                multiplePaymentsViewModel.multiplePaymentBeneficiaryWrapperList[regularBeneficiary.beneficiaryDetails.beneficiaryNumber]?.apply {
                    this.regularBeneficiary = regularBeneficiary
                    this.beneficiaryViewItem = beneficiaryViewItem
                }
            }
            binding.bodyLinearLayout.addView(beneficiaryViewItem.view)
            baseActivity.animate(beneficiaryViewItem.view, R.anim.fadein_quick)

            if (regularBeneficiary == selectedBeneficiaries.last()) {
                beneficiaryViewItem.setImeActionToDone()
            }
        }
    }

    private fun addFooter() {
        MainScope().launch {
            with(footer) {
                initialiseViews()
                updateViews()
                binding.bodyLinearLayout.addView(view)
                baseActivity.animate(requireView(), R.anim.fadein)
            }
            setUpPersonalClientAgreement()
        }
    }

    override fun doNavigation() {
        when {
            paymentsViewModel.hasIIPBeneficiary -> navigateToImmediateInterBankPaymentMultipleActivity()
            else -> navigateToPaymentConfirmationScreen()
        }
    }

    private fun navigateToPaymentConfirmationScreen() {
        navigate(MultiplePaymentSelectedBeneficiariesFragmentDirections.actionMultiplePaymentSelectedBeneficiariesFragmentToMultiplePaymentsConfirmationFragment())
    }

    private fun navigateToImmediateInterBankPaymentMultipleActivity() {
        navigate(MultiplePaymentSelectedBeneficiariesFragmentDirections.actionMultiplePaymentSelectedBeneficiariesFragmentToMultiplePaymentsImmediateInterbankPaymentFragment())
    }

    override fun navigateToPaymentDetails(regularBeneficiary: RegularBeneficiary) {
        paymentsViewModel.selectedBeneficiary = regularBeneficiary
        navigate(MultiplePaymentSelectedBeneficiariesFragmentDirections.actionMultiplePaymentSelectedBeneficiariesFragmentToPaymentDetailsFragment())
    }

    override fun navigateToCutOffScreen() {
        PdfUtil.showCutOffTimesPdf(baseActivity)
    }

    fun getBeneficiaryWrapper(beneficiaryNumber: Int) = multiplePaymentsViewModel.multiplePaymentBeneficiaryWrapperList[beneficiaryNumber]

    fun onTapToEditClicked(regularBeneficiary: RegularBeneficiary) {
        KeyboardUtils.hideKeyboard(this)
        navigateToPaymentDetails(regularBeneficiary)
    }

    private fun clientAgreementAccepted(isAgreementNotChecked: Boolean): Boolean {
        if (BMBConstants.ALPHABET_N.equals(clientAgreementDetails.clientAgreementAccepted, ignoreCase = true) && isAgreementNotChecked) {
            if ("I".equals(clientAgreementDetails.clientType, ignoreCase = true) || "S".equals(clientAgreementDetails.clientType, ignoreCase = true)) {
                showAgreementError(R.string.please_accept_agreement)
            } else {
                showAgreementError(R.string.please_accept_business_agreement)
            }
            return false
        }
        return true
    }

    fun validatePayment() {
        val isAgreementNotChecked = paymentsViewModel.hasIIPBeneficiary && !isClientAgreementAccepted

        if (clientAgreementAccepted(isAgreementNotChecked) && !footer.hasValidationError) {
            if (multiplePaymentsViewModel.hasZeroPaymentAmountBeneficiary()) {
                multiplePaymentsViewModel.getZeroPaymentAmountBeneficiary()?.beneficiaryViewItem?.setAmountError()
            } else {
                multiplePaymentsViewModel.calculateTotals()
                doNavigation()
            }

            //TODO : Fix this, its not right
            /*  with(multiplePaymentsViewModel) {
                  when {
                      totalFutureDatedPaymentAmount > BigDecimal.ZERO -> DigitalLimitsHelper.checkPaymentAmount(baseActivity, Amount(totalPaymentAmount), true)
                      totalNormalPaymentAmount + totalImmediatePaymentAmount > BigDecimal.ZERO -> DigitalLimitsHelper.checkPaymentAmount(baseActivity, Amount(totalPaymentAmount), true)
                      else -> DigitalLimitsHelper.checkMultiplePaymentAmount(baseActivity, Amount(totalNormalPaymentAmount + totalImmediatePaymentAmount), Amount(totalFutureDatedPaymentAmount))
                  }
              }

              DigitalLimitsHelper.digitalLimitState.observe(viewLifecycleOwner, { digitalLimitState: DigitalLimitState ->
                  DigitalLimitsHelper.digitalLimitState.removeObservers(this)
                  when {
                      digitalLimitState === DigitalLimitState.CHANGED -> doNavigation()
                      digitalLimitState === DigitalLimitState.UNCHANGED -> doNavigation()
                      else -> dismissProgressDialog()
                  }
              })*/
        }
    }

    fun onDeselectBeneficiaryClicked(regularBeneficiary: RegularBeneficiary) {
        AnalyticsUtils.getInstance().trackCustomScreenView("Selected beneficiaries - Deselect confirmation", MultipleBeneficiaryPaymentService.ANALYTICS_CHANNEL_NAME, BMBConstants.TRUE_CONST)
        BaseAlertDialog.showAlertDialog(AlertDialogProperties.Builder()
                .title(getString(R.string.deselect_beneficiary))
                .message(getString(R.string.deselect_beneficiary_message, regularBeneficiary.beneficiaryName))
                .positiveButton(getString(R.string.deselect))
                .positiveDismissListener { _: DialogInterface?, _: Int ->
                    var beneficiaryWrapper = getBeneficiaryWrapper(regularBeneficiary.beneficiaryNumber)
                    multiplePaymentsViewModel.removeBeneficiaryFromList(regularBeneficiary)
                    beneficiaryWrapper?.let {
                        binding.bodyLinearLayout.removeView(it.beneficiaryViewItem.view)
                        updateTotalAmount()
                    }
                    if (multiplePaymentsViewModel.multiplePaymentBeneficiaryWrapperList.size == 1) {
                        val lastItemKey = multiplePaymentsViewModel.multiplePaymentBeneficiaryWrapperList.keys.iterator().next()
                        beneficiaryWrapper = multiplePaymentsViewModel.multiplePaymentBeneficiaryWrapperList[lastItemKey]
                        beneficiaryWrapper?.beneficiaryViewItem?.hideDeleteImageView()
                    }
                }
                .negativeButton(getString(R.string.cancel))
                .build())
    }

    override fun showAgreementError(errorMessage: Int) {
        baseActivity.toastShort(getString(errorMessage))
    }

    override fun updateClientAgreementData(clientAgreementDetails: ClientAgreementDetails) {
        this.clientAgreementDetails = clientAgreementDetails
        setClientAgreementAccepted("Y".equals(clientAgreementDetails.clientAgreementAccepted, ignoreCase = true))
        footer.initialiseViews()
    }

    fun personalClientAgreementDocument() {
        AnalyticsUtils.getInstance().trackCustomScreenView("PDF container", MultipleBeneficiaryPaymentService.ANALYTICS_CHANNEL_NAME, BMBConstants.TRUE_CONST)
        PdfUtil.showTermsAndConditionsClientAgreement(baseActivity, CustomerProfileObject.instance.clientTypeGroup)
    }

    fun getSourceAccount(): SourceAccount = paymentsViewModel.selectedSourceAccount

    fun setSourceAccount(sourceAccount: SourceAccount) {
        paymentsViewModel.selectedSourceAccount = sourceAccount
    }

    fun updateTotalAmount() {
        footer.updateViews()
        setUpPersonalClientAgreement()
    }

    private fun setUpPersonalClientAgreement() {
        if (paymentsViewModel.hasIIPBeneficiary) {
            binding.personalClientAgreementTertiaryContentAndLabelView.visibility = View.VISIBLE
            val clientAgreement = if (isBusinessAccount) getString(R.string.business_client_agreement) else getString(R.string.personal_client_agreement)
            if (isClientAgreementAccepted) {
                binding.personalClientAgreementTertiaryContentAndLabelView.showCheckBox(false)
                CommonUtils.makeTextClickable(context, R.string.client_agreement_have_accepted, clientAgreement, performClickOnPersonalClientAgreement, binding.personalClientAgreementTertiaryContentAndLabelView.contentTextView, R.color.color_FF666666)
            } else {
                CommonUtils.makeTextClickable(context, R.string.accept_personal_client_agreement, clientAgreement, performClickOnPersonalClientAgreement, binding.personalClientAgreementTertiaryContentAndLabelView.contentTextView, R.color.color_FF666666)
            }
        } else {
            binding.personalClientAgreementTertiaryContentAndLabelView.visibility = View.GONE
        }
    }

    private val performClickOnPersonalClientAgreement: ClickableSpan = object : ClickableSpan() {
        override fun onClick(view: View) {
            personalClientAgreementDocument()
        }
    }

    override fun onBackPressed(): Boolean {
        KeyboardUtils.hideKeyboard(this)
        return false
    }
}

internal class FooterView(private val paymentSelectedBeneficiariesFragment: MultiplePaymentSelectedBeneficiariesFragment, parent: ViewGroup?) {
    private val binding = WidgetSelectedBeneficiaryFooterDetailsBinding.inflate(LayoutInflater.from(paymentSelectedBeneficiariesFragment.context), parent, false)

    val hasValidationError: Boolean
        get() = with(paymentSelectedBeneficiariesFragment) {
            multiplePaymentsViewModel.totalPaymentAmount > getSourceAccount().availableBalance.removeCommasAndDots().toBigDecimal()
        }

    val view: View
        get() = binding.root

    init {
        initialiseViews()
        updateViews()
    }

    fun initialiseViews() {
        val paymentsSourceAccounts = paymentSelectedBeneficiariesFragment.paymentsViewModel.paymentsSourceAccounts
        val accountSelectorList = paymentsSourceAccounts.toSelectorList {
            SourceAccountSelector().apply {
                accountName = it.accountName
                accountNumber = it.accountNumber
                availableBalance = it.availableBalance
            }
        }

        with(binding) {
            fromAccountView.setList(accountSelectorList, paymentSelectedBeneficiariesFragment.getString(R.string.select_account_toolbar_title))
            fromAccountView.setItemSelectionInterface { index: Int ->
                paymentSelectedBeneficiariesFragment.setSourceAccount(paymentsSourceAccounts[index])
                paymentSelectedBeneficiariesFragment.updateTotalAmount()
            }

            importantNoticeView.setOnClickListener { showPaymentNotice() }
        }
    }

    fun updateViews() {
        val totalAmount = paymentSelectedBeneficiariesFragment.multiplePaymentsViewModel.totalPaymentAmount
        val sourceAccount = paymentSelectedBeneficiariesFragment.getSourceAccount()
        val totalAmountString = TextFormatUtils.formatBasicAmount(totalAmount.toString())
        val amountRemaining = AccessibilityUtils.getTalkBackRandValueFromString(sourceAccount.availableBalance)

        binding.amountView.apply {
            title = paymentSelectedBeneficiariesFragment.getString(R.string.currency_with_amount, totalAmountString)
            contentDescription = paymentSelectedBeneficiariesFragment.getString(R.string.talkback_multipay_normal_payments_total, AccessibilityUtils.getTalkBackRandValueFromString(totalAmountString))
        }

        binding.fromAccountView.apply {
            text = getFromAccountNameAndNumber(sourceAccount)
            contentDescription = editText?.let { AccessibilityUtils.getTalkBackRandValueFromTextView(it) }
            descriptionTextView?.contentDescription = paymentSelectedBeneficiariesFragment.getString(R.string.talkback_multipay_account_details_available_balance, sourceAccount.accountType, sourceAccount.accountNumber.toFormattedAccountNumber(), amountRemaining)
        }

        when {
            sourceAccount.isBalanceMasked -> setFooterValue(sourceAccount, false)
            else -> {
                val availableBalance = sourceAccount.availableBalance.removeCommasAndDots().toBigDecimal()
                setFooterValue(sourceAccount, totalAmount > availableBalance)
            }
        }
    }

    private fun getFromAccountNameAndNumber(selectedAccount: SourceAccount): String = selectedAccount.accountNameAndNumber

    private fun setFooterValue(sourceAccount: SourceAccount, exceededTotalAmount: Boolean) {
        when {
            sourceAccount.isBalanceMasked -> binding.fromAccountView.setDescription(sourceAccount.availableBalance)
            exceededTotalAmount -> binding.fromAccountView.setError(String.format("%s %s", sourceAccount.availableBalance, paymentSelectedBeneficiariesFragment.getString(R.string.available_title)))
            else -> binding.fromAccountView.setDescription(paymentSelectedBeneficiariesFragment.getString(R.string.account_available_balance, sourceAccount.availableBalance.toRandAmount()))
        }
        binding.availableBalanceErrorMessage.visibility = if (exceededTotalAmount) View.VISIBLE else View.GONE
    }

    private fun showPaymentNotice() {
        AnalyticsUtils.getInstance().trackCustomScreenView("Important notice expanded", MultipleBeneficiaryPaymentService.ANALYTICS_CHANNEL_NAME, BMBConstants.TRUE_CONST)
        paymentSelectedBeneficiariesFragment.navigate(MultiplePaymentSelectedBeneficiariesFragmentDirections.actionMultiplePaymentSelectedBeneficiariesFragmentToImportantNoticeFragment())
    }
}

class BeneficiaryViewItem(private val selectedBeneficiaryFragment: MultiplePaymentSelectedBeneficiariesFragment, parent: ViewGroup, regularBeneficiary: RegularBeneficiary) {
    private val binding = WidgetSelectedBeneficiaryDetailsBinding.inflate(LayoutInflater.from(selectedBeneficiaryFragment.context), parent, false)

    private var beneficiaryNumber = regularBeneficiary.beneficiaryDetails.beneficiaryNumber
    private val beneficiaryDetailsView = BeneficiaryDetailsView(binding)

    val view: View
        get() = binding.root

    var amount: BigDecimal = BigDecimal.ZERO
        private set(amountValue) {
            binding.amountLargeInputView.clear()
            if (amountValue > BigDecimal(0)) {
                binding.amountLargeInputView.selectedValue = TextFormatUtils.formatBasicAmount(amountValue).removeCurrency()
            }
        }

    init {
        setUpListeners()
        beneficiaryDetailsView.setTapToEditClickListener(object : BeneficiaryDetailsView.TapToEditClickListener {
            override fun onTapToEditClicked() {
                selectedBeneficiaryFragment.onTapToEditClicked(regularBeneficiary)
            }
        })
        binding.paymentDetailsOptionActionButtonView.removeTopAndBottomMargins()
        binding.deleteImageView.contentDescription = selectedBeneficiaryFragment.getString(R.string.talkback_multipay_delete_beneficiary_from_payment_list, binding.profileView.nameTextView.text.toString())
        val lastTransactionAmount = selectedBeneficiaryFragment.getString(R.string.multiple_payment_last_transaction, getLastTransactionAmount(regularBeneficiary).toRandAmount())
        val accessibilityAmount = AccessibilityUtils.getTalkBackRandValueFromString(getLastTransactionAmount(regularBeneficiary))
        binding.profileView.setProfile(Profile(regularBeneficiary.beneficiaryName, lastTransactionAmount, null))
        binding.profileView.contentDescription = selectedBeneficiaryFragment.getString(R.string.talkback_multipay_last_transaction_amount, accessibilityAmount)
        val onFocusChangeListener: View.OnFocusChangeListener? = binding.amountLargeInputView.editText?.onFocusChangeListener
        binding.amountLargeInputView.editText?.onFocusChangeListener = View.OnFocusChangeListener { v: View?, hasFocus: Boolean ->
            onFocusChangeListener?.onFocusChange(v, hasFocus)
            if (!hasFocus) {
                binding.amountLargeInputView.setEditTextContentDescription(AccessibilityUtils.getTalkBackRandValueFromTextView(binding.amountLargeInputView.editText!!))
                binding.amountLargeInputView.titleTextView?.contentDescription = AccessibilityUtils.getTalkBackRandValueFromTextView(binding.amountLargeInputView.editText!!)
            }
        }
        binding.deleteImageView.setOnClickListener { v: View ->
            selectedBeneficiaryFragment.baseActivity.animate(v, R.anim.expand_half)
            selectedBeneficiaryFragment.onDeselectBeneficiaryClicked(regularBeneficiary)
        }
        updateView()
    }

    fun hideDeleteImageView() {
        binding.deleteImageView.visibility = View.INVISIBLE
    }

    private fun setUpListeners() {
        with(binding.amountLargeInputView) {
            addValueViewTextWatcher(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    hideError()
                    val beneficiaryWrapper = selectedBeneficiaryFragment.getBeneficiaryWrapper(beneficiaryNumber)
                    beneficiaryWrapper?.paymentAmount = if (selectedValueUnmasked.isNotBlank()) BigDecimal(selectedValueUnmasked) else BigDecimal.ZERO
                    selectedBeneficiaryFragment.updateTotalAmount()
                }
            })

            onFocusChangeListener = View.OnFocusChangeListener { _: View?, hasFocus: Boolean ->
                if (!hasFocus) {
                    val beneficiaryWrapper = selectedBeneficiaryFragment.getBeneficiaryWrapper(beneficiaryNumber)
                    beneficiaryWrapper?.let {
                        selectedValue = TextFormatUtils.formatBasicAmount(it.paymentAmount)
                    }
                    setEditTextContentDescription(selectedBeneficiaryFragment.getString(R.string.talkback_multipay_confirmation_amount_being_payed, beneficiaryWrapper?.regularBeneficiary?.beneficiaryName, binding.amountLargeInputView.editText?.let { AccessibilityUtils.getTalkBackRandValueFromTextView(it) }))
                    titleTextView?.contentDescription = selectedBeneficiaryFragment.getString(R.string.talkback_multipay_confirmation_amount_being_payed, beneficiaryWrapper?.regularBeneficiary?.beneficiaryName, binding.amountLargeInputView.editText?.let { AccessibilityUtils.getTalkBackRandValueFromTextView(it) })
                }
            }
        }
    }

    private fun updateView() {
        val wrapper = selectedBeneficiaryFragment.getBeneficiaryWrapper(beneficiaryNumber)
        wrapper?.let {
            amount = it.paymentAmount
            selectedBeneficiaryFragment.updateTotalAmount()
        }
    }

    fun setAmountError() {
        val stringAmount = selectedBeneficiaryFragment.getString(R.string.amount)
        binding.amountLargeInputView.setError(selectedBeneficiaryFragment.getString(R.string.pleaseEnterValid, stringAmount))
    }

    private fun getLastTransactionAmount(regularBeneficiary: RegularBeneficiary): String {
        regularBeneficiary.processedTransactions.firstOrNull()?.let { return it.paymentAmount }
        return "0.0"
    }

    fun setImeActionToDone() {
        binding.amountLargeInputView.setImeOptions(EditorInfo.IME_ACTION_DONE)
    }
}

internal class BeneficiaryDetailsView(private val binding: WidgetSelectedBeneficiaryDetailsBinding) {
    private var tapToEditClickListener: TapToEditClickListener? = null

    fun getAmount() = binding.amountLargeInputView.selectedValueUnmasked

    fun setTapToEditClickListener(tapToEditClickListener: TapToEditClickListener?) {
        this.tapToEditClickListener = tapToEditClickListener
        binding.paymentDetailsOptionActionButtonView.setOnClickListener { tapToEditClickListener?.onTapToEditClicked() }
    }

    interface TapToEditClickListener {
        fun onTapToEditClicked()
    }
}