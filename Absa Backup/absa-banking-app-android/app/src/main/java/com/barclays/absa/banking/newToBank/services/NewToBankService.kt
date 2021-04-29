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

package com.barclays.absa.banking.newToBank.services

import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.newToBank.dto.CustomerPortfolioInfo
import com.barclays.absa.banking.newToBank.services.dto.*
import com.barclays.absa.banking.relationshipBanking.services.dto.BusinessBankingSiteDetailsResponse
import com.barclays.absa.banking.relationshipBanking.services.dto.BusinessCustomerPortfolio
import com.barclays.absa.banking.relationshipBanking.services.dto.SicCodesResponse

interface NewToBankService {

    fun fetchPremiumBankingAccount(premiumBankingAccountExtendedResponseListener: ExtendedResponseListener<CardPackageResponse>)
    fun fetchGoldBankingAccount(goldBankingAccountExtendedResponseListener: ExtendedResponseListener<CardPackageResponse>)
    fun fetchFlexiBankingAccount(flexiBankingAccountExtendedResponseListener: ExtendedResponseListener<CardPackageResponse>)
    fun fetchBusinessEvolveIslamicAccount(businessEvolveIslamicExtendedResponseListener: ExtendedResponseListener<BusinessEvolveCardPackageResponse>)
    fun fetchBusinessEvolveStandardAccount(businessEvolveStandardAccountExtendedResponseListener: ExtendedResponseListener<BusinessEvolveCardPackageResponse>)
    fun fetchBusinessEvolveOptionalExtras(businessEvolveOptionalExtrasExtendedResponseListener: ExtendedResponseListener<BusinessEvolveOptionalExtrasResponse>)
    fun fetchProofOfResidenceInfo(proofOfResidenceResponseExtendedResponseListener: ExtendedResponseListener<ProofOfResidenceResponse>)
    fun fetchAbsaRewards(isFree: Boolean, absaRewardsResponseExtendedResponseListener: ExtendedResponseListener<AbsaRewardsResponse>)
    fun performPostalCodeLookup(postalCode: String, area: String, postalCodeLookupResponseExtendedResponseListener: ExtendedResponseListener<PostalCodeLookupResponse>)
    fun performAddressLookup(performAddressLookupResponseExtendedResponseListener: ExtendedResponseListener<AddressLookupResponse>)
    fun performGetAllConfigsForApplication(configsForApplicationResponseExtendedResponseListener: ExtendedResponseListener<GetAllConfigsForApplicationResponse>)
    fun performValidateCustomerAndCreateCase(userDetail: CustomerDetails, validateCustomerAndCreateCaseResponseExtendedResponseListener: ExtendedResponseListener<ValidateCustomerAndCreateCaseResponse>)
    fun performCasaScreening(nationality: String, casaScreeningResponseExtendedResponseListener: ExtendedResponseListener<CasaScreeningResponse>)
    fun performPhotoMatchAndMobileLookup(photoMatchAndMobileLookUpResponseExtendedResponseListener: ExtendedResponseListener<PhotoMatchAndMobileLookUpResponse>)
    fun performValidateCustomerAddress(addressDetails: AddressDetails, validateCustomerAddressResponseExtendedResponseListener: ExtendedResponseListener<ValidateCustomerAddressResponse>)
    fun performKeepAlive(keepAliveResponseExtendedResponseListener: ExtendedResponseListener<NewToBankKeepAliveResponse>)
    fun performGetScoringStatus(getScoringStatusResponseExtendedResponseListener: ExtendedResponseListener<GetScoringStatusResponse>)
    fun performGetFilteredSiteDetails(searchValue: String, filteredSiteDetailsResponseExtendedResponseListener: ExtendedResponseListener<GetFilteredSiteDetailsResponse>)
    fun performCreateCombiCardAccount(createCombiDetails: CreateCombiDetails, createCombiCardAccountResponseExtendedResponseListener: ExtendedResponseListener<CreateCombiCardAccountResponse>)
    fun performRetrieveIdOcrDetailsFromDocument(documentType: IdDocumentType, idNumber: String, retrieveIdOcrDetailsFromDocumentResponseExtendedResponseListener: ExtendedResponseListener<RetrieveIdOcrDetailsFromDocumentResponse>)
    fun performExpressPreLogonRequestSecurityNotificationNewToBank(cellNumber: String, expressPreLogonRequestSecurityNotificationResponseExtendedResponseListener: ExtendedResponseListener<ExpressPreLogonRequestSecurityNotificationResponse>)
    fun performExpressPreLogonValidateSecurityNotificationNewToBank(cellNumber: String, expressPreLogonValidateSecurityNotificationResponseExtendedResponseListener: ExtendedResponseListener<ExpressPreLogonValidateSecurityNotificationResponse>)
    fun performExpressPreLogonValidateSecurityNotificationNewToBank(cellNumber: String, TVN: String, expressPreLogonValidateSecurityNotificationResponseExtendedResponseListener: ExtendedResponseListener<ExpressPreLogonValidateSecurityNotificationResponse>)
    fun performExpressPreLogonResendSecurityNotificationNewToBank(cellNumber: String, expressPreLogonResendSecurityNotificationResponseExtendedResponseListener: ExtendedResponseListener<ExpressPreLogonResendSecurityNotificationResponse>)
    fun performCreateCustomerPortfolio(customerPortfolioInfo: CustomerPortfolioInfo, customerPortfolioAccountResponseExtendedResponseListener: ExtendedResponseListener<CreateCustomerPortfolioAccountResponse>)
    fun performCasaRiskProfiling(customerPortfolioInfo: CustomerPortfolioInfo, performCasaRiskProfilingResponseExtendedResponseListener: ExtendedResponseListener<PerformCasaRiskProfilingResponse>)
    fun performRegistrationNewApplicationCustomer(idNumber: String, PIN: String, nVal: String, clientTypeGroup: String, registrationNewApplicationCustomerResponseExtendedResponseListener: ExtendedResponseListener<RegistrationNewApplicationCustomerResponse>)
    fun performRegisterOnlineBankingPassword(password: String, tokenNumber: String, nonce: String, clientTypeGroup: String, registerOnlineBankingPasswordResponseExtendedResponseListener: ExtendedResponseListener<RegisterOnlineBankingPasswordResponse>)
    fun fetchStudentAccountBundle(studentSilverAccountListener: ExtendedResponseListener<CardPackageResponse>)
    fun fetchSicCodes(extendedResponseListener: ExtendedResponseListener<SicCodesResponse>, searchField: String)
    fun fetchBusinessBankingBranches(getFilteredSiteDetailsExtendedResponseListener: ExtendedResponseListener<BusinessBankingSiteDetailsResponse>)
    fun performCreateBusinessCustomerPortfolio(customerPortfolioInfo: CustomerPortfolioInfo, businessCustomerPortfolio: BusinessCustomerPortfolio, customerPortfolioAccountResponseExtendedResponseListener: ExtendedResponseListener<CreateCustomerPortfolioAccountResponse>)

    companion object {
        const val OP2023_CREATE_CUSTOMER_PORTFOLIO = "OP2023"
        const val OP2025_CASA_SCREENING = "OP2025"
        const val OP2028_RETRIEVE_ID_OCR_DETAILS_FROM_DOCUMENT = "OP2028"
        const val OP2031_ADDRESS_LOOKUP = "OP2031"
        const val OP2032_VALIDATE_CUSTOMER_ADDRESS = "OP2032"
        const val OP2033_POSTAL_CODE_LOOKUP = "OP2033"
        const val OP2034_VALIDATE_CUSTOMER_AND_CREATE_CASE = "OP2034"
        const val OP2035_GET_ALL_CONFIGS_FOR_APPLICATION = "OP2035"
        const val OP2036_GET_SCORING_STATUS = "OP2036"
        const val OP2037_NEW_TO_BANK_KEEP_ALIVE = "OP2037"
        const val OP2038_PHOTO_MATCH_AND_MOBILE_LOOKUP = "OP2038"
        const val OP2040_GET_FILTERED_SITE_DETAILS = "OP2040"
        const val OP2041_CREATE_COMBI_CARD_ACCOUNT = "OP2041"
        const val OP2044_PRE_LOGON_VALIDATE_SECURITY_NOTIFICATION = "OP2044"
        const val OP2045_PRE_LOGON_REQUEST_SECURITY_NOTIFICATION = "OP2045"
        const val OP2046_PRE_LOGON_RESEND_SECURITY_NOTIFICATION = "OP2046"
        const val OP2047_REGISTRATION_NEW_APPLICATION_CUSTOMER = "OP2047"
        const val OP2048_REGISTER_ONLINE_BANKING_PASSWORD = "OP2048"
        const val OP2084_CASA_RISK_PROFILING = "OP2084"
        const val OP2100_GET_SIC_CODES = "OP2100"
        const val OP2101_GET_BUSINESS_BANKING_SITE_DETAILS = "OP2101"
    }
}
