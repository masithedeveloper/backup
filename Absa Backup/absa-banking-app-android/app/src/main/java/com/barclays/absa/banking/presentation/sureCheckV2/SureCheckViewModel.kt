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

package com.barclays.absa.banking.presentation.sureCheckV2

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.boundary.shared.TransactionVerificationInteractor
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationGetVerificationStateResponse
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationResponse
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationState
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.app.BMBConstants
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService
import com.barclays.absa.banking.presentation.shared.BaseViewModel

class SureCheckViewModel : BaseViewModel() {

    private val transactionVerificationInteractor by lazy { TransactionVerificationInteractor() }
    private var hasPollReturned: Boolean = true
    private val appCacheService = getServiceInterface(IAppCacheService::class.java)

    var transactionVerificationStateLiveData: MutableLiveData<TransactionVerificationState> = MutableLiveData()

    companion object {
        const val RESPONSE_CODE_FOR_KNOWN_ERROR_RESPONSES = "FTR00980"
        const val REJECTION_ERROR_MESSAGE = "You selected the rejection option on your cellphone. The transaction has been cancelled"
        const val TIMEOUT_ERROR_MESSAGE = "We did not receive your verification message. Please resend the verification message or try again"
        const val SURE_CHECK_ALREADY_VERIFIED = "Surecheck transaction is already verified"
        const val SENDING_ERROR_MESSAGE = "Error sending Surecheck: null"
        const val PLEASE_RESEND_MESSAGE = "Please resend"
        const val OOPS_MESSAGE = "Oops!"
    }

    private val responseListener: ExtendedResponseListener<TransactionVerificationGetVerificationStateResponse> = object : ExtendedResponseListener<TransactionVerificationGetVerificationStateResponse>() {
        var oopsCount = 0
        val oopsCountLimit = 2

        override fun onRequestStarted() {}

        override fun onSuccess(successResponse: TransactionVerificationGetVerificationStateResponse) {
            val stateString = successResponse.transactionVerificationState
            if (stateString.isNotEmpty()) {
                try {
                    when (TransactionVerificationState.valueOf(stateString.toUpperCase(BMBApplication.getApplicationLocale()))) {
                        TransactionVerificationState.PROCESSING, TransactionVerificationState.PROCESSING_CAPS -> hasPollReturned = true
                        TransactionVerificationState.PROCESSED -> {
                            SureCheckHandler.stopTimer()
                            transactionVerificationStateLiveData.value = TransactionVerificationState.PROCESSED
                        }
                        TransactionVerificationState.REJECTED -> {
                            SureCheckHandler.stopTimer()
                            transactionVerificationStateLiveData.value = TransactionVerificationState.REJECTED
                        }
                        TransactionVerificationState.FAILED -> {
                            if (successResponse.txnMessage.contains(OOPS_MESSAGE) && oopsCount++ < oopsCountLimit) {
                                hasPollReturned = true
                            } else {
                                SureCheckHandler.stopTimer()
                                if (successResponse.txnMessage.contains(PLEASE_RESEND_MESSAGE)) {
                                    transactionVerificationStateLiveData.value = TransactionVerificationState.RESENDREQUIRED
                                } else {
                                    transactionVerificationStateLiveData.value = TransactionVerificationState.FAILED
                                }
                            }
                        }
                        else -> {
                            SureCheckHandler.stopTimer()
                            transactionVerificationStateLiveData.value = TransactionVerificationState.FAILED
                        }
                    }
                } catch (e: IllegalArgumentException) {
                    SureCheckHandler.stopTimer()
                    if (SURE_CHECK_ALREADY_VERIFIED.equals(successResponse.txnMessage, ignoreCase = true)) {
                        transactionVerificationStateLiveData.value = TransactionVerificationState.PROCESSED
                    } else {
                        transactionVerificationStateLiveData.value = TransactionVerificationState.FAILED
                    }
                }
            } else {
                SureCheckHandler.stopTimer()
                if (SURE_CHECK_ALREADY_VERIFIED.equals(successResponse.txnMessage, ignoreCase = true)) {
                    transactionVerificationStateLiveData.value = TransactionVerificationState.PROCESSED
                } else {
                    transactionVerificationStateLiveData.value = TransactionVerificationState.FAILED
                }
            }
        }

        override fun onFailure(failureResponse: ResponseObject) {
            if (RESPONSE_CODE_FOR_KNOWN_ERROR_RESPONSES.equals(failureResponse.responseCode, ignoreCase = true)) {
                SureCheckHandler.stopTimer()
                when {
                    TIMEOUT_ERROR_MESSAGE.equals(failureResponse.errorMessage, ignoreCase = true) -> transactionVerificationStateLiveData.value = TransactionVerificationState.RESENDREQUIRED
                    REJECTION_ERROR_MESSAGE.equals(failureResponse.errorMessage, ignoreCase = true) -> transactionVerificationStateLiveData.value = TransactionVerificationState.REJECTED
                    SENDING_ERROR_MESSAGE.equals(failureResponse.errorMessage, ignoreCase = true) -> transactionVerificationStateLiveData.value = TransactionVerificationState.FAILED
                    else -> transactionVerificationStateLiveData.value = TransactionVerificationState.FAILED
                }
            } else {
                hasPollReturned = true
            }
        }
    }

    private val transactionVerificationResendResponseListener: ExtendedResponseListener<TransactionVerificationResponse> = object : ExtendedResponseListener<TransactionVerificationResponse>() {
        override fun onRequestStarted() {}

        override fun onSuccess(response: TransactionVerificationResponse) {
            if (BMBConstants.FAILURE.equals(response.txnStatus, ignoreCase = true)) {
                transactionVerificationStateLiveData.value = TransactionVerificationState.FAILED
            } else if (BMBConstants.SUCCESS.equals(response.txnStatus, ignoreCase = true)) {
                appCacheService.apply {
                    setSureCheckCellphoneNumber(response.cellnumber)
                    setSureCheckEmail(response.email)
                    setSureCheckNotificationMethod(response.notificationMethod)
                    if (response.referenceNumber.isNotEmpty()) {
                        setSureCheckReferenceNumber(response.referenceNumber)
                    }

                    var type: TransactionVerificationType? = null
                    if (response.transactionVerificationType.isNotEmpty()) {
                        type = TransactionVerificationType.valueOf(response.transactionVerificationType.toUpperCase(BMBApplication.getApplicationLocale()))
                    }
                    if (type != null) {
                        appCacheService.getSureCheckDelegate()?.onResendSuccess(type)
                        when (type) {
                            TransactionVerificationType.SURECHECKV2,
                            TransactionVerificationType.SURECHECKV2Required,
                            TransactionVerificationType.SURECHECKV2_FALLBACK,
                            TransactionVerificationType.SURECHECKV2_FALLBACKRequired -> {
                                transactionVerificationStateLiveData.value = TransactionVerificationState.SEND_VERIFICATION
                            }
                            else -> {
                            }
                        }
                    }
                }
            }
        }

        override fun onFailure(response: ResponseObject) {
            transactionVerificationStateLiveData.value = TransactionVerificationState.FAILED
        }
    }

    fun resendSureCheck() {
        if (BMBApplication.getInstance().transaktInteractor?.isConnected == true) {
            BMBApplication.getInstance().transaktHandler.start()
        }

        if (appCacheService.getSecureHomePageObject() != null) {
            transactionVerificationInteractor.resendTransactionVerificationPostLogon(appCacheService.getSureCheckReferenceNumber(), transactionVerificationResendResponseListener)
        } else {
            transactionVerificationInteractor.resendTransactionVerification(appCacheService.getSureCheckReferenceNumber(), transactionVerificationResendResponseListener)
        }
    }

    fun resetPolling() {
        hasPollReturned = true
    }

    fun poll() {
        if (hasPollReturned) {
            hasPollReturned = false
            if (appCacheService.getSecureHomePageObject() != null) {
                transactionVerificationInteractor.checkVerificationStatusPostLogon(appCacheService.getSureCheckReferenceNumber(), responseListener)
            } else {
                transactionVerificationInteractor.checkVerificationStatus(appCacheService.getSureCheckReferenceNumber(), responseListener)
            }
        }
    }
}