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
package com.barclays.absa.banking.overdraft.services

class OverdraftMockFactory {

    companion object {
        fun overdraftQuoteDetails(): String {
            val choice = -1
            return when (choice) {
                1 -> "overdraft/op2006_overdraft_quote_details_policy_declined.json"
                2 -> "overdraft/op2006_overdraft_quote_details_failure.json"
                3 -> "overdraft/op2006_overdraft_qoute_details_referral.json"
                else -> "overdraft/op2006_overdraft_quote_details_policy_success.json"
            }
        }

        fun overdraftAcceptQuote(): String {
            val choice = -1
            return when (choice) {
                11 -> "overdraft/op2008_overdraft_accept_quote_failure.json"
                else -> "overdraft/op2008_overdraft_accept_quote_success.json"
            }
        }

        fun overdraftQuoteStatus(): String {
            val choice = 1
            return when (choice) {
                1 -> "overdraft/op2010_overdraft_quote_status_success.json"
                else -> "overdraft/op2010_overdraft_quote_status_failure.json"
            }
        }

        fun overdraftRejectQuote(): String {
            val choice = 1
            return when (choice) {
                1 -> "overdraft/op2009_overdraft_reject_quote_success.json"
                else -> "overdraft/op2009_overdraft_reject_quote_failure.json"
            }
        }

        fun overdraftSummary(): String {
            val choice = 1
            return when (choice) {
                1 -> "overdraft/op2013_overdraft_quote_details_summary.json"
                else -> "overdraft/op2013_overdraft_qoute_details_summary_failure.json"
            }
        }

        fun overdraftScore(): String {
            val choice = 1
            return when (choice) {
                1 -> "overdraft/op2005_overdraft_score_success.json"
                2 -> "overdraft/op2005_overdraft_score_account_not_found.json"
                else -> "overdraft/op2005_overdraft_score_failure.json"
            }
        }

        fun overdraftFICAAndCIFStatus(): String {
            val choice = -1
            return when (choice) {
                15 -> "overdraft/op2004_overdraft_fica_cif_status_failure.json"
                20 -> "overdraft/op2004_overdraft_fica_cif_status_missing_mandatory_details.json"
                else -> "overdraft/op2004_overdraft_fica_cif_status_yes.json"
            }
        }

        fun overdraftStatus(): String {
            val choice = -1
            return when (choice) {
                11 -> "account/op0827_overdraft_status_zero.json"
                22 -> "account/op0827_overdraft_status_none.json"
                else -> "overdraft/op0827_overdraft_status.json"
            }
        }

        fun personalInformationDetails(): String {
            val choice = -1
            return when (choice) {
                1 -> "overdraft/op2012_overdraft_marketing_consent_no_email_success.json"
                else -> "overdraft/op2012_fetch_personal_information.json"
            }
        }

        fun applyBusinessOverdraft(): String {
            val choice = 1
            return when (choice) {
                1 -> "overdraft/op2157_apply_business_overdraft_success.json"
                else -> "overdraft/op2157_apply_business_overdraft_failure.json"
            }
        }
    }
}