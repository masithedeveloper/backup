/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.boundary.monitoring

import com.barclays.absa.banking.boundary.model.CustomerProfileObject
import com.barclays.absa.banking.boundary.monitoring.MonitoringService.Companion.MONITORING_EVENT_ATTRIBUTE_NAME_ACCESS_ACCOUNT
import com.barclays.absa.banking.boundary.monitoring.MonitoringService.Companion.MONITORING_EVENT_ATTRIBUTE_NAME_DEVICE_ID
import com.barclays.absa.banking.boundary.monitoring.MonitoringService.Companion.MONITORING_EVENT_ATTRIBUTE_NAME_ERROR_LOCATION
import com.barclays.absa.banking.boundary.monitoring.MonitoringService.Companion.MONITORING_EVENT_ATTRIBUTE_NAME_ERROR_MESSAGE
import com.barclays.absa.banking.boundary.monitoring.MonitoringService.Companion.MONITORING_EVENT_ATTRIBUTE_NAME_ERROR_SERVICE
import com.barclays.absa.banking.boundary.monitoring.MonitoringService.Companion.MONITORING_EVENT_ATTRIBUTE_NAME_EXTRA_INFO
import com.barclays.absa.banking.boundary.monitoring.MonitoringService.Companion.MONITORING_EVENT_ATTRIBUTE_NAME_OPCODE
import com.barclays.absa.banking.boundary.monitoring.MonitoringService.Companion.MONITORING_EVENT_ATTRIBUTE_TRANSAKT
import com.barclays.absa.banking.boundary.monitoring.MonitoringService.Companion.MONITORING_EVENT_BIOMETRIC_ATTRIBUTES
import com.barclays.absa.banking.boundary.monitoring.MonitoringService.Companion.MONITORING_EVENT_NAME_BIOMETRIC_EVENT
import com.barclays.absa.banking.boundary.monitoring.MonitoringService.Companion.MONITORING_EVENT_NAME_EXPRESS
import com.barclays.absa.banking.boundary.monitoring.MonitoringService.Companion.MONITORING_EVENT_NAME_TECHNICAL
import com.barclays.absa.banking.boundary.monitoring.MonitoringService.Companion.MONITORING_EVENT_RESPONSE_LISTENER_GENERIC_ERROR
import com.barclays.absa.banking.framework.data.ResponseObject
import com.barclays.absa.crypto.SecureUtils
import com.barclays.absa.crypto.SymmetricCryptoHelper
import com.barclays.absa.crypto.SymmetricCryptoHelper.NEW_RELIC_SECURITY_KEY
import com.newrelic.agent.android.NewRelic
import za.co.absa.networking.ExpressNetworkingConfig
import za.co.absa.twoFactor.TransaktEngine
import java.util.*

class MonitoringInteractor : MonitoringService {

    @Deprecated("This should be replaced with logMonitoringEvent", ReplaceWith("logMonitoringEvent(name: String, eventData: Map<String, Any?>)"))
    fun logEvent(name: String, eventData: Map<String, Any?>) {
        NewRelic.recordCustomEvent(MonitoringService.MONITORING_EVENT_TYPE_CUSTOM, name, eventData)
    }

    fun logMonitoringEvent(name: String, eventData: HashMap<String, Any?>) {
        eventData["emCertId"] = TransaktEngine.getEmCert()
        NewRelic.recordCustomEvent(name, eventData)
    }

    fun logCaughtExceptionEvent(exception: Exception) {
        val eventMap = HashMap<String, Any?>()

        eventMap["message"] = exception.message

        if (exception.stackTrace.isNotEmpty()) {
            if (exception.stackTrace[0].className != null) {
                eventMap["class"] = exception.stackTrace[0].className
            }
            if (exception.stackTrace[0].methodName != null) {
                eventMap["method"] = exception.stackTrace[0].methodName
            }
            eventMap["lineNumber"] = exception.stackTrace[0].lineNumber
        }

        NewRelic.recordCustomEvent(MonitoringService.MONITORING_EVENT_TYPE_CAUGHT_EXCEPTIONS, eventMap)
    }

    override fun logEvent(name: String, response: ResponseObject?) {
        val eventMap = HashMap<String, Any?>()
        eventMap[MONITORING_EVENT_ATTRIBUTE_NAME_OPCODE] = response?.opCode
        eventMap[MONITORING_EVENT_ATTRIBUTE_NAME_ERROR_MESSAGE] = response?.errorMessage
        NewRelic.recordCustomEvent("monitoringEventTypeCustom", eventMap)
        logMonitoringEvent(name, eventMap)
    }

    override fun logEvent(name: String, transaktEMCert: String) {
        val eventMap = HashMap<String, Any?>()
        eventMap[MONITORING_EVENT_ATTRIBUTE_TRANSAKT] = transaktEMCert
        logMonitoringEvent(name, eventMap)
    }

    fun logExpressHttpErrorEvent(location: String?, serviceName: String?, error: String) {
        logExpressServiceErrorEvent(location, serviceName, "HTTP ERROR: $error")
    }

    fun logTechnicalEvent(location: String?, serviceName: String?, error: String) {
        val eventMap = HashMap<String, Any?>()
        eventMap[MONITORING_EVENT_ATTRIBUTE_NAME_ERROR_MESSAGE] = error
        eventMap[MONITORING_EVENT_ATTRIBUTE_NAME_ERROR_LOCATION] = location ?: ""
        eventMap[MONITORING_EVENT_ATTRIBUTE_NAME_ERROR_SERVICE] = serviceName ?: ""

        if (ExpressNetworkingConfig.isLoggedIn) {
            eventMap[MONITORING_EVENT_ATTRIBUTE_NAME_ACCESS_ACCOUNT] = SymmetricCryptoHelper.getInstance().encryptString(CustomerProfileObject.instance.accessAccount, NEW_RELIC_SECURITY_KEY)
        }

        eventMap[MONITORING_EVENT_ATTRIBUTE_NAME_DEVICE_ID] = SymmetricCryptoHelper.getInstance().encryptString(SecureUtils.getDeviceID(), NEW_RELIC_SECURITY_KEY)

        NewRelic.recordCustomEvent(MONITORING_EVENT_NAME_TECHNICAL, eventMap)
    }

    fun logExpressServiceErrorEvent(location: String?, serviceName: String?, error: String) {
        val eventMap = HashMap<String, Any?>()
        eventMap[MONITORING_EVENT_ATTRIBUTE_NAME_ERROR_MESSAGE] = error
        eventMap[MONITORING_EVENT_ATTRIBUTE_NAME_ERROR_LOCATION] = location ?: ""
        eventMap[MONITORING_EVENT_ATTRIBUTE_NAME_ERROR_SERVICE] = serviceName ?: ""

        if (ExpressNetworkingConfig.isLoggedIn) {
            eventMap[MONITORING_EVENT_ATTRIBUTE_NAME_ACCESS_ACCOUNT] = SymmetricCryptoHelper.getInstance().encryptString(CustomerProfileObject.instance.accessAccount, NEW_RELIC_SECURITY_KEY)
        }

        eventMap[MONITORING_EVENT_ATTRIBUTE_NAME_DEVICE_ID] = SymmetricCryptoHelper.getInstance().encryptString(SecureUtils.getDeviceID(), NEW_RELIC_SECURITY_KEY)

        NewRelic.recordCustomEvent(MONITORING_EVENT_NAME_EXPRESS, eventMap)
    }

    fun logBiometricCheckEvent(attributes: String) {
        val eventMap = HashMap<String, Any?>()
        eventMap[MONITORING_EVENT_BIOMETRIC_ATTRIBUTES] = attributes

        NewRelic.recordCustomEvent(MONITORING_EVENT_NAME_BIOMETRIC_EVENT, eventMap)
    }

    override fun logCustomErrorEvent(errorMessage: String) {
        val eventMap = HashMap<String, Any?>()
        eventMap[MONITORING_EVENT_ATTRIBUTE_NAME_ERROR_MESSAGE] = errorMessage
        NewRelic.recordCustomEvent("monitoringEventTypeCustom", eventMap)
    }

    override fun logCustomEvent(eventDetail: String) {
        val eventMap = HashMap<String, Any?>()
        eventMap[MONITORING_EVENT_NAME_TECHNICAL] = eventDetail
        NewRelic.recordCustomEvent("monitoringEventTypeCustom", eventMap)
    }

    override fun logEvent(name: String, response: ResponseObject?, extraInfo: String) {
        val eventMap = HashMap<String, Any?>()
        eventMap[MONITORING_EVENT_ATTRIBUTE_NAME_OPCODE] = response?.opCode
        eventMap[MONITORING_EVENT_ATTRIBUTE_NAME_ERROR_MESSAGE] = response?.errorMessage
        eventMap[MONITORING_EVENT_ATTRIBUTE_NAME_EXTRA_INFO] = extraInfo
        NewRelic.recordCustomEvent("monitoringEventTypeCustom", eventMap)
        logMonitoringEvent(name, eventMap)
    }

    override fun logEvent(name: String, response: ResponseObject?, extraInfo: Map<String, Any?>) {
        val eventMap = HashMap<String, Any?>()
        eventMap[MONITORING_EVENT_ATTRIBUTE_NAME_OPCODE] = response?.opCode
        eventMap[MONITORING_EVENT_ATTRIBUTE_NAME_ERROR_MESSAGE] = response?.errorMessage
        eventMap[MONITORING_EVENT_ATTRIBUTE_NAME_EXTRA_INFO] = extraInfo
        NewRelic.recordCustomEvent("monitoringEventTypeCustom", eventMap)
        logMonitoringEvent(name, eventMap)
    }

    fun logExtendedResponseListenerObjectNull(className: String) {
        val map = LinkedHashMap<String, Any?>()
        map["extendedResponseListenerName"] = className
        logMonitoringEvent(MONITORING_EVENT_RESPONSE_LISTENER_GENERIC_ERROR, map)
    }
}