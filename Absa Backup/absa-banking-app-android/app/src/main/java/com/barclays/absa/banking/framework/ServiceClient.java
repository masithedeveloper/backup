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

public class ServiceClient {

    private AbstractServiceTask serviceTask;

    public ServiceClient(String url, ExtendedRequest request) {
        serviceTask = ServiceTaskFactory.create(url, request);
    }

    public ServiceClient(ExtendedRequest request) {
        serviceTask = ServiceTaskFactory.create(request);
    }

    public ServiceClient(ExtendedRequest request, AbstractServiceTask.QueueCallBack callBack) {
        serviceTask = ServiceTaskFactory.create(request, callBack);
    }

    public void submitRequest() {
        serviceTask.execute();
    }
}