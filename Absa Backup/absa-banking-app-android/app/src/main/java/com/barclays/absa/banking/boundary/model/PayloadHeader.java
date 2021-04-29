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

public class PayloadHeader {

    @JsonProperty("resId")
    private String responseId;
    @JsonProperty("resCde")
    private String responseCode;
    @JsonProperty("resMsg")
    private String responseMessage;
    @JsonProperty("serVer")
    private String serverVersion;
    @JsonProperty("expTrace")
    private String exceptionTrace;

    public String getResponseId() {
        return responseId;
    }
    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public String getResponseCode() {
        return responseCode;
    }
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getServerVersion() {
        return serverVersion;
    }
    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public String getExceptionTrace() {
        return exceptionTrace;
    }
    public void setExceptionTrace(String exceptionTrace) {
        this.exceptionTrace = exceptionTrace;
    }
}
