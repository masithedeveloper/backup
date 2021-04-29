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
package com.barclays.absa.banking.presentation.security;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.databinding.SecurityActivityBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.BuildConfigHelper;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktDelegate;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktHandler;
import com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.SimplifiedLoginActivity;
import com.barclays.absa.banking.presentation.generateTokens.OfflineOtpGenerator;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.crypto.SymmetricCryptoHelper;
import com.barclays.absa.utils.ProfileManager;
import com.barclays.absa.utils.SharedPreferenceService;
import com.entersekt.sdk.Error;

public class SecurityActivity extends BaseActivity implements View.OnClickListener {

    private SecurityActivityBinding binding;
    private TransaktHandler transaktHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.security_activity, null, false);
        setContentView(binding.getRoot());
        setToolBarBack(R.string.security);
        setAppVersion();
        setSurephrase();
        setGenerateTokenButton();
        transaktHandler = BMBApplication.getInstance().getTransaktHandler();
        setCertificateNumber(transaktHandler.getEmCertID());
        if (BuildConfig.STUB) {
            setCertificateNumber("209218357723");
        }

        if (!BuildConfig.PRD) {
            binding.imiProfileIdContentAndLabelView.setVisibility(View.VISIBLE);
            binding.imiProfileIdContentAndLabelView.makeContentTextSelectable();
            SecureHomePageObject secureHomePageObject = getAppCacheService().getSecureHomePageObject();
            binding.imiProfileIdContentAndLabelView.setContentText(secureHomePageObject != null ? secureHomePageObject.getCustomerProfile().getMailboxProfileId() : "");
            binding.csidContentAndLabelView.setVisibility(View.VISIBLE);
            binding.csidContentAndLabelView.setContentText(getAppCacheService().getCustomerSessionId());
        }
    }

    private void setGenerateTokenButton() {
        binding.btnGenerateToken.setVisibility(BuildConfigHelper.STUB || getAppCacheService().isPrimarySecondFactorDevice() ? View.VISIBLE : View.GONE);
        binding.btnGenerateToken.setOnClickListener(this);
    }

    private void setAppVersion() {
        String versionText = getString(R.string.version) + " " + BuildConfig.VERSION_NAME;
        binding.appVersionView.setContentText(versionText);
    }

    private void setCertificateNumber(String emCertId) {
        binding.certificateNumberView.setContentText(emCertId);
    }

    private void setSecurityConnectionStatus(boolean connected) {
        setTextViewConnectionState(binding.securityConnectionView.getContentTextView(), connected);
    }

    private void setSurephrase() {
        CustomerProfileObject customerProfileObject = CustomerProfileObject.getInstance();
        if (customerProfileObject != null) {
            binding.surephraseView.setContentText(customerProfileObject.getSurePhrase());
        }
    }

    private void setTextViewConnectionState(TextView textView, boolean connected) {
        textView.setTextColor(connected ?
                ContextCompat.getColor(this, R.color.color_green_connected) :
                ContextCompat.getColor(this, R.color.color_red_not_connected));
        textView.setText(connected ? getString(R.string.connected) : getString(R.string.not_connected));
    }

    @Override
    protected void onResume() {
        super.onResume();
        TransaktDelegate delegate = new TransaktDelegate() {

            @Override
            protected void onConnected() {
                super.onConnected();
                setSecurityConnectionStatus(true);
                setCertificateNumber(transaktHandler.getEmCertID());
                if (BuildConfigHelper.STUB)
                    setCertificateNumber("209218357723");
            }

            @Override
            protected void onError(String errorMessage) {
                setSecurityConnectionStatus(false);
                dismissProgressDialog();
            }

            @Override
            protected void onConnectionError(Error error) {
                super.onConnectionError(error);
                setSecurityConnectionStatus(false);
            }
        };

        transaktHandler.setConnectCallbackTriggeredFlag(false);
        transaktHandler.setTransaktDelegate(delegate);
        transaktHandler.start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_generateToken) {
            SecureHomePageObject secureHomePageObject = getAppCacheService().getSecureHomePageObject();
            if (secureHomePageObject == null) {
                Intent intent = new Intent(this, SimplifiedLoginActivity.class);
                intent.putExtra(BMBConstants.IS_GENERATE_TOKEN, true);
                startActivity(intent);
            } else {
                UserProfile profile = ProfileManager.getInstance().getActiveUserProfile();
                if (profile != null && profile.getUserId() != null) {
                    SymmetricCryptoHelper symmetricCryptoHelper = SymmetricCryptoHelper.getInstance();
                    long longCredentials = SharedPreferenceService.INSTANCE.getOfflineOtpLongNumber(profile.getUserId());
                    byte[] otpSeed = symmetricCryptoHelper.retrieveOtpSeed();
                    try {
                        String otp = OfflineOtpGenerator.generateOTP(longCredentials, otpSeed);
                        BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                                .title(getString(R.string.surecheck_token))
                                .message(getString(R.string.use_this_surecheck_token) + "\n\n" + otp)
                                .build());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}