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
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.newToBank.NewToBankConstants.BUSINESS_EVOLVE_ACCOUNT
import com.barclays.absa.banking.newToBank.dto.NewToBankBulletItem
import com.barclays.absa.banking.newToBank.services.dto.BusinessEvolveCardPackageResponse
import com.barclays.absa.utils.CommonUtils
import kotlinx.android.synthetic.main.evolve_product_information_fragment.*
import styleguide.bars.FragmentPagerItem
import styleguide.utils.extensions.removeSpaces

class EvolveProductPagerItemFragment : FragmentPagerItem(R.layout.evolve_product_information_fragment) {
    private lateinit var newToBankBulletAdapter: NewToBankBulletAdapter
    private lateinit var newToBankView: NewToBankView
    private var selectedProduct: BusinessEvolveCardPackageResponse.Products = BusinessEvolveCardPackageResponse.Products()
    private var isShowingMore = false
    private val bulletItems = arrayListOf<NewToBankBulletItem>()

    companion object {
        @JvmStatic
        fun newInstance(description: String): EvolveProductPagerItemFragment {
            return EvolveProductPagerItemFragment().apply {
                arguments = Bundle().apply { putString(TAB_DESCRIPTION_KEY, description) }
            }
        }
    }

    override fun getTabDescription() = arguments?.getString(TAB_DESCRIPTION_KEY) ?: ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        newToBankView = activity as NewToBankView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newToBankBulletAdapter = NewToBankBulletAdapter(newToBankView)
        selectedProduct = newToBankView.newToBankTempData.selectedBusinessEvolvePackage.products.first { it.tabTitle.equals(getTabDescription(), true) }
        nextButton.text = selectedProduct.bottomButtonTitle
        with(packageNameAndFeePrimaryContentAndLabelView) {
            setContentText(selectedProduct.headerTitle)
            setLabelText(selectedProduct.headerSubTitle)
        }

        populateClassicBusinessBulletPoints()
        setOnClickListeners()
        setSpanTextClickable()
    }

    private fun setOnClickListeners() {
        nextButton.setOnClickListener {
            with(newToBankView) {
                val productName = getTabDescription().removeSpaces()
                if (BUSINESS_EVOLVE_ACCOUNT.equals(newToBankView.newToBankTempData.productType, true)) {
                    trackSoleProprietorCurrentFragment("SoleProprietor_BusinessStandard${productName}_ProceedButtonClicked")
                } else {
                    trackSoleProprietorCurrentFragment("SoleProprietor_BusinessIslamic${productName}_ProceedButtonClicked")
                }
                newToBankTempData.selectedBusinessEvolveProduct = selectedProduct
                navigateToOptionalAccountExtrasFragment()
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

    private fun setSpanTextClickable() {
        val clickableSpans = arrayOf(object : ClickableSpan() {
            override fun onClick(widget: View) {
                newToBankView.navigateToBusinessWebsite()
            }
        }, object : ClickableSpan() {
            override fun onClick(widget: View) {
                newToBankView.navigateToBusinessBankingApplicationFees()
            }
        })

        val textToMakeClickable: Array<String> = arrayOf(getString(R.string.new_to_bank_absa_website_lower), getString(R.string.relationship_banking_fees))
        CommonUtils.makeMultipleTextClickable(context, R.string.business_banking_product_and_pricing,
                textToMakeClickable, clickableSpans, businessNoteTextView, R.color.graphite)
    }

    private fun populateClassicBusinessBulletPoints() {
        selectedProduct.features.forEach {
            bulletItems.add(NewToBankBulletItem(it.featureName, NewToBankBulletItem.BulletType.HEADING))
            it.featurePoints.forEach { point ->
                bulletItems.add(NewToBankBulletItem(point.replace("•", "").trimStart(), NewToBankBulletItem.BulletType.DOT))
            }
        }
        with(packageFeaturesRecyclerView) {
            adapter = newToBankBulletAdapter
            isNestedScrollingEnabled = false
        }
        showLessAccountBenefits()
    }

    private fun showMoreAccountBenefits() {
        newToBankBulletAdapter.refreshItems(bulletItems)
        showMoreOptionActionButtonView.setIcon(R.drawable.ic_dash_dark)
        showMoreOptionActionButtonView.setCaptionText(getString(R.string.business_banking_show_less))
        packageFeaturesRecyclerView.scrollToPosition(0)
        isShowingMore = true
    }

    private fun showLessAccountBenefits() {
        val lessBullets = bulletItems.take(6)
        newToBankBulletAdapter.refreshItems(lessBullets)
        showMoreOptionActionButtonView.setIcon(R.drawable.ic_add_dark)
        showMoreOptionActionButtonView.setCaptionText(getString(R.string.business_banking_show_me_more))
        packageFeaturesRecyclerView.scrollToPosition(0)
        isShowingMore = false
    }
}