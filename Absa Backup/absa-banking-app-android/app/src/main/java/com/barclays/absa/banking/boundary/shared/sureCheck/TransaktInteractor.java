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
package com.barclays.absa.banking.boundary.shared.sureCheck;

import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktHandler;
import com.entersekt.sdk.Auth;
import com.entersekt.sdk.callback.AuthAnswerCallback;
import com.entersekt.sdk.callback.SignupCallback;
import com.entersekt.sdk.callback.TrustTokenCallback;

import za.co.absa.twoFactor.TransaktEngine;

public class TransaktInteractor implements TransaktService {

    private TransaktEngine transaktEngine;

    public TransaktInteractor(TransaktHandler transaktHandler) throws Exception {
        transaktEngine = new TransaktEngine(BMBApplication.getInstance(), transaktHandler.getRegisterListener(), transaktHandler.getConnectCallback(), transaktHandler.getConnectListener(), transaktHandler.getTDataListener(), transaktHandler.getAuthListener(), transaktHandler.getNotifyListener());
    }

    @Override
    public void connect() {
        if (transaktEngine != null) {
            transaktEngine.connect();
        }
    }

    @Override
    public void signUp(SignupCallback signupCallback) {
        if (transaktEngine != null) {
            String DEVICESELFREGISTER = "DEVICESELFREGISTER";
            signUp(DEVICESELFREGISTER, signupCallback);
        }
    }

    @Override
    public void signUp(String signupCode, SignupCallback signupCallback) {
        if (transaktEngine != null) {
            transaktEngine.signUp(signupCode, signupCallback);
        }
    }

    @Override
    public void generateToken(TrustTokenCallback callback) {
        if (transaktEngine != null) {
            transaktEngine.generateToken(callback);
        }
    }

    public void sendAuthorization(Auth auth, AuthAnswerCallback authAnswerCallback) {
        if (transaktEngine != null) {
            transaktEngine.submitAuth(auth, authAnswerCallback);
        }
    }

    @Override
    public void disconnect() {
        if (transaktEngine != null) {
            transaktEngine.disconnect();
        }
    }

    public boolean isConnected() {
        return transaktEngine.isConnected();
    }

    public String getEmCertID() {
        return transaktEngine.getEmCertID();
    }

    @Override
    public void setPushNotificationRegistrationToken(String pushNotificationRegistrationToken) {
        transaktEngine.setPushNotificationRegistrationToken(pushNotificationRegistrationToken);
    }

    public void setShouldConnectWithCallback(boolean shouldConnectWithCallback) {
        transaktEngine.setShouldConnectWithCallback(shouldConnectWithCallback);
    }

    public TransaktEngine getTransaktEngine() {
        return transaktEngine;
    }
}