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

package com.barclays.absa.banking.manage.profile.ui

enum class OccupationStatus(val occupationCode: String) {
    FULL_TIME_EMPLOYED("01"),
    SELF_EMPLOYED_PROFESSIONAL("02"),
    SELF_EMPLOYED_NON_PROFESSIONAL("03"),
    HOUSEWIFE("04"),
    STUDENT("05"),
    UNEMPLOYED("06"),
    PENSIONER("07"),
    PART_TIME_CONTRACT_WORKER("08"),
    TEMPORARY_EMPLOYED("09"),
    SCHOLAR("10"),
}