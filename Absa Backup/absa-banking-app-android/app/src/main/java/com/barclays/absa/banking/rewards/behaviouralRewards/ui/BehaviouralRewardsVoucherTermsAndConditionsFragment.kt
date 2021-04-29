/*
 *  Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.rewards.behaviouralRewards.ui

import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.core.text.HtmlCompat
import com.barclays.absa.banking.R
import com.barclays.absa.banking.rewards.behaviouralRewards.ui.dto.CustomerHistoryVoucher
import kotlinx.android.synthetic.main.behavioural_rewards_voucher_terms_and_conditions_fragment.*

class BehaviouralRewardsVoucherTermsAndConditionsFragment : BehaviouralRewardsBaseFragment(R.layout.behavioural_rewards_voucher_terms_and_conditions_fragment) {
    private lateinit var customerHistoryVoucher: CustomerHistoryVoucher

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.behavioural_rewards_challenge_terms_and_conditions_terms_and_conditions))
        arguments?.let {
            customerHistoryVoucher = BehaviouralRewardsVoucherDetailsFragmentArgs.fromBundle(it).customerHistoryVoucher
        }
        val byteValue = Base64.decode(customerHistoryVoucher.termsAndConditions, Base64.DEFAULT)
        termsAndConditionsTextView.text = HtmlCompat.fromHtml(String(byteValue), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}