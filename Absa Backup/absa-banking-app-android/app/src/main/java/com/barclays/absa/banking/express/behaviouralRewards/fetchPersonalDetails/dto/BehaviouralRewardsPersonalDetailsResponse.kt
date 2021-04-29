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
package com.barclays.absa.banking.express.behaviouralRewards.fetchPersonalDetails.dto

import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty
import za.co.absa.networking.dto.BaseResponse

class PersonalDetailsResponse : BaseResponse() {
    val personalDetails = PersonalDetails()
}

class PersonalDetails : BaseModel {
    var jointPartnerList: List<JointPartnerList> = emptyList()

    @JsonProperty("contactInformationVO")
    var contactInformation = BehaviouralRewardsContactInformation()

    @JsonProperty("employementVO")
    var employmentDetails = EmploymentDetails()

    @JsonProperty("financialInformationVO")
    var financialInformation = BehaviouralRewardsFinancialInformation()

    @JsonProperty("identificationAndVerificationVO")
    var identificationAndVerification = IdentificationAndVerification()

    @JsonProperty("holdsVO")
    var holds = Holds()
    var cifKey: String = ""
    var title: String = ""
    var firstName: String = ""
    var initials: String = ""
    var lastName: String = ""
    var dateOfBirth: String = ""
    var gender: String = ""
    var identityType: String = ""
    var identityNo: String = ""
    var maritalStatus: String = ""
    var maritalContract: String = ""
    var dependents: String = ""
    var correspondenceLanguage: String = ""
    var homeLanguage: String = ""
    var clientType: String = ""
    var typeOfBusiness: String = ""
    var practiceNumber: String = ""
    var companyYearEnd: String = ""
    var contactPersonNam: String = ""
    var contactPersonDesignation: String = ""
    var internetWebSite: String = ""

    @JsonProperty("nrOfParticipants")
    var numberOfParticipants: String = ""
    var residentialStatus: String = ""
    var clientNationality: String = ""
    var countryOfOrigins: String = ""

    @JsonProperty("militaryComtmntInd")
    var militaryCommitmentIndicator: String = ""
    var casaReferenceNumber: String = ""
    var dateIssued: String = ""
    var corporateDivision: String = ""
    var countryOfBirth: String = ""
    var countryResAddress: String = ""
    var subSegment: String = ""

    @JsonProperty("nbrOfAccounts")
    var numberOfAccounts: String = ""
    var bankingSector: String = ""
    var sbuSegment: String = ""

    @JsonProperty("cellphoneBankingInd")
    var cellphoneBankingIndicator: String = ""

    @JsonProperty("internetBankiningInd")
    var internetBankingIndicato: String = ""
    var secondaryCardIndicator: String = ""
    var securityIndicator: String = ""
    var liabilityIndicator: String = ""
    var telebankIndicator: String = ""

    @JsonProperty("unclaimedFundsInd")
    var unclaimedFundsIndicator: String = ""
    var dateClientOpened: String = ""
    var changeNumber: String = ""
    var dateLastChanged: String = ""
    var siteLastChanged: String = ""
    var sicCode: String = ""
    var portfolioCifKeyList = emptyList<PortfolioCifKeyList>()
}

class JointPartnerList : BaseModel

class BehaviouralRewardsContactInformation : BaseModel {
    var homeTelephoneCode: String = ""

    @JsonProperty("homeTelephoneNbr")
    var homeTelephoneNumber: String = ""
    var faxHomeCode: String = ""

    @JsonProperty("faxHomeNbr")
    var faxHomeNumber: String = ""

    @JsonProperty("cellphoneNbr")
    var cellphoneNumber: String = ""
    var clientEmailAddress: String = ""
    var postalAddress = BehaviouralRewardsAddress()
    var residentialAddress = BehaviouralRewardsAddress()
    var preferredContactMethod: String = ""
    var marketingUpdatesFlag: String = ""
    var marketingConsent: String = ""

    @JsonProperty("smsMarketingInd")
    var smsMarketingIndicator: String = ""

    @JsonProperty("teleMarketingInd")
    var teleMarketingIndicator: String = ""

    @JsonProperty("emailMarketingInd")
    var emailMarketingIndicator: String = ""

    @JsonProperty("mailMarkeitingInd")
    var mailMarketingIndicator: String = ""
    var workTelephoneCode: String = ""

    @JsonProperty("workTelephoneNbr")
    var workTelephoneNumber: String = ""

    @JsonProperty("workTelephoneExtn")
    var workTelephoneExtension: String = ""
    var faxWorkCode: String = ""

    @JsonProperty("faxWorkNbr")
    var faxWorkNumber: String = ""
    var nextOfKinDetails = BehaviouralRewardsNextOfKinDetails()
    var notifyMeIndicator: String = ""
    var nonCreditIndicator: String = ""
    var nonCreditDateChanged: String = ""
    var nonCreditSMS: String = ""
    var nonCreditEmail: String = ""
    var nonCreditAVoice: String = ""
    var nonCreditTel: String = ""
    var nonCreditPost: String = ""
    var creditIndicator: String = ""
    var creditDateChanged: String = ""
    var creditSMS: String = ""
    var creditEmail: String = ""
    var creditAVoice: String = ""
    var creditTel: String = ""
    var creditPost: String = ""
}

class BehaviouralRewardsAddress : BaseModel {
    var addressLine1: String = ""
    var addressLine2: String = ""
    var suburbRsa: String = ""
    var town: String = ""
    var postalCode: String = ""
}

class BehaviouralRewardsNextOfKinDetails : BaseModel {
    var surname: String = ""
    var firstNames: String = ""
    var relationship: String = ""
    var homeTelephoneCode: String = ""
    var homeTelephoneNumber: String = ""
    var workTelephoneCode: String = ""
    var workTelephoneNumber: String = ""

    @JsonProperty("cellphoneNbr")
    var cellphoneNumber: String = ""
    var participantTitle: String = ""
    var participantInitials: String = ""
    var participantSurname: String = ""
    var email: String = ""
}

class EmploymentDetails : BaseModel {
    var highestLvlOfEducation: String = ""

    @JsonProperty("postMetricInd")
    var postMetricIndicator: String = ""
    var occupationStatus: String = ""
    var occupation: String = ""
    var occupationLevel: String = ""
    var occupationSector: String = ""
    var residentialStatus: String = ""
    var employerAddress = BehaviouralRewardsAddress()
    var workTelephoneCode: String = ""

    @JsonProperty("workTelephoneNbr")
    var workTelephoneNumber: String = ""

    @JsonProperty("workTelephoneExtn")
    var workTelephoneExtension: String = ""
    var faxWorkCode: String = ""

    @JsonProperty("faxWorkNbr")
    var faxWorkNumber: String = ""
    var businessAddress: String = ""
    var physicalAddressList: String = ""
}

class BehaviouralRewardsFinancialInformation : BaseModel {
    var monthlyIncome: String = ""
    var taxRefNumber: String = ""
    var socialGrantFlag: String = ""
    var debtCounsellingFlag: String = ""
    var debtCounsellingDate: String = ""
    var creditWorthinessFlag: String = ""
    var debtCounsellingConsentFlag: String = ""
    var debtCounsellingConsentDate: String = ""
    var vatRegistrationNumber: String = ""
    var sourceOfIncome: String = ""
    var registeredForTax: String = ""
    var reasonSaTaxNotGiven: String = ""
    var foreignNationalTaxResident: String = ""
    var foreignNationalResidentCountry: String = ""
    var foreignNationalReasonSaTaxNotGiven: String = ""
    var ibrAffected: String = ""
    var inBusinessRescue: String = ""
    var placedBy: String = ""
    var dateBusRescueIss: String = ""
    var internalLifePolicy: String = ""
    var externalLifePolicy: String = ""
    var internalShortTermPolicy: String = ""
    var externalShortTermPolicy: String = ""
    var internalInvestmentPolicy: String = ""

    @JsonProperty("flexiFuneralpolicy")
    var flexiFuneralPolicy: String = ""
}

class IdentificationAndVerification : BaseModel {
    var branchClientOpened: String = ""
    var tellerLastChanged: String = ""
    var dateIdentified: String = ""
    var dateVerified: String = ""
    var employeeIdentified: String = ""
    var employeeVerified: String = ""
    var dateExempted: String = ""
    var employeeExempted: String = ""
    var exemptionStatus: String = ""
    var exemptionIndicator: String = ""
}

class Holds : BaseModel {
    @JsonProperty("clntAgrmntIssued")
    var clientAgreementIssued: String = ""
    var haveQualification: String = ""
    var whatQualification: String = ""
    var socialGrant: String = ""

    @JsonProperty("thrdPartyInd")
    var thirdPartyIndicator: String = ""

    @JsonProperty("updAddressInd")
    var updAddressIndicator: String = ""

    @JsonProperty("updTelephoneInd")
    var updTelephoneIndicator: String = ""

    @JsonProperty("updEmailInd")
    var updEmailIndicator: String = ""

    @JsonProperty("updDateInd")
    var updDateIndicator: String = ""
    var pingitWallet: String = ""
    var pingitReceive: String = ""

    @JsonProperty("physicalAddrHold")
    var physicalAddressHold: String = ""

    @JsonProperty("employerAddrHold")
    var employerAddressHold: String = ""
    var insolventIndicator: String = ""

    @JsonProperty("prohibitedInd")
    var prohibitedIndicator: String = ""

    @JsonProperty("postalAddrHold")
    var postalAddressHold: String = ""

    @JsonProperty("absaRewardInd")
    var absaRewardIndicator: String = ""
    var idRequiredHold: String = ""

    @JsonProperty("deceasedEstateHld")
    var deceasedEstateHold: String = ""

    @JsonProperty("deceasedSpouseHld")
    var deceasedSpouseHold: String = ""

    @JsonProperty("insolventEstateHld")
    var insolventEstateHold: String = ""
    var curatorshipHold: String = ""

    @JsonProperty("savngStatementHold")
    var savingStatementHold: String = ""

    @JsonProperty("courtAuthrityHold")
    var courtAuthorityHold: String = ""

    @JsonProperty("mandateCaptureHld")
    var mandateCaptureHold: String = ""

    @JsonProperty("powerAttornyHold")
    var powerAttorneyHold: String = ""
}

class PortfolioCifKeyList : BaseModel