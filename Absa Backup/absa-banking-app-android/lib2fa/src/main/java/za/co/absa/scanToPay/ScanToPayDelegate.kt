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
package za.co.absa.scanToPay

import com.entersekt.scan2pay.Cause
import com.entersekt.scan2pay.PaymentResult
import com.entersekt.scan2pay.PullPayment
import com.entersekt.scan2pay.Update

interface ScanToPayDelegate {
    fun onPaymentAuthorizationRequestReceived(payment: PullPayment)
    fun onPaymentDeclined(declinedData: PaymentResult)
    fun onPaymentSuccess(successData: PaymentResult)
    fun onPaymentFailed(failureInfo: Cause)
    fun onUpdateReceived(updateInfo: Update)
}