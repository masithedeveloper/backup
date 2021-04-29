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
import com.barclays.absa.banking.databinding.UltimateProtectorStepThreeFragmentBinding
import com.barclays.absa.banking.framework.AbsaBaseFragment
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.viewModel

class UltimateProtectorStepThreeFragment : AbsaBaseFragment<UltimateProtectorStepThreeFragmentBinding>() {

    private lateinit var ultimateProtectorViewModel: UltimateProtectorViewModel
    private lateinit var hostActivity: UltimateProtectorHostActivity

    override fun getLayoutResourceId(): Int = R.layout.ultimate_protector_step_three_fragment

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as UltimateProtectorHostActivity
        ultimateProtectorViewModel = hostActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hostActivity.setStep(3)
        populateRadioButtonView()
        attachViewCallbacks()
        restoreViewStates()
        AnalyticsUtil.trackAction(UltimateProtectorHostActivity.ULTIMATE_PROTECTOR_CHANNEL, "WIMI_Life_UP_Step3")
    }

    private fun populateRadioButtonView() {
        val selectorList = ultimateProtectorViewModel.buildOptionsSelectorList(getString(R.string.yes), getString(R.string.no))
        binding.stepThreeAnswerRadioButtonView.setDataSource(selectorList)
    }

    private fun attachViewCallbacks() {
        val ultimateProtectorInfo = ultimateProtectorViewModel.ultimateProtectorInfo

        binding.stepThreeNextButton.setOnClickListener {
            navigate(UltimateProtectorStepThreeFragmentDirections.actionUltimateProtectorStepThreeFragmentToUltimateProtectorStepFourFragment())
            ultimateProtectorViewModel.coverQuotationLiveData.value = null
        }

        binding.stepThreeAnswerRadioButtonView.setItemCheckedInterface {
            ultimateProtectorInfo.medicalQuestionThree = it > -1 && it == 0
            enablePrimaryActionButton()
        }

        binding.stepThreeDeclarationCheckBoxView.setOnCheckedListener {
            ultimateProtectorInfo.isStepThreeDeclarationAccepted = it
            enablePrimaryActionButton()
        }
    }

    private fun restoreViewStates() {
        val ultimateProtectorInfo = ultimateProtectorViewModel.ultimateProtectorInfo
        val medicalQuestionThree = ultimateProtectorInfo.medicalQuestionThree
        val isDeclarationAccepted = ultimateProtectorInfo.isStepThreeDeclarationAccepted

        medicalQuestionThree?.let {
            binding.stepThreeAnswerRadioButtonView.selectedIndex = if (it) 0 else 1
        }

        isDeclarationAccepted?.let {
            binding.stepThreeDeclarationCheckBoxView.isChecked = it
        }
    }

    private fun enablePrimaryActionButton() {
        val ultimateProtectorInfo = ultimateProtectorViewModel.ultimateProtectorInfo
        val medicalQuestionThree = ultimateProtectorInfo.medicalQuestionThree
        val isDeclarationChecked = ultimateProtectorInfo.isStepThreeDeclarationAccepted
        binding.stepThreeNextButton.isEnabled = medicalQuestionThree != null && (isDeclarationChecked != null && isDeclarationChecked)
    }
}