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

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject
import com.barclays.absa.banking.boundary.model.TransactionObject
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateUtils
import kotlinx.android.synthetic.main.prepaid_electricity_purchase_history_selected_fragment.*
import styleguide.content.BeneficiaryListItem
import styleguide.utils.extensions.insertSpaceAtIncrements
import java.util.*

const val BENEFICIARY_DETAIL_OBJECT = "beneficiaryDetailObject"

class PrepaidElectricityPurchaseHistorySelectedFragment : Fragment(R.layout.prepaid_electricity_purchase_history_selected_fragment) {
    private lateinit var prepaidElectricityView: PrepaidElectricityView

    companion object {

        private const val RECENT_DATE_INDICATOR = 31

        @JvmStatic
        fun newInstance(beneficiaryDetailObject: BeneficiaryDetailObject?) = PrepaidElectricityPurchaseHistorySelectedFragment().apply {
            arguments = Bundle().apply {
                putSerializable(BENEFICIARY_DETAIL_OBJECT, beneficiaryDetailObject)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepaidElectricityView = activity as PrepaidElectricityView
        prepaidElectricityView.setToolbarTitle(getString(R.string.prepaid_electricity_purchase_history_toolbar_title))
        arguments?.let { arguments ->
            val beneficiaryDetailObject = arguments.getSerializable(BENEFICIARY_DETAIL_OBJECT) as? BeneficiaryDetailObject
            beneficiaryDetailObject?.let { initViews(it) }
        }

        AnalyticsUtil.trackAction(PrepaidElectricityActivity.BUY_ELECTRICITY, "BuyElectricity_PurchaseHistoryScreen_ScreenDisplayed")
    }

    private fun initViews(beneficiaryDetailObject: BeneficiaryDetailObject) {
        val transactionObjects: List<TransactionObject> = beneficiaryDetailObject.transactions ?: emptyList()
        val prePaidElectricityTransactions: List<PrePaidElectricityTransaction> = transactionObjects.groupBy { transactionObject -> if (DateUtils.getDateDiff(DateUtils.getDate(transactionObject.date, "yyyy-MM-dd HH:mm:ss"), Calendar.getInstance().time) < RECENT_DATE_INDICATOR) getString(R.string.prepaid_electricity_recent) else getString(R.string.prepaid_electricity_older) }
                .flatMap { (header, transactionItems) ->
                    listOf(PrePaidElectricityTransaction.HeaderItem(header)) + transactionItems.map { PrePaidElectricityTransaction.TransactionItem(it) }
                }


        val prepaidElectricityPurchaseHistoryAdapter = PrepaidElectricityPurchaseHistoryAdapter(prePaidElectricityTransactions, object : PrepaidElectricityPurchaseHistoryAdapter.OnBeneficiaryClickListener {
            override fun onClickListener(transactionObject: TransactionObject) {
                AnalyticsUtil.trackAction(PrepaidElectricityActivity.BUY_ELECTRICITY, "BuyElectricity_PurchaseHistoryScreen_TransactionClicked")
                prepaidElectricityView.selectedHistoryTransaction = transactionObject
                prepaidElectricityView.navigateToPurchaseReceiptFragment()
            }
        })

        purchaseHistoryRecyclerView.adapter = prepaidElectricityPurchaseHistoryAdapter
        val formattedMeterNumber = beneficiaryDetailObject.beneficiaryAcctNo.insertSpaceAtIncrements(BMBConstants.INSERT_SPACE_AFTER_FOUR_DIGIT)
        beneficiaryView.setBeneficiary(BeneficiaryListItem(beneficiaryDetailObject.beneficiaryName, formattedMeterNumber, null))
    }

}