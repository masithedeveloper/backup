/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package za.co.absa.networking.hmac

import za.co.absa.networking.ExpressNetworkingConfig
import za.co.absa.networking.ExpressNetworkingConfig.applicationLocale
import za.co.absa.networking.hmac.utils.HmacUtils
import java.text.SimpleDateFormat
import java.util.*

class HmacCredentialContext {

    private var signatureDate: String = ""
    var deviceID: String = ""

    init {
        SimpleDateFormat("yyyyMMdd HHmmss", applicationLocale).apply {
            timeZone = TimeZone.getTimeZone("Africa/Johannesburg")
            Calendar.getInstance().apply {
                add(Calendar.MILLISECOND, ExpressNetworkingConfig.timeCorrection.toInt())
                signatureDate = format(time)
            }
        }
    }

    companion object {
        private const val END_STRING = "absa_za"
    }

    private val clientIdBase64Encoded: String
        get() = if (deviceID.isBlank()) "" else HmacUtils.encodeBase64NoWrapNoPaddingUrlSafe(deviceID)

    val signatureDateFormatted: String
        get() = signatureDate.substringBefore(" ")

    val signatureTimeFormatted: String
        get() = signatureDate.substringAfter(" ")

    override fun toString(): String = "$clientIdBase64Encoded/$signatureDateFormatted/$signatureTimeFormatted/$END_STRING"
}