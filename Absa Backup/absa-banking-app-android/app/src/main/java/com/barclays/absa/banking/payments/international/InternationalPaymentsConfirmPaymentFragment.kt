/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.payments.international

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.style.ClickableSpan
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.INTERNATIONAL_PAYMENTS
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.QUOTATION_DETAILS
import com.barclays.absa.banking.payments.international.data.TransferQuoteDetails
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.DateUtils
import kotlinx.android.synthetic.main.international_payments_confirm_payment_fragment.*
import java.util.*

class InternationalPaymentsConfirmPaymentFragment : InternationalPaymentsAbstractBaseFragment(R.layout.international_payments_confirm_payment_fragment), InternationalPaymentsContract.InternationalPaymentsConfirmPaymentView {

    private lateinit var presenter: InternationalPaymentsContract.InternationalPaymentsConfirmPaymentPresenter
    private var transferQuoteDetails: TransferQuoteDetails? = TransferQuoteDetails()
    private lateinit var sureCheckDelegate: SureCheckDelegate
    private var referenceNumber: String? = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sureCheckDelegate = object : SureCheckDelegate(internationalPaymentsActivity) {
            override fun onSureCheckProcessed() {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (internationalPaymentsActivity.isExistingBeneficiary) {
                        presenter.performBeneficiaryPayment()
                    } else {
                        presenter.processOnceOffPayment()
                    }
                }, 250)
            }

            override fun onSureCheckRejected() {
                super.onSureCheckRejected()
                AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "International Payments Payment Unsuccessful Screen")
            }

            override fun onSureCheckCancelled() {
                dismissProgressDialog()
                hideToolBar()
                internationalPaymentsActivity.hideProgressIndicator()
                Handler(Looper.getMainLooper()).postDelayed({
                    val genericResultScreenProperties = InternationalPaymentsResultFactory().buildInternationalPaymentSureCheckFailureBundle(internationalPaymentsActivity)
                    navigate(InternationalPaymentsConfirmPaymentFragmentDirections.actionInternationalPaymentsConfirmPaymentFragmentToInternationalPaymentsResultsFragment(genericResultScreenProperties, true))
                }, 250)
            }

            override fun onSureCheckFailed() {
                dismissProgressDialog()
                hideToolBar()
                internationalPaymentsActivity.hideProgressIndicator()
                Handler(Looper.getMainLooper()).postDelayed({
                    val genericResultScreenProperties = InternationalPaymentsResultFactory().buildInternationalPaymentSureCheckFailureBundle(internationalPaymentsActivity)
                    navigate(InternationalPaymentsConfirmPaymentFragmentDirections.actionInternationalPaymentsConfirmPaymentFragmentToInternationalPaymentsResultsFragment(genericResultScreenProperties, true))
                }, 250)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_ConfirmPaymentsScreen_ScreenDisplayed")
        presenter = InternationalPaymentsConfirmPaymentPresenter(this, sureCheckDelegate, internationalPaymentsActivity.fetchBeneficiaryBeneficiaryDetailsDataModel())
        setToolBarNoBack(R.string.confirm_payment)
        setHasOptionsMenu(true)
        internationalPaymentsActivity.setProgressStep(5)
        internationalPaymentsActivity.showProgressIndicator()
        beneficiaryPrimaryContentAndLabelView.setLabelText(internationalPaymentsActivity.flowTypeString)
        beneficiaryResidentialAddressSecondaryContentAndLabelView.setLabelText(getString(R.string.beneficiary_residential_address_title, internationalPaymentsActivity.flowTypeString))
        beneficiaryReceivesSecondaryContentAndLabelView.setLabelText(getString(R.string.international_payment_details_beneficiary_receives, internationalPaymentsActivity.flowTypeString))

        declarationCheckBoxView.checkBoxTextView.setOnClickListener(null)
        declarationCheckBoxView.setClickableLinkTitle(R.string.international_payments_i_have_read_declaration, R.string.international_payments_declaration_hyperlink, object : ClickableSpan() {
            override fun onClick(p0: View) {
                AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_DeclarationScreen_ScreenDisplayed")
                navigate(InternationalPaymentsConfirmPaymentFragmentDirections.actionInternationalPaymentsConfirmPaymentFragmentToInternationalPaymentsDeclarationNewFragment())
            }
        }, R.color.graphite)

        populateFields()

        payButton.setOnClickListener {
            if (declarationCheckBoxView.isValid) {
                AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_ConfirmPaymentsScreen_PayButtonClicked")
                presenter.paymentValidation(!internationalPaymentsActivity.isExistingBeneficiary, referenceNumber.toString())
            } else {
                declarationCheckBoxView.setErrorMessage(getString(R.string.international_payments_reserve_bank_error))
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Handler(Looper.getMainLooper()).postDelayed({
            scrollView?.post { scrollView?.smoothScrollTo(0, payButton.y.toInt()) }
        }, 350)
    }

    private fun populateFields() {
        transferQuoteDetails = arguments?.getParcelable(QUOTATION_DETAILS)
        referenceNumber = transferQuoteDetails?.transactionReferenceNumber
        totalAmountPrimaryContentAndLabelView.setContentText(Amount(transferQuoteDetails?.totalDue.toString()).toString())
        youSendSecondaryContentAndLabelView.setContentText(Amount(transferQuoteDetails?.localAmount.toString()).toString())
        beneficiaryReceivesSecondaryContentAndLabelView.setContentText(Amount(Currency.getInstance(transferQuoteDetails?.payOutCurrency.toString()).getSymbol(BMBApplication.getApplicationLocale()), transferQuoteDetails?.expectedPayoutAmount.toString()).toString())
        payoutCurrencyContentAndLabelView.setContentText(transferQuoteDetails?.payOutCurrency)
        fromAccountSecondaryContentAndLabelView.setContentText(transferQuoteDetails?.fromAccountDetails)
        beneficiaryPrimaryContentAndLabelView.setContentText(transferQuoteDetails?.getNameAndSurname())

        val gender = if (transferQuoteDetails?.gender.toString().length < 2) {
            if ("M".equals(transferQuoteDetails?.gender.toString(), true)) getString(R.string.male) else getString(R.string.female)
        } else {
            transferQuoteDetails?.gender
        }
        genderSecondaryContentAndLabelView.setContentText(gender)

        beneficiaryResidentialAddressSecondaryContentAndLabelView.setContentText(transferQuoteDetails?.streetAddress)
        transactionRefNumberAddressSecondaryContentAndLabelView.setContentText(transferQuoteDetails?.transactionReferenceNumber)
        destinationCurrencyRateSecondaryContentAndLabelView.setContentText(transferQuoteDetails?.getFormattedDestinationCurrencyRate())
        foreignCurrencySecondaryContentAndLabelView.setContentText(transferQuoteDetails?.expectedPayoutCurrency)
        foreignAmountSecondaryContentAndLabelView.setContentText(transferQuoteDetails?.expectedPayoutAmount)
        localCurrencySecondaryContentAndLabelView.setContentText(transferQuoteDetails?.localCurrency)
        localAmountSecondaryContentAndLabelView.setContentText(transferQuoteDetails?.localAmount)
        conversionRateInUSDSecondaryContentAndLabelView.setContentText(transferQuoteDetails?.getUSDollarExchangeRate())
        vatSecondaryContentAndLabelView.setContentText(Amount(transferQuoteDetails?.valueAddedTax.toString()).toString())
        commissionAmountSecondaryContentAndLabelView.setContentText(Amount(transferQuoteDetails?.commissionAmountFee.toString()).toString())
        originatingCountrySecondaryContentAndLabelView.setContentText(getString(R.string.south_africa))
        destinationCountrySecondaryContentAndLabelView.setContentText(transferQuoteDetails?.countryToSendTo)
        destinationStateSecondaryContentAndLabelView.setContentText(transferQuoteDetails?.destinationState)
        destinationCitySecondaryContentAndLabelView.setContentText(transferQuoteDetails?.destinationCity)
        natureOfPaymentSecondaryContentAndLabelView.setContentText(getString(R.string.gift))
        exchangeRateSecondaryContentAndLabelView.setContentText(transferQuoteDetails?.getCurrencyExchangeRate())

        if (!transferQuoteDetails?.testQuestion.isNullOrEmpty()) {
            securityQuestionSecondaryContentAndLabelView.setContentText(transferQuoteDetails?.testQuestion)
            answerToSecurityQuestionSecondaryContentAndLabelView.setContentText(transferQuoteDetails?.testAnswer)
        } else {
            securityQuestionSecondaryContentAndLabelView.visibility = View.GONE
            answerToSecurityQuestionSecondaryContentAndLabelView.visibility = View.GONE
        }

        if (transferQuoteDetails?.destinationState.isNullOrEmpty()) {
            destinationStateSecondaryContentAndLabelView.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.cancel_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cancel_menu_item -> {
                AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_ConfirmPaymentsScreen_BackButtonClicked")
                BaseAlertDialog.showYesNoDialog(AlertDialogProperties.Builder()
                        .title(getString(R.string.international_payments_cancel_title))
                        .message(getString(R.string.international_payments_cancel_message))
                        .positiveDismissListener { _, _ ->
                            AnalyticsUtils.getInstance().trackCancelButton(BaseActivity.mScreenName, BaseActivity.mSiteSection)
                            BaseAlertDialog.dismissAlertDialog()
                            activity?.finish()
                        })
                internationalPaymentsActivity.hideProgressIndicator()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun fetchbaseActivity(): BaseActivity = internationalPaymentsActivity

    override fun showPaymentPendingScreen(paymentDate: String, referenceNumber: String) {
        AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_PaymentSuccessfulScreen_ScreenDisplayed")
        hideToolBar()
        internationalPaymentsActivity.hideProgressIndicator()
        dismissProgressDialog()
        val genericResultScreenProperties = InternationalPaymentsResultFactory().buildInternationalPaymentProcessingBundle(internationalPaymentsActivity, DateUtils.getCurrentDateAndTime(), referenceNumber)
        navigate(InternationalPaymentsConfirmPaymentFragmentDirections.actionInternationalPaymentsConfirmPaymentFragmentToInternationalPaymentsResultsFragment(genericResultScreenProperties, true))
    }

    override fun showError(bmgErrorMessage: String) {
        AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_PaymentUnsuccessfulScreen_ScreenDisplayed")
        hideToolBar()
        internationalPaymentsActivity.hideProgressIndicator()
        dismissProgressDialog()
        val genericResultScreenProperties = InternationalPaymentsResultFactory().buildInternationalPaymentFailureBundle(internationalPaymentsActivity, bmgErrorMessage)
        navigate(InternationalPaymentsConfirmPaymentFragmentDirections.actionInternationalPaymentsConfirmPaymentFragmentToInternationalPaymentsResultsFragment(genericResultScreenProperties, true))
    }

    override fun showConnectionErrorScreen() = internationalPaymentsActivity.checkDeviceState()
}