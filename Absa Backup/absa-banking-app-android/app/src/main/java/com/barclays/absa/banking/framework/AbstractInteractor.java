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

import java.util.List;

public abstract class AbstractInteractor {

    protected boolean firstCall;
    protected boolean stubFailure;

    public void setFirstCall(boolean firstCall) {
        this.firstCall = firstCall;
    }

    protected void submitRequest(ExtendedRequest extendedRequest) {
        ServiceClient serviceClient = new ServiceClient(extendedRequest);
        serviceClient.submitRequest();
    }

    protected void submitRequest(ExtendedRequest extendedRequest, String mockfile) {
        extendedRequest.setMockResponseFile(mockfile);
        submitRequest(extendedRequest);
    }

    protected void submitQueuedRequests(ExtendedRequest... requests) {
        QueueServiceClient queueServiceClient = new QueueServiceClient();
        queueServiceClient.add(requests);
        queueServiceClient.processQueue();
    }

    protected void submitQueuedRequests(List<ExtendedRequest> requests) {
        QueueServiceClient queueServiceClient = new QueueServiceClient();
        queueServiceClient.add(requests);
        queueServiceClient.processQueue();
    }

    protected void submitQueuedRequests(List<ExtendedRequest> requests, QueueServiceClient.OnQueueCompletedListener onQueueCompletedListener) {
        QueueServiceClient queueServiceClient = new QueueServiceClient(onQueueCompletedListener);
        queueServiceClient.add(requests);
        queueServiceClient.processQueue();
    }
}