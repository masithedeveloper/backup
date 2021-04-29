/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.saveAndInvest

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.depositorPlus.ui.*
import com.barclays.absa.banking.express.invest.getProductDetailsInfo.SaveAndInvestProductDetailsInfoViewModel
import com.barclays.absa.banking.express.invest.saveUserProductDetails.UserProductDetailsSaveViewModel
import com.barclays.absa.banking.express.invest.saveUserProductDetails.dto.UserProductDetailsSaveRequest
import com.barclays.absa.banking.express.shared.getCustomerDetails.CustomerDetailsViewModel
import com.barclays.absa.banking.express.shared.getSavedApplications.SavedApplicationViewModel
import com.barclays.absa.banking.express.shared.getSavedApplications.dto.SavedApplication
import com.barclays.absa.banking.express.shared.getSavedApplications.dto.SavedApplicationRequest
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations
import styleguide.screens.GenericResultScreenFragment
import styleguide.screens.GenericResultScreenProperties
import styleguide.utils.extensions.separateThousands

abstract class SaveAndInvestApplyFragment(@LayoutRes layout: Int) : SaveAndInvestBaseFragment(layout) {

    private val savedApplicationViewModel by viewModels<SavedApplicationViewModel>()
    private val customerDetailsViewModel by viewModels<CustomerDetailsViewModel>()
    private val userProductDetailsSaveViewModel by viewModels<UserProductDetailsSaveViewModel>()
    private val saveAndInvestProductDetailsInfoViewModel by viewModels<SaveAndInvestProductDetailsInfoViewModel>()
    protected var lastSavedApplicationMutableLiveData = MutableLiveData<SavedApplication>()

    open var productType: String = PRODUCT_TYPE
    abstract var productName: String

    abstract fun navigateToGenericResultFragment()
    abstract fun navigateOnCIFHold()
    abstract fun navigateToPersonalInformation()
    abstract fun onProductDetailsReturned()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saveUserProductDetails()
    }

    protected fun boldAndFormatAmount(@StringRes text: Int, amount: String) : SpannableString {
        val formattedAmount = "R${amount.separateThousands()}"
        val fullText = getString(text, formattedAmount)
        val indexOfTextToBold = fullText.indexOf(formattedAmount)
        return SpannableString(fullText).apply { setSpan(StyleSpan(Typeface.BOLD), indexOfTextToBold, indexOfTextToBold + formattedAmount.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) }
    }

    private fun saveUserProductDetails() {
        with(userProductDetailsSaveViewModel) {
            saveUserProductDetails(UserProductDetailsSaveRequest().apply {
                productCode = saveAndInvestActivity.productType.productCode
                productType = this@SaveAndInvestApplyFragment.productType
                creditRatePlanCode = saveAndInvestActivity.productType.creditRatePlanCode
                productName = this@SaveAndInvestApplyFragment.productName
            })
            saveUserProductDetailsLiveData.observe(viewLifecycleOwner, {
                saveUserProductDetailsLiveData.removeObservers(viewLifecycleOwner)
                if (it.cifHoldStatus) {
                    dismissProgressDialog()
                    navigateOnCIFHold()
                } else {
                    fetchProductDetailsInfo()
                }
            })
            failureLiveData.observe(viewLifecycleOwner, {
                dismissProgressDialog()
                navigateToGenericResultFragment()
            })
        }
    }

    private fun fetchProductDetailsInfo() {
        saveAndInvestProductDetailsInfoViewModel.fetchProductDetailsInfo(saveAndInvestActivity.productType)
        saveAndInvestProductDetailsInfoViewModel.productDetailsInfoLiveData.observe(viewLifecycleOwner, {
            saveAndInvestViewModel.saveAndInvestProductInfo = it.saveAndInvestProductInfo
            onProductDetailsReturned()
            dismissProgressDialog()
        })
    }

    protected fun fetchCustomerDetails() {
        customerDetailsViewModel.fetchCustomerDetails()
        customerDetailsViewModel.customerDetailsLiveData.observe(viewLifecycleOwner, {
            customerDetailsViewModel.customerDetailsLiveData.removeObservers(viewLifecycleOwner)

            with(saveAndInvestViewModel) {
                customerDetails = it
                with(SaveAndInvestRiskBasedHelper) {
                    addressDetails = buildSaveApplicationAddressDetails(it.contactInfo.homeAddress, it.contactInfo.postalAddress)
                    contactDetails = buildSaveApplicationContactDetails(it.contactInfo)
                    personalDetails = buildSaveApplicationPersonalDetails(it.personalDetails, saveAndInvestActivity.productType, productName)
                    accountCreationDetails = buildSaveApplicationAccountCreationDetails(saveAndInvestActivity.productType.creditRatePlanCode)
                }
            }
            //TODO uncommented fetchSavedApplications() and remove navigateToPersonalInformation() when saved application feature is ready in the next release cycle
            //fetchSavedApplications()
            navigateToPersonalInformation()
        })
    }

    private fun fetchSavedApplications() {
        savedApplicationViewModel.fetchSavedApplications(SavedApplicationRequest().apply { idNumber = saveAndInvestViewModel.customerDetails.personalDetails.identityNumber })
        savedApplicationViewModel.savedApplicationsLiveData.observe(viewLifecycleOwner, {
            lastSavedApplicationMutableLiveData.value = it.lastOrNull { savedApplications -> DepositorPlusActivity.DEPOSITOR_PLUS.equals(savedApplications.productName, true) && !savedApplications.expired }
            savedApplicationViewModel.savedApplicationsLiveData.removeObservers(viewLifecycleOwner)
            dismissProgressDialog()
        })
    }

    protected fun getCIFHoldResultProperties(): GenericResultScreenProperties {
        GenericResultScreenFragment.setPrimaryButtonOnClick {
            baseActivity.navigateToHomeScreenSelectingHomeIcon()
        }

        return GenericResultScreenProperties.PropertiesBuilder()
                .setResultScreenAnimation(ResultAnimations.generalFailure)
                .setTitle(getString(R.string.depositor_plus_cif_error_header))
                .setDescription(getString(R.string.depositor_plus_cif_error_message))
                .setContactViewContactName(getString(R.string.depositor_plus_call_centre))
                .setContactViewContactNumber(getString(R.string.support_contact_number))
                .setPrimaryButtonLabel(getString(R.string.done))
                .build(false)
    }
}