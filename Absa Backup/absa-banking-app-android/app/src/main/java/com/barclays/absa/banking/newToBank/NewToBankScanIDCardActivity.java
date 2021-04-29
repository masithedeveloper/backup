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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NewToBankScanIdCardActivityBinding;
import com.barclays.absa.utils.ImageUtils;
import com.thisisme.sdk.fragments.IDCardCaptureFragment;
import com.thisisme.sdk.fragments.IDCardCaptureFragmentEventListener;
import com.thisisme.sdk.types.IDCardSide;
import com.thisisme.sdk.types.IdentityCard;

import static com.barclays.absa.banking.newToBank.NewToBankConstants.IMAGE_SIZE;
import static com.barclays.absa.banking.newToBank.NewToBankConstants.TIM_BACK;
import static com.barclays.absa.banking.newToBank.NewToBankConstants.TIM_FRONT;
import static com.barclays.absa.banking.newToBank.NewToBankConstants.TIM_ID_NUMBER;

public class NewToBankScanIDCardActivity extends AppCompatActivity {

    private NewToBankScanIdCardActivityBinding binding;
    private IDCardCaptureFragment captureFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.new_to_bank_scan_id_card_activity, null, false);
        setContentView(binding.getRoot());
        wireUpComponents();
        initViews();
    }

    private void wireUpComponents() {
        captureFragment = (IDCardCaptureFragment) getSupportFragmentManager().findFragmentById(R.id.captureViewIdCard);
    }

    private void initViews() {
        IDCardCaptureFragmentEventListener idCardCaptureFragmentEventListener = new IDCardCaptureFragmentEventListener() {
            @Override
            public void onCameraCapture(IDCardSide side, Bitmap photo) {
            }

            @Override
            public void onTimeoutProgress(int seconds) {
                super.onTimeoutProgress(seconds);
            }

            @Override
            public void onCaptureComplete(IdentityCard identityCard, Bitmap front, Bitmap back) {
                front = ImageUtils.scaleBitmap(front, IMAGE_SIZE);
                back = ImageUtils.scaleBitmap(front, IMAGE_SIZE);

                byte[] bytes = ImageUtils.convertToByteArrayForNewToBank(front);
                byte[] bytes2 = ImageUtils.convertToByteArrayForNewToBank(back);

                Intent intent = new Intent();
                intent.putExtra(TIM_FRONT, bytes);
                intent.putExtra(TIM_BACK, bytes2);
                intent.putExtra(TIM_ID_NUMBER, identityCard.getIdentityNumber());
                setResult(Activity.RESULT_OK, intent);
                finish();
            }

            @Override
            public void onNeedsPermission() {
            }

        };
        this.captureFragment.setEventListener(idCardCaptureFragmentEventListener);
    }
}