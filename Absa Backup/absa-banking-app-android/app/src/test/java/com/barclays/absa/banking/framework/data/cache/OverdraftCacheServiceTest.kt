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
 */

package com.barclays.absa.banking.framework.data.cache

import com.barclays.absa.DaggerTest
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftQuoteDetailsObject
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftResponse
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftSetup
import com.barclays.absa.banking.framework.app.BMBApplication
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue

class OverdraftCacheServiceTest : DaggerTest() {

    private lateinit var overdraftCacheService: IOverdraftCacheService
    private val defaultValueChangedMessage = "Default value changed"
    private val valueUpdateFailedMessage = "Value not updated successfully"

    @Before
    fun setup() {
        overdraftCacheService = BMBApplication.applicationComponent.getOverdraftCacheService()
    }

    @Test
    fun getOverdraftResponse() {
        val overdraftResponse = OverdraftResponse()
        assertNull(overdraftCacheService.getOverdraftResponse()) { defaultValueChangedMessage }
        overdraftCacheService.setOverdraftResponse(overdraftResponse)
        assertTrue(overdraftCacheService.getOverdraftResponse() == overdraftResponse) { valueUpdateFailedMessage }
    }

    @Test
    fun getOverdraftQuoteDetails() {
        val overdraftQuoteDetails = OverdraftQuoteDetailsObject("", "", "", "", "", "", "", "", 0.00)
        assertNull(overdraftCacheService.getOverdraftQuoteDetails()) { defaultValueChangedMessage }
        overdraftCacheService.setOverdraftQuoteDetails(overdraftQuoteDetails)
        assertTrue(overdraftCacheService.getOverdraftQuoteDetails() == overdraftQuoteDetails) { valueUpdateFailedMessage }
    }

    @Test
    fun getOverdraftSetup() {
        val overdraftSetup = OverdraftSetup("", "")
        assertNull(overdraftCacheService.getOverdraftSetup()) { defaultValueChangedMessage }
        overdraftCacheService.setOverdraftSetup(overdraftSetup)
        assertTrue(overdraftCacheService.getOverdraftSetup() == overdraftSetup) { valueUpdateFailedMessage }
    }

    @Test
    fun clear() {
        assertNull(overdraftCacheService.getOverdraftResponse()) { defaultValueChangedMessage }
        assertNull(overdraftCacheService.getOverdraftQuoteDetails()) { defaultValueChangedMessage }
        assertNull(overdraftCacheService.getOverdraftSetup()) { defaultValueChangedMessage }
    }
}