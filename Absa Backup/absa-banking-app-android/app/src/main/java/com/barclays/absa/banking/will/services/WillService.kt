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

package com.barclays.absa.banking.will.services

import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.will.services.dto.PortfolioInfoResponse
import com.barclays.absa.banking.will.services.dto.SaveCallMeBackInfo
import com.barclays.absa.banking.will.services.dto.SaveCallMeBackResponse
import com.barclays.absa.banking.will.services.dto.WillResponse

interface WillService {

    companion object {
        const val OP2115_FETCH_WILL = "OP2115"
        const val OP0851_PORTFOLIO_INFO = "OP0851"
        const val OP0875_SAVE_CALL_ME_BACK = "OP0875"
    }

    fun fetchWill(pdfRequired: Boolean, extendedResponseListener: ExtendedResponseListener<WillResponse>)
    fun fetchPortfolioInfo(extendedResponseListener: ExtendedResponseListener<PortfolioInfoResponse>)
    fun saveCallMeBack(saveCallMeBackInfo: SaveCallMeBackInfo, extendedReponseListener: ExtendedResponseListener<SaveCallMeBackResponse>)
}