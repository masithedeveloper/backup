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

import android.os.Bundle
import androidx.navigation.Navigation
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.debitOrder.DebitOrderDataModel
import com.barclays.absa.banking.boundary.model.debitOrder.DebitOrderDetailsResponse
import com.barclays.absa.banking.debiCheck.ui.DebitOrdersPagerItemFragment.Companion.DEBIT_ORDER
import com.barclays.absa.banking.debiCheck.ui.DebitOrdersPagerItemFragment.Companion.DEBIT_ORDER_DATA_MODEL
import com.barclays.absa.banking.debiCheck.ui.DebitOrdersPagerItemFragment.Companion.IS_FROM_DEBIT_ORDER_HUB
import com.barclays.absa.banking.framework.BaseActivity

class DebitOrdersActivity : BaseActivity() {
    private var isFromTransactionList = false

    companion object {
        var debitOrder = DebitOrderDetailsResponse()
        var debitOrderDataModel = DebitOrderDataModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debit_orders_activity)
        val navController = Navigation.findNavController(this, R.id.debitOrderNavigationHostFragment)

        savedInstanceState?.let {
            isFromTransactionList = it.getBoolean(IS_FROM_DEBIT_ORDER_HUB, false)
            debitOrder = it.getParcelable<DebitOrderDetailsResponse>(DEBIT_ORDER) as DebitOrderDetailsResponse
            debitOrderDataModel = it.getParcelable<DebitOrderDataModel>(DEBIT_ORDER_DATA_MODEL) as DebitOrderDataModel
            return
        }

        intent.extras?.apply {
            isFromTransactionList = getBoolean(IS_FROM_DEBIT_ORDER_HUB, false)
            if (isFromTransactionList) {
                debitOrder = getParcelable<DebitOrderDetailsResponse>(DEBIT_ORDER) as DebitOrderDetailsResponse
                debitOrderDataModel = getParcelable<DebitOrderDataModel>(DEBIT_ORDER_DATA_MODEL) as DebitOrderDataModel

                if (debitOrder.actionDate.isNotEmpty()) {
                    navController.navigate(StoppedDebitOrdersFragmentDirections.actionStoppedDebitOrdersFragmentToDebitOrderTransactionDetailsFragment(debitOrder, false))
                } else {
                    showGenericErrorMessageThenFinish()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(DEBIT_ORDER, debitOrder)
        outState.putParcelable(DEBIT_ORDER_DATA_MODEL, debitOrderDataModel)
    }
}
