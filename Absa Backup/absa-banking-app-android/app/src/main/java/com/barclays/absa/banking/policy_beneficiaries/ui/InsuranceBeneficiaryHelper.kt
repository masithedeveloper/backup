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

import android.content.Context
import com.barclays.absa.banking.R
import com.barclays.absa.banking.boundary.model.policy.PolicyBeneficiary
import com.barclays.absa.banking.express.exergy.dto.ExergyCodesDetails
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.shared.services.dto.LookupItem
import com.barclays.absa.banking.shared.services.dto.SuburbResult
import styleguide.forms.SelectorInterface
import styleguide.forms.SelectorList
import styleguide.forms.StringItem

object InsuranceBeneficiaryHelper {

    val titleMap = HashMap<String, String>()
    val idTypesMap = LinkedHashMap<String, String>()
    val relationshipMap = HashMap<String, String>()

    fun buildIdTypes(idTypes: Array<String>, codes: Array<String>) {
        idTypesMap[idTypes[0]] = codes[0]
        idTypesMap[idTypes[1]] = codes[1]
    }

    fun buildTitles(titles: Array<String>) {
        val codes = arrayOf("03", "01", "02", "21")
        codes.forEachIndexed { index, _ ->
            titleMap[titles[index]] = codes[index]
        }
    }

    fun buildRelationships(relationships: Array<String>) {
        val codes = arrayOf("SON", "DAUG", "SPEC", "STUD", "FATH", "MOTH", "SFAI", "SMOT", "FAIL", "MOIL", "GRAN", "BROT", "SIST", "EBRO", "ESIS", "UNC", "AUNT", "NEPH", "NIEC", "SPOU", "COUS", "OTHE")
        codes.forEachIndexed { index, _ ->
            relationshipMap[relationships[index]] = codes[index]
        }
    }

    fun findRelationship(code: String): String? {
        return relationshipMap.filter { it.value == code }.keys.firstOrNull()
    }

    fun findTitle(code: String): String? {
        return titleMap.filter { it.value == code }.keys.firstOrNull()
    }

    fun findIdType(code: String): String? {
        return idTypesMap.filter { it.value == code }.keys.firstOrNull()
    }

    fun findIdTypeIndex(code: String): Int {
        return idTypesMap.values.indexOfFirst { it == code }
    }

    fun findCategory(context: Context, relationship: String): String {
        val relationshipChildrenArray = context.resources.getStringArray(R.array.beneficiaryRelationshipChildren)
        val relationshipParentArray = context.resources.getStringArray(R.array.beneficiaryRelationshipParentsOrInLaws)
        val relationshipExtendedArray = context.resources.getStringArray(R.array.beneficiaryRelationshipExtendedFamily)

        return when {
            relationshipChildrenArray.filter { it == relationship }.isNotEmpty() -> context.getString(R.string.children)
            relationshipParentArray.filter { it == relationship }.isNotEmpty() -> context.getString(R.string.parents_in_laws)
            relationshipExtendedArray.filter { it == relationship }.isNotEmpty() -> context.getString(R.string.extended_family)
            else -> context.getString(R.string.spouse)
        }
    }

    fun buildSelectorOptionsFromCategory(context: Context, category: String): SelectorList<StringItem> {
        val relationshipChildrenArray = context.resources.getStringArray(R.array.beneficiaryRelationshipChildren)
        val relationshipParentArray = context.resources.getStringArray(R.array.beneficiaryRelationshipParentsOrInLaws)
        val relationshipExtendedArray = context.resources.getStringArray(R.array.beneficiaryRelationshipExtendedFamily)
        return when {
            context.getString(R.string.children).equals(category, ignoreCase = true) -> buildSelectorOptionsFromArray(relationshipChildrenArray)
            context.getString(R.string.parents_in_laws).equals(category, ignoreCase = true) -> buildSelectorOptionsFromArray(relationshipParentArray)
            context.getString(R.string.extended_family).equals(category, ignoreCase = true) -> buildSelectorOptionsFromArray(relationshipExtendedArray)
            context.getString(R.string.spouse).equals(category, ignoreCase = true) -> buildSelectorOptionsFromArguments(context.getString(R.string.spouse))
            else -> SelectorList()
        }
    }

    fun buildSelectorOptionsFromArguments(vararg options: String): SelectorList<StringItem> {
        val answerList = SelectorList<StringItem>()
        options.forEach {
            answerList.add(StringItem(it))
        }
        return answerList
    }

    fun buildSelectorOptionsFromArray(options: Array<String>): SelectorList<StringItem> {
        val answerList = SelectorList<StringItem>()
        options.forEach {
            answerList.add(StringItem(it))
        }
        return answerList
    }

    fun buildSelectorOptionsFromList(options: List<LookupItem>): SelectorList<LookupItem> {
        val answerList = SelectorList<LookupItem>()
        options.forEach {
            answerList.add(it)
        }
        return answerList
    }

    fun buildSuburbOptionsFromList(options: List<SuburbResult>): SelectorList<SuburbItem> {
        val answerList = SelectorList<SuburbItem>()
        options.forEach {
            answerList.add(SuburbItem(it))
        }
        return answerList
    }

    fun sortPolicyBeneficiaries(policyBeneficiaries: List<PolicyBeneficiary>): List<PolicyBeneficiary> {
        return policyBeneficiaries.sortedWith { firstBeneficiary, secondBeneficiary ->
            val firstBeneficiaryFullName = "${firstBeneficiary.firstName} ${firstBeneficiary.surname}"
            val secondBeneficiaryFullName = "${secondBeneficiary.firstName} ${secondBeneficiary.surname}"
            firstBeneficiaryFullName.compareTo(secondBeneficiaryFullName, ignoreCase = true)
        }
    }

    fun buildPercentageSelectorOptions(): SelectorList<StringItem> {
        val answerList = SelectorList<StringItem>()
        for (i in 10..100 step 10) {
            answerList.add(StringItem("$i%"))
        }
        return answerList
    }

    fun getMatchingLookupIndex(keyword: String, lookupItems: List<LookupItem>): Int {
        return lookupItems.indexOfFirst { it.itemCode == keyword }
    }

    fun getMatchingLookupItem(keyword: String, lookupItems: List<LookupItem>): LookupItem? {
        return lookupItems.find { it.itemCode.equals(keyword, true) || it.defaultLabel.equals(keyword, true) }
    }

    fun getMatchingExergyCodesDetails(keyword: String, exergyList: List<ExergyCodesDetails>): ExergyCodesDetails {
        return if (keyword.equals(BMBApplication.getInstance().topMostActivity.getString(R.string.spouse))) {
            exergyList.find { it.code.contains(keyword, true) || it.description.contains(keyword, true) } ?: ExergyCodesDetails()
        } else {
            exergyList.find { it.code.equals(keyword, true) || it.description.equals(keyword, true) } ?: ExergyCodesDetails()
        }
    }

    class SuburbItem(val suburbResult: SuburbResult) : SelectorInterface {

        override val displayValue: String?
            get() = suburbResult.suburb + ", " + suburbResult.townOrCity + ", " + suburbResult.streetPostalCode

        override val displayValueLine2: String?
            get() = ""

    }

    fun buildFamilyMembersGenderMappings(context: Context): Map<String, String> {
        val maleCode = "M"
        val femaleCode = "F"
        return mapOf(context.getString(R.string.flexi_funeral_son) to maleCode,
                context.getString(R.string.flexi_funeral_daughter) to femaleCode,
                context.getString(R.string.flexi_funeral_father) to maleCode,
                context.getString(R.string.flexi_funeral_mother) to femaleCode,
                context.getString(R.string.flexi_funeral_brother) to maleCode,
                context.getString(R.string.flexi_funeral_sister) to femaleCode,
                context.getString(R.string.flexi_funeral_uncle) to maleCode,
                context.getString(R.string.flexi_funeral_aunt) to femaleCode,
                context.getString(R.string.flexi_funeral_nephew) to maleCode,
                context.getString(R.string.flexi_funeral_niece) to femaleCode,
                context.getString(R.string.flexi_funeral_grandfather) to maleCode,
                context.getString(R.string.flexi_funeral_grandmother) to femaleCode,
                context.getString(R.string.flexi_funeral_father_in_law) to maleCode,
                context.getString(R.string.flexi_funeral_mother_in_law) to femaleCode,
                context.getString(R.string.flexi_funeral_male) to maleCode,
                context.getString(R.string.flexi_funeral_female) to femaleCode)
    }

    fun buildFamilyMembersRelationshipMappings(context: Context): Map<String, String> = mapOf(context.getString(R.string.flexi_funeral_son) to "SON",
            context.getString(R.string.flexi_funeral_daughter) to "DAUGHTER",
            context.getString(R.string.flexi_funeral_mother) to "MOTHER",
            context.getString(R.string.flexi_funeral_father) to "FATHER",
            context.getString(R.string.flexi_funeral_brother) to "BROTHER",
            context.getString(R.string.flexi_funeral_sister) to "SISTER",
            context.getString(R.string.flexi_funeral_uncle) to "UNCLE",
            context.getString(R.string.flexi_funeral_aunt) to "AUNT",
            context.getString(R.string.flexi_funeral_nephew) to "NEPHEW",
            context.getString(R.string.flexi_funeral_niece) to "NIECE",
            context.getString(R.string.flexi_funeral_cousin) to "COUSIN",
            context.getString(R.string.flexi_funeral_grandmother) to "GRAND_MOTHER",
            context.getString(R.string.flexi_funeral_grandfather) to "GRAND_FATHER",
            context.getString(R.string.flexi_funeral_mother_in_law) to "MOTHER_IN_LAW",
            context.getString(R.string.flexi_funeral_father_in_law) to "FATHER_IN_LAW",
            context.getString(R.string.flexi_funeral_gardener_or_caretaker) to "GARDERNER_OR_CARETAKER",
            context.getString(R.string.flexi_funeral_spouse) to "SPOUSE")
}