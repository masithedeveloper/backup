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

import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType
import com.barclays.absa.banking.framework.AbstractPresenter
import com.barclays.absa.banking.framework.app.BMBConstants.FAILED
import com.barclays.absa.banking.framework.app.BMBConstants.SUCCESS
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.payments.international.data.BeneficiaryEnteredDetails
import com.barclays.absa.banking.payments.international.data.CurrencyList
import com.barclays.absa.banking.payments.international.data.InternationalPaymentBeneficiaryDetails
import com.barclays.absa.banking.payments.international.data.ToCurrency
import com.barclays.absa.banking.payments.international.responseListeners.AddNewWesternUnionBeneficiaryExtendedResponseListener
import com.barclays.absa.banking.payments.international.responseListeners.ValidateNewWesternUnionBeneficiaryExtendedResponseListener
import com.barclays.absa.banking.payments.international.responseListeners.WesternUnionBeneficiaryDetailsExtendedResponseListener
import com.barclays.absa.banking.payments.international.responseListeners.WesternUnionCurrenciesResponseExtendedResponseListener
import com.barclays.absa.banking.payments.international.services.IInternationalPaymentCacheService
import com.barclays.absa.banking.payments.international.services.InternationalPaymentsInteractor
import com.barclays.absa.banking.payments.international.services.dto.AddNewWesternUnionBeneficiaryResponse
import com.barclays.absa.banking.payments.international.services.dto.ValidateNewWesternUnionBeneficiaryResponse
import com.barclays.absa.banking.payments.international.services.dto.WesternUnionBeneficiaryDetails
import com.barclays.absa.banking.payments.international.services.dto.WesternUnionCurrenciesResponse
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class InternationalPaymentsConfirmBeneficiaryDetailsPresenter(globalView: InternationalPaymentsContract.InternationalPaymentsConfirmBeneficiaryDetailsView, var sureCheckDelegate: SureCheckDelegate) : AbstractPresenter(WeakReference(globalView)), InternationalPaymentsContract.InternationalPaymentsConfirmBeneficiaryDetailsPresenter, InternationalPaymentsContract.UniversalInternationalPaymentsBeneficiary {

    private val internationalPaymentCacheService: IInternationalPaymentCacheService = getServiceInterface()
    private var internationalPaymentsInteractor: InternationalPaymentsInteractor = InternationalPaymentsInteractor()
    private val validateNewWesternUnionBeneficiaryExtendedResponseListener: ValidateNewWesternUnionBeneficiaryExtendedResponseListener by lazy { ValidateNewWesternUnionBeneficiaryExtendedResponseListener(this) }
    private val addNewWesternUnionBeneficiaryExtendedResponseListener: AddNewWesternUnionBeneficiaryExtendedResponseListener by lazy { AddNewWesternUnionBeneficiaryExtendedResponseListener(this) }
    private val westernUnionCurrenciesResponseExtendedResponseListener: WesternUnionCurrenciesResponseExtendedResponseListener by lazy { WesternUnionCurrenciesResponseExtendedResponseListener(this) }
    private val westernUnionBeneficiaryDetailsExtendedResponseListener: WesternUnionBeneficiaryDetailsExtendedResponseListener by lazy { WesternUnionBeneficiaryDetailsExtendedResponseListener(this) }
    private val beneficiaryDetails = InternationalPaymentBeneficiaryDetails()
    private var validateNewWesternUnionBeneficiaryResponse = ValidateNewWesternUnionBeneficiaryResponse()

    override fun validateBeneficiaryDetails(beneficiaryDetails: BeneficiaryEnteredDetails) {
        internationalPaymentsInteractor.validateNewWesternUnionBeneficiary(beneficiaryDetails, validateNewWesternUnionBeneficiaryExtendedResponseListener)
    }

    override fun validationReturned(successResponse: ValidateNewWesternUnionBeneficiaryResponse) {
        validateNewWesternUnionBeneficiaryResponse = successResponse
        val view = viewWeakReference.get() as InternationalPaymentsContract.InternationalPaymentsConfirmBeneficiaryDetailsView
        view.fetchBeneficiaryValidationResponse(successResponse)
        internationalPaymentsInteractor.addNewWesternUnionBeneficiary(successResponse.transactionReferenceId, addNewWesternUnionBeneficiaryExtendedResponseListener)
    }

    override fun beneficiaryAdded(successResponse: AddNewWesternUnionBeneficiaryResponse) {
        val view = viewWeakReference.get() as InternationalPaymentsContract.InternationalPaymentsConfirmBeneficiaryDetailsView
        if (!TransactionVerificationType.SURECHECKV2Required.toString().equals(successResponse.sureCheckFlag, ignoreCase = true) && SUCCESS.equals(successResponse.transactionStatus, true)) {
            beneficiaryDetails.beneficiaryId = successResponse.benId
            beneficiaryDetails.beneficiaryIFTType = successResponse.beneficiaryType
            beneficiaryDetails.cifkey = successResponse.cifKey
            beneficiaryDetails.eftNumber = successResponse.eftNumber
            beneficiaryDetails.tiebNumber = successResponse.tiebNumber
            beneficiaryDetails.transferType = "WesternUnion" //If this is ever not WesternUnion in this case I will buy someone a coffee
            beneficiaryDetails.status = "CURRENT" //This will always be current

            fetchBeneficiary(beneficiaryDetails)
        } else if (FAILED.equals(successResponse.transactionStatus, true)) {
            view.navigateToFailureScreen(successResponse.transactionMessage)
        }

        if (TransactionVerificationType.SURECHECKV2Required.toString().equals(successResponse.sureCheckFlag, ignoreCase = true)) {
            sureCheckDelegate.processSureCheck(view.fetchbaseActivity(), successResponse) { this.validationReturned(validateNewWesternUnionBeneficiaryResponse) }
        }
    }

    override fun fetchCurrencies() {
        internationalPaymentsInteractor.fetchWesternUnionCurrencies(internationalPaymentCacheService.getEnteredBeneficiaryDetails().paymentDestinationCountryCode.toString(), westernUnionCurrenciesResponseExtendedResponseListener)
    }

    fun currenciesReturned(successResponse: WesternUnionCurrenciesResponse) {
        val listOfPayoutCurrencies: ArrayList<CurrencyList> = arrayListOf()
        val listOfSendCurrencies: ArrayList<CurrencyList> = arrayListOf()
        val destinationCurrency = ToCurrency()
        val payoutCurrencies = successResponse.westernUnionCurrenciesResponse?.westernUnionCurrencyList
        val sendCurrencies = successResponse.westernUnionCurrenciesResponse?.sendCurrencyList
        val view = viewWeakReference.get() as InternationalPaymentsContract.InternationalPaymentsConfirmBeneficiaryDetailsView

        view.getLifecycleCoroutineScope().launch {
            payoutCurrencies?.forEach { currency ->
                val currencyItem = CurrencyList()
                currencyItem.currencyDescription = currency.currencyDescription
                currencyItem.currencyCode = currency.currencyCode
                listOfPayoutCurrencies.add(currencyItem)
            }

            sendCurrencies?.forEach { currency ->
                val currencyItem = CurrencyList()
                currencyItem.currencyDescription = currency.currencyDescription
                currencyItem.currencyCode = currency.currencyCode
                listOfSendCurrencies.add(currencyItem)
            }
        }

        successResponse.westernUnionCurrenciesResponse?.sendCurrencyList?.forEach { westernUnionCurrencyList ->
            destinationCurrency.toCurrencyCode = westernUnionCurrencyList.currencyCode
            destinationCurrency.toCurrencyDescription = westernUnionCurrencyList.currencyDescription
        }

        dismissProgressIndicator()
        view.populateCurrencyList(listOfPayoutCurrencies, listOfSendCurrencies, destinationCurrency)
    }

    override fun fetchBeneficiary(beneficiaryDetails: InternationalPaymentBeneficiaryDetails) {
        internationalPaymentsInteractor.fetchBeneficiaryDetails(beneficiaryDetails, westernUnionBeneficiaryDetailsExtendedResponseListener)
    }

    override fun beneficiaryServiceResponse(westernUnionBeneficiaryDetails: WesternUnionBeneficiaryDetails) {
        internationalPaymentCacheService.setBeneficiaryDetails(westernUnionBeneficiaryDetails)
        fetchCurrencies()
    }
}