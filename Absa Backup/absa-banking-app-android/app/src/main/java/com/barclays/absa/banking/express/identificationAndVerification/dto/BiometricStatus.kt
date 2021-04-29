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
 */

package com.barclays.absa.banking.express.identificationAndVerification.dto

enum class BiometricStatus {
    VERIFIED,
    ACTIVE,
    SUSPENDED,
    BLACKLISTED,
    INACTIVE,
    EXPIRED,
    IN_PROGRESS,
    NOT_VERIFIED,
    TECHNICAL_ERROR,
    BIOMETRICS_DISABLED,
    NOT_REGISTERED,
    SUSPICIOUS;

    companion object {
        @JvmStatic
        fun shouldAllowIdentifyFlow(status: BiometricStatus): Boolean {
            return status == VERIFIED || status == ACTIVE
        }

        @JvmStatic
        fun shouldNotFallBackToOldFlow(status: BiometricStatus): Boolean {
            val listOfStatusesNotAllowedToFallBack = arrayListOf(VERIFIED, ACTIVE, SUSPICIOUS, SUSPENDED, BLACKLISTED)
            return status !in listOfStatusesNotAllowedToFallBack
        }
    }
}