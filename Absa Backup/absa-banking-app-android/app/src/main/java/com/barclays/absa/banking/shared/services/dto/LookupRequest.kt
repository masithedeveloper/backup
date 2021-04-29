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
package com.barclays.absa.banking.shared.services.dto

import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.api.request.params.RequestParams
import com.barclays.absa.banking.funeralCover.services.FuneralCoverQuoteService.GROUP_CODE_KEY
import com.barclays.absa.banking.funeralCover.services.FuneralCoverQuoteService.LANGUAGE_CODE_KEY
import com.barclays.absa.banking.shared.services.SharedService.Companion.OP0861_LOOKUP

class LookupRequest<T>(cifGroupCode: CIFGroupCode,
                       responseListener: ExtendedResponseListener<T>) : ExtendedRequest<T>(responseListener) {
    init {
        val languageCode = CustomerProfileObject.instance.languageCode
        params = RequestParams.Builder()
                .put(OP0861_LOOKUP)
                .put(GROUP_CODE_KEY, cifGroupCode.key)
                .put(LANGUAGE_CODE_KEY, languageCode)
                .build()

        when (cifGroupCode.key) {
            CIFGroupCode.TITLE.key -> mockResponseFile = "shared/op0861_get_title_codes.json"
            CIFGroupCode.SOURCE_OF_FUNDS.key -> mockResponseFile = "shared/op0861_get_source_of_funds.json"
            CIFGroupCode.OCCUPATION.key -> mockResponseFile = "shared/op0861_get_occupation_code.json"
            CIFGroupCode.OCCUPATION_STATUS.key -> mockResponseFile = "shared/op0861_get_occupation_status.json"
            CIFGroupCode.OCCUPATION_LEVEL.key -> mockResponseFile = "shared/op0861_get_occupation_level.json"
            CIFGroupCode.EMPLOYMENT_SECTOR.key -> mockResponseFile = "shared/op0861_get_employment_sector.json"
            CIFGroupCode.POST_MATRIC_QUALIFICATION.key -> mockResponseFile = "shared/op0861_get_post_matric_qualification.json"
            CIFGroupCode.EMPLOYMENT_SECTOR.key -> mockResponseFile = "shared/op0861_get_occupation_status.json"
            CIFGroupCode.HOME_LANG.key -> mockResponseFile = "shared/op0861_get_home_language.json"
            CIFGroupCode.NATIONALITY.key -> mockResponseFile = "shared/op0861_get_nationality.json"
            CIFGroupCode.MARITAL_STATUS.key -> mockResponseFile = "shared/op0861_get_marital_status.json"
            CIFGroupCode.COUNTRY_PASSPORT.key -> mockResponseFile = "shared/op0861_get_passport_country.json"
            CIFGroupCode.PREFERRED_COMMUNICATION.key -> mockResponseFile = "shared/op0861_get_preferred_communication.json"
            CIFGroupCode.MONTHLY_INCOME_GROUP.key -> mockResponseFile = "shared/op0861_get_monthly_income_group.json"
            CIFGroupCode.SOURCE_OF_INCOME_I.key -> mockResponseFile = "shared/op0861_get_monthly_income_group_i.json"
        }

        printRequest()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getResponseClass(): Class<T> = LookupResult::class.java as Class<T>

    override fun isEncrypted(): Boolean = true
}