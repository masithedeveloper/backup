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
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.newToBank.NewToBankConstants.BUSINESS_EVOLVE_ACCOUNT
import com.barclays.absa.banking.newToBank.NewToBankConstants.BUSINESS_EVOLVE_ISLAMIC_ACCOUNT
import com.barclays.absa.banking.newToBank.services.dto.BusinessEvolveCardPackageResponse
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.business_banking_account_offers_fragment.*
import styleguide.cards.Offer

class BusinessBankOffersFragment : BaseFragment(R.layout.business_banking_account_offers_fragment) {
    private lateinit var businessBankingViewModel: BusinessBankingViewModel
    private lateinit var hostActivity: BusinessBankActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as BusinessBankActivity
        businessBankingViewModel = hostActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showToolBar()
        setToolBar(R.string.business_banking_choose_your_package)
        showClickableAbsaWebsiteLink()
        setObserversListener()
        businessBankingViewModel.fetchBusinessEvolvePackages()
    }

    private fun setObserversListener() {
        businessBankingViewModel.businessEvolveIslamicMutableLiveData.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            showBusinessEvolveIslamicOffer(it)
        })

        businessBankingViewModel.businessEvolveStandardMutableLiveData.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            showBusinessEvolveStandardOffer(it)
        })
    }

    private fun showClickableAbsaWebsiteLink() {
        val navigateToAbsaWebsite = object : ClickableSpan() {
            override fun onClick(widget: View) = hostActivity.navigateToInstantBusinessWebsite()
        }

        CommonUtils.makeTextClickable(context,
                getString(R.string.business_banking_soleprop_message, getString(R.string.new_to_bank_absa_website_lower)),
                getString(R.string.new_to_bank_absa_website_lower),
                navigateToAbsaWebsite, productsAndPricingTextView, R.color.graphite)
    }

    private fun showBusinessEvolveIslamicOffer(businessEvolveCardPackage: BusinessEvolveCardPackageResponse) {
        businessBankingViewModel.businessEvolveIslamicMutableLiveData.removeObservers(viewLifecycleOwner)
        with(secondOfferView) {
            visibility = View.VISIBLE
            hideLabel()
            offer = Offer("", businessEvolveCardPackage.packageName, "", "", false)
            setOfferImage(R.drawable.ic_business_evolve_islamic_small)
            setOnClickListener {
                navigateToProductInformationScreen(businessEvolveCardPackage, BUSINESS_EVOLVE_ISLAMIC_ACCOUNT)
            }
        }
    }

    private fun showBusinessEvolveStandardOffer(businessEvolveCardPackage: BusinessEvolveCardPackageResponse) {
        businessBankingViewModel.businessEvolveStandardMutableLiveData.removeObservers(viewLifecycleOwner)
        with(firstOfferView) {
            visibility = View.VISIBLE
            hideLabel()
            offer = Offer("", businessEvolveCardPackage.packageName, "", "", false)
            setOfferImage(R.drawable.ic_business_evolve_small)
            setOnClickListener {
                navigateToProductInformationScreen(businessEvolveCardPackage, BUSINESS_EVOLVE_ACCOUNT)
            }
        }
    }

    private fun navigateToProductInformationScreen(businessEvolveCardPackage: BusinessEvolveCardPackageResponse, productType: String) {
        businessBankingViewModel.selectedPackageMutableLiveData.value = businessEvolveCardPackage
        businessBankingViewModel.selectedProductType = productType
        hostActivity.goToNewToBankScreen()
    }
}