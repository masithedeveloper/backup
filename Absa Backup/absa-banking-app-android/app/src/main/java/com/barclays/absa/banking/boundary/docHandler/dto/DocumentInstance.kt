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

package com.barclays.absa.banking.boundary.docHandler.dto

import com.fasterxml.jackson.annotation.JsonProperty

class DocumentInstance {
    @JsonProperty("InstanceId")
    var instanceId: String? = null
    @JsonProperty("ContentType")
    var contentType: Int = 0
    @JsonProperty("Thumbnail")
    var thumbnail: String? = null
    @JsonProperty("DocumentImage")
    var documentImage: String? = null
    @JsonProperty("DocumentSubTypeId")
    var documentSubTypeId: String? = null
    @JsonProperty("Name")
    var name: String? = null
    @JsonProperty("Description")
    var description: String? = null
    @JsonProperty("Category")
    var category: String? = null
    @JsonProperty("Status")
    var status: Int = 0
    @JsonProperty("IsLocked")
    var isLocked: Boolean = false
}
