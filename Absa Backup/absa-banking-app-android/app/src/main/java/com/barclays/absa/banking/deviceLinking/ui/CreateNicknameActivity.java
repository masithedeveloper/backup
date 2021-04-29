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
package com.barclays.absa.banking.deviceLinking.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.databinding.LinkingCreateNicknameActivityBinding;
import com.barclays.absa.banking.deviceLinking.ui.verifyAlias.VerifyAliasDetails2faActivity;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.BuildConfigHelper;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.manage.devices.linking.DeviceLimitReached2faActivity;
import com.barclays.absa.banking.passcode.createPasscode.CreatePasscodeActivity;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AnalyticsUtil;

import static com.barclays.absa.banking.linking.ui.IdentificationAndVerificationConstants.ID_AND_V_LINK_DEVICE;

public class CreateNicknameActivity extends BaseActivity implements CreateNicknameView {
    private CreateNicknamePresenter presenter;
    private LinkingCreateNicknameActivityBinding binding;
    private final String defaultDeviceNickname = Build.MANUFACTURER + " " + Build.MODEL;
    private final String NON_BIOMETRIC_LINKING_ERROR = "RC\\\\u003d9011";
    private int retry = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.linking_create_nickname_activity, null, false);
        setContentView(binding.getRoot());

        setToolBarNoBackButton(R.string.device_name);
        mScreenName = BMBConstants.DEVICE_NICKNAME_CONST;
        mSiteSection = BMBConstants.SIMPLIFIED_LOGIN_CONST;

        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.DEVICE_NICKNAME_CONST, BMBConstants.SIMPLIFIED_LOGIN_CONST, BMBConstants.TRUE_CONST);
        populateView();
        presenter = new CreateNicknamePresenter(this);
        configureTalkBack();
    }

    private void configureTalkBack() {
        binding.deviceNicknameNormalInputView.setEditTextContentDescription(getString(R.string.talkback_register_device_nickname_field));
    }

    private void populateView() {
        binding.saveAndContinueButton.setOnClickListener(v -> {
            preventDoubleClick(v);
            boolean shouldSkip = binding.deviceNicknameNormalInputView.getText().trim().length() == 0;
            if (shouldSkip) {
                if (getAppCacheService().isBioAuthenticated()) {
                    AnalyticsUtil.INSTANCE.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_DeviceNameScreen_SkipButtonClicked");
                }
                binding.deviceNicknameNormalInputView.setText(defaultDeviceNickname);
            }
            storeDeviceNicknameAndProceed(shouldSkip);
        });

        String mDeviceNickName = Build.MANUFACTURER + " " + Build.MODEL;
        binding.deviceNicknameNormalInputView.setHintText(mDeviceNickName);

        InputFilter[] alphaNumericFilter = getAlphaNumericFilter();
        binding.deviceNicknameNormalInputView.getEditText().setFilters(alphaNumericFilter);

        binding.deviceNicknameNormalInputView.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.saveAndContinueButton.setText(s.toString().trim().length() == 0 ? R.string.skip : R.string.next);
                if (s.length() == 1 || s.length() == 0) {
                    animate(binding.saveAndContinueButton, R.anim.expand_horizontal);
                }
            }
        });
    }

    @NonNull
    private InputFilter[] getAlphaNumericFilter() {
        InputFilter[] alphaNumericFilter = new InputFilter[2];
        alphaNumericFilter[0] = (arg0, arg1, arg2, arg3, arg4, arg5) -> {
            for (int k = arg1; k < arg2; k++) {
                if (!Character.isLetterOrDigit(arg0.charAt(k)) && !Character.isSpaceChar(arg0.charAt(k))) {
                    return "";
                }
            }
            return null;
        };
        alphaNumericFilter[1] = new InputFilter.LengthFilter(20);
        return alphaNumericFilter;
    }

    @Override
    public void onError(final String errorMessage) {
        runOnUiThread(() -> showMessageError(errorMessage));
    }

    private void storeDeviceNicknameAndProceed(boolean shouldSkip) {
        String nickName = shouldSkip ? defaultDeviceNickname : binding.deviceNicknameNormalInputView.getText().trim();
        getAppCacheService().setDeviceNickname(nickName);
        if (BuildConfigHelper.STUB) {
            showCreatePasscodeScreen();
        } else {
            presenter.onProceed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.cancel_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancel_menu_item) {
            BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                    .title(getString(R.string.alert_dialog_header))
                    .message(getString(R.string.alert_dialog_text))
                    .positiveDismissListener((dialog, which) -> {
                        AnalyticsUtils.getInstance().trackCancelButton(mScreenName, mSiteSection);
                        BaseAlertDialog.INSTANCE.dismissAlertDialog();
                        Intent intent = new Intent(this, WelcomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        CustomerProfileObject.updateCustomerProfileObject(new CustomerProfileObject());
                        startActivity(intent);
                        finish();
                    }));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionTimeOut() {
        runOnUiThread(() -> showMessageError(getString(R.string.conn_timeout_msg)));
    }

    @Override
    public void showTransaktErrorMessage() {
        dismissProgressDialog();
        runOnUiThread(() -> {
            if (retry++ <= 3) {
                toastShort(R.string.retry);
                presenter.onProceed();
            } else {
                dismissProgressDialog();
                BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                        .title("Error")
                        .message("Technical error, please try again...")
                        .positiveButton("Retry")
                        .positiveDismissListener((dialog, which) -> presenter.onProceed())
                        .negativeButton("Cancel")
                        .negativeDismissListener((dialog, which) -> logoutAndGoToStartScreen())
                        .build());
            }
        });
    }

    @Override
    public void onBackPressed() {
        BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.alert_dialog_header))
                .message(getString(R.string.alert_dialog_text))
                .positiveDismissListener((dialog, which) -> {
                    AnalyticsUtils.getInstance().trackCancelButton(mScreenName, mSiteSection);
                    BaseAlertDialog.INSTANCE.dismissAlertDialog();
                    Intent intent = new Intent(this, WelcomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    CustomerProfileObject.updateCustomerProfileObject(new CustomerProfileObject());
                    startActivity(intent);
                    finish();
                }));
    }

    @Override
    public void launchAlmostDoneScreen() {
        final Intent intent = new Intent(CreateNicknameActivity.this, LinkingAlmostDoneActivity.class);
        intent.putExtra(VerifyAliasDetails2faActivity.VERIFY_ALIAS_FROM_SCREEN, VerifyAliasDetails2faActivity.CREATE_NICKAME_SCREEN);
        startActivity(intent);
    }

    @Override
    public void showCreatePasscodeScreen() {
        Intent passcodeIntent = new Intent(this, CreatePasscodeActivity.class);
        startActivity(passcodeIntent);
    }

    @Override
    public void showDeviceLimitReachedScreen() {
        Intent deviceLimitReachedIntent = new Intent(this, DeviceLimitReached2faActivity.class);
        startActivity(deviceLimitReachedIntent);
    }

    @Override
    public void goToVerifyAliasIdScreen() {
        Intent verifyAliasIdIntent = new Intent(this, VerifyAliasDetails2faActivity.class);
        startActivity(verifyAliasIdIntent);
    }

    @Override
    public void showLinkingFailedScreen(String errorMessage) {
        GenericResultActivity.bottomOnClickListener = v -> {
            Intent intent = new Intent(CreateNicknameActivity.this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        };

        Intent deviceLinkingFailedIntent = new Intent(this, GenericResultActivity.class)
                .putExtra(GenericResultActivity.IS_FAILURE, true)
                .putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_cross)
                .putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.cancel)
                .putExtra(GenericResultActivity.CALL_US_CONTACT_NUMBER, getString(R.string.support_center_number))
                .putExtra(BMBConstants.PRE_LOGIN_LAYOUT, true);

        if (errorMessage.contains(NON_BIOMETRIC_LINKING_ERROR)) {
            deviceLinkingFailedIntent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.device_cannot_be_linked);
            deviceLinkingFailedIntent.putExtra(GenericResultActivity.SUB_MESSAGE_STRING, R.string.linking_use_bio_authentication);
        } else {
            deviceLinkingFailedIntent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.device_cannot_be_linked);
            deviceLinkingFailedIntent.putExtra(GenericResultActivity.SUB_MESSAGE_STRING, errorMessage);
        }
        startActivity(deviceLinkingFailedIntent);
    }

    @Override
    public void showFailureDialog(String failureResponse) {
        BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.alert))
                .message(failureResponse)
                .build());
    }
}