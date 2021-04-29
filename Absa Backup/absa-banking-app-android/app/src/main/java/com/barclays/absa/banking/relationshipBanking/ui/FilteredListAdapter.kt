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
package com.barclays.absa.banking.relationshipBanking.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.relationshipBanking.ui.FilteredListAdapter.ItemViewHolder
import za.co.absa.presentation.uilib.R
import za.co.absa.presentation.uilib.databinding.SelectorListItemViewBinding
import java.util.*

class FilteredListAdapter(private val context: Context, private var items: ArrayList<String>) : RecyclerView.Adapter<ItemViewHolder>(), Filterable {

    lateinit var itemSelectionListener: ItemSelectionListener
    var original: ArrayList<String>? = null
    private var keyword: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = SelectorListItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(itemViewHolder: ItemViewHolder, position: Int) {
        itemViewHolder.onBind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ItemViewHolder(private val binding: SelectorListItemViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(item: String) {

            binding.apply {

                if (keyword.isEmpty()) {
                    contentTextView.text = item
                } else {
                    val startPosition = item.toLowerCase(BMBApplication.getApplicationLocale()).indexOf(keyword.toLowerCase(BMBApplication.getApplicationLocale()))
                    val endPosition = startPosition.plus(keyword.length)

                    if (startPosition != -1) {
                        val spannable = SpannableString(item)
                        val highlightColor = ColorStateList(arrayOf(intArrayOf()), intArrayOf(ContextCompat.getColor(context, R.color.graphite_light_theme_item_color)))
                        val highlightSpan = TextAppearanceSpan(null, Typeface.BOLD, -1, highlightColor, null)
                        spannable.setSpan(highlightSpan, startPosition, endPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        contentTextView.text = spannable
                    } else {
                        contentTextView.text = item
                    }
                }

                labelTextView.visibility = View.GONE

                root.setOnClickListener {
                    primaryContentCheckBox.isChecked = true
                }

                primaryContentCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        itemSelectionListener.onItemSelected(contentTextView, adapterPosition)
                    }
                }
            }
        }
    }

    interface ItemSelectionListener {
        fun onItemSelected(view: View, position: Int)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                keyword = constraint.toString()
                val filterResults = FilterResults()
                val results = ArrayList<String>()
                if (original == null)
                    original = items
                if (constraint != null) {
                    if (original!!.size > 0) {
                        for (lineItem in original!!) {
                            if (lineItem.toLowerCase().contains(constraint.toString(), ignoreCase = true)) {
                                results.add(lineItem)
                            }
                        }
                    }
                    filterResults.values = results
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                @Suppress("UNCHECKED_CAST")
                items = results.values as ArrayList<String>
                notifyDataSetChanged()
            }
        }

    }
}