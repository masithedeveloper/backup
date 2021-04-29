/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */

package styleguide.content

open class BeneficiaryListItem {
    var name: String? = null
    var accountNumber: String? = null
    var lastTransactionDetail: String? = null

    constructor()

    constructor(name: String?, accountNumber: String?, lastTransactionDetail: String?) {
        this.name = name
        this.accountNumber = accountNumber
        this.lastTransactionDetail = lastTransactionDetail
    }
}