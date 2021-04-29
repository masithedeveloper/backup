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

package com.barclays.absa.banking.debiCheck.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.barclays.absa.banking.R
import com.barclays.absa.banking.debiCheck.services.dto.DebiCheckMandateDetail
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.home.ui.IHomeCacheService
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.debicheck_pager_item_fragment.*
import styleguide.bars.FragmentPagerItem

class DebiCheckPagerItemFragment : FragmentPagerItem(R.layout.debicheck_pager_item_fragment) {
    private lateinit var debiCheckViewModel: DebiCheckViewModel
    private lateinit var hostActivity: DebiCheckActivity
    private val homeCacheService: IHomeCacheService = getServiceInterface()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as DebiCheckActivity
        debiCheckViewModel = hostActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        debiCheckViewModel.selectedTransaction = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        attachDataObserver()
        debitOrderTransactionsActionButtonView.setOnClickListener {
            tagEvent("TransactionsClicked")
            navigateToHostActivity(DebiCheckViewModel.Flow.TRANSACTIONS.name)
        }
        approvedDebitOrderActionButtonView.setOnClickListener {
            tagEvent("ApprovedClicked")
            navigateToHostActivity(DebiCheckViewModel.Flow.APPROVED.name)
        }
        suspendedDebitOrderActionButtonView.setOnClickListener {
            tagEvent("SuspendedClicked")
            navigateToHostActivity(DebiCheckViewModel.Flow.SUSPENDED.name)
        }
    }

    private fun tagEvent(tag: String) {
        AnalyticsUtil.trackAction("DebiCheck", "DebiCheck_DebiCheckHub_${tag}")
    }

    private fun navigateToHostActivity(tag: String) {
        startActivity(Intent(activity, DebiCheckHostActivity::class.java).apply {
            putExtra(DebiCheckHostActivity.CURRENT_FLOW, tag)
        })
    }

    private fun attachDataObserver() {
        debiCheckViewModel.pendingMandateResponse.observe(hostActivity, { response ->
            homeCacheService.setNumberOfPendingMandates(response.mandates.size)
            if (response.mandates.isNotEmpty()) {
                showPendingMandates(response.mandates)
            }
        })
    }

    private fun showPendingMandates(pendingMandates: List<DebiCheckMandateDetail>) {
        debiCheckPagerItemPendingLayout.visibility = View.VISIBLE
        val mandatesAdapter = PendingMandatesAdapter(requireActivity(), pendingMandates)
        pendingMandatesRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = mandatesAdapter
        }
        mandatesAdapter.notifyDataSetChanged()
    }

    override fun getTabDescription() = arguments?.getString(TAB_DESCRIPTION_KEY) ?: ""

    companion object {
        @JvmStatic
        fun newInstance(description: String): DebiCheckPagerItemFragment {
            return DebiCheckPagerItemFragment().apply {
                arguments = Bundle().apply { putString(TAB_DESCRIPTION_KEY, description) }
            }
        }
    }
}