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
package com.barclays.absa.banking.lawForYou.ui

data class LawForYouDetails(
        var cellNumber: String = "",
        var emailAddress: String = "",
        var addressLine1: String = "",
        var addressLine2: String = "",
        var suburb: String = "",
        var city: String = "",
        var postalCode: String = "",
        var country: String = "",
        var coverPremiumAmount: String = "",
        var coverAssuredAmount: String = "",
        var dayOfDebit: String = "",
        var inceptionDate: String = "",
        var businessSourceIndicator: String = "",
        var accountToBeDebited: String = "",
        var coverPlan: String = "")