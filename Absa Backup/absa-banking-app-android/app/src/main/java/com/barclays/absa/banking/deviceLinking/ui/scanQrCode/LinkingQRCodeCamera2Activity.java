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

import android.hardware.Camera;

import com.barclays.absa.banking.framework.utils.BMBLogger;

import java.io.IOException;

public class LinkingQRCodeCamera2Activity extends LinkingQRCodeActivity {

    private static final String TAG = LinkingQRCodeCamera2Activity.class.getSimpleName();

    @Override
    protected void startCameraSource() {
        try {
            cameraSourcePreview.startCamera2(cameraSource, graphicOverlay);
            cameraFocus(cameraSource, Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            isCameraPreviewRunning = true;
        } catch (IOException e) {
            cameraSource.release();
            cameraSource = null;
        } catch (android.hardware.camera2.CameraAccessException e) {
            BMBLogger.d(TAG, "CameraAccessException - " + e.getMessage());
        }
    }
}