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

package com.barclays.absa.banking.express.beneficiaries.addRegularBeneficiary.dto

import com.barclays.absa.banking.express.beneficiaries.dto.BeneficiaryAccountType
import com.barclays.absa.banking.express.beneficiaries.dto.BeneficiaryDetails
import com.barclays.absa.banking.express.beneficiaries.dto.BeneficiaryNotification
import com.barclays.absa.banking.express.beneficiaries.dto.BeneficiaryType
import com.barclays.absa.banking.express.shared.dto.InstructionType
import com.barclays.absa.banking.shared.BaseModel

class AddBeneficiaryRequest(beneficiaryDetails: BeneficiaryDetails) : BaseModel {
    var typeOfBeneficiary: BeneficiaryType = beneficiaryDetails.typeOfBeneficiary
    var instructionType: InstructionType = beneficiaryDetails.instructionType
    var sourceAccountReference: String = beneficiaryDetails.sourceAccountReference
    var targetAccountReference: String = beneficiaryDetails.targetAccountReference
    var targetAccountNumber: String = beneficiaryDetails.targetAccountNumber
    var beneficiaryName: String = beneficiaryDetails.beneficiaryName
    var bankNameOrInstitutionName: String = beneficiaryDetails.bankOrInstitutionName
    var clearingCodeOrInstitutionCode: String = beneficiaryDetails.clearingCodeOrInstitutionCode
    var targetAccountType: BeneficiaryAccountType = beneficiaryDetails.accountType
    var beneficiaryNotification: BeneficiaryNotification = beneficiaryDetails.beneficiaryNotification
    var ownNotification: BeneficiaryNotification = beneficiaryDetails.ownNotification
    var switchDetailsForAddBeneficiary: SwitchDetailsForAddBeneficiary? = null
}