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

package com.barclays.absa.banking.policy_beneficiaries.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.barclays.absa.banking.boundary.model.policy.PolicyBeneficiary
import com.barclays.absa.banking.boundary.model.policy.PolicyDetail
import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse
import com.barclays.absa.banking.express.exergy.dto.ExergyCodesDetails
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.funeralCover.services.InsurancePolicyInteractor
import com.barclays.absa.banking.funeralCover.services.InsurancePolicyService
import com.barclays.absa.banking.policy_beneficiaries.services.ManageBeneficiaryInteractor
import com.barclays.absa.banking.policy_beneficiaries.services.ManageBeneficiaryService
import com.barclays.absa.banking.policy_beneficiaries.services.dto.PolicyBeneficiaryInfo
import com.barclays.absa.banking.policy_beneficiaries.services.dto.PolicyInfo
import com.barclays.absa.banking.riskBasedApproach.services.dto.PersonalInformationResponse
import com.barclays.absa.banking.shared.services.dto.LookupItem
import com.barclays.absa.banking.ultimateProtector.services.dto.CallType
import styleguide.utils.extensions.stripSuffix
import styleguide.utils.extensions.toTitleCase
import java.util.*
import kotlin.collections.ArrayList

class ManageBeneficiaryViewModel : ViewModel() {
    private val manageBeneficiaryService: ManageBeneficiaryService by lazy { ManageBeneficiaryInteractor() }
    private val insurancePolicyService: InsurancePolicyService by lazy { InsurancePolicyInteractor() }
    private lateinit var beneficiaryResultLiveData: MutableLiveData<SureCheckResponse>
    private lateinit var policyDetailLiveData: MutableLiveData<PolicyDetail>
    private lateinit var policyBeneficiary: PolicyBeneficiary
    var exergyBeneficiaryResult = MutableLiveData<SureCheckResponse>()

    lateinit var beneficiaries: List<PolicyBeneficiary>
    lateinit var customerInformation: PersonalInformationResponse.CustomerInformation
    lateinit var beneficiaryAction: BeneficiaryAction

    var titles: List<LookupItem> = emptyList()
    var sourceOfFundList: List<LookupItem> = emptyList()
    var exergyTitles: List<ExergyCodesDetails> = emptyList()
    var exergyRelationships: List<ExergyCodesDetails> = emptyList()
    var policyBeneficiaryInfo = PolicyBeneficiaryInfo()
    var itemPosition: Int = -1
    var policyInfo = PolicyInfo()
    var isExergyPolicy: Boolean = false
    val addNewExergyBeneficiary: Boolean
        get() = isExergyPolicy && hasEstateBeneficiary() && beneficiaryAction == BeneficiaryAction.EDIT

    private val beneficiaryExtendedResponseListener by lazy { BeneficiaryExtendedResponseListener(beneficiaryResultLiveData) }
    private val policyDetailExtendedResponseListener by lazy { PolicyDetailExtendedResponseListener(policyDetailLiveData) }
    private val exergyBeneficiaryExtendedResponseListener: ExtendedResponseListener<SureCheckResponse> by lazy { ExergyBeneficiaryExtendedResponseListener(this) }
    private val appCacheService: IAppCacheService = getServiceInterface()

    var sourceOfFunds: String? = null
        private set
        get() = policyDetailLiveData.value?.accountInfo?.sourceOfFund

    var policyBeneficiaries: List<PolicyBeneficiary> = emptyList()
        private set
        get() = Collections.unmodifiableList(InsuranceBeneficiaryHelper.sortPolicyBeneficiaries(policyDetailLiveData.value?.policyBeneficiaries
                ?: emptyList()))

    fun addExergyBeneficiaryRequest(): LiveData<SureCheckResponse> {
        manageBeneficiaryService.addExergyBeneficiary(policyInfo, policyBeneficiaryInfo, exergyBeneficiaryExtendedResponseListener)
        return exergyBeneficiaryResult
    }

    fun editExergyBeneficiaryRequest(): LiveData<SureCheckResponse> {
        manageBeneficiaryService.editExergyBeneficiary(policyInfo, policyBeneficiaryInfo, exergyBeneficiaryExtendedResponseListener)
        return exergyBeneficiaryResult
    }

    fun removeExergyBeneficiaryRequest(): LiveData<SureCheckResponse> {
        manageBeneficiaryService.removeExergyBeneficiary(policyInfo, policyBeneficiaryInfo, exergyBeneficiaryExtendedResponseListener)
        return exergyBeneficiaryResult
    }

    fun fetchPolicyDetails(): LiveData<PolicyDetail?> {
        if (!::policyDetailLiveData.isInitialized) {
            policyDetailLiveData = MutableLiveData()
            appCacheService.getPolicyDetail()?.policy?.let {
                insurancePolicyService.fetchPolicyDetails(it.type.toString(), it.number.toString(), policyDetailExtendedResponseListener)
            }
        }
        return policyDetailLiveData
    }

    fun hasEstateBeneficiary(): Boolean {
        return if (isExergyPolicy) {
            policyDetailLiveData.value?.policyBeneficiaries?.find { it.title == "60" } != null
        } else {
            policyDetailLiveData.value?.policyBeneficiaries?.find { it.title == "07" } != null
        }
    }

    fun addBeneficiary(callType: CallType): LiveData<SureCheckResponse> {
        if (!::beneficiaryResultLiveData.isInitialized) {
            beneficiaryResultLiveData = MutableLiveData()
        }
        manageBeneficiaryService.addBeneficiary(policyInfo, policyBeneficiaryInfo, callType, beneficiaryExtendedResponseListener)
        return beneficiaryResultLiveData
    }

    fun buildBeneficiaryList(existingBeneficiaries: List<PolicyBeneficiary>): List<PolicyBeneficiary> {
        val beneficiaries = ArrayList<PolicyBeneficiary>()

        beneficiaries.apply {
            for (index in existingBeneficiaries.indices) {
                if (beneficiaryAction == BeneficiaryAction.REMOVE && index == itemPosition) {
                    continue
                }
                if (!hasEstateBeneficiary()) {
                    val existingBeneficiary = existingBeneficiaries[index]
                    existingBeneficiary.fullName = if (beneficiaryAction == BeneficiaryAction.EDIT && index == itemPosition) {
                        ("${policyBeneficiaryInfo.title.first} ${policyBeneficiaryInfo.firstName} ${policyBeneficiaryInfo.surname}").toTitleCase()
                    } else {
                        existingBeneficiary.fullName.toTitleCase()
                    }
                    add(existingBeneficiary)
                }
            }

            if (beneficiaryAction == BeneficiaryAction.ADD || addNewExergyBeneficiary) {
                addNewBeneficiaryToList(beneficiaries)
            }
        }

        if (beneficiaries.size == 1) {
            beneficiaries[0].allocation = MAX_SHARE_ALLOCATION.toString()
        }
        this.beneficiaries = beneficiaries
        return beneficiaries
    }

    private fun addNewBeneficiaryToList(beneficiaries: ArrayList<PolicyBeneficiary>) {
        if (!::policyBeneficiary.isInitialized || hasEstateBeneficiary()) {
            policyBeneficiary = PolicyBeneficiary()
            policyBeneficiary.fullName = ("${policyBeneficiaryInfo.title.first} ${policyBeneficiaryInfo.firstName} ${policyBeneficiaryInfo.surname}").toTitleCase()
            policyBeneficiary.allocation = MAX_SHARE_ALLOCATION.toString()
        }
        beneficiaries.add(policyBeneficiary)
    }

    fun calculateShareAllocation(beneficiaries: List<PolicyBeneficiary>) {
        val totalBeneficiaries = beneficiaries.size
        val defaultAllocation = MAX_SHARE_ALLOCATION / totalBeneficiaries
        beneficiaries.forEachIndexed { index, policyBeneficiary ->
            policyBeneficiary.allocation = if (totalBeneficiaries == 3 && index == 0) (defaultAllocation + 1).toString() else defaultAllocation.toString()
        }
    }

    fun hasEmptyField(beneficiaries: List<PolicyBeneficiary>): Boolean {
        return beneficiaries.last().allocation.isEmpty()
    }

    fun getTotalAllocation(beneficiaries: List<PolicyBeneficiary>): Int {
        var totalAllocation = 0
        if (hasEstateBeneficiary()) {
            totalAllocation = policyBeneficiary.allocation.toInt()
        } else {
            beneficiaries.forEach {
                if (it.allocation.isNotEmpty()) {
                    totalAllocation += it.allocation.toInt()
                }
            }
        }
        return totalAllocation
    }

    fun buildBeneficiaryData(beneficiaries: List<PolicyBeneficiary>) {
        if (hasEstateBeneficiary()) {
            val estateBeneficiary = policyBeneficiaries[0]
            policyBeneficiaryInfo.roleNumber = estateBeneficiary.roleNumber
            policyBeneficiaryInfo.lifeClientCode = estateBeneficiary.lifeClientCode
            policyBeneficiaryInfo.allocation = MAX_SHARE_ALLOCATION.toString()
        } else {
            val beneficiaryAllocationBuilder = StringBuilder()
            val beneficiaryNameBuilder = StringBuilder()
            val beneficiaryLifeClientCodeBuilder = StringBuilder()
            val beneficiaryRoleNumberBuilder = StringBuilder()

            when (beneficiaryAction) {
                BeneficiaryAction.ADD -> {
                    policyBeneficiaryInfo.allocation = beneficiaries[beneficiaries.lastIndex].allocation
                }
                BeneficiaryAction.EDIT -> {
                    policyBeneficiaryInfo.allocation = beneficiaries[itemPosition].allocation
                }
                else -> {
                }
            }

            for (index in policyBeneficiaries.indices) {
                if ((beneficiaryAction == BeneficiaryAction.EDIT || beneficiaryAction == BeneficiaryAction.REMOVE) && itemPosition == index) {
                    beneficiaryNameBuilder.stripSuffix()
                    beneficiaryAllocationBuilder.stripSuffix()
                    beneficiaryLifeClientCodeBuilder.stripSuffix()
                    beneficiaryRoleNumberBuilder.stripSuffix()
                    continue
                }

                val beneficiary = policyBeneficiaries[index]
                beneficiaryNameBuilder.append(beneficiary.fullName)
                beneficiaryAllocationBuilder.append(beneficiary.allocation)
                beneficiaryLifeClientCodeBuilder.append(beneficiary.lifeClientCode)
                beneficiaryRoleNumberBuilder.append(beneficiary.roleNumber)

                if (index != policyBeneficiaries.lastIndex) {
                    beneficiaryNameBuilder.append("|")
                    beneficiaryAllocationBuilder.append("|")
                    beneficiaryLifeClientCodeBuilder.append("|")
                    beneficiaryRoleNumberBuilder.append("|")
                }
            }

            policyBeneficiaryInfo.beneficiaryAllocation = beneficiaryAllocationBuilder.toString()
            policyBeneficiaryInfo.beneficiaryName = beneficiaryNameBuilder.toString()
            policyBeneficiaryInfo.beneficiaryLifeClientCode = beneficiaryLifeClientCodeBuilder.toString()
            policyBeneficiaryInfo.beneficiaryRoleNumber = beneficiaryRoleNumberBuilder.toString()
        }
    }

    fun changeBeneficiary(callType: CallType): LiveData<SureCheckResponse> {
        if (!::beneficiaryResultLiveData.isInitialized) {
            beneficiaryResultLiveData = MutableLiveData()
        }
        manageBeneficiaryService.changeBeneficiary(policyInfo, policyBeneficiaryInfo, callType, beneficiaryExtendedResponseListener)
        return beneficiaryResultLiveData
    }

    fun removeBeneficiary(callType: CallType): LiveData<SureCheckResponse> {
        if (!::beneficiaryResultLiveData.isInitialized) {
            beneficiaryResultLiveData = MutableLiveData()
        }
        manageBeneficiaryService.removeBeneficiary(policyInfo, policyBeneficiaryInfo, callType, beneficiaryExtendedResponseListener)
        return beneficiaryResultLiveData
    }

    fun setData(policyBeneficiary: PolicyBeneficiary, titleDescription: String, titleCode: String) {
        policyBeneficiaryInfo = PolicyBeneficiaryInfo()
        policyBeneficiaryInfo.title = Pair(titleDescription, titleCode)

        policyBeneficiaryInfo.firstName = policyBeneficiary.firstName
        policyBeneficiaryInfo.surname = policyBeneficiary.surname
        if (isExergyPolicy) {
            policyBeneficiaryInfo.relationship = Pair(InsuranceBeneficiaryHelper.getMatchingExergyCodesDetails(policyBeneficiary.relationship, exergyRelationships).description, policyBeneficiary.relationship)
        } else {
            InsuranceBeneficiaryHelper.findRelationship(policyBeneficiary.relationship)?.let {
                policyBeneficiaryInfo.relationship = Pair(it, policyBeneficiary.relationship)
            }
        }

        policyBeneficiaryInfo.allocation = policyBeneficiary.allocation
        policyBeneficiaryInfo.dateOfBirth = policyBeneficiary.dateOfBirth

        InsuranceBeneficiaryHelper.findIdType(policyBeneficiary.idType)?.let {
            policyBeneficiaryInfo.idType = Pair(it, policyBeneficiary.idType)
        }

        policyBeneficiaryInfo.idNumber = policyBeneficiary.idNumber
        policyBeneficiary.address?.let {
            policyBeneficiaryInfo.addressLine1 = it.addressLine1
            policyBeneficiaryInfo.addressLine2 = it.addressLine2
            policyBeneficiaryInfo.town = it.town
            policyBeneficiaryInfo.suburb = it.suburb
            policyBeneficiaryInfo.postalCode = it.postalCode
        }
        policyBeneficiaryInfo.cellphoneNumber = policyBeneficiary.cellphoneNumber
        policyBeneficiaryInfo.emailAddress = policyBeneficiary.emailAddress
        policyBeneficiaryInfo.roleNumber = policyBeneficiary.roleNumber
        policyBeneficiaryInfo.lifeClientCode = policyBeneficiary.lifeClientCode
    }

    fun buildAddress(): String {
        val addressBuilder = StringBuilder()
        addressBuilder.append(policyBeneficiaryInfo.addressLine1).append(", ")
        if (policyBeneficiaryInfo.addressLine2.isNotEmpty()) {
            addressBuilder.append(policyBeneficiaryInfo.addressLine2).append(", ")
        }
        addressBuilder.append(policyBeneficiaryInfo.suburb).append(", ")
                .append(policyBeneficiaryInfo.town).append(", ")
                .append(policyBeneficiaryInfo.postalCode)

        return addressBuilder.toString()
    }

    fun areAllocationsSharedEqual(beneficiaries: List<PolicyBeneficiary>): Boolean {
        val expectedAllocations = mutableListOf(34, 33, 33)
        val beneficiaryAllocations = mutableListOf<Int>()
        beneficiaries.forEach {
            if (it.allocation.isNotEmpty()) {
                beneficiaryAllocations.add(it.allocation.toInt())
            }
        }
        return expectedAllocations.containsAll(beneficiaryAllocations)
    }

    companion object {
        const val MAX_SHARE_ALLOCATION = 100
    }
}