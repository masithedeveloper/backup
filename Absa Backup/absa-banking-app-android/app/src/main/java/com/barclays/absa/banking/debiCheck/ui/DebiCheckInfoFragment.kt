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

package com.barclays.absa.banking.debiCheck.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.debi_check_important_info_fragment.*

class DebiCheckInfoFragment : BaseFragment(R.layout.debi_check_important_info_fragment) {

    private lateinit var viewModel: DebiCheckViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = (context as DebiCheckHostActivity).viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseActivity.setToolBarBack(if (viewModel.currentFlow == DebiCheckViewModel.Flow.APPROVED) R.string.debicheck_suspend_title else R.string.debicheck_dispute_title)
        if (viewModel.currentFlow == DebiCheckViewModel.Flow.APPROVED) initSuspendInfoContent() else initDisputeInfoContent()
    }

    override fun onResume() {
        super.onResume()
        viewModel.disputeReasonCode = ""
        viewModel.suspendReasonCode = ""
    }

    private fun initDisputeInfoContent() {
        importantInfoTitle.setText(R.string.debicheck_dispute_info_title)
        disputeInfoArrearsBulletItemView.setContentTextView(getString(R.string.debicheck_dispute_info_arrears))
        disputeInfoCreditScoreBulletItemView.setContentTextView(getString(R.string.debicheck_dispute_info_credit_score))
        disputeInfoArrearsCorrectlyMatchesItemView.setContentTextView(getString(R.string.debicheck_dispute_info_correctly_matches))
        nextButton.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_debiCheckDisputeInfoFragment_to_debiCheckReasonForDisputeFragment)
        }
    }

    private fun initSuspendInfoContent() {
        importantInfoTitle.setText(R.string.debicheck_suspend_info_title)
        disputeInfoArrearsBulletItemView.setContentTextView(getString(R.string.debicheck_suspend_info_credit_score))
        disputeInfoCreditScoreBulletItemView.setContentTextView(getString(R.string.debicheck_suspend_info_inform_party))
        disputeInfoArrearsCorrectlyMatchesItemView.setContentTextView(getString(R.string.debicheck_suspend_info_no_claim))
        suspendInfoFullResponsibilityItemView.visibility = View.VISIBLE
        suspendInfoFullResponsibilityItemView.setContentTextView(getString(R.string.debicheck_suspend_info_full_responsibility))
        nextButton.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_DebiCheckSuspendInfoFragment_to_DebiCheckReasonForSuspendFragment)
        }
    }
}