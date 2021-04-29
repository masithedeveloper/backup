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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatDelegate;

import com.adobe.mobile.Config;
import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.keepAlive.dto.KeepSessionAliveRequest;
import com.barclays.absa.banking.boundary.model.CustomerProfileObject;
import com.barclays.absa.banking.boundary.model.SessionAlive;
import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.boundary.shared.dto.LogoutRequest;
import com.barclays.absa.banking.boundary.shared.sureCheck.TransaktInteractor;
import com.barclays.absa.banking.express.logout.LogoutViewModel;
import com.barclays.absa.banking.express.ping.PingViewModel;
import com.barclays.absa.banking.framework.BackgroundHandlerThread;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.BuildConfigHelper;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;
import com.barclays.absa.banking.framework.SessionManager;
import com.barclays.absa.banking.framework.dagger.ApplicationComponent;
import com.barclays.absa.banking.framework.dagger.DaggerApplicationComponent;
import com.barclays.absa.banking.framework.dagger.ServiceModule;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitching;
import com.barclays.absa.banking.framework.featureSwitching.FeatureSwitchingCache;
import com.barclays.absa.banking.framework.push.PushNotificationHelper;
import com.barclays.absa.banking.framework.ssl.OkHttpConnectorServiceImpl;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktHandler;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktHandlerFactory;
import com.barclays.absa.banking.framework.twoFactorAuthentication.TransaktSdkAuthReceiver;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.banking.manage.profile.ManageProfileFileUtils;
import com.barclays.absa.banking.passcode.passcodeLogin.multipleUserLogin.SimplifiedLoginActivity;
import com.barclays.absa.banking.presentation.shared.IntentFactory;
import com.barclays.absa.banking.presentation.welcome.WelcomeActivity;
import com.barclays.absa.crypto.AESEncryption;
import com.barclays.absa.crypto.SecureUtils;
import com.barclays.absa.integration.DeviceProfilingInteractor;
import com.barclays.absa.utils.AbsaCacheManager;
import com.barclays.absa.utils.DeviceUtils;
import com.barclays.absa.utils.LocaleHelper;
import com.barclays.absa.utils.ProfileManager;
import com.barclays.absa.utils.UserSettingsManager;
import com.entersekt.sdk.Auth;
import com.google.firebase.FirebaseApp;
import com.newrelic.agent.android.NewRelic;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import za.co.absa.networking.ExpressNetworkingConfig;
import za.co.absa.networking.RetrofitClientFactory;

public final class BMBApplication extends Application implements BMBConstants, SessionManager.TimerListener {
    private static final String TAG = BMBApplication.class.getSimpleName();
    public static ApplicationComponent applicationComponent;
    public static final boolean FEATURE_BENEFICIARY_IMAGE = true;

    private boolean login_status = false; //Variable added to check status : true - insideLogin,false: outside

    /* to check RVN is set for session */
    public boolean is_RVN_checked = false;

    private static String deviceIntegrityFlag = ALPHABET_N;
    public static String SESSION_EXPIRE_EXTRA = "sessionExpire";

    /**
     * The stored encoded token.
     */
    private String STORED_ENCODED_TOKEN = "";

    /**
     * token used as nVal on download image
     */
    private String STORED_ENCODED_IVAL = "";

    private Activity topMostActivity;

    // Added for MI services ( Management Information )
    public String prevOpCode = "";
    public String previousOpCodeResponseTime = "";

    public String latestAppVersion = BuildConfig.VERSION_NAME;
    public int minimumSupportedSDK = Build.VERSION.SDK_INT;

    public long LOGGED_IN_START_TIME = 0;
    private long LOGGED_IN_END_TIME = 0;
    public static int TOTAL_FAILED_LOGIN_ATTEMPTS = 0;
    public boolean isFirstReqInitiated = false;
    public Auth auth;

    private TransaktHandler transaktHandler;
    private TransaktInteractor _2faInteractor;

    private ActivityLifeCycleObserver activityLifeCycleObserver;
    private boolean isInForeground;
    private TransaktSdkAuthReceiver transaktSdkAuthReceiver;
    private BackgroundHandlerThread backgroundHandlerThread = new BackgroundHandlerThread();
    private boolean deviceProfilingActive;
    private DeviceProfilingInteractor deviceProfilingInteractor;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerApplicationComponent
                .builder()
                .serviceModule(new ServiceModule(this))
                .build();

        backgroundHandlerThread.start();

        Handler backgroundThreadHandler = new Handler(backgroundHandlerThread.getLooper());
        if ("samsung".equalsIgnoreCase(Build.MANUFACTURER) && Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            new MonitoringInteractor().logMonitoringEvent("DEVICE_PROFILING_SKIPPED_INITIALIZATION_SAMSUNG_LOLLIPOP", new HashMap<>());
        } else {
            backgroundThreadHandler.post(() -> {
                if (BuildConfig.TOGGLE_DEF_DEVICE_PROFILING_ENABLED && !BMBApplication.this.getDeviceProfilingInteractor().initialize(BMBApplication.this)) {
                    new MonitoringInteractor().logMonitoringEvent("DEVICE_PROFILING_INITIALIZATION_FAILURE", new HashMap<>());
                }
            });
        }

        if (!BuildConfigHelper.STUB) {
            NewRelic.withApplicationToken(BuildConfig.NEW_RELIC_TOKEN).start(this);
            NewRelic.setAttribute("isHuaweiBuild", BuildConfig.IS_HUAWEI_BUILD);
        }

        ApplicationContextSingleton.getInstance().initializeSingleton(this);
/*        if (BuildConfig.DEBUG && (BuildConfig.DEV || BuildConfigHelper.STUB && BuildConfig.SIT)) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                return;
            }
            LeakCanary.install(this);
        }*/

        String adobeConfig = BuildConfig.PRD ? "adobe_mobile_config.json" : "adobe_mobile_config_dev.json";
        try {
            InputStream configInput = getAssets().open(adobeConfig);
            Config.overrideConfigStream(configInput);
        } catch (IOException ex) {
            System.out.println("IO Exception: " + ex.getMessage());
        } catch (Exception e) {
            logCaughtException(e);
        }

        /*
         * Adobe Tracking - Analytics
         *
         * set the context for the SDK
         * this is necessary for access to sharedPreferences and file i/o
         */
        Config.setDebugLogging(true);

        setTheme(R.style.Theme_Transparent);
        SessionManager.setTimerListener(this);

        transaktHandler = TransaktHandlerFactory.create();
        transaktSdkAuthReceiver = new TransaktSdkAuthReceiver();
        setTransaktSdkAuthReceiver(transaktSdkAuthReceiver);

/*        if (!BuildConfig.PRD && !BuildConfigHelper.STUB && !BuildConfig.UAT && !BuildConfig.UAT_LIVE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .build()
            );
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .build()
            );
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupNotificationChannels();
        }

        activityLifeCycleObserver = new ActivityLifeCycleObserver(this);
        registerActivityLifecycleCallbacks(activityLifeCycleObserver);
        setupFirebase();
        FeatureSwitchingCache.INSTANCE.setFeatureSwitchingToggles(new FeatureSwitching());
        IMIHelper.INSTANCE.startIMI(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            AppCompatDelegate.setDefaultNightMode(UserSettingsManager.INSTANCE.getDarkModeSetting());
        }
    }

    private void setupFirebase() {
        FirebaseApp.initializeApp(this);
    }

    public void logCaughtException(Exception exception) {
        new MonitoringInteractor().logCaughtExceptionEvent(exception);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void setupNotificationChannels() {
        PushNotificationHelper pushNotificationHelper = new PushNotificationHelper(this);
        pushNotificationHelper.createNotificationChannels();
    }

    public static BMBApplication getInstance() {
        return (BMBApplication) ApplicationContextSingleton.getInstance().getSingletonContext();
    }

    public void updateLanguage(Context ctx) {
        LocaleHelper.onAttach(ctx);
    }

    public static Locale getApplicationLocale() {
        return new Locale(LocaleHelper.getLanguage());
    }

    /**
     * Update language.
     *
     * @param ctx  the ctx
     * @param lang the lang
     */

    @SuppressLint("ApplySharedPref")
    public void updateLanguage(Context ctx, String lang) {
        LocaleHelper.setLocale(ctx, lang);
    }

    public void updateClientType(String clientTypeStr) {
        if (TextUtils.isEmpty(clientTypeStr) || clientTypeStr.equalsIgnoreCase("null")) {
            return;
        }

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BMBApplication.getInstance());
        final Editor custType = prefs.edit().putString(CLIENT_TYPE, clientTypeStr);
        custType.apply();
    }

    public static String getClientType() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BMBApplication.getInstance());
        return prefs.getString(CLIENT_TYPE, "");
    }

    /**
     * Gets the key.
     *
     * @param nonce the nonce
     * @return the key
     * @throws Exception the exception
     */
    public String getKey(String nonce) throws Exception {
        if (!TextUtils.isEmpty(nonce)) {
            STORED_ENCODED_TOKEN = nonce;
            return nonce;
        }
        return getKey();
    }

    /**
     * Gets the key.
     *
     * @return the key
     * @throws Exception the exception
     */
    public String getKey() throws Exception {
        return (TextUtils.isEmpty(STORED_ENCODED_TOKEN) ? getStaticKey() : STORED_ENCODED_TOKEN);
    }

    public String getStaticKey() throws Exception {
        final int START_OFFSET = 0;
        final int END_OFFSET = 16;

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BMBApplication.getInstance());

        //Get the device data
        String key = DeviceUtils.hasUuid() ? DeviceUtils.getDeviceUuid() : SecureUtils.INSTANCE.readDeviceIdentifier();

        //Gets the stored key token
        String storedKeyData = prefs.getString(BMBConstants.STORED_DETAILS, "");

        //Gets the key

        return AESEncryption.decrypt(key, storedKeyData, true).substring(START_OFFSET, END_OFFSET);
    }

    /**
     * Gets the request key.
     *
     * @return the request key
     */
    public String getRequestKey() {
        return STORED_ENCODED_TOKEN;
    }

    public String getIVal() {
        return STORED_ENCODED_IVAL;
    }

    public void setIVal(String iVal) {
        STORED_ENCODED_IVAL = iVal;
    }

    public boolean getUserLoggedInStatus() {
        return login_status;
    }

    public void setUserLoggedInStatus(boolean status) {
        login_status = status;
    }

    private long lastTouchTime = System.currentTimeMillis();

    public void setCurrentTime(long cTime) {
        lastTouchTime = cTime;
    }

    @Override
    public void timerReached() {
        long timeDiff = System.currentTimeMillis() - lastTouchTime;

        if (getUserLoggedInStatus()) {
            double threeAndHalfMinutesInMillis = 3.5 * 60 * 1000;
            if (timeDiff >= threeAndHalfMinutesInMillis) {
                if (BMBApplication.getInstance().isInForeground()) {
                    Intent logoutPopupIntent = IntentFactory.getLogOutDialog(topMostActivity);
                    PendingIntent pendingIntent = PendingIntent.getActivity(topMostActivity, 0, logoutPopupIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    try {
                        pendingIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                } else {
                    BMBApplication.getInstance().logoutAndGoToStartScreen();
                    return;
                }
            }

            KeepSessionAliveRequest<SessionAlive> keepSessionAliveRequest = new KeepSessionAliveRequest<>(getIVal(), sessionAliveResponseListener);
            ServiceClient serviceClient = new ServiceClient(keepSessionAliveRequest);
            serviceClient.submitRequest();

            new PingViewModel().pingKeepAlive();
        }
    }

    public static String getDeviceIntegrityFlag() {
        return deviceIntegrityFlag;
    }

    public void forceSignOut() {
        setUserLoggedInStatus(false);
        clearCaches();

        final int profileCount = ProfileManager.getInstance().getProfileCount();
        if (profileCount > 0) {
            signOut(profileCount);
            return;
        }

        ProfileManager.getInstance().loadAllUserProfiles(new ProfileManager.OnProfileLoadListener() {
            @Override
            public void onAllProfilesLoaded(List<UserProfile> userProfiles) {
                signOut(userProfiles.size());
            }

            @Override
            public void onProfilesLoadFailed() {

            }
        });
    }

    public void signOut(int count) {
        Intent sessionExpireIntent;
        if (count > 0) {
            sessionExpireIntent = new Intent(this, SimplifiedLoginActivity.class);
        } else {
            sessionExpireIntent = new Intent(this, WelcomeActivity.class);
        }
        sessionExpireIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        BMBLogger.d("SESSION EXPIRE");
        if (!isInForeground) {
            topMostActivity.setResult(Activity.RESULT_CANCELED);
            topMostActivity.finishAffinity();
        } else {
            startActivity(sessionExpireIntent);
        }
    }

    private ExtendedResponseListener<ResponseObject> logoutRequestExtendedResponseListener = new ExtendedResponseListener<ResponseObject>() {
        @Override
        public void onSuccess(ResponseObject successResponse) {
            Activity topMostActivity = BMBApplication.getInstance().getTopMostActivity();
            if (topMostActivity instanceof BaseActivity) {
                ((BaseActivity) topMostActivity).dismissProgressDialog();
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            Activity topMostActivity = BMBApplication.getInstance().getTopMostActivity();
            if (topMostActivity instanceof BaseActivity) {
                ((BaseActivity) topMostActivity).dismissProgressDialog();
            }
        }
    };

    public void logoutAndGoToStartScreen() {
        if (getUserLoggedInStatus()) {
            new LogoutViewModel().logout();
        }
        if (transaktSdkAuthReceiver != null) {
            transaktSdkAuthReceiver.stop();
        }
        logout();
        forceSignOut();
    }

    public void logout() {
        SessionManager.resetSession();
        getDeviceProfilingInteractor().destroySession();
        BMBLogger.d("Requesting Logout.....");
        // ---------------- changes for MI report -----------------------
        BMBApplication.getInstance().LOGGED_IN_END_TIME = System.currentTimeMillis();
        long logged_in_time = BMBApplication.getInstance().LOGGED_IN_END_TIME - BMBApplication.getInstance().LOGGED_IN_START_TIME;
        BMBLogger.d("MI", "User logged in for : " + logged_in_time + " milliseconds");
        BMBApplication.getInstance().LOGGED_IN_START_TIME = 0;
        BMBApplication.getInstance().LOGGED_IN_END_TIME = 0;

        final Activity topMostActivity = BMBApplication.getInstance().getTopMostActivity();
        if (topMostActivity instanceof BaseActivity) {
            logoutRequestExtendedResponseListener.setView(((BaseActivity) topMostActivity));
            ((BaseActivity) topMostActivity).trackLogout();
        }
        LogoutRequest<ResponseObject> logoutRequest = new LogoutRequest<>(logoutRequestExtendedResponseListener);
        logoutRequest.setMockResponseFile("logout/op0104_logout.json");

        ServiceClient serviceClient = new ServiceClient(logoutRequest);
        serviceClient.submitRequest();

        // Don't wait for the response from the server
        // Start Launcher Activity as soon as you send request for logoutAndGoToStartScreen
        // Don't care whether request is successful or not...
        ProfileManager profileManager = ProfileManager.getInstance();
        if (profileManager.getActiveUserProfile() != null && profileManager.getActiveUserProfile().getUserId() != null) {
            ManageProfileFileUtils.INSTANCE.removePreviewedFiles(BMBApplication.this, profileManager.getActiveUserProfile().getUserId());
        }

        updateLanguage(this, BMBConstants.ENGLISH_CODE);
        setUserLoggedInStatus(false); //Outside login : hence false.

        // Clear all caches
        clearCaches();
        ExpressNetworkingConfig.INSTANCE.setTokenExpireTime(null);
        OkHttpConnectorServiceImpl.getInstance().clearSessionCookies();
        RetrofitClientFactory.INSTANCE.clearCookieJar();
    }

    private void clearCaches() {
        CustomerProfileObject.getInstance().clear();
        AbsaCacheManager.getInstance().clearCache();
        applicationComponent.getAbsaCacheService().clear();
        applicationComponent.getAppCacheService().clear();
        applicationComponent.getHomeCacheService().clear();
        applicationComponent.getRewardsCacheService().clear();
        applicationComponent.getBeneficiaryCacheService().clear();
        applicationComponent.getOverdraftCacheService().clear();
        applicationComponent.getInternationalPaymentCacheService().clear();
    }

    public ExtendedResponseListener<SessionAlive> sessionAliveResponseListener = new ExtendedResponseListener<SessionAlive>() {

        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(SessionAlive sessionAlive) {
            if (!sessionAlive.isSessionAlive()) {
                forceSignOut();
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
        }
    };

    public TransaktHandler getTransaktHandler() {
        if (transaktHandler != null) {
            return transaktHandler;
        } else {
            transaktHandler = TransaktHandlerFactory.create();
        }

        return transaktHandler;
    }

    public TransaktInteractor getTransaktInteractor() {
        return _2faInteractor;
    }

    public void setTransaktInteractor(TransaktInteractor transaktInteractor) {
        _2faInteractor = transaktInteractor;
    }

    public synchronized Activity getTopMostActivity() {
        return topMostActivity;
    }

    public void setTopMostActivity(Activity topMostActivity) {
        this.topMostActivity = topMostActivity;
    }

    public boolean hasPendingAuth() {
        return transaktSdkAuthReceiver != null && transaktSdkAuthReceiver.hasAuthEventPending();
    }

    public void showVerificationScreen() {
        try {
            if (transaktSdkAuthReceiver == null) {
                transaktSdkAuthReceiver = new TransaktSdkAuthReceiver();
            }
            transaktSdkAuthReceiver.showAuthScreen(getTopMostActivity());
        } catch (Exception e) {
            BMBLogger.e(TAG, e.getMessage());
        }
    }

    public void clearPendingAuth() {
        if (transaktSdkAuthReceiver == null) {
            transaktSdkAuthReceiver = new TransaktSdkAuthReceiver();
        }
        transaktSdkAuthReceiver.clearPendingAuth();
    }

    public void clearVerificationRequest() {
        auth = null;
    }

    public void setVerificationRequest(Auth verificationRequest) {
        auth = verificationRequest;
    }

    public Auth getVerificationRequest() {
        return auth;
    }

    public void setForegrounded() {
        isInForeground = true;
    }

    public void setBackgrounded() {
        isInForeground = false;
    }

    public boolean isInForeground() {
        return isInForeground;
    }

    public boolean isNotListeningForAuth() {
        if (transaktSdkAuthReceiver == null) {
            transaktSdkAuthReceiver = new TransaktSdkAuthReceiver();
        }
        return !transaktSdkAuthReceiver.isListeningForAuthMessage();
    }

    public void dismissProgressDialog() {
        Activity activity = getTopMostActivity();
        if (activity instanceof BaseActivity && !(activity instanceof SimplifiedLoginActivity)) {
            ((BaseActivity) activity).dismissProgressDialog();
        }
    }

    public void onAuthReceived(Auth auth) {
        if (transaktSdkAuthReceiver == null) {
            transaktSdkAuthReceiver = new TransaktSdkAuthReceiver();
        }
        transaktSdkAuthReceiver.processAuth(auth);
    }

    //TODO Removed usage on 2019/10/28, is it still needed
    public void onAuthFailed() {
        if (transaktSdkAuthReceiver == null) {
            transaktSdkAuthReceiver = new TransaktSdkAuthReceiver();
        }
        transaktSdkAuthReceiver.onError();
    }

    public BackgroundHandlerThread getBackgroundHandlerThread() {
        return backgroundHandlerThread;
    }

    public boolean isDeviceProfilingActive() {
        return deviceProfilingActive;
    }

    public void setDeviceProfilingActive(boolean deviceProfilingActive) {
        this.deviceProfilingActive = deviceProfilingActive;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterActivityLifecycleCallbacks(activityLifeCycleObserver);
        backgroundHandlerThread.quit();
    }

    public void listenForAuth() {
        if (transaktSdkAuthReceiver == null) {
            transaktSdkAuthReceiver = new TransaktSdkAuthReceiver();
        }
        transaktSdkAuthReceiver.listen();
    }

    public void stopListeningForAuth() {
        if (transaktSdkAuthReceiver != null) {
            transaktSdkAuthReceiver.stop();
            transaktSdkAuthReceiver = null;
        }
    }

    private void setTransaktSdkAuthReceiver(TransaktSdkAuthReceiver transaktSdkAuthReceiver) {
        this.transaktSdkAuthReceiver = transaktSdkAuthReceiver;
    }

    public DeviceProfilingInteractor getDeviceProfilingInteractor() {
        if (deviceProfilingInteractor == null) {
            deviceProfilingInteractor = new DeviceProfilingInteractor();
        }
        return deviceProfilingInteractor;
    }
}