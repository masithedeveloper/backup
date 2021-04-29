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

public class ServiceParams extends OpCodeParams {

    private static final String SERVICE_VERSION_KEY = "serVer";
    private ServiceVersion serviceVersion;

    enum ServiceVersion {
        SERVICE_VERSION_2("2.0");
        public final String value;
        ServiceVersion(String value) {
            this.value = value;
        }
    }

    public void put(ServiceVersion serviceVersion) {
        put(SERVICE_VERSION_KEY, serviceVersion.value);
        this.serviceVersion = serviceVersion;
    }

    public ServiceVersion getServiceVersion() {
        return serviceVersion;
    }
}
