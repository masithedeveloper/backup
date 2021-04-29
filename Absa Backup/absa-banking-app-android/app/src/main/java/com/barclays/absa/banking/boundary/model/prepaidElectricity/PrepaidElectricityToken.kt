/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.boundary.model.prepaidElectricity

import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class PrepaidElectricityToken : ResponseObject() {

    @JsonProperty("tokenId")
    var tokenId: String? = null
    @JsonProperty("meterNumber")
    var meterNumber: String? = null
    @JsonProperty("utility")
    var utility: String? = null
    @JsonProperty("arrearsAmnt")
    var arrearsAmount: String? = null
    @JsonProperty("refNumber")
    var referenceNumber: String? = null
    @JsonProperty("supplierCode")
    var supplierCode: String? = null
    @JsonProperty("keyRevCode")
    var keyRevisionCode: String? = null
    @JsonProperty("tarrifIndex")
    var tarifIndex: String? = null
    @JsonProperty("algorithm")
    var algorithm: String? = null
    @JsonProperty("transactionType")
    var transactionType: String? = null
    @JsonProperty("customerMsg")
    var customerMessage: String? = null
    @JsonProperty("taxInvoiceNum")
    var taxInvoiceNumber: String? = null
    @JsonProperty("vatNumber")
    var vatNumber: String? = null
    @JsonProperty("tarrifName")
    var tarifName: String? = null
    @JsonProperty("receiptNum")
    var receiptNumber: String? = null
    @JsonProperty("tokenNumber")
    var tokenNumber: String? = null
    @JsonProperty("tokenValue")
    var tokenValue: String? = null
    @JsonProperty("tokenTax")
    var tokenTax: String? = null
    @JsonProperty("tokenUnits")
    var tokenUnits: String? = null
    @JsonProperty("tokenReceiptNum")
    var tokenReceiptNumber: String? = null
    @JsonProperty("tokenDateStamp")
    var tokenDateStamp: String? = null
    @JsonProperty("tokenDescription")
    var tokenDescription: String? = null
    @JsonProperty("tokenType")
    var tokenType: String? = null
    @JsonProperty("tarrif")
    var tarif: String? = null
    @JsonProperty("debtReason")
    var debtReason: String? = null
    @JsonProperty("debtAmount")
    var debtAmount: String? = null
    @JsonProperty("debtTax")
    var debtTax: String? = null
    @JsonProperty("debtRemainer")
    var debtRemainer: String? = null
    @JsonProperty("debtReceiptNo")
    var debtReceiptNumber: String? = null
    @JsonProperty("reason")
    var reason: String? = null
    @JsonProperty("amount")
    var amount: String? = null
    @JsonProperty("tax")
    var tax: String? = null
    @JsonProperty("receipt")
    var receipt: String? = null
    @JsonProperty("customerName")
    var customerName: String? = null
    @JsonProperty("customerAddress")
    var customerAddress: String? = null
    @JsonProperty("customerStandNo")
    var customerStandNumber: String? = null
    @JsonProperty("customerArea")
    var customerArea: String? = null
    @JsonProperty("tarrifBlockCount")
    var tarifBlockCount: String? = null
    @JsonProperty("prepaidElecTariffBlockDetail")
    var prepaidElectricityTarifBlockDetails: List<PrepaidElectricityTarifBlockDetail> = ArrayList()
    @JsonProperty("tokenAmount")
    var tokenAmount: String? = null

}
