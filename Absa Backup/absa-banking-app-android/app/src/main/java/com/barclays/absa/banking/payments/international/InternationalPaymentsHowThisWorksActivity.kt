/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.payments.international

import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.databinding.DataBindingUtil
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.InternationalPaymentsHowThisWorksActivityBinding
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.INTERNATIONAL_PAYMENTS
import com.barclays.absa.utils.AnalyticsUtil
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.pow

class InternationalPaymentsHowThisWorksActivity : BaseActivity() {

    private lateinit var binding: InternationalPaymentsHowThisWorksActivityBinding
    private var scrollRange: Float = 0.toFloat()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.international_payments_how_this_works_activity, null, false)
        setContentView(binding.root)
        AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_HowThisWorksScreen_ScreenDisplayed")

        toolbarSwitching()
        binding.okGotItButton.setOnClickListener {
            AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_HowThisWorksScreen_OkGotItButtonClicked")
            finish()
        }
    }

    private fun toolbarSwitching() {
        setToolBarBack(getString(R.string.how_this_works))
        binding.paymentsCanOnlyBeView.isEnabled = false
        binding.beneficiaryNeedsMoneyTransferControlNumber.isEnabled = false

        binding.appbarLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                scrollRange = binding.appbarLayout.totalScrollRange.toFloat()
                binding.appbarLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        binding.appbarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            if (verticalOffset == 0) {
                binding.inImageText.alpha = 1f
            } else {
                val invertedVerticalOffset = scrollRange + verticalOffset
                binding.inImageText.alpha = (invertedVerticalOffset / scrollRange).toDouble().pow(4.0).toFloat()
            }
        })
    }
}