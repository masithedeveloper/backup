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
import com.barclays.absa.banking.payments.international.services.IInternationalPaymentCacheService
import com.barclays.absa.banking.payments.international.services.InternationalPaymentsService.Companion.OP0884_VALIDATE_PAYMENT
import com.barclays.absa.banking.payments.international.services.WesternUnionParameters

class WesternUnionValidatePaymentRequest<T>(extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {

    private val internationalPaymentCacheService: IInternationalPaymentCacheService = getServiceInterface()
    private var validatePaymentDetails = internationalPaymentCacheService.getValidatePaymentDetails()
    var quoteDetails = internationalPaymentCacheService.getQuoteDetails()
    var westernUnionBeneficiaryDetails = internationalPaymentCacheService.getBeneficiaryDetails()
    val beneficiaryEnteredDetails = internationalPaymentCacheService.getEnteredBeneficiaryDetails()

    init {
        params = RequestParams.Builder()
                .put(OP0884_VALIDATE_PAYMENT)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_TRANSFER_TYPE, WesternUnionParameters.WESTERN_UNION)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_ENTITY_TYPE, WesternUnionParameters.INDIVIDUAL)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_TRANSACTION_REFERENCE_NUMBER, quoteDetails.transactionReferenceNumber)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_FOREIGN_CURRENCY, quoteDetails.foreignCurrency)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_FOREIGN_AMOUNT, quoteDetails.foreignAmount)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_USD_CONVERSION_RATE, quoteDetails.usdConversionRate)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_EXPECTED_PAYOUT_CURRENCY, quoteDetails.expectedPayoutCurrency)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_EXPECTED_PAYOUT_AMOUNT, quoteDetails.expectedPayoutAmount)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_DESTINATION_CURRENCY_RATE, quoteDetails.destinationCurrencyRate)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_LOCAL_CURRENCY, quoteDetails.localCurrency)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_LOCAL_AMOUNT, quoteDetails.localAmount)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_COMMISSION_AMOUNT_FEE, quoteDetails.commissionAmountFee)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_VAT_AMOUNT, quoteDetails.vatAmount)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_TOTAL_DUE, quoteDetails.totalDue)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_FROM_ACCOUNT, validatePaymentDetails.fromAccountNumber)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_SENDER_FIRST_NAME, quoteDetails.senderFirstName)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_SENDER_SURNAME, quoteDetails.senderSurname)
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_SHORT_NAME, westernUnionBeneficiaryDetails.beneficiaryShortName)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_RECEIVER_FIRST_NAME, quoteDetails.receiverFirstName)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_RECEIVER_SURNAME, quoteDetails.receiverSurname)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_MTCN_CODE, quoteDetails.moneyTransferControlNumberCode)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_DESTINATION_COUNTRY, validatePaymentDetails.destinationCountryCode)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_DESTINATION_STATE, validatePaymentDetails.destinationState)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_DESTINATION_CITY, validatePaymentDetails.destinationCity)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_ORIGINATING_COUNTRY, "ZA")
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_RESIDING_COUNTRY, validatePaymentDetails.destinationCountryCode)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_RESIDING_COUNTRY_DESCRIPTION, validatePaymentDetails.destinationCountryDescription)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_RESIDING_STATE, validatePaymentDetails.state)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_RESIDING_CITY, validatePaymentDetails.city)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_STREET_ADDRESS, validatePaymentDetails.streetAddress)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_DOCUMENT_SOURCE_IND, WesternUnionParameters.EMAIL)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_DOCUMENT_BEND_ID, westernUnionBeneficiaryDetails.id)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_IFT_TYPE, WesternUnionParameters.INDIVIDUAL)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_FIRST_NAME, validatePaymentDetails.beneficiaryFirstName)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_SURNAME, validatePaymentDetails.beneficiarySurName)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_ACCOUNT_NUMBER, validatePaymentDetails.fromAccountNumber)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_ACCOUNT_TYPE, westernUnionBeneficiaryDetails.accountType)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_REFERENCE, westernUnionBeneficiaryDetails.beneficiaryReference)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_STATUS, westernUnionBeneficiaryDetails.status)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_EFT_NUMBER, westernUnionBeneficiaryDetails.eftNumber)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_TIEB_NUMBER, westernUnionBeneficiaryDetails.tiebNumber)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_CIF_KEY, westernUnionBeneficiaryDetails.cifkey)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_COUNTRY_CODE, westernUnionBeneficiaryDetails.westernUnionDetails?.residingCountry?.countryCode.toString())
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_COUNTRY_DESCRIPTION, westernUnionBeneficiaryDetails.westernUnionDetails?.residingCountry?.countryDescription.toString())
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_STATE_NAME, westernUnionBeneficiaryDetails.westernUnionDetails?.residingCountry?.stateName.toString())
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_CITY_NAME, westernUnionBeneficiaryDetails.westernUnionDetails?.residingCountry?.cityName.toString())
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_ISO_COUNTRY_CODE, westernUnionBeneficiaryDetails.westernUnionDetails?.residingCountry?.isoCountryCode.toString())
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_BENEFICIARY_GENDER, validatePaymentDetails.gender)
                .put(WesternUnionParameters.SERVICE_BENEFICIARY_PAYMENT_TYPE, WesternUnionParameters.NORMAL)
                .put(WesternUnionParameters.SERVICE_VALIDATE_PAYMENT_FOREIGN_CURRENCY_WITH_ZAR, validatePaymentDetails.foreignCurrencyWithZar)
                .put(WesternUnionParameters.SERVICE_TEST_QUESTION, beneficiaryEnteredDetails.paymentQuestion)
                .put(WesternUnionParameters.SERVICE_TEST_ANSWER, beneficiaryEnteredDetails.paymentAnswer)
                .build()
        mockResponseFile = "international_payments/op0884_validate_payment.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T>? = WesternUnionValidatePaymentResponse::class.java as Class<T>
    override fun isEncrypted(): Boolean? = true
}