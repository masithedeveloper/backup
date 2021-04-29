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

public enum TransactionVerificationState {

    RESENDREQUIRED("ResendRequired"),
    PROCESSING("Processing"),
    PROCESSED("Processed"),
    REVERT_BACK("RevertBack"),
    FAILED("Failed"),
    REJECTED("Rejected"),
    SUCCESS("Success"),
    FAILURE("Failure"),
    SEND_VERIFICATION("SEND_VERIFICATION"),
    SEND_FAILED("SEND_FAILED"),

    PROCESSING_CAPS("PROCESSING");

    private final String state;

    TransactionVerificationState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}