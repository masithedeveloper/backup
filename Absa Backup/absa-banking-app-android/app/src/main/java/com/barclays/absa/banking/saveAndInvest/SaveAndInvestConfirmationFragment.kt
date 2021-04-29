/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.saveAndInvest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.ClientAgreementDetails
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.databinding.SaveAndInvestConfirmationFragmentBinding
import com.barclays.absa.banking.express.invest.createAndLinkAccountForExistingCustomer.AccountCreateAndLinkViewModel
import com.barclays.absa.banking.express.invest.createAndLinkAccountForExistingCustomer.dto.AccountCreateAndLinkRequest
import com.barclays.absa.banking.express.invest.createAndLinkAccountForExistingCustomer.dto.AccountCreateAndLinkStatus
import com.barclays.absa.banking.express.invest.saveExistingCustomerDetails.dto.AccountDetails
import com.barclays.absa.banking.express.invest.saveExistingCustomerDetails.dto.InitialPaymentDetails
import com.barclays.absa.banking.express.invest.saveExistingCustomerDetails.dto.MonthlyDebitOrder
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.settings.ui.ManageAccountsActivity
import com.barclays.absa.banking.shared.TermsAndConditionsInfo
import com.barclays.absa.utils.*
import com.barclays.absa.utils.PdfUtil.hash
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toFormattedAccountNumber
import styleguide.utils.extensions.toRandAmount
import styleguide.utils.extensions.toSentenceCase

abstract class SaveAndInvestConfirmationFragment : SaveAndInvestBaseFragment(R.layout.save_and_invest_confirmation_fragment) {

    private val absaCacheService: IAbsaCacheService = getServiceInterface()
    private lateinit var accountCreateAndLinkViewModel: AccountCreateAndLinkViewModel
    private var isPersonalClientAgreementAccepted = absaCacheService.isPersonalClientAgreementAccepted()
    protected val binding by viewBinding(SaveAndInvestConfirmationFragmentBinding::bind)

    abstract fun navigateOnCreateAccountSuccessResponse(accountCreateAndLinkStatus: AccountCreateAndLinkStatus)
    abstract fun navigateOnCreateAccountFailureResponse()
    abstract fun buildAccountCreateAndLinkRequest(): AccountCreateAndLinkRequest

    override fun onAttach(context: Context) {
        super.onAttach(context)
        accountCreateAndLinkViewModel = viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveExistingCustomerDetails()
        populateFields()
        setupListeners()
        setupObservers()
    }

    private fun buildAccountCreationDetails() {
        with(saveAndInvestViewModel) {
            accountCreationDetails.apply {
                investmentAmount = initialDepositAmount
                termDays = investmentTermInDays
                investmentTerm = if (investmentTermInDays > 0) investmentTermInDays.toString() else ""
                investmentDuration = ""
                maturityDate = DateTimeHelper.formatDate(this@with.maturityDate, DateTimeHelper.SLASH_PATTERN_YYYY_MM_DD)
                accountName = this@with.accountName
                initialPaymentDetails = InitialPaymentDetails().apply {
                    amount = initialDepositAmount
                    reference = initialDepositReference
                    accountDetails = AccountDetails().apply {
                        accountNumber = initialDepositAccount.accountNumber
                        bankName = BMBConstants.ABSA
                        branchCode = BMBConstants.ABSA_BRANCH_CODE
                        accountType = initialDepositAccount.accountType
                    }
                }
                interestPayoutDetails = MonthlyDebitOrder().apply {
                    reference = interestReference
                    accountDetails = AccountDetails().apply {
                        accountNumber = interestAccountNumber
                        bankName = interestBankName
                        branchCode = interestBranchCode
                        accountType = interestAccountType
                    }
                }
                if (recurringPaymentAccount.accountNumber.isNotBlank()) {
                    monthlyDebitOrder = MonthlyDebitOrder().apply {
                        amount = recurringPaymentAmount
                        reference = recurringPaymentReference
                        accountDetails = AccountDetails().apply {
                            accountNumber = recurringPaymentAccount.accountNumber
                            bankName = BMBConstants.ABSA
                            branchCode = BMBConstants.ABSA_BRANCH_CODE
                            accountType = recurringPaymentAccount.accountType
                        }
                        startDate = DateTimeHelper.formatDate(recurringPaymentStartDate, DateTimeHelper.SLASH_PATTERN_YYYY_MM_DD)
                        endDate = DateTimeHelper.formatDate(recurringPaymentEndDate, DateTimeHelper.SLASH_PATTERN_YYYY_MM_DD)
                    }
                }
            }
        }
    }

    private fun saveExistingCustomerDetails() {
        buildAccountCreationDetails()
        saveApplicationDetails()
        customerDetailsSaveViewModel.customerDetailsSaveLiveData.observe(viewLifecycleOwner, {
            dismissProgressDialog()
        })
    }

    private fun populateFields() {
        binding.disclaimerCheckBoxView.checkBoxTextView.isSaveEnabled = false
        binding.personalClientAgreementCheckBoxView.checkBoxTextView.isSaveEnabled = false
        with(saveAndInvestViewModel) {
            binding.accountNameSecondaryContentAndLabelView.setContentText(accountName)
            binding.initialDepositSecondaryContentAndLabelView.setContentText(initialDepositAmount.toRandAmount())
            binding.fromAccountSecondaryContentAndLabelView.setContentText(initialDepositAccount.accountInformation)
            binding.initialDepositReferenceSecondaryContentAndLabelView.setContentText(initialDepositReference)
            binding.interestRateSecondaryContentAndLabelView.setContentText("$interestRate %")
            binding.interestPaymentAccountSecondaryContentAndLabelView.setContentText(interestPaymentAccount)

            if (recurringPaymentAccount.accountNumber.isNotBlank()) {
                binding.recurringPaymentConstraintLayout.visibility = View.VISIBLE
                binding.recurringAmountSecondaryContentAndLabelView.setContentText(recurringPaymentAmount.toRandAmount())
                binding.recurringFromAccountSecondaryContentAndLabelView.setContentText(recurringPaymentAccount.accountInformation)
                binding.recurringPaymentFrequencySecondaryContentAndLabelView.setContentText(getString(R.string.depositor_plus_monthly))
                binding.numberOfPaymentsSecondaryContentAndLabelView.setContentText(numberOfPayments)
                binding.recurringStartDateSecondaryContentAndLabelView.setContentText(recurringPaymentStartDate)
                binding.recurringEndDateSecondaryContentAndLabelView.setContentText(recurringPaymentEndDate)
                binding.recurringPaymentReferenceSecondaryContentAndLabelView.setContentText(recurringPaymentReference)
            }

            if (interestAccountNumber.isNotBlank()) {
                binding.interestPaymentConstraintLayout.visibility = View.VISIBLE
                binding.bankSecondaryContentAndLabelView.setContentText(interestBankName)
                binding.accountTypeSecondaryContentAndLabelView.setContentText(interestAccountType)
                binding.accountNumberSecondaryContentAndLabelView.setContentText(interestAccountNumber.toFormattedAccountNumber())
                binding.branchCodeSecondaryContentAndLabelView.setContentText(interestBranchCode)
                binding.referenceSecondaryContentAndLabelView.setContentText(interestReference)
            }

            if (!isPersonalClientAgreementAccepted) {
                fetchPersonalClientAgreementDetails()
            } else {
                setupPersonalClientAgreement()
            }
        }
    }

    private fun setupListeners() {
        binding.confirmButton.setOnClickListener {
            when {
                !binding.disclaimerCheckBoxView.isChecked -> binding.disclaimerCheckBoxView.setErrorMessage(R.string.depositor_plus_please_accept_disclaimer)
                !binding.personalClientAgreementCheckBoxView.isChecked -> binding.personalClientAgreementCheckBoxView.setErrorMessage(getString(R.string.fixed_deposit_please_agree_to_these_terms))
                !isPersonalClientAgreementAccepted -> saveAndInvestViewModel.updatePersonalClientAgreement()
                else -> createAndLinkAccount()
            }
        }
    }

    private fun setupPersonalClientAgreement() {
        val checkboxText = if (isPersonalClientAgreementAccepted) R.string.client_agreement_have_accepted else R.string.depositor_plus_accept_personal_client_agreement
        ClientAgreementHelper.updateClientAgreementContainer(binding.personalClientAgreementCheckBoxView, isPersonalClientAgreementAccepted, checkboxText, R.string.personal_client_agreement, object : ClickableSpan() {
            override fun onClick(view: View) {
                if (NetworkUtils.isNetworkConnected()) {
                    val url = PdfUtil.getLink(CustomerProfileObject.instance.clientTypeGroup)
                    navigateToPersonalClientAgreement(TermsAndConditionsInfo(shouldDisplayCheckBox = false, isDocFusionDocument = true, url = url, cacheKey = url.hash()))
                } else {
                    Toast.makeText(context, getString(R.string.network_connection_error), Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    abstract fun navigateToPersonalClientAgreement(termsAndConditionsInfo: TermsAndConditionsInfo)

    private fun setupObservers() {
        saveAndInvestViewModel.successResponse.observe(viewLifecycleOwner, { response ->
            if (response is ClientAgreementDetails) {
                isPersonalClientAgreementAccepted = absaCacheService.isPersonalClientAgreementAccepted()
                setupPersonalClientAgreement()
                dismissProgressDialog()
            } else {
                createAndLinkAccount()
            }
        })
    }

    private fun createAndLinkAccount() {
        accountCreateAndLinkViewModel.createAndLinkAccount(buildAccountCreateAndLinkRequest())
        accountCreateAndLinkViewModel.accountCreateAndLinkLiveData.observe(viewLifecycleOwner, {
            saveAccountDetails(it)
            accountCreateAndLinkViewModel.accountCreateAndLinkLiveData.removeObservers(viewLifecycleOwner)
        })

        accountCreateAndLinkViewModel.failureLiveData.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            navigateOnCreateAccountFailureResponse()
            accountCreateAndLinkViewModel.failureLiveData.removeObservers(viewLifecycleOwner)
        })
    }

    private fun saveAccountDetails(accountCreateAndLinkStatus: AccountCreateAndLinkStatus) {
        saveAndInvestViewModel.employmentDetails.accountNumber = accountCreateAndLinkStatus.accountNumber
        saveEmploymentDetailsInfo()
        employmentDetailsViewModel.employmentDetailsLiveData.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            navigateOnCreateAccountSuccessResponse(accountCreateAndLinkStatus)
        })

        employmentDetailsViewModel.failureLiveData.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            navigateOnCreateAccountSuccessResponse(accountCreateAndLinkStatus)
        })
    }

    fun sourceOfFundsCodes(): String {
        var sourceOfFundsString = ""
        saveAndInvestViewModel.selectedSourceOfFunds.forEach { sourceOfFundsString += "${it.code.toSentenceCase()}," }
        return sourceOfFundsString.trimEnd(',')
    }

    protected open fun buildSuccessResultScreenProperties(accountDetails: AccountCreateAndLinkStatus, onDoneClicked: () -> Unit): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            onDoneClicked.invoke()
            loadAccountsClearingAccountProfileAndShowHomeScreen()
        }

        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.depositor_plus_account_opened_success))
                .setDescription(getString(R.string.depositor_plus_account_opened_success_message, saveAndInvestActivity.featureName, accountDetails.accountNumber))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(true)
    }

    protected open fun buildLinkAccountResultScreenProperties(accountDetails: AccountCreateAndLinkStatus, onDoneClicked: () -> Unit): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            onDoneClicked.invoke()
            startActivity(Intent(baseActivity, ManageAccountsActivity::class.java))
        }
        GenericResultScreenFragment.setSecondaryButtonOnClick { loadAccountsClearingAccountProfileAndShowHomeScreen() }

        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.depositor_plus_account_opened_success_link_unsuccessful))
                .setDescription(getString(R.string.depositor_plus_account_opened_success_link_profile, saveAndInvestActivity.featureName, accountDetails.accountNumber))
                .setPrimaryButtonLabel(getString(R.string.depositor_plus_account_link_account))
                .setSecondaryButtonLabel(getString(R.string.done))
                .build(true)
    }

    protected open fun buildFundAccountResultScreenProperties(accountDetails: AccountCreateAndLinkStatus, onDoneClicked: () -> Unit): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            onDoneClicked.invoke()
            loadAccountsClearingAccountProfileAndGoHomeWithShortcut(getString(R.string.transfer))
        }
        GenericResultScreenFragment.setSecondaryButtonOnClick { loadAccountsClearingAccountProfileAndShowHomeScreen() }

        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalSuccess)
                .setTitle(getString(R.string.depositor_plus_account_opened_success_fund_account))
                .setDescription(getString(R.string.depositor_plus_account_opened_please_transfer, saveAndInvestActivity.featureName, accountDetails.accountNumber.toString().toFormattedAccountNumber(), TextFormatUtils.formatBasicAmountAsRand(saveAndInvestViewModel.initialDepositAmount)))
                .setPrimaryButtonLabel(getString(R.string.depositor_plus_transfer_funds))
                .setSecondaryButtonLabel(getString(R.string.done))
                .build(true)
    }

    protected open fun buildFailureResultScreenProperties(onDoneClicked: () -> Unit): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            onDoneClicked.invoke()
            navigateToHomeScreenSelectingHomeIcon()
        }
        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalError)
                .setTitle(getString(R.string.depositor_plus_something_went_wrong))
                .setDescription(getString(R.string.depositor_plus_error_opening_account))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(false)
    }
}