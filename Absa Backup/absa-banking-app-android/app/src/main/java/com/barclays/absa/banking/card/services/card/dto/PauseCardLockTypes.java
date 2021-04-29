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
package com.barclays.absa.banking.card.services.card.dto;

public enum PauseCardLockTypes {

    PLACE_HOLD_ON_CARD("P"),
    REMOVE_HOLD_ON_CARD("D"),
    NO_HOLD_ON_CARDS("N"),
    PLACE_HOLD_ON_ITEM("Y"),
    REMOVE_HOLD_ON_ITEM("N");

    private final String key;

    PauseCardLockTypes(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
