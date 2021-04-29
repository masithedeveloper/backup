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

import com.barclays.absa.banking.shared.BaseModel
import com.fasterxml.jackson.annotation.JsonProperty

class NextOfKinDetails : BaseModel {
    @JsonProperty("nextKinRelationName")
    var nextOfKinRelationName: String = ""
    var surname: String = ""
    var firstNames: String = ""
    var relationship: String = ""
    var homeTelephoneCode: String = ""
    var homeTelephoneNumber: String = ""
    var workTelephoneCode: String = ""
    var workTelephoneNumber: String = ""

    @JsonProperty("cellphoneNbr")
    var cellphoneNumber: String = ""
    var participantTitle: String = ""
    var participantInitials: String = ""
    var participantSurname: String = ""
    var email: String = ""
    var relatedToPartSurname: String = ""
    var relatedToPartInitials: String = ""
    var relatedToPartTitleCode: String = ""
    var contactDetail: String = ""
}