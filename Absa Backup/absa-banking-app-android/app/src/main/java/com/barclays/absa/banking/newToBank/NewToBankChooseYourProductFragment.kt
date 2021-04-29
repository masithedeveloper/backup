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
package com.barclays.absa.banking.newToBank

import android.content.Context
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.*
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.newToBank.dto.NewToBankBulletItem
import com.barclays.absa.banking.newToBank.services.dto.CardPackage
import com.barclays.absa.banking.presentation.shared.NoScrollLinearLayoutManager
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.TextFormatUtils
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.new_to_bank_account_offer_full_fragment.*
import styleguide.utils.ViewUtils
import kotlin.math.pow

class NewToBankChooseYourProductFragment : BaseFragment(R.layout.new_to_bank_account_offer_full_fragment) {
    private lateinit var newToBankBulletAdapter: NewToBankBulletAdapter
    private lateinit var newToBankView: NewToBankView
    private lateinit var newToBankActivity: NewToBankActivity

    private var selectedPackage: CardPackage? = CardPackage()
    private var scrollRange: Float = 0.0F
    private var density: Float = 0.0F
    private var isShowingMore = false
    private val bulletItems = arrayListOf<NewToBankBulletItem>()

    companion object {
        @JvmStatic
        fun newInstance(): NewToBankChooseYourProductFragment = NewToBankChooseYourProductFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        newToBankView = context as NewToBankView
        newToBankActivity = context as NewToBankActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        newToBankBulletAdapter = NewToBankBulletAdapter(newToBankView)
        newToBankView.hideToolbar()
        selectedPackage = newToBankView.newToBankTempData.selectedPackage
        initViews()
        setupToolbar()
        setUpListeners()
        populateClassicBusinessBulletPoints()
    }

    private fun setupToolbar() {
        newToBankActivity.setSupportActionBar(fragmentToolbar)
        fragmentToolbar.title = ""
    }

    private fun initViews() {
        newToBankView.trackStudentAccount("StudentAccount_ProductScreen_ScreenDisplayed")
        offerImageConstraintLayout.setBackgroundResource(R.drawable.ic_student_account_large)
        amountTextView.text = TextFormatUtils.formatBasicAmountAsRand(selectedPackage?.monthlyFee)
        packageTextView.text = selectedPackage?.description
        headerTitleTextView.text = selectedPackage?.packageName

        if (newToBankView.isStudentFlow) {
            val navigateToAbsaWebsite = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    newToBankView.trackStudentAccount("StudentAccount_ProductScreen_NavigatedToAbsaOnline")
                    newToBankView.navigateToAbsaWebsite()
                }
            }
            val navigateToStudentAccountBenefitsTerms = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    newToBankView.trackStudentAccount("StudentAccount_ProductScreen_NavigatedToAccountBenefitTerms")
                    newToBankView.navigateToStudentAccountBenefitsTerms()
                }
            }

            CommonUtils.makeTextClickable(context, getString(R.string.new_to_bank_more_information_about_student_account,
                    getString(R.string.new_to_bank_click_here)),
                    getString(R.string.new_to_bank_click_here),
                    navigateToStudentAccountBenefitsTerms, noteTextView, R.color.graphite)
            CommonUtils.makeTextClickable(context, getString(R.string.new_to_bank_refer_absa_for_privacy_and_statement,
                    getString(R.string.new_to_bank_absa_website_lower)),
                    getString(R.string.new_to_bank_absa_website_lower),
                    navigateToAbsaWebsite, moreInformationTextView, R.color.graphite)

            if (selectedPackage?.monthlyFee == "0.00") {
                perMonthTextView.visibility = View.INVISIBLE
                amountTextView.text = getString(R.string.free)
            }
        }

        showMoreOptionActionButtonView.setOnClickListener {
            if (isShowingMore) {
                showLessAccountBenefits()
            } else {
                showMoreAccountBenefits()
            }
        }
    }

    private val offsetChangeLister: AppBarLayout.OnOffsetChangedListener = AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
        if (verticalOffset == 0) {
            headerTitleTextView.alpha = 1f
            fragmentToolbar.title = ""
        } else {
            val invertedVerticalOffset = scrollRange + verticalOffset
            val alpha = (invertedVerticalOffset / scrollRange).toDouble().pow(4.0).toFloat()
            fragmentToolbar.title = selectedPackage?.packageName
            headerTitleTextView.alpha = alpha
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
        nextButton.setOnClickListener {
            newToBankView.apply {
                if (isBusinessFlow) {
                    trackSoleProprietorCurrentFragment("SoleProprietor_ProductScreen_ApplyNowButtonClicked")
                } else {
                    trackStudentAccount("StudentAccount_ProductScreen_ApplyNowButtonClicked")
                }
                navigateToStartApplicationFragment()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_close, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuClose) {
            activity?.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun populateClassicBusinessBulletPoints() {
        selectedPackage?.let {
            it.packages.forEach { cardPackage ->
                bulletItems.add(NewToBankBulletItem(cardPackage.accountPackage, NewToBankBulletItem.BulletType.HEADING))
                cardPackage.bulletPoints.forEach { point ->
                    bulletItems.add(NewToBankBulletItem(point, NewToBankBulletItem.BulletType.BULLET))
                }
                if (cardPackage.extraInfo.title.isNotEmpty()) {
                    bulletItems.add(NewToBankBulletItem(cardPackage.extraInfo.title, NewToBankBulletItem.BulletType.INCLUDE))
                    cardPackage.extraInfo.bulletPoints.forEach { bullet ->
                        bulletItems.add(NewToBankBulletItem(bullet, NewToBankBulletItem.BulletType.EXTRA_BULLET))
                    }
                }
            }
            with(bulletsRecyclerView) {
                layoutManager = NoScrollLinearLayoutManager(baseActivity)
                adapter = newToBankBulletAdapter
            }
            showLessAccountBenefits()
        }
    }

    private fun showMoreAccountBenefits() {
        newToBankBulletAdapter.refreshItems(bulletItems)
        showMoreOptionActionButtonView.setIcon(R.drawable.ic_dash_dark)
        showMoreOptionActionButtonView.setCaptionText(getString(R.string.new_to_bank_benefits_show_less))
        bulletsRecyclerView.scrollToPosition(0)
        isShowingMore = true
    }

    private fun showLessAccountBenefits() {
        val lessBullets = bulletItems.take(4)
        newToBankBulletAdapter.refreshItems(lessBullets)
        showMoreOptionActionButtonView.setIcon(R.drawable.ic_add_dark)
        showMoreOptionActionButtonView.setCaptionText(getString(R.string.new_to_bank_benefits_show_more))
        bulletsRecyclerView.scrollToPosition(0)
        isShowingMore = false
    }
}