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
package com.barclays.absa.banking.relationshipBanking.ui

import android.os.Bundle
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.BusinessBankingChooseBranchForCardFragmentBinding
import com.barclays.absa.banking.newToBank.NewToBankView
import com.barclays.absa.banking.newToBank.dto.InBranchInfo
import com.barclays.absa.banking.newToBank.services.dto.CreateCombiDetails
import com.barclays.absa.banking.newToBank.services.dto.SiteFilteredDetailsVO
import com.barclays.absa.banking.presentation.shared.ExtendedFragment
import styleguide.forms.SelectorList
import styleguide.forms.StringItem

class BusinessBankingGetCardBranchFragment : ExtendedFragment<BusinessBankingChooseBranchForCardFragmentBinding>() {

    private lateinit var newToBankBusinessView: NewToBankView

    companion object {
        private const val CARD_DELIVERY_METHOD = "BRANCH"
    }

    override fun getToolbarTitle(): String = getString(R.string.relationship_banking_get_card)
    override fun getLayoutResourceId(): Int = R.layout.business_banking_choose_branch_for_card_fragment

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        newToBankBusinessView = activity as NewToBankView
        newToBankBusinessView.setToolbarBackTitle(toolbarTitle)
        initViews()
    }

    private fun initViews() {
        val userPreferredBranch: SiteFilteredDetailsVO = newToBankBusinessView.newToBankTempData.businessCustomerPortfolio.preferredBranch
        binding.apply {
            cityNormalInputView.selectedValue = userPreferredBranch.town.toString()
            suburbNormalInputView.selectedValue = userPreferredBranch.suburb.toString()
            branchNormalInputView.selectedValue = userPreferredBranch.siteName.toString()

            branchNormalInputView.setList(getBranchList(userPreferredBranch.suburb), getString(R.string.relationship_banking_choose_branch))
            cityNormalInputView.setList(getCityList(), getString(R.string.relationship_banking_choose_city))
            suburbNormalInputView.setList(getSuburbList(binding.cityNormalInputView.selectedValue), getString(R.string.relationship_banking_choose_suburb))
            cityNormalInputView.setItemSelectionInterface {
                suburbNormalInputView.selectedValue = ""
                suburbNormalInputView.setList(getSuburbList(binding.cityNormalInputView.selectedValue), getString(R.string.relationship_banking_choose_suburb))
            }
            suburbNormalInputView.setItemSelectionInterface {
                branchNormalInputView.selectedValue = ""
                branchNormalInputView.setList(getBranchList(binding.suburbNormalInputView.selectedValue), getString(R.string.relationship_banking_choose_branch))
            }

            nextButton.setOnClickListener {
                newToBankBusinessView.newToBankTempData.businessBankingSiteCodeDetails.forEach {
                    if (branchNormalInputView.selectedValue == it.displayValue) {
                        val selectedBranch = it.siteFilteredDetails
                        userPreferredBranch.apply {
                            town = selectedBranch.town
                            suburb = selectedBranch.suburb
                            siteName = selectedBranch.siteName
                            siteCode = selectedBranch.siteCode
                        }
                    }
                }
                val inBranchInfo = getSelectedBranchInfo(userPreferredBranch)
                newToBankBusinessView.createCombiCardAccount(getDeliveryCombiDetails(inBranchInfo))
            }
        }
    }

    private fun getDeliveryCombiDetails(inBranchInfo: InBranchInfo?): CreateCombiDetails {
        return CreateCombiDetails().apply {
            deliveryBranch = inBranchInfo!!.inBranchCode
            deliveryMethod = CARD_DELIVERY_METHOD
            isPersonalised = true
        }
    }

    private fun getSelectedBranchInfo(userPreferredBranch: SiteFilteredDetailsVO?): InBranchInfo? {
        return InBranchInfo().apply {
            inBranchIndicator = true
            inBranchName = userPreferredBranch?.siteName!!
            inBranchCode = userPreferredBranch.siteCode!!
        }
    }

    private fun getCityList(): SelectorList<StringItem> {
        val cities: SelectorList<StringItem> = SelectorList()
        newToBankBusinessView.newToBankTempData.businessBankingSiteCodeDetails.forEach {
            if (!cities.contains(StringItem(it.siteFilteredDetails.town))) {
                cities.add(StringItem(it.siteFilteredDetails.town))
            }
        }
        return cities
    }

    private fun getSuburbList(city: String): SelectorList<StringItem> {
        val suburbList = SelectorList<StringItem>()
        newToBankBusinessView.newToBankTempData.businessBankingSiteCodeDetails.forEach {
            if (city == it.siteFilteredDetails.town && !suburbList.contains(StringItem(it.siteFilteredDetails.suburb))) {
                suburbList.add(StringItem(it.siteFilteredDetails.suburb))
            }
        }
        return suburbList
    }

    private fun getBranchList(suburb: String?): SelectorList<StringItem> {
        val branchList = SelectorList<StringItem>()
        newToBankBusinessView.newToBankTempData.businessBankingSiteCodeDetails.forEach {
            if (suburb == it.siteFilteredDetails.suburb) {
                branchList.add(StringItem(it.siteFilteredDetails.siteName))
            }
        }
        return branchList
    }
}
