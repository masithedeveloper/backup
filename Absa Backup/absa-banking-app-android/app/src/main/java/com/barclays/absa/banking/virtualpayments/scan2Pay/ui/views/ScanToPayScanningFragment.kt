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

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.ScanToPayActivity
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.ScanToPayViewModel
import com.barclays.absa.utils.AnalyticsUtil
import com.entersekt.scan2pay.PullPayment
import com.google.zxing.BarcodeFormat
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.android.synthetic.main.fragment_scan_to_pay_scanning.*

class ScanToPayScanningFragment : ScanToPayAuthBaseFragment(R.layout.fragment_scan_to_pay_scanning) {

    private lateinit var captureManager: CaptureManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.qr_payments_menu_item)
        showToolBar()
        captureManager = CustomCaptureManager(requireActivity(), qrScannerDecoratedBarcodeView, object : CustomCaptureManager.ResultInterceptor {
            override fun onCapturedResult(result: String?) {
                result?.let { handleScannedResult(it) }
            }
        })

        captureManager.initializeFromIntent(IntentIntegrator(requireActivity()).apply {
            setDesiredBarcodeFormats(listOf(BarcodeFormat.QR_CODE.toString()))
            setOrientationLocked(true)
            setBeepEnabled(false)
            setPrompt("")
            captureActivity = ScanToPayActivity::class.java
        }.createScanIntent(), null)
        captureManager.decode()

        uniqueCodeButton.setOnClickListener {
            AnalyticsUtil.trackAction(ScanToPayViewModel.FEATURE_NAME, "ScanToPay_ScanQRCodeScreen_EnterUniqueCodeButtonClicked")
            navigate(ScanToPayScanningFragmentDirections.actionScanToPayScanningFragmentToScanToPayUniqueCodeFragment())
        }
    }

    override fun onPaymentAuthorizationRequestReceived(payment: PullPayment) {
        super.onPaymentAuthorizationRequestReceived(payment)
        navigate(ScanToPayScanningFragmentDirections.actionScanToPayScanningFragmentToScanToPayPaymentFragment())
    }

    private fun handleScannedResult(result: String) {
        scanToPayViewModel.qrCode = result
        requestToken()
    }

    override fun onResume() {
        super.onResume()
        captureManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        captureManager.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        captureManager.onDestroy()
    }

    class CustomCaptureManager(activity: Activity, decoratedBarcodeView: DecoratedBarcodeView, private val resultInterceptor: ResultInterceptor)
        : CaptureManager(activity, decoratedBarcodeView) {

        interface ResultInterceptor {
            fun onCapturedResult(result: String?)
        }

        override fun returnResult(rawResult: BarcodeResult?) {
            val intent = resultIntent(rawResult, null)
            val result = intent.getStringExtra("SCAN_RESULT")
            resultInterceptor.onCapturedResult(result)
        }
    }
}