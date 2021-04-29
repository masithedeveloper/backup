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

package com.barclays.absa.banking.fixedDeposit

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.shared.services.dto.LookupItem
import styleguide.forms.CheckBoxView
import styleguide.utils.AnimationHelper
import styleguide.utils.extensions.toTitleCaseRemovingCommas
import za.co.absa.presentation.uilib.databinding.SourceOfFundCheckboxItemBinding

class FixedDepositSourceOfFundsAdapter(private var sourceOfFunds: List<LookupItem>?, private var selectedList: ArrayList<LookupItem>?) : RecyclerView.Adapter<FixedDepositSourceOfFundsAdapter.SourceOfFundsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceOfFundsViewHolder {
        val view = DataBindingUtil.inflate<SourceOfFundCheckboxItemBinding>(LayoutInflater.from(parent.context), R.layout.source_of_fund_checkbox_item, parent, false)
        return SourceOfFundsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (sourceOfFunds == null) 0 else sourceOfFunds!!.size
    }

    override fun onViewRecycled(holder: SourceOfFundsViewHolder) {
        super.onViewRecycled(holder)
        holder.checkBoxView.setOnCheckedListener(null)
    }

    override fun onBindViewHolder(holder: SourceOfFundsViewHolder, position: Int) {
        val sourceOfFund = sourceOfFunds!![position]

        holder.checkBoxView.clearError()
        holder.checkBoxView.isChecked = false

        holder.checkBoxView.isEnabled = selectedList!!.size != MAXIMUM_FUNDS

        for (item in selectedList!!) {
            if (item.itemCode == sourceOfFund.itemCode) {
                holder.checkBoxView.isChecked = true
                holder.checkBoxView.isEnabled = true
                break
            }
        }

        holder.checkBoxView.setDescription(sourceOfFund.defaultLabel.toTitleCaseRemovingCommas())

        holder.checkBoxView.setOnCheckedListener { isChecked ->
            if (isChecked) {
                if (selectedList?.size!! < MAXIMUM_FUNDS) {
                    selectedList?.add(sourceOfFund)
                    updateAllFields()
                } else {
                    holder.checkBoxView.isChecked = false
                    AnimationHelper.shakeShakeAnimate(holder.checkBoxView)
                }
            } else {
                if (selectedList?.contains(sourceOfFund)!!) {
                    selectedList?.remove(sourceOfFund)
                }
                updateAllFields()
            }
        }
    }

    private fun updateAllFields() {
        Handler(Looper.getMainLooper()).postDelayed({ notifyDataSetChanged() }, 20)
    }

    class SourceOfFundsViewHolder(itemBinding: SourceOfFundCheckboxItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        var checkBoxView: CheckBoxView = itemBinding.checkBoxView
    }

    companion object {
        private const val MAXIMUM_FUNDS = 5
    }
}
