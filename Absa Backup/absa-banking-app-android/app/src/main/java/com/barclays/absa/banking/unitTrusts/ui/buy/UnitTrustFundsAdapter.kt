/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.unitTrusts.ui.buy

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.BuyUnitTrustFundListItemBinding
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustFund
import java.util.regex.Pattern

class UnitTrustFundsAdapter(private val context: Context, private val fragment: BuyUnitTrustFundsFragment, private val funds: ArrayList<UnitTrustFund> = arrayListOf()) : RecyclerView.Adapter<UnitTrustFundsAdapter.UnitTrustFundViewHolder>() {

    private lateinit var keyword: String
    private lateinit var originalFilteredFunds: ArrayList<UnitTrustFund>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitTrustFundViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<BuyUnitTrustFundListItemBinding>(inflater, R.layout.buy_unit_trust_fund_list_item, parent, false)
        return UnitTrustFundViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UnitTrustFundViewHolder, position: Int) {
        holder.bind(funds.get(position))
    }

    fun update(filteredFunds: List<UnitTrustFund>) {
        originalFilteredFunds = ArrayList(filteredFunds)
        funds.clear()
        funds.addAll(filteredFunds)
        notifyDataSetChanged()
    }

    fun search(keyword: String) {
        this.keyword = keyword
        if (keyword.isEmpty()) {
            refresh(originalFilteredFunds)
        } else {
            val searchResult = arrayListOf<UnitTrustFund>()
            for (fund in originalFilteredFunds) {
                if (!fund.fundName.isNullOrEmpty()) {
                    val pattern = Pattern.compile(keyword.toLowerCase(BMBApplication.getApplicationLocale()))
                    val matcher = pattern.matcher(fund.fundName?.toLowerCase(BMBApplication.getApplicationLocale()) ?: "")
                    if (matcher.find()) {
                        searchResult.add(fund)
                    }
                }
            }
            refresh(searchResult)
        }
    }

    private fun refresh(newFunds: ArrayList<UnitTrustFund>) {
        funds.clear()
        funds.addAll(newFunds)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = funds.size

    inner class UnitTrustFundViewHolder(val binding: BuyUnitTrustFundListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(unitTrustFund: UnitTrustFund) {
            if (!::keyword.isInitialized || keyword.isEmpty()) {
                binding.fundDescriptionTitleAndDescriptionView.setContentText(unitTrustFund.fundName)
            } else {
                val startPosition = unitTrustFund.fundName?.toLowerCase()?.indexOf(keyword.toLowerCase())
                val endPosition = startPosition?.plus(keyword.length)

                if (startPosition != -1) {
                    val spannable = SpannableString(unitTrustFund.fundName)
                    val highlightColor = ColorStateList(arrayOf(intArrayOf()), intArrayOf(ContextCompat.getColor(context, za.co.absa.presentation.uilib.R.color.graphite_light_theme_item_color)))
                    val highlightSpan = TextAppearanceSpan(null, Typeface.BOLD, -1, highlightColor, null)
                    spannable.setSpan(highlightSpan, startPosition!!, endPosition!!, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    binding.fundDescriptionTitleAndDescriptionView.setContentText(spannable.toString())
                } else {
                    binding.fundDescriptionTitleAndDescriptionView.setContentText(unitTrustFund.fundName)
                }
            }
            binding.riskSecondaryContentAndLabelView.setContentText(unitTrustFund.fundRisk)
            binding.termSecondaryContentAndLabelView.setContentText(unitTrustFund.fundTerm)
            binding.buyUnitTrustConstraintLayout.setOnClickListener {
                fragment.onFundListItemSelected(unitTrustFund)
            }
            binding.findOutMoreOptionActionButtonView.setOnClickListener {
                fragment.onFundListItemSelected(unitTrustFund)
            }
        }
    }
}