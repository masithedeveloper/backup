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

package com.barclays.absa.banking.presentation.generateTokens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.NoConnectionGenerateTokenActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.utils.TelephoneUtil;
import com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.SimplifiedLoginActivity;
import com.barclays.absa.utils.NetworkUtils;

import java.util.Timer;
import java.util.TimerTask;

public class NoConnectionGenerateTokenActivity extends BaseActivity implements View.OnClickListener, NoConnectionTokenView {

    private NoConnectionGenerateTokenActivityBinding binding;
    private NoConnectionPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.no_connection_generate_token_activity, null, false);
        setContentView(binding.getRoot());
        binding.getRoot().setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        binding.generateTokenButton.setOnClickListener(this);
        binding.reconnectButton.setOnClickListener(this);
        binding.contactView.setOnClickListener(this);
        binding.contactView.setContact(TelephoneUtil.getCallCentreContact(this));
        presenter = new NoConnectionPresenter(this);
        setToolBarWithNoBackButton("");
    }

    @Override
    public void onClick(View v) {
        preventDoubleClick(v);
        switch (v.getId()) {
            case R.id.generateTokenButton:
                Intent intent = new Intent(this, SimplifiedLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(BMBConstants.IS_GENERATE_TOKEN, true);
                startActivity(intent);
                break;
            case R.id.reconnectButton:
                presenter.reconnectInvoked();
                break;
            case R.id.contactView:
                TelephoneUtil.supportCallRegistrationIssues(NoConnectionGenerateTokenActivity.this);
                break;
            default:
                break;
        }
    }

    @Override
    public void attemptReconnect(int connectionCheckInterval, int totalConnectionChecks) {
        showProgressDialog();
        Timer connectivityTimer = new Timer();
        connectivityTimer.scheduleAtFixedRate(new ConnectivityTimerTask(totalConnectionChecks), 0, connectionCheckInterval);
    }

    private class ConnectivityTimerTask extends TimerTask {
        final int totalConnectionChecks;
        int currentConnectionCheck = 0;

        ConnectivityTimerTask(int totalConnectionChecks) {
            this.totalConnectionChecks = totalConnectionChecks;
        }

        @Override
        public void run() {
            if (currentConnectionCheck < totalConnectionChecks) {
                if (NetworkUtils.INSTANCE.isNetworkConnected()) {
                    runFinished();
                }
                ++currentConnectionCheck;
            } else {
                runFinished();
            }
        }

        private void runFinished() {
            cancel();
            finish();
            dismissProgressDialog();
        }
    }
}