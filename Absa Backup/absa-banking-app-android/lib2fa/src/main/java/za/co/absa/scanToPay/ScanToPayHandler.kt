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

import com.entersekt.scan2pay.*
import za.co.absa.twoFactor.TransaktEngine

object ScanToPayHandler {

    enum class FailureReasonCodes {
        END_USER_INPUT_DECLINED,
        TRANSAKT_AUTH_INVALID,
        END_USER_INPUT_EXPIRED,
        END_CODE_INVALID,
        END_TRN_TIMEOUT,
        END_TRN_ERROR_OTHER,
        END_SYS_UNAVAILABLE,
        END_USER_NO_ACCEPTED_CARDS
    }

    private lateinit var scanToPay: Scan2Pay
    private var transaktEngine: TransaktEngine? = null
    var scanToPayAuth: PullPayment? = null

    fun initialize(transaktEngine: TransaktEngine?) {
        if (!isInitialized()) {
            this.transaktEngine = transaktEngine
            val transaktSDK = transaktEngine?.sdk ?: return
            scanToPay = Scan2PayFactory.createScan2Pay(transaktSDK, transaktEngine.service)
        }
    }

    fun isInitialized(): Boolean = ScanToPayHandler::scanToPay.isInitialized

    fun requestPayment(code: String, token: String, scanToPayDelegate: ScanToPayDelegate) {
        val transaktEngine = transaktEngine ?: return
        if (!transaktEngine.isConnected) {
            transaktEngine.connect()
        }

        if (isInitialized()) {
            scanToPay.requestPayment(code, token, object : PaymentCallbacks {
                override fun doAuthorization(payment: Payment) = scanToPayDelegate.onPaymentAuthorizationRequestReceived(payment as PullPayment)
                override fun onDecline(result: PaymentResult) = scanToPayDelegate.onPaymentDeclined(result)
                override fun onError(cause: Cause) = scanToPayDelegate.onPaymentFailed(cause)
                override fun onSuccess(result: PaymentResult) = scanToPayDelegate.onPaymentSuccess(result)
                override fun onUpdate(update: Update) = scanToPayDelegate.onUpdateReceived(update)
            })
        } else {
            //what?
        }
    }

    fun cancelPayment() {
        scanToPayAuth?.reject()
    }

    fun authorize(sourceOfFunds: SourceOfFunds) {
        scanToPayAuth?.authorize(Authorization(sourceOfFunds))
    }
}