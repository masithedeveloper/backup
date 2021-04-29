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

package com.barclays.absa.banking.unitTrusts.ui.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustFund
import styleguide.buttons.OptionActionButtonView
import styleguide.utils.extensions.toTitleCase
import za.co.absa.presentation.uilib.databinding.AdditionalFundAccountItemBinding

class AllFundsAdapter(private var fundsList: List<UnitTrustFund>, private var allFundsItemClickListener: AllFundsItemClickListener) : RecyclerView.Adapter<AllFundsAdapter.AllFundsViewHolder>() {

    interface AllFundsItemClickListener {
        fun onFundNameClicked(unitTrustFund: UnitTrustFund)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllFundsViewHolder {
        val allFundsItemBinding = DataBindingUtil.inflate<AdditionalFundAccountItemBinding>(LayoutInflater.from(parent.context), R.layout.additional_fund_account_item, parent, false)
        return AllFundsViewHolder(allFundsItemBinding, allFundsItemClickListener)
    }

    override fun getItemCount(): Int = fundsList.size

    override fun onBindViewHolder(holder: AllFundsViewHolder, position: Int) {
        val unitTrustFundAccount = fundsList[position]
        holder.fundNameView.setCaptionText(unitTrustFundAccount.fundName.toTitleCase())
        holder.setFund(unitTrustFundAccount)
    }

    class AllFundsViewHolder(itemBinding: AdditionalFundAccountItemBinding, private var allFundsItemClickListener: AllFundsItemClickListener) : RecyclerView.ViewHolder(itemBinding.root) {
        var fundNameView: OptionActionButtonView = itemBinding.fundNameView
        private lateinit var unitTrustFund: UnitTrustFund

        init {
            fundNameView.setOnClickListener {
                allFundsItemClickListener.onFundNameClicked(unitTrustFund)
            }
        }

        fun setFund(unitTrustFund: UnitTrustFund) {
            this.unitTrustFund = unitTrustFund
        }
    }
}