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
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.ViewUnitTrustAccountItemBinding
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustAccount
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustFund
import com.barclays.absa.utils.TextFormatUtils
import styleguide.utils.extensions.toTitleCase

class ViewUnitTrustFundsAdapter(private val unitTrustAccounts: List<UnitTrustAccount>, private var unitTrustItemClickListener: UnitTrustItemClickListener) : androidx.recyclerview.widget.RecyclerView.Adapter<ViewUnitTrustFundsAdapter.ViewUnitTrustViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewUnitTrustViewHolder {
        val unitTrustItemBinding = DataBindingUtil.inflate<ViewUnitTrustAccountItemBinding>(LayoutInflater.from(parent.context), R.layout.view_unit_trust_account_item, parent, false)
        return ViewUnitTrustViewHolder(unitTrustItemBinding, unitTrustItemClickListener)
    }

    override fun onBindViewHolder(holder: ViewUnitTrustViewHolder, position: Int) {
        val unitTrustAccount = unitTrustAccounts[position]
        holder.bind(unitTrustAccount)
    }

    override fun getItemCount(): Int {
        return if (unitTrustAccounts.isNullOrEmpty()) 0 else unitTrustAccounts.size
    }

    inner class ViewUnitTrustViewHolder(val binding: ViewUnitTrustAccountItemBinding, private var unitTrustItemClickListener: ViewUnitTrustFundsAdapter.UnitTrustItemClickListener) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        fun bind(unitTrustAccount: UnitTrustAccount) {
            changeViewState(binding, View.VISIBLE)
            val unitTrustAccountNumber = unitTrustAccount.accountNumber
            val unitTrustAccountHolder = unitTrustAccount.accountHolderName
            val funds = unitTrustAccount.unitTrustFundList
            binding.selectedAccountNumberView.title = unitTrustAccountNumber
            binding.availableBalanceLineItemView.setLineItemViewContent(TextFormatUtils.formatBasicAmountAsRand(unitTrustAccount.availableBalance))
            binding.availableUnitsLineItemView.setLineItemViewContent(TextFormatUtils.formatBasicAmount(unitTrustAccount.availableAccountUnits))

            if (funds.isNullOrEmpty()) {
                changeViewState(binding, View.GONE)
            } else {
                binding.fundOneActionButtonView.setCaptionText(funds[0].fundName?.toTitleCase())
                when (funds.size) {
                    1 -> {
                        binding.fundTwoActionButtonView.visibility = View.GONE
                        binding.fundThreeActionButtonView.visibility = View.GONE
                        binding.showMoreFundsActionButtonView.visibility = View.GONE
                    }
                    2 -> {
                        binding.fundTwoActionButtonView.setCaptionText(funds[1].fundName?.toTitleCase())
                        binding.fundThreeActionButtonView.visibility = View.GONE
                        binding.showMoreFundsActionButtonView.visibility = View.GONE
                    }
                    3 -> {
                        binding.fundTwoActionButtonView.setCaptionText(funds[1].fundName?.toTitleCase())
                        binding.fundThreeActionButtonView.setCaptionText(funds[2].fundName?.toTitleCase())
                        binding.showMoreFundsActionButtonView.visibility = View.GONE
                    }
                    else -> {
                        binding.fundTwoActionButtonView.setCaptionText(funds[1].fundName?.toTitleCase())
                        binding.fundThreeActionButtonView.setCaptionText(funds[2].fundName?.toTitleCase())
                        binding.showMoreFundsActionButtonView.visibility = View.VISIBLE
                    }
                }
            }

            binding.fundOneActionButtonView.setOnClickListener {
                unitTrustItemClickListener.onFundItemClicked(funds[0], unitTrustAccountNumber, unitTrustAccountHolder)
            }

            binding.fundTwoActionButtonView.setOnClickListener {
                unitTrustItemClickListener.onFundItemClicked(funds[1], unitTrustAccountNumber, unitTrustAccountHolder)
            }

            binding.fundThreeActionButtonView.setOnClickListener {
                unitTrustItemClickListener.onFundItemClicked(funds[2], unitTrustAccountNumber, unitTrustAccountHolder)
            }

            binding.showMoreFundsActionButtonView.setOnClickListener {
                unitTrustItemClickListener.onShowMoreClicked(funds, unitTrustAccountNumber, unitTrustAccountHolder)
            }
        }

        private fun changeViewState(binding: ViewUnitTrustAccountItemBinding, viewState: Int) {
            binding.fundsLabelTextView.visibility = viewState
            binding.fundOneActionButtonView.visibility = viewState
            binding.fundTwoActionButtonView.visibility = viewState
            binding.fundThreeActionButtonView.visibility = viewState
            binding.showMoreFundsActionButtonView.visibility = viewState
            binding.titleDividerView.visibility = viewState
        }
    }

    interface UnitTrustItemClickListener {
        fun onShowMoreClicked(fundsList: List<UnitTrustFund>, accountNumber: String, accountHolder: String)
        fun onFundItemClicked(unitTrustFund: UnitTrustFund, accountNumber: String, accountHolder: String)
    }
}