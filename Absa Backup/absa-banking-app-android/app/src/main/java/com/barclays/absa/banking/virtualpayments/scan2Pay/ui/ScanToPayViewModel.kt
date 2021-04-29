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
package com.barclays.absa.banking.virtualpayments.scan2Pay.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BuildConfigHelper
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.barclays.absa.banking.presentation.whatsNew.WhatsNewAccessPrivileges
import com.barclays.absa.banking.presentation.whatsNew.WhatsNewHelper
import com.barclays.absa.banking.presentation.whatsNew.WhatsNewHelper.meetBusinessRequirements
import com.barclays.absa.banking.presentation.whatsNew.WhatsNewPage
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.ScanToPayInteractor
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.factory.ScanToPayMockFactory
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.response.ScanToPayRegistrationListener
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.response.ScanToPayRegistrationResponseListener
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.response.ScanToPayTokenResponseListener
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.response.dto.ScanToPayAuthResponse
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.response.dto.ScanToPayAuthResponse.Card
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.response.dto.ScanToPayCardListResponse
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.response.dto.ScanToPayRegistrationResponse
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.views.ScanToPayPaymentFragment.TipOption
import com.entersekt.scan2pay.Amount
import com.entersekt.scan2pay.PullPayment
import com.entersekt.scan2pay.SourceOfFunds
import com.entersekt.scan2pay.Tip
import com.google.gson.Gson

class ScanToPayViewModel : BaseViewModel() {

    companion object {
        fun scanToPayGone(): Boolean = if (BuildConfigHelper.STUB) {
            false
        } else {
            FeatureSwitchingCache.featureSwitchingToggles.scanToPay == FeatureSwitchingStates.GONE.key || !BuildConfig.TOGGLE_DEF_SCAN_TO_PAY_ENABLED || !getServiceInterface<IAppCacheService>().isPrimarySecondFactorDevice() || !scanToPayWhatsNewPage.meetBusinessRequirements()
        }

        fun customiseLoginOptionGone(): Boolean = if (BuildConfigHelper.STUB) {
            false
        } else {
            FeatureSwitchingCache.featureSwitchingToggles.scanToPay == FeatureSwitchingStates.GONE.key || !BuildConfig.TOGGLE_DEF_SCAN_TO_PAY_ENABLED || !scanToPayWhatsNewPage.meetBusinessRequirements()
        }

        fun scanToPayDisabled(): Boolean = FeatureSwitchingCache.featureSwitchingToggles.scanToPay == FeatureSwitchingStates.DISABLED.key

        const val FEATURE_NAME = "Scan to Pay"

        val scanToPayWhatsNewPage: WhatsNewPage = WhatsNewPage(R.string.scan_to_pay_whats_new_title, R.string.scan_to_pay_whats_new_content, "whats_new_scan_to_pay.json", null,
                WhatsNewHelper.isEnabledFeature(FeatureSwitchingCache.featureSwitchingToggles.scanToPay),
                WhatsNewAccessPrivileges(individual = true, operator = false, business = false, joint = false, soleProprietor = true))
    }

    enum class SplitBillViewOption { NO_SPLIT_OPTION, SPLIT, EDIT_SPLIT }

    var isTermsOfUseAccepted = false
    private val scanToPayService by lazy { ScanToPayInteractor() }
    private val scanToPayRegistrationResponseListener by lazy {
        ScanToPayRegistrationResponseListener(object : ScanToPayRegistrationListener {

            override fun setScanToPayRegistrationResponse(scanToPayRegistrationResponse: ScanToPayRegistrationResponse) {
                scanToPayRegistrationResponseLiveData.value = scanToPayRegistrationResponse
            }

            override fun setScanToPayRegistrationFailureResponse(failureResponse: ResponseObject?) {
                failureResponse?.let {
                    notifyFailure(it)
                }
            }
        })
    }

    fun fetchScanToPayCardList() = scanToPayService.fetchScanToPayCardList(object : ExtendedResponseListener<ScanToPayCardListResponse>() {
        override fun onSuccess(successResponse: ScanToPayCardListResponse) {
            if (BMBConstants.FAILURE.equals(successResponse.transactionStatus, true)) {
                fetchCardListFailedMessage = successResponse.transactionMessage
            }
            availableVisaCardList.value = successResponse.cardList
        }
    })

    var availableVisaCardList: MutableLiveData<List<ScanToPayCardListResponse.Card>> = MutableLiveData()
    var fetchCardListFailedMessage: String? = null
    var splitByPeople: Boolean = true

    private val scanToPayTokenResponseListener by lazy { ScanToPayTokenResponseListener(this) }

    var scanToPayRegistrationResponseLiveData: MutableLiveData<ScanToPayRegistrationResponse> = MutableLiveData()
    var selectedCard: MutableLiveData<ScanToPayCardListResponse.Card> = MutableLiveData()
    val selectedAuthCard: MutableLiveData<Card> = MutableLiveData()
    var scanToPayTokenResponseLiveData: MutableLiveData<String> = MutableLiveData()
    var scanToPayPartialPaymentDetails: ScanToPayPartialPaymentDetails = ScanToPayPartialPaymentDetails()

    var qrCode: String = ""
    var payForAmount: Double = 0.00
    var amount: Double = 0.00
    var isAmountEditable: Boolean = false
    var tipAmount: Double = 0.00
    var tipOption: TipOption? = null
    lateinit var tipOptionList: List<TipOption>

    lateinit var paymentAuth: PullPayment

    val partialPaymentPayForAmount: MutableLiveData<Double> = MutableLiveData()

    fun isValidPayForAmount(): Boolean = payForAmount > 0.00

    fun isNoTip(): Boolean = paymentAuth.tip is Tip.None
    fun isPartialPayment(): Boolean = paymentAuth.amount is Amount.InputRequired.PartialPayment
    fun isPayerReferenceRequired(): Boolean = paymentAuth.payerReferenceRequired

    fun registerCard(cardNumber: String) = scanToPayService.scanToPayRegistration(cardNumber, true, scanToPayRegistrationResponseListener)
    fun fetchScanToPayToken() = scanToPayService.fetchScanToPayToken(scanToPayTokenResponseListener)

    fun getSelectedSourceOfFunds(): SourceOfFunds? = paymentAuth.sourceOfFunds.first { sourceOfFunds -> sourceOfFunds.index == selectedAuthCard.value?.index }

    fun setupDefaultAuthCard() {
        val scanToPayAuthCardList = getScanToPayAuthCardList()
        val defaultMerchantCard = scanToPayAuthCardList.firstOrNull { it.acceptedByMerchant }
        selectedAuthCard.value = defaultMerchantCard ?: scanToPayAuthCardList.first { !it.acceptedByMerchant }
    }

    fun getScanToPayAuthCardList(): List<Card> = try {
        val authMetaData = if (BuildConfigHelper.STUB) {
            ScanToPayMockFactory.getMockAuthMetaData()
        } else {
            val nameValuePair = BMBApplication.getInstance().auth.nameValues.first { nameValue -> nameValue.name.equals("connekt_payment", true) }
            nameValuePair.value.substringAfter("value='").substringBefore("'")
        }
        BMBLogger.d(FEATURE_NAME, authMetaData)
        Gson().fromJson(authMetaData, ScanToPayAuthResponse::class.java).meta.cards.map { authCard -> getCardDetailsUpdatedAuthCard(authCard) }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }

    class ScanToPayPartialPaymentDetails {
        var splitBillViewOption: SplitBillViewOption = SplitBillViewOption.SPLIT
        var splitFactor: Double = 1.00
            set(value) {
                field = value
                splitBillViewOption = if (field == 1.00) {
                    SplitBillViewOption.SPLIT
                } else {
                    SplitBillViewOption.EDIT_SPLIT
                }
            }

        var splitBy: Int = 1
            set(value) {
                field = value
                updateSplitFactor()
            }

        var payFor: Int = 1
            set(value) {
                field = value
                updateSplitFactor()
            }

        private fun updateSplitFactor() {
            splitFactor = payFor.toDouble() / splitBy
        }
    }

    private fun getCardDetailsUpdatedAuthCard(authCard: Card): Card {
        val matchingCardDetails = availableVisaCardList.value?.firstOrNull { cardDetails -> authCard.cardNumber.take(6).equals(cardDetails.cardNumber.take(6), true) && authCard.cardNumber.takeLast(4).equals(cardDetails.cardNumber.takeLast(4), true) }
        return if (matchingCardDetails == null) {
            authCard
        } else {
            authCard.apply {
                cardNumber = matchingCardDetails.cardNumber
                cardType = matchingCardDetails.getCardTypeDescription()
            }
        }
    }
}