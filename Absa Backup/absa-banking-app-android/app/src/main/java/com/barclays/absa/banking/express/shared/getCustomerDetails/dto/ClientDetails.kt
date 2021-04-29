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

package com.barclays.absa.banking.express.shared.getCustomerDetails.dto

import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty

class ClientDetails : BaseModel {
    @JsonProperty("clntAgrmntIssued")
    var clientAgreementIssued: String = ""

    @JsonProperty("absaRewardInd")
    var absaRewardsIndicator: String = ""

    var idRequiredHold: String = ""

    @JsonProperty("deceasedEstateHld")
    var deceasedEstateHold: String = ""

    @JsonProperty("deceasedSpouseHld")
    var deceasedSpouseHold: String = ""

    @JsonProperty("insolvntEstateHld")
    var insolventEstateHold: String = ""

    var curatorshipHold: String = ""

    @JsonProperty("savngStatementHold")
    var savingsStatementHold: String = ""

    @JsonProperty("thrdPartyInd")
    var thirdPartyIndicator: String = ""

    @JsonProperty("mandateCaptureHld")
    var mandateCaptureHold: String = ""

    @JsonProperty("powerAttornyHold")
    var powerOfAttorneyHold: String = ""

    var marketingConsentFlag: String = ""
    var branchClientOpen: Long = 0L

    @JsonProperty("physicalAddrHold")
    var physicalAddressHold: String = ""

    @JsonProperty("employerAddrHold")
    var employerAddressHold: String = ""
    var employeeIdentified: String = ""
    var applyDebtCounsel: String = ""
    var dteApplyCounsel: Long = 0L
    var counselOrderIssue: String = ""
    var dteOrderIssue: Long = 0L
    var practiceNumber: String = ""
    var securityIndicator: String = ""
    var bankingSector: String = ""
    var liabilityIndicator: String = ""
    var telebankIndicator: String = ""

    @JsonProperty("unclaimedFundsInd")
    var unclaimedFundsInd: String = ""
    var dateClientOpened: String = ""
    var changeNumber: String = ""

    var dateLastChanged: String = ""
    var siteLastChanged: String = ""

    @JsonProperty("nbrOfAccounts")
    var numberOfAccounts: String = ""

    @JsonProperty("prohibitedInd")
    var prohibitedIndicator: String = ""
    var insolventIndicator: String = ""
    var clientWebsite: String = ""

    @JsonProperty("inliPolicy")
    var inliPolicy: String = ""

    @JsonProperty("exliPolicy")
    var exliPolicy: String = ""

    @JsonProperty("instPolicy")
    var instantPolicy: String = ""

    @JsonProperty("exstPolicy")
    var existingPolicy: String = ""

    @JsonProperty("inivPolicy")
    var inivPolicy: String = ""

    @JsonProperty("internetBankinInd")
    var internetBankingIndicator: String = ""
    var flexiFuneralPolicy: String = ""
    var subSegment: String = ""
    var corporateDivision: String = ""
    var receiveSocialGrant: String = ""
    var notifyMeIndicator: String = ""
    var ibrAffected: String = ""
    var placedBy: String = ""

    @JsonProperty("dateBusRescueIss")
    var dateBusinessRescueIssued: String = ""
    var pingitWallet: String = ""
    var pingitReceive: String = ""

    @JsonProperty("regAddrLine1")
    var registrationAddressLine1: String = ""

    @JsonProperty("regAddrLine2")
    var registrationAddressLine2: String = ""

    @JsonProperty("regSuburb")
    var registrationSuburb: String = ""

    @JsonProperty("regTown")
    var registrationTown: String = ""

    @JsonProperty("hoAddrLine1")
    var homeAddressLine1: String = ""

    @JsonProperty("hoAddrLine2")
    var homeAddressLine2: String = ""

    @JsonProperty("hoSuburb")
    var homeSuburb: String = ""

    @JsonProperty("hoTown")
    var homeTown: String = ""
    var secondaryCard: String = ""
    var inBusinessRescue: String = ""
    var metricQualificationFlag: String = ""
    var qualificationCode: String = ""
    var postalAddressSame: Boolean = false
    var residentialAddress: AddressInfo = AddressInfo()
    var titleCode: String = ""
    var titleValue: String = ""
    var genderCode: String = ""
    var genderValue: String = ""
    var clientType: String = ""

    @JsonProperty("idDocType")
    var idDocumentType: String = ""
    var incomeGroup: Long = 0L
    var clientGroup: String = ""

    @JsonProperty("smsMarketInd")
    var smsMarketingIndicator: String = ""

    @JsonProperty("teleMarkInd")
    var telephoneMarketingIndicator: String = ""

    @JsonProperty("emailMarkInd")
    var emailMarketingIndicator: String = ""

    @JsonProperty("mailInd")
    var mailIndicator: String = ""
    var preferredLanguage: String = ""
    var homeTelephone: String = ""
    var companyYearEnd: Long = 0

    @JsonProperty("homeTelCode")
    var homeTelephoneCode: Long = 0
    var exconExpiryDate: Long = 0

    @JsonProperty("updDateInd")
    var updateDateIndicator: Long = 0
    var dateIssued: Long = 0

    @JsonProperty("courtAuthrityHold")
    var courtAuthorityHold: String = ""
    var sbuSegment: String = ""

    @JsonProperty("postalAddrHold")
    var postalAddressHold: String = ""

    @JsonProperty("cellphoneBankInd")
    var cellphoneBankingIndicator: String = ""

    @JsonProperty("sec129DeliveryAdd")
    var section129DeliveryAddress: String = ""
}