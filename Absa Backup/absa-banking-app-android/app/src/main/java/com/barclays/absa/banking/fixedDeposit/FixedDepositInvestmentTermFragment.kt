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
import android.view.View
import android.widget.SeekBar
import com.barclays.absa.banking.R
import com.barclays.absa.banking.fixedDeposit.FixedDepositActivity.Companion.getMonthFromDays
import com.barclays.absa.banking.fixedDeposit.FixedDepositActivity.Companion.getMonthToDays
import com.barclays.absa.banking.fixedDeposit.responseListeners.FixedDepositViewModel
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.extensions.toLowerCase
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.investment_term_fragment.*
import styleguide.utils.AnimationHelper

class FixedDepositInvestmentTermFragment : InvestmentTermBaseFragment<FixedDepositViewModel>(R.layout.investment_term_fragment) {

    companion object {
        private const val MINIMUM_MONTH_EXTRA: String = "minimumMonthExtra"
        private const val MAX_DAYS: Int = 1826
        private const val MIN_DAYS: Int = 8
        private const val MAX_MONTHS: Int = 60
        private const val MIN_MONTHS: Int = 0
        private const val INCREMENT_STEPS = 20
    }

    override var maxMonths = MAX_MONTHS
    override var minMonths = MIN_MONTHS
    override var maxDays = MAX_DAYS
    override var minDays = MIN_DAYS
    override var viewModel = baseActivity.viewModel() as FixedDepositViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsUtil.trackAction(FixedDepositActivity.FIXED_DEPOSIT, "Fixed Deposit Investment term Screen")

        minMonths = arguments?.getInt(MINIMUM_MONTH_EXTRA, 0) ?: 0

        setupToolbar()
    }

    override fun setUpRadioView() {
        super.setUpRadioView()

        monthDayRadioButtonView.setItemCheckedInterface {
            clearFocusFromInvestmentTermView()

            investmentTermSliderView.incrementSize = (MAX_MONTHS) / INCREMENT_STEPS
            investmentTermSliderView.setMax(MAX_MONTHS)

            seekBarMinimum = if (minMonths == 0) {
                1
            } else {
                minMonths
            }
            seekBarRestriction = seekBarMinimum

            if (monthDayRadioButtonView.selectedIndex == 0) {
                if (seekBarMinimum == 1) {
                    investmentTermSliderView.setStartText("$seekBarMinimum ${getString(R.string.fixed_deposit_month)}".toLowerCase())
                } else {
                    investmentTermSliderView.setStartText("$seekBarMinimum ${getString(R.string.fixed_deposit_months)}".toLowerCase())
                }

                investmentTermSliderView.setEndText("$MAX_MONTHS ${getString(R.string.fixed_deposit_months)}".toLowerCase())
            } else {
                seekBarRestriction = if (minMonths == 0) {
                    minDays
                } else {
                    getMonthToDays(viewModel.dayTableList, minMonths)
                }
                investmentTermSliderView.setStartText("$seekBarRestriction ${getString(R.string.fixed_deposit_days)}".toLowerCase())
                investmentTermSliderView.setEndText("$MAX_MONTHS ${getString(R.string.fixed_deposit_months)}".toLowerCase())
            }
            investmentTermSliderView.setProgress(0)
            investmentTermNormalInputView.clearError()
            updateInvestmentView(0)
        }

        monthDayRadioButtonView.selectedIndex = 0
    }

    override fun setupViews() {
        investmentTermSliderView.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, p2: Boolean) {
                if (isTrackChange) {
                    investmentTermNormalInputView.clearError()

                    var position = progress + seekBarMinimum

                    if (position > MAX_MONTHS) {
                        position = MAX_MONTHS
                    }

                    position /= investmentTermSliderView.incrementSize
                    position *= investmentTermSliderView.incrementSize

                    if (monthDayRadioButtonView.selectedIndex == 1) {
                        updateInvestmentView(getMonthToDays(viewModel.dayTableList, position))
                    } else {
                        updateInvestmentView(position)
                    }
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                isTrackChange = true
                clearFocusFromInvestmentTermView()
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                isTrackChange = false
            }
        })

        investmentTermNormalInputView.addValueViewTextWatcher(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                if (isSelfChange) {
                    isSelfChange = false
                    return
                }

                if (editable.isNullOrEmpty()) {
                    suffix = getSuffix(0)
                    investmentTermNormalInputView.setError(getString(R.string.fixed_deposit_minimum_of, seekBarRestriction, suffix))
                    return
                }

                if (!isTrackChange && !editable.contains(" ") && editable.length < 10) {
                    var currentValue = editable.toString().toInt()
                    suffix = getSuffix(currentValue)

                    val maxTerm = if (monthDayRadioButtonView.selectedIndex == 0) {
                        MAX_MONTHS
                    } else {
                        MAX_DAYS
                    }

                    when {
                        currentValue < seekBarRestriction -> investmentTermNormalInputView.setError(getString(R.string.fixed_deposit_minimum_of, seekBarRestriction, suffix))
                        currentValue > (maxTerm) -> investmentTermNormalInputView.setError(getString(R.string.fixed_deposit_maximum_of, maxTerm, suffix))
                        else -> {
                            suffix = if (currentValue == 1) getSuffix(0) else getSuffix(currentValue)
                            if (monthDayRadioButtonView.selectedIndex == 1 && !viewModel.interestRateInfo.value?.interestRateTable.isNullOrEmpty()) {
                                currentValue = getMonthFromDays(viewModel.interestRateInfo.value?.interestRateTable, currentValue)
                            }
                            investmentTermSliderView.setProgress(currentValue)
                            investmentTermNormalInputView.clearError()
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        doneButton.setOnClickListener {
            clearFocusFromInvestmentTermView()
            if (!investmentTermNormalInputView.hasError()) {
                viewModel.investmentTerm.value = investmentTermNormalInputView.selectedValue
                activity?.onBackPressed()
            } else {
                AnimationHelper.shakeShakeAnimate(investmentTermNormalInputView)
            }
        }
    }

    private fun setupToolbar() = when (baseActivity) {
        is FixedDepositActivity -> {
            with(baseActivity as FixedDepositActivity) {
                setToolbarTitle(getString(R.string.fixed_deposit_investment_term))
                showToolbarBackArrow()
                showToolbar()
            }
        }
        else -> baseActivity.setToolBarBack(getString(R.string.fixed_deposit_investment_term)) { baseActivity.onBackPressed() }
    }
}