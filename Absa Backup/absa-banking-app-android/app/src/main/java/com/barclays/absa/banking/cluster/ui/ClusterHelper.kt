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

package com.barclays.absa.banking.cluster.ui

import android.content.Context
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.Entry
import com.barclays.absa.banking.boundary.model.Header
import com.barclays.absa.banking.boundary.model.policy.Policy
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.home.ui.AccountTypes

object ClusterHelper {

    fun getInvestmentAccounts(): List<String> = listOf(AccountTypes.NOTICE_DEPOSIT, AccountTypes.FIXED_DEPOSIT, AccountTypes.UNIT_TRUST_ACCOUNT, AccountTypes.SAVINGS_ACCOUNT)

    fun getSortedPolicyListWithHeader(list: List<Policy>, context: Context): ArrayList<Entry> {
        val sortedListWithHeader = ArrayList<Entry>()
        val longTermPolicies = ArrayList<Entry>()
        val shortTermPolicies = ArrayList<Entry>()

        list.forEach { policy ->
            when (policy.type) {
                BMBConstants.LONG_TERM_POLICY_TYPE, BMBConstants.EXERGY_POLICY_TYPE -> longTermPolicies.add(policy)
                BMBConstants.SHORT_TERM_POLICY_TYPE -> shortTermPolicies.add(policy)
            }
        }

        if (!longTermPolicies.isNullOrEmpty()) {
            longTermPolicies.add(0, Header(context.getString(R.string.insurance_cluster_life_and_funeral_cover_heading)))
        }

        if (!shortTermPolicies.isNullOrEmpty()) {
            shortTermPolicies.add(0, Header(context.getString(R.string.insurance_cluster_short_term_heading)))
        }

        sortedListWithHeader.addAll(longTermPolicies)
        sortedListWithHeader.addAll(shortTermPolicies)
        return sortedListWithHeader
    }
}