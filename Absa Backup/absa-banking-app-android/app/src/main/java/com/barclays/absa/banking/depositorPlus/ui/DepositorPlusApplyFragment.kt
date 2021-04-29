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

package com.barclays.absa.banking.depositorPlus.ui

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.DepositorPlusApplyFragmentBinding
import com.barclays.absa.banking.depositorPlus.ui.DepositorPlusActivity.Companion.DEPOSITOR_PLUS
import com.barclays.absa.banking.express.shared.getSavedApplications.dto.SavedApplication
import com.barclays.absa.banking.saveAndInvest.SaveAndInvestApplyFragment
import com.barclays.absa.banking.shared.BottomSheetOptionsFragment
import com.barclays.absa.banking.shared.TermsAndConditionsInfo
import com.barclays.absa.utils.extensions.viewBinding

class DepositorPlusApplyFragment : SaveAndInvestApplyFragment(R.layout.depositor_plus_apply_fragment) {

    private val binding by viewBinding(DepositorPlusApplyFragmentBinding::bind)

    override var productName: String = DEPOSITOR_PLUS

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveAndInvestActivity.hideProgressIndicatorAndToolbar()
        with(binding.toolbar.toolbar) {
            title = getString(R.string.depositor_plus_title)
            setNavigationIcon(R.drawable.ic_arrow_back_white)
            setNavigationOnClickListener {
                saveAndInvestActivity.onBackPressed()
            }
        }

        /*TODO uncomment this code segment when save application feature is ready in the next release cycle
        lastSavedApplicationMutableLiveData.observe(viewLifecycleOwner, { savedApplication ->
            if (savedApplication == null) {
                navigate(DepositorPlusApplyFragmentDirections.actionDepositorPlusApplyFragmentToDepositorPlusPersonalInformationFragment())
            } else {
                showOptionsDialog(savedApplication)
            }
        })*/

        setupClickListeners()
    }

    override fun onProductDetailsReturned() {
        binding.initialAmountTextView.text = boldAndFormatAmount(R.string.depositor_plus_initial_minimum_amount, saveAndInvestViewModel.saveAndInvestProductInfo.minimumInvestmentAmount.toString())
    }

    override fun navigateOnCIFHold() {
        navigate(DepositorPlusApplyFragmentDirections.actionDepositorPlusApplyFragmentToDepositorPlusGenericResultScreenFragment(getCIFHoldResultProperties()))
    }

    override fun navigateToGenericResultFragment() {
        navigate(DepositorPlusApplyFragmentDirections.actionDepositorPlusApplyFragmentToDepositorPlusGenericResultScreenFragment(getFailureScreenProperties()))
    }

    override fun navigateToPersonalInformation() {
        navigate(DepositorPlusApplyFragmentDirections.actionDepositorPlusApplyFragmentToDepositorPlusPersonalInformationFragment())
    }

    private fun showOptionsDialog(savedApplication: SavedApplication) {
        with(BottomSheetOptionsFragment.newInstance(getString(R.string.depositor_plus_application), getString(R.string.depositor_plus_saved_application), getString(R.string.depositor_plus_new_application))) {
            setOptionOneClickListener {
                saveAndInvestViewModel.savedApplication = savedApplication
                navigate(DepositorPlusApplyFragmentDirections.actionDepositorPlusApplyFragmentToDepositorPlusFundYourAccountFragment())
            }
            setOptionTwoClickListener {
                navigate(DepositorPlusApplyFragmentDirections.actionDepositorPlusApplyFragmentToDepositorPlusPersonalInformationFragment())
            }
            show(saveAndInvestActivity.supportFragmentManager, DEPOSITOR_PLUS)
        }
    }

    private fun setupClickListeners() {
        binding.findOutMoreOptionActionButtonView.setOnClickListener {
            with(TermsAndConditionsInfo(shouldDisplayCheckBox = false, productCode = saveAndInvestActivity.productType.productCode)) {
                saveAndInvestActivity.showToolbar()
                navigate(DepositorPlusApplyFragmentDirections.actionDepositorPlusApplyFragmentToTermsAndConditionsFragment(this))
            }
        }

        binding.applyNowButton.setOnClickListener {
            trackAnalyticsAction("InformationScreen_ApplyButtonClicked")
            fetchCustomerDetails()
        }
    }
}