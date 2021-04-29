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


import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.boundary.model.ErrorResponseObject;
import com.barclays.absa.banking.boundary.model.Payload;
import com.barclays.absa.banking.boundary.model.PayloadHeader;
import com.barclays.absa.banking.boundary.model.PayloadModel;
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.framework.api.request.ResponseListener;
import com.barclays.absa.banking.framework.api.request.params.OpCodeParams;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ResponseHeader;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.barclays.absa.utils.EncryptionUtils;
import com.barclays.absa.utils.NetworkUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import za.co.absa.networking.ResponseHelper;

abstract class AbstractServiceTask extends AsyncTask<Void, Void, String> implements ServiceTask {

    private static final String LOGOUT_RESULT_CODE = "RES0199";
    protected String url;
    protected ExtendedRequest request;
    private QueueCallBack callBack;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    AbstractServiceTask(ExtendedRequest request) {
        this(request.getServicePath(), request);
    }

    AbstractServiceTask(String url, ExtendedRequest request) {
        this.request = request;
        this.url = (url != null) ? url : BuildConfigHelper.INSTANCE.getServerPath();
    }

    AbstractServiceTask(ExtendedRequest request, QueueCallBack callback) {
        this(request.getServicePath(), request);
        this.callBack = callback;
    }

    public void setRequest(ExtendedRequest request) {
        this.request = request;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ExtendedResponseListener extendedResponseListener = request.getExtendedResponseListener();
        if (extendedResponseListener != null) {
            extendedResponseListener.onRequestStarted();
        }
        BMBLogger.d("Request Class", request.getClass().getName());
    }

    @Override
    public abstract String submitRequest();

    private ResponseObject getErrorResponse(String message) {
        ResponseObject responseObject = new ErrorResponseObject();
        responseObject.setOpCode(request.getRequestParams().getOpCode() != null ?
                request.getRequestParams().getOpCode() : request.getRequestParams().getParams().get(OpCodeParams.OPCODE_KEY));
        responseObject.setErrorMessage(message);
        trackErrorMsgEvent(message);
        return responseObject;
    }

    private boolean isSuccessBodyResponse(PayloadHeader payloadHeader) {
        final String responseCode = payloadHeader != null ? payloadHeader.getResponseCode() : "";

        // Check for success
        if (AppConstants.RESPONSE_CODE_SUCCESS.equals(responseCode)
                || AppConstants.RESPONSE_CODE_POLLING_SUCCESS.equals(responseCode)
                || AppConstants.RESPONSE_CODE_PARTIAL_SUCCESS.equals(responseCode)
                || AppConstants.RESPONSE_CODE_SESSION_EXPIRED.equals(responseCode)
                || AppConstants.RESPONSE_CODE_FORCE_SESSION_EXPIRED.equals(responseCode)
                || AppConstants.RESPONSE_CODE_EMS_FAILED.equals(responseCode))
            return true;

        if ((payloadHeader != null ? payloadHeader.getResponseMessage() : null) != null && ("No transactions available".equalsIgnoreCase(payloadHeader.getResponseMessage()) || "Geen transaksie beskikbaar nie".equalsIgnoreCase(payloadHeader.getResponseMessage()))) {
            return true;
        }

        String responseMessage = "";
        //Gateway's generic error codes start with FTR (e.g. SureCheck errors)
        if (responseCode != null && !responseCode.startsWith("FTR")) {
            // Check for authentication errors
            // Error Messages for Success Code with Warning.
            responseMessage = getErrorMsgFromCode(responseCode);
        }
        if (payloadHeader != null && !TextUtils.isEmpty(responseMessage)) {
            payloadHeader.setResponseMessage(responseMessage);
            return false;
        }

        return false;
    }

    private String getErrorMsgFromCode(String responseCode) {
        final String[] authenticationErrorMessages = BMBApplication.getInstance().getResources().getStringArray(
                R.array.authentication_error_msgs);
        final String[] authenticationResCodes = BMBApplication.getInstance().getResources().getStringArray(
                R.array.authentication_rescodes);

        for (int i = 0; i < authenticationResCodes.length; i++) {
            if (authenticationResCodes[i].equals(responseCode)) {
                return authenticationErrorMessages[i];
            }
        }
        return "";
    }

    private boolean isValidJsonResponse(String result) {
        char start = result.charAt(0);
        char end = result.charAt(result.length() - 1);
        return (start == '{' && end == '}');
    }

    @Override
    protected String doInBackground(Void... params) {
        return submitRequest();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onPostExecute(String response) {
        final String FAILURE_RESPONSE_STATUS = "FAILURE";
        final String SUCCESS_RESPONSE_STATUS = "SUCCESS";

        ExtendedResponseListener extendedResponseListener = request.getExtendedResponseListener();
        ResponseListener responseListener = request.getResponseListener();

        if (!TextUtils.isEmpty(response)) {
            response = response.replaceAll("\\r", "").replaceAll("\\t", "").replaceAll("\\n", "");
        }

        String finalResponse = response;
        Runnable runnable = () -> {
            final boolean usingExtendedResponseListener = extendedResponseListener != null;
            if (TextUtils.isEmpty(finalResponse)) {
                trackStatus(FAILURE_RESPONSE_STATUS);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    if (usingExtendedResponseListener) {
                        extendedResponseListener.onMaintenanceError();
                    } else {
                        responseListener.onMaintenanceError();
                    }
                });
                if (callBack != null) {
                    callBack.onRequestCompleted();
                }
                return;
            }

            if (isValidJsonResponse(finalResponse)) {
                ResponseHeader responseHeader = EncryptionUtils.getDecryptedResponseHeader(finalResponse);

                if (responseHeader == null) {
                    trackStatus(FAILURE_RESPONSE_STATUS);
                    new MonitoringInteractor().logTechnicalEvent(AbstractServiceTask.class.getSimpleName(), request.getRequestParams().getOpCode(), "Null Response Header");
                    final ResponseObject errorResponse = getErrorResponse(AppConstants.GENERIC_ERROR_MSG);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        if (usingExtendedResponseListener) {
                            extendedResponseListener.onFailure(errorResponse);
                        } else {
                            responseListener.onFailure(errorResponse);
                        }
                    });

                    if (callBack != null) {
                        callBack.onRequestCompleted();
                    }
                    return;

                }

                // Check if success response header code is received.
                if (!responseHeader.isSuccess()) {
                    trackStatus(FAILURE_RESPONSE_STATUS);
                    final ResponseObject errorResponse = getErrorResponse(responseHeader.getMessage());
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        if (usingExtendedResponseListener) {
                            extendedResponseListener.onFailure(errorResponse);
                        } else {
                            responseListener.onFailure(errorResponse);
                        }
                    });
                    if (callBack != null) {
                        callBack.onRequestCompleted();
                    }
                    return;
                }

                // Check if an SSL Exception
                if (responseHeader.getBody().equalsIgnoreCase(AppConstants.INVALID_CERTIFICATE_EXCEPTION)) {
                    ResponseObject responseObject = getErrorResponse(AppConstants.INVALID_CERTIFICATE_EXCEPTION);
                    responseObject.setResponseCode(AppConstants.RESPONSE_CODE_SESSION_EXPIRED);
                    trackStatus(FAILURE_RESPONSE_STATUS);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        if (usingExtendedResponseListener) {
                            extendedResponseListener.onFailure(responseObject);
                        } else {
                            responseListener.onFailure(responseObject);
                        }
                    });
                    if (callBack != null) {
                        callBack.onRequestCompleted();
                    }
                    return;
                }

                // Code Added for logging long responses
                BMBLogger.logPrettyJsonResponse("Response", responseHeader.getBody());

                PayloadHeader payloadHeader = null;
                PayloadModel payloadModel = null;

                if (responseHeader.getBody().contains("successResponseModel") || responseHeader.getBody().contains("failureResponseModel")) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    Payload payload;
                    try {
                        payload = objectMapper.readValue(responseHeader.getBody(), Payload.class);
                        payloadModel = payload.getPayloadModel();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (payloadModel != null) {
                        payloadHeader = payloadModel.getPayloadHeader();
                    }
                } else {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    try {
                        payloadModel = objectMapper.readValue(responseHeader.getBody(), PayloadModel.class);
                        payloadHeader = payloadModel.getPayloadHeader();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Check if success response code is received.
                if (payloadHeader != null && !isSuccessBodyResponse(payloadHeader)) {
                    trackStatus(FAILURE_RESPONSE_STATUS);
                    ResponseObject responseObject = getErrorResponse(payloadHeader.getResponseMessage());
                    if (payloadHeader.getResponseCode() != null) {
                        responseObject.setResponseCode(payloadHeader.getResponseCode());
                    }
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        if (usingExtendedResponseListener) {
                            if ("InvalidAuthLevel".equalsIgnoreCase(responseObject.getResponseCode()))
                                extendedResponseListener.onInvalidAuthLevel();
                            else
                                extendedResponseListener.onFailure(responseObject);
                        } else {
                            if ("InvalidAuthLevel".equalsIgnoreCase(responseObject.getResponseCode()))
                                responseListener.onInvalidAuthLevel();
                            else
                                responseListener.onFailure(responseObject);
                        }
                    });

                    if (callBack != null) {
                        callBack.onRequestCompleted();
                    }
                    return;
                }

                ResponseObject model = null;
                if (payloadModel != null && payloadModel.getPayloadData() != null) {
                    ResponseHelper.INSTANCE.removeNullsFromJsonNode(payloadModel.getPayloadData());
                    String payloadData = payloadModel.getPayloadData().toString();
                    model = (ResponseObject) request.getParsedResponse(payloadData);
                }
                if (model == null) {
                    model = new ErrorResponseObject();
                }

                if (responseHeader.getCode() == null) {
                    responseHeader = new ResponseHeader();
                    responseHeader.setCode("DEF001");
                    responseHeader.setMessage("Default response header");
                }
                // Set the model
                model.setResponseHeaderCode(responseHeader.getCode());
                model.setResponseHeaderMessage(responseHeader.getMessage());
                model.setResponseCode(payloadHeader.getResponseCode());
                model.setResponseMessage(payloadHeader.getResponseMessage());
                model.setOpCode(request.getRequestParams().getOpCode() != null ?
                        request.getRequestParams().getOpCode() : request.getRequestParams().getParams().get(OpCodeParams.OPCODE_KEY));
                model.setResponseId(payloadHeader.getResponseId());
                model.setActivityClass(null);
                model.setLaunchMode(null);
                trackStatus(SUCCESS_RESPONSE_STATUS);
                appCacheService.setLatestResponse(model);

                if (NetworkUtils.hasServerMaintenanceError(model.getResponseMessage())
                        || NetworkUtils.hasServerMaintenanceError(model.getErrorMessage())
                        || NetworkUtils.hasServerMaintenanceError(model.getResponseCode())) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        if (usingExtendedResponseListener) {
                            extendedResponseListener.onMaintenanceError();
                        } else {
                            responseListener.onMaintenanceError();
                        }
                    });

                    if (callBack != null) {
                        callBack.onRequestCompleted();
                    }
                    return;
                }


                if (model.getResponseId() != null && model.getResponseId().equalsIgnoreCase(LOGOUT_RESULT_CODE)) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        if (extendedResponseListener != null)
                            extendedResponseListener.forceLogout();
                    });
                } else {
                    Handler handler = new Handler(Looper.getMainLooper());
                    ResponseObject finalModel = model;
                    handler.post(() -> {
                        if (usingExtendedResponseListener) {
                            if (finalModel instanceof ErrorResponseObject) {
                                extendedResponseListener.onFailure(finalModel);
                            } else if (AppConstants.RESPONSE_CODE_POLLING_SUCCESS.equals(finalModel.getResponseCode())) {
                                extendedResponseListener.onPolling();
                            } else {
                                extendedResponseListener.onSuccess(finalModel);
                            }
                        } else {
                            if (finalModel instanceof ErrorResponseObject) {
                                responseListener.onFailure(finalModel);
                            } else if (AppConstants.RESPONSE_CODE_POLLING_SUCCESS.equals(finalModel.getResponseCode())) {
                                responseListener.onPolling();
                            } else {
                                responseListener.onSuccess(finalModel);
                            }
                        }
                    });
                }
            } else {
                trackStatus(FAILURE_RESPONSE_STATUS);
                if (BMBConstants.NETWORK_UNREACHABLE.equalsIgnoreCase(finalResponse)) {
                    ResponseObject errorResponse = getErrorResponse(BMBApplication.getInstance().getString(R.string.network_unreachable_msg));
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        if (usingExtendedResponseListener) {
                            extendedResponseListener.onNetworkUnreachable(errorResponse);
                        }
                    });
                } else {
                    ResponseObject errorResponse = getErrorResponse(finalResponse);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        if (usingExtendedResponseListener) {
                            extendedResponseListener.onFailure(errorResponse);
                        } else {
                            responseListener.onFailure(errorResponse);
                        }
                    });
                }
            }
            if (callBack != null) {
                callBack.onRequestCompleted();

            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

    }

    protected void trackStatus(String status) {
    }

    protected void trackErrorMsgEvent(String errorMsg) {
    }

    public interface QueueCallBack {
        void onRequestCompleted();
    }

}
