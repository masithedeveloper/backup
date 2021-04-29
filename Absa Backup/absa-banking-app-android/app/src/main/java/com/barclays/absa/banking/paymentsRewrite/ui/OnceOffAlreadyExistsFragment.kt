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
import com.barclays.absa.banking.databinding.OnceOffAlreadyExistsFragmentBinding
import com.barclays.absa.utils.extensions.viewBinding

class OnceOffAlreadyExistsFragment : PaymentsBaseFragment(R.layout.once_off_already_exists_fragment) {
    private val binding by viewBinding(OnceOffAlreadyExistsFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        paymentsActivity.revertToolbarForTabs()
        paymentsViewModel.updateToOnceOffFlow()

        if (paymentsViewModel.isBillPayment) {
            setToolBar(R.string.institution)
        } else {
            setToolBar(R.string.recipient_title)
        }
        setupLayout()
        setupClickListeners()
    }

    private fun setupLayout() {
        binding.onceOffExistsTitleAndDescriptionView.description = getString(R.string.payments_already_made_once_off_description, paymentsViewModel.selectedBeneficiary.targetAccountNumber)
    }

    private fun setupClickListeners() {
        with(binding) {
            addBeneficiaryButton.setOnClickListener {
                paymentsViewModel.updateToBeneficiaryFlow()
                navigate(OnceOffAlreadyExistsFragmentDirections.actionOnceOffAlreadyExistsFragmentToBeneficiaryDetailsConfirmationFragment())
            }
            cancelButton.setOnClickListener {
                popBackToPaymentsHub()
            }
        }
    }
}