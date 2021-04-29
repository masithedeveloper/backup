package com.barclays.absa.banking.funeralCover.ui.responseListeners

import com.barclays.absa.banking.boundary.model.funeralCover.FuneralCoverDetails
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.banking.funeralCover.ui.FuneralCoverQuotationActivityPresenter

class FuneralCoverQuotationExtendedResponseListener internal constructor(private val funeralCoverQuotationActivityPresenter: FuneralCoverQuotationActivityPresenter) : ExtendedResponseListener<FuneralCoverDetails>() {

    override fun onSuccess(funeralCoverDetails: FuneralCoverDetails?) {
        funeralCoverDetails?.let {
            if (BMBConstants.SUCCESS.equals(it.txnStatus, ignoreCase = true)) {
                funeralCoverQuotationActivityPresenter.onFuneralCoverApplicationSubmitted(it)
            } else if (BMBConstants.FAILURE.equals(it.txnStatus, ignoreCase = true)) {
                funeralCoverQuotationActivityPresenter.onFailedToSubmitFuneralCoverApplication(it)
            }
        }
    }

    override fun onFailure(failureResponse: ResponseObject?) {
        funeralCoverQuotationActivityPresenter.onServiceFailed()
    }
}