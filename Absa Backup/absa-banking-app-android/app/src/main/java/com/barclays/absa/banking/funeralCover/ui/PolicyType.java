/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.funeralCover.ui;

public enum PolicyType {

    SHORT_TERM("ST"), EXERGY("Exergy"), LONG_TERM("LI");

    String value;

    PolicyType(String value) {
        this.value = value;
    }

    public static PolicyType getPolicyType(String key) {
        for (PolicyType policyType : PolicyType.values()) {
            if (policyType.value.equalsIgnoreCase(key)) {
                return policyType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
