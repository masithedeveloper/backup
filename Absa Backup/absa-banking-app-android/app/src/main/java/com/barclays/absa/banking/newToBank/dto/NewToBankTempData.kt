/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.newToBank.dto

import com.barclays.absa.banking.boundary.docHandler.dto.DocHandlerGetCaseByIdResponse
import com.barclays.absa.banking.newToBank.services.dto.*
import com.barclays.absa.banking.relationshipBanking.services.dto.BusinessBankingBranchesSelector
import com.barclays.absa.banking.relationshipBanking.services.dto.BusinessCustomerPortfolio
import com.barclays.absa.banking.relationshipBanking.services.dto.SicCodesLookupDetailsSelector
import styleguide.forms.SelectorList

class NewToBankTempData(var flexiPackage: CardPackage? = null,
                        var goldPackage: CardPackage = CardPackage(),
                        var premiumPackage: CardPackage = CardPackage(),
                        var businessEvolveStandardPackage: BusinessEvolveCardPackageResponse = BusinessEvolveCardPackageResponse(),
                        var businessEvolveIslamicPackage: BusinessEvolveCardPackageResponse = BusinessEvolveCardPackageResponse(),
                        var selectedPackage: CardPackage = CardPackage(),
                        var selectedBusinessEvolvePackage: BusinessEvolveCardPackageResponse = BusinessEvolveCardPackageResponse(),
                        var selectedBusinessEvolveProduct: BusinessEvolveCardPackageResponse.Products = BusinessEvolveCardPackageResponse.Products(),
                        var customerDetails: CustomerDetails = CustomerDetails(),
                        var registrationDetails: RegistrationDetails = RegistrationDetails(),
                        var debitDay: String = "",
                        var agreeAbsaRewards: Boolean = false,
                        var productType: String = "",
                        var docHandlerGetCaseByIdResponse: DocHandlerGetCaseByIdResponse? = null,
                        var proofOfResidenceInfo: ProofOfResidenceResponse? = null,
                        var selectedMonthlyIncomeCode: String = "",
                        var rewardsAmount: String? = "",
                        var rewardsDateDeadline: String? = "2019-08-24",
                        var rewardsDateValid: Boolean = false,
                        var addressLookupDetails: PerformAddressLookup? = null,
                        var newToBankIncomeDetails: NewToBankIncomeDetails? = NewToBankIncomeDetails(),
                        var photoMatchAndMobileLookupResponse: PerformPhotoMatchAndMobileLookupDTO? = null,
                        var sourceOfFundsList: SelectorList<CodesLookupDetailsSelector> = SelectorList(),
                        var sourceOfIncomeList: SelectorList<CodesLookupDetailsSelector> = SelectorList(),
                        var residentialStatus: String = "",
                        var marketPropertyValue: String = "0",
                        var residentialAddressSince: String = "",
                        var occupationCodeList: SelectorList<CodesLookupDetailsSelector> = SelectorList(),
                        var employmentStatusList: SelectorList<CodesLookupDetailsSelector> = SelectorList(),
                        var monthlyIncomeRangeList: SelectorList<CodesLookupDetailsSelector>? = null,
                        var nationalityList: SelectorList<CodesLookupDetailsSelector>? = null,
                        var countryOfBirthList: SelectorList<CodesLookupDetailsSelector>? = null,
                        var medicalOccupationList: SelectorList<CodesLookupDetailsSelector> = SelectorList(),
                        var residentialStatusList: SelectorList<CodesLookupDetailsSelector>? = null,
                        var occupationLevelList: SelectorList<CodesLookupDetailsSelector>? = null,
                        var employmentSectorList: SelectorList<CodesLookupDetailsSelector>? = null,
                        var listOfBranches: List<SiteFilteredDetailsVO>? = null,
                        var inBranchInfo: InBranchInfo = InBranchInfo(),
                        var rewardsInfo: AbsaRewardsResponse = AbsaRewardsResponse(),
                        var addressDetails: AddressDetails = AddressDetails(),
                        var sourceOfFundsCode: String = "",
                        var totalMonthlyIncome: String = "",
                        var totalMonthlyExpense: String = "",
                        var absaRewardsExist: Boolean = false,
                        var sicCodesLookUpDetailsList: SelectorList<SicCodesLookupDetailsSelector> = SelectorList(),
                        var categorySicCodeDetailList: SelectorList<SicCodesLookupDetailsSelector> = SelectorList(),
                        var businessBankingSiteCodeDetails: SelectorList<BusinessBankingBranchesSelector> = SelectorList(),
                        var businessCustomerPortfolio: BusinessCustomerPortfolio = BusinessCustomerPortfolio(),
                        var useSelfie: Boolean = false,
                        var shortAndMediumTermFundingRewardsValueAddedService : String = "",
                        var assetFinanceRewardsValueAddedService: String = "",
                        var makeAndReceivePaymentsRewardsValueAddedService3: String = "",
                        var savingAndInvestmentProductsRewardsValueAddedService4: String = "")
