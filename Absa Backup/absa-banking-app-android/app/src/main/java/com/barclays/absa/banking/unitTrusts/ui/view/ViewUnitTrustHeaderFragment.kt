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

package com.barclays.absa.banking.unitTrusts.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.home.ui.IHomeCacheService
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustData
import com.barclays.absa.banking.unitTrusts.ui.buy.BuyUnitTrustHostActivity
import com.barclays.absa.banking.unitTrusts.ui.buy.BuyUnitTrustHostActivity.Companion.UNIT_TRUST_DATA
import kotlinx.android.synthetic.main.view_unit_trust_header_fragment.*

class ViewUnitTrustHeaderFragment : BaseFragment(R.layout.view_unit_trust_header_fragment) {
    private val homeCacheService: IHomeCacheService = getServiceInterface()
    private var accountObject: AccountObject? = homeCacheService.getUnitTrustAccountObject()

    companion object {
        fun newInstance(): ViewUnitTrustHeaderFragment {
            return ViewUnitTrustHeaderFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        redeemFundsImageView.visibility = View.GONE
        redeemTextView.visibility = View.GONE
        switchFundImageView.visibility = View.GONE
        switchTextView.visibility = View.GONE
        buyMoreUnitsImageView.visibility = View.GONE
        buyTextView.visibility = View.GONE
        if (BuildConfig.TOGGLE_DEF_BUY_NEW_FUND && featureSwitchingToggles.buyNewUnitTrustFund == FeatureSwitchingStates.ACTIVE.key) {
            buyNewFundButton.visibility = View.VISIBLE
        }
        val balance: Amount? = accountObject?.currentBalance
        balanceTextView.text = balance?.toString()
        coverAmountTitleTextView.text = getString(R.string.current_balance)
        balanceTextView.text = balance?.toString()
        coverAmountTitleTextView.text = getString(R.string.current_balance)

        buyNewFundButton.setOnClickListener {
            val intent = Intent(activity, BuyUnitTrustHostActivity::class.java).apply {
                putExtra(BuyUnitTrustHostActivity.IS_BUY_NEW_FUND, true)
                putExtra(UNIT_TRUST_DATA, UnitTrustData())
            }
            startActivity(intent)
        }
    }
}