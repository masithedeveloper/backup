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

package com.barclays.absa.banking.manage.profile.services

import com.barclays.absa.banking.framework.AbstractInteractor
import com.barclays.absa.banking.manage.profile.services.dto.*
import com.barclays.absa.banking.manage.profile.services.responselisteners.CustomerProfileExtendedResponseListener
import com.barclays.absa.banking.manage.profile.services.responselisteners.FicCaseIdExtendedResponseListener
import com.barclays.absa.banking.manage.profile.services.responselisteners.SuburbLookupExtendedResponseListener
import com.barclays.absa.banking.manage.profile.services.responselisteners.UpdateProfileExtendedResponseListener
import com.barclays.absa.banking.manage.profile.ui.models.ContactInfoModel
import com.barclays.absa.banking.manage.profile.ui.models.ManageProfileEducationDetailsModel
import com.barclays.absa.banking.manage.profile.ui.models.ManageProfileUpdateFinancialDetails
import com.barclays.absa.banking.manage.profile.ui.models.ManageProfileUpdateProfileModel

class ManageProfileInteractor : AbstractInteractor(), ManageProfileService {
    override fun fetchUserProfileDetails(extendedResponseListener: CustomerProfileExtendedResponseListener) {
        submitRequest(ManageProfileFetchPersonalDetailsRequest(extendedResponseListener))
    }

    override fun updatePersonalInformation(manageProfileUpdateProfileModel: ManageProfileUpdateProfileModel, personalInformation: PersonalInformation, updateProfileExtendedResponseListener: UpdateProfileExtendedResponseListener) {
        submitRequest(ManageProfileUpdatePersonalInformationRequest(manageProfileUpdateProfileModel, personalInformation, updateProfileExtendedResponseListener))
    }

    override fun updateContactInfo(contactInfoModel: ContactInfoModel, updateProfileExtendedResponseListener: UpdateProfileExtendedResponseListener) {
        submitRequest(ManageProfileUpdateContactInfoRequest(contactInfoModel, updateProfileExtendedResponseListener))
    }

    override fun updateEmploymentInfo(manageProfileEducationDetailsModel: ManageProfileEducationDetailsModel, updateProfileExtendedResponseListener: UpdateProfileExtendedResponseListener) {
        submitRequest(ManageProfileUpdateEmploymentInformationRequest(manageProfileEducationDetailsModel, updateProfileExtendedResponseListener))
    }

    override fun updateMarketingConsent(marketingIndicator: MarketingIndicator, updateProfileExtendedResponseListener: UpdateProfileExtendedResponseListener) {
        submitRequest(ManageProfileUpdateMarketingConsentRequest(marketingIndicator, updateProfileExtendedResponseListener))
    }

    override fun updateNextOfKin(nextOfKinDetails: NextOfKinDetails, updateProfileExtendedResponseListener: UpdateProfileExtendedResponseListener) {
        submitRequest(ManageProfileUpdateNextOfKinRequest(nextOfKinDetails, updateProfileExtendedResponseListener))
    }

    override fun updateFinancialInfo(financialDetails: ManageProfileUpdateFinancialDetails, updateProfileExtendedResponseListener: UpdateProfileExtendedResponseListener) {
        submitRequest(ManageProfileUpdateFinancialInfoRequest(financialDetails, updateProfileExtendedResponseListener))
    }

    override fun fetchSuburbs(suburb: String, extendedResponseListener: SuburbLookupExtendedResponseListener) {
        submitRequest(ManageProfileSuburbLookupRequest(suburb, extendedResponseListener))
    }

    override fun fetchCaseID(extendedResponseListener: FicCaseIdExtendedResponseListener) {
        submitRequest(CaseIdRequest(extendedResponseListener))
    }
}