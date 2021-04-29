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

package com.barclays.absa.banking.relationshipBanking.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.barclays.absa.banking.newToBank.NewToBankStudentAccountExtendedResponseListener
import com.barclays.absa.banking.newToBank.dto.CustomerPortfolioInfo
import com.barclays.absa.banking.newToBank.services.NewToBankInteractor
import com.barclays.absa.banking.newToBank.services.dto.*
import com.barclays.absa.banking.relationshipBanking.services.dto.BusinessBankingBranchesSelector
import com.barclays.absa.banking.relationshipBanking.services.dto.BusinessCustomerPortfolio
import com.barclays.absa.banking.relationshipBanking.services.dto.SicCodesLookupDetailsSelector
import com.barclays.absa.banking.shared.services.dto.CodesLookupDetails
import styleguide.forms.SelectorList
import styleguide.forms.StringItem

class NewToBankBusinessAccountViewModel : ViewModel() {
    private val newToBankService by lazy { NewToBankInteractor() }
    val filteredSiteLiveData by lazy { MutableLiveData<List<SiteFilteredDetailsVO>>() }
    val sicCodesListLiveData by lazy { MutableLiveData<List<CodesLookupDetails>>() }
    val categorySicCodeListLiveData by lazy { MutableLiveData<List<CodesLookupDetails>>() }
    val scoringStatusLiveData by lazy { MutableLiveData<GetScoringStatusResponse>() }
    val casaScreeningTransactionStatus: MutableLiveData<String> = MutableLiveData()
    val validateAddressTransactionMessage: MutableLiveData<String> = MutableLiveData()
    val businessCustomerPortfolioTransactionResponse: MutableLiveData<CreateCustomerPortfolioAccountResponse> = MutableLiveData()
    val scoringPollingStatus: MutableLiveData<Boolean> = MutableLiveData()
    val scoringFailureStatus: MutableLiveData<Boolean> = MutableLiveData()
    val studentAccountLiveData by lazy { MutableLiveData<CardPackageResponse>() }

    private val businessBankingExtendedResponseListener by lazy { FilteredSiteDetailsListener(filteredSiteLiveData) }
    private val businessCustomerPortfolioResponseListener by lazy { BusinessCustomerPortfolioResponseListener(businessCustomerPortfolioTransactionResponse) }
    private val performScoringResponseListener by lazy { GetScoringResponseListener(scoringStatusLiveData, scoringPollingStatus, scoringFailureStatus) }
    private val validateCustomerAddressResponseExtendedResponseListener by lazy { ValidateCustomerAddressResponseListener(validateAddressTransactionMessage) }
    private val performCasaScreeningResponseListener by lazy { CasaScreeningResponseListener(casaScreeningTransactionStatus) }
    private val fetchSicCodesListResponseListener by lazy { SicCodesResponseListener(sicCodesListLiveData) }
    private val fetchCategorySicCodesListResponseListener by lazy { SicCodesResponseListener(categorySicCodeListLiveData) }
    private val studentAccountExtendedResponseListener by lazy { NewToBankStudentAccountExtendedResponseListener(studentAccountLiveData) }

    val accountTypes: SelectorList<StringItem>
        get() = SelectorList<StringItem>().apply { AccountType.accountTypes.forEach { add(StringItem(it)) } }

    fun fetchBusinessBankingBranches() {
        newToBankService.fetchBusinessBankingBranches(businessBankingExtendedResponseListener)
    }

    fun buildBranchesSelectorList(branches: List<SiteFilteredDetailsVO>): SelectorList<BusinessBankingBranchesSelector> {
        val businessBankingSelectorList = SelectorList<BusinessBankingBranchesSelector>()
        branches.forEach {
            businessBankingSelectorList.add(BusinessBankingBranchesSelector(it))
        }
        return businessBankingSelectorList
    }

    fun buildSicCodesSelectorList(codesLookupDetails: List<CodesLookupDetails>?): SelectorList<SicCodesLookupDetailsSelector> {
        val sicCodesList = SelectorList<SicCodesLookupDetailsSelector>()
        codesLookupDetails?.forEach {
            sicCodesList.add(SicCodesLookupDetailsSelector(it.engCodeDescription, it.itemCode, it.codesLookupType))
        }
        return sicCodesList
    }

    fun submitBusinessBankingApplication(customerPortfolio: CustomerPortfolioInfo, businessCustomerPortfolio: BusinessCustomerPortfolio) {
        newToBankService.performCreateBusinessCustomerPortfolio(customerPortfolio, businessCustomerPortfolio, businessCustomerPortfolioResponseListener)
    }

    fun validateCustomerBusinessAddress(addressDetails: AddressDetails) {
        addressDetails.addressType = "PHYSICAL_ADDRESS"
        newToBankService.performValidateCustomerAddress(addressDetails, validateCustomerAddressResponseExtendedResponseListener)
    }

    fun fetchSicCodesList(searchField: String): LiveData<List<CodesLookupDetails>> {
        newToBankService.fetchSicCodes(fetchSicCodesListResponseListener, searchField.toUpperCase())
        return sicCodesListLiveData
    }

    fun fetchCategorySicCodesList(searchField: String): LiveData<List<CodesLookupDetails>> {
        newToBankService.fetchSicCodes(fetchCategorySicCodesListResponseListener, searchField.toUpperCase())
        return categorySicCodeListLiveData
    }

    fun performCasaScreening(nationalityCode: String) {
        newToBankService.performCasaScreening(nationalityCode, performCasaScreeningResponseListener)
    }

    fun performScoring() {
        newToBankService.performGetScoringStatus(performScoringResponseListener)
    }

    fun fetchStudentAccountBundle() {
        newToBankService.fetchStudentAccountBundle(studentAccountExtendedResponseListener)
    }
}