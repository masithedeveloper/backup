/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.card.ui.debitCard.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.CardReplacementAndFetchFees
import com.barclays.absa.banking.boundary.model.debitCard.DebitCardReplacementConfirmation
import com.barclays.absa.banking.dualAuthorisations.ui.pendingAuthorisation.DualAuthDebitCardReplacementPendingActivity
import com.barclays.absa.banking.framework.app.BMBConstants.*
import com.barclays.absa.banking.framework.utils.TelephoneUtil
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.TextFormatUtils
import kotlinx.android.synthetic.main.stop_and_replace_debit_card_overview_fragment.*
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.toTitleCaseRemovingCommas

const val SURE_CHECK_DELAY_MILLIS = 250L

class StopAndReplaceDebitCardOverviewFragment : DebitOrderStopAndReplaceBaseFragment(R.layout.stop_and_replace_debit_card_overview_fragment) {
    private var cardReplacementAndFetchFees = CardReplacementAndFetchFees()
    private var branchDeliveryMethod = false

    private val sureCheckDelegate = object : SureCheckDelegate(context) {
        override fun onSureCheckProcessed() {
            Handler(Looper.getMainLooper()).postDelayed({ debitCardDetailsViewModel.debitCardReplacement(debitCardDetailsViewModel.debitCardReplacementDetailsConfirmation) }, SURE_CHECK_DELAY_MILLIS)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        debitCardDetailsViewModel.cardReplacementAndFetchFeesExtendedResponse.value?.let {
            cardReplacementAndFetchFees = it
        }

        setToolBar(getString(R.string.order_new_card))
        initView()
        setUpObserver()
    }

    fun initView() {
        debitCardDetailsViewModel.debitCardReplacementDetailsConfirmation.apply {
            replacementReasonSecondaryContentAndLabelView.setContentText(reason)
            productTypeSecondaryContentAndLabelView.setContentText(productDescription)
            cardTypeSecondaryContentAndLabelView.setContentText(debitCardType)
            contactDetailsSecondaryContentAndLabelView.setContentText(clientContactNumber)

            if (BRANCH_DELIVERY.equals(cardDeliveryMethod, true)) {
                branchDeliveryMethod = true
                deliveryFeeSecondaryContentAndLabelView.visibility = View.GONE
                preferredBranchSecondaryContentAndLabelView.visibility = View.GONE
                cardDeliveryMethodSecondaryContentAndLabelView.setContentText(getString(R.string.collect_from_branch))
                cardDeliveryMethodBranchSecondaryContentAndLabelView.setContentText(preferredBranch)
                cardDeliveryMethodAddressSecondaryContentAndLabelView.apply {
                    setLabelText(getString(R.string.branch_address))
                    setContentText(branchAddress.toTitleCaseRemovingCommas())
                }
            } else if (FACE_TO_FACE_DELIVERY.equals(cardDeliveryMethod, true)) {
                cardDeliveryMethodBranchSecondaryContentAndLabelView.visibility = View.GONE
                divider3.visibility = View.VISIBLE
                cardDeliveryMethodSecondaryContentAndLabelView.setContentText(getString(R.string.face_to_face_delivery))
                cardDeliveryMethodAddressSecondaryContentAndLabelView.apply {
                    setLabelText(getString(R.string.address))
                    setContentText(clientAddress)
                }
                preferredBranchSecondaryContentAndLabelView.apply {
                    visibility = View.VISIBLE
                    setContentText(branchAddress.toTitleCaseRemovingCommas())
                }
            }

            cardReplacementAndFetchFees.cardDeliveryFee?.let { deliveryFeeAmount ->
                deliveryFeeSecondaryContentAndLabelView.contentTextView.text = TextFormatUtils.formatBasicAmountAsRand(deliveryFeeAmount.getAmount())
            }

            cardReplacementAndFetchFees.replacementFee?.let { replacementFeeAmount ->
                replacementFeeSecondaryContentAndLabelView.contentTextView.text = TextFormatUtils.formatBasicAmountAsRand(replacementFeeAmount.getAmount())
            }

            orderNewCardButton.setOnClickListener {
                if (agreeToTermsCheckBoxView.isChecked) {
                    debitCardDetailsViewModel.debitCardReplacementDetailsConfirmation.apply {
                        cardDeliveryFee = cardReplacementAndFetchFees.cardDeliveryFee?.getAmount()
                        deliveryFeeApplicable = cardReplacementAndFetchFees.cardDeliveryFeeApplicable
                        cardDeliveryFeeType = cardReplacementAndFetchFees.cardDeliveryFeeType

                        replacementFee = cardReplacementAndFetchFees.cardDeliveryFee?.getAmount()
                        replacementFeeApplicable = cardReplacementAndFetchFees.replacementFeeApplicable
                        replacementFeeType = cardReplacementAndFetchFees.replacementFeeType

                        debitCardDetailsViewModel.debitCardReplacement(debitCardDetailsViewModel.debitCardReplacementDetailsConfirmation)
                    }
                } else {
                    agreeToTermsCheckBoxView.setErrorMessage(getString(R.string.please_accept_fees))
                }
            }
        }
    }

    private fun setUpObserver() {
        debitCardDetailsViewModel.debitCardReplacementConfirmationResponse.observe(viewLifecycleOwner, { debitCardReplacementConfirmation ->
            dismissProgressDialog()
            sureCheckDelegate.processSureCheck(baseActivity, debitCardReplacementConfirmation) {
                if (debitCardReplacementConfirmation.status != null && SUCCESS.equals(debitCardReplacementConfirmation.status, true)) {
                    navigateToDebitCardReplacementSuccessfulResultScreen(debitCardReplacementConfirmation)
                } else {
                    genericResultScreen()
                }
            }
        })
    }

    private fun navigateToDebitCardReplacementSuccessfulResultScreen(debitCardReplacementConfirmation: DebitCardReplacementConfirmation) {
        if (SUCCESS.equals(debitCardReplacementConfirmation.status, true)) {
            if (AUTHORISATION_OUTSTANDING_TRANSACTION.equals(debitCardReplacementConfirmation.statusMessage, true)) {
                startActivity(Intent(context, DualAuthDebitCardReplacementPendingActivity::class.java))
            } else {
                val deliveryMessage = if (branchDeliveryMethod) R.string.your_new_order_has_been_ordered_branch_delivery_message else R.string.your_new_order_has_been_ordered_face_to_face_delivery_message
                startActivity(IntentFactory.getSuccessfulCallHelpLineResultScreen(activity, R.string.your_new_card_has_been_ordered, deliveryMessage))
            }
        }
    }

    private fun genericResultScreen() {
        val resultScreenProperties = GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.unable_to_order_new_card))
                .setDescription(getString(R.string.unable_to_order_new_card_message))
                .setPrimaryButtonLabel(getString(R.string.call_helpline))
                .setSecondaryButtonLabel(getString(R.string.home))
                .build(true)

        GenericResultScreenFragment.setPrimaryButtonOnClick { TelephoneUtil.supportCallRegistrationIssues(activity) }
        GenericResultScreenFragment.setSecondaryButtonOnClick { baseActivity.navigateToHomeScreenAndShowAccountsList() }
        navigate(StopAndReplaceDebitCardOverviewFragmentDirections.actionStopAndReplaceDebitCardOverviewFragmentToStopAndReplaceGenericResultScreenFragment(resultScreenProperties))
    }
}