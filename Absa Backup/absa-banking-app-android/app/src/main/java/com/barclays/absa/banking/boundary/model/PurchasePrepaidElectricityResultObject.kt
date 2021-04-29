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
import java.io.Serializable

class PurchasePrepaidElectricityResultObject : ResponseObject(), Serializable {

    var benId: String? = null
    var benName: String? = null
    var fromAccountNumber: String? = null
    var amount: String? = null
    var accountType: String? = null
    var meterNumber: String? = null
    var serviceProvider: String? = null
    var myNoticeMethod: String? = null
    var myCellNumber: String? = null
    var myEmail: String? = null
    var myFaxCode: String? = null
    var myFaxNumber: String? = null
    var benNoticeTyp: String? = null
    var benCellNumber: String? = null
    var benEmail: String? = null
    var benFaxCode: String? = null
    var benFaxNumber: String? = null
    var isHasImage: Boolean = false
    var imageName: String? = null
    var arrearsAmount: Amount? = null
    var accountDescription: String? = null

}
