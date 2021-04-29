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

import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.freeCover.services.dto.ApplyForFreeCoverResponse
import com.barclays.absa.banking.freeCover.services.dto.ApplyFreeCoverData
import com.barclays.absa.banking.freeCover.services.dto.CoverAmountApplyFreeCoverResponse

interface FreeCoverService {
    companion object {
        const val OP2191_APPLY_FOR_FREE_COVER = "OP2191"
        const val OP2190_GET_COVER_AMOUNT = "OP2190"
    }

    fun applyFreeCover(applyFreeCoverData: ApplyFreeCoverData, applyForFreeCoverExtendedResponseListener: ExtendedResponseListener<ApplyForFreeCoverResponse>)
    fun fetchCoverAmount(coverAmountApplyFreeCoverExtendedResponseListener: ExtendedResponseListener<CoverAmountApplyFreeCoverResponse>)
}