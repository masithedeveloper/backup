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

class ServiceTaskFactory {

    public static AbstractServiceTask create(String url, ExtendedRequest extendedRequest) {
        AbstractServiceTask serviceTask;
        if (BuildConfigHelper.STUB || extendedRequest.isForcedStubMode()) {
            serviceTask = new StubServiceTask(extendedRequest);
        } else {
            serviceTask = new HttpServiceTask(url, extendedRequest);
        }
        return serviceTask;
    }

    public static AbstractServiceTask create(ExtendedRequest extendedRequest) {
        AbstractServiceTask serviceTask;
        if (BuildConfigHelper.STUB || extendedRequest.isForcedStubMode()) {
            serviceTask = new StubServiceTask(extendedRequest);
        } else {
            serviceTask = new HttpServiceTask(extendedRequest);
        }
        return serviceTask;
    }

    public static AbstractServiceTask create(ExtendedRequest request, AbstractServiceTask.QueueCallBack callBack) {
        AbstractServiceTask serviceTask;
        if (BuildConfigHelper.STUB || request.isForcedStubMode()) {
            serviceTask = new StubServiceTask(request, callBack);
        } else {
            serviceTask = new HttpServiceTask(request, callBack);
        }
        return serviceTask;
    }
}