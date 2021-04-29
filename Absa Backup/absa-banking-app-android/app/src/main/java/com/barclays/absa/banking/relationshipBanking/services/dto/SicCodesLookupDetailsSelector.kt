/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.relationshipBanking.services.dto

import styleguide.forms.SelectorInterface
import styleguide.utils.extensions.toSentenceCase

data class SicCodesLookupDetailsSelector(var engCodeDescription: String, var itemCode: String, var codesLookupType: String) : SelectorInterface {

    override val displayValue: String?
        get() = engCodeDescription.replace("_", " ").toSentenceCase()

    override val displayValueLine2: String?
        get() = engCodeDescription
}