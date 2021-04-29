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
package com.barclays.absa.banking.boundary.model.creditCardStopAndReplace

import java.io.Serializable

class CreditCardReplacement : Serializable {
    var reasonForReplacement: String? = ""
    var creditCardnumber: String? = ""
    var incidentDate: String? = ""
    var lastUsedDate: String? = ""
    var cardType: String? = ""
    var cardLimit: String? = ""
    var deliveryMethod: String? = ""
    var contactNumber: String? = ""
    var selectedBranch: String? = ""
    var selectedBranchCode: String? = ""
}
