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

package com.barclays.absa.banking.presentation.sureCheck;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.EnterSureCheckVerificationNumberActivityBinding;
import com.barclays.absa.banking.framework.ConnectivityMonitorActivity;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.presentation.shared.datePickerUtils.RebuildUtils;
import com.barclays.absa.utils.CommonUtils;

import org.jetbrains.annotations.NotNull;

import styleguide.utils.extensions.StringExtensions;

public class EnterVerificationNumber2faActivity extends ConnectivityMonitorActivity implements View.OnClickListener, EnterVerificationNumberView {

    private String enteredVerificationNumber;
    protected EnterSureCheckVerificationNumberActivityBinding binding;
    private EnterVerificationNumberPresenter presenter;
    private SureCheckDelegate sureCheckDelegate;
    private boolean isUserLoggedIn = getAppCacheService().getSecureHomePageObject() != null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.enter_sure_check_verification_number_activity, null, false);
        setContentView(binding.getRoot());

        if (enteredVerificationNumber != null) {
            binding.enterCodeNormalInputView.setSelectedValue(enteredVerificationNumber);
        }
        binding.submitButton.setOnClickListener(this);
        binding.resendButton.setOnClickListener(this);
        binding.cancelButton.setOnClickListener(this);

        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.TVN_FALLBACK_CONST, BMBConstants.CONFIRM_CONST, BMBConstants.TRUE_CONST);
        presenter = new EnterVerificationNumberPresenter(this);

        String contentText = getString(R.string.sure_check_title_content_tvn);
        String enterCodeTitleNormalInputViewTitleText = getString(R.string.sure_check_enter_code_tvn);
        String enterCodeHintNormalInputViewHintText = getString(R.string.sure_check_enter_code_hint_tvn);
        String resendButtonTitle = getString(R.string.sure_check_resend_button_tvn);
        String toolbarTitle = getString(R.string.sure_check_toolbar_title_tvn);
        String labelText;

        if ("S".equalsIgnoreCase(getAppCacheService().getSureCheckNotificationMethod())) {
            final String sureCheckCellphoneNumber = getAppCacheService().getSureCheckCellphoneNumber();
            String maskedCellphoneNumber = StringExtensions.toMaskedCellphoneNumber(sureCheckCellphoneNumber);
            labelText = getString(R.string.tvn_heading_2fa_SMS, maskedCellphoneNumber);
            if (getAppCacheService().isLinkingFlow()) {
                labelText = getString(R.string.rvn_heading_2fa);
                contentText = getString(R.string.sure_check_title_content_rvn);
                enterCodeTitleNormalInputViewTitleText = getString(R.string.sure_check_enter_code_rvn);
                enterCodeHintNormalInputViewHintText = getString(R.string.sure_check_enter_code_hint_rvn);
                toolbarTitle = getString(R.string.sure_check_toolbar_title_rvn);
                resendButtonTitle = getString(R.string.sure_check_resend_button_rvn);
            }
        } else {
            final String sureCheckEmail = getAppCacheService().getSureCheckEmail();
            String maskedEmailAddress = StringExtensions.toMaskedEmailAddress(sureCheckEmail);
            labelText = getString(R.string.tvn_heading_2fa_email, maskedEmailAddress);
            if (getAppCacheService().isLinkingFlow()) {
                labelText = getString(R.string.rvn_heading_2fa_email, maskedEmailAddress);
                contentText = getString(R.string.sure_check_title_content_rvn);
                enterCodeTitleNormalInputViewTitleText = getString(R.string.sure_check_enter_code_rvn);
                enterCodeHintNormalInputViewHintText = getString(R.string.sure_check_enter_code_hint_rvn);
                toolbarTitle = getString(R.string.sure_check_toolbar_title_rvn);
                resendButtonTitle = getString(R.string.sure_check_resend_button_rvn);
            }
        }
        binding.enterCodePrimaryContentAndLabelView.setLabelText(labelText);
        binding.enterCodePrimaryContentAndLabelView.setContentText(contentText);
        binding.enterCodeNormalInputView.setTitleText(enterCodeTitleNormalInputViewTitleText);
        binding.enterCodeNormalInputView.setHintText(enterCodeHintNormalInputViewHintText);
        binding.resendButton.setText(resendButtonTitle);

        RebuildUtils.setupToolBar(this, toolbarTitle, R.drawable.ic_arrow_back_white, false, null);
        sureCheckDelegate = getAppCacheService().getSureCheckDelegate();
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
            if (isUserLoggedIn) {
                onBackPressed();
            } else {
                CommonUtils.callWelcomeActivity(this);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancelButton:
                if (!isUserLoggedIn) {
                    CommonUtils.callWelcomeActivity(this);
                } else {
                    onBackPressed();
                }
                break;
            case R.id.submitButton:
                presenter.submitButtonInvoked(binding.enterCodeNormalInputView.getSelectedValue());
                break;

            case R.id.resendButton:
                presenter.resendVerficationNumber();
                break;
            default:
                break;
        }
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        enteredVerificationNumber = binding.enterCodeNormalInputView.getSelectedValue();
    }

    @Override
    public void showIncorrectRVNCodeMessage(int retriesRemaining) {
        String errorMessage = getString(R.string.verification_error_message_2fa, retriesRemaining);
        binding.enterCodeNormalInputView.setError(errorMessage);
    }

    @Override
    public void showSuccessOutcome() {
        sureCheckDelegate.onSureCheckProcessed();
        finish();
    }

    @Override
    public void showFailureOutcome() {
        if (sureCheckDelegate != null) {
            sureCheckDelegate.onSureCheckFailed();
        }
        finish();
    }

    @Override
    public void showValidationError(int errorMessageId) {
        String textToUse = getAppCacheService().isLinkingFlow() ? getString(R.string.sure_check_rvn) : getString(R.string.sure_check_tvn);
        String errorMessage = String.format(getString(errorMessageId), textToUse.replaceAll(":", ""));
        binding.enterCodeNormalInputView.setError(errorMessage);
    }

    @Override
    public void showOtpEntryScreen() {
        if (sureCheckDelegate != null) {
            sureCheckDelegate.initiateTransactionVerificationEntryScreen();
        }
    }

    @Override
    public void showSureCheckScreen() {
        if (sureCheckDelegate != null) {
            sureCheckDelegate.initiateV1CountDownScreen();
        }
    }

    @Override
    public void incorrectVerificationNumber(String errorMessage) {
        binding.enterCodeNormalInputView.setError(errorMessage);
    }

    @Override
    public void showRetriesExceeded() {
        showMessageError(getString(R.string.resend_RVN_exceeded), (dialog, which) -> finish());
    }
}