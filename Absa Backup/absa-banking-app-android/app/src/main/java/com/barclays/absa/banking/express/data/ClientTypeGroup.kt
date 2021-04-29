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
package com.barclays.absa.banking.express.data

enum class ClientTypeGroup(val value: String) {
    INDIVIDUAL_CLIENT("I"),
    JOINT_AND_SEVERAL("J"),
    SOLE_TRADER_CLIENT("S"),
    NON_INDIVIDUAL_CLIENT("N"),
    PARTNERSHIP_CLIENT("P"),
    ESTATE_CLIENT("E")
}

fun String.isBusiness(): Boolean {
    return equals(ClientTypeGroup.NON_INDIVIDUAL_CLIENT.value, true)
            || equals(ClientTypeGroup.SOLE_TRADER_CLIENT.value, true)
            || equals(ClientTypeGroup.PARTNERSHIP_CLIENT.value, true)
}