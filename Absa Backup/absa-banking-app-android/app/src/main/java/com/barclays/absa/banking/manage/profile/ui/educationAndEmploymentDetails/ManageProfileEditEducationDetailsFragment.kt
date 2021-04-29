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

package com.barclays.absa.banking.manage.profile.ui.educationAndEmploymentDetails

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.barclays.absa.banking.R
import com.barclays.absa.banking.manage.profile.services.dto.EmploymentInformation
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.ANALYTICS_TAG
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.NO
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.YES
import com.barclays.absa.banking.manage.profile.ui.widgets.ConfirmScreenItem
import com.barclays.absa.banking.shared.services.dto.LookupItem
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.manage_profile_edit_education_details_fragment.*
import styleguide.forms.SelectorList
import styleguide.forms.StringItem

class ManageProfileEditEducationDetailsFragment : ManageProfileBaseFragment(R.layout.manage_profile_edit_education_details_fragment) {
    private var educationDetails = EmploymentInformation()
    private var qualificationLookup = SelectorList<LookupItem>()
    private var selectedItemIndex = -1
    private var originalHighestQualificationSelectedIndex = -1
    private var selectedRadioButton = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initChangeListeners()
        initRadioGroup()
        initData()
        initOnClickListeners()
    }

    private fun initData() {
        manageProfileViewModel.customerInformation.value?.customerInformation?.employmentInformation?.let {
            educationDetails = it
        }

        qualificationLookup = manageProfileViewModel.retrieveQualificationList()
        highestQualificationNormalInputView.setList(qualificationLookup, getString(R.string.manage_profile_highest_qualification_label))
        postMatricRadioGroup.selectedIndex = if (YES.equals(educationDetails.postMetricInd, true)) 0 else 1

        if (postMatricRadioGroup.selectedIndex == 0) {
            originalHighestQualificationSelectedIndex = qualificationLookup.indexOfFirst { it.itemCode.equals(educationDetails.highestLvlOfEducation, true) }
            highestQualificationNormalInputView.selectedIndex = originalHighestQualificationSelectedIndex
        }
    }

    private fun initRadioGroup() {
        val radioGroupOptions: SelectorList<StringItem> = SelectorList()
        radioGroupOptions.add(StringItem(getString(R.string.yes)))
        radioGroupOptions.add(StringItem(getString(R.string.no)))

        postMatricRadioGroup.setDataSource(radioGroupOptions)
    }

    private fun initChangeListeners() {
        postMatricRadioGroup.setItemCheckedInterface { index ->
            if (index == 0) {
                selectedRadioButton = 0
                highestQualificationNormalInputView.visibility = View.VISIBLE
            } else {
                selectedRadioButton = 1
                highestQualificationNormalInputView.visibility = View.GONE
            }
            validateInputs()
        }

        highestQualificationNormalInputView.setItemSelectionInterface {
            selectedItemIndex = it
            validateInputs()
        }
    }

    private fun validateInputs() {
        continueButton.isEnabled = YES.equals(educationDetails.postMetricInd, ignoreCase = true) && selectedRadioButton == 1 || (YES.equals(educationDetails.postMetricInd, ignoreCase = true) || NO.equals(educationDetails.postMetricInd, ignoreCase = true) && selectedRadioButton == 0) && selectedItemIndex != originalHighestQualificationSelectedIndex && highestQualificationNormalInputView.selectedValue.isNotEmpty()
    }

    private fun initOnClickListeners() {
        continueButton.setOnClickListener {
            manageProfileViewModel.confirmScreenItemList.clear()

            manageProfileViewModel.manageProfileEducationDetailsModel.apply {
                hasPostMatricQualification = if (postMatricRadioGroup.selectedIndex == 0) YES else NO
                highestQualification = if (postMatricRadioGroup.selectedIndex == 0 && highestQualificationNormalInputView.isVisible) qualificationLookup[selectedItemIndex].defaultLabel.toString() else "00"
                highestQualificationCode = if (postMatricRadioGroup.selectedIndex == 0 && highestQualificationNormalInputView.isVisible) qualificationLookup[selectedItemIndex].itemCode.toString() else "00"
            }

            ConfirmScreenItem().apply {
                value = if (postMatricRadioGroup.selectedIndex == 0) getString(R.string.yes) else getString(R.string.no)
                label = getString(R.string.manage_profile_education_details_post_matric_label)

                manageProfileViewModel.confirmScreenItemList.add(this)
            }

            if (postMatricRadioGroup.selectedIndex == 0) {
                ConfirmScreenItem().apply {
                    value = manageProfileViewModel.manageProfileEducationDetailsModel.highestQualification
                    label = getString(R.string.manage_profile_education_details_highest_qualification_label)

                    manageProfileViewModel.confirmScreenItemList.add(this)
                }
            }

            AnalyticsUtil.trackAction(ANALYTICS_TAG, "ManageProfile_EducationDetailsScreen_ContinueButtonClicked")
            navigate(ManageProfileEditEducationDetailsFragmentDirections.actionManageProfileEditEducationDetailsFragmentToManageProfileConfirmEducationDetailsChangeFragment())
        }
    }
}