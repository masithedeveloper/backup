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

package com.barclays.absa.banking.boundary.model.airtime

import android.util.Base64
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.IOException

class AddedBeneficiary {

    @JsonProperty("benId")
    var beneficiaryId: String = ""

    @JsonProperty("benType")
    var beneficiaryType: String = ""

    @JsonProperty("benNam")
    var beneficiaryName: String = ""

    @JsonProperty("cellNo")
    var cellNumber: String = ""

    @JsonProperty("isFav")
    var isFavourite: String = ""

    @JsonProperty("networkProviderName")
    var networkProviderName: String = ""

    @JsonProperty("benImage")
    var beneficiaryImage: String = ""
        set(value) {
            field = value
            try {
                imageData = Base64.decode(value, Base64.URL_SAFE)
            } catch (e: IOException) {
                BMBLogger.e("UNABLE TO DECODE IMAGE DATA")
            }
        }

    var imageData: ByteArray? = null
        set(value) {
            println("setImageData to $value")
            field = value
        }
}