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

package com.barclays.absa.banking.presentation.sureCheckV2;

import android.content.Intent;
import android.os.Bundle;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.manage.devices.SureCheckEnterCardInfo2faActivity;
import com.barclays.absa.banking.passcode.PasscodeActivity;
import com.barclays.absa.banking.presentation.shared.datePickerUtils.RebuildUtils;

import static com.barclays.absa.banking.manage.devices.ManageDeviceConstants.DEVICE_OBJECT;

public class SureCheckDevicePasscode2faActivity extends PasscodeActivity implements SureCheckDevicePasscodeView {

    private SureCheckDevicePasscodePresenterInterface sureCheckPasscodePresenter;
    private String fillerText;
    public final static String DELINK_REASON_KEY = "delinkReason";
    public final static String FILLER_TEXT_KEY = "fillerText";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RebuildUtils.setupToolBar(this, null, R.drawable.ic_arrow_back_white, false, null);
        sureCheckPasscodePresenter = new SureCheckDevicePasscodePresenter(this);
        fillerText = getIntent().getStringExtra(FILLER_TEXT_KEY);
        hideKeypadBottomLeftButton();
        setPasscodeInstruction(getString(R.string.enter_passcode_of_surecheck, fillerText));
    }

    @Override
    protected void onPasscodeEntered() {
        sureCheckPasscodePresenter.onPasscodeEntered(enteredPasscode);
    }

    @Override
    protected void onBackSpaceClicked() {
    }

    @Override
    protected void onBottomLeftKeyClicked() {
    }

    @Override
    protected void resetPasscodeInstruction() {
        setPasscodeInstruction(getString(R.string.enter_passcode_of_surecheck, fillerText));
    }

    @Override
    protected void doEarlyValidation() {
    }

    @Override
    public void navigateToEnterAtmInformationScreen(String passcode) {
        final Intent intent = new Intent(this, SureCheckEnterCardInfo2faActivity.class);
        intent.putExtra(DELINK_REASON_KEY, getIntent().getIntExtra(DELINK_REASON_KEY, 0));
        intent.putExtra(DEVICE_OBJECT, getIntent().getSerializableExtra(DEVICE_OBJECT));
        intent.putExtra(PASSCODE, passcode);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getAppCacheService().hasNoPrimaryDeviceVerificationErrorOccurred()) {
            getAppCacheService().setNoPrimaryDeviceVerificationErrorOccurred(false);
            setPasscodeInstruction(getString(R.string.no_primary_credentials_validation_error_message));
        }
    }
}