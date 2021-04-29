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

import androidx.lifecycle.LifecycleCoroutineScope
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.BaseView
import com.barclays.absa.banking.payments.international.data.*
import com.barclays.absa.banking.payments.international.services.dto.*

interface InternationalPaymentsContract {
    interface WesternUnionPaymentHubView : BaseView {
        fun navigateToInternationalPaymentsDisclaimer()
        fun navigateToInternationalPaymentsHoursNote()
        fun beneficiaryListReturned(beneficiaries: ArrayList<InternationalPaymentBeneficiaryDetails>, recentBeneficiaryList: ArrayList<InternationalPaymentBeneficiaryDetails>)
        fun navigateToDisclaimerFragment()
        fun saveBeneficiaryDetails(westernUnionBeneficiaryDetails: WesternUnionBeneficiaryDetails)
        fun navigateToPendingTransactionScreen()
        fun navigateToGenericErrorScreen(transactionMessage: String?)
        fun getLifecycleCoroutineScope(): LifecycleCoroutineScope
    }

    interface WesternUnionPaymentHubPresenter {
        fun viewInstantiated()
        fun beneficiaryServiceResponse(westernUnionBeneficiaryListObject: WesternUnionBeneficiaryListObject)
        fun showErrorScreen()
        fun transactionStatusResponse(pendingTransactionResponse: OutBoundPendingTransactionResponse?)
        fun onPaySomeoneNew()
        fun holidaysAndTimeValidationResponse(validateForHolidaysAndTimeResponse: ValidateForHolidaysAndTimeResponse)
        fun fetchBeneficiary(beneficiaryDetails: InternationalPaymentBeneficiaryDetails)
        fun beneficiaryServiceResponse(westernUnionBeneficiaryDetails: WesternUnionBeneficiaryDetails)
    }

    interface InternationalPaymentsExtraDetailsView : BaseView {
        fun populateStateList(stateList: ArrayList<String>)
        fun populateLongCityList(cityList: ArrayList<String>)
        fun allowUserToEnterCity()
        fun noStateRequired()
        fun showSecurityQuestion()
        fun hideSecurityQuestion()
    }

    interface InternationalPaymentsExtraDetailsPresenter {
        fun stateListReturned(westernUnionCountries: WesternUnionCountriesListResponse?)
        fun countryChanged(countryCode: String)
        fun stateChanged(countryCode: String, state: String)
        fun cityListReturned(westernUnionCountriesListResponse: WesternUnionCountriesListResponse)
        fun cityEnteredOrChanged(shouldShowSecurityQuestionAndAnswer: String)
    }

    interface InternationalPaymentsCalculateView : BaseView {
        fun populateExchangeRate(exchangeRate: String)
        fun showRetryScreen()
        fun returnDataToQuoteDetailsScreen(transferQuoteDetails: TransferQuoteDetails)
        fun showErrorMessage(errorMessage: String)
        fun fillValidationError(error: String)
        fun isButtonContinueButton(): Boolean
    }

    interface InternationalPaymentsCalculatePresenter {
        fun calculationButtonClicked(paymentCalculations: PaymentCalculations, beneficiaryEnteredDetails: BeneficiaryEnteredDetails)
        fun fetchQuotation(beneficiaryEnteredDetails: BeneficiaryEnteredDetails)
        fun quotationDetailsReturned(successResponse: QuoteDetailsResponse)
    }

    interface InternationalPaymentsConfirmPaymentView : BaseView {
        fun fetchbaseActivity(): BaseActivity
        fun showPaymentPendingScreen(paymentDate: String, referenceNumber: String)
        fun showError(bmgErrorMessage: String)
        fun showConnectionErrorScreen()
    }

    interface InternationalPaymentsConfirmPaymentPresenter {
        fun paymentValidation(isOnceOff: Boolean, reference: String)
        fun paymentValidated(successResponse: WesternUnionValidatePaymentResponse?)
        fun processOnceOffPayment()
        fun performBeneficiaryPayment()
        fun onceOffPaymentResponse(successResponse: PerformOnceOffInternationalPaymentResponse)
        fun existingBeneficiaryPaymentResponse(successResponse: InternationalPaymentsResultResponse)
    }

    interface InternationalPaymentsConfirmBeneficiaryDetailsView : BaseView {
        fun fetchbaseActivity(): BaseActivity
        fun navigateToSuccessScreen()
        fun navigateToFailureScreen(transactionMessage: String?)
        fun populateCurrencyList(listOfSendCurrencies: ArrayList<CurrencyList>, listOfCurrencies: ArrayList<CurrencyList>, destinationCurrency: ToCurrency)
        fun fetchBeneficiaryValidationResponse(validateNewWesternUnionBeneficiaryResponse: ValidateNewWesternUnionBeneficiaryResponse)
        fun getLifecycleCoroutineScope(): LifecycleCoroutineScope
    }

    interface InternationalPaymentsConfirmBeneficiaryDetailsPresenter {
        fun validateBeneficiaryDetails(beneficiaryDetails: BeneficiaryEnteredDetails)
        fun validationReturned(successResponse: ValidateNewWesternUnionBeneficiaryResponse)
        fun fetchBeneficiary(beneficiaryDetails: InternationalPaymentBeneficiaryDetails)
        fun beneficiaryAdded(successResponse: AddNewWesternUnionBeneficiaryResponse)
        fun fetchCurrencies()
    }

    interface InternationalPaymentsCountryListPresenter {
        fun fetchListOfCountries()
    }

    interface InternationalPaymentsCountryListView : BaseView {
        fun countryListReturned(westernUnionCountries: ArrayList<InternationalCountryList>)
        fun getLifecycleCoroutineScope(): LifecycleCoroutineScope
    }

    interface UniversalInternationalPaymentsBeneficiary {
        fun beneficiaryServiceResponse(westernUnionBeneficiaryDetails: WesternUnionBeneficiaryDetails)
        fun showGenericErrorMessage(className: String)
    }
}