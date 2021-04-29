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
 */

package com.barclays.absa.banking.express.behaviouralRewards.fetchVoucherPartnerMetaData.dto

import za.co.absa.networking.dto.BaseResponse

open class BehaviouralRewardsMarketingConsentRequestResponse : BaseResponse() {
    var nonCreditIndicator: String = ""
    var nonCreditSMS: String = ""
    var nonCreditEmail: String = ""
    var nonCreditAVoice: String = ""
    var nonCreditTel: String = ""
    var nonCreditPost: String = ""
    var nonCreditDateChange: String = ""
    var creditIndicator: String = ""
    var creditSMS: String = ""
    var creditEmail: String = ""
    var creditAVoice: String = ""
    var creditTel: String = ""
    var creditPost: String = ""
    var creditDateChange: String = ""
}