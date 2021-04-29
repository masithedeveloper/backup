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
package com.barclays.absa.banking.newToBankSilverStudent

import android.content.Context
import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.newToBank.NewToBankConstants
import com.barclays.absa.banking.newToBank.NewToBankView
import com.barclays.absa.banking.newToBank.dto.CustomerPortfolioInfo
import com.barclays.absa.banking.newToBank.dto.NewToBankIncomeDetails
import kotlinx.android.synthetic.main.new_to_bank_student_account_source_of_funds_fragment.*
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher

class NewToBankWhatYouDoFragment : BaseFragment(R.layout.new_to_bank_student_account_source_of_funds_fragment) {

    private lateinit var newToBankView: NewToBankView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        newToBankView = context as NewToBankView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        sourceOfIncomeNormalInputView.addRequiredValidationHidingTextWatcher()
        sourceOfIncomeNormalInputView.setList(newToBankView.newToBankTempData.sourceOfIncomeList, getString(R.string.new_to_bank_source_of_funds_title))

        if (newToBankView.isStudentFlow) {
            newToBankView.trackStudentAccount("StudentAccount_SourceOfFundsScreen_ScreenDisplayed")
            sourceOfIncomeNormalInputView.selectedValue = newToBankView.newToBankTempData.sourceOfIncomeList.first { it.engCodeDescription.equals("Allowance", true) }.engCodeDescription
        }

        nextButton.setOnClickListener {
            if (validateFields()) {
                val newToBankIncomeDetails = NewToBankIncomeDetails().apply {
                    sourceOfIncomeCode = NewToBankConstants.ALLOWANCE_SOURCE_OF_INCOME_CODE
                    employmentTypeCode = NewToBankConstants.STUDENT_EMPLOYMENT_CODE
                }

                newToBankView.newToBankTempData.apply {
                    sourceOfIncomeList.forEach {
                        if (it.engCodeDescription == sourceOfIncomeNormalInputView.selectedValue) {
                            sourceOfFundsCode = it.itemCode
                        }
                    }

                    selectedMonthlyIncomeCode = NewToBankConstants.STUDENT_DEFAULT_MONTHLY_INCOME_CODE
                    productType = NewToBankConstants.STUDENT_SILVER_CHEQUE
                    totalMonthlyExpense = "0"
                    totalMonthlyIncome = "0"
                    this.newToBankIncomeDetails = newToBankIncomeDetails
                }

                val customerPortfolioInfo = CustomerPortfolioInfo().apply {
                    productType = newToBankView.newToBankTempData.productType
                    occupationCode = NewToBankConstants.OTHER_OCCUPATION_CODE
                    sourceOfIncome = newToBankIncomeDetails.sourceOfIncomeCode
                    occupationStatus = newToBankIncomeDetails.employmentTypeCode
                }
                newToBankView.requestCasaRiskStatus(customerPortfolioInfo)
            }
        }
    }

    private fun setUpToolbar() {
        setToolBarNoBack(R.string.source_of_funds)
        newToBankView.setProgressStep(8)
        newToBankView.showProgressIndicator()
    }

    private fun validateFields(): Boolean {

        val isFieldEmpty = sourceOfIncomeNormalInputView.text.isNullOrEmpty()

        if (isFieldEmpty) {
            sourceOfIncomeNormalInputView.setError(getString(R.string.relationship_banking_please_select_income))
        }

        return !isFieldEmpty
    }
}