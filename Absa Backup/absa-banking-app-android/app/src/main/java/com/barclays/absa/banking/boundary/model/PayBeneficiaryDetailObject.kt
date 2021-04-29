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

class PayBeneficiaryDetailObject : ResponseObject(), Serializable {

    var benSurname: String? = null
    var benNickname: String? = null
    var benCellnum: String? = null
    var acctNum: String? = null
    var acctNumAtInstitution: String? = null
    var branchName: String? = null
    var branchCode: String? = null
    var benReference: String? = null
    var selfReference: String? = null
    var benStatusType: String? = null
    var immediatePmtIndicator: String? = null
    var statusCode: String? = null
    var benSqNum: String? = null
    var benRef: String? = null
    var myRef: String? = null
    var isFav: Boolean = false
    var lastTxnAmt: Amount? = null
    var lastTxnDate: String? = null
    var benNam: String? = null
    var benId: String? = null
    var txnTyp: String? = null
    var image: String? = null
}
