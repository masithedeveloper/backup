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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.cashSendPlus.services.CashSendPlusSendMultiplePaymentDetails
import com.barclays.absa.utils.TextFormatUtils
import kotlinx.android.synthetic.main.cash_send_plus_pay_multiple_confirm_payment_details_list_item.view.*

class CashSendPlusConfirmMultiplePaymentAdapter(private val selectedBeneficiaryList: MutableList<CashSendPlusSendMultiplePaymentDetails>) : RecyclerView.Adapter<CashSendPlusConfirmMultiplePaymentAdapter.ConfirmMultiplePaymentItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ConfirmMultiplePaymentItemHolder {
        return ConfirmMultiplePaymentItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.cash_send_plus_pay_multiple_confirm_payment_details_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ConfirmMultiplePaymentItemHolder, position: Int) {
        with(holder.mainView) {
            selectedBeneficiaryList[position].apply {
                val beneficiaryName = "${beneficiaryInfo.beneficiaryName} ${beneficiaryInfo.beneficiarySurname}"
                val accountInfo = "${accountDetail.accountType} ${accountDetail.accountNumber}"
                beneficiaryNamePrimaryContentAndLabelView.setContentText(beneficiaryName)
                amountPrimaryContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRand(amount))
                accountFromPrimaryContentAndLabelView.setContentText(accountInfo)
                referencePrimaryContentAndLabelView.setContentText(reference)
                accessPinPrimaryContentAndLabelView.setContentText(accessPin)
            }
        }
    }

    override fun getItemCount(): Int  = selectedBeneficiaryList.size

    class ConfirmMultiplePaymentItemHolder(val mainView: View) : RecyclerView.ViewHolder(mainView)
}