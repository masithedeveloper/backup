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
package com.barclays.absa.banking.presentation.inAppNotifications

enum class InAppTreads(val threadId: String) {
    BANKING_TIPS_THREAD("225fcafc-33b7-4d13-af03-9c01458db991"),
    SASOL_THREAD("d274abf6-3c24-484f-a85a-4910702ac003"),
    ALERTS_THREAD("66fed51d-07ce-4e57-a833-afb1f9a5fbac"),
    GENERAL_THREAD("2cc16ec9-3791-4138-bdb6-5b9004959630"),
    MARKETING_THREAD("934921d5-ee1a-40af-84c2-11369386ce62"),
    BANKING_APP_THREAD("fbb01ea8-ea0b-46d7-b8ec-c4f7a2adf70a"),
    BANKING_APP_THREAD_TWO("1988e194-0d43-486b-afae-e2b30e77c648"),
    BANKING_APP_THREAD_THREE("698d22a4-f8da-4d29-8e85-2e6fefdbac77"),
    BANKING_APP_THREAD_FOUR("0d2ac92f-59fb-4a1c-bd45-610a68dde507")
}