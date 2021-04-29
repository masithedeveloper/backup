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

package com.barclays.absa.banking.newToBank;

import android.content.Context;
import android.util.SparseArray;

import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiDetector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.thisisme.sdk.fragments.IDBookCaptureFragment;

public class CustomCaptureFragment extends IDBookCaptureFragment {

    public CustomCaptureFragment() {
    }

    protected MultiDetector setupDetectors(Context c) {
        FaceDetector faceDetector = (new FaceDetector.Builder(c)).build();
        faceDetector.setProcessor(new Detector.Processor<Face>() {
            public void release() {
            }

            public void receiveDetections(Detector.Detections<Face> detections) {
                SparseArray<Face> faces = detections.getDetectedItems();
                processFaces(faces);
            }
        });

        BarcodeDetector barcodeDetector = (new com.google.android.gms.vision.barcode.BarcodeDetector.Builder(c)).build();
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            public void release() {
            }

            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> codes = detections.getDetectedItems();
                BMBLogger.d("SCAN Size", String.valueOf(codes.size()));
                processBarCodes(codes);
            }
        });

        return (new com.google.android.gms.vision.MultiDetector.Builder()).add(faceDetector).add(barcodeDetector).build();
    }
}
