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
 */
package com.barclays.absa.banking.rewards.behaviouralRewards.ui

import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.shared.OnBackPressedInterface
import kotlinx.android.synthetic.main.behavioural_rewards_activity.*
import styleguide.screens.GenericResultScreenFragment

class BehaviouralRewardsActivity : BaseActivity(R.layout.behavioural_rewards_activity) {

    var appBarStateExpanded: Boolean = true
    var isSearchActive: Boolean = false

    override fun onBackPressed() {
        if (currentFragment !is OnBackPressedInterface || !(currentFragment as OnBackPressedInterface).onBackPressed()) {
            when (currentFragment) {
                is GenericResultScreenFragment -> {
                    // Do Nothing
                }
                is BehaviouralRewardsHubFragment -> finish()
                else -> super.onBackPressed()
            }
        }
    }

    fun hideRewardsToolBarSeparator() {
        toolbarDividerView.visibility = View.GONE
    }

    fun showRewardsToolBarSeparator() {
        toolbarDividerView.visibility = View.VISIBLE
    }

    fun getSearchView(): SearchView = simpleSearchView

    val hubToolbar: Toolbar
        get() {
            return toolbar
        }

    fun resetAppBarState() {
        appBarStateExpanded = true
    }
}