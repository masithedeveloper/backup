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
package com.barclays.absa.banking.account.services

enum class AccountRequestParameters constructor(val key: String) {
    ACCOUNTS("accounts"),
    ACCOUNT_NUMBER("accountNumber"),
    IS_LINKED("isLinked"),
    LINK_STATE_CHANGED("linkingStateChanged"),
    ACCOUNT_TYPE("accountType"),
    TYPE("type"),
    HAS_ORDER_CHANGED("hasOrderChanged"),
    HAS_ACCOUNT_LINKING_STATES_CHANGED("hasAccountLinkingStateChanged"),
    TO_BE_LINKED_ACCOUNTS("toBeLinkedAccounts"),
    TO_BE_UNLINKED_ACCOUNTS("toBeUnLinkedAccounts"),
    TO_BE_LINKED_ACCOUNT_TYPES("toBeLinkedAccountType"),
    TO_BE_UNLINKED_ACCOUNT_TYPES("toBeUnLinkedAccountType")
}