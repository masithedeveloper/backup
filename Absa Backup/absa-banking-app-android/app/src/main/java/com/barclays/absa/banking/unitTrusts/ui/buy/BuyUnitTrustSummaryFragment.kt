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
import com.barclays.absa.banking.unitTrusts.ui.buy.BuyUnitTrustTaxFragment.Companion.YES
import com.barclays.absa.utils.TextFormatUtils
import com.barclays.absa.utils.TextFormatUtils.formatBasicAmount
import kotlinx.android.synthetic.main.buy_unit_trust_summary_screen.*

class BuyUnitTrustSummaryFragment : BuyUnitTrustBaseFragment(R.layout.buy_unit_trust_summary_screen) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.summary))
        hostActivity.apply {
            setCurrentProgress(3)
            if (buyUnitTrustViewModel.isBuyNewFund) {
                hostActivity.trackEvent("UTBuyNewFund_SummaryScreen_ScreenDisplayed")
            } else {
                trackCurrentFragment("WIMI_UT_BuyNew_Step3_Summary")
            }
        }

        val unitTrustFund = buyUnitTrustViewModel.unitTrustAccountInfo.unitTrustFund
        continueButton.setOnClickListener {
            navigate(BuyUnitTrustSummaryFragmentDirections.actionBuyUnitTrustSummaryFragmentToBuyUnitTrustTermsOfUseFragment(unitTrustFund))
        }
        initView()
    }

    private fun initView() {
        val unitTrustAccountInfo = buyUnitTrustViewModel.unitTrustAccountInfo
        val unitTrustFund = unitTrustAccountInfo.unitTrustFund
        val debitOrderInfo = unitTrustAccountInfo.debitOrderInfo
        val taxInfo = unitTrustAccountInfo.taxInfo
        val lumpSum = unitTrustAccountInfo.lumpSumInfo
        val incomeDistributionAccountInfo = unitTrustAccountInfo.incomeDistributionAccountInfo
        val incomeFromInvestment = unitTrustAccountInfo.incomeFromInvestment
        val sourceOfFunds = unitTrustAccountInfo.sourceOfFunds
        val employmentStatus = unitTrustAccountInfo.employmentStatus
        val occupation = unitTrustAccountInfo.occupation

        if (unitTrustFund.fundName != null) {
            selectedFundContentAndLabelView.apply {
                visibility = View.VISIBLE
                setContentText(unitTrustFund.fundName)
            }
        }

        incomeFromInvestmentContentAndLabelView.apply {
            visibility = View.VISIBLE
            setContentText(incomeFromInvestment)
        }

        selectedAccountContentAndLabelView.apply {
            visibility = if (incomeFromInvestment == getString(R.string.buy_unit_trust_tax_reinvest_into_fund)) View.GONE else View.VISIBLE
            setContentText(incomeDistributionAccountInfo.description + " - " + incomeDistributionAccountInfo.incomeAccountsNumber)
        }

        if (debitOrderInfo != null) {
            investmentMethodContentAndLabelView.visibility = View.VISIBLE
            debitOrderAccountContentAndLabelView.visibility = View.VISIBLE
            debitDayContentAndLabelView.visibility = View.VISIBLE
            debitOrderAmountContentAndLabelView.visibility = View.VISIBLE
            yearlyIncreaseContentAndLabelView.visibility = View.VISIBLE
            investmentMethodContentAndLabelView.setContentText(getString(R.string.buy_unit_trust_debit_oder))
            debitOrderAccountContentAndLabelView.setContentText(debitOrderInfo.accountInfo?.description + " - " + debitOrderInfo.accountInfo?.accountNumber)
            debitDayContentAndLabelView.setContentText(debitOrderInfo.debitDay)
            debitOrderAmountContentAndLabelView.setContentText(TextFormatUtils.formatBasicAmountAsRand(debitOrderInfo.amount))
            yearlyIncreaseContentAndLabelView.setContentText(debitOrderInfo.autoIncreasePercentage + "%")
        }

        if (lumpSum != null) {
            investmentMethodContentAndLabelView.visibility = View.VISIBLE
            investmentMethodContentAndLabelView.setContentText(getString(R.string.buy_unit_trust_lump_sum))
            lumpSumAmountContentAndLabelView.visibility = View.VISIBLE
            lumpSumAmountContentAndLabelView.setContentText(String.format("R %s", formatBasicAmount(lumpSum.amount)))
        }

        if (sourceOfFunds != null) {
            sourceOfFundsContentAndLabelView.visibility = View.VISIBLE
            sourceOfFundsContentAndLabelView.setContentText(sourceOfFunds.defaultLabel)
        }

        if (employmentStatus.isNotEmpty()) {
            employmentStatusContentAndLabelView.visibility = View.VISIBLE
            employmentStatusContentAndLabelView.setContentText(employmentStatus)
        }

        if (occupation.isNotEmpty()) {
            occupationContentAndLabelView.visibility = View.VISIBLE
            occupationContentAndLabelView.setContentText(occupation)
        }

        if (buyUnitTrustViewModel.isBuyNewFund) {
            registeredForTaxSAContentAndLabelView.visibility = View.GONE
            saIncomeTaxNumberContentAndLabelView.visibility = View.GONE
            registeredForForeignTaxContentAndLabelView.visibility = View.GONE
            foreignTaxNumberContentAndLabelView.visibility = View.GONE
            foreignTaxCountryContentAndLabelView.visibility = View.GONE
        }

        if (!buyUnitTrustViewModel.isBuyNewFund) {
            if (YES.equals(taxInfo.isRegisteredForSATax, true)) {
                registeredForTaxSAContentAndLabelView.setContentText(getString(R.string.yes))
                if (!taxInfo.saTaxNumber.isNullOrEmpty()) {
                    saIncomeTaxNumberContentAndLabelView.visibility = View.VISIBLE
                    saIncomeTaxNumberContentAndLabelView.setContentText(taxInfo.saTaxNumber)
                }
            } else {
                registeredForTaxSAContentAndLabelView.setContentText(getString(R.string.no))
                registeredForForeignTaxContentAndLabelView.visibility = View.VISIBLE
                if (YES.equals(taxInfo.isRegisteredForForeignTax, true)) {
                    registeredForForeignTaxContentAndLabelView.setContentText(getString(R.string.yes))
                    if (!taxInfo.foreignTaxNumber.isNullOrEmpty()) {
                        foreignTaxNumberContentAndLabelView.visibility = View.VISIBLE
                        foreignTaxNumberContentAndLabelView.setContentText(taxInfo.foreignTaxNumber)
                    }

                    if (!taxInfo.foreignTaxCountry.isNullOrEmpty()) {
                        foreignTaxCountryContentAndLabelView.visibility = View.VISIBLE
                        foreignTaxCountryContentAndLabelView.setContentText(taxInfo.foreignTaxCountry)
                    }
                } else {
                    registeredForForeignTaxContentAndLabelView.setContentText(getString(R.string.no))
                }
            }
        }
    }
}
