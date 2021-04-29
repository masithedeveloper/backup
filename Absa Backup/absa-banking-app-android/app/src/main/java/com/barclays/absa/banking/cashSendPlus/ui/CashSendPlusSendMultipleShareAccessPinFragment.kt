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
package com.barclays.absa.banking.cashSendPlus.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.utils.DateUtils
import kotlinx.android.synthetic.main.cash_send_plus_send_multiple_share_access_pin_fragment.*
import java.util.*

class CashSendPlusSendMultipleShareAccessPinFragment : BaseFragment(R.layout.cash_send_plus_send_multiple_share_access_pin_fragment) {
    private lateinit var cashSendPlusSendMultipleSharePinAdapter: CashSendPlusSendMultipleSharePinAdapter
    private val selectedBeneficiaryPaymentDetails = appCacheService.getCashSendPlusSendMultipleBeneficiariesPaymentDetails().toMutableList()
    private lateinit var cashSendPlusSendMultipleActivity: CashSendPlusSendMultipleActivity
    private val cashSendPlusViewModel by activityViewModels<CashSendPlusViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        cashSendPlusSendMultipleActivity = context as CashSendPlusSendMultipleActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cashSendPlusSendMultipleSharePinAdapter = CashSendPlusSendMultipleSharePinAdapter(selectedBeneficiaryPaymentDetails)
        cashSendPlusPaymentListRecyclerView.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = cashSendPlusSendMultipleSharePinAdapter
        }

        val date = DateUtils.format(Date(), DateUtils.DATE_DISPLAY_PATTERN_FULL)
        val time = DateUtils.format(Date(), DateUtils.TWELVE_HOUR_TIME_PATTERN).toLowerCase(BMBApplication.getApplicationLocale())
        transactionDateTimeTextView.text = getString(R.string.cash_send_plus_transaction_date_time, date, time)

        doneCashSendPlusSendMultipleButton.setOnClickListener {
            cashSendPlusViewModel.sendCheckCashSendPlusRegistration()

            if (cashSendPlusViewModel.checkCashSendPlusRegistrationStatusResponse.value == null) {
                cashSendPlusViewModel.failureResponse.observe(requireActivity(), {
                    dismissProgressDialog()
                    continueWithDoneAction()
                })

                cashSendPlusViewModel.checkCashSendPlusRegistrationStatusResponse.observe(requireActivity(), {
                    dismissProgressDialog()
                    appCacheService.setCashSendPlusRegistrationStatus(it)
                    continueWithDoneAction()
                })
            } else {
                continueWithDoneAction()
            }
        }
    }

    private fun continueWithDoneAction() {
        val hasSharedAllAccessPin = selectedBeneficiaryPaymentDetails.none { !it.isAccessPinShared }
        if (hasSharedAllAccessPin) {
            cashSendPlusSendMultipleActivity.finish()
        } else {
            cashSendPlusSendMultipleActivity.showPinNotSharedDialog()
        }
    }
}