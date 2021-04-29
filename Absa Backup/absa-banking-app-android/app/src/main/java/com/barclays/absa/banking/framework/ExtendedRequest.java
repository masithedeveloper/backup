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
import com.barclays.absa.banking.framework.api.request.AbstractRequest;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.parsers.ResponseParser;

import java.util.Map;
import java.util.Set;

public class ExtendedRequest<T> extends AbstractRequest<T> {

    private static final String TAG = ExtendedRequest.class.getSimpleName();
    private Class expectedResponseType;
    private ResponseParser responseParser;
    private boolean forcedStubMode;
    boolean isGetRequest;
    private String servicePath;
    protected RequestParams params;
    private Boolean isEncrypted = null;

    public ExtendedRequest(String serviceUrl, ExtendedResponseListener<T> responseListener) {
        super(responseListener);
        servicePath = serviceUrl;
    }

    public ExtendedRequest(ExtendedResponseListener<T> responseListener) {
        this(BuildConfigHelper.INSTANCE.getServerPath(), responseListener);
    }

    public ExtendedRequest(String serviceUrl, RequestParams requestParams, ExtendedResponseListener<T> responseListener) {
        this(serviceUrl, responseListener);
        this.params = requestParams;
    }

    public ExtendedRequest(RequestParams requestParams, ExtendedResponseListener<T> responseListener) {
        this(BuildConfigHelper.INSTANCE.getServerPath(), requestParams, responseListener);
        printRequest();
    }

    public ExtendedRequest(RequestParams params, ExtendedResponseListener<T> accountDetailResponseListener, Class<T> expectedResponseClass) {
        this(params, accountDetailResponseListener);
        expectedResponseType = expectedResponseClass;
    }

    @Override
    public RequestParams getRequestParams() {
        return params;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<T> getResponseClass() {
        if (expectedResponseType != null) {
            return (Class<T>) expectedResponseType;
        }
        return (Class<T>) ResponseObject.class;
    }

    String getServicePath() {
        return servicePath;
    }

    protected void printRequest() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, buildJsonString());
            Log.d(TAG, "Request Details:");
            Log.d(TAG, "--------------------------------------------------------------------------------------------------");
            Log.d(TAG, String.format("%-15s", "Service path:") + servicePath);
            Log.d(TAG, String.format("%-15s", "OpCode:") + getRequestParams().getOpCode());
            Log.d(TAG, "Parameters:");
            Set<Map.Entry<String, String>> params = getRequestParams().getParams().entrySet();
            for (Map.Entry<String, String> param : params) {
                Log.d(TAG, String.format("%-50s", param.getKey()) + "= " + param.getValue());
            }
            Log.d(TAG, "**************************************************************************************************");
            Log.d("-", " ");
        }
    }

    private String buildJsonString() {
        StringBuilder output = new StringBuilder();
        output.append("{");
        Set<Map.Entry<String, String>> params = getRequestParams().getParams().entrySet();
        for (Map.Entry<String, String> param : params) {
            output.append("\"").append(param.getKey()).append("\"").append(":").append("\"").append(param.getValue()).append("\"").append(", ");
        }
        output.delete(output.length() - 2, output.length());
        output.append("}");
        return output.toString();
    }

    public void setResponseParser(ResponseParser responseParser) {
        this.responseParser = responseParser;
    }

    protected void setForcedStubMode(boolean shouldForceStubMode) {
        this.forcedStubMode = !BuildConfig.PRD && shouldForceStubMode;
    }

    boolean isForcedStubMode() {
        return forcedStubMode;
    }

    boolean isGetRequest() {
        return isGetRequest;
    }

    public ResponseParser getResponseParser() {
        return responseParser;
    }

    public ExtendedResponseListener<T> getExtendedResponseListener() {
        return responseListener;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getParsedResponse(String response) {
        if (responseParser != null) {
            return (T) responseParser.getParsedResponse(response);
        } else {
            return super.getParsedResponse(response);
        }
    }

    public void setEncrypted(boolean encryptRequest) {
        isEncrypted = encryptRequest;
    }

    public Boolean isEncrypted() {
        return isEncrypted;
    }

}