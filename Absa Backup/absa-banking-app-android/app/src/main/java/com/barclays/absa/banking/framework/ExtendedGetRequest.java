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
package com.barclays.absa.banking.framework;

import com.barclays.absa.banking.framework.api.request.params.RequestParams;

public class ExtendedGetRequest<T> extends ExtendedRequest<T> {

    public ExtendedGetRequest(ExtendedResponseListener<T> responseListener) {
        super(responseListener);
        isGetRequest = true;
    }

    public ExtendedGetRequest(String serviceUrl, ExtendedResponseListener<T> responseListener) {
        super(serviceUrl, responseListener);
        isGetRequest = true;
    }

    public ExtendedGetRequest(String serviceUrl, RequestParams requestParams, ExtendedResponseListener<T> responseListener) {
        super(serviceUrl, requestParams, responseListener);
        isGetRequest = true;
    }

    public ExtendedGetRequest(RequestParams requestParams, ExtendedResponseListener<T> responseListener) {
        super(requestParams, responseListener);
        isGetRequest = true;
    }

}