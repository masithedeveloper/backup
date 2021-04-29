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
package com.barclays.absa.banking.boundary.model.airtime

import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject
import com.barclays.absa.banking.boundary.model.NetworkProvider
import com.barclays.absa.banking.framework.data.ResponseObject

class AirtimeBuyBeneficiary : ResponseObject() {
    var benficiaryName: String? = null
    var airtimeType: String? = null
    var amount: Amount? = null
    var txtfromAccount: String? = null
    var cellNumber: String? = null
    var lastTransactionDate: String? = null
    var transactionType: String? = null
    var lastTransactionAmount: Amount? = null
    var networkProviders: List<NetworkProvider>? = null
    var smsProviders: List<NetworkProvider>? = null
    var pay_ID: String? = null
    var fromAccounts: List<AccountObject>? = null
    var toBeneficiaries: List<AirtimeBuyBeneficiary>? = null
    var beneficiaryDetails: BeneficiaryDetailObject? = null
    var beneficiaryImage: String? = null
    var networkProvider: String? = null
    var networkProviderCode: String? = null
}