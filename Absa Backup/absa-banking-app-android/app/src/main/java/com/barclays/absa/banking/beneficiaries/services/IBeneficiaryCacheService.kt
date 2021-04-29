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

import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject
import com.barclays.absa.banking.boundary.model.BeneficiaryObject
import com.barclays.absa.banking.boundary.model.MeterNumberObject

interface IBeneficiaryCacheService {

    fun getPaymentBeneficiaries(): List<BeneficiaryObject>
    fun setPaymentsBeneficiaries(beneficiaries: List<BeneficiaryObject>)

    fun getPrepaidBeneficiaries(): List<BeneficiaryObject>
    fun setPrepaidBeneficiaries(beneficiaries: List<BeneficiaryObject>)

    fun getCashSendBeneficiaries(): List<BeneficiaryObject>
    fun setCashSendBeneficiaries(beneficiaries: List<BeneficiaryObject>)

    fun getElectricityBeneficiaries(): List<BeneficiaryObject>
    fun setElectricityBeneficiaries(beneficiaries: List<BeneficiaryObject>)

    fun getPaymentRecentTransactionList(): List<BeneficiaryObject>
    fun setPaymentRecentTransactionList(beneficiaryObjectList: List<BeneficiaryObject>)

    fun getPrepaidRecentTransactionList(): List<BeneficiaryObject>
    fun setPrepaidRecentTransactionList(beneficiaryObjectList: List<BeneficiaryObject>)

    fun getCashSendRecentTransactionList(): List<BeneficiaryObject>
    fun setCashSendRecentTransactionList(beneficiaryObjectList: List<BeneficiaryObject>)

    fun getTabPositionToReturnTo(): String
    fun setTabPositionToReturnTo(tabToReturnTo: String)

    fun getBeneficiaryDetails(): BeneficiaryDetailObject?
    fun setBeneficiaryDetails(beneficiaryDetailObject: BeneficiaryDetailObject)

    fun getPrepaidElectricityMeterNumber(): MeterNumberObject?
    fun setPrepaidElectricityMeterNumber(meterNumberObject: MeterNumberObject)

    fun isBeneficiaryAdded(): Boolean
    fun setBeneficiaryAdded(beneficiaryAdded: Boolean)

    fun clear()
}