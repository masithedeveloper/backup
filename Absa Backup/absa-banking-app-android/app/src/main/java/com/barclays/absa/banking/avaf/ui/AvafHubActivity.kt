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
package com.barclays.absa.banking.avaf.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.barclays.absa.banking.R
import com.barclays.absa.banking.accountHub.*
import com.barclays.absa.banking.avaf.documentRequest.ui.AvafDocumentRequestFragment
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.databinding.AvafHubActivityBinding
import com.barclays.absa.banking.express.accountBalances.AccountBalancesViewModel
import com.barclays.absa.banking.express.accountBalances.dto.AccountBalanceResponse
import com.barclays.absa.banking.express.avafDocumentPrevalidation.AvafDocumentPrevalidationViewModel
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.passcode.passcodeLogin.ExpressAuthenticationHelper
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.transfer.TransferConstants
import com.barclays.absa.utils.AccountHelper
import com.barclays.absa.utils.AnalyticsUtil
import styleguide.bars.StateChangedListener
import styleguide.bars.TabBarFragment

class AvafHubActivity : AccountHubBaseActivity() {
    private val binding by viewBinding(AvafHubActivityBinding::inflate)
    private val hubViewModel by viewModels<AvafHubViewModel>()
    private val accountHubViewModel by viewModels<AccountHubViewModel>()
    private val documentPrevalidationViewModel by viewModels<AvafDocumentPrevalidationViewModel>()
    private val accountBalancesViewModel by viewModels<AccountBalancesViewModel>()
    private val isVehicleFinanceAccountInformationActive: Boolean = FeatureSwitchingCache.featureSwitchingToggles.vehicleFinanceAccountInformation == FeatureSwitchingStates.ACTIVE.key
    private val isVehicleFinanceDocumentRequestActive: Boolean = FeatureSwitchingCache.featureSwitchingToggles.vehicleFinanceDocumentRequest == FeatureSwitchingStates.ACTIVE.key
    override val searchView: SearchView
        get() = binding.searchView

    companion object {
        private const val TRANSACTION_HISTORY_RANGE_DAYS = -30
        private const val AVAF_ACCOUNT_HUB = "AVAF"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AnalyticsUtil.trackAction("AbsaVehicleAssetFinance_AbsaVehicleAssetFinanceHub_ScreenDisplayed")

        setSupportActionBar(binding.toolbar)
        setToolBarBack(R.string.avaf_hub_title)
        intent.extras?.let { it ->
            val accountObject = it[ACCOUNT_OBJECT] as? AccountObject
            if (accountObject != null) {
                setupObserver()
                verifyAndInitialise(accountObject)
            } else {
                showGenericErrorMessageThenFinish()
            }
        }
    }

    private fun initialise(accountObject: AccountObject) {
        when {
            isVehicleFinanceAccountInformationActive -> {
                hubViewModel.failureLiveData.observe(this, {
                    dismissProgressDialog()
                    if (AvafConstants.RESPONSE_CODE_NO_DATA == it.statuscode) {
                        startActivity(IntentFactory.capabilityUnavailable(this, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_absa_vehicle_and_asset_finance))))
                    } else {
                        GenericResultActivity.bottomOnClickListener = View.OnClickListener { buttonView ->
                            preventDoubleClick(buttonView)
                            loadAccountsAndGoHome()
                        }

                        with(Intent(this, GenericResultActivity::class.java)) {
                            putExtra(GenericResultActivity.IS_FAILURE, true)
                            putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_cross)
                            putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.generic_error)
                            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.cancel)
                            putExtra(GenericResultActivity.CALL_US_CONTACT_NUMBER, getString(R.string.avaf_contact_number))
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            putExtra(BMBConstants.PRE_LOGIN_LAYOUT, true)
                            startActivity(this)
                        }
                    }
                })
                hubViewModel.fetchAvafAccountDetail(accountObject.accountNumber)
                hubViewModel.avafAccountDetailLiveData.observe(this, {
                    if (isVehicleFinanceDocumentRequestActive) {
                        documentPrevalidationViewModel.fetchPrevalidationRules(accountObject.accountNumber)
                        documentPrevalidationViewModel.prevalidationResponseLiveData.observe(this@AvafHubActivity, {
                            setupViews()
                        })
                    } else {
                        dismissProgressDialog()
                        setupViews()
                    }
                })
            }
            isVehicleFinanceDocumentRequestActive -> {
                documentPrevalidationViewModel.fetchPrevalidationRules(accountObject.accountNumber)
                documentPrevalidationViewModel.prevalidationResponseLiveData.observe(this@AvafHubActivity, {
                    setupViews()
                })
            }
            else -> {
                setupViews()
            }
        }
    }

    private fun setupObserver() {
        accountHubViewModel.activateSearchView.observe(this) { activateSearchView ->
            if (activateSearchView) {
                binding.collapsingAppbarView.collapseToolbar()
            }
        }
        accountHubViewModel.shouldReloadHub.observe(this, { shouldReloadHub ->
            if (shouldReloadHub) {
                accountHubViewModel.shouldReloadHub.postValue(false)
            }
        })
    }

    private fun setupViews() {
        binding.collapsingAppbarView.addHeaderView(AvafHubHeaderFragment.newInstance())
        binding.collapsingAppbarView.setAppBarState(accountHubViewModel.appBarStateExpanded)
        accountHubViewModel.initialTransactionHistoryDateRange = TRANSACTION_HISTORY_RANGE_DAYS

        val tabBars = mutableListOf<TabBarFragment>().apply {
            add(TabBarFragment(AccountHubTransactionHistoryFragment(), getString(R.string.transaction)))
        }

        if (isVehicleFinanceAccountInformationActive) {
            tabBars.add(TabBarFragment(AvafAccountInformationFragment.newInstance(getString(R.string.avaf_hub_info_tab)), getString(R.string.avaf_hub_info_tab)))
        }

        if (FeatureSwitchingCache.featureSwitchingToggles.vehicleFinanceArchivedStatements == FeatureSwitchingStates.ACTIVE.key) {
            tabBars.add(TabBarFragment(AccountStatementFragment(), getString(R.string.statements)))
        }

        if (isVehicleFinanceDocumentRequestActive) {
            tabBars.add(TabBarFragment(AvafDocumentRequestFragment.newInstance(getString(R.string.avaf_document_request_tab_title)), getString(R.string.avaf_document_request_tab_title)))
        }

        binding.collapsingAppbarView.addToolBarAndFragments(this, tabBars, object : StateChangedListener {
            override fun onStateChanged(isExpanded: Boolean) {
                accountHubViewModel.appBarStateExpanded = isExpanded
                if (isExpanded && accountHubViewModel.isSearchActive) {
                    accountHubViewModel.activateSearchView.value = false
                }
            }

            override fun onTabChanged(position: Int) {
                if (position == 0) {
                    AnalyticsUtil.trackAction(AVAF_ACCOUNT_HUB, "AVAFAccountHub_HubScreen_TransactionHistoryTabDisplayed")
                } else if (position == 1) {
                    AnalyticsUtil.trackAction(AVAF_ACCOUNT_HUB, "AVAFAccountHub_HubScreen_InformationTabDisplayed")
                }

                if (accountHubViewModel.isSearchActive) {
                    accountHubViewModel.activateSearchView.value = false
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val isTransferSuccessful = data?.getBooleanExtra(TransferConstants.IS_TRANSFER_SUCCESSFUL, false) ?: false
            val isDocumentRequestSuccessful = data?.getBooleanExtra(AvafConstants.IS_DOCUMENT_REQUEST_SUCCESSFUL, false) ?: false

            if (isTransferSuccessful || isDocumentRequestSuccessful) {
                AccountHelper.getAccountObjectForAccountNumber(accountHubViewModel.accountObject.accountNumber)?.let { cachedAccountObject ->
                    verifyAndReload(cachedAccountObject)
                }
            }
        }
    }

    private fun verifyAndInitialise(accountObject: AccountObject, retries: Int = 0) {
        val balance = accountObject.currentBalance.getAmount()
        if (!AvafConstants.BALANCE_PENDING.equals(balance, ignoreCase = true) && !AvafConstants.BALANCE_FAILED.equals(balance, ignoreCase = true) && !AvafConstants.BALANCE_NOT_AVAILABLE.equals(balance, ignoreCase = true)) {
            initialise(accountObject)
            return
        }

        var balanceRetries = retries
        if (balanceRetries++ < AvafConstants.DEFAULT_BALANCE_RETRY_MAX) {
            val finalBalanceRetries = balanceRetries
            accountBalancesViewModel.failureLiveData.observe(this, { verifyAndInitialise(accountObject, finalBalanceRetries) })
            accountBalancesViewModel.fetchAccountBalance(accountObject.accountNumber, true)
            accountBalancesViewModel.accountBalanceLiveData.observe(this, { accountBalanceResponse: AccountBalanceResponse ->
                when {
                    AvafConstants.BALANCE_PENDING.equals(accountBalanceResponse.accountBalances.balance, ignoreCase = true) -> {
                        verifyAndInitialise(accountObject, finalBalanceRetries)
                    }
                    AvafConstants.BALANCE_FAILED.equals(accountBalanceResponse.accountBalances.balance, ignoreCase = true) || AvafConstants.BALANCE_NOT_AVAILABLE.equals(accountBalanceResponse.accountBalances.balance, ignoreCase = true) -> {
                        dismissProgressDialog()
                        startActivity(IntentFactory.capabilityUnavailable(this, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_absa_vehicle_and_asset_finance))))
                    }
                    else -> {
                        ExpressAuthenticationHelper(this).updateAccountBalance(accountBalanceResponse.accountBalances)
                        val updatedAccountObject = AccountHelper.getAccountObjectForAccountNumber(accountObject.accountNumber)
                        if (updatedAccountObject != null) {
                            initialise(updatedAccountObject)
                        } else {
                            showGenericErrorMessageThenFinish()
                        }
                    }
                }
            })
        } else {
            dismissProgressDialog()
            startActivity(IntentFactory.capabilityUnavailable(this, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_absa_vehicle_and_asset_finance))))
        }
    }

    private fun verifyAndReload(accountObject: AccountObject, retries: Int = 0) {
        val balance = accountObject.currentBalance.getAmount()
        if (!AvafConstants.BALANCE_PENDING.equals(balance, ignoreCase = true) && !AvafConstants.BALANCE_FAILED.equals(balance, ignoreCase = true) && !AvafConstants.BALANCE_NOT_AVAILABLE.equals(balance, ignoreCase = true)) {
            reloadHubInfo(accountObject)
            return
        }

        var balanceRetries = retries
        if (balanceRetries++ < AvafConstants.DEFAULT_BALANCE_RETRY_MAX) {
            val finalBalanceRetries = balanceRetries
            val accountBalancesViewModel = ViewModelProvider(this).get(AccountBalancesViewModel::class.java)
            accountBalancesViewModel.failureLiveData.observe(this, { verifyAndInitialise(accountObject, finalBalanceRetries) })
            accountBalancesViewModel.fetchAccountBalance(accountObject.accountNumber, true)
            accountBalancesViewModel.accountBalanceLiveData.observe(this, { accountBalanceResponse: AccountBalanceResponse ->
                when {
                    AvafConstants.BALANCE_PENDING.equals(accountBalanceResponse.accountBalances.balance, ignoreCase = true) -> {
                        verifyAndReload(accountObject, finalBalanceRetries)
                    }
                    AvafConstants.BALANCE_FAILED.equals(accountBalanceResponse.accountBalances.balance, ignoreCase = true) || AvafConstants.BALANCE_NOT_AVAILABLE.equals(accountBalanceResponse.accountBalances.balance, ignoreCase = true) -> {
                        dismissProgressDialog()
                        startActivity(IntentFactory.capabilityUnavailable(this, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_absa_vehicle_and_asset_finance))))
                    }
                    else -> {
                        ExpressAuthenticationHelper(this).updateAccountBalance(accountBalanceResponse.accountBalances)
                        val updatedAccountObject = AccountHelper.getAccountObjectForAccountNumber(accountObject.accountNumber)
                        if (updatedAccountObject != null) {
                            reloadHubInfo(updatedAccountObject)
                        } else {
                            showGenericErrorMessageThenFinish()
                        }
                    }
                }
            })
        } else {
            dismissProgressDialog()
            startActivity(IntentFactory.capabilityUnavailable(this, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, getString(R.string.feature_switching_absa_vehicle_and_asset_finance))))
        }
    }

    private fun reloadHubInfo(accountObject: AccountObject) {
        accountHubViewModel.accountObject = accountObject
        when {
            isVehicleFinanceAccountInformationActive -> {
                hubViewModel.fetchAvafAccountDetail(accountObject.accountNumber)
                hubViewModel.avafAccountDetailLiveData.observe(this, {
                    if (isVehicleFinanceDocumentRequestActive) {
                        documentPrevalidationViewModel.fetchPrevalidationRules(accountObject.accountNumber)
                        documentPrevalidationViewModel.prevalidationResponseLiveData.observe(this@AvafHubActivity, {
                            accountHubViewModel.shouldReloadHub.value = true
                        })
                    }
                })
            }
            isVehicleFinanceDocumentRequestActive -> {
                documentPrevalidationViewModel.fetchPrevalidationRules(accountObject.accountNumber)
                documentPrevalidationViewModel.prevalidationResponseLiveData.observe(this@AvafHubActivity, {
                    accountHubViewModel.shouldReloadHub.value = true
                })
            }
        }
    }
}