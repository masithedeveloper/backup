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
package com.barclays.absa.banking.payments.international.services

import com.barclays.absa.banking.framework.AbstractInteractor
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.payments.international.data.BeneficiaryEnteredDetails
import com.barclays.absa.banking.payments.international.data.InternationalPaymentBeneficiaryDetails
import com.barclays.absa.banking.payments.international.services.dto.*

class InternationalPaymentsInteractor : AbstractInteractor(), InternationalPaymentsService {
    override fun getAllWesternUnionCountriesList(westernUnionCountriesListResponseListener: ExtendedResponseListener<WesternUnionCountriesListResponse?>) {
        val westernUnionCountriesListRequest = WesternUnionCountriesListRequest(westernUnionCountriesListResponseListener)
        submitRequest(westernUnionCountriesListRequest)
    }

    override fun getAllWesternUnionStatesList(countryCode: String, westernUnionStatesListResponseListener: ExtendedResponseListener<WesternUnionCountriesListResponse?>) {
        val westernUnionStatesListRequest = WesternUnionStatesListRequest(countryCode, westernUnionStatesListResponseListener)
        submitRequest(westernUnionStatesListRequest)
    }

    override fun getAllWesternUnionCitiesList(countryCode: String, stateName: String, westernUnionCitiesListResponseListener: ExtendedResponseListener<WesternUnionCountriesListResponse?>) {
        val westernUnionCitiesListRequest = WesternUnionCitiesListRequest(countryCode, stateName, westernUnionCitiesListResponseListener)
        submitRequest(westernUnionCitiesListRequest)
    }

    override fun getWesternUnionBeneficiariesList(responseListener: ExtendedResponseListener<WesternUnionBeneficiaryListObject?>) {
        val westernUnionBeneficiaryListRequest = WesternUnionBeneficiaryListRequest(responseListener)
        submitRequest(westernUnionBeneficiaryListRequest)
    }

    override fun fetchWesternUnionCurrencies(countryCode: String, responseListener: ExtendedResponseListener<WesternUnionCurrenciesResponse?>) {
        val westernUnionCurrenciesRequest = WesternUnionCurrenciesRequest(countryCode, responseListener)
        submitRequest(westernUnionCurrenciesRequest)
    }

    override fun validateNewWesternUnionBeneficiary(beneficiaryEnteredDetails: BeneficiaryEnteredDetails, responseListener: ExtendedResponseListener<ValidateNewWesternUnionBeneficiaryResponse>) {
        val validateNewWesternUnionBeneficiaryRequest = ValidateNewWesternUnionBeneficiaryRequest(beneficiaryEnteredDetails, responseListener)
        submitRequest(validateNewWesternUnionBeneficiaryRequest)
    }

    override fun validateForHolidaysAndTime(validateForHolidaysAndTimeRequestExtendedResponseListener: ExtendedResponseListener<ValidateForHolidaysAndTimeResponse?>) {
        val validateForHolidaysAndTimeRequest = ValidateForHolidaysAndTimeRequest(validateForHolidaysAndTimeRequestExtendedResponseListener)
        //leaving this here as an example of forceStubMode usage
        //validateForHolidaysAndTimeRequest.mockResponseFile = "international_payments/op0885_validate_holidays_and_time_failure.json"
        //validateForHolidaysAndTimeRequest.setForcedStubMode(true)
        submitRequest(validateForHolidaysAndTimeRequest)
    }

    override fun getOutBoundPendingTransaction(beneficiaryId: String, getOutBoundPendingTransactionResponseExtendedResponseListener: ExtendedResponseListener<OutBoundPendingTransactionResponse?>) {
        val getOutBoundPendingTransactionRequest = OutBoundPendingTransactionRequest(getOutBoundPendingTransactionResponseExtendedResponseListener, beneficiaryId)
        submitRequest(getOutBoundPendingTransactionRequest)
    }

    override fun addNewWesternUnionBeneficiary(transactionReferenceId: String, responseListener: ExtendedResponseListener<AddNewWesternUnionBeneficiaryResponse?>) {
        val addNewWesternUnionBeneficiaryRequest = AddNewWesternUnionBeneficiaryRequest(transactionReferenceId, responseListener)
        submitRequest(addNewWesternUnionBeneficiaryRequest)
    }

    override fun validatePayment(responseListener: ExtendedResponseListener<WesternUnionValidatePaymentResponse?>) {
        val validatePaymentRequest = WesternUnionValidatePaymentRequest(responseListener)
        submitRequest(validatePaymentRequest)
    }

    override fun getQuoteDetails(validatePaymentDetails: ValidatePaymentDetails, responseListener: ExtendedResponseListener<QuoteDetailsResponse?>) {
        val getQuoteDetailsRequest = InternationalPaymentsQuoteDetailsRequest(validatePaymentDetails, responseListener)
        submitRequest(getQuoteDetailsRequest)
    }

    override fun fetchBeneficiaryDetails(beneficiaryDetails: InternationalPaymentBeneficiaryDetails, beneficiaryDetailsResponseListener: ExtendedResponseListener<WesternUnionBeneficiaryDetails?>) {
        val getBeneficiaryDetailsRequest = WesternUnionBeneficiaryDetailsRequest(beneficiaryDetails, beneficiaryDetailsResponseListener)
        submitRequest(getBeneficiaryDetailsRequest)
    }

    override fun fetchClientType(clientTypeResponseListener: ExtendedResponseListener<ClientTypeResponse>) {
        val clientTypeRequest = ClientTypeRequest(clientTypeResponseListener)
        submitRequest(clientTypeRequest)
    }

    override fun performInternationalPayment(transactionReferenceId: String, paymentResultResponseListener: ExtendedResponseListener<InternationalPaymentsResultResponse?>) {
        val resultRequest = InternationalPaymentsResultRequest(transactionReferenceId, paymentResultResponseListener)
        submitRequest(resultRequest)
    }

    override fun performOnceOffInternationalPayment(paymentDetails: BeneficiaryEnteredDetails?, quoteDetails: QuoteDetails, responseListener: ExtendedResponseListener<PerformOnceOffInternationalPaymentResponse?>) {
        val performOnceOffInternationalPayment = PerformOnceOffInternationalPaymentRequest(paymentDetails, responseListener)
        submitRequest(performOnceOffInternationalPayment)
    }
}