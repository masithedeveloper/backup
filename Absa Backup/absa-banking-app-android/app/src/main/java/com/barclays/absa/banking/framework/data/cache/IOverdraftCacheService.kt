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

interface IOverdraftCacheService {

    fun getOverdraftResponse(): OverdraftResponse?
    fun setOverdraftResponse(overdraftResponse: OverdraftResponse)

    fun getOverdraftQuoteDetails(): OverdraftQuoteDetailsObject?
    fun setOverdraftQuoteDetails(overdraftQuoteDetailsObject: OverdraftQuoteDetailsObject)

    fun getOverdraftSetup(): OverdraftSetup?
    fun setOverdraftSetup(overdraftSetupObject: OverdraftSetup)

    fun clear()
}