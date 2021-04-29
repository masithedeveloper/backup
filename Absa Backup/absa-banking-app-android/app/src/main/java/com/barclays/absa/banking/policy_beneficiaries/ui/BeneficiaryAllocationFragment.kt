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

package com.barclays.absa.banking.policy_beneficiaries.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.policy.PolicyBeneficiary
import com.barclays.absa.banking.databinding.ManageBeneficiariesShareAllocationItemBinding
import com.barclays.absa.banking.funeralCover.ui.InsurancePolicyClaimsBaseActivity
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.manage_beneficiaries_allocation_fragment.*
import styleguide.forms.StringItem

class BeneficiaryAllocationFragment : ManageBeneficiaryBaseFragment(R.layout.manage_beneficiaries_allocation_fragment) {

    private lateinit var adapter: AllocationAdapter
    private var showDescriptionMessage = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.manage_policy_beneficiaries_city_allocation))
        manageBeneficiaryActivity.setStep(3)
        AnalyticsUtil.trackAction("Insurance_Hub", "${InsurancePolicyClaimsBaseActivity.policyType}_AllocationScreen_ScreenDisplayed")

        beneficiariesRecyclerView.layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically() = false
        }
        beneficiariesRecyclerView.setHasFixedSize(true)
        manageBeneficiaryViewModel.policyBeneficiaries.let { existingBeneficiaries ->
            val beneficiaries = manageBeneficiaryViewModel.buildBeneficiaryList(existingBeneficiaries)
            adapter = AllocationAdapter(beneficiaries)
            showDescriptionMessage = beneficiaries.size == 3 && manageBeneficiaryViewModel.areAllocationsSharedEqual(beneficiaries)
            beneficiariesRecyclerView.adapter = adapter
            if (beneficiaries.size == 1) {
                shareButton.visibility = View.GONE
            } else {
                shareButton.visibility = View.VISIBLE
                shareButton.setOnClickListener {
                    when {
                        beneficiaries.size == 2 || beneficiaries.size > 3 -> calculateAllocation(beneficiaries)
                        beneficiaries.size == 3 -> {
                            calculateAllocation(beneficiaries)
                            adapter.showDescription()
                        }
                    }
                }
            }
            continueButton.setOnClickListener {
                manageBeneficiaryViewModel.buildBeneficiaryData(beneficiaries)
                val totalAllocation = manageBeneficiaryViewModel.getTotalAllocation(beneficiaries)
                val hasEmptyField = manageBeneficiaryViewModel.hasEmptyField(beneficiaries)
                when {
                    hasEmptyField -> adapter.showError(getString(R.string.manage_policy_beneficiaries_allocation_error_message))
                    (totalAllocation > ManageBeneficiaryViewModel.MAX_SHARE_ALLOCATION ||
                            totalAllocation < ManageBeneficiaryViewModel.MAX_SHARE_ALLOCATION) -> {
                        adapter.hideDescription()
                        adapter.showError(getString(R.string.manage_policy_beneficiaries_allocation_error_message2, totalAllocation))
                    }
                    else -> {
                        navigate(BeneficiaryAllocationFragmentDirections.beneficiaryAllocationFragmentToBeneficiaryConfirmationFragment())
                    }
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (showDescriptionMessage) {
            adapter.showDescription()
        }
    }

    private fun calculateAllocation(beneficiaries: List<PolicyBeneficiary>) {
        manageBeneficiaryViewModel.calculateShareAllocation(beneficiaries)
        if (adapter.hasError()) {
            adapter.hideError()
        }
        adapter.notifyDataSetChanged()
    }

    inner class AllocationAdapter(private val beneficiaries: List<PolicyBeneficiary>) : RecyclerView.Adapter<AllocationAdapter.BeneficiaryViewHolder>() {
        private var error = ""
        private val description = getString(R.string.manage_policy_beneficiaries_allocation_three_beneficiary_share)
        private var isErrorShown = false
        private var isDescriptionShown = false

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BeneficiaryViewHolder {
            val binding = ManageBeneficiariesShareAllocationItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
            return BeneficiaryViewHolder(binding)
        }

        override fun onBindViewHolder(beneficiaryViewHolder: BeneficiaryViewHolder, position: Int) {
            beneficiaryViewHolder.onBind(position)
        }

        fun showError(error: String) {
            this.error = error
            isErrorShown = true
            adapter.notifyDataSetChanged()
        }

        fun hideError() {
            isErrorShown = false
            adapter.notifyDataSetChanged()
        }

        fun hideDescription() {
            isDescriptionShown = false
            adapter.notifyDataSetChanged()
        }

        fun showDescription() {
            isDescriptionShown = true
            adapter.notifyDataSetChanged()
        }

        fun hasError() = isErrorShown

        override fun getItemCount(): Int = beneficiaries.size

        inner class BeneficiaryViewHolder(private val binding: ManageBeneficiariesShareAllocationItemBinding) : RecyclerView.ViewHolder(binding.root) {
            fun onBind(position: Int) {
                binding.apply {
                    if (position == beneficiaries.lastIndex) {
                        if (isErrorShown) {
                            allocationNormalInputView.setError(error)
                        } else {
                            allocationNormalInputView.showError(false)
                        }

                        if (isDescriptionShown) {
                            allocationNormalInputView.setDescription(description)
                        } else {
                            allocationNormalInputView.showDescription(false)
                        }
                    }

                    val policyBeneficiary = beneficiaries[position]
                    allocationNormalInputView.setTitleText(policyBeneficiary.fullName)
                    if (policyBeneficiary.allocation.isNotEmpty()) {
                        allocationNormalInputView.selectedValue = policyBeneficiary.allocation + "%"
                        val allocationNormalInputViewStringItem = StringItem(allocationNormalInputView.selectedValue, "")
                        allocationNormalInputView.selectedIndex = InsuranceBeneficiaryHelper.buildPercentageSelectorOptions().indexOf(allocationNormalInputViewStringItem)
                    }
                    allocationNormalInputView.setItemSelectionInterface {
                        allocationNormalInputView.showError(false)
                        policyBeneficiary.allocation = binding.allocationNormalInputView.selectedValue.removeSuffix("%")
                    }
                    allocationNormalInputView.setList(InsuranceBeneficiaryHelper.buildPercentageSelectorOptions(), getString(R.string.manage_policy_beneficiaries_allocation_hint))
                }
            }
        }
    }
}