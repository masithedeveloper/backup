/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.card.ui.creditCardHotLeads

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.utils.KeyboardUtils
import com.barclays.absa.utils.ViewAnimation
import kotlinx.android.synthetic.main.hot_leads_host_activity.*
import styleguide.widgets.OfferBannerView

class HotLeadsHostActivity : BaseActivity() {
    private lateinit var navHostFragment: Fragment
    private lateinit var toolbarAnimation: ViewAnimation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hot_leads_host_activity)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.hotLeadNavHostFragment)!!
        setUpToolbar()
    }

    override fun onBackPressed() {
        KeyboardUtils.hideKeyboard(this)
        val navController = findNavController(R.id.hotLeadNavHostFragment)
        if (navController.currentDestination?.id == R.id.hotLeadsContactDetailsFragment) {
            hideToolbar()
        }
        if (navController.currentDestination?.id != R.id.hotLeadsResultScreenFragment) {
            super.onBackPressed()
        }
    }

    private fun setUpToolbar() {
        toolbar.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                toolbarAnimation = ViewAnimation(toolbar)
                toolbarAnimation.collapseView(0)
                toolbar.viewTreeObserver?.removeOnGlobalLayoutListener(this)
            }
        })

        setSupportActionBar(toolbar as Toolbar?)
    }

    fun showToolbarBackArrow() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun showToolbar() {
        if (toolbar.visibility != View.VISIBLE) {
            toolbarAnimation.expandView(OfferBannerView.ANIMATION_DURATION)
        }
        showToolbarBackArrow()
    }

    fun hideToolbar() {
        if (toolbar.visibility != View.GONE) {
            toolbarAnimation.collapseView(OfferBannerView.ANIMATION_DURATION)
        }
    }

    fun setToolbarTitle(title: String) {
        setToolBarBack(title) { onBackPressed() }
    }
}