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
package com.barclays.absa.banking.relationshipBanking.ui

import android.content.Context
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.view.ViewTreeObserver
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.newToBank.EvolveProductPagerItemFragment
import com.barclays.absa.banking.newToBank.NewToBankBulletAdapter
import com.barclays.absa.banking.newToBank.NewToBankConstants.BUSINESS_EVOLVE_ACCOUNT
import com.barclays.absa.banking.newToBank.NewToBankConstants.BUSINESS_EVOLVE_ISLAMIC_ACCOUNT
import com.barclays.absa.banking.newToBank.NewToBankView
import com.barclays.absa.banking.newToBank.services.dto.BusinessEvolveCardPackageResponse
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import kotlinx.android.synthetic.main.new_to_bank_business_choose_product_fragment.*
import styleguide.bars.FragmentPagerItem
import styleguide.bars.TabPager
import styleguide.utils.ViewUtils
import kotlin.math.pow

class BusinessChooseYourProductFragment : BaseFragment(R.layout.new_to_bank_business_choose_product_fragment) {
    private lateinit var newToBankBulletAdapter: NewToBankBulletAdapter
    private lateinit var newToBankView: NewToBankView

    private var businessEvolveSelectedPackage: BusinessEvolveCardPackageResponse = BusinessEvolveCardPackageResponse()
    private var scrollRange: Float = 0.0F
    private var density: Float = 0.0F

    companion object {
        @JvmStatic
        fun newInstance(): BusinessChooseYourProductFragment = BusinessChooseYourProductFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        newToBankView = context as NewToBankView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newToBankBulletAdapter = NewToBankBulletAdapter(newToBankView)
        businessEvolveSelectedPackage = newToBankView.newToBankTempData.selectedBusinessEvolvePackage
        initViews()
        setupToolbar()
        initializeTabs()
        setUpListeners()
    }

    private fun initViews() {
        headerTitleTextView.text = businessEvolveSelectedPackage.packageName
        when {
            BUSINESS_EVOLVE_ACCOUNT.equals(newToBankView.newToBankTempData.productType, true) -> {
                newToBankView.trackSoleProprietorCurrentFragment("SoleProprietor_BusinessStandardEvolve_ButtonClicked")
                appbarLayout.setBackgroundResource(R.drawable.ic_business_evolve_large)
            }
            BUSINESS_EVOLVE_ISLAMIC_ACCOUNT.equals(newToBankView.newToBankTempData.productType, true) -> {
                newToBankView.trackSoleProprietorCurrentFragment("SoleProprietor_BusinessIslamicEvolve_ScreenDisplayed")
                appbarLayout.setBackgroundResource(R.drawable.ic_business_evolve_islamic_large)
            }
        }
    }

    private fun initializeTabs() {
        val evolveProductsList = businessEvolveSelectedPackage.products
        val tabs = SparseArray<FragmentPagerItem>().apply {
            evolveProductsList.forEachIndexed { index, product ->
                append(index, EvolveProductPagerItemFragment.newInstance(product.tabTitle))
            }
        }
        evolveAppbarTabBarView.addTabs(tabs)
        evolveProductViewPager.adapter = TabPager(childFragmentManager, tabs)
        evolveProductViewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(evolveAppbarTabBarView))
        evolveAppbarTabBarView.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(evolveProductViewPager))
        evolveAppbarTabBarView.setupWithViewPager(evolveProductViewPager)
        evolveProductViewPager.post { evolveProductViewPager.requestLayout() }
    }

    private val offsetChangeLister: AppBarLayout.OnOffsetChangedListener = AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
        if (verticalOffset == 0) {
            headerTitleTextView.alpha = 1f
        } else {
            val invertedVerticalOffset = scrollRange + verticalOffset
            val alpha = (invertedVerticalOffset / scrollRange).toDouble().pow(4.0).toFloat()
            headerTitleTextView.alpha = alpha
        }
    }

    private fun setupToolbar() {
        setToolBar("")
        hideToolBar()
        with(fragmentToolbar) {
            setNavigationIcon(R.drawable.ic_arrow_back_white)
            title = getString(R.string.feature_switching_business_bank_account)
            setNavigationOnClickListener {
                baseActivity.onBackPressed()
            }
        }
    }

    private fun setUpListeners() {
        density = ViewUtils.getDisplayDensity()
        appbarLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                scrollRange = appbarLayout.totalScrollRange.toFloat()
                appbarLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        appbarLayout.addOnOffsetChangedListener(offsetChangeLister)
    }
}