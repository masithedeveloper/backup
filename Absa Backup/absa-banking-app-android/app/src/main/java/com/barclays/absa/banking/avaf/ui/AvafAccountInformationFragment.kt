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
package com.barclays.absa.banking.avaf.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.express.avaf.accountInformation.dto.AbsaVehicleAndAssetFinanceDetail
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.TextFormatUtils
import kotlinx.android.synthetic.main.avaf_account_detail_fragment.*
import styleguide.bars.FragmentPagerItem

class AvafAccountInformationFragment : FragmentPagerItem() {
    private val avafHubViewModel by activityViewModels<AvafHubViewModel>()

    companion object {
        @JvmStatic
        fun newInstance(description: String): AvafAccountInformationFragment {
            val avafAccountInformationFragment = AvafAccountInformationFragment()
            return avafAccountInformationFragment.apply {
                arguments = Bundle().apply { putString(TAB_DESCRIPTION_KEY, description) }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.avaf_account_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        avafHubViewModel.avafAccountDetailLiveData.observe(viewLifecycleOwner, {
            displayAvafAccountInformation(it.absaVehicleAndAssetFinanceDetail)
        })
    }

    private fun displayAvafAccountInformation(absaVehicleAndAssetFinanceDetail: AbsaVehicleAndAssetFinanceDetail) {
        absaVehicleAndAssetFinanceDetail.apply {
            makeAndModelSecondaryContentAndLabelView.setContentText(makeAndModel)
            paymentMethodLineItemView.setLineItemViewContent(paymentMethod)
            originalFinanceAmountLineItemView.setLineItemViewContent(TextFormatUtils.formatBasicAmountAsRand(originalFinanceAmount))
            contractStartDateLineItemView.setLineItemViewContent(DateUtils.formatDateMonth(contractStartDate))
            contractEndDateLineItemView.setLineItemViewContent(DateUtils.formatDateMonth(contractEndDate))
            paymentFrequencyLineItemView.setLineItemViewContent(paymentFrequency)
            termLineItemView.setLineItemViewContent("$originalTerm ${getString(R.string.personal_loan_hub_months)}")
            remainingTermLineItemView.setLineItemViewContent("$remainingTerm ${getString(R.string.personal_loan_hub_months)}")
            installmentAmountLineItemView.setLineItemViewContent(TextFormatUtils.formatBasicAmountAsRand(installmentAmount))
            nextInstalmentDateLineItemView.setLineItemViewContent(if (nextInstallmentDate.isNotEmpty()) {
                DateUtils.formatDateMonth(nextInstallmentDate)
            } else {
                getString(R.string.avaf_current_balance_not_available)
            })
            residualValueLineItemView.setLineItemViewContent(TextFormatUtils.formatBasicAmountAsRand(residualValue))
            interesetRateLineItemView.setLineItemViewContent("$interestRate %")
            interesetRateTypeLineItemView.setLineItemViewContent(interestRateType)
            estimatedSettlementLineItemView.setLineItemViewContent(TextFormatUtils.formatBasicAmountAsRand(estimatedSettlementValue))
        }
    }

    override fun getTabDescription() = arguments?.getString(TAB_DESCRIPTION_KEY) ?: ""

}
