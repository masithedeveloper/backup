/*
 *
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package com.barclays.absa.banking.card.services.card.dto

import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.barclays.absa.banking.card.services.card.CardService.OP2056_UPDATE_TRAVEL_NOTIFICATION
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.utils.DateUtils
import java.util.*

class TravelUpdateRequest<T>(travelUpdateModel: TravelUpdateModel, responseListener: ExtendedResponseListener<T>)
    : ExtendedRequest<T>(responseListener) {

    init {
        val currentDate = DateUtils.hyphenateDate(Calendar.getInstance().time).replace("-", "")
        val referralStartDate = travelUpdateModel.referralStartDate?.replace("-".toRegex(), "") ?: currentDate
        val referralEndDate = travelUpdateModel.referralEndDate?.replace("-".toRegex(), "") ?: currentDate

        params = RequestParams.Builder()
                .put(OP2056_UPDATE_TRAVEL_NOTIFICATION)
                .put(CardRequestParameters.CARD_NUMBER.key, travelUpdateModel.cardNumber)
                .put(CardRequestParameters.ACTION_SELECTED.key, travelUpdateModel.actionSelected)
                .put(CardRequestParameters.REFERRAL_START_DATE.key, referralStartDate)
                .put(CardRequestParameters.REFERRAL_END_DATE.key, referralEndDate)
                .build()

        mockResponseFile = "card/op2056_travel_abroad_updated.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = SureCheckResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = false
}