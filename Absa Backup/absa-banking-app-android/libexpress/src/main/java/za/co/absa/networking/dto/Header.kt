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

package za.co.absa.networking.dto

import za.co.absa.networking.ExpressNetworkingConfig

data class Header(val applicationId: String = "AXOBMOBAPP",
                  val userAgent: String = "ANDROID - ${ExpressNetworkingConfig.appVersion}",
                  var language: String = "en",
                  var sourceip: String = "",
                  var service: String = "",
                  var operation: String = "",
                  var jsessionid: String = "",
                  var nonce: String = "",
                  var paginationContext: PaginationContext? = null)