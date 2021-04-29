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
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.UltimateProtectorStepTwoFragmentBinding
import com.barclays.absa.banking.framework.AbsaBaseFragment
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.viewModel

class UltimateProtectorStepTwoFragment : AbsaBaseFragment<UltimateProtectorStepTwoFragmentBinding>() {

    private lateinit var ultimateProtectorViewModel: UltimateProtectorViewModel
    private lateinit var hostActivity: UltimateProtectorHostActivity

    override fun getLayoutResourceId(): Int = R.layout.ultimate_protector_step_two_fragment

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as UltimateProtectorHostActivity
        ultimateProtectorViewModel = hostActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hostActivity.setStep(2)
        populateRadioButtonView()
        attachViewCallbacks()
        restoreRadioButtonViewState()
        AnalyticsUtil.trackAction(UltimateProtectorHostActivity.ULTIMATE_PROTECTOR_CHANNEL, "WIMI_Life_UP_Step2")
    }

    private fun populateRadioButtonView() {
        val selectorList = ultimateProtectorViewModel.buildOptionsSelectorList(getString(R.string.yes), getString(R.string.no))
        binding.stepTwoAnswerRadioButtonView.setDataSource(selectorList)
    }

    private fun attachViewCallbacks() {
        binding.stepTwoAnswerRadioButtonView.setItemCheckedInterface { position ->
            binding.stepTwoNextButton.isEnabled = position > -1
            ultimateProtectorViewModel.ultimateProtectorInfo.medicalQuestionTwo = position > -1 && position == 0
            ultimateProtectorViewModel.coverQuotationLiveData.value = null
        }

        binding.stepTwoNextButton.setOnClickListener {
            navigate(UltimateProtectorStepTwoFragmentDirections.actionUltimateProtectorStepTwoFragmentToUltimateProtectorStepThreeFragment())
        }
    }

    private fun restoreRadioButtonViewState() {
        val medicalQuestionTwo = ultimateProtectorViewModel.ultimateProtectorInfo.medicalQuestionTwo
        medicalQuestionTwo?.let {
            binding.stepTwoAnswerRadioButtonView.selectedIndex = if (it) 0 else 1
        }
    }
}