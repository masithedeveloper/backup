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

package com.barclays.absa.banking.beneficiaries.services

import com.barclays.absa.DaggerTest
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject
import com.barclays.absa.banking.boundary.model.BeneficiaryObject
import com.barclays.absa.banking.boundary.model.MeterNumberObject
import com.barclays.absa.banking.framework.app.BMBApplication
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class BeneficiaryCacheServiceTest : DaggerTest() {

    private lateinit var beneficiaryCacheService: IBeneficiaryCacheService
    private val defaultValueChangedMessage = "Default value changed"
    private val valueUpdateFailedMessage = "Value not updated successfully"

    @Before
    fun setup() {
        beneficiaryCacheService = BMBApplication.applicationComponent.getBeneficiaryCacheService()
    }

    @Test
    fun getPaymentBeneficiaries() {
        val beneficiaryList = listOf(BeneficiaryObject(), BeneficiaryObject())
        assertTrue(beneficiaryCacheService.getPaymentBeneficiaries().isEmpty()) { defaultValueChangedMessage }
        beneficiaryCacheService.setPaymentsBeneficiaries(beneficiaryList)
        assertTrue(beneficiaryCacheService.getPaymentBeneficiaries().size == beneficiaryList.size) { valueUpdateFailedMessage }
    }

    @Test
    fun getPrepaidBeneficiaries() {
        val beneficiaryList = listOf(BeneficiaryObject(), BeneficiaryObject())
        assertTrue(beneficiaryCacheService.getPrepaidBeneficiaries().isEmpty()) { defaultValueChangedMessage }
        beneficiaryCacheService.setPrepaidBeneficiaries(beneficiaryList)
        assertTrue(beneficiaryCacheService.getPrepaidBeneficiaries().size == beneficiaryList.size) { valueUpdateFailedMessage }
    }

    @Test
    fun getCashSendBeneficiaries() {
        val beneficiaryList = listOf(BeneficiaryObject(), BeneficiaryObject())
        assertTrue(beneficiaryCacheService.getCashSendBeneficiaries().isEmpty()) { defaultValueChangedMessage }
        beneficiaryCacheService.setCashSendBeneficiaries(beneficiaryList)
        assertTrue(beneficiaryCacheService.getCashSendBeneficiaries().size == beneficiaryList.size) { valueUpdateFailedMessage }
    }

    @Test
    fun getElectricityBeneficiaries() {
        val beneficiaryList = listOf(BeneficiaryObject(), BeneficiaryObject())
        assertTrue(beneficiaryCacheService.getElectricityBeneficiaries().isEmpty()) { defaultValueChangedMessage }
        beneficiaryCacheService.setElectricityBeneficiaries(beneficiaryList)
        assertTrue(beneficiaryCacheService.getElectricityBeneficiaries().size == beneficiaryList.size) { valueUpdateFailedMessage }
    }


    @Test
    fun getPaymentRecentTransactionList() {
        val beneficiaryList = listOf(BeneficiaryObject(), BeneficiaryObject())
        assertTrue(beneficiaryCacheService.getPaymentRecentTransactionList().isEmpty()) { defaultValueChangedMessage }
        beneficiaryCacheService.setPaymentRecentTransactionList(beneficiaryList)
        assertTrue(beneficiaryCacheService.getPaymentRecentTransactionList().size == beneficiaryList.size) { valueUpdateFailedMessage }
    }


    @Test
    fun getPrepaidRecentTransactionList() {
        val beneficiaryList = listOf(BeneficiaryObject(), BeneficiaryObject())
        assertTrue(beneficiaryCacheService.getPrepaidRecentTransactionList().isEmpty()) { defaultValueChangedMessage }
        beneficiaryCacheService.setPrepaidRecentTransactionList(beneficiaryList)
        assertTrue(beneficiaryCacheService.getPrepaidRecentTransactionList().size == beneficiaryList.size) { valueUpdateFailedMessage }
    }

    @Test
    fun getCashSendRecentTransactionList() {
        val beneficiaryList = listOf(BeneficiaryObject(), BeneficiaryObject())
        assertTrue(beneficiaryCacheService.getCashSendRecentTransactionList().isEmpty()) { defaultValueChangedMessage }
        beneficiaryCacheService.setCashSendRecentTransactionList(beneficiaryList)
        assertTrue(beneficiaryCacheService.getCashSendRecentTransactionList().size == beneficiaryList.size) { valueUpdateFailedMessage }
    }

    @Test
    fun getTabPositionToReturnTo() {
        assertTrue(beneficiaryCacheService.getTabPositionToReturnTo().isEmpty()) { defaultValueChangedMessage }
        beneficiaryCacheService.setTabPositionToReturnTo("PPE")
        assertTrue(beneficiaryCacheService.getTabPositionToReturnTo() == "PPE") { valueUpdateFailedMessage }
    }

    @Test
    fun getBeneficiaryDetails() {
        val beneficiaryDetail = BeneficiaryDetailObject()
        assertNull(beneficiaryCacheService.getBeneficiaryDetails()) { defaultValueChangedMessage }
        beneficiaryCacheService.setBeneficiaryDetails(beneficiaryDetail)
        assertTrue(beneficiaryCacheService.getBeneficiaryDetails() == beneficiaryDetail) { valueUpdateFailedMessage }
    }

    @Test
    fun getPrepaidElectricityMeterNumber() {
        val meterNumber = MeterNumberObject()
        assertNull(beneficiaryCacheService.getPrepaidElectricityMeterNumber()) { defaultValueChangedMessage }
        beneficiaryCacheService.setPrepaidElectricityMeterNumber(meterNumber)
        assertTrue(beneficiaryCacheService.getPrepaidElectricityMeterNumber() == meterNumber) { valueUpdateFailedMessage }
    }

    @Test
    fun isBeneficiaryAdded() {
        assertFalse(beneficiaryCacheService.isBeneficiaryAdded()) { defaultValueChangedMessage }
        beneficiaryCacheService.setBeneficiaryAdded(true)
        assertTrue(beneficiaryCacheService.isBeneficiaryAdded() ) { valueUpdateFailedMessage }
    }

    @Test
    fun clear() {
        beneficiaryCacheService.clear()
        assertTrue(beneficiaryCacheService.getPaymentBeneficiaries().isEmpty()) { defaultValueChangedMessage }
        assertTrue(beneficiaryCacheService.getPrepaidBeneficiaries().isEmpty()) { defaultValueChangedMessage }
        assertTrue(beneficiaryCacheService.getCashSendBeneficiaries().isEmpty()) { defaultValueChangedMessage }
        assertTrue(beneficiaryCacheService.getElectricityBeneficiaries().isEmpty()) { defaultValueChangedMessage }
        assertTrue(beneficiaryCacheService.getPaymentRecentTransactionList().isEmpty()) { defaultValueChangedMessage }
        assertTrue(beneficiaryCacheService.getPrepaidRecentTransactionList().isEmpty()) { defaultValueChangedMessage }
        assertTrue(beneficiaryCacheService.getCashSendRecentTransactionList().isEmpty()) { defaultValueChangedMessage }
        assertTrue(beneficiaryCacheService.getTabPositionToReturnTo().isEmpty()) { defaultValueChangedMessage }
        assertNull(beneficiaryCacheService.getBeneficiaryDetails()) { defaultValueChangedMessage }
        assertNull(beneficiaryCacheService.getPrepaidElectricityMeterNumber()) { defaultValueChangedMessage }
        assertFalse(beneficiaryCacheService.isBeneficiaryAdded()) { defaultValueChangedMessage }
    }
}