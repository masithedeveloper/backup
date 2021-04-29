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
 */

package com.barclays.absa.banking.avaf.documentRequest.ui

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.barclays.absa.banking.R
import com.barclays.absa.banking.avaf.ui.AvafConstants.OFFER_INFO
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.utils.AnalyticsUtil

class AvafDocumentRequestActivity : BaseActivity(R.layout.avaf_document_request_activity) {
    companion object {
        internal const val DOCUMENT_TYPE = "document_request_type"
        internal const val ACCOUNT_NUMBER = "document_request_account_number"
        internal const val ACCOUNT_DETAIL = "document_request_account_detail"
        internal const val SHOW_DOCUMENT_INFO = "show_document_request_info"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initNavGraph()
    }

    override fun onBackPressed() {
        findNavController(R.id.fragmentContainer).currentDestination?.id.let { destinationId ->
            when (destinationId) {
                R.id.natisCopyFragment -> AnalyticsUtil.trackAction("AVAFCopyOfNaTIS_ElectronicNaTISRequestScreen_BackButtonClicked")
                R.id.natisCopyConfirmationFragment -> AnalyticsUtil.trackAction("AVAFCopyOfNaTIS_ConfirmRequestScreen_BackButtonClicked")
                R.id.crossBorderFragment -> AnalyticsUtil.trackAction("AVAFCrossBorderLetterRequest_CrossBorderLetterRequestScreen_BackButtonClicked")
                R.id.crossBorderConfirmationFragment -> AnalyticsUtil.trackAction("AVAFCrossBorderLetterRequest_ConfirmRequestScreen_BackButtonClicked")
                R.id.paidUpFragment -> AnalyticsUtil.trackAction("AVAFPaidUpLetterRequest_PaidUpLetterRequestScreen_BackButtonClicked")
                R.id.paidUpConfirmationFragment -> AnalyticsUtil.trackAction("AVAFPaidUpLetterRequest_ConfirmRequestScreen_BackButtonClicked")
                R.id.settlementQuoteRequestFragment -> AnalyticsUtil.trackAction("AVAFSettlementQuoteRequest_SettlementQuoteRequestScreen_BackButtonClicked")
                R.id.settlementQuoteConfirmationFragment -> AnalyticsUtil.trackAction("AVAFSettlementQuoteRequest_ConfirmRequestScreen_BackButtonClicked")
                R.id.certificateFragment -> AnalyticsUtil.trackAction("AVAFAuditTaxCertificate_AuditTaxCertificatesScreen_BackButtonClicked")
                R.id.certificateConfirmationFragment -> AnalyticsUtil.trackAction("AVAFAuditTaxCertificate_ConfirmRequestScreen_BackButtonClicked")
                R.id.avafLoanAmortizationFragment -> AnalyticsUtil.trackAction("AmortisationSchedule_AmortisationRequestScreen_BackButtonClicked")
                R.id.avafDetailedStatementRequestFragment -> AnalyticsUtil.trackAction("DetailedStatement_DetailedStatementRequestScreen_BackButtonClicked")
                R.id.avafDetailedStatementConfirmationFragment -> AnalyticsUtil.trackAction("DetailedStatement_ConfirmRequestScreen_BackButtonClicked")
            }
        }

        super.onBackPressed()
    }

    private fun initNavGraph() {
        val showDocumentInfo: Boolean = intent.getBooleanExtra(SHOW_DOCUMENT_INFO, false)
        when (intent.getStringExtra(DOCUMENT_TYPE)) {
            DocumentRequestType.SETTLEMENT_QUOTE.name -> {
                if (!showDocumentInfo) {
                    setNavGraph(navGraphId = R.navigation.avaf_settlement_quote_navigation_graph)
                } else {
                    AvafOfferInfo().apply {
                        type = DocumentRequestType.SETTLEMENT_QUOTE
                        offerTitle = getString(R.string.avaf_offer_settlement_title)
                        offerHeader = getString(R.string.avaf_offer_content_header)
                        offerSubTitle = getString(R.string.avaf_vehicle_finance_documents)
                        offerDescription = getString(R.string.avaf_offer_settlement_content_text)
                        offerClosingText = getString(R.string.avaf_offer_settlement_note)
                        acceptText = getString(R.string.avaf_request_full_quote)
                        acceptFunction = {
                            findNavController(R.id.fragmentContainer).navigate(R.id.action_avafDocumentOfferFragment_to_settlementQuoteRequestFragment)
                        }
                        val bundle = bundleOf(OFFER_INFO to this)
                        setNavGraph(navGraphId = R.navigation.avaf_settlement_quote_navigation_graph, destinationArgs = bundle, startDestinationId = R.id.avafDocumentOfferFragment)
                    }
                }
            }
            DocumentRequestType.TAX_CERTIFICATE.name -> setNavGraph(R.navigation.avaf_tax_certificate_navigation_graph)
            DocumentRequestType.PAID_UP.name -> {
                if (!showDocumentInfo) {
                    setNavGraph(R.navigation.avaf_paidup_letter_navigation_graph)
                } else {
                    AvafOfferInfo().apply {
                        type = DocumentRequestType.PAID_UP
                        offerTitle = getString(R.string.avaf_offer_paidup_title)
                        offerHeader = getString(R.string.avaf_request_letter)
                        offerSubTitle = getString(R.string.avaf_vehicle_finance_documents)
                        offerDescription = getString(R.string.avaf_offer_paidup_content_text)
                        offerClosingText = getString(R.string.avaf_offer_free_of_charge)
                        acceptText = getString(R.string.avaf_request_letter)
                        acceptFunction = {
                            findNavController(R.id.fragmentContainer).navigate(R.id.action_avafDocumentOfferFragment_to_paidUpFragment)
                        }
                        val bundle = bundleOf(OFFER_INFO to this)
                        setNavGraph(navGraphId = R.navigation.avaf_paidup_letter_navigation_graph, destinationArgs = bundle, startDestinationId = R.id.avafDocumentOfferFragment)
                    }
                }
            }
            DocumentRequestType.ELECTRONIC_NATIS.name -> {
                AvafOfferInfo().run {
                    type = DocumentRequestType.ELECTRONIC_NATIS
                    headerTitle = getString(R.string.avaf_document_request_electronic_natis_title)
                    offerTitle = getString(R.string.avaf_get_copy_of_natis_now)
                    offerHeader = getString(R.string.avaf_natis_copy_info_title)
                    offerDescription = getString(R.string.avaf_natis_copy_info_descripton)
                    acceptText = getString(R.string.avaf_request_documents)
                    acceptFunction = { findNavController(R.id.fragmentContainer).navigate(R.id.action_avafDocumentInfoFragment_to_natisCopyFragment) }
                    setNavGraph(R.navigation.avaf_natis_copy_navigation_graph, bundleOf(OFFER_INFO to this))
                }
            }
            DocumentRequestType.CROSS_BORDER.name -> setNavGraph(R.navigation.avaf_cross_border_navigation_graph)
            DocumentRequestType.LOAN_AMORTIZATION.name -> {
                AvafOfferInfo().apply {
                    type = DocumentRequestType.LOAN_AMORTIZATION
                    headerTitle = getString(R.string.avaf_vehicle_amortization_schedule)
                    offerTitle = getString(R.string.avaf_loan_amortization_offer_header)
                    offerHeader = getString(R.string.avaf_loan_amortization_offer_title)
                    offerDescription = getString(R.string.avaf_loan_amortization_offer_description)
                    acceptText = getString(R.string.continue_button)
                    acceptFunction = { findNavController(R.id.fragmentContainer).navigate(R.id.action_avafDocumentOfferFragment_to_avafLoanAmortizationFragment) }
                    val bundle = bundleOf(OFFER_INFO to this)
                    setNavGraph(R.navigation.avaf_loan_amortization_navigation_graph, bundle)
                }
            }
            DocumentRequestType.DETAILED_STATEMENT.name -> {
                with(AvafOfferInfo()) {
                    type = DocumentRequestType.DETAILED_STATEMENT
                    headerTitle = getString(R.string.avaf_detailed_statement)
                    offerTitle = getString(R.string.avaf_detailed_statement_info_title)
                    offerHeader = getString(R.string.avaf_detailed_statement_info_header)
                    offerDescription = getString(R.string.avaf_detailed_statement_info_description)
                    acceptText = getString(R.string.continue_button)
                    acceptFunction = { findNavController(R.id.fragmentContainer).navigate(R.id.action_avafDocumentInfoFragment_to_avafDetailedStatementRequestFragment) }
                    setNavGraph(R.navigation.avaf_detailed_statement_navigation_graph, bundleOf(OFFER_INFO to this))
                }
            }
        }
    }

    private fun setNavGraph(navGraphId: Int, destinationArgs: Bundle? = null, @IdRes startDestinationId: Int = -1) {
        val navController = Navigation.findNavController(this, R.id.fragmentContainer)
        val navGraph = navController.navInflater.inflate(navGraphId)
        if (startDestinationId != -1) {
            navGraph.startDestination = startDestinationId
        }

        navController.setGraph(navGraph, destinationArgs)
    }
}