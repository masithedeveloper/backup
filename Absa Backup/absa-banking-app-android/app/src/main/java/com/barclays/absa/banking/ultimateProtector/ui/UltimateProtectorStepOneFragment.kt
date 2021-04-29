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

package com.barclays.absa.banking.ultimateProtector.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.UltimateProtectorStepOneFragmentBinding
import com.barclays.absa.banking.framework.AbsaBaseFragment
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.viewModel

class UltimateProtectorStepOneFragment : AbsaBaseFragment<UltimateProtectorStepOneFragmentBinding>() {

    private lateinit var ultimateProtectorViewModel: UltimateProtectorViewModel
    private lateinit var hostActivity: UltimateProtectorHostActivity

    override fun getLayoutResourceId(): Int = R.layout.ultimate_protector_step_one_fragment

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as UltimateProtectorHostActivity
        ultimateProtectorViewModel = hostActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        populateRadioButtonView()
        attachViewCallbacks()
        restoreRadioButtonViewState()
        AnalyticsUtil.trackAction(UltimateProtectorHostActivity.ULTIMATE_PROTECTOR_CHANNEL, "WIMI_Life_UP_Step1")
    }

    private fun setUpToolbar() {
        val hostActivity = activity as UltimateProtectorHostActivity
        hostActivity.showToolbar()
        hostActivity.setToolBar(getString(R.string.health_questions))
        hostActivity.showProgressIndicator()
        hostActivity.setStep(1)
        findNavController(this).addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.ultimateProtectorApplyFragment) {
                hostActivity.hideToolbar()
                hostActivity.hideProgressIndicator()
            }
        }
    }

    private fun populateRadioButtonView() {
        val selectorList = ultimateProtectorViewModel.buildOptionsSelectorList(getString(R.string.yes), getString(R.string.no))
        binding.stepOneAnswerRadioButtonView.setDataSource(selectorList)
    }

    private fun attachViewCallbacks() {
        binding.stepOneAnswerRadioButtonView.setItemCheckedInterface { position ->
            binding.stepOneNextButton.isEnabled = position > -1
            ultimateProtectorViewModel.ultimateProtectorInfo.medicalQuestionOne = position > -1 && position == 0
            ultimateProtectorViewModel.coverQuotationLiveData.value = null
        }

        binding.stepOneNextButton.setOnClickListener {
            navigate(UltimateProtectorStepOneFragmentDirections.actionUltimateProtectorStepOneFragmentToUltimateProtectorStepTwoFragment())
        }
    }

    private fun restoreRadioButtonViewState() {
        val medicalQuestionOne = ultimateProtectorViewModel.ultimateProtectorInfo.medicalQuestionOne
        medicalQuestionOne?.let {
            binding.stepOneAnswerRadioButtonView.selectedIndex = if (it) 0 else 1
        }
    }
}