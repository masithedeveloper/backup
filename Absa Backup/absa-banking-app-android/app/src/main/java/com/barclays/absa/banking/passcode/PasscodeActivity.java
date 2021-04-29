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
package com.barclays.absa.banking.passcode;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.deviceLinking.ui.TermsAndConditionsSelectorActivity;
import com.barclays.absa.banking.framework.ArxanProtection;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.login.ui.RootTamperActivity;
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;

import java.util.Date;

import styleguide.forms.KeypadView;
import styleguide.forms.OnPasscodeChangeListener;

public abstract class PasscodeActivity extends BaseActivity implements View.OnClickListener {

    protected TextView enterOrConfirmPasscode, enterOrConfirmPasscodeInstrution;
    protected String enteredPasscode = "";
    private TextView tvPasscodeInstruction;
    protected KeypadView numericKeypad;
    protected Toolbar toolbar;
    public static final String SET_PASSCODE = "SET_PASSCODE";
    protected static final int PASSCODE_LENGTH = 5;

    protected abstract void onPasscodeEntered();

    protected abstract void onBackSpaceClicked();

    protected abstract void onBottomLeftKeyClicked();

    protected abstract void resetPasscodeInstruction();

    protected abstract void doEarlyValidation();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_navigation_passcode);
        enterOrConfirmPasscode = findViewById(R.id.enterOrConfirmPasscodeTextView);
        enterOrConfirmPasscodeInstrution = findViewById(R.id.enterOrConfirmPasscodeInstructionTextView);

        BMBLogger.d("x-zy", "z " + getString(R.string.size));
        if (ArxanProtection.isDeviceRooted()) {
            startActivity(new Intent(this, RootTamperActivity.class));
            finish();
        } else {
            toolbar = findViewById(R.id.toolbar);
            if (toolbar != null)
                setSupportActionBar(toolbar);

            tvPasscodeInstruction = findViewById(R.id.instructionTextView);

            numericKeypad = findViewById(R.id.numericKeypad);
            numericKeypad.setOnPasscodeChangeListener(new OnPasscodeChangeListener() {
                @Override
                public void onCompleted(String passcode) {
                    enteredPasscode = passcode;
                    onPasscodeEntered();
                }

                @Override
                public void onChangedPasscode(String currentPasscode) {
                    enteredPasscode = currentPasscode;
                    doEarlyValidation();
                }

                @Override
                public void onKeyEntered(String currentPasscode) {

                }
            });
            numericKeypad.setForgotPasscodeOnclickListener(v -> onBottomLeftKeyClicked());
            numericKeypad.setClearPasscodeOnclickListener(v -> {
                resetPasscodeInstruction();
                onBackSpaceClicked();
            });

            TextView tvVisitOnline = findViewById(R.id.visitOnlineTextView);
            TextView tvTermsAndConditions = findViewById(R.id.termsAndConditionsTextView);
            tvVisitOnline.setOnClickListener(this);
            tvTermsAndConditions.setOnClickListener(this);
        }
    }

    protected void hideOldInstruction() {
        getPasscodeInstructionTextView().setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        numericKeypad.clearPasscode();
    }

    @Override
    protected void onStop() {
        numericKeypad.clearPasscode();
        super.onStop();
    }

    public String getTag() {
        return this.getClass().getSimpleName();
    }

    protected void resetPasscodeIndicator() {
        BMBLogger.d(getTag() + " -  resetting passcode indicators", "@" + new Date().getTime());
        numericKeypad.clearPasscode();
    }

    protected void setPasscodeInstruction(String instruction) {
        if (tvPasscodeInstruction != null) {
            if (TextUtils.isEmpty(instruction)) {
                tvPasscodeInstruction.setVisibility(View.GONE);
            } else {
                tvPasscodeInstruction.setVisibility(View.VISIBLE);
                tvPasscodeInstruction.setTextColor(ContextCompat.getColor(this, R.color.graphite_light_theme_item_color));
                tvPasscodeInstruction.setText(instruction);
            }
        }
    }

    protected void setPasscodeError(String error) {
        if (tvPasscodeInstruction != null) {
            animate(tvPasscodeInstruction, R.anim.shake);
            tvPasscodeInstruction.setVisibility(View.VISIBLE);
            tvPasscodeInstruction.setTextColor(Color.RED);
            tvPasscodeInstruction.setText(error);
        }
    }

    protected TextView getPasscodeInstructionTextView() {
        return tvPasscodeInstruction;
    }

    protected void setBottomLeftKeyText(String bottomLeftKeyText) {
        numericKeypad.changeForgotPasscodeText(bottomLeftKeyText);
    }

    protected void hideKeypadBottomLeftButton() {
        numericKeypad.hideBottomLeftKey();
    }

    protected void shakeForIncorrect() {
        Animation shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        tvPasscodeInstruction.startAnimation(shakeAnimation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cancel_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancel_menu_item) {
            showCancelMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showCancelMenu() {

        BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.are_you_sure_stop_setup))
                .message(getString(R.string.this_will_end_session))
                .positiveButton(getString(R.string.yes))
                .negativeButton(getString(R.string.cancel))
                .positiveDismissListener((dialog, which) -> {
                    AnalyticsUtils.getInstance().trackCancelButton(mScreenName, mSiteSection);
                    dialog.cancel();
                    Intent intent = new Intent(PasscodeActivity.this, WelcomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .build());
    }

    private void visitOnlineActivity() {
        final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.absa.co.za"));
        startActivityIfAvailable(intent);
    }

    private void startTermsAndConditionActivity() {
        Intent termsAndConditionIntent = new Intent(this, TermsAndConditionsSelectorActivity.class);
        startActivity(termsAndConditionIntent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.visitOnlineTextView:
                visitOnlineActivity();
                break;
            case R.id.termsAndConditionsTextView:
                startTermsAndConditionActivity();
                break;
            default:
                break;
        }
    }
}