/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *   outside the Bank without the prior written permission of the Absa Legal
 *
 *   In the event that such disclosure is permitted the code shall not be copied
 *   or distributed other than on a need-to-know basis and any recipients may be
 *   required to sign a confidentiality undertaking in favor of Absa Bank Limited
 */

package com.barclays.absa.banking.presentation.util;

import androidx.test.espresso.IdlingResource;

public class ElapsedTimeIdlingResource implements IdlingResource {

    private final long startTime;
    private final long waitingTime;
    private ResourceCallback resourceCallback;


    public ElapsedTimeIdlingResource(long waitingTime){
        this.startTime = System.currentTimeMillis();
        this.waitingTime = waitingTime;

    }
    @Override
    public String getName() {
        return ElapsedTimeIdlingResource.class.getName() + ":" + waitingTime;
    }

    @Override
    public boolean isIdleNow() {

        long elapsedTime = System.currentTimeMillis() - startTime;
        boolean idle = (elapsedTime >= startTime);
        if(idle){
            resourceCallback.onTransitionToIdle();
        }
        return idle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }
}
