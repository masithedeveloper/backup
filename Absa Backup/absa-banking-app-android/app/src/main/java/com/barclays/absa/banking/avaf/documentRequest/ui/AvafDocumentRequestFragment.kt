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

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.barclays.absa.banking.R
import com.barclays.absa.banking.accountHub.AccountHubViewModel
import com.barclays.absa.banking.avaf.documentRequest.ui.AvafDocumentRequestActivity.Companion.ACCOUNT_DETAIL
import com.barclays.absa.banking.avaf.documentRequest.ui.AvafDocumentRequestActivity.Companion.ACCOUNT_NUMBER
import com.barclays.absa.banking.avaf.documentRequest.ui.AvafDocumentRequestActivity.Companion.DOCUMENT_TYPE
import com.barclays.absa.banking.avaf.ui.AvafConstants
import com.barclays.absa.banking.avaf.ui.AvafHubViewModel
import com.barclays.absa.banking.boundary.model.AccountObject
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.databinding.AvafDocumentRequestFragmentBinding
import com.barclays.absa.banking.express.avafDocumentPrevalidation.AvafDocumentPrevalidationViewModel
import com.barclays.absa.banking.express.avafDocumentPrevalidation.dto.PrevalidationRule
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity
import com.barclays.absa.banking.presentation.shared.IntentFactory
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateUtils
import com.barclays.absa.utils.extensions.viewBinding
import styleguide.bars.FragmentPagerItem
import styleguide.buttons.OptionActionButtonView
import java.util.*

class AvafDocumentRequestFragment : FragmentPagerItem(R.layout.avaf_document_request_fragment) {
    private val documentPrevalidationViewModel: AvafDocumentPrevalidationViewModel by activityViewModels()
    private val avafHubViewModel: AvafHubViewModel by activityViewModels()
    private val accountHubViewModel by activityViewModels<AccountHubViewModel>()
    private var accountObject: AccountObject? = null
    private val binding by viewBinding(AvafDocumentRequestFragmentBinding::bind)

    companion object {
        @JvmStatic
        fun newInstance(description: String) = AvafDocumentRequestFragment().apply {
            arguments = Bundle().apply {
                putString(TAB_DESCRIPTION_KEY, description)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AnalyticsUtil.trackAction("AbsaVehicleAssetFinance_AbsaVehicleAssetFinanceHubDocumentsTab_ScreenDisplayed")
        accountObject = accountHubViewModel.accountObject
        initViews()
        setupListeners()
    }

    override fun getTabDescription(): String = arguments?.getString(TAB_DESCRIPTION_KEY) ?: ""

    private fun setupListeners() {
        val accountNumber = accountObject?.accountNumber

        documentPrevalidationViewModel.prevalidationResponseLiveData.value?.let { validationResponse ->
            var settlementQuotePrevalidationRule: PrevalidationRule? = null
            var certificatePrevalidationRule: PrevalidationRule? = null
            var paidUpPrevalidationRule: PrevalidationRule? = null
            var electronicNatisPrevalidationRule: PrevalidationRule? = null
            var crossborderPrevalidationRule: PrevalidationRule? = null
            var detailedStatementPrevalidationRule: PrevalidationRule? = null
            var amortizationPrevalidationRule: PrevalidationRule? = null

            validationResponse.documentsObject.prevalidationRules.forEach { prevalidationRule ->
                when (prevalidationRule.name) {
                    DocumentRequestType.SETTLEMENT_QUOTE.documentName -> settlementQuotePrevalidationRule = prevalidationRule
                    DocumentRequestType.TAX_CERTIFICATE.documentName -> certificatePrevalidationRule = prevalidationRule
                    DocumentRequestType.PAID_UP.documentName -> paidUpPrevalidationRule = prevalidationRule
                    DocumentRequestType.ELECTRONIC_NATIS.documentName -> electronicNatisPrevalidationRule = prevalidationRule
                    DocumentRequestType.CROSS_BORDER.documentName -> crossborderPrevalidationRule = prevalidationRule
                    DocumentRequestType.DETAILED_STATEMENT.documentName -> detailedStatementPrevalidationRule = prevalidationRule
                    DocumentRequestType.LOAN_AMORTIZATION.documentName -> amortizationPrevalidationRule = prevalidationRule
                }
            }

            binding.settlementQuoteOptionActionButtonView.setOnClickListener {
                if (FeatureSwitchingCache.featureSwitchingToggles.vehicleFinanceSettlementQuote == FeatureSwitchingStates.ACTIVE.key) {
                    AnalyticsUtil.trackAction("AbsaVehicleAssetFinance_AbsaVehicleAssetFinanceHubDocumentsTab_SettlementQuoteButtonClicked")
                    settlementQuotePrevalidationRule?.let { prevalidationRule ->
                        if (prevalidationRule.isIndicatorYes()) {
                            Intent(activity, AvafDocumentRequestActivity::class.java).apply {
                                putExtra(DOCUMENT_TYPE, DocumentRequestType.SETTLEMENT_QUOTE.name)
                                putExtra(ACCOUNT_NUMBER, accountNumber)
                                startActivityForResult(this, AvafConstants.REQUEST_CODE_DOCUMENTS)
                            }
                        } else {
                            showDocumentUnavailable(prevalidationRule.message, "AVAFSettlementQuoteRequest_UnableToRequestDocumentScreen_ScreenDisplayed", "AVAFSettlementQuoteRequest_UnableToRequestDocumentScreen_CloseButtonClicked")
                        }
                    }
                } else {
                    showFeatureUnavailable(getString(R.string.avaf_document_request_settlement_quote_title))
                }
            }

            binding.taxCertificateOptionActionButtonView.setOnClickListener {
                if (FeatureSwitchingCache.featureSwitchingToggles.vehicleFinanceTaxCertificate == FeatureSwitchingStates.ACTIVE.key) {
                    AnalyticsUtil.trackAction("AbsaVehicleAssetFinance_AbsaVehicleAssetFinanceHubDocumentsTab_AuditTaxCertificatesButtonClicked")
                    certificatePrevalidationRule?.let { prevalidationRule ->
                        if (prevalidationRule.isIndicatorYes() && FeatureSwitchingCache.featureSwitchingToggles.vehicleFinanceAccountInformation == FeatureSwitchingStates.ACTIVE.key) {
                            avafHubViewModel.avafAccountDetailLiveData.value?.absaVehicleAndAssetFinanceDetail?.let { accountDetail ->
                                val contractEndDate = DateUtils.getDate(accountDetail.contractEndDate, DateUtils.DASHED_DATE_PATTERN)
                                val earliestAllowedDate = Calendar.getInstance().apply { add(Calendar.YEAR, DocumentRequestConstants.CERTIFICATE_EARLIEST_YEAR_BACKTRACK) }.time
                                val contractEndBufferDate = Calendar.getInstance().apply {
                                    time = contractEndDate
                                    add(Calendar.MONTH, DocumentRequestConstants.CERTIFICATE_CONTRACT_END_BUFFER_IN_MONTHS)
                                }.time

                                if (contractEndBufferDate.before(earliestAllowedDate)) {
                                    showDocumentUnavailable(prevalidationRule.message, "AVAFAuditTaxCertificate_UnableToRequestDocumentScreen_ScreenDisplayed", "AVAFAuditTaxCertificate_UnableToRequestDocumentScreen_CloseButtonClicked")
                                } else {
                                    Intent(activity, AvafDocumentRequestActivity::class.java).apply {
                                        putExtra(DOCUMENT_TYPE, DocumentRequestType.TAX_CERTIFICATE.name)
                                        putExtra(ACCOUNT_NUMBER, accountNumber)
                                        putExtra(ACCOUNT_DETAIL, accountDetail)
                                        startActivityForResult(this, AvafConstants.REQUEST_CODE_DOCUMENTS)
                                    }
                                }
                            }
                        } else {
                            showDocumentUnavailable(prevalidationRule.message, "AVAFAuditTaxCertificate_UnableToRequestDocumentScreen_ScreenDisplayed", "AVAFAuditTaxCertificate_UnableToRequestDocumentScreen_CloseButtonClicked")
                        }
                    }
                } else {
                    showFeatureUnavailable(getString(R.string.avaf_document_request_tax_certificate_title))
                }
            }

            binding.paidupLetterOptionActionButtonView.setOnClickListener {
                if (FeatureSwitchingCache.featureSwitchingToggles.vehicleFinancePaidUpLetter == FeatureSwitchingStates.ACTIVE.key) {
                    AnalyticsUtil.trackAction("AbsaVehicleAssetFinance_AbsaVehicleAssetFinanceHubDocumentsTab_PaidUpLetterButtonClicked")
                    paidUpPrevalidationRule?.let { prevalidationRule ->
                        if (prevalidationRule.isIndicatorYes()) {
                            Intent(activity, AvafDocumentRequestActivity::class.java).apply {
                                putExtra(ACCOUNT_NUMBER, accountNumber)
                                putExtra(DOCUMENT_TYPE, DocumentRequestType.PAID_UP.name)
                                startActivityForResult(this, AvafConstants.REQUEST_CODE_DOCUMENTS)
                            }
                        } else {
                            showDocumentUnavailable(prevalidationRule.message, "AVAFPaidUpLetterRequest_UnableToRequestDocumentScreen_ScreenDisplayed", "AVAFPaidUpLetterRequest_UnableToRequestDocumentScreen_CloseButtonClicked")
                        }
                    }
                } else {
                    showFeatureUnavailable(getString(R.string.avaf_document_request_paidup_title))
                }
            }

            binding.electronicNatisOptionActionButtonView.setOnClickListener {
                if (FeatureSwitchingCache.featureSwitchingToggles.vehicleFinanceElectronicNatis == FeatureSwitchingStates.ACTIVE.key) {
                    AnalyticsUtil.trackAction("AbsaVehicleAssetFinance_AbsaVehicleAssetFinanceHubDocumentsTab_ElectronicCopyOfNaTISButtonClicked")
                    electronicNatisPrevalidationRule?.let { prevalidationRule ->
                        if (prevalidationRule.isIndicatorYes()) {
                            Intent(activity, AvafDocumentRequestActivity::class.java).apply {
                                putExtra(ACCOUNT_NUMBER, accountNumber)
                                putExtra(DOCUMENT_TYPE, DocumentRequestType.ELECTRONIC_NATIS.name)
                                startActivityForResult(this, AvafConstants.REQUEST_CODE_DOCUMENTS)
                            }
                        } else {
                            showDocumentUnavailable(prevalidationRule.message, "AVAFCopyOfNaTIS_UnableToRequestDocumentScreen_ScreenDisplayed", "AVAFCopyOfNaTIS_UnableToRequestDocumentScreen_CloseButtonClicked")
                        }
                    }
                } else {
                    showFeatureUnavailable(getString(R.string.avaf_document_request_electronic_natis_title))
                }
            }

            binding.crossborderOptionActionButtonView.setOnClickListener {
                if (FeatureSwitchingCache.featureSwitchingToggles.vehicleFinanceCrossBorderLetter == FeatureSwitchingStates.ACTIVE.key) {
                    AnalyticsUtil.trackAction("AbsaVehicleAssetFinance_AbsaVehicleAssetFinanceHubDocumentsTab_CrossBorderLetterButtonClicked")
                    crossborderPrevalidationRule?.let { prevalidationRule ->
                        if (prevalidationRule.isIndicatorYes()) {
                            Intent(activity, AvafDocumentRequestActivity::class.java).apply {
                                putExtra(ACCOUNT_NUMBER, accountNumber)
                                putExtra(DOCUMENT_TYPE, DocumentRequestType.CROSS_BORDER.name)
                                startActivityForResult(this, AvafConstants.REQUEST_CODE_DOCUMENTS)
                            }
                        } else {
                            showDocumentUnavailable(prevalidationRule.message, "AVAFCrossBorderLetterRequest_UnableToRequestDocumentScreen_ScreenDisplayed", "AVAFCrossBorderLetterRequest_UnableToRequestDocumentScreen_CloseButtonClicked")
                        }
                    }
                } else {
                    showFeatureUnavailable(getString(R.string.avaf_document_request_cross_border_letter))
                }
            }

            binding.amortizationOptionActionButtonView.setOnClickListener {
                if (FeatureSwitchingCache.featureSwitchingToggles.vehicleFinanceLoanAmortization == FeatureSwitchingStates.ACTIVE.key) {
                    AnalyticsUtil.trackAction("AmortisationSchedule_DocumentRequestScreen_AmortisationRequestButtonClicked")
                    amortizationPrevalidationRule?.let { prevalidationRule ->
                        if (prevalidationRule.isIndicatorYes()) {
                            Intent(activity, AvafDocumentRequestActivity::class.java).apply {
                                putExtra(ACCOUNT_NUMBER, accountNumber)
                                putExtra(DOCUMENT_TYPE, DocumentRequestType.LOAN_AMORTIZATION.name)
                                startActivityForResult(this, AvafConstants.REQUEST_CODE_DOCUMENTS)
                            }
                        } else {
                            showDocumentUnavailable(prevalidationRule.message, "", "")
                        }
                    }
                } else {
                    showFeatureUnavailable(getString(R.string.avaf_loan_amortization_schedule))
                }
            }

            binding.detailedStatementOptionActionButtonView.setOnClickListener {
                if (FeatureSwitchingCache.featureSwitchingToggles.vehicleFinanceDetailedStatement == FeatureSwitchingStates.ACTIVE.key) {
                    detailedStatementPrevalidationRule?.let { prevalidationRule ->
                        if (prevalidationRule.isIndicatorYes()) {
                            Intent(activity, AvafDocumentRequestActivity::class.java).apply {
                                putExtra(ACCOUNT_NUMBER, accountNumber)
                                putExtra(DOCUMENT_TYPE, DocumentRequestType.DETAILED_STATEMENT.name)
                                startActivityForResult(this, AvafConstants.REQUEST_CODE_DOCUMENTS)
                            }
                        } else {
                            showDocumentUnavailable(prevalidationRule.message, "", "DetailedStatement_UnableToRequestDocumentScreen_CloseButtonClicked")
                        }
                    }
                } else {
                    showFeatureUnavailable(getString(R.string.avaf_detailed_statement))
                }
            }
        }
    }

    private fun initViews() {
        val balance = accountObject?.currentBalance ?: Amount()

        if (balance.amountDouble < 0) {
            binding.settlementQuoteOptionActionButtonView.visibility = View.VISIBLE
            binding.paidupLetterOptionActionButtonView.visibility = View.GONE
        } else {
            binding.settlementQuoteOptionActionButtonView.visibility = View.INVISIBLE
            binding.paidupLetterOptionActionButtonView.visibility = View.VISIBLE
        }

        with(FeatureSwitchingCache.featureSwitchingToggles) {
            setActionButtonVisibilityState(vehicleFinanceSettlementQuote, binding.settlementQuoteOptionActionButtonView)
            setActionButtonVisibilityState(vehicleFinanceTaxCertificate, binding.taxCertificateOptionActionButtonView)
            setActionButtonVisibilityState(vehicleFinancePaidUpLetter, binding.paidupLetterOptionActionButtonView)
            setActionButtonVisibilityState(vehicleFinanceElectronicNatis, binding.electronicNatisOptionActionButtonView)
            setActionButtonVisibilityState(vehicleFinanceCrossBorderLetter, binding.crossborderOptionActionButtonView)
            setActionButtonVisibilityState(vehicleFinanceLoanAmortization, binding.amortizationOptionActionButtonView)
            setActionButtonVisibilityState(vehicleFinanceDetailedStatement, binding.detailedStatementOptionActionButtonView)
        }
    }

    private fun showFeatureUnavailable(featureName: String) {
        startActivity(IntentFactory.featureUnavailable(activity, R.string.feature_unavailable, getString(R.string.feature_unavailable_message, featureName)))
    }

    private fun setActionButtonVisibilityState(featureToggle: Int, actionButtonView: OptionActionButtonView) {
        if (featureToggle == FeatureSwitchingStates.GONE.key) {
            actionButtonView.visibility = View.GONE
        }
    }

    private fun showDocumentUnavailable(documentMessage: String, analyticsTagScreen: String, analyticsTagButton: String) {
        if (analyticsTagScreen.isNotBlank()) {
            AnalyticsUtil.trackAction(analyticsTagScreen)
        }

        IntentFactory.IntentBuilder()
                .setClass(activity, GenericResultActivity::class.java)
                .setGenericResultIconToAlert()
                .setGenericResultHeaderMessage(R.string.avaf_document_request_unable_to_request)
                .setGenericResultSubMessage(documentMessage)
                .setGenericResultBottomButton(R.string.close) {
                    BMBApplication.getInstance().topMostActivity.finish()
                    if (analyticsTagButton.isNotBlank()) {
                        AnalyticsUtil.trackAction(analyticsTagButton)
                    }
                }
                .build().run { startActivity(this) }
    }
}