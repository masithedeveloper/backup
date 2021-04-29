/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.express.notificationDetails.dto

import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty

class AlertInfo : BaseModel {
    var date: String = ""
    var time: String = ""

    @JsonProperty("logonAlrt")
    var logonAlert: Boolean = false

    @JsonProperty("clientPaymentAlrt")
    var clientPaymentAlert: Boolean = false

    @JsonProperty("stopOrderAlrt")
    var stopOrderAlert: Boolean = false

    @JsonProperty("prepaidAlrt")
    var prepaidAlert: Boolean = false

    @JsonProperty("changePinAlrt")
    var changePinAlert: Boolean = false

    @JsonProperty("changePasswordAlrt")
    var changePasswordAlert: Boolean = false

    @JsonProperty("regOperatorAlrt")
    var regOperatorAlert: Boolean = false

    @JsonProperty("changeOperatorAlrt")
    var changeOperatorAlert: Boolean = false
    var beneficiaryCreated: Boolean = false

    @JsonProperty("paymentRejectedAlrt")
    var paymentRejectedAlert: Boolean = false

    @JsonProperty("outstandingAuthAlrt")
    var outstandingAuthAlert: Boolean = false

    @JsonProperty("fundTransferAlrt")
    var fundTransferAlert: Boolean = false

    @JsonProperty("notifToNewCell")
    var notificationToNewCell: Boolean = false

    @JsonProperty("notifToOldCell")
    var notificationToOldCell: Boolean = false

    @JsonProperty("chgNotifEventAlrt")
    var changeNotificationEventAlert: Boolean = false
    var futureDatedPaymentDue: Boolean = false
    var futureDatedInterAccountTransferDue: Boolean = false
    var recurringPaymentDue: Boolean = false
    var cellNumber: String = ""
    var faxNumber: String = ""
    var email: String = ""
    var preferredRVNMethod: String = ""

    @JsonProperty("preferredAlrtMethod")
    var preferredAlertMethod: String = ""
    var preferredPopMethod: String = ""
    var preferredGeneralMethod: String = ""
    var preferredReminderMethod: String = ""

    @JsonProperty("defaultClientPaymentAlrt")
    var defaultClientPaymentAlert: Boolean = false
    var simSwopHoldImposedDate: String = ""

    @JsonProperty("alrts")
    var alerts: List<String> = mutableListOf()

    @JsonProperty("ussdAlertInd")
    var ussdAlertIndicator: Boolean = false

    @JsonProperty("internationalPaymentsPrivilegesInd")
    var internationalPaymentsPrivilegesIndicator: Boolean = false
    var serviceProvider: String = ""

    @JsonProperty("profileImageInd")
    var profileImageIndicator: Boolean = false

    @JsonProperty("updateCIFDetAlrt")
    var updateCIFDetailAlert: Boolean = false
}