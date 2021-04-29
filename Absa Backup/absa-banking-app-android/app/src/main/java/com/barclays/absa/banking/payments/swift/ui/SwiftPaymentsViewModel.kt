/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank Limited
 *
 */
package com.barclays.absa.banking.payments.swift.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.payments.swift.services.SwiftInteractor
import com.barclays.absa.banking.payments.swift.services.request.SwiftBopDataValidationRequestDataModel
import com.barclays.absa.banking.payments.swift.services.request.SwiftBopFieldsRequestDataModel
import com.barclays.absa.banking.payments.swift.services.request.SwiftProcessTransactionRequestDataModel
import com.barclays.absa.banking.payments.swift.services.request.SwiftQuoteRequestDataModel
import com.barclays.absa.banking.payments.swift.services.response.*
import com.barclays.absa.banking.payments.swift.services.response.dto.*
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.barclays.absa.utils.AbsaCacheManager

class SwiftPaymentsViewModel : BaseViewModel() {

    companion object {
        private const val SWIFT_CATEGORY_FLOW = "IN"
    }

    enum class SenderType(val key: String, val indicator: String) {
        INDIVIDUAL("individual", "P"),
        BUSINESS("business", "B")
    }

    private val swiftInteractor = SwiftInteractor()

    private val levelOneCategoriesResponseListener by lazy { SwiftLevelOneCategoriesResponseListener(this) }
    private val levelTwoCategoriesResponseListener by lazy { SwiftLevelTwoCategoriesResponseListener(this) }
    private val bopDataValidationResponseListener by lazy { SwiftBopDataValidationResponseListener(this) }
    private val bopFieldsResponseListener by lazy { SwiftBopFieldsResponseListener(this) }
    private val swiftQuoteResponseListener by lazy { SwiftQuoteResponseListener(this) }
    private val processTransactionResponseListener by lazy { SwiftProcessTransactionResponseListener(this) }

    var swiftTransaction: MutableLiveData<SwiftTransactionPending> = MutableLiveData()
    val levelOneCategoriesResponse: MutableLiveData<SwiftLevelOneCategoriesResponse> = MutableLiveData()
    val levelTwoCategoriesResponse: MutableLiveData<SwiftLevelTwoCategoriesResponse> = MutableLiveData()
    var bopFieldsResponse: MutableLiveData<SwiftBopFieldsResponse> = MutableLiveData()
    var bopDataValidationResponse: MutableLiveData<SwiftBopDataValidationResponse> = MutableLiveData()
    var swiftQuoteResponse: MutableLiveData<SwiftQuoteResponse> = MutableLiveData()
    var processTransactionResponse: MutableLiveData<SwiftProcessTransactionResponse> = MutableLiveData()

    var senderType: SenderType? = null
    var selectedLevelOneCategory: String? = null
    var selectedLevelTwoCategory: SwiftLevelTwoCategoryResponse? = null

    fun hasCharges(): Boolean = getTotalCharges().compareTo(0.00) != 0

    fun requestLevel1Categories() {
        val swiftTransaction = swiftTransaction.value ?: return
        swiftInteractor.requestLevel1Categories(SWIFT_CATEGORY_FLOW, swiftTransaction.toAccount, getSenderType(), levelOneCategoriesResponseListener)
    }

    fun requestLevel2Categories() {
        val swiftTransaction = swiftTransaction.value ?: return
        swiftInteractor.requestLevel2Categories(SWIFT_CATEGORY_FLOW, swiftTransaction.toAccount, getSenderType(), selectedLevelOneCategory
                ?: "", levelTwoCategoriesResponseListener)
    }

    fun requestBopFields() {
        val swiftTransaction = swiftTransaction.value ?: return
        val levelTwoCategory = selectedLevelTwoCategory ?: return
        val bopFieldsRequestDataModel = SwiftBopFieldsRequestDataModel(levelTwoCategory.categoryCode,
                levelTwoCategory.subCategoryCode, SWIFT_CATEGORY_FLOW, getTypeOfPayment(), "ZA",
                swiftTransaction.originatingCountryCode, swiftTransaction.foreignCurrencyCode,
                swiftTransaction.transactionDate, swiftTransaction.senderFirstName, swiftTransaction.senderLastName,
                swiftTransaction.toAccount, swiftTransaction.caseIDNumber)

        swiftInteractor.requestBopFields(bopFieldsRequestDataModel, bopFieldsResponseListener)
    }

    fun requestBopDataValidation() {
        val swiftTransaction = swiftTransaction.value ?: return
        val levelTwoCategory = selectedLevelTwoCategory ?: return
        val bopDataValidationRequestDataModel = SwiftBopDataValidationRequestDataModel(levelTwoCategory.categoryCode,
                levelTwoCategory.subCategoryCode, levelTwoCategory.rulingCode, swiftTransaction.foreignCurrencyCode,
                SWIFT_CATEGORY_FLOW, swiftTransaction.foreignCurrencyAmount, getTypeOfPayment())

        swiftInteractor.requestBopDataValidation(bopDataValidationRequestDataModel, bopDataValidationResponseListener)
    }

    fun requestSwiftQuote() {
        val swiftTransaction = swiftTransaction.value ?: return
        val levelTwoCategory = selectedLevelTwoCategory ?: return
        val swiftQuoteRequestDataModel = SwiftQuoteRequestDataModel(swiftTransaction.toAccount,
                levelTwoCategory.categoryCode, swiftTransaction.foreignCurrencyCode, SWIFT_CATEGORY_FLOW,
                swiftTransaction.foreignCurrencyAmount, "ZA", swiftTransaction.whoWillPay, swiftTransaction.caseIDNumber)

        swiftInteractor.requestSwiftQuote(swiftQuoteRequestDataModel, swiftQuoteResponseListener)
    }

    fun requestProcessTransaction() {
        val swiftTransaction = swiftTransaction.value ?: return
        val levelTwoCategory = selectedLevelTwoCategory ?: return
        val swiftQuote = swiftQuoteResponse.value?.swiftQuoteDetailsResponse ?: return
        val processTransactionRequestDataModel = SwiftProcessTransactionRequestDataModel(swiftTransaction.toAccount,
                swiftTransaction.expectedPayoutAmount, swiftTransaction.caseIDNumber, swiftTransaction.foreignCurrencyCode,
                SWIFT_CATEGORY_FLOW, swiftTransaction.foreignCurrencyAmount, swiftTransaction.senderFirstName,
                swiftTransaction.originatingCountryCode, levelTwoCategory.categoryCode, levelTwoCategory.subCategoryCode,
                swiftTransaction.foreignCurrencyCode, swiftQuote.destinationCurrencyRate, swiftQuote.localCurrency,
                swiftQuote.commissionAmountFee, swiftQuote.vatAmount, swiftQuote.totalDue, swiftQuote.beneficiaryName,
                swiftQuote.beneficiarySurname, swiftQuote.valueDate, swiftQuote.localAmount)

        swiftInteractor.requestProcessTransaction(processTransactionRequestDataModel, processTransactionResponseListener)
    }

    fun getAccountDetails(): AccountObject? = AbsaCacheManager.getInstance().accountsList.accountsList.find { accountObject ->
        accountObject.accountNumber.replace(" ", "") == swiftTransaction.value?.toAccount?.replace(" ", "")
    }

    fun getTotalCharges(): Double = try {
        getAbsaCharges().plus(getSwiftCharges()).plus(getRecoveryCharges()).plus(getVatCharges())
    } catch (e: Exception) {
        0.00
    }

    // 0.00 is placeholder for absa charges data that will eventually become available
    fun getAbsaCharges(): Double = 0.00

    // 0.00 is placeholder for swift charges data that will eventually become available
    fun getSwiftCharges(): Double = 0.00

    fun getRecoveryCharges(): Double = try {
        swiftQuoteResponse.value?.swiftQuoteDetailsResponse?.commissionAmountFee?.toDouble() ?: 0.00
    } catch (e: Exception) {
        0.00
    }

    fun getVatCharges(): Double = try {
        swiftQuoteResponse.value?.swiftQuoteDetailsResponse?.vatAmount?.toDouble() ?: 0.00
    } catch (e: Exception) {
        0.00
    }

    private fun getSenderType() = senderType?.key ?: ""

    private fun getTypeOfPayment(): String {
        val swiftTransaction = swiftTransaction.value ?: SwiftTransactionPending()
        val beneficiaryType = if (swiftTransaction.beneficiaryType.equals("business", true)) SenderType.BUSINESS.indicator else SenderType.INDIVIDUAL.indicator
        val senderType = senderType?.indicator ?: ""
        return "${beneficiaryType}2${senderType}"
    }
}