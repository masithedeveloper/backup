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

package com.barclays.absa.banking.newToBank

import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.NewToBankClientIncomeFragmentBinding
import com.barclays.absa.banking.newToBank.dto.CustomerPortfolioInfo
import com.barclays.absa.banking.newToBank.dto.NewToBankIncomeDetails
import com.barclays.absa.banking.newToBank.dto.NewToBankTempData
import com.barclays.absa.banking.newToBank.services.dto.CodesLookupDetailsSelector
import com.barclays.absa.banking.presentation.shared.ExtendedFragment
import styleguide.forms.NormalInputView
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher

class NewToBankClientIncomeFragment : ExtendedFragment<NewToBankClientIncomeFragmentBinding>() {

    private var newToBankView: NewToBankView? = null
    private var newToBankIncomeDetails: NewToBankIncomeDetails? = null
    private var newToBankTempData: NewToBankTempData? = null

    private val isValidInput: Boolean
        get() = when {
            isInvalidField(binding.totalMonthlyIncomeNormalInputView, getString(R.string.new_to_bank_enter_amount)) -> false
            isInvalidField(binding.totalMonthlyExpensesNormalInputView, getString(R.string.new_to_bank_enter_amount)) -> false
            else -> !isInvalidField(binding.accountFundingMethodNormalInputView, getString(R.string.please_select))
        }

    override fun getLayoutResourceId(): Int = R.layout.new_to_bank_client_income_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newToBankView = activity as NewToBankView?
        newToBankView?.setToolbarBackTitle(toolbarTitle)
        if (newToBankView?.isStudentFlow == true) {
            newToBankView?.trackStudentAccount("StudentAccount_SourceOfFundsScreen_ScreenDisplayed")
        } else {
            newToBankView?.trackCurrentFragment(NewToBankConstants.INCOME_AND_EXPENSES)
        }
        newToBankTempData = newToBankView?.newToBankTempData
        newToBankIncomeDetails = newToBankTempData?.newToBankIncomeDetails
        binding.totalMonthlyIncomeNormalInputView.selectedValue = ""
        binding.totalMonthlyExpensesNormalInputView.selectedValue = ""

        setUpFundList()
        setPreviousSelectedSourceOfFunds()

        binding.submitButton.setOnClickListener {

            if (isValidInput) {
                val customerPortfolioInfo = CustomerPortfolioInfo()
                customerPortfolioInfo.productType = newToBankTempData!!.productType
                customerPortfolioInfo.sourceOfIncome = newToBankIncomeDetails!!.sourceOfIncomeCode
                customerPortfolioInfo.occupationStatus = newToBankIncomeDetails!!.employmentTypeCode
                customerPortfolioInfo.occupationCode = newToBankIncomeDetails!!.occupation

                val accountLookupDetails = binding.accountFundingMethodNormalInputView.selectedItem as CodesLookupDetailsSelector
                newToBankTempData?.sourceOfFundsCode = accountLookupDetails.itemCode
                newToBankTempData?.totalMonthlyIncome = binding.totalMonthlyIncomeNormalInputView.selectedValueUnmasked
                newToBankTempData?.totalMonthlyExpense = binding.totalMonthlyExpensesNormalInputView.selectedValueUnmasked

                newToBankView?.requestCasaRiskStatus(customerPortfolioInfo)

                if (newToBankIncomeDetails!!.occupation == "0") {
                    newToBankIncomeDetails!!.occupation = ""
                }
            }
        }

        binding.totalMonthlyIncomeNormalInputView.addRequiredValidationHidingTextWatcher()
        binding.totalMonthlyExpensesNormalInputView.addRequiredValidationHidingTextWatcher()
    }

    private fun isInvalidField(normalInputView: NormalInputView<*>, errorToDisplay: String): Boolean {
        if (normalInputView.visibility == VISIBLE && normalInputView.selectedValueUnmasked.isEmpty()) {
            normalInputView.setError(errorToDisplay)
            scrollToTopOfView(normalInputView)
            return true
        } else {
            normalInputView.clearError()
        }
        return false
    }

    private fun setPreviousSelectedSourceOfFunds() {
        val sourceOfIncomeCode = newToBankIncomeDetails!!.sourceOfIncomeCode

        newToBankTempData?.sourceOfFundsList?.forEachIndexed { index, codesLookupDetailsSelector ->
            if (codesLookupDetailsSelector.itemCode.equals(sourceOfIncomeCode, ignoreCase = true)) {
                binding.accountFundingMethodNormalInputView.selectedIndex = index
                return
            }
        }
    }

    private fun scrollToTopOfView(view: View) {
        val scrollView = binding.scrollView
        scrollView.post { scrollView.smoothScrollTo(0, view.y.toInt()) }
    }

    private fun setUpFundList() {
        binding.accountFundingMethodNormalInputView.setList(newToBankTempData?.sourceOfFundsList, getString(R.string.new_to_bank_how_will_you_fund_this_account))
    }

    override fun getToolbarTitle(): String {
        return getString(R.string.new_to_bank_income_and_expense_title)
    }

    companion object {

        fun newInstance(): NewToBankClientIncomeFragment {
            return NewToBankClientIncomeFragment()
        }
    }
}
