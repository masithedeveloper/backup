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
package com.barclays.absa.banking.virtualpayments.scan2Pay.ui.views

import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.barclays.absa.banking.R
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.ScanToPayViewModel
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.fragment_scan_to_pay_card_activated.*

class ScanToPayCardActivatedFragment : ScanToPayCloseBaseFragment(R.layout.fragment_scan_to_pay_card_activated) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var lastCardActivated = false
        arguments?.let {
            lastCardActivated = ScanToPayCardActivatedFragmentArgs.fromBundle(it).lastCardActivated
        }

        with(activateAnotherCardButton) {
            visibility = if (lastCardActivated) View.GONE else View.VISIBLE
            setOnClickListener {
                AnalyticsUtil.trackAction(ScanToPayViewModel.FEATURE_NAME, "ScanToPay_CardActivationScreen_ActivateAnotherCardNowButtonClicked")
                navigate(ScanToPayCardActivatedFragmentDirections.actionScanToPayCardActivatedFragmentToScanToPayCardActivationListFragment(true))
            }
        }
        doneButton.setOnClickListener {
            scanToPayViewModel.scanToPayTokenResponseLiveData = MutableLiveData()
            scanToPayViewModel.scanToPayTokenResponseLiveData.observe(viewLifecycleOwner, Observer {
                dismissProgressDialog()
                AnalyticsUtil.trackAction(ScanToPayViewModel.FEATURE_NAME, "ScanToPay_CardActivationScreen_DoneButtonClicked")
                navigate(ScanToPayCardActivatedFragmentDirections.actionScanToPayCardActivatedFragmentToScanToPayScanningFragment())
            })
            scanToPayViewModel.fetchScanToPayToken()
        }
    }
}