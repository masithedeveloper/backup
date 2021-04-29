/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank Limited
 *
 */

package com.barclays.absa.banking.futurePlan

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.SeekBar
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.InvestmentTermFragmentBinding
import com.barclays.absa.banking.fixedDeposit.InvestmentTermBaseFragment
import com.barclays.absa.banking.saveAndInvest.SaveAndInvestViewModel
import com.barclays.absa.utils.extensions.toLowerCase
import com.barclays.absa.utils.extensions.viewBinding
import com.barclays.absa.utils.viewModel
import styleguide.utils.AnimationHelper
import styleguide.utils.extensions.takeLeadingNumbersOrZero
import java.util.*
import java.util.concurrent.TimeUnit

class FuturePlanInvestmentTermFragment : InvestmentTermBaseFragment<SaveAndInvestViewModel>(R.layout.investment_term_fragment) {

    private val binding by viewBinding(InvestmentTermFragmentBinding::bind)

    companion object {
        const val MAX_MONTHS: Int = 60
        private const val INCREMENT_STEPS = 20
    }

    override var viewModel = baseActivity.viewModel() as SaveAndInvestViewModel
    override var minMonths = viewModel.saveAndInvestProductInfo.minimumInvestmentPeriod.takeLeadingNumbersOrZero().toInt()
    override var maxDays = calculateDaysDifference(GregorianCalendar().apply { add(Calendar.MONTH, MAX_MONTHS) })
    override var minDays = viewModel.minimumInvestmentTermInDays
    override var maxMonths = MAX_MONTHS

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.future_plan_investment_term)
    }

    override fun setupViews() {
        binding.investmentTermSliderView.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, p2: Boolean) {
                if (isTrackChange) {
                    binding.investmentTermNormalInputView.clearError()
                    val position = progress + seekBarMinimum
                    updateInvestmentView(position)
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

        with(binding.investmentTermNormalInputView) {
            addValueViewTextWatcher(object : TextWatcher {
                override fun afterTextChanged(editable: Editable?) {
                    val minTerm = if (binding.monthDayRadioButtonView.selectedIndex == 0) minMonths else minDays - 1
                    val maxTerm = if (binding.monthDayRadioButtonView.selectedIndex == 0) maxMonths else maxDays

                    if (isSelfChange) {
                        isSelfChange = false
                        return
                    }
                    if (editable.isNullOrEmpty()) {
                        suffix = getSuffix(0)
                        setError(getString(R.string.future_plan_investment_term_error, "$seekBarRestriction $suffix", "$maxTerm $suffix"))
                        return
                    }
                    if (!isTrackChange && !editable.contains(" ") && editable.length < 10) {
                        val currentValue = editable.toString().toInt()
                        suffix = getSuffix(currentValue)
                        when {
                            currentValue < seekBarRestriction || currentValue > maxTerm -> setError(getString(R.string.future_plan_investment_term_error, "$seekBarRestriction $suffix", "$maxTerm $suffix"))
                            else -> {
                                binding.investmentTermSliderView.setProgress(currentValue - minTerm)
                                clearError()
                            }
                        }
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        binding.doneButton.setOnClickListener {
            clearFocusFromInvestmentTermView()
            if (!binding.investmentTermNormalInputView.hasError()) {
                viewModel.investmentTerm.value = binding.investmentTermNormalInputView.selectedValue
                activity?.onBackPressed()
            } else {
                AnimationHelper.shakeShakeAnimate(binding.investmentTermNormalInputView)
            }
        }
    }

    override fun setUpRadioView() {
        super.setUpRadioView()

        binding.monthDayRadioButtonView.setItemCheckedInterface {
            with(binding.investmentTermSliderView) {
                clearFocusFromInvestmentTermView()
                if (binding.monthDayRadioButtonView.selectedIndex == 0) {
                    seekBarRestriction = minMonths
                    seekBarMinimum = minMonths

                    incrementSize = (maxMonths) / INCREMENT_STEPS
                    setMax(maxMonths - minMonths)

                    setStartText(String.format("%s %s", minMonths.toString(), getString(R.string.future_plan_months).toLowerCase()))
                    setEndText(String.format("%s %s", maxMonths.toString(), getString(R.string.future_plan_months)).toLowerCase())
                } else {
                    seekBarRestriction = minDays
                    seekBarMinimum = minDays

                    incrementSize = (maxDays) / INCREMENT_STEPS
                    setMax(maxDays - minDays)

                    setStartText(String.format("%s %s", minDays.toString(), getString(R.string.future_plan_days)).toLowerCase())
                    setEndText(String.format("%s %s", (maxDays).toString(), getString(R.string.future_plan_days)).toLowerCase())
                }

                with(binding.investmentTermNormalInputView) {
                    clearError()
                    if (selectedValue.isNotEmpty()) {
                        convertTermType(selectedValue.takeLeadingNumbersOrZero().toInt())
                    }
                }
            }
        }
        binding.monthDayRadioButtonView.selectedIndex = 0
        binding.investmentTermSliderView.setProgress(0)
    }

    private fun calculateDaysDifference(futureDate: GregorianCalendar) = TimeUnit.MILLISECONDS.toDays(futureDate.timeInMillis - GregorianCalendar().timeInMillis).toInt() + 1
}