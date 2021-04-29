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
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.BENEFICIARY_DETAILS
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.CITY_SELECTED
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.COUNTRY_CODE
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.COUNTRY_DESCRIPTION
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.COUNTRY_SELECTED
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.ENTERED_CITY
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.INTERNATIONAL_CITY_LIST
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.INTERNATIONAL_COUNTRY_LIST
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.INTERNATIONAL_PAYMENTS
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.INTERNATIONAL_STATE_LIST
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.IS_CITY_SELECTED
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.IS_SECURITY_QUESTION_REQUIRED
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.SELECTED_CITY
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.SELECTED_STATE
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.SHOULD_DISPLAY_SECURITY_QUESTION
import com.barclays.absa.banking.payments.international.InternationalPaymentsConstants.Companion.STATE_SELECTED
import com.barclays.absa.banking.payments.international.data.BeneficiaryEnteredDetails
import com.barclays.absa.banking.payments.international.data.InternationalBeneficiaryItem
import com.barclays.absa.banking.payments.international.data.InternationalCountryList
import com.barclays.absa.banking.payments.international.services.IInternationalPaymentCacheService
import com.barclays.absa.banking.shared.AlertDialogProperties
import com.barclays.absa.banking.shared.BaseAlertDialog
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.international_payments_payment_details_fragment.*
import styleguide.forms.validation.addRequiredValidationHidingTextWatcher
import styleguide.utils.extensions.toSentenceCase

class InternationalPaymentsPaymentDetailsFragment : InternationalPaymentsAbstractBaseFragment(R.layout.international_payments_payment_details_fragment),
        InternationalPaymentsContract.InternationalPaymentsExtraDetailsView {

    private val internationalPaymentCacheService: IInternationalPaymentCacheService = getServiceInterface()
    private lateinit var presenter: InternationalPaymentsContract.InternationalPaymentsExtraDetailsPresenter
    private var beneficiaryDetails: BeneficiaryEnteredDetails? = BeneficiaryEnteredDetails()
    private var countryList: ArrayList<InternationalCountryList> = ArrayList()
    private var stateList: ArrayList<String> = ArrayList()
    private var cityList: ArrayList<String> = ArrayList()
    private var countryCode = ""
    private var isSecurityQuestionRequired = ""
    private var selectedState = ""
    private var selectedCity = ""
    private var isCitySelected = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            var enteredCity = ""
            if (savedInstanceState.getString(ENTERED_CITY) != null) {
                enteredCity = savedInstanceState.getString(ENTERED_CITY)!!
            }
            beneficiaryDetails = savedInstanceState.getParcelable(BENEFICIARY_DETAILS)
            countryCode = savedInstanceState.getString(COUNTRY_CODE)!!
            isSecurityQuestionRequired = savedInstanceState.getString(IS_SECURITY_QUESTION_REQUIRED)!!
            selectedState = savedInstanceState.getString(SELECTED_STATE)!!
            isCitySelected = savedInstanceState.getBoolean(IS_CITY_SELECTED, false)

            if (selectedState.isNotEmpty()) {
                selectDestinationStateLargeInputView.visibility = View.VISIBLE
            }

            if (!isCitySelected) {
                enterDestinationCityInputView.visibility = View.VISIBLE
                enterDestinationCityInputView.selectedValue = enteredCity
            }
        }

        presenter = InternationalPaymentsPaymentDetailsPresenter(this)

        setToolBar(getString(R.string.payment_details))
        AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_PaymentDetailsScreen_ScreenDisplayed")
        setHasOptionsMenu(true)

        beneficiaryDetails = internationalPaymentsActivity.fetchBeneficiaryBeneficiaryDetailsDataModel()

        arguments?.let {
            countryList = InternationalPaymentsPaymentDetailsFragmentArgs.fromBundle(it).countryList.toList() as ArrayList<InternationalCountryList>
        }

        initOnClickListener()
        checkIsExistingBeneficiary()
        initTextWatchers()
        populateCountryList()

        beneficiaryResidentialAddressInputView.setTitleText(getString(R.string.beneficiary_residential_address_title, internationalPaymentsActivity.flowTypeString))
        beneficiaryResidentialAddressInputView.setHintText((getString(R.string.enter_beneficiary_address_hint, internationalPaymentsActivity.flowHintTypeString)).toSentenceCase())
        internationalPaymentsActivity.setProgressStep(3)

        val internationalBeneficiaryItem = InternationalBeneficiaryItem()
        internationalBeneficiaryItem.name = beneficiaryDetails?.beneficiaryNames?.split("\\s".toRegex())?.get(0)
        if (beneficiaryDetails?.beneficiaryCitizenship.toString() == InternationalPaymentsConstants.NON_RESIDENT_OTHER || getString(R.string.non_sa_resident).equals(beneficiaryDetails?.beneficiaryCitizenship, ignoreCase = true)) {
            internationalBeneficiaryItem.residentialStatus = getString(R.string.non_sa_resident)
        } else {
            internationalBeneficiaryItem.residentialStatus = getString(R.string.temporarily_abroad)
        }
        beneficiaryView.setBeneficiary(internationalBeneficiaryItem)
        BMBApplication.getInstance().deviceProfilingInteractor.notifyTransaction()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(BENEFICIARY_DETAILS, beneficiaryDetails)
        outState.putString(COUNTRY_CODE, countryCode)
        outState.putString(IS_SECURITY_QUESTION_REQUIRED, isSecurityQuestionRequired)
        outState.putString(SELECTED_STATE, selectedState)

        outState.putBoolean(IS_CITY_SELECTED, isCitySelected)
        internationalPaymentCacheService.setEnteredBeneficiaryDetails(beneficiaryDetails!!)

        super.onSaveInstanceState(outState)
    }

    private fun checkIsExistingBeneficiary() {
        if (!internationalPaymentsActivity.isExistingBeneficiary) {
            beneficiaryResidentialAddressInputView.addRequiredValidationHidingTextWatcher()
            if (!beneficiaryDetails?.paymentCity.isNullOrEmpty()) {
                enterDestinationCityInputView.visibility = View.VISIBLE
                enterDestinationCityInputView.selectedValue = beneficiaryDetails?.paymentCity.toString()
            }
        } else {
            val westernUnionBeneficiaryDetails = internationalPaymentsActivity.westernUnionBeneficiaryDetails

            beneficiaryDetails?.beneficiaryGender = westernUnionBeneficiaryDetails?.westernUnionDetails?.gender
            beneficiaryResidentialAddressInputView.visibility = View.GONE

            val westernUnionDetails = westernUnionBeneficiaryDetails?.westernUnionDetails
            beneficiaryResidentialAddressInputView.selectedValue = westernUnionDetails?.streetAddress.toString()

            if (!westernUnionDetails?.residingCountry?.countryDescription.isNullOrEmpty()) {
                destinationCountryInputView.selectedValue = westernUnionDetails?.residingCountry?.countryDescription.toString()
                countryCode = westernUnionDetails?.residingCountry?.countryCode.toString()
            }

            if (!beneficiaryDetails?.paymentState.isNullOrEmpty() || !westernUnionDetails?.residingCountry?.stateName.isNullOrEmpty()) {
                presenter.countryChanged(countryCode)
                selectedState = westernUnionDetails?.residingCountry?.stateName.toString()
                if (selectedState.isNotEmpty()) {
                    selectDestinationStateLargeInputView.visibility = View.VISIBLE
                    selectDestinationStateLargeInputView.selectedValue = selectedState
                } else {
                    selectDestinationStateLargeInputView.visibility = View.GONE
                }

                if (!westernUnionDetails?.residingCountry?.cityName.isNullOrEmpty()) {
                    enterDestinationCityInputView.visibility = View.VISIBLE
                    selectedCity = westernUnionDetails?.residingCountry?.cityName!!
                    enterDestinationCityInputView.selectedValue = selectedCity
                }
            } else {
                if (!westernUnionDetails?.residingCountry?.cityName.isNullOrEmpty()) {
                    selectDestinationCityLargeInputView.visibility = View.GONE
                    enterDestinationCityInputView.visibility = View.VISIBLE
                    selectedCity = westernUnionDetails?.residingCountry?.cityName!!
                    enterDestinationCityInputView.selectedValue = selectedCity
                }
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        restoreFields()
    }

    private fun initTextWatchers() {
        enterDestinationCityInputView.addValueViewTextWatcher(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    presenter.cityEnteredOrChanged(isSecurityQuestionRequired)
                }
            }
        })

        securityQuestionInputView.addRequiredValidationHidingTextWatcher()
        securityAnswerInputView.addRequiredValidationHidingTextWatcher()
    }

    private fun initOnClickListener() {
        destinationCountryInputView.setOnClickListener {
            val intent = Intent(internationalPaymentsActivity, InternationalPaymentsCountrySelectorActivity::class.java)
            intent.putExtra(INTERNATIONAL_COUNTRY_LIST, countryList)
            startActivityForResult(intent, COUNTRY_SELECTED)
        }

        selectDestinationStateLargeInputView.setOnClickListener {
            val intent = Intent(internationalPaymentsActivity, InternationalPaymentsStateSelectorActivity::class.java)
            intent.putExtra(INTERNATIONAL_STATE_LIST, stateList)
            startActivityForResult(intent, STATE_SELECTED)
        }

        selectDestinationCityLargeInputView.setOnClickListener {
            val intent = Intent(internationalPaymentsActivity, InternationalPaymentsCitySelectorActivity::class.java)
            intent.putExtra(INTERNATIONAL_CITY_LIST, cityList)
            startActivityForResult(intent, CITY_SELECTED)
        }

        continueButton.setOnClickListener {
            AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_PaymentDetailsScreen_NextButtonClicked")
            if (validateInputs()) {
                beneficiaryDetails?.paymentAddress = beneficiaryResidentialAddressInputView.selectedValue
                beneficiaryDetails?.paymentCountry = destinationCountryInputView.selectedValue
                beneficiaryDetails?.paymentState = selectedState
                if (selectedCity.isNotEmpty()) {
                    internationalPaymentsActivity.westernUnionBeneficiaryDetails?.westernUnionDetails?.residingCountry?.cityName = selectedCity
                    beneficiaryDetails?.paymentCity = selectedCity
                } else {
                    internationalPaymentsActivity.westernUnionBeneficiaryDetails?.westernUnionDetails?.residingCountry?.cityName = enterDestinationCityInputView.selectedValue
                    beneficiaryDetails?.paymentCity = enterDestinationCityInputView.selectedValue
                }
                beneficiaryDetails?.paymentQuestion = securityQuestionInputView.selectedValue
                beneficiaryDetails?.paymentAnswer = securityAnswerInputView.selectedValue
                beneficiaryDetails?.paymentDestinationCountryCode = countryCode
                beneficiaryDetails?.let { it1 -> internationalPaymentsActivity.updateBeneficiaryBeneficiaryDetailsDataModel(it1) }

                navigate(InternationalPaymentsPaymentDetailsFragmentDirections.actionInternationalPaymentsPaymentDetailsFragmentToInternationalPaymentsConfirmBeneficiaryDetailsFragment())
            }
        }
    }

    private fun restoreFields() {
        if (selectedState.isNotEmpty()) {
            selectDestinationStateLargeInputView.visibility = View.VISIBLE

            if (selectedCity.isNotEmpty()) {
                enterDestinationCityInputView.visibility = View.VISIBLE
            }
        } else {
            if (selectedCity.isNotEmpty()) {
                enterDestinationCityInputView.visibility = View.VISIBLE
            }
        }
    }

    private fun validateInputs(): Boolean {
        if (beneficiaryResidentialAddressInputView.selectedValue.isEmpty()) {
            beneficiaryResidentialAddressInputView.setError(getString(R.string.please_enter_a_valid_address).toSentenceCase())
            return false
        } else {
            beneficiaryResidentialAddressInputView.hideError()
        }

        if (destinationCountryInputView.selectedValue.isEmpty()) {
            destinationCountryInputView.setError(getString(R.string.please_select_a_valid_country))
            return false
        } else {
            destinationCountryInputView.hideError()
        }

        if (selectDestinationCityLargeInputView.visibility == View.VISIBLE && selectDestinationCityLargeInputView.selectedValue.isEmpty()) {
            selectDestinationCityLargeInputView.setError(getString(R.string.please_select_a_valid_city))
            return false
        } else {
            selectDestinationCityLargeInputView.hideError()
        }

        if (selectDestinationStateLargeInputView.visibility == View.VISIBLE && selectDestinationStateLargeInputView.selectedValue.isEmpty()) {
            selectDestinationStateLargeInputView.setError(getString(R.string.please_select_a_valid_state))
            return false
        } else {
            selectDestinationStateLargeInputView.hideError()
        }

        if (securityQuestionInputView.visibility == View.VISIBLE && securityQuestionInputView.selectedValue.isEmpty()) {
            securityQuestionInputView.setError(getString(R.string.pelase_enter_a_security_question))
            return false
        } else {
            securityQuestionInputView.hideError()
        }

        if (securityAnswerInputView.visibility == View.VISIBLE && securityAnswerInputView.selectedValue.isEmpty()) {
            securityAnswerInputView.setError(getString(R.string.please_enter_a_security_answer))
            return false
        } else {
            securityAnswerInputView.hideError()
        }

        if (enterDestinationCityInputView.visibility == View.VISIBLE && enterDestinationCityInputView.selectedValue.isEmpty()) {
            enterDestinationCityInputView.setError(getString(R.string.please_enter_a_valid_city))
            return false
        } else {
            enterDestinationCityInputView.hideError()
        }
        return true
    }

    private fun populateCountryList() {
        if (countryCode.isNotEmpty() && countryList.find { s -> s.countryCode == countryCode }?.requiresSecurityQuestion == "Y") {
            securityQuestionInputView.visibility = View.VISIBLE
            securityAnswerInputView.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == COUNTRY_SELECTED) {
                destinationCountryInputView.selectedValue = data?.getStringExtra(COUNTRY_DESCRIPTION).toString()
                destinationCountryInputView.hideError()
                if (data != null) {
                    countryCode = data.getStringExtra(COUNTRY_CODE) ?: ""
                    beneficiaryDetails?.paymentCountry = countryCode
                    isSecurityQuestionRequired = data.getStringExtra(SHOULD_DISPLAY_SECURITY_QUESTION) ?: ""
                }
                resetAndHideOtherFieldsIfCountryChanged()
                presenter.countryChanged(beneficiaryDetails?.paymentCountry.toString())

                val westernUnionBeneficiaryDetails = internationalPaymentsActivity.westernUnionBeneficiaryDetails

                if (selectedState.isNotEmpty() && westernUnionBeneficiaryDetails?.westernUnionDetails != null) {
                    selectedState = ""
                    westernUnionBeneficiaryDetails.westernUnionDetails?.residingCountry?.stateName = ""
                }
            }

            if (requestCode == STATE_SELECTED) {
                data?.let { selectedState = it.getStringExtra(SELECTED_STATE) ?: "" }
                selectDestinationStateLargeInputView.hideError()
                selectDestinationStateLargeInputView.selectedValue = selectedState

                val westernUnionBeneficiaryDetails = internationalPaymentsActivity.westernUnionBeneficiaryDetails
                if (westernUnionBeneficiaryDetails != null) {
                    westernUnionBeneficiaryDetails.westernUnionDetails?.residingCountry?.stateName = selectedState
                } else {
                    beneficiaryDetails?.paymentState = selectedState
                }

                presenter.stateChanged(countryCode, selectedState)
                selectDestinationStateLargeInputView.visibility = View.VISIBLE
            }

            if (requestCode == CITY_SELECTED) {
                data?.let { selectedCity = it.getStringExtra(SELECTED_CITY) ?: "" }
                selectDestinationCityLargeInputView.hideError()
                selectDestinationCityLargeInputView.selectedValue = selectedCity
                isCitySelected = true
            }
        }
    }

    private fun resetAndHideOtherFieldsIfCountryChanged() {
        selectedState = ""
        selectedCity = ""

        val westernUnionBeneficiaryDetails = internationalPaymentsActivity.westernUnionBeneficiaryDetails
        if (westernUnionBeneficiaryDetails != null) {
            westernUnionBeneficiaryDetails.westernUnionDetails?.residingCountry?.stateName = ""
        } else {
            beneficiaryDetails?.paymentState = ""
        }

        apply {
            securityAnswerInputView.selectedValue = ""
            securityQuestionInputView.selectedValue = ""
            enterDestinationCityInputView.selectedValue = ""
            selectDestinationCityLargeInputView.selectedValue = ""

            selectDestinationStateLargeInputView.visibility = View.VISIBLE
            securityAnswerInputView.visibility = View.GONE
            securityQuestionInputView.visibility = View.GONE
            enterDestinationCityInputView.visibility = View.GONE
            selectDestinationCityLargeInputView.visibility = View.GONE
            selectDestinationStateLargeInputView.selectedValue = ""
        }
    }

    override fun populateStateList(stateList: ArrayList<String>) {
        this.stateList = stateList
        selectDestinationStateLargeInputView.visibility = View.VISIBLE
    }

    override fun populateLongCityList(cityList: ArrayList<String>) {
        this.cityList = cityList
        selectDestinationCityLargeInputView.visibility = View.VISIBLE
        enterDestinationCityInputView.visibility = View.GONE
    }

    override fun allowUserToEnterCity() {
        selectDestinationCityLargeInputView.visibility = View.GONE
        enterDestinationCityInputView.visibility = View.VISIBLE
    }

    override fun noStateRequired() {
        selectDestinationStateLargeInputView.visibility = View.GONE
        enterDestinationCityInputView.visibility = View.VISIBLE
    }

    override fun showSecurityQuestion() {
        internationalPaymentsActivity.hasSecurityQuestion = true
        securityQuestionInputView.visibility = View.VISIBLE
        securityAnswerInputView.visibility = View.VISIBLE
    }

    override fun hideSecurityQuestion() {
        internationalPaymentsActivity.hasSecurityQuestion = false
        securityQuestionInputView.visibility = View.GONE
        securityAnswerInputView.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.cancel_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cancel_menu_item -> {
                AnalyticsUtil.trackAction(INTERNATIONAL_PAYMENTS, "InternationalPayments_PaymentDetailsScreen_CancelButtonClicked")
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
