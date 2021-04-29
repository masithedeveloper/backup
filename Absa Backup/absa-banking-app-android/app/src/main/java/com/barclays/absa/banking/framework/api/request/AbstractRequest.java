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
package com.barclays.absa.banking.framework.api.request;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.framework.BaseActivity;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.utils.fileUtils.FileReaderUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.module.kotlin.KotlinModule;

import za.co.absa.networking.jackson.BooleanDeserializer;

public abstract class AbstractRequest<T> {

    public abstract RequestParams getRequestParams();

    private String mockResponseFile;

    public abstract Class<T> getResponseClass();

    protected ExtendedResponseListener<T> responseListener;

    public AbstractRequest(ExtendedResponseListener<T> responseListener) {
        this.responseListener = responseListener;
    }

    public ResponseListener<T> getResponseListener() {
        return responseListener;
    }

    public void setMockResponseFile(String mockResponseFile) {
        BMBLogger.d("x-class-setMockFile", mockResponseFile);
        this.mockResponseFile = mockResponseFile;
    }

    public String getMockResponseFile() {
        return this.mockResponseFile;
    }

    public boolean hasMockResponse() {
        return this.mockResponseFile != null;
    }

    public String getMockResponse() {
        return FileReaderUtils.getFileContent(mockResponseFile);
    }

    @SuppressWarnings("unchecked")
    public T getParsedResponse(String response) {
        Class<T> responseClass = getResponseClass();
        if (responseClass == null) {
            responseClass = (Class<T>) ResponseObject.class;
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new KotlinModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        SimpleModule booleanDeserializerModule = new SimpleModule("BooleanDeserializerModule");
        BooleanDeserializer booleanDeserializer = new BooleanDeserializer();
        booleanDeserializerModule.addDeserializer(Boolean.TYPE, booleanDeserializer);
        booleanDeserializerModule.addDeserializer(Boolean.class, booleanDeserializer);
        mapper.registerModule(booleanDeserializerModule);

        try {
            return mapper.readValue(response, responseClass);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                ((BaseActivity) BMBApplication.getInstance().getTopMostActivity()).dismissProgressDialog();
                BMBLogger.e(e.getMessage());
            }
            return null;
        }
    }
}