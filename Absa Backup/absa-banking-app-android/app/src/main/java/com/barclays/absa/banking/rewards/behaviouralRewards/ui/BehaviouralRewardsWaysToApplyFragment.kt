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
package com.barclays.absa.banking.rewards.behaviouralRewards.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import kotlinx.android.synthetic.main.behavioural_rewards_apply_fragment.*

class BehaviouralRewardsWaysToApplyFragment : BehaviouralRewardsBaseFragment(R.layout.behavioural_rewards_apply_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.behavioural_rewards_ways_to_apply))
        goToWebActionButtonView.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.absa.co.za")))
        }

        trackAnalytics("WaysToApply_ScreenDisplayed")
    }
}