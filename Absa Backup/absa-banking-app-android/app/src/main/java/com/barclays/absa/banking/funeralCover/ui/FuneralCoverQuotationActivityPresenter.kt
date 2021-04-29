/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */

package com.barclays.absa.banking.funeralCover.ui

import com.barclays.absa.banking.boundary.model.funeralCover.FuneralCoverDetails
import com.barclays.absa.banking.framework.AbstractPresenter
import com.barclays.absa.banking.funeralCover.services.FuneralCoverQuoteInteractor
import com.barclays.absa.banking.funeralCover.ui.responseListeners.FuneralCoverQuotationExtendedResponseListener
import org.jetbrains.annotations.TestOnly
import java.lang.ref.WeakReference

class FuneralCoverQuotationActivityPresenter internal constructor(coverQuotationActivityViewWeakReference: WeakReference<FuneralCoverQuotationActivityView>)
    : AbstractPresenter(coverQuotationActivityViewWeakReference) {
    private var funeralCoverQuoteInteractor = FuneralCoverQuoteInteractor()
    private val funeralCoverQuotationExtendedResponseListener: FuneralCoverQuotationExtendedResponseListener by lazy { FuneralCoverQuotationExtendedResponseListener(this) }

    @TestOnly
    internal constructor(funeralCoverQuoteInteractor: FuneralCoverQuoteInteractor, coverQuotationActivityViewWeakReference: WeakReference<FuneralCoverQuotationActivityView>)
            : this(coverQuotationActivityViewWeakReference) {
        this.funeralCoverQuoteInteractor = funeralCoverQuoteInteractor
    }

    fun onAcceptQuoteButtonClicked(funeralCoverDetails: FuneralCoverDetails) {
        funeralCoverQuoteInteractor.pullFuneralPlanApplication(funeralCoverQuotationExtendedResponseListener, funeralCoverDetails)
        showProgressIndicator()
    }

    fun onFuneralCoverApplicationSubmitted(funeralCoverDetails: FuneralCoverDetails) {
        val funeralCoverQuotationActivityView = viewWeakReference.get() as FuneralCoverQuotationActivityView?
        funeralCoverQuotationActivityView?.let {
            it.showFuneralApplicationSuccessResponse(funeralCoverDetails)
            dismissProgressIndicator()
        }
    }

    fun onServiceFailed() {
        val funeralCoverQuotationActivityView = viewWeakReference.get() as FuneralCoverQuotationActivityView?
        funeralCoverQuotationActivityView?.let {
            it.showSomethingWentWrongScreen()
            dismissProgressIndicator()
        }
    }

    fun onFailedToSubmitFuneralCoverApplication(funeralCoverDetails: FuneralCoverDetails) {
        val funeralCoverQuotationActivityView = viewWeakReference.get() as FuneralCoverQuotationActivityView?
        funeralCoverQuotationActivityView?.let {
            it.showFuneralApplicationFailureResponse(funeralCoverDetails)
            dismissProgressIndicator()
        }
    }
}
