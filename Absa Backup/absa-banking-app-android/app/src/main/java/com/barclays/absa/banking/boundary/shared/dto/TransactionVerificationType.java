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
package com.barclays.absa.banking.boundary.shared.dto;

public enum TransactionVerificationType {
    SURECHECKV1("SURECHECKV1"),
    SURECHECKV1Required("SURECHECKV1Required"),
    TVMRequired("TVMRequired"),
    SURECHECKV1_FALLBACKRequired("SURECHECKV1_FALLBACKRequired"),
    SURECHECKV1_FALLBACK("SURECHECKV1_FALLBACK"),
    SURECHECKV2("SURECHECKV2"),
    SURECHECKV2_FALLBACK("SURECHECKV2_FALLBACK"),
    SURECHECKV2Required("SURECHECKV2Required"),
    SURECHECKV2_FALLBACKRequired("SURECHECKV2_FALLBACKRequired"),
    SecurityCode("SecurityCode"),
    NotNeeded("NotNeeded"),
    Failed("Failed"),
    NoPrimaryDevice("NoPrimaryDevice");

    private final String key;

    TransactionVerificationType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}