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

import android.util.Base64
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.IOException

open class AddBeneficiaryObject : TransactionResponse() {

    @JsonProperty("benId")
    open var beneficiaryId: String? = null

    @JsonProperty("benTyp")
    open var beneficiaryType: String? = null

    @JsonProperty("imageTimeStamp")
    var timestamp: String? = null
    var imageName: String? = null
    var userNumber: String? = null

    open var status: String? = null
    open var msg: String? = null

    @JsonProperty("benImage")
    var benImage: String? = null
        set(value) {
            field = value
            if (value != null)
                try {
                    imageData = Base64.decode(value, Base64.URL_SAFE)
                } catch (e: IOException) {
                    BMBLogger.e("UNABLE TO DECODE IMAGE DATA")
                }
        }

    var imageData: ByteArray? = null
        set(value) {
            println("setImageData to " + value)
            field = value
        }

    fun setImageData() {
        if (benImage != null)
            try {
                imageData = Base64.decode(benImage, Base64.URL_SAFE)
            } catch (e: IOException) {
                BMBLogger.e("UNABLE TO DECODE IMAGE DATA")
            }
    }

    override fun toString(): String {
        return ("Ben ID: " + beneficiaryId
                + " Ben Type: " + beneficiaryType
                + " Image Name: " + imageName)
    }
}