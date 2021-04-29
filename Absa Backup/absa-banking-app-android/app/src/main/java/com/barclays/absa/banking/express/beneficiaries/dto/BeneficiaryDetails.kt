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

package com.barclays.absa.banking.express.beneficiaries.dto

import com.barclays.absa.banking.express.shared.dto.InstructionType
import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty

class BeneficiaryDetails : BaseModel {
    var cifKey: String = ""
    var tieBreaker: String = ""
    var beneficiaryNumber: Int = 0
    var beneficiaryStatus: String = ""
    var typeOfBeneficiary: BeneficiaryType = BeneficiaryType.INTERNAL
    var uniqueEFTNumber: String = ""
    var instructionType: InstructionType = InstructionType.NORMAL
    var realTimePaymentAllowed = false
    var clearingCodeOrInstitutionCode: String = ""
    var targetAccountNumber: String = ""
    var institutionAccountNumber: String = ""
    @JsonProperty("bankName")
    var bankOrInstitutionName: String = ""
    var branchName: String = ""
    var sourceAccountReference: String = ""
    var targetAccountReference: String = ""
    var beneficiaryName: String = ""
    var ownNotification: BeneficiaryNotification = BeneficiaryNotification()
    var beneficiaryNotification: BeneficiaryNotification = BeneficiaryNotification()
    var accountType: BeneficiaryAccountType = BeneficiaryAccountType.NONE
    var verifiedPaymentIndicator = false
    var beneficiaryContactDetails: String = ""
    var ownNotificationType: BeneficiaryNotificationMethod = BeneficiaryNotificationMethod.NONE
}