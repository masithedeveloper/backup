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
package com.barclays.absa.banking.beneficiaries.services

class BeneficiariesMockFactory {
    companion object {
        fun beneficiaries(): String {
            return "beneficiaries/op0310_view_beneficiaries.json"
        }

        fun beneficiaryDetails(): String {
            val choice = -1
            return when (choice) {
                1 -> "beneficiaries/op0311_electricity_beneficiary_details.json"
                else -> "beneficiaries/op0311_beneficiary_details.json"
            }
        }

        fun airtimeBeneficiaries(): String {
            return "beneficiaries/op0310_view_beneficiaries_airtime.json"
        }

        fun uploadBeneficiaryImage(): String {
            return "beneficiaries/op0402_beneficiary_image_upload.json"
        }

        fun addPaymentConfirmation(): String {
            return "beneficiaries/op0329_add_payment_confirmation.json"
        }

        fun addPaymentResults(): String {
            return "beneficiaries/op0330_add_payment_result.json"
        }

        fun editAirtimeConfirmation(): String {
            return "beneficiaries/op0332_edit_airtime_confirm.json"
        }

        fun editPaymentConfirmation(): String {
            return "beneficiaries/op0332_edit_payment_confirmation.json"
        }

        fun beneficiaryPaymentConfirmation(): String {
            return "beneficiaries/op0525_beneficiary_payment_confirmation.json"
        }

        fun editAirtimeResults(): String {
            return "beneficiaries/op0333_edit_airtime_result.json"
        }

        fun editPaymentResults(): String {
            return "beneficiaries/op0333_edit_payment_result.json"
        }

        fun beneficiaryPaymentResults(): String {
            return "beneficiaries/op0526_beneficiary_payment_result.json"
        }

        fun editPrepaidElectricityBeneficiaryResults(): String {
            val choice = 1
            return when (choice) {
                1 -> "beneficiaries/op2086_edit_prepaid_electricity_beneficiary_success.json"
                else -> "beneficiaries/op2086_edit_prepaid_electricity_beneficiary_failure.json"
            }
        }

        fun saveBeneficiary(): String {
            return "beneficiaries/op0334_rebuild_save_beneficiary.json"
        }

        fun beneficiaryImageDownload(): String {
            return "beneficiaries/op0401_beneficiary_image_download.json"
        }

        fun onceOffCashSendEncyption(): String {
            return "beneficiaries/op0609_onceoff_cashsend_encryption.json"
        }

        fun addPaymentOwnNotification(): String {
            return "beneficiaries/op0786_add_payment_own_notification.json"
        }

    }
}