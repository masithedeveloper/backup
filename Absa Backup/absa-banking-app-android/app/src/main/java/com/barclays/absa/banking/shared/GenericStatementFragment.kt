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
package com.barclays.absa.banking.shared

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccessPrivileges
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.home.ui.AccountTypes
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.shared.genericTransactionHistory.ui.GenericStatementView
import com.barclays.absa.banking.shared.genericTransactionHistory.ui.GenericTransactionHistoryViewModel
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.FilterAccountList
import kotlinx.android.synthetic.main.generic_statement_fragment.*
import styleguide.bars.FragmentPagerItem

class GenericStatementFragment : FragmentPagerItem(), View.OnClickListener {
    private lateinit var genericTransactionHistoryViewModel: GenericTransactionHistoryViewModel
    private lateinit var hostActivity: BaseActivity
    private var featureTag = ""
    private var operatorNotAllowed: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = activity as BaseActivity
        genericTransactionHistoryViewModel = (activity as GenericStatementView).statementViewModel()
    }

    companion object {
        const val FEATURE_NAME = "featureName"

        @JvmStatic
        fun newInstance(description: String, featureName: String): GenericStatementFragment {
            return GenericStatementFragment().apply {
                arguments = Bundle().apply {
                    putString(TAB_DESCRIPTION_KEY, description)
                    putString(FEATURE_NAME, featureName)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.generic_statement_fragment, container, false).rootView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        featureTag = arguments?.getString(FEATURE_NAME) ?: ""

        operatorNotAllowed = AccessPrivileges.instance.isOperator && (genericTransactionHistoryViewModel.accountDetail?.isAccountDetailAllowed == false || "Y".equals(genericTransactionHistoryViewModel.accountDetail?.isBalanceMasked, ignoreCase = true))

        csvStatementButton.visibility = if (operatorNotAllowed) View.GONE else View.VISIBLE

        archivedStatementButton.setOnClickListener(this)
        stampedStatementButton.setOnClickListener(this)
        csvStatementButton.setOnClickListener(this)
        setupFeatureSwitchingVisibilityToggles()

        tagStatements("AccountHub_StatementsTab_TabSelected")
    }

    private fun tagStatements(actionTag: String) {
        AnalyticsUtil.trackAction("Statements", "$featureTag$actionTag")
    }

    private fun setupFeatureSwitchingVisibilityToggles() {
        val featureSwitchingToggles = FeatureSwitchingCache.featureSwitchingToggles
        featureSwitchingToggles.let {
            when (genericTransactionHistoryViewModel.accountDetail?.accountType) {
                AccountTypes.CREDIT_CARD -> setArchiveFeatureState(featureSwitchingToggles.creditCardArchivedStatements)
                AccountTypes.PERSONAL_LOAN -> setArchiveFeatureState(featureSwitchingToggles.personalLoanArchivedStatements)
                AccountTypes.ABSA_VEHICLE_AND_ASSET_FINANCE -> setArchiveFeatureState(featureSwitchingToggles.vehicleFinanceArchivedStatements)
                AccountTypes.HOME_LOAN -> setArchiveFeatureState(featureSwitchingToggles.homeLoanArchivedStatements)
                else -> setArchiveFeatureState(featureSwitchingToggles.archivedStatements)
            }
        }

        setStampedFeatureState()
    }

    private fun setArchiveFeatureState(state: Int) {
        if ((operatorNotAllowed) || state == FeatureSwitchingStates.GONE.key || !FilterAccountList.isArchivedStatementsAllowed(genericTransactionHistoryViewModel.accountDetail)) {
            archivedStatementButton.visibility = View.GONE
        } else {
            setFeatureState(archivedStatementButton, state, getString(R.string.feature_switching_archived_statement))
        }
    }

    private fun setStampedFeatureState() {
        if (!FilterAccountList.isStampedStatementsAllowed(genericTransactionHistoryViewModel.accountDetail) || FeatureSwitchingCache.featureSwitchingToggles.stampedStatements == FeatureSwitchingStates.GONE.key) {
            stampedStatementButton.visibility = View.GONE
        } else {
            setFeatureState(stampedStatementButton, FeatureSwitchingStates.ACTIVE.key, getString(R.string.feature_switching_stamped_statement))
        }
    }

    private fun setFeatureState(optionActionButtonView: View, state: Int, featureName: String) {
        if (state == FeatureSwitchingStates.DISABLED.key) {
            optionActionButtonView.setOnClickListener { startActivity(IntentFactory.capabilityUnavailable(activity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, featureName))) }
        } else {
            optionActionButtonView.visibility = View.VISIBLE
        }
    }

    override fun onClick(view: View) {
        BaseActivity.preventDoubleClick(view)
        val featureSwitchingToggles = FeatureSwitchingCache.featureSwitchingToggles
        when (view.id) {
            R.id.archivedStatementButton -> if (featureSwitchingToggles.archivedStatements == FeatureSwitchingStates.DISABLED.key) {
                startActivity(IntentFactory.capabilityUnavailable(hostActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_archived_statement))))
            } else {
                navigateToArchivedStatement()
            }
            R.id.stampedStatementButton -> if (featureSwitchingToggles.stampedStatements == FeatureSwitchingStates.DISABLED.key) {
                startActivity(IntentFactory.capabilityUnavailable(hostActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_stamped_statement))))
            } else {
                navigateToStampedStatement()
            }
            R.id.csvStatementButton -> navigateToCsvStatement()
        }
    }

    private fun navigateToCsvStatement() {
        startActivity(IntentFactory.getCsvStatement(hostActivity, genericTransactionHistoryViewModel.accountDetail, featureTag))
        tagStatements("AccountHub_StatementsTab_ExportTransactionsCSVSelected")
    }

    private fun navigateToArchivedStatement() {
        if (operatorNotAllowed) {
            BaseAlertDialog.showRequestAccessAlertDialog(getString(R.string.archived_statement))
        } else {
            startActivity(IntentFactory.getArchivedStatement(hostActivity, genericTransactionHistoryViewModel.accountDetail, featureTag))
            tagStatements("AccountHub_StatementsTab_DownloadBankStatementSelected")
        }
    }

    private fun navigateToStampedStatement() {
        startActivity(IntentFactory.getStampedStatement(hostActivity, genericTransactionHistoryViewModel.accountDetail?.accountNumber, featureTag))
        tagStatements("AccountHub_StatementsTab_DownloadStampedStatementSelected")
    }

    override fun getTabDescription() = arguments?.getString(TAB_DESCRIPTION_KEY) ?: ""
}