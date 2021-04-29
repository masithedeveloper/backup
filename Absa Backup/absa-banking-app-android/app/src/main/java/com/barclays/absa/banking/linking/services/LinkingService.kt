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
package com.barclays.absa.banking.linking.services

import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.linking.services.dto.BioReferenceResponse
import com.barclays.absa.banking.linking.services.dto.LinkedProfilesRequestDetails
import com.barclays.absa.banking.linking.services.dto.LinkedProfilesResponse

interface LinkingService {

    companion object {
        const val OP2206_FETCH_LINKED_PROFILES_BY_ID_NUMBER = "OP2206"
        const val OP2207_GET_BIO_REFERENCE = "OP2207"
    }

    fun fetchLinkedProfilesByIdNumber(details: LinkedProfilesRequestDetails, responseListener: ExtendedResponseListener<LinkedProfilesResponse>)
    fun fetchBioReference(responseListener: ExtendedResponseListener<BioReferenceResponse>)
}