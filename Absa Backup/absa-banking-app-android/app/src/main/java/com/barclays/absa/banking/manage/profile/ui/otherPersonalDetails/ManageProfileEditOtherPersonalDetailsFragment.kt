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

package com.barclays.absa.banking.manage.profile.ui.otherPersonalDetails

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.barclays.absa.banking.R
import com.barclays.absa.banking.manage.profile.ui.ManageProfileBaseFragment
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants
import com.barclays.absa.banking.manage.profile.ui.models.ManageProfileUpdatedPersonalInformation
import com.barclays.absa.banking.manage.profile.ui.models.PersonalInformationDisplay
import com.barclays.absa.banking.shared.services.dto.LookupItem
import com.barclays.absa.utils.AnalyticsUtil
import kotlinx.android.synthetic.main.manage_profile_edit_other_personal_details_fragment.*
import styleguide.forms.NormalInputView
import styleguide.forms.SelectorList
import styleguide.utils.extensions.toTitleCase

class ManageProfileEditOtherPersonalDetailsFragment : ManageProfileBaseFragment(R.layout.manage_profile_edit_other_personal_details_fragment) {
    private lateinit var personalInformation: PersonalInformationDisplay

    private var nationalityList: List<LookupItem> = listOf()
    private var titleList: List<LookupItem> = listOf()
    private var homeLanguageList: List<LookupItem> = listOf()
    private var hasValuesChanged = false
    private var isFieldEmpty = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(R.string.manage_profile_other_personal_details_toolbar_title)
        initData()
        initViewContent()
        continueButton.isEnabled = validateInputs()
        initListeners()
        setUpTextWatchers()
    }

    private fun initData() {
        personalInformation = manageProfileViewModel.personalInformation.value ?: PersonalInformationDisplay()
        nationalityList = manageProfileViewModel.retrieveNationalityList()
        titleList = manageProfileViewModel.retrieveTitleList()
        homeLanguageList = manageProfileViewModel.retrieveHomeLanguageList()

        titleNormalInputView.setList(titleList as SelectorList<LookupItem>, getString(R.string.manage_profile_title_toolbar_title))
        homeLanguageNormalInputView.setList(homeLanguageList as SelectorList<LookupItem>, getString(R.string.manage_profile_home_language_toolbar_title))
        nationalityNormalInputView.setList(nationalityList as SelectorList<LookupItem>, getString(R.string.manage_profile_nationality_toolbar_title))
    }

    private fun initViewContent() {
        personalInformation.apply {
            if (title.isNotBlank()) {
                titleNormalInputView.selectedValue = title.toTitleCase()
                titleNormalInputView.selectedIndex = titleList.indexOfFirst { it.defaultLabel.equals(title, true) }
            }

            if (homeLanguage.isNotBlank()) {
                homeLanguageNormalInputView.selectedIndex = homeLanguageList.indexOfFirst { it.defaultLabel.equals(homeLanguage, true) }
                homeLanguageNormalInputView.selectedValue = homeLanguage.toTitleCase()
            }

            if (nationality.isNotBlank()) {
                nationalityNormalInputView.selectedIndex = nationalityList.indexOfFirst { it.defaultLabel.equals(nationality, true) }
                nationalityNormalInputView.selectedValue = nationality.toTitleCase()
            }

            dependentsNormalInputView.selectedValue = numberOfDependants
        }
    }

    private fun initListeners() {
        continueButton.setOnClickListener {
            AnalyticsUtil.trackAction(ManageProfileConstants.ANALYTICS_TAG, "ManageProfile_EditOtherPersonalDetailsScreen_ContinueButtonClicked")
            val personalDetails = ManageProfileUpdatedPersonalInformation()
            personalDetails.apply {
                maritalStatus = maritalStatus.toTitleCase()
                title = titleList[titleNormalInputView.selectedIndex].itemCode.toString()
                homeLanguage = homeLanguageList[homeLanguageNormalInputView.selectedIndex].itemCode.toString()
                nationality = nationalityList[nationalityNormalInputView.selectedIndex].itemCode.toString()
                numberOfDependant = dependentsNormalInputView.selectedValue.ifEmpty { "0" }
                preferredCorrespondenceLanguage = personalInformation.preferredCorrespondenceLanguage
                homeLanguageDisplayValue = homeLanguageList[homeLanguageNormalInputView.selectedIndex].defaultLabel.toTitleCase()
                titleDisplayValue = titleList[titleNormalInputView.selectedIndex].defaultLabel.toTitleCase()
                homeLanguageDisplayValue = homeLanguageNormalInputView.selectedValueUnmasked
                nationalityDisplayValue = nationalityList[nationalityNormalInputView.selectedIndex].defaultLabel.toTitleCase()
            }

            manageProfileViewModel.personalDetailsToUpdate = personalDetails
            navigate(ManageProfileEditOtherPersonalDetailsFragmentDirections.actionManageProfileEditOtherPersonalDetailsFragmentToManageProfileEditPersonalDetailsConfirmationFragment())
        }
    }

    private fun setUpTextWatchers() {
        titleNormalInputView.addValueViewTextWatcher(genericTextWatcher(titleNormalInputView))
        dependentsNormalInputView.addValueViewTextWatcher(genericTextWatcher(dependentsNormalInputView))
        homeLanguageNormalInputView.addValueViewTextWatcher(genericTextWatcher(homeLanguageNormalInputView))
        nationalityNormalInputView.addValueViewTextWatcher(genericTextWatcher(nationalityNormalInputView))
    }

    private fun genericTextWatcher(normalInputView: NormalInputView<*>): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                isFieldEmpty = isSelectedValueNotEmpty()
                hasValuesChanged = hasFieldChanged(normalInputView)
                continueButton.isEnabled = validateInputs()
                normalInputView.clearError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    private fun isSelectedValueNotEmpty(): Boolean {
        val listOfInputs = listOf(nationalityNormalInputView, homeLanguageNormalInputView, titleNormalInputView)

        listOfInputs.forEach {
            if (it.selectedValueUnmasked.isBlank() || it.selectedIndex == -1) {
                return false
            }
        }
        return true
    }

    private fun hasFieldChanged(normalInputView: NormalInputView<*>): Boolean {
        val listOfInputs = listOf(homeLanguageNormalInputView, titleNormalInputView, dependentsNormalInputView, nationalityNormalInputView)

        listOfInputs.forEach {
            if (it == normalInputView && it.hasValueChanged()) {
                return true
            }
        }
        return false
    }

    private fun validateInputs(): Boolean {
        return isFieldEmpty && hasValuesChanged
    }
}