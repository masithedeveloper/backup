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
package com.barclays.absa.banking.personalLoan.services

import com.barclays.absa.banking.framework.AbstractInteractor
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.personalLoan.ui.PersonalLoanHubInformation
import com.barclays.absa.banking.riskBasedApproach.services.dto.FicaStatus
import com.barclays.absa.banking.riskBasedApproach.services.dto.FicaStatusRequest

interface PersonalLoansService {
    companion object {
        const val OP2133_GET_PERSONAL_LOAN_INFORMATION = "OP2133"
        const val OP2118_GET_CREDIT_LIMITS_SERVICE = "OP2118"
    }

    fun fetchFicaStatus(ficaStatusExtendedResponseListener: ExtendedResponseListener<FicaStatus>)
    fun fetchPersonalLoanInformation(accountNumber: String, ficaStatusExtendedResponseListener: ExtendedResponseListener<PersonalLoanHubInformation>)
}

class PersonalLoansInteractor : AbstractInteractor(), PersonalLoansService {

    override fun fetchFicaStatus(ficaStatusExtendedResponseListener: ExtendedResponseListener<FicaStatus>) {
        submitRequest(FicaStatusRequest(ficaStatusExtendedResponseListener))
    }

    override fun fetchPersonalLoanInformation(accountNumber: String, ficaStatusExtendedResponseListener: ExtendedResponseListener<PersonalLoanHubInformation>) {
        submitRequest(PersonalLoanInformationRequest(accountNumber, ficaStatusExtendedResponseListener))
    }
}