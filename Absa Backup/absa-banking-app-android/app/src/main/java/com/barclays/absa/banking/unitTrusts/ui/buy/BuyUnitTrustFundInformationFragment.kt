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
import com.barclays.absa.banking.R
import com.barclays.absa.utils.PdfUtil
import com.barclays.absa.utils.TextFormatUtils
import kotlinx.android.synthetic.main.buy_unit_trust_fund_information.*

class BuyUnitTrustFundInformationFragment : BuyUnitTrustBaseFragment(R.layout.buy_unit_trust_fund_information) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.buy_unit_trust_fund_information_title))

        hostActivity.hideStepIndicator()
        bindDataToViews()
        if (buyUnitTrustViewModel.isBuyNewFund) {
            hostActivity.trackEvent("UTBuyNewFund_FundInformationScreen_ScreenDisplayed")
        } else {
            hostActivity.trackCurrentFragment("WIMI_UT_BuyNew_ViewFundInfo")
        }
    }

    private fun bindDataToViews() {
        val unitTrustFund = buyUnitTrustViewModel.unitTrustAccountInfo.unitTrustFund
        buyTypeTitleTextView.text = unitTrustFund.fundName
        findDescriptionTextView.text = unitTrustFund.fundObjective
        if (unitTrustFund.fundSuitableFor == null) {
            investmentMarketArticleCardView.visibility = View.GONE
        } else {
            investmentMarketArticleCardView.setDescription(unitTrustFund.fundSuitableFor)
        }

        if (unitTrustFund.fundRisk != null && unitTrustFund.fundTerm != null) {
            investmentPeriodArticleCardView.setDescription(getString(R.string.buy_unit_trust_find_information_investment_period, unitTrustFund.fundRisk, unitTrustFund.fundTerm))
        } else {
            investmentPeriodArticleCardView.visibility = View.GONE
        }

        if (unitTrustFund.minLumpSumAmount != null && unitTrustFund.minDebitOrderAmount != null) {
            contributionArticleCardView.setDescription(getString(R.string.buy_unit_trust_fund_information_investment_amount, TextFormatUtils.formatBasicAmount(unitTrustFund.minLumpSumAmount!!), TextFormatUtils.formatBasicAmount(unitTrustFund.minDebitOrderAmount!!)))
        } else {
            contributionArticleCardView.visibility = View.GONE
        }

        viewMDDTextView.setOnClickListener {
            unitTrustFund.fundPdfUrl?.let { PdfUtil.showPDFInApp(hostActivity, it) }
        }

        investInFundButton.setOnClickListener {
            navigate(BuyUnitTrustFundInformationFragmentDirections.actionBuyUnitTrustFundInformationToBuyUnitTrustInvestmentMethodFragment())
        }
    }
}