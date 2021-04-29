/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.manage.devices.services.dto

import com.barclays.absa.banking.boundary.model.PINObject
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.api.request.params.TransactionParams.Transaction
import com.barclays.absa.banking.manage.devices.services.ManageDevicesService.OP0816_VALIDATE_PRIMARY_DEVICE_PASSCODE_AND_ATM_PIN
import com.barclays.absa.utils.DeviceUtils

class ValidatePrimaryDevicePasscodeAndAtmPinRequest<T>(primaryDevicePasscode: String, atmCardNumber: String,
                                                       atmPinBlock: PINObject, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        params = RequestParams.Builder()
                .put(OP0816_VALIDATE_PRIMARY_DEVICE_PASSCODE_AND_ATM_PIN)
                .put(Transaction.SERVICE_CHANNEL_IND, DeviceUtils.channelId)
                .put(Transaction.PRIMARY_DEVICE_PASSCODE, primaryDevicePasscode)
                .put(Transaction.ATM_CARD_NO, atmCardNumber)
                .put(Transaction.ATM_PIN_BLOCK, atmPinBlock.accessPin)
                .put(Transaction.AUTH_VIRTUAL_SERVER_ID, atmPinBlock.virtualSessionId)
                .put("mapID", atmPinBlock.mapId)
                .build()

        mockResponseFile = "alias/op0816_validate_primary_device_passocode_and_atm_pin.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = GenericValidationResponse::class.java as Class<T>

    override fun isEncrypted(): Boolean? = true
}