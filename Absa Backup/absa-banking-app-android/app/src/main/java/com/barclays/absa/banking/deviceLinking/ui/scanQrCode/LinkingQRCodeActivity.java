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

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.deviceLinking.ui.CreateNicknameActivity;
import com.barclays.absa.banking.deviceLinking.ui.uniqueNumber.LinkingUniqueNumberActivity;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TDataResponse;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktDelegate;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity;
import com.barclays.absa.utils.PermissionHelper;
import com.entersekt.sdk.Notify;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;

import za.co.absa.twoFactor.TransaktEngine;

@Deprecated
@SuppressWarnings("deprecation")
public class LinkingQRCodeActivity extends BaseActivity implements View.OnClickListener, ScanQRCodeView {

    private static final String TAG = LinkingQRCodeActivity.class.getSimpleName();
    protected CameraSourcePreview cameraSourcePreview;
    protected CameraSource cameraSource;
    protected GraphicOverlay graphicOverlay;
    private ScanQRCodePresenter scanQRCodePresenter;
    private int scanCount = 0;
    private boolean isDialogShowing;
    boolean isCameraPreviewRunning;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2fa_scan_qr);
        setToolBarBack(R.string.two_fa_scan_qr_title);
        cameraSourcePreview = findViewById(R.id.camera_preview);
        graphicOverlay = findViewById(R.id.bulls_eye_box);
        Button buttonEnterUniqueNumber = findViewById(R.id.btn_enter_unique_numberq);
        buttonEnterUniqueNumber.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.PermissionCode.ACCESS_CAMERA.value) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (scanQRCodePresenter == null) {
                    scanQRCodePresenter = new ScanQRCodePresenter(this, scanQrTransaktDelegate);
                }
                //fall back to unique code screen
                scanQRCodePresenter.onCameraPermissionDenied();
            } else {
                activateQRScanner();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (scanQRCodePresenter == null) {
            scanQRCodePresenter = new ScanQRCodePresenter(this, scanQrTransaktDelegate);
            scanQRCodePresenter.onViewStarted();
        }
    }


    @Override
    public void goToUniqueCodeScreen() {
        Intent uniqueCodeIntent = new Intent(this, LinkingUniqueNumberActivity.class);
        startActivity(uniqueCodeIntent);
    }

    @Override
    public void requestCameraPermission() {
        PermissionHelper.requestCameraAccessPermission(this, this::activateQRScanner);
    }

    @Override
    public void goToDeviceNicknameScreen() {
        Intent deviceNicknameIntent = new Intent(this, CreateNicknameActivity.class);
        startActivity(deviceNicknameIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isDialogShowing) {
            stopScanning();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    @Override
    public void stopScanning() {
        if (cameraSourcePreview != null) {
            if (isCameraPreviewRunning) {
                cameraSourcePreview.stop();
            }
            isCameraPreviewRunning = false;
        }
    }

    private void activateQRScanner() {
        try { // TODO investigate this IllegalArgumentException issue, Model: Pixel, Manufacturer: Google, Android: 7.1.2
            BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                    .setBarcodeFormats(Barcode.QR_CODE)
                    .build();
            BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(graphicOverlay);
            barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());
            BarcodeProcessor barcodeProcessor = new BarcodeProcessor();
            barcodeDetector.setProcessor(barcodeProcessor);

            cameraSource = new CameraSource.Builder(LinkingQRCodeActivity.this, barcodeDetector)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(960, 720)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(6)
                    .build();

            if (barcodeDetector.isOperational()) {
                startCameraSource();
            } else {
                Toast.makeText(LinkingQRCodeActivity.this, "QR code detector not yet ready. Please close and re-open this screen. If this problem persists, please use the unique number below.", Toast.LENGTH_LONG).show();
            }
        } catch (IllegalArgumentException e) {
            showMessageError("Error accessing the camera " + e.getMessage());
        }
    }

    protected void startCameraSource() {
        try {
            cameraSourcePreview.start(cameraSource, graphicOverlay);
            LinkingQRCodeActivity.cameraFocus(cameraSource, Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            isCameraPreviewRunning = true;
        } catch (IOException e) {
            cameraSource.release();
            cameraSource = null;
        } catch (IllegalStateException e) {
            BMBLogger.d(TAG, "CameraAccessException - " + e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        scanQRCodePresenter.onUniqueNumberButtonClicked();
    }

    private class BarcodeProcessor implements Detector.Processor<Barcode> {

        @Override
        public void release() {
        }

        @Override
        public void receiveDetections(Detector.Detections<Barcode> detections) {
            SparseArray<Barcode> detectedBarcodes = detections.getDetectedItems();
            for (int i = 0; i < detectedBarcodes.size(); i++) {
                final Barcode barcode = detectedBarcodes.valueAt(i);
                BMBLogger.d(LinkingQRCodeActivity.class.getSimpleName(), "rawValue: " + barcode.rawValue + ", display value: " + barcode.displayValue);
                if (scanCount == 0) {
                    ++scanCount;
                    BMBLogger.d(LinkingQRCodeActivity.class.getSimpleName(), "displayValue is " + barcode.displayValue);
                    scanQRCodePresenter.onQRCodeScanned(barcode.displayValue);
                    //stopScanning();
                }
            }
        }
    }

    /**
     * Custom annotation to allow only valid focus modes.
     */
    @StringDef({
            Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE,
            Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO,
            Camera.Parameters.FOCUS_MODE_AUTO,
            Camera.Parameters.FOCUS_MODE_EDOF,
            Camera.Parameters.FOCUS_MODE_FIXED,
            Camera.Parameters.FOCUS_MODE_INFINITY,
            Camera.Parameters.FOCUS_MODE_MACRO
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface FocusMode {
    }

    public static boolean cameraFocus(@NonNull CameraSource cameraSource, @FocusMode @NonNull String focusMode) {
        Field[] declaredFields = CameraSource.class.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getType() == Camera.class) {
                field.setAccessible(true);
                try {
                    Camera camera = (Camera) field.get(cameraSource);
                    if (camera != null) {
                        Camera.Parameters params = camera.getParameters();
                        if (!params.getSupportedFocusModes().contains(focusMode)) {
                            return false;
                        }
                        params.setFocusMode(focusMode);
                        camera.setParameters(params);
                        return true;
                    }
                    return false;
                } catch (IllegalAccessException e) {
                    BMBLogger.e(TAG, e.getMessage());
                }

                break;
            }
        }

        return false;
    }

    private static class BarcodeGraphic extends GraphicOverlay.Graphic {

        private final Barcode barcode;

        private Paint overlayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        BarcodeGraphic(GraphicOverlay overlay, Barcode barcode) {
            super(overlay);
            this.barcode = barcode;
            overlayPaint.setStyle(Paint.Style.STROKE);
            overlayPaint.setColor(Color.CYAN);
            overlayPaint.setStrokeWidth(2.0f);
        }

        @Override
        public void draw(Canvas canvas) {
            Rect overlayRect = barcode.getBoundingBox();
            overlayRect.left = (int) translateX((float) overlayRect.left);
            overlayRect.top = (int) translateY((float) overlayRect.top);
            overlayRect.right = (int) translateX((float) overlayRect.right);
            overlayRect.bottom = (int) translateY((float) overlayRect.bottom);

            canvas.drawRect(overlayRect, overlayPaint);
            canvas.drawText(barcode.rawValue, overlayRect.left, overlayRect.bottom, overlayPaint);
        }
    }

    private void releaseCamera() {
        stopScanning();
        if (cameraSourcePreview != null) {
            cameraSourcePreview.release();
            cameraSourcePreview = null;
        }
    }

    private TransaktDelegate scanQrTransaktDelegate = new TransaktDelegate() {
        boolean hasSignUpOrCallbackAlreadyFired;
        boolean deviceNicknameScreenStarted;

        @Override
        protected void onRegisterSuccess() {
            super.onRegisterSuccess();
            if (hasSignUpOrCallbackAlreadyFired && !deviceNicknameScreenStarted) {
                dismissProgressDialog();
                goToDeviceNicknameScreen();
            }
            BMBLogger.e(TAG, "+_+_+_+_+ Device successfully registered with Entersekt +_+_+_+_+");
        }

        @Override
        public void onConnected() {
            super.onConnected();
            scanQRCodePresenter.onTransaktConnected();
        }

        @Override
        protected void onError(String errorMessage) {
            super.onError(errorMessage);
            dismissProgressDialog();
        }

        @Override
        protected void onNotifyReceived(Notify notify) {
            super.onNotifyReceived(notify);
            showMessage(notify.getType(), notify.getText(), (dialog, which) -> {
                dismissProgressDialog();
                scanCount = 0;
                activateQRScanner();
            });
        }

        @Override
        protected void onTDataReceived(TDataResponse tDataResponse) {
            super.onTDataReceived(tDataResponse);
            String tDataCommand = tDataResponse.getCommand();
            BMBLogger.e(TAG, "+_+_+_+_+ TData received [ " + tDataCommand + " ]; proceeding... +_+_+_+_+");

            if (CONTINUE_ENROLLMENT.equals(tDataCommand)) {
                if (TransaktEngine.isRegistered()) {
                    deviceNicknameScreenStarted = true;
                    dismissProgressDialog();
                    goToDeviceNicknameScreen();
                }
                hasSignUpOrCallbackAlreadyFired = true;
            } else {
                dismissProgressDialog();
                showDeviceLinkingFailureScreen();
            }
        }
    };

    private void showDeviceLinkingFailureScreen() {
        GenericResultActivity.bottomOnClickListener = v -> {
            startActivity(new Intent(LinkingQRCodeActivity.this, WelcomeActivity.class));
            finish();
        };
        Intent deviceLinkingFailedIntent = new Intent(this, GenericResultActivity.class);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_cross);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.device_cannot_be_linked);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.SUB_MESSAGE, R.string.device_linking_error_explanation);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.cancel);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.CALL_US_CONTACT_NUMBER, getString(R.string.support_center_number));
        deviceLinkingFailedIntent.putExtra(BMBConstants.PRE_LOGIN_LAYOUT, true);
        startActivity(deviceLinkingFailedIntent);
    }

    @Override
    public void dismissProgressDialog() {
        super.dismissProgressDialog();
        isDialogShowing = false;
    }
}