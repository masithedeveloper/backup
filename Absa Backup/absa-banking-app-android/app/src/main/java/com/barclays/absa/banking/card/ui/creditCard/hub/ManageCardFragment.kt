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
package com.barclays.absa.banking.card.ui.creditCard.hub

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.CardPin
import com.barclays.absa.banking.boundary.model.debitCard.DebitCard
import com.barclays.absa.banking.card.services.card.dto.CardDetails
import com.barclays.absa.banking.card.services.card.dto.ManageCardResponseObject.ManageCardItem
import com.barclays.absa.banking.card.services.card.dto.PauseStates
import com.barclays.absa.banking.card.services.card.dto.TravelUpdateModel
import com.barclays.absa.banking.card.ui.CardManagementView
import com.barclays.absa.banking.card.ui.cardPinRetrieval.CardPinRetrievalActivity
import com.barclays.absa.banking.card.ui.cardViewDetails.ViewCardDetailsActivity
import com.barclays.absa.banking.card.ui.creditCard.hub.manageMobilePayments.ManageMobilePaymentsActivity
import com.barclays.absa.banking.card.ui.creditCard.hub.manageMobilePayments.ManageMobilePaymentsActivity.Companion.CARD_NUMBER
import com.barclays.absa.banking.card.ui.creditCardReplacement.CreditCardReplacementOverviewActivity
import com.barclays.absa.banking.card.ui.debitCard.ui.StopAndReplaceDebitCardActivity
import com.barclays.absa.banking.card.ui.debitCard.ui.StopAndReplaceDebitCardDetailsFragment
import com.barclays.absa.banking.card.ui.pauseCard.PauseCardActivity
import com.barclays.absa.banking.card.ui.secondaryCard.SecondaryCardActivity
import com.barclays.absa.banking.card.ui.secondaryCard.SecondaryCardActivity.Companion.SECONDARY_CARD_OBJECT
import com.barclays.absa.banking.express.secondaryCard.getSecondaryCardMandate.dto.GetSecondaryCardMandateResponse
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.banking.settings.ui.SettingsCardLimitsActivity
import com.barclays.absa.banking.shared.ItemPagerFragment
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.ScanToPayViewModel
import com.barclays.absa.crypto.SymmetricCryptoHelper
import com.barclays.absa.crypto.SymmetricCryptoHelper.DecryptionFailureException
import com.barclays.absa.utils.*
import com.google.android.gms.common.util.Hex
import kotlinx.android.synthetic.main.card_fragment.*
import styleguide.content.Card
import styleguide.utils.extensions.removeSpaces
import styleguide.utils.extensions.toFormattedAccountNumber

class ManageCardFragment : ItemPagerFragment() {

    enum class PaymentProviderIdentifier(val value: Char) {
        VISA('4'),
        MASTERCARD('5')
    }

    private lateinit var baseActivity: BaseActivity
    private lateinit var manageCardViewModel: ManageCardViewModel
    private lateinit var sureCheckDelegate: SureCheckDelegate
    private lateinit var card: Card
    private lateinit var manageCardItem: ManageCardItem
    private lateinit var secondaryCards: GetSecondaryCardMandateResponse
    private var isFromPinRetrieval = true
    private var isFromCardHub = false
    private var isCardPaused = false
    private val appCacheService: IAppCacheService = getServiceInterface()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseActivity = context as BaseActivity
        manageCardViewModel = viewModel()

        sureCheckDelegate = object : SureCheckDelegate(baseActivity) {
            override fun onSureCheckProcessed() {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (isFromPinRetrieval) {
                        manageCardViewModel.retrievePin()
                    } else {
                        manageCardViewModel.retrieveCardDetails()
                    }
                }, 250)
            }
        }
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putParcelable(CARD_ITEM, manageCardItem)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.card_fragment, container, false).rootView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!::manageCardItem.isInitialized) {
            manageCardItem = savedInstanceState?.getParcelable(CARD_ITEM) ?: ManageCardItem()
        }

        secondaryCards = arguments?.getParcelable(SECONDARY_CARD_INFO) ?: GetSecondaryCardMandateResponse()

        secondaryCards.addAdditionalSecondaryCardToList()

        manageCardItem.cardLimit?.cardType?.let { cardType -> card = Card(cardType, manageCardItem.cardLimit?.cardNumber.toFormattedAccountNumber()) }

        isFromCardHub = (activity as CardManagementView).isFromCardHub

        initView()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (!::manageCardItem.isInitialized) {
            manageCardItem = savedInstanceState?.getParcelable(CARD_ITEM) ?: ManageCardItem()
        }

        manageCardItem.cardLimit?.cardType?.let { cardType -> card = Card(cardType, manageCardItem.cardLimit?.cardNumber.toFormattedAccountNumber()) }
    }

    private fun initView() {
        isFromCardHub = (activity as CardManagementView).isFromCardHub

        if (!::card.isInitialized) {
            return
        }

        if (card.cardType != null && listOf(CREDIT_CARD, getString(R.string.credit_card_type), getString(R.string.credit_card)).compareWithIgnoreCase(card.cardType!!)) {
            card.cardType = getString(R.string.credit_card)
        } else {
            card.cardType = getString(R.string.debit_card)
        }
        cardView.setCard(card)
        hideItemsNotAllowed()

        if (isCardPaused(manageCardItem.pauseStates)) {
            isCardPaused = true
            cardView.setLockVisibility(View.VISIBLE, getString(R.string.vcl_lock))
        }

        if (!appCacheService.isPrimarySecondFactorDevice()) {
            viewCardDetailsActionButtonView.visibility = View.GONE
        }

        setupFeatureSwitchingVisibilityToggles()
        setUpOnClickListeners()
        setUpObservers()
    }

    private fun setUpObservers() {
        manageCardViewModel.cardPin = MutableLiveData()
        manageCardViewModel.cardPin.observe(viewLifecycleOwner, {
            sureCheckDelegate.processSureCheck(baseActivity, it) {
                navigateToPinRetrievalScreen(it)
            }
            baseActivity.dismissProgressDialog()
        })

        manageCardViewModel.virtualCardDetailsResponse = MutableLiveData()
        manageCardViewModel.virtualCardDetailsResponse.observe(viewLifecycleOwner, {
            sureCheckDelegate.processSureCheck(baseActivity, it) {
                val cardDetails: CardDetails? = it.cardDetails

                if (cardDetails != null) {
                    card.cardType?.let { cardType -> cardDetails.cardType = cardType }
                    val decryptedCardDetails: String = decryptCardDetails(cardDetails)

                    if (decryptedCardDetails.length > 10) {
                        cardDetails.apply {
                            expiryDate = DateUtils.formatDate(decryptedCardDetails.substring(0, 8), "yyyymmdd", "mm/yy")
                            cvv = decryptedCardDetails.substring(8, 11)
                        }
                        navigateToViewCardDetailsScreen(cardDetails)
                    } else {
                        baseActivity.showGenericErrorMessage()
                    }
                }
                baseActivity.dismissProgressDialog()
            }
        })

        manageCardViewModel.failureResponse.observe(viewLifecycleOwner, {
            baseActivity.showGenericErrorMessage()
            baseActivity.dismissProgressDialog()
        })
    }

    private fun decryptCardDetails(cardDetails: CardDetails): String {
        return try {
            val encryptedCardDetails = Hex.stringToBytes(cardDetails.encryptedCardDetails)
            val decryptedCardDetails = SymmetricCryptoHelper.getInstance().decryptCardDetails(encryptedCardDetails)
            String(decryptedCardDetails)
        } catch (e: DecryptionFailureException) {
            BMBLogger.e(ManageCardFragment::class.java.simpleName, "Failed to decrypt card details")
            ""
        }
    }

    private fun isCardPaused(pauseStates: PauseStates?): Boolean = listOf(pauseStates?.internationalAtmTransactions
            ?: NOT_PAUSED, pauseStates?.internationalPointOfSaleTransactions
            ?: NOT_PAUSED, pauseStates?.localAtmTransactions
            ?: NOT_PAUSED, pauseStates?.localPointOfSaleTransactions
            ?: NOT_PAUSED, pauseStates?.onlinePurchases
            ?: NOT_PAUSED).compareWithIgnoreCase(PAUSED)

    override fun getTabDescription() = arguments?.getString(TAB_DESCRIPTION_KEY) ?: ""

    private fun setupFeatureSwitchingVisibilityToggles() {
        featureSwitchingToggles.let { featureToggle ->
            if (featureToggle.stopAndReplaceCreditCard == FeatureSwitchingStates.GONE.key) {
                stopCardActionButtonView.visibility = View.GONE
            }
            if (featureToggle.creditCardPINRetrieval == FeatureSwitchingStates.GONE.key) {
                pinRetrievalActionButtonView.visibility = View.GONE
            }
            if (featureToggle.cardTravelAbroad == FeatureSwitchingStates.GONE.key) {
                cardWhenTravellingActionButtonView.visibility = View.GONE
            }
            if (featureToggle.creditCardTemporaryLock == FeatureSwitchingStates.GONE.key) {
                temporaryLockActionButtonView.visibility = View.GONE
            }

            if (featureToggle.cardViewDetails == FeatureSwitchingStates.GONE.key) {
                viewCardDetailsActionButtonView.visibility = View.GONE
            }

            val isVisaCard = PaymentProviderIdentifier.VISA.value == manageCardItem.cardLimit?.cardNumber?.firstOrNull() ?: false
            if (ScanToPayViewModel.scanToPayGone() || !isVisaCard) {
                mobilePaymentsActionButtonView.visibility = View.GONE
            }
        }
    }

    private fun navigateToTemporaryPauseCard(pauseStates: PauseStates?) {
        val pauseCardIntent = Intent(baseActivity, PauseCardActivity::class.java).apply {
            putExtra(PAUSE_CARD, pauseStates as? Parcelable)
            putExtra(MANAGE_CARD_ITEM, manageCardItem as? Parcelable)
            putExtra(CreditCardHubActivity.IS_FROM_CARD_HUB, isFromCardHub)
        }
        startActivity(pauseCardIntent)
    }

    private fun navigateToPinRetrievalScreen(cardPin: CardPin) {
        val navigateToPinRetrievalScreen = Intent(baseActivity, CardPinRetrievalActivity::class.java).apply {
            putExtra(CreditCardHubActivity.CARD_ITEM, cardPin)
        }
        startActivity(navigateToPinRetrievalScreen)
        AnalyticsUtil.trackAction("Pin Retrieval", if (isFromCardHub) "Pin Retrieval Card hub" else "Pin Retrieval Manage Cards")
    }

    private fun setUpOnClickListeners() {
        stopCardActionButtonView.setOnClickListener {
            AnalyticsUtil.trackAction("Manage Cards", "Credit card hub - Card tabs - S&R")
            if (featureSwitchingToggles.stopAndReplaceCreditCard == FeatureSwitchingStates.DISABLED.key) {
                startActivity(IntentFactory.capabilityUnavailable(activity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_stop_replace_credit_card))))
            } else {
                if (card.cardType != null && listOf(CREDIT_CARD, getString(R.string.credit_card_type), getString(R.string.credit_card)).compareWithIgnoreCase(card.cardType!!)) {
                    card.cardNumber?.let {
                        navigateToStopAndReplaceCreditCardScreen(it)
                    }
                } else {
                    val debitCard = DebitCard().apply {
                        cardType = card.cardType
                        debitCardNumber = card.cardNumber.removeSpaces()
                    }
                    navigateToStopAndReplaceDebitCardScreen(debitCard)
                }
            }
        }
        transactionLimitActionButtonView.setOnClickListener {
            val cardType = if (listOf(CREDIT_CARD, getString(R.string.credit_card_type), getString(R.string.credit_card)).compareWithIgnoreCase(card.cardType ?: "")) {
                "CreditCardLimit"
            } else {
                "DebitCardLimit"
            }
            AnalyticsUtil.trackAction("Manage Cards", "${cardType}_${if (isFromCardHub) "CreditCardHubScreen" else "ManageCardScreen"}_CardTransactionLimitsButtonClicked")
            manageCardItem.cardLimit?.let { navigateToChangeLimitScreen() }
        }
        pinRetrievalActionButtonView.setOnClickListener {
            AnalyticsUtil.trackAction("Manage Cards", "PIN Retrieval button")
            if (featureSwitchingToggles.creditCardPINRetrieval == FeatureSwitchingStates.DISABLED.key) {
                startActivity(IntentFactory.capabilityUnavailable(baseActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_pin_retrieval))))
            } else {
                isFromPinRetrieval = true
                manageCardItem.cardLimit?.cardNumber?.let { manageCardViewModel.retrievePin(it) }
            }
        }
        cardWhenTravellingActionButtonView.setOnClickListener {
            if (featureSwitchingToggles.cardTravelAbroad == FeatureSwitchingStates.DISABLED.key) {
                startActivity(IntentFactory.capabilityUnavailable(baseActivity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_travel_abroad))))
            } else {
                navigateToTravelAbroadScreen()
            }
        }
        temporaryLockActionButtonView.setOnClickListener {
            AnalyticsUtil.trackAction("Manage Cards", "Credit card hub - Card tabs - Temporary lock")
            if (featureSwitchingToggles.creditCardTemporaryLock == FeatureSwitchingStates.DISABLED.key) {
                startActivity(IntentFactory.capabilityUnavailable(activity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_temporary_lock))))
            } else {
                navigateToTemporaryPauseCard(manageCardItem.pauseStates)
            }
        }
        viewCardDetailsActionButtonView.setOnClickListener {
            isFromPinRetrieval = false
            AnalyticsUtil.trackAction("Manage Cards", "ViewCard_ManageCardsScreen_ViewCardDetailsButtonClicked")
            if (featureSwitchingToggles.cardViewDetails == FeatureSwitchingStates.DISABLED.key) {
                startActivity(IntentFactory.capabilityUnavailable(activity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_temporary_lock))))
            } else {
                if (manageCardItem.cardLimit?.cardNumber != null && manageCardItem.cardLimit?.cardNumber!!.isNotEmpty()) {
                    manageCardViewModel.retrieveCardDetails(manageCardItem.cardLimit?.cardNumber!!)
                }
            }
        }

        mobilePaymentsActionButtonView.setOnClickListener {
            AnalyticsUtil.trackAction("Manage Cards", "ViewCard_ManageCardsScreen_MobilePaymentsButtonClicked")
            if (ScanToPayViewModel.scanToPayDisabled()) {
                startActivity(IntentFactory.featureUnavailable(activity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.scan_to_pay))))
            } else {
                val cardNumber = manageCardItem.cardLimit?.cardNumber
                if (!cardNumber.isNullOrEmpty()) {
                    AnalyticsUtil.trackAction("Manage Cards", "ManageCards_CardOptionsScreen_MobilePaymentsButtonClicked")
                    startActivity(Intent(activity, ManageMobilePaymentsActivity::class.java).putExtra(CARD_NUMBER, cardNumber))
                }
            }
        }

        manageSecondaryCardActionButtonView.setOnClickListener {
            if (featureSwitchingToggles.secondaryCardManagement == FeatureSwitchingStates.DISABLED.key) {
                startActivity(IntentFactory.capabilityUnavailable(activity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_secondary_card))))
            } else {
                AnalyticsUtil.trackAction("Secondary card", "SecondaryCard_${if (isFromCardHub) "CreditCardHub" else "ManageCards"}Screen_SecondaryCardButtonClicked")
                Intent(baseActivity, SecondaryCardActivity::class.java).apply {
                    putExtra(SECONDARY_CARD_OBJECT, secondaryCards)
                    startActivity(this)
                }
            }
        }
    }

    private fun navigateToTravelAbroadScreen() {
        if (manageCardItem.travelAbroad == null) {
            manageCardItem.travelAbroad = TravelUpdateModel()
        }
        val intent = Intent(baseActivity, CardTravelAbroadActivity::class.java).apply {
            putExtra(TRAVEL_DATA, manageCardItem.travelAbroad)
            putExtra(CreditCardHubActivity.IS_FROM_CARD_HUB, isFromCardHub)
            putExtra(CreditCardHubActivity.CARD_NUMBER, card.cardNumber.removeSpaces())
        }
        startActivity(intent)

        if (card.cardType != null && listOf(CREDIT_CARD, getString(R.string.credit_card_type), getString(R.string.credit_card)).compareWithIgnoreCase(card.cardType!!)) {
            AnalyticsUtil.trackAction("Travel Abroad", "TravelAbroad_ManageCardsScreen_TravelAbroadCreditButtonClicked")
        } else {
            AnalyticsUtil.trackAction("Travel Abroad", "TravelAbroad_ManageCardsScreen_TravelAbroadDebitButtonClicked")
        }
    }

    private fun navigateToViewCardDetailsScreen(cardDetails: CardDetails) {
        val navigateToViewCardDetailsScreen = Intent(baseActivity, ViewCardDetailsActivity::class.java).apply {
            putExtra(CARD_DETAILS, cardDetails)
            putExtra(PAUSE_CARD, isCardPaused)
        }
        startActivity(navigateToViewCardDetailsScreen)
    }

    private fun navigateToChangeLimitScreen() {
        val settingsChangeCardLimitsIntent = Intent(baseActivity, SettingsCardLimitsActivity::class.java).apply {
            putExtra(MANAGE_CARD_ITEM, manageCardItem as? Parcelable)
            putExtra(CreditCardHubActivity.IS_FROM_CARD_HUB, isFromCardHub)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(settingsChangeCardLimitsIntent)
    }

    private fun navigateToStopAndReplaceCreditCardScreen(cardNumber: String) {
        val navigateToCreditCardReplacementScreen = Intent(baseActivity, CreditCardReplacementOverviewActivity::class.java).apply {
            putExtra(CreditCardHubActivity.CARD_NUMBER, cardNumber.removeSpaces())
            putExtra(BMBConstants.ORIGIN_SCREEN, baseActivity.javaClass.simpleName)
        }
        appCacheService.setCreditCardFlow(true)
        startActivity(navigateToCreditCardReplacementScreen)
    }

    private fun hideItemsNotAllowed() {
        if (!OperatorPermissionUtils.isMainUser() || baseActivity.isBusinessAccount || baseActivity.shouldBlockUser()) {
            stopCardActionButtonView.visibility = View.GONE
            pinRetrievalActionButtonView.visibility = View.GONE
            transactionLimitActionButtonView.visibility = View.GONE
            temporaryLockActionButtonView.visibility = View.GONE
            viewCardDetailsActionButtonView.visibility = View.GONE
        }

        if (secondaryCards.secondaryCardMandateDetailsList.isNotEmpty() && manageCardItem.cardLimit?.cardNumber == secondaryCards.primaryPlastic && featureSwitchingToggles.secondaryCardManagement != FeatureSwitchingStates.GONE.key) {
            manageSecondaryCardActionButtonView.visibility = View.VISIBLE
        }

        if (featureSwitchingToggles.secondaryCardGracePeriod != FeatureSwitchingStates.ACTIVE.key && manageCardItem.cardLimit?.closeSubReason?.contains(Regex("SN|AN")) == true) {
            stopCardActionButtonView.visibility = View.GONE
            transactionLimitActionButtonView.visibility = View.GONE
        }
    }

    private fun navigateToStopAndReplaceDebitCardScreen(debitCard: DebitCard) {
        val debitCardIntent = Intent(baseActivity, StopAndReplaceDebitCardActivity::class.java).apply {
            putExtra(StopAndReplaceDebitCardDetailsFragment.DEBIT_CARD, debitCard)
        }
        startActivity(debitCardIntent)
    }

    override fun onResume() {
        super.onResume()
        KeyboardUtils.hideSoftKeyboard(rootConstraintLayout)
    }

    companion object {
        const val PAUSE_CARD = "PAUSE_CARD"
        const val MANAGE_CARD_ITEM = "MANAGE_CARD_ITEM"
        const val CREDIT_CARD = "CC"
        const val PAUSED = "Y"
        const val NOT_PAUSED = "N"
        const val CARD_ITEM = "CARD_ITEM"
        const val TRAVEL_DATA = "TRAVEL_DATA"
        const val CARD_DETAILS = "CARD_DETAILS"
        const val SECONDARY_CARD_INFO = "SECONDARY_CARD_INFO"

        @JvmStatic
        fun newInstance(manageCardItem: ManageCardItem, secondaryCards: GetSecondaryCardMandateResponse, description: String?): ManageCardFragment {
            return ManageCardFragment().apply {
                manageCardItem.cardLimit?.cardType?.let { cardType ->
                    card = Card(cardType, manageCardItem.cardLimit?.cardNumber.toFormattedAccountNumber())
                }
                this.manageCardItem = manageCardItem
                arguments = Bundle().apply {
                    putString(TAB_DESCRIPTION_KEY, description)
                    putParcelable(SECONDARY_CARD_INFO, secondaryCards)
                }
            }
        }
    }

    private fun List<String>.compareWithIgnoreCase(comparatorString: String): Boolean {
        this.forEach {
            if (it.contains(comparatorString, ignoreCase = true)) {
                return true
            }
        }
        return false
    }
}