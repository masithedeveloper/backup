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
package com.barclays.absa.banking.beneficiaries.ui

import com.barclays.absa.banking.beneficiaries.services.IBeneficiaryCacheService
import com.barclays.absa.banking.boundary.model.BeneficiaryListObject
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.ResponseObject
import java.util.*

class BeneficiaryPresenter(beneficiaryListView: BeneficiaryListView) {

    private val beneficiaryCacheService: IBeneficiaryCacheService = getServiceInterface()

    private val paymentBeneficiaryListResponseListener = object : ExtendedResponseListener<BeneficiaryListObject>() {
        override fun onSuccess(successResponse: BeneficiaryListObject) {
            beneficiaryCacheService.setPaymentsBeneficiaries(successResponse.paymentBeneficiaryList)
            beneficiaryListView.fetchBeneficiaryListView(successResponse)
        }

        override fun onFailure(failureResponse: ResponseObject) {
            if ("FTR00565".equals(failureResponse.responseCode, ignoreCase = true)) {
                beneficiaryCacheService.setPaymentsBeneficiaries(ArrayList())
            }
        }
    }

    init {
        paymentBeneficiaryListResponseListener.setView(beneficiaryListView)
    }
}
