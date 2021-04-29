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
package com.barclays.absa.banking.boundary.pushNotifications;

import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse;
import com.barclays.absa.banking.framework.ExtendedResponseListener;

public interface PushNotificationService {
    String OP0825_MANAGE_PUSH_NOTIFICATION_RECORD = "OP0825";
    String OP0826_DELETE_PUSH_NOTIFICATION_RECORD = "OP0826";

    void addPushNotificationRecord(String pushNotificationRegistrationToken, ExtendedResponseListener<TransactionResponse> responseListener);
    void removePushNotificationRecord(String pushNotificationRegistrationToken, ExtendedResponseListener<TransactionResponse> responseListener);
}