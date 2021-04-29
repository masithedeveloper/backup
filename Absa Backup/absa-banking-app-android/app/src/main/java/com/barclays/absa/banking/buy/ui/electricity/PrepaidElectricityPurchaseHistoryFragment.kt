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
package com.barclays.absa.banking.buy.ui.electricity

import android.content.Context
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.BeneficiaryListObject
import com.barclays.absa.banking.boundary.model.BeneficiaryObject
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.presentation.shared.GenericGroupingBeneficiaryAdapter
import com.barclays.absa.banking.presentation.shared.GenericRecentBeneficiaryAdapter
import com.barclays.absa.banking.presentation.shared.NoScrollLinearLayoutManager
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.prepaid_electricity_purchase_history_beneficiaries_fragment.*

class PrepaidElectricityPurchaseHistoryFragment : BaseFragment(R.layout.prepaid_electricity_purchase_history_beneficiaries_fragment) {
    private lateinit var prepaidElectricityView: PrepaidElectricityView

    companion object {
        private const val BENEFICIARY_LIST_OBJECT = "beneficiaryListObject"

        @JvmStatic
        fun newInstance(beneficiaryListObject: BeneficiaryListObject?) = PrepaidElectricityPurchaseHistoryFragment().apply {
            arguments = Bundle().apply {
                putSerializable(BENEFICIARY_LIST_OBJECT, beneficiaryListObject)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        prepaidElectricityView = context as PrepaidElectricityView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireArguments().apply {
            setToolBar(getString(R.string.prepaid_electricity_beneficiary_history))
            val beneficiaryListObject = getSerializable(BENEFICIARY_LIST_OBJECT) as BeneficiaryListObject?
            beneficiaryListObject?.let { initViews(it) }
        }

        AnalyticsUtil.trackAction(PrepaidElectricityActivity.BUY_ELECTRICITY, "BuyElectricity_BeneficiaryHistoryScreen_ScreenDisplayed")
    }

    private fun initViews(beneficiaryListObject: BeneficiaryListObject) {
        val latestTransactionsBeneficiaryList: List<BeneficiaryObject?> = beneficiaryListObject.latestTransactionBeneficiaryList ?: ArrayList()
        val recentTransactionBeneficiaryList: MutableList<BeneficiaryObject?> = ArrayList()

        recentTransactionBeneficiaryList.addAll(latestTransactionsBeneficiaryList.take(3))

        if (latestTransactionsBeneficiaryList.isEmpty()) {
            blankStateAnimationView.visibility = View.VISIBLE
            purchaseHistoryRecyclerView.visibility = View.GONE
            beneficiaryTertiaryContentAndLabelView.setContentText(getString(R.string.no_purchase_history))
        } else {
            val electricityBeneficiaryAdapter = GenericGroupingBeneficiaryAdapter(latestTransactionsBeneficiaryList) { beneficiaryObject: BeneficiaryObject? ->
                prepaidElectricityView.getMeterHistory(beneficiaryObject)
                AnalyticsUtil.trackAction(PrepaidElectricityActivity.BUY_ELECTRICITY, "BuyElectricity_BeneficiaryHistoryScreen_BeneficiaryClicked")
            }

            blankStateAnimationView.visibility = View.GONE
            with(purchaseHistoryRecyclerView) {
                visibility = View.VISIBLE
                layoutManager = NoScrollLinearLayoutManager(context)
                adapter = electricityBeneficiaryAdapter
            }
        }
        if (latestTransactionsBeneficiaryList.isEmpty()) {
            recentElectricityPurchasesRecyclerView.visibility = View.GONE
            recentPurchasesHeadingView.visibility = View.GONE
            dividerView2.visibility = View.GONE
        } else {
            val recentTransactionsBeneficiaryAdapter = GenericRecentBeneficiaryAdapter(recentTransactionBeneficiaryList) { beneficiaryObject: BeneficiaryObject? -> prepaidElectricityView.getMeterHistory(beneficiaryObject) }
            with(recentElectricityPurchasesRecyclerView) {
                visibility = View.VISIBLE
                layoutManager = NoScrollLinearLayoutManager(context)
                adapter = recentTransactionsBeneficiaryAdapter
            }
        }
    }
}