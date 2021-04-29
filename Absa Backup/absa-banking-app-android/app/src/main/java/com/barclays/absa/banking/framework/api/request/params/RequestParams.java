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
package com.barclays.absa.banking.framework.api.request.params;

import java.util.Map;

public class RequestParams extends ServiceParams {

    private RequestParams() {
    }

    public static class Builder {
        private final RequestParams requestParams = new RequestParams();

        public Builder() {
            requestParams.put(ServiceVersion.SERVICE_VERSION_2);
        }

        public Builder(String opCode) {
            this();
            requestParams.put(opCode);
        }

        public Builder put(@OpCode String opcode) {
            requestParams.put(opcode);
            return this;
        }

        public Builder put(Transaction transaction, String transactionValue) {
            requestParams.put(transaction, transactionValue);
            return this;
        }

        public Builder put(Map<String, String> params) {
            requestParams.put(params);
            return this;
        }

        public Builder put(String key, String value) {
            requestParams.put(key, value);
            return this;
        }

        public RequestParams build() {
            return requestParams;
        }
    }
}
