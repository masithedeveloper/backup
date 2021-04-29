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

package com.barclays.absa.banking.shared.services

import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.shared.services.dto.*

interface SharedService {
    companion object {
        const val OP0861_LOOKUP = "OP0861"
        const val OP2099_GET_SUBURBS = "OP2099"
        const val AREA = "area"
        const val OP2022_GET_CODES = "OP2022"
    }

    fun fetchCIFCodes(sourceOfFundsFilter: SourceOfFundsLookUpType, lookUpType: CIFCodeLookUpType, getCodesExtendedResponseListener: ExtendedResponseListener<GetCodesResponse?>)
    fun performLookup(cifGroupCode: CIFGroupCode, lookupExtendedResponseListener: ExtendedResponseListener<LookupResult>)
    fun fetchSuburbs(area: String, extendedResponseListener: ExtendedResponseListener<SuburbResponse>)
}