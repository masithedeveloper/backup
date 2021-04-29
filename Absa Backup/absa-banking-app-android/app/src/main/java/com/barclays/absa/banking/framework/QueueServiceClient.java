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

import android.os.Handler;
import android.os.Looper;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class QueueServiceClient implements AbstractServiceTask.QueueCallBack {

    private LinkedList<ServiceClient> clients;

    private ServiceClient activeClient;
    private OnQueueCompletedListener onQueueCompletedListener;

    public QueueServiceClient() {
    }

    public QueueServiceClient(OnQueueCompletedListener queueCompletedListener) {
        onQueueCompletedListener = queueCompletedListener;
    }

    public QueueServiceClient(ServiceClient client) {
        clients = new LinkedList<>();
        clients.add(client);
    }

    public QueueServiceClient(ServiceClient client, OnQueueCompletedListener queueCompletedListener) {
        onQueueCompletedListener = queueCompletedListener;
        clients = new LinkedList<>();
        clients.add(client);
    }

    public void add(ServiceClient client) {
        if (clients == null) {
            clients = new LinkedList<>();
        }
        clients.add(client);
    }

    private void submitNextRequest() {
        activeClient = getNextServiceClient();
        if (activeClient != null) {
            activeClient.submitRequest();
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                if (onQueueCompletedListener != null) {
                    onQueueCompletedListener.onQueueCompleted();
                }
            });
        }
    }

    private ServiceClient getNextServiceClient() {
        if (clients!= null && clients.peek() != null) {
            return clients.pop();
        }
        return null;
    }
    //TODO: implement method to quit nextRequest if critical service call in que returns failure

    @Override
    public void onRequestCompleted() {
        submitNextRequest();
    }

    public void add(List<ExtendedRequest> requests) {
        for (ExtendedRequest request : requests) {
            ServiceClient serviceClient = new ServiceClient(request, this);
            add(serviceClient);
        }
    }

    public void processQueue() {
        submitNextRequest();
    }

    public void add(ExtendedRequest[] requests) {
        add(Arrays.asList(requests));
    }

    public interface OnQueueCompletedListener {
        void onQueueCompleted();
    }
}