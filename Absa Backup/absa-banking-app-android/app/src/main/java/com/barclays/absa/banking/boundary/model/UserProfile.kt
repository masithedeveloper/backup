/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.boundary.model

import java.io.Serializable

data class UserProfile(
        var customerName: String? = "",
        var backgroundImageId: String? = "",
        var dateTimestamp: String? = "",
        var imageName: String? = "",
        var userId: String? = "",
        var clientType: String? = "",
        var alias: ByteArray? = null,
        var randomAliasId: ByteArray? = null,
        var fingerprintId: ByteArray? = null,
        var isTwoFAEnabled: Boolean,
        var languageCode: String = "E",
        var migrationVersion: Int,
        var mailboxId: String = "",
        var userNumber: Int? = 1) : Serializable {

    constructor() : this(isTwoFAEnabled = true, languageCode = "E", migrationVersion = CURRENT_PROFILE_MIGRATION_VERSION)

    companion object {
        const val CURRENT_PROFILE_MIGRATION_VERSION = 2
    }
}
