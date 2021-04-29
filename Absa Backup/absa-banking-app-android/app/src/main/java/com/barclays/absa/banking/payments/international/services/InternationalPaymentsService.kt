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

import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.payments.international.data.BeneficiaryEnteredDetails
import com.barclays.absa.banking.payments.international.data.InternationalPaymentBeneficiaryDetails
import com.barclays.absa.banking.payments.international.services.dto.*

interface InternationalPaymentsService {
    companion object {
        const val OP0852_GET_CLIENT_TYPE = "OP0852"
        const val OP0867_ADD_NEW_WESTERN_UNION_BENEFICIARY = "OP0867"
        const val OP0868_PERFORM_INTERNATIONAL_PAYMENT = "OP0868"
        const val OP0872_GET_QUOTE_DETAILS = "OP0872"
        const val OP0878_VALIDATE_NEW_WESTERN_UNION_BENEFICIARY = "OP0878"
        const val OP0881_GET_WESTERN_UNION_CURRENCIES = "OP0881"
        const val OP0882_GET_WESTERN_UNION_BENEFICIARIES = "OP0882"
        const val OP0883_GET_ALL_WESTERN_UNION_COUNTRIES = "OP0883"
        const val OP0884_VALIDATE_PAYMENT = "OP0884"
        const val OP0885_VALIDATE_FOR_HOLIDAYS_AND_TIME = "OP0885"
        const val OP0886_GET_OUT_BOUNDING_PENDING_TRANSACTION = "OP0886"
        const val OP0889_GET_WESTERN_UNION_BENEFICIARY_DETAILS = "OP0889"
        const val OP0897_PERFORM_ONCE_OFF_INTERNATIONAL_PAYMENT = "OP0897"
    }

    fun getAllWesternUnionCountriesList(westernUnionCountriesListResponseListener: ExtendedResponseListener<WesternUnionCountriesListResponse?>)
    fun getAllWesternUnionStatesList(countryCode: String, westernUnionStatesListResponseListener: ExtendedResponseListener<WesternUnionCountriesListResponse?>)
    fun getAllWesternUnionCitiesList(countryCode: String, stateName: String, westernUnionCitiesListResponseListener: ExtendedResponseListener<WesternUnionCountriesListResponse?>)
    fun getWesternUnionBeneficiariesList(responseListener: ExtendedResponseListener<WesternUnionBeneficiaryListObject?>)
    fun fetchWesternUnionCurrencies(countryCode: String, responseListener: ExtendedResponseListener<WesternUnionCurrenciesResponse?>)
    fun validateForHolidaysAndTime(validateForHolidaysAndTimeRequestExtendedResponseListener: ExtendedResponseListener<ValidateForHolidaysAndTimeResponse?>)
    fun validateNewWesternUnionBeneficiary(beneficiaryEnteredDetails: BeneficiaryEnteredDetails, responseListener: ExtendedResponseListener<ValidateNewWesternUnionBeneficiaryResponse>)
    fun addNewWesternUnionBeneficiary(transactionReferenceId: String, responseListener: ExtendedResponseListener<AddNewWesternUnionBeneficiaryResponse?>)
    fun validatePayment(responseListener: ExtendedResponseListener<WesternUnionValidatePaymentResponse?>)
    fun getOutBoundPendingTransaction(beneficiaryId: String, getOutBoundPendingTransactionResponseExtendedResponseListener: ExtendedResponseListener<OutBoundPendingTransactionResponse?>)
    fun getQuoteDetails(validatePaymentDetails: ValidatePaymentDetails, responseListener: ExtendedResponseListener<QuoteDetailsResponse?>)
    fun fetchBeneficiaryDetails(beneficiaryDetails: InternationalPaymentBeneficiaryDetails, beneficiaryDetailsResponseListener: ExtendedResponseListener<WesternUnionBeneficiaryDetails?>)
    fun fetchClientType(clientTypeResponseListener: ExtendedResponseListener<ClientTypeResponse>)
    fun performInternationalPayment(transactionReferenceId: String, paymentResultResponseListener: ExtendedResponseListener<InternationalPaymentsResultResponse?>)
    fun performOnceOffInternationalPayment(paymentDetails: BeneficiaryEnteredDetails?, quoteDetails: QuoteDetails, responseListener: ExtendedResponseListener<PerformOnceOffInternationalPaymentResponse?>)
}