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
package com.barclays.absa.utils;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;

public final class FragmentPermissionHelper {

    private FragmentPermissionHelper() {
        throw new UnsupportedOperationException("FragmentPermissionHelper does not support creating instances");
    }

    public static void requestCameraAccessPermission(Fragment fragment, OnPermissionGrantListener callback) {
        requestPermission(fragment, Manifest.permission.CAMERA,
                "You need to allow access to camera to activate take continue",
                PermissionCode.ACCESS_CAMERA.value,
                callback);
    }

    private static void requestPermission(Fragment fragment,
                                          String permission,
                                          String message,
                                          int requestCode, @NonNull OnPermissionGrantListener callback) {
        if (isPermissionGranted(fragment.getContext(), permission)) {
            callback.onPermissionGranted();
        } else {
            firePermissionRequest(fragment, permission, message, requestCode);
        }
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private static void firePermissionRequest(final Fragment fragment,
                                              final String permission,
                                              final String message,
                                              final int requestCode) {
        if (fragment.getActivity() != null && ActivityCompat.shouldShowRequestPermissionRationale(fragment.getActivity(), permission)) {
            showPermissionRequestRationale(message, (dialog, which) -> fragment.requestPermissions(new String[]{permission}, requestCode));
        } else {
            fragment.requestPermissions(new String[]{permission}, requestCode);
        }
    }

    private static void showPermissionRequestRationale(String message, DialogInterface.OnClickListener positiveListener) {
        BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                .message(message)
                .positiveDismissListener(positiveListener)
                .build());
    }

    public interface OnPermissionGrantListener {
        void onPermissionGranted();
    }

    public enum PermissionCode {
        ACCESS_ACCOUNTS(101),
        ACCESS_GPS_LOCATION(102),
        ACCESS_NETWORK_LOCATION(103),
        LOAD_CONTACTS(104),
        SAVE_CONTACTS(105),
        READ_FROM_EXTERNAL_STORAGE(106),
        WRITE_TO_EXTERNAL_STORAGE(107),
        CALL_PHONE_NUMBER(108),
        ACCESS_DEVICE_STATE(109),
        ACCESS_CAMERA(110),
        ACCESS_FINGERPRINT(111),
        ACCESS_LOCATION(112);

        public int value;

        PermissionCode(int value) {
            this.value = value;
        }
    }
}