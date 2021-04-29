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

package com.barclays.absa.banking.cashSendPlus.ui

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
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.BeneficiaryObject
import com.barclays.absa.banking.cashSendPlus.ui.CashSendPlusSendMultipleActivity.Companion.MAX_ALLOWED_BENEFICIARIES
import com.barclays.absa.banking.databinding.MultipleBeneficiarySelectionItemBinding
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.paymentsRewrite.ui.multiple.MultipleBeneficiarySectionListItem
import com.barclays.absa.utils.CommonUtils

class CashSendPlusChooseBeneficiaryAdapter(private val beneficiaryObjectList: MutableList<BeneficiaryObject>, private val chooseBeneficiaryListener: OnChooseBeneficiaryItemSelectedListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private var beneficiarySectionedList = arrayOf<MultipleBeneficiarySectionListItem>()
    private var adapterBackingBeneficiaryList = mutableListOf<BeneficiaryObject>()
    private val listItems = mutableListOf<MultipleBeneficiarySectionListItem>()
    private val selectedBeneficiaryObjectList = mutableListOf<BeneficiaryObject>()
    private var maximumAllowedBeneficiaries = MAX_ALLOWED_BENEFICIARIES
    private var searchTerm: String = ""

    companion object {
        private const val ITEM_TYPE_BENEFICIARY = 0
        private const val ITEM_TYPE_ALPHABET = 1
    }

    class AlphabetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var alphabetTextView: TextView = view.findViewById(R.id.sectionListAlphabetView)
    }

    init {
        adapterBackingBeneficiaryList = beneficiaryObjectList
        if (adapterBackingBeneficiaryList.isNotEmpty()) {
            beneficiarySectionedList = CommonUtils.createCashSendPlusSectionedList(adapterBackingBeneficiaryList)
        }

        val dataSetObserver: AdapterDataObserver = object : AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                updateSessionCache()
            }
        }
        registerAdapterDataObserver(dataSetObserver)
        updateSessionCache()
    }

    override fun getItemViewType(position: Int): Int {
        return if (listItems[position].isSection) ITEM_TYPE_ALPHABET else ITEM_TYPE_BENEFICIARY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_TYPE_ALPHABET -> AlphabetViewHolder(inflater.inflate(R.layout.section_view, parent, false))
            else -> {
                val multipleBeneficiarySelectionItemBinding: MultipleBeneficiarySelectionItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.multiple_beneficiary_selection_item, parent, false)
                ChooseBeneficiaryItemHolder(multipleBeneficiarySelectionItemBinding)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_TYPE_BENEFICIARY -> bindBeneficiaryView(viewHolder, position)
            ITEM_TYPE_ALPHABET -> bindAlphabetView(viewHolder as AlphabetViewHolder, position)
        }
    }

    private fun bindBeneficiaryView(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val multipleBeneficiarySectionListItem = listItems[position]
        val beneficiaryObject = multipleBeneficiarySectionListItem.item as BeneficiaryObject
        val multipleBeneficiarySelectionItemBinding: MultipleBeneficiarySelectionItemBinding = (viewHolder as ChooseBeneficiaryItemHolder).binding
        val beneficiaryName = beneficiaryObject.beneficiaryName
        val beneficiaryPhoneNumber = beneficiaryObject.beneficiaryAccountNumber
        val context = multipleBeneficiarySelectionItemBinding.root.context
        val alreadySelected = selectedBeneficiaryObjectList.any { it.beneficiaryAccountNumber == beneficiaryObject.beneficiaryAccountNumber }

        with(multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView) {
            setContentText(beneficiaryName)
            setLabelText(beneficiaryPhoneNumber)
            secondaryCheckBox.setOnClickListener {
                if (!multipleBeneficiarySectionListItem.isSection) {
                    chooseBeneficiaryListener.onBeneficiarySelected(secondaryCheckBox.isChecked, beneficiaryObject)
                }
            }

            setOnClickListener { secondaryCheckBox.performClick() }

            setSecondaryCheckBoxChecked(alreadySelected)
            val isDisable = (selectedBeneficiaryObjectList.size == maximumAllowedBeneficiaries && !alreadySelected)

            alpha = if (isDisable) 0.5f else 1f
            secondaryCheckBox.isEnabled = !isDisable
            showCheckBox(!isDisable)
        }

        if (searchTerm.isNotEmpty()) {
            if (beneficiaryName != null) {
                val startPosition = beneficiaryName.toLowerCase(BMBApplication.getApplicationLocale()).indexOf(searchTerm)
                val endPosition = startPosition + searchTerm.length
                if (startPosition != -1) {
                    val spannable: Spannable = SpannableString(beneficiaryName)
                    val highlightColor = ColorStateList(arrayOf(intArrayOf()), intArrayOf(ContextCompat.getColor(context, R.color.filter_color)))
                    val highlightSpan = TextAppearanceSpan(null, Typeface.BOLD, -1, highlightColor, null)
                    spannable.setSpan(highlightSpan, startPosition, endPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView.contentTextView.text = spannable
                } else {
                    multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView.setContentText(beneficiaryName)
                }
            }
        } else {
            multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView.setContentText(beneficiaryName)
        }
    }

    private fun bindAlphabetView(viewHolder: AlphabetViewHolder, position: Int) {
        viewHolder.alphabetTextView.text = listItems[position].section
        viewHolder.alphabetTextView.setPadding(0, 0, 0, 0)
    }

    override fun getItemCount(): Int = listItems.size

    @Synchronized
    private fun updateSessionCache() {
        listItems.clear()
        var previousItem: MultipleBeneficiarySectionListItem? = null
        val count = adapterBackingBeneficiaryList.size
        for (position in 0 until count) {
            val currentItem: MultipleBeneficiarySectionListItem? = getSectionListItem(position)
            if (currentItem != null) {
                if (previousItem == null || !isTheSame(previousItem.section, currentItem.section)) {
                    val listItem = MultipleBeneficiarySectionListItem(Any(), currentItem.section)
                    listItem.isSection = true
                    currentItem.isBeginningOfSection = true
                    listItems.add(listItem)
                }
                val nextItem: MultipleBeneficiarySectionListItem? = getSectionListItem(position + 1)
                if (nextItem != null && !isTheSame(currentItem.section, nextItem.section)) {
                    currentItem.isEndOfSection = true
                }
                if (position == count - 1) {
                    currentItem.isEndOfSection = true
                }
                listItems.add(currentItem)
                previousItem = currentItem
            }
        }
    }

    private fun isTheSame(previousSection: String?, newSection: String) =
            previousSection != null && previousSection == newSection

    private fun getSectionListItem(linkedItemPosition: Int): MultipleBeneficiarySectionListItem? {
        var sectionListItem: MultipleBeneficiarySectionListItem? = null
        if (linkedItemPosition >= 0 && beneficiarySectionedList.isNotEmpty() && beneficiarySectionedList.size > linkedItemPosition) {
            sectionListItem = beneficiarySectionedList[linkedItemPosition]
        }
        return sectionListItem
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                searchTerm = constraint.toString().toLowerCase(BMBApplication.getApplicationLocale())
                val filterResults = FilterResults()
                val results = mutableListOf<BeneficiaryObject>()
                beneficiaryObjectList.forEach {
                    if (it.beneficiaryName != null && it.beneficiaryName?.toLowerCase(BMBApplication.getApplicationLocale())?.contains(constraint.toString().toLowerCase(BMBApplication.getApplicationLocale())) == true) {
                        results.add(it)
                    }
                }
                filterResults.values = results
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                results.values?.let {
                    adapterBackingBeneficiaryList = it as MutableList<BeneficiaryObject>
                    chooseBeneficiaryListener.onBeneficiaryListFiltered(adapterBackingBeneficiaryList)
                    beneficiarySectionedList = CommonUtils.createCashSendPlusSectionedList(adapterBackingBeneficiaryList)
                    updateSessionCache()
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun updateSelectedBeneficiaries(selectedBeneficiaryList: MutableList<BeneficiaryObject>) {
        selectedBeneficiaryObjectList.clear()
        selectedBeneficiaryObjectList.addAll(selectedBeneficiaryList)
        notifyDataSetChanged()
    }

    class ChooseBeneficiaryItemHolder(val binding: MultipleBeneficiarySelectionItemBinding) : RecyclerView.ViewHolder(binding.secondaryContentAndLabelView)

    interface OnChooseBeneficiaryItemSelectedListener {
        fun onBeneficiarySelected(isChecked: Boolean, item: BeneficiaryObject)
        fun onBeneficiaryListFiltered(results: MutableList<BeneficiaryObject>)
    }
}