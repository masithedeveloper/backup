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

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractParams {

    private final HashMap<String, String> parameters = new HashMap<>();

    protected void put(String key, String value) {
        parameters.put(key, value);
    }

    public void put(Map<String, String> params) {
        parameters.putAll(params);
    }

    public HashMap<String, String> getParams() {
        return parameters;
    }
}
