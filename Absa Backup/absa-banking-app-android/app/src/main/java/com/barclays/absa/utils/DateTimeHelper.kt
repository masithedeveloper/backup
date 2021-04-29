/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.utils

import com.barclays.absa.banking.framework.app.BMBApplication
import java.text.SimpleDateFormat
import java.util.*

object DateTimeHelper {

    const val DASHED_PATTERN_DD_MM = "dd-MM"
    const val DASHED_PATTERN_YYYY_MM_DD = "yyyy-MM-dd"
    const val DASHED_PATTERN_DD_MM_YYYY = "dd-MM-yyyy"
    const val DASHED_PATTERN_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm"
    const val DASHED_PATTERN_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"
    const val DASHED_PATTERN_YYYY_M_DD_HH_MM = "yyyy-M-dd-HH:mm"

    const val SPACED_PATTERN_D_MMMM = "d MMMM"
    const val SPACED_PATTERN_DD_MMM = "dd MMM"
    const val SPACED_PATTERN_D_MMM_YYYY = "d MMM yyyy"
    const val SPACED_PATTERN_DD_MM_YYYY = "dd MM yyyy"
    const val SPACED_PATTERN_DD_MMM_YYYY = "dd MMM yyyy"
    const val SPACED_PATTERN_DD_MMMM_YYYY = "dd MMMM yyyy"
    const val SPACED_PATTERN_D_MMMM_YYYY = "d MMMM yyyy"
    const val SPACED_COMMA_PATTERN_DD_MMMM_YYYY = "dd MMMM, yyyy"
    const val SPACED_PATTERN_EE_DD_MMM_YYYY = "EE dd MMM yyyy"
    const val SPACED_PATTERN_dd_MMM_yyyy_h_mm_aa = "dd MMM yyyy, h:mm aa"
    const val SPACED_PATTERN_YYYY_MM_DD = "yyyy MM dd"
    const val SPACED_PATTERN_YYYYMMDD_HHMMSS = "yyyyMMdd HHmmss"
    const val NO_SPACED_PATTERN_YYYYMMDD = "yyyyMMdd"
    const val NO_SPACED_PATTERN_YYMMDD = "yyMMdd"
    const val NO_SPACED_PATTERN_DD = "dd"
    const val NO_SPACED_PATTERN_MMM = "MMM"
    const val NO_SPACED_PATTERN_MMMM = "MMMM"

    const val SLASH_PATTERN_DD_MM_YYYY = "dd/MM/yyyy"
    const val SLASH_PATTERN_MM_DD_YYYY = "MM/dd/yyyy"
    const val SLASH_PATTERN_YYYY_MM_DD = "yyyy/MM/dd"
    const val SLASH_PATTERN_YYYY_MM_DD_HH_MM = "yyyy/MM/dd HH:mm"

    const val NO_PATTERN_YYYYMM = "yyyyMM"

    const val TWELVE_HOUR_TIME_PATTERN = "hh:mma"
    const val TWENTY_FOUR_HOUR_TIME_PATTERN = "HH:mm"
    const val TWENTY_FOUR_HOUR_AND_SECONDS_TIME_PATTERN = "HH:mm:ss"
    const val TWENTY_FOUR_HOUR_INDICATED_TIME_PATTERN = "hh:mm a"

    fun String.getDateFormat(): String {
        return when {
            this.matches(Regex("\\d{6}")) -> NO_SPACED_PATTERN_YYMMDD
            this.matches(Regex("\\d{8}")) -> NO_SPACED_PATTERN_YYYYMMDD
            this.matches(Regex("\\d{8} \\d{6}")) -> SPACED_PATTERN_YYYYMMDD_HHMMSS
            this.matches(Regex("\\d{2}-\\d{2}-\\d{4}")) -> DASHED_PATTERN_DD_MM_YYYY
            this.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) -> DASHED_PATTERN_YYYY_MM_DD
            this.matches(Regex("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) -> DASHED_PATTERN_YYYY_MM_DD_HH_MM_SS
            this.matches(Regex("\\d{4}-([1-9]|1[012])-\\d{2}-\\d{2}:\\d{2}")) -> DASHED_PATTERN_YYYY_M_DD_HH_MM
            this.matches(Regex("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")) -> DASHED_PATTERN_YYYY_MM_DD_HH_MM
            this.matches(Regex("\\d{2} \\d{2} \\d{4}")) -> SPACED_PATTERN_DD_MM_YYYY
            this.matches(Regex("\\d{2} [a-zA-Z]{3} \\d{4}")) -> SPACED_PATTERN_DD_MMM_YYYY
            this.matches(Regex("([1-9]|[1-3]\\d) [a-zA-Z]{3} \\d{4}")) -> SPACED_PATTERN_D_MMM_YYYY
            this.matches(Regex("\\d{2} [a-zA-Z]+ \\d{4}")) -> SPACED_PATTERN_DD_MMMM_YYYY
            this.matches(Regex("\\d{4} \\d{2} \\d{2}")) -> SPACED_PATTERN_YYYY_MM_DD
            this.matches(Regex("\\d{2}/\\d{2}/\\d{4}")) -> SLASH_PATTERN_DD_MM_YYYY
            this.matches(Regex("\\d{4}/\\d{2}/\\d{2}")) -> SLASH_PATTERN_YYYY_MM_DD
            this.matches(Regex("\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}")) -> SLASH_PATTERN_YYYY_MM_DD_HH_MM
            else -> ""
        }
    }

    @JvmOverloads
    fun String.toDate(locale: Locale = BMBApplication.getApplicationLocale()): Date = try {
        val sourceFormat = SimpleDateFormat(this.getDateFormat(), locale)
        sourceFormat.parse(this) ?: Date()
    } catch (e: Exception) {
        Date()
    }

    @JvmOverloads
    fun String.toCalendar(locale: Locale = BMBApplication.getApplicationLocale()): Calendar = GregorianCalendar().apply {
        time = this@toCalendar.toDate(locale)
    }

    @JvmOverloads
    fun Date.toFormattedString(toDatePattern: String = SPACED_PATTERN_DD_MMM_YYYY): String = formatDate(this, toDatePattern)

    @JvmOverloads
    fun formatDate(date: Date, toDatePattern: String = SPACED_PATTERN_DD_MMM_YYYY, locale: Locale = BMBApplication.getApplicationLocale()): String = SimpleDateFormat(toDatePattern, locale).format(date)

    @JvmOverloads
    fun formatDate(calendar: Calendar, toDatePattern: String = SPACED_PATTERN_DD_MMM_YYYY, locale: Locale = BMBApplication.getApplicationLocale()): String = SimpleDateFormat(toDatePattern, locale).format(calendar.time)

    @JvmOverloads
    fun formatDate(unFormattedDate: String, toDatePattern: String = SPACED_PATTERN_DD_MMM_YYYY, locale: Locale = BMBApplication.getApplicationLocale()): String = formatDate(unFormattedDate, unFormattedDate.getDateFormat(), toDatePattern, locale)

    @JvmOverloads
    fun formatDate(unFormattedDate: String, fromDatePattern: String, toDatePattern: String, locale: Locale = BMBApplication.getApplicationLocale()): String = try {
        val sourceFormat = SimpleDateFormat(fromDatePattern, locale)
        val destinationFormat = SimpleDateFormat(toDatePattern, locale)
        val sourceDate: Date = sourceFormat.parse(unFormattedDate) ?: Date()
        destinationFormat.format(sourceDate)
    } catch (e: Exception) {
        ""
    }
}