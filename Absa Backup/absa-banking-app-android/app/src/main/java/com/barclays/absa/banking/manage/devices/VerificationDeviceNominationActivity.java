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
package com.barclays.absa.banking.manage.devices;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType;
import com.barclays.absa.banking.databinding.Activity2faMakeSurecheckDeviceBinding;
import com.barclays.absa.banking.framework.ConnectivityMonitorActivity;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.sureCheck.SureCheckDelegate;
import com.barclays.absa.banking.presentation.sureCheckV2.SureCheckConfirmation2faActivity;
import com.barclays.absa.crypto.SecureUtils;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.UserSettingsManager;

import styleguide.forms.SelectorList;
import styleguide.forms.StringItem;

import static com.barclays.absa.banking.linking.ui.IdentificationAndVerificationConstants.ID_AND_V_LINK_DEVICE;

public class VerificationDeviceNominationActivity extends ConnectivityMonitorActivity implements VerificationDeviceNominationView {

    public static final String MAKE_SURECHECK = "make_surecheck";
    private VerificationDeviceNominationPresenter presenter;
    private boolean isFingerPrintAuthenticationEnabled;
    private int selectedRadioButton = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BMBApplication.getInstance().isDeviceProfilingActive()) {
            BMBLogger.d(TAG, "Notifying transaction...");
            getDeviceProfilingInteractor().notifyTransaction();
        }
        Activity2faMakeSurecheckDeviceBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_2fa_make_surecheck_device, null, false);
        setContentView(binding.getRoot());

        SelectorList<StringItem> yesNoList = new SelectorList<>();
        yesNoList.add(new StringItem(getString(R.string.yes)));
        yesNoList.add(new StringItem(getString(R.string.no)));

        binding.changePrimaryDeviceYesNoRadioButtonView.setDataSource(yesNoList);
        binding.changePrimaryDeviceYesNoRadioButtonView.setSelectedIndex(1);

        if (getIntent().getSerializableExtra("isFingerPrintAuthenticationEnabled") != null) {
            isFingerPrintAuthenticationEnabled = getIntent().getBooleanExtra("isFingerPrintAuthenticationEnabled", false);
        } else {
            isFingerPrintAuthenticationEnabled = false;
        }
        presenter = new VerificationDeviceNominationPresenter(this);

        binding.changePrimaryDeviceYesNoRadioButtonView.setItemCheckedInterface(index -> selectedRadioButton = index);

        binding.continueButton.setOnClickListener(view -> {
            if (selectedRadioButton == 0) {
                getAppCacheService().setSureCheckDelegate(sureCheckDelegate);
                getAppCacheService().setReturnToScreen(SureCheckConfirmation2faActivity.class);
                presenter.executeMakeSurecheck(SecureUtils.INSTANCE.getDeviceID());
            } else {
                Intent surecheck2SuccessIntent = new Intent(VerificationDeviceNominationActivity.this, SureCheckConfirmation2faActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(MAKE_SURECHECK, "no");
                AnalyticsUtil.trackActionFromStaticContext(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_MakeThisYourVerficationDeviceScreen_NoButtonClicked");
                UserSettingsManager.INSTANCE.setFingerprintActive(isFingerPrintAuthenticationEnabled);
                startActivity(surecheck2SuccessIntent);
            }
        });
    }

    @Override
    public void showSurecheckConfirmation() {
        AnalyticsUtil.trackActionFromStaticContext(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_MakeThisYourVerficationDeviceScreen_YesButtonClicked");
        Intent intent = new Intent(this, SureCheckConfirmation2faActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(MAKE_SURECHECK, "yes");
        UserSettingsManager.INSTANCE.setFingerprintActive(isFingerPrintAuthenticationEnabled);
        startActivity(intent);
    }

    SureCheckDelegate sureCheckDelegate = new SureCheckDelegate(this) {
        @Override
        public void onSureCheckProcessed() {
            new Handler(Looper.getMainLooper()).postDelayed(() -> presenter.executeMakeSurecheck(SecureUtils.INSTANCE.getDeviceID()), 250);
        }

        @Override
        public void onSureCheckRejected() {
            GenericResultActivity.bottomOnClickListener = v -> {
                Intent surecheck2SuccessIntent = new Intent(VerificationDeviceNominationActivity.this, SureCheckConfirmation2faActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(MAKE_SURECHECK, "no");
                UserSettingsManager.INSTANCE.setFingerprintActive(isFingerPrintAuthenticationEnabled);
                startActivity(surecheck2SuccessIntent);
            };

            Intent intent = new Intent(VerificationDeviceNominationActivity.this, GenericResultActivity.class)
                    .putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.continue_button)
                    .putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_cross)
                    .putExtra(GenericResultActivity.IMAGE, R.drawable.ic_cross)
                    .putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.transaction_rejected);
            startActivity(intent);
        }
    };

    public void goToSureCheckCountDownScreen(TransactionVerificationType verificationType) {
        if (verificationType == TransactionVerificationType.SURECHECKV2Required) {
            sureCheckDelegate.initiateV2CountDownScreen();
        }
    }
}
