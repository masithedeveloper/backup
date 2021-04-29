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
package com.barclays.absa.banking.unitTrusts.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.home.ui.IHomeCacheService
import com.barclays.absa.banking.shared.ItemPagerFragment
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustAccount
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustFund
import com.barclays.absa.banking.unitTrusts.ui.ListSorter
import kotlinx.android.synthetic.main.view_unit_trust_recyclerview.*

class ViewUnitTrustAccountsFragment : ItemPagerFragment(R.layout.view_unit_trust_recyclerview), ViewUnitTrustFundsAdapter.UnitTrustItemClickListener {

    companion object {
        private const val FUND_PARCEL = "fundParcel"
        const val LIST_OF_FUNDS_KEY = "mylist"
        const val UNIT_TRUST_ACCOUNT_NUMBER = "accountNumber"
        const val UNIT_TRUST_ACCOUNT_HOLDER = "accountHolder"

        fun newInstance(description: String): ViewUnitTrustAccountsFragment = ViewUnitTrustAccountsFragment().apply {
            arguments = Bundle().apply {
                putString(TAB_DESCRIPTION_KEY, description)
            }
        }
    }

    private var viewUnitTrustAccounts: List<UnitTrustAccount> = listOf()
    private val homeCacheService: IHomeCacheService = getServiceInterface()
    private val unitTrustResponseModel by lazy { homeCacheService.getUnitTrustResponseModel() }

    override fun getTabDescription(): String = arguments?.getString(TAB_DESCRIPTION_KEY) ?: ""

    @Override
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewUnitTrustAccounts = unitTrustResponseModel?.unitTrustAccounts.orEmpty()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (viewUnitTrustAccounts.isNotEmpty()) {
            for (unitTrustAccount in viewUnitTrustAccounts) {
                unitTrustAccount.unitTrustFundList = ListSorter.sortUnitTrustFunds(unitTrustAccount.unitTrustFundList)
            }
        }
        unitTrustRecyclerView.adapter = ViewUnitTrustFundsAdapter(viewUnitTrustAccounts, this)
    }

    override fun onShowMoreClicked(fundsList: List<UnitTrustFund>, accountNumber: String, accountHolder: String) = startActivity(Intent(activity, ViewMoreFundAccountsActivity::class.java).apply {
        putExtras(Bundle().apply {
            putParcelableArrayList(LIST_OF_FUNDS_KEY, ArrayList(fundsList))
            putString(UNIT_TRUST_ACCOUNT_NUMBER, accountNumber)
            putString(UNIT_TRUST_ACCOUNT_HOLDER, accountHolder)
        })
    })

    override fun onFundItemClicked(unitTrustFund: UnitTrustFund, accountNumber: String, accountHolder: String) = startActivity(Intent(activity, ViewUnitTrustFundBaseActivity::class.java).apply {
        putExtra(FUND_PARCEL, unitTrustFund)
        putExtra(UNIT_TRUST_ACCOUNT_NUMBER, accountNumber)
        putExtra(UNIT_TRUST_ACCOUNT_HOLDER, accountHolder)
    })
}