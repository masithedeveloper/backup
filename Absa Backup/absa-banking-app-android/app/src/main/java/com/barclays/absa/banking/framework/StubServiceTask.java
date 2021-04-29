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


public class StubServiceTask extends AbstractServiceTask {

    public static int delayTime = 700;

    StubServiceTask(ExtendedRequest extendedRequest) {
        super(extendedRequest);
    }

    StubServiceTask(ExtendedRequest request, QueueCallBack callback) {
        super(request, callback);
    }

    public String submitRequest() {
        try {
            Thread.sleep(delayTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return request.getMockResponse();
    }
}