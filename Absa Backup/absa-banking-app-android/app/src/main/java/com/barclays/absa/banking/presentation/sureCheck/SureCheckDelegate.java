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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.FragmentTransaction;

import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.boundary.monitoring.MonitoringService;
import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.BaseView;
import com.barclays.absa.banking.framework.BuildConfigHelper;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.presentation.sureCheckV2.OfflineOtpActivity;
import com.barclays.absa.banking.presentation.sureCheckV2.SureCheck2CountDownDialogFragment;
import com.barclays.absa.banking.presentation.sureCheckV2.SureCheckHandler;
import com.barclays.absa.banking.presentation.sureCheckV2.SureCheckReTriggerHandler;
import com.barclays.absa.banking.presentation.verification.SureCheckAuth2faActivity;
import com.barclays.absa.banking.shared.ActionHandler;

import java.util.HashMap;

public abstract class SureCheckDelegate {

    protected final Context context;
    private boolean isFirstRequest = true;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    public SureCheckDelegate(Context context) {
        this.context = context;
        appCacheService.setSureCheckDelegate(this);
    }

    public abstract void onSureCheckProcessed();

    public void onSureCheckCancelled() {

    }

    public void onSureCheckCancelled(BaseActivity activity) {
        if (activity != null && !activity.isFinishing()) {
            activity.finish();
            activity.dismissProgressDialog();
        }
    }

    public void onSureCheckFailed() {
    }

    public void onSureCheckFailed(String errorMessage) {
    }

    public void onSureCheckFailed(BaseActivity activity) {
        onSureCheckCancelled(activity);
    }

    public void onSureCheckRejected() {
        navigateToTransactionRejectedActivity(SureCheckAuth2faActivity.isFraud);
    }

    private void navigateToTransactionRejectedActivity(boolean isFraud) {
        Activity topMostActivity = BMBApplication.getInstance().getTopMostActivity();
        Intent intent;
        if (appCacheService.getUserLoggedInStatus()) {
            intent = IntentFactory.getRejectedResultScreen(topMostActivity, isFraud);
            topMostActivity.startActivity(intent);
        } else {
            intent = IntentFactory.getRejectedPreLoginResultScreen(topMostActivity, isFraud);
            topMostActivity.startActivity(intent);
            topMostActivity.finish();
        }
    }

    public void onResendSuccess(TransactionVerificationType transactionVerificationType) {
        if (transactionVerificationType != null)
            switch (transactionVerificationType) {
                case SURECHECKV1:
                case SURECHECKV1Required:
                    initiateV1CountDownScreen();
                    break;
                case SURECHECKV2:
                case SURECHECKV2Required:
                    initiateV2CountDownScreen();
                    break;
                case SURECHECKV1_FALLBACK:
                case SURECHECKV1_FALLBACKRequired:
                    initiateTransactionVerificationEntryScreen();
                    break;
                case SURECHECKV2_FALLBACK:
                case SURECHECKV2_FALLBACKRequired:
                    initiateOfflineOtpScreen();
                    break;
                default:
                    onSureCheckFailed();
                    break;
            }
        else onSureCheckFailed();
    }

    private void initiateV1CountDownScreen(Context context) {
        if (appCacheService.isBioAuthenticated() || appCacheService.isIdentificationAndVerificationFlow() || appCacheService.isIdentityAndVerificationPostLogin()) {
            if (!SureCheckHandler.INSTANCE.isActive()) {
                BaseActivity topMostActivity = (BaseActivity) BMBApplication.getInstance().getTopMostActivity();
                if (!topMostActivity.isFinishing()) {
                    FragmentTransaction fragmentTransaction = topMostActivity.getSupportFragmentManager().beginTransaction();
                    LinkingSureCheckCountdownDialogFragment sureCheck2CountDownDialogFragment = LinkingSureCheckCountdownDialogFragment.newInstance();
                    fragmentTransaction.add(sureCheck2CountDownDialogFragment, LinkingSureCheckCountdownDialogFragment.class.getSimpleName());
                    fragmentTransaction.commitAllowingStateLoss();
                }
            }
        } else {
            Intent sureCheckIntent = new Intent(context, SureCheckCountdownActivity.class);
            context.startActivity(sureCheckIntent);
        }
    }

    public void initiateV1CountDownScreen() {
        initiateV1CountDownScreen(context);
    }

    private void initiateTransactionVerificationEntryScreen(Context context) {
        Intent tvnRvnIntent;
        if (appCacheService.isIdentificationAndVerificationFlow()) {
            tvnRvnIntent = new Intent(context, LinkingEnterVerificationNumberActivity.class);
        } else {
            tvnRvnIntent = new Intent(context, EnterVerificationNumber2faActivity.class);
        }

        context.startActivity(tvnRvnIntent);
    }

    public void initiateTransactionVerificationEntryScreen() {
        initiateTransactionVerificationEntryScreen(context);
    }

    public void initiateV2CountDownScreen() {
        if (!SureCheckHandler.INSTANCE.isActive()) {
            BaseActivity topMostActivity = (BaseActivity) BMBApplication.getInstance().getTopMostActivity();
            if (!topMostActivity.isFinishing()) {
                FragmentTransaction fragmentTransaction = topMostActivity.getSupportFragmentManager().beginTransaction();
                SureCheck2CountDownDialogFragment sureCheck2CountDownDialogFragment = SureCheck2CountDownDialogFragment.newInstance();
                fragmentTransaction.add(sureCheck2CountDownDialogFragment, SureCheck2CountDownDialogFragment.class.getSimpleName());
                fragmentTransaction.commitAllowingStateLoss();
            }
        }
    }

    public void initiateV2CountDownScreen(ActionHandler actionHandler) {
        if (!appCacheService.isBioAuthenticated() && !appCacheService.isForgotPrimaryDeviceButtonClicked()) {
            SureCheckReTriggerHandler.INSTANCE.setReTriggerAction(actionHandler);
        }
        if (!SureCheckHandler.INSTANCE.isActive()) {
            BaseActivity topMostActivity = (BaseActivity) BMBApplication.getInstance().getTopMostActivity();
            if (!topMostActivity.isFinishing()) {
                FragmentTransaction fragmentTransaction = topMostActivity.getSupportFragmentManager().beginTransaction();
                SureCheck2CountDownDialogFragment sureCheck2CountDownDialogFragment = SureCheck2CountDownDialogFragment.newInstance();
                fragmentTransaction.add(sureCheck2CountDownDialogFragment, SureCheck2CountDownDialogFragment.class.getSimpleName());
                fragmentTransaction.commitAllowingStateLoss();
            }
        }
    }

    private void initiateOfflineOtpScreen(Activity activity) {
        if (activity != null) {
            Intent intent = new Intent(activity, OfflineOtpActivity.class);
            activity.startActivity(intent);
        }
    }

    public void initiateOfflineOtpScreen() {
        initiateOfflineOtpScreen((Activity) context);
    }

    public void processSureCheck(final BaseView baseView, SureCheckResponse sureCheckResponse, final SuccessCallback successCallback) {
        BaseActivity activity = (BaseActivity) baseView;
        if (sureCheckResponse != null && sureCheckResponse.getSureCheckFlag() != null && !sureCheckResponse.getSureCheckFlag().isEmpty()) {
            TransactionVerificationType verificationType = TransactionVerificationType.valueOf(sureCheckResponse.getSureCheckFlag());

            cacheSureCheckData(sureCheckResponse);
            switch (verificationType) {
                case NoPrimaryDevice:
                    if (!appCacheService.isBioAuthenticated() && !appCacheService.isChangePrimaryDeviceFromNoPrimaryDeviceScreen()) {
                        SureCheckReTriggerHandler.INSTANCE.setReTriggerAction(this::onSureCheckProcessed);
                    }
                    baseView.showNoPrimaryDeviceScreen();
                    break;
                case NotNeeded:
                    onSureCheckProcessed();
                    break;
                case TVMRequired:
                case SURECHECKV1:
                case SURECHECKV1Required:
                    initiateV1CountDownScreen(activity);
                    break;
                case SURECHECKV1_FALLBACK:
                case SURECHECKV1_FALLBACKRequired:
                    initiateTransactionVerificationEntryScreen(activity);
                    break;
                case SURECHECKV2:
                case SURECHECKV2Required:
                    if (appCacheService.isPrimarySecondFactorDevice()) {
                        recordMonitoringEventAuthTriggered();
                    }
                    initiateV2CountDownScreen(this::onSureCheckProcessed);
                    break;
                case SURECHECKV2_FALLBACK:
                case SURECHECKV2_FALLBACKRequired:
                    initiateOfflineOtpScreen(activity);
                    break;
                case SecurityCode:
                    SecurityCodeDelegate.handleSecurityCodeCaseChangePrimaryAndRetriggerAction(activity, this::onSureCheckProcessed);
                    break;
                case Failed:
                    onSureCheckFailed(activity);
                    break;
                default:
                    break;
            }
        } else {
            if (BuildConfigHelper.STUB && isFirstRequest) {
                isFirstRequest = false;
                initiateV2CountDownScreen();
            } else {
                successCallback.proceed();
            }
        }
    }

    private void recordMonitoringEventAuthTriggered() {
        HashMap<String, Object> eventData = new HashMap<>();
        eventData.put(MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_TIMESTAMP, System.currentTimeMillis());
        new MonitoringInteractor().logMonitoringEvent(MonitoringService.MONITORING_EVENT_NAME_AUTH_RECEIVED_ON_INITIATING_DEVICE_START_TIME, eventData);
    }

    private void cacheSureCheckData(SureCheckResponse sureCheckResponse) {
        appCacheService.setSureCheckReferenceNumber(sureCheckResponse.getReferenceNumber());
        appCacheService.setSureCheckCellphoneNumber(sureCheckResponse.getCellnumber());
        appCacheService.setSureCheckEmail(sureCheckResponse.getEmail());
        appCacheService.setSureCheckNotificationMethod(sureCheckResponse.getNotificationMethod());
    }

    public interface SuccessCallback {
        void proceed();
    }
}