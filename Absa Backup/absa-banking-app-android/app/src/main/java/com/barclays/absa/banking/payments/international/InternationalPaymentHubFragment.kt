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

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.barclays.absa.banking.R
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.INTERNATIONAL_PAYMENTS
import com.barclays.absa.banking.payments.international.adapters.BeneficiaryListRecyclerViewAdapter
import com.barclays.absa.banking.payments.international.adapters.InternationalItemSelectedInterface
import com.barclays.absa.banking.payments.international.data.InternationalPaymentBeneficiaryDetails
import com.barclays.absa.banking.payments.international.services.dto.WesternUnionBeneficiaryDetails
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.international_payments_hub_fragment.*

class InternationalPaymentHubFragment : InternationalPaymentsAbstractBaseFragment(R.layout.international_payments_hub_fragment), InternationalPaymentsContract.WesternUnionPaymentHubView {

    private lateinit var presenter: InternationalPaymentsContract.WesternUnionPaymentHubPresenter
    private lateinit var beneficiaryAdapter: BeneficiaryListRecyclerViewAdapter
    private lateinit var beneficiaries: ArrayList<InternationalPaymentBeneficiaryDetails>
    private var hasNoBeneficiaries = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        AnalyticsUtil.trackAction(InternationalPaymentsConstants.INTERNATIONAL_PAYMENTS, "InternationalPayments_HubScreen_ScreenDisplayed")
        setToolBar(R.string.international_payments)

        showToolBar()
        presenter = InternationalPaymentsPaymentHubPresenter(this)
        presenter.viewInstantiated()
        internationalPaymentsActivity.apply {
            isExistingBeneficiary = false
            isOnceOffPayment = false
            showOnceOffDialog = true
        }
        setHasOptionsMenu(true)

        howThisWorksButton.setOnClickListener {
            AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_HubScreen_HowDoesThisWorkOptionClicked")
            val intent = Intent(internationalPaymentsActivity, InternationalPaymentsHowThisWorksActivity::class.java)
            startActivity(intent)
        }
        paySomeoneNewButton.setOnClickListener {
            internationalPaymentsActivity.apply {
                AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_HubScreen_PaySomeoneNewOptionClicked")
                isExistingBeneficiary = false
                isOnceOffPayment = false
                flowTypeString = getString(R.string.international_payments_beneficiary)
                flowHintTypeString = getString(R.string.international_payments_beneficiaries)
            }
            validateForHolidaysAndTime()
        }
        onceOffPaymentButton.setOnClickListener {
            internationalPaymentsActivity.apply {
                AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_HubScreen_OnceOffPaymentOptionClicked")
                isOnceOffPayment = true
                flowTypeString = getString(R.string.international_payments_recipient)
                flowHintTypeString = getString(R.string.international_payments_recipients)
            }
            validateForHolidaysAndTime()
        }
    }

    private fun validateForHolidaysAndTime() = presenter.onPaySomeoneNew()

    override fun navigateToInternationalPaymentsDisclaimer() {
        navigate(InternationalPaymentHubFragmentDirections.actionInternationalPaymentHubFragmentToInternationalPaymentsDisclaimerFragment())
    }

    override fun beneficiaryListReturned(beneficiaries: ArrayList<InternationalPaymentBeneficiaryDetails>, recentBeneficiaryList: ArrayList<InternationalPaymentBeneficiaryDetails>) {
        this.beneficiaries = beneficiaries
        if (beneficiaries.isNotEmpty()) {
            beneficiaryListRecyclerView.layoutManager = LinearLayoutManager(internationalPaymentsActivity)
            beneficiaryAdapter = BeneficiaryListRecyclerViewAdapter(beneficiaries, object : InternationalItemSelectedInterface {
                override fun onBeneficiarySelected(beneficiaryObject: InternationalPaymentBeneficiaryDetails) {
                    AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_HubScreen_ExistingBeneficiaryOptionClicked")
                    presenter.fetchBeneficiary(beneficiaryObject)
                }
            })
            beneficiaryListRecyclerView.adapter = beneficiaryAdapter
        } else {
            hasNoBeneficiaries = true
            beneficiaryListRecyclerView.visibility = View.GONE
            noBeneficiariesTextView.visibility = View.VISIBLE
        }
        dismissProgressDialog()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val menuItem = menu.findItem(R.id.action_search)
        val search = menuItem.actionView as SearchView

        menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                setViewsVisibility(View.GONE)
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                setViewsVisibility(View.VISIBLE)
                listEmptyTextView.visibility = View.GONE
                return true
            }
        })

        searching(search)
        super.onPrepareOptionsMenu(menu)
    }

    private fun searching(search: SearchView) {
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { startSearch(it) }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                startSearch(newText)
                return true
            }
        })
    }

    override fun navigateToDisclaimerFragment() {
        navigate(InternationalPaymentHubFragmentDirections.actionInternationalPaymentHubFragmentToInternationalPaymentsDisclaimerFragment())
    }

    override fun saveBeneficiaryDetails(westernUnionBeneficiaryDetails: WesternUnionBeneficiaryDetails) {
        internationalPaymentsActivity.westernUnionBeneficiaryDetails = westernUnionBeneficiaryDetails
        internationalPaymentsActivity.isExistingBeneficiary = true
    }

    private fun startSearch(query: String) = filterBeneficiaryList(query.trim())

    private fun setViewsVisibility(visibility: Int) {
        divider.visibility = visibility
        paySomeoneNewButton.visibility = visibility
        howThisWorksButton.visibility = visibility
        onceOffPaymentButton.visibility = visibility
    }

    private fun filterBeneficiaryList(query: String) {
        val beneficiaryList = beneficiaries
        val beneficiaryListFiltered = ArrayList<InternationalPaymentBeneficiaryDetails>()
        if (query.isNotEmpty()) {
            for (beneficiaryObject in beneficiaryList) {
                val beneficiaryDisplayName = beneficiaryObject.beneficiaryDisplayName ?: continue
                if (beneficiaryDisplayName.contains(query, true)) {
                    beneficiaryListFiltered.add(beneficiaryObject)
                }
            }
        } else {
            beneficiaryListFiltered.addAll(beneficiaryList)
        }

        beneficiaryAdapter = BeneficiaryListRecyclerViewAdapter(beneficiaryListFiltered, query, object : InternationalItemSelectedInterface {
            override fun onBeneficiarySelected(beneficiaryObject: InternationalPaymentBeneficiaryDetails) {
                presenter.fetchBeneficiary(beneficiaryObject)
            }
        })

        if (beneficiaryListFiltered.isEmpty()) {
            listEmptyTextView.visibility = View.VISIBLE
            beneficiaryListRecyclerView.visibility = View.GONE
            noBeneficiariesTextView.visibility = View.GONE
        } else {
            listEmptyTextView.visibility = View.GONE
            beneficiaryListRecyclerView.visibility = View.VISIBLE

            if (hasNoBeneficiaries) {
                noBeneficiariesTextView.visibility = View.VISIBLE
            }
        }

        beneficiaryListRecyclerView.adapter = beneficiaryAdapter
        beneficiaryAdapter.notifyDataSetChanged()
    }

    override fun navigateToPendingTransactionScreen() {
        dismissProgressDialog()
        hideToolBar()
        internationalPaymentsActivity.hideProgressIndicator()

        val genericResultScreenProperties = InternationalPaymentsResultFactory().buildInternationalPaymentLastPaymentPendingBundle(internationalPaymentsActivity)
        navigate(InternationalPaymentHubFragmentDirections.actionInternationalPaymentHubFragmentToInternationalPaymentsResultsFragment(genericResultScreenProperties, true))
    }

    override fun navigateToGenericErrorScreen(transactionMessage: String?) {
        hideToolBar()
        internationalPaymentsActivity.hideProgressIndicator()

        var message = if ("joint".equals(transactionMessage, true)) {
            getString(R.string.international_payment_result_payment_unsuccessful_joint_account)
        } else {
            transactionMessage.toString()
        }

        if (message == "Payments/SendOrReceiveWesternUnionTransfer/Validation/CloseTime") {
            message = getString(R.string.international_payment_result_payment_notice_times)
        }

        val genericResultScreenProperties = InternationalPaymentsResultFactory().buildFicaFailureBundle(internationalPaymentsActivity, getString(R.string.international_payments_you_cannot_continue), message)
        navigate(InternationalPaymentHubFragmentDirections.actionInternationalPaymentHubFragmentToInternationalPaymentsResultsFragment(genericResultScreenProperties, true))
    }

    override fun getLifecycleCoroutineScope() = lifecycleScope

    override fun navigateToInternationalPaymentsHoursNote() {
        hideToolBar()
        internationalPaymentsActivity.hideProgressIndicator()
        val genericResultScreenProperties = InternationalPaymentsResultFactory().buildInternationalPaymentPleaseNoteBundle(internationalPaymentsActivity)
        navigate(InternationalPaymentHubFragmentDirections.actionInternationalPaymentHubFragmentToInternationalPaymentsResultsFragment(genericResultScreenProperties, true))
    }
}
