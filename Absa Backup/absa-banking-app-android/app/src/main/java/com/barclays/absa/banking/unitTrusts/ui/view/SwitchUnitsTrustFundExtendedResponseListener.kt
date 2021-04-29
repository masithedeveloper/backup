package com.barclays.absa.banking.unitTrusts.ui.view

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.unitTrusts.services.dto.SwitchFundsListResponse

class SwitchUnitsTrustFundExtendedResponseListener(private val switchUnitTrustFundsLiveData: MutableLiveData<SwitchFundsListResponse>) : ExtendedResponseListener<SwitchFundsListResponse>() {
    override fun onSuccess(successResponse: SwitchFundsListResponse) {
        switchUnitTrustFundsLiveData.value = successResponse
    }
}