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

package com.barclays.absa.banking.express.payments.validatePayment.dto

import com.barclays.absa.banking.express.beneficiaries.dto.RegularBeneficiary
import com.barclays.absa.banking.express.payments.dto.BaseValidatePaymentRequest

class ValidatePaymentRequest(val beneficiary: RegularBeneficiary) : BaseValidatePaymentRequest(beneficiary) {
    var beneficiaryNumber = beneficiary.beneficiaryDetails.beneficiaryNumber
    var tieBreaker: String = beneficiary.beneficiaryDetails.tieBreaker
    var updateBeneficiaryNotificationDetails: Boolean = true
}