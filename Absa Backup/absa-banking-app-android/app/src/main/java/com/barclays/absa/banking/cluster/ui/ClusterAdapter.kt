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
 *
 */

package com.barclays.absa.banking.cluster.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.Entry
import com.barclays.absa.banking.boundary.model.Header
import com.barclays.absa.banking.boundary.model.policy.Policy
import com.barclays.absa.banking.home.ui.AccountCardHelper
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.TextFormatUtils
import styleguide.cards.Account
import styleguide.cards.AccountView

class ClusterAdapter(private val clusterView: ClusterView, private val list: List<Entry>) : RecyclerView.Adapter<ClusterAdapter.ViewHolder>() {
    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.home_account_item, parent, false).rootView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val accountName: String
        val balanceLabel: String
        val cardNumber: String
        val amount: String

        when (val entry = list[position]) {
            is Header -> {
                holder.headingTextView.visibility = View.VISIBLE
                holder.headingTextView.text = entry.label
                holder.accountView.visibility = View.GONE
            }
            is Policy -> {
                accountName = entry.description ?: ""
                balanceLabel = context.getString(R.string.premium)
                cardNumber = entry.number ?: ""
                amount = TextFormatUtils.formatBasicAmount(entry.monthlyPremium)

                holder.accountView.setBackground(R.drawable.rounded_account_card_fixed_deposit)
                holder.accountView.visibility = View.VISIBLE
                holder.headingTextView.visibility = View.GONE
                holder.accountView.setAccount(Account(amount, cardNumber, accountName, balanceLabel))

                holder.accountView.setOnClickListener {
                    clusterView.onCardClicked(entry)
                }
            }
            is AccountObject -> {
                val isSupported = CommonUtils.isAccountSupportedRebuild(entry)

                accountName = AccountCardHelper.getAccountDescription(entry)
                cardNumber = AccountCardHelper.getCardNumber(entry)
                balanceLabel = AccountCardHelper.getBalanceLabel(entry, isSupported, context)
                amount = entry.currentBalance.toString()

                holder.accountView.setBackground(R.drawable.rounded_account_card_home_loans)
                holder.accountView.visibility = View.VISIBLE
                holder.headingTextView.visibility = View.GONE
                holder.accountView.setAccount(Account(amount, cardNumber, accountName, balanceLabel))

                holder.accountView.setOnClickListener {
                    clusterView.onCardClicked(entry)
                }
            }
        }
    }

    override fun getItemCount() = list.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var accountView: AccountView = itemView.findViewById(R.id.accountView)
        var headingTextView: TextView = itemView.findViewById(R.id.headingTextView)
    }
}