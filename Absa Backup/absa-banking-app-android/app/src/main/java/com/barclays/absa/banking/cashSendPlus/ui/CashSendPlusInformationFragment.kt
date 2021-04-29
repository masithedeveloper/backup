/*
 *  Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.cashSendPlus.ui

import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewTreeObserver
import com.barclays.absa.banking.R
import com.barclays.absa.banking.cashSendPlus.services.CheckCashSendPlusRegistrationStatusResponse
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.utils.CommonUtils
import com.barclays.absa.utils.PdfUtil
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.cash_send_plus_information_fragment.*
import styleguide.utils.extensions.toTitleCase
import kotlin.math.pow

class CashSendPlusInformationFragment : CashSendPlusBaseFragment(R.layout.cash_send_plus_information_fragment) {
    private var scrollRange: Float = 0.0f
    private val cashSendPlusRegStatusData: CheckCashSendPlusRegistrationStatusResponse? = appCacheService.getCashSendPlusRegistrationStatus()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        setUpComponentListeners()
        setUpTermsView()
        setupFeesView()

        val isClientAgreementAlreadyAccepted = "Y".equals(cashSendPlusRegStatusData?.clientAgreementAccepted, true)

        if (isClientAgreementAlreadyAccepted) {
            businessClientAgreementCheckBoxView.visibility = View.GONE
            acceptedBusinessClientAgreementTextView.visibility = View.VISIBLE
            businessClientAgreementCheckBoxView.isChecked = true
        }

        registerNowButton.setOnClickListener {
            if (cashSendPlusTermsAndConditionCheckBoxView.isChecked && businessClientAgreementCheckBoxView.isChecked) {
                if (isClientAgreementAlreadyAccepted) {
                    navigateToCashSendPlusRegistration()
                } else {
                    cashSendPlusViewModel.updateClientAgreementDetails()
                }
            } else {
                cashSendPlusTermsAndConditionCheckBoxView.setErrorMessage(getString(R.string.plz_accept_conditions))
                businessClientAgreementCheckBoxView.setErrorMessage(getString(R.string.i_accept_business_client_agreement_error_message))
            }
        }

        cashSendPlusViewModel.updateCustomerAgreementDetailsResponse.observe(viewLifecycleOwner, {
            if (BMBConstants.SUCCESS.equals(it.transactionStatus, true)) {
                cashSendPlusRegStatusData?.clientAgreementAccepted = "Y"
                cashSendPlusRegStatusData?.let { appCacheService.setCashSendPlusRegistrationStatus(cashSendPlusRegStatusData) }
            }
            dismissProgressDialog()
            navigateToCashSendPlusRegistration()
        })

        cashSendPlusTermsAndConditionCheckBoxView.setOnCheckedListener { cashSendPlusTermsAndConditionCheckBoxView.clearError() }
        businessClientAgreementCheckBoxView.setOnCheckedListener { businessClientAgreementCheckBoxView.clearError() }
    }

    private fun navigateToCashSendPlusRegistration() {
        navigate(CashSendPlusInformationFragmentDirections.actionCashSendPlusInformationFragmentToCashSendPlusRegistrationFragment())
    }

    private fun setupFeesView() {
        CommonUtils.makeTextClickable(baseActivity, R.string.cash_send_plus_transaction_fee, getString(R.string.fees), object : ClickableSpan() {
            override fun onClick(widget: View) {
                PdfUtil.showPDFInApp(baseActivity, cashSendPlusRegStatusData?.cashSendPlusViewFeesPdf)
            }
        }, cashSendPlusTransactionFeeTertiaryContentAndLabelView.contentTextView, android.R.color.black)
    }

    private fun setUpTermsView() {
        CommonUtils.makeTextClickable(baseActivity, R.string.i_have_read_and_understood_the_cash_send_plus_terms, getString(R.string.cash_send_plus_terms_of_use_description), object : ClickableSpan() {
            override fun onClick(widget: View) {
                PdfUtil.showPDFInApp(baseActivity, cashSendPlusRegStatusData?.cashSendPlusTermsOfUse)
            }
        }, cashSendPlusTermsAndConditionCheckBoxView.checkBoxTextView, android.R.color.black)

        CommonUtils.makeTextClickable(baseActivity, R.string.i_accept_business_client_agreement, getString(R.string.cash_send_plus_business_client_agreement_descrption), object : ClickableSpan() {
            override fun onClick(widget: View) {
                PdfUtil.showPDFInApp(baseActivity, cashSendPlusRegStatusData?.cashSendPlusBusinessClientAgreement)
            }
        }, businessClientAgreementCheckBoxView.checkBoxTextView, android.R.color.black)

        CommonUtils.makeTextClickable(baseActivity, R.string.i_have_previously_accepted_the_business_client_agreement, getString(R.string.cash_send_plus_business_client_agreement_descrption), object : ClickableSpan() {
            override fun onClick(widget: View) {
                PdfUtil.showPDFInApp(baseActivity, cashSendPlusRegStatusData?.cashSendPlusBusinessClientAgreement)
            }
        }, acceptedBusinessClientAgreementTextView, android.R.color.black)
    }

    private fun setUpToolbar() {
        hideToolBar()
        fragmentToolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_white)
            visibility = View.VISIBLE
            title = getString(R.string.cash_send_plus_title).toTitleCase()
            setNavigationOnClickListener {
                cashSendPlusActivity.onBackPressed()
            }
        }
    }

    private fun setUpComponentListeners() {
        cashSendPlusAppbarLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                scrollRange = cashSendPlusAppbarLayout.totalScrollRange.toFloat()
                cashSendPlusAppbarLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        cashSendPlusAppbarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            if (verticalOffset == 0) {
                animateTitleViews(1f)
            } else {
                val invertedVerticalOffset = scrollRange + verticalOffset
                val collapsePercent = (invertedVerticalOffset / scrollRange).toDouble().pow(1.0).toFloat()
                animateTitleViews(collapsePercent)
            }
        })
    }

    private fun animateTitleViews(alphaPercentage: Float) {
        cashSendPlusTitleTextView?.apply {
            alpha = alphaPercentage.toDouble().pow(3.0).toFloat()
            scaleX = alphaPercentage
            scaleY = alphaPercentage
        }
    }
}