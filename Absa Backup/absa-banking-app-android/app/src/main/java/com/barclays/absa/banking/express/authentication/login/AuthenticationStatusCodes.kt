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

package com.barclays.absa.banking.express.authentication.login

enum class AuthenticationStatusCodes(val statusCode: String) {
    AUTHENTICATION_FAILED("700"),
    INVALID_CREDENTIALS("701"),
    SERVICE_SUSPENDED("702"),
    SERVICE_CANCELLED("703"),
    ACCOUNT_LOCKED("704"),
    SERVICE_SUSPENDED_UNPAID_FEES("705"),
    FRAUD_HOLD("706"),
    DUPLICATE_ACCOUNT("707"),
    PASSWORD_DIGITS_ERROR("708"),
    ACCOUNT_NOT_FOUND("709"),
    CARD_HOLDER_WITHOUT_MANDATE("710"),
    REVOKED("800"),
    CREDENTIAL_NOT_FOUND("801"),
    AUTHENTICATION_FAILED_IDP("802"),
    ALIAS_ID_NOT_FOUND("803"),
    MAPPED_USER_NOT_FOUND("804"),
    ALIAS_NOT_LINKED_TO_MAPPED_USER("805"),
    DEVICE_ID_NOT_FOUND_FOR_ALIAS_ID("806"),
    INACTIVE_ALIAS("807"),
    CREDENTIAL_DISABLED("808")
}