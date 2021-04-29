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

package com.barclays.absa.banking.express.shared.updateCustomerDetails.dto

import com.barclays.absa.banking.express.shared.getCustomerDetails.dto.*
import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty

class CustomerDetailsUpdateRequest : BaseModel {
    var cifKey: String = ""

    @JsonProperty("cifClientInfoVO")
    var clientInfo = ClientDetails()

    @JsonProperty("contactDetailsVO")
    var contactDetails = ContactDetails()

    var pricingCode: String = ""
    var productCode: String = ""

    @JsonProperty("employmentVO")
    var employmentDetails = EmploymentDetails()

    @JsonProperty("nextOfKinInfoVO")
    var nextOfKinInfo = NextOfKinDetails()

    @JsonProperty("personalInfoVO")
    var personalDetails = PersonalDetails()

    @JsonProperty("financeInfoVO")
    var financeInfo = FinanceDetails()

    @JsonProperty("taxDetailsInputVOList")
    var taxDetails = emptyList<TaxDetails>()
    var raceIndicator: String = ""
}