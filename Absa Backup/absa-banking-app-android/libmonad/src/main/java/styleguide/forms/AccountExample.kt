/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */

package styleguide.forms

class AccountExample : SelectorInterface {

    var cardNumber: String = ""
    var accountType: String = ""
    var cardHolderName: String = ""

    override val displayValue: String?
        get() = cardHolderName

    override val displayValueLine2: String?
        get() = cardNumber
}