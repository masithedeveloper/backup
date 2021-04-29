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

package com.barclays.absa.banking.presentation.connectivity;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.R;
import com.barclays.absa.banking.databinding.ActivityNoConnectivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktDelegate;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktHandler;

public class NoConnectivityActivity extends BaseActivity implements NoConnectivityView, View.OnClickListener {

    public static final String MESSAGE = "message";
    public static final String INSTRUCTION = "instruction";
    public static final String CONNECTIVITY_TYPE = "action_to_perform";
    public static final String CONNECTIVITY_ENTERSEKT = "Entersekt";
    public static final String CONNECTIVITY_MAINTENANCE = "Maintenance";
    public static final String CONNECTIVITY_DATA_SIGNAL = "DataSignal";
    private String message;
    private String instruction;
    private String connectivityType = "";
    private ActivityNoConnectivityBinding binding;
    private NoConnectivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_no_connectivity, null, false);
        setContentView(binding.getRoot());

        if (BuildConfig.PRD) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setScreenInfo();
        initViews();
        presenter = new NoConnectivityPresenter(this);

        if (connectivityType == null) {
            connectivityType = "";
        }
        presenter.onViewLoaded(connectivityType);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BMBApplication.getInstance().getTransaktInteractor() != null && BMBApplication.getInstance().getTransaktInteractor().isConnected()) {
            finish();
        }
    }

    private void initViews() {
        binding.btnCallToAction.setOnClickListener(this);
    }

    private void setScreenInfo() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            message = bundle.getString(MESSAGE);
            instruction = bundle.getString(INSTRUCTION);
            connectivityType = bundle.getString(CONNECTIVITY_TYPE);
        }
        setMessage(message);
        setInstruction(instruction);
    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public void retry() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                //if reconnected, disconnect Transakt and reconnect to ensure smoothness going forward
                if (CONNECTIVITY_ENTERSEKT.equals(connectivityType)) {
                    connectToVerificationServer();
                } else {
                    finish();
                }
                String networkType = activeNetwork.getTypeName();
                Toast.makeText(NoConnectivityActivity.this, String.format(getString(R.string.sure_check_connectivity_restored_message), " ", networkType), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void connectToVerificationServer() {
        BMBApplication.getInstance().listenForAuth();
        TransaktDelegate transaktDelegate = new TransaktDelegate() {
            @Override
            protected void onConnected() {
                super.onConnected();
                dismissProgressDialog();
                close();
            }
        };

        TransaktHandler transaktHandler = BMBApplication.getInstance().getTransaktHandler();
        transaktHandler.setConnectCallbackTriggeredFlag(false);
        transaktHandler.setTransaktDelegate(transaktDelegate);
        transaktHandler.start();
        showProgressDialog();
    }

    @Override
    public void setActionText(@NonNull String text) {
        binding.btnCallToAction.setText(text);
    }

    @Override
    public void onClick(View view) {
        presenter.onCallToActionClicked(connectivityType);
    }

    @Override
    public void setMessage(@NonNull String message) {
        binding.tvInformation1.setText(message);
    }

    @Override
    public void setInstruction(@NonNull String instruction) {
        binding.tvInformation2.setText(instruction);
    }
}