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
package com.barclays.absa.banking.presentation.homeLoan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.policy.PolicyDetail
import com.barclays.absa.banking.framework.utils.TelephoneUtil
import com.barclays.absa.banking.funeralCover.ui.InsurancePolicyClaimsBaseActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.shared.BaseAlertDialog.showGenericErrorDialog
import com.barclays.absa.banking.shared.ItemPagerFragment
import com.barclays.absa.banking.shared.genericTransactionHistory.ui.GenericTransactionHistoryViewModel
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.TextFormatUtils
import kotlinx.android.synthetic.main.home_loan_account_insurance_fragment.*
import styleguide.content.Contact
import styleguide.utils.extensions.toFormattedAccountNumber
import styleguide.utils.extensions.toTitleCaseRemovingCommas

class HomeLoanAccountInsuranceFragment : ItemPagerFragment(R.layout.home_loan_account_insurance_fragment) {
    private lateinit var genericTransactionHistoryViewModel: GenericTransactionHistoryViewModel
    private lateinit var homeLoanPerilsHubActivity: HomeLoanPerilsHubActivity

    companion object {
        fun newInstance(description: String?, policyDetail: PolicyDetail?): HomeLoanAccountInsuranceFragment {
            return HomeLoanAccountInsuranceFragment().apply {
                val arguments = Bundle()
                arguments.putString(TAB_DESCRIPTION_KEY, description)
                arguments.putSerializable(InsurancePolicyClaimsBaseActivity.POLICY_DETAIL, policyDetail)
                this.arguments = arguments
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeLoanPerilsHubActivity = (context as HomeLoanPerilsHubActivity)
        genericTransactionHistoryViewModel = homeLoanPerilsHubActivity.statementViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callCentreContactView.setContact(Contact(getString(R.string.call_centre), getString(R.string.call_centre_times)))
        callCentreContactView.setOnClickListener { TelephoneUtil.call(activity, getString(R.string.claim_support_number)) }
        val policyDetail = arguments?.getSerializable(HomeLoanPerilsHubActivity.POLICY_DETAIL) as PolicyDetail?
        if (policyDetail != null) {
            displayPolicyDetails(policyDetail)
        } else {
            hidePolicyDetails()
        }
        submitClaimButton.setOnClickListener {
            if (policyDetail != null) {
                navigateToClaimsDetailsScreen(policyDetail)
            } else {
                startActivity(IntentFactory.getFailureResultScreen(homeLoanPerilsHubActivity, R.string.claim_error_text, R.string.try_later_text))
            }
        }
    }

    private fun navigateToClaimsDetailsScreen(policyDetail: PolicyDetail) {
        Intent(homeLoanPerilsHubActivity, HomeloanPerilsClaimDetailsActivity::class.java).apply {
            putExtra(HomeLoanPerilsHubActivity.POLICY_DETAIL, policyDetail)
            startActivity(this)
        }
    }

    private fun hidePolicyDetails() {
        propertyInsuranceContainer.visibility = View.GONE
    }

    private fun displayPolicyDetails(policyDetail: PolicyDetail) {
        val policy = policyDetail.policy
        if (policy != null) {
            premiumAmountView.setLineItemViewContent(TextFormatUtils.formatBasicAmount(policy.monthlyPremium))
            coverAmountView.setLineItemViewContent(TextFormatUtils.formatBasicAmount(policy.coverAmount))
            statusView.setLineItemViewContent(policy.status)
            if (policyDetail.accountInfo != null) {
                endDateView.setLineItemViewContent(DateUtils.getDateWithMonthNameFromHyphenatedString(policyDetail.policy!!.renewalDate))
                premiumFrequencyView.setLineItemViewContent(policyDetail.accountInfo!!.premiumFrequency.toTitleCaseRemovingCommas())
            } else {
                showGenericErrorDialog()
            }
            startDatView.setLineItemViewContent(DateUtils.getDateWithMonthNameFromHyphenatedString(policyDetail.inceptionDate))
            propertyInsuranceTitleView.description = policy.number.toFormattedAccountNumber()
            contactUsContainer.visibility = View.GONE
        } else {
            hidePolicyDetails()
            showGenericErrorDialog()
        }
    }

    override fun getTabDescription(): String {
        return arguments?.getString(TAB_DESCRIPTION_KEY).toString()
    }
}