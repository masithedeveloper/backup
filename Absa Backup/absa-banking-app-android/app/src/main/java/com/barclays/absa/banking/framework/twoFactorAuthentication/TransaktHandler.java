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

import android.os.Handler;
import android.os.NetworkOnMainThreadException;

import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.boundary.monitoring.MonitoringService;
import com.barclays.absa.banking.boundary.shared.sureCheck.TransaktInteractor;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.utils.AliasEncrypter;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.entersekt.sdk.Auth;
import com.entersekt.sdk.ConnectionContext;
import com.entersekt.sdk.Error;
import com.entersekt.sdk.Notify;
import com.entersekt.sdk.Service;
import com.entersekt.sdk.TData;
import com.entersekt.sdk.TrustToken;
import com.entersekt.sdk.callback.AuthAnswerCallback;
import com.entersekt.sdk.callback.ConnectCallback;
import com.entersekt.sdk.callback.SignupCallback;
import com.entersekt.sdk.callback.TrustTokenCallback;
import com.entersekt.sdk.listener.AuthListener;
import com.entersekt.sdk.listener.ConnectionListener;
import com.entersekt.sdk.listener.NotifyListener;
import com.entersekt.sdk.listener.RegisterListener;
import com.entersekt.sdk.listener.TDataListener;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import za.co.absa.scanToPay.ScanToPayHandler;

import static com.barclays.absa.banking.boundary.monitoring.MonitoringService.MONITORING_EVENT_ATTRIBUTE_TRANSAKT_ERROR;

public class TransaktHandler {

    private long transaktConnectStartTimeMillis = -1;
    private static final String TAG = TransaktHandler.class.getSimpleName();
    private boolean hasTransaktConnectCallbackTriggered;
    private ConnectionContext _2faConnectionContext;

    TransaktDelegate transaktDelegate;

    public TransaktHandler() {
        //DO NOT ERASE, this is required by BMBApplication
    }

    public TransaktHandler(TransaktDelegate transaktDelegate) {
        this.transaktDelegate = transaktDelegate;
    }

    private final RegisterListener _2faRegisterListener = new RegisterListener() {
        @Override
        public void onRegister(Service service) {
            BMBLogger.d(TAG, "Device successfully registered to Transakt Service " + service.getServiceId());
            if (transaktDelegate != null) {
                transaktDelegate.onRegisterSuccess();
            }
        }

        @Override
        public void onUnregister(Service service) {
            BMBLogger.d(TAG, "Device successfully deregistered from Transakt Service " + service.getServiceId());
        }
    };

    private final ConnectCallback _2faConnectCallback = new ConnectCallback() {
        @Override
        public void onSuccess() {
            getInteractor().setShouldConnectWithCallback(true);

            BMBLogger.d(TAG, "Transakt connection available :-)");

            if (!hasTransaktConnectCallbackTriggered && transaktDelegate != null) {
                transaktDelegate.onConnected();
                hasTransaktConnectCallbackTriggered = true;
            }
        }

        @Override
        public void onError(Error error) {
            getInteractor().setShouldConnectWithCallback(true);

            recordConnectionErrorMonitoringEvent();
            BMBLogger.e(TAG, "Failed to start to 2FA service: " + error.toString());
            //show the "Oops 'X'" screen

            HashMap<String, Object> map = new HashMap<>();
            map.put("errorName", error.name());
            map.put("errorMessage", error.toString());
            new MonitoringInteractor().logMonitoringEvent(MONITORING_EVENT_ATTRIBUTE_TRANSAKT_ERROR, map);

            BMBApplication.getInstance().dismissProgressDialog();
            if (transaktDelegate != null) {
                BMBLogger.e(TAG, "errorName" + error.name());
                BMBLogger.e(TAG, "errorMessage" + error.toString());
                transaktDelegate.onConnectionError(error);
            }
        }

        private void recordConnectionErrorMonitoringEvent() {
            HashMap<String, Long> eventData = new HashMap<>();
            eventData.put(MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_TIMESTAMP, System.currentTimeMillis());
            new MonitoringInteractor().logEvent(MonitoringService.MONITORING_EVENT_NAME_TRANSAKT_CONNECT_ERROR, eventData);
        }
    };

    private final ConnectionListener _2faConnectionListener = new ConnectionListener() {
        @Override
        public void onConnected(ConnectionContext connectionContext) {
            logMonitoringEvent();
            BMBLogger.d(TAG, "Transakt SDK connected :-)");
            BMBLogger.d(TAG, "Transakt SDK emCertID: " + getInteractor().getEmCertID());
            _2faConnectionContext = connectionContext;
            if (!hasTransaktConnectCallbackTriggered && transaktDelegate != null) {
                transaktDelegate.onConnected();
                if (!ScanToPayHandler.INSTANCE.isInitialized()) {
                    ScanToPayHandler.INSTANCE.initialize(getInteractor().getTransaktEngine());
                }
                hasTransaktConnectCallbackTriggered = true;
            }
        }

        private void logMonitoringEvent() {
            long transaktConnectEndTimeMillis = System.currentTimeMillis();
            Map<String, Long> event_data = new HashMap<>();
            event_data.put(MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_TIMESTAMP, transaktConnectEndTimeMillis);
            new MonitoringInteractor().logEvent(MonitoringService.MONITORING_EVENT_NAME_TRANSAKT_TIME_TO_CONNECT_END_TIME, event_data);

            long transaktConnectStartTimeMillis = TransaktHandler.this.transaktConnectStartTimeMillis;

            Map<String, String> eventData = new HashMap<>();
            eventData.put(MonitoringService.MONITORING_EVENT_NAME_TRANSAKT_TIME_TO_CONNECT_START_TIME, "" + transaktConnectStartTimeMillis);
            eventData.put(MonitoringService.MONITORING_EVENT_NAME_TRANSAKT_TIME_TO_CONNECT_END_TIME, "" + transaktConnectEndTimeMillis);
            long transaktConnectTimeEllapsed = transaktConnectEndTimeMillis - transaktConnectStartTimeMillis;
            eventData.put(MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_ELAPSED_TIME, "" + transaktConnectTimeEllapsed);
            new MonitoringInteractor().logEvent(MonitoringService.MONITORING_EVENT_NAME_TRANSAKT_TIME_TO_CONNECT, eventData);
        }

        @Override
        public void onDisconnected(ConnectionContext connectionContext) {
            getInteractor().setShouldConnectWithCallback(true);
            recordDisconnectMonitoringEvent();
            BMBLogger.d(TAG, "Transakt SDK disconnected :)");
            if (transaktDelegate != null) {
                transaktDelegate.onDisconnected();
                hasTransaktConnectCallbackTriggered = false;
            }
        }

        private void recordDisconnectMonitoringEvent() {
            Map<String, String> eventData = new HashMap<>();
            eventData.put(MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_APP_WENT_TO_BACKGROUND, "" + !BMBApplication.getInstance().isInForeground());
            new MonitoringInteractor().logEvent(MonitoringService.MONITORING_EVENT_NAME_TRANSAKT_DISCONNECTED, eventData);
        }
    };

    private final TrustTokenCallback _2faTrustTokenCallback = new TrustTokenCallback() {
        @Override
        public void onSuccess(Service service, TrustToken trustToken) {
            BMBLogger.d(TAG, "Transakt SDK trust token generated");
            if (transaktDelegate != null) {
                transaktDelegate.onGenerateTrustTokenSuccess(trustToken.getToken());
            }
        }

        @Override
        public void onError(Service service, Error error) {
            BMBLogger.d(TAG, "Transakt SDK could not generate trust token :(");
            BMBApplication.getInstance().dismissProgressDialog();
            HashMap<String, Object> map = new HashMap<>();
            map.put("errorName", error.name());
            map.put("errorMessage", error.toString());
            new MonitoringInteractor().logMonitoringEvent(MONITORING_EVENT_ATTRIBUTE_TRANSAKT_ERROR, map);

            BMBApplication.getInstance().dismissProgressDialog();
            if (transaktDelegate != null) {
                BMBLogger.e(TAG, "errorName" + error.name());
                BMBLogger.e(TAG, "errorMessage" + error.toString());
                transaktDelegate.onConnectionError(error);
            }
            if (transaktDelegate != null) {
                transaktDelegate.onGenerateTrustTokenFailure(error);
            }
        }
    };

    private SignupCallback _2faSignupCallback = new SignupCallback() {
        @Override
        public void onSuccess(Service service) {
            if (service.isRegistered()) {
                BMBLogger.d(TAG, "Transakt SDK sign up succeeded :). Registered");
                if (transaktDelegate != null) {
                    transaktDelegate.onSignupSuccess();
                }
            } else {
                BMBLogger.d(TAG, "Transakt SDK sign up succeeded :). Not Registered");
                if (transaktDelegate != null) {
                    transaktDelegate.onSignupError(Error.FIELD_NOT_SET);
                }
            }
        }

        @Override
        public void onError(Service service, Error error) {
            BMBLogger.d(TAG, "Transakt SDK sign up failure :)");
            //if you are already signed up this should be called
            if (service.isRegistered()) {
                BMBLogger.d(TAG, "Transakt sign up error [" + error + "]. but don't worry. The service is registered");
                if (transaktDelegate != null) {
                    transaktDelegate.onSignupSuccess();
                }
            } else {
                BMBLogger.e(TAG, "Transakt SDK error: " + error.toString());
                //show the "Oops 'X'" screen
                if (transaktDelegate != null) {
                    transaktDelegate.onSignupError(error);
                }
            }
        }
    };

    private final TDataListener _2faTDataListener = new TDataListener() {
        @Override
        public void onTData(Service service, TData tData) {
            BMBLogger.d(TAG, "Transakt SDK TData received :)");
            if (tData != null) {
                try {
                    TDataResponse tDataResponse = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(tData.getPayload(), TDataResponse.class);
                    if (transaktDelegate != null) {
                        transaktDelegate.onTDataReceived(tDataResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private AuthListener _2faAuthListener = new AuthListener() {
        @Override
        public void onAuthReceived(Service service, Auth auth) {
            BMBLogger.d(TAG, "Transakt SDK auth received :)");

            if (transaktDelegate != null) {
                BMBLogger.d(TAG, "Auth " + transaktDelegate.getClass().getSimpleName());
                BMBApplication.getInstance().onAuthReceived(auth);
            }
        }

        @Override
        public void onOfflineAuthReceived(Service service, Auth auth) {

        }
    };

    private final NotifyListener _2faNotifyListener = new NotifyListener() {
        @Override
        public void onNotify(Service service, Notify notify) {
            BMBLogger.d(TAG, "Transakt SDK Notify received :)");
            if (transaktDelegate != null) {
                transaktDelegate.onNotifyReceived(notify);
            }
        }
    };

    private final AuthAnswerCallback authAnswerCallback = new AuthAnswerCallback() {

        @Override
        public void onSuccess(Service service, Auth auth) {
            BMBLogger.d(TAG, "Transakt SDK auth answer successful: " + auth);
            if (transaktDelegate != null) {
                transaktDelegate.onAuthSucceeded(auth);
            }
        }

        @Override
        public void onError(Service service, Error error, Auth auth) {
            BMBLogger.d(TAG, "Transakt SDK auth failed :" + error);
            HashMap<String, Object> eventData = new HashMap<>();
            eventData.put(MonitoringService.MONITORING_EVENT_ATTRIBUTE_AUTH_FAILURE_REASON, error != null ? error.toString() : "");
            new MonitoringInteractor().logMonitoringEvent(MonitoringService.MONITORING_EVENT_NAME_AUTH_FAILED, eventData);
            //BMBApplication.getInstance().onAuthFailed();
        }
    };

    public void start() {
        if (FirebaseInstanceId.getInstance() != null) {
            String pushNotificationRegistrationToken = FirebaseInstanceId.getInstance().getToken();
            if (getInteractor() != null) {
                if (!getInteractor().isConnected()) {
                    if (pushNotificationRegistrationToken != null) {
                        getInteractor().setPushNotificationRegistrationToken(pushNotificationRegistrationToken);
                    }
                    transaktConnectStartTimeMillis = System.currentTimeMillis();
                    Map<String, Long> eventData = new HashMap<>();
                    eventData.put(MonitoringService.MONITORING_EVENT_ATTRIBUTE_NAME_TIMESTAMP, transaktConnectStartTimeMillis);
                    new MonitoringInteractor().logEvent(MonitoringService.MONITORING_EVENT_NAME_TRANSAKT_TIME_TO_CONNECT_START_TIME, eventData);
                    getInteractor().connect();
                } else {
                    _2faConnectionListener.onConnected(_2faConnectionContext);
                }
            } else {
                try {
                    BMBApplication.getInstance().setTransaktInteractor(new TransaktInteractor(this));
                    if (pushNotificationRegistrationToken != null) {
                        getInteractor().setPushNotificationRegistrationToken(pushNotificationRegistrationToken);
                    }
                    getInteractor().connect();
                } catch (Exception e) {
                    if (transaktDelegate != null) {
                        transaktDelegate.onConnectionError(Error.OPERATION_REMOTE_ERROR);
                        if (getInteractor() != null) {
                            getInteractor().connect();
                        }
                        BMBApplication.getInstance().dismissProgressDialog();
                    }
                }
            }
        } else {
            Map<String, String> loggingData = new HashMap<>();
            loggingData.put("data", "Firebase Instance ID was null");
            new MonitoringInteractor().logEvent("TransaktHandler", loggingData);
        }
    }

    void disconnect() {
        hasTransaktConnectCallbackTriggered = false;
        BMBApplication.getInstance().dismissProgressDialog();
        if (getInteractor() != null) {
            if (getInteractor().isConnected()) {
                getInteractor().disconnect();
            } else {
                _2faConnectionListener.onDisconnected(_2faConnectionContext);
            }
        }
    }

    public void setConnectCallbackTriggeredFlag(boolean hasTriggered) {
        hasTransaktConnectCallbackTriggered = hasTriggered;
    }

    public ConnectionListener getConnectListener() {
        return _2faConnectionListener;
    }

    public ConnectCallback getConnectCallback() {
        return _2faConnectCallback;
    }

    public RegisterListener getRegisterListener() {
        return _2faRegisterListener;
    }

    public void signUp(String uniqueCode) {
        getInteractor().signUp(uniqueCode, _2faSignupCallback);
    }

    public void signUp() {
        getInteractor().signUp(_2faSignupCallback);
    }

    public void generateTrustToken() {
        BMBLogger.d(TAG, "generateTrustToken");
        getInteractor().generateToken(_2faTrustTokenCallback);
    }

    public void sendAuthorization(final Auth auth, final UserProfile authOwner) {
        if (authOwner != null) {
            send(auth, authOwner);
        }
    }

    private void send(Auth auth, UserProfile authOwner) {
        try {
            String userId = authOwner.getUserId();
            Handler handler = new Handler(BMBApplication.getInstance().getBackgroundHandlerThread().getLooper());
            handler.post(() -> {
                String authResult = AliasEncrypter.encryptAlias(userId, true);
                BMBLogger.d(TAG, "Transakt auth response from user was " + authResult);
                auth.getTextBoxes().get(0).setUserResponse(authResult);
                BMBLogger.d(TAG, "Transakt auth just before sending..." + auth.getTextBoxes().get(0).getUserResponse());
                getInteractor().sendAuthorization(auth, authAnswerCallback);
            });
        } catch (NetworkOnMainThreadException e) {
            BMBLogger.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    public TDataListener getTDataListener() {
        return _2faTDataListener;
    }

    public AuthListener getAuthListener() {
        return _2faAuthListener;
    }

    public NotifyListener getNotifyListener() {
        return _2faNotifyListener;
    }

    public void setTransaktDelegate(TransaktDelegate transaktDelegate) {
        this.transaktDelegate = transaktDelegate;
    }

    public String getEmCertID() {
        return getInteractor() == null ? "" : getInteractor().getEmCertID();
    }

    boolean isConnected() {
        return getInteractor() != null && getInteractor().isConnected();
    }

    private TransaktInteractor getInteractor() {
        return BMBApplication.getInstance().getTransaktInteractor();
    }
}