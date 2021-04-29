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
package com.barclays.absa.banking.account.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccessPrivileges.Companion.instance
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.home.ui.AccountTypes
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.shared.ItemPagerFragment
import com.barclays.absa.utils.FilterAccountList
import kotlinx.android.synthetic.main.account_activity_statements_fragment.*

class AccountActivityStatementsFragment : ItemPagerFragment(R.layout.account_activity_statements_fragment) {
    private lateinit var accountActivity: AccountActivity
    private var operatorNotAllowed = false

    companion object {
        @JvmStatic
        fun newInstance(description: String?): AccountActivityStatementsFragment {
            return AccountActivityStatementsFragment().apply {
                this.arguments = Bundle().apply { putString(TAB_DESCRIPTION_KEY, description) }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        accountActivity = context as AccountActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accountActivity.accountObject?.let {
            operatorNotAllowed = instance.isOperator && (!it.isAccountDetailAllowed || "Y".equals(it.isBalanceMasked, ignoreCase = true))
            archivedStatementButton.visibility = if (operatorNotAllowed) View.GONE else View.VISIBLE
            csvStatementButton.visibility = if (operatorNotAllowed) View.GONE else View.VISIBLE
            stampedStatementButton.visibility = if (instance.isOperator) View.GONE else View.VISIBLE
        }

        archivedStatementButton.setOnClickListener {
            if (featureSwitchingToggles.archivedStatements == FeatureSwitchingStates.DISABLED.key) {
                startActivity(IntentFactory.capabilityUnavailable(accountActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_archived_statement))))
            } else {
                accountActivity.navigateToArchivedStatement()
            }
        }
        stampedStatementButton.setOnClickListener {
            if (featureSwitchingToggles.stampedStatements == FeatureSwitchingStates.DISABLED.key) {
                startActivity(IntentFactory.capabilityUnavailable(accountActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_stamped_statement))))
            } else {
                accountActivity.navigateToStampedStatement()
            }
        }
        csvStatementButton.setOnClickListener {
            accountActivity.navigateToCsvStatement()
        }

        setupFeatureSwitchingVisibilityToggles()
    }

    private fun setupFeatureSwitchingVisibilityToggles() {
        when (accountActivity.accountObject?.accountType) {
            AccountTypes.CREDIT_CARD -> setArchiveFeatureState(featureSwitchingToggles.creditCardArchivedStatements)
            AccountTypes.PERSONAL_LOAN -> setArchiveFeatureState(featureSwitchingToggles.personalLoanArchivedStatements)
            AccountTypes.ABSA_VEHICLE_AND_ASSET_FINANCE -> setArchiveFeatureState(featureSwitchingToggles.vehicleFinanceArchivedStatements)
            AccountTypes.HOME_LOAN -> setArchiveFeatureState(featureSwitchingToggles.homeLoanArchivedStatements)
        }
        if (!FilterAccountList.isArchivedStatementsAllowed(accountActivity.accountObject) || featureSwitchingToggles.archivedStatements == FeatureSwitchingStates.GONE.key) {
            archivedStatementButton.visibility = View.GONE
        }
        setStampedFeatureState()
    }

    private fun setArchiveFeatureState(state: Int) {
        setFeatureState(archivedStatementButton, state, getString(R.string.feature_switching_archived_statement))
    }

    private fun setStampedFeatureState() {
        if (!FilterAccountList.isStampedStatementsAllowed(accountActivity.accountObject) || featureSwitchingToggles.stampedStatements == FeatureSwitchingStates.GONE.key) {
            stampedStatementButton.visibility = View.GONE
        } else {
            setFeatureState(stampedStatementButton, FeatureSwitchingStates.ACTIVE.key, getString(R.string.feature_switching_stamped_statement))
        }
    }

    private fun setFeatureState(optionActionButtonView: View?, state: Int, featureName: String) {
        if (state == FeatureSwitchingStates.GONE.key) {
            optionActionButtonView?.visibility = View.GONE
        } else if (state == FeatureSwitchingStates.DISABLED.key) {
            optionActionButtonView?.setOnClickListener { startActivity(IntentFactory.capabilityUnavailable(activity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, featureName))) }
        }
    }

    override fun getTabDescription(): String = requireArguments().getString(TAB_DESCRIPTION_KEY).toString()
}