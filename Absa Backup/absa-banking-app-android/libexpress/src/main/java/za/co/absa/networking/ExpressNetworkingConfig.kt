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

package za.co.absa.networking

import android.content.Context
import java.util.*

object ExpressNetworkingConfig {
    const val REG_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgj7dlIBEZZ1tgMoMeiKiLHaEMb1zucwn_OFKm3fN_MKYlzIy7KM0QKker2PFPP05951xyn_tTwe-uYUREKKSpB86k_4HI3z2GtlmTo1rk6HW6A6zoK31Q2sUSVPuhnLI6g2wwuuGSWEzGKW2ILLJuEfGd3VVyqKxpmqZ7KsGF087USvwJpS-Iy93yPSu8Zf9rx6mbjJ0AhbQpnbYkC4aGb7VMBD4QkiRTaJZC-elUiC52bgbIjtdbbUIBx5KgEdhOLDDNOof0kd7C2jep3u6kWyy9M0P97QxBTK-yyYGE5ADEj0PiFCyrAHWwvW-Tle1x5TtiYdekYqPwsP3LsNbfQIDAQAB"
    const val REG_PUBLIC_KEY_ID = "device_registration_v1.0"

    lateinit var appContext: Context
    lateinit var applicationLocale: Locale
    lateinit var deviceId: String

    var tokenExpireTime: Date? = null
    var serverDateTime: Date = Date()
    var timeCorrection: Long = 0

    var appVersion = ""
    var publicKeyId: String = REG_PUBLIC_KEY_ID
    var publicKey: String = REG_PUBLIC_KEY

    var expressEnvironment: String = BuildConfig.EXPRESS_ENVIRONMENT

    @JvmStatic
    var hMacSecret: String = ""

    @JvmStatic
    var accessToken: String = ""

    var isLoggedIn: Boolean = false

    var sessionMap: MutableMap<String, SessionInfo> = mutableMapOf()

    fun setTokenExpiryTime(seconds: Int) {
        tokenExpireTime = if (seconds > 200) {
            Calendar.getInstance().apply { add(Calendar.SECOND, seconds - 60) }.time
        } else {
            Calendar.getInstance().apply { add(Calendar.SECOND, seconds / 2) }.time
        }
    }

    class SessionInfo {
        var nonce: String = ""
        var jsessionid: String = ""
    }
}