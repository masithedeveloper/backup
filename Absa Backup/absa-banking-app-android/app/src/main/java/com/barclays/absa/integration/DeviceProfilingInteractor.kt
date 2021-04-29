/*
 *  Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.integration

import android.content.Context
import android.os.Handler
import com.barclays.absa.banking.BuildConfig
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.barclays.absa.banking.framework.app.BMBApplication
import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache.featureSwitchingToggles
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates
import com.barclays.absa.banking.framework.utils.BMBLogger
import com.barclays.absa.integration.deviceScoring.DeviceProfilingScoringInteractor
import com.trusteer.tas.*
import com.trusteer.tas.atas.*
import java.util.*

class DeviceProfilingInteractor : DeviceProfilingService {

    private var backgroundThreadHandler: Handler? = null
    private var nextActionCallback: NextActionCallback? = null

    companion object {
        @JvmStatic
        var IBMRiskAssessmentSessionPointer = TAS_OBJECT_REF()
    }

    override fun initialize(context: Context): Boolean {
        val initFlags = tas.TAS_INIT_EXTRA_DRA_DATA

        val client = TAS_CLIENT_INFO().apply {
            vendorId = BuildConfig.vendorId
            clientId = BuildConfig.clientId
            clientKey = BuildConfig.clientKey
        }

        val overlayCallback = OverlayFoundCallbackInterface { _, _, _ ->
            //TODO: implement overlay detection action
        }
        val exceptionCallback = ExceptionCallbackInterface { exceptionMessage ->
            run {
                MonitoringInteractor().logMonitoringEvent("DEVICE_PROFILING_EXCEPTION", hashMapOf(Pair("exceptionMessage", exceptionMessage)))
            }
        }
        val deviceProfilingCallbacks: Map<Int, Any> = mapOf(
                Pair(TAS_EXCEPTION_CALLBACK_KEY, exceptionCallback),
                Pair(TAS_OVERLAY_CALLBACK_KEY, overlayCallback)
        )

        val app = context as BMBApplication
        backgroundThreadHandler = Handler(app.backgroundHandlerThread.looper)
        try {
            client.comment = null
            val sdkInitializationResult = TasInitializeWithCallbacks(app, client, initFlags, deviceProfilingCallbacks)
            BMBLogger.d("DeviceProfilingInteractor#initialize", "Initialization result was: $sdkInitializationResult")
            return if (sdkInitializationResult == tas.TAS_RESULT_SUCCESS) {
                app.isDeviceProfilingActive = true
                true
            } else {
                BMBLogger.d("DeviceProfilingInteractor#initialize", "Initialization failed...")
                MonitoringInteractor().logMonitoringEvent("DEVICE_PROFILING_INITIALIZATION_FAILURE", hashMapOf(
                        Pair("initializationResultCode", sdkInitializationResult)
                ))
                false
            }
        } catch (e: RuntimeException) {
            BMBLogger.e("DeviceProfilingInteractor#initialize", e.message)
        } catch (e: Error) {
            BMBLogger.e("DeviceProfilingInteractor#initialize", e.message)
        }
        return false
    }

    override fun disable() {
        BMBApplication.getInstance().isDeviceProfilingActive = false
    }

    private fun createSession(sessionId: String) {
        destroySession()
        val riskAssessmentCreateSessionResult = tas.TasRaCreateSession(IBMRiskAssessmentSessionPointer, sessionId)
        BMBLogger.d("DeviceProfilingInteractor#createSession", "Create session result was: $riskAssessmentCreateSessionResult")
        if (riskAssessmentCreateSessionResult != tas.TAS_RESULT_SUCCESS) {
            BMBLogger.d("DeviceProfilingInteractor initialization failed", "CSID: $sessionId")
            MonitoringInteractor().logMonitoringEvent("DEVICE_PROFILING_INITIALIZATION_FAILURE", hashMapOf(
                    Pair("createSessionResultCode", riskAssessmentCreateSessionResult)
            ))
        } else {
            MonitoringInteractor().logCustomEvent("DEVICE_PROFILING_CREATE_SESSION_SUCCESS")
            BMBLogger.d("DeviceProfilingInteractor create session was successful", "CSID: $sessionId")
        }
    }

    override fun createPreLoginSession(sessionId: String?) {
        if (BMBApplication.getInstance().isDeviceProfilingActive) {
            backgroundThreadHandler?.post {
                BMBLogger.d("DeviceProfilingInteractor creating pre-login session...", "CSID: $sessionId")
                createSession(sessionId ?: "")
                notifyLogin()
            }
        }
    }

    override fun createPostLoginSession(sessionId: String?, permanentUserId: String?, userId: String?) {
        if (BMBApplication.getInstance().isDeviceProfilingActive) {
            backgroundThreadHandler?.post {
                BMBLogger.d("DeviceProfilingInteractor creating post-login session...", "CSID<->PUID<->UID: $sessionId<->$permanentUserId<->$userId")
                createSession(sessionId ?: "")
                notifyInSession()
            }
        }
    }

    override fun notifyLogin() {
        notifyDeviceProfilingActivity(DeviceProfilingUserActivityType.LOGIN)
    }

    private fun notifyDeviceProfilingActivity(activityType: DeviceProfilingUserActivityType) {
        if (BMBApplication.getInstance().isDeviceProfilingActive) {
            backgroundThreadHandler?.post {
                val deviceProfilingActivityDataPointer = TAS_RA_ACTIVITY_DATA_REF()
                val result1: Int = tas.TasRaCreateActivityData(deviceProfilingActivityDataPointer)
                BMBLogger.d("DeviceProfilingInteractor#notifyDeviceProfilingActivity", "Create activity Result was: $result1")
                BMBLogger.d("DeviceProfilingInteractor calling notify...", activityType.typeName)
                val activityData: TAS_RA_ACTIVITY_DATA? = if (activityType == DeviceProfilingUserActivityType.LOGIN) deviceProfilingActivityDataPointer._value else deviceProfilingActivityDataPointer._value
                var result: Int = tas.TasRaNotifyUserActivity(IBMRiskAssessmentSessionPointer._value, activityType.typeName, activityData, 30000)
                BMBLogger.d("DeviceProfilingInteractor#notifyDeviceProfilingActivity", "Notify Result was: $result")
                if (featureSwitchingToggles.monitorDeviceProfiling == FeatureSwitchingStates.ACTIVE.key && result != tas.TAS_RESULT_SUCCESS) {
                    MonitoringInteractor().logMonitoringEvent("DEVICE_PROFILING_NOTIFY_CALL_FAILED", hashMapOf(Pair("resultCode", result)))
                }
                result = tas.TasRaDestroyActivityData(deviceProfilingActivityDataPointer._value)
                BMBLogger.d("DeviceProfilingInteractor#notifyDeviceProfilingActivity", "Destroy activity data result was: $result")
            }
        }
    }

    override fun notifyInSession() {
        notifyDeviceProfilingActivity(DeviceProfilingUserActivityType.IN_SESSION)
    }

    override fun notifyAddBeneficiary() {
        notifyDeviceProfilingActivity(DeviceProfilingUserActivityType.ADD_PAYEE)
    }

    override fun notifyTransaction() {
        notifyDeviceProfilingActivity(DeviceProfilingUserActivityType.TRANSACTION)
    }

    override fun destroySession() {
        if (BMBApplication.getInstance().isDeviceProfilingActive) {
            val result = tas.TasRaDestroySession(IBMRiskAssessmentSessionPointer._value)
            BMBLogger.d("DeviceProfilingInteractor#destroySession", "Destroy session result was: $result")
        }
    }

    override fun callForDeviceProfilingScore(function: String) {
        if (BMBApplication.getInstance().isDeviceProfilingActive) {
            BMBLogger.d("DeviceProfilingInteractor#callForDeviceProfilingScore", "Calling for score for $function...")
            DeviceProfilingScoringInteractor().requestDeviceProfilingScore(function, object : ExtendedResponseListener<TransactionResponse>() {
                override fun onRequestStarted() {
                    if (nextActionCallback != null) {
                        super.onRequestStarted()
                    }
                }

                override fun onSuccess(successResponse: TransactionResponse) {
                    BMBLogger.d("DeviceProfilingInteractor#callForDeviceProfilingScore", "Status: " + successResponse.transactionStatus + ", message: " + successResponse.transactionMessage)
                    doNextActionAndReset()
                }

                override fun onFailure(failureResponse: ResponseObject) {
                    BMBLogger.d("DeviceProfilingInteractor#callForDeviceProfilingScore", "Call for score failed [${failureResponse.errorMessage}]")
                    val stringObjectHashMap = HashMap<String, Any?>()
                    stringObjectHashMap["failureReason"] = failureResponse.errorMessage
                    MonitoringInteractor().logMonitoringEvent("DEVICE_PROFILING_CALL_FOR_SCORE_FAILED", stringObjectHashMap)
                    doNextActionAndReset()
                }
            })
        } else {
            doNextActionAndReset()
        }
    }

    fun callForDeviceProfilingScoreForPayments() = callForDeviceProfilingScore("payment")

    fun callForDeviceProfilingScoreForPayments(action: NextActionCallback) {
        nextActionCallback = action
        callForDeviceProfilingScore("payment")
    }

    fun callForDeviceProfilingScoreForLogin(action: NextActionCallback) {
        nextActionCallback = action
        callForDeviceProfilingScore("login")
    }

    private fun doNextActionAndReset() {
        nextActionCallback?.onNextAction()
        nextActionCallback = null
    }

    interface NextActionCallback {
        fun onNextAction()
    }
}