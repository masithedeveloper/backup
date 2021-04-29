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

package com.barclays.absa.banking.express.shared.dto

import com.barclays.absa.banking.shared.BaseModel

open class BeneficiaryDetails : BaseModel {
    var cifKey: String = ""
    var tieBreaker: String = ""
    var beneficiaryNumber: Int = 0
    var instructionType: InstructionType = InstructionType.NORMAL
    var uniqueEFTNumber: String = ""
    var beneficiaryStatus: String = ""
}