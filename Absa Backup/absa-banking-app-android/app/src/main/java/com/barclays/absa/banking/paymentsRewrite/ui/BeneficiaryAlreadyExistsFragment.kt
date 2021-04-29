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

package com.barclays.absa.banking.paymentsRewrite.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.BeneficiaryAlreadyExistsFragmentBinding
import com.barclays.absa.banking.databinding.BeneficiaryTransactionListItemBinding
import com.barclays.absa.banking.express.beneficiaries.dto.RegularBeneficiary
import com.barclays.absa.utils.DateTimeHelper
import com.barclays.absa.utils.extensions.viewBinding

class BeneficiaryAlreadyExistsFragment : PaymentsBaseFragment(R.layout.beneficiary_already_exists_fragment) {
    private val binding by viewBinding(BeneficiaryAlreadyExistsFragmentBinding::bind)
    private lateinit var selectedBeneficiary: RegularBeneficiary

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        paymentsActivity.revertToolbarForTabs()
        if (paymentsViewModel.isBillPayment) {
            setToolBar(R.string.institution)
        } else {
            setToolBar(R.string.recipient_title)
        }
        setupLayout()
        setupClickListeners()
    }

    private fun setupLayout() {
        with(binding) {
            beneficiaryExistsTitleAndDescriptionView.description = getString(R.string.recipient_account_number_description, paymentsViewModel.selectedBeneficiary.targetAccountNumber)
            val beneficiaryTransactionList = paymentsViewModel.beneficiaryList.filter { it.targetAccountNumber == paymentsViewModel.selectedBeneficiary.targetAccountNumber }
            val beneficiaryListAdapter = BeneficiaryListAdapter(beneficiaryTransactionList, object : BeneficiaryListItemClickListener {
                override fun onBeneficiaryListItemCheckChangeListener(selectedBeneficiary: RegularBeneficiary) {
                    this@BeneficiaryAlreadyExistsFragment.selectedBeneficiary = selectedBeneficiary
                }
            })
            beneficiaryExistListRecyclerView.adapter = beneficiaryListAdapter
            beneficiaryListAdapter.notifyDataSetChanged()
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            addNewBeneficiaryButton.setOnClickListener {
                navigate(BeneficiaryAlreadyExistsFragmentDirections.actionBeneficiaryAlreadyExistsFragmentToBeneficiaryDetailsConfirmationFragment())
            }
            nextButton.setOnClickListener {
                paymentsViewModel.selectedBeneficiary = selectedBeneficiary
                paymentsViewModel.beneficiaryAdded = true
                navigate(BeneficiaryAlreadyExistsFragmentDirections.actionBeneficiaryAlreadyExistsFragmentToPaymentDetailsFragment())
            }
        }
    }

    inner class BeneficiaryListAdapter(private var beneficiaryList: List<RegularBeneficiary>, private val beneficiaryListItemClickListener: BeneficiaryListItemClickListener) : RecyclerView.Adapter<BeneficiaryListAdapter.BeneficiaryListViewHolder>() {

        private var selectedItemPosition = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeneficiaryListViewHolder {
            val beneficiaryListItemBinding = BeneficiaryTransactionListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return BeneficiaryListViewHolder(beneficiaryListItemBinding)
        }

        override fun getItemCount() = beneficiaryList.size

        override fun onBindViewHolder(holder: BeneficiaryListAdapter.BeneficiaryListViewHolder, position: Int) {
            val regularBeneficiary = beneficiaryList[position]
            with(holder.binding) {
                existingBeneficiaryNameTextView.text = regularBeneficiary.beneficiaryName
                if (regularBeneficiary.processedTransactions.isNotEmpty()) {
                    statementReferenceContentView.setContentText(regularBeneficiary.processedTransactions.first().paymentTargetAccountReference)
                    val lastPaymentDate = DateTimeHelper.formatDate(regularBeneficiary.processedTransactions.first().paymentTransactionDateAndTime, DateTimeHelper.SPACED_PATTERN_DD_MMMM_YYYY)
                    lastPaymentDateContentView.setContentText(lastPaymentDate)
                }
                beneficiaryTransactionCheckbox.setOnCheckedChangeListener { _, _ ->
                    beneficiaryListItemClickListener.onBeneficiaryListItemCheckChangeListener(regularBeneficiary)
                }
                beneficiaryTransactionCheckbox.isChecked = position == selectedItemPosition
                val clickListener = View.OnClickListener {
                    selectedItemPosition = position
                    notifyDataSetChanged()
                }
                holder.itemView.setOnClickListener(clickListener)
                beneficiaryTransactionCheckbox.setOnClickListener(clickListener)
            }
        }

        inner class BeneficiaryListViewHolder(val binding: BeneficiaryTransactionListItemBinding) : RecyclerView.ViewHolder(binding.root)

    }

    interface BeneficiaryListItemClickListener {
        fun onBeneficiaryListItemCheckChangeListener(selectedBeneficiary: RegularBeneficiary)
    }
}