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
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.RegisterProfileDetail;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType;
import com.barclays.absa.banking.databinding.ActivityRegisterConfirmContactDetailsRebrandBinding;
import com.barclays.absa.banking.framework.ConnectivityMonitorActivity;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity;
import com.barclays.absa.utils.CommonUtils;
import com.barclays.absa.utils.SharedPreferenceService;

public class RegisterConfirmContactDetails2faActivity extends ConnectivityMonitorActivity implements View.OnClickListener, RegisterConfirmContactDetailsView {

    private RegisterConfirmContactDetailsPresenter presenter;
    private RegisterProfileDetail registerProfileDetailObj;
    private ActivityRegisterConfirmContactDetailsRebrandBinding binding;
    private Handler handler = new Handler();

    private SureCheckDelegate sureCheckDelegate = new SureCheckDelegate(this) {
        @Override
        public void onSureCheckProcessed() {
            //go to personal details screen
            handler.postDelayed(RegisterConfirmContactDetails2faActivity.this::navigateToPersonalDetailsScreen, 250);
        }

        @Override
        public void onSureCheckFailed() {
            registerProfileDetailObj.setShowPasswordScreen(false);
            goToRegistrationUnsuccessfulScreen(registerProfileDetailObj);

        }

        @Override
        public void onSureCheckRejected() {
            goToRegistrationUnsuccessfulScreen(registerProfileDetailObj);
        }

        @Override
        public void onSureCheckCancelled() {
            registerProfileDetailObj.setShowPasswordScreen(false);
            goToRegistrationUnsuccessfulScreen(registerProfileDetailObj);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_register_confirm_contact_details_rebrand, null, false);
        setContentView(binding.getRoot());

        setToolBarBackWithMenu(getString(R.string.register), R.menu.cancel_menu);
        presenter = new RegisterConfirmContactDetailsPresenter(this, sureCheckDelegate);
        initViews();
        configureTalkBack();
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.REGISTER_CONST, BMBConstants.CONFIRM_CONTACT_DETAILS_CONST, BMBConstants.TRUE_CONST);
    }

    private void configureTalkBack() {
        if (isAccessibilityEnabled()) {
            String lastDigits = binding.contactDetailText.getText().toString().substring(8, 12);
            String formattedDigits = lastDigits.replace("", ",");
            binding.contactDetailText.setContentDescription(getString(R.string.talkback_register_phone_number_masked, formattedDigits));
            binding.noButton.setContentDescription(getString(R.string.talkback_register_confirm_no_button));
            binding.yesButton.setContentDescription(getString(R.string.talkback_register_confirm_yes_button));
            binding.toolbar.toolbar.setContentDescription(getString(R.string.register));
        }
    }

    private void initViews() {
        registerProfileDetailObj = (RegisterProfileDetail) getIntent().getSerializableExtra(AppConstants.RESULT);
        presenter = new RegisterConfirmContactDetailsPresenter(this, sureCheckDelegate);

        if (TextUtils.isEmpty(registerProfileDetailObj.getCellPhoneNumberActual())) {
            displayEmailInfo(registerProfileDetailObj);
        } else {
            displayMobileInfo(registerProfileDetailObj);
        }
        binding.yesButton.setOnClickListener(this);
        binding.noButton.setOnClickListener(this);
    }

    private void displayMobileInfo(RegisterProfileDetail registerProfileDetailObj) {
        binding.titleView.setDescription(getString(R.string.register_confirm_number_prompt));
        final String cellPhoneNumber = registerProfileDetailObj.getCellPhoneNumber();
        String maskedMobileNumber = new StringBuilder(cellPhoneNumber != null ? cellPhoneNumber : "")
                .insert(3, " ")
                .insert(7, " ")
                .toString();
        binding.contactDetailText.setText(maskedMobileNumber);
        binding.pressNextToVerifyText.setText(getString(R.string.register_confirm_mobile_number_verify_prompt));
    }

    private void displayEmailInfo(RegisterProfileDetail registerProfileDetailObj) {
        binding.titleView.setDescription(getString(R.string.register_confirm_email_prompt));
        binding.contactDetailText.setText(registerProfileDetailObj.getEmail());
        binding.pressNextToVerifyText.setText(getString(R.string.register_confirm_email_verify_prompt));
    }

    @Override
    public void onClick(View v) {
        preventDoubleClick(v);
        getAppCacheService().setScanQRFlow(false);
        switch (v.getId()) {
            case R.id.yesButton:
                SharedPreferenceService.INSTANCE.setIsPartialRegistration(false);
                presenter.onYesButtonClicked();
                break;
            case R.id.noButton:
                SharedPreferenceService.INSTANCE.setIsPartialRegistration(true);
                presenter.onNoButtonClicked();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancel_menu_item) {
            CommonUtils.showAlertDialogWelcomeScreen(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToRegistrationUnsuccessfulScreen(RegisterProfileDetail registerProfileDetailObj) {
        registerProfileDetailObj.setShowPasswordScreen(false);

        IntentFactory.IntentBuilder failureResultScreenBuilder = IntentFactory.getFailureResultScreenBuilder(this, R.string.registration_failure_text, "");
        failureResultScreenBuilder.setGenericResultDoneButton(this, v -> {
            Intent welcomeScreenIntent = new Intent(RegisterConfirmContactDetails2faActivity.this, WelcomeActivity.class);
            welcomeScreenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(welcomeScreenIntent);
        });
        startActivity(failureResultScreenBuilder.build());
    }

    @Override
    public void navigateToPartialRegisterActivity() {
        Intent callPartialRegisterActivity = new Intent(this, PartialRegistrationInfoActivity.class);
        callPartialRegisterActivity.putExtra(getString(R.string.register_profile_detail_obj), registerProfileDetailObj);
        startActivity(callPartialRegisterActivity);
    }

    @Override
    public void goToCountDownTimerScreen(TransactionVerificationType verificationType) {
        sureCheckDelegate.initiateV1CountDownScreen();
    }

    @Override
    public void goToOtpEntryScreen() {
        sureCheckDelegate.initiateTransactionVerificationEntryScreen();
    }

    @Override
    public void navigateToPersonalDetailsScreen() {
        registerProfileDetailObj.setShowPasswordScreen(true);
        Intent resultCodeSuccessIntent = new Intent(RegisterConfirmContactDetails2faActivity.this, RegisterActivity.class);
        resultCodeSuccessIntent.putExtra(getString(R.string.register_profile_detail_obj), registerProfileDetailObj);
        startActivity(resultCodeSuccessIntent);
        finish();
    }
}
