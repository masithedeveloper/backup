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

package com.barclays.absa.banking.expressCashSend.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.CashSendExistingBeneficiaryFragmentBinding
import com.barclays.absa.banking.express.cashSend.validateCashSend.CashSendValidationViewModel
import com.barclays.absa.banking.express.shared.dto.CashSendInstructionType
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.utils.extensions.viewBinding

class CashSendExistingBeneficiaryFragment : CashSendBaseFragment(R.layout.cash_send_existing_beneficiary_fragment) {

    private val binding by viewBinding(CashSendExistingBeneficiaryFragmentBinding::bind)
    private val validateCashSendViewModel by activityViewModels<CashSendValidationViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.CASHSEND_OVERVIEW_CONST, BMBConstants.CASHSEND_CONST, BMBConstants.TRUE_CONST)
        setToolBar(R.string.recipient_label)
        onPopulateView()
    }

    private fun onPopulateView() {
        val existingBeneficiary = cashSendViewModel.beneficiaryDetail
        val beneficiaryName = "${existingBeneficiary.beneficiaryDetails.beneficiaryName} ${existingBeneficiary.beneficiaryDetails.beneficiarySurname}"
        with(binding) {
            existingBeneficiaryNameTextView.text = beneficiaryName
            cashSendStatementReferenceLabelView.setContentText(existingBeneficiary.beneficiaryDetails.statementReference)
            cashSendLastPaymentDateLabelView.setContentText(existingBeneficiary.beneficiaryDetails.lastPaymentDateAndTime)
            cancelCashSendButton.setOnClickListener {
                navigate(CashSendExistingBeneficiaryFragmentDirections.actionCashSendExistingBeneficiaryFragmentToCashSendDetailFragment())
            }
            cashSendToThisBeneficiaryButton.setOnClickListener {
                cashSendViewModel.cashSendValidationDataModel.apply {
                    tieBreaker = existingBeneficiary.beneficiaryDetails.tieBreaker
                    cifKey = existingBeneficiary.beneficiaryDetails.cifKey
                    beneficiaryNumber = existingBeneficiary.beneficiaryDetails.beneficiaryNumber.toString()
                    beneficiaryShortName = existingBeneficiary.beneficiaryDetails.beneficiaryShortName
                    instructionType = CashSendInstructionType.CASHSEND_BENEFICIARY.value
                }
                validateCashSendViewModel.validateCashSend(cashSendViewModel.cashSendValidationDataModel)
                validateCashSendViewModel.cashSendValidationResponseLiveData.observe(viewLifecycleOwner) { cashSendValidationResponse ->
                    dismissProgressDialog()
                    cashSendViewModel.cashSendValidationResponse = cashSendValidationResponse
                    cashSendViewModel.cashSendValidationDataModel = cashSendViewModel.cashSendValidationDataModel
                    navigate(CashSendExistingBeneficiaryFragmentDirections.actionCashSendExistingBeneficiaryFragmentToCashSendOnceOffConfirmFragment(cashSendViewModel.cashSendValidationDataModel.pin))
                }
            }
        }
    }
}