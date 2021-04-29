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

package com.barclays.absa.banking.saveAndInvest

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.express.shared.getLookupInfo.dto.LookupItem
import com.barclays.absa.utils.AnimationHelper
import styleguide.utils.extensions.toTitleCaseRemovingCommas

class SaveAndInvestSourceOfFundsAdapter(private val sourceOfFundsList: List<LookupItem>, private var selectedList: MutableList<LookupItem>) : RecyclerView.Adapter<SaveAndInvestSourceOfFundsAdapter.SourceOfFundsViewHolder>() {

    companion object {
        private const val MAXIMUM_SELECTION = 5
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceOfFundsViewHolder {
        return SourceOfFundsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.source_of_funds_item, parent, false) as CheckBox)
    }

    override fun getItemCount(): Int = sourceOfFundsList.size

    override fun onBindViewHolder(holder: SourceOfFundsViewHolder, position: Int) {
        val sourceOfFund = sourceOfFundsList[position]

        with(holder.checkBoxView) {
            isChecked = false
            selectedList.forEach {
                if (it.code == sourceOfFund.code) {
                    isChecked = true
                    return@forEach
                }
            }

            text = sourceOfFund.value.toTitleCaseRemovingCommas()
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (selectedList.size < MAXIMUM_SELECTION) {
                        selectedList.add(sourceOfFund)
                    } else {
                        this.isChecked = false
                        AnimationHelper.shakeShakeAnimate(this)
                    }
                } else {
                    selectedList.remove(sourceOfFund)
                }
            }
        }
    }

    override fun onViewRecycled(holder: SourceOfFundsViewHolder) {
        super.onViewRecycled(holder)
        holder.checkBoxView.setOnCheckedChangeListener(null)
    }

    inner class SourceOfFundsViewHolder(val checkBoxView: CheckBox) : RecyclerView.ViewHolder(checkBoxView)
}