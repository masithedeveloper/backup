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

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.framework.BuildConfigHelper
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.push.CustomTags
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.barclays.absa.crypto.SecureUtils
import com.barclays.absa.utils.ProfileManager
import com.barclays.absa.utils.SharedPreferenceService
import com.imimobile.connect.core.ICDeviceProfile
import com.imimobile.connect.core.IMIconnect
import com.imimobile.connect.core.enums.ICDeviceProfileParam
import com.imimobile.connect.core.enums.ICMessageStatus
import com.imimobile.connect.core.exception.ICException
import com.imimobile.connect.core.messaging.ICMessage
import com.imimobile.connect.core.messaging.ICMessaging
import com.imimobile.connect.core.messaging.ICThread
import java.util.*
import kotlin.collections.ArrayList

class InAppNotificationViewModel : BaseViewModel() {
    var registrationComplete = MutableLiveData<Boolean>()
    var onDeleteCompleted = MutableLiveData<Boolean>()
    var inAppMessageThreads = MutableLiveData<ArrayList<InAppSection>>()
    var inAppTreadMessages = MutableLiveData<ArrayList<InAppMessage>>()

    val currentDate: Date = Calendar.getInstance().time
    var hasMoreMessagesAvailable: Boolean = false
    var customTags: CustomTags = CustomTags()
    var lastSelectedSection: InAppSection = InAppSection()
    private val appCacheService: IAppCacheService = getServiceInterface()

    fun registerForInAppMessages() {
        val customerProfile = CustomerProfileObject.instance
        if (!BuildConfigHelper.STUB) {
            updateActiveUserProfile(customerProfile)
            if (!appCacheService.isImiSessionActive()) {
                if (SharedPreferenceService.hasDeviceUnregisteredFromIMI()) {
                    registerUser(customerProfile)
                } else {
                    unregisterOnIMIConnect()
                }
            } else {
                registrationComplete.value = true
            }
        } else {
            registrationComplete.value = true
        }
    }

    private fun registerUser(customerProfile: CustomerProfileObject) {
        try {
            if (!customerProfile.mailboxProfileId.isNullOrEmpty() && !IMIconnect.getDeviceProfile()?.appUserId.equals(customerProfile.mailboxProfileId, ignoreCase = true)) {
                IMIconnect.register(ICDeviceProfile(SecureUtils.getDeviceID(), customerProfile.mailboxProfileId)) { _: Bundle?, _: ICException? ->
                    updateProfileData(customerProfile)
                }
            } else {
                updateProfileData(customerProfile)
            }
        } catch (e: Exception) {
            BMBLogger.e(e.message)
        }
    }

    private fun updateProfileData(customerProfile: CustomerProfileObject) {
        registrationComplete.value = true
        appCacheService.setImiProfileRegistered(true)
        try {
            IMIconnect.updateProfileData(ICDeviceProfileParam.CustomerId, customerProfile.mailboxProfileId) { _: Bundle?, exception: ICException? ->
                exception?.let { BMBApplication.getInstance().logCaughtException(it) }
                appCacheService.setImiSessionActive(true)
            }
        } catch (e: Exception) {
            BMBApplication.getInstance().logCaughtException(e)
        }
    }

    private fun updateActiveUserProfile(customerProfile: CustomerProfileObject) {
        val userProfile = ProfileManager.getInstance().activeUserProfile
        if (userProfile != null && (userProfile.mailboxId.isEmpty() || userProfile.mailboxId != customerProfile.mailboxProfileId)) {
            customerProfile.mailboxProfileId?.let { userProfile.mailboxId = it }
            ProfileManager.getInstance().updateProfile(userProfile, null)
        }
    }

    private fun unregisterOnIMIConnect() {
        val customerProfile = CustomerProfileObject.instance
        if (IMIconnect.isRegistered()) {
            IMIconnect.unregister { _: Bundle?, e: ICException? ->
                if (e == null) {
                    SharedPreferenceService.unregisterDeviceFromIMI()
                    registerUser(customerProfile)
                }
            }
        } else {
            registerUser(customerProfile)
        }
    }

    fun buildNotificationSectionList() {
        var threadCounter = 0;
        if (BuildConfig.STUB) {
            inAppMessageThreads.value = InAppStubUtility().buildThreadSectionList()
            return
        }
        val inAppSections: ArrayList<InAppSection> = arrayListOf()
        try {
            if (IMIconnect.isStarted() && IMIconnect.isRegistered()) {
                ICMessaging.getInstance().connect()
                ICMessaging.getInstance().fetchThreads(currentDate, 15) { threads: Array<ICThread>?, _: Boolean, exception: ICException? ->
                    exception?.let { return@fetchThreads }

                    if (threads.isNullOrEmpty()) {
                        inAppMessageThreads.value = inAppSections
                        return@fetchThreads
                    }

                    threads.forEach { threadItem ->
                        ICMessaging.getInstance().fetchMessages(threadItem.id.trim { it <= ' ' }, currentDate, 1) { icMessages: Array<ICMessage>?, _: Boolean, _: ICException? ->
                            if (!icMessages.isNullOrEmpty()) {
                                val newestMessage = icMessages[0]
                                InAppSection().apply {
                                    threadId = threadItem
                                    lastMessage = InAppMessage().apply {
                                        notificationMessage = newestMessage
                                        messageRead = newestMessage.readAt != null
                                    }
                                    inAppSections.add(this)
                                }
                            }

                            threadCounter++
                            if (threadCounter == threads.size) {
                                inAppMessageThreads.value = inAppSections
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hasUnreadMessages(): Boolean {
        if (IMIconnect.isRegistered()) {
            inAppMessageThreads.value?.forEach { sectionItem ->
                if (!sectionItem.lastMessage.messageRead) {
                    return true
                }
            }
        }
        return false
    }

    fun fetchInAppMessages(threadId: String, beforeData: Date) {
        if (BuildConfig.STUB) {
            buildListOfMessages(InAppStubUtility().buildMessageSectionList().toTypedArray())
            return
        }
        ICMessaging.getInstance().fetchMessages(threadId.trim { it <= ' ' }, beforeData, 20) { icMessages: Array<ICMessage>?, hasMoreMessagesAvailable: Boolean, _: ICException? ->
            this.hasMoreMessagesAvailable = hasMoreMessagesAvailable
            icMessages?.let { buildListOfMessages(it) }
        }
    }

    private fun buildListOfMessages(messages: Array<ICMessage>?) {
        val inAppMessages: ArrayList<InAppMessage> = ArrayList()
        messages?.forEach { messageItem ->
            InAppMessage().apply {
                notificationMessage = messageItem
                if (ICMessageStatus.Read == messageItem.status) {
                    messageRead = true
                }
                inAppMessages.add(this)
            }
        }
        inAppTreadMessages.value = inAppMessages
    }

    fun markAllMessagesAsRead(messageId: String?) {
        if (IMIconnect.isRegistered()) {
            ICMessaging.getInstance().setMessageAsRead(messageId) { _: Array<String>?, e: ICException? ->
                if (e != null) {
                    BMBLogger.e("MessageAsRead", e.toString())
                } else {
                    BMBLogger.d("MessageAsRead", "Updated Successfully")
                }
            }
        }
    }

    fun deleteMessage(message: ICMessage) {
        ICMessaging.getInstance().deleteMessage(message.transactionId) { _: String?, _: ICException? ->
            onDeleteCompleted.value = true
        }
    }
}