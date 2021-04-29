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
package com.barclays.absa.banking.framework;

import android.util.Log;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.framework.analytics.AnalyticsUtils;
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.ssl.OkHttpConnectorService;
import com.barclays.absa.banking.framework.ssl.OkHttpConnectorServiceImpl;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.crypto.AESEncryption;
import com.barclays.absa.crypto.SecureUtils;
import com.barclays.absa.utils.DeviceUtils;
import com.barclays.absa.utils.NetworkUtils;

import java.util.HashMap;
import java.util.Map;

import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.NULL;
import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0404_UNKNOWN;
import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP1000_LOGIN_SECURE_HOME_PAGE;

public class HttpServiceTask extends AbstractServiceTask {

    private static final String TAG = HttpServiceTask.class.getSimpleName();

    private OkHttpConnectorService okHttpConnectorService = OkHttpConnectorServiceImpl.getInstance();
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    HttpServiceTask(ExtendedRequest request) {
        this(request.getServicePath(), request);
    }

    HttpServiceTask(String url, ExtendedRequest request) {
        super(url, request);
    }

    HttpServiceTask(ExtendedRequest request, QueueCallBack callBack) {
        super(request, callBack);
    }

    @Override
    public String submitRequest() {
        applyDelayHack();
        addMonitoringData();
        String opCode = request.getRequestParams().getParams().get(OpCodeParams.OPCODE_KEY);
        Map<String, String> requestParams = request.getRequestParams().getParams();
        try {
            if (request.isEncrypted()) {
                requestParams = encryptRequestPayload(requestParams);
            }
        } catch (NullPointerException e) {
            throw new IllegalStateException("Please specify whether this ExtendedRequest is encrypted or not. OpCode: " + opCode);
        }
        augmentRequestBody(requestParams);

        if (request.isGetRequest()) {
            requestParams.clear();
        }

        long sendTime = System.currentTimeMillis();
        String result;
        result = okHttpConnectorService.callHttpService(url, opCode, requestParams);
        long receiveTime = System.currentTimeMillis();

        if (!request.isGetRequest()) {
            BMBLogger.d("Response", String.format("%-12s", "Encrypted") + result);
        }

        BMBApplication.getInstance().prevOpCode = opCode;
        BMBApplication.getInstance().previousOpCodeResponseTime = (receiveTime - sendTime) + "|";
        return result;
    }

    private void applyDelayHack() {
        String operationCode = request.getRequestParams().getOpCode();
        try {
            final BMBApplication app = BMBApplication.getInstance();
            if (operationCode != null) {
                if (!NULL.equals(operationCode)
                        && !OP0404_UNKNOWN.equals(operationCode)
                        && app.isFirstReqInitiated) {
                    Thread.sleep(2000);
                    app.isFirstReqInitiated = false;
                }
            } else {
                String opCode = request.getRequestParams().getParams().get(OpCodeParams.OPCODE_KEY);
                if (opCode != null
                        && !OP0404_UNKNOWN.equals(opCode)
                        && app.isFirstReqInitiated) {
                    Thread.sleep(2000);
                    app.isFirstReqInitiated = false;
                }
            }
        } catch (InterruptedException e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Delayed failed", e);
            }
        }
    }

    private void addMonitoringData() {
        try {
            String operationCode = request.getRequestParams().getOpCode();
            Map<String, String> params = request.getRequestParams().getParams();
            ResponseObject latestResponse = appCacheService.getLatestResponse();
            String previousOpCode = latestResponse != null ? latestResponse.getOpCode() : "";
            if (operationCode != null) {
                if (!NULL.equals(operationCode) && OP1000_LOGIN_SECURE_HOME_PAGE.equals(operationCode) && previousOpCode == null)
                    params.put("BMB_PREV_OPCODE", "AUTHENTICATION");  // WTF
                else {
                    params.put("BMB_PREV_OPCODE", previousOpCode);
                }
            } else {
                String opCode = request.getRequestParams().getParams().get(OpCodeParams.OPCODE_KEY);
                if (OP1000_LOGIN_SECURE_HOME_PAGE.equals(opCode) && previousOpCode == null)
                    params.put("BMB_PREV_OPCODE", "AUTHENTICATION");  // WTF
                else {
                    params.put("BMB_PREV_OPCODE", previousOpCode);
                }
            }

            BMBApplication.getInstance().prevOpCode = previousOpCode;
            params.put("BMB_PREV_OPCODE_RES_TIME", BMBApplication.getInstance().previousOpCodeResponseTime);
        } catch (Exception e) {
            BMBLogger.d(e);
        }
    }

    private void augmentRequestBody(Map<String, String> params) {
        params.put("channelInd", "S");
        if (!params.containsKey("serVer")) {
            params.put("serVer", "2.0");
        }
        params.put("BMB_DEVICE_ID", SecureUtils.INSTANCE.getDeviceID());
        params.put("BMB_DEVICE_OS_NAME", DeviceUtils.getOsName());
        params.put("BMB_DEVICE_OS_VERSION", DeviceUtils.getOsVersion());
        params.put("BMB_DEVICE_MODEL_NAME", DeviceUtils.getBuildModel());
        params.put("BMB_APPLICATION_VERSION", DeviceUtils.getVersionName());
        BMBLogger.d(TAG, "Final http request payload is " + params.toString());
    }

    private Map<String, String> encryptRequestPayload(final Map<String, String> params) {
        if (params == null || params.size() == 0) {
            throw new IllegalArgumentException("No parameters specified");
        }

        Map<String, String> parameters = new HashMap<>();
        boolean containsIValue = params.containsKey(BMBConstants.IVALUE);
        String urlEncodedParameters = NetworkUtils.urlEncode(params);

        if (BuildConfig.DEBUG) {
            Log.i(TAG, urlEncodedParameters);
        }

        try {
            final BMBApplication app = BMBApplication.getInstance();
            String iv = containsIValue ? app.getIVal() : app.getKey();
            String rawKey = app.getStaticKey();
            if (containsIValue) {
                parameters.put(BMBConstants.IVALUE, iv);
            } else {
                parameters.put(BMBConstants.NVALUE, BMBApplication.getInstance().getRequestKey());
            }
            String payload = AESEncryption.encrypt(iv, rawKey, urlEncodedParameters);
            parameters.put(BMBConstants.ENCRYPTED_PAYLOAD, payload);
            params.clear();

        } catch (Exception e) {
            BMBLogger.e(TAG, "Oops, Error in encrypting the request body... " + e);
        }
        return parameters;
    }

    protected void trackStatus(String status) {
        BMBApplication.getInstance().previousOpCodeResponseTime = BMBApplication.getInstance().previousOpCodeResponseTime + status;
    }

    @Override
    protected void trackErrorMsgEvent(String errorMsg) {
        AnalyticsUtils.getInstance().trackAppError(appCacheService.getAnalyticsScreenName(), appCacheService.getAnalyticsAppSection(), errorMsg);
    }
}