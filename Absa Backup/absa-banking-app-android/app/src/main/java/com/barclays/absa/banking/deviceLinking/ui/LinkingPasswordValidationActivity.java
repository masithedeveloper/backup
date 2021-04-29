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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.PasswordDigit;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType;
import com.barclays.absa.banking.databinding.PasswordValidationActivityBinding;
import com.barclays.absa.banking.deviceLinking.ui.verifyAlias.VerifyAliasDetails2faActivity;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.linking.ui.GenericBottomSheetContent;
import com.barclays.absa.banking.linking.ui.GenericBottomSheetDialogFragment;
import com.barclays.absa.banking.manage.devices.linking.ForgotPassword2faActivity;
import com.barclays.absa.banking.newToBank.NewToBankConstants;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.sureCheck.SecurityCodeDelegate;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;
import com.barclays.absa.banking.presentation.sureCheckV2.SecurityCodeActivity;
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.utils.AccessibilityUtils;
import com.barclays.absa.utils.AnalyticsUtil;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.barclays.absa.banking.linking.ui.IdentificationAndVerificationConstants.ID_AND_V_LINK_DEVICE;

public class LinkingPasswordValidationActivity extends BaseActivity implements View.OnClickListener, TextWatcher, LinkingPasswordValidationView {

    public static final String IS_NO_PRIMARY_STATE = "isNoPrimaryState";
    public static String PASSWORD_LOCKED = "PASSWORD_LOCKED";

    private static final int MAX_PASSWORD_DIGITS = 12;
    private LinkedList<EditText> passwordEditTextLinkedList;
    private ArrayList<Integer> passwordPositions;
    private String passwordPositionMessage;
    private SecureHomePageObject secureHomePageObject;
    private PasswordValidationActivityBinding binding;
    private LinkingPasswordValidationPresenter presenter;
    private EnterPasswordSureCheckDelegate sureCheckDelegate;
    private boolean isNoPrimaryState;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.password_validation_activity, null, false);
        setContentView(binding.getRoot());

        if (NewToBankConstants.ON_NEW_TO_BANK_FLOW) {
            setToolBar(R.string.linking_link_device, null);
        } else {
            setToolBarBack(R.string.linking_link_device);
        }
        if (getIntent().getExtras() != null) {
            isNoPrimaryState = getIntent().getExtras().getBoolean(IS_NO_PRIMARY_STATE, false);
        }

        mScreenName = BMBConstants.PASSWORD_CONST;
        mSiteSection = BMBConstants.SIMPLIFIED_LOGIN_CONST;
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.PASSWORD_CONST,
                BMBConstants.SIMPLIFIED_LOGIN_CONST, BMBConstants.TRUE_CONST);

        secureHomePageObject = getAppCacheService().getSecureHomePageObject();
        if (secureHomePageObject == null) {
            BaseAlertDialog.INSTANCE.showGenericErrorDialog((dialog, which) -> returnToWelcomeScreen());
            return;
        }

        passwordPositions = new ArrayList<>();
        final List<PasswordDigit> passwordDigits = secureHomePageObject.getPasswordDigits();
        if (secureHomePageObject != null && passwordDigits != null) {
            for (int i = 0; i < passwordDigits.size(); i++) {
                final PasswordDigit passwordDigit = passwordDigits.get(i);
                if (passwordDigit != null) {
                    passwordDigit.getIndex();
                    passwordPositions.add(Integer.valueOf(passwordDigit.getIndex()));
                }
            }

            String[] numberPositions = getResources().getStringArray(R.array.number_positions);
            passwordPositionMessage = "";
            if (passwordPositions.size() > 2) {
                passwordPositionMessage = getString(R.string.char_missing_password_fields, numberPositions[passwordPositions.get(0)], numberPositions[passwordPositions.get(1)], numberPositions[passwordPositions.get(2)]);
            }
            populateView();
            sureCheckDelegate = new EnterPasswordSureCheckDelegate(this);
            presenter = new LinkingPasswordValidationPresenter(this, sureCheckDelegate);
        } else {
            navigateToNicknameScreen();
        }
    }

    private void navigateToNicknameScreen() {
        startActivity(new Intent(this, CreateNicknameActivity.class));
    }

    @Override
    public void onBackPressed() {
        if (NewToBankConstants.ON_NEW_TO_BANK_FLOW) {
            showEndSessionDialogPrompt();
        } else {
            AnalyticsUtil.trackActionFromStaticContext(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_AccessAccountNumberAndPIN_CloseButtonClicked");
            super.onBackPressed();
        }
    }

    private void showEndSessionDialogPrompt() {
        BaseAlertDialog.INSTANCE.showYesNoDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.new_to_bank_are_you_sure))
                .message(getString(R.string.new_to_bank_if_you_go_back))
                .positiveDismissListener((dialog, which) -> returnToWelcomeScreen()));
    }

    private void returnToWelcomeScreen() {
        Intent intent = new Intent(LinkingPasswordValidationActivity.this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    public boolean isNoPrimaryState() {
        return isNoPrimaryState;
    }

    private void populateView() {
        binding.tvMissingChar.setText(passwordPositionMessage);
        binding.btnLinkDeviceContinue.setOnClickListener(this);
        binding.tvForgotPassword.setOnClickListener(this);
        binding.tvHowTo.setOnClickListener(this);
        binding.tvResetPassword.setOnClickListener(this);

        if (isAccessibilityEnabled()) {
            AccessibilityUtils.announceRandValueTextFromView(binding.tvMissingChar);
            binding.btnLinkDeviceContinue.setContentDescription(getString(R.string.talkback_linking_activity_continue));
            binding.tvResetPassword.setContentDescription(getString(R.string.talkback_linking_restart_process));
        }

        passwordEditTextLinkedList = new LinkedList<>();
        Collections.addAll(passwordEditTextLinkedList,
                binding.etPassword1, binding.etPassword2, binding.etPassword3, binding.etPassword4,
                binding.etPassword5, binding.etPassword6, binding.etPassword7, binding.etPassword8,
                binding.etPassword9, binding.etPassword10, binding.etPassword11, binding.etPassword12);
        removeNonRequiredPasswordFields();
        activateRequiredPasswordFields();

        int count = 0;
        for (EditText editText : passwordEditTextLinkedList) {
            editText.clearFocus();
            animate(editText, R.anim.password_contract);
            if (editText.isEnabled()) {
                editText.setOnFocusChangeListener((v, hasFocus) -> {
                    editText.setBackgroundResource(hasFocus ? R.drawable.password_edittext_selected : R.drawable.password_edittext_selection);
                    animate(editText, hasFocus ? R.anim.password_expand : R.anim.password_contract);
                });

                int finalCount = count;
                editText.setOnKeyListener((v, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (editText.getText().toString().isEmpty()) {
                            if (finalCount > 0 && secureHomePageObject.getPasswordDigits() != null) {
                                PasswordDigit passwordDigit = secureHomePageObject.getPasswordDigits().get(finalCount - 1);
                                if (passwordDigit != null) {
                                    passwordDigit.getIndex();
                                    passwordEditTextLinkedList.get(Integer.parseInt(passwordDigit.getIndex())).requestFocus();
                                }
                            }
                            return true;
                        }
                    }
                    return false;
                });
                count++;
            }
        }
        resetRequiredPasswordFields();
    }

    private void removeNonRequiredPasswordFields() {
        for (int x = 1; x <= MAX_PASSWORD_DIGITS; x++) {
            try {
                if (secureHomePageObject != null && secureHomePageObject.getPasswordLength() != null && x > Integer.valueOf(secureHomePageObject.getPasswordLength())) {
                    passwordEditTextLinkedList.getLast().setVisibility(View.GONE);
                    passwordEditTextLinkedList.removeLast();
                }
            } catch (NumberFormatException e) {
                BMBLogger.e(LinkingPasswordValidationActivity.class.getSimpleName(), "Invalid number format -> " + x);
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void activateRequiredPasswordFields() {
        for (int passwordPosition : passwordPositions) {
            if (passwordPosition < passwordEditTextLinkedList.size()) {
                EditText requiredPasswordEditText = passwordEditTextLinkedList.get(passwordPosition);
                requiredPasswordEditText.setEnabled(true);
                requiredPasswordEditText.setHint(String.valueOf(passwordPosition + 1));
                requiredPasswordEditText.setFocusableInTouchMode(true);
                requiredPasswordEditText.setCursorVisible(true);
                requiredPasswordEditText.addTextChangedListener(this);
                requiredPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                requiredPasswordEditText.setTransformationMethod(new BigDotPasswordTransformationMethod());
                requiredPasswordEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
            }
        }
    }

    @Override
    public void resetRequiredPasswordFields() {
        if (secureHomePageObject != null && secureHomePageObject.getPasswordDigits() != null)
            for (PasswordDigit item : secureHomePageObject.getPasswordDigits()) {
                if (item != null) {
                    item.getIndex();
                    int x = Integer.parseInt(item.getIndex());
                    if (x < passwordEditTextLinkedList.size()) {
                        passwordEditTextLinkedList.get(x).setText("");
                    }
                }
            }
    }

    @Override
    public void showAttemptsErrorMessage() {
        Toast.makeText(this, getString(R.string.attempt_title_3), Toast.LENGTH_LONG).show();
    }

    @Override
    public void goToForgotPasswordScreen() {
        navigateToForgotPasswordScreen();
    }

    @Override
    public void showInvalidPasswordCharactersError() {
        BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                .title(getString(R.string.alert))
                .message(getString(R.string.decouple_blank_pass_msg))
                .build());
    }

    @Override
    public void showNoPrimaryFailure() {
        showNoPrimaryDeviceScreen();
    }

    @Override
    public void showError(String failureResponse) {
        showMessageError(failureResponse);
    }

    @Override
    public void onClick(View v) {
        preventDoubleClick(v);
        switch (v.getId()) {
            case R.id.btn_linkDeviceContinue:
                commenceDeviceLinking();
                break;
            case R.id.tv_ForgotPassword: {
                AnalyticsUtil.trackActionFromStaticContext(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_AccessAccountNumberAndPIN_ForgotPasswordButtonClicked");
                navigateToForgotPasswordScreen();
                break;
            }
            case R.id.tv_howTo:
                startActivity(new Intent(this, ForgotPassword2faActivity.class));
                break;
            case R.id.tv_resetPassword:
                resetRequiredPasswordFields();
                break;
            default:
                break;
        }
    }

    private String getEditText(int id) {
        if (secureHomePageObject != null && secureHomePageObject.getPasswordDigits() != null && secureHomePageObject.getPasswordDigits().get(id) != null) {
            final String index = secureHomePageObject.getPasswordDigits().get(id).getIndex();
            return passwordEditTextLinkedList.get(Integer.parseInt(index)).getText().toString();
        }
        return "";
    }

    private void navigateToForgotPasswordScreen() {
        GenericBottomSheetContent bottomSheetContent = new GenericBottomSheetContent();
        bottomSheetContent.setToolbarActionTitle(R.string.done);
        bottomSheetContent.setContentText(R.string.linking_please_contact_us_to_help_reset_password);
        bottomSheetContent.setContactName(R.string.banking_app_support_title);
        bottomSheetContent.setContactNumber(R.string.banking_app_support_contact);

        GenericBottomSheetDialogFragment addBottomDialogFragment = GenericBottomSheetDialogFragment.newInstance(bottomSheetContent);

        GenericBottomSheetDialogFragment.setToolbarActionOnClickListener(view -> {
            AnalyticsUtil.INSTANCE.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_ScanFaceLearnMoreScreen_DoneButtonClicked");
            addBottomDialogFragment.dismiss();
        });

        addBottomDialogFragment.show(getSupportFragmentManager(), "");
    }

    private boolean isAllFieldsComplete() {
        return !getEditText(0).isEmpty() &&
                !getEditText(1).isEmpty() &&
                !getEditText(2).isEmpty();
    }

    private void commenceDeviceLinking() {
        try {
            final List<PasswordDigit> passwordDigits = secureHomePageObject.getPasswordDigits();
            String value1, value2, value3;
            value1 = value2 = value3 = "";
            if (passwordEditTextLinkedList != null && passwordDigits != null && !passwordDigits.isEmpty()) {
                final PasswordDigit firstPasswordDigit = passwordDigits.get(0);
                if (firstPasswordDigit != null) {
                    firstPasswordDigit.getIndex();
                    value1 = passwordEditTextLinkedList.get(Integer.parseInt(firstPasswordDigit.getIndex())).getText().toString();
                }
                final PasswordDigit secondPasswordDigit = passwordDigits.get(1);
                if (secondPasswordDigit != null) {
                    secondPasswordDigit.getIndex();
                    value2 = passwordEditTextLinkedList.get(Integer.parseInt(secondPasswordDigit.getIndex())).getText().toString();
                }
                final PasswordDigit thirdPasswordDigit = passwordDigits.get(2);
                if (thirdPasswordDigit != null) {
                    thirdPasswordDigit.getIndex();
                    value3 = passwordEditTextLinkedList.get(Integer.parseInt(thirdPasswordDigit.getIndex())).getText().toString();
                }
                presenter.onContinueInvoked(value1, value2, value3);
            }
        } catch (IndexOutOfBoundsException e) {
            BMBLogger.d(e.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        binding.btnLinkDeviceContinue.setEnabled(isAllFieldsComplete());

        for (int passwordPosition : passwordPositions) {
            EditText requiredPasswordEditText = passwordEditTextLinkedList.get(passwordPosition);
            if (TextUtils.isEmpty(requiredPasswordEditText.getText().toString())) {
                requiredPasswordEditText.requestFocus();
                return;
            }
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void goToCountDownTimerScreen(TransactionVerificationType verificationType) {
        if (verificationType == TransactionVerificationType.SURECHECKV1) {
            sureCheckDelegate.initiateV1CountDownScreen();
        } else {
            sureCheckDelegate.initiateV2CountDownScreen();
        }
    }

    @Override
    public void goToOtpEntryScreen() {
        sureCheckDelegate.initiateTransactionVerificationEntryScreen();
    }

    @Override
    public void showAlmostDoneScreen() {
        startActivity(new Intent(LinkingPasswordValidationActivity.this, LinkingAlmostDoneActivity.class));
    }

    @Override
    public void goToSecurityCodeEntryScreen() {
        getAppCacheService().setSecurityCodeDelegate(new SecurityCodeDelegate() {

            @Override
            public void onSuccess() {
                Intent sureCheckIntent = new Intent(LinkingPasswordValidationActivity.this, CreateNicknameActivity.class);
                startActivity(sureCheckIntent);
            }

            @Override
            public void onCancel() {
                goToLaunchScreen(LinkingPasswordValidationActivity.this);
            }

            @Override
            public void onFailure(String errorMessage) {
                dismissProgressDialog();
                showMessageError(errorMessage);
            }
        });
        startActivity(new Intent(this, SecurityCodeActivity.class));
    }

    @Override
    public void showDeviceLinkingFailureScreen() {
        GenericResultActivity.bottomOnClickListener = v -> {
            startActivity(new Intent(LinkingPasswordValidationActivity.this, WelcomeActivity.class));
            finish();
        };
        Intent deviceLinkingFailedIntent = new Intent(this, GenericResultActivity.class);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.IS_FAILURE, true);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.device_linking_error);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.SUB_MESSAGE, R.string.device_linking_error_explanation);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.cancel);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.CALL_US_CONTACT_NUMBER, getString(R.string.support_center_number));
        deviceLinkingFailedIntent.putExtra(BMBConstants.PRE_LOGIN_LAYOUT, true);
        startActivity(deviceLinkingFailedIntent);
    }

    private class EnterPasswordSureCheckDelegate extends SureCheckDelegate implements Serializable {

        EnterPasswordSureCheckDelegate(Context context) {
            super(context);
        }

        @Override
        public void onSureCheckCancelled() {
            goToLaunchScreen(LinkingPasswordValidationActivity.this);
        }

        @Override
        public void onSureCheckFailed() {
            handler.postDelayed(() -> {
                dismissProgressDialog();
                BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                        .title(getString(R.string.error))
                        .message(getString(R.string.surecheck_failed))
                        .positiveDismissListener((dialog, which) -> finish())
                        .build());
            }, 250);
        }

        @Override
        public void onSureCheckProcessed() {
            handler.postDelayed(this::goToCreateNicknameScreen, 250);
        }

        private void goToCreateNicknameScreen() {
            Intent createNickNameIntent = new Intent(context, CreateNicknameActivity.class);
            createNickNameIntent.putExtra(VerifyAliasDetails2faActivity.VERIFY_ALIAS_FROM_SCREEN, VerifyAliasDetails2faActivity.ENTER_PASSWORD_SCREEN);
            startActivity(createNickNameIntent);
        }

        @Override
        public void onSureCheckRejected() {
            showDeviceLinkingFailureScreen();
        }
    }

    private static class BigDotPasswordTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return new BigDotCharSequence(source);
        }

        private class BigDotCharSequence implements CharSequence {

            private final CharSequence sourceCharSequence;

            BigDotCharSequence(CharSequence source) {
                sourceCharSequence = source;
            }

            @Override
            public int length() {
                return sourceCharSequence.length();
            }

            @Override
            public char charAt(int index) {
                return '\u25cf';
            }

            @NotNull
            @Override
            public CharSequence subSequence(int start, int end) {
                return sourceCharSequence.subSequence(start, end);
            }
        }
    }
}