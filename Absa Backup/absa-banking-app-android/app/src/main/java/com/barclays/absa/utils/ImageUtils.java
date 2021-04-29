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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Base64;
import android.widget.ImageView;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.framework.BuildConfigHelper;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.framework.utils.ImageStub;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {

    /**
     * Sets the image from bitmap
     *
     * @param profileImageView the image view for the profile picture
     * @param beneficiaryImage the beneficiary image url
     */
    public static boolean setImageFromBitmap(ImageView profileImageView, Bitmap beneficiaryImage) {
        if (profileImageView == null) {
            BMBLogger.d("ImageView null...");
            return false;
        }

        if (BuildConfigHelper.STUB) {
            profileImageView.setImageBitmap(ImageStub.getInstance().getStubImageBitmapByName(null,
                    R.drawable.ic_profile_home_img));
            return true;
        }

        if (null != beneficiaryImage) {
            profileImageView.setImageBitmap(beneficiaryImage);
            return true;
        } else {
            profileImageView.setImageResource(R.drawable.ic_profile_home_img);
            return false;
        }
    }

    private static byte[] compressBitmapTo50k(Bitmap bitmap) {
        // service call fail with bytes size of 3230
        // service call  pass with bytes size of 3207
        final int IMAGE_SIZE_LIMIT = 3200;

        final int QUALITY_LIMIT = 8;
        final int QUALITY_STEP = 2;
        int quality = 50;

        int size = 160;
        final int size_step = 16;

        byte[] bytes;
        BMBLogger.d("x-img", "---\n\n");
        bitmap = resizeBitmap(bitmap, size, size);
        do {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BMBLogger.d("x-img-quality", "quality " + quality);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            bytes = baos.toByteArray();
            if (quality > QUALITY_LIMIT) {
                quality -= QUALITY_STEP;
            } else if (size > size_step) {
                size -= size_step;
                bitmap = resizeBitmap(bitmap, size, size);
            }
            BMBLogger.d("x-img-bytes", "bytes.length: " + bytes.length);
        } while (bytes.length > IMAGE_SIZE_LIMIT);
        return bytes;
    }

    public static Bitmap convertBase64StringToBitmap(String base64EncodeImage) throws IOException {
        byte[] decodedImageBytes = Base64.decode(base64EncodeImage, Base64.URL_SAFE);
        return BitmapFactory.decodeByteArray(decodedImageBytes, 0, decodedImageBytes.length);
    }

    private static Bitmap resizeBitmap(Bitmap bm, int newWidth, int newHeight) {
        BMBLogger.d("x-img-resize", bm.getWidth() + ":" + bm.getHeight() + " to " + newWidth + ":" + newHeight);
        if (bm.getHeight() <= newHeight && bm.getWidth() <= newWidth) {
            return bm;
        } else {
            int width = bm.getWidth(), height = bm.getHeight();
            float scaleWidth = ((float) newWidth) / width,
                    scaleHeight = ((float) newHeight) / height;
            // 	CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);
            // RECREATE THE NEW BITMAP
            return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        }
    }

    /**
     * Gets the rounded corner bitmap.
     *
     * @param bitmap the bitmap
     * @param pixels the pixels
     * @return the rounded corner bitmap
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, (float) pixels, (float) pixels, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * Return Base64 encoded image content
     *
     * @param bitmap image bitmap
     * @return Base64 encoded image services
     * @throws Exception when failed to compress or image size limit exceeded
     */
    public static String convertToBase64(Bitmap bitmap) throws Exception {
        String encodedImage = null;
        if (null != bitmap) {
            byte[] b = compressBitmapTo50k(bitmap);
            encodedImage = Base64.encodeToString(b, Base64.URL_SAFE);
            if (encodedImage.length() > 50000) {
                throw new Exception("Failed to compress image!");
            }
        }
        return encodedImage;
    }

    public static String convertBitmapToBase64EncodedStringQuality80(Bitmap bitmap) {
        if (bitmap == null) {
            return "";
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }

    /**
     * Converts bitmap to byte array
     *
     * @param bitmap the bitmap
     * @return byte array containing image services
     */
    public static byte[] convertToByteArray(Bitmap bitmap) {
        if (bitmap != null) {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return baos.toByteArray();
        } else return null;
    }

    public static byte[] convertToByteArrayForNewToBank(Bitmap bitmap) {
        if (bitmap != null) {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            return baos.toByteArray();
        } else return null;
    }

    public static Bitmap convertImageToBitmapFromBase64(String base64ImageUtils) {
        return convertImageToBitmapFromBase64(base64ImageUtils, Base64.DEFAULT);
    }

    public static Bitmap convertImageToBitmapFromBase64(String base64ImageUtils, int options) {
        byte[] imageData = Base64.decode(base64ImageUtils, options);
        if (imageData != null) {
            return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        }
        return null;
    }

    public static Bitmap compressBitmapToJPEG(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.toByteArray().length);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int smallestSide) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        double ratio;

        if (height < width) {
            ratio = (double) width / height;
            height = smallestSide;
            width = (int) (smallestSide * ratio);
        } else {
            ratio = (double) height / width;
            width = smallestSide;
            height = (int) (smallestSide * ratio);
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }
}