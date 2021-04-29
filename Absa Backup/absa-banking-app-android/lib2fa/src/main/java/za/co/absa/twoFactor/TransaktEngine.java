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
package za.co.absa.twoFactor;

import android.app.Application;
import android.util.Log;

import com.entersekt.sdk.Auth;
import com.entersekt.sdk.Service;
import com.entersekt.sdk.Signup;
import com.entersekt.sdk.TransaktSDK;
import com.entersekt.sdk.callback.AuthAnswerCallback;
import com.entersekt.sdk.callback.ConnectCallback;
import com.entersekt.sdk.callback.SignupCallback;
import com.entersekt.sdk.callback.TrustTokenCallback;
import com.entersekt.sdk.listener.AuthListener;
import com.entersekt.sdk.listener.ConnectionListener;
import com.entersekt.sdk.listener.NotifyListener;
import com.entersekt.sdk.listener.RegisterListener;
import com.entersekt.sdk.listener.TDataListener;

public class TransaktEngine {

    private final String TAG = TransaktEngine.class.getSimpleName();
    private TransaktSDK sdk;
    private Service service;
    private ConnectCallback connectCallback;

    private boolean shouldConnectWithCallback = false;

    public TransaktEngine(Application app, RegisterListener registerListener, ConnectCallback connectCallback, ConnectionListener connectionListener, TDataListener tDataListener, AuthListener authListener, NotifyListener notifyListener) throws Exception {
        this.connectCallback = connectCallback;
        TransaktSDK.setRegisterListener(registerListener);
        TransaktSDK.setConnectionListener(connectionListener);
        TransaktSDK.getConfig().setAccessPoint(BuildConfig.ENTERSEKT_ZERO_RATED_ACCESS_POINT);
        try {
            sdk = TransaktSDK.init(app);
            configureCallbacksAndConnect(tDataListener, authListener, notifyListener);
        } catch (RuntimeException e) {
            //TODO: log issue to NewRelic
        }
    }

    public void connectWithCallBack() {
        if (sdk != null) {
            sdk.connect(connectCallback);
            service = sdk.getService(BuildConfig.TRANSAKT_SDK_SERVICE_ID);
        }
        shouldConnectWithCallback = false;
    }

    private void configureCallbacksAndConnect(TDataListener tDataListener, AuthListener authListener, NotifyListener notifyListener) {
        TransaktSDK.setNotifyListener(notifyListener);
        sdk.addListener(authListener);
        sdk.addListener(tDataListener);
        sdk.connect(connectCallback);
    }

    public void connect() {
        if (shouldConnectWithCallback) {
            connectWithCallBack();
            return;
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Attempting to connect to Entersekt");
        }
        if (sdk != null) {
            sdk.connect();
            service = sdk.getService(BuildConfig.TRANSAKT_SDK_SERVICE_ID);
        }
    }

    public static String getEmCert() {
        try {
            return TransaktSDK.get().getService(BuildConfig.TRANSAKT_SDK_SERVICE_ID).getEmCert().getEmCertId();
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean isRegistered() {
        if (BuildConfig.STUB) {
            return true;
        }
        try {
            return TransaktSDK.get().getService(BuildConfig.TRANSAKT_SDK_SERVICE_ID).isRegistered();
        } catch (Exception e) {
            return false;
        }
    }

    public void signUp(String signUpCode, SignupCallback signupCallback) {
        Signup signup = new Signup();
        signup.setSignupCode(signUpCode);
        if (service != null) {
            service.signup(signup, signupCallback);
        }
    }

    public void submitAuth(Auth auth, AuthAnswerCallback authAnswerCallback) {
        sdk.sendAuthAnswer(auth, authAnswerCallback);
    }

    public void generateToken(TrustTokenCallback callback) {
        if (service != null) {
            service.getTrustToken(callback);
        }
    }

    public void disconnect() {
        try {
            if (sdk != null) {
                sdk.disconnect();
            }
        } catch (IllegalArgumentException e) {
            // Service not registered
        }
    }

    public String getEmCertID() {
        if (service == null) {
            return "";
        }
        return service.getEmCert().getEmCertId();
    }

    public void setPushNotificationRegistrationToken(String pushNotificationRegistrationToken) {
        try {
            TransaktSDK.getConfig().setGoogleCloudMessagingId(pushNotificationRegistrationToken);
        } catch (NullPointerException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    public boolean isConnected() {
        if (sdk == null) {
            try {
                sdk = TransaktSDK.get();
            } catch (Exception e) {
                return false;
            }
        }
        return sdk.isConnected();
    }

    public TransaktSDK getSdk() {
        return sdk;
    }

    public Service getService() {
        return service;
    }

    public void setShouldConnectWithCallback(boolean shouldConnectWithCallback) {
        this.shouldConnectWithCallback = shouldConnectWithCallback;
    }
}