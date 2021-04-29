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
package com.barclays.absa.banking.relationshipBanking.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.BusinessBankingApplicationSummaryFragmentBinding
import com.barclays.absa.banking.newToBank.NewToBankActivity
import com.barclays.absa.banking.newToBank.NewToBankView
import com.barclays.absa.banking.newToBank.dto.CustomerPortfolioInfo
import com.barclays.absa.banking.presentation.shared.ExtendedFragment
import com.barclays.absa.utils.viewModel
import styleguide.forms.CheckBoxView
import styleguide.forms.OnCheckedListener
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.utils.AnimationHelper
import styleguide.utils.extensions.toRandAmount
import styleguide.utils.extensions.toSentenceCase

class BusinessBankingApplicationSummaryFragment : ExtendedFragment<BusinessBankingApplicationSummaryFragmentBinding>() {

    private lateinit var newToBankBusinessView: NewToBankView
    private lateinit var viewModel: NewToBankBusinessAccountViewModel

    override fun getToolbarTitle(): String = getString(R.string.relationship_banking_application_summary)

    override fun getLayoutResourceId(): Int = R.layout.business_banking_application_summary_fragment

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = (context as NewToBankActivity).viewModel()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        newToBankBusinessView = activity as NewToBankView
        newToBankBusinessView.trackSoleProprietorCurrentFragment("SoleProprietor_ApplicationSummaryScreen_ScreenDisplayed")
        newToBankBusinessView.toolbarTitle = toolbarTitle
        newToBankBusinessView.showToolbar()
        newToBankBusinessView.hideProgressIndicator()
        initViews()
        setUpOffersRadio()
    }

    private fun initViews() {
        val newToBankTempData = newToBankBusinessView.newToBankTempData
        val newToBankIncomeDetails = newToBankTempData.newToBankIncomeDetails
        val selectedBusinessEvolveAccount = newToBankTempData.selectedBusinessEvolvePackage
        val selectedBusinessEvolveProduct = newToBankTempData.selectedBusinessEvolveProduct
        binding.apply {
            fullNamePrimaryContentAndLabelView.setContentText(newToBankTempData.customerDetails.fullName)
            accountTypePrimaryContentAndLabelView.setContentText(selectedBusinessEvolveAccount.packageName)
            accountCostPrimaryContentAndLabelView.setContentText(selectedBusinessEvolveProduct.monthlyFee.toRandAmount())
            preferredBranchPrimaryContentAndLabelView.setContentText(newToBankTempData.businessCustomerPortfolio.preferredBranch.siteName.toSentenceCase())
            submitButton.setOnClickListener {
                if (validateFields()) {
                    newToBankBusinessView.trackSoleProprietorCurrentFragment("SoleProprietor_ApplicationSummaryScreen_SubmitApplicationButtonClicked")
                    val customerPortfolioInfo = CustomerPortfolioInfo().apply {
                        productType = selectedBusinessEvolveProduct.identifier
                        sourceOfIncome = newToBankIncomeDetails!!.sourceOfIncomeCode
                        sourceOfFunds = newToBankTempData.sourceOfFundsCode
                        employmentSector = ""
                        monthlyIncomeRange = "0"
                        occupationCode = newToBankIncomeDetails.occupation
                        medicalOccupationCode = newToBankIncomeDetails.medicalCode
                        occupationStatus = newToBankIncomeDetails.employmentTypeCode
                        occupationLevel = "01"
                        registeredForTax = newToBankIncomeDetails.registeredForTax
                        foreignNationalTaxResident = newToBankIncomeDetails.foreignTaxResident
                        foreignNationalResidentCountry = newToBankIncomeDetails.foreignCountry
                        personalClientAgreementAccepted = true
                        creditCheckConsent = true
                        underDebtCounseling = false
                        feesLinkDisplayed = true
                        accountFeatureDisplayed = true
                        totalMonthlyIncome = "0.00"
                        totalMonthlyExpenses = "0.00"
                        marketPropertyValue = newToBankTempData.marketPropertyValue
                        rewardsDayOfDebitOrder = "0"
                        residentialAddressSince = newToBankTempData.residentialAddressSince
                        currentEmploymentSince = ""
                        residentialStatus = newToBankTempData.residentialStatus
                        overdraftRequired = false
                        eStatementRequired = true
                        notifyMeRequired = true
                        smsMarketingIndicator = false
                        teleMarketingIndicator = false
                        mailMarketingIndicator = false
                        useSelfie = newToBankTempData.useSelfie
                        shortAndMediumTermFundingRewardsValueAddedService = newToBankTempData.shortAndMediumTermFundingRewardsValueAddedService
                        assetFinanceRewardsValueAddedService = newToBankTempData.assetFinanceRewardsValueAddedService
                        makeAndReceivePaymentsRewardsValueAddedService3 = newToBankTempData.makeAndReceivePaymentsRewardsValueAddedService3
                        savingAndInvestmentProductsRewardsValueAddedService4 = newToBankTempData.savingAndInvestmentProductsRewardsValueAddedService4
                        if (binding.offersRadioButtonView.selectedIndex == 0) {
                            preferredMarketingCommunication = "E-MAIL"
                            marketingIndicator = true
                            smsMarketingIndicator = true
                            teleMarketingIndicator = true
                            emailMarketingIndicator = true
                        } else {
                            marketingIndicator = !binding.noThanksCheckBoxView.isChecked
                            smsMarketingIndicator = binding.smsCheckBoxView.isChecked
                            teleMarketingIndicator = binding.voiceCheckBoxView.isChecked
                            emailMarketingIndicator = binding.emailCheckBoxView.isChecked
                        }
                    }
                    val businessCustomerPortfolio = newToBankBusinessView.newToBankTempData.businessCustomerPortfolio
                    viewModel.submitBusinessBankingApplication(customerPortfolioInfo, businessCustomerPortfolio)
                    newToBankBusinessView.navigateToNewToBankApplicationProcessingFragment()
                }
            }
        }
    }

    private fun validateFields(): Boolean {
        return when {
            isInvalidField(binding.confirmMandateCheckBox) -> false
            isInvalidField(binding.informationSharingCheckBox) -> false
            isInvalidField(binding.informationCorrectCheckBox) -> false
            binding.offersRadioButtonView.selectedValue == null -> {
                binding.offersRadioButtonView.setErrorMessage(getString(R.string.please_select))
                scrollToTopOfView(binding.offersRadioButtonView)
                false
            }
            binding.offersRadioButtonView.selectedIndex > 0 -> {
                return if (binding.emailCheckBoxView.isValid || binding.smsCheckBoxView.isValid || binding.voiceCheckBoxView.isValid || binding.noThanksCheckBoxView.isValid) {
                    true
                } else {
                    AnimationHelper.shakeShakeAnimate(binding.emailCheckBoxView)
                    AnimationHelper.shakeShakeAnimate(binding.smsCheckBoxView)
                    AnimationHelper.shakeShakeAnimate(binding.voiceCheckBoxView)
                    binding.noThanksCheckBoxView.setErrorMessage(getString(R.string.please_select))
                    false
                }
            }
            else -> true
        }
    }

    private fun isInvalidField(checkBoxView: CheckBoxView): Boolean {
        if (checkBoxView.visibility == VISIBLE && !checkBoxView.isValid) {
            checkBoxView.setErrorMessage(getString(R.string.new_to_bank_agree_terms_of_conditions))
            scrollToTopOfView(checkBoxView)
            return true
        } else {
            checkBoxView.clearError()
        }
        return false
    }

    private fun setUpOffersRadio() {
        val selectorList = SelectorList<StringItem>().apply {
            add(StringItem(getString(R.string.yes)))
            add(StringItem(getString(R.string.no)))
        }

        binding.apply {
            offersRadioButtonView.setDataSource(selectorList)
            offersRadioButtonView.setItemCheckedInterface { index ->
                offersRadioButtonView.hideError()
                areYouSureLinearLayout.visibility = if (index == 0) View.GONE else View.VISIBLE
                scrollToTopOfView(offersRadioButtonView)
            }
            val isCheckedListener = OnCheckedListener { isChecked ->
                if (isChecked) {
                    binding.noThanksCheckBoxView.isChecked = false
                    binding.noThanksCheckBoxView.clearError()
                }
            }
            smsCheckBoxView.setOnCheckedListener(isCheckedListener)
            emailCheckBoxView.setOnCheckedListener(isCheckedListener)
            voiceCheckBoxView.setOnCheckedListener(isCheckedListener)

            noThanksCheckBoxView.setOnCheckedListener { isChecked ->
                if (isChecked) {
                    noThanksCheckBoxView.clearError()
                    smsCheckBoxView.isChecked = false
                    emailCheckBoxView.isChecked = false
                    voiceCheckBoxView.isChecked = false
                }
            }
        }
    }

    private fun scrollToTopOfView(view: View) {
        val scrollView = binding.scrollView
        scrollView.post { scrollView.smoothScrollTo(0, view.y.toInt()) }
    }
}
