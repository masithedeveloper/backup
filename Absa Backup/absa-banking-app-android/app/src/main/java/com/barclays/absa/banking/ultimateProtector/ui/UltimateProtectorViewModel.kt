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

package com.barclays.absa.banking.ultimateProtector.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.funeralCover.services.dto.RetailAccount
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.barclays.absa.banking.ultimateProtector.services.LifeCoverInteractor
import com.barclays.absa.banking.ultimateProtector.services.LifeCoverService
import com.barclays.absa.banking.ultimateProtector.services.dto.*
import styleguide.forms.SelectorInterface
import styleguide.forms.SelectorList
import styleguide.forms.StringItem

class UltimateProtectorViewModel : BaseViewModel() {
    var ultimateProtectorInfo = LifeCoverInfo()
    lateinit var ultimateProtectorData: UltimateProtectorData
    var coverQuotationLiveData: MutableLiveData<Quotation> = MutableLiveData()
    var retailAccountsLiveData: MutableLiveData<List<RetailAccount>> = MutableLiveData()
    var lifeCoverApplicationLiveData = MutableLiveData<LifeCoverApplicationResult>()

    val titleMap = HashMap<String, String>()
    val relationshipMap = HashMap<String, String>()

    var lifeCoverService: LifeCoverService = LifeCoverInteractor()

    fun getCoverQuotation() {
        if (coverQuotationLiveData.value == null) {
            val benefitCode = getBenefitCode()
            lifeCoverService.fetchLifeCoverQuotation(benefitCode, QuotationExtendedResponseListener(coverQuotationLiveData))
            ultimateProtectorInfo.benefitCode = benefitCode
        }
    }

    fun loadRetailAccounts() {
        if (retailAccountsLiveData.value == null) {
            lifeCoverService.fetchRetailAccount(RetailAccountsExtendedResponseListener(retailAccountsLiveData))
        }
    }

    fun buildTitles(titles: Array<String>) {
        val codes = arrayOf("03", "01", "02", "21")
        for (index in codes.indices) {
            titleMap[titles[index]] = codes[index]
        }
    }

    fun buildRelationships(relationships: Array<String>) {
        val codes = arrayOf("SON", "DAUG", "SPEC", "STUD", "FATH", "MOTH", "SFAI", "SMOT", "FAIL", "MOIL", "GRAN", "BROT", "SIST", "EBRO", "ESIS", "UNC", "AUNT", "NEPH", "NIEC", "SPOU", "COUS", "OTHE")
        for (index in codes.indices) {
            relationshipMap[relationships[index]] = codes[index]
        }
    }

    fun applyForLifeCover(callType: CallType) {
        lifeCoverService.applyForLifeCover(ultimateProtectorInfo, LifeCoverApplicationExtendedResponseListener(lifeCoverApplicationLiveData), callType)
    }

    fun getMatchingPremium(coverAmount: Int): Premium {
        val premiums = coverQuotationLiveData.value?.sliderPremiums
        var matchingPremium = Premium()
        premiums?.forEach { premium ->
            if (premium.coverAmount?.toInt() == coverAmount) {
                matchingPremium = premium
            }
        }
        return matchingPremium
    }

    fun getCoverIndex(coverAmount: Int): Int? {
        return coverQuotationLiveData.value?.sliderPremiums?.indexOfFirst { it.coverAmount?.toInt() == coverAmount }
    }

    fun getBenefitCode(): String {
        val medicalQuestionOne = ultimateProtectorInfo.medicalQuestionOne
        val medicalQuestionTwo = ultimateProtectorInfo.medicalQuestionTwo
        val medicalQuestionThree = ultimateProtectorInfo.medicalQuestionThree

        return if ((medicalQuestionOne != null && !medicalQuestionOne)
                && (medicalQuestionTwo != null && !medicalQuestionTwo)
                && (medicalQuestionThree != null && !medicalQuestionThree)) "U01" else "U02"
    }

    fun buildAccountsSelectorList(accounts: List<RetailAccount>): SelectorList<RetailAccountItem> {
        val accountsSelectorList = SelectorList<RetailAccountItem>()
        accounts.forEach {
            accountsSelectorList.add(RetailAccountItem(it))
        }
        return accountsSelectorList
    }

    fun buildOptionsSelectorList(vararg options: String): SelectorList<StringItem> {
        val answerList = SelectorList<StringItem>()
        options.forEach {
            answerList.apply {
                add(StringItem(it))
            }
        }
        return answerList
    }

    fun buildOptionsSelectorListFromArray(options: Array<String>): SelectorList<StringItem> {
        val answerList = SelectorList<StringItem>()
        options.forEach {
            answerList.apply {
                add(StringItem(it))
            }
        }
        return answerList
    }

    fun shouldShowOccupation(itemCode: String?): Boolean {
        itemCode?.let {
            val occupationStatusBlackList = arrayOf("04", "07", "10", "05", "06")
            for (occupationStatus in occupationStatusBlackList) {
                if (occupationStatus == itemCode) {
                    return false
                }
            }
        }
        return true
    }

    class RetailAccountItem(private val retailAccount: RetailAccount) : SelectorInterface {

        override val displayValue: String?
            get() = retailAccount.accountDescription as String

        override val displayValueLine2: String?
            get() = retailAccount.accountNumber as String
    }
}