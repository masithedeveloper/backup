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
package com.barclays.absa.banking.card.ui.pauseCard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import com.barclays.absa.banking.R
import com.barclays.absa.banking.card.services.card.dto.ManageCardResponseObject.ManageCardItem
import com.barclays.absa.banking.card.services.card.dto.PauseCardLockTypes
import com.barclays.absa.banking.card.services.card.dto.PauseStates
import com.barclays.absa.banking.card.services.card.dto.TravelUpdateModel
import com.barclays.absa.banking.card.ui.CardIntentFactory
import com.barclays.absa.banking.card.ui.creditCard.hub.CreditCardHubActivity
import com.barclays.absa.banking.card.ui.creditCard.hub.ManageCardFragment
import com.barclays.absa.banking.card.ui.creditCard.hub.ManageCardViewModel
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.pause_card_activity.*
import styleguide.bars.ToggleView.OnCustomCheckChangeListener

class PauseCardActivity : BaseActivity(), View.OnClickListener, OnCustomCheckChangeListener {
    private lateinit var manageCardViewModel: ManageCardViewModel
    private lateinit var sureCheckDelegate: SureCheckDelegate
    private var isFromCreditCardHub = false
    private var manageCardItem: ManageCardItem? = null
    private var pauseStates: PauseStates? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pause_card_activity)
        setToolBarBack(R.string.pause_card_temporary_lock)
        manageCardViewModel = viewModel()

        sureCheckDelegate = object : SureCheckDelegate(this) {
            override fun onSureCheckProcessed() {
                Handler(Looper.getMainLooper()).postDelayed({ pauseStates?.let { manageCardViewModel.updatePauseCarsStates(it) } }, 250)
            }

            override fun onSureCheckFailed() {
                manageCardItem?.pauseStates?.cardNumber?.let { cardNumber -> manageCardViewModel.fetchPauseCardStates(cardNumber) }
            }
        }

        pauseStates = intent?.getSerializableExtra(ManageCardFragment.PAUSE_CARD) as? PauseStates
        manageCardItem = intent?.getSerializableExtra(ManageCardFragment.MANAGE_CARD_ITEM) as? ManageCardItem
        isFromCreditCardHub = intent.getBooleanExtra(CreditCardHubActivity.IS_FROM_CARD_HUB, false)
        updatePauseStates(pauseStates)
        updateTravelDates(manageCardItem?.travelAbroad)
        initialiseViews()
        setUpObservers()
        AnalyticsUtil.trackAction("Pause card", "Pause Card")
    }

    private fun setUpObservers() {
        manageCardViewModel.updatePauseCardResponse.observe(this, { pauseCardResponse ->
            if (BMBConstants.SUCCESS.equals(pauseCardResponse.transactionStatus, ignoreCase = true)) {
                sureCheckDelegate.processSureCheck(this, pauseCardResponse) { navigateToTemporaryLockUpdatedResultScreen() }
            } else {
                navigateToTemporaryLockFailureResultScreen()
            }
            dismissProgressDialog()
        })

        manageCardViewModel.pauseCardStates.observe(this, { currentPauseCardStates ->
            updatePauseStates(currentPauseCardStates.pauseStates)
            dismissProgressDialog()
        })
    }

    private fun updateTravelDates(travelAbroad: TravelUpdateModel?) {
        if (!travelAbroad?.referralEndDate.isNullOrEmpty() && !"0".equals(travelAbroad?.referralEndDate, ignoreCase = true) && travelAbroad?.referralStartDate.isNullOrEmpty() && !"0".equals(travelAbroad?.referralStartDate, ignoreCase = true)) {
            internationalBottomDividerView.visibility = View.VISIBLE
            internationalDividerView.visibility = View.VISIBLE
            internationalDateDescriptionTextView.visibility = View.VISIBLE
            internationalDateTextView.visibility = View.VISIBLE
            internationalDateTextView.text = DateUtils.formatTravelDate(travelAbroad?.referralStartDate, travelAbroad?.referralEndDate)
        }
    }

    override fun onClick(view: View) {
        if (pauseStates == null) {
            showGenericErrorMessage()
            return
        }

        when {
            isAllOptionsSelected() -> pauseStates?.allTransactions = PauseCardLockTypes.PLACE_HOLD_ON_CARD.key
            isSomeOptionsSelected() -> pauseStates?.allTransactions = PauseCardLockTypes.NO_HOLD_ON_CARDS.key
            else -> pauseStates?.allTransactions = PauseCardLockTypes.REMOVE_HOLD_ON_CARD.key
        }

        pauseStates?.apply {
            internationalAtmTransactions = if (enableInternationalAtmTransactionsToggleView.isChecked) PauseCardLockTypes.PLACE_HOLD_ON_ITEM.key else PauseCardLockTypes.REMOVE_HOLD_ON_ITEM.key
            internationalPointOfSaleTransactions = if (enableInternationalInStoreTransactionsToggleView.isChecked) PauseCardLockTypes.PLACE_HOLD_ON_ITEM.key else PauseCardLockTypes.REMOVE_HOLD_ON_ITEM.key
            localAtmTransactions = if (enableLocalAtmTransactionsToggleView.isChecked) PauseCardLockTypes.PLACE_HOLD_ON_ITEM.key else PauseCardLockTypes.REMOVE_HOLD_ON_ITEM.key
            localPointOfSaleTransactions = if (enableLocalInStoreTransactionsToggleView.isChecked) PauseCardLockTypes.PLACE_HOLD_ON_ITEM.key else PauseCardLockTypes.REMOVE_HOLD_ON_ITEM.key
            onlinePurchases = if (enableOnlinePurchasesToggleView.isChecked) PauseCardLockTypes.PLACE_HOLD_ON_ITEM.key else PauseCardLockTypes.REMOVE_HOLD_ON_ITEM.key
        }

        manageCardItem?.pauseStates?.cardNumber?.let { cardNumber -> pauseStates?.cardNumber = cardNumber }

        if (pauseStates != null) {
            manageCardViewModel.updatePauseCarsStates(pauseStates!!)
            analyticsTagging()
        } else {
            navigateToTemporaryLockFailureResultScreen()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.combined_cancel_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.cancel) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun analyticsTagging() {
        if (PauseCardLockTypes.PLACE_HOLD_ON_ITEM.key.equals(pauseStates?.allTransactions, ignoreCase = true)) {
            AnalyticsUtil.trackAction("Pause card", "Pause - All transactions locked")
        } else {
            AnalyticsUtil.trackAction("Pause card", "Pause - All transactions unlocked")
        }
        if (PauseCardLockTypes.PLACE_HOLD_ON_ITEM.key.equals(pauseStates?.internationalAtmTransactions, ignoreCase = true)) {
            AnalyticsUtil.trackAction("Pause card", "Pause - Int. ATM locked")
        } else {
            AnalyticsUtil.trackAction("Pause card", "Pause - Int. ATM unlocked")
        }
        if (PauseCardLockTypes.PLACE_HOLD_ON_ITEM.key.equals(pauseStates?.internationalPointOfSaleTransactions, ignoreCase = true)) {
            AnalyticsUtil.trackAction("Pause card", "Pause -  Int POS locked")
        } else {
            AnalyticsUtil.trackAction("Pause card", "Pause -  Int POS unlocked")
        }
        if (PauseCardLockTypes.PLACE_HOLD_ON_ITEM.key.equals(pauseStates?.localAtmTransactions, ignoreCase = true)) {
            AnalyticsUtil.trackAction("Pause card", "Pause - Local ATM locked")
        } else {
            AnalyticsUtil.trackAction("Pause card", "Pause - Local ATM unlocked")
        }
        if (PauseCardLockTypes.PLACE_HOLD_ON_ITEM.key.equals(pauseStates?.localPointOfSaleTransactions, ignoreCase = true)) {
            AnalyticsUtil.trackAction("Pause card", "Pause - Local POS locked")
        } else {
            AnalyticsUtil.trackAction("Pause card", "Pause - Local POS unlocked")
        }
        if (PauseCardLockTypes.PLACE_HOLD_ON_ITEM.key.equals(pauseStates?.onlinePurchases, ignoreCase = true)) {
            AnalyticsUtil.trackAction("Pause card", "Pause - Online locked")
        } else {
            AnalyticsUtil.trackAction("Pause card", "Pause - Online unlocked")
        }
        AnalyticsUtil.trackAction("Pause card", "Pause - confirm")
    }

    private fun initialiseViews() {
        toggleAllTransactions()
        enableAllTransactionsToggleView.isChecked = isAllOptionsSelected()
        confirmTemporaryLockButton.setOnClickListener(this)
        enableLocalAtmTransactionsToggleView.setOnCustomCheckChangeListener(this)
        enableInternationalAtmTransactionsToggleView.setOnCustomCheckChangeListener(this)
        enableOnlinePurchasesToggleView.setOnCustomCheckChangeListener(this)
        enableLocalInStoreTransactionsToggleView.setOnCustomCheckChangeListener(this)
        enableInternationalInStoreTransactionsToggleView.setOnCustomCheckChangeListener(this)
    }

    private fun isAllOptionsSelected(): Boolean = (enableInternationalAtmTransactionsToggleView.isChecked && enableLocalAtmTransactionsToggleView.isChecked
            && enableLocalInStoreTransactionsToggleView.isChecked && enableOnlinePurchasesToggleView.isChecked
            && enableInternationalInStoreTransactionsToggleView.isChecked)

    private fun isSomeOptionsSelected(): Boolean = (enableInternationalAtmTransactionsToggleView.isChecked || enableLocalAtmTransactionsToggleView.isChecked
            || enableLocalInStoreTransactionsToggleView.isChecked || enableOnlinePurchasesToggleView.isChecked
            || enableInternationalInStoreTransactionsToggleView.isChecked)

    private fun hasPauseCardStateChanged(): Boolean = enableInternationalAtmTransactionsToggleView.isChecked == PauseCardLockTypes.PLACE_HOLD_ON_ITEM.key.equals(pauseStates?.internationalAtmTransactions, ignoreCase = true)
            && enableInternationalInStoreTransactionsToggleView.isChecked == PauseCardLockTypes.PLACE_HOLD_ON_ITEM.key.equals(pauseStates?.internationalPointOfSaleTransactions, ignoreCase = true)
            && enableLocalAtmTransactionsToggleView.isChecked == PauseCardLockTypes.PLACE_HOLD_ON_ITEM.key.equals(pauseStates?.localAtmTransactions, ignoreCase = true)
            && enableLocalInStoreTransactionsToggleView.isChecked == PauseCardLockTypes.PLACE_HOLD_ON_ITEM.key.equals(pauseStates?.localPointOfSaleTransactions, ignoreCase = true)
            && enableOnlinePurchasesToggleView.isChecked == PauseCardLockTypes.PLACE_HOLD_ON_ITEM.key.equals(pauseStates?.onlinePurchases, ignoreCase = true)

    private fun enableAllTransactions() {
        enableLocalAtmTransactionsToggleView.isChecked = true
        enableInternationalAtmTransactionsToggleView.isChecked = true
        enableOnlinePurchasesToggleView.isChecked = true
        enableLocalInStoreTransactionsToggleView.isChecked = true
        enableInternationalInStoreTransactionsToggleView.isChecked = true
    }

    private fun disableAllTransactions() {
        enableLocalAtmTransactionsToggleView.isChecked = false
        enableInternationalAtmTransactionsToggleView.isChecked = false
        enableOnlinePurchasesToggleView.isChecked = false
        enableLocalInStoreTransactionsToggleView.isChecked = false
        enableInternationalInStoreTransactionsToggleView.isChecked = false
    }

    private fun navigateToTemporaryLockUpdatedResultScreen() {
        finish()
        startActivity(CardIntentFactory.getPauseCardResultScreen(this, R.string.pause_card_selection_updated, R.string.pause_card_selection_updated_message, isFromCreditCardHub, manageCardItem))
    }

    private fun navigateToTemporaryLockFailureResultScreen() {
        finish()
        startActivity(CardIntentFactory.getPauseCardFailureScreen(this, R.string.pause_card_update_failure, R.string.pause_card_update_failure_message, isFromCreditCardHub, manageCardItem))
    }

    private fun updatePauseStates(pauseStates: PauseStates?) {
        this.pauseStates = pauseStates
        enableLocalAtmTransactionsToggleView.isChecked = PauseCardLockTypes.PLACE_HOLD_ON_ITEM.key.equals(pauseStates?.localAtmTransactions, ignoreCase = true)
        enableInternationalAtmTransactionsToggleView.isChecked = PauseCardLockTypes.PLACE_HOLD_ON_ITEM.key.equals(pauseStates?.internationalAtmTransactions, ignoreCase = true)
        enableOnlinePurchasesToggleView.isChecked = PauseCardLockTypes.PLACE_HOLD_ON_ITEM.key.equals(pauseStates?.onlinePurchases, ignoreCase = true)
        enableLocalInStoreTransactionsToggleView.isChecked = PauseCardLockTypes.PLACE_HOLD_ON_ITEM.key.equals(pauseStates?.localPointOfSaleTransactions, ignoreCase = true)
        enableInternationalInStoreTransactionsToggleView.isChecked = PauseCardLockTypes.PLACE_HOLD_ON_ITEM.key.equals(pauseStates?.internationalPointOfSaleTransactions, ignoreCase = true)
        when {
            PauseCardLockTypes.PLACE_HOLD_ON_CARD.key.equals(pauseStates?.allTransactions, ignoreCase = true) -> {
                enableAllTransactionsToggleView.isChecked = true
                enableAllTransactions()
            }
            PauseCardLockTypes.REMOVE_HOLD_ON_CARD.key.equals(pauseStates?.allTransactions, ignoreCase = true) -> {
                enableAllTransactionsToggleView.isChecked = false
                disableAllTransactions()
            }
            else -> enableAllTransactionsToggleView.isChecked = false
        }
    }

    override fun onCustomCheckChangeListener(compoundButton: CompoundButton, isChecked: Boolean) {
        enableAllTransactionsToggleView.setOnCustomCheckChangeListener(null)
        enableAllTransactionsToggleView.isChecked = isAllOptionsSelected()
        toggleAllTransactions()
        confirmTemporaryLockButton.isEnabled = !hasPauseCardStateChanged()
    }

    private fun toggleAllTransactions() {
        enableAllTransactionsToggleView.setOnCustomCheckChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                enableAllTransactions()
            } else {
                disableAllTransactions()
            }
        }
    }
}