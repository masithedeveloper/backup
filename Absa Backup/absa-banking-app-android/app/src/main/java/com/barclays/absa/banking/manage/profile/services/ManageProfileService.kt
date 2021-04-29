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

import com.barclays.absa.banking.manage.profile.services.dto.MarketingIndicator
import com.barclays.absa.banking.manage.profile.services.dto.NextOfKinDetails
import com.barclays.absa.banking.manage.profile.services.dto.PersonalInformation
import com.barclays.absa.banking.manage.profile.services.responselisteners.CustomerProfileExtendedResponseListener
import com.barclays.absa.banking.manage.profile.services.responselisteners.FicCaseIdExtendedResponseListener
import com.barclays.absa.banking.manage.profile.services.responselisteners.SuburbLookupExtendedResponseListener
import com.barclays.absa.banking.manage.profile.services.responselisteners.UpdateProfileExtendedResponseListener
import com.barclays.absa.banking.manage.profile.ui.models.ContactInfoModel
import com.barclays.absa.banking.manage.profile.ui.models.ManageProfileEducationDetailsModel
import com.barclays.absa.banking.manage.profile.ui.models.ManageProfileUpdateFinancialDetails
import com.barclays.absa.banking.manage.profile.ui.models.ManageProfileUpdateProfileModel

interface ManageProfileService {
    fun fetchUserProfileDetails(extendedResponseListener: CustomerProfileExtendedResponseListener)
    fun updatePersonalInformation(manageProfileUpdateProfileModel: ManageProfileUpdateProfileModel, personalInformation: PersonalInformation, updateProfileExtendedResponseListener: UpdateProfileExtendedResponseListener)
    fun updateContactInfo(contactInfoModel: ContactInfoModel, updateProfileExtendedResponseListener: UpdateProfileExtendedResponseListener)
    fun updateEmploymentInfo(manageProfileEducationDetailsModel: ManageProfileEducationDetailsModel, updateProfileExtendedResponseListener: UpdateProfileExtendedResponseListener)
    fun updateMarketingConsent(marketingIndicator: MarketingIndicator, updateProfileExtendedResponseListener: UpdateProfileExtendedResponseListener)
    fun updateNextOfKin(nextOfKinDetails: NextOfKinDetails, updateProfileExtendedResponseListener: UpdateProfileExtendedResponseListener)
    fun updateFinancialInfo(financialDetails: ManageProfileUpdateFinancialDetails, updateProfileExtendedResponseListener: UpdateProfileExtendedResponseListener)
    fun fetchSuburbs(suburb: String, extendedResponseListener: SuburbLookupExtendedResponseListener)
    fun fetchCaseID(extendedResponseListener: FicCaseIdExtendedResponseListener)

    companion object ManageProfileServiceParameters {
        const val OP2177_CUSTOMER_INFORMATION = "OP2177"
        const val OP2107_UPDATE_PROFILE = "OP2107"
        const val OP2099_GET_SUBURBS = "OP2099"
        const val OP2116_GET_OR_CREATE_FICA_CASE = "OP2116"
    }
}