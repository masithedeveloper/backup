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

package com.barclays.absa.banking.manage.profile.ui.financialDetails

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.manage.profile.services.dto.FinancialInformation
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.banking.shared.services.dto.LookupItem
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.manage_profile_income_details_fragment.*
import styleguide.forms.SelectorList

class ManageProfileEditIncomeDetailsFragment : ManageProfileBaseFragment(R.layout.manage_profile_income_details_fragment) {
    private lateinit var financialInformation: FinancialInformation
    private var isSourceOfIncomeChanged = false
    private var isMonthlyIncomeChanged = false
    private var sourceOfIncomeList: List<LookupItem> = listOf()
    private var monthlyIncomeList: List<LookupItem> = listOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.manage_profile_financial_details_hub_income_details_label)
        initData()
        setItemSelectionInterface()
        setUpOnClickListener()
    }

    private fun initData() {
        financialInformation = manageProfileViewModel.customerInformation.value?.customerInformation?.financialInformation ?: FinancialInformation()
        continueButton.isEnabled = enableContinueButton()
        sourceOfIncomeList = manageProfileViewModel.retrieveSourceOfIncomeList()
        monthlyIncomeList = manageProfileViewModel.retrieveMonthlyIncomeList()
        sourceOfIncomeNormalInputView.selectedValue = manageProfileViewModel.sourceOfIncomeLookupResult.value?.let { manageProfileViewModel.getLookupValue(it, manageProfileViewModel.manageProfileFinancialDetailsMetaData.sourceOfIncome) }.toString()
        monthlyIncomeNormalInputView.selectedValue = manageProfileViewModel.monthlyIncomeLookupResult.value?.let { manageProfileViewModel.getLookupValue(it, manageProfileViewModel.manageProfileFinancialDetailsMetaData.monthlyIncome) }.toString()
        sourceOfIncomeNormalInputView.setList(sourceOfIncomeList as SelectorList<LookupItem>, getString(R.string.manage_profile_financial_details_select_source_of_income))
        monthlyIncomeNormalInputView.setList(monthlyIncomeList as SelectorList<LookupItem>, getString(R.string.manage_profile_financial_details_select_monthly_income))
        dismissProgressDialog()
    }

    private fun setItemSelectionInterface() {
        sourceOfIncomeNormalInputView.setItemSelectionInterface {
            manageProfileViewModel.manageProfileFinancialDetailsMetaData.sourceOfIncome = sourceOfIncomeList[it].itemCode.toString()
            isSourceOfIncomeChanged = financialInformation.sourceOfIncome != manageProfileViewModel.manageProfileFinancialDetailsMetaData.sourceOfIncome
            continueButton.isEnabled = isSourceOfIncomeChanged || isMonthlyIncomeChanged
        }
        monthlyIncomeNormalInputView.setItemSelectionInterface {
            manageProfileViewModel.manageProfileFinancialDetailsMetaData.monthlyIncome = monthlyIncomeList[it].itemCode.toString()
            isMonthlyIncomeChanged = financialInformation.monthlyIncome != manageProfileViewModel.manageProfileFinancialDetailsMetaData.monthlyIncome
            continueButton.isEnabled = isSourceOfIncomeChanged || isMonthlyIncomeChanged
        }
    }

    private fun enableContinueButton(): Boolean {
        isSourceOfIncomeChanged = financialInformation.sourceOfIncome != manageProfileViewModel.manageProfileFinancialDetailsMetaData.sourceOfIncome
        isMonthlyIncomeChanged = financialInformation.monthlyIncome != manageProfileViewModel.manageProfileFinancialDetailsMetaData.monthlyIncome
        return isSourceOfIncomeChanged || isMonthlyIncomeChanged
    }

    private fun setUpOnClickListener() {
        continueButton.setOnClickListener {
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_IncomeDetailsScreen_ContinueButtonClicked")
            navigate(ManageProfileEditIncomeDetailsFragmentDirections.actionManageProfileIncomeDetailsFragmentToManageProfileIncomeDetailsConfirmationFragment())
        }
    }
}