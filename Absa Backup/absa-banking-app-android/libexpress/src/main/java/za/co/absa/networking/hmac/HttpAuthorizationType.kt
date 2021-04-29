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

enum class HttpAuthorizationType(private val type: String) {
    NONE("none"),
    BASIC("Basic"),
    BEARER("Bearer"),
    DIGEST("Digest"),
    HOBA("HOBA"),
    MUTUAL("MUTUAL"),
    AWS4_HMAC_SHA256("AWS4-HMAC-SHA256"),
    ABSA_HMAC_SHA256("ABSA-HMAC-SHA256"),
    ABSA_HMAC_SHA256_JWT("ABSA-HMAC-SHA256-JWT"),
    ABSA_HMAC_SHA256_PIN("ABSA-HMAC-SHA256-PIN"),
    ABSA_HMAC_SHA256_JWT_PIN("ABSA-HMAC-SHA256-JWT-PIN");

    override fun toString(): String {
        return type
    }
}