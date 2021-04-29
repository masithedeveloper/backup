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

package com.barclays.absa.banking.unitTrusts.ui.funds

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.unitTrusts.ui.buy.BuyUnitTrustHostActivity
import com.barclays.absa.banking.unitTrusts.ui.buy.BuyUnitTrustViewModel
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.buy_new_fund_accounts_fragment.*

class BuyNewFundAccountsFragment : BaseFragment(R.layout.buy_new_fund_accounts_fragment) {
    private lateinit var buyUnitTrustViewModel: BuyUnitTrustViewModel
    private lateinit var hostActivity: BuyUnitTrustHostActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as BuyUnitTrustHostActivity
        buyUnitTrustViewModel = hostActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hostActivity.trackEvent("UTBuyNewFund_AccountScreen_ScreenDisplayed")
        setToolBar(R.string.buy_unit_trust_choose_unit_trust_account_toolbar_title)

        unitTrustAccountsRadioButtonView.setDataSource(buyUnitTrustViewModel.accountsSelectorList, buyUnitTrustViewModel.selectedUnitTrustAccountIndex)
        unitTrustAccountsRadioButtonView.setItemCheckedInterface { index ->
            buyUnitTrustViewModel.onUnitTrustAccountSelected(index)
            Navigation.findNavController(view).navigate(R.id.action_accountsFragment_to_buyUnitTrustFundsFragment)
        }
    }
}