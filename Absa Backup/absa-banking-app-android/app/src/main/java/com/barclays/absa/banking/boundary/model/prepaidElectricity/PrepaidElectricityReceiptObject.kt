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

class PrepaidElectricityReceiptObject : ResponseObject() {

    var purchasedUnit: String? = null
    var transactionDateTimeStamp: String? = null
    var tokenTax: String? = null
    var isHasImage: Boolean = false
    var imageName: String? = null
    var arrearsAmount: String? = null
    var vat: String? = null
    var chargesReason: String? = null
    var chargesAmount: String? = null
    var costOfElectricity: String? = null
    var ti: String? = null
    var scg: String? = null
    var krn: String? = null
    var totalUnits: String? = null

}
