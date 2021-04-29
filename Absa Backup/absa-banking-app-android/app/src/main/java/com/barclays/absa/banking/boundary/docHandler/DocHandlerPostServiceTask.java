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

import com.barclays.absa.banking.boundary.docHandler.dto.DocHandlerDocument;
import com.barclays.absa.banking.boundary.docHandler.dto.DocHandlerUploadDocumentResponse;
import com.barclays.absa.banking.framework.BuildConfigHelper;
import com.barclays.absa.banking.framework.ssl.DocHandlerRequestUtils;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.utils.fileUtils.FileReaderUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DocHandlerPostServiceTask extends AsyncTask<Void, Void, String> {

    final static int READ_TIME_OUT = 30000;
    final static int CONNECTION_TIMEOUT = 60000;
    final static String AUTH_TOKEN = "Basic Ymxhbms=";

    private static final String POST_SERVICE = "DocHandler/api/document/";
    private static final String TAG = DocHandlerPostServiceTask.class.getSimpleName();

    private DocHandlerDocument document;
    private DocHandlerUploadResponseListener docHandlerUploadResponseListener;
    private boolean isError = false;
    private String encodedPassword = "";

    DocHandlerPostServiceTask(DocHandlerDocument document, DocHandlerUploadResponseListener responseListener) {
        this.docHandlerUploadResponseListener = responseListener;
        this.document = document;
    }

    DocHandlerPostServiceTask(DocHandlerDocument document, String password, DocHandlerUploadResponseListener responseListener) {
        this.docHandlerUploadResponseListener = responseListener;
        this.document = document;

        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        encodedPassword = "Basic " + Base64.encodeToString(passwordBytes, Base64.DEFAULT);
    }

    protected String doInBackground(Void... params) {
        String response = null;
        HttpURLConnection connection = null;
        String docHandlerPostUrl = String.format("https://%s/%s", BuildConfigHelper.INSTANCE.getDocHandlerHostNameBaseUrl(), POST_SERVICE);

        try {
            isError = true;
            BMBLogger.d(docHandlerPostUrl);

            final URL serviceHttpUrl = new URL(docHandlerPostUrl);
            if (serviceHttpUrl.getProtocol().equals("http")) {
                connection = (HttpURLConnection) serviceHttpUrl.openConnection();
            } else if (serviceHttpUrl.getProtocol().equals("https")) {
                DocHandlerRequestUtils docHandlerRequestUtils = new DocHandlerRequestUtils();
                connection = docHandlerRequestUtils.createSecureConnection(serviceHttpUrl);
            }

            connection.setRequestMethod("POST");
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(READ_TIME_OUT);

            if (document != null) {
                DocHandlerUploadUtility multipartUtility;
                if (encodedPassword.isEmpty()) {
                    multipartUtility = new DocHandlerUploadUtility(connection, AUTH_TOKEN);
                } else {
                    multipartUtility = new DocHandlerUploadUtility(connection, encodedPassword);
                }
                multipartUtility.addFormField("caseId", document.getCaseId());
                multipartUtility.addFormField("docId", document.getDocId());
                multipartUtility.addFormField("docSubType", document.getDocSubType());
                multipartUtility.addFormField("category", document.getCategory());
                multipartUtility.addFormField("description", document.getDescription());

                if (document.getUploadDocument() != null) {
                    multipartUtility.addFilePart(document.getFileName(), document.getUploadDocument());
                } else {
                    multipartUtility.addFilePart(document.getFileName(), document.getUploadImage());
                }
                response = multipartUtility.finish();
                isError = false;
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
                BMBLogger.d(TAG, docHandlerPostUrl);
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
        DocHandlerUploadDocumentResponse[] docHandlerUploadDocumentResponses = null;

        try {
            docHandlerUploadDocumentResponses = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(response, DocHandlerUploadDocumentResponse[].class);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            final DocHandlerUploadDocumentResponse finalDocHandlerUploadDocumentResponse;
            if (docHandlerUploadDocumentResponses != null) {
                finalDocHandlerUploadDocumentResponse = docHandlerUploadDocumentResponses[0];
                docHandlerUploadResponseListener.onUploadSuccess(finalDocHandlerUploadDocumentResponse);
            } else {
                docHandlerUploadResponseListener.onUploadFailure("Failure: No Response");
            }
        }
    }

    private void onRequestFailure(final String response) {
        docHandlerUploadResponseListener.onUploadFailure(response);
    }

    public void submitRequest() {
        if (BuildConfigHelper.STUB && encodedPassword.isEmpty()) {
            onRequestCompletedWithSuccess(FileReaderUtils.getFileContent("new_to_bank/doc_handler_post_document.json"));
        } else if (BuildConfigHelper.STUB && !encodedPassword.isEmpty()) {
            onRequestCompletedWithSuccess(FileReaderUtils.getFileContent("new_to_bank/doc_handler_document_uploaded.json"));
        } else {
            execute();
        }
    }
}
