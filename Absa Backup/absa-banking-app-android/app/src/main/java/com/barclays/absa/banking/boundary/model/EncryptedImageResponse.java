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
package com.barclays.absa.banking.boundary.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EncryptedImageResponse {

    @JsonProperty("iVal")
    private String iVal;
    @JsonProperty("data")
    private String data;

    public String getiVal() {
        return iVal;
    }

    public String getData() {
        return data;
    }
}