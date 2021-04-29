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
 */
package com.barclays.absa.banking.paymentsRewrite.ui.multiple

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
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.MultipleBeneficiarySelectionItemBinding
import com.barclays.absa.banking.databinding.MultiplePaymentsSectionViewBinding
import com.barclays.absa.banking.express.beneficiaries.dto.RegularBeneficiary
import com.barclays.absa.utils.CommonUtils
import styleguide.utils.extensions.toLowerCaseWithLocale
import java.util.*

class MultipleBeneficiarySectionAdapter(val context: Context, private val multipleBeneficiarySelectionView: MultipleBeneficiarySelectionView, regularBeneficiaryArrayList: List<RegularBeneficiary>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private val maximumAllowedBeneficiaries: Int = if (multipleBeneficiarySelectionView.isBusinessAccount) MultiplePaymentsViewModel.MAX_ALLOWED_BUSINESS_BENEFICIARIES else MultiplePaymentsViewModel.MAX_ALLOWED_RETAIL_BENEFICIARIES
    private val beneficiarySectionList: MutableList<MultipleBeneficiarySectionListItem> = ArrayList()
    private var searchTerm: String = ""

    private var beneficiarySectionedList: Array<MultipleBeneficiarySectionListItem>
    private var originalBeneficiaryList: List<RegularBeneficiary> = regularBeneficiaryArrayList
    private var adapterBackingBeneficiaryList: List<RegularBeneficiary>

    companion object {
        private const val ITEM_TYPE_BENEFICIARY = 0
        private const val ITEM_TYPE_ALPHABET = 1
    }

    init {
        adapterBackingBeneficiaryList = originalBeneficiaryList
        beneficiarySectionedList = CommonUtils.createMultiplePaymentsSectionedList(adapterBackingBeneficiaryList)
        val dataSetObserver = object : AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                updateSessionCache()
            }
        }
        registerAdapterDataObserver(dataSetObserver)
        updateSessionCache()
    }

    override fun getItemViewType(position: Int): Int = if (beneficiarySectionList[position].isSection) ITEM_TYPE_ALPHABET else ITEM_TYPE_BENEFICIARY

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = if (viewType == ITEM_TYPE_ALPHABET) {
        AlphabetViewHolder(MultiplePaymentsSectionViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    } else {
        BeneficiaryItemHolder(MultipleBeneficiarySelectionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_TYPE_BENEFICIARY -> bindBeneficiaryView(viewHolder as BeneficiaryItemHolder, position)
            ITEM_TYPE_ALPHABET -> bindAlphabetView(viewHolder as AlphabetViewHolder, position)
        }
    }

    private fun bindBeneficiaryView(beneficiaryItemHolder: BeneficiaryItemHolder, position: Int) {
        val regularBeneficiary = beneficiarySectionList[position].item as RegularBeneficiary
        val multipleBeneficiarySelectionItemBinding = beneficiaryItemHolder.multipleBeneficiarySelectionItemBinding

        val labelTextStringBuilder = StringBuilder().apply {
            append(regularBeneficiary.beneficiaryDetails.bankOrInstitutionName)
            if (regularBeneficiary.beneficiaryDetails.targetAccountNumber.isNotEmpty()) {
                append(" | ").append(regularBeneficiary.beneficiaryDetails.targetAccountNumber)
            }
        }
        multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView.setLabelText(labelTextStringBuilder.toString())

        val beneficiaryName = regularBeneficiary.beneficiaryName
        val accountNumber = regularBeneficiary.beneficiaryDetails.targetAccountNumber.replace("", ",")
        multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView.contentDescription = context.getString(R.string.talkback_multipay_choose_beneficiary, beneficiaryName, accountNumber)
        if (multipleBeneficiarySelectionView.selectedBeneficiaries.contains(regularBeneficiary)) {
            multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView.setSecondaryCheckBoxChecked(true)
        } else {
            multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView.setSecondaryCheckBoxChecked(false)
        }
        if (searchTerm.isNotEmpty()) {
            val startPosition = beneficiaryName.toLowerCaseWithLocale().indexOf(searchTerm)
            val endPosition = startPosition + searchTerm.length
            if (startPosition != -1) {
                val spannable: Spannable = SpannableString(beneficiaryName)
                val highlightColor = ColorStateList(arrayOf(intArrayOf()), intArrayOf(ContextCompat.getColor(context, R.color.dark_orange)))
                val highlightSpan = TextAppearanceSpan(null, Typeface.BOLD, -1, highlightColor, null)
                spannable.setSpan(highlightSpan, startPosition, endPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView.setContentText(spannable)
            } else {
                multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView.setContentText(beneficiaryName)
            }
        } else {
            multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView.setContentText(beneficiaryName)
        }
        val isDisabled = (multipleBeneficiarySelectionView.selectedBeneficiaries.size == maximumAllowedBeneficiaries && !multipleBeneficiarySelectionView.selectedBeneficiaries.contains(regularBeneficiary))
        multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView.alpha = if (isDisabled) 0.5f else 1f
        multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView.showCheckBox(!isDisabled)
    }

    private fun bindAlphabetView(viewHolder: AlphabetViewHolder, position: Int) {
        with(viewHolder.sectionViewBinding) {
            dividerView.visibility = if (position == 0) View.GONE else View.VISIBLE
            sectionListAlphabetTextView.text = beneficiarySectionList[position].section
            sectionListAlphabetTextView.setPadding(0)
        }
    }

    override fun getItemCount(): Int = beneficiarySectionList.size

    @Synchronized
    private fun updateSessionCache() {
        beneficiarySectionList.clear()
        var previousItem: MultipleBeneficiarySectionListItem? = null
        val count = adapterBackingBeneficiaryList.size
        for (position in 0 until count) {
            val currentItem = getSectionListItem(position)
            if (currentItem != null) {
                if (previousItem == null || isNotTheSame(previousItem.section, currentItem.section)) {
                    val listItem = MultipleBeneficiarySectionListItem(Any(), currentItem.section)
                    listItem.isSection = true
                    currentItem.isBeginningOfSection = true
                    beneficiarySectionList.add(listItem)
                }
                val nextItem = getSectionListItem(position + 1)
                if (nextItem != null && isNotTheSame(currentItem.section, nextItem.section)) {
                    currentItem.isEndOfSection = true
                }
                if (position == count - 1) {
                    currentItem.isEndOfSection = true
                }
                beneficiarySectionList.add(currentItem)
                previousItem = currentItem
            }
        }
    }

    private fun isNotTheSame(previousSection: String?, newSection: String): Boolean = previousSection == null || previousSection != newSection

    private fun getSectionListItem(linkedItemPosition: Int): MultipleBeneficiarySectionListItem? {
        var sectionListItem: MultipleBeneficiarySectionListItem? = null
        if (linkedItemPosition >= 0 && beneficiarySectionedList.isNotEmpty() && beneficiarySectionedList.size > linkedItemPosition) {
            sectionListItem = beneficiarySectionedList[linkedItemPosition]
        }
        return sectionListItem
    }

    fun getSectionListBeneficiary(position: Int): RegularBeneficiary? {
        var regularBeneficiary: RegularBeneficiary? = null
        val sectionListItem = getSectionListItem(position)
        if (sectionListItem != null && !sectionListItem.isSection) {
            regularBeneficiary = sectionListItem.item as RegularBeneficiary
        }
        return regularBeneficiary
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                searchTerm = constraint.toString().toLowerCaseWithLocale()
                val filterResults = FilterResults()
                val results: MutableList<RegularBeneficiary> = ArrayList()
                if (originalBeneficiaryList.isNotEmpty()) {
                    results.addAll(originalBeneficiaryList.filter { it.beneficiaryName.toLowerCaseWithLocale().contains(constraint.toString().toLowerCaseWithLocale()) })
                }
                filterResults.values = results
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                results.values?.let {
                    adapterBackingBeneficiaryList = it as List<RegularBeneficiary>
                    multipleBeneficiarySelectionView.onBeneficiaryListFiltered(adapterBackingBeneficiaryList)
                    beneficiarySectionedList = CommonUtils.createMultiplePaymentsSectionedList(adapterBackingBeneficiaryList)
                    updateSessionCache()
                    notifyDataSetChanged()
                }
            }
        }
    }

    internal inner class AlphabetViewHolder internal constructor(val sectionViewBinding: MultiplePaymentsSectionViewBinding) : RecyclerView.ViewHolder(sectionViewBinding.root)

    internal inner class BeneficiaryItemHolder(val multipleBeneficiarySelectionItemBinding: MultipleBeneficiarySelectionItemBinding) : RecyclerView.ViewHolder(multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView) {
        init {
            multipleBeneficiarySelectionItemBinding.root.setOnClickListener { beneficiaryItemClicked() }
            multipleBeneficiarySelectionItemBinding.secondaryContentAndLabelView.secondaryCheckBox.setOnClickListener { beneficiaryItemClicked() }
        }

        private fun beneficiaryItemClicked() {
            if (adapterPosition >= 0 && adapterPosition < beneficiarySectionList.size) {
                val listItem = beneficiarySectionList[adapterPosition]
                if (!listItem.isSection) {
                    val beneficiary = listItem.item as RegularBeneficiary
                    val index = adapterBackingBeneficiaryList.lastIndexOf(beneficiary)
                    multipleBeneficiarySelectionView.onBeneficiaryClicked(index)
                    multipleBeneficiarySelectionView.stopSearch()
                }
            }
        }
    }
}