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

import com.barclays.absa.banking.framework.AbstractInteractor
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.will.services.dto.*

class WillInteractor : AbstractInteractor(), WillService {

    override fun fetchWill(pdfRequired: Boolean, extendedResponseListener: ExtendedResponseListener<WillResponse>) {
        submitRequest(WillRequest(pdfRequired, extendedResponseListener))
    }

    override fun fetchPortfolioInfo(extendedResponseListener: ExtendedResponseListener<PortfolioInfoResponse>) {
        submitRequest(PortfolioInfoRequest(extendedResponseListener))
    }

    override fun saveCallMeBack(saveCallMeBackInfo: SaveCallMeBackInfo, extendedReponseListener: ExtendedResponseListener<SaveCallMeBackResponse>) {
        submitRequest(SaveCallMeBackRequest(saveCallMeBackInfo, extendedReponseListener))
    }
}