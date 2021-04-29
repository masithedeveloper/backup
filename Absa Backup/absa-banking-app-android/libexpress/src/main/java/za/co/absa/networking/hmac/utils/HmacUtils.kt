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

package za.co.absa.networking.hmac.utils

import android.util.Base64
import za.co.absa.networking.ExpressNetworkingConfig
import java.security.Key
import java.security.KeyFactory
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

object HmacUtils {

    var aesSymmetricKey: SecretKey? = null

    private const val OUTPUT_KEY_DERIVATION_ALGORITHM = "AES"
    private const val CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding"
    private const val DECRYPT_ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val REGISTRATION_SALT = "REGISTER_DEVICE"
    private const val OUTPUT_KEY_LENGTH = 256

    fun convertMapListToMapString(map: Map<String, List<String>>): Map<String, String> {
        val theMap: MutableMap<String, String> = HashMap()
        map.forEach {
            if (it.key.isNotBlank()) {
                theMap[it.key] = convertListToDelimitedString(it.value)
            }
        }

        return theMap
    }

    private fun convertListToDelimitedString(list: List<String>): String = if (list.isEmpty()) "" else list.joinToString(",")

    fun encodeBase64NoWrapNoPaddingUrlSafe(stringToEncode: String): String {
        val flags = Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING
        return Base64.encodeToString(stringToEncode.toByteArray(), flags)
    }

    fun getRegistrationHMacSecret(): String {
        val hMacSecret = PBKDFHelper.generatePBDKF2Key(ExpressNetworkingConfig.deviceId.toCharArray(), REGISTRATION_SALT.toByteArray(), 1000)
        return Base64.encodeToString(hMacSecret.encoded, Base64.NO_WRAP)
    }

    fun generateAndEncodeAesSymmetricKey(publicKeyString: String): String {
        aesSymmetricKey = generateKey()
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        val publicKey: Key? = getPublicKey(publicKeyString)
        cipher.init(Cipher.WRAP_MODE, publicKey)
        val wrappedAesKey = cipher.wrap(aesSymmetricKey)
        val base64EncodedKey: String = Base64.encodeToString(wrappedAesKey, Base64.NO_WRAP)
        println("base64EncodedKey: $base64EncodedKey")
        return base64EncodedKey
    }

    private fun getPublicKey(publicKeyString: String): PublicKey? {
        try {
            val publicKeyBytes: ByteArray = Base64.decode(publicKeyString, Base64.URL_SAFE)
            val keySpec = X509EncodedKeySpec(publicKeyBytes)
            val keyFactory = KeyFactory.getInstance("RSA")
            return keyFactory.generatePublic(keySpec)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun generateKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(OUTPUT_KEY_DERIVATION_ALGORITHM)
        val secureRandom = SecureRandom()
        keyGenerator.init(OUTPUT_KEY_LENGTH, secureRandom)
        return keyGenerator.generateKey()
    }

    fun decrypt(secretKey: SecretKey?, cipherData: ByteArray): ByteArray? {
        return try {
            val cipher = Cipher.getInstance(DECRYPT_ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, generateIV())
            cipher.doFinal(cipherData)
        } catch (e: Exception) {
            return null
        }
    }

    fun decrypt(secretKey: SecretKey, initializationVector: AlgorithmParameterSpec, cipherData: ByteArray): ByteArray? {
        return try {
            val cipher = Cipher.getInstance(DECRYPT_ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, initializationVector)
            cipher.doFinal(cipherData)
        } catch (e: Exception) {
            return null
        }
    }

    private fun generateIV(): AlgorithmParameterSpec? {
        val ivLength = 16
        val ivBuffer = ByteArray(ivLength)
        Arrays.fill(ivBuffer, 0x00.toByte())
        return IvParameterSpec(ivBuffer)
    }

    fun generateRandomIV(): IvParameterSpec {
        val ivLength = 16
        val ivBuffer = ByteArray(ivLength)
        val random = SecureRandom()
        random.nextBytes(ivBuffer)
        return IvParameterSpec(ivBuffer)
    }
}