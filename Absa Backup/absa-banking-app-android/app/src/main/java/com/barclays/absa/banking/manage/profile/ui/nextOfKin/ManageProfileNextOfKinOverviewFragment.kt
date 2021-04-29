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

package com.barclays.absa.banking.manage.profile.ui.nextOfKin

import android.os.Bundle
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.manage.profile.services.dto.NextOfKinDetails
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.manage_profile_next_of_kin_overview_fragment.*
import styleguide.content.BaseContentAndLabelView
import styleguide.utils.extensions.toFormattedCellphoneNumber
import styleguide.utils.extensions.toTitleCase

class ManageProfileNextOfKinOverviewFragment : ManageProfileBaseFragment(R.layout.manage_profile_next_of_kin_overview_fragment) {
    private lateinit var nextOfKinDetails: NextOfKinDetails
    private var relationshipLabel: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(getString(R.string.manage_profile_hub_next_of_kin_title))
        manageProfileViewModel.fetchRelationship()
        initObservers()
        initOnclickListeners()
    }

    private fun initObservers() {
        manageProfileViewModel.customerInformation.value?.customerInformation?.nextOfKinDetails?.let {
            nextOfKinDetails = it
        }
        manageProfileViewModel.relationshipLookUpResult.observe(viewLifecycleOwner, {
            relationshipLabel = manageProfileViewModel.getLookupValue(it, nextOfKinDetails.relationship)
            initData()
            dismissProgressDialog()
            manageProfileViewModel.relationshipLookUpResult.removeObservers(this)
        })
    }

    private fun initData() {
        manageProfileViewModel.nextOfKinDetailsToUpdate.apply {
            firstName = nextOfKinDetails.firstName
            surname = nextOfKinDetails.surname
            relationship = nextOfKinDetails.relationship
            cellphoneNumber = nextOfKinDetails.cellphoneNumber
            email = nextOfKinDetails.email
            homeTelephoneCode = nextOfKinDetails.homeTelephoneCode
            homeTelephoneNumber = nextOfKinDetails.homeTelephoneNumber
            workTelephoneCode = nextOfKinDetails.workTelephoneCode
            workTelephoneNumber = nextOfKinDetails.workTelephoneNumber
        }

        setContentViewTextAndVisibility(firstNamePrimaryContentAndLabelView, nextOfKinDetails.firstName.toTitleCase())
        setContentViewTextAndVisibility(surnamePrimaryContentAndLabelView, nextOfKinDetails.surname.toTitleCase())
        setContentViewTextAndVisibility(relationshipSecondaryContentAndLabelView, relationshipLabel)
        setContentViewTextAndVisibility(cellphoneSecondaryContentAndLabelView, nextOfKinDetails.cellphoneNumber.toFormattedCellphoneNumber())
        setContentViewTextAndVisibility(emailAddressSecondaryContentAndLabelView, nextOfKinDetails.email.toLowerCase(BMBApplication.getApplicationLocale()))
        setContentViewTextAndVisibility(homePhoneSecondaryContentAndLabelView, (nextOfKinDetails.homeTelephoneCode + nextOfKinDetails.homeTelephoneNumber).toFormattedCellphoneNumber())
        setContentViewTextAndVisibility(workPhoneSecondaryContentAndLabelView, (nextOfKinDetails.workTelephoneCode + nextOfKinDetails.workTelephoneNumber).toFormattedCellphoneNumber())
    }

    private fun setContentViewTextAndVisibility(contentView: BaseContentAndLabelView, content: String) {
        if (content.isEmpty()) {
            contentView.visibility = View.GONE
        } else {
            contentView.setContentText(content)
            contentView.visibility = View.VISIBLE
        }
    }

    private fun initOnclickListeners() {
        editNextOfKinActionView.setCustomActionOnclickListener {
            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_NextOfKinSummaryScreen_EditNextOfKinDetailsButtonClicked")
            navigate(ManageProfileNextOfKinOverviewFragmentDirections.actionManageProfileNextOfKinOverviewFragmentToManageProfileEditNextOfKinFragment())
        }
    }
}