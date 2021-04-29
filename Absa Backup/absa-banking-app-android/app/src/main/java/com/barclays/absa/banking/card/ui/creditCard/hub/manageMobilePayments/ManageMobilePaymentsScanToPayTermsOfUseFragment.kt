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

import android.content.Context
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
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.response.dto.ScanToPayRegistrationResponse
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.fragment_scan_to_pay_terms_of_use.*
import styleguide.screens.GenericResultScreenFragment

class ManageMobilePaymentsScanToPayTermsOfUseFragment : ManageMobilePaymentsBaseFragment(R.layout.fragment_scan_to_pay_terms_of_use) {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sureCheckDelegate = object : SureCheckDelegate(context) {
            override fun onSureCheckProcessed() {
                manageMobilePaymentsViewModel.registerScanToPayCard(manageMobilePaymentsViewModel.cardNumber, true)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        continueButton.setOnClickListener { enableScanToPay() }
    }

    private fun enableScanToPay() {
        with(manageMobilePaymentsViewModel) {
            scanToPayRegistrationResponse = MutableLiveData()
            scanToPayRegistrationResponse.observe(viewLifecycleOwner, Observer { registrationResponse -> handleRegistrationResponse(registrationResponse) })
            failureResponse = MutableLiveData()
            failureResponse.observe(viewLifecycleOwner, Observer { failureResponse ->
                dismissProgressDialog()
                showMessage(failureResponse.responseMessage, failureResponse.responseMessage, DialogInterface.OnClickListener { _, _ ->
                    findNavController().navigateUp()
                })
            })
            AnalyticsUtil.trackAction(ManageMobilePaymentsViewModel.FEATURE_NAME, "ManageMobilePayments_ManageMobilePaymentsScreen_DeActivateScanToPayToggleClicked")
            registerScanToPayCard(manageMobilePaymentsViewModel.cardNumber, true)
        }
    }

    private fun handleRegistrationResponse(registrationResponse: ScanToPayRegistrationResponse) {
        dismissProgressDialog()
        val currentActivity = activity as? BaseActivity
        sureCheckDelegate.processSureCheck(currentActivity, registrationResponse) {
            hideToolBar()
            GenericResultScreenFragment.setPrimaryButtonOnClick { currentActivity?.currentFragment?.findNavController()?.navigateUp() }
            if (BMBConstants.SUCCESS.equals(registrationResponse.transactionStatus, true) && registrationResponse.isCardUpdated) {
                navigate(ManageMobilePaymentsScanToPayTermsOfUseFragmentDirections.actionManageMobilePaymentsScanToPayTermsOfUseFragmentToUpdateSuccessResultScreenFragment(getSuccessProperties()))
            } else if (registrationResponse.transactionMessage == "INVALID CARD NUMBER") {
                navigate(ManageMobilePaymentsScanToPayTermsOfUseFragmentDirections.actionManageMobilePaymentsScanToPayTermsOfUseFragmentToUpdateFailureResultScreenFragment(getFailureProperties(true)))
            } else {
                navigate(ManageMobilePaymentsScanToPayTermsOfUseFragmentDirections.actionManageMobilePaymentsScanToPayTermsOfUseFragmentToUpdateFailureResultScreenFragment(getFailureProperties()))
            }
        }
    }
}