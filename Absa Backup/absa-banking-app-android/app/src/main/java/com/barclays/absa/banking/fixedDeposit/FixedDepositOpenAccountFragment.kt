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

package com.barclays.absa.banking.fixedDeposit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.fixedDeposit.FixedDepositActivity.Companion.getMonthToDays
import com.barclays.absa.banking.fixedDeposit.responseListeners.FixedDepositViewModel
import com.barclays.absa.banking.fixedDeposit.services.dto.InterestRateTable
import com.barclays.absa.banking.fixedDeposit.services.dto.TermDepositInterestRateDayTable
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.TextFormatUtils
import com.barclays.absa.utils.viewModel
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fixed_deposit_open_account_fragment.*
import styleguide.content.LineItemView
import styleguide.utils.extensions.toDoubleStringPercentage
import styleguide.utils.extensions.toTitleCase
import kotlin.math.pow

class FixedDepositOpenAccountFragment : BaseFragment(R.layout.fixed_deposit_open_account_fragment), FixedDepositView {

    private lateinit var viewModel: FixedDepositViewModel
    private var scrollRange: Float = 0.0f
    private val minimumAmount: Float = 1000f

    companion object {
        private const val SENIOR_AGE: Int = 55
        private const val ADDITIONAL_INTEREST: Double = 0.5
        private const val DEPOSITS_UP_TO: Int = 100000

        const val FIXED_DEPOSIT_PRODUCT_CODE = "03040"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsUtil.trackAction(FixedDepositActivity.FIXED_DEPOSIT, "Fixed Deposit Open Account Screen")

        initialiseViewModel()
        setHasOptionsMenu(true)
        setUpToolbar()

        if (isBusinessAccount) {
            bullet5ItemView.visibility = View.GONE
        } else {
            bullet5ItemView.setContentTextView(getString(R.string.fixed_deposit_investment_bullet_5, SENIOR_AGE, ADDITIONAL_INTEREST, TextFormatUtils.formatBasicAmountAsRandNoDecimal(DEPOSITS_UP_TO)))
        }

        fixedDepositTextView.requestFocus()

        setUpComponentListeners()

        investmentAmountNormalInputView.selectedValue =
                if (::viewModel.isInitialized && viewModel.fixedDepositData.amount.toString() != "0.00")
                    "" else viewModel.fixedDepositData.amount.toString()
    }

    private fun setUpComponentListeners() {
        appbarLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                scrollRange = appbarLayout.totalScrollRange.toFloat()
                appbarLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        appbarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            if (verticalOffset == 0) {
                animateTitleViews(1f)
            } else {
                val invertedVerticalOffset = scrollRange + verticalOffset
                val collapsePercent = Math.pow((invertedVerticalOffset / scrollRange).toDouble(), 1.0).toFloat()
                animateTitleViews(collapsePercent)
            }
        })

        investmentTermNormalInputView.setOnClickListener {
            navigate(FixedDepositOpenAccountFragmentDirections.actionFixedDepositOpenAccountFragmentToFixedDepositInvestmentTermFragment(0))
        }

        viewModel.failureResponse.observe(viewLifecycleOwner, {
            val baseActivity = activity as? BaseActivity
            baseActivity?.let { base ->
                it.transactionMessage?.let { transactionMessage -> base.showMessageError(transactionMessage) { _, _ -> base.finish() } }
            }
        })

        viewModel.investmentTerm.removeObservers(viewLifecycleOwner)
        viewModel.investmentTerm.observe(viewLifecycleOwner, { investmentTerm ->
            investmentTerm?.let {
                investmentTermNormalInputView.selectedValue = it
                calculateRateTable()
            }
        })

        investmentAmountNormalInputView.addValueViewTextWatcher(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (investmentAmountNormalInputView.selectedValueUnmasked.isNotEmpty() && investmentAmountNormalInputView.selectedValueUnmasked.toDouble() >= minimumAmount) {
                    calculateRateTable()
                }
                investmentAmountNormalInputView.clearError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        openFixedDepositButton.setOnClickListener {
            if (investmentAmountNormalInputView.selectedValueUnmasked.isEmpty()) {
                investmentAmountNormalInputView.setError(getString(R.string.international_payments_enter_an_amount))
                nestedScrollView.post { nestedScrollView.scrollTo(0, investmentAmountNormalInputView.y.toInt()) }
            } else if (investmentAmountNormalInputView.selectedValueUnmasked.toDouble() < minimumAmount) {
                investmentAmountNormalInputView.setError(getString(R.string.min_amount_error_message, minimumAmount))
                nestedScrollView.post { nestedScrollView.scrollTo(0, investmentAmountNormalInputView.y.toInt()) }
            } else if (investmentTermNormalInputView.selectedValue.isEmpty()) {
                investmentTermNormalInputView.setError(getString(R.string.fixed_deposit_select_investment_term))
                nestedScrollView.post { nestedScrollView.scrollTo(0, investmentTermNormalInputView.y.toInt()) }
            } else {
                viewModel.fixedDepositData.amount = Amount(investmentAmountNormalInputView.selectedValueUnmasked)

                viewModel.createAccountResponse.observe(viewLifecycleOwner, {
                    dismissProgressDialog()
                    navigate(FixedDepositOpenAccountFragmentDirections.actionFixedDepositOpenAccountFragmentToRiskBasedApproachPersonalDetailsFragment(
                            FIXED_DEPOSIT_PRODUCT_CODE, R.id.action_riskBasedApproachPersonalDetailsFragment_to_fixedDepositNewFixedDepositFragment))

                    viewModel.createAccountResponse.removeObservers(viewLifecycleOwner)
                })

                viewModel.createAccount()
            }
        }

        informationOptionActionButtonView.setOnClickListener {
            navigate(FixedDepositOpenAccountFragmentDirections.actionFixedDepositOpenAccountFragmentToFixedDepositTermsAndConditionsFragment(false))
        }
    }

    private fun calculateRateTable() {
        if (investmentAmountNormalInputView.selectedValueUnmasked.isEmpty() || investmentTermNormalInputView.selectedValue.isEmpty()) {
            return
        }

        val currentAmount = investmentAmountNormalInputView.selectedValueUnmasked.toDouble()
        val selectedValue = investmentTermNormalInputView.selectedValue
        val days: Int

        val term = selectedValue.substring(0, selectedValue.indexOf(" ")).trim().toInt()
        days = if (selectedValue.contains(getString(R.string.fixed_deposit_month))) {
            getMonthToDays(viewModel.dayTableList, term)
        } else {
            term
        }

        if (viewModel.interestRateInfo.value!!.interestRateTable != null) {
            for (item: InterestRateTable? in viewModel.interestRateInfo.value!!.interestRateTable!!) {
                val (minAmountLower, maxAmountLower) = getMinMaxAmount(item?.termDepositAmountRangeMin!!)

                if (currentAmount in minAmountLower..maxAmountLower) {
                    if (item.termDepositAmountRangeMinInterestTable != null) {
                        setUpTable(item.termDepositAmountRangeMinInterestTable, days)
                    }
                } else {
                    val (minAmountUpper, maxAmountUpper) = getMinMaxAmount(item.termDepositAmountRangeMax!!)
                    if (currentAmount in minAmountUpper..maxAmountUpper) {
                        if (item.termDepositAmountRangeMaxInterestTable != null) {
                            setUpTable(item.termDepositAmountRangeMaxInterestTable, days)
                        }
                        break
                    }
                }
            }
        }
    }

    private fun setUpTable(dayTables: List<TermDepositInterestRateDayTable>, days: Int) {
        for (dayTable: TermDepositInterestRateDayTable? in dayTables) {
            val daysFrom = dayTable?.daysFrom?.toInt()

            val daysTo = dayTable?.daysTo?.toInt()
            if (days >= daysFrom!! && days <= daysTo!!) {
                setUpTable(dayTable)
                break
            }
        }
    }

    private fun setUpTable(dayTable: TermDepositInterestRateDayTable) {
        interestRateTableLinearLayout.visibility = View.VISIBLE
        interestRateCopy.visibility = View.VISIBLE

        interestRateTableLinearLayout.removeAllViews()

        val context = requireContext()
        val monthlyLineItemView = LineItemView(context)
        val quarterlyLineItemView = LineItemView(context)
        val halfYearlyLineItemView = LineItemView(context)
        val yearlyLineItemView = LineItemView(context)
        val maturityLineItemView = LineItemView(context)

        monthlyLineItemView.getLabelTextView().text = getString(R.string.fixed_deposit_monthly)
        monthlyLineItemView.getContentTextView().text = dayTable.categoryRateMonthly.toDoubleStringPercentage()
        interestRateTableLinearLayout.addView(monthlyLineItemView)

        quarterlyLineItemView.getLabelTextView().text = getString(R.string.fixed_deposit_quarterly)
        quarterlyLineItemView.getContentTextView().text = dayTable.categoryRateQuaterly.toDoubleStringPercentage()
        interestRateTableLinearLayout.addView(quarterlyLineItemView)

        halfYearlyLineItemView.getLabelTextView().text = getString(R.string.fixed_deposit_half_yearly)
        halfYearlyLineItemView.getContentTextView().text = dayTable.categoryRateHalfYearly.toDoubleStringPercentage()
        interestRateTableLinearLayout.addView(halfYearlyLineItemView)

        yearlyLineItemView.getLabelTextView().text = getString(R.string.fixed_deposit_yearly)
        yearlyLineItemView.getContentTextView().text = dayTable.categoryRateAnnually.toDoubleStringPercentage()
        interestRateTableLinearLayout.addView(yearlyLineItemView)

        maturityLineItemView.getLabelTextView().text = getString(R.string.fixed_deposit_maturity)
        maturityLineItemView.getContentTextView().text = dayTable.categoryRateMaturity.toDoubleStringPercentage()
        interestRateTableLinearLayout.addView(maturityLineItemView)

        viewModel.currentRateTable = dayTable
    }

    private fun getMinMaxAmount(amountRange: String): Pair<Double, Double> {
        val amounts = amountRange.replace("R", "").split("-")

        return if (amounts.size > 1) {
            val minAmount = if (amounts[0].isNotEmpty()) amounts[0].toDouble() else 0.0
            val maxAmount = if (amounts[1].isNotEmpty()) amounts[1].toDouble() else 0.0
            Pair(minAmount, maxAmount)
        } else {
            Pair(0.0, 0.0)
        }
    }

    private fun initialiseViewModel() {
        if (!::viewModel.isInitialized) {
            viewModel = baseActivity.viewModel()
        }
    }

    private fun setUpToolbar() {
        val fixedDepositActivity = activity as FixedDepositActivity
        fragmentToolbar.setNavigationIcon(R.drawable.ic_left_arrow_light)
        fixedDepositActivity.setSupportActionBar(fragmentToolbar)
        fixedDepositActivity.apply {
            supportActionBar?.title = getString(R.string.fixed_deposit_account).toTitleCase()
            showToolbar()
        }
    }

    private fun animateTitleViews(alphaPercentage: Float) {
        val minScaleValue = -3.4028235E38
        fixedDepositTextView.apply {
            alpha = alphaPercentage.toDouble().pow(3.0).toFloat()
            if (alphaPercentage < minScaleValue || alphaPercentage == Float.NEGATIVE_INFINITY) {
                scaleX = minScaleValue.toFloat()
                scaleY = minScaleValue.toFloat()
            } else {
                scaleX = alphaPercentage
                scaleY = alphaPercentage
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        activity?.onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}