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

public enum TransactionVerificationMethod {
    IS_TRANSACTION_VERIFICATION_NEEDED("isTransactionVerificationNeeded"),
    GET_TRANSACTION_VERIFICATION_STATE("getTransactionVerificationState"),
    RESEND_TRANSACTION_VERIFICATION("resendTransactionVerification");

    private final String key;

    TransactionVerificationMethod(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}