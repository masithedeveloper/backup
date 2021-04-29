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

package com.barclays.absa.banking.presentation.sureCheck;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.SureCheckCountdown2faActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.presentation.help.HelpActivity;
import com.barclays.absa.banking.presentation.shared.widget.CountDownCircularView;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AccessibilityUtils;

import styleguide.utils.extensions.StringExtensions;

public class SureCheckCountdownActivity extends BaseActivity implements SureCheckCountdownView {

    public static final int COUNT_DOWN_INTERVAL = 1000;
    public static final int MILLIS_DURATION = 60000;

    private SureCheckCountdownPresenter presenter;
    private CountDownTimer countDownTimer;
    private SureCheckCountdown2faActivityBinding binding;
    private SureCheckDelegate sureCheckDelegate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.sure_check_countdown_2fa_activity, null, false);
        setContentView(binding.getRoot());

        setupToolbar();
        String sureCheckCellphoneNumber = getAppCacheService().getSureCheckCellphoneNumber();
        if (!sureCheckCellphoneNumber.isEmpty()) {
            sureCheckCellphoneNumber = StringExtensions.toMaskedCellphoneNumber(sureCheckCellphoneNumber);
            binding.sureCheckDisclaimerTextView.setText(getString(R.string.sure_check_tv_second_cellphone, sureCheckCellphoneNumber));
        } else if (!getAppCacheService().getSureCheckEmail().isEmpty()) {
            String sureCheckEmail = getAppCacheService().getSureCheckEmail();
            binding.sureCheckDisclaimerTextView.setText(getString(R.string.sure_check_tv_second_email, sureCheckEmail));
        } else {
            binding.sureCheckDisclaimerTextView.setText(getString(R.string.sure_check_no_method_specified));
        }
        setupTalkBack();
        int totalDuration = MILLIS_DURATION / COUNT_DOWN_INTERVAL;
        binding.countDownCircularView.setDuration(totalDuration);
        binding.countDownCircularView.setDisplayText(String.valueOf(totalDuration));

        presenter = new SureCheckCountdownPresenter(this, totalDuration);
        countDownTimer = new CountDownTimer(MILLIS_DURATION, COUNT_DOWN_INTERVAL) {

            @Override
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int) (millisUntilFinished / COUNT_DOWN_INTERVAL);
                presenter.onTimerTicked(secondsLeft);
            }

            @Override
            public void onFinish() {
                presenter.onTimerRanOut();
            }
        };
        sureCheckDelegate = getAppCacheService().getSureCheckDelegate();
        presenter.onViewCreated();

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.resendSureCheckButton.setOnClickListener(v -> {
            preventDoubleClick(v);
            presenter.onResendClicked();
        });
    }

    @Override
    public void startTimer() {
        countDownTimer.start();
    }

    @Override
    public void updateCountDownCircle(int secondsRemaining) {
        String prefix = "";
        if (secondsRemaining < 10) {
            prefix = "0";
        }
        String displayValue = prefix + secondsRemaining;
        binding.countDownCircularView.setDisplayText(displayValue);
        announceTimerInfo(binding.countDownCircularView, secondsRemaining);
    }

    @Override
    public void stopTimer() {
        binding.countDownCircularView.setDisplayText("00");
        countDownTimer.cancel();
    }

    @Override
    public void displayResendOption() {
        binding.sureCheckDisclaimerTextView.setVisibility(View.GONE);
        binding.resendSureCheckButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayFailureResult() {
        if (sureCheckDelegate != null) {
            sureCheckDelegate.onSureCheckFailed();
        }
        displayResendOption();
    }

    @Override
    public void sureCheckProcessed() {
        if (sureCheckDelegate != null) {
            sureCheckDelegate.onSureCheckProcessed();
        }
    }

    @Override
    public void showSureCheckRejected() {
        disableResendOption();
        if (sureCheckDelegate != null) {
            sureCheckDelegate.onSureCheckRejected();
        }
    }

    @Override
    public void maxRetriesExceeded() {
        binding.remainingApprovalTextView.setText(R.string.max_retries_exceeded);
        binding.resendSureCheckButton.setVisibility(View.GONE);
        stopTimer();
    }

    @Override
    public void showError(String errorMessage) {
        BaseAlertDialog.INSTANCE.showErrorAlertDialog(errorMessage);
    }

    @Override
    public void cancelSureCheck() {
        if (sureCheckDelegate != null) {
            sureCheckDelegate.onSureCheckCancelled();
        }
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public void disableResendOption() {
        binding.resendSureCheckButton.setEnabled(false);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) binding.toolbar.toolbar;
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getString(R.string.surecheck));
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_cross_dark);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sure_check_countdown_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showCancelDialog();
                return true;
            case R.id.help_menu_item:
                Intent helpIntent = new Intent(SureCheckCountdownActivity.this, HelpActivity.class);
                startActivity(helpIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showCancelDialog() {
        BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.cancel_surechek_dialog_title))
                .message(getString(R.string.cancel_surechek_dialog_text))
                .positiveButton(getString(R.string.yes))
                .negativeButton(getString(R.string.cancel))
                .positiveDismissListener((dialog, which) -> presenter.onCancelClicked())
                .build());
    }

    private void announceTimerInfo(@NonNull CountDownCircularView countDownCircularView, int secondsLeft) {
        if (isAccessibilityEnabled()) {
            AccessibilityUtils.announceTimerInfo(countDownCircularView, secondsLeft);
        }
    }

    private void setupTalkBack() {
        if (isAccessibilityEnabled()) {
            binding.sureCheckHeader.setContentDescription(getString(R.string.talkback_surecheck_header));
            binding.sureCheckDisclaimerTextView.setContentDescription(binding.sureCheckDisclaimerTextView.getText().toString());
            AccessibilityUtils.announceRandValueTextFromView(binding.sureCheckDisclaimerTextView);
        }
    }
}