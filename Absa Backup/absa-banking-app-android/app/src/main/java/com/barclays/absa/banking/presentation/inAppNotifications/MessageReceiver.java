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
package com.barclays.absa.banking.presentation.inAppNotifications;

import android.content.Context;

import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.imimobile.connect.core.messaging.ICMessage;
import com.imimobile.connect.core.messaging.ICMessagingReceiver;

public class MessageReceiver extends ICMessagingReceiver {

    @Override
    protected void onMessageReceived(final Context context, final ICMessage icMessage) {
        String message = icMessage.getMessage();
        String msgtid = icMessage.getTransactionId();
        BMBLogger.d("Messaging Register", "onMessageArrived msgtid =" + msgtid + "message =" + message);
    }
}
