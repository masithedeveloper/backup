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

package com.barclays.absa.banking.personalLoan.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.home.services.HomeScreenInteractor
import com.barclays.absa.banking.home.services.clickToCall.dto.CallBackResponse
import com.barclays.absa.banking.personalLoan.services.CallBackExtendedResponseListener
import com.barclays.absa.banking.personalLoan.services.PersonalLoanFicaCheckExtendedResponseListener
import com.barclays.absa.banking.personalLoan.services.PersonalLoanHubExtendedResponseListener
import com.barclays.absa.banking.personalLoan.services.PersonalLoansInteractor
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.barclays.absa.banking.riskBasedApproach.services.dto.FicaStatus

class PersonalLoanVCLViewModel : BaseViewModel() {

    var personalLoanInteractor = PersonalLoansInteractor()
    var homeScreenInteractor = HomeScreenInteractor()

    private val callBackExtendedResponseListener: ExtendedResponseListener<CallBackResponse> by lazy { CallBackExtendedResponseListener(this) }
    private val ficaCheckExtendedResponseListener: ExtendedResponseListener<FicaStatus> by lazy { PersonalLoanFicaCheckExtendedResponseListener(this) }
    private val personalLoanHubExtendedResponseListener: ExtendedResponseListener<PersonalLoanHubInformation> by lazy { PersonalLoanHubExtendedResponseListener(this) }

    var callBackExtendedResponse = MutableLiveData<CallBackResponse>()
    val ficaCheckResponseExtendedResponse = MutableLiveData<FicaStatus>()
    var personalLoanHubExtendedResponse = MutableLiveData<PersonalLoanHubInformation>()

    fun requestCallBack(secretCode: String, callBackDateTime: String) {
        homeScreenInteractor.requestCallBack(secretCode, callBackDateTime, callBackExtendedResponseListener)
    }

    fun fetchFicaStatus() {
        personalLoanInteractor.fetchFicaStatus(ficaCheckExtendedResponseListener)
    }

    fun fetchPersonalLoanInformation(accountNumber: String) {
        personalLoanInteractor.fetchPersonalLoanInformation(accountNumber, personalLoanHubExtendedResponseListener)
    }
}