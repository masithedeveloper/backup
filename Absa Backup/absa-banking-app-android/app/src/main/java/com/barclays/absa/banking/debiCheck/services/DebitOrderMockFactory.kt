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
package com.barclays.absa.banking.debiCheck.services

class DebitOrderMockFactory {
    companion object {
        fun debitOrderList(choice: Int): String {
            return when (choice) {
                1 -> "debit_orders/op2110_search_debit_order_success.json"
                else -> "debit_orders/op2110_search_debit_order_failure.json"
            }
        }

        fun stoppedDebitOrderList(choice: Int): String {
            return when (choice) {
                1 -> "debit_orders/op2111_stopped_debit_order_success.json"
                else -> "debit_orders/op2111_stopped_debit_order_failure.json"
            }
        }

        fun stopDebitOrder(): String {
            return "debit_orders/op2113_stopped_debit_order_success.json"
        }

        fun reverseDebitOrder(): String {
            return "debit_orders/op2114_reverse_debit_order_success.json"
        }
    }
}