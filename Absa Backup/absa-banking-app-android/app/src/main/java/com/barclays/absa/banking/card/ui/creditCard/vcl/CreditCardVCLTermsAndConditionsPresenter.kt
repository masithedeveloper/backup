/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.card.ui.creditCard.vcl

import com.barclays.absa.banking.card.services.card.dto.CreditCardInteractor
import com.barclays.absa.banking.card.services.card.dto.CreditCardService
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardBureauData
import com.barclays.absa.banking.card.services.card.dto.creditCard.CreditCardOverdraft
import com.barclays.absa.banking.card.services.card.dto.creditCard.VCLParcelableModel
import com.barclays.absa.banking.framework.AbstractPresenter
import java.lang.ref.WeakReference

class CreditCardVCLTermsAndConditionsPresenter(viewWeakReference: WeakReference<CreditCardVCLTermsAndConditionsView>) : AbstractPresenter(viewWeakReference) {
    private val creditCardService: CreditCardService = CreditCardInteractor()
    private val view = viewWeakReference.get() as CreditCardVCLTermsAndConditionsView
    private val fetchBureauDataExtendedRequest: FetchBureauDataExtendedRequest = FetchBureauDataExtendedRequest(this)
    private val vclGadgetExtendedResponseListener: VclGadgetExtendedResponseListener = VclGadgetExtendedResponseListener(this)

    fun onContinueButtonClick(bureauData: VCLParcelableModel?) {
        if ("false".equals(bureauData?.isFreshBureauDataAvailable, ignoreCase = true)) {
            showProgressIndicator()
            creditCardService.fetchClientBureauData(bureauData, fetchBureauDataExtendedRequest)
        } else {
            view.navigateToIncomeAndExpenseScreen()
        }
    }

    fun onBureauDataResponseSuccess(bureauData: CreditCardBureauData?) {
        view.navigateToIncomeAndExpenseScreen(bureauData)
        dismissProgressIndicator()
    }

    fun onGadgetSuccessResponse(successResponse: CreditCardOverdraft?) {
        view.updateCreditCardGadgetData(successResponse)
        dismissProgressIndicator()
    }

    fun onViewLoad(vclDataModel: VCLParcelableModel?) {
        if (vclDataModel?.creditCardVCLGadget == null) {
            showProgressIndicator()
            creditCardService.overdraftOfferRequest(vclGadgetExtendedResponseListener)
        }
    }
}
