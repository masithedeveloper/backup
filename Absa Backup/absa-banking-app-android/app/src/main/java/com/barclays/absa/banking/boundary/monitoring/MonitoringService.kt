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

import com.barclays.absa.banking.framework.data.ResponseObject

interface MonitoringService {
    fun logEvent(name: String, response: ResponseObject?)
    fun logEvent(name: String, response: ResponseObject?, extraInfo: String)
    fun logEvent(name: String, response: ResponseObject?, extraInfo: Map<String, Any?>)
    fun logEvent(name: String, transaktEMCert: String)
    fun logCustomErrorEvent(errorMessage: String)
    fun logCustomEvent(eventDetail: String)

    companion object {
        const val MONITORING_EVENT_TYPE_CAUGHT_EXCEPTIONS = "AndroidCaughtExceptions"
        const val MONITORING_EVENT_TYPE_CUSTOM = "ABACustomEvent"
        const val MONITORING_EVENT_NAME_LINKING_FAILED = "linkingFailed"
        const val MONITORING_EVENT_NAME_UNEXPECTED_ERROR_AT_LOGIN_LINKING = "unexpectedErrorAtLoginLinking"
        const val MONITORING_EVENT_NAME_UNEXPECTED_ERROR_AT_LOGIN_PASSCODE = "unexpectedErrorAtLoginPasscode"
        const val MONITORING_EVENT_NAME_NEGATIVE_FLOW_AUTH_FAILED = "negativeFlowAuthFailed"
        const val MONITORING_EVENT_NAME_TRANSAKT_TIME_TO_CONNECT = "TransaktTimeToConnect"
        const val MONITORING_EVENT_NAME_TRANSAKT_TIME_TO_CONNECT_START_TIME = "TransaktTimeToConnectStartTime"
        const val MONITORING_EVENT_NAME_TRANSAKT_TIME_TO_CONNECT_END_TIME = "TransaktTimeToConnectEndTime"
        const val MONITORING_EVENT_NAME_TRANSAKT_CONNECT_ERROR = "TransaktConnectError"
        const val MONITORING_EVENT_NAME_TRANSAKT_DISCONNECTED = "TransaktDisconnected"
        const val MONITORING_EVENT_NAME_AUTH_RECEIVED_FROM_NOTIFICATION_START_TIME = "authReceivedFromNotificationStartTime"
        const val MONITORING_EVENT_NAME_AUTH_RECEIVED_FROM_NOTIFICATION_END_TIME = "authReceivedFromNotificationEndTime"
        const val MONITORING_EVENT_NAME_AUTH_RECEIVED_ON_INITIATING_DEVICE_START_TIME = "authReceivedOnInitiatingDeviceStartTime"
        const val MONITORING_EVENT_NAME_AUTH_RECEIVED_ON_INITIATING_DEVICE_END_TIME = "authReceivedOnInitiatingDeviceEndTime"
        const val MONITORING_EVENT_NAME_TIME_TAKEN_FOR_AUTH_TO_BE_PROCESSED = "authTimeTakenToProcessSuccessfullyOnPollingDevice"
        const val MONITORING_EVENT_NAME_TIME_TAKEN_FOR_AUTH_TO_FAIL = "authTimeTakenToFailOnPollingDevice"
        const val MONITORING_EVENT_NAME_AUTH_FAILED = "authFailed"
        const val MONITORING_EVENT_NAME_SECURITY_CODE_FAILED = "securityCodeFailed"
        const val MONITORING_EVENT_NAME_SECURITY_CODE_SCREEN_SHOWN = "securityCodeScreenShown"
        const val MONITORING_EVENT_NAME_ERROR_RESPONSE = "errorResponse"
        const val MONITORING_EVENT_NAME_INCORRECT_AOL_LOGIN = "incorrectAOLLogin"
        const val MONITORING_EVENT_NAME_INCORRECT_ONLINE_BANKING_PASSWORD = "incorrectOnlineBankingPassword"
        const val MONITORING_EVENT_NAME_INCORRECT_PASSCODE = "incorrectPasscode"
        const val MONITORING_EVENT_NAME_ELECTRICITY_PURCHASE = "electricityPurchase"
        const val MONITORING_EVENT_NAME_REWARDS_REDEMPTION = "rewardsRedemption"
        const val MONITORING_EVENT_NAME_DIGITAL_PROFILE_ALREADY_EXISTS = "digitalProfileAlreadyExists"
        const val MONITORING_EVENT_NAME_REGISTRATION_FAILED = "registrationFailed"
        const val MONITORING_EVENT_NAME_OFFLINE_OTP_FALL_BACK_TRIGGERED = "offlineOtpFallbackTriggered"
        const val MONITORING_EVENT_NAME_EXCEPTION_CAUGHT = "exceptionCaught"

        const val MONITORING_EVENT_NAME_EXPRESS = "expressEvent"
        const val MONITORING_EVENT_NAME_TECHNICAL = "technicalEvent"

        const val MONITORING_EVENT_ATTRIBUTE_NAME_DEVICE_ID = "expressDeviceID"
        const val MONITORING_EVENT_ATTRIBUTE_NAME_ERROR_LOCATION = "errorLocation"
        const val MONITORING_EVENT_ATTRIBUTE_NAME_ERROR_SERVICE = "errorService"
        const val MONITORING_EVENT_ATTRIBUTE_NAME_ACCESS_ACCOUNT = "accessFingerprint"
        const val MONITORING_EVENT_ATTRIBUTE_NAME_ERROR_MESSAGE = "errorMessage"

        const val MONITORING_EVENT_ATTRIBUTE_TRANSAKT = "transakt"
        const val MONITORING_EVENT_ATTRIBUTE_TRANSAKT_SDK = "TransaktSDK"
        const val MONITORING_EVENT_ATTRIBUTE_TRANSAKT_ERROR = "TransaktError"

        const val MONITORING_EVENT_ATTRIBUTE_NAME_OPCODE = "opCode"
        const val MONITORING_EVENT_ATTRIBUTE_NAME_AUTH_TYPE = "authType"
        const val MONITORING_EVENT_ATTRIBUTE_NAME_ELAPSED_TIME = "elapsedTime"
        const val MONITORING_EVENT_ATTRIBUTE_NAME_REDEMPTION_TYPE = "redemptionType"
        const val MONITORING_EVENT_ATTRIBUTE_NAME_TIMESTAMP = "timeStamp"
        const val MONITORING_EVENT_ATTRIBUTE_AUTH_FAILURE_REASON = "authFailureReason"

        const val MONITORING_EVENT_ATTRIBUTE_AUTH_TYPE_VALUE_ATM_CARD_PIN = "atmCardPIN"
        const val MONITORING_EVENT_ATTRIBUTE_AUTH_TYPE_VALUE_PREVIOUS_PASSCODE = "previousPasscode"
        const val MONITORING_EVENT_ATTRIBUTE_REDEMPTION_TYPE_PREPAID = "prepaidRedemption"
        const val MONITORING_EVENT_ATTRIBUTE_REDEMPTION_TYPE_CASH = "cashRedemption"
        const val MONITORING_EVENT_ATTRIBUTE_NAME_EXTRA_INFO = "extraInfo"
        const val MONITORING_EVENT_ATTRIBUTE_NAME_APP_WENT_TO_BACKGROUND = "appWentToBackground"
        const val MONITORING_EVENT_ATTRIBUTE_NAME_INCORRECT_CURRENCY_CODE = "incorrectCurrencyCode"

        const val MONITORING_EVENT_RESPONSE_LISTENER_GENERIC_ERROR = "GenericError"

        const val MONITORING_EVENT_MANAGE_PROFILE_FAILURE_REASONS = "ManageProfileFailureReasons"
        const val MONITORING_EVENT_MANAGE_PROFILE_VALIDATION_ERROR_MESSAGE = "ManageProfileValidationErrorMessage"

        const val MONITORING_EVENT_NAME_BIOMETRIC_EVENT = "BioCheckEvent"
        const val MONITORING_EVENT_BIOMETRIC_ATTRIBUTES = "BioCheck"
    }

}