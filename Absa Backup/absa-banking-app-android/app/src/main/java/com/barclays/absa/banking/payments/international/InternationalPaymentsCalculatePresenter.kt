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

package com.barclays.absa.banking.payments.international

import com.barclays.absa.banking.framework.AbstractPresenter
import com.barclays.absa.banking.framework.app.BMBConstants.FAILURE
import com.barclays.absa.banking.framework.app.BMBConstants.SUCCESS
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.payments.international.data.BeneficiaryEnteredDetails
import com.barclays.absa.banking.payments.international.data.PaymentCalculations
import com.barclays.absa.banking.payments.international.data.TransferQuoteDetails
import com.barclays.absa.banking.payments.international.responseListeners.QuoteDetailsResponseExtendedResponseListener
import com.barclays.absa.banking.payments.international.services.IInternationalPaymentCacheService
import com.barclays.absa.banking.payments.international.services.InternationalPaymentsInteractor
import com.barclays.absa.banking.payments.international.services.dto.QuoteDetailsResponse
import com.barclays.absa.banking.payments.international.services.dto.ValidatePaymentDetails
import java.lang.ref.WeakReference

class InternationalPaymentsCalculatePresenter(view: InternationalPaymentsContract.InternationalPaymentsCalculateView) : AbstractPresenter(WeakReference(view)), InternationalPaymentsContract.InternationalPaymentsCalculatePresenter {

    private val internationalPaymentCacheService: IInternationalPaymentCacheService = getServiceInterface()
    private var internationalPaymentsInteractor: InternationalPaymentsInteractor = InternationalPaymentsInteractor()
    private val quoteDetailsResponseExtendedResponseListener: QuoteDetailsResponseExtendedResponseListener by lazy { QuoteDetailsResponseExtendedResponseListener(this) }
    private var beneficiaryEnteredDetails: BeneficiaryEnteredDetails? = null

    override fun calculationButtonClicked(paymentCalculations: PaymentCalculations, beneficiaryEnteredDetails: BeneficiaryEnteredDetails) {
        showProgressIndicator()
        this.beneficiaryEnteredDetails = beneficiaryEnteredDetails
        val validatePaymentDetails = ValidatePaymentDetails()
        validatePaymentDetails.accountType = beneficiaryEnteredDetails.fromAccountType
        validatePaymentDetails.payoutCurrency = beneficiaryEnteredDetails.currencyToPay
        validatePaymentDetails.availableBalance = beneficiaryEnteredDetails.fromAccountBalance
        validatePaymentDetails.beneficiaryId = beneficiaryEnteredDetails.beneficiaryId
        validatePaymentDetails.beneficiaryFirstName = beneficiaryEnteredDetails.beneficiaryNames
        validatePaymentDetails.beneficiarySurName = beneficiaryEnteredDetails.beneficiarySurname
        validatePaymentDetails.destinationCountryCode = beneficiaryEnteredDetails.paymentDestinationCountryCode
        validatePaymentDetails.streetAddress = beneficiaryEnteredDetails.paymentAddress
        validatePaymentDetails.city = beneficiaryEnteredDetails.paymentCity
        validatePaymentDetails.sendCurrency = beneficiaryEnteredDetails.sendCurrency
        validatePaymentDetails.foreignCurrencyWithZar = "true" //ALWAYS TRUE
        val beneficiaryGender = beneficiaryEnteredDetails.beneficiaryGender
        if (!beneficiaryGender.isNullOrEmpty()) {
            validatePaymentDetails.gender = if ('V' == beneficiaryGender[0]) "F" else beneficiaryGender[0].toString()
        }
        validatePaymentDetails.state = beneficiaryEnteredDetails.paymentState
        validatePaymentDetails.nonResidentAccountIdentifier = beneficiaryEnteredDetails.beneficiaryResidentialStatus()
        validatePaymentDetails.residingCountry = beneficiaryEnteredDetails.paymentDestinationCountryCode
        validatePaymentDetails.availableBalance = paymentCalculations.availableBalance
        validatePaymentDetails.fromAccountNumber = beneficiaryEnteredDetails.fromAccountNumber
        validatePaymentDetails.amountToSend = beneficiaryEnteredDetails.paymentAmount
        validatePaymentDetails.destinationState = beneficiaryEnteredDetails.paymentState
        validatePaymentDetails.destinationCity = beneficiaryEnteredDetails.paymentCity
        validatePaymentDetails.sendCurrency = beneficiaryEnteredDetails.sendCurrency
        validatePaymentDetails.payoutCurrency = beneficiaryEnteredDetails.sendCurrency
        validatePaymentDetails.payoutCurrency = paymentCalculations.currencyCode
        validatePaymentDetails.fromAccountNumber = paymentCalculations.accountNumber
        validatePaymentDetails.amountToSend = paymentCalculations.amountEntered
        validatePaymentDetails.sendCurrency = paymentCalculations.sendCurrency

        internationalPaymentCacheService.setValidatePaymentDetails(validatePaymentDetails)
        showProgressIndicator()
        internationalPaymentsInteractor.getQuoteDetails(validatePaymentDetails, quoteDetailsResponseExtendedResponseListener)
    }

    override fun fetchQuotation(beneficiaryEnteredDetails: BeneficiaryEnteredDetails) {
        this.beneficiaryEnteredDetails = beneficiaryEnteredDetails
        if (internationalPaymentCacheService.getQuotation().paymentQuoteDetails != null) {
            quotationDetailsReturned(internationalPaymentCacheService.getQuotation())
        } else {
            val validatePaymentDetails = ValidatePaymentDetails()
            validatePaymentDetails.accountType = beneficiaryEnteredDetails.fromAccountType
            validatePaymentDetails.payoutCurrency = beneficiaryEnteredDetails.currencyToPay
            validatePaymentDetails.availableBalance = beneficiaryEnteredDetails.fromAccountBalance
            validatePaymentDetails.beneficiaryId = beneficiaryEnteredDetails.beneficiaryId
            validatePaymentDetails.beneficiaryFirstName = beneficiaryEnteredDetails.beneficiaryNames
            validatePaymentDetails.beneficiarySurName = beneficiaryEnteredDetails.beneficiarySurname
            validatePaymentDetails.destinationCountryCode = beneficiaryEnteredDetails.paymentDestinationCountryCode
            validatePaymentDetails.streetAddress = beneficiaryEnteredDetails.paymentAddress
            validatePaymentDetails.city = beneficiaryEnteredDetails.paymentCity
            validatePaymentDetails.sendCurrency = beneficiaryEnteredDetails.sendCurrency
            validatePaymentDetails.foreignCurrencyWithZar = "true" //ALWAYS TRUE
            val beneficiaryGender = beneficiaryEnteredDetails.beneficiaryGender
            if (!beneficiaryGender.isNullOrEmpty()) {
                validatePaymentDetails.gender = if ('V' == beneficiaryGender[0]) "F" else beneficiaryGender[0].toString()
            }
            validatePaymentDetails.state = beneficiaryEnteredDetails.paymentState
            validatePaymentDetails.nonResidentAccountIdentifier = beneficiaryEnteredDetails.beneficiaryResidentialStatus()
            validatePaymentDetails.residingCountry = beneficiaryEnteredDetails.paymentDestinationCountryCode
            validatePaymentDetails.availableBalance = beneficiaryEnteredDetails.fromAccountBalance
            validatePaymentDetails.fromAccountNumber = beneficiaryEnteredDetails.fromAccountNumber
            validatePaymentDetails.amountToSend = beneficiaryEnteredDetails.paymentAmount
            validatePaymentDetails.destinationState = beneficiaryEnteredDetails.paymentState
            validatePaymentDetails.destinationCity = beneficiaryEnteredDetails.paymentCity
            validatePaymentDetails.sendCurrency = beneficiaryEnteredDetails.sendCurrency
            validatePaymentDetails.payoutCurrency = beneficiaryEnteredDetails.currencyToPay

            internationalPaymentCacheService.setValidatePaymentDetails(validatePaymentDetails)
            showProgressIndicator()
            internationalPaymentsInteractor.getQuoteDetails(validatePaymentDetails, quoteDetailsResponseExtendedResponseListener)
        }
    }

    override fun quotationDetailsReturned(successResponse: QuoteDetailsResponse) {
        val view = viewWeakReference.get() as InternationalPaymentsContract.InternationalPaymentsCalculateView
        if (view.isButtonContinueButton()) {
            when {
                SUCCESS.equals(successResponse.transactionStatus, true) -> {
                    val transferQuoteDetails = TransferQuoteDetails()
                    transferQuoteDetails.foreignCurrency = successResponse.paymentQuoteDetails?.foreignCurrency
                    transferQuoteDetails.localCurrency = successResponse.paymentQuoteDetails?.localCurrency
                    transferQuoteDetails.destinationCurrencyRate = successResponse.paymentQuoteDetails?.destinationCurrencyRate
                    transferQuoteDetails.gender = beneficiaryEnteredDetails?.beneficiaryGender
                    transferQuoteDetails.totalDue = successResponse.paymentQuoteDetails?.totalDue
                    transferQuoteDetails.commissionAmountFee = successResponse.paymentQuoteDetails?.commissionAmountFee
                    transferQuoteDetails.expectedPayoutAmount = successResponse.paymentQuoteDetails?.expectedPayoutAmount
                    transferQuoteDetails.payOutCurrency = successResponse.paymentQuoteDetails?.expectedPayoutCurrency
                    transferQuoteDetails.receiverFirstName = successResponse.paymentQuoteDetails?.receiverFirstName
                    transferQuoteDetails.receiverSurname = successResponse.paymentQuoteDetails?.receiverSurname
                    transferQuoteDetails.transactionReferenceNumber = successResponse.paymentQuoteDetails?.transactionReferenceNumber
                    transferQuoteDetails.testQuestion = successResponse.paymentQuoteDetails?.testQuestion
                    transferQuoteDetails.testAnswer = successResponse.paymentQuoteDetails?.testAnswer
                    transferQuoteDetails.streetAddress = successResponse.streetAddress
                    transferQuoteDetails.valueAddedTax = successResponse.paymentQuoteDetails?.vatAmount
                    transferQuoteDetails.destinationCity = successResponse.paymentQuoteDetails?.destinationCity
                    transferQuoteDetails.youSend = successResponse.paymentQuoteDetails?.expectedPayoutAmount
                    transferQuoteDetails.localAmount = successResponse.paymentQuoteDetails?.localAmount
                    transferQuoteDetails.foreignAmount = successResponse.paymentQuoteDetails?.foreignAmount
                    transferQuoteDetails.fromAccountDetails = beneficiaryEnteredDetails?.fromAccountDetails()
                    transferQuoteDetails.usdConversionRate = successResponse.paymentQuoteDetails?.usdConversionRate
                    transferQuoteDetails.countryToSendTo = beneficiaryEnteredDetails?.paymentCountry
                    transferQuoteDetails.destinationState = beneficiaryEnteredDetails?.paymentState
                    transferQuoteDetails.destinationCity = beneficiaryEnteredDetails?.paymentCity
                    transferQuoteDetails.reference = successResponse.paymentQuoteDetails?.moneyTransferControlNumberCode
                    transferQuoteDetails.expectedPayoutCurrency = successResponse.paymentQuoteDetails?.expectedPayoutCurrency
                    view.returnDataToQuoteDetailsScreen(transferQuoteDetails)
                }
                FAILURE.equals(successResponse.transactionStatus, true) -> {
                    successResponse.transactionMessage?.let { view.fillValidationError(it) }
                }
                else -> showErrorMessage(successResponse.transactionMessage.toString())
            }
        } else {
            if (SUCCESS.equals(successResponse.transactionStatus, true)) {
                view.populateExchangeRate(successResponse.paymentQuoteDetails?.totalDue.toString())
            } else {
                view.fillValidationError(successResponse.transactionMessage.toString())
            }
        }

        dismissProgressIndicator()
    }
}
