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
package com.barclays.absa.banking.unitTrusts.ui.view

import android.content.Context
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.ViewUnitTrustBuyMoreUnitStepTwoFragmentBinding
import com.barclays.absa.banking.framework.AbsaBaseFragment
import com.barclays.absa.utils.TextFormatUtils
import com.barclays.absa.utils.viewModel
import styleguide.utils.extensions.toTitleCase

class ViewUnitTrustBuyMoreUnitStepTwoFragment : AbsaBaseFragment<ViewUnitTrustBuyMoreUnitStepTwoFragmentBinding>() {

    private lateinit var viewUnitTrustViewModel: ViewUnitTrustViewModel

    override fun getLayoutResourceId(): Int = R.layout.view_unit_trust_buy_more_unit_step_two_fragment

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewUnitTrustViewModel = (context as ViewUnitTrustHostActivity).viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        displayData()
        binding.continueButton.setOnClickListener {
            navigate(ViewUnitTrustBuyMoreUnitStepTwoFragmentDirections.actionViewUnitTrustBuyMoreUnitSteptwoFragmentToViewUnitTrustBuyMoreUnitStepThreeFragment())
        }
    }

    private fun setUpToolbar() {
        val hostActivity = activity as ViewUnitTrustHostActivity
        hostActivity.apply {
            setToolBar(getString(R.string.summary))
            showProgressIndicator()
            setStep(2)
        }
    }

    private fun displayData() {
        val accountHolder = viewUnitTrustViewModel.buyMoreUnitsData.value?.accountHolder
        val accountNumber = viewUnitTrustViewModel.buyMoreUnitsData.value?.accountNumber
        val buyMoreUnitsLumpSumInfo = viewUnitTrustViewModel.buyMoreUnitsData.value?.buyMoreUnitsLumpSumInfo
        val fund = viewUnitTrustViewModel.buyMoreUnitsData.value?.fund

        binding.accountHolderContentAndLabelView.setContentText(accountHolder.toTitleCase())
        binding.accountNumberContentAndLabelView.setContentText(accountNumber)
        binding.debitOrderAmountContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRand(buyMoreUnitsLumpSumInfo?.amount))
        binding.debitOrderAccountContentAndLabelView.setContentText(buyMoreUnitsLumpSumInfo?.accountInfo?.displayValue)
        binding.selectedFundTitleAndDescriptionView.title = fund?.fundName.toTitleCase()
    }
}