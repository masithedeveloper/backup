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

import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty

import java.io.Serializable

class AccessPrivileges : ResponseObject(), Serializable {

    @JsonProperty("accessMessage")
    var accessMessage: String? = null

    @JsonProperty("isOperator")
    var isOperator: Boolean = false

    @JsonProperty("isInterAccountTransferAllowed")
    var interAccountTransferAllowed: Boolean = false

    @JsonProperty("isBeneficiaryPaymentAllowed")
    var beneficiaryPaymentAllowed: Boolean = false

    @JsonProperty("isCashSendAllowed")
    var cashSendAllowed: Boolean = false

    @JsonProperty("isPrepaidAllowed")
    var isPrepaidAllowed: Boolean = false

    var isPrepaidElectricityAllowed: Boolean = false

    init {
        this.accessMessage = ""
        this.interAccountTransferAllowed = true
        this.beneficiaryPaymentAllowed = true
        this.cashSendAllowed = true
        this.isPrepaidAllowed = true
        this.isPrepaidElectricityAllowed = true
    }

    companion object {

        @JvmStatic
        val instance = AccessPrivileges()

        fun updateInstance(accessPrivileges: AccessPrivileges) {
            instance.accessMessage = accessPrivileges.accessMessage
            instance.isOperator = accessPrivileges.isOperator
            instance.interAccountTransferAllowed = accessPrivileges.interAccountTransferAllowed
            instance.beneficiaryPaymentAllowed = accessPrivileges.beneficiaryPaymentAllowed
            instance.cashSendAllowed = accessPrivileges.cashSendAllowed
            instance.isPrepaidAllowed = accessPrivileges.isPrepaidAllowed
            instance.isPrepaidElectricityAllowed = accessPrivileges.isPrepaidElectricityAllowed
        }
    }
}