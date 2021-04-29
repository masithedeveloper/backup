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
package com.barclays.absa.banking.express.secondaryCard.updateSecondaryCard.dto

class UpdateSecondaryCardMandateRequest {
    var primaryPlastic: String = ""
    var secondaryPlastic: String = ""
    var secondaryTenantMandate: String = ""
    var secondaryCardUpdateMandateDetailsList: ArrayList<SecondaryCardsRequest> = arrayListOf()
    var additionalSureCheckParameters: HashMap<String, Any> = HashMap()
}

class SecondaryCardsRequest(var additionalPlasticNumber: String = "",
                            var additionalTenantMandate: String = "")