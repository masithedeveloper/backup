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

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.CustomerProfileObject.Companion.instance
import com.barclays.absa.banking.cashSendPlus.services.CashSendPlusSendMultiplePaymentDetails
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import kotlinx.android.synthetic.main.cash_send_plus_send_multiple_share_pin_list_item.view.*
import styleguide.utils.extensions.toRandAmount


class CashSendPlusSendMultipleSharePinAdapter(private val selectedBeneficiaryPaymentDetails: MutableList<CashSendPlusSendMultiplePaymentDetails>) : RecyclerView.Adapter<CashSendPlusSendMultipleSharePinAdapter.ShareAccessPinItemHolder>() {

    private val appCacheService: IAppCacheService = getServiceInterface()

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ShareAccessPinItemHolder {
        return ShareAccessPinItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.cash_send_plus_send_multiple_share_pin_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ShareAccessPinItemHolder, position: Int) {
        val item = selectedBeneficiaryPaymentDetails[position]

        with(holder.mainView) {
            val beneficiaryFullName = "${item.beneficiaryInfo.beneficiaryName} ${item.beneficiaryInfo.beneficiarySurname}"
            beneficiaryNameTextView.text = beneficiaryFullName
            amountTextView.text = item.amount.toRandAmount()
            accessPinPrimaryContentAndLabelView.setLabelText(item.accessPin)

            if (item.isAccessPinShared) {
                shareButton.setImageResource(R.drawable.ic_tick_black_24)
            }

            shareButton.setOnClickListener {
                shareAccessPin(it.context, item, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return selectedBeneficiaryPaymentDetails.size
    }

    private fun shareAccessPin(context: Context, paymentDetails: CashSendPlusSendMultiplePaymentDetails, position: Int) {
        val sharePinMessage = String.format(context.getString(R.string.share_atm_pin_message),
                "${paymentDetails.beneficiaryInfo.beneficiaryName} ${paymentDetails.beneficiaryInfo.beneficiarySurname}",
                paymentDetails.amount,
                paymentDetails.accessPin,
                instance.customerName)

        with(Intent()) {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, sharePinMessage)
            type = "text/plain"
            startActivity(context, Intent.createChooser(this, ""), null)
        }
        selectedBeneficiaryPaymentDetails[position].isAccessPinShared = true
        appCacheService.setCashSendPlusSendMultipleBeneficiariesPaymentDetails(selectedBeneficiaryPaymentDetails)
        notifyDataSetChanged()
    }

    class ShareAccessPinItemHolder(val mainView: View) : RecyclerView.ViewHolder(mainView)
}