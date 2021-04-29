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
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.debiCheck.services.dto.DisputeReason
import com.barclays.absa.banking.debiCheck.services.dto.RejectReason
import com.barclays.absa.banking.debiCheck.services.dto.SuspendReason
import com.barclays.absa.banking.debiCheck.ui.DebiCheckViewModel.Flow.APPROVED
import com.barclays.absa.banking.debiCheck.ui.DebiCheckViewModel.Flow.TRANSACTIONS
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.debi_check_reason_for_dispute_fragment.*
import styleguide.forms.ItemCheckedInterface
import styleguide.forms.SelectorList
import styleguide.forms.StringItem

class DebiCheckReasonsFragment : BaseFragment(R.layout.debi_check_reason_for_dispute_fragment) {

    private lateinit var viewModel: DebiCheckViewModel
    private lateinit var sureCheckDelegate: SureCheckDelegate
    private lateinit var hostActivity: DebiCheckHostActivity
    private val reasonList = SelectorList<StringItem>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as DebiCheckHostActivity
        viewModel = hostActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (viewModel.currentFlow) {
            APPROVED -> initContentForSuspend()
            TRANSACTIONS -> initContentForDispute()
            else -> initContentForReject()
        }
        attachDataObserver()
        setupSureCheckDelegate()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.disputeReasonCode.isNotEmpty()) {
            disclaimerMessageTextView.visibility = View.VISIBLE
            disclaimerMessageCheckBoxView.visibility = View.VISIBLE
        }
    }

    private fun attachDataObserver() {
        viewModel.debiCheckSuspendResponse.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            hostActivity.showMandateSuspendResultScreen(it.responseDTO.success)
            viewModel.debiCheckSuspendResponse.removeObservers(this)
        })

        viewModel.debitOrderRejectResponse = MutableLiveData()
        viewModel.debitOrderRejectResponse.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            sureCheckDelegate.processSureCheck(baseActivity, it) { hostActivity.showRejectedResultScreen() }
        })
    }

    private fun initContentForReject() {
        setToolBar(R.string.debicheck_reason_for_rejection_title)

        reasonList.apply {
            add(StringItem(RejectReason.noneOfTheseReasons.second))
            add(StringItem(RejectReason.unknownCreditor.second))
            add(StringItem(RejectReason.incorrectDay.second))
        }

        val onItemChecked = ItemCheckedInterface { index ->
            if (index > -1) {
                reasonList[index].item?.let {
                    viewModel.rejectionReasonCode = RejectReason.codeFromReason(it)
                }
                disputeReasonNextButton.isEnabled = true
            } else {
                disputeReasonNextButton.isEnabled = false
            }
        }

        disputeReasonNormalInputView.visibility = View.GONE
        disputeReasonNextButton.apply {
            setText(R.string.reject)
            setOnClickListener {
                viewModel.rejectDebitOrder()
                hostActivity.tagDebiCheckEvent("ReasonForRejection_RejectButtonClicked")
            }
        }

        disputeRejectReasonsRadioButtonView.apply {
            visibility = View.VISIBLE
            setDataSource(reasonList)
            setItemCheckedInterface(onItemChecked)
        }
    }

    private fun initContentForDispute() {
        setToolBar(R.string.debicheck_dispute_title)

        apply {
            disputeReasonNormalInputView.titleTextView?.setText(R.string.debicheck_reason_for_dispute)
            disputeReasonNormalInputView.setHintText(R.string.debicheck_select_reason_for_dispute)
            disclaimerMessageTextView.setText(R.string.debicheck_dispute_disclaimer)
        }
        reasonList.apply {
            clear()
            add(StringItem(DisputeReason.incorrectDebitDay.second))
            add(StringItem(DisputeReason.incorrectAmount.second))
            add(StringItem(DisputeReason.didNotAgree.second))
        }

        disputeReasonNormalInputView.setList(reasonList, getString(R.string.debicheck_reason_for_dispute))
        disputeReasonNormalInputView.setItemSelectionInterface { index ->
            if (disputeReasonNormalInputView.selectedIndex > -1) {
                disclaimerMessageTextView.visibility = View.VISIBLE
                disclaimerMessageCheckBoxView.visibility = View.VISIBLE
                viewModel.disputeReasonCode = DisputeReason.codeFromReason(reasonList[index].item!!)
            }
        }

        disclaimerMessageCheckBoxView.setOnCheckedListener {
            disputeReasonNextButton.isEnabled = it
        }

        disputeReasonNextButton.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_debiCheckReasonForDisputeFragment_to_debiCheckConfirmDisputeFragment)
        }
    }

    private fun initContentForSuspend() {
        setToolBar(R.string.debicheck_suspend_title)

        apply {
            disputeReasonNormalInputView.titleTextView?.setText(R.string.debicheck_reason_for_suspending)
            disputeReasonNormalInputView.setHintText(R.string.debicheck_select_reason_for_suspend)
            disclaimerMessageTextView.setText(R.string.debicheck_suspend_reason_disclaimer)
            disputeReasonNextButton.setText(R.string.debicheck_suspend_title)
        }

        reasonList.apply {
            clear()
            add(StringItem(SuspendReason.cancellingMandate.second))
            add(StringItem(SuspendReason.mandateEnded.second))
            add(StringItem(SuspendReason.mandateChanged.second))
            add(StringItem(SuspendReason.mandateNotAuthorised.second))
        }

        disputeReasonNormalInputView.setList(reasonList, getString(R.string.debicheck_reason_for_suspend))
        disputeReasonNormalInputView.setItemSelectionInterface { index ->
            if (disputeReasonNormalInputView.selectedIndex > -1) {
                disclaimerMessageTextView.visibility = View.VISIBLE
                disclaimerMessageCheckBoxView.visibility = View.VISIBLE
                viewModel.suspendReasonCode = SuspendReason.codeFromReason(reasonList[index].item!!)
            }
        }

        disclaimerMessageCheckBoxView.setOnCheckedListener {
            disputeReasonNextButton.isEnabled = it
        }

        disputeReasonNextButton.setOnClickListener {
            hostActivity.tagDebiCheckEvent("SuspendMandate_SuspendButtonClicked")
            viewModel.suspendMandate()
        }
    }

    private fun setupSureCheckDelegate() {
        sureCheckDelegate = object : SureCheckDelegate(context) {
            override fun onSureCheckProcessed() {
                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel.rejectDebitOrder(false)
                }, DebiCheckContracts.DEFAULT_DELAY.toLong())
            }

            override fun onSureCheckRejected() = hostActivity.showSureCheckRejectedScreen()
        }
    }
}