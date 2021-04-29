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

package com.barclays.absa.banking.payments.international.services

import com.barclays.absa.DaggerTest
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.payments.international.data.BeneficiaryEnteredDetails
import com.barclays.absa.banking.payments.international.services.dto.*
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue

class InternationalPaymentCacheServiceTest : DaggerTest() {

    private lateinit var internationalPaymentCacheService: IInternationalPaymentCacheService
    private val defaultValueChangedMessage = "Default value changed"
    private val valueUpdateFailedMessage = "Value not updated successfully"

    @Before
    fun setup() {
        internationalPaymentCacheService = BMBApplication.applicationComponent.getInternationalPaymentCacheService()
    }

    @Test
    fun getQuoteDetails() {
        val quoteDetails = QuoteDetails()
        assertCastException { internationalPaymentCacheService.getQuoteDetails() }
        internationalPaymentCacheService.setQuoteDetails(quoteDetails)
        assertTrue(internationalPaymentCacheService.getQuoteDetails() == quoteDetails) { valueUpdateFailedMessage }
    }

    @Test
    fun getBeneficiaryDetails() {
        val westernUnionBeneficiaryDetails = WesternUnionBeneficiaryDetails()
        assertCastException { internationalPaymentCacheService.getBeneficiaryDetails() }
        internationalPaymentCacheService.setBeneficiaryDetails(westernUnionBeneficiaryDetails)
        assertTrue(internationalPaymentCacheService.getBeneficiaryDetails() == westernUnionBeneficiaryDetails) { valueUpdateFailedMessage }
    }

    @Test
    fun getValidatePaymentDetails() {
        val validatePaymentDetails = ValidatePaymentDetails()
        assertCastException { internationalPaymentCacheService.getValidatePaymentDetails() }
        internationalPaymentCacheService.setValidatePaymentDetails(validatePaymentDetails)
        assertTrue(internationalPaymentCacheService.getValidatePaymentDetails() == validatePaymentDetails) { valueUpdateFailedMessage }
    }

    @Test
    fun getEnteredBeneficiaryDetails() {
        val beneficiaryEnteredDetails = BeneficiaryEnteredDetails()
        assertCastException { internationalPaymentCacheService.getEnteredBeneficiaryDetails() }
        internationalPaymentCacheService.setEnteredBeneficiaryDetails(beneficiaryEnteredDetails)
        assertTrue(internationalPaymentCacheService.getEnteredBeneficiaryDetails() == beneficiaryEnteredDetails) { valueUpdateFailedMessage }
    }

    @Test
    fun getClientTypeResponse() {
        val clientTypeResponse = ClientTypeResponse()
        assertNull(internationalPaymentCacheService.getClientTypeResponse()) { defaultValueChangedMessage }
        internationalPaymentCacheService.setClientTypeResponse(clientTypeResponse)
        assertTrue(internationalPaymentCacheService.getClientTypeResponse() == clientTypeResponse) { valueUpdateFailedMessage }
    }

    @Test
    fun getQuotation() {
        val quoteDetailsResponse = QuoteDetailsResponse()
        assertCastException { internationalPaymentCacheService.getQuotation() }
        internationalPaymentCacheService.setQuotation(quoteDetailsResponse)
        assertTrue(internationalPaymentCacheService.getQuotation() == quoteDetailsResponse) { valueUpdateFailedMessage }
    }

    @Test
    fun clear() {
        assertCastException { internationalPaymentCacheService.getQuoteDetails() }
        assertCastException { internationalPaymentCacheService.getBeneficiaryDetails() }
        assertCastException { internationalPaymentCacheService.getValidatePaymentDetails() }
        assertCastException { internationalPaymentCacheService.getEnteredBeneficiaryDetails() }
        assertCastException { internationalPaymentCacheService.getQuotation() }
        assertNull(internationalPaymentCacheService.getClientTypeResponse()) { defaultValueChangedMessage }
    }

    private fun assertCastException(block: () -> Any) {
        try {
            block.invoke()
        } catch (e: Exception) {
            assertTrue(e is NullPointerException, "Method changed to no longer return null that cant be cast to object")
        }
    }
}