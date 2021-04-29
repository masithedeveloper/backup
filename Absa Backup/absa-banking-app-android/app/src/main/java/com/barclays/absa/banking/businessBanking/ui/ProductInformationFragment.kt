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
package com.barclays.absa.banking.businessBanking.ui

import android.content.Context
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.MenuItem
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.newToBank.NewToBankBulletAdapter
import com.barclays.absa.banking.newToBank.dto.NewToBankBulletItem
import com.barclays.absa.banking.newToBank.services.dto.BusinessEvolveCardPackageResponse
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.presentation.shared.NoScrollLinearLayoutManager
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.viewModel
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.product_information_fragment.*
import kotlin.math.pow

class ProductInformationFragment : BaseFragment(R.layout.product_information_fragment) {
    private var scrollRange = 0.0f
    private var isShowingMore = false
    private lateinit var hostActivity: BusinessBankActivity
    private lateinit var businessBankingViewModel: BusinessBankingViewModel
    private lateinit var bulletItemAdapter: NewToBankBulletAdapter
    private lateinit var packageOptions: List<NewToBankBulletItem>

    companion object {
        const val BUSINESS_EVOLVE_ISLAMIC_ACCOUNT = "Business Evolve Islamic Account"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as BusinessBankActivity
        businessBankingViewModel = hostActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        attachEventHandlers()
        hostActivity.trackSoleProprietorCurrentFragment("SoleProprietor_ProductScreen_ScreenDisplayed")
    }

    private fun setUpViews() {
        setHasOptionsMenu(true)
        fragmentToolbar.apply {
            title = getString(R.string.business_banking_title)
            setNavigationIcon(R.drawable.ic_arrow_back_white)
            setNavigationOnClickListener { hostActivity.onBackPressed() }
        }

        hostActivity.apply {
            hideToolBar()
            setSupportActionBar(fragmentToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        val cardPackage = businessBankingViewModel.selectedPackageMutableLiveData.value
        cardPackage?.let {
            val backgroundRes = if (BUSINESS_EVOLVE_ISLAMIC_ACCOUNT.equals(it.packageName, true)) {
                R.drawable.ic_business_evolve_islamic_large
            } else {
                R.drawable.ic_business_evolve_large
            }
            headerConstraintLayout.setBackgroundResource(backgroundRes)
            packageTextView.text = it.description
            headerTitleTextView.text = it.packageName
            populateProductPackageOptions(cardPackage)
            dismissProgressDialog()
        }
    }

    private fun attachEventHandlers() {
        appbarLayout?.let {
            it.viewTreeObserver.addOnGlobalLayoutListener {
                scrollRange = it.totalScrollRange.toFloat()
                it.viewTreeObserver.removeOnGlobalLayoutListener {}
            }

            it.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                val collapsePercent = if (verticalOffset == 0) 1f else ((scrollRange + verticalOffset) / scrollRange).toDouble().pow(1.0).toFloat()
                animateViews(collapsePercent)
            })
        }

        val clickableSpans = arrayOf(object : ClickableSpan() {
            override fun onClick(widget: View) = hostActivity.navigateToInstantBusinessWebsite()
        }, object : ClickableSpan() {
            override fun onClick(widget: View) = hostActivity.navigateToBusinessBankingApplicationFees()
        })

        val textToMakeClickable: Array<String> = arrayOf(getString(R.string.new_to_bank_absa_website_lower), getString(R.string.business_banking_fees))
        CommonUtils.makeMultipleTextClickable(context, R.string.business_banking_product_and_pricing,
                textToMakeClickable, clickableSpans, noteTextView, R.color.graphite)

        val primaryEventHandler = View.OnClickListener { hostActivity.logout() }
        val secondaryEventHandler = View.OnClickListener {
            val app = BMBApplication.getInstance()
            app.topMostActivity.finish()
        }
        showMoreOptionActionButtonView.setOnClickListener {
            if (isShowingMore) {
                showLessAccountBenefits()
            } else {
                showMoreAccountBenefits()
            }
        }

        nextButton.setOnClickListener {
            startActivity(IntentFactory.getAlertResultScreenWithEventHandlers(activity,
                    R.string.please_note,
                    R.string.business_bank_logout_alert_message,
                    primaryEventHandler,
                    secondaryEventHandler)
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        hostActivity.onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun populateProductPackageOptions(cardPackage: BusinessEvolveCardPackageResponse) {
        packageOptions = mutableListOf<NewToBankBulletItem>().apply {
            cardPackage.products.forEach { currentPackage ->
                currentPackage.features.forEach {
                    add(NewToBankBulletItem(it.featureName, NewToBankBulletItem.BulletType.HEADING))

                    it.featurePoints.forEach { featurePoint ->
                        add(NewToBankBulletItem(featurePoint, NewToBankBulletItem.BulletType.BULLET))
                    }
                }
            }
        }
        bulletItemAdapter = NewToBankBulletAdapter(packageOptions)
        bulletsRecyclerView.apply {
            isNestedScrollingEnabled = false
            layoutManager = NoScrollLinearLayoutManager(context)
            adapter = bulletItemAdapter
        }
        showLessAccountBenefits()
    }

    private fun showMoreAccountBenefits() {
        bulletItemAdapter.refreshItems(packageOptions)
        showMoreOptionActionButtonView.setCaptionText(getString(R.string.business_banking_show_less))
        bulletsRecyclerView.scrollToPosition(0)
        isShowingMore = true
    }

    private fun showLessAccountBenefits() {
        val lessBullets = packageOptions.take(4)
        bulletItemAdapter.refreshItems(lessBullets)
        showMoreOptionActionButtonView.setCaptionText(getString(R.string.business_banking_show_me_more))
        bulletsRecyclerView.scrollToPosition(0)
        isShowingMore = false
    }

    private fun animateViews(alphaPercentage: Float) {
        animateView(headerTitleTextView, alphaPercentage)
        animateView(amountTextView, alphaPercentage)
        animateView(perMonthTextView, alphaPercentage)
    }

    private fun animateView(view: View, alphaPercentage: Float) {
        view.apply {
            alpha = alphaPercentage.toDouble().pow(3.0).toFloat()
            scaleX = alphaPercentage
            scaleY = alphaPercentage
        }
    }
}