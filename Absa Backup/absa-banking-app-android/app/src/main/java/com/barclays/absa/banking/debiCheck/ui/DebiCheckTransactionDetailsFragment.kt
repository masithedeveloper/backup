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
import com.barclays.absa.banking.debiCheck.services.dto.DisputeReason
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.presentation.shared.observeWithReset
import com.barclays.absa.utils.AbsaCacheManager
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.TextFormatUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.debi_check_transaction_details_fragment.*
import styleguide.utils.extensions.toFormattedAccountNumber

class DebiCheckTransactionDetailsFragment : BaseFragment(R.layout.debi_check_transaction_details_fragment) {

    private lateinit var viewModel: DebiCheckViewModel
    private lateinit var hostActivity: DebiCheckHostActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as DebiCheckHostActivity
        viewModel = hostActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hostActivity.tagDebiCheckEvent("TransactionDetailScreen_ScreenDisplayed")
        setToolbarTitle()
        initContentViews()
        attachDataObserver()
    }

    private fun attachDataObserver() {
        viewModel.debiCheckTransactionDisputableResponse.observe(this, {
            dismissProgressDialog()
            viewModel.debiCheckTransactionDisputableResponse.removeObservers(this)
            it?.let {
                if (it.transactionDisputableDetail.disputable) processDispute() else hostActivity.showTransactionDisputeResultScreen(false)
            }
        })

        viewModel.debiCheckDisputeTransactionResponse.observe(this, {
            dismissProgressDialog()
            viewModel.debiCheckDisputeTransactionResponse.removeObservers(this)
            it?.let {
                hostActivity.showTransactionDisputeResultScreen(true)
            }
        })

        viewModel.mandateResponse.observeWithReset(this, {
            dismissProgressDialog()
            it?.let {
                if (it.debitOrders.isNotEmpty()) {
                    viewModel.selectedDebitOrder = it.debitOrders[0]
                }
                navigateToDetails()
            }
        })
    }

    private fun initContentViews() {
        val selectedTransaction = viewModel.selectedTransaction
        selectedTransaction?.let {
            creditorNameContentView.setContentText(selectedTransaction.creditorName)
            installmentAmountContentView.setContentText("-${TextFormatUtils.formatBasicAmountAsRand(selectedTransaction.collectionAmount)}")
            dateContentView.setContentText(DateUtils.formatDateMonth(selectedTransaction.lastPaymentDate))
            referenceContentView.setContentText(selectedTransaction.mandateReferenceNumber)
            val accountType = getAccountType(selectedTransaction.debtorAccountNumber)
            val formattedAccountNumber = selectedTransaction.debtorAccountNumber.toFormattedAccountNumber()
            val accountDescription = if (accountType.isEmpty()) formattedAccountNumber else "$accountType ($formattedAccountNumber)"
            accountNumberContentView.setContentText(accountDescription)
            if (it.disputable) {
                disputeButton.visibility = View.VISIBLE
            } else {
                disputeButton.visibility = View.GONE
                transactionDisputedTextView.visibility = View.VISIBLE
            }
            viewContractButton.setOnClickListener {
                fetchMandate()
            }
        }

        if (!viewModel.disputeReasonCode.isBlank()) {
            viewContractButton.visibility = View.GONE
            disputeReasonContentView.visibility = View.VISIBLE
            disputeReasonContentView.setContentText(DisputeReason.reasonFromCode(viewModel.disputeReasonCode))
        }

        initCallToAction()
    }

    private fun fetchMandate() {
        viewModel.fetchMandates("", "", viewModel.selectedTransaction?.mandateReferenceNumber!!)
    }

    private fun initCallToAction() {
        disputeButton.setOnClickListener {
            if (viewModel.disputeReasonCode.isBlank()) {
                navigateToInfo()
            } else {
                checkDisputable()
            }
        }
    }

    private fun setToolbarTitle() {
        var toolBarTitle = R.string.debicheck_transaction_details
        if (!viewModel.disputeReasonCode.isBlank()) {
            toolBarTitle = R.string.debicheck_confirm_dispute
        }
        baseActivity.setToolBarBack(toolBarTitle)
    }

    private fun checkDisputable() {
        viewModel.selectedTransaction?.let {
            viewModel.checkIfTransactionDisputable(it, viewModel.disputeReasonCode)
        }
    }

    private fun processDispute() {
        viewModel.selectedTransaction?.let {
            it.paymentKey.apply {
                viewModel.disputeTransaction(this)
            }
        }
    }

    private fun getAccountType(accountNumber: String): String {
        var accountType = ""
        val account = AbsaCacheManager.getTransactionalAccounts().find { it.accountNumber == accountNumber }
        account?.let {
            accountType = it.description
        }

        return accountType
    }

    private fun navigateToInfo() {
        view?.findNavController()?.navigate(R.id.action_debiCheckTransactionDetailFragment_to_debiCheckDisputeInfoFragment)
    }

    private fun navigateToDetails() {
        viewModel.mandateResponse.value = null
        view?.findNavController()?.navigate(R.id.action_debiCheckTransactionDetailFragment_to_debiCheckMandateDetailsFragment)
    }
}