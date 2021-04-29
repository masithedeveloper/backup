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
package com.barclays.absa.banking.card.services.card

class CardMockFactory {

    companion object {

        fun creditCardProtection(): String {
            val choice = 1
            return creditCardProtection(choice)
        }

        fun creditCardLimitIncrease(firstCall: Boolean): String {
            val choice = 2
            return if (firstCall) {
                "vcl/op0906_vcl_application_response.json"
            } else {
                when (choice) {
                    1 -> "vcl/op0906_vcl_application_accepted.json"
                    2 -> "vcl/op0906_vcl_application_pending.json"
                    3 -> "vcl/op0906_vcl_application_declined.json"
                    4 -> "vcl/op0906_vcl_application_failed.json"
                    else -> "vcl/op0906_vcl_application_accepted.json"
                }
            }
        }

        private fun creditCardProtection(choice: Int): String {
            return when (choice) {
                1 -> "credit_card_insurance/op0864_fetch_credit_card_protection_insurance.json"
                else -> "credit_card_insurance/op0864_fetch_credit_card_protection_insurance_failed.json"
            }
        }

        fun creditCardInfo(): String {
            val choice = 0
            return when (choice) {
                1 -> "account/op0842_request_card_not_part_of_portfol_error.json"
                2 -> "account/op0842_request_card_not_linked.json"
                else -> "account/op0842_request_card_info.json"
            }
        }

        fun creditCardInsurancePolicy(): String {
            val choice = 1
            return creditCardInsurancePolicy(choice)
        }

        private fun creditCardInsurancePolicy(choice: Int): String {
            return when (choice) {
                1 -> "credit_card_insurance/op0863_credit_card_no_insurance_policy.json"
                else -> "credit_card_insurance/op0863_credit_card_insurance.json"
            }
        }

        fun debitCardStopAndReplaceClientContactInformation(): String {
            val choice = -1
            return when (choice) {
                1 -> "manage_cards/op0804_client_contact_information_null.json"
                else -> "manage_cards/op0804_client_contact_information.json"
            }
        }

        fun debitCardStopAndReplaceConfirmation(): String {
            val choice = -1
            return when (choice) {
                1 -> "manage_cards/op0807_replace_debit_card_confirmation_failure.json"
                2 -> "manage_cards/op0807_replace_debit_card_confirmation_status_null.json"
                else -> "manage_cards/op0807_replace_debit_card_confirmation.json"
            }
        }

        fun submitCreditProtection(): String {
            return "credit_card_insurance/op0865_submit_credit_card_protection_insurance_succesful.json"
        }

        fun applyForHotLead(): String {
            return when (-1) {
                1 -> "hot_leads/op2135_hot_lead_duplicate.json"
                2 -> "hot_leads/op2135_hot_lead_policy_decline.json"
                else -> "hot_leads/op2135_hot_lead_approved.json"
            }
        }
    }
}