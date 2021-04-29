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
package com.barclays.absa.banking.framework.app

class AppVersion(appVersion: String) {

    var major = 0
    var minor = 0
    var patch = 0

    init {
        val appVersionSplit = appVersion.split(".")
        if (appVersionSplit.isNotEmpty()) {
            major = appVersionSplit[0].toInt()
        }
        if (appVersionSplit.size > 1) {
            minor = appVersionSplit[1].toInt()
        }
        if (appVersionSplit.size > 2) {
            patch = appVersionSplit[2].toInt()
        }
    }

    fun compareTo(other: AppVersion): Int {
        return when {
            major != other.major -> major.compareTo(other.major)
            minor != other.minor -> minor.compareTo(other.minor)
            patch != other.patch -> patch.compareTo(other.patch)
            else -> 0
        }
    }
}