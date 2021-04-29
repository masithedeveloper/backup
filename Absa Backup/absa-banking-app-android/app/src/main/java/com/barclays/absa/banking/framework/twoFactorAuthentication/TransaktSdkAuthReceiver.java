/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.framework.twoFactorAuthentication;

import android.app.Activity;
import android.content.Intent;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactoryGenericResult;
import com.barclays.absa.banking.presentation.sureCheckV2.SureCheckHandler;
import com.barclays.absa.crypto.SecureUtils;
import com.barclays.absa.utils.ProfileManager;
import com.entersekt.sdk.Auth;
import com.entersekt.sdk.Button;
import com.entersekt.sdk.NameValue;

import java.util.List;

import static com.barclays.absa.banking.framework.app.BMBConstants.AFRIKAANS_CODE;
import static com.barclays.absa.banking.framework.app.BMBConstants.ENGLISH_CODE;
import static com.barclays.absa.banking.presentation.genericResult.GenericResultActivity.BOTTOM_BUTTON_MESSAGE;
import static com.barclays.absa.banking.presentation.genericResult.GenericResultActivity.IS_FAILURE;
import static com.barclays.absa.banking.presentation.genericResult.GenericResultActivity.NOTICE_MESSAGE;

public class TransaktSdkAuthReceiver {

    private static final String TAG = "TransaktSdkAuthReceiver";
    private final String AFRIKAANS_BUTTON_INDICATOR = "Aanvaar";
    private final String authPositiveRole = "positive";

    private TransaktDelegate transaktAuthDelegate;
    private VerificationRequestProcessor verificationRequestProcessor;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    public TransaktSdkAuthReceiver() {
        transaktAuthDelegate = new TransaktDelegate() {

            @Override
            protected void onConnected() {
                super.onConnected();
                BMBLogger.d(TAG, "Transakt auth onConnected");
                BMBApplication.getInstance().dismissProgressDialog();
            }

            @Override
            protected void onDisconnected() {
                super.onDisconnected();
                if (BMBApplication.getInstance().isInForeground() && appCacheService.isPrimarySecondFactorDevice()) {
                    BMBApplication.getInstance().listenForAuth();
                }
            }
        };
        verificationRequestProcessor = new VerificationRequestProcessor();
    }

    public boolean isListeningForAuthMessage() {
        return BMBApplication.getInstance().getTransaktHandler().isConnected();
    }

    public void listen() {
        TransaktHandler transaktHandler = BMBApplication.getInstance().getTransaktHandler();
        transaktHandler.setTransaktDelegate(transaktAuthDelegate);
        transaktHandler.start();
    }

    public void processAuth(Auth auth) {
        setLocaleFromAuth(auth);
        if (isAuthFromPrimary(auth)) {
            SureCheckHandler.INSTANCE.start();
        }
        verificationRequestProcessor.processAuth(auth);
    }

    private boolean isAuthFromPrimary(Auth auth) {
        for (NameValue nameValue : auth.getNameValues()) {
            if (nameValue.getName().equals("Device ID") && nameValue.getValue().equals(SecureUtils.INSTANCE.getDeviceID())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAuthEventPending() {
        return verificationRequestProcessor.hasPendingAuth();
    }

    public void clearPendingAuth() {
        verificationRequestProcessor.clearPendingAuth();
    }

    public void stop() {
        BMBApplication.getInstance().getTransaktHandler().disconnect();
    }

    public void onError() {
        final BMBApplication app = BMBApplication.getInstance();
        app.dismissProgressDialog();

        final Activity topMostActivity = BMBApplication.getInstance().getTopMostActivity();
        GenericResultActivity.bottomOnClickListener = v -> topMostActivity.finish();

        if (topMostActivity != null) {
            Intent failureIntent = IntentFactoryGenericResult.getFailureResultBuilder(topMostActivity)
                    .setGenericResultSubMessage(R.string.surecheck_transaction_failed)
                    .setGenericResultDoneButton(topMostActivity)
                    .build();
            topMostActivity.startActivity(failureIntent);
        } else {
            Intent intent = new Intent(app, GenericResultActivity.class);
            intent.putExtra(IS_FAILURE, true);
            intent.putExtra(NOTICE_MESSAGE, R.string.surecheck_transaction_failed);
            intent.putExtra(BOTTOM_BUTTON_MESSAGE, R.string.done);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            app.startActivity(intent);
        }

    }

    public void showAuthScreen(Activity activity) {
        setLocaleFromAuth(BMBApplication.getInstance().getVerificationRequest());
        verificationRequestProcessor.showAuthScreen(activity);
    }

    private void setLocaleFromAuth(Auth auth) {
        String authLanguage = ENGLISH_CODE;
        List<Button> buttons = auth.getButtons();
        for (Button button : buttons) {
            if (button.getRole().equalsIgnoreCase(authPositiveRole)) {
                authLanguage = button.getLabel().equalsIgnoreCase(AFRIKAANS_BUTTON_INDICATOR) ? AFRIKAANS_CODE : ENGLISH_CODE;
            }
        }
        final UserProfile activeUserProfile = ProfileManager.getInstance().getActiveUserProfile();

        if (activeUserProfile != null && !activeUserProfile.getLanguageCode().equalsIgnoreCase(authLanguage)) {
            BMBApplication.getInstance().updateLanguage(BMBApplication.getInstance().getTopMostActivity(), authLanguage);
        }
    }

    interface LogoutDialogListener {
        void onYes();

        void onNo();
    }
}