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
package com.barclays.absa.banking.presentation.launch;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Process;
import android.provider.Settings;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.adobe.mobile.Config;
import com.airbnb.lottie.LottieAnimationView;
import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.PermissionFacade;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.services.AppCacheService;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingStates;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.manage.profile.ManageProfileFileUtils;
import com.barclays.absa.banking.passcode.passcodeLogin.ExpressAuthenticationHelper;
import com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.SimplifiedLoginActivity;
import com.barclays.absa.banking.presentation.generateTokens.NoConnectionGenerateTokenActivity;
import com.barclays.absa.banking.presentation.genericResult.GenericResultActivity;
import com.barclays.absa.banking.presentation.sessionTimeout.SessionTimeOutDialogActivity;
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity;
import com.barclays.absa.banking.shared.AlertDialogProperties;
import com.barclays.absa.banking.shared.BaseAlertDialog;
import com.barclays.absa.crypto.SecureUtils;
import com.barclays.absa.utils.AppShortcutsHandler;
import com.barclays.absa.utils.CompatibilityUtils;
import com.barclays.absa.utils.IAppShortcutsHandler;
import com.barclays.absa.utils.NetworkUtils;
import com.barclays.absa.utils.PermissionHelper;
import com.barclays.absa.utils.ProfileManager;
import com.barclays.absa.utils.SharedPreferenceService;
import com.barclays.absa.utils.UserSettingsManager;
import com.google.firebase.iid.FirebaseInstanceId;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import kotlin.Unit;
import za.co.absa.networking.ExpressNetworkingConfig;

import static com.barclays.absa.banking.framework.push.PushMessageListener.IN_APP_NOTIFICATION_IDENTIFIER;
import static com.barclays.absa.banking.framework.push.PushMessageListener.IN_APP_SHORTCUT;

public class SplashActivity extends BaseActivity implements SplashScreenView {

    private static final String TAG = "LauncherActivity";
    private static final String BLOCK_LOGIN_PERMISSION = "com.absa.barclays.banking.permission.BLOCK_LOGIN";
    private BroadcastReceiver rootDetectionReceiver;
    private SplashPresenterInterface presenter;
    public static final String SHORTCUT = "shortcut";
    private String currentShortcutId;
    private int returnValue = R.drawable.jhb_background_0;
    private ExpressAuthenticationHelper expressAuthenticationHelper;
    private boolean permissionDialogAccept = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        getAppCacheService().setCacheAvailable(true);
        SessionTimeOutDialogActivity.shouldShow = false;

        ExpressNetworkingConfig.applicationLocale = BMBApplication.getApplicationLocale();
        ExpressNetworkingConfig.appContext = BMBApplication.getInstance().getApplicationContext();
        ExpressNetworkingConfig.INSTANCE.setTokenExpireTime(null);

        Config.setContext(this.getApplicationContext());
        BMBApplication.getInstance().updateLanguage(this, BMBConstants.ENGLISH_CODE);
        SharedPreferenceService.INSTANCE.setIsPartialRegistration(false);
        setBackgroundImage();

        String versionName = BuildConfig.IS_HUAWEI_BUILD ? BuildConfig.VERSION_NAME + "H" : BuildConfig.VERSION_NAME;
        String versionCode = BuildConfig.PRD ? "" : String.format(Locale.ENGLISH, " (%d)", BuildConfig.VERSION_CODE);
        String appVersionInfo = getString(R.string.app_version, versionName, versionCode, "");

        TextView appVersionCheckTextView = findViewById(R.id.appVersionTextView);
        appVersionCheckTextView.setText(appVersionInfo);

        if (getIntent().getBooleanExtra(GenericResultActivity.SHOULD_QUIT, false)) {
            moveTaskToBack(false);
            if (CompatibilityUtils.isVersionGreaterThanOrEqualTo(Build.VERSION_CODES.LOLLIPOP)) {
                finishAndRemoveTask();
            } else {
                finishAffinity();
            }
            Process.killProcess(Process.myPid());
            return;
        }

        presenter = new SplashScreenPresenter(this, ProfileManager.getInstance());

        AsyncTask.execute(() -> {
            ManageProfileFileUtils.INSTANCE.removeInternalFilesOlderThanSevenDays(SplashActivity.this);
            ManageProfileFileUtils.INSTANCE.removeAllTempFilesForManageProfile(SplashActivity.this);
        });

        mScreenName = BMBConstants.FIRST_LOGIN_CONST;
        mSiteSection = BMBConstants.SIMPLIFIED_LOGIN_CONST;
        AnalyticsUtils.getInstance().trackCustomScreenView(mScreenName, mSiteSection, BMBConstants.TRUE_CONST);

        BMBApplication.getInstance().setUserLoggedInStatus(false);

        BMBApplication.getInstance().LOGGED_IN_START_TIME = 0;
        if (UserSettingsManager.INSTANCE.isNotificationServicesEnabled()) {
            try {
                if (FirebaseInstanceId.getInstance() != null) {
                    BMBLogger.d(TAG, "FIR token is " + FirebaseInstanceId.getInstance().getToken());
                }
            } catch (IllegalStateException e) {
                BMBLogger.e(TAG, e.getMessage());
            }
        }
        findViewById(R.id.horizontalScrollView).setOnTouchListener((v, event) -> {
            v.performClick();
            return true;
        });

        if (getIntent().hasExtra(SHORTCUT)) {
            currentShortcutId = getIntent().getStringExtra(SHORTCUT);
        }

        if (Build.VERSION.SDK_INT >= 25) {
            ((IAppShortcutsHandler) AppShortcutsHandler.INSTANCE).createOutsideAppShortcuts(this);
        }
    }

    private void setBackgroundImage() {
        ImageView imageView = findViewById(R.id.backgroundImageView);
        try {
            imageView.setImageResource(getSplashWindowBackgroundResourceId());
        } catch (Exception e) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("error", e.getMessage());
            new MonitoringInteractor().logMonitoringEvent("BackgroundImageLoadError", map);
        }

        int backgroundImageScrollDurationMilliseconds = 2000;
        imageView.animate().alpha(1.0f).setDuration(backgroundImageScrollDurationMilliseconds).start();
        ObjectAnimator animator = ObjectAnimator.ofInt(imageView, "scrollX", 170);
        animator.setInterpolator(new DecelerateInterpolator(1.2f));
        animator.setDuration(backgroundImageScrollDurationMilliseconds);
        animator.start();
    }

    private void setLottieAnimationListener() {
        LottieAnimationView view = findViewById(R.id.backgroundLottieView);
        if (view != null) {
            view.playAnimation();
            view.removeAllAnimatorListeners();
            view.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    presenter.populateUserProfiles();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        float durationScale = Settings.Global.getFloat(getContentResolver(), Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f);
        BMBLogger.d(TAG, "Duration scale: " + durationScale);
        if (durationScale == 0) {
            try {
                ValueAnimator.class.getMethod("setDurationScale", float.class).invoke(null, .75f);
                durationScale = Settings.Global.getFloat(getContentResolver(), Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f);
                BMBLogger.d(TAG, "Duration scale is: " + durationScale);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                BMBLogger.d(TAG, "Duration scale is: " + e.getMessage());
            }
        }
    }

    public void onProfilesLoaded() {
        if (!permissionDialogAccept) {
            boolean hasNoUserProfiles = ProfileManager.getInstance().getProfileCount() == 0; //this 2nd condition would be the case if the last remaining profile on the device was deleted
            if (hasNoUserProfiles) {
                SecureUtils.INSTANCE.setHasNewDeviceID(true);
                SecureUtils.INSTANCE.removeEncryptedToken();
                if (SharedPreferenceService.INSTANCE.getProfileMigrationVersion() == 1) {
                    SharedPreferenceService.INSTANCE.setProfileMigrationVersion(UserProfile.CURRENT_PROFILE_MIGRATION_VERSION);
                }
            }
            if (!SecureUtils.INSTANCE.hasDeviceLinkingEncryptedToken()) {
                performHello();
            } else {
                PermissionFacade.requestDeviceStatePermission(SplashActivity.this, this::performHello);
            }
        }
    }

    private void performHello() {
        expressAuthenticationHelper = new ExpressAuthenticationHelper(this);
        if (NetworkUtils.INSTANCE.isNetworkConnected()) {
            expressAuthenticationHelper.performSplashHello(this::continueWithNavigation);
        } else {
            showNoConnectionScreen();
        }
    }

    private void createDeviceProfilingSession() {
        String customerSessionId = getAppCacheService().getCustomerSessionId();
        if (customerSessionId != null) {
            getDeviceProfilingInteractor().createPreLoginSession(customerSessionId);
        } else {
            getDeviceProfilingInteractor().disable();
        }
    }

    public Unit continueWithNavigation() {
        createDeviceProfilingSession();

        if (presenter.isDeviceLinked()) {
            launchPasscodeLoginScreen();
        } else {
            launchWelcomeScreen();
        }
        return Unit.INSTANCE;
    }

    public boolean isNewToBankDisabled() {
        return FeatureSwitchingCache.INSTANCE.getFeatureSwitchingToggles().getNtbRegistration() == FeatureSwitchingStates.DISABLED.getKey();
    }

    @Override
    public void onProfileLoadingFailed() {
        GenericResultActivity.bottomOnClickListener = v -> {
            moveTaskToBack(true);
            if (CompatibilityUtils.isVersionGreaterThan(Build.VERSION_CODES.LOLLIPOP)) {
                finishAndRemoveTask();
            } else {
                finishAffinity();
            }
            Process.killProcess(Process.myPid());
        };
        Intent deviceLinkingFailedIntent = new Intent(this, GenericResultActivity.class);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.IMAGE_RESOURCE_ID, R.drawable.ic_cross);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.NOTICE_MESSAGE, R.string.fatal_error);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.SUB_MESSAGE_STRING, getString(R.string.profile_loading_failed));
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.BOTTOM_BUTTON_MESSAGE, R.string.ok);
        deviceLinkingFailedIntent.putExtra(GenericResultActivity.CALL_US_CONTACT_NUMBER, getString(R.string.support_center_number));
        deviceLinkingFailedIntent.putExtra(BMBConstants.PRE_LOGIN_LAYOUT, true);
        startActivity(deviceLinkingFailedIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            if (requestCode == PermissionHelper.PermissionCode.ACCESS_DEVICE_STATE.value) {
                int permissionResult = grantResults[0];
                switch (permissionResult) {
                    case PackageManager.PERMISSION_GRANTED:
                        permissionDialogAccept = true;
                        performHello();
                        break;
                    case PackageManager.PERMISSION_DENIED:
                        onProfilesLoaded();
                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BMBLogger.d(TAG, "onResume");

        rootDetectionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BaseAlertDialog.INSTANCE.showAlertDialog(new AlertDialogProperties.Builder()
                        .title(getString(R.string.alert))
                        .message(getString(R.string.rooted_device_alert))
                        .build());
            }
        };
        registerReceiver(rootDetectionReceiver, new IntentFilter(BMBConstants.BLOCK_LOGIN), BLOCK_LOGIN_PERMISSION, null);
        setLottieAnimationListener();
    }

    @Override
    protected void onPause() {
        if (rootDetectionReceiver != null) {
            try {
                unregisterReceiver(rootDetectionReceiver);
                rootDetectionReceiver = null;
            } catch (IllegalArgumentException e) {
                BMBLogger.d(TAG, e.getMessage());
            }
        }
        super.onPause();
    }

    @Override
    public void showFailureMessage(String errorMessage) {
        showMessageError(errorMessage, (dialog, which) -> {
            if (dialog != null) {
                dialog.dismiss();
            }
            finish();
        });
    }

    @Override
    public void launchLandingScreen() {
        startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
        finish();
    }

    @Override
    public void launchWelcomeScreen() {
        Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class)
                .putExtra(WelcomeActivity.SKIP_HELLO_CONFIG, true);
        startActivity(intent);
        finish();
    }

    @Override
    public void launchPasscodeLoginScreen() {
        Intent intent = new Intent(SplashActivity.this, SimplifiedLoginActivity.class);
        if (getIntent() != null) {
            intent.putExtra(BMBConstants.PUSH_NOTIFICATION_APP_START, getIntent().getBooleanExtra(BMBConstants.PUSH_NOTIFICATION_APP_START, false));
            intent.putExtra(BMBConstants.PUSH_NOTIFICATION_TIME, getIntent().getLongExtra(BMBConstants.PUSH_NOTIFICATION_TIME, -1));
            if (getIntent().hasExtra(IN_APP_NOTIFICATION_IDENTIFIER)) {
                intent.putExtra(IN_APP_NOTIFICATION_IDENTIFIER, (Parcelable) getIntent().getParcelableExtra(IN_APP_NOTIFICATION_IDENTIFIER));
                currentShortcutId = IN_APP_SHORTCUT;
            }
            if (currentShortcutId != null) {
                intent.putExtra(SHORTCUT, currentShortcutId);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                currentShortcutId = null;
            }
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void showNoConnectionScreen() {
        startActivity(new Intent(SplashActivity.this, NoConnectionGenerateTokenActivity.class));
    }

    @DrawableRes
    private int getSplashWindowBackgroundResourceId() {
        switch (new Random().nextInt(8)) {
            case 0:
                returnValue = R.drawable.jhb_background_0;
                break;
            case 1:
                returnValue = R.drawable.jhb_background_1;
                break;
            case 2:
                returnValue = R.drawable.jhb_background_2;
                break;
            case 3:
                returnValue = R.drawable.dbn_background_0;
                break;
            case 4:
                returnValue = R.drawable.dbn_background_1;
                break;
            case 5:
                returnValue = R.drawable.dbn_background_2;
                break;
            case 6:
                returnValue = R.drawable.cpt_background_0;
                break;
            case 7:
                returnValue = R.drawable.cpt_background_1;
                break;
            case 8:
                returnValue = R.drawable.cpt_background_2;
                break;
        }
        return returnValue;
    }
}