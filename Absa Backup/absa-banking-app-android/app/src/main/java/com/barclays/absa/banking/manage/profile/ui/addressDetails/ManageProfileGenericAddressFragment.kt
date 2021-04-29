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

package com.barclays.absa.banking.manage.profile.ui.addressDetails

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileActivity
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ADDRESS_DETAILS
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ON_BUTTON_CLICK_EVENT
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ON_BUTTON_CLICK_EVENT_TAG
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.TOOLBAR_TITLE
import com.barclays.absa.banking.manage.profile.ui.models.SuburbLookupResults
import com.barclays.absa.banking.manage.profile.ui.widgets.AddressDetails
import com.barclays.absa.banking.manage.profile.ui.widgets.AddressFlowType
import com.barclays.absa.banking.manage.profile.ui.widgets.ManageProfileAddressInterface
import com.barclays.absa.banking.manage.profile.ui.widgets.ManageProfileAddressWidget
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.viewModel
import kotlinx.android.synthetic.main.manage_profile_address_entry_layout.*
import kotlinx.android.synthetic.main.manage_profile_generic_address_fragment.*

class ManageProfileGenericAddressFragment : BaseFragment(R.layout.manage_profile_generic_address_fragment), ManageProfileAddressInterface {
    private lateinit var manageProfileAddressDetailsViewModel: ManageProfileAddressDetailsViewModel
    private lateinit var manageProfileActivity: ManageProfileActivity
    private lateinit var manageProfileAddressWidget: ManageProfileAddressWidget
    private lateinit var selectedSuburb: SuburbLookupResults
    private lateinit var genericAddressDetails: GenericAddressDetails
    private lateinit var residentialAddress: AddressDetails
    private lateinit var genericAddress: AddressDetails
    private lateinit var flowType: AddressFlowType
    private lateinit var nextStep: (fragment: BaseFragment, manageProfileUpdatedAddressDetails: ManageProfileUpdatedAddressDetails) -> Unit
    private var buttonAnalyticsTag: String = ""
    private var manageProfileUpdatedAddressDetails: ManageProfileUpdatedAddressDetails = ManageProfileUpdatedAddressDetails()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        manageProfileActivity = (context as ManageProfileActivity)
        manageProfileAddressDetailsViewModel = manageProfileActivity.viewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @Suppress("UNCHECKED_CAST")
        arguments?.apply {
            setToolBar(getString(TOOLBAR_TITLE) ?: getString(R.string.address))
            genericAddressDetails = getParcelable(ADDRESS_DETAILS) ?: GenericAddressDetails()
            nextStep = getSerializable(ON_BUTTON_CLICK_EVENT) as (fragment: BaseFragment, manageProfileUpdatedAddressDetails: ManageProfileUpdatedAddressDetails) -> Unit
            buttonAnalyticsTag = getString(ON_BUTTON_CLICK_EVENT_TAG, "")
        }

        genericAddressDetails.residentialAddress?.let { residentialAddress = it }
        genericAddressDetails.genericAddress?.let { genericAddress = it }
        genericAddressDetails.flowType?.let { flowType = it }

        initData()
        setUpOnClickListeners()
    }

    private fun initData() {
        selectedSuburb = manageProfileAddressDetailsViewModel.selectedPostSuburb

        manageProfileAddressWidget = if (::residentialAddress.isInitialized) {
            ManageProfileAddressWidget(manageProfileActivity, flowType, genericAddress, this, residentialAddress)
        } else {
            ManageProfileAddressWidget(manageProfileActivity, flowType, genericAddress, this, null)
        }

        setConstraintsProgrammatically()

        if (manageProfileAddressDetailsViewModel.selectedPostSuburb.suburbPostalCode.isNotEmpty()) {
            manageProfileAddressDetailsViewModel.selectedPostSuburb.apply {
                Handler(Looper.getMainLooper()).postDelayed({
                    suburbNormalInputView.selectedValue = suburbName
                    postalCodeLabelView.setContentText(suburbPostalCode)
                    cityOrSuburbLabelView.setContentText(townName)
                    validateData()
                }, 50)
            }
        }
        manageProfileAddressDetailsViewModel.clearData()
        validateData()
    }

    private fun setUpOnClickListeners() {
        continueButton.setOnClickListener {
            if (buttonAnalyticsTag.isNotEmpty()) {
                AnalyticsUtil.trackAction(ANALYTICS_TAG, buttonAnalyticsTag)
            }

            if (validateData()) {
                manageProfileUpdatedAddressDetails.apply {
                    addressLineOne = addressLineOneNormalInputView.selectedValue
                    addressLineTwo = addressLineTwoNormalInputView.selectedValue
                    suburb = suburbNormalInputView.selectedValue
                    town = cityOrSuburbLabelView.contentTextViewValue
                    postalCode = postalCodeLabelView.contentTextViewValue

                    if (flowType == AddressFlowType.EMPLOYER) {
                        employerName = employerNameNormalInputView.selectedValue
                        employerTelephoneNumber = telephoneNumberNormalInputView.selectedValue
                        employerFaxNumber = if (faxNumberNormalInputView.visibility == View.VISIBLE) faxNumberNormalInputView.selectedValue else ""
                    }
                }
                nextStep.invoke(this, manageProfileUpdatedAddressDetails)
            }
        }
    }

    override fun saveFormData() {}

    override fun validateData(validationResult: Boolean?, wasAnyFieldsChanged: Boolean?): Boolean {
        val isValid = ::manageProfileAddressWidget.isInitialized && manageProfileAddressWidget.validateInputs()
        continueButton.isEnabled = isValid
        return isValid
    }

    private fun setConstraintsProgrammatically() {
        ConstraintSet().apply {
            clone(contactDetailsConstraintLayout)
            contactDetailsConstraintLayout.addView(manageProfileAddressWidget)
            manageProfileAddressWidget.id = View.generateViewId()
            constrainHeight(manageProfileAddressWidget.id, ConstraintSet.WRAP_CONTENT)
            constrainWidth(manageProfileAddressWidget.id, ConstraintSet.MATCH_CONSTRAINT)
            setMargin(manageProfileAddressWidget.id, ConstraintSet.BOTTOM, resources.getDimensionPixelSize(R.dimen.medium_space))

            connect(manageProfileAddressWidget.id, ConstraintSet.TOP, contactDetailsConstraintLayout.id, ConstraintSet.TOP)
            connect(manageProfileAddressWidget.id, ConstraintSet.START, contactDetailsConstraintLayout.id, ConstraintSet.START)
            connect(manageProfileAddressWidget.id, ConstraintSet.END, contactDetailsConstraintLayout.id, ConstraintSet.END)

            connect(continueButton.id, ConstraintSet.TOP, manageProfileAddressWidget.id, ConstraintSet.BOTTOM)
            connect(continueButton.id, ConstraintSet.BOTTOM, contactDetailsConstraintLayout.id, ConstraintSet.BOTTOM)
            setVerticalBias(continueButton.id, 1f)

            applyTo(contactDetailsConstraintLayout)
        }
    }
}