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
package com.barclays.absa.banking.virtualpayments.scan2Pay.services

import com.barclays.absa.banking.framework.AbstractInteractor
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.request.ScanToPayCardListRequest
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.request.ScanToPayCardNumberEnquiryRequest
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.request.ScanToPayRegistrationRequest
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.request.ScanToPayTokenRequest
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.response.dto.ScanToPayCardListResponse
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.response.dto.ScanToPayCardNumberEnquiryResponse
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.response.dto.ScanToPayRegistrationResponse
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.response.dto.ScanToPayTokenResponse

class ScanToPayInteractor : AbstractInteractor(), ScanToPayService {

    override fun fetchScanToPayCardList(responseListener: ExtendedResponseListener<ScanToPayCardListResponse>) = submitRequest(ScanToPayCardListRequest(responseListener))

    override fun scanToPayCardNumberEnquiry(cardNumber: String, responseListener: ExtendedResponseListener<ScanToPayCardNumberEnquiryResponse>) = submitRequest(ScanToPayCardNumberEnquiryRequest(cardNumber, responseListener))

    override fun scanToPayRegistration(cardNumber: String, scanToPay: Boolean, responseListener: ExtendedResponseListener<ScanToPayRegistrationResponse>) = submitRequest(ScanToPayRegistrationRequest(cardNumber, scanToPay, responseListener))

    override fun fetchScanToPayToken(responseListener: ExtendedResponseListener<ScanToPayTokenResponse>) = submitRequest(ScanToPayTokenRequest(responseListener))
}