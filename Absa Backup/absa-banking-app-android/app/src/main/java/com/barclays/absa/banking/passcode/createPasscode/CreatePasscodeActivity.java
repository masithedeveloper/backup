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
package com.barclays.absa.banking.passcode.createPasscode;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.utils.validators.BMBPasscodeValidator;
import com.barclays.absa.banking.login.ui.passcode.PasscodeHelpActivity;
import com.barclays.absa.banking.passcode.PasscodeActivity;

public class CreatePasscodeActivity extends PasscodeActivity {

    private final static String IS_TOOLBAR_BACK_BUTTON_VISIBLE = "isToolbarBackButtonVisible";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDeviceProfilingInteractor().notifyLogin();
        if (isToolbarBackButtonVisible()) {
            setToolBarBack(R.string.passcode);
        } else {
            setToolBarNoBackButton(R.string.passcode);
        }

        findViewById(R.id.termsAndConditionsTextView).setVisibility(View.INVISIBLE);
        findViewById(R.id.visitOnlineTextView).setVisibility(View.INVISIBLE);
        findViewById(R.id.createOrConfirmLayout).setVisibility(View.VISIBLE);
        enterOrConfirmPasscode.setVisibility(View.VISIBLE);
        enterOrConfirmPasscode.setText(R.string.linking_enter_passcode);
        enterOrConfirmPasscodeInstrution.setVisibility(View.VISIBLE);
        enterOrConfirmPasscodeInstrution.setText(R.string.enter_a_five_digit_passcode);
        hideOldInstruction();

        mScreenName = CREATE_PASSCODE_CONST;
        mSiteSection = SIMPLIFIED_LOGIN_CONST;
        AnalyticsUtils.getInstance().trackCustomScreenView(CREATE_PASSCODE_CONST, SIMPLIFIED_LOGIN_CONST, TRUE_CONST);

        setBottomLeftKeyText(getString(R.string.need_help));
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetPasscodeIndicator();
    }

    @Override
    protected void onPasscodeEntered() {
        validatePasscode();
    }

    private void validatePasscode() {
        String errorMessage = BMBPasscodeValidator.validatePasscodeWithMessage(enteredPasscode);
        if (errorMessage.isEmpty()) {
            navigateToConfirmPasscode();
        } else {
            resetPasscodeIndicator();
            setPasscodeError(errorMessage);
        }
    }

    private void navigateToConfirmPasscode() {
        Intent confirmIntent = new Intent(this, ConfirmPasscodeActivity.class);
        confirmIntent.putExtra(SET_PASSCODE, enteredPasscode);
        startActivity(confirmIntent);
    }

    @Override
    protected void onBackSpaceClicked() {
        doEarlyValidation();
    }

    @Override
    protected void onBottomLeftKeyClicked() {
        startActivity(new Intent(this, PasscodeHelpActivity.class));
    }

    protected void doEarlyValidation() {
        int len = enteredPasscode.length();
        if (len > 2 && len < PASSCODE_LENGTH) {
            String errorMessage = BMBPasscodeValidator.validPasscodeWithMessageIgnoreLengthOfPin(enteredPasscode);
            if (!errorMessage.isEmpty()) {
                setPasscodeError(errorMessage);
            }
        }
    }

    @Override
    protected void resetPasscodeInstruction() {
        setPasscodeInstruction(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isToolbarBackButtonVisible() {
        if (getIntent() != null) {
            return getIntent().getBooleanExtra(IS_TOOLBAR_BACK_BUTTON_VISIBLE, false);
        }
        return false;
    }
}