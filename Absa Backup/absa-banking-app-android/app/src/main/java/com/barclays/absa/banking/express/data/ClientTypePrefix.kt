/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.express.data

enum class ClientTypePrefix(val clientTypeCode: String) {
    PRIVATE_INDIVIDUAL("1"),
    STAFF("4"),
    JOINT_AND_SEVERAL("3"),
    STAFF_JOINT_AND_SEVERAL("6"),
    SOLE_PROPRIETOR("81"),
    FARMER("84"),
    ABSA_BANK_OFFICE_ACCOUNTS("43"),
    ABSA_GROUP_ACCOUNTS("30"),
    ABSA_PROPERTY_IN_POSSESSION("50"),
    BANKS("41"),
    CENTRAL_BANKS("48"),
    CENTRAL_GOVERNMENT("65"),
    CLOSE_CORPORATION("31"),
    CLUBS("75"),
    CO_OPERATIVES("83"),
    COMPANY_LIMITED_BY_GUARANTEE("32"),
    CORPORATION_FOR_PUBLIC_DEPOSITS("69"),
    FINANCIAL_GOVERNMENT_ENTERPRISES("62"),
    INCORPORATED_COMPANY("29"),
    INSTITUTIONS_OF_HIGHER_EDUCATION("66"),
    INSURANCE_COMPANY("25"),
    LANDBANK("58"),
    LOCAL_GOVERNMENT("68"),
    MEDICAL_AID_COMPANIES("40"),
    NON_FINANCIAL_GOVERNMENT_ENTERPRISES("60"),
    NON_PROFIT_COMPANY_AND_FRIENDLY_SOCIETIES("76"),
    NON_RESIDENT_ENTITY("85"),
    STATE_OWNED_COMPANY("86"),
    OTHER_FINANCIAL_INTERMEDIARIES("49"),
    PENSION_OR_PROVIDENT_FUND("26"),
    PRIVATE_COMPANY("23"),
    PROVINCIAL_GOVERNMENT("64"),
    PUBLIC_COMPANY("22"),
    PUBLIC_INVESTMENTS_COMMISSIONERS("61"),
    SCHOOLS("80"),
    SOCIAL_SECURITY_FUNDS("70"),
    TRUSTS("28"),
    PARTNERSHIP("78"),
    ESTATE_LATE("15")
}

fun String.isSoleProprietor(): Boolean {
    return startsWith(ClientTypePrefix.SOLE_PROPRIETOR.clientTypeCode) || startsWith(ClientTypePrefix.FARMER.clientTypeCode)
}