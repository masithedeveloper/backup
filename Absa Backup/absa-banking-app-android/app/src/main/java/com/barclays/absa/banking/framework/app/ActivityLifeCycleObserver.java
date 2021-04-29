/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */

package com.barclays.absa.banking.framework.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.newToBank.NewToBankActivity;
import com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.SimplifiedLoginActivity;

import org.jetbrains.annotations.NotNull;

public final class ActivityLifeCycleObserver implements Application.ActivityLifecycleCallbacks {

    private int activitiesStartedCount = 0;
    public static boolean shouldDismissDialog = false;
    private final BMBApplication appInstance;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    ActivityLifeCycleObserver(BMBApplication bmbApplication) {
        appInstance = bmbApplication;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        BMBLogger.d("ActivityLifeCycleObserver", "Top most activity is " + activity.getClass().getName());
        if (!(activity instanceof NewToBankActivity)) {
            appInstance.updateLanguage(activity);
        }
        appInstance.setTopMostActivity(activity);
    }
    
    @Override
    public void onActivityStarted(@NotNull Activity activity) {
        appInstance.setForegrounded();
        if (activitiesStartedCount == 0) {
            BMBLogger.d("ActivityLifeCycleObserver", "App is in the foreground");
        }
    }

    @Override
    public void onActivityResumed(@NotNull Activity activity) {
        BMBLogger.d("x-class", "Activity resumed in life cycle observer");
        appInstance.setTopMostActivity(activity);
        activitiesStartedCount++;
        if (shouldDismissDialog && activity instanceof BaseActivity) {
            shouldDismissDialog = false;
            ((BaseActivity) activity).dismissProgressDialog();
        }

        if (appInstance.isNotListeningForAuth() && (appCacheService.isPrimarySecondFactorDevice() || appInstance.getTopMostActivity() instanceof SimplifiedLoginActivity)) {
            appInstance.listenForAuth();
        }
    }

    @Override
    public void onActivityPaused(@NotNull Activity activity) {
        BMBLogger.d("x-class", "Activity paused in life cycle observer");
        activitiesStartedCount--;
    }

    @Override
    public void onActivityStopped(@NotNull Activity activity) {
        if (activitiesStartedCount == 0) {
            appInstance.setBackgrounded();
            BMBLogger.d("ActivityLifeCycleObserver", "App is in the background");
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NotNull Activity activity, @NotNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        BMBLogger.d("x-class", activity.getLocalClassName() + " destroyed in life cycle observer");
    }
}