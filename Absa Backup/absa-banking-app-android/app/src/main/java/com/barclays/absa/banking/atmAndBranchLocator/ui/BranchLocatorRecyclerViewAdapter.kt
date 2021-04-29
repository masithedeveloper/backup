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

package com.barclays.absa.banking.atmAndBranchLocator.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.atmAndBranchLocator.services.dto.AtmBranchDetails
import com.barclays.absa.banking.atmAndBranchLocator.ui.AtmBranchLocatorActivity.Companion.BRANCH
import com.barclays.absa.banking.framework.BaseActivity
import styleguide.buttons.OptionActionButtonView
import styleguide.utils.extensions.toTitleCase

class BranchLocatorRecyclerViewAdapter(var atmBranchResults: List<AtmBranchDetails>, var itemClickedInterface: ItemClickedInterface) : RecyclerView.Adapter<BranchLocatorRecyclerViewAdapter.ViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.atm_branch_locator_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val atmBranchResult = atmBranchResults.get(position)
        holder.optionActionButtonView.hideRightArrowImage()
        holder.optionActionButtonView.setImageViewVisibility(View.VISIBLE)
        holder.optionActionButtonView.makeCaptionTextBold()
        if (BRANCH.equals(atmBranchResult.outletType, true)) {
            holder.optionActionButtonView.setCaptionText(atmBranchResult.address.toTitleCase())
            holder.optionActionButtonView.setIcon(R.drawable.ic_branch_locator)
            holder.optionActionButtonView.subCaptionTextView.visibility = View.GONE

        } else {
            holder.optionActionButtonView.setCaptionText(atmBranchResult.address.toTitleCase())
            holder.optionActionButtonView.setIcon(R.drawable.ic_atm_locator)
            holder.optionActionButtonView.subCaptionTextView.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            BaseActivity.preventDoubleClick(it)
            atmBranchResult.let {
                itemClickedInterface.itemClicked(it)
            }
        }
    }

    override fun getItemCount() = atmBranchResults.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val optionActionButtonView: OptionActionButtonView = itemView.findViewById(R.id.atmBranchLocatorOptionActionButtonView)
    }

    interface ItemClickedInterface {
        fun itemClicked(atmBranchResults: AtmBranchDetails)
    }
}