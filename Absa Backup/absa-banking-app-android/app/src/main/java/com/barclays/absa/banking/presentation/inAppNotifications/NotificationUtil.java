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

import java.util.ArrayList;
import java.util.Collections;

class NotificationUtil {
    static ArrayList<InAppMessage> sortBeneficiariesByFirstName(ArrayList<InAppMessage> messages) {
        Collections.sort(messages, (o1, o2) -> {
            if (o1 != null && o1.getNotificationMessage() != null && o1.getNotificationMessage().getSubmittedAt() != null
                    && o2 != null && o2.getNotificationMessage() != null && o2.getNotificationMessage().getSubmittedAt() != null) {
                return (o2.getNotificationMessage().getSubmittedAt()).compareTo(o1.getNotificationMessage().getSubmittedAt());
            } else {
                return 0;
            }
        });
        return messages;
    }

    static ArrayList<InAppSection> sortSectionsByDate(ArrayList<InAppSection> messages) {
        Collections.sort(messages, (o1, o2) -> {
            if (o1 != null && o1.getLastMessage() != null && o1.getLastMessage().getNotificationMessage() != null && o1.getLastMessage().getNotificationMessage().getSubmittedAt() != null
                    && o2 != null && o2.getLastMessage() != null && o2.getLastMessage().getNotificationMessage() != null && o2.getLastMessage().getNotificationMessage().getSubmittedAt() != null) {
                return (o2.getLastMessage().getNotificationMessage().getSubmittedAt()).compareTo(o1.getLastMessage().getNotificationMessage().getSubmittedAt());
            } else {
                return 0;
            }
        });
        return messages;
    }
}
