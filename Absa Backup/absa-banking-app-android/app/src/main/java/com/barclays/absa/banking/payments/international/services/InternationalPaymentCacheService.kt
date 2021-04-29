/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */
package com.barclays.absa.banking.payments.international.services

import com.barclays.absa.banking.payments.international.data.BeneficiaryEnteredDetails
import com.barclays.absa.banking.payments.international.services.dto.*

class InternationalPaymentCacheService : IInternationalPaymentCacheService {

    private var CACHE = HashMap<String, Any>()

    companion object {
        private const val QUOTE_DETAILS = "quoteDetails"
        private const val BENEFICIARY_DETAILS = "beneficiaryDetails"
        private const val VALIDATE_PAYMENT_DETAILS = "validatePaymentDetails"
        private const val ENTERED_BENEFICIARY_DETAILS = "beneficiaryEnteredDetails"
        private const val CLIENT_TYPE_RESPONSE = "ClientTypeResponse"
        private const val QUOTATION_DETAILS_RESPONSE = "quotationDetailsResponse"
    }

    override fun getQuoteDetails(): QuoteDetails = CACHE[QUOTE_DETAILS] as QuoteDetails
    override fun setQuoteDetails(validatePaymentDetails: QuoteDetails) {
        CACHE[QUOTE_DETAILS] = validatePaymentDetails
    }

    override fun getBeneficiaryDetails(): WesternUnionBeneficiaryDetails = CACHE[BENEFICIARY_DETAILS] as WesternUnionBeneficiaryDetails
    override fun setBeneficiaryDetails(westernUnionBeneficiaryDetails: WesternUnionBeneficiaryDetails) {
        CACHE[BENEFICIARY_DETAILS] = westernUnionBeneficiaryDetails
    }

    override fun getValidatePaymentDetails(): ValidatePaymentDetails = CACHE[VALIDATE_PAYMENT_DETAILS] as ValidatePaymentDetails
    override fun setValidatePaymentDetails(validatePaymentDetails: ValidatePaymentDetails) {
        CACHE[VALIDATE_PAYMENT_DETAILS] = validatePaymentDetails
    }

    override fun getEnteredBeneficiaryDetails(): BeneficiaryEnteredDetails = (CACHE[ENTERED_BENEFICIARY_DETAILS] as? BeneficiaryEnteredDetails) ?: BeneficiaryEnteredDetails()
    override fun setEnteredBeneficiaryDetails(beneficiaryEnteredDetails: BeneficiaryEnteredDetails) {
        CACHE[ENTERED_BENEFICIARY_DETAILS] = beneficiaryEnteredDetails
    }

    override fun getClientTypeResponse(): ClientTypeResponse? = CACHE[CLIENT_TYPE_RESPONSE] as? ClientTypeResponse
    override fun setClientTypeResponse(clientTypeResponse: ClientTypeResponse) {
        CACHE[CLIENT_TYPE_RESPONSE] = clientTypeResponse
    }

    override fun getQuotation(): QuoteDetailsResponse = CACHE[QUOTATION_DETAILS_RESPONSE] as QuoteDetailsResponse
    override fun setQuotation(quoteDetailsResponse: QuoteDetailsResponse) {
        CACHE[QUOTATION_DETAILS_RESPONSE] = quoteDetailsResponse
    }

    override fun clear() {
        CACHE = HashMap()
    }
}