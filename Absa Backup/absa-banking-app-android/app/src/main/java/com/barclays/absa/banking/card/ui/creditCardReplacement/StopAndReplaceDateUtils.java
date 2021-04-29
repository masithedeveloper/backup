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

package com.barclays.absa.banking.card.ui.creditCardReplacement;

public class StopAndReplaceDateUtils {
    static String extractFirstDigitsOfDate(String input) {
        String lastUsedDate = "";
        if (input != null) {
            lastUsedDate = input.substring(0, 2);
        }
        return lastUsedDate;
    }

    static String extractLastUsedDate(String input) {
        String lastUsedDate = "";
        if (input != null) {
            lastUsedDate = input.substring(2);
        }
        return lastUsedDate;
    }

    static String extractIncidentDate(String input) {
        String incidentDate = "";
        if (input != null) {
            incidentDate = input.substring(2);
        }
        return incidentDate;
    }
}
