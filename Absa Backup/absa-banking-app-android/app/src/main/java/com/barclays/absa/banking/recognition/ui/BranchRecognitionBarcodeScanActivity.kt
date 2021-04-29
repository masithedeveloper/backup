/*
 * Copyright (c) 2019. Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied or distributed other than on a
 * need-to-know basis and any recipients may be required to sign a confidentiality undertaking in favor of Absa Bank Limited
 */

package com.barclays.absa.banking.recognition.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.BranchRatingBarcodeScanActivityBinding
import com.barclays.absa.banking.framework.BaseActivity
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class BranchRecognitionBarcodeScanActivity : BaseActivity() {

    private lateinit var captureManager: CaptureManager
    private lateinit var barcodeScannerView: DecoratedBarcodeView
    private lateinit var binding: BranchRatingBarcodeScanActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.branch_rating_barcode_scan_activity, null, false)
        setContentView(binding.root)
        setToolBarBack(getString(R.string.branch_help_scan_qr_code_toolbar))
        initViews(savedInstanceState)
    }

    private fun initViews(savedInstanceState: Bundle?) {
        barcodeScannerView = binding.qrBacodeScannerView
        captureManager = CaptureManager(this, barcodeScannerView)
        captureManager.initializeFromIntent(intent, savedInstanceState)
        captureManager.decode()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        captureManager.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(Intents.Scan.RESULT, "no-value-detected")
        setResult(Activity.RESULT_CANCELED, intent)
        super.onBackPressed()
    }
}