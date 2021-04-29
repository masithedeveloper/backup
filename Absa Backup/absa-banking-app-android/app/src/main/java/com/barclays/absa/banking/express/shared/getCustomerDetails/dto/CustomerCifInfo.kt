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

import com.barclays.absa.banking.express.shared.updateMarketingConsentDetails.dto.MarketingConsentRequest
import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty

class CustomerCifInfo : BaseModel {
    @JsonProperty("cIFClientDetailsVO")
    var clientDetails: ClientDetails = ClientDetails()

    @JsonProperty("personalInformationVO")
    val personalDetails: CifPersonalInfo = CifPersonalInfo()

    @JsonProperty("contactInformationVO")
    val contactInfo: ContactInfo = ContactInfo()

    @JsonProperty("employmentInformationVO")
    val employmentInfo: EmploymentInfo = EmploymentInfo()

    @JsonProperty("nextOfKinVO")
    var nextOfKinDetails: NextOfKinDetails = NextOfKinDetails()

    @JsonProperty("financialDetailsInputVO")
    val financeDetails: FinanceDetails = FinanceDetails()

    @JsonProperty("taxDetailsVOList")
    val taxDetails: List<TaxDetails> = emptyList()

    val taxDetailsExist: Boolean = false
    val raceIndicator: String = ""

    @JsonProperty("marketingConsentDetailsVO")
    val marketingConsent: MarketingConsentRequest = MarketingConsentRequest()

    val cifKey: String = ""
}