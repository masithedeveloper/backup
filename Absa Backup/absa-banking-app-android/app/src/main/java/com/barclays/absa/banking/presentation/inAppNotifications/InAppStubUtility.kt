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

import com.imimobile.connect.core.messaging.ICMessage
import com.imimobile.connect.core.messaging.ICThread
import java.util.*

class InAppStubUtility {

    fun buildThreadSectionList(): ArrayList<InAppSection> {
        val inAppSections: ArrayList<InAppSection> = arrayListOf()

        inAppSections.add(InAppSection().apply {
            threadId = ICThread().apply { title = "Notifications" }
            lastMessage = InAppMessage().apply {
                messageRead = false
                notificationMessage = ICMessage().apply {
                    messageRead = false
                    message = "You have logged on to Banking App account no ending in 0000 On: 22-07-21 09:41:11. For login queries: 0860 0860, if you suspect fraud: 0860 557 557."
                }
            }
        })

        inAppSections.add(InAppSection().apply {
            threadId = ICThread().apply { title = "Notifications" }
            lastMessage = InAppMessage().apply {
                messageRead = true
                notificationMessage = ICMessage().apply {
                    messageRead = true
                    message = "You have logged on to Banking App account no ending in 0000 On: 22-07-21 09:41:11. For login queries: 0860 0860, if you suspect fraud: 0860 557 557."
                }
            }
        })

        return inAppSections
    }

    fun buildMessageSectionList(): MutableList<ICMessage> {
        val inAppSections: MutableList<ICMessage> = mutableListOf()

        inAppSections.add(ICMessage().apply {
            message = "You have logged on to Banking App account no ending in 0000 On: 22-07-21 09:41:11. For login queries: 0860 0860, if you suspect fraud: 0860 557 557."
            readAt = null
        })

        inAppSections.add(ICMessage().apply {
            message = "You have logged on to Banking App account no ending in 0000 On: 22-07-21 09:41:11. For login queries: 0860 0860, if you suspect fraud: 0860 557 557."
            readAt = Date()
        })

        return inAppSections
    }
}