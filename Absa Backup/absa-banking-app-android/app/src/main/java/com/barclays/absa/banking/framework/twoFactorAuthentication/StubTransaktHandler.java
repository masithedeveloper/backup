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
package com.barclays.absa.banking.framework.twoFactorAuthentication;

import com.entersekt.sdk.TrustToken;

import java.util.UUID;

class StubTransaktHandler extends TransaktHandler {

    StubTransaktHandler(TransaktDelegate delegate) {
        super(delegate);
    }

    StubTransaktHandler() {
        //DO NOT DELETE: Required
    }

    @Override
    public void start() {
        if (transaktDelegate != null) {
            transaktDelegate.onConnected();
        }
    }

    @Override
    public void signUp() {
        if (transaktDelegate != null) {
            transaktDelegate.onSignupSuccess();
            TDataResponse response = new TDataResponse();
            response.setCommand("_CONT_ENROLLMENT_");
            transaktDelegate.onTDataReceived(response);
        }
    }

    @Override
    public void generateTrustToken() {
        TrustToken trustToken = new TrustToken() {
            @Override
            public String getToken() {
                return UUID.randomUUID().toString();
            }

            @Override
            public String getScope() {
                return UUID.randomUUID().toString();
            }
        };

        if (transaktDelegate != null) {
            transaktDelegate.onGenerateTrustTokenSuccess(trustToken.getToken());
        }
    }

    @Override
    public void signUp(String uniqueCode) {
        if (transaktDelegate != null) {
            transaktDelegate.onSignupSuccess();
        }
    }

}
