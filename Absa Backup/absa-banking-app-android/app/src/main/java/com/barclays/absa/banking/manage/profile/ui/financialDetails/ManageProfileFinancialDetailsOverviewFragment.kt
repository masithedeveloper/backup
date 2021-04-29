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
 */

package com.barclays.absa.banking.manage.profile.ui.financialDetails

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.manage.profile.ManageProfileFlow
import com.barclays.absa.banking.manage.profile.services.dto.FinancialInformation
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.YES
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.manage_profile_financial_details_overview_fragment.*
import styleguide.utils.extensions.toSentenceCase

class ManageProfileFinancialDetailsOverviewFragment : ManageProfileBaseFragment(R.layout.manage_profile_financial_details_overview_fragment) {
    private lateinit var financialDetails: FinancialInformation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.manage_profile_financial_details_toolbar_title)

        financialDetails = manageProfileViewModel.customerInformation.value?.customerInformation?.financialInformation ?: FinancialInformation()

        manageProfileViewModel.manageProfileFlow = ManageProfileFlow.UPDATE_FINANCIAL_DETAILS
        manageProfileViewModel.retrieveSouthAfricanTaxDetails()

        setUpObservers()
        initViews(view)
        initOnClickListeners()
    }

    private fun setUpObservers() {
        manageProfileViewModel.sourceOfIncomeLookupResult.observe(viewLifecycleOwner, {
            sourceOfIncomeContentView.setContentText(manageProfileViewModel.getLookupValue(it, financialDetails.sourceOfIncome))
            manageProfileViewModel.sourceOfIncomeLookupResult.removeObservers(this)
            manageProfileViewModel.fetchMonthlyIncome()

            if (sourceOfIncomeContentView.contentTextViewValue.isEmpty()) {
                incomeDetailsActionView.setCustomActionOnclickListener {
                    showGenericErrorAndExitFlow()
                }
            }
        })

        manageProfileViewModel.monthlyIncomeLookupResult.observe(viewLifecycleOwner, {
            monthlyIncomeContentView.setContentText(manageProfileViewModel.getLookupValue(it, financialDetails.monthlyIncome))
            manageProfileViewModel.monthlyIncomeLookupResult.removeObservers(this)
            manageProfileViewModel.fetchCountryLookupTableForTax()
        })
    }

    private fun initViews(view: View) {
        manageProfileViewModel.originalForeignTaxDetails.observe(viewLifecycleOwner, { originalForeignTaxDetails ->
            if (originalForeignTaxDetails.isEmpty()) {
                foreignTaxCountryLayout.visibility = View.GONE
                foreignTaxContentView.visibility = View.VISIBLE
            } else {
                foreignTaxCountryLayout.visibility = View.VISIBLE
                foreignTaxContentView.visibility = View.GONE
                originalForeignTaxDetails.forEachIndexed { index, taxDetails ->
                    val manageProfileTaxOverviewWidget = ManageProfileTaxOverviewWidget(view.context, taxDetails, index == originalForeignTaxDetails.size)
                    foreignTaxCountryLayout.addView(manageProfileTaxOverviewWidget)
                }
            }
            dismissProgressDialog()
            manageProfileViewModel.originalForeignTaxDetails.removeObservers(this)
        })

        manageProfileViewModel.southAfricanTax.observe(viewLifecycleOwner, { southAfricanTaxDetails ->
            if (southAfricanTaxDetails.areYouRegisteredForSouthAfricanTax == YES) {
                if (southAfricanTaxDetails.taxNumber.isEmpty()) {
                    southAfricanTaxDetailsContentView.setContentText(getString(R.string.none))
                    if (southAfricanTaxDetails.reasonForNoTaxNumberCode.isNotEmpty()) {
                        southAfricanTaxReasonContentView.visibility = View.VISIBLE
                        southAfricanTaxReasonContentView.setContentText(manageProfileViewModel.taxReasons[manageProfileViewModel.taxReasons.indexOfFirst { southAfricanTaxDetails.reasonForNoTaxNumberCode == it.reasonCode.toString() }].reasonDescription.toSentenceCase())
                    }
                } else {
                    southAfricanTaxDetailsContentView.setContentText(financialDetails.saIncomeTaxNumber)
                }
                southAfricanTaxDetailsContentView.setLabelText(getString(R.string.manage_profile_financial_details_tax_number_lable))
            } else {
                southAfricanTaxDetailsContentView.setContentText(getString(R.string.no))
                southAfricanTaxDetailsContentView.setLabelText(getString(R.string.manage_profile_financial_details_are_you_registered_for_foreign_tax))
            }
        })

        financialDetails = manageProfileViewModel.customerInformation.value?.customerInformation?.financialInformation ?: FinancialInformation()
        manageProfileViewModel.manageProfileFinancialDetailsMetaData.apply {
            sourceOfIncome = financialDetails.sourceOfIncome
            monthlyIncome = financialDetails.monthlyIncome
            socialGrantFlag = financialDetails.socialGrantFlag
            creditWorthinessFlag = financialDetails.creditWorthinessFlag
        }
        manageProfileViewModel.fetchSourceOfIncome()
        if (YES.equals(financialDetails.socialGrantFlag, true)) {
            socialGrantContentView.setContentText(getString(R.string.yes))
        } else {
            socialGrantContentView.setContentText(getString(R.string.no))
        }

        if (YES.equals(financialDetails.creditWorthinessFlag, true)) {
            creditWorthinessConsentContentView.setContentText(getString(R.string.yes))
        } else {
            creditWorthinessConsentContentView.setContentText(getString(R.string.no))
        }
    }

    private fun initOnClickListeners() {
        foreignTaxActionView.setCustomActionOnclickListener {
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_FinancialDetailsScreen_EditForeignTaxDetailsButtonClicked")
            manageProfileViewModel.manageProfileFlow = ManageProfileFlow.FOREIGN_TAX
            navigate(ManageProfileFinancialDetailsOverviewFragmentDirections.actionManageProfileFinancialDetailsOverviewFragmentToManageProfileRegisteredForForeignTaxFragment())
        }

        incomeDetailsActionView.setCustomActionOnclickListener {
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_FinancialDetailsScreen_EditIncomeDetailsButtonClicked")
            navigate(ManageProfileFinancialDetailsOverviewFragmentDirections.actionManageProfileFinancialDetailsOverviewFragmentToManageProfileIncomeDetailsFragment())
        }

        otherFinancialDetailsActionView.setCustomActionOnclickListener {
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_FinancialDetailsScreen_EditOtherFinancialDetailsButtonClicked")
            navigate(ManageProfileFinancialDetailsOverviewFragmentDirections.actionManageProfileFinancialDetailsOverviewFragmentToManageProfileOtherFinancialDetailsFragment())
        }
    }
}