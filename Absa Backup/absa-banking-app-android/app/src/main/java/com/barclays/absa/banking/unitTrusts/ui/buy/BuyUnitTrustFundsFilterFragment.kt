/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.unitTrusts.ui.buy

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.barclays.absa.banking.R
import kotlinx.android.synthetic.main.buy_unit_trust_funds_filter_fragment.*

class BuyUnitTrustFundsFilterFragment : BuyUnitTrustBaseFragment(R.layout.buy_unit_trust_funds_filter_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.unit_trust_funds_toolbar_title))
        hostActivity.trackCurrentFragment("WIMI_UT_BuyNew_SeeFunds")

        buyUnitTrustViewModel.selectedFilterOption.value?.let {
            unitTrustFundsFilterRadioButtonView.setDataSource(buyUnitTrustViewModel.createFilterSelectorList(), it.first)
        }

        unitTrustFundsFilterRadioButtonView.setItemCheckedInterface { selectedIndex ->
            if (buyUnitTrustViewModel.isBuyNewFund) {
                when (selectedIndex) {
                    0 -> hostActivity.trackEvent("UTBuyNewFund_FilterFundsScreen_AllFundsSelected")
                    1 -> hostActivity.trackEvent("UTBuyNewFund_FilterFundsScreen_CoreFundsSelected")
                    2 -> hostActivity.trackEvent("UTBuyNewFund_FilterFundsScreen_LowFundsSelected")
                    3 -> hostActivity.trackEvent("UTBuyNewFund_FilterFundsScreen_LowMedFundsSelected")
                    4 -> hostActivity.trackEvent("UTBuyNewFund_FilterFundsScreen_MedFundsSelected")
                    5 -> hostActivity.trackEvent("UTBuyNewFund_FilterFundsScreen_MedHighFundsSelected")
                    6 -> hostActivity.trackEvent("UTBuyNewFund_FilterFundsScreen_HighFundsSelected")
                }
            } else {
                when (selectedIndex) {
                    0 -> hostActivity.trackFragmentAction("WIMI_UT_BuyNew_Filter_Funds", "WIMI_UT_BuyNew_SeeAllFunds")
                    1 -> hostActivity.trackFragmentAction("WIMI_UT_BuyNew_Filter_Funds", "WIMI_UT_BuyNew_SeeCoreFunds")
                    2 -> hostActivity.trackFragmentAction("WIMI_UT_BuyNew_Filter_Funds", "WIMI_UT_BuyNew_SeeLowFunds")
                    3 -> hostActivity.trackFragmentAction("WIMI_UT_BuyNew_Filter_Funds", "WIMI_UT_BuyNew_SeeLowMedFunds")
                    4 -> hostActivity.trackFragmentAction("WIMI_UT_BuyNew_Filter_Funds", "WIMI_UT_BuyNew_SeeMedFunds")
                    5 -> hostActivity.trackFragmentAction("WIMI_UT_BuyNew_Filter_Funds", "WIMI_UT_BuyNew_SeeMedHighFunds")
                    6 -> hostActivity.trackFragmentAction("WIMI_UT_BuyNew_Filter_Funds", "WIMI_UT_BuyNew_SeeHighFunds")
                }
            }
            buyUnitTrustViewModel.selectedFilterOption.value = Pair(selectedIndex, unitTrustFundsFilterRadioButtonView.selectedValue?.displayValue ?: "")
            findNavController().popBackStack()
        }
    }
}