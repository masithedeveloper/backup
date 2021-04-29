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

package com.barclays.absa.banking.newToBank.services.dto

import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty

class AbsaRewardsResponse : ResponseObject() {
    @JsonProperty("Description1")
    var description1: String = ""
    @JsonProperty("Description2")
    var description2: String = ""
    @JsonProperty("Description3")
    var description3: String = ""
    @JsonProperty("Description4")
    var description4: String = ""
    @JsonProperty("Description5")
    var description5: String = ""
    @JsonProperty("Description6")
    var description6: String = ""
    @JsonProperty("Description7")
    var description7: String = ""
    @JsonProperty("Description8")
    var description8: String = ""
    @JsonProperty("Description9")
    var description9: String = ""
    var packages: ArrayList<Packages> = ArrayList()
}
