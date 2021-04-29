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
package com.barclays.absa.banking.deviceLinking.ui.uniqueNumber;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.databinding.ActivityRegisterUniqueNumberRebrandBinding;
import com.barclays.absa.banking.deviceLinking.ui.CreateNicknameActivity;
import com.barclays.absa.banking.deviceLinking.ui.scanQrCode.LinkingQRCodeActivity;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.BuildConfigHelper;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TDataResponse;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktDelegate;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AnimationHelper;
import com.entersekt.sdk.Notify;

import org.jetbrains.annotations.NotNull;

@Deprecated
public class LinkingUniqueNumberActivity extends BaseActivity implements View.OnClickListener, EnterUniqueNumberView {

    private static final String TAG = LinkingUniqueNumberActivity.class.getSimpleName();
    private EnterUniqueNumberPresenter presenter;
    private int numberOfAttemptsSoFar = 0;
    private ActivityRegisterUniqueNumberRebrandBinding binding;
    private TransaktDelegate uniqueNumberTransaktDelegate = new TransaktDelegate(this) {

        @Override
        protected void onRegisterSuccess() {
            super.onRegisterSuccess();
            BMBLogger.e(TAG, "+_+_+_+_+ Device successfully registered with Entersekt +_+_+_+_+");
        }

        @Override
        protected void onConnected() {
            super.onConnected();
            presenter.onTransaktConnected();
        }

        @Override
        protected void onSignupSuccess() {
            super.onSignupSuccess();
            BMBLogger.e(TAG, "+_+_+_+_+ Device was already registered with Entersekt; proceeding +_+_+_+_+");
            if (BuildConfigHelper.STUB) {
                TDataResponse tDataResponse = new TDataResponse();
                tDataResponse.setCommand(CONTINUE_ENROLLMENT);
                onTDataReceived(tDataResponse);
            }
        }

        @Override
        protected void onTDataReceived(TDataResponse tDataResponse) {
            super.onTDataReceived(tDataResponse);
            String tDataCommand = tDataResponse.getCommand();
            BMBLogger.e(TAG, "+_+_+_+_+ TData received [ " + tDataCommand + " ]; proceeding... +_+_+_+_+");
            dismissProgressDialog();

            if (CONTINUE_ENROLLMENT.equals(tDataCommand)) {
                goToDeviceNicknameScreen();
            } else {
                showDeviceLinkingFailureScreen();
            }
        }

        @Override
        protected void onNotifyReceived(Notify notify) {
            super.onNotifyReceived(notify);
            showMessage(notify.getType(), notify.getText(), (dialog, which) -> dismissProgressDialog());
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_register_unique_number_rebrand, null, false);
        setContentView(binding.getRoot());

        setToolBarBack(R.string.enter_unique_number);
        initViews();
    }

    private void initViews() {
        binding.nextButton.setOnClickListener(this);
        presenter = new EnterUniqueNumberPresenter(this, uniqueNumberTransaktDelegate);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nextButton:
                presenter.onContinueButtonPressed(binding.uniqueNumberInputText.getSelectedValueUnmasked().trim());
                break;
            default:
                break;
        }
    }

    public boolean onKeyDown(int keyCode, @NotNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            presenter.onBackKeyPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void navigateToCreateNickNameActivity() {
        String IS_FROM_SCAN_QR_SCREEN = "isFromScanQRScreen";
        Intent launcherIntent = new Intent();
        launcherIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        launcherIntent.setClass(this, CreateNicknameActivity.class);
        launcherIntent.putExtra(IS_FROM_SCAN_QR_SCREEN, true);
        startActivity(launcherIntent);
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
            showCancelWarningDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showCancelWarningDialog();
    }

    private void showCancelWarningDialog() {
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
    public void onInvalidUniqueNumberInput() {
        int totalNumberOfAttempts = 3;
        ++numberOfAttemptsSoFar;
        if (numberOfAttemptsSoFar < totalNumberOfAttempts) {
            String errorMessage = String.format(getString(R.string.unique_number_error_msg), numberOfAttemptsSoFar, totalNumberOfAttempts);
            AnimationHelper.shakeShakeAnimate(binding.uniqueNumberInputText);
            binding.uniqueNumberInputText.setError(errorMessage);
            binding.uniqueNumberInputText.showError(true);
        } else {
            showUniqueNumberFailed();
        }
    }

    @Override
    public void goToDeviceNicknameScreen() {
        Intent deviceNicknameIntent = new Intent(this, CreateNicknameActivity.class);
        startActivity(deviceNicknameIntent);
    }

    private void showDeviceLinkingFailureScreen() {
        GenericResultActivity.bottomOnClickListener = v -> {
            startActivity(new Intent(LinkingUniqueNumberActivity.this, WelcomeActivity.class));
            finish();
        };

        Intent deviceLinkingFailedIntent = new Intent(LinkingUniqueNumberActivity.this, GenericResultActivity.class);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_cross);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.device_cannot_be_linked);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.SUB_MESSAGE, R.string.device_linking_error_explanation);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.cancel);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.CALL_US_CONTACT_NUMBER, "08600 08600");
        deviceLinkingFailedIntent.putExtra(BMBConstants.PRE_LOGIN_LAYOUT, true);
        startActivity(deviceLinkingFailedIntent);
    }

    private void showUniqueNumberFailed() {
        GenericResultActivity.bottomOnClickListener = v -> {
            Intent intent = new Intent();
            intent.setClass(LinkingUniqueNumberActivity.this, LinkingQRCodeActivity.class);
            startActivity(intent);
        };

        Intent deviceLinkingFailedIntent = new Intent(LinkingUniqueNumberActivity.this, GenericResultActivity.class);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_cross);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.unique_number_invalid);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.SUB_MESSAGE, R.string.invalid_number_content);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.retry);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.CALL_US_CONTACT_NUMBER, getString(R.string.support_center_number));
        deviceLinkingFailedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        deviceLinkingFailedIntent.putExtra(BMBConstants.PRE_LOGIN_LAYOUT, true);
        startActivity(deviceLinkingFailedIntent);
        finish();
    }
}