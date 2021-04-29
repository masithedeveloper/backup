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
package com.barclays.absa.banking.boundary.model

import com.barclays.absa.banking.express.identificationAndVerification.dto.BiometricStatus
import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class CustomerProfileObject : ResponseObject(), Serializable {
    @JsonProperty("idNoRequired")
    var idNumberRequired: Boolean = false
    var reachedDeviceMaxLimit: Boolean = false
    var sessionId: String? = null
    var clientTypeGroup: String = ""
    var clientType: String? = null
    var customerType: String? = null
    var surePhrase: String? = null
    var authPassNotRegistered: String? = null
    var limitsNotSet: String? = null
    var alias: String? = null
    var secondFactorState = 0
    var cellNumber: String? = null
    var numberOfAuthorisations: String = "0"
    var mailboxProfileId: String? = null
    var newMailboxProfileId: Boolean? = null
    var userId: String? = null
    var permanentUserId: String? = null
    var customerSessionId: String? = null
    var accessAccount: String = ""
    var secondaryCardAccessBits: Int = 0
    var biometricStatus: BiometricStatus = BiometricStatus.BIOMETRICS_DISABLED

    var title: String = ""
    var initials: String = ""
    var sbuSegment: String = ""
    var sbuSubSegment: String = ""

    var idType: String = ""
    var idNumber: String = ""

    @JsonProperty("custName")
    var customerName: String? = null

    @JsonProperty("lastLgnTm")
    var lastLoginTime: String? = null

    @JsonProperty("langCode")
    var languageCode: String = "E"

    @JsonProperty("transactionalUser")
    var isTransactionalUser = false

    fun clear() {
        instance = CustomerProfileObject()
    }

    companion object {
        @JvmStatic
        var instance: CustomerProfileObject = CustomerProfileObject()

        @JvmStatic
        fun updateCustomerProfileObject(customerProfileObject: CustomerProfileObject) {
            instance.reachedDeviceMaxLimit = customerProfileObject.reachedDeviceMaxLimit
            instance.sessionId = customerProfileObject.sessionId
            instance.cellNumber = customerProfileObject.cellNumber
            instance.clientTypeGroup = customerProfileObject.clientTypeGroup
            instance.clientType = customerProfileObject.clientType
            instance.customerType = customerProfileObject.customerType
            instance.customerName = customerProfileObject.customerName
            instance.lastLoginTime = customerProfileObject.lastLoginTime
            instance.surePhrase = customerProfileObject.surePhrase
            instance.languageCode = customerProfileObject.languageCode
            instance.authPassNotRegistered = customerProfileObject.authPassNotRegistered
            instance.limitsNotSet = customerProfileObject.limitsNotSet
            instance.numberOfAuthorisations = customerProfileObject.numberOfAuthorisations
            instance.mailboxProfileId = customerProfileObject.mailboxProfileId
            instance.idNumberRequired = customerProfileObject.idNumberRequired
            instance.newMailboxProfileId = customerProfileObject.newMailboxProfileId
            instance.secondFactorState = customerProfileObject.secondFactorState
            instance.secondaryCardAccessBits = customerProfileObject.secondaryCardAccessBits

            instance.title = customerProfileObject.title
            instance.initials = customerProfileObject.initials
            instance.accessAccount = customerProfileObject.accessAccount
            instance.sbuSegment = customerProfileObject.sbuSegment
            instance.sbuSubSegment = customerProfileObject.sbuSubSegment
            instance.isTransactionalUser = customerProfileObject.isTransactionalUser

            instance.idType = customerProfileObject.idType
            instance.idNumber = customerProfileObject.idNumber
            instance.biometricStatus = customerProfileObject.biometricStatus

            if (instance.userId == null || customerProfileObject.customerSessionId != null) {
                instance.customerSessionId = customerProfileObject.customerSessionId
                instance.permanentUserId = customerProfileObject.permanentUserId
                instance.userId = customerProfileObject.userId
            }
            if (instance.alias == null) {
                instance.alias = customerProfileObject.alias
            }
        }
    }
}