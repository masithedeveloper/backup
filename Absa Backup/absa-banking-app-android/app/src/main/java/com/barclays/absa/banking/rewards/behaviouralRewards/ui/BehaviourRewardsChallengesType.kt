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
package com.barclays.absa.banking.rewards.behaviouralRewards.ui

enum class BehaviourRewardsChallengesType(val key: String) {
    MARKETING_CONSENT("X.1"),
    UPDATE_DETAILS("X.2"),
    ONLINE_SPEND("X.3"),
    POINT_OF_SALES("X.4"),
    CASHSEND("X.5")
}