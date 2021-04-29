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
import za.co.absa.networking.hmac.utils.HmacUtils
import java.net.URI

class HmacSha256AuthorizationScheme {

    private lateinit var hmacSha256Signer: HmacSha256Signer

    lateinit var uri: URI
    lateinit var content: String
    lateinit var client: ClientDetails

    fun generateSha256Scheme(): HttpAuthorizationScheme {

        hmacSha256Signer = HmacSha256Signer(uri, content, client)
        hmacSha256Signer.sign()

        val httpAuthorizationHeader = HttpAuthorizationHeader().apply {
            credential = "Credential=" + hmacSha256Signer.credentialScope + ", SignedHeaders=" + hmacSha256Signer.signedHeaders + ", Signature=" + hmacSha256Signer.getSignature()
            authorizationType = HttpAuthorizationType.ABSA_HMAC_SHA256
        }

        return HttpAuthorizationScheme().apply {
            authorizationHeader = httpAuthorizationHeader
            requestHeaders = HmacUtils.convertMapListToMapString(hmacSha256Signer.httpHeaders())
        }
    }

    fun generateSha256JwtScheme(isPinRequest: Boolean = false): HttpAuthorizationScheme {
        HttpAuthorizationScheme().apply {

            val hmacResponseVO = generateSha256Scheme()

            requestHeaders = hmacResponseVO.requestHeaders

            val stringBuilder = StringBuilder().apply {
                append(hmacResponseVO.authorizationHeader.credential)
                append(", token=")
                append(ExpressNetworkingConfig.accessToken)
            }

            authorizationHeader = HttpAuthorizationHeader().apply {
                authorizationType = if (isPinRequest) HttpAuthorizationType.ABSA_HMAC_SHA256_JWT_PIN else HttpAuthorizationType.ABSA_HMAC_SHA256_JWT
                credential = stringBuilder.toString()
            }

            return this
        }
    }
}