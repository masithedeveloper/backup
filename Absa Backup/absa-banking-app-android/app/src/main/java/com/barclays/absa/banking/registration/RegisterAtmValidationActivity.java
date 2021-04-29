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
package com.barclays.absa.banking.registration;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.RegisterProfileDetail;
import com.barclays.absa.banking.databinding.RegisterAtmValidationActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.PermissionFacade;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.linking.ui.LinkingActivity;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.sessionTimeout.SessionTimeOutDialogActivity;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AccessibilityUtils;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.DeviceUtils;
import com.barclays.absa.utils.PermissionHelper;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import styleguide.forms.validation.ValidationExtensions;

public class RegisterAtmValidationActivity extends BaseActivity implements View.OnClickListener, RegisterAtmCredentialsView {

    public static final int MAX_FORMATTED_CARD_NUMBER_LENGTH = 19;
    static final int REQUEST_CODE_SCAN_CARD = 1;

    private RegisterAtmCredentialsPresenter presenter;
    private RegisterAtmValidationActivityBinding binding;
    private String atmCardNumber;
    private String atmPin;
    private boolean settingsIntentInvoked = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SessionTimeOutDialogActivity.shouldShow = false;
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.register_atm_validation_activity, null, false);
        setContentView(binding.getRoot());

        setToolBarBackWithMenu(getString(R.string.register_atm_screen_title), R.menu.cancel_menu);
        initViews();
        configureTalkBack();
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.REGISTER_CONST, BMBConstants.CARD_AND_PIN_CONST, BMBConstants.TRUE_CONST);
        presenter = new RegisterAtmCredentialsPresenter(this);
    }

    private void configureTalkBack() {
        binding.titleView.setContentDescription(getString(R.string.talkback_register_online_banking_header));
        binding.atmCardNumberInputView.setContentDescription(getString(R.string.talkback_register_atm_card_number));
        binding.atmPinNumberInputView.setContentDescription(getString(R.string.talkback_register_atm_pinNumber));
        binding.nextButton.setContentDescription(getString(R.string.talkback_password_continue_button));
        if (isAccessibilityEnabled()) {
            binding.atmCardNumberInputView.setOnClickListener(v -> binding.atmCardNumberInputView.getEditText().requestFocus());
            binding.atmPinNumberInputView.setOnClickListener(v -> binding.atmPinNumberInputView.getEditText().requestFocus());
        }
    }

    private void initViews() {
        binding.nextButton.setOnClickListener(this);

        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.atmCardNumberInputView);
        ValidationExtensions.addRequiredValidationHidingTextWatcher(binding.atmPinNumberInputView);

        binding.atmCardNumberInputView.setIconViewImage(R.drawable.ic_camera_dark);
        binding.atmCardNumberInputView.setImageViewVisibility(View.VISIBLE);
        binding.atmCardNumberInputView.setImageViewOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionHelper.PermissionCode.ACCESS_DEVICE_STATE.value == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                presenter.onContinueClicked(atmCardNumber, atmPin);
            }
        }
        if (PermissionHelper.PermissionCode.ACCESS_CAMERA.value == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scanCard();
            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
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
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.ok_button_text);
                    intent.putExtra(GenericResultActivity.TOP_BUTTON_MESSAGE, R.string.register_camera_unavailable_screen_open_settings);
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.nextButton) {
            atmCardNumber = binding.atmCardNumberInputView.getText().trim();
            if (TextUtils.isEmpty(atmCardNumber)) {
                onInvalidCardNumber(false);
                return;
            } else if (atmCardNumber.length() != MAX_FORMATTED_CARD_NUMBER_LENGTH) {
                onInvalidCardNumber(true);
                return;
            }
            atmPin = binding.atmPinNumberInputView.getText().trim();
            if (TextUtils.isEmpty(atmPin)) {
                onInvalidPin(false);
            } else {
                //because we are in registration, this could be the first time the device ID is requested, in which case the old mechanism would kick in
                // and the user would be requested for the device state permission;
                //to prevent this, we call getDeviceUuid() so that the device ID is generated, then we 'request' the device permission, which should cause
                //the new mechanism to kick in
                DeviceUtils.getDeviceUuid();
                PermissionFacade.requestDeviceStatePermission(this, () -> presenter.onContinueClicked(atmCardNumber, atmPin));
            }
        } else if (v.getId() == R.id.icon_view) {
            PermissionHelper.requestCameraAccessPermission(this, this::scanCard);
        }
    }

    @Override
    public void onInvalidCardNumber(boolean isInvalidLength) {
        binding.atmCardNumberInputView.setError(getString(R.string.register_atm_card_length_validation_error));
        announceErrorText(binding.atmCardNumberInputView.getErrorTextView());
    }

    @Override
    public void onInvalidPin(boolean isInvalidLength) {
        binding.atmPinNumberInputView.setError(getString(R.string.register_atm_pin_length_validation_error));
        announceErrorText(binding.atmPinNumberInputView.getErrorTextView());
    }

    @Override
    public void showAlreadyRegisterDialog() {
        BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.already_register_dialog_title))
                .message(getString(R.string.already_register_digital_profile))
                .positiveDismissListener((dialog, which) -> {
                    BaseAlertDialog.INSTANCE.dismissAlertDialog();
                    Intent intent = new Intent(RegisterAtmValidationActivity.this, LinkingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                })
                .build());
    }

    @Override
    public void showCardNumberAndPinFailureDialog(final RegisterProfileDetail registerProfileDetailObj) {
        BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.card_no_pin_dont_match_dialog_title))
                .message(getString(R.string.card_no_pin_dont_match_dialog_msg))
                .positiveDismissListener((dialog, which) -> {
                    BaseAlertDialog.INSTANCE.dismissAlertDialog();
                    binding.atmCardNumberInputView.clear();
                    binding.atmPinNumberInputView.clear();
                })
                .build());
    }

    @Override
    public void onMobileRecordNotFound(RegisterProfileDetail registerProfileDetailObj) {
        Intent callPartialRegisterActivity = new Intent(RegisterAtmValidationActivity.this, PartialRegistrationInfoActivity.class);
        callPartialRegisterActivity.putExtra(getString(R.string.register_profile_detail_obj), registerProfileDetailObj);
        startActivity(callPartialRegisterActivity);
    }

    @Override
    public void goToConfirmContactDetailScreen(RegisterProfileDetail registerProfileDetailObj) {
        Intent intent = new Intent(RegisterAtmValidationActivity.this, RegisterConfirmContactDetails2faActivity.class);
        intent.putExtra(AppConstants.RESULT, registerProfileDetailObj);
        startActivity(intent);
    }

    @Override
    public void showInvalidCardNumberDialog(String failureMessage) {
        BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                .title("Invalid card number")
                .message(failureMessage)
                .positiveDismissListener((dialog, which) -> {
                    BaseAlertDialog.INSTANCE.dismissAlertDialog();
                    binding.atmCardNumberInputView.clear();
                    binding.atmPinNumberInputView.clear();
                })
                .build());
    }

    @Override
    public void showErrorDialog(String error) {
        BaseAlertDialog.INSTANCE.showErrorAlertDialog(error);
    }

    private void announceErrorText(TextView textView) {
        if (isAccessibilityEnabled()) {
            AccessibilityUtils.announceRandValueTextFromView(textView);
        }
    }

    private void scanCard() {
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
                binding.atmCardNumberInputView.setSelectedValue(scanResult.cardNumber);
                binding.atmPinNumberInputView.setSelectedValue("");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancel_menu_item) {
            CommonUtils.showAlertDialogWelcomeScreen(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showSolePropErrorMessage() {
        showMessage(getString(R.string.sole_proprietor), getString(R.string.sole_proprietor_registration_message), (dialog, which) -> goToLaunchScreen(RegisterAtmValidationActivity.this));
    }

    @Override
    public void showBusinessBankingProfileErrorMessage() {
        showMessage(getString(R.string.register_business_profile), getString(R.string.register_business_error_message), (dialog, which) -> goToLaunchScreen(RegisterAtmValidationActivity.this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isAccessibilityEnabled()) {
            binding.atmPinNumberInputView.getEditText().sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
        }
        if (settingsIntentInvoked) {
            if (PackageManager.PERMISSION_GRANTED == this.checkCallingOrSelfPermission(Manifest.permission.CAMERA)) {
                scanCard();
                settingsIntentInvoked = false;
            }
        }
    }
}