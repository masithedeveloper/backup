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
import java.io.Serializable

open class SourceAccount : BaseModel, Serializable {
    var accountNumber: String = ""
    var availableBalance: String = ""
    var balance: String = ""
    var accountName: String = ""
    var accountType: String = ""
    var accountNameAndNumber: String = ""
    var productCode: String = ""

    var isBalanceMasked: Boolean = false // TODO See if still needed
}