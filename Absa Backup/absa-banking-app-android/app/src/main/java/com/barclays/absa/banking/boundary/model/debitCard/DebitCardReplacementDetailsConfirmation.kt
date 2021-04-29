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
package com.barclays.absa.banking.boundary.model.debitCard

import com.barclays.absa.banking.framework.data.ResponseObject


class DebitCardReplacementDetailsConfirmation : ResponseObject() {

    var cardType: String? = null
    var oldDebitCardNumber: String? = null
    var reasonCode: String? = null
    var reason: String? = null
    var productDescription: String? = null
    var productCode: String? = null
    var productType: String? = null
    var debitCardType: String? = null
    var brandNumber: String? = null
    var brandType: String? = null
    var cardDeliveryMethod: String? = null
    var branchCode: String? = null
    var branchAddress: String? = null
    var preferredBranch: String? = null
    var clientAddress: String? = null
    var clientContactNumber: String? = null
    var workTelephoneNumber: String? = null
    var homeTelephoneNumber: String? = null
    var addressLine1: String? = null
    var addressLine2: String? = null
    var suburb: String? = null
    var town: String? = null
    var postalCode: String? = null
    var replacementFee: String? = null
    var replacementFeeApplicable: String? = null
    var replacementFeeType: String? = null
    var deliveryFeeApplicable: String? = null
    var cardDeliveryFeeType: String? = null
    var cardDeliveryFee: String? = null

}
