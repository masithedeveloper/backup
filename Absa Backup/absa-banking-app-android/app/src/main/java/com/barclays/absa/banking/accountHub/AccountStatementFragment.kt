/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.accountHub

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccessPrivileges
import com.barclays.absa.banking.databinding.GenericStatementFragmentBinding
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.home.ui.AccountTypes
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.FilterAccountList
import com.barclays.absa.utils.extensions.viewBinding

class AccountStatementFragment : BaseFragment(R.layout.generic_statement_fragment) {
    private val binding by viewBinding(GenericStatementFragmentBinding::bind)
    private val accountHubViewModel by activityViewModels<AccountHubViewModel>()

    private var operatorNotAllowed: Boolean = false
    private val featureTag: String
        get() = "${accountHubViewModel.accountObject.accountType}AccountHub_"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        operatorNotAllowed = AccessPrivileges.instance.isOperator && (!accountHubViewModel.accountObject.isAccountDetailAllowed || "Y".equals(accountHubViewModel.accountObject.isBalanceMasked, ignoreCase = true))

        binding.csvStatementButton.visibility = if (operatorNotAllowed) View.GONE else View.VISIBLE

        binding.archivedStatementButton.setOnClickListener {
            if (featureSwitchingToggles.archivedStatements == FeatureSwitchingStates.DISABLED.key) {
                startActivity(IntentFactory.capabilityUnavailable(baseActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_archived_statement))))
            } else {
                navigateToArchivedStatement()
            }
        }
        binding.stampedStatementButton.setOnClickListener {
            if (featureSwitchingToggles.stampedStatements == FeatureSwitchingStates.DISABLED.key) {
                startActivity(IntentFactory.capabilityUnavailable(baseActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_stamped_statement))))
            } else {
                navigateToStampedStatement()
            }
        }
        binding.csvStatementButton.setOnClickListener {
            navigateToCsvStatement()
        }
        setupFeatureSwitchingVisibilityToggles()

        tagStatements("StatementsTab_TabSelected")
    }

    private fun setupFeatureSwitchingVisibilityToggles() {
        when (accountHubViewModel.accountObject.accountType) {
            AccountTypes.CREDIT_CARD -> setArchiveFeatureState(featureSwitchingToggles.creditCardArchivedStatements)
            AccountTypes.PERSONAL_LOAN -> setArchiveFeatureState(featureSwitchingToggles.personalLoanArchivedStatements)
            AccountTypes.ABSA_VEHICLE_AND_ASSET_FINANCE -> setArchiveFeatureState(featureSwitchingToggles.vehicleFinanceArchivedStatements)
            AccountTypes.HOME_LOAN -> setArchiveFeatureState(featureSwitchingToggles.homeLoanArchivedStatements)
            else -> setArchiveFeatureState(featureSwitchingToggles.archivedStatements)
        }

        setStampedFeatureState()
    }

    private fun setStampedFeatureState() {
        if (!FilterAccountList.isStampedStatementsAllowed(accountHubViewModel.accountObject) || featureSwitchingToggles.stampedStatements == FeatureSwitchingStates.GONE.key) {
            binding.stampedStatementButton.visibility = View.GONE
        } else {
            setFeatureState(binding.stampedStatementButton, FeatureSwitchingStates.ACTIVE.key, getString(R.string.feature_switching_stamped_statement))
        }
    }

    private fun setArchiveFeatureState(state: Int) {
        if ((operatorNotAllowed) || state == FeatureSwitchingStates.GONE.key || !FilterAccountList.isArchivedStatementsAllowed(accountHubViewModel.accountObject)) {
            binding.archivedStatementButton.visibility = View.GONE
        } else {
            setFeatureState(binding.archivedStatementButton, state, getString(R.string.feature_switching_archived_statement))
        }
    }

    private fun setFeatureState(optionActionButtonView: View, state: Int, featureName: String) {
        if (state == FeatureSwitchingStates.DISABLED.key) {
            optionActionButtonView.setOnClickListener { startActivity(IntentFactory.capabilityUnavailable(activity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, featureName))) }
        } else {
            optionActionButtonView.visibility = View.VISIBLE
        }
    }

    private fun tagStatements(actionTag: String) {
        AnalyticsUtil.trackAction("Statements", "$featureTag$actionTag")
    }

    private fun navigateToCsvStatement() {
        startActivity(IntentFactory.getCsvStatement(baseActivity, accountHubViewModel.accountObject, featureTag))
        tagStatements("StatementsTab_ExportTransactionsCSVSelected")
    }

    private fun navigateToArchivedStatement() {
        if (operatorNotAllowed) {
            BaseAlertDialog.showRequestAccessAlertDialog(getString(R.string.archived_statement))
        } else {
            startActivity(IntentFactory.getArchivedStatement(baseActivity, accountHubViewModel.accountObject, featureTag))
            tagStatements("StatementsTab_DownloadBankStatementSelected")
        }
    }

    private fun navigateToStampedStatement() {
        startActivity(IntentFactory.getStampedStatement(baseActivity, accountHubViewModel.accountObject.accountNumber, featureTag))
        tagStatements("StatementsTab_DownloadStampedStatementSelected")
    }
}