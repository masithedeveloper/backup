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

package com.barclays.absa.banking.presentation.generateTokens;

import java.lang.ref.WeakReference;

public class NoConnectionPresenter implements NoConnectionPresenterInterface {
    private final WeakReference<NoConnectionTokenView> viewWeakReference;
    private static final int TIMER_CHECK_RATE_MS = 500;
    private static final int TIMER_CHECKS_MAX = 4;

    NoConnectionPresenter(NoConnectionTokenView view) {
        viewWeakReference = new WeakReference<>(view);
    }

    @Override
    public void reconnectInvoked() {
        NoConnectionTokenView view = viewWeakReference.get();
        if (view == null) {
            return;
        }

        view.attemptReconnect(TIMER_CHECK_RATE_MS, TIMER_CHECKS_MAX);
    }
}