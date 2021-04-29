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

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.BeneficiaryDetailsConfirmationFragmentBinding
import com.barclays.absa.banking.express.beneficiaries.addRegularBeneficiary.dto.AddBeneficiaryRequest
import com.barclays.absa.banking.express.beneficiaries.dto.BeneficiaryType
import com.barclays.absa.banking.express.beneficiaries.enquireRegularBeneficiary.dto.BeneficiaryEnquiryRequest
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.content.BeneficiaryListItem

class BeneficiaryDetailsConfirmationFragment : PaymentsBaseFragment(R.layout.beneficiary_details_confirmation_fragment) {

    private val binding by viewBinding(BeneficiaryDetailsConfirmationFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        paymentsActivity.revertToolbarForTabs()
        paymentsViewModel.beneficiaryAdded = false

        if (paymentsViewModel.isBillPayment) {
            setToolBar(R.string.institution)
        } else {
            setToolBar(R.string.recipient_label)
        }

        with(binding) {
            with(paymentsViewModel.selectedBeneficiary) {
                if (beneficiaryDetails.typeOfBeneficiary == BeneficiaryType.INSTITUTIONAL) {
                    beneficiaryView.setBeneficiary(BeneficiaryListItem().apply {
                        name = beneficiaryDetails.bankOrInstitutionName
                        accountNumber = getString(R.string.institution_name)
                    })
                    bankSecondaryContentAndLabelView.setLabelText(getString(R.string.account_holder_name))
                    bankSecondaryContentAndLabelView.setContentText(beneficiaryDetails.targetAccountReference)
                    branchSecondaryContentAndLabelView.visibility = View.GONE
                    branchCodeSecondaryContentAndLabelView.visibility = View.GONE
                    accountTypeSecondaryContentAndLabelView.visibility = View.GONE
                    accountNumberSecondaryContentAndLabelView.setLabelText(getString(R.string.bill_account_number))
                    accountNumberSecondaryContentAndLabelView.setContentText(beneficiaryDetails.targetAccountNumber)
                    if (beneficiaryDetails.sourceAccountReference.isNotEmpty()) {
                        myReferenceSecondaryContentAndLabelView.setLabelText(getString(R.string.reference_for_my_statement))
                        myReferenceSecondaryContentAndLabelView.setContentText(beneficiaryDetails.sourceAccountReference)
                    } else {
                        myReferenceSecondaryContentAndLabelView.visibility = View.GONE
                    }
                    paymentNotificationSecondaryContentAndLabelView.visibility = View.GONE

                    beneficiaryReferenceSecondaryContentAndLabelView.visibility = View.GONE
                } else {
                    beneficiaryView.setBeneficiary(BeneficiaryListItem().apply {
                        name = beneficiaryDetails.beneficiaryName
                        accountNumber = getString(R.string.beneficiary_name)
                    })
                    if (beneficiaryDetails.branchName.isEmpty()) {
                        branchSecondaryContentAndLabelView.visibility = View.GONE
                    } else {
                        branchSecondaryContentAndLabelView.contentTextView.text = beneficiaryDetails.branchName
                    }
                    bankSecondaryContentAndLabelView.contentTextView.text = beneficiaryDetails.bankOrInstitutionName
                    branchCodeSecondaryContentAndLabelView.contentTextView.text = beneficiaryDetails.clearingCodeOrInstitutionCode
                    accountTypeSecondaryContentAndLabelView.contentTextView.text = getAccountTypeString(beneficiaryDetails.accountType)
                    accountNumberSecondaryContentAndLabelView.setContentText(beneficiaryDetails.targetAccountNumber)
                    myReferenceSecondaryContentAndLabelView.contentTextView.text = beneficiaryDetails.sourceAccountReference
                    paymentNotificationSecondaryContentAndLabelView.contentTextView.text = getNotificationMethodDetails(beneficiaryDetails.beneficiaryNotification)
                    beneficiaryReferenceSecondaryContentAndLabelView.contentTextView.text = beneficiaryDetails.targetAccountReference
                }
            }

            nextButton.setOnClickListener {
                if (paymentsViewModel.isOnceOffPayment) {
                    navigate(BeneficiaryDetailsConfirmationFragmentDirections.actionBeneficiaryDetailsConfirmationFragmentToPaymentDetailsFragment())
                } else {
                    addRegularBeneficiaryViewModel.addBeneficiary(AddBeneficiaryRequest(paymentsViewModel.selectedBeneficiary.beneficiaryDetails))

                    addRegularBeneficiaryViewModel.addBeneficiaryResponse.observe(viewLifecycleOwner, {
                        paymentsViewModel.beneficiarySuccessfullyAdded()

                        enquireBeneficiaryDetailsViewModel.fetchBeneficiaryDetails(BeneficiaryEnquiryRequest().apply {
                            beneficiaryNumber = it.beneficiaryNumber
                            uniqueEFTNumber = it.uniqueEFTNumber
                            tieBreaker = it.tieBreaker
                            cifKey = it.cifKey
                            instructionType = it.instructionType
                        })

                        enquireBeneficiaryDetailsViewModel.beneficiaryDetailsResponse.observe(viewLifecycleOwner, {
                            dismissProgressDialog()
                            paymentsViewModel.selectedBeneficiary.beneficiaryDetails = it.beneficiaryDetails
                            navigate(BeneficiaryDetailsConfirmationFragmentDirections.actionBeneficiaryDetailsConfirmationFragmentToPaymentDetailsFragment())
                        })
                    })
                }
            }
        }
    }
}