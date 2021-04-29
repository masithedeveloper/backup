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
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.databinding.Activity2faSureCheckConfirmationBinding;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.home.ui.HomeContainerActivity;
import com.barclays.absa.banking.home.ui.StandaloneHomeActivity;
import com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.SimplifiedLoginActivity;
import com.barclays.absa.banking.presentation.whatsNew.WhatsNewHelper;
import com.barclays.absa.banking.presentation.whatsNew.WhatsNewLottieActivity;
import com.barclays.absa.utils.AnalyticsUtil;
import com.barclays.absa.utils.BannerManager;
import com.barclays.absa.utils.SharedPreferenceService;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.barclays.absa.banking.linking.ui.IdentificationAndVerificationConstants.ID_AND_V_LINK_DEVICE;


public class SureCheckConfirmation2faActivity extends BaseActivity {

    private final long INTERVAL_TIME = TimeUnit.SECONDS.toMillis(1);
    private final long TIMEOUT_TIME = TimeUnit.SECONDS.toSeconds(15);
    private CountDownTimerTask countDownTimerTask;
    private Activity2faSureCheckConfirmationBinding binding;
    private final View.OnClickListener exploreClickListener = v -> {
        AnalyticsUtil.INSTANCE.trackAction(ID_AND_V_LINK_DEVICE, "ID&VLinkDevice_YoureAllSetScreen_ExploreAppButtonClicked");
        BannerManager.INSTANCE.incrementBannerViews();
        CustomerProfileObject customerProfile = getAppCacheService().getSecureHomePageObject().getCustomerProfile();
        boolean isTransactionalUser = false;
        if (customerProfile != null) {
            isTransactionalUser = customerProfile.isTransactionalUser();
        }
        getAppCacheService().setIsTransactionalUser(isTransactionalUser);

        if (SharedPreferenceService.INSTANCE.getFirstLoginStatus() && isTransactionalUser && !WhatsNewHelper.INSTANCE.getEnabledWhatsScreens().isEmpty()) {
            updateLanguage();
            Intent whatsNewIntent = new Intent(this, WhatsNewLottieActivity.class);
            whatsNewIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(whatsNewIntent);
        } else {
            goToHomeScreen(isTransactionalUser);
        }
        finish();
    };
    private final View.OnClickListener loginClickListener = v -> {
        Intent passcodeLoginIntent = new Intent(this, SimplifiedLoginActivity.class);
        startActivity(passcodeLoginIntent);
        finish();
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_2fa_sure_check_confirmation, null, false);
        setContentView(binding.getRoot());
        binding.getRoot().setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mScreenName = BMBConstants.CREATE_PASSCODE_SUCCESS_CONST;
        mSiteSection = BMBConstants.SIMPLIFIED_LOGIN_CONST;
        AnalyticsUtils.getInstance().trackCustomScreenView(BMBConstants.CREATE_PASSCODE_SUCCESS_CONST,
                BMBConstants.SIMPLIFIED_LOGIN_CONST, BMBConstants.TRUE_CONST);

        binding.exploreButton.setOnClickListener(exploreClickListener);
        startTimer();
    }

    private void updateLanguage() {
        BMBApplication.getInstance().setUserLoggedInStatus(true);
        SecureHomePageObject secureHomePageObject = getAppCacheService().getSecureHomePageObject();
        if (secureHomePageObject != null) {
            BMBApplication.getInstance().updateLanguage(this, String.valueOf(AFRIKAANS_LANGUAGE).equalsIgnoreCase(secureHomePageObject.getLangCode()) ? BMBConstants.AFRIKAANS_CODE : BMBConstants.ENGLISH_CODE);
        }
    }

    private void goToHomeScreen(boolean isTransactionUser) {
        updateLanguage();
        Intent homePageIntent = new Intent(this, isTransactionUser ? HomeContainerActivity.class : StandaloneHomeActivity.class);
        homePageIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homePageIntent);
        overridePendingTransition(R.anim.fade_activity_in, R.anim.fade_activity_out);
        finish();
    }

    @Override
    protected void onStop() {
        cancelTimer();
        super.onStop();
    }

    private void startTimer() {
        Timer countDownTimer = new Timer();
        countDownTimerTask = new CountDownTimerTask(TIMEOUT_TIME);
        countDownTimer.scheduleAtFixedRate(countDownTimerTask, 0, INTERVAL_TIME);
    }

    private void cancelTimer() {
        if (countDownTimerTask != null) {
            countDownTimerTask.cancel();
        }
    }

    private class CountDownTimerTask extends TimerTask {
        long countDownSeconds;

        CountDownTimerTask(long countDownSeconds) {
            this.countDownSeconds = countDownSeconds;
        }

        @Override
        public void run() {
            runOnUiThread(() -> {
                if (countDownSeconds != 0) {
                    countDownSeconds--;
                } else {
                    binding.exploreButton.setText(R.string.login_text);
                    binding.exploreButton.setOnClickListener(loginClickListener);
                    binding.timeoutLoginButton.setOnClickListener(loginClickListener);
                    cancel();
                }
            });
        }
    }
}