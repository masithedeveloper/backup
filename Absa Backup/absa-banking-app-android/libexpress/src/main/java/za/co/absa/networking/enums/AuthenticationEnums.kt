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
package za.co.absa.networking.enums

enum class AliasTypeEnum {
    USERNAME,
    MOBILEAPP,
    FACEBOOK,
    LINKEDIN,
    TWITTER,
    ATMCARD,
    CASE_SENSITIVE_USERNAME,
    SSO,
    USSD,
    MOBILEAPP_SECONDFACTOR_ENABLED,
    ACCESSACCOUNT,
    WHATSAPP,
    ATMCARD_ALIASID,

    /** Permanent User Identity */
    PUID;
}

enum class CredentialTypeEnum {
    MOBILEAPP_5DIGIT_PIN,
    MOBILEAPP_TOUCHID
}