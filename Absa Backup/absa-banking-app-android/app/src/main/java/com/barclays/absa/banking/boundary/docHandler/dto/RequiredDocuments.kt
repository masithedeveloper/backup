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

class RequiredDocuments {
    @JsonProperty("DocumentInstance")
    var documentInstance: DocumentInstance? = null
    @JsonProperty("AvailableSubTypes")
    var availableSubTypes: ArrayList<AvailableSubTypes>? = null
    @JsonProperty("Description")
    var description: String? = null
    @JsonProperty("DisplayName")
    var displayName: String? = null
    @JsonProperty("DocumentTypeId")
    var documentTypeId: String? = null
    @JsonProperty("IsEditable")
    var isEditable: Boolean = false
}