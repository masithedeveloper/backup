/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank Limited
 *
 */
package com.barclays.absa.banking.payments.swift.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.payments.swift.ui.SwiftTransactionsActivity.Companion.SWIFT
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.utils.AbsaCacheManager
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.FilterAccountList
import kotlinx.android.synthetic.main.swift_share_account_details_activity.*

class SwiftShareAccountDetailsActivity : BaseActivity(R.layout.swift_share_account_details_activity) {

    private lateinit var shareRandAccountsAdapter: SwiftShareAccountsAdapter
    private lateinit var shareCiaAccountsAdapter: SwiftShareAccountsAdapter

    private var transactionalAccounts = FilterAccountList.getTransactionalAndCreditAccounts(AbsaCacheManager.getInstance().accountsList.accountsList)
    private var ciaAccounts = AbsaCacheManager.getCiaAccounts().map { accountObject ->
        if (accountObject.currency.equals("R", true) || accountObject.currency.equals("ZAR", true)) {
            accountObject.apply {
                currency = ""
            }
        } else {
            accountObject
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBarBack(getString(R.string.swift_transactions_english_only))

        setAccountRecyclers()
    }

    private fun setAccountRecyclers() {
        shareRandAccountsAdapter = SwiftShareAccountsAdapter(object : SwiftShareAccountDetailsInterface {
            override fun onAccountSelected(account: AccountObject) {
                startActivityIfAvailable(Intent.createChooser(createShareIntent(account, false), getString(R.string.share_account_details)))
            }
        }, transactionalAccounts)
        swiftShareZarAccountDetailsRecyclerView.adapter = shareRandAccountsAdapter
        shareRandAccountsAdapter.notifyDataSetChanged()

        if (ciaAccounts.isNotEmpty()) {
            shareCiaAccountsAdapter = SwiftShareAccountsAdapter(object : SwiftShareAccountDetailsInterface {
                override fun onAccountSelected(account: AccountObject) {
                    startActivity(buildCiaCurrencyNoticeIntent(account))
                }
            }, ciaAccounts)
            swiftShareCiaAccountDetailsRecyclerView.adapter = shareCiaAccountsAdapter
            shareCiaAccountsAdapter.notifyDataSetChanged()
        } else {
            swiftShareCiaAccountDetailsRecyclerView.visibility = View.GONE
            swiftAvailableCiaAccountsLabelTextView.visibility = View.GONE
            dividerView.visibility = View.GONE
        }

    }

    private fun buildCiaCurrencyNoticeIntent(account: AccountObject): Intent? {
        GenericResultActivity.bottomOnClickListener = View.OnClickListener {
            AnalyticsUtil.trackAction(SWIFT, "Swift_ShareCiaAccountDetailsScreen_OkButtonClicked")
            BMBApplication.getInstance().topMostActivity.finish()
            startActivityIfAvailable(Intent.createChooser(createShareIntent(account, true), getString(R.string.share_account_details)))
        }
        return Intent(this, GenericResultActivity::class.java).apply {
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.ok)
            putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.swift_please_note)
            putExtra(GenericResultActivity.SUB_MESSAGE, R.string.swift_currency_notice)
            putExtra(GenericResultActivity.IS_GENERAL_ALERT, true)
        }
    }

    private fun createShareIntent(account: AccountObject, isCiaAccount: Boolean): Intent? {
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_account_details))
            putExtra(Intent.EXTRA_TEXT, getAccountDetailsToShare(account, isCiaAccount))
        }
    }

    private fun getAccountDetailsToShare(account: AccountObject, isCiaAccount: Boolean): String {
        var ciaCurrencyLabel = ""
        val accountDescription: String

        if (isCiaAccount) {
            ciaCurrencyLabel = getString(R.string.swift_account_currency_label, account.currency)
            accountDescription = getString(R.string.swift_currency_investment_account)
        } else {
            accountDescription = account.description
        }

        return getString(R.string.share_swift_account_details_content,
                CustomerProfileObject.instance.customerName,
                accountDescription,
                ciaCurrencyLabel,
                account.accountNumberFormatted)
    }
}