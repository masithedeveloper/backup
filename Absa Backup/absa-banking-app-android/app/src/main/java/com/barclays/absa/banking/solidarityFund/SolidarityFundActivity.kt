/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.solidarityFund

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.core.text.bold
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.SolidarityFundBankingFeesActivityBinding
import com.barclays.absa.banking.framework.BaseActivity
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

class SolidarityFundActivity : BaseActivity(R.layout.solidarity_fund_banking_fees_activity) {
    private val binding by viewBinding(SolidarityFundBankingFeesActivityBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpToolbar()
        setOnClickListeners()
    }

    private fun setUpToolbar() {
        binding.fragmentToolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_white)
            visibility = View.VISIBLE
            title = getString(R.string.solidarity_fund_banking_fees_title)
            setNavigationOnClickListener {
                onBackPressed()
            }
        }

        binding.appbarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            binding.reducedFeesTextView.alpha = 1 - abs(verticalOffset).toFloat() / binding.appbarLayout.totalScrollRange
        })

        binding.descriptionTextView.text = boldText(getString(R.string.solidarity_fund_low_fees_from_1_March_description), "1 ${getString(R.string.March)}")
    }

    private fun boldText(text: String, textToBold: String): SpannableStringBuilder {
        val lastIndexOf = text.lastIndexOf(textToBold)
        return if (lastIndexOf > 0) SpannableStringBuilder().append(text.substring(0, lastIndexOf)).bold { append(textToBold) }.append(text.substring(lastIndexOf + textToBold.length)) else SpannableStringBuilder(text)
    }

    private fun setOnClickListeners() {
        binding.viewRatesAndFeesButton.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.absa.co.za/rates-and-fees/")))
        }

        binding.exploreAccountsButton.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.absa.co.za/personal/bank/an-account/explore/")))
        }
    }
}