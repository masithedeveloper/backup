/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.saveAndInvest

import com.barclays.absa.banking.express.invest.saveExistingCustomerDetails.dto.AccountCreationDetails
import com.barclays.absa.banking.express.invest.saveExistingCustomerDetails.dto.AddressDetails
import com.barclays.absa.banking.express.invest.saveExistingCustomerDetails.dto.ContactDetails
import com.barclays.absa.banking.express.invest.saveExistingCustomerDetails.dto.PersonalDetails
import com.barclays.absa.banking.express.invest.validatePersonalDetails.dto.PersonalDetailsValidationRequest
import com.barclays.absa.banking.express.invest.validatePersonalDetails.dto.PersonalInfo
import com.barclays.absa.banking.express.invest.validatePersonalDetails.dto.ValidationInfo
import com.barclays.absa.banking.express.shared.getCustomerDetails.dto.AddressInfo
import com.barclays.absa.banking.express.shared.getCustomerDetails.dto.CifPersonalInfo
import com.barclays.absa.banking.express.shared.getCustomerDetails.dto.ContactInfo
import com.barclays.absa.banking.express.shared.getCustomerDetails.dto.CustomerCifInfo
import com.barclays.absa.banking.express.shared.updateCustomerDetails.dto.CustomerDetailsUpdateRequest
import com.barclays.absa.banking.express.shared.updateCustomerDetails.dto.EmploymentDetails
import com.barclays.absa.banking.express.shared.updateCustomerDetails.dto.ContactDetails as UpdateCustomerDetailsContactDetails
import com.barclays.absa.banking.express.shared.updateCustomerDetails.dto.PersonalDetails as UpdateCustomerDetailsPersonalDetails

object SaveAndInvestRiskBasedHelper {

    const val DEFAULT_NO_OCCUPATION_CODE = "0"
    const val INITIAL = "INITIAL"
    private const val MONTHLY = "MONTHLY"

    fun buildSaveApplicationAddressDetails(homeAddress: AddressInfo, postalAddress: AddressInfo): AddressDetails {
        return AddressDetails().apply {
            residentialAddressLine1 = homeAddress.addressLine1
            residentialAddressLine2 = homeAddress.addressLine2
            residentialSuburb = homeAddress.postalSuburb
            residentialCity = homeAddress.town
            residentialPostalCode = homeAddress.postalCode
            postalAddressLine1 = postalAddress.addressLine1
            postalAddressLine2 = postalAddress.addressLine2
            postalSuburb = postalAddress.postalSuburb
            postalCity = postalAddress.town
            postalCode = postalAddress.postalCode
        }
    }

    fun buildSaveApplicationContactDetails(contactInfo: ContactInfo): ContactDetails {
        return ContactDetails().apply {
            preferredContactMethodCode = contactInfo.preferredContactMethod
            workPhoneNumberCode = contactInfo.workTelephoneCode
            gsmNumber = contactInfo.cellNumber
            alternativeNumber = contactInfo.alternativeNumber
            emailAddress = contactInfo.emailAddress
        }
    }

    fun buildSaveApplicationPersonalDetails(personalDetails: CifPersonalInfo, saveAndInvestProductType: SaveAndInvestProductType, saveAndInvestProductName: String): PersonalDetails {
        return PersonalDetails().apply {
            productCode = saveAndInvestProductType.productCode
            productName = saveAndInvestProductName
            crpCode = saveAndInvestProductType.creditRatePlanCode
            titleCode = personalDetails.title
            firstName = personalDetails.firstName
            lastName = personalDetails.lastName
            initials = personalDetails.initials
            identityID = personalDetails.identityNumber
            genderCode = personalDetails.gender
            dateOfBirth = personalDetails.dateOfBirth
            countryOfResidenceCode = personalDetails.countryOfResidence
            countryOfBirthCode = personalDetails.countryOfBirth
            nationalityCode = personalDetails.nationality
            referenceNumber = personalDetails.casaReferenceNumber
            applicationType = PRODUCT_TYPE
            homeLanguageCode = personalDetails.homeLanguage
            maritalStatusCode = personalDetails.maritalStatus
            spousalConsentCheck = NO
        }
    }

    fun buildSaveApplicationAccountCreationDetails(creditRatePlanCode: String): AccountCreationDetails {
        return AccountCreationDetails().apply {
            savingFrequency = INITIAL
            receiveInterestFrequency = MONTHLY
            creditRatePlan = creditRatePlanCode
        }
    }

    fun buildCustomerDetailsUpdateRequest(customerDetails: CustomerCifInfo, saveAndInvestProductType: SaveAndInvestProductType): CustomerDetailsUpdateRequest {
        with(customerDetails) {
            return CustomerDetailsUpdateRequest().apply {
                cifKey = this@with.cifKey
                clientInfo = clientDetails
                contactDetails = UpdateCustomerDetailsContactDetails().apply {
                    cellNumber = contactInfo.cellNumber
                    alternativeNumber = contactInfo.alternativeNumber
                    emailAddress = contactInfo.emailAddress
                    workTelephoneCode = contactInfo.workTelephoneCode
                    homeAddress = contactInfo.homeAddress
                    postalAddress = contactInfo.postalAddress
                    samePostalAddress = contactInfo.samePostalAddress
                    preferredContactMethodCode = contactInfo.preferredContactMethod
                }
                pricingCode = saveAndInvestProductType.creditRatePlanCode
                productCode = saveAndInvestProductType.productCode
                employmentDetails = EmploymentDetails().apply {
                    employerAddress = employmentInfo.employerAddress
                    employedSince = employmentInfo.employedSince
                    highestLevelOfEducation = employmentInfo.highestLevelOfEducation
                    matricQualificationFlag = employmentInfo.matricQualificationFlag
                    employerName = employmentInfo.nameOfEmployer
                    occupationLevelCode = employmentInfo.occupationLevel
                    occupationSector = employmentInfo.occupationSector
                }
                nextOfKinInfo = nextOfKinDetails
                personalDetails = UpdateCustomerDetailsPersonalDetails().apply {
                    val cifPersonalDetails = this@with.personalDetails
                    titleCode = cifPersonalDetails.title
                    firstName = cifPersonalDetails.firstName
                    lastName = cifPersonalDetails.lastName
                    initials = cifPersonalDetails.initials
                    identityNumber = cifPersonalDetails.identityNumber
                    nationalityCode = cifPersonalDetails.nationality
                    countryOfBirthCode = cifPersonalDetails.countryOfBirth
                    countryOfResidenceCode = cifPersonalDetails.countryOfResidence
                    dateOfBirth = cifPersonalDetails.dateOfBirth
                    maritalStatusCode = cifPersonalDetails.maritalStatus
                    maritalContract = cifPersonalDetails.maritalContract
                    casaReferenceNumber = cifPersonalDetails.casaReferenceNumber
                    gender = cifPersonalDetails.gender
                    homeLanguage = cifPersonalDetails.homeLanguage
                    residentialStatus = cifPersonalDetails.residentialStatus
                    nextOfKinFullName = cifPersonalDetails.nextOfKinFullName
                    nextOfKinSurname = cifPersonalDetails.nextOfKinSurname
                    nextOfKinRelationship = cifPersonalDetails.nextOfKinRelationship
                    nextOfKinContactNumber = cifPersonalDetails.nextOfKinContactNumber
                    nextOfKinEmailAddress = cifPersonalDetails.nextOfKinEmailAddress
                    localTaxNumber = cifPersonalDetails.saTaxNumber
                }
                financeInfo = financeDetails
                taxDetails = this@with.taxDetails
                raceIndicator = this@with.raceIndicator
            }
        }
    }

    fun buildPersonalDetailsValidationRequest(customerDetails: CustomerCifInfo): PersonalDetailsValidationRequest {
        return PersonalDetailsValidationRequest().apply {
            val customerPersonalInfo = customerDetails.personalDetails
            val customerContactInfo = customerDetails.contactInfo
            validationInfo = ValidationInfo().apply {
                personalInfo = PersonalInfo().apply {
                    title = customerPersonalInfo.title
                    firstName = customerPersonalInfo.firstName
                    lastName = customerPersonalInfo.lastName
                    initials = customerPersonalInfo.initials
                    identityNo = customerPersonalInfo.identityNumber
                    nationality = customerPersonalInfo.nationality
                    countryOfBirth = customerPersonalInfo.countryOfBirth
                    countryOfResidence = customerPersonalInfo.countryOfResidence
                    dateOfBirth = customerPersonalInfo.dateOfBirth
                    maritalStatus = customerPersonalInfo.maritalStatus
                    maritalContract = customerPersonalInfo.maritalContract
                    casaReferenceNumber = customerPersonalInfo.casaReferenceNumber
                    gender = customerPersonalInfo.gender
                    homeLanguage = customerPersonalInfo.homeLanguage
                }
                contactInfo = customerContactInfo
                contactInfo.postalAddress.country = contactInfo.postalAddress.country.ifEmpty { customerContactInfo.homeAddress.country }
                applicationType = PRODUCT_TYPE
            }
        }
    }
}