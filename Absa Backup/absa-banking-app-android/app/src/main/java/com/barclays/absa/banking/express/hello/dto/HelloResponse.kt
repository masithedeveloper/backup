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

package com.barclays.absa.banking.express.hello.dto

import za.co.absa.networking.dto.BaseResponse

class HelloResponse : BaseResponse() {
    val hmacSecret: String = ""
    val publicKey: String = ""
    val publicKeyId: String = ""
    val fingerprintList: List<String> = arrayListOf()
    val appVersionVO: AppVersion = AppVersion()
    val configurationList: List<Configurations> = arrayListOf()
    val customerSessionId: String = ""
}