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
 */

package com.barclays.absa.banking.manage.profile.ui.financialDetails

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.manage.profile.ui.models.DisplayForeignTaxCountry
import styleguide.content.PrimaryContentAndLabelWithImageView
import styleguide.forms.ItemSelectionInterface

class ManageProfileCountryAdapter<T>(private var foreignCountryTaxDetails: ArrayList<T>, private val onTaxCountryItemClicked: ItemSelectionInterface? = null) : RecyclerView.Adapter<ManageProfileCountryAdapter.ManageProfileViewHolder>() {
    private lateinit var foreignTaxDetails: ArrayList<DisplayForeignTaxCountry>

    @Suppress("unchecked_cast")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageProfileViewHolder {
        val primaryContentAndLabelView = PrimaryContentAndLabelWithImageView(parent.context)
        foreignTaxDetails = foreignCountryTaxDetails as ArrayList<DisplayForeignTaxCountry>

        primaryContentAndLabelView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return ManageProfileViewHolder(primaryContentAndLabelView)
    }

    override fun getItemCount() = foreignCountryTaxDetails.size

    override fun onBindViewHolder(holder: ManageProfileViewHolder, position: Int) {
        holder.apply {
            contentText.text = foreignTaxDetails[position].taxCountry
            labelText.text = foreignTaxDetails[position].taxNumber.ifEmpty { foreignTaxDetails[position].reasonForNoTaxNumber }
            secondaryImage.visibility = View.VISIBLE

            itemView.apply {
                if (contentText.text.isEmpty()) {
                    visibility = View.GONE
                    layoutParams = RecyclerView.LayoutParams(0, 0)
                }

                if (onTaxCountryItemClicked != null) {
                    setOnClickListener {
                        onTaxCountryItemClicked.onItemClicked(position)
                    }
                } else {
                    secondaryImage.visibility = View.GONE
                    (this as PrimaryContentAndLabelWithImageView).disableClickAnimation()
                }
            }
        }
    }

    class ManageProfileViewHolder(contentView: PrimaryContentAndLabelWithImageView) : RecyclerView.ViewHolder(contentView) {
        val labelText: TextView = contentView.labelTextView
        val contentText: TextView = contentView.contentTextView
        val secondaryImage: ImageView = contentView.secondaryImageView
    }
}