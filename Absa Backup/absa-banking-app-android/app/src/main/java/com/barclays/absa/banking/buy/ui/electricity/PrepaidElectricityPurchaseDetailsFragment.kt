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
package com.barclays.absa.banking.buy.ui.electricity

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.*
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.utils.AlertInfo
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.shared.NotificationMethodSelectionActivity
import com.barclays.absa.utils.AbsaCacheManager
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.extensions.toRandAmount
import kotlinx.android.synthetic.main.prepaid_electricity_purchase_details_fragment.*
import styleguide.content.BeneficiaryListItem
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.forms.notificationmethodview.NotificationMethodData
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties.PropertiesBuilder
import styleguide.utils.extensions.insertSpaceAtIncrements
import styleguide.utils.extensions.removeSpaces
import styleguide.utils.extensions.toFormattedCellphoneNumber
import java.util.*

class PrepaidElectricityPurchaseDetailsFragment : BaseFragment(R.layout.prepaid_electricity_purchase_details_fragment) {
    private lateinit var prepaidElectricityView: PrepaidElectricityView
    private lateinit var selectedAccountObject: AccountObject
    private lateinit var beneficiaryDetailObject: BeneficiaryDetailObject
    private var purchasePrepaidElectricityResultObject: PurchasePrepaidElectricityResultObject? = null
    private lateinit var meterNumberObject: MeterNumberObject
    private lateinit var transactionalAccounts: ArrayList<AccountObject>
    private var index = 0
    private var hasAccountArrearsCheckExecuted = false

    companion object {
        private const val BENEFICIARY_OBJECT = "beneficiaryObject"
        private const val BENEFICIARY_DETAIL_OBJECT = "beneficiaryDetailObject"
        private const val METER_NUMBER_OBJECT = "meterNumberObject"
        private const val INDEX = "index"
        private const val MINIMUM_AMOUNT = 30
        private const val MAX_AMOUNT = 1000

        @JvmStatic
        fun newInstance(beneficiaryObject: BeneficiaryObject?, beneficiaryDetailObject: BeneficiaryDetailObject?, meterNumberObject: MeterNumberObject?) = PrepaidElectricityPurchaseDetailsFragment().apply {
            arguments = Bundle().apply {
                putSerializable(BENEFICIARY_OBJECT, beneficiaryObject)
                putSerializable(BENEFICIARY_DETAIL_OBJECT, beneficiaryDetailObject)
                putSerializable(METER_NUMBER_OBJECT, meterNumberObject)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        prepaidElectricityView = context as PrepaidElectricityView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireArguments().apply {
            setToolBar(getString(R.string.prepaid_electricity_purchase_details))
            beneficiaryDetailObject = getSerializable(BENEFICIARY_DETAIL_OBJECT) as BeneficiaryDetailObject
            meterNumberObject = getSerializable(METER_NUMBER_OBJECT) as MeterNumberObject
            beneficiaryDetailObject.benNoticeType = BMBConstants.NOTICE_TYPE_NONE_SHORT

            initViews()
            if (!hasAccountArrearsCheckExecuted) {
                initAccountArrearsCheck()
            }
        }

        AnalyticsUtil.trackAction(PrepaidElectricityActivity.BUY_ELECTRICITY, "BuyElectricity_PurchaseDetailsScreen_ScreenDisplayed")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(INDEX, index)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(inState: Bundle?) {
        super.onViewStateRestored(inState)
        if (inState != null && inState.containsKey(INDEX)) {
            selectAccount(inState.getInt(INDEX).also { index = it })
        }
    }

    private fun initViews() {
        transactionalAccounts = AbsaCacheManager.getTransactionalAccounts()
        sendTokensToNormalInputView.selectedValue = getString(R.string.notification_method_none)
        if (!setUpAccountList(transactionalAccounts)) return
        sendTokensToNormalInputView.setImageViewVisibility(View.VISIBLE)
        amountLargeInputView.setImeOptions(EditorInfo.IME_ACTION_DONE)
        beneficiaryDetailObject.let {
            var lastPaymentDetail = ""
            val transactions = beneficiaryDetailObject.transactions
            if (!transactions.isNullOrEmpty()) {
                val lastTransactionAmount = transactions.first().amount
                val lastTransactionDate = transactions.first().date
                lastTransactionAmount.let {
                    lastPaymentDetail = getString(R.string.last_transaction_beneficiary, it, getString(R.string.purchased), lastTransactionDate)
                }
            }
            val formattedMeterNumber = beneficiaryDetailObject.beneficiaryAcctNo.insertSpaceAtIncrements(BMBConstants.INSERT_SPACE_AFTER_FOUR_DIGIT)
            beneficiaryView.setBeneficiary(BeneficiaryListItem(beneficiaryDetailObject.beneficiaryName, formattedMeterNumber, lastPaymentDetail))
            selectedAccountObject.let {
                availableTextView.text = String.format("%s %s", it.availableBalance.toString(), getString(R.string.available_lower))
                accountRoundedSelectorView.selectedValue = it.accountInformation ?: ""
            }
            if (purchasePrepaidElectricityResultObject == null) {
                purchasePrepaidElectricityResultObject = PurchasePrepaidElectricityResultObject()
                purchasePrepaidElectricityResultObject?.benNoticeTyp = beneficiaryDetailObject.benNoticeType
            } else {
                sendTokensToNormalInputView.selectedValue = getNotificationValue(purchasePrepaidElectricityResultObject?.benNoticeTyp) ?: ""
            }
            setUpComponentListeners()
            BMBApplication.getInstance().deviceProfilingInteractor.notifyTransaction()
        }
    }

    private fun initAccountArrearsCheck() {
        meterNumberObject.arrearsAmount.let {
            val amount = Amount(meterNumberObject.arrearsAmount ?: "0")
            if (amount.amountInt > 0) {
                val primaryButtonClickListener = View.OnClickListener {
                    AnalyticsUtil.trackAction(PrepaidElectricityActivity.BUY_ELECTRICITY, "BuyElectricity_AccountInArrearsScreen_ContinueButtonClicked")
                    parentFragmentManager.popBackStack()
                    showToolBar()
                }
                val resultScreenProperties = PropertiesBuilder()
                        .setResultScreenAnimation(ResultAnimations.generalAlert)
                        .setTitle(getString(R.string.prepaid_electricity_account_arrears_title))
                        .setDescription(getString(R.string.prepaid_electricity_account_arrears_description, amount.amountDouble.toRandAmount()))
                        .setPrimaryButtonLabel(getString(R.string.continue_button))
                        .build(false)
                val genericResultScreenFragment = GenericResultScreenFragment.newInstance(resultScreenProperties, false, primaryButtonClickListener, null)
                baseActivity.startFragment(genericResultScreenFragment, false, BaseActivity.AnimationType.FADE)
                AnalyticsUtil.trackAction(PrepaidElectricityActivity.BUY_ELECTRICITY, "BuyElectricity_AccountInArrearsScreen_ScreenDisplayed")
                hideToolBar()
            }
        }
        hasAccountArrearsCheckExecuted = true
    }

    private fun setUpComponentListeners() {
        continueButton.setOnClickListener {
            AnalyticsUtil.trackAction(PrepaidElectricityActivity.BUY_ELECTRICITY, "BuyElectricity_PurchaseDetailsScreen_ContinueButtonClicked")

            if (!isValidInput()) {
                return@setOnClickListener
            }
            if (NotificationMethodData.TYPE.NONE.toString().equals(sendTokensToNormalInputView.selectedValue, ignoreCase = true)) {
                val alertInfo = AlertInfo().apply {
                    title = getString(R.string.please_note)
                    message = getString(R.string.prepaid_electricity_none_chosen)
                    positiveButtonText = getString(R.string.continue_button)
                    negativeButtonText = getString(R.string.cancel)
                    positiveDismissListener = DialogInterface.OnClickListener { _: DialogInterface?, _: Int ->
                        setUpModel()
                        prepaidElectricityView.navigateToConfirmPurchaseFragment(purchasePrepaidElectricityResultObject)
                    }
                }
                showCustomAlertDialog(alertInfo)
            } else {
                setUpModel()
                prepaidElectricityView.navigateToConfirmPurchaseFragment(purchasePrepaidElectricityResultObject)
            }
        }
        sendTokensToNormalInputView.setOnClickListener {
            val intent = Intent(activity, NotificationMethodSelectionActivity::class.java).apply {
                putExtra(NotificationMethodSelectionActivity.SHOW_SELF_NOTIFICATION_METHOD, true)
                putExtra(NotificationMethodSelectionActivity.SHOW_BENEFICIARY_NOTIFICATION_TITLE, false)
                putExtra(NotificationMethodSelectionActivity.TOOLBAR_TITLE, getString(R.string.prepaid_electricity_send_tokens_to))
                purchasePrepaidElectricityResultObject.let {
                    val benNoticeTyp = it?.benNoticeTyp
                    putExtra(NotificationMethodSelectionActivity.LAST_SELECTED_TYPE_FOR_BENEFICIARY, benNoticeTyp)
                    putExtra(NotificationMethodSelectionActivity.LAST_SELECTED_VALUE_FOR_BENEFICIARY, getNotificationValue(benNoticeTyp))
                }
            }
            startActivityForResult(intent, NotificationMethodSelectionActivity.NOTIFICATION_METHOD_SELF_REQUEST_CODE)
        }
        amountLargeInputView.addValueViewTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                amountLargeInputView.hideError()
                continueButton.isEnabled = true
            }
        })
        amountLargeInputView.setValueViewEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                return@setValueViewEditorActionListener !isValidInput()
            }
            true
        }
    }

    private fun setUpModel() {
        purchasePrepaidElectricityResultObject?.apply {
            fromAccountNumber = selectedAccountObject.accountNumber
            accountType = selectedAccountObject.accountType
            accountDescription = selectedAccountObject.description
            benId = beneficiaryDetailObject.beneficiaryId
            benName = beneficiaryDetailObject.beneficiaryName
            isHasImage = beneficiaryDetailObject.hasImage == true
            myNoticeMethod = beneficiaryDetailObject.myNoticeType
            myCellNumber = beneficiaryDetailObject.cellNo
            myEmail = beneficiaryDetailObject.email
            myFaxCode = beneficiaryDetailObject.faxCode
            myFaxNumber = beneficiaryDetailObject.faxNumber
            imageName = beneficiaryDetailObject.imageName
            imageName = beneficiaryDetailObject.imageName
            meterNumber = meterNumberObject.meterNumber
            serviceProvider = meterNumberObject.utility
            arrearsAmount = meterNumberObject.arrearsAmount?.let { Amount(it) }
            amount = amountLargeInputView.selectedValueUnmasked
        }
    }

    private fun getNotificationValue(benNoticeTyp: String?): String? = when {
        BMBConstants.NOTICE_TYPE_EMAIL_SHORT.equals(benNoticeTyp, ignoreCase = true) -> purchasePrepaidElectricityResultObject?.benEmail
        BMBConstants.NOTICE_TYPE_SMS_SHORT.equals(benNoticeTyp, ignoreCase = true) -> purchasePrepaidElectricityResultObject?.benCellNumber.toFormattedCellphoneNumber()
        BMBConstants.NOTICE_TYPE_FAX_SHORT.equals(benNoticeTyp, ignoreCase = true) -> (purchasePrepaidElectricityResultObject?.benFaxCode + purchasePrepaidElectricityResultObject?.benFaxNumber).toFormattedCellphoneNumber()
        else -> "None"
    }

    private fun setUpAccountList(transactionalAccounts: ArrayList<AccountObject>?): Boolean {
        return if (!transactionalAccounts.isNullOrEmpty()) {
            selectedAccountObject = transactionalAccounts.first()
            accountRoundedSelectorView.selectedIndex = 0
            val selectorList = SelectorList<StringItem?>()
            selectorList.addAll(transactionalAccounts.map { accountObject -> StringItem(accountObject.description, String.format(getString(R.string.ppe_available_balance), accountObject.accountNumberFormatted, accountObject.availableBalance)) })
            accountRoundedSelectorView.setList(selectorList, getString(R.string.select_an_acc_from))
            accountRoundedSelectorView.setItemSelectionInterface { index: Int -> selectAccount(index) }
            true
        } else {
            prepaidElectricityView.showGenericErrorMessageThenFinish()
            false
        }
    }

    private fun selectAccount(index: Int) {
        if (index < transactionalAccounts.size) {
            this.index = index
            selectedAccountObject = transactionalAccounts[index]
            accountRoundedSelectorView.apply {
                selectedIndex = index
                selectedValue = selectedAccountObject.accountInformation
            }
            availableTextView.text = getString(R.string.currency_rand_available, selectedAccountObject.availableBalance?.getAmount())
            isValidInput()
        }
    }

    private fun isValidInput(): Boolean {
        var isValid = true
        if (amountLargeInputView.selectedValueUnmasked.isNotEmpty()) {
            val currentValue = amountLargeInputView.selectedValueUnmasked.toDouble()
            val availableBalance = selectedAccountObject.availableBalance
            if (availableBalance != null && currentValue > availableBalance.amountDouble) {
                isValid = false
                amountLargeInputView.setError(getString(R.string.amount_exceeds_available))
            } else if (currentValue < MINIMUM_AMOUNT || currentValue > MAX_AMOUNT) {
                isValid = false
                amountLargeInputView.setError(getString(R.string.own_amount_error_message, MINIMUM_AMOUNT, MAX_AMOUNT))
            }
        } else {
            isValid = false
            amountLargeInputView.setError(getString(R.string.enter_amount_electricity))
        }
        if (isValid) {
            amountLargeInputView.hideError()
        }
        continueButton.isEnabled = isValid
        return isValid
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == NotificationMethodSelectionActivity.NOTIFICATION_METHOD_SELF_REQUEST_CODE) {
            val notificationMethodData: NotificationMethodData? = data?.getParcelableExtra(NotificationMethodSelectionActivity.SHOW_SELF_NOTIFICATION_METHOD)
            notificationMethodData?.let {
                val notificationType = it.notificationMethodType.name.substring(0, 1)
                purchasePrepaidElectricityResultObject?.benNoticeTyp = notificationType
                if (it.notificationMethodDetail.isNotEmpty()) {
                    when {
                        BMBConstants.NOTICE_TYPE_EMAIL_SHORT.equals(notificationType, ignoreCase = true) -> {
                            sendTokensToNormalInputView.selectedValue = it.notificationMethodDetail
                            purchasePrepaidElectricityResultObject?.benEmail = sendTokensToNormalInputView.selectedValue
                        }
                        BMBConstants.NOTICE_TYPE_SMS_SHORT.equals(notificationType, ignoreCase = true) -> {
                            sendTokensToNormalInputView.selectedValue = it.notificationMethodDetail.toFormattedCellphoneNumber()
                            purchasePrepaidElectricityResultObject?.benCellNumber = sendTokensToNormalInputView.selectedValue.removeSpaces()
                        }
                        BMBConstants.NOTICE_TYPE_FAX_SHORT.equals(notificationType, ignoreCase = true) -> {
                            sendTokensToNormalInputView.selectedValue = it.notificationMethodDetail.toFormattedCellphoneNumber()
                            val faxNumber = sendTokensToNormalInputView.selectedValue.removeSpaces()
                            purchasePrepaidElectricityResultObject?.benFaxCode = faxNumber.substring(0, 3)
                            purchasePrepaidElectricityResultObject?.benFaxNumber = faxNumber.substring(3)
                        }
                    }
                }
            }
        }
    }
}