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

package com.barclays.absa.banking.boundary.docHandler;

import android.os.AsyncTask;
import android.util.Base64;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.boundary.docHandler.dto.DocHandlerGetCaseByIdResponse;
import com.barclays.absa.banking.framework.BuildConfigHelper;
import com.barclays.absa.banking.framework.ssl.DocHandlerRequestUtils;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.utils.fileUtils.FileReaderUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class DocHandlerGetServiceTask extends AsyncTask<Void, Void, String> {

    private static final String GET_SERVICE = "DocHandler/api/CaseController/";
    private static final String TAG = DocHandlerGetServiceTask.class.getSimpleName();

    private final String caseId;
    private DocHandlerGetCaseResponseListener docHandlerGetCaseResponseListener;
    private boolean isError = false;
    private String encodedPassword = "";

    DocHandlerGetServiceTask(String caseId, DocHandlerGetCaseResponseListener responseListener) {
        this.caseId = caseId;
        this.docHandlerGetCaseResponseListener = responseListener;
    }

    DocHandlerGetServiceTask(String caseId, String password, DocHandlerGetCaseResponseListener responseListener) {
        this.caseId = caseId;
        this.docHandlerGetCaseResponseListener = responseListener;

        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        encodedPassword = "Basic " + Base64.encodeToString(passwordBytes, Base64.DEFAULT);
    }

    protected String doInBackground(Void... params) {
        String response;
        HttpURLConnection connection = null;
        String docHandlerPostUrl = String.format("https://%s/%s", BuildConfigHelper.INSTANCE.getDocHandlerHostNameBaseUrl(), GET_SERVICE);
        String serviceUrl = docHandlerPostUrl + caseId;

        try {
            isError = true;
            BMBLogger.d(serviceUrl);
            final URL serviceHttpUrl = new URL(serviceUrl);
            if (serviceHttpUrl.getProtocol().equals("http")) {
                connection = (HttpURLConnection) serviceHttpUrl.openConnection();
            } else if (serviceHttpUrl.getProtocol().equals("https")) {
                DocHandlerRequestUtils httpRequestUtils = new DocHandlerRequestUtils();
                if (BuildConfig.ENABLE_CERT_PINNING) {
                    connection = httpRequestUtils.createSecureConnection(serviceHttpUrl);
                } else {
                    connection = (HttpsURLConnection) serviceHttpUrl.openConnection(); //uses system trust manager(s)
                }
            }

            assert connection != null;
            connection.setConnectTimeout(DocHandlerPostServiceTask.CONNECTION_TIMEOUT);
            connection.setReadTimeout(DocHandlerPostServiceTask.READ_TIME_OUT);

            connection.setRequestMethod("GET");
            if (encodedPassword.isEmpty()) {
                connection.setRequestProperty("Authorization", DocHandlerPostServiceTask.AUTH_TOKEN);
            } else {
                connection.setRequestProperty("Authorization", encodedPassword);
            }
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.setRequestProperty("Content-length", "0");
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String temp;
                while ((temp = bufferedReader.readLine()) != null) {
                    responseBuilder.append(temp);
                }
                response = responseBuilder.toString();
                isError = false;
            } else {
                BMBLogger.e(String.valueOf(connection.getResponseCode()));
                response = "Connection Failure";
            }
        } catch (SocketTimeoutException e) {
            response = "SocketTimeoutException";
        } catch (IOException e) {
            response = e.getMessage();
            BMBLogger.e("IO exception: ", e.getMessage());
        } catch (Exception e) {
            response = e.getMessage();
            BMBLogger.e("Failed to create secure connection: ", e.getMessage());
        } finally {
            if (connection != null) {
                BMBLogger.d(TAG, serviceUrl);
                connection.disconnect();
            }
        }
        return response;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String response) {
        if (response == null) {
            return;
        }
        if (isError) {
            onRequestFailure(response);
        } else {
            onRequestCompletedWithSuccess(response);
        }
    }

    private void onRequestCompletedWithSuccess(final String response) {
        BMBLogger.d(TAG, response);
        DocHandlerGetCaseByIdResponse[] docHandlerGetCaseByIdResponses = null;
        try {
            docHandlerGetCaseByIdResponses = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(response, DocHandlerGetCaseByIdResponse[].class);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (docHandlerGetCaseByIdResponses != null) {
                final DocHandlerGetCaseByIdResponse finalDocHandlerGetCaseByIdResponse = docHandlerGetCaseByIdResponses[0];
                docHandlerGetCaseResponseListener.onGetCaseSuccess(finalDocHandlerGetCaseByIdResponse);
            }
        }
    }

    private void onRequestFailure(final String response) {
        docHandlerGetCaseResponseListener.onGetCaseFailure(response);
    }

    public void submitRequest() {
        if (BuildConfigHelper.STUB && encodedPassword.isEmpty()) {
            onRequestCompletedWithSuccess(FileReaderUtils.getFileContent("new_to_bank/doc_handler_get_required_document.json"));
        } else if (BuildConfigHelper.STUB && !encodedPassword.isEmpty()) {
            onRequestCompletedWithSuccess(FileReaderUtils.getFileContent("new_to_bank/manage_profile_doc_handler_case.json"));
        } else {
            execute();
        }
    }
}
