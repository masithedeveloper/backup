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
package com.barclays.absa.banking.deviceLinking.ui.scanQrCode;

import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktDelegate;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktHandler;
import com.barclays.absa.banking.framework.utils.BMBLogger;

class ScanQRCodePresenter {

    private static final String TAG = ScanQRCodePresenter.class.getSimpleName();
    private String scannedQrCode;

    private ScanQRCodeView view;
    private TransaktHandler transaktHandler;
    private TransaktDelegate transaktDelegate;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    ScanQRCodePresenter(ScanQRCodeView scanQRCodeView, TransaktDelegate scanQrTransaktDelegate) {
        view = scanQRCodeView;
        transaktDelegate = scanQrTransaktDelegate;
    }

    void onQRCodeScanned(String qrCode) {
        BMBLogger.e(TAG, "QR code scanned: " + qrCode);
        scannedQrCode = qrCode.substring(0, Math.min(9, qrCode.length()));
        BMBLogger.e(TAG, "QR code truncated: " + scannedQrCode);

        view.showProgressDialog();

        transaktHandler = BMBApplication.getInstance().getTransaktHandler();
        transaktHandler.setConnectCallbackTriggeredFlag(false);

        transaktHandler.setTransaktDelegate(transaktDelegate);
        transaktHandler.start();

        appCacheService.setScanQRFlow(true);
    }

    void onCameraPermissionDenied() {
        view.goToUniqueCodeScreen();
    }

    void onViewStarted() {
        view.requestCameraPermission();
    }

    void onUniqueNumberButtonClicked() {
        view.goToUniqueCodeScreen();
    }

    void onTransaktConnected() {
        transaktHandler.signUp(scannedQrCode);
    }
}