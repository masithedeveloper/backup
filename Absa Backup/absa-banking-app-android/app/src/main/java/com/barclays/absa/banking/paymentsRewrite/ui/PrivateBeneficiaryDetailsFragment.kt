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
 */
package com.barclays.absa.banking.paymentsRewrite.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.beneficiaries.ui.beneficiarySwitching.dto.OcrResponse
import com.barclays.absa.banking.databinding.PrivateBeneficiaryDetailsFragmentBinding
import com.barclays.absa.banking.express.beneficiaries.beneficiariesForBranchAndAccount.BeneficiariesForBranchAndAccountViewModel
import com.barclays.absa.banking.express.beneficiaries.dto.BeneficiaryAccountType
import com.barclays.absa.banking.express.beneficiaries.dto.BeneficiaryType
import com.barclays.absa.banking.express.beneficiaries.dto.RegularBeneficiary
import com.barclays.absa.banking.express.beneficiaries.dto.TypeOfBeneficiaryList
import com.barclays.absa.banking.express.payments.listBankNames.dto.BankNameDetails
import com.barclays.absa.banking.express.shared.dto.InstructionType
import com.barclays.absa.banking.framework.app.BMBConstants.ABSA_BRANCH_CODE
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.payments.BeneficiaryImportBaseActivity
import com.barclays.absa.banking.payments.BeneficiaryImportExplanationActivity
import com.barclays.absa.banking.presentation.shared.NotificationMethodSelectionActivity
import com.barclays.absa.banking.presentation.shared.NotificationMethodSelectionActivity.NOTIFICATION_METHOD_BENEFICIARY_REQUEST_CODE
import com.barclays.absa.utils.BannerManager.getLargeBannerShownCount
import com.barclays.absa.utils.BannerManager.incrementLargeBannerShownCount
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.extensions.hasSingleItem
import com.barclays.absa.utils.extensions.toSelectorList
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.cards.Alert
import styleguide.cards.Offer
import styleguide.forms.Form
import styleguide.forms.StringItem
import styleguide.forms.notificationmethodview.NotificationMethodData
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import styleguide.utils.extensions.removeParentheses
import styleguide.utils.extensions.toTitleCase
import java.util.*

class PrivateBeneficiaryDetailsFragment : PaymentsBaseFragment(R.layout.private_beneficiary_details_fragment) {

    private val binding by viewBinding(PrivateBeneficiaryDetailsFragmentBinding::bind)
    private val beneficiariesForBranchAndAccountViewModel by viewModels<BeneficiariesForBranchAndAccountViewModel>()

    private var selectBankHasMultipleBranches = false

    //private var beneficiaryImageHelper: BeneficiaryImageHelperInterface? = null

    companion object {
        fun newInstance() = PrivateBeneficiaryDetailsFragment()

        private const val REQUEST_CODE_BENEFICIARY_IMPORT = 8000
        private const val ABSA_BANK = "Absa Bank"
        private const val ABSA = "Absa"
        private const val FNB = "FIRST NATIONAL BANK"
        private const val NUMBER_OF_TIMES_LARGE_BANNER_SHOULD_BE_DISPLAYED = 5
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        paymentsViewModel.selectedBeneficiary = RegularBeneficiary().apply {
            beneficiaryDetails.instructionType = if (paymentsViewModel.isOnceOffPayment) InstructionType.ONCE_OFF_PAYMENT else InstructionType.NORMAL
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInputViews()
        fetchBankNames()
        populateAccountTypes()
        setupClickListeners()
        //beneficiaryImageHelper.setupImageHelper(binding.beneficiaryPictureRoundedImageView)

        if (featureSwitchingToggles.beneficiarySwitching == FeatureSwitchingStates.ACTIVE.key && !paymentsViewModel.isOnceOffPayment) {
            val numberOfTimesLargeBannerDisplayed = getLargeBannerShownCount()
            if (numberOfTimesLargeBannerDisplayed > NUMBER_OF_TIMES_LARGE_BANNER_SHOULD_BE_DISPLAYED) {
                createBeneficiarySwitchingSmallCard()
            } else {
                incrementLargeBannerShownCount()
                createBeneficiarySwitchingLargeCard()
            }
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            nextButton.setOnClickListener {
                if (Form(parentConstraintLayout).isValid()) {
                    paymentsViewModel.isBillPayment = false

                    paymentsViewModel.selectedBeneficiary.apply {
                        targetAccountNumber = accountNumberNormalInputView.selectedValueUnmasked.trim()
                        beneficiaryDetails.sourceAccountReference = myReferenceNormalInputView.selectedValue.trim()
                        beneficiaryDetails.targetAccountReference = theirReferenceNormalInputView.selectedValue.trim()
                        beneficiaryDetails.targetAccountNumber = targetAccountNumber
                        beneficiaryDetails.beneficiaryName = beneficiaryNameNormalInputView.text?.trim() ?: ""

                        beneficiariesForBranchAndAccountViewModel.fetchBeneficiariesForBranchAndAccount(beneficiaryDetails.clearingCodeOrInstitutionCode, beneficiaryDetails.targetAccountNumber)
                        beneficiariesForBranchAndAccountViewModel.beneficiariesForBranchAndAccountResponse.observe(viewLifecycleOwner, {
                            dismissProgressDialog()
                            handleNavigationForBranchAndAccount(it.typeOfBeneficiaryList)
                        })
                    }
                }
            }
        }
    }

    private fun setupInputViews() {
        with(binding) {
            beneficiaryNameNormalInputView.addRequiredValidationHidingTextWatcher(R.string.validation_error_beneficiary_name)
            accountNumberNormalInputView.addRequiredValidationHidingTextWatcher(R.string.validation_error_account_number)
            theirReferenceNormalInputView.addRequiredValidationHidingTextWatcher(R.string.validation_error_reference)
            myReferenceNormalInputView.addRequiredValidationHidingTextWatcher(R.string.validation_error_reference)
            branchNormalInputView.setViewEnabled(selectBankHasMultipleBranches)

            myReferenceNormalInputView.setImeOptions(IME_ACTION_DONE)

            branchNormalInputView.setOnClickListener {
                preventDoubleClick(branchNormalInputView)
                if (bankNormalInputView.selectedValue.isEmpty()) {
                    bankNormalInputView.setError(R.string.plz_sel_bank)
                } else if (selectBankHasMultipleBranches) {
                    branchNormalInputView.triggerListActivity()
                }
            }

            val beneficiaryNotification = paymentsViewModel.selectedBeneficiary.beneficiaryDetails.beneficiaryNotification
            paymentNotificationNormalInputView.selectedValue = getNotificationMethodDetails(beneficiaryNotification)

            paymentNotificationNormalInputView.setOnClickListener {
                val notificationMethodSelectionIntent = Intent(context, NotificationMethodSelectionActivity::class.java).apply {
                    putExtra(NotificationMethodSelectionActivity.TOOLBAR_TITLE, getString(R.string.payment_notification_title))
                    putExtra(NotificationMethodSelectionActivity.SHOW_BENEFICIARY_NOTIFICATION_METHOD, true)
                    putExtra(NotificationMethodSelectionActivity.LAST_SELECTED_TYPE_FOR_BENEFICIARY, beneficiaryNotification.notificationMethod.name)
                    putExtra(NotificationMethodSelectionActivity.LAST_SELECTED_VALUE_FOR_BENEFICIARY, getNotificationMethodDetails(beneficiaryNotification))
                }
                startActivityForResult(notificationMethodSelectionIntent, NOTIFICATION_METHOD_BENEFICIARY_REQUEST_CODE)
            }
        }
    }

    private fun fetchBankNames() {
        if (!listBankNamesViewModel.isBankNamesInitialised()) {
            listBankNamesViewModel.fetchListOfBanks()

            listBankNamesViewModel.bankNamesResponse.observe(viewLifecycleOwner, {
                dismissProgressDialog()
                populateBankData(it.listOfBankNames)
            })
        } else {
            populateBankData(listBankNamesViewModel.bankNamesResponse.value?.listOfBankNames)
        }
    }

    private fun populateBankData(bankNameList: List<BankNameDetails>?) {
        bankNameList?.toSelectorList { bankNameDetails -> StringItem(bankNameDetails.bankNames.toTitleCase()) }?.let { bankNamesSelectorList ->
            bankNamesSelectorList.add(0, StringItem(ABSA_BANK))
            binding.bankNormalInputView.setList(bankNamesSelectorList, getString(R.string.select_bank))

            var selectBankIndex = -1

            binding.bankNormalInputView.setItemSelectionInterface { index ->
                binding.branchNormalInputView.isEnabled = true
                if (selectBankIndex != index) {
                    binding.branchNormalInputView.clearSelectedIndexAndValue()
                    populateBranchesData(bankNamesSelectorList[index].item ?: "")
                }
                selectBankIndex = index
            }
        }
    }

    private fun populateBranchesData(bank: String) {
        if (bank == ABSA_BANK) {
            with(paymentsViewModel.selectedBeneficiary.beneficiaryDetails) {
                bankOrInstitutionName = ABSA
                typeOfBeneficiary = BeneficiaryType.INTERNAL
                clearingCodeOrInstitutionCode = ABSA_BRANCH_CODE
            }
            binding.branchNormalInputView.isEnabled = false
            binding.branchNormalInputView.text = ABSA_BRANCH_CODE
            selectBankHasMultipleBranches = false
            return
        }
        paymentsViewModel.selectedBeneficiary.beneficiaryDetails.typeOfBeneficiary = BeneficiaryType.EXTERNAL
        paymentsViewModel.selectedBeneficiary.beneficiaryDetails.bankOrInstitutionName = bank

        listBranchCodesForBankViewModel.fetchBranchesForBank(bank.toUpperCase(Locale.ENGLISH))
        listBranchCodesForBankViewModel.bankBranchCodesResponse.observe(viewLifecycleOwner, { branchCodesResponse ->
            dismissProgressDialog()

            val branchStringItems = branchCodesResponse.branchCodesList.toSelectorList { StringItem(it.branchName.toTitleCase(), it.bankCode) }
            binding.branchNormalInputView.setList(branchStringItems, getString(R.string.select_branch))

            branchCodesResponse.branchCodesList.let { branchList ->
                if (branchList.hasSingleItem()) {
                    paymentsViewModel.checkIfIipIsAllowed(branchList.first())
                    binding.branchNormalInputView.selectedIndex = 0
                    setBranchDetails(branchList.first().branchName, branchList.first().bankCode)
                    binding.branchNormalInputView.isEnabled = false
                    selectBankHasMultipleBranches = false
                } else {
                    binding.branchNormalInputView.clear()
                    binding.branchNormalInputView.isEnabled = true
                    selectBankHasMultipleBranches = true
                }
            }

            binding.branchNormalInputView.setItemSelectionInterface { index ->
                branchCodesResponse.branchCodesList.let { branchList ->
                    val selectedBranch = branchList[index]
                    paymentsViewModel.checkIfIipIsAllowed(selectedBranch)
                    setBranchDetails(selectedBranch.branchName, selectedBranch.bankCode)
                }
            }
        })
    }

    private fun setBranchDetails(branchName: String, branchCode: String) {
        binding.branchNormalInputView.text = branchName.toTitleCase()
        paymentsViewModel.selectedBeneficiary.beneficiaryDetails.branchName = branchName.toTitleCase()
        paymentsViewModel.selectedBeneficiary.beneficiaryDetails.clearingCodeOrInstitutionCode = branchCode
    }

    private fun populateAccountTypes() {
        val accountTypeList = listOf(getString(R.string.chequeAccount), getString(R.string.savingAccount))
        val accountTypeSelectorList = accountTypeList.toSelectorList { StringItem(it) }

        binding.accountTypeNormalInputView.setList(accountTypeSelectorList, getString(R.string.select_account_type))
        binding.accountTypeNormalInputView.setItemSelectionInterface { position ->
            when (position) {
                0 -> paymentsViewModel.selectedBeneficiary.beneficiaryDetails.accountType = BeneficiaryAccountType.CURRENT_ACCOUNT
                else -> paymentsViewModel.selectedBeneficiary.beneficiaryDetails.accountType = BeneficiaryAccountType.SAVINGS_ACCOUNT
            }
        }
    }

    private fun createBeneficiarySwitchingLargeCard() {
        val switchingOffer = Offer(text = getString(R.string.payments_import_beneficiaries_offer_title))
        with(binding.importBeneficiariesOfferView) {
            setCardViewColor(ContextCompat.getColor(context, R.color.transaction_card_dark_background_color))
            visibility = View.VISIBLE
            offer = switchingOffer
            hideLabel()
            setActionButtonOnClickListener {
                startActivityForResult(Intent(activity, BeneficiaryImportExplanationActivity::class.java), REQUEST_CODE_BENEFICIARY_IMPORT)
            }
        }
    }

    private fun createBeneficiarySwitchingSmallCard() {
        with(binding.importBeneficiariesAlertView) {
            setAlert(Alert(getString(R.string.beneficiary_import_alert_title)))
            setImageViewWidget(R.drawable.ic_camera_dark)
            visibility = View.VISIBLE
            hideTextView()
            setOnClickListener {
                startActivityForResult(Intent(activity, BeneficiaryImportExplanationActivity::class.java), REQUEST_CODE_BENEFICIARY_IMPORT)
            }
        }
    }

    private fun populateBeneficiaryDetails(ocrResponse: OcrResponse) {
        binding.beneficiaryNameNormalInputView.text = ocrResponse.name.removeParentheses()
        binding.accountNumberNormalInputView.text = ocrResponse.accountNumber
        val bankName = ocrResponse.bank ?: ""
        if (bankName.startsWith(ABSA, true)) {
            val accountTypeIndex = paymentsViewModel.determineAccountTypeIndex(ocrResponse)
            binding.accountTypeNormalInputView.simulateIndexSelected(accountTypeIndex)
            binding.bankNormalInputView.simulateIndexSelected(0)
        } else {
            with(paymentsViewModel.selectedBeneficiary.beneficiaryDetails) {
                typeOfBeneficiary = BeneficiaryType.EXTERNAL
                clearingCodeOrInstitutionCode = ocrResponse.branchCode.toString()
                bankOrInstitutionName = bankName
            }
            binding.bankNormalInputView.text = bankName
            binding.branchNormalInputView.text = ocrResponse.branchCode
            setUpBankForExternalBank(bankName)
        }

        binding.myReferenceNormalInputView.text = ocrResponse.myReference ?: ""
        binding.theirReferenceNormalInputView.text = ocrResponse.theirReference ?: ""
    }

    private fun setUpBankForExternalBank(bankName: String) {
        val universalBank = CommonUtils.getUniversalBank(bankName)
        if (universalBank != null) {
            binding.branchNormalInputView.setTitleText(getString(R.string.payment_universal))
            binding.branchNormalInputView.isEnabled = (ABSA_BANK != bankName)
            binding.branchNormalInputView.setImageViewVisibility(View.INVISIBLE)
            paymentsViewModel.isIipAllowed = universalBank.immediatePaymentAllowed.toBoolean()
            if (!FNB.equals(bankName, ignoreCase = true)) {
                paymentsViewModel.selectedBeneficiary.beneficiaryDetails.clearingCodeOrInstitutionCode = universalBank.branchCode
                binding.branchNormalInputView.text = universalBank.branchCode
                binding.accountTypeNormalInputView.isFocusable = true
                binding.accountTypeNormalInputView.requestFocus()
            } else {
                binding.branchNormalInputView.setTitleText(getString(R.string.branch))
                binding.branchNormalInputView.setImageViewVisibility(View.VISIBLE)
            }
        } else {
            with(binding.branchNormalInputView) {
                setTitleText(getString(R.string.branch))
                setImageViewVisibility(View.VISIBLE)
                isEnabled = true
                clear()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                NOTIFICATION_METHOD_BENEFICIARY_REQUEST_CODE -> {
                    data?.getParcelableExtra<NotificationMethodData>(NotificationMethodSelectionActivity.SHOW_BENEFICIARY_NOTIFICATION_METHOD)?.let { notificationMethodData ->
                        updateNotificationMethodDetails(paymentsViewModel.selectedBeneficiary.beneficiaryDetails.beneficiaryNotification, notificationMethodData, binding.paymentNotificationNormalInputView)
                    }
                }
                REQUEST_CODE_BENEFICIARY_IMPORT -> {
                    if (data != null) {
                        val ocrResponse: OcrResponse? = data.getParcelableExtra(BeneficiaryImportBaseActivity.ocrResult)
                        if (ocrResponse != null) {
                            populateBeneficiaryDetails(ocrResponse)
                            showMessage(getString(R.string.note), getString(R.string.beneficiary_import_check_details_message), null)
                        } else {
                            showGenericErrorMessage()
                        }
                    }
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        //beneficiaryImageHelper = null
    }
}