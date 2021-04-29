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

import com.barclays.absa.utils.DateTimeHelper.DASHED_PATTERN_DD_MM_YYYY
import com.barclays.absa.utils.DateTimeHelper.DASHED_PATTERN_YYYY_MM_DD
import com.barclays.absa.utils.DateTimeHelper.DASHED_PATTERN_YYYY_MM_DD_HH_MM
import com.barclays.absa.utils.DateTimeHelper.DASHED_PATTERN_YYYY_MM_DD_HH_MM_SS
import com.barclays.absa.utils.DateTimeHelper.DASHED_PATTERN_YYYY_M_DD_HH_MM
import com.barclays.absa.utils.DateTimeHelper.NO_SPACED_PATTERN_YYMMDD
import com.barclays.absa.utils.DateTimeHelper.NO_SPACED_PATTERN_YYYYMMDD
import com.barclays.absa.utils.DateTimeHelper.SLASH_PATTERN_DD_MM_YYYY
import com.barclays.absa.utils.DateTimeHelper.SLASH_PATTERN_YYYY_MM_DD
import com.barclays.absa.utils.DateTimeHelper.SLASH_PATTERN_YYYY_MM_DD_HH_MM
import com.barclays.absa.utils.DateTimeHelper.SPACED_PATTERN_DD_MMMM_YYYY
import com.barclays.absa.utils.DateTimeHelper.SPACED_PATTERN_DD_MMM_YYYY
import com.barclays.absa.utils.DateTimeHelper.SPACED_PATTERN_DD_MM_YYYY
import com.barclays.absa.utils.DateTimeHelper.SPACED_PATTERN_YYYYMMDD_HHMMSS
import com.barclays.absa.utils.DateTimeHelper.SPACED_PATTERN_YYYY_MM_DD
import com.barclays.absa.utils.DateTimeHelper.getDateFormat
import com.barclays.absa.utils.DateTimeHelper.toCalendar
import com.barclays.absa.utils.DateTimeHelper.toDate
import com.barclays.absa.utils.DateTimeHelper.toFormattedString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

internal class DateTimeHelperTest {
    private val validTestDates = listOf("20201126", "26-11-2020", "2020-11-26", "26 11 2020", "26 Nov 2020", "26 November 2020", "2020 11 26", "26/11/2020", "2020/11/26")
    private val invalidTestDates = listOf("", "Some random text", "2020-131-22", "20111/13/31")

    companion object {
        const val DATE_IN_LONG_26_NOV_2020 = 1606341600000
    }

    @Test
    fun shouldReturnNonSpaced_yy_MM_dd_DatePattern_whenDateStringIsProvided() = assertEquals(NO_SPACED_PATTERN_YYMMDD, "201126".getDateFormat())

    @Test
    fun shouldReturnNonSpaced_yyyy_MM_dd_DatePattern_whenDateStringIsProvided() = assertEquals(NO_SPACED_PATTERN_YYYYMMDD, "20201126".getDateFormat())

    @Test
    fun shouldReturnDashed_dd_MM_yyyy_DatePattern_whenDateStringIsProvided() = assertEquals(DASHED_PATTERN_DD_MM_YYYY, "26-11-2020".getDateFormat())

    @Test
    fun shouldReturnDashed_yyyy_MM_dd_DatePattern_whenDateStringIsProvided() = assertEquals(DASHED_PATTERN_YYYY_MM_DD, "2020-11-26".getDateFormat())

    @Test
    fun shouldReturnDashed_yyyy_MM_dd_hh_mm_ss_DatePattern_whenDateStringIsProvided() = assertEquals(DASHED_PATTERN_YYYY_MM_DD_HH_MM_SS, "2020-11-26 10:50:11".getDateFormat())

    @Test
    fun shouldReturnDashed_yyyy_MM_dd_hhmm_DatePattern_whenDateStringIsProvided() = assertEquals(DASHED_PATTERN_YYYY_M_DD_HH_MM, "2020-11-26-10:50".getDateFormat())

    @Test
    fun shouldReturnDashed_yyyy_MM_dd_hh_mm_DatePattern_whenDateStringIsProvided() = assertEquals(DASHED_PATTERN_YYYY_MM_DD_HH_MM, "2020-11-26 10:50".getDateFormat())

    @Test
    fun shouldReturnSpaced_yyyy_MM_dd_hh_mm_ss_DatePattern_whenDateStringIsProvided() = assertEquals(SPACED_PATTERN_YYYYMMDD_HHMMSS, "20201126 105010".getDateFormat())

    @Test
    fun shouldReturnSpaced_dd_MM_yyyy_DatePattern_whenDateStringIsProvided() = assertEquals(SPACED_PATTERN_DD_MM_YYYY, "26 11 2020".getDateFormat())

    @Test
    fun shouldReturnSpaced_dd_MMM_yyyy_DatePattern_whenDateStringIsProvided() = assertEquals(SPACED_PATTERN_DD_MMM_YYYY, "26 Nov 2020".getDateFormat())

    @Test
    fun shouldReturnSpaced_d_MMM_yyyy_DatePattern_whenDateStringIsProvided() = assertEquals(DateTimeHelper.SPACED_PATTERN_D_MMM_YYYY, "2 Nov 2020".getDateFormat())

    @Test
    fun shouldReturnSpaced_dd_MMMM_yyyy_DatePattern_whenDateStringIsProvided() = assertEquals(SPACED_PATTERN_DD_MMMM_YYYY, "26 November 2020".getDateFormat())

    @Test
    fun shouldReturnSpaced_yyyy_MM_dd_DatePattern_whenDateStringIsProvided() = assertEquals(SPACED_PATTERN_YYYY_MM_DD, "2020 11 26".getDateFormat())

    @Test
    fun shouldReturnSlashed_dd_MM_yyyy_DatePattern_whenDateStringIsProvided() = assertEquals(SLASH_PATTERN_DD_MM_YYYY, "26/11/2020".getDateFormat())

    @Test
    fun shouldReturnSlashed_yyyy_MM_dd_DatePattern_whenDateStringIsProvided() = assertEquals(SLASH_PATTERN_YYYY_MM_DD, "2020/11/26".getDateFormat())

    @Test
    fun shouldReturnSlashed_yyyy_MM_dd_HH_mm_DatePattern_whenDateStringIsProvided() = assertEquals(SLASH_PATTERN_YYYY_MM_DD_HH_MM, "2020/11/26 10:26".getDateFormat())

    @Test
    fun shouldReturnBlankDatePattern_whenEmptyDateStringIsProvided() = assertEquals("", "".getDateFormat())

    @Test
    fun shouldReturnBlankDatePattern_whenInvalidDateStringIsProvided() = assertEquals("", "Something here".getDateFormat())

    @Test
    fun shouldReturnADateObjectWithCorrectDate_whenAValidDateStringIsProvided() {
        validTestDates.forEach {
            assertEquals(Date(DATE_IN_LONG_26_NOV_2020), it.toDate(), "Expected date $it could not be formatted")
        }
    }

    @Test
    fun shouldReturnDateObject_whenInvalidDateStringIsProvided() {
        invalidTestDates.forEach {
            assertThat(it.toDate(), instanceOf(Date::class.java))
        }
    }

    @Test
    fun shouldReturnACalendarObjectWithCorrectDate_whenAValidDateStringIsProvided() {
        val calendarItemToTest = GregorianCalendar().apply {
            time = Date(DATE_IN_LONG_26_NOV_2020)
        }
        validTestDates.forEach {
            assertEquals(calendarItemToTest, it.toCalendar(), "Expected date $it could not be formatted")
        }
    }

    @Test
    fun shouldReturnDateFormattedString_whenDateObjectIsProvided() {
        validTestDates.forEach {
            assertEquals(it, DateTimeHelper.formatDate(Date(DATE_IN_LONG_26_NOV_2020), it.getDateFormat()))
        }
    }

    @Test
    fun shouldReturnDateFormattedStringUsingExtensionFunction_whenDateObjectIsProvided() {
        validTestDates.forEach {
            assertEquals(it, Date(DATE_IN_LONG_26_NOV_2020).toFormattedString(it.getDateFormat()))
        }
    }

    @Test
    fun shouldReturnDateFormattedString_whenCalendarObjectIsProvided() {
        val calendar = Calendar.getInstance().apply {
            time = Date(DATE_IN_LONG_26_NOV_2020)
        }
        validTestDates.forEach {
            assertEquals(it, DateTimeHelper.formatDate(calendar, it.getDateFormat()))
        }
    }

    @Test
    fun shouldReturnDateStringFormattedAs_dd_mmm_yyyy_whenValidUnformattedStringIsProvided() {
        validTestDates.forEach {
            assertEquals("26 Nov 2020", DateTimeHelper.formatDate(it))
        }
    }

    @Test
    fun shouldReturnEmptyString_whenStringProvidedIsInvalid() {
        invalidTestDates.forEach {
            assertEquals("", DateTimeHelper.formatDate(it))
        }
    }
}