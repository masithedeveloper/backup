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

import android.app.Activity;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NewToBankScanIdBookActivityBinding;
import com.barclays.absa.utils.ImageUtils;
import com.thisisme.sdk.fragments.IDBookCaptureFragmentEventListener;

import static com.barclays.absa.banking.newToBank.NewToBankConstants.IMAGE_QUALITY;
import static com.barclays.absa.banking.newToBank.NewToBankConstants.IMAGE_SIZE;
import static com.barclays.absa.banking.newToBank.NewToBankConstants.TIM_ID_NUMBER;
import static com.barclays.absa.banking.newToBank.NewToBankConstants.TIM_IMAGE;

public class NewToBankScanIDBookActivity extends AppCompatActivity {

    private NewToBankScanIdBookActivityBinding binding;
    private CustomCaptureFragment captureFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.new_to_bank_scan_id_book_activity, null, false);
        setContentView(binding.getRoot());

        wireUpComponents();
        setUpComponentListeners();
    }

    private void wireUpComponents() {
        this.captureFragment = (CustomCaptureFragment) getSupportFragmentManager().findFragmentById(R.id.captureViewIdBook);
        this.captureFragment.enableCaptureTimeout(60);
    }

    private void setUpComponentListeners() {
        captureFragment.setEventListener(new IDBookCaptureFragmentEventListener() {

            @Override
            public void onCaptureComplete(String identityNumber, Bitmap photo) {
                captureFragment.setEventListener(null);

                photo = ImageUtils.scaleBitmap(photo, IMAGE_SIZE);
                Bitmap bitmap = ImageUtils.compressBitmapToJPEG(photo, IMAGE_QUALITY);
                byte[] bytes = ImageUtils.convertToByteArrayForNewToBank(bitmap);

                Intent intent = new Intent();
                intent.putExtra(TIM_IMAGE, bytes);
                intent.putExtra(TIM_ID_NUMBER, identityNumber);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }

            @Override
            public void onTimeoutProgress(int seconds) {
                super.onTimeoutProgress(seconds);
                if (seconds == 0) {
                    setResult(Activity.RESULT_FIRST_USER, null);
                    finish();
                }
            }

            @Override
            public void onCameraCapture(Bitmap photo) {

            }

            @Override
            public void onNeedsPermission() {

            }
        });
    }
}