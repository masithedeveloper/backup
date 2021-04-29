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
import android.view.View
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.debiCheck.ui.DebitOrdersActivity.Companion.debitOrder
import com.barclays.absa.banking.debiCheck.ui.DebitOrdersActivity.Companion.debitOrderDataModel
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.debit_order_stopped_information_fragment.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties

class StoppedDebitOrderInformationFragment : DebitOrderBaseFragment(R.layout.debit_order_stopped_information_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.debit_order_information_title)
        showToolBar()

        debitOrderDataModel.apply {
            actionDate = debitOrder.actionDate
            userReference = debitOrder.userReference
            amount = debitOrder.amount
            stopPaymentType = debitOrder.stopPaymentType
            debitOrderStatus = debitOrder.debitOrderStatus
            userCode = debitOrder.userCode
            userReference = debitOrder.userReference
            tiebNumber = debitOrder.tiebNumber
            instructionNumber = debitOrder.instructionNumber
            userSequence = debitOrder.userSequence
        }

        stopDebitOrderButton.setOnClickListener {
            debitOrderViewModel.stopDebitOrder(debitOrderDataModel)
            debitOrderViewModel.stopDebitOrderResponse.observe(viewLifecycleOwner, {
                dismissProgressDialog()
                hideToolBar()
                val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                        .setResultScreenAnimation(ResultAnimations.generalSuccess)
                        .setTitle(getString(R.string.debit_order_stopped))
                        .setDescription(getString(R.string.debit_order_stopped_success_message))
                        .setSecondaryButtonLabel(getString(R.string.reverse_payment))
                        .setPrimaryButtonLabel(getString(R.string.done))
                        .build(true)

                GenericResultScreenFragment.setPrimaryButtonOnClick { debitOrdersActivity.finish() }
                GenericResultScreenFragment.setSecondaryButtonOnClick {
                    findNavController().navigate(R.id.action_debitOrderResultsFragment_to_reverseDebitOrderInformationFragment)
                }

                navigate(StoppedDebitOrderInformationFragmentDirections.actionStoppedDebitOrderInformationFragmentToDebitOrderResultsFragment(resultScreenProperties, true))
                AnalyticsUtil.trackAction("Debit Orders", "DebitOrder_TransactionSuccessfullyStoppedScreen_ScreenDisplayed")
            })
        }
    }
}
