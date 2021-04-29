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

package com.barclays.absa.banking.manage.profile.services.dto

import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.manage.profile.services.ManageProfileService.ManageProfileServiceParameters.OP2107_UPDATE_PROFILE
import com.barclays.absa.banking.manage.profile.ui.models.ManageProfileUpdateProfileModel

class ManageProfileUpdatePersonalInformationRequest<T>(manageProfileUpdateProfileModel: ManageProfileUpdateProfileModel, personalInformation: PersonalInformation, extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {
    init {
        params = RequestParams.Builder(OP2107_UPDATE_PROFILE)
                .put("action", "UPDATE_PERSONAL_INFO")
                .put("clientType", manageProfileUpdateProfileModel.clientType)
                .put("cifKey", personalInformation.cifKey)
                .put("firstName", personalInformation.firstName)
                .put("initials", personalInformation.initials)
                .put("lastName", personalInformation.lastName)
                .put("dateOfBirth", personalInformation.dateOfBirth)
                .put("gender", personalInformation.gender)
                .put("identityNo", personalInformation.identityNo)
                .put("identityType", personalInformation.identityType)
                .put("maritalStatus", personalInformation.maritalStatus)
                .put("title", manageProfileUpdateProfileModel.title)
                .put("dependents", manageProfileUpdateProfileModel.dependents)
                .put("correspondenceLanguage", manageProfileUpdateProfileModel.correspondenceLanguage)
                .put("homeLanguage", manageProfileUpdateProfileModel.homeLanguage)
                .put("clientNationality", manageProfileUpdateProfileModel.clientNationality)
                .put("countryOfBirth", personalInformation.countryOfBirth)
                .build()

        mockResponseFile = "shared/op2107_success_response.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = UpdatedFields::class.java as Class<T>

    override fun isEncrypted() = true
}