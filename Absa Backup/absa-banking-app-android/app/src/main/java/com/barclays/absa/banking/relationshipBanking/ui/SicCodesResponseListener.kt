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

package com.barclays.absa.banking.relationshipBanking.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.relationshipBanking.services.dto.SicCodesResponse
import com.barclays.absa.banking.shared.services.dto.CodesLookupDetails

class SicCodesResponseListener(private val sicCodesListLiveData: MutableLiveData<List<CodesLookupDetails>>) : ExtendedResponseListener<SicCodesResponse>() {
    override fun onSuccess(successResponse: SicCodesResponse) {
        super.onSuccess()
        sicCodesListLiveData.value = successResponse.codesLookupDetails
    }
}