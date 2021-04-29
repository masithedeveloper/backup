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
package com.barclays.absa.banking.rewards.services

class RewardsMockFactory {

    companion object {
        fun buyVoucherConfirmation(): String {
            return "rewards/op0912_buy_voucher_confirmation.json"
        }

        fun donateToCharityConfirmation(): String {
            return "rewards/op0913_donate_to_charity_confirmation.json"
        }
    }
}