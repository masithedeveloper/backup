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

package com.barclays.absa.banking.will.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.barclays.absa.banking.will.services.WillInteractor
import com.barclays.absa.banking.will.services.dto.PortfolioInfoResponse
import com.barclays.absa.banking.will.services.dto.SaveCallMeBackInfo
import com.barclays.absa.banking.will.services.dto.SaveCallMeBackResponse
import com.barclays.absa.banking.will.services.dto.WillResponse

class WillViewModel : BaseViewModel() {
    var willInteractor = WillInteractor()
    var isFromSettings = false

    private val willExtendedResponseListener: ExtendedResponseListener<WillResponse> = WillExtendedResponseListener(this)

    private val portfolioExtendedResponseListener: ExtendedResponseListener<PortfolioInfoResponse> by lazy {
        PortfolioInfoExtendedResponseListener(this)
    }

    private val saveCallMeBackExtendedReponseListener: ExtendedResponseListener<SaveCallMeBackResponse> by lazy {
        SaveCallMeBackExtendedResponseListener(this)
    }
    var willExtendedResponse = MutableLiveData<WillResponse>()
    var portfolioExtendedResponse = MutableLiveData<PortfolioInfoResponse>()
    var saveCallMeBackResponse = MutableLiveData<SaveCallMeBackResponse>()

    fun fetchWill(isPdfRequired: Boolean) {
        willInteractor.fetchWill(isPdfRequired, willExtendedResponseListener)
    }

    fun fetchPortfolioInfo() {
        willInteractor.fetchPortfolioInfo(portfolioExtendedResponseListener)
    }

    fun saveCallMeBack(saveCallMeBackInfo: SaveCallMeBackInfo) {
        willInteractor.saveCallMeBack(saveCallMeBackInfo, saveCallMeBackExtendedReponseListener)
    }
}