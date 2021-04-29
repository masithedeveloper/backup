/*
 * Copyright (c) 2021 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 */

package com.barclays.absa.banking.express.shared.dto

import com.fasterxml.jackson.annotation.JsonProperty

enum class InstructionType(val value: String) {
    @JsonProperty("VP")
    NORMAL("VP"),

    @JsonProperty("OOP")
    ONCE_OFF_PAYMENT("OOP"),

    @JsonProperty("PPI")
    PREPAID("PPI"),

    @JsonProperty("PPE")
    PREPAID_ELECTRICITY("PPE"),

    @JsonProperty("PPO")
    PREPAID_ELECTRICITY_ONCE_OFF("PPO"),

    @JsonProperty("CLI")
    CASHSEND_BENEFICIARY("CLI"),

    @JsonProperty("CLR")
    CASHSEND_ONCE_OFF("CLR"),

    @JsonProperty("XFR")
    INTER_ACCOUNT_TRANSFER("XFR"),

    @JsonProperty("MPP")
    MOBILE_PROXY_PAYMENT("MPP"),

    @JsonProperty("PTP")
    PERSON_TO_PERSON("PTP"),

    @JsonProperty("ATC")
    ADULT_TO_CHILD("ATC"),

    @JsonProperty("SO")
    STOP_ORDER("SO"),

    @JsonProperty("TOI")
    TRANSFER_OUT("TOI")
}