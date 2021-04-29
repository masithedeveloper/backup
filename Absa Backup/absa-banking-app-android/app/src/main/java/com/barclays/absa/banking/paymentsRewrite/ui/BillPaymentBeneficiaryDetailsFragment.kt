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

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.BillPaymentDetailsFragmentBinding
import com.barclays.absa.banking.express.beneficiaries.beneficiariesForBranchAndAccount.BeneficiariesForBranchAndAccountViewModel
import com.barclays.absa.banking.express.beneficiaries.dto.BeneficiaryType
import com.barclays.absa.banking.express.beneficiaries.dto.RegularBeneficiary
import com.barclays.absa.banking.express.payments.absaListedBeneficiaries.dto.AbsaListedBeneficiary
import com.barclays.absa.banking.express.shared.dto.InstructionType
import com.barclays.absa.utils.extensions.toSelectorList
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.forms.Form
import styleguide.forms.StringItem
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import styleguide.utils.extensions.toTitleCase

class BillPaymentBeneficiaryDetailsFragment : PaymentsBaseFragment(R.layout.bill_payment_details_fragment) {
    private val binding by viewBinding(BillPaymentDetailsFragmentBinding::bind)
    private val beneficiariesForBranchAndAccountViewModel by viewModels<BeneficiariesForBranchAndAccountViewModel>()

    companion object {
        fun newInstance() = BillPaymentBeneficiaryDetailsFragment()
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
        setupClickListeners()
        //beneficiaryImageHelper.setupImageHelper(binding.beneficiaryPictureRoundedImageView)
    }

    private fun setupClickListeners() {
        with(binding) {
            nextButton.setOnClickListener {
                if (Form(parentConstraintLayout).isValid()) {
                    paymentsViewModel.isBillPayment = true

                    paymentsViewModel.selectedBeneficiary.apply {
                        targetAccountNumber = accountNumberNormalInputView.selectedValueUnmasked.trim()
                        beneficiaryDetails.targetAccountNumber = accountNumberNormalInputView.selectedValueUnmasked.trim()
                        beneficiaryDetails.sourceAccountReference = myReferenceNormalInputView.selectedValue.trim()
                        beneficiaryDetails.targetAccountReference = accountHolderNameNormalInputView.selectedValue.trim()
                        beneficiaryDetails.beneficiaryName = accountHolderNameNormalInputView.selectedValue.trim()
                        beneficiaryName = beneficiaryDetails.beneficiaryName

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
            institutionNormalInputView.addRequiredValidationHidingTextWatcher(R.string.validation_error_choose_institution)
            accountNumberNormalInputView.addRequiredValidationHidingTextWatcher(R.string.validation_error_account_number)
            accountHolderNameNormalInputView.addRequiredValidationHidingTextWatcher(R.string.validation_error_account_holder_name)
            myReferenceNormalInputView.addRequiredValidationHidingTextWatcher(R.string.validation_error_reference)
            myReferenceNormalInputView.setImeOptions(EditorInfo.IME_ACTION_DONE)
        }
    }

    private fun fetchBankNames() {
        if (!absaListedBeneficiariesViewModel.isAbsaListedBeneficiariesInitialised()) {
            absaListedBeneficiariesViewModel.fetchAbsaListedBeneficiaries()

            absaListedBeneficiariesViewModel.absaListedBeneficiariesResponse.observe(viewLifecycleOwner, {
                dismissProgressDialog()
                populateBankData(it.institutionalBeneficiaryList)
            })
        } else {
            populateBankData(absaListedBeneficiariesViewModel.absaListedBeneficiariesResponse.value?.institutionalBeneficiaryList)
        }
    }

    private fun populateBankData(absaListedBeneficiaryList: List<AbsaListedBeneficiary>?) {
        absaListedBeneficiaryList?.toSelectorList { absaListedBeneficiary -> StringItem(absaListedBeneficiary.institutionName.toTitleCase(), absaListedBeneficiary.institutionCode) }?.let { absaListedBeneficiarySelectorList ->
            binding.institutionNormalInputView.setList(absaListedBeneficiarySelectorList, getString(R.string.select_institution))

            binding.institutionNormalInputView.setItemSelectionInterface { index ->
                val absaListedBeneficiary = absaListedBeneficiaryList[index]
                with(paymentsViewModel.selectedBeneficiary.beneficiaryDetails) {
                    typeOfBeneficiary = BeneficiaryType.INSTITUTIONAL
                    clearingCodeOrInstitutionCode = absaListedBeneficiary.institutionCode
                    institutionAccountNumber = absaListedBeneficiary.institutionAccountNumber
                    bankOrInstitutionName = absaListedBeneficiary.institutionName
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        //beneficiaryImageHelper = null
    }
}