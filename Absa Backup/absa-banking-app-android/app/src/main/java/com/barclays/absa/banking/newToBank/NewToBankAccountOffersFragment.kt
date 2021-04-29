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
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.newToBank.services.dto.BusinessEvolveCardPackageResponse
import com.barclays.absa.banking.newToBank.services.dto.CardPackage
import com.barclays.absa.utils.CommonUtils
import kotlinx.android.synthetic.main.new_to_bank_account_offers_fragment.*
import styleguide.cards.Offer
import styleguide.cards.OfferView
import styleguide.utils.ViewUtils
import styleguide.utils.extensions.toTitleCase

class NewToBankAccountOffersFragment : BaseFragment(R.layout.new_to_bank_account_offers_fragment) {
    private lateinit var newToBankView: NewToBankView
    private var premiumPackage: CardPackage = CardPackage()
    private var goldPackage: CardPackage = CardPackage()
    private var businessEvolveStandardPackage: BusinessEvolveCardPackageResponse = BusinessEvolveCardPackageResponse()
    private var businessEvolveIslamicPackage: BusinessEvolveCardPackageResponse = BusinessEvolveCardPackageResponse()

    companion object {
        @JvmStatic
        fun newInstance(): NewToBankAccountOffersFragment = NewToBankAccountOffersFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        newToBankView = context as NewToBankView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showToolBar()
        setToolBar(getString(R.string.new_to_bank_choose_package).toTitleCase())
        newToBankView.trackCurrentFragment(NewToBankConstants.CHOOSE_ACCOUNT_SCREEN)
        setRecommendation()
        initViews()
    }

    private fun setRecommendation() {
        if (newToBankView.isBusinessFlow) {
            businessEvolveStandardPackage = newToBankView.newToBankTempData.businessEvolveStandardPackage
            businessEvolveIslamicPackage = newToBankView.newToBankTempData.businessEvolveIslamicPackage
            val areBusinessPackagesNotAvailable = businessEvolveStandardPackage.packageName.isEmpty() && businessEvolveIslamicPackage.packageName.isEmpty()
            if (areBusinessPackagesNotAvailable) {
                return
            }

            divider.visibility = View.GONE
            with(firstOfferView) {
                visibility = View.VISIBLE
                hideLabel()
                offer = Offer("", businessEvolveStandardPackage.packageName, "", "", false)
                setOfferImage(R.drawable.ic_business_evolve_small)
            }

            with(secondOfferView) {
                visibility = View.VISIBLE
                hideLabel()
                offer = Offer("", businessEvolveIslamicPackage.packageName, "", "", false)
                setOfferImage(R.drawable.ic_business_evolve_islamic_small)
            }
            showMoreOptionActionButton.visibility = View.GONE
        } else {
            val itemCode = newToBankView.newToBankTempData.selectedMonthlyIncomeCode.toInt()
            premiumPackage = newToBankView.newToBankTempData.premiumPackage
            goldPackage = newToBankView.newToBankTempData.goldPackage
            val recommendedText = getString(R.string.new_to_bank_recommended_for_you)
            val premiumIncome = getString(R.string.new_to_bank_earn_at_least, premiumPackage.minimumIncome)
            val goldIncome = getString(R.string.new_to_bank_earn_at_least, goldPackage.minimumIncome)
            val areRetailPackagesNotAvailable = premiumPackage.packageName.isEmpty() || goldPackage.packageName.isEmpty()
            if (areRetailPackagesNotAvailable) {
                return
            }

            when {
                itemCode < 12 -> {
                    with(secondOfferView) {
                        visibility = View.VISIBLE
                        setOfferImage(R.drawable.ic_gold_package_small)
                        offer = Offer(goldIncome, goldPackage.packageName, "", "", false)
                    }

                    with(thirdOfferView) {
                        setOfferImage(R.drawable.ic_premium_package_small)
                        visibility = View.VISIBLE
                        offer = Offer(premiumIncome, premiumPackage.packageName, "", "", false)
                    }

                    firstOfferView.visibility = View.GONE
                    showMoreOptionActionButton.visibility = View.GONE
                }
                itemCode < 16 -> {
                    with(firstOfferView) {
                        visibility = View.VISIBLE
                        setOfferImage(R.drawable.ic_premium_package_small)
                        offer = Offer(premiumIncome, premiumPackage.packageName, "", recommendedText, false)
                    }

                    with(secondOfferView) {
                        visibility = View.VISIBLE
                        setOfferImage(R.drawable.ic_gold_package_small)
                        offer = Offer(goldIncome, goldPackage.packageName, "", "", false)
                    }

                    showMoreOptionActionButton.visibility = View.GONE
                }
                else -> {
                    with(firstOfferView) {
                        visibility = View.VISIBLE
                        setOfferImage(R.drawable.ic_premium_package_small)
                        offer = Offer(premiumIncome, premiumPackage.packageName, "", recommendedText, false)
                    }

                    with(secondOfferView) {
                        visibility = View.GONE
                        setOfferImage(R.drawable.ic_gold_package_small)
                        offer = Offer(goldIncome, goldPackage.packageName, "", "", false)
                    }
                    thirdOfferView.visibility = View.GONE
                }
            }
        }
    }

    private fun setSelectedPackage(offerView: OfferView, requiresAccountInfo: Boolean) {
        if (newToBankView.isBusinessFlow) {
            if (offerView.offer.text.equals(businessEvolveStandardPackage.packageName, ignoreCase = true)) {
                newToBankView.newToBankTempData.productType = NewToBankConstants.BUSINESS_EVOLVE_ACCOUNT
                newToBankView.newToBankTempData.selectedBusinessEvolvePackage = businessEvolveStandardPackage
            } else if (offerView.offer.text.equals(businessEvolveIslamicPackage.packageName, ignoreCase = true)) {
                newToBankView.newToBankTempData.productType = NewToBankConstants.BUSINESS_EVOLVE_ISLAMIC_ACCOUNT
                newToBankView.newToBankTempData.selectedBusinessEvolvePackage = businessEvolveIslamicPackage
            }
        } else {
            if (offerView.offer.text.equals(goldPackage.packageName, ignoreCase = true)) {
                newToBankView.newToBankTempData.productType = NewToBankConstants.GOLD_ACCOUNT
                newToBankView.newToBankTempData.selectedPackage = goldPackage
                if (!requiresAccountInfo) {
                    newToBankView.trackFragmentAction(NewToBankConstants.CHOOSE_ACCOUNT_SCREEN, NewToBankConstants.CLIENT_ACCOUNT_SELECTION_ACTION_GVB)
                }
            } else if (offerView.offer.text.equals(premiumPackage.packageName, ignoreCase = true)) {
                newToBankView.newToBankTempData.productType = NewToBankConstants.PREMIUM_ACCOUNT
                newToBankView.newToBankTempData.selectedPackage = premiumPackage
                if (!requiresAccountInfo) {
                    newToBankView.trackFragmentAction(NewToBankConstants.CHOOSE_ACCOUNT_SCREEN, NewToBankConstants.CLIENT_ACCOUNT_SELECTION_ACTION_PB)
                }
            }
        }
    }

    private fun initViews() {
        if (newToBankView.isBusinessFlow) {
            with(firstOfferView) {
                setOnClickListener {
                    setSelectedPackage(this, false)
                    newToBankView.navigateToChooseYourProductFragment()
                    newToBankView.trackSoleProprietorCurrentFragment("SoleProprietor_BusinessStandardEvolve_ButtonClicked")
                }
                setOnClickListener {
                    setSelectedPackage(this, true)
                    newToBankView.navigateToChooseYourProductFragment()
                    newToBankView.trackSoleProprietorCurrentFragment("SoleProprietor_BusinessStandardEvolve_ButtonClicked")
                }
            }

            with(secondOfferView) {
                setOnClickListener {
                    setSelectedPackage(this, false)
                    newToBankView.navigateToChooseYourProductFragment()
                    newToBankView.trackSoleProprietorCurrentFragment("SoleProprietor_BusinessIslamicEvolve_ButtonClicked")
                }
                setOnClickListener {
                    setSelectedPackage(this, true)
                    newToBankView.navigateToChooseYourProductFragment()
                    newToBankView.trackSoleProprietorCurrentFragment("SoleProprietor_BusinessIslamicEvolve_ButtonClicked")
                }
            }

            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    newToBankView.navigateToBusinessWebsite()
                }
            }
            ViewUtils.setTextAppearance(productsAndPricingTextView, requireActivity(), R.style.SmallTextRegularDark)
            CommonUtils.makeTextClickable(newToBankView as Context?, R.string.business_banking_soleprop_message,
                    getString(R.string.new_to_bank_absa_website_lower), clickableSpan, productsAndPricingTextView, R.color.graphite)
        } else {
            val textToMakeClickable = arrayOf("absa.co.za", "fees")
            val clickableSpans = arrayOf(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.absa.co.za"))
                    newToBankView.trackFragmentAction(NewToBankConstants.CHOOSE_ACCOUNT_SCREEN, NewToBankConstants.CLIENT_ABSA_LINK_CLICK_ACTION)
                    startActivity(intent)
                }
            }, object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val viewFeesIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.absa.co.za/rates-and-fees/personal-banking/"))
                    newToBankView.trackFragmentAction(NewToBankConstants.CHOOSE_ACCOUNT_SCREEN, NewToBankConstants.CLIENT_ABSA_FEES_SELECTION_CLICK)
                    startActivity(viewFeesIntent)
                }
            })
            CommonUtils.makeMultipleTextClickable(requireContext(), R.string.new_to_bank_products_and_pricing, textToMakeClickable, clickableSpans, productsAndPricingTextView, R.color.graphite)

            showMoreOptionActionButton.setOnClickListener {
                secondOfferView.visibility = View.VISIBLE
                showMoreOptionActionButton.visibility = View.GONE
            }

            with(firstOfferView) {
                setOnClickListener {
                    setSelectedPackage(this, false)
                    newToBankView.navigateToVerifyIdentityFragment()
                }
                setOnClickListener {
                    setSelectedPackage(this, true)
                    newToBankView.navigateToAccountOfferFullFragment()
                }
            }

            with(secondOfferView) {
                setOnClickListener {
                    setSelectedPackage(this, false)
                    newToBankView.navigateToVerifyIdentityFragment()
                }
                setOnClickListener {
                    setSelectedPackage(this, true)
                    newToBankView.navigateToAccountOfferFullFragment()
                }
            }

            with(thirdOfferView) {
                setOnClickListener {
                    setSelectedPackage(this, false)
                    newToBankView.navigateToVerifyIdentityFragment()
                }
                setOnClickListener {
                    setSelectedPackage(this, true)
                    newToBankView.navigateToAccountOfferFullFragment()
                }
            }
        }
    }
}