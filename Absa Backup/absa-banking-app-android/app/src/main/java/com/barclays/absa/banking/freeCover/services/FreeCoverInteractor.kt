/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.freeCover.services

import com.barclays.absa.banking.framework.AbstractInteractor
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.freeCover.services.dto.ApplyForFreeCoverRequest
import com.barclays.absa.banking.freeCover.services.dto.ApplyForFreeCoverResponse
import com.barclays.absa.banking.freeCover.services.dto.ApplyFreeCoverData
import com.barclays.absa.banking.freeCover.services.dto.CoverAmountApplyFreeCoverRequest
import com.barclays.absa.banking.freeCover.services.dto.CoverAmountApplyFreeCoverResponse

class FreeCoverInteractor : AbstractInteractor(), FreeCoverService {
    override fun applyFreeCover(applyFreeCoverData: ApplyFreeCoverData, applyForFreeCoverExtendedResponseListener: ExtendedResponseListener<ApplyForFreeCoverResponse>) {
        submitRequest(ApplyForFreeCoverRequest(applyFreeCoverData, applyForFreeCoverExtendedResponseListener))
    }

    override fun fetchCoverAmount(coverAmountApplyFreeCoverExtendedResponseListener: ExtendedResponseListener<CoverAmountApplyFreeCoverResponse>) {
        submitRequest(CoverAmountApplyFreeCoverRequest(coverAmountApplyFreeCoverExtendedResponseListener))
    }
}