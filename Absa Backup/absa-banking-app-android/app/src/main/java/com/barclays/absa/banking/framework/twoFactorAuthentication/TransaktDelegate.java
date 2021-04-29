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
package com.barclays.absa.banking.framework.twoFactorAuthentication;

import android.content.Intent;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.presentation.connectivity.NoConnectivityActivity;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.entersekt.sdk.Auth;
import com.entersekt.sdk.Button;
import com.entersekt.sdk.Error;
import com.entersekt.sdk.Notify;

import static com.entersekt.sdk.Error.CONNECTION_FAILED;

public class TransaktDelegate {
    private static final String GENERIC_ERROR = "Cannot connect to authentication server";
    private static final String TAG = TransaktDelegate.class.getSimpleName();
    protected static final String CONTINUE_ENROLLMENT = "_CONT_ENROLLMENT_";
    public static final int CREDENTIAL_TYPE_5_DIGIT_PASSCODE = 2;
    public static final int CREDENTIAL_TYPE_BIOMETRIC = 3;

    private boolean showErrorDialog = true;
    private BaseActivity activity;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    public TransaktDelegate() {
    }

    public TransaktDelegate(BaseActivity activity) {
        this.activity = activity;
    }

    protected void onSignupSuccess() {
        BMBLogger.e(TAG, "onSignupSuccess");
    }

    public void setShowErrorDialog(boolean showErrorDialog) {
        this.showErrorDialog = showErrorDialog;
    }

    protected void onSignupError(Error error) {
        BMBLogger.d(TAG, "-----++ onSignUpError ++-----");
        if (error != null) {
            BMBLogger.d(TAG, "Error is " + error.toString());
        }
    }

    protected void onRegisterSuccess() {
    }

    protected void onGenerateTrustTokenSuccess(String trustToken) {
        if (trustToken != null) {
            appCacheService.setTrustToken(trustToken);
        }
        BMBLogger.i(TAG, "onGenerateTrustTokenSuccess");
    }

    protected void onGenerateTrustTokenFailure(Error error) {
        BMBLogger.e(TAG, error.toString());
    }

    protected void onConnected() {
    }

    protected void onDisconnected() {
    }

    protected void onTDataReceived(TDataResponse tDataResponse) {
        BMBLogger.e(TAG, "onTDataReceived");
        BMBLogger.e(TAG, "...in " + getClass() + " class");
        BMBLogger.e(TAG, "+_+_+_+_+ TData received [ " + tDataResponse.getCommand() + " ]; proceeding... +_+_+_+_+");
    }

    protected void onNotifyReceived(Notify notify) {
    }

    protected void onAuthSucceeded(Auth auth) {
    }

    protected void onConnectionError(Error error) {
        //Error enum values: CONNECTION_FAILED,  FIELD_NOT_SET, OPERATION_TIMED_OUT,  SDK_NO_DATA, SDK_DISCONNECTED,  SERVICE_UNAVAILABLE, SERVICE_UNREGISTERED
        if (CONNECTION_FAILED.name().equals(error.name())) {
            onError(GENERIC_ERROR);
        } else {
            new MonitoringInteractor().logTechnicalEvent(TAG, "onConnectionError", error.name());
            showGenericError();
        }
    }

    public Button findButtonThatWasSelected(Auth auth) {
        Button selectedButton = null;
        for (Button button : auth.getButtons()) {
            if (button.isSelected()) {
                selectedButton = button;
            }
        }
        return selectedButton;
    }

    protected void onError(String errorMessage) {
        if (activity != null && !activity.isFinishing()) {
            activity.dismissProgressDialog();
            if (showErrorDialog) {
                showConnectionError();
            }
        }
    }

    private void showConnectionError() {
        if (activity != null && !activity.isFinishing()) {
            Intent intent = new Intent(activity, NoConnectivityActivity.class);
            intent.putExtra(NoConnectivityActivity.MESSAGE, activity.getString(R.string.connectivity_network_unavailable));
            intent.putExtra(NoConnectivityActivity.INSTRUCTION, activity.getString(R.string.connectivity_turn_on_connection));
            intent.putExtra(NoConnectivityActivity.CONNECTIVITY_TYPE, NoConnectivityActivity.CONNECTIVITY_ENTERSEKT);
            activity.startActivity(intent);
        }
    }

    private void showGenericError() {
        if (activity != null && !activity.isFinishing()) {
            Intent intent = new Intent(activity, GenericResultActivity.class);
            intent.putExtra(GenericResultActivity.IS_FAILURE, true);
            intent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.something_went_wrong);
            intent.putExtra(GenericResultActivity.SUB_MESSAGE, R.string.something_went_wrong_message);
            intent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.ok);
            GenericResultActivity.bottomOnClickListener = v -> activity.loadAccountsAndGoHome();
            activity.startActivity(intent);
        }
    }
}