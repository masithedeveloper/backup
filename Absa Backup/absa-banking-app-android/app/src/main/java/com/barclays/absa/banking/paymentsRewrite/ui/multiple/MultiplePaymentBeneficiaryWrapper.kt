/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package com.barclays.absa.banking.paymentsRewrite.ui.multiple

import com.barclays.absa.banking.express.beneficiaries.dto.RegularBeneficiary
import com.barclays.absa.utils.DateTimeHelper
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import za.co.absa.networking.jackson.JsonDateFormat
import za.co.absa.networking.jackson.JsonDateSerializer
import java.math.BigDecimal
import java.util.*

class MultiplePaymentBeneficiaryWrapper {
    lateinit var beneficiaryViewItem: BeneficiaryViewItem

    var regularBeneficiary: RegularBeneficiary = RegularBeneficiary()
    var paymentAmount: BigDecimal = BigDecimal(0)
    var isImmediate: Boolean = false
    var useTime: Boolean = false

    @JsonSerialize(using = JsonDateSerializer::class)
    @JsonDateFormat(dateFormat = DateTimeHelper.DASHED_PATTERN_YYYY_MM_DD_HH_MM)
    var paymentTransactionDateAndTime: Date = Date()
}