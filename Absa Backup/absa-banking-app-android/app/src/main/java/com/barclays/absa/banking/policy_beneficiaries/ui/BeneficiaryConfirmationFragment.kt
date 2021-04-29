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
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.policy.PolicyBeneficiary
import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.barclays.absa.banking.databinding.ManageBeneficiaryLabelViewItemBinding
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.funeralCover.ui.InsurancePolicyClaimsBaseActivity
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.banking.ultimateProtector.services.dto.CallType
import com.barclays.absa.utils.AnalyticsUtil.trackAction
import com.barclays.absa.utils.DateUtils
import kotlinx.android.synthetic.main.manage_beneficiaries_confirmation_fragment.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toTitleCase

class BeneficiaryConfirmationFragment : ManageBeneficiaryBaseFragment(R.layout.manage_beneficiaries_confirmation_fragment) {

    private lateinit var sureCheckDelegate: SureCheckDelegate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trackAction("Insurance_Hub", "${InsurancePolicyClaimsBaseActivity.policyType}_ConfirmationScreen_ScreenDisplayed")

        setToolBar(getString(R.string.manage_policy_beneficiaries_confirmation))
        manageBeneficiaryActivity.setStep(4)
        initializeSurecheckDelegate()

        val policyBeneficiaryInfo = manageBeneficiaryViewModel.policyBeneficiaryInfo
        val policyInfo = manageBeneficiaryViewModel.policyInfo

        when (manageBeneficiaryViewModel.beneficiaryAction) {
            BeneficiaryAction.REMOVE -> {

                if (manageBeneficiaryViewModel.policyBeneficiaries.size == 1) {
                    estateLateDescriptionTextView.visibility = View.VISIBLE
                }
                beneficiaryNameLabelView.visibility = View.GONE
                relationshipLabelView.visibility = View.GONE
                sourceOfFundsLabelView.visibility = View.GONE
                dateOfBirthLabelView.visibility = View.GONE
                identificationTypeLabelView.visibility = View.GONE
                identificationNumberLabelView.visibility = View.GONE
                addressLabelView.visibility = View.GONE
                contactNumberLabelView.visibility = View.GONE
                emailAddressLabelView.visibility = View.GONE
                divider.visibility = View.GONE
            }
            else -> {
                beneficiaryNameLabelView.setContentText("${policyBeneficiaryInfo.title.first.toTitleCase()} ${policyBeneficiaryInfo.firstName} ${policyBeneficiaryInfo.surname}")
                relationshipLabelView.setContentText(policyBeneficiaryInfo.relationship?.first)

                if (manageBeneficiaryViewModel.sourceOfFunds == null) {
                    sourceOfFundsLabelView.setContentText(policyInfo.sourceOfFunds?.displayValue)
                } else {
                    sourceOfFundsLabelView.visibility = View.GONE
                }

                dateOfBirthLabelView.setContentText(DateUtils.formatDate(policyBeneficiaryInfo.dateOfBirth, DateUtils.DASHED_DATE_PATTERN, DateUtils.DATE_DISPLAY_PATTERN))

                if (policyBeneficiaryInfo.idType?.first.isNullOrEmpty()) {
                    identificationTypeLabelView.visibility = View.GONE
                } else {
                    identificationTypeLabelView.setContentText(policyBeneficiaryInfo.idType?.first)
                }

                if (policyBeneficiaryInfo.idNumber.isEmpty()) {
                    identificationNumberLabelView.visibility = View.GONE
                } else {
                    identificationNumberLabelView.setContentText(policyBeneficiaryInfo.idNumber)
                }

                addressLabelView.setContentText(manageBeneficiaryViewModel.buildAddress().toTitleCase())

                if (policyBeneficiaryInfo.cellphoneNumber.isEmpty()) {
                    contactNumberLabelView.visibility = View.GONE
                } else {
                    contactNumberLabelView.setContentText(policyBeneficiaryInfo.cellphoneNumber)
                }

                if (policyBeneficiaryInfo.emailAddress.isEmpty()) {
                    emailAddressLabelView.visibility = View.GONE
                } else {
                    emailAddressLabelView.setContentText(policyBeneficiaryInfo.emailAddress)
                }
            }
        }
        if (manageBeneficiaryViewModel.policyBeneficiaries.size > 1) {
            beneficiariesRecyclerView.layoutManager = object : LinearLayoutManager(context) {
                override fun canScrollVertically() = false
            }
            beneficiariesRecyclerView.setHasFixedSize(true)
            beneficiariesRecyclerView.adapter = PolicyBeneficiaryConfirmationAdapter(manageBeneficiaryViewModel.beneficiaries)
        }

        confirmButton.setOnClickListener { performBeneficiaryManageAction() }
    }

    private fun performBeneficiaryManageAction() {
        when (manageBeneficiaryViewModel.beneficiaryAction) {
            BeneficiaryAction.ADD -> {
                if (manageBeneficiaryViewModel.isExergyPolicy) {
                    manageBeneficiaryViewModel.addExergyBeneficiaryRequest().observe(viewLifecycleOwner, {
                        startSurecheckVerification(it)
                    })
                } else if (manageBeneficiaryViewModel.hasEstateBeneficiary()) {
                    manageBeneficiaryViewModel.changeBeneficiary(CallType.SURECHECKV2_REQUIRED).observe(viewLifecycleOwner, {
                        startSurecheckVerification(it)
                    })
                } else {
                    manageBeneficiaryViewModel.addBeneficiary(CallType.SURECHECKV2_REQUIRED).observe(viewLifecycleOwner, {
                        startSurecheckVerification(it)
                    })
                }
            }
            BeneficiaryAction.EDIT -> {
                if (manageBeneficiaryViewModel.isExergyPolicy) {
                    manageBeneficiaryViewModel.editExergyBeneficiaryRequest().observe(viewLifecycleOwner, {
                        startSurecheckVerification(it)
                    })
                } else {
                    manageBeneficiaryViewModel.changeBeneficiary(CallType.SURECHECKV2_REQUIRED).observe(viewLifecycleOwner, {
                        startSurecheckVerification(it)
                    })
                }
            }
            BeneficiaryAction.REMOVE -> {
                if (manageBeneficiaryViewModel.isExergyPolicy) {
                    manageBeneficiaryViewModel.removeExergyBeneficiaryRequest().observe(viewLifecycleOwner, {
                        startSurecheckVerification(it)
                    })
                } else {
                    manageBeneficiaryViewModel.removeBeneficiary(CallType.SURECHECKV2_REQUIRED).observe(viewLifecycleOwner, {
                        startSurecheckVerification(it)
                    })
                }
            }
        }
    }

    private fun startSurecheckVerification(sureCheckResponse: SureCheckResponse?) {
        dismissProgressDialog()
        sureCheckResponse?.let {
            sureCheckDelegate.processSureCheck(manageBeneficiaryActivity, it) {
                if (it.transactionStatus.equals(BMBConstants.SUCCESS, true) && getString(R.string.exergy_timeout_message).equals(it.transactionMessage, true)) {
                    launchSuccessScreen()
                    trackAction("Insurance_Hub", "${InsurancePolicyClaimsBaseActivity.policyType}_${manageBeneficiaryViewModel.beneficiaryAction.name.toTitleCase()}BeneficiaryTimeoutScreen_ScreenDisplayed")
                } else if (it.transactionStatus.equals(BMBConstants.SUCCESS, true)) {
                    launchSuccessScreen()
                    trackAction("Insurance_Hub", "${InsurancePolicyClaimsBaseActivity.policyType}_${manageBeneficiaryViewModel.beneficiaryAction.name.toTitleCase()}BeneficiarySuccessScreen_ScreenDisplayed")
                } else {
                    launchFailureScreen()
                }
            }
        }
    }

    private fun initializeSurecheckDelegate() {
        val handler = Handler(Looper.getMainLooper())
        sureCheckDelegate = object : SureCheckDelegate(manageBeneficiaryActivity) {
            override fun onSureCheckProcessed() {
                handler.postDelayed({
                    when (manageBeneficiaryViewModel.beneficiaryAction) {
                        BeneficiaryAction.ADD -> {
                            if (manageBeneficiaryViewModel.isExergyPolicy) {
                                manageBeneficiaryViewModel.addExergyBeneficiaryRequest()
                            } else if (manageBeneficiaryViewModel.hasEstateBeneficiary()) {
                                manageBeneficiaryViewModel.changeBeneficiary(CallType.SURECHECKV2_PASSED)
                            } else {
                                manageBeneficiaryViewModel.addBeneficiary(CallType.SURECHECKV2_PASSED)
                            }
                        }
                        BeneficiaryAction.EDIT -> {
                            if (manageBeneficiaryViewModel.isExergyPolicy) {
                                manageBeneficiaryViewModel.editExergyBeneficiaryRequest()
                            } else {
                                manageBeneficiaryViewModel.changeBeneficiary(CallType.SURECHECKV2_PASSED)
                            }
                        }
                        BeneficiaryAction.REMOVE -> {
                            if (manageBeneficiaryViewModel.isExergyPolicy) {
                                manageBeneficiaryViewModel.removeExergyBeneficiaryRequest()
                            } else {
                                manageBeneficiaryViewModel.removeBeneficiary(CallType.SURECHECKV2_PASSED)
                            }
                        }
                    }
                }, 250)
            }

            override fun onSureCheckFailed() {
                launchFailureScreen()
            }
        }
    }

    private fun launchSuccessScreen() {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            manageBeneficiaryActivity.finish()
        }
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.succes_text))
                .setDescription(getString(R.string.manage_policy_beneficiaries_success_message))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(false)
        hideToolBar()
        manageBeneficiaryActivity.hideProgressIndicator()
        navigate(BeneficiaryConfirmationFragmentDirections.beneficiaryConfirmationFragmentToGenericResultScreenFragment(resultScreenProperties))
    }

    private fun launchFailureScreen() {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            manageBeneficiaryActivity.finish()
        }

        val title = if (manageBeneficiaryViewModel.beneficiaryAction == BeneficiaryAction.ADD || manageBeneficiaryViewModel.addNewExergyBeneficiary) {
            getString(R.string.manage_policy_beneficiaries_add_failure_message)
        } else {
            getString(R.string.manage_policy_beneficiaries_update_failure_message)
        }

        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(title)
                .setDescription(getString(R.string.manage_policy_beneficiaries_failure_instruction))
                .setPrimaryButtonLabel(getString(R.string.done))
                .setClickableText(getString(R.string.lifecover_contact))
                .build(false)
        hideToolBar()
        manageBeneficiaryActivity.hideProgressIndicator()
        navigate(BeneficiaryConfirmationFragmentDirections.beneficiaryConfirmationFragmentToGenericResultScreenFragment(resultScreenProperties))
        trackAction("Insurance_Hub", "${InsurancePolicyClaimsBaseActivity.policyType}_${manageBeneficiaryViewModel.beneficiaryAction.name.toTitleCase()}BeneficiaryFailureScreen_ScreenDisplayed")
    }

    inner class PolicyBeneficiaryConfirmationAdapter(private val policyBeneficiaries: List<PolicyBeneficiary>) : RecyclerView.Adapter<PolicyBeneficiaryConfirmationAdapter.PolicyBeneficiaryViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PolicyBeneficiaryViewHolder {
            val binding = DataBindingUtil.inflate<ManageBeneficiaryLabelViewItemBinding>(LayoutInflater.from(parent.context),
                    R.layout.manage_beneficiary_label_view_item, parent, false)
            return PolicyBeneficiaryViewHolder(binding)
        }

        override fun getItemCount() = policyBeneficiaries.size

        override fun onBindViewHolder(holder: PolicyBeneficiaryViewHolder, position: Int) {
            val policyBeneficiary = policyBeneficiaries[position]
            holder.binding.beneficiaryLabelViewItem.setLabelText(policyBeneficiary.fullName)
            when (manageBeneficiaryViewModel.beneficiaryAction) {
                BeneficiaryAction.REMOVE -> holder.binding.beneficiaryLabelViewItem.setContentText(getString(R.string.manage_policy_beneficiaries_percentage_allocation, policyBeneficiary.allocation))
                else -> holder.binding.beneficiaryLabelViewItem.setContentText(policyBeneficiary.allocation + "%")
            }
        }

        inner class PolicyBeneficiaryViewHolder(val binding: ManageBeneficiaryLabelViewItemBinding) : RecyclerView.ViewHolder(binding.root)
    }
}
