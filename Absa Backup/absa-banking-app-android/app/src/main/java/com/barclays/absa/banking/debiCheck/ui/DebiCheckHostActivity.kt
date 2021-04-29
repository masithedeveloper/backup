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

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import com.barclays.absa.banking.R
import com.barclays.absa.banking.debiCheck.services.dto.DebiCheckMandateDetail
import com.barclays.absa.banking.debiCheck.services.dto.DisputeReason
import com.barclays.absa.banking.debiCheck.services.dto.RejectReason
import com.barclays.absa.banking.debiCheck.services.dto.SuspendReason
import com.barclays.absa.banking.debiCheck.ui.DebiCheckViewModel.Flow.*
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.viewModel
import styleguide.utils.extensions.toFormattedAmount

class DebiCheckHostActivity : BaseActivity() {

    companion object {
        const val CURRENT_FLOW = "CURRENT_FLOW"
        const val CURRENT_DEBIT_ORDER = "CURRENT_DEBIT_ORDER"
    }

    private lateinit var viewModel: DebiCheckViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debi_check_host_activity)
        setToolBarBack(getString(R.string.debicheck_debit_orders))
        DisputeReason.initializeReasons(this)
        RejectReason.initializeReasons(this)
        SuspendReason.initializeReasons(this)
        viewModel = viewModel()
        val debitOrder = intent.extras?.get(CURRENT_DEBIT_ORDER)
        debitOrder?.let {
            viewModel.selectedDebitOrder = (it as DebiCheckMandateDetail)
        }
        
        viewModel.networkUnreachableLiveData.observe(this, {
            dismissProgressDialog()
            checkDeviceState()
        })

        viewModel.failureResponse.observe(this, {
            dismissProgressDialog()
            showGenericErrorMessage()
        })

        initNavigationGraph()
    }

    private fun initNavigationGraph() {
        when (intent.getStringExtra(CURRENT_FLOW)) {
            SUSPENDED.name -> {
                viewModel.currentFlow = SUSPENDED
                swapNavGraph(R.navigation.debi_check_suspend_navigation, R.id.DebiCheckSuspendedFragment)
            }
            APPROVED.name -> {
                viewModel.currentFlow = APPROVED
                swapNavGraph(R.navigation.debi_check_suspend_navigation, R.id.DebiCheckApprovedFragment)
            }
            TRANSACTIONS.name -> {
                viewModel.currentFlow = TRANSACTIONS
                swapNavGraph(R.navigation.debi_check_transactions_navigation)
            }
            else -> {
                viewModel.currentFlow = PENDING
                swapNavGraph(R.navigation.debi_check_amend_new_navigation)
            }
        }
    }

    private fun swapNavGraph(navigationId: Int, destinationId: Int) {
        val navController = Navigation.findNavController(this, R.id.fragmentContainer)
        val navGraph = navController.navInflater.inflate(navigationId)
        navGraph.startDestination = destinationId
        navController.graph = navGraph
    }

    private fun swapNavGraph(navigationId: Int) {
        val navController = Navigation.findNavController(this, R.id.fragmentContainer)
        val navGraph = navController.navInflater.inflate(navigationId)
        navController.graph = navGraph
    }

    fun showTransactionDisputeResultScreen(isSuccess: Boolean) {
        val messageResId = if (isSuccess) R.string.debicheck_dispute_successful else R.string.debicheck_dispute_failed
        val subMessage = if (isSuccess) getString(R.string.debicheck_dispute_success_sub_message, viewModel.selectedTransaction?.collectionAmount.toFormattedAmount()) else getString(R.string.debicheck_dispute_rejected_sub_message)
        startActivity(buildResultIntent(messageResId, isSuccess, subMessage))
    }

    fun showMandateSuspendResultScreen(isSuccess: Boolean) {
        val messageResId = if (isSuccess) R.string.debicheck_mandate_suspended else R.string.debicheck_mandate_not_suspended
        val subMessage = if (isSuccess) getString(R.string.debicheck_suspend_success_sub_message) else getString(R.string.debicheck_suspend_rejected_sub_message)
        startActivity(buildResultIntent(messageResId, isSuccess, subMessage))
    }

    fun showApproveResultScreen() {
        startActivity(buildResultIntent(R.string.debicheck_approved_message, true, getString(R.string.debicheck_approved_sub_message)))
    }

    fun showRejectedResultScreen() {
        if (viewModel.currentFlow == PENDING && viewModel.rejectionReasonCode == "MS02") {
            startActivity(buildResultIntent(R.string.debicheck_rejected_message, true, getString(R.string.debicheck_rejected_sub_message)))
        } else {
            startActivity(buildResultIntent(R.string.debicheck_rejected_message))
        }
    }

    fun showSureCheckRejectedScreen() {
        startActivity(buildResultIntent(R.string.transaction_rejected, false, ""))
    }

    fun tagDebiCheckEvent(tag: String) {
        AnalyticsUtil.trackAction("DebiCheck", "DebiCheck_${tag}")
    }

    private fun buildResultIntent(messageResId: Int, isSuccess: Boolean, subMessage: String): Intent {
        GenericResultActivity.bottomOnClickListener = View.OnClickListener {
            val intent = Intent(this, DebiCheckActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
        }

        val drawableResId = if (isSuccess) R.drawable.ic_tick else R.drawable.ic_cross

        return Intent(this, GenericResultActivity::class.java).apply {
            putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, drawableResId)
            putExtra(GenericResultActivity.NOTICE_MESSAGE, messageResId)
            putExtra(GenericResultActivity.SUB_MESSAGE_STRING, subMessage)
            putExtra(GenericResultActivity.IS_SUCCESS, isSuccess)
            putExtra(GenericResultActivity.IS_FAILURE, !isSuccess)
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.done)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
    }

    private fun buildResultIntent(messageResId: Int): Intent {
        GenericResultActivity.bottomOnClickListener = View.OnClickListener {
            val intent = Intent(this, DebiCheckActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
        }

        return Intent(this, GenericResultActivity::class.java).apply {
            putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_tick)
            putExtra(GenericResultActivity.NOTICE_MESSAGE, messageResId)
            putExtra(GenericResultActivity.IS_SUCCESS, true)
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.done)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
    }
}