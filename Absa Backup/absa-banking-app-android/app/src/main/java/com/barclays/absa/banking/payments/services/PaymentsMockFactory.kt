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
package com.barclays.absa.banking.payments.services

class PaymentsMockFactory {

    companion object {
        fun multiplePaymentsResult(): String {
            val choice = 3

            return when (choice) {
                1 -> "multiple_payments/op0528_multiple_payments_result_three.json"
                2 -> "multiple_payments/op0528_multiple_payments_result_three_success.json"
                3 -> "multiple_payments/op0528_multiple_payments_two_normal_successful_one_iip_failure.json"
                else -> "multiple_payments/op0528_multiple_payments_result.json"
            }
        }

        fun westernUnionExchangeRate(): String {
            val option = 1
            return when (option) {
                1 -> "international_payments/op2069_exchange_rate_no_value.json"
                else -> "international_payments/op2069_exchange_rate.json"
            }
        }
    }
}