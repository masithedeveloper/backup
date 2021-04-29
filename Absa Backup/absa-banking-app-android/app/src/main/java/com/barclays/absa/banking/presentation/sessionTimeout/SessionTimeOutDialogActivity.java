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
package com.barclays.absa.banking.presentation.sessionTimeout;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.accessibility.AccessibilityEvent;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.DialogSessionTimeoutBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;

import java.util.concurrent.TimeUnit;

public class SessionTimeOutDialogActivity extends BaseActivity implements View.OnClickListener {

    private final long COUNTDOWN_INTERVAL_TIME = TimeUnit.MILLISECONDS.toMillis(500);
    private final long COUNTDOWN_MAX_TIME = TimeUnit.SECONDS.toMillis(30);
    private CountDownTimer downTimer;
    private String mScreenName;
    public static boolean shouldShow = false;
    private DialogSessionTimeoutBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        shouldShow = true;
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_session_timeout, null, false);
        setContentView(binding.getRoot());

        this.setFinishOnTouchOutside(false);
        binding.yesButton.setOnClickListener(this);
        binding.logoutButton.setOnClickListener(this);
        binding.counterTimerTextView.setContentDescription(getString(R.string.talkback_timeout_heading_dialog));
        mScreenName = getIntent().getStringExtra(BMBConstants.SCREEN_NAME_CONST);
        startTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        shouldShow = false;
    }

    private void startTimer() {
        downTimer = new CountDownTimer(COUNTDOWN_MAX_TIME, COUNTDOWN_INTERVAL_TIME) {

            public void onTick(long millisUntilFinished) {
                try {
                    long secondsLeft = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
                    String timeOutMessage = String.format(getString(R.string.session_timeout), secondsLeft);
                    binding.counterTimerTextView.setText(timeOutMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void onFinish() {
                binding.counterTimerTextView.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
                binding.counterTimerTextView.setContentDescription(getString(R.string.talkback_timeout_logout_announcement));
                logoutAndGoToStartScreen();
                finish();
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        final BMBApplication app = BMBApplication.getInstance();
        switch (v.getId()) {
            case R.id.yesButton:
                downTimer.cancel();
                app.setUserLoggedInStatus(true);
                AnalyticsUtils.getInstance().trackLogoutPopUpNo(mScreenName);
                finish();
                break;

            case R.id.logoutButton:
                downTimer.cancel();
                logoutAndGoToStartScreen();
                app.setUserLoggedInStatus(false);
                AnalyticsUtils.getInstance().trackLogoutPopUpYes(mScreenName);
                finish();
                break;

            default:
                break;
        }
    }
}