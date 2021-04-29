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
package com.barclays.absa.banking.framework.push

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
class PushNotificationExtras(@JsonProperty("customtags")
                             var customTags: CustomTags = CustomTags(),
                             @JsonProperty("notificationaction")
                             var notificationAction: NotificationAction? = NotificationAction()) : Parcelable

@Parcelize
class CustomTags(@JsonProperty("mailbox_id")
                 var mailboxId: String = "",
                 @JsonProperty("thread_id")
                 var threadId: String = "") : Parcelable

@Parcelize
class NotificationAction(@JsonProperty("action")
                         var action: String = "") : Parcelable
