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
 *
 */

package com.barclays.absa.banking.manage.profile.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.manage.profile.services.dto.MarketingIndicator
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.banking.settings.ui.ProfileViewHelper
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.imageHelpers.ImageHelper
import com.barclays.absa.utils.imageHelpers.ProfileViewImageHelper
import kotlinx.android.synthetic.main.manage_profile_hub_fragment.*
import styleguide.utils.extensions.extractTwoLetterAbbreviation

class ManageProfileHubFragment : ManageProfileBaseFragment(R.layout.manage_profile_hub_fragment) {
    private lateinit var marketingIndicator: MarketingIndicator
    private lateinit var profileImageHelper: ProfileViewImageHelper
    private lateinit var customerProfileObject: CustomerProfileObject

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileImageHelper = ProfileViewImageHelper(requireActivity(), customerProfilePicImageView)

        if (manageProfileViewModel.customerInformation.value == null) {
            manageProfileViewModel.fetchCustomerInformation()
        }

        showToolBar()
        setToolBar(getString(R.string.manage_profile_my_profile_toolbar_title))
        AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_LandingScreen_ScreenDisplayed")

        customerProfileObject = CustomerProfileObject.instance

        manageProfileViewModel.bcmsCaseId.removeObservers(viewLifecycleOwner)
        manageProfileViewModel.customerInformation.removeObservers(viewLifecycleOwner)

        manageProfileViewModel.customerInformation.observe(viewLifecycleOwner, {
            marketingIndicator = it.customerInformation.marketingIndicator
            manageProfileViewModel.customerInformation.removeObservers(viewLifecycleOwner)
        })

        manageProfileViewModel.bcmsCaseId.observe(viewLifecycleOwner, {
            if (it.documentUploadStatus.isEmpty() && it.caseID.isNotEmpty() && it.password.isNotEmpty() && (BuildConfig.PRD_BETA || BuildConfig.PRD)) {
                manageProfileViewModel.fetchCase()
            } else {
                initOnClickListeners()
                initBeneficiaryItem()
            }
            manageProfileViewModel.bcmsCaseId.removeObservers(viewLifecycleOwner)
        })

        manageProfileViewModel.docHandlerResponse.observe(viewLifecycleOwner, {
            manageProfileViewModel.bcmsCaseId = MutableLiveData()
            manageProfileViewModel.fetchCaseId()

            initOnClickListeners()
            initBeneficiaryItem()
            manageProfileViewModel.docHandlerResponse.removeObservers(viewLifecycleOwner)
        })

        manageProfileViewModel.bcmsCaseIdFailure.observe(viewLifecycleOwner, {
            dismissProgressDialog()
            manageProfileViewModel.bcmsCaseIdFailure.removeObservers(viewLifecycleOwner)
        })

        manageProfileViewModel.customerProfilePictureResult.observe(viewLifecycleOwner, {
            initBeneficiaryItem()
        })

        manageProfileViewModel.docHandlerFetchCaseFailure.observe(viewLifecycleOwner, {
            showGenericErrorMessageThenFinish()
        })
    }

    private fun initBeneficiaryItem() {
        if (profileImageHelper.imageBitmap == null) {
            initialsTextView.visibility = View.VISIBLE
            initialsTextView.text = customerProfileObject.customerName.extractTwoLetterAbbreviation()
        } else {
            initialsTextView.visibility = View.GONE
        }
        cameraIconImageView.visibility = View.VISIBLE
        customerProfilePicImageView.setImageBitmap(profileImageHelper.imageBitmap)
        customerNameTextView.text = customerProfileObject.customerName.toString()
        clientTypeTextView.text = ProfileViewHelper.getCustomerAccountType(baseActivity)
        dismissProgressDialog()
    }

    private fun initOnClickListeners() {
        profileImageHelper.setOnImageActionListener { ImageHelper.onImageActionListener { sendProfileSetupRequest() } }

        personalDetailsActionButton.setOnClickListener {
            navigate(ManageProfileHubFragmentDirections.actionManageProfileHubFragmentToManageProfilePersonalDetailsOverviewFragment())
        }

        addressDetailsActionButton.setOnClickListener {
            navigate(ManageProfileHubFragmentDirections.actionManageProfileHubFragmentToManageProfileAddressDetailsOverviewFragment())
        }

        contactDetailsActionButton.setOnClickListener {
            navigate(ManageProfileHubFragmentDirections.actionManageProfileHubFragmentToManageProfileContactDetailsOverviewFragment())
        }

        educationActionButton.setOnClickListener {
            navigate(ManageProfileHubFragmentDirections.actionManageProfileHubFragmentToManageProfileEducationAndEmploymentDetailsOverviewFragment())
        }

        financialDetailsActionButton.setOnClickListener {
            navigate(ManageProfileHubFragmentDirections.actionManageProfileHubFragmentToManageProfileFinancialDetailsOverviewFragment())
        }

        marketingConsentActionButton.setOnClickListener {
            navigate(ManageProfileHubFragmentDirections.actionManageProfileHubFragmentToManageProfileMarketingConsentOverviewFragment())
        }

        nextOfKinActionButton.setOnClickListener {
            navigate(ManageProfileHubFragmentDirections.actionManageProfileHubFragmentToManageProfileNextOfKinOverviewFragment())
        }
    }

    private fun sendProfileSetupRequest() = CustomerProfileObject.instance.let {
        manageProfileViewModel.fireProfileSetupRequest(it.languageCode.toString(), it.customerName.toString(), "1", profileImageHelper)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ProfileViewImageHelper.PROFILE_IMAGE_REQUEST -> profileImageHelper.cropThumbnail(data)
                ProfileViewImageHelper.PROFILE_IMAGE_REQUEST_AFTER_CROP -> {
                    profileImageHelper.retrieveThumbnail(data)
                    sendProfileSetupRequest()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) = profileImageHelper.handlePermissionResults(requestCode, grantResults)
}