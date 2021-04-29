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
package com.barclays.absa.banking.newToBank

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.widget.Toolbar
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.utils.AppConstants
import com.barclays.absa.banking.newToBank.dto.NewToBankBulletItem
import com.barclays.absa.banking.newToBank.services.dto.CardPackage
import com.barclays.absa.banking.presentation.shared.NoScrollLinearLayoutManager
import com.barclays.absa.utils.AnalyticsUtil.trackAction
import com.barclays.absa.utils.CommonUtils
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.new_to_bank_account_offer_full_fragment.*
import styleguide.utils.ViewUtils
import styleguide.utils.extensions.toRandAmount
import java.util.*
import kotlin.math.pow

class NewToBankAccountOfferFullFragment : BaseFragment(R.layout.new_to_bank_account_offer_full_fragment), AppBarLayout.OnOffsetChangedListener {
    private lateinit var newToBankView: NewToBankView
    private var marginTop = 0
    private var selectedPackage: CardPackage = CardPackage()
    private var scrollRange = 0f
    private var density = 0f
    private var isShowingMore = false
    private var newToBankBulletAdapter: NewToBankBulletAdapter = NewToBankBulletAdapter()
    private var bankingMoreInfoUrl: String = ""
    private val bulletItems: MutableList<NewToBankBulletItem> = ArrayList()

    companion object {
        private const val PREMIUM_BANKING = "premium banking"
        private const val GOLD_VALUE_BUNDLE = "gold value bundle"

        @JvmStatic
        fun newInstance(): NewToBankAccountOfferFullFragment = NewToBankAccountOfferFullFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        newToBankView = context as NewToBankView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newToBankView.dismissProgressDialog()
        setHasOptionsMenu(true)
        newToBankBulletAdapter = NewToBankBulletAdapter(newToBankView)
        marginTop = resources.getDimensionPixelSize(R.dimen.double_medium_space)

        val toolbar: Toolbar = fragmentToolbar

        toolbar.title = ""
        baseActivity.setSupportActionBar(toolbar)
        newToBankView.hideToolbar()
        initViews()
        setUpListeners()
    }

    private fun animateTitleViews(alphaPercentage: Float) {
        val baseSize = 64
        val top = ((1 - alphaPercentage) * (baseSize * density)).toInt() + marginTop
        headerTitleTextView.y = top.toFloat()
    }

    private fun initViews() {
        selectedPackage = newToBankView.newToBankTempData?.selectedPackage ?: CardPackage()
        packageTextView.text = selectedPackage.description
        headerTitleTextView.text = selectedPackage.packageName

        CommonUtils.makeTextClickable(requireActivity(), getString(R.string.new_to_bank_more_information, selectedPackage.packageName), getString(R.string.new_to_bank_click_here), object : ClickableSpan() {
            override fun onClick(widget: View) {
                when {
                    PREMIUM_BANKING.equals(selectedPackage.packageName, ignoreCase = true) -> {
                        trackAction("NTB_PB_Additional_MORE_INFO")
                    }
                    GOLD_VALUE_BUNDLE.equals(selectedPackage.packageName, ignoreCase = true) -> {
                        trackAction("NTB_GVB_Additional_MORE_INFO")
                    }
                }
                newToBankView.navigateToPDFViewerFragment(bankingMoreInfoUrl, selectedPackage.packageName)
                setAnalyticsDataOnClick(selectedPackage.packageName, true)
            }
        }, moreInformationTextView, R.color.graphite)
        CommonUtils.makeTextClickable(requireActivity(), R.string.new_to_bank_offer_note, "absa.co.za", object : ClickableSpan() {
            override fun onClick(widget: View) {
                trackAction("NTB_OD_COZA")
                val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.absa.co.za"))
                startActivity(intent)
            }
        }, noteTextView, R.color.graphite)

        when {
            selectedPackage.packageName.equals(PREMIUM_BANKING, ignoreCase = true) -> {
                bankingMoreInfoUrl = AppConstants.NEW_TO_BANK_PREMIUM_ABSA_PDF_URL
                offerImageConstraintLayout.setBackgroundResource(R.drawable.ic_premium_package_large)
                newToBankView.trackCurrentFragment(NewToBankConstants.CLIENT_MORE_INFO_VIEW_PB)
            }
            selectedPackage.packageName.equals(GOLD_VALUE_BUNDLE, ignoreCase = true) -> {
                bankingMoreInfoUrl = AppConstants.NEW_TO_BANK_GOLD_ABSA_PDF_URL
                offerImageConstraintLayout.setBackgroundResource(R.drawable.ic_gold_package_large)
                newToBankView.trackCurrentFragment(NewToBankConstants.CLIENT_MORE_INFO_VIEW_GVB)
            }
        }
        amountTextView.text = selectedPackage.monthlyFee.toRandAmount()
        selectedPackage.packages.forEach { currentPackage ->
            bulletItems.add(NewToBankBulletItem(currentPackage.accountPackage, NewToBankBulletItem.BulletType.HEADING))
            currentPackage.bulletPoints.forEach { bullet ->
                bulletItems.add(NewToBankBulletItem(bullet, NewToBankBulletItem.BulletType.BULLET))
            }
            if (currentPackage.extraInfo.title.isNotBlank()) {
                bulletItems.add(NewToBankBulletItem(currentPackage.extraInfo.title, NewToBankBulletItem.BulletType.INCLUDE))
                currentPackage.extraInfo.bulletPoints.forEach { bullet ->
                    bulletItems.add(NewToBankBulletItem(bullet, NewToBankBulletItem.BulletType.EXTRA_BULLET))
                }
            }
        }
        bulletsRecyclerView.isNestedScrollingEnabled = false
        bulletsRecyclerView.layoutManager = NoScrollLinearLayoutManager(requireContext())
        bulletsRecyclerView.adapter = newToBankBulletAdapter
        showLessAccountBenefits()
    }

    private fun setUpListeners() {
        density = ViewUtils.getDisplayDensity()
        appbarLayout.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                scrollRange = appbarLayout.totalScrollRange.toFloat()
                appbarLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        appbarLayout.addOnOffsetChangedListener(this)

        nextButton.setOnClickListener {
            releaseFragment()
            setAnalyticsDataOnClick(selectedPackage.packageName, false)
            newToBankView.navigateToVerifyIdentityFragment()
        }

        showMoreOptionActionButtonView.setOnClickListener {
            if (isShowingMore) {
                showLessAccountBenefits()
            } else {
                showMoreAccountBenefits()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_close, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        activity?.onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun releaseFragment() {
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
    }

    private fun setAnalyticsDataOnClick(packageName: String, requiresInfo: Boolean) {
        if (PREMIUM_BANKING.equals(packageName, ignoreCase = true)) {
            if (requiresInfo) {
                newToBankView.trackFragmentAction(NewToBankConstants.CLIENT_MORE_INFO_VIEW_PB, NewToBankConstants.CLIENT_MORE_INFO_SELECTION_ACTION_PB)
            } else {
                newToBankView.trackFragmentAction(NewToBankConstants.CHOOSE_ACCOUNT_SCREEN, NewToBankConstants.CLIENT_PB_APPLICATION_SELECTION_ACTION)
            }
        } else if (GOLD_VALUE_BUNDLE.equals(packageName, ignoreCase = true)) {
            if (requiresInfo) {
                newToBankView.trackFragmentAction(NewToBankConstants.CLIENT_MORE_INFO_VIEW_GVB, NewToBankConstants.CLIENT_MORE_INFO_SELECTION_ACTION_GVB)
            } else {
                newToBankView.trackFragmentAction(NewToBankConstants.CHOOSE_ACCOUNT_SCREEN, NewToBankConstants.CLIENT_GVB_APPLICATION_SELECTION_ACTION)
            }
        }
    }

    private fun showMoreAccountBenefits() {
        newToBankBulletAdapter.refreshItems(bulletItems)
        showMoreOptionActionButtonView.setCaptionText(getString(R.string.new_to_bank_benefits_show_less))
        bulletsRecyclerView.scrollToPosition(0)
        isShowingMore = true
    }

    private fun showLessAccountBenefits() {
        val lessBullets: MutableList<NewToBankBulletItem> = ArrayList()
        bulletItems.forEach { bulletItem ->
            if (lessBullets.size < 5) {
                lessBullets.add(bulletItem)
            }
        }
        if (lessBullets.isNotEmpty()) {
            newToBankBulletAdapter.refreshItems(lessBullets)
            showMoreOptionActionButtonView.setCaptionText(getString(R.string.new_to_bank_benefits_show_more))
            bulletsRecyclerView.scrollToPosition(0)
            isShowingMore = false
        }
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        if (verticalOffset == 0) {
            animateTitleViews(1f)
        } else {
            val invertedVerticalOffset: Float = scrollRange + verticalOffset
            val collapsePercent = (invertedVerticalOffset / scrollRange).toDouble().pow(4.0).toFloat()
            animateTitleViews(collapsePercent)
        }
    }
}