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

package com.barclays.absa.banking.payments

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.BeneficiaryImportCouldNotReadActivityBinding
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.payments.BeneficiaryImportBaseActivity.Companion.EXTRA_CAMERA_IMPORT

class BeneficiaryImportFailureActivity : BaseActivity() {

    private lateinit var binding: BeneficiaryImportCouldNotReadActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.inflate(layoutInflater, R.layout.beneficiary_import_could_not_read_activity, null, false)
        setContentView(binding.root)
        binding.root.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        val isCameraError = intent.getBooleanExtra(EXTRA_CAMERA_IMPORT, false)

        if (!isCameraError) {
            binding.bullet0.visibility = View.GONE
            binding.bullet1.visibility = View.GONE
            binding.bullet2.visibility = View.GONE
            binding.bullet3.visibility = View.GONE
        }

        binding.tryAgainButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onBackPressed() {
        loadAccountsAndGoHome()
    }
}
