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

import okhttp3.internal.and
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PBKDFHelper {

    private const val OUTPUT_KEY_LENGTH = 256
    private const val KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA1"

    fun generatePBDKF2Key(password: CharArray, seed: ByteArray, iterations: Int): SecretKey {
        val keySpec: KeySpec = PBEKeySpec(password, seed, iterations, OUTPUT_KEY_LENGTH)
        return try {
            val secretKeyFactory = SecretKeyFactory.getInstance(KEY_FACTORY_ALGORITHM)
            secretKeyFactory.generateSecret(keySpec)
        } catch (e: NoSuchAlgorithmException) {
            throw KeyGenerationFailureException(e)
        } catch (e: InvalidKeySpecException) {
            throw KeyGenerationFailureException(e)
        }
    }

    fun byteArrayToLong(bytes: ByteArray?): Long {
        var result: Long = 0
        if (bytes == null) {
            return result
        }
        for (aByte in bytes) {
            result = (result shl 8) + (aByte and 0xff)
        }
        if (result < 0) {
            result *= -1
        }
        return result
    }

    internal class KeyGenerationFailureException(e: Throwable?) : Exception(e)
}