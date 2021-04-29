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

package com.barclays.absa.banking.manage.devices;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.deviceLinking.ui.CreateNicknameActivity;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitching;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates;
import com.barclays.absa.banking.manage.devices.services.dto.Device;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.presentation.shared.IntentFactoryGenericResult;
import com.barclays.absa.banking.presentation.shared.datePickerUtils.RebuildUtils;
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.crypto.SecureUtils;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.PermissionHelper;
import com.barclays.absa.utils.ValidationUtils;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import styleguide.forms.NormalInputView;

import static com.barclays.absa.banking.linking.ui.IdentificationAndVerificationConstants.ID_AND_V_LINK_DEVICE;
import static com.barclays.absa.banking.manage.devices.ManageDeviceConstants.DEVICE_OBJECT;

public class SureCheckEnterCardInfo2faActivity extends BaseActivity implements EnterCardInfoView, View.OnClickListener {
    static final int REQUEST_CODE_SCAN_CARD = 1;

    private NormalInputView atmPinNormalInputView;
    private NormalInputView atmCardNormalInputView;
    private EnterCardInfoPresenterInterface enterCardInformationPresenter;
    private boolean settingsIntentInvoked = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surecheck_enter_card_number);

        RebuildUtils.setupToolBar(this, null, -1, false, null);

        Device device = (Device) getIntent().getSerializableExtra(DEVICE_OBJECT);
        enterCardInformationPresenter = new EnterCardInfoPresenter(this, device, SecureUtils.INSTANCE.getDeviceID());

        initViews();
    }

    private void initViews() {
        atmPinNormalInputView = findViewById(R.id.atm_card_pin_input_view);
        atmCardNormalInputView = findViewById(R.id.atm_card_number_input_view);

        atmCardNormalInputView.setIconViewImage(R.drawable.ic_camera_dark);
        atmCardNormalInputView.setImageViewVisibility(View.VISIBLE);
        atmCardNormalInputView.setImageViewOnClickListener(this);

        findViewById(R.id.continue_button).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cancel_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancel_menu_item) {
            showCancelFlowDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.continue_button) {
            preventDoubleClick(v);

            boolean isValid = true;
            atmCardNormalInputView.showError(false);
            atmPinNormalInputView.showError(false);

            if (!ValidationUtils.isValidATMCardNumber(atmCardNormalInputView.getSelectedValue())) {
                atmCardNormalInputView.setError(String.format(BMBApplication.getInstance().getResources()
                                .getString(R.string.pleaseEnterValid),
                        BMBApplication.getInstance().getResources().getString(R.string.card_number_text).toLowerCase()));
                isValid = false;
            }
            if (!ValidationUtils.isValidATMPin(atmPinNormalInputView.getSelectedValue())) {
                atmPinNormalInputView.setError(String.format(BMBApplication.getInstance().getResources()
                                .getString(R.string.pleaseEnterValid),
                        BMBApplication.getInstance().getResources().getString(R.string.enter_pin_text).toLowerCase()));
                isValid = false;
            }
            if (isValid) {
                String passcode = getIntent().getStringExtra(PASSCODE);
                final String DELIMITER = " ";
                String atmCardNumber = atmCardNormalInputView.getSelectedValue().replace(DELIMITER, "").trim();
                enterCardInformationPresenter.onConfirmCardInfoButtonClicked(passcode, atmCardNumber, atmPinNormalInputView.getSelectedValue());
            }
        } else if (v.getId() == R.id.icon_view) {
            PermissionHelper.requestCameraAccessPermission(this, this::scanCard);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionHelper.PermissionCode.ACCESS_CAMERA.value == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                AnalyticsUtil.trackActionFromStaticContext(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_MakeVerificationDeviceUsingAppcodeCardnumberPINScreen_AllowCameraButtonClicked");
                scanCard();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA);
                if (!showRationale) {
                    GenericResultActivity.bottomOnClickListener = v -> BMBApplication.getInstance().getTopMostActivity().finish();
                    GenericResultActivity.topOnClickListener = v -> {
                        BMBApplication.getInstance().getTopMostActivity().finish();
                        startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", getPackageName(), null)));
                        settingsIntentInvoked = true;
                    };

                    Intent intent = new Intent(this, GenericResultActivity.class);
                    intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.register_camera_unavailable_screen_title);
                    intent.putExtra(GenericResultActivity.SUB_MESSAGE_STRING, getString(R.string.register_camera_unavailable_screen_description));
                    intent.putExtra(GenericResultActivity.IS_GENERAL_ALERT, true);
                    intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.ok_button_text);
                    intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.register_camera_unavailable_screen_open_settings);
                    startActivity(intent);
                }
            }
        }
    }

    @Deprecated
    @Override
    public void navigateToSureCheckConfirmationScreen() {
        Intent deviceDelinkingSuccessIntent = new Intent(this, GenericResultActivity.class);
        deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.IS_SUCCESS, true);
        deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_tick);
        deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.this_is_now_your_surecheck_2_0_device);
        deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.SUB_MESSAGE, R.string.surecheck_2_0_passcode_explanation);

        final Class screenToReturnTo = getAppCacheService().getReturnToScreen();
        if (screenToReturnTo != null) {
            getAppCacheService().setNoPrimaryDeviceState(false);
            deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.reinitiate_transaction);
            GenericResultActivity.bottomOnClickListener = v -> {
                Intent returnToTransactionIntent = new Intent(SureCheckEnterCardInfo2faActivity.this, screenToReturnTo);
                returnToTransactionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                returnToTransactionIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(returnToTransactionIntent);
                getAppCacheService().setReturnToScreen(null);
            };
        } else {
            deviceDelinkingSuccessIntent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.home);
            GenericResultActivity.bottomOnClickListener = v -> loadAccountsAndGoHome();
        }

        deviceDelinkingSuccessIntent.putExtra(BMBConstants.POST_LOGIN_LAYOUT, true);
        startActivity(deviceDelinkingSuccessIntent);
        finish();
    }

    @Override
    public void showInvalidPasscodeOrCredentialsError() {
        getAppCacheService().setNoPrimaryDeviceVerificationErrorOccurred(true);
        onBackPressed();
    }

    @Override
    public void showSecurityCodeMessage() {
        @StringRes int securityCodeMessageToUse;
        FeatureSwitching featureSwitchingToggles = FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles();
        if (featureSwitchingToggles.getSecurityCodeAvailableOnATM() == FeatureSwitchingStates.ACTIVE.getKey()) {
            securityCodeMessageToUse = R.string.passcode_revoked_description_atm;
        } else {
            securityCodeMessageToUse = R.string.passcode_revoked_description;
        }

        IntentFactory.IntentBuilder intentBuilder = IntentFactoryGenericResult.getGenericResultFailureBuilder(this);
        intentBuilder.setGenericResultHeaderMessage(R.string.security_code_required)
                .setGenericResultSubMessage(securityCodeMessageToUse)
                .setGenericResultIconToError()
                .setGenericResultBottomButton(R.string.ok_got_it, v -> {
                    Intent intent = new Intent(SureCheckEnterCardInfo2faActivity.this, WelcomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                });
        Intent securityCodeRequiredIntent = intentBuilder.build();
        startActivity(securityCodeRequiredIntent);
    }

    public void navigateCreateNicknameScreen() {
        startActivity(new Intent(this, CreateNicknameActivity.class));
    }

    @Override
    public void showFailure(String errorMessage) {
        dismissProgressDialog();
        showMessageError(errorMessage);
    }

    private void showCancelFlowDialog() {
        BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.sure_check_cancel_flow_message_title))
                .message(getString(R.string.sure_check_cancel_flow_message))
                .positiveDismissListener((dialog, which) -> {
                    AnalyticsUtils.getInstance().trackCustomScreenView("SureCheckEnterCardInfo2faActivity", mScreenName, BMBConstants.TRUE_CONST);
                    goToLaunchScreen(this);
                }));
    }

    private void scanCard() {
        AnalyticsUtil.trackActionFromStaticContext(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_MakeVerificationDeviceUsingAppcodeCardnumberPINScreen_CameraButtonClicked");
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        scanIntent.putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, false);
        scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_SCAN_EXPIRY, false);
        scanIntent.putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_SCAN_OVERLAY_LAYOUT_ID, R.layout.register_atm_card_ocr_overlay);
        startActivityForResult(scanIntent, REQUEST_CODE_SCAN_CARD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SCAN_CARD) {
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
                atmCardNormalInputView.setSelectedValue(scanResult.cardNumber);
                atmPinNormalInputView.setSelectedValue("");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (settingsIntentInvoked) {
            if (PackageManager.PERMISSION_GRANTED == this.checkCallingOrSelfPermission(Manifest.permission.CAMERA)) {
                scanCard();
                settingsIntentInvoked = false;
            }
        }
    }
}