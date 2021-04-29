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

package com.barclays.absa.banking.passcode.passcodeLogin;

import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktDelegate;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktHandler;
import com.barclays.absa.banking.presentation.verification.SureCheckAuth2faActivity;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.entersekt.sdk.Error;

public class SurecheckAuth2faTransaktDelegate extends TransaktDelegate {
    private SureCheckAuth2faActivity activity;
    private TransaktHandler handler;
    private SurecheckAuth2faTransaktDelegateListener listener;

    public SurecheckAuth2faTransaktDelegate(SureCheckAuth2faActivity activity,
                                            TransaktHandler handler,
                                            SurecheckAuth2faTransaktDelegate.SurecheckAuth2faTransaktDelegateListener listener) {
        this.activity = activity;
        this.handler = handler;
        this.listener = listener;
    }

    @Override
    protected void onConnected() {
        super.onConnected();
        handler.generateTrustToken();
    }

    @Override
    protected void onGenerateTrustTokenFailure(Error error) {
        super.onGenerateTrustTokenFailure(error);
        onError("Transakt token error!");
    }

    @Override
    protected void onSignupError(Error error) {
        super.onSignupError(error);
        onError("Transakt signup error!");
    }

    protected void onError(String errorMessage) {
        if (BMBApplication.getInstance().isInForeground()) {
            activity.dismissProgressDialog();
            BaseAlertDialog.INSTANCE.showErrorAlertDialog(errorMessage);
        }
        if (listener != null) {
            listener.onError(errorMessage);
        }
    }

    @Override
    protected void onGenerateTrustTokenSuccess(String trustToken) {
        super.onGenerateTrustTokenSuccess(trustToken);

        if (listener != null) {
            listener.onSuccess();
        }
    }

    public interface SurecheckAuth2faTransaktDelegateListener {
        void onError(String errorMessage);

        void onSuccess();
    }
}