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

import com.barclays.absa.banking.framework.data.ResponseObject
import com.google.gson.annotations.SerializedName

open class OnceOffPaymentObject : ResponseObject() {

    var fromAccounts: List<AccountObject>? = null

    var feeKey: String? = null
    var feeName: String? = null
    var feeAmount: String? = null
    var cellNo: String? = null
    var benOptType: String? = null
    var email: String? = null
    var faxCode: String? = null
    @SerializedName("fax_Number")
    var faxNumber: String? = null

}
