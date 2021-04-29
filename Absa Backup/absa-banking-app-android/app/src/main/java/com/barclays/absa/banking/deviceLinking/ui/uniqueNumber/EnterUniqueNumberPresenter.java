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
package com.barclays.absa.banking.deviceLinking.ui.uniqueNumber;

import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktDelegate;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktHandler;

class EnterUniqueNumberPresenter implements EnterUniqueNumberPresenterInterface {

    private final TransaktDelegate transaktDelegate;
    private EnterUniqueNumberView view;
    private TransaktHandler transaktHandler;
    private String uniqueNumber;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    EnterUniqueNumberPresenter(EnterUniqueNumberView uniqueNumberViewInterface, TransaktDelegate transaktDelegate) {
        this.view = uniqueNumberViewInterface;
        this.transaktDelegate = transaktDelegate;
    }

    @Override
    public void onRetryButtonPressed() {
        view.navigateToCreateNickNameActivity();
    }

    @Override
    public void onBackKeyPressed() {
        view.onBackPressed();
    }

    @Override
    public void onContinueButtonPressed(String uniqueCode) {
        validateUniqueNumber(uniqueCode);
    }

    @Override
    public void onTransaktConnected() {
        appCacheService.setScanQRFlow(true);
        transaktHandler.signUp(uniqueNumber);
    }

    private void validateUniqueNumber(String uniqueCode) {
        uniqueCode = uniqueCode.replace(" ", "");
        if (uniqueCode.length() < 9) {
            view.onInvalidUniqueNumberInput();
        } else {
            uniqueNumber = uniqueCode;

            view.showProgressDialog();

            transaktHandler = BMBApplication.getInstance().getTransaktHandler();
            transaktHandler.setConnectCallbackTriggeredFlag(false);
            transaktHandler.setTransaktDelegate(transaktDelegate);
            transaktHandler.start();
        }
    }
}
