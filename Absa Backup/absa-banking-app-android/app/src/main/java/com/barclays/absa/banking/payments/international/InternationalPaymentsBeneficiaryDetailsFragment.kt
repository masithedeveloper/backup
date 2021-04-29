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

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.INTERNATIONAL_PAYMENTS
import com.barclays.absa.banking.payments.international.data.BeneficiaryEnteredDetails
import com.barclays.absa.banking.payments.international.data.InternationalCountryList
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog.showAlertDialog
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.international_payments_beneficiary_details_fragment.*
import styleguide.forms.SelectorList
import styleguide.forms.StringItem
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher

class InternationalPaymentsBeneficiaryDetailsFragment : InternationalPaymentsAbstractBaseFragment(R.layout.international_payments_beneficiary_details_fragment),
        InternationalPaymentsContract.InternationalPaymentsCountryListView {

    private lateinit var presenter: InternationalPaymentsContract.InternationalPaymentsCountryListPresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_PayNewBeneficiaryDetailsScreen_ScreenDisplayed")

        presenter = InternationalPaymentsCountryListPresenter(this)
        setToolBar(getString(R.string.international_payments_toolbar, internationalPaymentsActivity.flowTypeString))
        setUpRadioButtons()

        if (internationalPaymentsActivity.isOnceOffPayment && internationalPaymentsActivity.showOnceOffDialog) {
            internationalPaymentsActivity.showOnceOffDialog = false
            showAlertDialog(AlertDialogProperties.Builder()
                    .title(getString(R.string.western_union_once_off_payment))
                    .message(getString(R.string.western_union_once_off_payments_body))
                    .positiveButton(getString(R.string.continue_button))
                    .negativeButton(getString(R.string.international_payments_go_back))
                    .negativeDismissListener { _, _ -> Navigation.findNavController(activity as Activity, R.id.international_nav_host_fragment).navigate(R.id.internationalPaymentHubFragment) }
                    .build())
        }

        continueButton.setOnClickListener {
            AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_PayNewBeneficiaryDetailsScreen_NextButton")
            if (validateInputs()) {
                addDataToDataModel()
                presenter.fetchListOfCountries()
            }
        }

        nameInputView.addRequiredValidationHidingTextWatcher()
        surnameInputView.addRequiredValidationHidingTextWatcher()

        selectGenderRadioButtonView.setItemCheckedInterface {
            selectGenderRadioButtonView.hideError()
        }

        residentialStatusRadioButtonView.setItemCheckedInterface {
            residentialStatusRadioButtonView.hideError()
        }

        beneficiaryResidentialStatusLabel.text = getString(R.string.beneficiary_residential_status, internationalPaymentsActivity.flowTypeString)

        internationalPaymentsActivity.showProgressIndicator()
        internationalPaymentsActivity.setProgressStep(1)
        BMBApplication.getInstance().deviceProfilingInteractor.notifyAddBeneficiary()
    }

    private fun addDataToDataModel() = internationalPaymentsActivity.updateBeneficiaryBeneficiaryDetailsDataModel(BeneficiaryEnteredDetails().apply {
        beneficiaryNames = nameInputView.selectedValue
        beneficiarySurname = surnameInputView.selectedValue
        beneficiaryGender = determineSelectedGender()
        beneficiaryCitizenship = determineCitizenshipStatus()
    })

    private fun determineCitizenshipStatus(): String {
        return if (residentialStatusRadioButtonView.selectedIndex == 0) {
            InternationalPaymentsConstants.RES_ACCOUNT_ABROAD
        } else {
            InternationalPaymentsConstants.NON_RESIDENT_OTHER
        }
    }

    private fun determineSelectedGender(): String =
            if (selectGenderRadioButtonView.selectedIndex == 0) getString(R.string.male) else getString(R.string.female)

    private fun validateInputs(): Boolean {
        if (nameInputView.selectedValue.isEmpty()) {
            nameInputView.setError(getString(R.string.please_enter_beneficiary_names, internationalPaymentsActivity.flowHintTypeString))
            return false
        } else {
            nameInputView.hideError()
        }

        if (surnameInputView.selectedValue.isEmpty()) {
            surnameInputView.setError(getString(R.string.please_enter_beneficiary_surnames, internationalPaymentsActivity.flowHintTypeString))
            return false
        } else {
            surnameInputView.hideError()
        }

        if (!selectGenderRadioButtonView.isValid) {
            selectGenderRadioButtonView.setErrorMessage(getString(R.string.please_select_a_gender))
            return false
        } else {
            selectGenderRadioButtonView.hideError()
        }

        if (!residentialStatusRadioButtonView.isValid) {
            residentialStatusRadioButtonView.setErrorMessage(getString(R.string.please_select_a_residential_status))
            return false
        } else {
            residentialStatusRadioButtonView.hideError()
        }

        return true
    }

    private fun setUpRadioButtons() {
        selectGenderRadioButtonView.setDataSource(SelectorList<StringItem>().apply {
            add(StringItem(getString(R.string.male)))
            add(StringItem(getString(R.string.female)))
        })
        residentialStatusRadioButtonView.setDataSource(SelectorList<StringItem>().apply {
            add(StringItem(getString(R.string.temporarily_abroad)))
            add(StringItem(getString(R.string.non_sa_resident)))
        })
    }

    override fun countryListReturned(westernUnionCountries: ArrayList<InternationalCountryList>) {
        navigate(InternationalPaymentsBeneficiaryDetailsFragmentDirections.actionBeneficiaryDetailsFragmentToInternationalPaymentsPaymentDetailsFragment(westernUnionCountries.toTypedArray()))
    }

    override fun getLifecycleCoroutineScope() = lifecycleScope
}