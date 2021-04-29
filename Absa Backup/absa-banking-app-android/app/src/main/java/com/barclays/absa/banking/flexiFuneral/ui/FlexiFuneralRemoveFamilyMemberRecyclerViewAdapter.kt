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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.flexiFuneral.services.dto.MultipleDependentsDetails
import com.barclays.absa.banking.policy_beneficiaries.ui.InsuranceBeneficiaryHelper
import kotlinx.android.synthetic.main.flexi_funeral_remove_family_member_item.view.*
import styleguide.utils.extensions.toRandAmount

class FlexiFuneralRemoveFamilyMemberRecyclerViewAdapter(private val familyMemberList: MutableList<MultipleDependentsDetails>, var itemClickedInterface: ItemClickedInterface) : RecyclerView.Adapter<FlexiFuneralRemoveFamilyMemberRecyclerViewAdapter.ViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.flexi_funeral_remove_family_member_item, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val constraintLayout: ConstraintLayout = itemView.flexiFuneralRemoveFamilyMemberConstraintLayout
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentMember = familyMemberList[position]
        holder.constraintLayout.removeOptionActionButtonView.apply {
            val dependentsRelationship = InsuranceBeneficiaryHelper.buildFamilyMembersRelationshipMappings(context).filterValues { it == currentMember.dependentsRelationship }.keys.first()
            setCaptionText(context.getString(R.string.flexi_funeral_remove_member_caption, currentMember.dependentsInitials, currentMember.dependentsSurname, dependentsRelationship))
            if (currentMember.dependentsCoverAmount.isNotEmpty() && currentMember.dependentsPremium.isNotEmpty()) {
                setSubCaption(context.getString(R.string.flexi_funeral_main_member_cover_option, currentMember.dependentsCoverAmount.toRandAmount(), currentMember.dependentsPremium.toRandAmount()))
            } else {
                setSubCaption("")
            }

            setRightImageOnClickListener {
                familyMemberList.let {
                    itemClickedInterface.itemClicked(position, it)
                }
            }
        }
    }

    interface ItemClickedInterface {
        fun itemClicked(position: Int, familyMemberList: MutableList<MultipleDependentsDetails>)
    }

    override fun getItemCount() = familyMemberList.size
}