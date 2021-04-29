/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.flexiFuneral.services.dto

import com.barclays.absa.banking.shared.BaseModel

class FlexiFuneralPolicyDetails : BaseModel {
    var policyNumber: String = ""
    var commencementDate: String = ""
    var planCounter: String = ""
    var spousePlanCounter: String = ""
    var totalCoverAmount: String = ""
    var totalPremium: String = ""
    var noOfBeneficieries: String = ""
    var noOfRoleplayers: String = ""
    var noOfBenefits: String = ""
    var isPolicyLink: String = ""
    var doubleAccidentalDeath: String = ""
    var repatriation: String = ""
}