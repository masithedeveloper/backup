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

class DocHandlerUploadDocumentResponse {
    var group: String? = ""
    var name: String? = null
    var type: String? = null
    var size: Double = 0.toDouble()
    var progress: String? = null
    var url: String? = null
    @JsonProperty("thumbnail_url")
    var thumbnailUrl: String? = null
    @JsonProperty("delete_url")
    var deleteUrl: String? = null
    @JsonProperty("delete_type")
    var deleteType: String? = null
    var error: String? = null
}