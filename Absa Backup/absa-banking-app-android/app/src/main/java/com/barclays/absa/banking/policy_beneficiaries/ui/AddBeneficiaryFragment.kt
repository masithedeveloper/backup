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

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.policy.PolicyBeneficiary
import com.barclays.absa.banking.boundary.model.policy.PolicyDetail
import com.barclays.absa.banking.databinding.ManageBeneficiariesListItemBinding
import com.barclays.absa.banking.express.exergy.GetExergyCodesByTypeViewModel
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.funeralCover.ui.InsurancePolicyClaimsBaseActivity.policyType
import com.barclays.absa.banking.policy_beneficiaries.services.dto.PolicyBeneficiaryInfo
import com.barclays.absa.banking.riskBasedApproach.services.dto.RiskBasedApproachViewModel
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog.showAlertDialog
import com.barclays.absa.banking.shared.services.SharedViewModel
import com.barclays.absa.banking.shared.services.dto.CIFGroupCode
import com.barclays.absa.utils.AnalyticsUtil.trackAction
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.manage_beneficiaries_fragment.*
import styleguide.content.BeneficiaryListItem
import styleguide.utils.extensions.toTitleCase

class AddBeneficiaryFragment : ManageBeneficiaryBaseFragment(R.layout.manage_beneficiaries_fragment) {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var riskViewModel: RiskBasedApproachViewModel
    private lateinit var exergyByCodeTypesViewModel: GetExergyCodesByTypeViewModel
    private lateinit var adapter: BeneficiaryAdapter
    private var removeMenuItem: MenuItem? = null
    private var cancelMenuItem: MenuItem? = null

    companion object {
        private const val ESTATE_LATE = "Estate Late"
        private const val RELATIONSHIP = "RELATIONSHIP"
        private const val EXERGY_ID_CODE = "1"
        private const val EXERGY_PASSPORT_CODE = "2"
        private const val ID_CODE = "01"
        private const val RELATIONSHIP_CODE = "03"

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedViewModel = manageBeneficiaryActivity.viewModel()
        riskViewModel = manageBeneficiaryActivity.viewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.remove_cancel_menu, menu)
        removeMenuItem = menu.findItem(R.id.removeMenuItem)
        cancelMenuItem = menu.findItem(R.id.cancelMenuItem)
        removeMenuItem?.isVisible = (manageBeneficiaryViewModel.policyBeneficiaries.isNotEmpty() && manageBeneficiaryViewModel.policyBeneficiaries.size != 1)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.removeMenuItem -> {
                removeMenuItem?.isVisible = false
                cancelMenuItem?.isVisible = true
                adapter.isDeleteEnabled = true
                true
            }
            R.id.cancelMenuItem -> {
                cancelMenuItem?.isVisible = false
                removeMenuItem?.isVisible = true
                adapter.isDeleteEnabled = false
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.manage_beneficiary_title))
        manageBeneficiaryActivity.hideProgressIndicator()
        trackAction("Insurance_Hub", "${policyType}_ManageBeneficiariesScreen_ScreenDisplayed")

        manageBeneficiaryViewModel.fetchPolicyDetails().observe(viewLifecycleOwner, { detail ->
            dismissProgressDialog()
            detail?.let { policyDetail ->
                manageBeneficiaryViewModel.isExergyPolicy = policyDetail.policy?.type == BMBConstants.EXERGY_POLICY_TYPE
                val idCodesArray = if (manageBeneficiaryViewModel.isExergyPolicy) {
                    arrayOf(EXERGY_ID_CODE, EXERGY_PASSPORT_CODE)
                } else {
                    arrayOf(ID_CODE, RELATIONSHIP_CODE)
                }

                InsuranceBeneficiaryHelper.buildIdTypes(resources.getStringArray(R.array.idTypes), idCodesArray)

                val sourceOfFund = policyDetail.accountInfo?.sourceOfFund
                when {
                    manageBeneficiaryViewModel.sourceOfFundList.isEmpty() -> {
                        sharedViewModel.codesLiveData.observe(viewLifecycleOwner, { result ->
                            result?.items?.let { items ->
                                if (items.isNotEmpty()) {

                                    when (items[0].groupCode) {
                                        CIFGroupCode.SOURCE_OF_FUNDS.key -> {
                                            manageBeneficiaryViewModel.sourceOfFundList = items
                                            if (!sourceOfFund.isNullOrEmpty()) {
                                                manageBeneficiaryViewModel.policyInfo.sourceOfFunds = InsuranceBeneficiaryHelper.getMatchingLookupItem(sourceOfFund, items)
                                            }
                                            if (manageBeneficiaryViewModel.isExergyPolicy) {
                                                exergyByCodeTypesViewModel = GetExergyCodesByTypeViewModel()
                                                exergyByCodeTypesViewModel.fetchExergyCodesByType("TITLE")
                                                attachExergyByCodeTypesObserver(policyDetail)
                                            } else if (manageBeneficiaryViewModel.titles.isEmpty()) {
                                                sharedViewModel.getCIFCodes(CIFGroupCode.TITLE)
                                            }
                                        }
                                        CIFGroupCode.TITLE.key -> {
                                            manageBeneficiaryViewModel.titles = items.filterNot { item -> ESTATE_LATE.equals(item.defaultLabel, true) || item.defaultLabel?.contains('-', true) ?: false }.sortedBy { item -> item.defaultLabel }
                                            populateBeneficiaries(policyDetail)
                                            riskViewModel.fetchPersonalInformation()
                                        }
                                    }
                                }
                            }
                        })
                        sharedViewModel.getCIFCodes(CIFGroupCode.SOURCE_OF_FUNDS)
                    }
                    !sourceOfFund.isNullOrEmpty() -> {
                        manageBeneficiaryViewModel.policyInfo.sourceOfFunds = InsuranceBeneficiaryHelper.getMatchingLookupItem(sourceOfFund, manageBeneficiaryViewModel.sourceOfFundList)
                    }
                }
                if (manageBeneficiaryViewModel.titles.isNotEmpty() || manageBeneficiaryViewModel.exergyTitles.isNotEmpty()) {
                    populateBeneficiaries(policyDetail)
                }
                policyDetail.policy?.number?.let { policyNumber ->
                    manageBeneficiaryViewModel.policyInfo.policyNumber = policyNumber
                }
            }
        })

        riskViewModel.personalInformationResponse.observe(viewLifecycleOwner, { personalInformation ->
            dismissProgressDialog()
            personalInformation?.customerInformation?.let { customerInformation ->
                manageBeneficiaryViewModel.customerInformation = customerInformation
                manageBeneficiaryViewModel.customerInformation.preferredContactMethod?.let {
                    manageBeneficiaryViewModel.policyInfo.preferredCommunication = it
                }
            }
        })

        addBeneficiaryOptionActionButtonView.setOnClickListener {
            manageBeneficiaryViewModel.policyBeneficiaryInfo = PolicyBeneficiaryInfo()
            if (manageBeneficiaryViewModel.isExergyPolicy) {
                manageBeneficiaryViewModel.beneficiaryAction = when {
                    manageBeneficiaryViewModel.policyBeneficiaries.isNotEmpty() && manageBeneficiaryViewModel.policyBeneficiaries.size == manageBeneficiaryViewModel.fetchPolicyDetails().value?.maximumAllowedBeneficiaries -> {
                        BeneficiaryAction.EDIT
                    }
                    else -> {
                        BeneficiaryAction.ADD
                    }
                }
                exergyByCodeTypesViewModel.fetchExergyCodesByType(RELATIONSHIP)
                exergyByCodeTypesViewModel.exergyCodesByTypeLiveData.observe(viewLifecycleOwner, {
                    manageBeneficiaryViewModel.exergyRelationships = it.exergyCodesLookupList
                    dismissProgressDialog()
                    navigate(AddBeneficiaryFragmentDirections.addBeneficiaryFragmentToBeneficiaryDetailsFragment())
                })
            } else {
                manageBeneficiaryViewModel.beneficiaryAction = BeneficiaryAction.ADD
                navigate(AddBeneficiaryFragmentDirections.addBeneficiaryFragmentToBeneficiaryDetailsFragment())
            }
            trackAction("Insurance_Hub", "${policyType}_ManageBeneficiariesScreen_AddBeneficiaryButtonClicked")
        }
    }

    private fun attachExergyByCodeTypesObserver(policyDetail: PolicyDetail) {
        exergyByCodeTypesViewModel.exergyCodesByTypeLiveData.observe(viewLifecycleOwner, {
            manageBeneficiaryViewModel.exergyTitles = it.exergyCodesLookupList.toMutableList().filterNot { item -> ESTATE_LATE.equals(item.description, true) }.sortedBy { item -> item.description }

            populateBeneficiaries(policyDetail)
            riskViewModel.fetchPersonalInformation()
            exergyByCodeTypesViewModel.exergyCodesByTypeLiveData.removeObservers(this)
        })
    }

    private fun populateBeneficiaries(policyDetail: PolicyDetail) {
        policyDetail.policyBeneficiaries.let { policyBeneficiaries ->
            when {
                manageBeneficiaryViewModel.hasEstateBeneficiary() || policyBeneficiaries.isEmpty() -> {
                    addBeneficiaryOptionActionButtonView.visibility = View.VISIBLE
                    beneficiaryRecyclerView.visibility = View.GONE
                    errorTextView.visibility = View.VISIBLE
                    removeMenuItem?.isVisible = false
                }
                (policyBeneficiaries.size >= policyDetail.maximumAllowedBeneficiaries && policyBeneficiaries.size != 1) -> {
                    addBeneficiaryOptionActionButtonView.visibility = View.GONE
                    enableRecyclerView(policyBeneficiaries)
                    removeMenuItem?.isVisible = true
                }
                (policyBeneficiaries.size >= policyDetail.maximumAllowedBeneficiaries && policyBeneficiaries.size == 1) -> {
                    addBeneficiaryOptionActionButtonView.visibility = View.GONE
                    enableRecyclerView(policyBeneficiaries)
                    removeMenuItem?.isVisible = false
                }
                (policyBeneficiaries.size < policyDetail.maximumAllowedBeneficiaries && policyBeneficiaries.size == 1) -> {
                    addBeneficiaryOptionActionButtonView.visibility = View.VISIBLE
                    enableRecyclerView(policyBeneficiaries)
                    removeMenuItem?.isVisible = false
                }
                else -> {
                    addBeneficiaryOptionActionButtonView.visibility = View.VISIBLE
                    enableRecyclerView(policyBeneficiaries)
                    removeMenuItem?.isVisible = true
                }
            }
        }
    }

    private fun enableRecyclerView(policyBeneficiaries: List<PolicyBeneficiary>) {
        adapter = BeneficiaryAdapter(InsuranceBeneficiaryHelper.sortPolicyBeneficiaries(policyBeneficiaries))
        errorTextView.visibility = View.GONE
        beneficiaryRecyclerView.visibility = View.VISIBLE
        beneficiaryRecyclerView.adapter = adapter
    }

    inner class BeneficiaryAdapter(private val policyBeneficiaries: List<PolicyBeneficiary>) : RecyclerView.Adapter<BeneficiaryAdapter.BeneficiaryViewHolder>() {

        var isDeleteEnabled: Boolean = false
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeneficiaryViewHolder {
            val binding = DataBindingUtil.inflate<ManageBeneficiariesListItemBinding>(LayoutInflater.from(parent.context), R.layout.manage_beneficiaries_list_item, parent, false)
            return BeneficiaryViewHolder(binding)
        }

        override fun onBindViewHolder(holder: BeneficiaryViewHolder, position: Int) {
            holder.onBind(policyBeneficiaries[position])
        }

        override fun getItemCount(): Int = policyBeneficiaries.size

        inner class BeneficiaryViewHolder(private val binding: ManageBeneficiariesListItemBinding) : RecyclerView.ViewHolder(binding.root) {
            fun onBind(policyBeneficiary: PolicyBeneficiary) {
                binding.apply {
                    val allocation = policyBeneficiary.allocation

                    val titleDescription = if (manageBeneficiaryViewModel.isExergyPolicy) {
                        val exergyTitleDetails = InsuranceBeneficiaryHelper.getMatchingExergyCodesDetails(policyBeneficiary.title, manageBeneficiaryViewModel.exergyTitles)
                        exergyTitleDetails.description
                    } else {
                        val lookupItem = InsuranceBeneficiaryHelper.getMatchingLookupItem(policyBeneficiary.title, manageBeneficiaryViewModel.titles)
                        lookupItem?.defaultLabel ?: ""
                    }

                    val fullName = "${titleDescription.toTitleCase()} ${policyBeneficiary.firstName} ${policyBeneficiary.surname}"
                    beneficiaryView.setBeneficiary(BeneficiaryListItem(fullName, getString(R.string.manage_policy_beneficiaries_percentage_allocation, allocation), null))
                    beneficiaryView.setOnClickListener {
                        with(manageBeneficiaryViewModel) {
                            beneficiaryAction = BeneficiaryAction.EDIT
                            itemPosition = adapterPosition

                            if (isExergyPolicy && exergyRelationships.isEmpty()) {
                                exergyByCodeTypesViewModel.fetchExergyCodesByType(RELATIONSHIP)
                                exergyByCodeTypesViewModel.exergyCodesByTypeLiveData.observe(viewLifecycleOwner, {
                                    exergyRelationships = it.exergyCodesLookupList
                                    setData(policyBeneficiaries[adapterPosition], titleDescription, policyBeneficiary.title)
                                    dismissProgressDialog()
                                    navigate(AddBeneficiaryFragmentDirections.addBeneficiaryFragmentToBeneficiaryDetailsFragment())
                                })
                            } else {
                                setData(policyBeneficiaries[adapterPosition], titleDescription, policyBeneficiary.title)
                                navigate(AddBeneficiaryFragmentDirections.addBeneficiaryFragmentToBeneficiaryDetailsFragment())
                            }
                        }
                    }

                    deleteImageButton.setOnClickListener {
                        manageBeneficiaryViewModel.itemPosition = adapterPosition
                        manageBeneficiaryViewModel.setData(policyBeneficiaries[adapterPosition], titleDescription, policyBeneficiary.title)
                        showDeleteConfirmation()
                    }
                    if (isDeleteEnabled) {
                        deleteImageButton.visibility = View.VISIBLE
                    } else {
                        deleteImageButton.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun showDeleteConfirmation() {
        val policyBeneficiaryInfo = manageBeneficiaryViewModel.policyBeneficiaryInfo
        val message = if (manageBeneficiaryViewModel.policyBeneficiaries.size == 1) {
            getString(R.string.manage_policy_one_beneficiary_delete_warning, "${policyBeneficiaryInfo.title.first.toTitleCase()} ${policyBeneficiaryInfo.firstName} ${policyBeneficiaryInfo.surname}")
        } else {
            getString(R.string.delete_warning, "${policyBeneficiaryInfo.title.first.toTitleCase()} ${policyBeneficiaryInfo.firstName} ${policyBeneficiaryInfo.surname}")
        }

        showAlertDialog(AlertDialogProperties.Builder()
                .title(getString(R.string.manage_policy_beneficiaries_remove_beneficiary))
                .message(message)
                .positiveButton(getString(R.string.yes))
                .positiveDismissListener { _, _ ->
                    manageBeneficiaryViewModel.beneficiaryAction = BeneficiaryAction.REMOVE
                    if (manageBeneficiaryViewModel.policyBeneficiaries.size == 1) {
                        navigate(AddBeneficiaryFragmentDirections.actionAddBeneficiaryFragmentToBeneficiaryConfirmationFragment())
                    } else {
                        navigate(AddBeneficiaryFragmentDirections.addBeneficiaryFragmentToBeneficiaryAllocationFragment())
                    }
                    trackAction("Insurance_Hub", "${policyType}_ManageBeneficiariesScreen_RemoveBeneficiaryButtonClicked")
                }
                .negativeButton(getString(R.string.cancel))
                .build())
    }
}