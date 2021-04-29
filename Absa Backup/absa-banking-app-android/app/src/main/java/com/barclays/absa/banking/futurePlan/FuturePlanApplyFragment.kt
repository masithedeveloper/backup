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
 *
 */

package com.barclays.absa.banking.futurePlan

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.FuturePlanApplyFragmentBinding
import com.barclays.absa.banking.express.shared.getSavedApplications.dto.SavedApplication
import com.barclays.absa.banking.futurePlan.FuturePlanActivity.Companion.FUTURE_PLAN
import com.barclays.absa.banking.saveAndInvest.SaveAndInvestApplyFragment
import com.barclays.absa.banking.shared.BottomSheetOptionsFragment
import com.barclays.absa.banking.shared.TermsAndConditionsInfo
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.utils.extensions.takeLeadingNumbersOrZero

class FuturePlanApplyFragment : SaveAndInvestApplyFragment(R.layout.future_plan_apply_fragment) {

    private val binding by viewBinding(FuturePlanApplyFragmentBinding::bind)

    override var productName: String = FUTURE_PLAN

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveAndInvestActivity.hideProgressIndicatorAndToolbar()
        with(binding.toolbar.toolbar) {
            title = getString(R.string.future_plan_title)
            setNavigationIcon(R.drawable.ic_arrow_back_white)
            setNavigationOnClickListener {
                saveAndInvestActivity.onBackPressed()
            }
        }

        /*TODO uncomment this code segment when save application feature is ready in the next release cycle
        lastSavedApplicationMutableLiveData.observe(viewLifecycleOwner, { savedApplication ->
            if (savedApplication == null) {
                navigate(FuturePlanApplyFragmentDirections.actionFuturePlanApplyFragmentToFuturePlanPersonalInformationFragment())
            } else {
                showOptionsDialog(savedApplication)
            }
        })*/

        setupClickListeners()
    }

    override fun onProductDetailsReturned() {
        with(saveAndInvestViewModel.saveAndInvestProductInfo) {
            binding.initialMinimumTextView.text = boldAndFormatAmount(R.string.future_plan_apply_initial_minimum_amount, minimumInvestmentAmount.toString())
            binding.termTextView.text = getString(R.string.future_plan_apply_term, minimumInvestmentPeriod.takeLeadingNumbersOrZero())
        }
    }

    override fun navigateOnCIFHold() {
        navigate(FuturePlanApplyFragmentDirections.actionFuturePlanApplyFragmentToGenericResultScreenFragment(getCIFHoldResultProperties()))
    }

    override fun navigateToGenericResultFragment() {
        navigate(FuturePlanApplyFragmentDirections.actionFuturePlanApplyFragmentToGenericResultScreenFragment(getFailureScreenProperties()))
    }

    override fun navigateToPersonalInformation() {
        navigate(FuturePlanApplyFragmentDirections.actionFuturePlanApplyFragmentToFuturePlanPersonalInformationFragment())
    }

    private fun setupClickListeners() {
        binding.findOutMoreOptionActionButtonView.setOnClickListener {
            saveAndInvestActivity.hideProgressIndicatorAndToolbar()
            navigate(FuturePlanApplyFragmentDirections.actionFuturePlanApplyFragmentToFuturePlanTermsAndConditionsFragment(TermsAndConditionsInfo(shouldDisplayCheckBox = false, productCode = saveAndInvestActivity.productType.productCode)))
        }
        binding.applyNowButton.setOnClickListener {
            trackAnalyticsAction("InformationScreen_ApplyButtonClicked")
            fetchCustomerDetails()
        }
    }

    private fun showOptionsDialog(savedApplication: SavedApplication) {
        with(BottomSheetOptionsFragment.newInstance(getString(R.string.future_plan_application), getString(R.string.future_plan_saved_application), getString(R.string.future_plan_new_application))) {
            setOptionOneClickListener {
                trackAnalyticsAction("InformationScreen_ContinueWithSavedApplicationButtonClicked")
                saveAndInvestViewModel.savedApplication = savedApplication
                navigate(FuturePlanApplyFragmentDirections.actionFuturePlanApplyFragmentToFuturePlanFundYourAccountFragment())
            }
            setOptionTwoClickListener {
                trackAnalyticsAction("InformationScreen_StartANewApplicationButtonClicked")
                navigate(FuturePlanApplyFragmentDirections.actionFuturePlanApplyFragmentToFuturePlanPersonalInformationFragment())
            }
            show(saveAndInvestActivity.supportFragmentManager, FUTURE_PLAN)
        }
    }
}