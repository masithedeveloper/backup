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

package com.barclays.absa.utils.imageHelpers;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.data.cache.AddBeneficiaryDAO;
import com.barclays.absa.banking.presentation.shared.bottomSheet.BottomSheet;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.PermissionHelper;
import com.barclays.absa.utils.fileUtils.FileReaderUtils;

import java.io.File;

import styleguide.content.ProfileView;
import styleguide.widgets.RoundedImageView;

public abstract class ProfileViewBaseImageHelper {

    public static final int PROFILE_IMAGE_REQUEST = 11100;
    public static final int PROFILE_IMAGE_REQUEST_AFTER_CROP = 11101;
    private final boolean exceedsMarshmallow = android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M;

    private int action;

    private boolean photoUpdated;
    private boolean photoDeleted;

    private Uri tempCroppedPhotoUri;

    protected Bitmap bitmap;
    private File imageFile;

    protected final Activity activity;
    private OnImageActionListener onImageActionListener;
    private final PackageManager packageManager;

    abstract byte[] getImageData();

    public interface OnImageActionListener {
        void onProfileImageDelete();
    }

    private class OnSelectionListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            action = which;
            PermissionHelper.requestExternalStorageWritePermission(activity, ProfileViewBaseImageHelper.this::uploadImage);
        }
    }

    ProfileViewBaseImageHelper(final Activity activity, ProfileView imageView) {
        this(activity);
        setImageView(imageView);
    }

    ProfileViewBaseImageHelper(final Activity activity, RoundedImageView imageView) {
        this(activity);
        setImageView(imageView);
    }

    ProfileViewBaseImageHelper(final Activity activity) {
        this.activity = activity;
        this.packageManager = activity.getPackageManager();
    }

    private void setImageView(RoundedImageView imageView) {
        View.OnClickListener onClickListener = view -> new BottomSheet.Builder(activity, R.style.BottomSheet_StyleDialog)
                .title(activity.getString(R.string.profile_picture_chooser_title))
                .sheet(hasDatabaseImage() ? R.menu.menu_bottom_sheet : R.menu.menu_with_no_image).
                        listener(new OnSelectionListener())
                .show();
        imageView.setOnClickListener(onClickListener);
    }

    private void setImageView(ProfileView imageView) {
        View.OnClickListener onClickListener = view -> new BottomSheet.Builder(activity, R.style.BottomSheet_StyleDialog)
                .title(activity.getString(R.string.profile_picture_chooser_title))
                .sheet(hasDatabaseImage() ? R.menu.menu_bottom_sheet : R.menu.menu_with_no_image).
                        listener(new OnSelectionListener())
                .show();
        imageView.setOnClickListener(onClickListener);
    }

    byte[] getDatabaseImage(String imageName) {
        AddBeneficiaryDAO addBeneficiaryDAO = new AddBeneficiaryDAO(activity);
        AddBeneficiaryObject addBeneficiaryObject = addBeneficiaryDAO.getBeneficiary(imageName);
        if (addBeneficiaryObject == null) {
            throw new RuntimeException("Beneficiary Object is null");
        }
        return addBeneficiaryObject.getImageData();
    }

    public void setOnImageActionListener(OnImageActionListener onImageActionListener) {
        if (this.onImageActionListener == null) {
            this.onImageActionListener = onImageActionListener;
        }
    }

    protected boolean hasDatabaseImage() {
        try {
            byte[] data = getImageData();
            return data != null && data.length > 0;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public boolean isPhotoDeleted() {
        return photoDeleted;
    }

    public boolean isPhotoUpdated() {
        return photoUpdated;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    private void uploadImage() {
        imageFile = FileReaderUtils.createImageFile(activity);
        photoDeleted = false;

        switch (action) {
            case R.id.camera:
                PermissionHelper.requestCameraAccessPermission(activity, this::captureProfileImage);
                break;
            case R.id.upload:
                pickGalleryImage();
                break;
            case R.id.delete:
                deleteImage();
                break;
            default:
                break;
        }
    }

    public void cropThumbnail(Intent data) {
        try {
            Uri imageUri;
            if (data != null && data.getData() != null) {
                imageUri = data.getData();
            } else {
                imageUri = exceedsMarshmallow ?
                        FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", imageFile) :
                        Uri.fromFile(imageFile);
            }

            File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File tempImageFile = File.createTempFile("temp", ".jpg", storageDir);
            tempCroppedPhotoUri = exceedsMarshmallow ?
                    FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", tempImageFile) :
                    Uri.fromFile(tempImageFile);
            CommonUtils.performCrop(activity, imageUri, tempCroppedPhotoUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void captureProfileImage() {
        if (imageFile != null) {
            Uri photoURI = exceedsMarshmallow ?
                    FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", imageFile) :
                    Uri.fromFile(imageFile);
            Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            imageCaptureIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            if (imageCaptureIntent.resolveActivity(packageManager) != null) {
                activity.startActivityForResult(imageCaptureIntent, PROFILE_IMAGE_REQUEST);
            } else if (activity instanceof BaseActivity) {
                BaseAlertDialog.INSTANCE.showGenericErrorDialog();
            }
        } else {
            if (activity instanceof BaseActivity) {
                BaseAlertDialog.INSTANCE.showGenericErrorDialog();
            }
        }
    }

    private void pickGalleryImage() {
        Intent galleryIntent;
        galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (imageFile != null) {
            galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
            if (galleryIntent.resolveActivity(packageManager) != null) {
                activity.startActivityForResult(Intent.createChooser(galleryIntent, "Complete action using"),
                        PROFILE_IMAGE_REQUEST);
            }
        }
    }

    private void deleteImage() {
        if (hasDatabaseImage()) {
            showImageDeleteDialog();
        }
    }

    private void showImageDeleteDialog() {
        BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                .message(activity.getString(R.string.remove_photo))
                .positiveDismissListener((dialog, which) -> {
                    photoDeleted = true;
                    bitmap = null;
                    if (onImageActionListener != null) {
                        onImageActionListener.onProfileImageDelete();
                    }
                }));
    }

    public void retrieveThumbnail(Intent data) {
        if (data != null) {
            try {
                Uri uri = data.getData() == null ? tempCroppedPhotoUri : data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void handlePermissionResults(int requestCode, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            int permissionResult = grantResults[0];
            switch (permissionResult) {
                case PackageManager.PERMISSION_GRANTED:
                    if (requestCode == PermissionHelper.PermissionCode.WRITE_TO_EXTERNAL_STORAGE.value) {
                        uploadImage();
                    } else if (requestCode == PermissionHelper.PermissionCode.ACCESS_CAMERA.value) {
                        captureProfileImage();
                    }
                    break;
                case PackageManager.PERMISSION_DENIED:
                    if (requestCode == PermissionHelper.PermissionCode.WRITE_TO_EXTERNAL_STORAGE.value) {
                        PermissionHelper.requestExternalStorageWritePermission(activity, this::uploadImage);
                    } else if (requestCode == PermissionHelper.PermissionCode.ACCESS_CAMERA.value) {
                        PermissionHelper.requestCameraAccessPermission(activity, this::captureProfileImage);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}