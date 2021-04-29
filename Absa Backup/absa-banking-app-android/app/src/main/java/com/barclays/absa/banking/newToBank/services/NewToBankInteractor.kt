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

import com.barclays.absa.banking.framework.AbstractInteractor
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.newToBank.dto.CustomerPortfolioInfo
import com.barclays.absa.banking.newToBank.services.dto.*
import com.barclays.absa.banking.relationshipBanking.services.BusinessBankingCreateCustomerPortfolioRequest
import com.barclays.absa.banking.relationshipBanking.services.NewToBankGetBusinessBankingSiteDetailsRequest
import com.barclays.absa.banking.relationshipBanking.services.NewToBankGetSicCodesRequest
import com.barclays.absa.banking.relationshipBanking.services.dto.BusinessBankingSiteDetailsResponse
import com.barclays.absa.banking.relationshipBanking.services.dto.BusinessCustomerPortfolio
import com.barclays.absa.banking.relationshipBanking.services.dto.SicCodesResponse


class NewToBankInteractor : AbstractInteractor(), NewToBankService {

    override fun fetchPremiumBankingAccount(premiumBankingAccountExtendedResponseListener: ExtendedResponseListener<CardPackageResponse>) {
        val newToBankPremiumBankingRequest = NewToBankPremiumBankingRequest(premiumBankingAccountExtendedResponseListener)
        submitRequest(newToBankPremiumBankingRequest)
    }

    override fun fetchGoldBankingAccount(goldBankingAccountExtendedResponseListener: ExtendedResponseListener<CardPackageResponse>) {
        val newToBankGoldBankingRequest = NewToBankGoldBankingRequest(goldBankingAccountExtendedResponseListener)
        submitRequest(newToBankGoldBankingRequest)
    }

    override fun fetchFlexiBankingAccount(flexiBankingAccountExtendedResponseListener: ExtendedResponseListener<CardPackageResponse>) {
        val newToBankFlexiBankingRequest = NewToBankFlexiBankingRequest(flexiBankingAccountExtendedResponseListener)
        submitRequest(newToBankFlexiBankingRequest)
    }

    override fun fetchBusinessEvolveIslamicAccount(businessEvolveIslamicExtendedResponseListener: ExtendedResponseListener<BusinessEvolveCardPackageResponse>) {
        val newToBankBusinessEvolveIslamicRequest = NewToBankBusinessEvolveIslamicRequest(businessEvolveIslamicExtendedResponseListener)
        submitRequest(newToBankBusinessEvolveIslamicRequest)
    }

    override fun fetchBusinessEvolveStandardAccount(businessEvolveStandardAccountExtendedResponseListener: ExtendedResponseListener<BusinessEvolveCardPackageResponse>) {
        val newToBankBusinessEvolveStandardRequest = NewToBankBusinessEvolveStandardRequest(businessEvolveStandardAccountExtendedResponseListener)
        submitRequest(newToBankBusinessEvolveStandardRequest)
    }

    override fun fetchBusinessEvolveOptionalExtras(businessEvolveOptionalExtrasExtendedResponseListener: ExtendedResponseListener<BusinessEvolveOptionalExtrasResponse>) {
        val newToBankBusinessEvolveOptionalExtrasRequest = NewToBankBusinessEvolveOptionalExtrasRequest(businessEvolveOptionalExtrasExtendedResponseListener)
        submitRequest(newToBankBusinessEvolveOptionalExtrasRequest)
    }

    override fun fetchAbsaRewards(isFree: Boolean, absaRewardsResponseExtendedResponseListener: ExtendedResponseListener<AbsaRewardsResponse>) {
        val newToBankAbsaRewardsRequest = NewToBankAbsaRewardsRequest(isFree, absaRewardsResponseExtendedResponseListener)
        submitRequest(newToBankAbsaRewardsRequest)
    }

    override fun fetchProofOfResidenceInfo(proofOfResidenceResponseExtendedResponseListener: ExtendedResponseListener<ProofOfResidenceResponse>) {
        val newToBankProofOfResidenceRequest = NewToBankProofOfResidenceRequest(proofOfResidenceResponseExtendedResponseListener)
        submitRequest(newToBankProofOfResidenceRequest)
    }

    override fun performPostalCodeLookup(postalCode: String, area: String, postalCodeLookupResponseExtendedResponseListener: ExtendedResponseListener<PostalCodeLookupResponse>) {
        val postalCodeLookupRequest = NewToBankPostalCodeLookupRequest(postalCode, area, postalCodeLookupResponseExtendedResponseListener)
        submitRequest(postalCodeLookupRequest)
    }

    override fun performAddressLookup(performAddressLookupResponseExtendedResponseListener: ExtendedResponseListener<AddressLookupResponse>) {
        val newToBankAddressLookupRequest = NewToBankAddressLookupRequest(performAddressLookupResponseExtendedResponseListener)
        submitRequest(newToBankAddressLookupRequest)
    }

    override fun performGetAllConfigsForApplication(configsForApplicationResponseExtendedResponseListener: ExtendedResponseListener<GetAllConfigsForApplicationResponse>) {
        val getAllConfigsForApplicationRequest = NewToBankGetAllConfigsForApplicationRequest(configsForApplicationResponseExtendedResponseListener)
        submitRequest(getAllConfigsForApplicationRequest)
    }

    override fun performValidateCustomerAndCreateCase(userDetail: CustomerDetails, validateCustomerAndCreateCaseResponseExtendedResponseListener: ExtendedResponseListener<ValidateCustomerAndCreateCaseResponse>) {
        val validateCustomerAndCreateCaseRequest = NewToBankValidateCustomerAndCreateCaseRequest(userDetail, validateCustomerAndCreateCaseResponseExtendedResponseListener)
        submitRequest(validateCustomerAndCreateCaseRequest)
    }

    override fun performCasaScreening(nationality: String, casaScreeningResponseExtendedResponseListener: ExtendedResponseListener<CasaScreeningResponse>) {
        val casaScreeningRequest = NewToBankCasaScreeningRequest(nationality, casaScreeningResponseExtendedResponseListener)
        submitRequest(casaScreeningRequest)
    }

    override fun performPhotoMatchAndMobileLookup(photoMatchAndMobileLookUpResponseExtendedResponseListener: ExtendedResponseListener<PhotoMatchAndMobileLookUpResponse>) {
        val photoMatchAndMobileLookupRequest = NewToBankPhotoMatchAndMobileLookupRequest(photoMatchAndMobileLookUpResponseExtendedResponseListener)
        submitRequest(photoMatchAndMobileLookupRequest)
    }

    override fun performValidateCustomerAddress(addressDetails: AddressDetails, validateCustomerAddressResponseExtendedResponseListener: ExtendedResponseListener<ValidateCustomerAddressResponse>) {
        val validateCustomerAddressRequest = NewToBankValidateCustomerAddressRequest(addressDetails, validateCustomerAddressResponseExtendedResponseListener)
        submitRequest(validateCustomerAddressRequest)
    }

    override fun performKeepAlive(keepAliveResponseExtendedResponseListener: ExtendedResponseListener<NewToBankKeepAliveResponse>) {
        val keepAliveRequest = NewToBankKeepAliveRequest(keepAliveResponseExtendedResponseListener)
        submitRequest(keepAliveRequest)
    }

    override fun performGetScoringStatus(getScoringStatusResponseExtendedResponseListener: ExtendedResponseListener<GetScoringStatusResponse>) {
        val getScoringStatusRequest = NewToBankGetScoringStatusRequest(getScoringStatusResponseExtendedResponseListener)
        submitRequest(getScoringStatusRequest)
    }

    override fun performGetFilteredSiteDetails(searchValue: String, filteredSiteDetailsResponseExtendedResponseListener: ExtendedResponseListener<GetFilteredSiteDetailsResponse>) {
        val getFilteredSiteDetailsRequest = NewToBankGetFilteredSiteDetailsRequest(searchValue, filteredSiteDetailsResponseExtendedResponseListener)
        submitRequest(getFilteredSiteDetailsRequest)
    }

    override fun performCreateCombiCardAccount(createCombiDetails: CreateCombiDetails, createCombiCardAccountResponseExtendedResponseListener: ExtendedResponseListener<CreateCombiCardAccountResponse>) {
        val createCombiCardAccountRequest = NewToBankCreateCombiCardAccountRequest(createCombiDetails, createCombiCardAccountResponseExtendedResponseListener)
        submitRequest(createCombiCardAccountRequest)
    }

    override fun performRetrieveIdOcrDetailsFromDocument(documentType: IdDocumentType, idNumber: String, retrieveIdOcrDetailsFromDocumentResponseExtendedResponseListener: ExtendedResponseListener<RetrieveIdOcrDetailsFromDocumentResponse>) {
        val retrieveIdOcrDetailsFromDocumentRequest = NewToBankRetrieveIdOcrDetailsFromDocumentRequest(documentType, idNumber, retrieveIdOcrDetailsFromDocumentResponseExtendedResponseListener)
        submitRequest(retrieveIdOcrDetailsFromDocumentRequest)
    }

    override fun performExpressPreLogonRequestSecurityNotificationNewToBank(cellNumber: String, expressPreLogonRequestSecurityNotificationResponseExtendedResponseListener: ExtendedResponseListener<ExpressPreLogonRequestSecurityNotificationResponse>) {
        val requestSecurityNotificationRequest = ExpressPreLogonRequestSecurityNotificationRequest(cellNumber, "verifyNewToBankCustomer", expressPreLogonRequestSecurityNotificationResponseExtendedResponseListener)
        submitRequest(requestSecurityNotificationRequest)
    }

    override fun performExpressPreLogonValidateSecurityNotificationNewToBank(cellNumber: String, expressPreLogonValidateSecurityNotificationResponseExtendedResponseListener: ExtendedResponseListener<ExpressPreLogonValidateSecurityNotificationResponse>) {
        val requestSecurityNotificationRequest = ExpressPreLogonValidateSecurityNotificationRequest(cellNumber, "verifyNewToBankCustomer", expressPreLogonValidateSecurityNotificationResponseExtendedResponseListener)
        submitRequest(requestSecurityNotificationRequest)
    }

    override fun performExpressPreLogonValidateSecurityNotificationNewToBank(cellNumber: String, TVN: String, expressPreLogonValidateSecurityNotificationResponseExtendedResponseListener: ExtendedResponseListener<ExpressPreLogonValidateSecurityNotificationResponse>) {
        val requestSecurityNotificationRequest = ExpressPreLogonValidateSecurityNotificationRequest(cellNumber, "verifyNewToBankCustomer", TVN, expressPreLogonValidateSecurityNotificationResponseExtendedResponseListener)
        submitRequest(requestSecurityNotificationRequest)
    }

    override fun performExpressPreLogonResendSecurityNotificationNewToBank(cellNumber: String, expressPreLogonResendSecurityNotificationResponseExtendedResponseListener: ExtendedResponseListener<ExpressPreLogonResendSecurityNotificationResponse>) {
        val requestSecurityNotificationRequest = ExpressPreLogonResendSecurityNotificationRequest(cellNumber, "verifyNewToBankCustomer", expressPreLogonResendSecurityNotificationResponseExtendedResponseListener)
        submitRequest(requestSecurityNotificationRequest)
    }

    override fun performCreateCustomerPortfolio(customerPortfolioInfo: CustomerPortfolioInfo, customerPortfolioAccountResponseExtendedResponseListener: ExtendedResponseListener<CreateCustomerPortfolioAccountResponse>) {
        val createCustomerPortfolioAccountRequest = NewToBankCreateCustomerPortfolioAccountRequest(customerPortfolioInfo, customerPortfolioAccountResponseExtendedResponseListener)
        submitRequest(createCustomerPortfolioAccountRequest)
    }

    override fun performCasaRiskProfiling(customerPortfolioInfo: CustomerPortfolioInfo, performCasaRiskProfilingResponseExtendedResponseListener: ExtendedResponseListener<PerformCasaRiskProfilingResponse>) {
        val performCasaRiskProfilingRequest = NewToBankPerformCasaRiskProfilingRequest(customerPortfolioInfo, performCasaRiskProfilingResponseExtendedResponseListener)
        submitRequest(performCasaRiskProfilingRequest)
    }

    override fun performRegistrationNewApplicationCustomer(idNumber: String, PIN: String, nVal: String, clientTypeGroup: String, registrationNewApplicationCustomerResponseExtendedResponseListener: ExtendedResponseListener<RegistrationNewApplicationCustomerResponse>) {
        val registrationNewApplicationCustomerRequest = NewToBankRegistrationNewApplicationCustomerRequest(idNumber, PIN, nVal, clientTypeGroup, registrationNewApplicationCustomerResponseExtendedResponseListener)
        submitRequest(registrationNewApplicationCustomerRequest)
    }

    override fun performRegisterOnlineBankingPassword(password: String, tokenNumber: String, nonce: String, clientTypeGroup: String, registerOnlineBankingPasswordResponseExtendedResponseListener: ExtendedResponseListener<RegisterOnlineBankingPasswordResponse>) {
        val registerOnlineBankingPasswordRequest = NewToBankRegisterOnlineBankingPasswordRequest(password, tokenNumber, nonce, clientTypeGroup, registerOnlineBankingPasswordResponseExtendedResponseListener)
        submitRequest(registerOnlineBankingPasswordRequest)
    }

    override fun fetchStudentAccountBundle(studentSilverAccountListener: ExtendedResponseListener<CardPackageResponse>) {
        submitRequest(NewToBankStudentSilverRequest(studentSilverAccountListener))
    }

    override fun fetchSicCodes(extendedResponseListener: ExtendedResponseListener<SicCodesResponse>, searchField: String) {
        val newToBankGetSicCodesRequest = NewToBankGetSicCodesRequest(extendedResponseListener, searchField)
        submitRequest(newToBankGetSicCodesRequest)
    }

    override fun fetchBusinessBankingBranches(getFilteredSiteDetailsExtendedResponseListener: ExtendedResponseListener<BusinessBankingSiteDetailsResponse>) {
        val getBusinessBankingSiteDetailsRequest = NewToBankGetBusinessBankingSiteDetailsRequest(getFilteredSiteDetailsExtendedResponseListener)
        submitRequest(getBusinessBankingSiteDetailsRequest)
    }

    override fun performCreateBusinessCustomerPortfolio(customerPortfolioInfo: CustomerPortfolioInfo, businessCustomerPortfolio: BusinessCustomerPortfolio, customerPortfolioAccountResponseExtendedResponseListener: ExtendedResponseListener<CreateCustomerPortfolioAccountResponse>) {
        val createBusinessCustomerPortfolioRequest = BusinessBankingCreateCustomerPortfolioRequest(customerPortfolioInfo, businessCustomerPortfolio, customerPortfolioAccountResponseExtendedResponseListener)
        submitRequest(createBusinessCustomerPortfolioRequest)
    }
}