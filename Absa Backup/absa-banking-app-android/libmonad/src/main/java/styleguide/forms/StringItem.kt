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

data class StringItem(var item: String? = "", var item2: String? = "") : SelectorInterface {
    constructor(item: String) : this(item, "")

    constructor() : this("", "")

    override val displayValue: String?
        get() = item
    override val displayValueLine2: String?
        get() = item2
}