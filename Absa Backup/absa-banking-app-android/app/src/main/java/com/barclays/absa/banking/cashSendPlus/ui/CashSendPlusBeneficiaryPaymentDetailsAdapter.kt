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

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.BeneficiaryObject
import com.barclays.absa.banking.cashSendPlus.services.CashSendPlusSendMultiplePaymentDetails
import com.barclays.absa.banking.cashSendPlus.ui.CashSendPlusSendMultipleActivity.Companion.ACCESS_PIN_LENGTH
import com.barclays.absa.banking.cashSendPlus.ui.CashSendPlusSendMultipleActivity.Companion.MAX_AMOUNT_TO_SEND
import com.barclays.absa.banking.cashSendPlus.ui.CashSendPlusSendMultipleActivity.Companion.MIN_AMOUNT_TO_SEND
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.utils.TextFormatUtils
import kotlinx.android.synthetic.main.cash_send_plus_pay_multiple_beneficiary_payment_details_list_item.view.*
import styleguide.content.BeneficiaryListItem
import styleguide.utils.extensions.removeCurrencyDefaultZero
import kotlin.math.roundToInt

class CashSendPlusBeneficiaryPaymentDetailsAdapter(private val selectedBeneficiaryList: MutableList<CashSendPlusSendMultiplePaymentDetails>, private val beneficiaryPaymentListener: BeneficiaryPaymentListener) : RecyclerView.Adapter<CashSendPlusBeneficiaryPaymentDetailsAdapter.SelectedBeneficiaryPaymentDetailsItemHolder>() {

    private val cachedCashSendPlusData = getServiceInterface<IAppCacheService>().getCashSendPlusRegistrationStatus()
    private var useSameAccessPin = false
    private var invalidItemPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): SelectedBeneficiaryPaymentDetailsItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cash_send_plus_pay_multiple_beneficiary_payment_details_list_item, parent, false)
        return SelectedBeneficiaryPaymentDetailsItemHolder(view)
    }

    override fun onBindViewHolder(holder: SelectedBeneficiaryPaymentDetailsItemHolder, position: Int) {
        val cashSendPlusMultiplePaymentDetailsItem = selectedBeneficiaryList[position]
        with(holder.mainView) {
            with(useAccessPinForAllBeneficiariesCheckBoxView) {
                if (position == 0 && selectedBeneficiaryList.size > 1) {
                    visibility = View.VISIBLE
                    setOnCheckedListener {
                        if (cashSendPlusMultiplePaymentDetailsItem.accessPin.length == ACCESS_PIN_LENGTH) {
                            useSameAccessPin = isChecked
                            beneficiaryPaymentListener.onUseSameAccessPinForAllChecked(isChecked)
                        } else {
                            isChecked = false
                        }
                    }
                } else {
                    visibility = View.GONE
                }
            }

            if (invalidItemPosition == position) {
                with(cashSendPlusMultiplePaymentDetailsItem) {
                    if (amount == "0") {
                        amountNormalInputView.setError(context.getString(R.string.cash_send_plus_send_between_20_and_3000))
                    }

                    if (accessPin.length != ACCESS_PIN_LENGTH) {
                        accessPinNormalInputView.setError(context.getString(R.string.access_pin_errormessage))
                    }
                }
                invalidItemPosition = -1
            }

            with(cashSendPlusMultiplePaymentDetailsItem.beneficiaryInfo) {
                val beneficiaryName = "$beneficiaryName $beneficiarySurname"
                val lastTransactionAmount = context.getString(R.string.cash_send_plus_last_transaction_amount, lastTransactionAmount)
                selectedBeneficiaryView.setBeneficiary(BeneficiaryListItem(beneficiaryName, lastTransactionAmount, lastTransactionDate))
            }

            cachedCashSendPlusData?.cashSendPlusResponseData?.let {
                accountAvailableBalanceTextView.text = context.getString(R.string.cash_send_plus_available_balance_and_account, TextFormatUtils.formatBasicAmountAsRand(it.cashSendPlusLimitAmountAvailable), cashSendPlusMultiplePaymentDetailsItem.accountDetail.accountNumberFormatted)
            }

            removeBeneficiaryImageButton.setOnClickListener {
                beneficiaryPaymentListener.onRemoveItem(cashSendPlusMultiplePaymentDetailsItem.beneficiaryInfo)
            }

            paymentDetailsOptionActionButtonView.setOnClickListener {
                beneficiaryPaymentListener.onEditPaymentDetails(position)
            }

            accessPinNormalInputView.text = cashSendPlusMultiplePaymentDetailsItem.accessPin
            amountNormalInputView.text = if (cashSendPlusMultiplePaymentDetailsItem.amount.isNotEmpty() && cashSendPlusMultiplePaymentDetailsItem.amount != "0") cashSendPlusMultiplePaymentDetailsItem.amount else ""
            amountNormalInputView.addValueViewTextWatcher(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    s.toString().removeCurrencyDefaultZero().let {
                        val amount = it.roundToInt()
                        val isMultipleOfTen = amount % 10 == 0
                        when {
                            isMultipleOfTen && amount in MIN_AMOUNT_TO_SEND..MAX_AMOUNT_TO_SEND -> {
                                beneficiaryPaymentListener.onAmountChanged(position, amount.toString())
                                amountNormalInputView.clearError()
                            }
                            !isMultipleOfTen -> {
                                amountNormalInputView.setError(context.getString(R.string.cash_send_plus_amount_can_only_be_in_r10_increment))
                            }
                            isMultipleOfTen && amount < MIN_AMOUNT_TO_SEND -> {
                                amountNormalInputView.setError(context.getString(R.string.cash_send_plus_amount_must_be_equal_or_more_than_r20))
                            }
                            isMultipleOfTen && amount > MAX_AMOUNT_TO_SEND -> {
                                amountNormalInputView.setError(context.getString(R.string.cash_send_plus_amount_cannot_be_more_than_r3000))
                            }
                            isMultipleOfTen && amount < cachedCashSendPlusData?.cashSendPlusResponseData?.cashSendPlusLimitAmountAvailable?.toInt() ?: 0 -> {
                                amountNormalInputView.setError(context.getString(R.string.cash_send_plus_amount_exceeds_available_amount))
                            }
                        }
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

            accessPinNormalInputView.addValueViewTextWatcher(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    s?.let {
                        when {
                            it.length == ACCESS_PIN_LENGTH && position == 0 || position > 0 && !useSameAccessPin -> {
                                beneficiaryPaymentListener.onAccessPinChanged(position, it.toString())
                                accessPinNormalInputView.clearError()
                            }
                            it.isNotEmpty() && it.length < ACCESS_PIN_LENGTH -> {
                                accessPinNormalInputView.setError(context.getString(R.string.access_pin_errormessage))
                            }
                            else -> {
                                accessPinNormalInputView.clearError()
                            }
                        }
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return selectedBeneficiaryList.size
    }

    override fun getItemId(position: Int): Long {
        return selectedBeneficiaryList[position].id
    }

    fun showInvalidItem() {
        val firstInvalidItemPosition = selectedBeneficiaryList.indexOfFirst { it.amount == "0" || it.accessPin.isEmpty() }
        invalidItemPosition = firstInvalidItemPosition
        notifyItemChanged(firstInvalidItemPosition)
    }

    class SelectedBeneficiaryPaymentDetailsItemHolder(val mainView: View) : RecyclerView.ViewHolder(mainView)

    interface BeneficiaryPaymentListener {
        fun onRemoveItem(item: BeneficiaryObject)
        fun onEditPaymentDetails(position: Int)
        fun onAmountChanged(position: Int, amount: String)
        fun onAccessPinChanged(position: Int, newAccessPin: String)
        fun onUseSameAccessPinForAllChecked(isChecked: Boolean)
    }
}