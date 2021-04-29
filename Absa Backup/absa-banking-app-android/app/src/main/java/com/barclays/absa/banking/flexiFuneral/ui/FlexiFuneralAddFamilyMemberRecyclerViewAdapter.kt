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

package com.barclays.absa.banking.flexiFuneral.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.flexiFuneral.services.dto.CoverDetails
import com.barclays.absa.banking.flexiFuneral.services.dto.MultipleDependentsDetails
import kotlinx.android.synthetic.main.flexi_funeral_add_family_member_item.view.*
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.utils.extensions.toRandAmount

class FlexiFuneralAddFamilyMemberRecyclerViewAdapter(private val familyMemberList: MutableList<MultipleDependentsDetails>, var itemClickedInterface: ItemClickedInterface) : RecyclerView.Adapter<FlexiFuneralAddFamilyMemberRecyclerViewAdapter.ViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.flexi_funeral_add_family_member_item, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val addFamilyMemberNormalInputView = itemView.addFamilyMemberNormalInputView
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentMember = familyMemberList[position]
        holder.addFamilyMemberNormalInputView.apply {
            setTitleText(context.getString(R.string.flexi_funeral_family_member_initial_and_surname, currentMember.dependentsInitials, currentMember.dependentsSurname))
            if (currentMember.dependentsCoverAmount.isNotEmpty() && currentMember.dependentsPremium.isNotEmpty()) {
                selectedIndex = currentMember.selectedCoverIndex
                selectedValue = context.getString(R.string.flexi_funeral_main_member_cover_option, currentMember.dependentsCoverAmount.toRandAmount(), currentMember.dependentsPremium.toRandAmount())
            } else {
                selectedIndex = -1
                selectedValue = ""
            }
            setList(buildSelectorList(currentMember.familyMemberQuotes), context.getString(R.string.flexi_funeral_choose_your_cover))
            setItemSelectionInterface {
                currentMember.apply {
                    dependentsCoverAmount = currentMember.familyMemberQuotes[it].coverAmount
                    dependentsPremium = currentMember.familyMemberQuotes[it].monthlyPremium
                    selectedCoverIndex = it
                }
                itemClickedInterface.itemClicked()
            }
        }
    }

    interface ItemClickedInterface {
        fun itemClicked()
    }

    override fun getItemCount() = familyMemberList.size

    private fun buildSelectorList(coverOptions: List<CoverDetails>): SelectorList<StringItem> {
        val selectorList = SelectorList<StringItem>()
        coverOptions.forEach {
            selectorList.add(StringItem(context.getString(R.string.flexi_funeral_main_member_cover_option, it.coverAmount.toRandAmount(), it.monthlyPremium.toRandAmount())))
        }
        return selectorList
    }
}