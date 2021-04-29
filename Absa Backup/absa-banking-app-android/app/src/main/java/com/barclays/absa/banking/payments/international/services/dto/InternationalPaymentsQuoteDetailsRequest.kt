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
package com.barclays.absa.banking.payments.international.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.payments.international.services.IInternationalPaymentCacheService
import com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.Companion.OP0872_GET_QUOTE_DETAILS
import com.barclays.absa.banking.payments.international.services.WesternUnionParameters
import styleguide.utils.extensions.removeCommas

class InternationalPaymentsQuoteDetailsRequest<T>(validatePaymentDetails: ValidatePaymentDetails, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    private val internationalPaymentCacheService: IInternationalPaymentCacheService = getServiceInterface()
    private val userEnteredDetails = internationalPaymentCacheService.getEnteredBeneficiaryDetails()

    init {
        params = RequestParams.Builder()
                .put(OP0872_GET_QUOTE_DETAILS)
                .put(WesternUnionParameters.SERVICE_BOP_CATEGORY_CODE, WesternUnionParameters.BOP_CATEGORY_CODE_VALUE)
                .put(WesternUnionParameters.SERVICE_BOP_SUB_CATEGORY, WesternUnionParameters.BOP_SUB_CATEGORY_VALUE)
                .put(WesternUnionParameters.SERVICE_RULING_CODE, WesternUnionParameters.RULING_CODE_VALUE)
                .put(WesternUnionParameters.SERVICE_FLOW, WesternUnionParameters.OUT)
                .put(WesternUnionParameters.SERVICE_TYPE_OF_PAYMENT, WesternUnionParameters.TYPE_OF_PAYMENT_VALUE)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_DESTINATION_COUNTRY, validatePaymentDetails.destinationCountryCode)
                .put(WesternUnionParameters.SERVICE_FROM_ACCOUNT_NUMBER, validatePaymentDetails.fromAccountNumber)
                .put(WesternUnionParameters.SERVICE_RECEIVING_BANK, WesternUnionParameters.RECEIVING_BANK_VALUE)
                .put(WesternUnionParameters.SERVICE_TRANSACTION_TYPE, WesternUnionParameters.WESTERN_UNION)
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_ACCOUNT_NUMBER, WesternUnionParameters.NOT_AVAILABLE)
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_NAME, validatePaymentDetails.beneficiaryFirstName)
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_SUR_NAME, validatePaymentDetails.beneficiarySurName)
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_GENDER, validatePaymentDetails.gender)
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_RESIDENTIAL_STATUS, validatePaymentDetails.nonResidentAccountIdentifier)
                .put(WesternUnionParameters.SERVICE_AMOUNT, validatePaymentDetails.amountToSend)
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_NUMBER, validatePaymentDetails.beneficiaryId)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_DESTINATION_CITY, validatePaymentDetails.destinationCity)
                .put(WesternUnionParameters.SERVICE_SEND_CURRENCY, validatePaymentDetails.sendCurrency)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_EXPECTED_PAYOUT_CURRENCY, validatePaymentDetails.payoutCurrency)
                .put(WesternUnionParameters.SERVICE_STREET_ADDRESS, validatePaymentDetails.streetAddress)
                .put(WesternUnionParameters.SERVICE_CITY, validatePaymentDetails.city)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_RESIDING_COUNTRY, validatePaymentDetails.residingCountry)
                .put(WesternUnionParameters.SERVICE_STATE, validatePaymentDetails.state)
                .put(WesternUnionParameters.SERVICE_DESTINATION_STATE, validatePaymentDetails.destinationState)
                .put(WesternUnionParameters.SERVICE_AVAILABLE_BALANCE, validatePaymentDetails.availableBalance.removeCommas())
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_FOREIGN_CURRENCY_WITH_ZAR, validatePaymentDetails.foreignCurrencyWithZar)
                .put(WesternUnionParameters.SERVICE_TEST_QUESTION, userEnteredDetails.paymentQuestion)
                .put(WesternUnionParameters.SERVICE_TEST_ANSWER, userEnteredDetails.paymentAnswer)
                .build()

        mockResponseFile = "international_payments/op0872_quote_details.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T>? = QuoteDetailsResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}
