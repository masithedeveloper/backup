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

package com.barclays.absa.banking.payments.international

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.INTERNATIONAL_PAYMENTS
import com.barclays.absa.banking.payments.international.data.BeneficiaryEnteredDetails
import com.barclays.absa.banking.payments.international.data.CurrencyList
import com.barclays.absa.banking.payments.international.data.ToCurrency
import com.barclays.absa.banking.payments.international.services.dto.ValidateNewWesternUnionBeneficiaryResponse
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.international_payments_confirm_beneficiary_details_fragment.*
import styleguide.utils.extensions.toSentenceCase

class InternationalPaymentsConfirmBeneficiaryDetailsFragment : InternationalPaymentsAbstractBaseFragment(R.layout.international_payments_confirm_beneficiary_details_fragment),
        InternationalPaymentsContract.InternationalPaymentsConfirmBeneficiaryDetailsView {

    private var beneficiaryDetails: BeneficiaryEnteredDetails? = BeneficiaryEnteredDetails()
    private lateinit var presenter: InternationalPaymentsContract.InternationalPaymentsConfirmBeneficiaryDetailsPresenter
    private lateinit var sureCheckDelegate: SureCheckDelegate
    private var validateNewWesternUnionBeneficiaryResponse = ValidateNewWesternUnionBeneficiaryResponse()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sureCheckDelegate = object : SureCheckDelegate(internationalPaymentsActivity) {
            override fun onSureCheckProcessed() {
                Handler(Looper.getMainLooper()).postDelayed({ presenter.validationReturned(validateNewWesternUnionBeneficiaryResponse) }, 250)
            }

            override fun onSureCheckRejected() {
                Handler(Looper.getMainLooper()).postDelayed({
                    dismissProgressDialog()
                    hideToolBar()
                    internationalPaymentsActivity.hideProgressIndicator()
                    navigate(InternationalPaymentsConfirmBeneficiaryDetailsFragmentDirections.actionInternationalPaymentsConfirmBeneficiaryDetailsFragmentToInternationalPaymentsResultsFragment(
                            InternationalPaymentsResultFactory().buildInternationalPaymentBeneficiaryNotSavedBundle(internationalPaymentsActivity, ""), true))
                }, 250)
            }

            override fun onSureCheckCancelled() {
                Handler(Looper.getMainLooper()).postDelayed({
                    dismissProgressDialog()
                    hideToolBar()
                    internationalPaymentsActivity.hideProgressIndicator()
                    navigate(InternationalPaymentsConfirmBeneficiaryDetailsFragmentDirections.actionInternationalPaymentsConfirmBeneficiaryDetailsFragmentToInternationalPaymentsResultsFragment(
                            InternationalPaymentsResultFactory().buildInternationalPaymentBeneficiaryNotSavedBundle(internationalPaymentsActivity, ""), true))
                }, 250)
            }

            override fun onSureCheckFailed() {
                Handler(Looper.getMainLooper()).postDelayed({
                    dismissProgressDialog()
                    hideToolBar()
                    internationalPaymentsActivity.hideProgressIndicator()
                    navigate(InternationalPaymentsConfirmBeneficiaryDetailsFragmentDirections.actionInternationalPaymentsConfirmBeneficiaryDetailsFragmentToInternationalPaymentsResultsFragment(
                            InternationalPaymentsResultFactory().buildInternationalPaymentBeneficiaryNotSavedBundle(internationalPaymentsActivity, ""), true))
                }, 250)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_ConfirmBeneficiaryCreationScreen_ScreenDisplayed")
        setToolBar(getString(R.string.international_payments_confirm_details))
        setHasOptionsMenu(true)
        beneficiaryDetails = internationalPaymentsActivity.fetchBeneficiaryBeneficiaryDetailsDataModel()
        presenter = InternationalPaymentsConfirmBeneficiaryDetailsPresenter(this, sureCheckDelegate)

        populateFields()

        personalInformationTitleTextView.text = getString(R.string.international_payments_personal_info_text, internationalPaymentsActivity.flowTypeString).toSentenceCase()
        personGenderContentView.setLabelText(getString(R.string.beneficiary_gender_text, internationalPaymentsActivity.flowTypeString))
        residentialStatusContentView.setLabelText(getString(R.string.residential_status_text, internationalPaymentsActivity.flowTypeString))
        addressInformationTextView.text = getString(R.string.international_payments_beneficiary_address_information, internationalPaymentsActivity.flowTypeString)
        residentialAddressContentView.setLabelText(getString(R.string.international_beneficiary_address, internationalPaymentsActivity.flowTypeString))
        residentialCityContentView.setLabelText(getString(R.string.international_payments_city, internationalPaymentsActivity.flowTypeString))
        residentialStateContentView.setLabelText(getString(R.string.international_payments_state, internationalPaymentsActivity.flowTypeString))
        residentialCountryContentView.setLabelText(getString(R.string.international_payments_country, internationalPaymentsActivity.flowTypeString))

        saveBeneficiaryButton.setOnClickListener {
            AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_ConfirmBeneficiaryCreationScreen_ContinueButtonClicked")
            if (!internationalPaymentsActivity.isOnceOffPayment && !internationalPaymentsActivity.isExistingBeneficiary) {
                internationalPaymentsActivity.isExistingBeneficiary = true
                beneficiaryDetails?.let {
                    presenter.validateBeneficiaryDetails(it)
                }
            } else {
                presenter.fetchCurrencies()
            }
        }
    }

    private fun populateFields() {
        namesContentView.setContentText(beneficiaryDetails?.formattedFirstName().toString())

        if (beneficiaryDetails?.beneficiaryGender?.length!! < 2) {
            if (beneficiaryDetails?.beneficiaryGender.equals("M")) {
                personGenderContentView.setContentText(getString(R.string.male))
            } else {
                personGenderContentView.setContentText(getString(R.string.female))
            }
        } else {
            personGenderContentView.setContentText(beneficiaryDetails?.beneficiaryGender)
        }

        if (beneficiaryDetails?.beneficiaryCitizenship.toString() == InternationalPaymentsConstants.RES_ACCOUNT_ABROAD) {
            residentialStatusContentView.setContentText(getString(R.string.temporarily_abroad))
        } else {
            residentialStatusContentView.setContentText(getString(R.string.non_sa_resident))
        }
        residentialAddressContentView.setContentText(beneficiaryDetails?.paymentAddress)

        if (!beneficiaryDetails?.paymentState.isNullOrEmpty()) {
            residentialStateContentView.setContentText(beneficiaryDetails?.paymentState)
        } else {
            residentialStateContentView.visibility = View.GONE
        }
        residentialCityContentView.setContentText(beneficiaryDetails?.paymentCity)
        residentialCountryContentView.setContentText(beneficiaryDetails?.paymentCountry)
    }

    override fun navigateToSuccessScreen() {
        hideToolBar()
        internationalPaymentsActivity.hideProgressIndicator()
        navigate(InternationalPaymentsConfirmBeneficiaryDetailsFragmentDirections.actionInternationalPaymentsConfirmBeneficiaryDetailsFragmentToInternationalPaymentsCalculateFragment(null, null, null))
    }

    override fun navigateToFailureScreen(transactionMessage: String?) {
        hideToolBar()
        AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_ConfirmBeneficiaryCreationUnsuccessfulScreen_ScreenDisplayed")
        internationalPaymentsActivity.hideProgressIndicator()
        navigate(InternationalPaymentsConfirmBeneficiaryDetailsFragmentDirections.actionInternationalPaymentsConfirmBeneficiaryDetailsFragmentToInternationalPaymentsResultsFragment(
                InternationalPaymentsResultFactory().buildInternationalPaymentBeneficiaryNotSavedBundle(internationalPaymentsActivity, transactionMessage.toString()), true))
    }

    override fun populateCurrencyList(listOfSendCurrencies: ArrayList<CurrencyList>, listOfCurrencies: ArrayList<CurrencyList>, destinationCurrency: ToCurrency) {
        navigate(InternationalPaymentsConfirmBeneficiaryDetailsFragmentDirections.actionInternationalPaymentsConfirmBeneficiaryDetailsFragmentToInternationalPaymentsCalculateFragment(
                listOfSendCurrencies.toTypedArray(), listOfCurrencies.toTypedArray(), destinationCurrency))
    }

    override fun fetchbaseActivity(): BaseActivity = internationalPaymentsActivity

    override fun fetchBeneficiaryValidationResponse(validateNewWesternUnionBeneficiaryResponse: ValidateNewWesternUnionBeneficiaryResponse) {
        this.validateNewWesternUnionBeneficiaryResponse = validateNewWesternUnionBeneficiaryResponse
    }

    override fun getLifecycleCoroutineScope() = lifecycleScope

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.cancel_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cancel_menu_item -> {
                AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_ConfirmBeneficiaryCreationScreen_CancelButtonClicked")
                BaseAlertDialog.showYesNoDialog(AlertDialogProperties.Builder()
                        .message(getString(R.string.international_are_you_sure_you_want_to_cancel))
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
}