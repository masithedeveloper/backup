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
package com.barclays.absa.banking.boundary.model

import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.framework.data.ResponseObject
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class ApplicationVersionDetails : ResponseObject() {

    @JsonProperty("benCaching")
    var beneficiaryCaching: String? = null

    @JsonProperty("accCaching")
    var accountCaching: Boolean = false

    @JsonProperty("appVersionLst")
    var applicationVersionEntries = ArrayList<ApplicationVersionEntry>()

    @JsonProperty("nVal")
    var nVal: String? = null

    @JsonProperty("iVal")
    var iVal: String? = null

    var features = ArrayList<String>()
    var maintenanceDownTime: Boolean = false
    var currentMajorVersion: String? = "6.0"
    var customerSessionId: String? = ""
    var deviceProfilingEnabled: Boolean = BuildConfig.TOGGLE_DEF_DEVICE_PROFILING_ENABLED
    @JsonProperty("enterpriseSessionID")
    var enterpriseSessionId: String? = ""

}