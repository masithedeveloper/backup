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
package com.barclays.absa.crypto

import java.util.*
import java.util.regex.Pattern

class PinEncryptionUtils {
    private fun get8BitByteArray(stringValue: String): IntArray {
        val bitList = ArrayList<Int>()
        var i = 0
        while (i < stringValue.length) {
            val subStringValue = stringValue.substring(i, i + 2)
            bitList.add(Integer.parseInt(subStringValue, 16))
            i += 2
        }

        if (stringValue.length == 6) {
            for (j in 0 until stringValue.length - 1) {
                bitList.add(Integer.parseInt("00", 16))
            }
        }

        val bits = IntArray(bitList.size)
        for (k in bitList.indices) {
            bits[k] = bitList[k]
        }

        return bits
    }

    private fun decodePin(pan: String, pinBlock: String, refNo: String): String {

        if (pan.length == 16) {
            if (pinBlock.length == 16) {
                if (refNo.length == 6) {

                    val panBytes = get8BitByteArray(pan)
                    val pinBlockBytes = get8BitByteArray(pinBlock)
                    val refNoBytes = get8BitByteArray(refNo)

                    val panPinXOred = IntArray(panBytes.size)
                    for (i in panBytes.indices) {
                        panPinXOred[i] = panBytes[i] xor pinBlockBytes[i]
                    }

                    val panPinResultRefNoXOred = IntArray(panBytes.size)
                    for (i in panBytes.indices) {
                        if (i < refNoBytes.size) {
                            panPinResultRefNoXOred[i] = panPinXOred[i] xor refNoBytes[i]
                        }
                    }

                    val tempPin = StringBuilder()
                    var pinLength = 0
                    for (i in panBytes.indices) {
                        if (i == 0) {
                            pinLength = Integer.parseInt(panPinResultRefNoXOred[i].toString(), 16)
                        } else {
                            val numberInPan = panPinResultRefNoXOred[i]
                            if (numberInPan < 10) {
                                tempPin.append("0").append(Integer.toString(numberInPan, 16))
                            } else {
                                if ("f".equals(Integer.toString(numberInPan, 16), ignoreCase = true)) {
                                    tempPin.append("0")
                                } else {
                                    tempPin.append(Integer.toString(numberInPan, 16))
                                }
                            }
                        }
                    }

                    val decodedPin: String
                    if (tempPin.length >= pinLength) {
                        decodedPin = tempPin.substring(0, pinLength)
                    } else {
                        decodedPin = "ERROR"
                    }
                    return decodedPin
                } else {
                    return "Invalid RefNo"
                }
            } else {
                return "Invalid PinBlock"
            }
        } else {
            return "Invalid PAN Number"
        }
    }

    fun decodedPin(cardPinBlock: String): String {
        val regexPattern = "<!\\[CDATA\\[[0-9]\\|[0-9]\\|(.*)\\|[0-9]{16}(.*)\\|(.*)]]>"
        val pattern = Pattern.compile(regexPattern)
        val matcher = pattern.matcher(cardPinBlock)

        val decodedPin: String
        decodedPin = if (matcher.find()) {
            decodePin(matcher.group(1) ?: "", matcher.group(2) ?: "", matcher.group(3) ?: "")
        } else {
            decodePin("", "", "")
        }
        return decodedPin
    }
}