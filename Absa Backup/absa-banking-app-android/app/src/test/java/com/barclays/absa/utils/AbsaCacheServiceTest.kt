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

package com.barclays.absa.utils

import com.barclays.absa.DaggerTest
import com.barclays.absa.banking.framework.app.BMBApplication
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class AbsaCacheServiceTest : DaggerTest() {

    private lateinit var absaCacheService: IAbsaCacheService
    private val defaultValueChangedMessage = "Default value changed"
    private val valueUpdateFailedMessage = "Value not updated successfully"

    @Before
    fun setup() {
        absaCacheService = BMBApplication.applicationComponent.getAbsaCacheService()
    }

    @Test
    fun isPersonalClientAgreementAccepted() {
        assertFalse(absaCacheService.isPersonalClientAgreementAccepted()) { defaultValueChangedMessage }
        absaCacheService.setPersonalClientAgreementAccepted(true)
        assertTrue(absaCacheService.isPersonalClientAgreementAccepted()) { valueUpdateFailedMessage }
    }

    @Test
    fun isClientTypeCached() {
        assertFalse(absaCacheService.isClientTypeCached()) { defaultValueChangedMessage }
        absaCacheService.setClientTypeCached(true)
        assertTrue(absaCacheService.isClientTypeCached()) { valueUpdateFailedMessage }
    }

    @Test
    fun isCallMeBackRequested() {
        assertFalse(absaCacheService.isCallMeBackRequested()) { defaultValueChangedMessage }
        absaCacheService.setCallMeBackRequested(true)
        assertTrue(absaCacheService.isCallMeBackRequested()) { valueUpdateFailedMessage }
    }

    @Test
    fun isInternationalPaymentsAllowed() {
        assertFalse(absaCacheService.isInternationalPaymentsAllowed()) { defaultValueChangedMessage }
        absaCacheService.setInternationalPaymentsAllowed(true)
        assertTrue(absaCacheService.isInternationalPaymentsAllowed()) { valueUpdateFailedMessage }
    }

    @Test
    fun clear() {
        absaCacheService.clear()
        assertFalse(absaCacheService.isPersonalClientAgreementAccepted()) { defaultValueChangedMessage }
        assertFalse(absaCacheService.isClientTypeCached()) { defaultValueChangedMessage }
        assertFalse(absaCacheService.isCallMeBackRequested()) { defaultValueChangedMessage }
        assertFalse(absaCacheService.isInternationalPaymentsAllowed()) { defaultValueChangedMessage }
    }
}