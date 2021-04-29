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
package com.barclays.absa.banking.buy.ui.airtime;

import androidx.annotation.NonNull;

import styleguide.forms.SelectorInterface;

public class PrepaidAmountWrapper implements SelectorInterface {
    private String value, code;

    public PrepaidAmountWrapper(@NonNull String amountString, @NonNull String translatedFor) {
        String[] element = amountString.split("/");
        code = element[0].trim();
        value = element[1].replace("for", translatedFor).trim();
        if (amountString.contains("MTN") && element.length > 2) {
            value += " / " + element[2];
        }
    }

    @Override
    public String getDisplayValue() {
        return value;
    }

    @Override
    public String getDisplayValueLine2() {
        return "";
    }

    public String getCode() {
        return code;
    }
}
