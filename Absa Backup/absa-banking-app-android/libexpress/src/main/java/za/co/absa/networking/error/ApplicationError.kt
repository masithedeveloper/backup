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
package za.co.absa.networking.error

data class ApplicationError(val errorType: ApplicationErrorType, val serviceName: String, val friendlyMessage: String, val actualMessage: String) {

    companion object {
        const val UNCLASSIFIED_ERROR = -1
        const val GENERAL_ERROR = 100
        const val CERT_PINNING_ERROR = 101

        private fun determineErrorType(throwable: Throwable): ApplicationErrorType {
            return when (throwable) {
                is javax.net.ssl.SSLPeerUnverifiedException ->
                    ApplicationErrorType.CERTIFICATE_PINNING
                else -> ApplicationErrorType.GENERAL
            }
        }
    }

    private val errorMap: HashMap<ApplicationErrorType, Int> = hashMapOf(
            ApplicationErrorType.UNCLASSIFIED to UNCLASSIFIED_ERROR,
            ApplicationErrorType.GENERAL to GENERAL_ERROR,
            ApplicationErrorType.CERTIFICATE_PINNING to CERT_PINNING_ERROR
    )

    var errorCode = errorMap[errorType] ?: -1

    constructor(errorType: ApplicationErrorType, serviceName: String, friendlyMessage: String) : this(errorType, serviceName, friendlyMessage, friendlyMessage)

    constructor(serviceName: String, throwable: Throwable) : this(determineErrorType(throwable), serviceName, throwable.message.toString(), throwable.message.toString())

    constructor(serviceName: String, friendlyMessage: String) : this(ApplicationErrorType.GENERAL, serviceName, friendlyMessage, "")

    constructor(serviceName: String, friendlyMessage: String, actualMessage: String) : this(ApplicationErrorType.GENERAL, serviceName, friendlyMessage, actualMessage)
}