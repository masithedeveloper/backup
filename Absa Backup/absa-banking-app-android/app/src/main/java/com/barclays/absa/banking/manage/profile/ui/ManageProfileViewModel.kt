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

package com.barclays.absa.banking.manage.profile.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.boundary.docHandler.DocHandlerInteractor
import com.barclays.absa.banking.boundary.docHandler.dto.*
import com.barclays.absa.banking.boundary.model.ProfileSetupResult
import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.banking.framework.ssl.DocHandlerDownloadFileService
import com.barclays.absa.banking.framework.ssl.DocHandlerDownloadFileServiceImpl
import com.barclays.absa.banking.framework.ssl.DocHandlerRemoveFileService
import com.barclays.absa.banking.framework.ssl.FileResponse
import com.barclays.absa.banking.manage.profile.ManageProfileDocUploadType
import com.barclays.absa.banking.manage.profile.ManageProfileEducationDetailsFlow
import com.barclays.absa.banking.manage.profile.ManageProfileFinancialDetailsFlow
import com.barclays.absa.banking.manage.profile.ManageProfileFlow
import com.barclays.absa.banking.manage.profile.services.ManageProfileInteractor
import com.barclays.absa.banking.manage.profile.services.dto.*
import com.barclays.absa.banking.manage.profile.services.responselisteners.*
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.PROOF_OF_IDENTIFICATION
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.PROOF_OF_PHYSICAL_RESIDENCE
import com.barclays.absa.banking.manage.profile.ui.ManageProfileConstants.YES
import com.barclays.absa.banking.manage.profile.ui.educationAndEmploymentDetails.EmploymentInformationStateInformation
import com.barclays.absa.banking.manage.profile.ui.models.*
import com.barclays.absa.banking.manage.profile.ui.widgets.ConfirmScreenItem
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.barclays.absa.banking.settings.ui.SettingsHubInteractor
import com.barclays.absa.banking.shared.services.SharedInteractor
import com.barclays.absa.banking.shared.services.dto.CIFGroupCode
import com.barclays.absa.banking.shared.services.dto.LookupItem
import com.barclays.absa.banking.shared.services.dto.LookupResult
import com.barclays.absa.utils.imageHelpers.ProfileViewImageHelper
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import styleguide.forms.SelectorList
import styleguide.utils.extensions.removeSpaces
import styleguide.utils.extensions.toSentenceCase
import styleguide.utils.extensions.toTitleCase

class ManageProfileViewModel : BaseViewModel() {
    var customerInformation = MutableLiveData<CustomerInformationResponse>()
    var bcmsCaseId = MutableLiveData<BCMSCaseIdResponse>()
    var bcmsCaseIdFailure = MutableLiveData<ResponseObject>()
    var updateCustomerInformation = MutableLiveData<UpdatedFields>()
    var personalInformation = MutableLiveData<PersonalInformationDisplay>()
    var country = MutableLiveData<String>()
    var communicationMethodLookUpResult = MutableLiveData<LookupResult>()
    var originalForeignTaxDetails = MutableLiveData<ArrayList<OriginalForeignTaxDisplayValues>>()
    var foreignTaxDetails = MutableLiveData<ArrayList<ForeignTaxDisplayValues>>()
    var southAfricanTax = MutableLiveData<SouthAfricanTaxDetails>()
    var monthlyIncomeLookupResult = MutableLiveData<LookupResult>()
    var sourceOfIncomeLookupResult = MutableLiveData<LookupResult>()
    var relationshipLookUpResult = MutableLiveData<LookupResult>()
    var educationDetails = MutableLiveData<EducationDetailsDisplayInformation>()
    var docHandlerResponse = MutableLiveData<DocHandlerGetCaseByIdResponse>()
    var customerProfilePictureResult = MutableLiveData<ProfileSetupResult>()
    var lookupFailure = MutableLiveData<String>()

    var docHandlerFetchCaseFailure = MutableLiveData<String>()
    var docHandlerUploadFailureResponse = MutableLiveData<String>()
    var docHandlerUploadResponse = MutableLiveData<DocHandlerUploadDocumentResponse>()

    var contactInformationToUpdate = ManageProfileUpdatedContactInformation()
    var postalAddressDetailsToUpdate = ManageProfileUpdatedPostalAddressDetails()
    var personalDetailsToUpdate = ManageProfileUpdatedPersonalInformation()
    var nextOfKinDetailsToUpdate = ManageProfileUpdatedNextOfKinDetails()
    var updatedPostalAddressDetails = PostalAddressDetails()
    var displayForeignTaxDetails = ArrayList<ForeignTaxDisplayValues>()
    var southAfricanTaxDetails = SouthAfricanTaxDetails()
    var originalDisplayForeignTaxDetails = ArrayList<OriginalForeignTaxDisplayValues>()
    var taxReasons = SelectorList<ReasonsForNoTaxNumber>()
    var manageProfileFinancialDetailsMetaData = ManageProfileFinancialDetailsMetaData()
    var marketingConsentDetailsToUpdate = ManageProfileUpdatedMarketingConsentInformation()
    var manageProfileEducationDetailsModel = ManageProfileEducationDetailsModel()
    var editedAddressFields = AddressLineFields()
    var employmentInformationStateInformation = EmploymentInformationStateInformation()
    var manageProfileAddressDetails = ManageProfileAddressDetails()
    private var proofOfResidenceBCMSData = RequiredDocuments()
    private var proofOfIdentityBCMSData = RequiredDocuments()
    var originalEducationDetailsDisplayInformation = EducationDetailsDisplayInformation()
    private var manageProfileUpdateFinancialDetails: ManageProfileUpdateFinancialDetails = ManageProfileUpdateFinancialDetails()

    var proofOfIdentityFile: ByteArray? = null
    var proofOfIdentityFileDetails = FileDetails()
    var proofOfResidenceFile: ByteArray? = null
    var proofOfResidenceFileDetails = FileDetails()

    var documents = arrayListOf<DocHandlerDocument>()
    var idDocumentType = ManageProfileDocHandlerDocumentType()
    var proofOfResidenceType = ManageProfileDocHandlerDocumentType()

    private val manageProfileInteractor = ManageProfileInteractor()
    private val sharedInteractor = SharedInteractor()
    private val settingsHubInteractor: SettingsHubInteractor = SettingsHubInteractor()
    private val docHandlerInteractor = DocHandlerInteractor()
    private val docHandlerFileDownloadInteractor: DocHandlerDownloadFileService = DocHandlerDownloadFileServiceImpl()
    private val docHandlerRemoveFileService: DocHandlerRemoveFileService = DocHandlerRemoveFileService()

    private var foreignTaxCountryDetails = ForeignTaxCountryDetails()
    private var preProcessedPersonalInformation = PersonalInformationDisplay()
    private var titleLookupResult = LookupResult()
    private var nationalityLookupResult = LookupResult()
    private var homeLanguageLookupResult = LookupResult()
    private var countryCodeLookupResult = LookupResult()
    private var preferredCommunicationMethod = LookupResult()

    private var titleLookupTable = SelectorList<LookupItem>()
    private var nationalityLookupTable = SelectorList<LookupItem>()
    private var homeLanguageLookupTable = SelectorList<LookupItem>()
    private var countryListLookupTable = SelectorList<LookupItem>()

    private var qualificationListLookupResult = LookupResult()
    private var occupationListLookupResult = LookupResult()
    private var occupationSectorListLookupResult = LookupResult()
    private var occupationLevelListLookupResult = LookupResult()
    private var occupationStatusListLookupResult = LookupResult()

    private var qualificationListLookupTable = SelectorList<LookupItem>()
    private var occupationListLookupTable = SelectorList<LookupItem>()
    private var occupationSectorListLookupTable = SelectorList<LookupItem>()
    private var occupationLevelListLookupTable = SelectorList<LookupItem>()
    private var occupationStatusListLookupTable = SelectorList<LookupItem>()

    private val customerProfileExtendedResponseListener = CustomerProfileExtendedResponseListener(this)
    private val ficCaseIdExtendedResponseListener = FicCaseIdExtendedResponseListener(this)
    private val lookupTableExtendedResponseListener = PersonalDetailsLookupTableExtendedResponseListener(this)
    private val educationDetailsLookupTableExtendedResponseListener = EducationDetailsLookupTableExtendedResponseListener(this)
    private val countryLookupTableExtendedResponseListener = CountryLookupTableResponse(this)
    private val updateProfileExtendedResponseListener = UpdateProfileExtendedResponseListener(this)
    private val preferredCommunicationLookupResponseListener = PreferredCommunicationMethodLookupTableExtendedResponseListener(this)
    private val monthlyIncomeExtendedResponseListener = MonthlyIncomeExtendedResponseListener(this)
    private val sourceOfIncomeExtendedResponseListener = SourceOfIncomeExtendedResponseListener(this)
    private val manageProfileDocHandlerGetCaseExtendedResponseListener: ManageProfileDocHandlerGetCaseExtendedResponseListener by lazy { ManageProfileDocHandlerGetCaseExtendedResponseListener(this) }
    private val manageProfileDocHandlerUploadResponse: ManageProfileDocHandlerUploadResponseListener by lazy { ManageProfileDocHandlerUploadResponseListener(this) }
    private val relationshipExtendedResponseListener = RelationshipExtendedResponseListener(this)
    private val customerProfilePictureExtendedResponseListener = CustomerProfilePictureExtendedResponseListener(this)

    private var currentLookupIndex = 0
    private var documentIndex = 0
    private var currentEducationAndEmploymentLookupIndex = 0

    var manageProfileFlow: ManageProfileFlow? = null
    var manageProfileDocUploadType: ManageProfileDocUploadType? = null
    var manageProfileEducationDetailsFlow: ManageProfileEducationDetailsFlow? = null
    var manageProfileFinancialDetailsFlow: ManageProfileFinancialDetailsFlow? = null
    var communicationMethodToUpdate = ""
    var wasAddressFieldsChangedInEmploymentFlow: Boolean = false
    var isEmploymentFlowAddressRemoved: Boolean = true
    var hasPostalAddressDetailsChanged: Boolean = false

    var confirmScreenItemList = ArrayList<ConfirmScreenItem>()

    fun fetchCustomerInformation() {
        manageProfileInteractor.fetchUserProfileDetails(customerProfileExtendedResponseListener)
    }

    fun fetchCaseId() {
        manageProfileInteractor.fetchCaseID(ficCaseIdExtendedResponseListener)
    }

    fun fetchLookupTableValuesForPersonalDetailsScreen() {
        when (currentLookupIndex) {
            0 -> sharedInteractor.performLookup(CIFGroupCode.TITLE, lookupTableExtendedResponseListener)
            1 -> sharedInteractor.performLookup(CIFGroupCode.MARITAL_STATUS, lookupTableExtendedResponseListener)
            2 -> sharedInteractor.performLookup(CIFGroupCode.NATIONALITY, lookupTableExtendedResponseListener)
            3 -> sharedInteractor.performLookup(CIFGroupCode.HOME_LANG, lookupTableExtendedResponseListener)
            4 -> sharedInteractor.performLookup(CIFGroupCode.COUNTRY_PASSPORT, lookupTableExtendedResponseListener)
        }
    }

    fun fetchMonthlyIncome() {
        if (monthlyIncomeLookupResult.value == null) {
            sharedInteractor.performLookup(CIFGroupCode.MONTHLY_INCOME_GROUP, monthlyIncomeExtendedResponseListener)
        }
    }

    fun fetchSourceOfIncome() {
        if (sourceOfIncomeLookupResult.value == null) {
            sharedInteractor.performLookup(CIFGroupCode.SOURCE_OF_INCOME_I, sourceOfIncomeExtendedResponseListener)
        }
    }

    fun fetchRelationship() {
        if (relationshipLookUpResult.value == null) {
            sharedInteractor.performLookup(CIFGroupCode.RELATIONSHIP, relationshipExtendedResponseListener)
        }
    }

    fun fetchLookupTableValuesForEducationAndEmploymentDetailsScreen() {
        when (currentEducationAndEmploymentLookupIndex) {
            0 -> sharedInteractor.performLookup(CIFGroupCode.POST_MATRIC_QUALIFICATION, educationDetailsLookupTableExtendedResponseListener)
            1 -> sharedInteractor.performLookup(CIFGroupCode.OCCUPATION_STATUS, educationDetailsLookupTableExtendedResponseListener)
            2 -> sharedInteractor.performLookup(CIFGroupCode.OCCUPATION, educationDetailsLookupTableExtendedResponseListener)
            3 -> sharedInteractor.performLookup(CIFGroupCode.EMPLOYMENT_SECTOR, educationDetailsLookupTableExtendedResponseListener)
            4 -> sharedInteractor.performLookup(CIFGroupCode.OCCUPATION_LEVEL, educationDetailsLookupTableExtendedResponseListener)
        }
    }

    fun populatePersonalDetailsFields(successResponse: LookupResult) {
        var personalInformationForDisplay = PersonalInformation()
        customerInformation.value?.customerInformation?.personalInformation?.let { personalInformationForDisplay = it }

        when (currentLookupIndex) {
            0 -> {
                titleLookupResult = successResponse
                preProcessedPersonalInformation.title = getLookupValue(successResponse, personalInformationForDisplay.title)
            }
            1 -> preProcessedPersonalInformation.maritalStatus = getLookupValue(successResponse, personalInformationForDisplay.maritalStatus)
            2 -> {
                nationalityLookupResult = successResponse
                preProcessedPersonalInformation.nationality = getLookupValue(successResponse, personalInformationForDisplay.clientNationality)
            }
            3 -> {
                homeLanguageLookupResult = successResponse
                preProcessedPersonalInformation.homeLanguage = getLookupValue(successResponse, personalInformationForDisplay.homeLanguage)
            }
            4 -> {
                preProcessedPersonalInformation.countryOfBirth = getLookupValue(successResponse, personalInformationForDisplay.countryOfBirth)
                preProcessedPersonalInformation.preferredCorrespondenceLanguage = personalInformationForDisplay.correspondenceLanguage
                preProcessedPersonalInformation.numberOfDependants = personalInformationForDisplay.dependents

                personalInformation.value = preProcessedPersonalInformation
            }
        }
        currentLookupIndex += 1
        fetchLookupTableValuesForPersonalDetailsScreen()
    }

    fun populateEducationAndEmploymentDetailsLookupValues(successResponse: LookupResult) {
        var employmentInformation = EmploymentInformation()
        customerInformation.value?.customerInformation?.employmentInformation?.let {
            employmentInformation = it
        }

        when (currentEducationAndEmploymentLookupIndex) {
            0 -> {
                qualificationListLookupResult = successResponse
                originalEducationDetailsDisplayInformation.highestQualification = getLookupValue(successResponse, employmentInformation.highestLvlOfEducation)
            }
            1 -> {
                occupationStatusListLookupResult = successResponse
                originalEducationDetailsDisplayInformation.occupationStatus = getLookupValue(successResponse, employmentInformation.occupationStatus)
            }
            2 -> {
                occupationListLookupResult = successResponse
                originalEducationDetailsDisplayInformation.occupation = getLookupValue(successResponse, employmentInformation.occupation)
            }
            3 -> {
                occupationSectorListLookupResult = successResponse
                originalEducationDetailsDisplayInformation.occupationSector = getLookupValue(successResponse, employmentInformation.occupationSector)
            }
            4 -> {
                occupationLevelListLookupResult = successResponse
                originalEducationDetailsDisplayInformation.occupationLevelCode = employmentInformation.occupationLevel
                originalEducationDetailsDisplayInformation.occupationLevel = getLookupValue(successResponse, employmentInformation.occupationLevel)

                originalEducationDetailsDisplayInformation.occupationCode = employmentInformation.occupation
                originalEducationDetailsDisplayInformation.highestQualificationCode = employmentInformation.highestLvlOfEducation
                originalEducationDetailsDisplayInformation.occupationStatusCode = employmentInformation.occupationStatus
                originalEducationDetailsDisplayInformation.occupationSectorCode = employmentInformation.occupationSector
                originalEducationDetailsDisplayInformation.employerTelephoneCode = employmentInformation.workTelephoneCode
                originalEducationDetailsDisplayInformation.employerTelephoneNumber = employmentInformation.workNumber
                originalEducationDetailsDisplayInformation.employerFaxCode = employmentInformation.faxWorkCode
                originalEducationDetailsDisplayInformation.employerFaxNumber = employmentInformation.workFaxNumber
                originalEducationDetailsDisplayInformation.employerName = employmentInformation.employerAddressDTO.addressLine1.toSentenceCase()
                originalEducationDetailsDisplayInformation.employerAddressLineTwo = employmentInformation.employerAddressDTO.addressLine2.toSentenceCase()
                originalEducationDetailsDisplayInformation.employerSuburb = employmentInformation.employerAddressDTO.suburbRsa.toSentenceCase()
                originalEducationDetailsDisplayInformation.employerTown = employmentInformation.employerAddressDTO.town.toSentenceCase()
                originalEducationDetailsDisplayInformation.employerPostalCode = employmentInformation.employerAddressDTO.postalCode.toSentenceCase()
                originalEducationDetailsDisplayInformation.postMatric = employmentInformation.postMetricInd

                educationDetails.value = originalEducationDetailsDisplayInformation
            }
        }
        currentEducationAndEmploymentLookupIndex += 1
        fetchLookupTableValuesForEducationAndEmploymentDetailsScreen()
    }

    fun populateCustomerInformation(successResponse: CustomerInformationResponse) {
        fetchCaseId()
        customerInformation.value = successResponse
    }

    fun retrieveNationalityList(): SelectorList<LookupItem> {
        nationalityLookupTable = convertLookupTableToInputView(nationalityLookupResult.items)
        return nationalityLookupTable
    }

    fun retrieveTitleList(): SelectorList<LookupItem> {
        titleLookupTable = convertLookupTableToInputView(titleLookupResult.items)
        return titleLookupTable
    }

    fun retrieveHomeLanguageList(): SelectorList<LookupItem> {
        homeLanguageLookupTable = convertLookupTableToInputView(homeLanguageLookupResult.items)
        return homeLanguageLookupTable
    }

    fun retrieveCountryList(): SelectorList<LookupItem> {
        countryListLookupTable = convertLookupTableToInputView(countryCodeLookupResult.items)
        return countryListLookupTable
    }

    fun retrieveSourceOfIncomeList(): SelectorList<LookupItem> {
        return convertLookupTableToInputView(sourceOfIncomeLookupResult.value?.items)
    }

    fun retrieveMonthlyIncomeList(): SelectorList<LookupItem> {
        return convertLookupTableToInputView(monthlyIncomeLookupResult.value?.items)
    }

    fun retrieveRelationshipList(): SelectorList<LookupItem> {
        return convertLookupTableToInputView(relationshipLookUpResult.value?.items)
    }

    fun retrieveCommunicationMethodList(): SelectorList<LookupItem> {
        return convertLookupTableToInputView(communicationMethodLookUpResult.value?.items)
    }

    fun updatePersonalDetails() {
        val manageProfileUpdateProfileModel = ManageProfileUpdateProfileModel().apply {
            clientType = customerInformation.value?.customerInformation?.personalInformation?.clientType.toString()
            title = personalDetailsToUpdate.title
            dependents = personalDetailsToUpdate.numberOfDependant
            correspondenceLanguage = personalDetailsToUpdate.preferredCorrespondenceLanguage
            homeLanguage = personalDetailsToUpdate.homeLanguage
            clientNationality = personalDetailsToUpdate.nationality
        }
        manageProfileInteractor.updatePersonalInformation(manageProfileUpdateProfileModel, customerInformation.value?.customerInformation?.personalInformation ?: PersonalInformation(), updateProfileExtendedResponseListener)
    }

    fun updatePostalAddress() {
        val contactInformation = customerInformation.value?.customerInformation?.contactInformation
        val residentialAddress = customerInformation.value?.customerInformation?.residentialAddress

        val contactInfoModel = ContactInfoModel().apply {
            clientType = customerInformation.value?.customerInformation?.personalInformation?.clientType.toString()
            residentialAddressLine1 = residentialAddress?.addressLine1.toString()
            residentialAddressLine2 = residentialAddress?.addressLine2.toString()
            residentialSuburbRsa = residentialAddress?.suburbRsa.toString()
            residentialTown = residentialAddress?.town.toString()
            residentialPostalCode = residentialAddress?.postalCode.toString()
            residentialCountry = residentialAddress?.country.toString()
            postalAddressLine1 = updatedPostalAddressDetails.addressLineOne
            postalAddressLine2 = updatedPostalAddressDetails.addressLineTwo
            postalSuburbRsa = updatedPostalAddressDetails.addressSuburb
            postalTown = updatedPostalAddressDetails.addressCity
            postalPostalCode = updatedPostalAddressDetails.addressPostalCode
            preferredContactMethod = contactInformation?.preferredContactMethod.toString()
            homeTelephoneCode = contactInformation?.homeTelephoneCode.toString()
            homeTelephoneNumber = contactInformation?.homeNumber.toString()
            homeFaxCode = contactInformation?.faxHomeCode.toString()
            cellNumber = contactInformation?.cellNumber.toString()
            emailAddress = contactInformation?.email.toString()
            homeFaxNumber = contactInformation?.homeFaxNumber.toString()
        }
        manageProfileInteractor.updateContactInfo(contactInfoModel, updateProfileExtendedResponseListener)
    }

    fun updateContactDetails() {
        val residentialAddress = customerInformation.value?.customerInformation?.residentialAddress ?: PostalAddress()

        val contactInfoModel = ContactInfoModel().apply {
            clientType = customerInformation.value?.customerInformation?.personalInformation?.clientType.toString()
            residentialAddressLine1 = residentialAddress.addressLine1
            residentialAddressLine2 = residentialAddress.addressLine2
            residentialSuburbRsa = residentialAddress.suburbRsa
            residentialTown = residentialAddress.town
            residentialPostalCode = residentialAddress.postalCode
            residentialCountry = residentialAddress.country
            postalAddressLine1 = postalAddressDetailsToUpdate.addressLineOne
            postalAddressLine2 = postalAddressDetailsToUpdate.addressLineTwo
            postalSuburbRsa = postalAddressDetailsToUpdate.addressSuburb
            postalTown = postalAddressDetailsToUpdate.addressCity
            postalPostalCode = postalAddressDetailsToUpdate.addressPostalCode
            preferredContactMethod = communicationMethodToUpdate
            homeTelephoneCode = contactInformationToUpdate.homeTelephoneCode
            homeTelephoneNumber = if (contactInformationToUpdate.homeNumber.length > 1) contactInformationToUpdate.homeNumber.removeSpaces() else " "
            homeFaxCode = contactInformationToUpdate.faxHomeCode
            cellNumber = contactInformationToUpdate.cellNumber.removeSpaces()
            emailAddress = contactInformationToUpdate.email
            homeFaxNumber = contactInformationToUpdate.homeFaxNumber.removeSpaces()
        }
        manageProfileInteractor.updateContactInfo(contactInfoModel, updateProfileExtendedResponseListener)
    }

    fun fetchCountryForCountryCode() {
        sharedInteractor.performLookup(CIFGroupCode.COUNTRY_PASSPORT, countryLookupTableExtendedResponseListener)
    }

    fun updateCountryDisplayed(successResponse: LookupResult) {
        countryCodeLookupResult = successResponse
        country.value = getLookupValue(successResponse, customerInformation.value?.customerInformation?.residentialAddress?.country.toString())
    }

    fun fetchPreferredCommunicationMethod() {
        sharedInteractor.performLookup(CIFGroupCode.PREFERRED_COMMUNICATION, preferredCommunicationLookupResponseListener)
    }

    fun updatePreferredCommunicationMethod(successResponse: LookupResult) {
        preferredCommunicationMethod = successResponse
        communicationMethodLookUpResult.value = preferredCommunicationMethod
    }

    fun fetchCountryLookupTableForTax() {
        if (countryCodeLookupResult.items.isEmpty()) {
            sharedInteractor.performLookup(CIFGroupCode.COUNTRY_PASSPORT, countryLookupTableExtendedResponseListener)
        } else {
            updateForeignTaxCountryValues(countryCodeLookupResult)
        }
    }

    fun updateForeignTaxCountryValues(successResponse: LookupResult) {
        var foreignCountryTaxDetails = arrayListOf<ForeignCountryTaxDetails>()
        countryCodeLookupResult = successResponse
        customerInformation.value?.customerInformation?.financialInformation?.foreignCountryTaxDetails?.let { foreignCountryTaxDetails = it }

        if (originalDisplayForeignTaxDetails.isEmpty()) {
            foreignCountryTaxDetails.forEach { foreignTaxDetails ->
                OriginalForeignTaxDisplayValues().apply {
                    taxCountry = getLookupValue(successResponse, foreignTaxDetails.foreignTaxCountry)
                    taxCountryCode = foreignTaxDetails.foreignTaxCountry
                    taxNumber = foreignTaxDetails.foreignTaxNumber
                    reasonForNoTaxNumberCode = foreignTaxDetails.reasonForForeignTaxNotGiven
                    reasonForNoTaxNumber = if (foreignTaxDetails.reasonForForeignTaxNotGiven.isNotEmpty()) retrieveForNoTaxNumber(foreignTaxDetails.reasonForForeignTaxNotGiven) else ""
                    isTaxNumberAvailable = foreignTaxDetails.isForeignTaxNumberAvailable
                    originalDisplayForeignTaxDetails.add(this)
                }
            }
        }
        originalForeignTaxDetails.value = originalDisplayForeignTaxDetails
    }

    fun populateEmptyForeignDetails() {
        originalDisplayForeignTaxDetails.forEach {
            ForeignTaxDisplayValues().apply {
                taxCountry = it.taxCountry
                taxCountryCode = it.taxCountryCode
                taxNumber = it.taxNumber
                reasonForNoTaxNumber = it.reasonForNoTaxNumber
                reasonForNoTaxNumberCode = it.reasonForNoTaxNumberCode
                isTaxNumberAvailable = it.isTaxNumberAvailable
                displayForeignTaxDetails.add(this)
            }
        }
        foreignTaxDetails.value = displayForeignTaxDetails
    }

    private fun retrieveForeignTaxDetails(): ArrayList<OriginalForeignTaxDisplayValues> {
        populateEmptyForeignDetails()
        return originalDisplayForeignTaxDetails
    }

    fun updateFinancialDetails() {
        val financialInformation = customerInformation.value?.customerInformation?.financialInformation ?: FinancialInformation()
        manageProfileUpdateFinancialDetails.clientType = customerInformation.value?.customerInformation?.personalInformation?.clientType.toString()
        mergeCurrentChanges()

        when (manageProfileFinancialDetailsFlow) {
            ManageProfileFinancialDetailsFlow.UPDATE_INCOME_DETAILS -> {
                mergeForeignTaxDetailsData(financialInformation)
                mergeLocalTaxDetailsData(financialInformation)
                mergeOtherFinancialDetailsData(financialInformation)
            }
            ManageProfileFinancialDetailsFlow.UPDATE_LOCAL_TAX -> {
                mergeIncomeDetailsData(financialInformation)
                mergeForeignTaxDetailsData(financialInformation)
                mergeOtherFinancialDetailsData(financialInformation)
            }
            ManageProfileFinancialDetailsFlow.UPDATE_FOREIGN_TAX -> {
                mergeIncomeDetailsData(financialInformation)
                mergeLocalTaxDetailsData(financialInformation)
                mergeOtherFinancialDetailsData(financialInformation)
                mergeForeignTaxDetailsData(financialInformation)
            }
            ManageProfileFinancialDetailsFlow.UPDATE_OTHER_FINANCIAL_DETAILS -> {
                mergeIncomeDetailsData(financialInformation)
                mergeIncomeDetailsData(financialInformation)
                mergeForeignTaxDetailsData(financialInformation)
            }
        }

        updateFinancialDetails(manageProfileUpdateFinancialDetails)
    }

    private fun mergeIncomeDetailsData(financialInformation: FinancialInformation) {
        manageProfileUpdateFinancialDetails.apply {
            monthlyIncome = manageProfileFinancialDetailsMetaData.monthlyIncome.ifEmpty { financialInformation.monthlyIncome }
            sourceOfIncome = manageProfileFinancialDetailsMetaData.sourceOfIncome.ifEmpty { financialInformation.sourceOfIncome }
            sourceOfFunds = financialInformation.sourceOfFunds
        }
    }

    private fun mergeLocalTaxDetailsData(financialInformation: FinancialInformation) {
        manageProfileUpdateFinancialDetails.apply {
            saIncomeTaxNumber = if (manageProfileFlow == ManageProfileFlow.LOCAL_TAX) southAfricanTaxDetails.taxNumber else financialInformation.saIncomeTaxNumber
            areYouRegisteredForSaTax = if (manageProfileFinancialDetailsMetaData.isRegisteredForSouthAfricanTax.isEmpty()) financialInformation.areYouRegisteredSaTax else manageProfileFinancialDetailsMetaData.isRegisteredForSouthAfricanTax
            reasonNotGivenSaTaxNumber = southAfricanTaxDetails.reasonForNoTaxNumberCode
            isSaTaxNoAvailable = southAfricanTaxDetails.isTaxNumberAvailable
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun mergeForeignTaxDetailsData(financialInformation: FinancialInformation) {
        manageProfileUpdateFinancialDetails.apply {
            areYouRegisteredForeignTax = if (manageProfileFinancialDetailsMetaData.isRegisteredForForeignTax.isNotEmpty()) manageProfileFinancialDetailsMetaData.isRegisteredForForeignTax else financialInformation.areYouRegisteredForeignTax
            foreignCountryCount = if (manageProfileFinancialDetailsMetaData.isRegisteredForForeignTax == YES) displayForeignTaxDetails.size else 0
            var taxList = ""
            if (manageProfileFinancialDetailsMetaData.isRegisteredForForeignTax == YES && manageProfileFlow == ManageProfileFlow.FOREIGN_TAX) {
                foreignTaxCountryDetails.foreignCountryTaxDetails = displayForeignTaxDetails as ArrayList<DisplayForeignTaxCountry>
                taxList = buildJsonRequestForForeignTax(foreignTaxCountryDetails)
            } else if (retrieveForeignTaxDetails().isNotEmpty()) {
                foreignTaxCountryDetails.foreignCountryTaxDetails = retrieveForeignTaxDetails() as ArrayList<DisplayForeignTaxCountry>
                taxList = buildJsonRequestForForeignTax(foreignTaxCountryDetails)
            }
            foreignTaxList = taxList
        }
    }

    private fun mergeOtherFinancialDetailsData(financialInformation: FinancialInformation) {
        manageProfileUpdateFinancialDetails.apply {
            socialGrantFlag = manageProfileFinancialDetailsMetaData.socialGrantFlag.ifEmpty { financialInformation.socialGrantFlag }
            debtCounsellingFlag = financialInformation.debtCounsellingFlag
            debtCounsellingDate = financialInformation.debtCounsellingDate
            creditWorthinessFlag = manageProfileFinancialDetailsMetaData.creditWorthinessFlag.ifEmpty { financialInformation.creditWorthinessFlag }
            debtCounsellingConsentFlag = financialInformation.debtCounsellingConsentFlag
            debtCounsellingConsentDate = financialInformation.debtCounsellingConsentDate
        }
    }

    private fun mergeCurrentChanges() {
        manageProfileUpdateFinancialDetails.apply {
            monthlyIncome = manageProfileFinancialDetailsMetaData.monthlyIncome
            socialGrantFlag = manageProfileFinancialDetailsMetaData.socialGrantFlag
            creditWorthinessFlag = manageProfileFinancialDetailsMetaData.creditWorthinessFlag
            sourceOfIncome = manageProfileFinancialDetailsMetaData.sourceOfIncome
            saIncomeTaxNumber = manageProfileFinancialDetailsMetaData.saIncomeTaxNumber
            areYouRegisteredForSaTax = manageProfileFinancialDetailsMetaData.isRegisteredForSouthAfricanTax
            areYouRegisteredForeignTax = manageProfileFinancialDetailsMetaData.isRegisteredForForeignTax
            reasonNotGivenSaTaxNumber = manageProfileFinancialDetailsMetaData.reasonForNoTaxNumberCode
            isSaTaxNoAvailable = manageProfileFinancialDetailsMetaData.isSaTaxNumberAvailable
        }
    }

    fun updateMarketingConsent() {
        customerInformation.value?.customerInformation?.marketingIndicator?.apply {
            clientType = customerInformation.value?.customerInformation?.personalInformation?.clientType.toString()
            nonCreditIndicator = marketingConsentDetailsToUpdate.nonCreditIndicator
            nonCreditEmailIndicator = marketingConsentDetailsToUpdate.nonCreditEmailIndicator
            nonCreditSmsIndicator = marketingConsentDetailsToUpdate.nonCreditSmsIndicator
            nonCreditAutoVoiceIndicator = marketingConsentDetailsToUpdate.nonCreditAutoVoiceIndicator
            creditIndicator = marketingConsentDetailsToUpdate.creditIndicator
            creditEmailIndicator = marketingConsentDetailsToUpdate.creditEmailIndicator
            creditSmsIndicator = marketingConsentDetailsToUpdate.creditSmsIndicator
            creditTeleIndicator = marketingConsentDetailsToUpdate.creditTeleIndicator
            manageProfileInteractor.updateMarketingConsent(this, updateProfileExtendedResponseListener)
        }
    }

    fun retrieveSouthAfricanTaxDetails() {
        var financialInformation = FinancialInformation()
        customerInformation.value?.customerInformation?.financialInformation?.let { financialInformation = it }

        southAfricanTaxDetails.apply {
            isTaxNumberAvailable = financialInformation.isSaTaxNoAvailable
            reasonForNoTaxNumberCode = financialInformation.reasonNotGivenSaTaxNumber
            taxNumber = financialInformation.saIncomeTaxNumber
            areYouRegisteredForSouthAfricanTax = financialInformation.areYouRegisteredSaTax
            southAfricanTax.value = this
        }
    }

    fun modifySouthAfricanTaxDetails() {
        var localSouthAfricanTaxDetails = SouthAfricanTaxDetails()
        southAfricanTax.value?.let { localSouthAfricanTaxDetails = it }

        southAfricanTaxDetails.apply {
            areYouRegisteredForSouthAfricanTax = localSouthAfricanTaxDetails.areYouRegisteredForSouthAfricanTax
            isTaxNumberAvailable = localSouthAfricanTaxDetails.isTaxNumberAvailable
            taxNumber = localSouthAfricanTaxDetails.taxNumber
            reasonForNoTaxNumberCode = localSouthAfricanTaxDetails.reasonForNoTaxNumberCode
            reasonForNoTaxNumber = localSouthAfricanTaxDetails.reasonForNoTaxNumber
        }
    }

    fun updateNextOfKinDetails(nextOfKinDetails: NextOfKinDetails) {
        manageProfileInteractor.updateNextOfKin(nextOfKinDetails, updateProfileExtendedResponseListener)
    }

    fun retrieveQualificationList(): SelectorList<LookupItem> {
        qualificationListLookupTable = convertLookupTableToInputView(qualificationListLookupResult.items)
        return qualificationListLookupTable
    }

    fun retrieveOccupationList(): SelectorList<LookupItem> {
        occupationListLookupTable = convertLookupTableToInputView(occupationListLookupResult.items)
        return occupationListLookupTable
    }

    fun retrieveOccupationSectorList(): SelectorList<LookupItem> {
        occupationSectorListLookupTable = convertLookupTableToInputView(occupationSectorListLookupResult.items)
        return occupationSectorListLookupTable
    }

    fun retrieveOccupationLevelList(): SelectorList<LookupItem> {
        occupationLevelListLookupTable = convertLookupTableToInputView(occupationLevelListLookupResult.items)
        return occupationLevelListLookupTable
    }

    fun retrieveOccupationStatusList(): SelectorList<LookupItem> {
        occupationStatusListLookupTable = convertLookupTableToInputView(occupationStatusListLookupResult.items)
        return occupationStatusListLookupTable
    }

    fun updateEmploymentInformation() {
        updateEmploymentInformationModel()
        manageProfileInteractor.updateEmploymentInfo(manageProfileEducationDetailsModel, updateProfileExtendedResponseListener)
    }

    private fun updateEmploymentInformationModel() {
        var employmentInformation = EmploymentInformation()
        customerInformation.value?.customerInformation?.employmentInformation?.let {
            employmentInformation = it
        }
        manageProfileEducationDetailsModel.clientType = customerInformation.value?.customerInformation?.personalInformation?.clientType.toString()

        when {
            manageProfileEducationDetailsFlow == ManageProfileEducationDetailsFlow.EDUCATION -> {
                mergeEmploymentDetailsData(employmentInformation)
                mergeEmployerDetailsData(employmentInformation)
            }
            manageProfileEducationDetailsFlow == ManageProfileEducationDetailsFlow.EMPLOYMENT && isEmploymentFlowAddressRemoved && !wasAddressFieldsChangedInEmploymentFlow -> {
                mergeEducationDetailsData(employmentInformation)
            }
            manageProfileEducationDetailsFlow == ManageProfileEducationDetailsFlow.EMPLOYMENT && wasAddressFieldsChangedInEmploymentFlow -> {
                mergeEducationDetailsData(employmentInformation)
            }
            manageProfileEducationDetailsFlow == ManageProfileEducationDetailsFlow.EMPLOYMENT -> {
                mergeEducationDetailsData(employmentInformation)
                mergeEmployerDetailsData(employmentInformation)
            }
            manageProfileEducationDetailsFlow == ManageProfileEducationDetailsFlow.EMPLOYER -> {
                mergeEducationDetailsData(employmentInformation)
                mergeEmploymentDetailsData(employmentInformation)
            }
        }
    }

    private fun mergeEducationDetailsData(employmentInformation: EmploymentInformation) {
        manageProfileEducationDetailsModel.apply {
            hasPostMatricQualification = hasPostMatricQualification.ifEmpty { employmentInformation.postMetricInd }
            highestQualificationCode = highestQualificationCode.ifEmpty { employmentInformation.highestLvlOfEducation }
        }
    }

    private fun mergeEmploymentDetailsData(employmentInformation: EmploymentInformation) {
        manageProfileEducationDetailsModel.apply {
            occupationStatusCode = occupationStatusCode.ifEmpty { employmentInformation.occupationStatus }
            occupationCode = occupationCode.ifEmpty { employmentInformation.occupation }
            occupationSectorCode = occupationSectorCode.ifEmpty { employmentInformation.occupationSector }
            occupationLevelCode = occupationLevelCode.ifEmpty { employmentInformation.occupationLevel }
        }
    }

    private fun mergeEmployerDetailsData(employmentInformation: EmploymentInformation) {
        manageProfileEducationDetailsModel.apply {
            employerAddressLineOne = employerAddressLineOne.ifEmpty { employmentInformation.employerAddressDTO.addressLine1 }
            employerAddressLineTwo = employerAddressLineTwo.ifEmpty { employmentInformation.employerAddressDTO.addressLine2 }
            employerSuburb = employerSuburb.ifEmpty { employmentInformation.employerAddressDTO.suburbRsa }
            employerTown = employerTown.ifEmpty { employmentInformation.employerAddressDTO.town }
            employerPostalCode = employerPostalCode.ifEmpty { employmentInformation.employerAddressDTO.postalCode }
            employerTelephoneCode = employerTelephoneCode.ifEmpty { employmentInformation.workTelephoneCode }
            employerTelephoneNumber = employerTelephoneNumber.ifEmpty { employmentInformation.workNumber }
            employerFaxCode = employerFaxCode.ifEmpty { employmentInformation.faxWorkCode }
            employerFaxNumber = employerFaxNumber.ifEmpty { employmentInformation.workFaxNumber }
        }
    }

    private fun buildJsonRequestForForeignTax(taxDetails: ForeignTaxCountryDetails): String = ObjectMapper().writeValueAsString(taxDetails)

    private fun updateFinancialDetails(manageProfileUpdateFinancialDetails: ManageProfileUpdateFinancialDetails) {
        manageProfileInteractor.updateFinancialInfo(manageProfileUpdateFinancialDetails, updateProfileExtendedResponseListener)
    }

    private fun retrieveForNoTaxNumber(reason: String): String {
        return if (reason != "0") {
            val taxReasonIndex = taxReasons.indexOfFirst { reason == it.reasonCode }
            taxReasons[taxReasonIndex].reasonDescription
        } else {
            ""
        }

    }

    fun getLookupValue(lookupTable: LookupResult, cifValue: String): String {
        var lookupValue = ""
        lookupTable.items.forEach { lookupItem ->
            if (lookupItem.itemCode == cifValue) {
                lookupValue = if (lookupItem.defaultLabel.equals("Mail", true)) "Post" else lookupItem.defaultLabel.toTitleCase()
            }
        }
        return lookupValue
    }

    fun getLookupItem(lookupResult: LookupResult, cifValue: String): LookupItem {
        var lookupItem = LookupItem()
        lookupResult.items.forEach {
            if (it.itemCode == cifValue) {
                lookupItem = it
            }
        }
        return lookupItem
    }

    private fun uploadDocuments(index: Int) {
        if (BuildConfig.STUB) {
            Handler(Looper.getMainLooper()).postDelayed({
                docHandlerInteractor.submitDocument(documents[index], bcmsCaseId.value?.password, manageProfileDocHandlerUploadResponse)
            }, 250)
        } else {
            docHandlerInteractor.submitDocument(documents[index], bcmsCaseId.value?.password, manageProfileDocHandlerUploadResponse)
        }

        documentIndex = index + 1
    }

    fun fetchCase() {
        if (!bcmsCaseId.value?.caseID.isNullOrEmpty() && !bcmsCaseId.value?.password.isNullOrEmpty()) {
            docHandlerInteractor.getDocumentByCaseId(bcmsCaseId.value?.caseID, bcmsCaseId.value?.password, manageProfileDocHandlerGetCaseExtendedResponseListener)
        }
    }

    fun docHandlerResponse(docHandlerGetCaseByIdResponse: DocHandlerGetCaseByIdResponse) {
        if (docHandlerGetCaseByIdResponse.requiredDocuments.size == 2) {
            val proofOfResidenceListIndex = docHandlerGetCaseByIdResponse.requiredDocuments.indexOfFirst { it.displayName?.contains("Address") == true }
            proofOfResidenceBCMSData = docHandlerGetCaseByIdResponse.requiredDocuments[if (proofOfResidenceListIndex != -1) proofOfResidenceListIndex else 0]
            val idListIndex = docHandlerGetCaseByIdResponse.requiredDocuments.indexOfFirst { it.displayName?.contains("ID") == true }
            proofOfIdentityBCMSData = docHandlerGetCaseByIdResponse.requiredDocuments[if (idListIndex != -1) idListIndex else 1]
        }

        docHandlerResponse.value = docHandlerGetCaseByIdResponse
    }

    fun uploadDocument(docHandlerUploadDocumentResponse: DocHandlerUploadDocumentResponse) {
        if (documentIndex < documents.size) {
            uploadDocuments(documentIndex)
        } else if (documentIndex == documents.size) {
            docHandlerUploadResponse.value = docHandlerUploadDocumentResponse
        }
    }

    fun retrieveProofOfResidenceSourceList(): ArrayList<ManageProfileDocHandlerDocumentType> {
        return retrieveSourceList(proofOfResidenceBCMSData.availableSubTypes)
    }

    fun retrieveProofOfIdentificationSourceList(): ArrayList<ManageProfileDocHandlerDocumentType> {
        return retrieveSourceList(proofOfIdentityBCMSData.availableSubTypes)
    }

    private fun retrieveSourceList(availableSubType: ArrayList<AvailableSubTypes>?): ArrayList<ManageProfileDocHandlerDocumentType> {
        val listOfDocumentTypes = arrayListOf<ManageProfileDocHandlerDocumentType>()
        availableSubType?.forEach {
            ManageProfileDocHandlerDocumentType().apply {
                displayName = it.displayName.toString()
                description = it.description.toString()
                id = it.id.toString()
                listOfDocumentTypes.add(this)
            }
        }
        return listOfDocumentTypes
    }

    private fun convertLookupTableToInputView(lookupItemList: List<LookupItem>?): SelectorList<LookupItem> {
        val lookupList = SelectorList<LookupItem>()

        lookupItemList?.forEach { lookupItem ->
            if (lookupItem.defaultLabel?.contains("---") == true) {
                return@forEach
            }
            if (lookupItem.defaultLabel.equals("Mail", true)) {
                lookupItem.defaultLabel = "Post"
            }
            lookupList.add(LookupItem(lookupItem.itemCode, lookupItem.groupCode, lookupItem.defaultLabel.toTitleCase(), lookupItem.cmsKey))
        }
        return lookupList
    }

    fun createDocuments() {
        createDocHandlerDocumentForID()
        createDocHandlerDocumentForProofOfResidence()
        uploadDocuments(0)
    }

    fun downloadFile(manageProfileDocHandlerFileToFetchDetails: ManageProfileDocHandlerFileToFetchDetails): LiveData<FileResponse> {
        return liveData(Dispatchers.IO) {
            val fileResponse = docHandlerFileDownloadInteractor.downloadFileAsync(manageProfileDocHandlerFileToFetchDetails)
            emit(fileResponse)
        }
    }

    private fun createDocHandlerDocumentForID() {
        DocHandlerDocument().apply {
            caseId = bcmsCaseId.value?.caseID.toString()
            docId = bcmsCaseId.value?.documentUploadStatus?.first()?.documentId
            docSubType = idDocumentType.id
            description = PROOF_OF_IDENTIFICATION
            fileName = proofOfIdentityFileDetails.fileName
            uploadDocument = proofOfIdentityFile
            documents.add(this)
        }
    }

    private fun createDocHandlerDocumentForProofOfResidence() {
        DocHandlerDocument().apply {
            caseId = bcmsCaseId.value?.caseID.toString()
            docId = bcmsCaseId.value?.documentUploadStatus?.get(1)?.documentId
            docSubType = proofOfResidenceType.id
            description = PROOF_OF_PHYSICAL_RESIDENCE
            fileName = proofOfResidenceFileDetails.fileName
            uploadDocument = proofOfResidenceFile
            documents.add(this)
        }
    }

    fun clearData() {
        currentLookupIndex = 0
        documentIndex = 0
        customerInformation = MutableLiveData()
        personalInformation = MutableLiveData()
        updateCustomerInformation = MutableLiveData()
        customerProfilePictureResult = MutableLiveData()
        bcmsCaseId = MutableLiveData()
        manageProfileFlow = null
        foreignTaxDetails = MutableLiveData()
        preProcessedPersonalInformation = PersonalInformationDisplay()
        originalForeignTaxDetails = MutableLiveData()
        foreignTaxDetails = MutableLiveData()
        originalDisplayForeignTaxDetails = arrayListOf()
        displayForeignTaxDetails = arrayListOf()
        southAfricanTax = MutableLiveData()
        confirmScreenItemList.clear()
        proofOfIdentityFile = null
        proofOfResidenceFile = null
        educationDetails = MutableLiveData()
        originalEducationDetailsDisplayInformation = EducationDetailsDisplayInformation()
        currentEducationAndEmploymentLookupIndex = 0
        docHandlerResponse = MutableLiveData()
    }

    fun fireProfileSetupRequest(languageCode: String, customerName: String, backgroundImageId: String, profileViewImageHelper: ProfileViewImageHelper) {
        settingsHubInteractor.requestProfileSetup(profileViewImageHelper.isPhotoDeleted, profileViewImageHelper.isPhotoUpdated,
                profileViewImageHelper.bitmap, languageCode, customerName, backgroundImageId, customerProfilePictureExtendedResponseListener)
    }
}