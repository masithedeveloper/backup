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
import com.barclays.absa.banking.manage.profile.ui.models.ContactInfoModel

class ManageProfileUpdateContactInfoRequest<T>(contactInfoModel: ContactInfoModel, extendedResponseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(extendedResponseListener) {
    init {
        params = RequestParams.Builder(OP2107_UPDATE_PROFILE)
                .put("action", "UPDATE_CONTACT_INFO")
                .put("clientType", contactInfoModel.clientType)
                .put("residentialAddressLine1", contactInfoModel.residentialAddressLine1)
                .put("residentialAddressLine2", contactInfoModel.residentialAddressLine2)
                .put("residentialSuburbRsa", contactInfoModel.residentialSuburbRsa)
                .put("residentialTown", contactInfoModel.residentialTown)
                .put("residentialPostalCode", contactInfoModel.residentialPostalCode)
                .put("country", contactInfoModel.residentialCountry)
                .put("postalAddressLine1", contactInfoModel.postalAddressLine1)
                .put("postalAddressLine2", contactInfoModel.postalAddressLine2)
                .put("postalSuburbRsa", contactInfoModel.postalSuburbRsa)
                .put("postalTown", contactInfoModel.postalTown)
                .put("postalPostalCode", contactInfoModel.postalPostalCode)
                .put("preferredContactMethod", contactInfoModel.preferredContactMethod)
                .put("homeTelephoneCode", contactInfoModel.homeTelephoneCode)
                .put("homeTelephoneNumber", contactInfoModel.homeTelephoneNumber)
                .put("homeFaxCode", contactInfoModel.homeFaxCode)
                .put("cellNumber", contactInfoModel.cellNumber)
                .put("emailAddress", contactInfoModel.emailAddress)
                .put("homeFaxNumber", contactInfoModel.homeFaxNumber)
                .build()

        mockResponseFile = "shared/op2107_success_response.json"
        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass() = UpdatedFields::class.java as Class<T>

    override fun isEncrypted() = true
}