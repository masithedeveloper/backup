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

package com.barclays.absa.banking.express.payments.dto

import com.barclays.absa.banking.express.beneficiaries.dto.BeneficiaryAccountType
import com.barclays.absa.banking.express.beneficiaries.dto.BeneficiaryNotification
import com.barclays.absa.banking.express.beneficiaries.dto.BeneficiaryType
import com.barclays.absa.banking.express.beneficiaries.dto.RegularBeneficiary
import com.barclays.absa.banking.express.shared.dto.InstructionType
import com.barclays.absa.banking.shared.BaseModel
import com.barclays.absa.utils.DateTimeHelper.DASHED_PATTERN_YYYY_MM_DD_HH_MM
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import za.co.absa.networking.jackson.JsonDateFormat
import za.co.absa.networking.jackson.JsonDateSerializer
import java.util.*

abstract class BaseValidatePaymentRequest(beneficiary: RegularBeneficiary) : BaseModel {
    var beneficiaryName: String = beneficiary.beneficiaryDetails.beneficiaryName
    var targetAccountNumber: String = beneficiary.beneficiaryDetails.targetAccountNumber
    var beneficiaryAccountType: BeneficiaryAccountType = beneficiary.beneficiaryDetails.accountType
    var clearingCodeOrInstitutionCode: String = beneficiary.beneficiaryDetails.clearingCodeOrInstitutionCode
    var bankName: String = beneficiary.beneficiaryDetails.bankOrInstitutionName
    var sourceAccountReference: String = beneficiary.beneficiaryDetails.sourceAccountReference
    var targetAccountReference: String = beneficiary.beneficiaryDetails.targetAccountReference
    var cifKey: String = beneficiary.beneficiaryDetails.cifKey
    var uniqueEFTNumber: String = beneficiary.beneficiaryDetails.uniqueEFTNumber
    var beneficiaryStatus: String = beneficiary.beneficiaryDetails.beneficiaryStatus
    var typeOfBeneficiary: BeneficiaryType = beneficiary.beneficiaryDetails.typeOfBeneficiary
    var verifiedPaymentIndicator: Boolean = beneficiary.beneficiaryDetails.verifiedPaymentIndicator
    var instructionType: InstructionType = beneficiary.beneficiaryDetails.instructionType
    var paymentAmount: String = ""
    var notes: String = ""
    var paymentSourceAccountNumber: String = ""
    var useTime: Boolean = false

    @JsonSerialize(using = JsonDateSerializer::class)
    @JsonDateFormat(dateFormat = DASHED_PATTERN_YYYY_MM_DD_HH_MM)
    var paymentTransactionDateAndTime: Date = Date()

    var immediatePayment: Boolean = false
    var retryIIP: Int = 0
    var realTimeProcessedPayment: Boolean = false
    var realTimeProcessedPaymentReferenceNumber: String = ""
    var paymentNumber: String = ""

    @JsonProperty("ownNotificationVO")
    var ownNotification: BeneficiaryNotification = beneficiary.beneficiaryDetails.ownNotification
    var beneficiaryNotification: BeneficiaryNotification = beneficiary.beneficiaryDetails.beneficiaryNotification.apply {
        recipientName = beneficiaryName
    }
}