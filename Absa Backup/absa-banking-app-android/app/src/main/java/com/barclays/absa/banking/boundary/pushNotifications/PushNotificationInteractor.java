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

import com.barclays.absa.banking.boundary.pushNotifications.dto.DeleteNotificationRecordRequest;
import com.barclays.absa.banking.boundary.pushNotifications.dto.ManagePushNotificationRecordRequest;
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;

public class PushNotificationInteractor implements PushNotificationService {

    public PushNotificationInteractor() {
    }

    @Override
    public void addPushNotificationRecord(String pushNotificationRegistrationToken, ExtendedResponseListener<TransactionResponse> responseListener) {
        ManagePushNotificationRecordRequest<TransactionResponse> request = new ManagePushNotificationRecordRequest<>(pushNotificationRegistrationToken, responseListener);
        request.setMockResponseFile("push_notifications/op0825_manage_push_notification_record.json");
        ServiceClient serviceClient = new ServiceClient(request);
        serviceClient.submitRequest();
    }

    @Override
    public void removePushNotificationRecord(String pushNotificationRegistrationToken, ExtendedResponseListener<TransactionResponse> responseListener) {
        DeleteNotificationRecordRequest<TransactionResponse> request = new DeleteNotificationRecordRequest<>(pushNotificationRegistrationToken, responseListener);
        request.setMockResponseFile("push_notifications/op0826_delete_push_notification_record.json");
        ServiceClient serviceClient = new ServiceClient(request);
        serviceClient.submitRequest();
    }
}