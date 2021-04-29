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

package com.barclays.absa.banking.funeralCover.ui

import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.barclays.absa.banking.framework.AbstractPresenter
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.funeralCover.services.InsurancePolicyInteractor
import com.barclays.absa.banking.funeralCover.ui.responseListeners.ChangePolicyPaymentDetailsResponseListener
import java.lang.ref.WeakReference

class ChangePolicyPaymentDetailsPresenter(viewWeakReference: WeakReference<ChangePolicyPaymentDetailsView>) : AbstractPresenter(viewWeakReference) {
    private val insurancePolicyService = InsurancePolicyInteractor()
    private val changePolicyPaymentDetailsResponseListener = ChangePolicyPaymentDetailsResponseListener(this)

    fun onSubmitButtonClicked(policyType: String, changePaymentDetails: ChangePaymentDetails) {
        showProgressIndicator()
        if (BMBConstants.EXERGY_POLICY_TYPE.equals(policyType, ignoreCase = true)) {
            insurancePolicyService.changeExergyPolicyPaymentDetails(changePaymentDetails, changePolicyPaymentDetailsResponseListener)
        } else {
            insurancePolicyService.changePolicyPaymentDetails(changePaymentDetails, changePolicyPaymentDetailsResponseListener)
        }
    }

    fun onPolicyPaymentDetailsChanged(successResponse: SureCheckResponse) {
        val view = viewWeakReference.get() as ChangePolicyPaymentDetailsView?
        view?.let {
            it.startSureCheckVerification(successResponse)
        }
    }
}