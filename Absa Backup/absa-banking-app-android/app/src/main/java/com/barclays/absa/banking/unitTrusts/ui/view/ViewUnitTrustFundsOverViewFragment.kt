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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.ViewUnitTrustFundsOverviewFragmentBinding
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustFund
import com.barclays.absa.utils.TextFormatUtils
import com.barclays.absa.banking.shared.ItemPagerFragment

class ViewUnitTrustFundsOverViewFragment : ItemPagerFragment() {
    lateinit var binding: ViewUnitTrustFundsOverviewFragmentBinding

    override fun getTabDescription(): String = arguments?.getString(TAB_DESCRIPTION_KEY) ?: ""

    companion object {
        private const val FUND_PARCEL = "fundParcel"

        fun newInstance(unitTrustFund: UnitTrustFund, description: String): ViewUnitTrustFundsOverViewFragment {
            val viewUnitTrustFundsOverViewFragment = ViewUnitTrustFundsOverViewFragment()
            val arguments = Bundle()
            arguments.putParcelable(FUND_PARCEL, unitTrustFund)
            arguments.putString(TAB_DESCRIPTION_KEY, description)
            viewUnitTrustFundsOverViewFragment.arguments = arguments
            return viewUnitTrustFundsOverViewFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.view_unit_trust_funds_overview_fragment, container, false)
        return binding.root
    }

    @Override
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fund = arguments?.getParcelable(FUND_PARCEL) as UnitTrustFund?
        fund?.let {
            binding.accountBalanceLineItemView.setLineItemViewContent(TextFormatUtils.formatBasicAmountAsRand(it.fundBalance))
            binding.accountUnitsLineItemView.setLineItemViewContent(TextFormatUtils.formatBasicAmount(it.fundUnits))
            binding.availableUnitsLineItemView.setLineItemViewContent(TextFormatUtils.formatBasicAmount(it.fundAvailablelUnits))
        }
    }
}