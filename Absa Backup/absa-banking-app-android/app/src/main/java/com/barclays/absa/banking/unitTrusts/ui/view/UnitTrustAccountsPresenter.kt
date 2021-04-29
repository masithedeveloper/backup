/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.unitTrusts.ui.view

import com.barclays.absa.banking.framework.AbstractPresenter
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.funeralCover.services.FuneralCoverQuoteInteractor
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustAccountsWrapper
import java.lang.ref.WeakReference

class UnitTrustAccountsPresenter internal constructor(viewWeakReference: WeakReference<out UnitTrustAccountView>) : AbstractPresenter(viewWeakReference) {
    private val unitTrustAccountsExtendedResponseListener: UnitTrustAccountsExtendedResponseListener = UnitTrustAccountsExtendedResponseListener(this)
    private val funeralCoverQuoteService = FuneralCoverQuoteInteractor()

    fun onUnitTrustAccountsReceived(successResponse: UnitTrustAccountsWrapper?) {
        val unitTrustAccountView = viewWeakReference.get() as UnitTrustAccountView?
        successResponse?.let {
            if (BMBConstants.SUCCESS.equals(it.transactionStatus, ignoreCase = true)) {
                unitTrustAccountView?.displayUnitTrustAccount(it)
            } else {
                onFailedToReceiveUnitTrustAccounts()
            }
        }
        dismissProgressIndicator()
    }

    fun onUnitTrustAccountsHubCreated() {
        showProgressIndicator()
        funeralCoverQuoteService.fetchUnitTrustAccounts(unitTrustAccountsExtendedResponseListener)
    }

    fun onFailedToReceiveUnitTrustAccounts() {
        val unitTrustAccountView = viewWeakReference.get() as UnitTrustAccountView?
        unitTrustAccountView?.displaySomethingWentWrongScreen()
    }
}