/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.boundary.model.prepaidElectricity

import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty

class PrepaidElectricityBeneficiaryDetails : ResponseObject() {

    @JsonProperty("benName")
    var beneficiaryName: String? = null
    @JsonProperty("benId")
    var beneficiaryId: String? = null
    @JsonProperty("fromAccountNumber")
    var fromAccountNumber: String? = null
    @JsonProperty("amount")
    var amount: String? = null
    @JsonProperty("accountType")
    var accountType: String? = null
    @JsonProperty("meterNumber")
    var meterNumber: String? = null
    @JsonProperty("serviceProvider")
    var serviceProvider: String? = null
    @JsonProperty("myNoticeMethod")
    var myNoticeMethod: String? = null
    @JsonProperty("myCellNumber")
    var myCellNumber: String? = null
    @JsonProperty("myEmail")
    var myEmail: String? = null
    @JsonProperty("myFaxCode")
    var myFaxCode: String? = null
    @JsonProperty("myFaxNumber")
    var myFaxNumber: String? = null
    @JsonProperty("benNoticeMethod")
    var beneficiaryNoticeMethod: String? = null
    @JsonProperty("benCellNumber")
    var beneficiaryCellNumber: String? = null
    @JsonProperty("benEmail")
    var beneficiaryEmail: String? = null
    @JsonProperty("benFaxCode")
    var beneficiaryFaxCode: String? = null
    @JsonProperty("benFaxNumber")
    var beneficiaryFaxNumber: String? = null

}
