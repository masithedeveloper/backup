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
package com.barclays.absa.banking.framework.data.cache

import com.barclays.absa.banking.boundary.model.overdraft.OverdraftQuoteDetailsObject
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftResponse
import com.barclays.absa.banking.boundary.model.overdraft.OverdraftSetup

class OverdraftCacheService : IOverdraftCacheService {

    companion object {
        private const val OVERDRAFT_DETAILS = "OVERDRAFT_DETAILS"
        private const val OVERDRAFT_RESPONSE = "OVERDRAFT_RESPONSE"
        private const val OVERDRAFT_SETUP = "OVERDRAFT_SETUP"
    }

    private var CACHE = HashMap<String, Any>()

    override fun getOverdraftResponse(): OverdraftResponse? = CACHE[OVERDRAFT_RESPONSE] as? OverdraftResponse
    override fun setOverdraftResponse(overdraftResponse: OverdraftResponse) {
        CACHE[OVERDRAFT_RESPONSE] = overdraftResponse
    }

    override fun getOverdraftQuoteDetails(): OverdraftQuoteDetailsObject? = CACHE[OVERDRAFT_DETAILS] as? OverdraftQuoteDetailsObject
    override fun setOverdraftQuoteDetails(overdraftQuoteDetailsObject: OverdraftQuoteDetailsObject) {
        CACHE[OVERDRAFT_DETAILS] = overdraftQuoteDetailsObject
    }

    override fun getOverdraftSetup(): OverdraftSetup? = CACHE[OVERDRAFT_SETUP] as? OverdraftSetup
    override fun setOverdraftSetup(overdraftSetupObject: OverdraftSetup) {
        CACHE[OVERDRAFT_SETUP] = overdraftSetupObject
    }

    override fun clear() {
        CACHE = HashMap()
    }
}