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

class BeneficiaryCacheService : IBeneficiaryCacheService {

    private var BACKING_STORE: HashMap<String, Any> = HashMap()

    companion object {
        private const val BENEFICIARY_LIST_PAYMENTS = "beneficiary_list_payments"
        private const val BENEFICIARY_LIST_PREPAID = "beneficiary_list_prepaid"
        private const val BENEFICIARY_LIST_CASHSEND = "beneficiary_list_cashsend"
        private const val BENEFICIARY_LIST_ELECTRICITY = "beneficiary_list_electricity"
        private const val RECENT_PAYMENT_TRANSACTION_LIST = "recent_payment_transaction_list"
        private const val RECENT_PREPAID_TRANSACTION_LIST = "recent_prepaid_transaction_list"
        private const val RECENT_CASHSEND_TRANSACTION_LIST = "recent_cashsend_transaction_list"
        private const val TAB_TO_RETURN_TO = "return_to_previous_tab"
        private const val BENEFICIARY_DETAILS = "beneficiaryDetails"
        private const val BENEFICIARY_METER_OBJECT = "PPEMeterObject"
        private const val BENEFICIARY_ADDED = "PPEBenAdded"
    }

    @Suppress("UNCHECKED_CAST")
    override fun getPaymentBeneficiaries(): List<BeneficiaryObject> = (BACKING_STORE[BENEFICIARY_LIST_PAYMENTS] as? List<BeneficiaryObject>) ?: emptyList()
    override fun setPaymentsBeneficiaries(beneficiaries: List<BeneficiaryObject>) {
        BACKING_STORE[BENEFICIARY_LIST_PAYMENTS] = beneficiaries
    }

    @Suppress("UNCHECKED_CAST")
    override fun getPrepaidBeneficiaries(): List<BeneficiaryObject> = (BACKING_STORE[BENEFICIARY_LIST_PREPAID] as? List<BeneficiaryObject>) ?: emptyList()
    override fun setPrepaidBeneficiaries(beneficiaries: List<BeneficiaryObject>) {
        BACKING_STORE[BENEFICIARY_LIST_PREPAID] = beneficiaries
    }

    @Suppress("UNCHECKED_CAST")
    override fun getCashSendBeneficiaries(): List<BeneficiaryObject> = (BACKING_STORE[BENEFICIARY_LIST_CASHSEND] as? List<BeneficiaryObject>) ?: emptyList()
    override fun setCashSendBeneficiaries(beneficiaries: List<BeneficiaryObject>) {
        BACKING_STORE[BENEFICIARY_LIST_CASHSEND] = beneficiaries
    }

    @Suppress("UNCHECKED_CAST")
    override fun getElectricityBeneficiaries(): List<BeneficiaryObject> = (BACKING_STORE[BENEFICIARY_LIST_ELECTRICITY] as? List<BeneficiaryObject>) ?: emptyList()
    override fun setElectricityBeneficiaries(beneficiaries: List<BeneficiaryObject>) {
        BACKING_STORE[BENEFICIARY_LIST_ELECTRICITY] = beneficiaries
    }

    @Suppress("UNCHECKED_CAST")
    override fun getPaymentRecentTransactionList(): List<BeneficiaryObject> = (BACKING_STORE[RECENT_PAYMENT_TRANSACTION_LIST] as? List<BeneficiaryObject>) ?: emptyList()
    override fun setPaymentRecentTransactionList(beneficiaryObjectList: List<BeneficiaryObject>) {
        BACKING_STORE[RECENT_PAYMENT_TRANSACTION_LIST] = beneficiaryObjectList
    }

    @Suppress("UNCHECKED_CAST")
    override fun getPrepaidRecentTransactionList(): List<BeneficiaryObject> = (BACKING_STORE[RECENT_PREPAID_TRANSACTION_LIST] as? List<BeneficiaryObject>) ?: emptyList()
    override fun setPrepaidRecentTransactionList(beneficiaryObjectList: List<BeneficiaryObject>) {
        BACKING_STORE[RECENT_PREPAID_TRANSACTION_LIST] = beneficiaryObjectList
    }

    @Suppress("UNCHECKED_CAST")
    override fun getCashSendRecentTransactionList(): List<BeneficiaryObject> = (BACKING_STORE[RECENT_CASHSEND_TRANSACTION_LIST] as? List<BeneficiaryObject>) ?: emptyList()
    override fun setCashSendRecentTransactionList(beneficiaryObjectList: List<BeneficiaryObject>) {
        BACKING_STORE[RECENT_CASHSEND_TRANSACTION_LIST] = beneficiaryObjectList
    }

    override fun getTabPositionToReturnTo(): String = (BACKING_STORE[TAB_TO_RETURN_TO] as? String) ?: ""
    override fun setTabPositionToReturnTo(tabToReturnTo: String) {
        BACKING_STORE[TAB_TO_RETURN_TO] = tabToReturnTo
    }

    override fun getBeneficiaryDetails(): BeneficiaryDetailObject? = BACKING_STORE[BENEFICIARY_DETAILS] as? BeneficiaryDetailObject
    override fun setBeneficiaryDetails(beneficiaryDetailObject: BeneficiaryDetailObject) {
        with(BACKING_STORE) { put(BENEFICIARY_DETAILS, beneficiaryDetailObject) }
    }

    override fun getPrepaidElectricityMeterNumber(): MeterNumberObject? = BACKING_STORE[BENEFICIARY_METER_OBJECT] as? MeterNumberObject
    override fun setPrepaidElectricityMeterNumber(meterNumberObject: MeterNumberObject) {
        BACKING_STORE[BENEFICIARY_METER_OBJECT] = meterNumberObject
    }

    override fun isBeneficiaryAdded(): Boolean = (BACKING_STORE[BENEFICIARY_ADDED] as? Boolean) ?: false
    override fun setBeneficiaryAdded(beneficiaryAdded: Boolean) {
        BACKING_STORE[BENEFICIARY_ADDED] = beneficiaryAdded
    }

    override fun clear() {
        BACKING_STORE = HashMap()
    }
}