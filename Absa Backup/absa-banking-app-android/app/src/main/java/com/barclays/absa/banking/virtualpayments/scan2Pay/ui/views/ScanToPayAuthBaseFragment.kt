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

import android.content.Intent
import android.view.View
import androidx.annotation.LayoutRes
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BuildConfigHelper
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.factory.ScanToPayMockFactory
import com.barclays.absa.banking.virtualpayments.scan2Pay.services.helpers.ScanToPayHelper
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.ScanToPayViewModel
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.extensions.toRandAmount
import com.entersekt.scan2pay.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import za.co.absa.scanToPay.ScanToPayDelegate
import za.co.absa.scanToPay.ScanToPayHandler

abstract class ScanToPayAuthBaseFragment(@LayoutRes layoutResId: Int) : ScanToPayBaseFragment(layoutResId), ScanToPayDelegate {

    fun requestToken() = with(scanToPayViewModel) {
        showProgressDialog()
        failureResponse = MutableLiveData()
        failureResponse.observe(viewLifecycleOwner, {
            showMessage(it.transactionStatus, it.transactionMessage) { _, _ -> navigateToHomeScreenWithoutReloadingAccounts() }
        })

        scanToPayTokenResponseLiveData = MutableLiveData()
        scanToPayTokenResponseLiveData.observe(viewLifecycleOwner, { token -> onTokenReceived(token) })
        fetchScanToPayToken()
    }

    override fun onPaymentAuthorizationRequestReceived(payment: PullPayment) {
        dismissProgressDialog()
        ScanToPayHelper.logCleanResponse(payment, scanToPayViewModel.qrCode)
        with(scanToPayViewModel) {
            paymentAuth = payment
            amount = when (payment.amount) {
                is Amount.Fixed -> {
                    isAmountEditable = false
                    (payment.amount as Amount.Fixed).fixedAmount
                }
                is Amount.InputRequired.PartialPayment -> {
                    isAmountEditable = false
                    (payment.amount as Amount.InputRequired.PartialPayment).fullAmount
                }
                is Amount.InputRequired -> {
                    isAmountEditable = true
                    0.00
                }
            }
            payForAmount = amount
            tipAmount = when (payment.tip) {
                is Tip.None -> 0.00
                is Tip.Fixed -> (payment.tip as Tip.Fixed).tipAmount
                is Tip.InputRequired -> ((payment.tip as Tip.InputRequired).percentageHint/100) * scanToPayViewModel.amount
            }
            setupDefaultAuthCard()
        }
    }

    override fun onUpdateReceived(updateInfo: Update) {
        BMBLogger.d(ScanToPayViewModel.FEATURE_NAME, "onUpdateReceived: $updateInfo")
    }

    override fun onPaymentDeclined(declinedData: PaymentResult) {
        dismissProgressDialog()
        BMBLogger.d(ScanToPayViewModel.FEATURE_NAME, "onPaymentDeclined: $declinedData")
        startActivity(getFailureIntent())
    }

    override fun onPaymentFailed(failureInfo: Cause) {
        dismissProgressDialog()
        BMBLogger.d(ScanToPayViewModel.FEATURE_NAME, "onPaymentFailed: $failureInfo")
        if (failureInfo.reasonCode == "END_USER_INPUT_DECLINED") {
            navigateToHomeScreenWithoutReloadingAccounts()
            return
        }
        startActivity(getFailureIntent())
    }

    override fun onPaymentSuccess(successData: PaymentResult) {
        dismissProgressDialog()
        BMBLogger.d(ScanToPayViewModel.FEATURE_NAME, "onPaymentSuccess: $successData")
        val successProperties = getSuccessProperties(successData.receipt.amount.toRandAmount(), "${successData.receipt.merchantName} ${successData.receipt.subMerchantName}")
        navigate(ScanToPayPaymentFragmentDirections.actionScanToPayPaymentFragmentToGenericResultScreenFragment(successProperties))
    }

    private fun onTokenReceived(token: String) {
        if (BuildConfigHelper.STUB) {
            onPaymentAuthorizationRequestReceived(ScanToPayMockFactory.getPullPaymentByUniqueCode(scanToPayViewModel.qrCode))
        } else {
            AnalyticsUtil.trackAction(ScanToPayViewModel.FEATURE_NAME, "ScanToPay_QRInputScreen_PaymentRequested")
            ScanToPayHandler.requestPayment(scanToPayViewModel.qrCode, token, this)
        }
    }

    private fun getSuccessProperties(amount: String, vendorName: String): GenericResultScreenProperties {
        hideToolBar()
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            AnalyticsUtil.trackAction(ScanToPayViewModel.FEATURE_NAME, "ScanToPay_PaymentSuccessScreen_DoneButtonClicked")
            loadAccountsAndGoHome()
        }
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.scan_to_pay_payment_success_title))
                .setDescription(getString(R.string.scan_to_pay_payment_to_vendor_description, amount, vendorName))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)
    }

    private fun getFailureIntent(): Intent {
        hideToolBar()
        GenericResultActivity.bottomOnClickListener = View.OnClickListener {
            navigateToHomeScreenWithoutReloadingAccounts()
        }
        return Intent(scanToPayActivity, GenericResultActivity::class.java).apply {
            putExtra(GenericResultActivity.IS_ERROR, true)
            putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.scan_to_pay_payment_unsuccessful_title)
            putExtra(GenericResultActivity.SUB_MESSAGE, R.string.scan_to_pay_payment_unsuccessful_description)
            putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.done)
        }
    }
}