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
 */

package com.barclays.absa.banking.manage.profile.ui.docHandler

import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.manage.profile.ui.models.ManageProfileDocHandlerDocumentType
import styleguide.content.TertiaryContentAndLabelView
import styleguide.forms.ItemSelectionInterface

class ManageProfileDocumentTypeRecyclerViewAdapter(private var documentTypes: ArrayList<ManageProfileDocHandlerDocumentType>, onItemSelected: ItemSelectionInterface? = null) : RecyclerView.Adapter<ManageProfileDocumentTypeRecyclerViewAdapter.ManageProfileDocumentTypeViewHolder>() {
    private var selectedItemPosition: Int = -1
    private lateinit var itemSelectionInterface: ItemSelectionInterface

    init {
        if (onItemSelected != null) {
            itemSelectionInterface = onItemSelected
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageProfileDocumentTypeViewHolder {
        val tertiaryContentAndLabelView = TertiaryContentAndLabelView((parent.context))
        tertiaryContentAndLabelView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        tertiaryContentAndLabelView.showCheckBox(true)
        return ManageProfileDocumentTypeViewHolder(tertiaryContentAndLabelView)
    }

    override fun getItemCount() = documentTypes.size

    override fun onBindViewHolder(holder: ManageProfileDocumentTypeViewHolder, position: Int) {
        holder.apply {
            contentTextView.text = documentTypes[position].displayName
            labelTextView.visibility = View.GONE

            itemSelectedCheckbox.isChecked = position == selectedItemPosition

            itemSelectedCheckbox.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }
    }

    inner class ManageProfileDocumentTypeViewHolder(var contentView: TertiaryContentAndLabelView) : RecyclerView.ViewHolder(contentView), View.OnClickListener {
        val contentTextView: TextView = contentView.findViewById(R.id.contentTextView)
        val labelTextView: TextView = contentView.findViewById(R.id.labelTextView)
        val itemSelectedCheckbox: CheckBox = contentView.findViewById(R.id.tertiary_content_check_box)

        override fun onClick(view: View) {
            if (view == contentView || view == itemSelectedCheckbox) {
                if (adapterPosition == selectedItemPosition) {
                    itemSelectedCheckbox.isChecked = false
                    selectedItemPosition = -1
                } else {
                    selectedItemPosition = adapterPosition
                    notifyDataSetChanged()
                }
                itemSelectionInterface.onItemClicked(selectedItemPosition)
            }
        }
    }
}