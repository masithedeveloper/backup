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
 */

package com.barclays.absa.banking.manage.profile.ui.addressDetails

import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.navigation.Navigation
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.BaseFragment
import com.barclays.absa.banking.framework.ssl.FileResponse
import com.barclays.absa.banking.manage.profile.ManageProfileFileUtils
import com.barclays.absa.banking.manage.profile.ManageProfileFlow
import com.barclays.absa.banking.manage.profile.services.dto.BCMSCaseIdResponse
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ON_BUTTON_CLICK_EVENT
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ON_BUTTON_CLICK_EVENT_TAG
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.PROOF_OF_PHYSICAL_RESIDENCE
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.TOOLBAR_TITLE
import com.barclays.absa.banking.manage.profile.ui.ManageProfileResultFactory
import com.barclays.absa.banking.manage.profile.ui.models.ManageProfileDocHandlerFileToFetchDetails
import com.barclays.absa.banking.manage.profile.ui.widgets.AddressDetails
import com.barclays.absa.banking.manage.profile.ui.widgets.AddressFlowType
import com.barclays.absa.banking.manage.profile.ui.widgets.ManageProfileFileOptions
import com.barclays.absa.banking.manage.profile.ui.widgets.ManageProfileUploadedDocumentsWidget
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.ProfileManager
import kotlinx.android.synthetic.main.manage_profile_address_details_overview_fragment.*
import styleguide.content.BaseContentAndLabelView
import styleguide.utils.extensions.toTitleCase
import java.io.File
import java.io.Serializable

class ManageProfileAddressDetailsOverviewFragment : ManageProfileBaseFragment(R.layout.manage_profile_address_details_overview_fragment), ManageProfileFileOptions {
    private var genericAddressDetails: GenericAddressDetails = GenericAddressDetails()
    private lateinit var manageProfileUploadedDocumentWidget: ManageProfileUploadedDocumentsWidget
    private lateinit var country: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.manage_profile_address_details_overview_toolbar_title)
        manageProfileViewModel.manageProfileFlow = ManageProfileFlow.UPDATE_ADDRESS_DETAILS

        manageProfileViewModel.fetchCountryForCountryCode()
        initData()
        initOnClickListeners()
    }

    private fun initData() {
        manageProfileViewModel.country.observe(viewLifecycleOwner, { country ->
            this.country = country
            val addressDetails = manageProfileViewModel.customerInformation.value?.customerInformation

            addressDetails?.residentialAddress?.apply {
                hideEmptyFields(addressLineOneContentView, addressLine1.toTitleCase())
                hideEmptyFields(addressLineTwoContentView, addressLine2.toTitleCase())
                hideEmptyFields(suburbContentView, suburbRsa.toTitleCase())
                hideEmptyFields(cityTownContentView, town.toTitleCase())
                hideEmptyFields(postalCodeContentView, postalCode.toTitleCase())
                hideEmptyFields(countryContentView, country.toTitleCase())
            }

            addressDetails?.postalAddress?.apply {
                hideEmptyFields(postalAddressLineOneContentView, addressLine1.toTitleCase())
                hideEmptyFields(postalAddressLineTwoContentView, addressLine2.toTitleCase())
                hideEmptyFields(postalSuburbContentView, suburbRsa.toTitleCase())
                hideEmptyFields(postalCityContentView, town.toTitleCase())
                hideEmptyFields(postalPostalCodeContentView, postalCode.toTitleCase())
            }

            genericAddressDetails = GenericAddressDetails().apply {
                flowType = AddressFlowType.PERSONAL

                genericAddress = AddressDetails().apply {
                    addressLineOne = addressDetails?.postalAddress?.addressLine1.toString()
                    addressLineTwo = addressDetails?.postalAddress?.addressLine2.toString()
                    suburb = addressDetails?.postalAddress?.suburbRsa.toString()
                    town = addressDetails?.postalAddress?.town.toString()
                    postalCode = addressDetails?.postalAddress?.postalCode.toString()
                }

                residentialAddress = AddressDetails().apply {
                    addressLineOne = addressDetails?.residentialAddress?.addressLine1.toString()
                    addressLineTwo = addressDetails?.residentialAddress?.addressLine2.toString()
                    suburb = addressDetails?.residentialAddress?.suburbRsa.toString()
                    town = addressDetails?.residentialAddress?.town.toString()
                    postalCode = addressDetails?.residentialAddress?.postalCode.toString()
                }
            }
            dismissProgressDialog()
            manageProfileViewModel.country.removeObservers(viewLifecycleOwner)
        })

        var bcmsCase = BCMSCaseIdResponse()
        manageProfileViewModel.bcmsCaseId.value?.let { bcmsCase = it }

        if (bcmsCase.isDocumentUploadComplete && bcmsCase.documentUploadStatus.isNotEmpty()) {
            residentialAddressActionView.setActionTextGone()
            manageProfileUploadedDocumentWidget = ManageProfileUploadedDocumentsWidget(manageProfileActivity, bcmsCase.documentUploadStatus[1], this)
            documentLinearLayout.addView(manageProfileUploadedDocumentWidget)
        } else {
            residentialAddressActionView.setActionTextVisible()
        }
    }

    private fun initOnClickListeners() {
        postalAddressActionView.setCustomActionOnclickListener {
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_AddressDetailsScreen_EditPostalAddressButtonClicked")
            Bundle().apply {
                putString(TOOLBAR_TITLE, getString(R.string.manage_profile_address_details_edit_postal_address_toolbar_title))
                putParcelable(ManageProfileConstants.ADDRESS_DETAILS, genericAddressDetails)
                putSerializable(ON_BUTTON_CLICK_EVENT, nextStep() as Serializable)
                putString(ON_BUTTON_CLICK_EVENT_TAG, "ManageProfile_PostalAddressScreen_ContinueButtonClicked")
                Navigation.findNavController(it).navigate(R.id.manageProfileGenericAddressFragment, this)
            }
        }

        residentialAddressActionView.setCustomActionOnclickListener {
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_AddressDetailsScreen_EditResidentialAddressButtonClicked")
            navigate(ManageProfileAddressDetailsOverviewFragmentDirections.actionManageProfileAddressDetailsOverviewFragmentToManageProfileDocumentUploadAddressInformationFragment(country))
        }
    }

    private fun hideEmptyFields(displayElement: BaseContentAndLabelView, content: String) {
        if (content.isBlank()) {
            displayElement.visibility = View.GONE
        } else {
            displayElement.setContentText(content)
        }
    }

    override fun openFile() {
        val bcmsCase = manageProfileViewModel.bcmsCaseId.value ?: BCMSCaseIdResponse()
        val indexOfFile = bcmsCase.documentUploadStatus.indexOfFirst { it.description == PROOF_OF_PHYSICAL_RESIDENCE }
        showProgressDialog()

        val filePath = "UserFiles/${ProfileManager.getInstance().activeUserProfile.userId.toString()}/${bcmsCase.documentUploadStatus[indexOfFile].name}"
        val file = File(baseActivity.cacheDir, filePath)
        if (!file.exists()) {
            ManageProfileDocHandlerFileToFetchDetails().apply {
                caseId = bcmsCase.caseID
                documentId = bcmsCase.documentUploadStatus[indexOfFile].documentId
                encodedPassword = Base64.encodeToString(bcmsCase.password.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
                fileName = bcmsCase.documentUploadStatus[indexOfFile].name

                manageProfileViewModel.downloadFile(this).observe(viewLifecycleOwner, {
                    if (it is FileResponse.SuccessResponse) {
                        ManageProfileFileUtils.saveFileToLocalStorage(baseActivity, ProfileManager.getInstance().activeUserProfile.userId.toString(), it.byteArray, bcmsCase.documentUploadStatus[indexOfFile].name)
                        openLocalFile()
                    } else {
                        navigate(ManageProfileAddressDetailsOverviewFragmentDirections.actionGlobalManageProfileResultFragment(ManageProfileResultFactory(this@ManageProfileAddressDetailsOverviewFragment).updatedOtherPersonalInformationFailure()))
                    }
                    dismissProgressDialog()
                })
            }
        } else {
            dismissProgressDialog()
            openLocalFile()
        }
    }

    private fun openLocalFile() {
        val filePath = "UserFiles/${ProfileManager.getInstance().activeUserProfile.userId.toString()}/${manageProfileViewModel.bcmsCaseId.value?.documentUploadStatus?.get(1)?.name}"
        val file = File(baseActivity.cacheDir, filePath)
        manageProfileActivity.openFileOnDevice(file)
    }
}

private fun nextStep(): (baseFragment: BaseFragment, manageProfileUpdatedAddressDetails: ManageProfileUpdatedAddressDetails) -> Unit {
    return { baseFragment: BaseFragment, manageProfileUpdatedAddressDetails: ManageProfileUpdatedAddressDetails ->
        baseFragment.navigate(ManageProfileGenericAddressFragmentDirections.actionManageProfileGenericAddressFragmentToManageProfileConfirmPostalAddressDetailsFragment(manageProfileUpdatedAddressDetails))
    }
}