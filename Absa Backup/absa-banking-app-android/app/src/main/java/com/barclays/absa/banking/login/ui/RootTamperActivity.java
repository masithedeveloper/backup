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
package com.barclays.absa.banking.login.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.framework.utils.TelephoneUtil;

import java.util.Timer;
import java.util.TimerTask;

public class RootTamperActivity extends AppCompatActivity {

    private Timer timer;
    private long timeCounter = 10;  // Seconds to set the Countdown from

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_check);
        ImageView call = findViewById(R.id.iv_call);
        call.setOnClickListener(v -> TelephoneUtil.supportCall(RootTamperActivity.this));

        try {
            startCountDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (timer != null)
                timer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void startCountDown() {
        timer = new Timer();        // A thread of execution is instantiated
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                --timeCounter;
                if (timeCounter == 0) {
                    timer.cancel();
                    RootTamperActivity.this.finish();
                }
            }
        }, 0, 1000);
    }
}
