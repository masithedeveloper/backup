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

import za.co.absa.networking.Logger
import za.co.absa.networking.hmac.interfaces.HmacSigner
import java.math.BigInteger
import java.net.URI
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList

class HmacSha256Signer(val uri: URI, var content: String, var clientDetails: ClientDetails, private val isPinEncryptionRequest: Boolean = false) : HmacSigner {

    private lateinit var absaHmacCredentialScope: HmacCredentialContext
    private lateinit var hmacSignature: String
    private var absaHmacAlgorithmName: String = if (isPinEncryptionRequest) ABSA_HMAC_ALGORITHM_NAME_SHA256_JWT_PIN else ABSA_HMAC_ALGORITHM_NAME_SHA256

    private val hmacHeaders: MutableMap<String, String> = HashMap()

    companion object {
        private const val SIGNATURE_DELIMITER = ":"
        private const val MAC_ALGORITHM_HMAC_SHA256 = "HmacSHA256"
        private var ABSA_HMAC_ALGORITHM_NAME_SHA256: String = HttpAuthorizationType.ABSA_HMAC_SHA256.toString()
        private var ABSA_HMAC_ALGORITHM_NAME_SHA256_JWT_PIN: String = HttpAuthorizationType.ABSA_HMAC_SHA256_JWT_PIN.toString()
        private var SIGNATURE_HEADERS: MutableList<String> = ArrayList()

        init {
            SIGNATURE_HEADERS = ArrayList()
            SIGNATURE_HEADERS.add("Host")
            SIGNATURE_HEADERS.add("Content-Type")
            SIGNATURE_HEADERS.add("X-Absa-Date")
        }
    }

    override fun sign() {
        absaHmacCredentialScope = HmacCredentialContext().apply {
            deviceID = clientDetails.deviceID
        }

        setDefaultHeaders()

        val canonicalRequest = createCanonicalRequest()
        Logger.d("EXPRESS", "Canonical:$canonicalRequest")
        val stringToSign = createStringToSign(canonicalRequest)
        Logger.d("EXPRESS", "StringToSign:$stringToSign")
        val signatureKey = createSignature()

        hmacSignature = String.format("%064x", BigInteger(1, hMacSHA256(stringToSign, signatureKey))).toLowerCase(Locale.ENGLISH)
    }

    private fun setDefaultHeaders() {
        val dateISO8601 = StringBuilder(this.absaHmacCredentialScope.signatureDateFormatted).append("T").append(this.absaHmacCredentialScope.signatureTimeFormatted)
        val signedHeadersAsDelimitedString = StringBuilder()

        SIGNATURE_HEADERS.forEach {
            if (signedHeadersAsDelimitedString.isNotEmpty()) {
                signedHeadersAsDelimitedString.append(";")
            }
            signedHeadersAsDelimitedString.append(it)
        }

        hmacHeaders.clear()
        hmacHeaders["Host"] = uri.authority
        hmacHeaders["X-Absa-Date"] = dateISO8601.toString()
        hmacHeaders["Content-Type"] = if (isPinEncryptionRequest) "application/x-www-form-urlencoded" else { "application/json" } + "; charset=utf-8"
        hmacHeaders["X-Absa-Algorithm"] = absaHmacAlgorithmName
        hmacHeaders["X-Absa-SignedHeaders"] = signedHeadersAsDelimitedString.toString()
    }

    override fun getSignature(): String = hmacSignature

    override fun httpHeaders(): Map<String, List<String>> {
        val newMap: MutableMap<String, List<String>> = HashMap(hmacHeaders.size)
        hmacHeaders.entries.forEach {
            newMap[it.key] = ArrayList<String>().apply { add(it.value) }
        }
        return newMap
    }

    val credentialScope: String
        get() = absaHmacCredentialScope.toString()

    val signedHeaders: String
        get() = hmacHeaders["X-Absa-SignedHeaders"].toString()

    private fun createCanonicalRequest(): String {
        val canonicalRequest = StringBuilder().apply {
            append(uri.path).append(":")
            append("query=empty").append(":")

            var signedHeadersSorted: List<String> = ArrayList(SIGNATURE_HEADERS)
            signedHeadersSorted = signedHeadersSorted.sorted()

            signedHeadersSorted.forEach {
                append(it.toLowerCase(Locale.ENGLISH)).append(":").append(hmacHeaders[it]).append(":")
            }

            val signedHeadersAsDelimitedString = StringBuilder()

            signedHeadersSorted.forEach {
                if (signedHeadersAsDelimitedString.isNotEmpty()) {
                    signedHeadersAsDelimitedString.append(";")
                }
                signedHeadersAsDelimitedString.append(it.toLowerCase(Locale.ENGLISH))
            }

            append(signedHeadersAsDelimitedString).append(":")
            append(hashAndHexEncodeContent(content))
        }
        return canonicalRequest.toString()
    }

    private fun createStringToSign(canonicalRequest: String): String {
        val stringToSign = StringBuilder().apply {
            append(absaHmacAlgorithmName).append(SIGNATURE_DELIMITER)
            append(hmacHeaders["X-Absa-Date"]).append(SIGNATURE_DELIMITER)
            append(absaHmacCredentialScope.toString()).append(SIGNATURE_DELIMITER)
            append(hashAndHexEncodeContent(canonicalRequest))
        }
        return stringToSign.toString()
    }

    private fun createSignature(): ByteArray {
        val secret = stringToUtf8ByteArray("ABSA_${clientDetails.secret}")
        val date = hMacSHA256(absaHmacCredentialScope.signatureDateFormatted, secret)
        val region = hMacSHA256("ZA", date)
        return hMacSHA256(clientDetails.deviceID, region)
    }

    private fun hashAndHexEncodeContent(content: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(stringToUtf8ByteArray(content))
        val digest = messageDigest.digest()
        return String.format("%064x", BigInteger(1, digest)).toLowerCase(Locale.ENGLISH)
    }

    private fun hMacSHA256(data: String, key: ByteArray): ByteArray {
        val mac = Mac.getInstance(MAC_ALGORITHM_HMAC_SHA256)
        mac.init(SecretKeySpec(key, MAC_ALGORITHM_HMAC_SHA256))
        return mac.doFinal(stringToUtf8ByteArray(data))
    }

    private fun stringToUtf8ByteArray(data: String): ByteArray {
        return try {
            data.toByteArray(StandardCharsets.UTF_8)
        } catch (ex: Exception) {
            stringToUtf8ByteArray("")
        }
    }
}