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
package com.barclays.absa.banking.virtualpayments.scan2Pay.services.helpers

import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.banking.virtualpayments.scan2Pay.ui.ScanToPayViewModel
import com.entersekt.scan2pay.PullPayment

object ScanToPayHelper {

    // Using these logs when communicating with Entersekt, will be deleted in the future.
    fun logCleanResponse(pullPayment: PullPayment, qrCode: String) {
        val tag = ScanToPayViewModel.FEATURE_NAME
        BMBLogger.d(tag, "----------------------------------------------------------------")
        BMBLogger.d(tag, String.format("%-25s", "QR Code:") + qrCode)

        with(pullPayment) {
            BMBLogger.d(tag, String.format("%-25s", "Amount:") + this.amount)
            BMBLogger.d(tag, String.format("%-25s", "CurrencyCode:") + this.currencyCode)
            BMBLogger.d(tag, String.format("%-25s", "Description:") + this.description)
            BMBLogger.d(tag, String.format("%-25s", "MerchantName:") + this.merchantName)
            BMBLogger.d(tag, String.format("%-25s", "PayerInputRequired:") + this.payerInputRequired)
            BMBLogger.d(tag, String.format("%-25s", "PayerReferenceRequired:") + this.payerReferenceRequired)
            BMBLogger.d(tag, String.format("%-25s", "PinRequired:") + this.pinRequired)
            BMBLogger.d(tag, String.format("%-25s", "SourceOfFunds:") + this.sourceOfFunds)
            BMBLogger.d(tag, String.format("%-25s", "Tip:") + this.tip)
            BMBLogger.d(tag, String.format("%-25s", "Title:") + this.title)
            BMBLogger.d(tag, "----------------------------------------------------------------")
        }
    }
}
