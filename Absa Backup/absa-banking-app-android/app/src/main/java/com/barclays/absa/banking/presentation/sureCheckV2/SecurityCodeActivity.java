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

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.boundary.monitoring.MonitoringService;
import com.barclays.absa.banking.boundary.shared.TransactionVerificationInteractor;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationValidateCodeResponse;
import com.barclays.absa.banking.databinding.Activity2faSecurityCodeBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.presentation.sureCheck.SecurityCodeDelegate;

import java.util.HashMap;

public class SecurityCodeActivity extends BaseActivity implements View.OnClickListener {

    private Activity2faSecurityCodeBinding binding;
    private TransactionVerificationInteractor interactor;
    private final ExtendedResponseListener<TransactionVerificationValidateCodeResponse> validateCodeResponseListener = new ExtendedResponseListener<TransactionVerificationValidateCodeResponse>() {

        @Override
        public void onSuccess(final TransactionVerificationValidateCodeResponse successResponse) {
            dismissProgressDialog();
            SecurityCodeDelegate delegate = getAppCacheService().getSecurityCodeDelegate();
            String status = successResponse.getTxnStatus();
            if (BMBConstants.SUCCESS.equalsIgnoreCase(status)) {
                if (delegate != null) {
                    delegate.onSuccess();
                    finish();
                }
            } else {
                ResponseObject responseObject = (ResponseObject) getIntent().getSerializableExtra(AppConstants.RESULT);
                new MonitoringInteractor().logEvent(MonitoringService.MONITORING_EVENT_NAME_SECURITY_CODE_FAILED, responseObject);
                final int MAXIMUM_ATTEMPTS = 5;
                final int attemptsRemaining = successResponse.getRemainingVerificationAttempts();
                BMBLogger.d("x-Response", "attemptsRemaining = " + attemptsRemaining);
                final int attemptsSofar = MAXIMUM_ATTEMPTS - attemptsRemaining;

                if (attemptsRemaining > 0) {
                    showMessageError(getString(R.string.the_security_code_you_have_entered_is_incorrect, attemptsSofar, MAXIMUM_ATTEMPTS));
                } else if ("".equals(successResponse.getTxnMessage())) {
                    showMessageError(BMBConstants.FAILURE);
                } else {
                    showMessageError(successResponse.getTxnMessage());
                    if ("RETRIES EXCEEDED".equalsIgnoreCase(successResponse.getTxnMessage())) {
                        int FINISH_DELAY = 2000;
                        new Handler(getMainLooper()).postDelayed(() -> {
                            SecurityCodeActivity.this.finish();
                            goToLaunchScreen(SecurityCodeActivity.this);
                        }, FINISH_DELAY);
                    }
                }
            }
        }

        @Override
        public void onFailure(final ResponseObject failureResponse) {
            ResponseObject responseObject = (ResponseObject) getIntent().getSerializableExtra(AppConstants.RESULT);
            new MonitoringInteractor().logEvent(MonitoringService.MONITORING_EVENT_NAME_SECURITY_CODE_FAILED, responseObject);
            dismissProgressDialog();
            String errorMessage = failureResponse.getErrorMessage();
            binding.securityCodeNormalInputView.setError(errorMessage);
            SecurityCodeDelegate delegate = getAppCacheService().getSecurityCodeDelegate();
            if (delegate != null) {
                delegate.onFailure(errorMessage);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_2fa_security_code, null, false);
        setContentView(binding.getRoot());

        validateCodeResponseListener.setView(this);
        binding.btnContinue.setOnClickListener(this);

        interactor = new TransactionVerificationInteractor();
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(R.string.security_code_required);
        }
        recordMonitoringEvent();
    }

    private void recordMonitoringEvent() {
        HashMap<String, Object> eventData = new HashMap<>();
        eventData.put(MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_TIMESTAMP, System.currentTimeMillis());
        new MonitoringInteractor().logMonitoringEvent(MonitoringService.MONITORING_EVENT_NAME_SECURITY_CODE_SCREEN_SHOWN, eventData);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cancel_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancel_menu_item) {
            SecurityCodeDelegate delegate = getAppCacheService().getSecurityCodeDelegate();
            if (delegate != null) {
                delegate.onCancel();
            }
            finish();
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        preventDoubleClick(v);
        String securityCode = binding.securityCodeNormalInputView.getSelectedValue().replace(" ", "");
        interactor.validateSecurityCode(securityCode, validateCodeResponseListener);
    }

    @Override
    public void onBackPressed() {
        // disable back press
    }
}