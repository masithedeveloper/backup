/*
 * Copyright (c) 2018. Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclose
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied or distributed other than on a
 * need-to-know basis and any recipients may be required to sign a confidentiality undertaking in favor of Absa Bank Limited
 */
package com.barclays.absa.banking.settings.ui

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import com.barclays.absa.banking.R
import com.barclays.absa.banking.account.services.dto.ManageAccounts
import com.barclays.absa.banking.account.ui.ManageAccountsPresenter
import com.barclays.absa.banking.databinding.ManageAccountsActivityBinding
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.presentation.shared.IntentFactoryGenericResult
import com.barclays.absa.banking.presentation.shared.widget.AccountReorderCallbackHelper
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog.showYesNoDialog
import com.barclays.absa.utils.AnalyticsUtil
import com.google.android.material.snackbar.Snackbar
import java.lang.ref.WeakReference

class ManageAccountsActivity : BaseActivity(), ManageAccountsView, OnStartDragListener {
    private lateinit var presenter: ManageAccountsPresenter
    private lateinit var binding: ManageAccountsActivityBinding
    lateinit var adapter: ManageAccountsAdapter
    private lateinit var touchHelper: ItemTouchHelper
    private var accountOrderChanged: Boolean = false

    private val sureCheckDelegate: SureCheckDelegate = object : SureCheckDelegate(this) {

        override fun onSureCheckProcessed() {
            presenter.requestAccountStateUpdate()
        }

        override fun onSureCheckCancelled() {
            onSaveChangesCancelled()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.manage_accounts_activity, null, false)
        setContentView(binding.root)

        bindPresenter()
        setToolBarBack(getString(R.string.title_manage_accounts))

        binding.saveButton.setOnClickListener {
            if (::adapter.isInitialized) {
                presenter.onSaveStateClicked(ArrayList(adapter.getAccounts()), accountOrderChanged)
                tagEvent()
            }
        }
        AnalyticsUtil.trackAction("Manage Accounts", "Manage Accounts Screen")
    }

    private fun tagEvent() {
        if (presenter.filterUnChangedAccountsOut().isNotEmpty()) {
            AnalyticsUtil.trackAction("Manage Accounts", "ManageAccount_ManageAccountScreen_ShowAndHideToggleClicked")
        }
        if (accountOrderChanged) {
            AnalyticsUtil.trackAction("Manage Accounts", "ManageAccount_ManageAccountScreen_ReorderAccountButtonClicked")
        }
    }

    override fun bindPresenter() {
        presenter = ManageAccountsPresenter(WeakReference(this), sureCheckDelegate)
        presenter.onViewLoaded()
    }

    override fun displayListOnView(accountsList: List<ManageAccounts>) {
        binding.manageAccountsRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.manageAccountsRecyclerView.setHasFixedSize(true)
        adapter = ManageAccountsAdapter(this, accountsList, this)

        val callback = AccountReorderCallbackHelper(adapter)
        touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.manageAccountsRecyclerView)
        binding.manageAccountsRecyclerView.adapter = adapter
    }

    override fun onSaveChangesSuccess() {
        val successIntent = IntentFactoryGenericResult.getSuccessfulResultBuilder(this)
                .setGenericResultHeaderMessage(getString(R.string.manage_accounts_changes_saved))
                .setGenericResultBottomButton(R.string.done) {
                    loadAccountsClearingAccountProfileAndShowHomeScreen()
                }
                .build()
        successIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivityIfAvailable(successIntent)
    }

    override fun onOrderSaveChangesFailure() {
        val failureIntent = IntentFactoryGenericResult.getFailureResultBuilder(this)
                .setGenericResultHeaderMessage(R.string.manage_accounts_changes_save_failure_title)
                .setGenericResultSubMessage(R.string.manage_accounts_changes_save_failure_content)
                .setGenericResultBottomButton(R.string.done) { loadAccountsClearingAccountProfileAndShowHomeScreen() }
                .build()
        failureIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivityIfAvailable(failureIntent)
    }

    override fun onTechnicalDifficulty() {
        val technicalDifficulty = IntentFactoryGenericResult.getFailureResultBuilder(this)
                .setGenericResultHeaderMessage(R.string.unable_to_continue)
                .setGenericResultSubMessage(R.string.manage_accounts_unable_to_continue_failure_body)
                .setGenericResultBottomButton(R.string.done) {
                    navigateToHomeScreenWithoutReloadingAccounts()
                }
                .build()
        technicalDifficulty.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivityIfAvailable(technicalDifficulty)
    }

    override fun onAccountLinkingFailure(numberOfNominatedAccounts: Int, nominatedAccountNumbers: String) {
        val accountLinkingFailure = IntentFactoryGenericResult.getFailureResultBuilder(this)
                .setGenericResultHeaderMessage(R.string.manage_accounts_unable_to_save_changes)
                .setGenericResultSubMessage(buildNominatedAccountResponseMessage(numberOfNominatedAccounts, nominatedAccountNumbers))
                .setGenericResultBottomButton(R.string.done) {
                    navigateToHomeScreenWithoutReloadingAccounts()
                }
                .build()
        accountLinkingFailure.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivityIfAvailable(accountLinkingFailure)
    }

    override fun onAccountLinkingReorderFailure(numberOfNominatedAccounts: Int, nominatedAccountNumbers: String) {
        val accountLinkingFailure = IntentFactoryGenericResult.getFailureResultBuilder(this)
                .setGenericResultHeaderMessage(R.string.manage_accounts_unable_to_save_changes)
                .setGenericResultSubMessage(buildNominatedAccountResponseMessage(numberOfNominatedAccounts, nominatedAccountNumbers) + getString(R.string.manage_accounts_accounts_cant_be_hidden_order_failure))
                .setGenericResultBottomButton(R.string.done) {
                    navigateToHomeScreenWithoutReloadingAccounts()
                }
                .build()
        accountLinkingFailure.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivityIfAvailable(accountLinkingFailure)
    }

    override fun onSaveChangesCancelled() {
        val cancelChangesIntent = IntentFactoryGenericResult.getFailureResultBuilder(this)
                .setGenericResultHeaderMessage(R.string.unable_to_continue)
                .setGenericResultSubMessage(R.string.surecheck_cancelled_description)
                .setGenericResultBottomButton(R.string.done) {
                    navigateToHomeScreenWithoutReloadingAccounts()
                }
                .build()
        cancelChangesIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivityIfAvailable(cancelChangesIntent)
    }

    override fun onBackPressed() {
        if (::adapter.isInitialized) {
            presenter.onBackClicked(adapter.getAccounts(), accountOrderChanged)
        } else {
            super.onBackPressed()
        }
    }

    override fun showBackDialog() {
        showYesNoDialog(AlertDialogProperties.Builder()
                .title(getString(R.string.manage_accounts_cancel_changes_title))
                .message(getString(R.string.manage_accounts_cancel_changes_content))
                .positiveDismissListener { _, _ -> navigateToMenuFragment() })
    }

    override fun onStartDrag(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        accountOrderChanged = true
        touchHelper.startDrag(viewHolder)
    }

    override fun navigateToMenuFragment() {
        AnalyticsUtil.trackAction("Navigate back")
        finish()
    }

    override fun navigateToHomeScreenAndShowAccountsList() {
        AnalyticsUtil.trackAction("Navigate to home screen")
        super.navigateToHomeScreenAndShowAccountsList()
    }

    override fun navigateToGeneralFailureScreen() {
        val failureIntent = IntentFactoryGenericResult.getFailureResultBuilder(this)
                .setGenericResultHeaderMessage(R.string.manage_accounts_unable_to_continue_failure_title)
                .setGenericResultSubMessage(R.string.manage_accounts_unable_to_continue_failure_body)
                .setGenericResultBottomButton(R.string.done) {
                    navigateToHomeScreenWithoutReloadingAccounts()
                }
                .build()
        failureIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivityIfAvailable(failureIntent)
    }

    override fun showNoStatesUpdated() = Snackbar.make(binding.containerConstraintLayout, getString(R.string.manage_accounts_no_changes_was_made), Snackbar.LENGTH_LONG).show()

    override fun showAddMoreAccountsScreen() {
        val addMoreAccounts = IntentFactoryGenericResult.getCustomResultBuilder(this, "add_more_accounts.json")
                .setGenericResultHeaderMessage(R.string.manage_accounts_add_accounts_title)
                .setGenericResultSubMessage(getString(R.string.manage_accounts_add_accounts_content))
                .setGenericResultBottomButton(R.string.done) {
                    loadAccountsAndGoHome()
                }
                .build()
        addMoreAccounts.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivityIfAvailable(addMoreAccounts)
    }

    private fun buildNominatedAccountResponseMessage(numberOfNominatedAccounts: Int, nominatedAccountNumbers: String): String {
        return when {
            numberOfNominatedAccounts == 1 -> getString(R.string.manage_accounts_accounts_cant_be_hidden_failure, nominatedAccountNumbers)
            numberOfNominatedAccounts > 1 -> getString(R.string.manage_accounts_accounts_cant_be_hidden_failure_single_account, nominatedAccountNumbers)
            else -> getString(R.string.manage_accounts_accounts_cant_be_hidden_failure, "-")
        }
    }

    companion object {
        const val NOMINATED_ACCOUNT_CODE = "16"
    }
}