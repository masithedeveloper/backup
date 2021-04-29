/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.card.ui.creditCard.hub.manageMobilePayments

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.ScanToPayInteractor
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.response.ScanToPayEnquiryResponseListener
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.response.ScanToPayRegistrationListener
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.response.ScanToPayRegistrationResponseListener
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.response.dto.ScanToPayCardNumberEnquiryResponse
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.response.dto.ScanToPayRegistrationResponse

class ManageMobilePaymentsViewModel : BaseViewModel(), ScanToPayRegistrationListener {

    companion object {
        const val FEATURE_NAME = "ManageMobilePayments"
    }
    lateinit var cardNumber: String

    private val scanToPayService by lazy { ScanToPayInteractor() }
    private val scanToPayEnquiryResponseListener by lazy { ScanToPayEnquiryResponseListener(this) }
    private val scanToPayRegistrationResponseListener by lazy { ScanToPayRegistrationResponseListener(this) }

    var scanToPayCardNumberEnquiryResponse: MutableLiveData<ScanToPayCardNumberEnquiryResponse> = MutableLiveData()
    var scanToPayRegistrationResponse: MutableLiveData<ScanToPayRegistrationResponse> = MutableLiveData()

    fun fetchScanToPayCardEnabledState(cardNumber: String) = scanToPayService.scanToPayCardNumberEnquiry(cardNumber, scanToPayEnquiryResponseListener)
    fun registerScanToPayCard(cardNumber: String, register: Boolean) = scanToPayService.scanToPayRegistration(cardNumber, register, scanToPayRegistrationResponseListener)

    override fun setScanToPayRegistrationResponse(scanToPayRegistrationResponse: ScanToPayRegistrationResponse) {
        this.scanToPayRegistrationResponse.value = scanToPayRegistrationResponse
    }

    override fun setScanToPayRegistrationFailureResponse(failureResponse: ResponseObject?) {
        failureResponse?.let {
            notifyFailure(it)
        }
    }
}