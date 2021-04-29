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
package com.barclays.absa.banking.settings.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.dualAuthorisations.ui.pendingAuthorisation.DualAuthDigitalLimitChangePendingActivity
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBConstants.AUTHORISATION_OUTSTANDING_TRANSACTION
import com.barclays.absa.banking.framework.app.BMBConstants.FAILURE
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.CALLING_FRAGMENT
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.INTERNATIONAL_PAYMENT
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.presentation.shared.IntentFactoryGenericResult
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.banking.rewards.ui.rewardsHub.ProgressHandler
import com.barclays.absa.banking.rewards.ui.rewardsHub.State
import com.barclays.absa.banking.settings.ui.DigitalLimitViewModel.EventSource
import com.barclays.absa.banking.settings.ui.DigitalLimitViewModel.EventSource.*
import com.barclays.absa.utils.viewModel

class ManageDigitalLimitsActivity : BaseActivity(), ManageDigitalLimitsView {

    private var stubDigitalLimitsChangeRequestSureCheck2Required = true
    private var isFromInternationalPayment = false
    private lateinit var digitalLimitViewModel: DigitalLimitViewModel

    private val manageDigitalLimitsSureCheckDelegate = object : SureCheckDelegate(this) {
        var canceled: Boolean = false

        override fun onSureCheckProcessed() {
            if (!canceled) {
                stubDigitalLimitsChangeRequestSureCheck2Required = false
                digitalLimitViewModel.confirmDigitalLimitsChange(stubDigitalLimitsChangeRequestSureCheck2Required)
            }
        }

        override fun onSureCheckCancelled() {
            super.onSureCheckCancelled(this@ManageDigitalLimitsActivity)
            canceled = true
        }

        override fun onSureCheckFailed() = showFailedResultScreen()
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_change_digital_limits_update)
        setToolBarBack(R.string.title_manage_digital_limits)

        if (INTERNATIONAL_PAYMENT.equals(intent.extras?.getString(CALLING_FRAGMENT), true)) {
            isFromInternationalPayment = true
        }

        digitalLimitViewModel = viewModel()
        digitalLimitViewModel.eventSourceAndState.observe(this, { this.processNavigation(it) })
        digitalLimitViewModel.retrieveDigitalLimits()
    }

    private fun processNavigation(eventSourceStatePair: Pair<EventSource, State>) {
        val eventSource = eventSourceStatePair.first
        val state = eventSourceStatePair.second
        when (eventSource) {
            DIGITAL_LIMITS_RETRIEVAL -> {
                ProgressHandler.handleProgress(this, state)
                when (state) {
                    State.COMPLETED -> navigateToManageDigitalLimitsFragment()
                    State.FAILED -> onBackPressed()
                    else -> {
                    }
                }
            }
            DIGITAL_LIMITS_CHANGE -> if (state == State.FAILED) {
                ProgressHandler.handleProgress(this, State.FAILED)
            }

            DIGITAL_LIMITS_CHANGE_CONFIRMATION -> {
                if (state == State.COMPLETED) {
                    initiateSurecheckProcessing()
                } else {
                    ProgressHandler.handleProgress(this, state)
                }
            }
        }
    }

    override fun navigateToManageDigitalLimitsFragment() {
        val manageDigitalLimitsFragment = ManageDigitalLimitsFragment.newInstance()
        startFragment(manageDigitalLimitsFragment, R.id.fragmentContainer, true, AnimationType.FADE, true, ManageDigitalLimitsFragment::class.java.name)
    }

    override fun initiateSurecheckProcessing() {
        val changeConfirmationResult = digitalLimitViewModel.digitalLimitChangeConfirmationResult
        manageDigitalLimitsSureCheckDelegate.processSureCheck(this, changeConfirmationResult) { launchResultScreen() }
    }

    override fun navigateToEditManageDigitalLimitsFragment() {
        val editManageDigitalLimitsFragment = EditManageDigitalLimitsFragment.newInstance()
        startFragment(editManageDigitalLimitsFragment, R.id.fragmentContainer, true, AnimationType.FADE, true, EditManageDigitalLimitsFragment::class.java.name)
    }

    override fun onBackPressed() {
        if (currentFragmentName == EditManageDigitalLimitsFragment::class.java.name) {
            removeFragments(1)
        } else {
            finish()
        }
    }

    private fun launchResultScreen() {
        digitalLimitViewModel.digitalLimitChangeConfirmationResult?.let {
            val successFailureMessage = it.successOrFailMsgValue
            val transactionStatus = it.transactionStatus

            if (AUTHORISATION_OUTSTANDING_TRANSACTION.equals(successFailureMessage, ignoreCase = true)) {
                startActivity(Intent(this, DualAuthDigitalLimitChangePendingActivity::class.java))
            } else {
                if (isFromInternationalPayment) {
                    finish()
                } else {
                    val intent: Intent = when {
                        FAILURE.equals(it.status, ignoreCase = true)
                        -> IntentFactory.getFailureResultScreen(this, R.string.failureMsg, -1)

                        FAILURE.equals(transactionStatus, ignoreCase = true)
                        -> IntentFactoryGenericResult.getFailureResultBuilder(this)
                                .setGenericResultHeaderMessage(getString(R.string.system_error))
                                .setGenericResultSubMessage(it.oldTransactionMessage)
                                .build()

                        else -> IntentFactory.getSuccessfulResultScreen(this, R.string.message_limits_set_message, -1, false)
                    }

                    GenericResultActivity.bottomOnClickListener = View.OnClickListener { navigateToHomeScreenWithoutReloadingAccounts() }
                    intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.done)
                    startActivityIfAvailable(intent)
                }
            }
        }
    }

    private fun showFailedResultScreen() {
        GenericResultActivity.bottomOnClickListener = View.OnClickListener { loadAccountsAndGoHome() }
        startActivity(Intent(this, GenericResultActivity::class.java).apply {
            putExtra(GenericResultActivity.IS_FAILURE, true)
            putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_cross)
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home)
            putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.surecheck_failed)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
    }
}