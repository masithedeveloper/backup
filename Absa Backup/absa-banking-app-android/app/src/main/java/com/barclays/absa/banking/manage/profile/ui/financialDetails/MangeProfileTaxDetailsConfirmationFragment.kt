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

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType
import com.barclays.absa.banking.framework.app.BMBConstants.FAILURE
import com.barclays.absa.banking.manage.profile.ManageProfileFinancialDetailsFlow
import com.barclays.absa.banking.manage.profile.ManageProfileFlow
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.NO
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.YES
import com.barclays.absa.banking.manage.profile.ui.ManageProfileResultFactory
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.manage_profile_tax_confirmation_fragment.*

class MangeProfileTaxDetailsConfirmationFragment : ManageProfileBaseFragment(R.layout.manage_profile_tax_confirmation_fragment) {
    private lateinit var sureCheckDelegate: SureCheckDelegate
    private lateinit var saveButtonAnalyticsTag: String
    private lateinit var successAnalyticsTag: String
    private lateinit var failureAnalyticsTag: String

    override fun onAttach(context: Context) {
        super.onAttach(context)

        sureCheckDelegate = object : SureCheckDelegate(context) {
            override fun onSureCheckProcessed() {
                Handler(Looper.getMainLooper()).postDelayed({ manageProfileViewModel.updateFinancialDetails() }, 250)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (manageProfileViewModel.manageProfileFlow == ManageProfileFlow.FOREIGN_TAX) {
            populateForeignTaxDetails()
        } else {
            populateSouthAfricanTaxDetails()
        }

        setOnClickListeners()
        initObservers()
    }

    private fun populateSouthAfricanTaxDetails() {
        failureAnalyticsTag = "ManageProfile_SATaxDetailsUpdateErrorScreen_OKButtonClicked"
        successAnalyticsTag = "MangeProfile_SATaxDetailsUpdateSuccessScreen_DoneButtonClicked"
        saveButtonAnalyticsTag = "ManageProfile_ConfirmSATaxDetailsChangesScreen_DoneButtonClicked"
        val southAfricanTax = (manageProfileViewModel.southAfricanTaxDetails)

        if (YES.equals(manageProfileViewModel.manageProfileFinancialDetailsMetaData.isRegisteredForSouthAfricanTax, true)) {
            val profileChange = ManageProfileTaxConfirmationWidget(manageProfileActivity, southAfricanTax)
            contentLinearLayout.addView(profileChange)
            profileChange.hideDivider()
        } else {
            noForeignTaxContentView.visibility = View.VISIBLE
            contentLinearLayout.visibility = View.GONE
        }
    }

    private fun populateForeignTaxDetails() {
        failureAnalyticsTag = "MangeProfile_ForeignTaxDetailsUpdateSuccessScreen_DoneButtonClicked"
        successAnalyticsTag = "ManageProfile_ForeignTaxDetailsUpdateFailureScreen_OKButtonClicked"
        saveButtonAnalyticsTag = "ManageProfile_ConfirmForeignTaxDetailChanges_SaveButtonClicked"
        val taxCountries = manageProfileViewModel.displayForeignTaxDetails

        if (NO.equals(manageProfileViewModel.manageProfileFinancialDetailsMetaData.isRegisteredForForeignTax, true)) {
            noForeignTaxContentView.visibility = View.VISIBLE
            contentLinearLayout.visibility = View.GONE
        } else {
            val emptyTaxCountry = taxCountries.indexOfFirst { it.taxCountry.isEmpty() }
            noForeignTaxContentView.visibility = View.GONE
            if (emptyTaxCountry != -1) {
                taxCountries.removeAt(emptyTaxCountry)
            }

            taxCountries.forEachIndexed { index, foreignTaxDisplayValues ->
                val profileChange = ManageProfileTaxConfirmationWidget(manageProfileActivity, foreignTaxDisplayValues)
                if (index == taxCountries.size - 1 || taxCountries.size == 1) {
                    profileChange.hideDivider()
                }
                contentLinearLayout.addView(profileChange)
            }
        }
    }

    private fun setOnClickListeners() {
        saveButton.setOnClickListener {
            AnalyticsUtil.trackAction(ANALYTICS_TAG, saveButtonAnalyticsTag)
            if (manageProfileViewModel.manageProfileFlow == ManageProfileFlow.FOREIGN_TAX) {
                manageProfileViewModel.manageProfileFinancialDetailsFlow = ManageProfileFinancialDetailsFlow.UPDATE_FOREIGN_TAX
            } else {
                manageProfileViewModel.manageProfileFinancialDetailsFlow = ManageProfileFinancialDetailsFlow.UPDATE_LOCAL_TAX
            }
            manageProfileViewModel.updateFinancialDetails()
        }
    }

    private fun initObservers() {
        manageProfileViewModel.updateCustomerInformation.observe(viewLifecycleOwner, { customerInformation ->
            when {
                customerInformation.sureCheckFlag == null && customerInformation.updatedFields.isNotEmpty() -> navigate(MangeProfileTaxDetailsConfirmationFragmentDirections.actionGlobalManageProfileResultFragment(ManageProfileResultFactory(this).updatedOtherPersonalInformationSuccess("", successAnalyticsTag)))
                FAILURE.equals(customerInformation.transactionStatus, true) -> navigate(MangeProfileTaxDetailsConfirmationFragmentDirections.actionGlobalManageProfileResultFragment(ManageProfileResultFactory(this).updatedOtherPersonalInformationFailure(customerInformation.transactionMessage, failureAnalyticsTag)))
                TransactionVerificationType.SURECHECKV2Required.toString().equals(customerInformation.sureCheckFlag, true) -> sureCheckDelegate.processSureCheck(baseActivity, manageProfileViewModel.updateCustomerInformation.value) { manageProfileViewModel.updateFinancialDetails() }
            }
        })
    }
}