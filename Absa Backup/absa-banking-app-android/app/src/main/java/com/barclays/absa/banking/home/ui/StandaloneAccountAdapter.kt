/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.home.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.Entry
import com.barclays.absa.banking.boundary.model.policy.Policy
import com.barclays.absa.banking.express.getAllBalances.dto.AccountTypesBMG
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.TextFormatUtils
import kotlinx.android.synthetic.main.account_small_card_fragment.view.*
import styleguide.cards.Account
import styleguide.cards.AccountView

class StandaloneAccountAdapter(private val entries: List<Entry>) : RecyclerView.Adapter<StandaloneAccountAdapter.EntryViewHolder>() {

    companion object {
        private const val TYPE_ACCOUNT = 0
        private const val TYPE_OFFER = 1
    }

    private lateinit var context: Context

    lateinit var standaloneAccountView: StandaloneAccountView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        context = parent.context
        val itemLayoutViewId = if (TYPE_OFFER == viewType) {
            R.layout.offer_view
        } else {
            R.layout.account_small_card_fragment
        }

        val itemView = LayoutInflater.from(parent.context).inflate(itemLayoutViewId, parent, false)
        return EntryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        holder.bind(entries[position])
    }

    override fun getItemViewType(position: Int) = if (entries[position].entryType == Entry.OFFERS) TYPE_OFFER else TYPE_ACCOUNT

    override fun getItemCount() = entries.size

    inner class EntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(entry: Entry) {

            val accountName = getAccountName(entry)
            val balanceLabel = getBalanceLabel(entry)
            val cardNumber = getCardNumber(entry)
            val amount = getAmount(entry)
            val accountView = itemView.accountView as AccountView

            accountView.apply {
                enableNewColorScheme()
                setBackground(R.drawable.layer_light_orange_pale_grey)
                adjustPadding()
                setAccount(Account(amount, cardNumber, accountName, balanceLabel))
            }

            if (entry is AccountObject) {
                if (!CommonUtils.isStandaloneAccountSupported(entry)) {
                    accountView.setBackground(R.drawable.layer_dark_orange_dark_grey)
                }

                if (AccountTypesBMG.absaVehicleAndAssetFinance.name == entry.accountType) {
                    accountView.setBackground(R.drawable.layer_light_orange_pale_grey)
                }
                accountView.setOnClickListener {
                    val animation = AnimationUtils.loadAnimation(accountView.context, R.anim.scale_in_out_small)
                    accountView.startAnimation(animation)
                    when {
                        AccountTypesBMG.homeLoan.name.equals(entry.accountType, ignoreCase = true) -> standaloneAccountView.onHomeLoanCardClicked(entry)
                        AccountTypesBMG.personalLoan.name.equals(entry.accountType, ignoreCase = true) -> standaloneAccountView.onPersonalLoanCardClicked(entry)
                        else -> standaloneAccountView.onAccountCardClicked(entry)
                    }
                }
            } else if (entry is Policy) {
                standaloneAccountView.onInsuranceCardClicked(entry)
            }
        }

        private fun getAccountName(entry: Entry): String? {
            return if (entry.entryType == Entry.ACCOUNT) {
                val account = entry as AccountObject
                if (account.description.isNullOrBlank()) {
                    account.accountType.trim { it <= ' ' }
                } else {
                    account.description.trim { it <= ' ' }
                }
            } else {
                (entry as Policy).description
            }
        }

        private fun getBalanceLabel(entry: Entry): String {
            return if (entry.entryType == Entry.ACCOUNT) {
                val account = entry as AccountObject
                if (AccountTypesBMG.homeLoan.name.equals(account.accountType, true)) {
                    context.getString(R.string.balance_owed)
                } else {
                    context.getString(R.string.current_balance)
                }
            } else {
                context.getString(R.string.premium_amount)
            }
        }

        private fun getCardNumber(entry: Entry): String? {
            return if (entry.entryType == Entry.ACCOUNT) {
                val account = entry as AccountObject
                if ("onlineShareTrading".equals(account.accountType, ignoreCase = true)) {
                    ""
                } else {
                    account.accountNumber.trim { it <= ' ' }
                }
            } else {
                (entry as Policy).number
            }
        }

        private fun getAmount(entry: Entry): String {
            return if (entry.entryType == Entry.ACCOUNT) {
                val account = entry as AccountObject
                account.currentBalance.toString()
            } else {
                val policy = entry as Policy
                TextFormatUtils.formatBasicAmount(policy.monthlyPremium)
            }
        }
    }

    interface StandaloneAccountView {
        fun onInsuranceCardClicked(policy: Policy)
        fun onHomeLoanCardClicked(accountObject: AccountObject)
        fun onAccountCardClicked(accountObject: AccountObject)
        fun onPersonalLoanCardClicked(accountObject: AccountObject)
    }
}