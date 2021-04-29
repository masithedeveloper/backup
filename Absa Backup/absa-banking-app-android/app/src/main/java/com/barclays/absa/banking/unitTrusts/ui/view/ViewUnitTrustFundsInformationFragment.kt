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
import com.barclays.absa.banking.databinding.ViewUnitTrustFundInformationFragmentBinding
import com.barclays.absa.banking.shared.ItemPagerFragment
import com.barclays.absa.banking.unitTrusts.services.dto.UnitTrustFund
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.PdfUtil

class ViewUnitTrustFundsInformationFragment : ItemPagerFragment() {
    lateinit var binding: ViewUnitTrustFundInformationFragmentBinding

    override fun getTabDescription(): String = arguments?.getString(TAB_DESCRIPTION_KEY) ?: ""

    companion object {
        private const val FUND_PARCEL_KEY = "fundParcel"

        fun newInstance(unitTrustFund: UnitTrustFund, description: String): ViewUnitTrustFundsInformationFragment {
            val viewUnitTrustFundsInformationFragment = ViewUnitTrustFundsInformationFragment()
            viewUnitTrustFundsInformationFragment.arguments = Bundle().apply {
                putParcelable(FUND_PARCEL_KEY, unitTrustFund)
                putString(TAB_DESCRIPTION_KEY, description)
            }
            return viewUnitTrustFundsInformationFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.view_unit_trust_fund_information_fragment, container, false)
        return binding.root
    }

    @Override
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fund = arguments?.getParcelable(FUND_PARCEL_KEY) as UnitTrustFund?
        if (!fund?.fundObjective.isNullOrEmpty() && !fund?.fundPdfUrl.isNullOrEmpty()) {
            binding.fundInformationTextView.text = fund?.fundObjective
            binding.fundFactActionButtonView.setOnClickListener {
                fund?.fundPdfUrl?.let { pdfUrl -> PdfUtil.showPDFInApp((activity as ViewUnitTrustFundBaseActivity), pdfUrl) }
                AnalyticsUtil.trackAction("WIMI_UT_CHANNEL", "WIMI_UT_View_OpenFundSheet")
            }
        } else {
            binding.fundInformationTextView.text = getString(R.string.view_unit_trust_noinfo_message)
            binding.fundFactActionButtonView.visibility = View.GONE
            binding.titleDividerView.visibility = View.GONE
        }
    }
}