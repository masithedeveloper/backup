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

import com.barclays.absa.banking.framework.AbstractInteractor
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.shared.services.dto.*

class SharedInteractor : AbstractInteractor(), SharedService {

    override fun fetchCIFCodes(sourceOfFundsFilter: SourceOfFundsLookUpType, lookUpType: CIFCodeLookUpType, getCodesExtendedResponseListener: ExtendedResponseListener<GetCodesResponse?>) {
        val getCIFCodesRequest = GetCIFCodesRequest(sourceOfFundsFilter, lookUpType, getCodesExtendedResponseListener)
        submitRequest(getCIFCodesRequest)
    }

    override fun performLookup(cifGroupCode: CIFGroupCode, lookupExtendedResponseListener: ExtendedResponseListener<LookupResult>) {
        val lookupRequest = LookupRequest(cifGroupCode, lookupExtendedResponseListener)
        submitRequest(lookupRequest)
    }

    override fun fetchSuburbs(area: String, extendedResponseListener: ExtendedResponseListener<SuburbResponse>) {
        val suburbRequest = SuburbRequest(area, extendedResponseListener)
        submitRequest(suburbRequest)
    }
}