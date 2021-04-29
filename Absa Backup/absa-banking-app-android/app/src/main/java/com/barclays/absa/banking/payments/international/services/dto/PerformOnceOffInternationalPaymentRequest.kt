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
package com.barclays.absa.banking.payments.international.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.payments.international.data.BeneficiaryEnteredDetails
import com.barclays.absa.banking.payments.international.services.IInternationalPaymentCacheService
import com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.Companion.OP0897_PERFORM_ONCE_OFF_INTERNATIONAL_PAYMENT
import com.barclays.absa.banking.payments.international.services.WesternUnionParameters

class PerformOnceOffInternationalPaymentRequest<T>(paymentDetails: BeneficiaryEnteredDetails?, responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {

    private val internationalPaymentCacheService: IInternationalPaymentCacheService = getServiceInterface()
    private val quoteDetailsResponse = internationalPaymentCacheService.getQuoteDetails()
    private val userDetails = internationalPaymentCacheService.getEnteredBeneficiaryDetails()

    init {
        params = RequestParams.Builder()
                .put(OP0897_PERFORM_ONCE_OFF_INTERNATIONAL_PAYMENT)
                .put(WesternUnionParameters.SERVICE_TRANSFER_TYPE, WesternUnionParameters.WESTERN_UNION)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_ENTITY_TYPE, WesternUnionParameters.INDIVIDUAL)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_TRANSACTION_REFERENCE_NUMBER, quoteDetailsResponse.transactionReferenceNumber)
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_SHORT_NAME, quoteDetailsResponse.receiverFirstName + quoteDetailsResponse.receiverSurname)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_FOREIGN_CURRENCY_WITH_ZAR, quoteDetailsResponse.foreignCurrency)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_FOREIGN_AMOUNT, quoteDetailsResponse.foreignAmount)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_USD_CONVERSION_RATE, quoteDetailsResponse.usdConversionRate)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_EXPECTED_PAYOUT_CURRENCY, quoteDetailsResponse.expectedPayoutCurrency)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_EXPECTED_PAYOUT_AMOUNT, quoteDetailsResponse.expectedPayoutAmount)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_DESTINATION_CURRENCY_RATE, quoteDetailsResponse.destinationCurrencyRate)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_RESIDING_COUNTRY_DESCRIPTION, "ZA")
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_LOCAL_AMOUNT, quoteDetailsResponse.localAmount)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_COMMISSION_AMOUNT_FEE, quoteDetailsResponse.commissionAmountFee)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_VAT_AMOUNT, quoteDetailsResponse.vatAmount)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_TOTAL_DUE, quoteDetailsResponse.totalDue)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_FROM_ACCOUNT, paymentDetails?.fromAccountNumber)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_SENDER_FIRST_NAME, quoteDetailsResponse.senderFirstName)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_SENDER_SURNAME, quoteDetailsResponse.senderSurname)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_RECEIVER_FIRST_NAME, quoteDetailsResponse.receiverFirstName)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_RECEIVER_SURNAME, quoteDetailsResponse.receiverSurname)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_SURNAME, paymentDetails?.beneficiarySurname)
                .put(WesternUnionParameters.SERVICE_VALUE_DATE, "")
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_MTCN_CODE, quoteDetailsResponse.moneyTransferControlNumberCode)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_DESTINATION_COUNTRY, quoteDetailsResponse.destinationCountry)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_DESTINATION_STATE, paymentDetails?.paymentState)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_DESTINATION_CITY, quoteDetailsResponse.destinationCity)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_ORIGINATING_COUNTRY, "ZA")
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_LOCAL_CURRENCY, paymentDetails?.sendCurrency)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_RESIDING_COUNTRY, quoteDetailsResponse.originatingCountry)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_RESIDING_STATE, paymentDetails?.paymentState)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_STREET_ADDRESS, paymentDetails?.paymentAddress)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_DOCUMENT_SOURCE_IND, WesternUnionParameters.EMAIL)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_DOCUMENT_BEND_ID, "")
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_IFT_TYPE, WesternUnionParameters.INDIVIDUAL)
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_SHORT_NAME, quoteDetailsResponse.receiverFirstName + quoteDetailsResponse.receiverSurname)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_FIRST_NAME, quoteDetailsResponse.receiverFirstName)
                .put(WesternUnionParameters.BENEFICIARY_SURNAME, paymentDetails?.beneficiarySurname)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_ACCOUNT_NUMBER, paymentDetails?.fromAccountNumber)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_REFERENCE, "")
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_STATUS, WesternUnionParameters.CURRENT)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_EFT_NUMBER, "")
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_TIEB_NUMBER, "")
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_CIF_KEY, "")
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_COUNTRY_CODE, paymentDetails?.paymentDestinationCountryCode)
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_COUNTRY_DESCRIPTION, paymentDetails?.paymentDestinationCountryCode)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_STATE_NAME, paymentDetails?.paymentState)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_CITY_NAME, paymentDetails?.paymentCity)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_ISO_COUNTRY_CODE, "")
                .put(WesternUnionParameters.SERVICE_COUNTRY_CODE, paymentDetails?.paymentDestinationCountryCode)
                .put(WesternUnionParameters.BENEFICIARY_COUNTRY, paymentDetails?.paymentDestinationCountryCode)
                .put(WesternUnionParameters.DISPLAY_QUESTIONS_AND_ANSWERS, if (paymentDetails?.paymentQuestion.isNullOrBlank()) "" else paymentDetails?.paymentQuestion + "," + paymentDetails?.paymentAnswer)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_ACCOUNT_TYPE, WesternUnionParameters.NOT_AVAILABLE)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_GENDER, paymentDetails?.beneficiaryGender?.substring(0, 1))
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_PAYMENT_TYPE, WesternUnionParameters.ONCE_OFF_PAYMENT)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_FOREIGN_CURRENCY_WITH_ZAR, "true")
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_RESIDING_COUNTRY, "ZA")
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_RESIDENTIAL_STATUS, paymentDetails?.beneficiaryCitizenship)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_RESIDING_CITY, paymentDetails?.paymentCity)
                .put(WesternUnionParameters.SERVICE_TEST_QUESTION, userDetails.paymentQuestion)
                .put(WesternUnionParameters.SERVICE_TEST_ANSWER, userDetails.paymentAnswer)
                .build()

        mockResponseFile = "international_payments/op0897_international_payments_once_off_result.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T>? = PerformOnceOffInternationalPaymentResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}