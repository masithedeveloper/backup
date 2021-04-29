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

package com.barclays.absa.banking.fixedDeposit

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import com.barclays.absa.banking.R
import com.barclays.absa.banking.fixedDeposit.FixedDepositOpenAccountFragment.Companion.FIXED_DEPOSIT_PRODUCT_CODE
import com.barclays.absa.banking.fixedDeposit.FixedDepositReinvestInstructionsFragment.ReinvestInstruction.*
import com.barclays.absa.banking.fixedDeposit.services.dto.FixedDepositRenewalInstructionResponse
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.utils.DateUtils
import kotlinx.android.synthetic.main.fixed_deposit_investment_instruction_item.view.*
import kotlinx.android.synthetic.main.fixed_deposit_maintenance_activity.*
import kotlinx.android.synthetic.main.fixed_deposit_reinvest_instructions_fragment.*
import styleguide.utils.AnimationHelper

class FixedDepositReinvestInstructionsFragment : FixedDepositBaseFragment(R.layout.fixed_deposit_reinvest_instructions_fragment) {

    companion object {
        const val YES_INDICATOR = "Y"
        const val NO_INDICATOR = "N"
    }

    private var reinvestmentInstruction: ReinvestInstruction
        get() = NONE
        set(value) {
            fixedDepositViewModel.reinvestInstruction = value
            clearCheckBoxes()
            when (value) {
                AUTO_REINVEST -> autoReinvestOptionItem.reinvestCheckBox.isChecked = true
                CUSTOM_REINVEST -> customReinvestOptionItem.reinvestCheckBox.isChecked = true
                NONE -> clearCheckBoxes()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.fixed_deposit_reinvest_instructions)
        fixedDepositMaintenanceActivity.progressIndicatorView.visibility = View.GONE

        val currentFixedDepositBalance = fixedDepositViewModel.accountObject.currentBalance?.amountDouble ?: 0.00
        if (currentFixedDepositBalance < 1000.00) {
            autoReinvestOptionItem.visibility = View.GONE
        } else {
            autoReinvestOptionItem.setUpView(AUTO_REINVEST, R.string.fixed_deposit_auto_reinvest, R.string.fixed_deposit_auto_reinvest_info)
        }

        customReinvestOptionItem.setUpView(CUSTOM_REINVEST, R.string.fixed_deposit_custom_reinvestment, R.string.fixed_deposit_custom_reinvestment_info)
        setUpObservers()
        nextButton.setOnClickListener { navigateToInvestmentFlow() }
    }

    override fun onResume() {
        super.onResume()
        reinvestmentInstruction = fixedDepositViewModel.reinvestInstruction
    }

    private fun setUpObservers() {
        fixedDepositViewModel.renewalInstructionResponse.value = FixedDepositRenewalInstructionResponse()
        fixedDepositViewModel.renewalInstructionResponse.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            if (it.transactionMessage.equals(BMBConstants.SUCCESS, true)) {
                if (fixedDepositViewModel.reinvestInstruction == AUTO_REINVEST) {
                    navigateToAutoReinvestFlow()
                } else {
                    fixedDepositViewModel.renewalInstructionData.apply {
                        accountNumber = it.renewalInstructionDetails.accountNumber
                        islamicIndicator = it.renewalInstructionDetails.islamicIndicator
                        productCode = it.renewalInstructionDetails.productCode
                        startDate = DateUtils.getTodaysDate()
                    }
                    navigate(FixedDepositReinvestInstructionsFragmentDirections.actionFixedDepositReinvestInstructionsFragmentToFixedDepositReinvestmentAmountFragment())
                }
            }
        })
    }

    private fun clearCheckBoxes() {
        autoReinvestOptionItem.reinvestCheckBox.isChecked = false
        customReinvestOptionItem.reinvestCheckBox.isChecked = false
    }

    private fun navigateToInvestmentFlow() {
        when (fixedDepositViewModel.reinvestInstruction) {
            AUTO_REINVEST, CUSTOM_REINVEST -> {
                fixedDepositViewModel.fetchRenewalInstruction(fixedDepositViewModel.accountDetailsResponse.value?.fixedDeposit?.accountNumber ?: "")
            }
            NONE -> {
                AnimationHelper.shakeShakeAnimate(reinvestOptionLinearLayout)
            }
        }
    }

    private fun navigateToAutoReinvestFlow() {
        var interestRate = ""
        var capFrequency = ""
        fixedDepositViewModel.fixedDepositPayoutDetailsData.apply {
            fixedDepositViewModel.accountDetailsResponse.value?.fixedDeposit?.let {
                accountType = it.accountType
                accountNumber = it.accountNumber
                interestRate = it.currentInterestRate
                capFrequency = it.capFrequency
            }
            automaticReinvestment = true.toString()
            addPaymentInstruction = NO_INDICATOR
            addCapitalizationInfo = NO_INDICATOR
            addDebitOrderInstruction = NO_INDICATOR
            productCode = FIXED_DEPOSIT_PRODUCT_CODE
            reinvestCapInt = false.toString()
        }
        navigate(FixedDepositReinvestInstructionsFragmentDirections.actionFixedDepositReinvestInstructionsFragmentToFixedDepositReinvestConfirmationFragment(interestRate, capFrequency))
    }

    private fun View.setUpView(instruction: ReinvestInstruction, @StringRes label: Int, @StringRes info: Int) {
        this.apply {
            reinvestInfoTextView.text = getString(info)
            reinvestLabelTextView.text = getString(label)
            setOnClickListener { reinvestmentInstruction = instruction }
        }
    }

    override fun onDestroyView() {
        fixedDepositViewModel.renewalInstructionResponse.removeObservers(viewLifecycleOwner)
        super.onDestroyView()
    }

    enum class ReinvestInstruction {
        AUTO_REINVEST,
        CUSTOM_REINVEST,
        NONE
    }
}