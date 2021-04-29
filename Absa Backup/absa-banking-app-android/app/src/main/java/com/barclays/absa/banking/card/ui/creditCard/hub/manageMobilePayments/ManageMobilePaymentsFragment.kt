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
package com.barclays.absa.banking.card.ui.creditCard.hub.manageMobilePayments

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.response.dto.ScanToPayCardNumberEnquiryResponse
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.response.dto.ScanToPayRegistrationResponse
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.ScanToPayViewModel
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.manage_mobile_payment_fragment.*
import styleguide.screens.GenericResultScreenFragment

class ManageMobilePaymentsFragment : ManageMobilePaymentsBaseFragment(R.layout.manage_mobile_payment_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sureCheckDelegate = object : SureCheckDelegate(context) {
            override fun onSureCheckProcessed() {
                manageMobilePaymentsViewModel.registerScanToPayCard(manageMobilePaymentsViewModel.cardNumber, false)
            }
        }
        manageMobilePaymentsInterface.setToolBarBack(R.string.mobile_payments_title_mobile_payments)
        showToolBar()

        if (ScanToPayViewModel.scanToPayGone() || ScanToPayViewModel.scanToPayDisabled()) {
            scanToPayToggleView.visibility = View.GONE
        } else {
            enquireScanToPay()
        }
    }

    private fun enquireScanToPay() = with(manageMobilePaymentsViewModel) {
        scanToPayCardNumberEnquiryResponse = MutableLiveData()
        scanToPayCardNumberEnquiryResponse.observe(viewLifecycleOwner, Observer { updateScanToPayState(it) })
        fetchScanToPayCardEnabledState(manageMobilePaymentsViewModel.cardNumber)
    }

    private fun updateScanToPayState(cardEnquiryResponse: ScanToPayCardNumberEnquiryResponse) {
        dismissProgressDialog()
        scanToPayToggleView.isChecked = cardEnquiryResponse.scanToPayEnquiry.scanToPayEnabled()
        scanToPayToggleView.setOnCustomCheckChangeListener { _, _ -> toggleScanToPay() }
    }

    private fun toggleScanToPay() {
        if (scanToPayToggleView.isChecked) {
            navigate(ManageMobilePaymentsFragmentDirections.actionManageMobilePaymentsFragmentToManageMobilePaymentsScanToPayTermsOfUseFragment())
            AnalyticsUtil.trackAction(ManageMobilePaymentsViewModel.FEATURE_NAME, "ManageMobilePayments_ManageMobilePaymentsScreen_ActivateScanToPayToggleClicked")
        } else {
            disableScanToPay()
        }
    }

    private fun disableScanToPay() {
        with(manageMobilePaymentsViewModel) {
            scanToPayRegistrationResponse = MutableLiveData()
            scanToPayRegistrationResponse.observe(viewLifecycleOwner, Observer { registrationResponse -> handleRegistrationResponse(registrationResponse) })
            failureResponse = MutableLiveData()
            failureResponse.observe(viewLifecycleOwner, Observer { failureResponse ->
                dismissProgressDialog()
                showMessage(failureResponse.responseMessage, failureResponse.responseMessage, DialogInterface.OnClickListener { _, _ ->
                    scanToPayToggleView.isChecked = !scanToPayToggleView.isChecked
                })
            })
            AnalyticsUtil.trackAction(ManageMobilePaymentsViewModel.FEATURE_NAME, "ManageMobilePayments_ManageMobilePaymentsScreen_DeActivateScanToPayToggleClicked")
            registerScanToPayCard(manageMobilePaymentsViewModel.cardNumber, false)
        }
    }

    private fun handleRegistrationResponse(registrationResponse: ScanToPayRegistrationResponse) {
        dismissProgressDialog()
        sureCheckDelegate.processSureCheck(activity as BaseActivity, registrationResponse) {
            hideToolBar()
            GenericResultScreenFragment.setPrimaryButtonOnClick { findNavController().navigateUp() }
            if (BMBConstants.SUCCESS.equals(registrationResponse.transactionStatus, true) && registrationResponse.isCardUpdated) {
                navigate(ManageMobilePaymentsFragmentDirections.actionManageMobilePaymentsFragmentToUpdateSuccessResultScreenFragment(getSuccessProperties()))
            } else {
                navigate(ManageMobilePaymentsFragmentDirections.actionManageMobilePaymentsFragmentToUpdateFailureResultScreenFragment(getFailureProperties()))
            }
        }
    }

    override fun onDestroyView() {
        scanToPayToggleView.setOnCustomCheckChangeListener(null)
        super.onDestroyView()
    }
}