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
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;

import java.util.Arrays;
import java.util.Collections;

public final class PermissionHelper {

    private static OnPermissionGrantListener callback;
    private static int requestCode;
    private static String permission;

    private PermissionHelper() {
        throw new UnsupportedOperationException("PermissionHelper does not support creating instances");
    }

    public static void requestContactsReadPermission(Activity context, OnPermissionGrantListener callback) {
        requestPermission(context,
                Manifest.permission.READ_CONTACTS,
                "You need to allow the application to read contacts",
                PermissionCode.LOAD_CONTACTS.value, callback);

    }

    public static void requestExternalStorageReadPermission(Activity context, OnPermissionGrantListener callback) {
        requestPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE,
                "You need to allow read access to external storage to continue",
                PermissionCode.READ_FROM_EXTERNAL_STORAGE.value,
                callback);

    }

    public static void requestExternalStorageWritePermission(Activity context, OnPermissionGrantListener callback) {
        requestPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                "You need to allow write access to external storage to continue",
                PermissionCode.WRITE_TO_EXTERNAL_STORAGE.value,
                callback);

    }

    public static void requestFingerprintPermission(Activity context, OnPermissionGrantListener callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission(context, Manifest.permission.USE_FINGERPRINT,
                    "You need to allow the application to use fingerprint to continue",
                    PermissionCode.ACCESS_FINGERPRINT.value,
                    callback);
        }
    }

    public static boolean isPermissionReadPhoneStateGranted(Context context) {
        return PackageManager.PERMISSION_GRANTED == context.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE);
    }

    public static void requestDeviceStateAccessPermission(Activity context, OnPermissionGrantListener callback) {
        requestPermission(context, Manifest.permission.READ_PHONE_STATE,
                "You need to allow access to device state information to continue",
                PermissionCode.ACCESS_DEVICE_STATE.value,
                callback);

    }

    public static void requestCameraAccessPermission(Activity context, OnPermissionGrantListener callback) {
        requestPermission(context, Manifest.permission.CAMERA,
                "You need to allow access to the camera in order to continue",
                PermissionCode.ACCESS_CAMERA.value,
                callback);
    }

    public static void requestFileSystemAccessPermission(Activity context, OnPermissionGrantListener callback) {
        requestPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE,
                "You need to allow access to the file system in order to continue",
                PermissionCode.READ_FROM_EXTERNAL_STORAGE.value,
                callback);
    }

    public static void requestCameraAccessPermission(Activity context, @StringRes int messageId, OnPermissionGrantListener callback) {
        requestPermission(context, Manifest.permission.CAMERA,
                context.getString(messageId),
                PermissionCode.ACCESS_CAMERA.value,
                callback);
    }

    public static void requestLocationAccessPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PermissionCode.ACCESS_LOCATION.value);
    }

    public static void requestLocationAccessPermission(Activity activity, OnPermissionGrantListener callback) {
        requestPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION, activity.getString(R.string.atm_and_branch_locator_location_permission_check), FragmentPermissionHelper.PermissionCode.ACCESS_LOCATION.value, callback);
    }

    private static void requestPermission(Activity context,
                                          String permission,
                                          String message,
                                          int requestCode, @NonNull OnPermissionGrantListener callback) {
        PermissionHelper.callback = callback;
        PermissionHelper.requestCode = requestCode;
        PermissionHelper.permission = permission;

        if (isPermissionGranted(context, permission)) {
            PermissionHelper.callback.onPermissionGranted();
        } else {
            firePermissionRequest(context, permission, message, requestCode);
        }
    }

    private static boolean isPermissionGranted(Activity context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private static void firePermissionRequest(final Activity context,
                                              final String permission,
                                              final String message,
                                              final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
            showPermissionRequestRationale(context, message, (dialog, which) -> ActivityCompat.requestPermissions(context, new String[]{permission}, requestCode));
        } else {
            ActivityCompat.requestPermissions(context, new String[]{permission}, requestCode);
        }
    }

    private static void showPermissionRequestRationale(Context context, String message, DialogInterface.OnClickListener positiveListener) {
        BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                .message(message)
                .positiveDismissListener(positiveListener)
                .build());
    }

    public static void handlePermissionsResponse(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionHelper.requestCode
                && Collections.binarySearch(Arrays.asList(permissions), permission) > -1
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            PermissionHelper.callback.onPermissionGranted();
        }
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
