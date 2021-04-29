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

package com.barclays.absa.banking.manage.profile.ui.addressDetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.manage.profile.ui.models.SuburbLookupResults
import kotlinx.android.synthetic.main.manage_profile_suburb_lookup_item.view.*
import styleguide.forms.ItemSelectionInterface

class PostalCodeLookupAdapter(private var suburbList: ArrayList<SuburbLookupResults>, private val onSuburbItemClicked: ItemSelectionInterface) : RecyclerView.Adapter<PostalCodeLookupAdapter.PostalCodeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostalCodeViewHolder {
        return PostalCodeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.manage_profile_suburb_lookup_item, parent, false))
    }

    override fun getItemCount() = suburbList.size

    override fun onBindViewHolder(holder: PostalCodeViewHolder, position: Int) {
        val selectedSuburb = suburbList[position]
        StringBuilder().apply {
            append(selectedSuburb.suburbName).append(", ")
            append(selectedSuburb.townName).append(", ")
            append(selectedSuburb.suburbPostalCode)

            holder.suburbTextView.text = this.toString()
        }

        holder.suburbTextView.setOnClickListener {
            onSuburbItemClicked.onItemClicked(position)
        }
    }

    class PostalCodeViewHolder(var layout: View) : RecyclerView.ViewHolder(layout) {
        val suburbTextView: TextView = layout.suburbTextView
    }
}