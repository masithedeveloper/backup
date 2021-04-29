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

package com.barclays.absa.banking.debiCheck.ui

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.transition.TransitionManager
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.debiCheck.services.dto.DebiCheckMandateDetail
import com.barclays.absa.banking.debiCheck.ui.DebiCheckViewModel.Flow.*
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.utils.AbsaCacheManager
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.TextFormatUtils
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.debi_check_order_details_activity.*
import styleguide.content.BaseContentAndLabelView
import styleguide.content.BeneficiaryListItem
import styleguide.utils.extensions.toFormattedAccountNumber
import styleguide.utils.extensions.toTitleCase

class DebiCheckMandateDetailsFragment : BaseFragment(R.layout.debi_check_order_details_activity) {

    private lateinit var viewModel: DebiCheckViewModel
    private lateinit var sureCheckDelegate: SureCheckDelegate
    private lateinit var hostActivity: DebiCheckHostActivity

    private var initialConstraint = false
    private val constraintSetInitial = ConstraintSet()
    private val constraintSetExtended = ConstraintSet()
    private var debitOrder = DebiCheckMandateDetail()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = context as DebiCheckHostActivity
        viewModel = hostActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        debitOrder = viewModel.selectedDebitOrder
        when (viewModel.currentFlow) {
            APPROVED -> {
                setToolBar(R.string.debicheck_approved_title)
            }
            SUSPENDED -> {
                setToolBar(R.string.debicheck_suspended_title)
            }
            PENDING -> {
                if (debitOrder.mandateType == "AMENDMENTS") {
                    setToolBar(R.string.debicheck_amended_mandate_toolbartitle)
                } else {
                    setToolBar(R.string.debicheck_mandate)
                }
            }
            else -> setToolBar(R.string.debicheck_mandate)
        }

        debitOrder = viewModel.selectedDebitOrder
        initialiseView()
        setupConstraintSet()
        setupSureCheckDelegate()
        attachDataObserver()
    }

    private fun initialiseView() {
        hideFieldsIfEmpty()
        toggleButtonVisibility()
        bindDataToViews()
        if (debitOrder.amendmentChangedItems.isNotEmpty()) {
            initAmendedFlags()
            amendedDetailsNotice.visibility = View.VISIBLE
        }
        attachEventHandlers()
    }

    private fun hideFieldsIfEmpty() {
        val trackingIndicator = if (viewModel.currentFlow == PENDING) debitOrder.trackingInd else debitOrder.trackingIndicator

        if (debitOrder.installmentAmount.isBlank() || debitOrder.debitValueType.contains("USAGE")) {
            installmentAmountContentView.visibility = View.GONE
        }

        if (debitOrder.initialAmount.isBlank()) {
            initialAmountContentView.visibility = View.GONE
        }

        if (debitOrder.adjustmentAmount.isBlank()) {
            adjustmentAmountContentView.visibility = View.GONE
        }

        if (debitOrder.adjustmentRate.isBlank()) {
            adjustmentRateContentView.visibility = View.GONE
        }

        if (debitOrder.adjustmentCategory.isBlank()) {
            adjustmentCategoryContentView.visibility = View.GONE
        }

        if (debitOrder.firstCollectionDate.isBlank()) {
            firstCollectionDayContentView.visibility = View.GONE
        }

        if (debitOrder.collectionDay.isBlank()) {
            collectionDayContentView.visibility = View.GONE
        }

        if (debitOrder.debitValueType.isBlank() && debitOrder.debitType.isBlank()) {
            typeOfDebitValueContentView.visibility = View.GONE
        }
        if (trackingIndicator.isBlank()) {
            trackingIndicatorContentView.visibility = View.GONE
        }

        if (debitOrder.dateAdjustmentRuleInd.isBlank()) {
            dateAdjustmentRuleIndicatorContentView.visibility = View.GONE
        }

        if (debitOrder.frequency.isBlank()) {
            frequencyContentView.visibility = View.GONE
        }

        if (viewModel.currentFlow == APPROVED || viewModel.currentFlow == SUSPENDED || viewModel.currentFlow == TRANSACTIONS) {
            pleaseNoteTextView.visibility = View.GONE
        }
    }

    private fun toggleButtonVisibility() {
        when (viewModel.currentFlow) {
            SUSPENDED, TRANSACTIONS -> {
                acceptButton.visibility = View.GONE
                rejectButton.visibility = View.GONE
            }
            APPROVED -> {
                rejectButton.visibility = View.GONE
            }
            else -> {
                acceptButton.visibility = View.VISIBLE
                rejectButton.visibility = View.VISIBLE
            }
        }
    }

    private fun bindDataToViews() {
        val creditorShortName = if (viewModel.currentFlow == PENDING) debitOrder.creditorShortName else debitOrder.creditorAbbreviatedShortName
        val maxCollection = if (viewModel.currentFlow == PENDING) debitOrder.maxCollectAmount else debitOrder.maxCollectionAmount
        val contractReference = if (viewModel.currentFlow == PENDING) debitOrder.contractRefNo else debitOrder.contractReference
        val trackingIndicator = if (viewModel.currentFlow == PENDING) debitOrder.trackingInd else debitOrder.trackingIndicator
        val accountNumber = if (viewModel.currentFlow == PENDING) debitOrder.debtorAccNo else debitOrder.debtorAccountNumber
        val accountDescription = getAccountDescription(accountNumber)
        val formattedAccountNumber = accountNumber.toFormattedAccountNumber()
        val formattedAccountDescription = if (accountDescription.isEmpty()) formattedAccountNumber else "$accountDescription ($formattedAccountNumber)"
        val collectionDay = if (debitOrder.collectionDay.equals(getString(R.string.debicheck_collection_last_day), true)) getString(R.string.debicheck_last_day_of_the_month) else DateUtils.formatCollectionDay(debitOrder.collectionDay.toInt())

        debitOrderDetailView.setBeneficiary(BeneficiaryListItem(debitOrder.creditorName.ifEmpty { debitOrder.creditorShortName }, getString(R.string.debicheck_details_creditor_name), ""))
        creditorNameContentView.setContentText(creditorShortName)
        contractReferenceContentView.setContentText(contractReference)
        accountNumberContentView.setContentText(formattedAccountDescription)
        installmentAmountContentView.setContentText("${TextFormatUtils.formatBasicAmountAsRand(debitOrder.installmentAmount)} ${debitOrder.frequency.toLowerCase(BMBApplication.getApplicationLocale())}")
        initialAmountContentView.setContentText("${TextFormatUtils.formatBasicAmountAsRand(debitOrder.initialAmount)} once off")
        maxAmountContentView.setContentText("${TextFormatUtils.formatBasicAmountAsRand(maxCollection.toLowerCase(BMBApplication.getApplicationLocale()))} ${debitOrder.frequency.toLowerCase(BMBApplication.getApplicationLocale())}")
        adjustmentAmountContentView.setContentText(TextFormatUtils.formatBasicAmountAsRand(debitOrder.adjustmentAmount.toLowerCase(BMBApplication.getApplicationLocale())))
        adjustmentRateContentView.setContentText("${debitOrder.adjustmentRate}%")
        adjustmentCategoryContentView.setContentText(getAdjustmentStringValue(debitOrder.adjustmentCategory))
        firstCollectionDayContentView.setContentText(DateUtils.formatDateMonth(debitOrder.firstCollectionDate))
        collectionDayContentView.setContentText(collectionDay)
        when (viewModel.currentFlow) {
            APPROVED, SUSPENDED -> {
                typeOfDebitValueContentView.setContentText(debitOrder.debitType.toTitleCase())
                if (maxCollection.isBlank() || debitOrder.debitType.contains("VARIABLE") || debitOrder.debitType.contains("FIXED")) {
                    maxAmountContentView.visibility = View.GONE
                }
            }
            else -> {
                val debitValue = debitOrder.debitValueType.ifEmpty { debitOrder.debitType }.toTitleCase()
                typeOfDebitValueContentView.setContentText(debitValue)
                if (maxCollection.isBlank() || debitValue.contains("VARIABLE") || debitValue.contains("FIXED")) {
                    maxAmountContentView.visibility = View.GONE
                }
            }
        }
        trackingIndicatorContentView.setContentText(getBoolHelper(trackingIndicator))
        dateAdjustmentRuleIndicatorContentView.setContentText(getBoolHelper(debitOrder.dateAdjustmentRuleInd))
        frequencyContentView.setContentText(debitOrder.frequency)
    }

    private fun initAmendedFlags() {
        debitOrder.amendmentChangedItems.forEach {
            val contextView = when (it.changedFieldName.toUpperCase(BMBApplication.getApplicationLocale())) {
                "FIRST COLLECTION DATE" -> firstCollectionDayContentView
                "MAXIMUM INSTALLMENT AMOUNT" -> maxAmountContentView
                "COLLECTION DAY" -> collectionDayContentView
                "CREDITOR SHORT NAME" -> creditorNameContentView
                "DEBTOR ACCOUNT NUMBER" -> accountNumberContentView
                "FIRST COLLECTION AMOUNT" -> initialAmountContentView
                "INSTALLMENT AMOUNT" -> installmentAmountContentView
                "ADJUSTMENT CATEGORY" -> adjustmentCategoryContentView
                "ADJUSTMENT RATE" -> adjustmentRateContentView
                "TRACKING INDICATOR" -> trackingIndicatorContentView
                "ADJUSTMENT AMOUNT" -> adjustmentAmountContentView
                else -> null
            }

            if (contextView != null) {
                applySpan(contextView)
            }
        }
    }

    private fun applySpan(contextView: BaseContentAndLabelView) {
        val pinkColorSpan = ForegroundColorSpan(ContextCompat.getColor(baseActivity, R.color.pink))
        val boldSpan = StyleSpan(Typeface.BOLD)
        val spannableBuilder = SpannableStringBuilder("${contextView.contentTextView.text} *")
        spannableBuilder.setSpan(pinkColorSpan, spannableBuilder.length - 1, spannableBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableBuilder.setSpan(boldSpan, spannableBuilder.length - 1, spannableBuilder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        contextView.contentTextView.text = spannableBuilder
    }

    private fun attachEventHandlers() {
        seeMoreTextView.setOnClickListener {
            swapLayoutConstraints()
        }
        arrowImageView.setOnClickListener {
            swapLayoutConstraints()
        }

        if (viewModel.currentFlow == APPROVED) {
            acceptButton.text = getString(R.string.debicheck_suspend_title)
            acceptButton.setOnClickListener {
                view?.findNavController()?.navigate(R.id.action_DebiCheckMandateDetailsFragment_to_DebiCheckSuspendInfoFragment)
                hostActivity.tagDebiCheckEvent("MandateDetails_SuspendButtonClicked")
            }
        } else {
            acceptButton.setOnClickListener {
                viewModel.approveDebitOrder(true)
                hostActivity.tagDebiCheckEvent("MandateDetails_AcceptButtonClicked")
            }

            rejectButton.setOnClickListener {
                view?.findNavController()?.navigate(R.id.action_debiCheckDetailsFragment_to_debiCheckReasonForRejectFragment)
                hostActivity.tagDebiCheckEvent("MandateDetails_RejectButtonClicked")
            }
        }
    }

    private fun setupConstraintSet() {
        constraintSetInitial.clone(constraintLayout)
        constraintSetExtended.clone(activity, R.layout.debi_check_order_details_extended_activity)
    }

    private fun setupSureCheckDelegate() {
        sureCheckDelegate = object : SureCheckDelegate(context) {
            override fun onSureCheckProcessed() {
                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel.approveDebitOrder(false)
                }, DebiCheckContracts.DEFAULT_DELAY.toLong())
            }

            override fun onSureCheckRejected() = hostActivity.showSureCheckRejectedScreen()
        }
    }

    private fun attachDataObserver() {
        viewModel.debitOrderAcceptResponse = MutableLiveData()
        viewModel.debitOrderAcceptResponse.observe(viewLifecycleOwner, {
            it?.let { successResponse ->
                dismissProgressDialog()
                sureCheckDelegate.processSureCheck(baseActivity, successResponse) { hostActivity.showApproveResultScreen() }
            }
        })
    }

    private fun swapLayoutConstraints() {
        seeMoreTextView.text = if (initialConstraint) getString(R.string.debicheck_see_more) else getString(R.string.debicheck_see_less)
        TransitionManager.beginDelayedTransition(constraintLayout)
        val constraint = if (initialConstraint) constraintSetInitial else constraintSetExtended
        constraint.applyTo(constraintLayout)
        initialiseView()

        if (seeMoreTextView.text == getString(R.string.debicheck_see_more)) {
            hostActivity.tagDebiCheckEvent("MandateDetails_ShowMoreClicked")
        }

        if (!initialConstraint) {
            ObjectAnimator.ofInt(scrollView, "scrollY", seeMoreTextView.y.toInt()).apply {
                duration = 500L
                startDelay = 250L
                start()
            }
        }
        initialConstraint = !initialConstraint
    }

    private fun getAccountDescription(accountNumber: String): String {
        var accountDescription = ""
        val account = AbsaCacheManager.getTransactionalAccounts().find { it.accountNumber == accountNumber }
        account?.let { accountDescription = it.description }
        return accountDescription
    }

    private fun getBoolHelper(inputBool: String): String =
            if ("Y".equals(inputBool, true) || "t".equals(inputBool, true)) getString(R.string.yes) else getString(R.string.no)

    private fun getAdjustmentStringValue(item: String): String {
        return when (item) {
            "N" -> getString(R.string.debicheck_adjustment_never)
            "Q" -> getString(R.string.debicheck_adjustment_quarterly)
            "A" -> getString(R.string.debicheck_adjustment_annually)
            "B" -> getString(R.string.debicheck_adjustment_bi_annually)
            "R" -> getString(R.string.debicheck_adjustment_repo)
            else -> ""
        }
    }
}