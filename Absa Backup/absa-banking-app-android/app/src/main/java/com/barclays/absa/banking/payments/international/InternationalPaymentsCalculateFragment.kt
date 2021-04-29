/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.payments.international

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor
import com.barclays.absa.banking.boundary.monitoring.MonitoringService.Companion.MONITORING_EVENT_ATTRIBUTE_NAME_INCORRECT_CURRENCY_CODE
import com.barclays.absa.banking.cashSend.ui.AccountObjectWrapper
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.AMOUNT_EXCEEDS_AVAILABLE
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.AMOUNT_TOO_LARGE
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.CALLING_FRAGMENT
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.CURRENCY_NOT_SUPPORTED
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.D0012
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.D0204
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.D0208
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.D0219_ERROR
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.EXCEEDS
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.FICA
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.INTERNATIONAL_PAYMENT
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.INTERNATIONAL_PAYMENTS
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.MAXIMUM_ZAR_AMOUNT
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.MINIMUM_USD_AMOUNT
import com.barclays.absa.banking.payments.international.data.*
import com.barclays.absa.banking.payments.international.services.IInternationalPaymentCacheService
import com.barclays.absa.banking.settings.ui.ManageDigitalLimitsActivity
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.barclays.absa.utils.AbsaCacheManager
import com.barclays.absa.utils.AnalyticsUtil
import com.newrelic.agent.android.NewRelic
import kotlinx.android.synthetic.main.international_payments_calculate_fragment.*
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import java.util.*
import kotlin.collections.ArrayList

class InternationalPaymentsCalculateFragment : InternationalPaymentsAbstractBaseFragment(R.layout.international_payments_calculate_fragment),
        InternationalPaymentsContract.InternationalPaymentsCalculateView {

    private val internationalPaymentCacheService: IInternationalPaymentCacheService = getServiceInterface()
    private lateinit var presenter: InternationalPaymentsContract.InternationalPaymentsCalculatePresenter
    private lateinit var fromAccounts: SelectorList<AccountObjectWrapper>
    private lateinit var paymentCalculations: PaymentCalculations
    private var beneficiaryDetails: BeneficiaryEnteredDetails? = BeneficiaryEnteredDetails()
    private var toCurrency = ToCurrency()
    private var selectedFromAccount = AccountObject()
    private var payoutCurrencyList: ArrayList<CurrencyList> = arrayListOf()
    private var sendCurrencyList: ArrayList<CurrencyList> = arrayListOf()
    private var payoutCurrencyCode: String = ""
    private var sendCurrencyCode: String? = ""
    private var currencySymbol: String = ""
    private var amountToSend: String? = ""
    private var availableBalance = Amount()
    private var isContinueButton = false
    private var totalAmount = Amount()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_CalculateExchangeRateScreen_ScreenDisplayed")

        showToolBar()
        setHasOptionsMenu(true)
        setToolBarNoBack(R.string.payment_details)

        arguments?.let {
            val bundle = InternationalPaymentsCalculateFragmentArgs.fromBundle(it)
            val tempCurrencyList = bundle.listOfCurrencies
            val tempSendCurrencyList = bundle.sendCurrency
            val tempToCurrency = bundle.toCurrency
            if (tempCurrencyList != null && tempToCurrency != null && tempSendCurrencyList != null) {
                sendCurrencyList = ArrayList(tempCurrencyList.toList())
                payoutCurrencyList = ArrayList(tempSendCurrencyList.toList())
                toCurrency = tempToCurrency
            }
        }

        setUpCurrencyList()

        internationalPaymentsActivity.setProgressStep(4)
        presenter = InternationalPaymentsCalculatePresenter(this)
        beneficiaryDetails = internationalPaymentCacheService.getEnteredBeneficiaryDetails()

        val beneficiaryListItem = InternationalBeneficiaryItem()
        beneficiaryListItem.name = beneficiaryDetails?.beneficiaryNames?.split("\\s".toRegex())?.get(0)
        beneficiaryListItem.residentialStatus = if (beneficiaryDetails?.beneficiaryCitizenship.equals(InternationalPaymentsConstants.NON_RESIDENT_OTHER, ignoreCase = true) || beneficiaryDetails?.beneficiaryCitizenship.equals(InternationalPaymentsConstants.NON_SA_RESIDENT, ignoreCase = true)) {
            getString(R.string.non_sa_resident)
        } else {
            getString(R.string.temporarily_abroad)
        }

        beneficiaryView.setBeneficiary(beneficiaryListItem)
        accountSelectorView.selectedValue = getString(R.string.select_an_acc_from)

        val accountList = AbsaCacheManager.getTransactionalAccounts()
        fromAccounts = SelectorList()
        if (accountList != null) {
            accountList.forEach { fromAccounts.add(AccountObjectWrapper(it)) }

            accountSelectorView.setList(fromAccounts, getString(R.string.account_to_pay_from))

            if (fromAccounts.isNotEmpty()) {
                val firstAccount = fromAccounts.first()
                selectedFromAccount = firstAccount.accountObject
                accountSelectorView.selectedIndex = 0
                accountSelectorView.selectedValue = firstAccount.formattedValue
                availableBalance = firstAccount.accountObject.availableBalance
                amountToSendInputView.setDescription(String.format("%s %s", firstAccount.accountObject.availableBalanceFormated, getString(R.string.available)))
            }
        }

        accountSelectorView.setItemSelectionInterface { index ->
            selectedFromAccount = fromAccounts[index].accountObject
            accountSelectorView.selectedValue = fromAccounts[index].formattedValue
            amountToSendInputView.setDescription(String.format("%s %s", fromAccounts[index].accountObject.availableBalanceFormated, getString(R.string.available)))
            availableBalance = fromAccounts[index].accountObject.availableBalance
            validateInputs()
        }

        continueButton.text = getString(R.string.calculate_exchange_rate)

        try {
            currencySymbol = Currency.getInstance(sendCurrencyCode).symbol
        } catch (e: IllegalArgumentException) {
            recordIncorrectCurrency()
        }

        amountToSendInputView.setCurrency(currencySymbol)

        continueButton.setOnClickListener {
            if (!isContinueButton) {
                var amountToSend = 0.0
                if (amountToSendInputView.selectedValueUnmasked.isNotEmpty()) {
                    amountToSend = amountToSendInputView.selectedValueUnmasked.toDouble()
                }

                if (amountToSend > 0) {
                    AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_CalculateExchangeRateScreen_CalculateExchangeRateButtonClicked")
                    paymentCalculations = PaymentCalculations()
                    paymentCalculations.amountEntered = Amount(amountToSendInputView.selectedValueUnmasked).getAmount()
                    paymentCalculations.country = beneficiaryDetails?.paymentDestinationCountryCode
                    paymentCalculations.currencyCode = payoutCurrencyCode
                    paymentCalculations.sendCurrency = sendCurrencyCode
                    val gender = beneficiaryDetails?.beneficiaryGender
                    if (!gender.isNullOrEmpty()) {
                        paymentCalculations.gender = gender[0].toString()
                    }
                    paymentCalculations.availableBalance = availableBalance.getAmount()
                    paymentCalculations.accountNumber = selectedFromAccount.accountNumber
                    presenter.calculationButtonClicked(paymentCalculations, internationalPaymentCacheService.getEnteredBeneficiaryDetails())
                } else {
                    amountToSendInputView.setError(getString(R.string.international_payments_enter_an_amount))
                }
            } else {
                BaseAlertDialog.showAlertDialog(AlertDialogProperties.Builder()
                        .title(getString(R.string.international_payments_confirmation_title))
                        .message(getString(R.string.international_payments_confirmation_message))
                        .positiveButton(getString(R.string.continue_button))
                        .positiveDismissListener { _, _ ->
                            beneficiaryDetails?.apply {
                                destinationCurrency = toCurrency.toCurrencyCode
                                currencyToPay = payoutCurrencyCode
                                sendCurrency = sendCurrencyCode
                                paymentAmount = amountToSendInputView.selectedValueUnmasked
                                fromAccountNumber = selectedFromAccount.accountNumber
                                fromAccountBalance = selectedFromAccount.availableBalance?.getAmount()
                                fromAccountDescription = selectedFromAccount.description
                                fromAccountType = selectedFromAccount.accountType
                            }

                            beneficiaryDetails?.let {
                                internationalPaymentsActivity.updateBeneficiaryBeneficiaryDetailsDataModel(it)
                                presenter.fetchQuotation(it)
                            }
                            AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_CalculateExchangeRateScreen_ContinueButtonClicked")
                        }
                        .negativeButton(getString(R.string.international_payments_go_back))
                        .build())
            }
        }

        amountToSendInputView.addValueViewTextWatcher(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != amountToSend) {
                    amountToSendInputView.showDescription(true)
                    continueButton.text = getString(R.string.calculate_exchange_rate)
                    isContinueButton = false
                }
            }
        })
    }

    private fun setUpCurrencyList() {
        val payoutCurrencySelectorList: SelectorList<StringItem> = SelectorList()
        payoutCurrencyList.forEach { item ->
            payoutCurrencySelectorList.add(StringItem(item.getCurrencyMenuItem()))
        }

        val sendSelectorList: SelectorList<StringItem> = SelectorList()
        sendCurrencyList.forEach { item ->
            sendSelectorList.add(StringItem(item.getCurrencyMenuItem()))
        }

        sendCurrencyNormalInputView.setList(sendSelectorList, "")
        if (sendSelectorList.isNotEmpty()) {
            sendCurrencyNormalInputView.selectedIndex = 0
        }

        payoutCurrencyInputView.setList(payoutCurrencySelectorList, "")
        if (payoutCurrencySelectorList.isNotEmpty()) {
            payoutCurrencyInputView.selectedIndex = 0
        }

        sendCurrencyNormalInputView.setItemSelectionInterface { index ->
            sendCurrencyCode = sendCurrencyList[index].currencyCode
            try {
                currencySymbol = Currency.getInstance(sendCurrencyCode).symbol
            } catch (e: IllegalArgumentException) {
                recordIncorrectCurrency()
            }

            amountToSendInputView.setCurrency(currencySymbol)
            payoutCurrencyCode = sendCurrencyList[index].currencyCode ?: ""
            internationalPaymentsActivity.calculateRetryCount = 0
            amountToSendInputView.clear()

            payoutCurrencyCode = payoutCurrencyList[0].currencyCode ?: ""
        }

        payoutCurrencyInputView.setItemSelectionInterface {
            amountToSendInputView.clear()
        }

        if (!payoutCurrencySelectorList.isNullOrEmpty()) {
            val selectedIndex = 0
            try {
                currencySymbol = Currency.getInstance(sendCurrencyCode).symbol
            } catch (e: IllegalArgumentException) {
                recordIncorrectCurrency()
            }
            amountToSendInputView.setCurrency(currencySymbol)
            sendCurrencyCode = sendCurrencyList[selectedIndex].currencyCode
            payoutCurrencyCode = payoutCurrencyList[selectedIndex].currencyCode ?: ""
            sendCurrencyNormalInputView.selectedIndex = selectedIndex
        }
    }

    private fun recordIncorrectCurrency() {
        val eventMap = HashMap<String, Any>()
        eventMap[MONITORING_EVENT_ATTRIBUTE_NAME_INCORRECT_CURRENCY_CODE] = payoutCurrencyCode as Any
        NewRelic.recordCustomEvent("StringLocale", eventMap)
    }

    override fun populateExchangeRate(exchangeRate: String) {
        totalAmount = Amount(exchangeRate)
        totalAmountTextView.text = totalAmount.toString()
        amountDueByYouLabelTextView.visibility = View.VISIBLE
        totalAmountTextView.visibility = View.VISIBLE
        totalAmountDueTextView.visibility = View.VISIBLE
        continueButton.text = getString(R.string.continue_button)
        isContinueButton = true
        Handler(Looper.getMainLooper()).postDelayed({
            focusBottomOfView()
        }, 350)
    }

    override fun showRetryScreen() {
        internationalPaymentsActivity.calculateRetryCount = internationalPaymentsActivity.calculateRetryCount + 1
        internationalPaymentsActivity.hideProgressIndicator()
        hideToolBar()
        val factory = InternationalPaymentsResultFactory()
        val genericResultScreenProperties = if (internationalPaymentsActivity.calculateRetryCount <= 2) {
            factory.buildInternationalPaymentCannotCalculateBundle(internationalPaymentsActivity)
        } else {
            factory.buildInternationalPaymentCannotCalculateFailedBundle(internationalPaymentsActivity)
        }
        navigate(InternationalPaymentsCalculateFragmentDirections.actionInternationalPaymentsCalculateFragmentToInternationalPaymentsResultsFragment(genericResultScreenProperties, true))
    }

    override fun isButtonContinueButton(): Boolean = isContinueButton

    private fun focusBottomOfView() {
        scrollView.post { scrollView.smoothScrollTo(0, continueButton.y.toInt()) }
    }

    private fun validateInputs() {
        if (totalAmount.amountDouble > MAXIMUM_ZAR_AMOUNT.toDouble()) {
            amountToSendInputView.setError(getString(R.string.international_payments_amount_cannot_be_more_than, Amount(MAXIMUM_ZAR_AMOUNT).getAmount()))
            isContinueButton = false
        }
        if (availableBalance.amountDouble < totalAmount.amountDouble) {
            amountToSendInputView.setError(getString(R.string.international_payments_the_payment_exceeds_your_available_balance))
            isContinueButton = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.cancel_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cancel_menu_item -> {
                AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_CalculateExchangeRateScreen_CancelButtonClicked")
                BaseAlertDialog.showYesNoDialog(AlertDialogProperties.Builder()
                        .message(getString(R.string.international_are_you_sure_you_want_to_cancel))
                        .positiveDismissListener { _, _ ->
                            AnalyticsUtils.getInstance().trackCancelButton(BaseActivity.mScreenName, BaseActivity.mSiteSection)
                            BaseAlertDialog.dismissAlertDialog()
                            activity?.finish()
                        })
                internationalPaymentsActivity.hideProgressIndicator()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun returnDataToQuoteDetailsScreen(transferQuoteDetails: TransferQuoteDetails) {
        navigate(InternationalPaymentsCalculateFragmentDirections.actionInternationalPaymentsCalculateFragmentToInternationalPaymentsConfirmPaymentFragment(transferQuoteDetails))
    }

    override fun showErrorMessage(errorMessage: String) = internationalPaymentsActivity.showMessageError(errorMessage)

    override fun fillValidationError(error: String) {
        when {
            error.contains(D0219_ERROR, true) -> amountToSendInputView.setError(getString(R.string.international_payments_minimum_amount_error, Amount("$", MINIMUM_USD_AMOUNT).toString()))
            error.contains(AMOUNT_TOO_LARGE) -> amountToSendInputView.setError(getString(R.string.international_payments_amount_cannot_be_more_than, Amount(MAXIMUM_ZAR_AMOUNT).toString()))
            error.contains(EXCEEDS) -> {
                BaseAlertDialog.showAlertDialog(AlertDialogProperties.Builder()
                        .title(getString(R.string.international_payments_transfer_limit_heading))
                        .message(getString(R.string.international_payments_transfer_limit))
                        .positiveButton(getString(R.string.update))
                        .positiveDismissListener { _, _ ->
                            val intent = Intent(internationalPaymentsActivity, ManageDigitalLimitsActivity::class.java)
                            intent.putExtra(CALLING_FRAGMENT, INTERNATIONAL_PAYMENT)
                            startActivity(intent)
                        }
                        .negativeButton(getString(R.string.cancel))
                        .build())
            }
            error.contains(FICA) -> {
                hideToolBar()
                internationalPaymentsActivity.hideProgressIndicator()
                val genericResultScreenProperties = InternationalPaymentsResultFactory().buildFicaFailureBundle(internationalPaymentsActivity)
                navigate(InternationalPaymentsCalculateFragmentDirections.actionInternationalPaymentsCalculateFragmentToInternationalPaymentsResultsFragment(genericResultScreenProperties, true))
            }

            error.contains(D0012) -> {
                val map = LinkedHashMap<String, Any?>()
                map[D0012] = "internationalPayments"
                MonitoringInteractor().logMonitoringEvent(D0012, map)
                showRetryScreen()
            }

            error.contains(D0204) -> {
                amountToSendInputView.setError(getString(R.string.international_payments_amount_cannot_be_more_than, Amount(MAXIMUM_ZAR_AMOUNT).toString()))
            }

            error.contains(CURRENCY_NOT_SUPPORTED) -> {
                amountToSendInputView.setError(getString(R.string.international_payments_currency_not_supported))
            }

            error.contains(AMOUNT_EXCEEDS_AVAILABLE) -> {
                showErrorMessage(error)
            }

            error.contains(D0208) -> showErrorMessage(error)

            BuildConfig.UAT -> showErrorMessage(error)

            else -> showGenericErrorMessage()
        }
    }
}