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
 */

package styleguide.utils

object IdentityDocumentValidationUtil {
    fun isValidIdNumber(idNumber: String): Boolean = idNumber.isNotBlank() && calculateIdNumberControlDigit(idNumber) == idNumber.last().toString()

    const val southAfricanIdValidationPattern = "^([0-9]){2}(((0[1-9]|1[0-2])([01][1-9]|10|2[0-8]))|((0[2])(29))|((0[13-9]|1[0-2])(29|30))|((0[13578]|1[02])31))([0-9]){4}([0-1])([0-9]){2}?$"

    fun calculateIdNumberControlDigit(idNumber: String): String {
        var controlDigit = -1
        try {
            var dateDigits = 0
            for (i in 0..5) {
                dateDigits += idNumber[2 * i].toString().toInt()
            }
            var offset = 0
            for (i in 0..5) {
                offset = offset * 10 + idNumber[2 * i + 1].toString().toInt()
            }
            offset *= 2
            var modDigit = 0
            do {
                modDigit += offset % 10
                offset /= 10
            } while (offset > 0)
            modDigit += dateDigits
            controlDigit = 10 - modDigit % 10
            if (controlDigit == 10) controlDigit = 0
        } catch (ignored: Exception) {
        }

        return controlDigit.toString()
    }
}