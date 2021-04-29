/*
 * Copyright (c) 2018. Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclose
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied or distributed other than on a
 * need-to-know basis and any recipients may be required to sign a confidentiality undertaking in favor of Absa Bank Limited
 */

package com.barclays.absa.banking.settings.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.account.services.dto.ManageAccounts
import com.barclays.absa.banking.databinding.ManageAccountsRecyclerviewLayoutBinding
import com.barclays.absa.banking.presentation.shared.widget.AccountReorderCallbackHelper
import styleguide.utils.extensions.toFormattedAccountNumber
import java.util.*

class ManageAccountsAdapter(private val context: Context, private var accountsList: List<ManageAccounts>, private val dragStartListener: OnStartDragListener) : RecyclerView.Adapter<ManageAccountsAdapter.ManageAccountsViewHolder>(), AccountReorderCallbackHelper.ItemTouchHelperAdapter {

    private var onItemCheckedChangeListener: OnItemCheckedChangeListener? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ManageAccountsViewHolder {
        val binding = ManageAccountsRecyclerviewLayoutBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ManageAccountsViewHolder(binding)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(manageAccountsViewHolder: ManageAccountsViewHolder, position: Int) {
        manageAccountsViewHolder.binding.accountStatusView.accountName = accountsList[position].accountName
        manageAccountsViewHolder.binding.accountStatusView.accountNumber = accountsList[position].accountNumber.toFormattedAccountNumber()

        if (accountsList[position].accountLinked != null) {
            manageAccountsViewHolder.binding.accountStatusView.toggleBarStyle(accountsList[position].accountLinked!!, context)
            if (accountsList[position].accessAccount != null && accountsList[position].accessAccount!! && accountsList[position].accountLinked!!) {
                manageAccountsViewHolder.binding.accountStatusView.accountToggle.visibility = View.GONE
            } else if (accountsList[position].linkingOrUnlinkingPossible != null && !accountsList[position].linkingOrUnlinkingPossible!!) {
                manageAccountsViewHolder.binding.accountStatusView.accountToggle.visibility = View.GONE
            } else {
                manageAccountsViewHolder.binding.accountStatusView.accountToggle.visibility = View.VISIBLE
            }

            manageAccountsViewHolder.binding.accountStatusView.accountToggle.setOnCheckedChangeListener { _, isChecked ->
                accountsList[position].accountLinked = !accountsList[position].accountLinked!!
                manageAccountsViewHolder.binding.accountStatusView.setAccountToggleStyle(manageAccountsViewHolder.binding.accountStatusView.accountToggle, accountsList[position].accountLinked!!)
                onItemCheckedChangeListener?.onItemCheckedChanged(position, isChecked)
            }
        } else {
            manageAccountsViewHolder.binding.accountStatusView.accountName = accountsList[position].accountName
            manageAccountsViewHolder.binding.accountStatusView.accountNumber = context.getString(R.string.manage_accounts_retrieval_error)
            manageAccountsViewHolder.binding.accountStatusView.accountToggle.isChecked = false
        }

        manageAccountsViewHolder.binding.accountStatusView.getreorderImageView().setOnTouchListener { _, motionEvent ->
            if (motionEvent.actionMasked == MotionEvent.ACTION_DOWN) {
                dragStartListener.onStartDrag(manageAccountsViewHolder)
            }
            false
        }
    }

    override fun onViewRecycled(holder: ManageAccountsViewHolder) {
        super.onViewRecycled(holder)
        holder.binding.accountStatusView.accountToggle.setOnCheckedChangeListener(null)
    }

    interface OnItemCheckedChangeListener {
        fun onItemCheckedChanged(position: Int, isChecked: Boolean)
    }

    fun getAccounts(): List<ManageAccounts> {
        return accountsList
    }

    override fun getItemCount(): Int {
        return accountsList.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(accountsList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(accountsList, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    class ManageAccountsViewHolder(val binding: ManageAccountsRecyclerviewLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.executePendingBindings()
        }
    }

    override fun onItemMoved() {
        notifyDataSetChanged()
    }
}
