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

package com.barclays.absa.banking.flexiFuneral.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.flexiFuneral.services.dto.*
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.funeralCover.services.dto.RetailAccountsResponse
import com.barclays.absa.banking.presentation.shared.BaseViewModel

class FlexiFuneralViewModel : BaseViewModel() {
    var flexiFuneralInteractor = FlexiFuneralInteractor()
    var flexiFuneralData = FlexiFuneralData()
    var flexiFuneralValidationRulesDetails = emptyList<FlexiFuneralValidationRulesDetails>()

    var isFromFamilyMemberDetailsFragment: Boolean = false
    var familyMemberList: MutableList<MultipleDependentsDetails> = mutableListOf()
    var delimitedFamilyMemberLists = FamilyLists()
    var multipleDependentsDetails = MultipleDependentsDetails()
    var flexiFuneralBeneficiaryDetails = FlexiFuneralBeneficiaryDetails()
    var applyForFlexiFuneralData = ApplyForFlexiFuneralData()

    var validationRules = MutableLiveData<FlexiFuneralValidationRulesResponse>()
    var mainMemberCoverAmounts = MutableLiveData<MainMemberCoverAmountsResponse>()
    var familyMemberCoverAmounts = MutableLiveData<FamilyMemberCoverAmountsResponse>()
    var addBeneficiaryStatus = MutableLiveData<AddBeneficiaryStatusResponse>()
    var flexiFuneralPremium = MutableLiveData<FlexiFuneralPremiumResponse>()
    var applyForFlexiFuneral = MutableLiveData<ApplyForFlexiFuneralResponse>()

    var getRetailAccounts = MutableLiveData<RetailAccountsResponse>()

    var planCode = ""
    var coverAmount = ""

    private val validationRulesExtendedResponseListener: ExtendedResponseListener<FlexiFuneralValidationRulesResponse> by lazy {
        FlexiFuneralValidationRulesExtendedResponseListener(this)
    }

    private val mainMemberCoverAmountsExtendedResponseListener: ExtendedResponseListener<MainMemberCoverAmountsResponse> by lazy {
        MainMemberCoverAmountsExtendedResponseListener(this)
    }

    private val familyMemberCoverAmountsExtendedResponseListener: ExtendedResponseListener<FamilyMemberCoverAmountsResponse> by lazy {
        FamilyMemberCoverAmountsExtendedResponseListener(this)
    }

    private val addFlexiFuneralBeneficiaryResponseListener: ExtendedResponseListener<AddBeneficiaryStatusResponse> by lazy {
        AddBeneficiaryExtendedResponseListener(this)
    }
    
    private val flexiFuneralPremiumExtendedResponseListener: ExtendedResponseListener<FlexiFuneralPremiumResponse> by lazy {
        FlexiFuneralPremiumExtendedResponseListener(this)
    }

    private val applyForFlexiFuneralExtendedResponseListener: ExtendedResponseListener<ApplyForFlexiFuneralResponse> by lazy {
        ApplyForFlexiFuneralExtendedResponseListener(this)
    }

    fun fetchValidationRules() {
        flexiFuneralInteractor.fetchValidationRules(validationRulesExtendedResponseListener)
    }

    fun fetchMainMemberCoverAmounts() {
        flexiFuneralInteractor.fetchMainMemberCoverAmounts(mainMemberCoverAmountsExtendedResponseListener)
    }

    fun fetchFamilyMemberCoverAmounts(multipleDependentsDetails: MultipleDependentsDetails) {
        flexiFuneralInteractor.fetchFamilyMemberCoverAmounts(multipleDependentsDetails, familyMemberCoverAmountsExtendedResponseListener)
    }

    fun addBeneficiary(beneficiaryDetails: FlexiFuneralBeneficiaryDetails) {
        flexiFuneralInteractor.addFlexiFuneralBeneficiary(beneficiaryDetails, addFlexiFuneralBeneficiaryResponseListener)
    }

    fun fetchFlexiFuneralPremium(multipleDependentsDetails: MultipleDependentsDetails) {
        flexiFuneralInteractor.fetchFlexiFuneralPremium(multipleDependentsDetails, flexiFuneralPremiumExtendedResponseListener)
    }

    fun applyForFlexiFuneral(applyForFlexiFuneralData: ApplyForFlexiFuneralData) {
        flexiFuneralInteractor.applyForFlexiFuneral(applyForFlexiFuneralData, applyForFlexiFuneralExtendedResponseListener)
    }

    fun buildFamilyMemberDelimitedList(familyMember: List<MultipleDependentsDetails>) {
        delimitedFamilyMemberLists = FamilyLists()
        familyMember.forEach {
            delimitedFamilyMemberLists.apply {
                initialsList.add(it.dependentsInitials)
                surnamesList.add(it.dependentsSurname)
                relationshipsList.add(it.dependentsRelationship)
                dateOfBirthList.add(it.dependentsDateOfBirth)
                coverAmountList.add(it.dependentsCoverAmount)
                premiumList.add(it.dependentsPremium)
                genderList.add(it.dependentsGender)
            }
        }

        multipleDependentsDetails.apply {
            dependentsInitials = delimitedFamilyMemberLists.initialsList.joinToString("|")
            dependentsSurname = delimitedFamilyMemberLists.surnamesList.joinToString("|")
            dependentsRelationship = delimitedFamilyMemberLists.relationshipsList.joinToString("|")
            dependentsDateOfBirth = delimitedFamilyMemberLists.dateOfBirthList.joinToString("|")
            dependentsCoverAmount = delimitedFamilyMemberLists.coverAmountList.joinToString("|")
            dependentsPremium = delimitedFamilyMemberLists.premiumList.joinToString("|")
            dependentsGender = delimitedFamilyMemberLists.genderList.joinToString("|")
        }
    }
}