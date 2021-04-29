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

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.RegisterProfileDetail;
import com.barclays.absa.banking.databinding.ActivityRegisterCreatePasswordBinding;
import com.barclays.absa.banking.deviceLinking.ui.CreateNicknameActivity;
import com.barclays.absa.banking.framework.ConnectivityMonitorActivity;
import com.barclays.absa.banking.framework.PermissionFacade;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.SimplifiedLoginActivity;
import com.barclays.absa.utils.AnimationHelper;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.PermissionHelper;

import styleguide.buttons.OptionActionButtonView;
import styleguide.forms.NormalInputView;
import styleguide.utils.extensions.StringExtensions;

public class RegisterCreatePasswordActivity extends ConnectivityMonitorActivity implements RegisterCreatePasswordView {

    private RegisterCreatePasswordPresenter presenter;

    private RegisterProfileDetail registerProfileDetail;
    private String nameOfUser;
    private String navigatedFrom = "DS1";
    private final Bundle registrationBundle = new Bundle();
    private ActivityRegisterCreatePasswordBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_register_create_password, null, false);
        setContentView(binding.getRoot());
        boolean isPasswordResetFlow = getAppCacheService().isPasswordResetFlow();
        setToolBarBack(isPasswordResetFlow ? R.string.create_your_password : R.string.register);
        initViews();
        createTalkBacks();
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.REGISTER_CONST, BMBConstants.CREATE_PASSWORD_AND_SUREPHRASE_CONST, BMBConstants.TRUE_CONST);
    }

    private void createTalkBacks() {
        binding.titleText.setContentDescription(getString(R.string.talkback_create_password_header));
        binding.createYourPasswordTextView.setContentDescription(getString(R.string.talkback_create_password_sub_heading));
        binding.userPassword.setContentDescription(getString(R.string.talkback_create_password_field));
        binding.userPasswordConfirmation.setContentDescription(getString(R.string.talkback_verify_password_field));
        binding.numbersAndLettersView.setContentDescription(getString(R.string.talkback_password_number_letters));
        binding.minimumLengthView.setContentDescription(getString(R.string.talkback_password_minimum_length));
        binding.mustNotContainName.setContentDescription(getString(R.string.talkback_password_no_name));
        binding.noSequenceView.setContentDescription(getString(R.string.talkback_password_no_number_sequences));
        binding.continueButton.setContentDescription(getString(R.string.talkback_password_continue_button));
        binding.specialCharactersView.setContentDescription(getString(R.string.talkback_password_no_special_chars));
    }

    private void initViews() {
        presenter = new RegisterCreatePasswordPresenter(this);
        binding.continueButton.setEnabled(false);
        binding.mustNotContainName.setEnabled(false);
        binding.minimumLengthView.setEnabled(false);
        binding.noSequenceView.setEnabled(false);
        binding.numbersAndLettersView.setEnabled(false);
        binding.specialCharactersView.setEnabled(false);
        binding.userPassword.addValueViewTextWatcher(new PasswordTextWatcher());
        binding.userPassword.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(binding.userPassword.getText()) && binding.userPassword.getText().equals(binding.userPasswordConfirmation.getText())) {
                    binding.userPasswordConfirmation.setImageViewVisibility(View.VISIBLE);
                } else {
                    binding.userPasswordConfirmation.setImageViewVisibility(View.INVISIBLE);
                }
                binding.userPassword.showError(false);
            }
        });

        binding.userPasswordConfirmation.addValueViewTextWatcher(new PasswordTextWatcher());
        binding.userPasswordConfirmation.addValueViewTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(binding.userPassword.getText()) && binding.userPassword.getText().equals(binding.userPasswordConfirmation.getText())) {
                    binding.userPasswordConfirmation.setImageViewVisibility(View.VISIBLE);
                } else {
                    binding.userPasswordConfirmation.setImageViewVisibility(View.INVISIBLE);
                }
                binding.userPasswordConfirmation.showError(false);
            }
        });

        registerProfileDetail = (RegisterProfileDetail) getIntent().getSerializableExtra(
                getString(R.string.register_profile_detail_obj));

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(BMBConstants.NAVIGATED_FROM)) {
            navigatedFrom = getIntent().getExtras().getString(BMBConstants.NAVIGATED_FROM);
        }

        if (registerProfileDetail != null) {
            nameOfUser = (registerProfileDetail.getSurname());
        }

        binding.continueButton.setOnClickListener(v -> {
            if (validatePasswordFields()) {
                presenter.doneButtonTapped();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionHelper.PermissionCode.ACCESS_DEVICE_STATE.value) {
            Bundle mBundle = new Bundle();
            mBundle.putString(BMBConstants.PASSWORD, binding.userPassword.getSelectedValue());
            if (registerProfileDetail != null) {
                mBundle.putString(BMBConstants.ONLINE_PIN, registerProfileDetail.getOnlinePin());
                mBundle.putString(BMBConstants.SURE_PHRASE, registerProfileDetail.getFirstname() + " " + registerProfileDetail.getSurname());
            }
            if (grantResults.length > 0) {
                int permissionStatus = grantResults[0];
                switch (permissionStatus) {
                    case PackageManager.PERMISSION_GRANTED:
                        presenter.onDeviceStatePermissionGranted(binding.userPassword.getSelectedValue(), binding.userPasswordConfirmation.getSelectedValue(), registerProfileDetail, navigatedFrom);
                        break;
                    case PackageManager.PERMISSION_DENIED:
                        PermissionFacade.requestDeviceStatePermission(this, () -> presenter.onDeviceStatePermissionGranted(binding.userPassword.getSelectedValue(), binding.userPasswordConfirmation.getSelectedValue(), registerProfileDetail, navigatedFrom));
                        break;
                }
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
            CommonUtils.showAlertDialogWelcomeScreen(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void launchRegistrationResultScreen(ResponseObject responseObject) {
        Intent registrationResultIntent = new Intent(getApplicationContext(), RegistrationResultActivity.class);
        registrationResultIntent.putExtras(registrationBundle);
        registrationResultIntent.putExtra(AppConstants.RESULT, responseObject);
        registrationResultIntent.putExtra(PASSCODE_STRING, getIntent().getStringExtra(PASSCODE_STRING));
        registrationResultIntent.putExtra(SimplifiedLoginActivity.USER_POSITION, getIntent().getIntExtra(SimplifiedLoginActivity.USER_POSITION, 0));
        startActivity(registrationResultIntent);
    }

    @Override
    public void markAllPasswordRulesInvalid() {
        markAlphanumericValidationRule(false);
        markLengthValidationRule(false);
        markSpacesAndSpecialCharactersValidationRule(false);
        markNameOfUserValidationRule(false);
        markSequenceValidationRule(false);
        binding.continueButton.setEnabled(false);
    }

    @Override
    public void markSequenceValidationRule(boolean isValid) {
        changeLeftDrawable(isValid, binding.noSequenceView);
    }

    @Override
    public void markNameOfUserValidationRule(boolean isValid) {
        changeLeftDrawable(isValid, binding.mustNotContainName);
    }

    @Override
    public void markSpacesAndSpecialCharactersValidationRule(boolean isValid) {
        changeLeftDrawable(isValid, binding.specialCharactersView);
    }

    @Override
    public void markLengthValidationRule(boolean isValid) {
        changeLeftDrawable(isValid, binding.minimumLengthView);
    }

    @Override
    public void markAlphanumericValidationRule(boolean isValid) {
        changeLeftDrawable(isValid, binding.numbersAndLettersView);
    }

    private void changeLeftDrawable(boolean isValid, OptionActionButtonView optionActionButtonView) {
        int markingDrawable = isValid ? R.drawable.ic_check_dark : R.drawable.ic_close;
        optionActionButtonView.setIcon(markingDrawable);
    }

    @Override
    public void requestDeviceStatePermissions() {
        PermissionFacade.requestDeviceStatePermission(this, () -> {
            registrationBundle.putString(BMBConstants.PASSWORD, binding.userPasswordConfirmation.getText());
            if (registerProfileDetail != null) {
                registrationBundle.putString(BMBConstants.ONLINE_PIN, registerProfileDetail.getOnlinePin());
                String surePhrase = StringExtensions.toTitleCase(registerProfileDetail.getFirstname()) + " " + StringExtensions.toTitleCase(registerProfileDetail.getSurname());
                registrationBundle.putString(BMBConstants.SURE_PHRASE, surePhrase);
            }
            registrationBundle.putBoolean("isPartiallyRegistered", false);
            presenter.onDeviceStatePermissionGranted(binding.userPassword.getText(), binding.userPasswordConfirmation.getText(), registerProfileDetail, navigatedFrom);
        });
    }

    @Override
    public void showInvalidPasswordMessage() {
        setError(getString(R.string.password_format), binding.userPassword);
    }

    @Override
    public void showPasswordsDoNotMatch() {
        setError(getString(R.string.fields_same), binding.userPasswordConfirmation);
    }

    @Override
    public void goToCreateNicknameScreen() {
        startActivity(new Intent(this, CreateNicknameActivity.class));
    }

    @Override
    public void goToHomeScreen() {
        loadAccountsAndGoHome();
    }

    @Override
    public void goToLoginScreen() {
        logoutAndGoToStartScreen();
    }

    @Override
    public void returnValidity(boolean isPasswordValid) {
        binding.userPassword.setImageViewVisibility(isPasswordValid ? View.VISIBLE : View.INVISIBLE);
        binding.continueButton.setEnabled(isPasswordValid);
    }

    private void setError(String errorMessage, NormalInputView normalInputView) {
        AnimationHelper.shakeShakeAnimate(normalInputView);
        normalInputView.setError(errorMessage);
    }

    private boolean validatePasswordFields() {
        if (TextUtils.isEmpty(binding.userPassword.getText())) {
            setError(String.format(getString(R.string.pleaseEnterValid), "password"), binding.userPassword);
            return false;
        }
        if (TextUtils.isEmpty(binding.userPasswordConfirmation.getText())) {
            setError(String.format(getString(R.string.pleaseEnterValid), "password"), binding.userPasswordConfirmation);
            return false;
        }
        return true;
    }

    private class PasswordTextWatcher implements TextWatcher {

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence password, int start, int before, int count) {
            presenter.validatePassword(password, nameOfUser);
        }
    }
}