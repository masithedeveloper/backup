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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.shared.TransactionVerificationInteractor;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationValidateCodeResponse;
import com.barclays.absa.banking.databinding.Activity2faOfflineOtpBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.presentation.generateTokens.OfflineOtpGenerator;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;
import com.barclays.absa.utils.AccessibilityUtils;

import org.jetbrains.annotations.NotNull;

public class OfflineOtpActivity extends BaseActivity implements View.OnClickListener {
    private Activity2faOfflineOtpBinding binding;

    private ExtendedResponseListener<TransactionVerificationValidateCodeResponse> validateCodeResponseListener = new ExtendedResponseListener<TransactionVerificationValidateCodeResponse>() {
        @Override
        public void onSuccess(final TransactionVerificationValidateCodeResponse response) {
            String status = response.getTxnStatus();
            if (BMBConstants.SUCCESS.equalsIgnoreCase(status)) {
                SureCheckDelegate sureCheckDelegate = getAppCacheService().getSureCheckDelegate();
                if (sureCheckDelegate != null) {
                    sureCheckDelegate.onSureCheckProcessed();
                }
                finish();
            } else {
                final int retriesLeft = response.getRetries();
                if (retriesLeft > 0) {
                    final int MAX_NUMBER_OF_RETRIES = 3;
                    final int retriesSoFar = MAX_NUMBER_OF_RETRIES - retriesLeft;
                    String errorText = getString(R.string.incorrect_token_number, retriesSoFar, MAX_NUMBER_OF_RETRIES);
                    binding.uniqueNumberNormalInputView.setError(errorText);
                } else {
                    SureCheckDelegate sureCheckDelegate = getAppCacheService().getSureCheckDelegate();
                    if (sureCheckDelegate != null) {
                        sureCheckDelegate.onSureCheckFailed();
                    }
                    navigateToErrorResultActivity(R.string.token_number_invalid, getString(R.string.you_need_to_make_a_new_transaction));
                }
            }
            dismissProgressDialog();
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            navigateToErrorResultActivity(R.string.token_number_verification, String.format("%s\n\n%s", getString(R.string.to_keep_you_secure), getString(R.string.please_make_a_new_transaction)));
            dismissProgressDialog();
        }
    };

    private void navigateToErrorResultActivity(int headerId, String message) {
        Intent intent = IntentFactory.getFailureResultScreenBuilder(OfflineOtpActivity.this, headerId, message).build();
        GenericResultActivity.bottomOnClickListener = v -> {
            if (BMBApplication.getInstance().getUserLoggedInStatus()) {
                loadAccountsAndGoHome();
            } else {
                SureCheckDelegate delegate = getAppCacheService().getSureCheckDelegate();
                if (delegate != null) {
                    delegate.onSureCheckFailed();
                } else {
                    BMBLogger.d("x-Response", "delegate is null");
                }
                BMBApplication.getInstance().getTopMostActivity().finish();
            }
        };
        startActivity(intent);
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validateCodeResponseListener.setView(this);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_2fa_offline_otp, null, false);
        setContentView(binding.getRoot());

        if (getAppCacheService().isPrimarySecondFactorDevice()) {
            String offlineOtp = OfflineOtpGenerator.generateOfflineToken();
            binding.uniqueNumberNormalInputView.setSelectedValue(offlineOtp);
        }
        binding.continueButton.setOnClickListener(this);
        setToolBarWithNoBackButton("");
        setupTalkBack();
    }

    public void setupTalkBack() {
        AccessibilityUtils.announceErrorFromTextWidget(binding.instructionTertiaryContentAndLabelView.getContentTextView());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cancel_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        final SureCheckDelegate sureCheckDelegate = getAppCacheService().getSureCheckDelegate();
        if (sureCheckDelegate != null) {
            sureCheckDelegate.onSureCheckCancelled();
        }
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        preventDoubleClick(v);
        if (v.getId() == binding.continueButton.getId()) {
            TransactionVerificationInteractor interactor = new TransactionVerificationInteractor();

            String tokenNumber = binding.uniqueNumberNormalInputView.getSelectedValue();

            if (TextUtils.isEmpty(tokenNumber)) {
                binding.uniqueNumberNormalInputView.setError(getString(R.string.please_enter_a_token_number));
                return;
            }

            if (getAppCacheService().getSecureHomePageObject() != null) {
                BMBLogger.d("x response ", " validate post log on");
                interactor.validateOtpPostLogon(tokenNumber, getAppCacheService().getSureCheckReferenceNumber(), validateCodeResponseListener);
            } else {
                BMBLogger.d("x response ", " validate pre log on");
                interactor.validateOtp(tokenNumber, getAppCacheService().getSureCheckReferenceNumber(), validateCodeResponseListener);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable back press
    }
}