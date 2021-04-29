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
package com.barclays.absa.banking.transfer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.rewards.RewardsRedeemConfirmation
import com.barclays.absa.banking.dualAuthorisations.ui.pendingAuthorisation.DualAuthPaymentPendingActivity
import com.barclays.absa.banking.express.transfer.InterAccountTransferViewModel
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.banking.presentation.transactions.AccountRefreshInterface
import com.barclays.absa.banking.rewards.behaviouralRewards.ui.RewardsViewModel
import com.barclays.absa.banking.rewards.ui.rewardsHub.State
import com.barclays.absa.banking.transfer.TransferFundsContract.TransferFundsConfirmationView
import com.barclays.absa.utils.*
import com.barclays.absa.utils.DateTimeHelper.SPACED_PATTERN_DD_MMMM_YYYY
import kotlinx.android.synthetic.main.transfer_funds_confirmation_fragment.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties.PropertiesBuilder
import za.co.absa.networking.dto.ResultMessage
import java.text.ParseException

class TransferFundsConfirmationFragment : BaseFragment(R.layout.transfer_funds_confirmation_fragment), TransferFundsConfirmationView {
    companion object {
        private const val SUCCESS_SCREEN = ResultAnimations.paymentSuccess
        private const val FAILURE_SCREEN = ResultAnimations.generalFailure
    }

    private val sureCheckDelegate: SureCheckDelegate
    private var rewardsRedeemConfirmation: RewardsRedeemConfirmation? = null

    private var transferConfirmationData: TransferConfirmationData = TransferConfirmationData()
    private lateinit var transferViewModel: TransferViewModel
    private lateinit var interAccountTransferViewModel: InterAccountTransferViewModel
    private lateinit var activity: TransferFundsActivity

    init {
        sureCheckDelegate = object : SureCheckDelegate(baseActivity) {
            override fun onSureCheckProcessed() = transferViewModel.sureCheckConfirmed()

            override fun onSureCheckRejected() = launchFailureScreen()

            override fun onSureCheckCancelled() = launchFailureScreen()

            override fun onSureCheckFailed() = launchFailureScreen()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as TransferFundsActivity
        transferViewModel = activity.viewModel()
        interAccountTransferViewModel = activity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.transfer_confirm_request_title)) { activity.onBackPressed() }

        transferConfirmationData = transferViewModel.transferConfirmationData
        rewardsRedeemConfirmation = transferViewModel.rewardsRedeemConfirmation

        initialDataReturned(transferConfirmationData)
        trackAvafAction("AVAFInterAccountTransfer_ConfirmTransferScreen_ScreenDisplayed")

        if (transferConfirmationData.fromAccountDescription.contains("Rewards")) {
            transferTypeContentView.visibility = View.GONE
            lineDividerReferenceView.visibility = View.GONE

            confirmButton.setOnClickListener {
                rewardsRedeemConfirmation?.let {
                    transferViewModel.rewardsTransferConfirmed(it)
                }
            }
        } else {
            transferTypeContentView.visibility = View.VISIBLE
            lineDividerReferenceView.visibility = View.VISIBLE
            confirmButton.setOnClickListener {
                trackAvafAction("AVAFInterAccountTransfer_ConfirmTransferScreen_TransferButtonClicked")
                with(interAccountTransferViewModel) {
                    validateAndPay(transferConfirmationData)

                    transferResponseLiveData.observe(viewLifecycleOwner, Observer { response ->
                        transferResponseLiveData.removeObservers(viewLifecycleOwner)
                        val resultMessage: ResultMessage? = response.header.resultMessages.firstOrNull()
                        val responseMessage: String = resultMessage?.responseMessage ?: ""
                        dismissProgressDialog()
                        if (responseMessage.isNotBlank() && responseMessage.contains(getString(R.string.transfer_authorization_outstanding), ignoreCase = true)) {
                            showAuthorizationRequiredScreen()
                            return@Observer
                        }

                        showSuccessfulInterAccountTransferResultScreen(transferConfirmationData, resultMessage)
                        var analyticsTag = "AVAFInterAccountTransfer_TransferSuccessfulScreen_ScreenDisplayed"
                        val transferSuccessIntent = Intent().apply {
                            putExtra(TransferConstants.IS_TRANSFER_SUCCESSFUL, true)
                        }

                        if (transferConfirmationData.isFutureDatedTransfer) {
                            analyticsTag = "AVAFInterAccountTransfer_TransferScheduleSuccessfulScreen_ScreenDisplayed"
                            transferSuccessIntent.apply {
                                putExtra(TransferConstants.IS_FUTURE_TRANSFER, true)
                            }
                        }

                        baseActivity.setResult(Activity.RESULT_OK, transferSuccessIntent)
                        trackAvafAction(analyticsTag)
                    })
                }

            }
        }

        transferViewModel.rewardsRedeemResultLiveData.observe(viewLifecycleOwner, {
            if (it.registrationStatus != null) {
                if ("success".equals(it.registrationStatus, ignoreCase = true) && rewardsRedeemConfirmation != null) {
                    rewardsRedemptionSuccessResult()
                } else {
                    showFailureScreenForRewardsRedemption(TransferResultData(null, null, null, it.registrationMessage))
                }
                dismissProgressDialog()
            }

            if ("SURECHECKV2Required".equals(it.sureCheckFlag, ignoreCase = true)) {
                sureCheckDelegate.processSureCheck(baseActivity, it) { transferViewModel.sureCheckConfirmed() };
            }
        })

        if (transferViewModel.isAvafTransfer) {
            transferNoteTextView.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        showToolBar()
    }

    private fun setupTalkBack(fromAccount: String, toAccount: String, amountToTransfer: String) {
        fromAccountContentView.contentTextView.contentDescription = getString(R.string.talkback_transfer_chosen_source_account, fromAccount)
        toAccountContentView.contentTextView.contentDescription = getString(R.string.talkback_transfer_chosen_destination_account, toAccount)
        amountContentView.contentTextView.contentDescription = getString(R.string.talkback_transfer_amount_being_transferred, AccessibilityUtils.getTalkBackRandValueFromString(amountToTransfer))
    }

    override fun initialDataReturned(transferFundsConfirmationData: TransferConfirmationData) {
        setupTalkBack(transferFundsConfirmationData.fromAccountNumber, transferFundsConfirmationData.toAccountNumber, transferFundsConfirmationData.amountToTransfer)
        amountContentView.setContentText(transferFundsConfirmationData.amountToTransfer)
        fromAccountContentView.setContentText(transferFundsConfirmationData.fromAccountDescription)
        toAccountContentView.setContentText(transferFundsConfirmationData.toAccountDescription)
        if (transferFundsConfirmationData.fromAccountReference.isNotEmpty()) {
            lineDividerAccountView.visibility = View.VISIBLE
            fromAccountReferenceContentView.visibility = View.VISIBLE
            fromAccountReferenceContentView.setContentText(transferFundsConfirmationData.fromAccountReference)
        }
        if (transferFundsConfirmationData.toAccountReference.isNotEmpty()) {
            toAccountReferenceContentView.visibility = View.VISIBLE
            toAccountReferenceContentView.setContentText(transferFundsConfirmationData.toAccountReference)
        }
        if (transferFundsConfirmationData.isFutureDatedTransfer) {
            try {
                val futureDate = transferFundsConfirmationData.transactionDate
                transferTypeContentView.setLabelText(getString(R.string.transfer_type))
                transferTypeContentView.setContentText(getString(R.string.future_dated_transfer))
                futureDatedTransferDateContentView.visibility = View.VISIBLE
                futureDatedTransferDateContentView.setContentText(DateUtils.format(futureDate, "dd MMMM yyyy"))
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        } else {
            transferTypeContentView.setLabelText(getString(R.string.transfer_type))
            transferTypeContentView.setContentText(getString(R.string.normal))
        }
    }

    override fun rewardsRedemptionSuccessResult() {
        showSuccessScreenForRedeemingRewards()
    }

    override fun showSuccessfulInterAccountTransferResultScreen(transferConfirmationData: TransferConfirmationData, resultMessage: ResultMessage?) {
        if (transferViewModel.isAvafTransfer) {
            if (transferConfirmationData.isFutureDatedTransfer) {
                trackAvafAction("AVAFInterAccountTransfer_TransferScheduleSuccessfulScreen_ScreenDisplayed")
            } else {
                trackAvafAction("AVAFInterAccountTransfer_TransferSuccessfulScreen_ScreenDisplayed")
            }
        }

        hideToolBar()
        AbsaCacheManager.getInstance().setAccountsCacheStatus(false)
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            if (transferViewModel.isAvafTransfer) {
                val analyticsTag = if (transferConfirmationData.isFutureDatedTransfer) {
                    "AVAFInterAccountTransfer_TransferScheduleSuccessfulScreen_DoneButtonClicked"
                } else {
                    "AVAFInterAccountTransfer_TransferSuccessfulScreen_DoneButtonClicked"
                }

                trackAvafAction(analyticsTag)
                updateBalancesAndFinish()
            } else {
                loadAccountsAndGoHome()
            }
        }
        GenericResultScreenFragment.setSecondaryButtonOnClick {
            if (transferViewModel.isAvafTransfer) {
                val analyticsTag = if (transferConfirmationData.isFutureDatedTransfer) {
                    "AVAFInterAccountTransfer_TransferScheduleSuccessfulScreen_MakeAnotherTransferButtonClicked"
                } else {
                    "AVAFInterAccountTransfer_TransferSuccessfulScreen_MakeAnotherTransferButtonClicked"
                }

                AnalyticsUtil.trackAction(analyticsTag)
            }

            updateAccountsAndNavigateBack()
        }

        val primaryButtonLabel = if (transferViewModel.isAvafTransfer) getString(R.string.done) else getString(R.string.home)
        val message = if (transferConfirmationData.isFutureDatedTransfer) {
            getString(R.string.future_dated_transfer_fund_result_substring, transferConfirmationData.amountToTransfer, transferConfirmationData.fromAccountDescription, transferConfirmationData.toAccountDescription, DateTimeHelper.formatDate(transferConfirmationData.transactionDate, SPACED_PATTERN_DD_MMMM_YYYY))
        } else {
            val resultSubstringResId = if (!transferViewModel.isAvafTransfer) R.string.transfer_fund_result_substring else R.string.transfer_avaf_fund_result_substring
            getString(resultSubstringResId, transferConfirmationData.amountToTransfer, transferConfirmationData.fromAccountDescription, transferConfirmationData.toAccountDescription)
        }

        val title = when {
            !resultMessage?.responseMessage.isNullOrBlank() -> resultMessage?.responseMessage
            transferConfirmationData.isFutureDatedTransfer -> getString(R.string.future_dated_transfer_successful)
            else -> getString(R.string.transfer_successful)
        }

        val resultScreenProperties = PropertiesBuilder()
                .setResultScreenAnimation(SUCCESS_SCREEN)
                .setTitle(title)
                .setDescription(message)
                .setSecondaryButtonLabel(getString(R.string.do_another_transfer))
                .setSecondaryButtonContentDescription(getString(R.string.talkback_transfer_transfer_complete_button))
                .setPrimaryButtonLabel(primaryButtonLabel)
                .setPrimaryButtonContentDescription(getString(R.string.talkback_transfer_transfer_complete_go_home))
                .build(true)

        navigate(TransferFundsConfirmationFragmentDirections.actionTransferFundsConfirmationFragmentToGenericResultScreenFragment(resultScreenProperties))
    }

    private fun navigateBackToTransferScreen(baseActivity: BaseActivity) {
        if (transferViewModel.isAvafTransfer) {
            findNavController().navigate(R.id.action_genericResultScreenFragment_to_transferFundsFragment)
        } else {
            val transferIntent = Intent(baseActivity, TransferFundsActivity::class.java)
            transferIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            baseActivity.startActivity(transferIntent)
        }
    }

    private fun updateAccountsAndNavigateBack() {
        transferViewModel.clearAccounts()
        AccountBalanceUpdateHelper(activity).refreshHomeScreenAccountsAndBalances(object : AccountRefreshInterface {
            override fun onSuccess() {
                dismissAndNavigateToTransfer()
            }

            override fun onFailure() {
                dismissAndNavigateToTransfer()
            }
        })
    }

    private fun updateRewardsAccountsAndNavigateBack() {
        AccountBalanceUpdateHelper(baseActivity).updateRewardsBalance(object : AccountRefreshInterface {
            override fun onSuccess() {
                val rewardsViewModel = ViewModelProvider(baseActivity).get(RewardsViewModel::class.java)
                if (appCacheService.hasRewardsAccount()) {
                    rewardsViewModel.resetState()
                    rewardsViewModel.state.observe(baseActivity, { state: State ->
                        if (state == State.QUEUE_COMPLETED || state == State.FAILED) {
                            dismissAndNavigateToTransfer()
                        }
                    })
                    rewardsViewModel.fetchRedeemRewards()
                } else {
                    dismissAndNavigateToTransfer()
                }
            }

            override fun onFailure() {
                dismissAndNavigateToTransfer()
            }
        })
    }

    private fun dismissAndNavigateToTransfer() {
        dismissProgressDialog()
        navigateBackToTransferScreen(baseActivity)
    }

    private fun showSuccessScreenForRedeemingRewards() {
        hideToolBar()
        AbsaCacheManager.getInstance().setAccountsCacheStatus(false)

        GenericResultScreenFragment.setPrimaryButtonOnClick { loadAccountsClearingRewardsBalanceAndShowHomeScreen() }
        GenericResultScreenFragment.setSecondaryButtonOnClick { updateRewardsAccountsAndNavigateBack() }

        val resultScreenProperties = PropertiesBuilder()
                .setResultScreenAnimation(SUCCESS_SCREEN)
                .setTitle(getString(R.string.transfer_successful))
                .setDescription(getString(R.string.rewards_redemption_successful, ""))
                .setSecondaryButtonLabel(getString(R.string.transfer))
                .setSecondaryButtonContentDescription(getString(R.string.talkback_transfer_transfer_complete_button))
                .setPrimaryButtonLabel(getString(R.string.home))
                .setPrimaryButtonContentDescription(getString(R.string.talkback_transfer_transfer_complete_go_home))
                .build(true)
        navigate(TransferFundsConfirmationFragmentDirections.actionTransferFundsConfirmationFragmentToGenericResultScreenFragment(resultScreenProperties))
    }

    private fun showFailureScreenForRewardsRedemption(resultData: TransferResultData?) {
        hideToolBar()

        GenericResultScreenFragment.setPrimaryButtonOnClick { navigateToHomeScreenWithoutReloadingAccounts() }
        GenericResultScreenFragment.setSecondaryButtonOnClick { navigateBackToTransferScreen(baseActivity) }

        val resultScreenProperties = PropertiesBuilder()
                .setResultScreenAnimation(FAILURE_SCREEN)
                .setTitle(getString(R.string.rewards_redemption_unsuccessful_title))
                .setDescription(resultData?.errorMessage)
                .setSecondaryButtonLabel(getString(R.string.transfer))
                .setSecondaryButtonContentDescription(getString(R.string.talkback_transfer_transfer_complete_button))
                .setPrimaryButtonLabel(getString(R.string.home))
                .setPrimaryButtonContentDescription(getString(R.string.talkback_transfer_transfer_complete_go_home))
                .build(true)
        navigate(TransferFundsConfirmationFragmentDirections.actionTransferFundsConfirmationFragmentToGenericResultScreenFragment(resultScreenProperties))
    }

    override fun showAuthorizationRequiredScreen() {
        val transferIntent = Intent(baseActivity, DualAuthPaymentPendingActivity::class.java)
        transferIntent.putExtra(BMBConstants.TRANSACTION_TYPE, BMBConstants.TRANSACTION_TYPE_TRANSFER)
        startActivity(transferIntent)
    }

    private fun launchFailureScreen() {
        hideToolBar()
        dismissProgressDialog()

        GenericResultScreenFragment.setPrimaryButtonOnClick { navigateToHomeScreenWithoutReloadingAccounts() }
        GenericResultScreenFragment.setSecondaryButtonOnClick { navigateBackToTransferScreen(baseActivity) }

        val resultScreenProperties = PropertiesBuilder()
                .setResultScreenAnimation(FAILURE_SCREEN)
                .setTitle(getString(R.string.transfer_error_msg))
                .setDescription(getString(R.string.surecheck_error_unabletocomplete))
                .setSecondaryButtonLabel(getString(R.string.transfer))
                .setSecondaryButtonContentDescription(getString(R.string.talkback_transfer_transfer_complete_button))
                .setPrimaryButtonLabel(getString(R.string.home))
                .setPrimaryButtonContentDescription(getString(R.string.talkback_transfer_transfer_complete_go_home))
                .build(true)
        navigate(TransferFundsConfirmationFragmentDirections.actionTransferFundsConfirmationFragmentToGenericResultScreenFragment(resultScreenProperties))
    }

    private fun updateBalancesAndFinish() {
        AccountBalanceUpdateHelper(activity).refreshHomeScreenAccountsAndBalances(object : AccountRefreshInterface {
            override fun onSuccess() {
                activity.finish()
            }

            override fun onFailure() {
                activity.finish()
            }
        })
    }

    private fun trackAvafAction(tag: String) {
        if (transferViewModel.isAvafTransfer) {
            AnalyticsUtil.trackAction(tag)
        }
    }
}