/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank Limited
 *
 */
package com.barclays.absa.banking.payments.swift.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.payments.swift.ui.SwiftShareAccountsAdapter.*
import kotlinx.android.synthetic.main.swift_share_account_item.view.*
import styleguide.buttons.OptionActionButtonView
import java.util.*

class SwiftShareAccountsAdapter(private val swiftShareAccountDetailsInterface: SwiftShareAccountDetailsInterface,
                                private val accountsList: List<AccountObject>) : RecyclerView.Adapter<SwiftAccountViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SwiftAccountViewHolder = SwiftAccountViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.swift_share_account_item, parent, false).rootView)

    override fun getItemCount(): Int = accountsList.size

    override fun onBindViewHolder(holder: SwiftAccountViewHolder, position: Int) {
        holder.onBind(accountsList[position], swiftShareAccountDetailsInterface)
    }

    inner class SwiftAccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val swiftAccountView = itemView.swiftShareAccountDetailsOptionActionButtonView as OptionActionButtonView

        fun onBind(accountObject: AccountObject, swiftShareAccountDetailsInterface: SwiftShareAccountDetailsInterface) {
            swiftAccountView.apply {
                captionTextView.text = accountObject.description
                val subCaptionText = if (accountObject.accountType.equals("cia", true)) {
                    try {
                        context.getString(R.string.swift_sub_caption_cia_template, Currency.getInstance(accountObject.currency).getDisplayName(Locale.ENGLISH), accountObject.currency)
                    } catch (e:Exception) {
                        e.printStackTrace()
                        ""
                    }
                } else {
                    context.getString(R.string.swift_sub_caption_zar_template, Currency.getInstance("ZAR").getDisplayName(Locale.ENGLISH))
                }
                subCaptionTextView.text = resources.getString(R.string.swift_sub_caption_template, subCaptionText, accountObject.accountNumber)
                subCaptionTextView.visibility = View.VISIBLE

                setOnClickListener { swiftShareAccountDetailsInterface.onAccountSelected(accountObject) }
            }
        }
    }
}