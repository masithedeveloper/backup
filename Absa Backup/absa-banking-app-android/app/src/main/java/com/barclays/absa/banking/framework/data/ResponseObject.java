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
package com.barclays.absa.banking.framework.data;

import androidx.annotation.NonNull;

import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.framework.utils.BMBLogger;

import java.io.Serializable;
import java.util.Locale;

import static com.barclays.absa.banking.framework.utils.AppConstants.GENERIC_ERROR_MSG;
import static com.barclays.absa.banking.framework.utils.AppConstants.GENERIC_ERROR_MSG_AF;

/**
 * The Class ResponseObject.
 */
public abstract class ResponseObject implements Serializable, BMBConstants {

    /**
     * The response header code.
     */
    private String responseHeaderCode;

    /**
     * The response header message.
     */
    private String responseHeaderMessage;

    /**
     * The response id.
     */
    private String responseId;

    /**
     * The response code.
     */
    private String responseCode;

    /**
     * The response message.
     */
    private String responseMessage;

    /**
     * The error message.
     */
    private String errorMessage;

    /**
     * The service version.
     */
    private String serviceVersion;

    /**
     * The launch mode.
     */
    private Integer launchMode;

    /**
     * The activity class.
     */
    private Class<?> activityClass;

    /**
     * The flow type.
     */
    private ApplicationFlowType flowType;

    /**
     * The op code.
     */
    private String opCode;

    /**
     * Gets the response id.
     *
     * @return the response id
     */
    public String getResponseId() {
        return this.responseId;
    }

    /**
     * Sets the response id.
     *
     * @param responseId the new response id
     */
    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    /**
     * Gets the op code.
     *
     * @return the op code
     */
    public String getOpCode() {
        return this.opCode;
    }

    /**
     * Sets the op code.
     *
     * @param opCode the new op code
     */
    public void setOpCode(String opCode) {
        this.opCode = opCode;
    }

    /**
     * Gets the response header code.
     *
     * @return the response header code
     */
    public String getResponseHeaderCode() {
        return this.responseHeaderCode;
    }

    /**
     * Sets the response header code.
     *
     * @param responseHeaderCode the new response header code
     */
    public void setResponseHeaderCode(String responseHeaderCode) {
        this.responseHeaderCode = responseHeaderCode;
    }

    /**
     * Gets the response header message.
     *
     * @return the response header message
     */
    public String getResponseHeaderMessage() {
        return this.responseHeaderMessage;
    }

    /**
     * Sets the response header message.
     *
     * @param responseHeaderMessage the new response header message
     */
    public void setResponseHeaderMessage(String responseHeaderMessage) {
        this.responseHeaderMessage = responseHeaderMessage;
    }

    /**
     * Gets the response code.
     *
     * @return the response code
     */
    public String getResponseCode() {
        return this.responseCode;
    }

    /**
     * Sets the response code.
     *
     * @param responseCode the new response code
     */
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * Gets the response message.
     *
     * @return the response message
     */
    public String getResponseMessage() {
        return this.responseMessage;
    }

    /**
     * Sets the response message.
     *
     * @param responseMessage the new response message
     */
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    /**
     * Gets the service version.
     *
     * @return the service version
     */
    public String getServiceVersion() {
        return this.serviceVersion;
    }

    /**
     * Sets the service version.
     *
     * @param serviceVersion the new service version
     */
    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    /**
     * Gets the flow type.
     *
     * @return the flow type
     */
    public ApplicationFlowType getFlowType() {
        return this.flowType;
    }

    /**
     * Sets the flow type.
     *
     * @param flowType the new flow type
     */
    public void setFlowType(ApplicationFlowType flowType) {
        this.flowType = flowType;
    }

    /**
     * Sets the launch mode.
     *
     * @param launchMode the new launch mode
     */
    public void setLaunchMode(Integer launchMode) {
        this.launchMode = launchMode;
    }

    /**
     * Gets the launch mode.
     *
     * @return the launch mode
     */
    public Integer getLaunchMode() {
        return launchMode;
    }

    /**
     * Sets the activity class.
     *
     * @param activityClass the new activity class
     */
    public void setActivityClass(Class<?> activityClass) {
        this.activityClass = activityClass;
    }

    /**
     * Gets the activity class.
     *
     * @return the activity class
     */
    public Class<?> getActivityClass() {
        return activityClass;
    }

    /**
     * Sets the error message.
     *
     * @param errorMessage the new error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Gets the error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    public static String extractErrorMessage(ResponseObject rob) {

        String genericErrorMessage = getGenericErrorMessage();

        String message = genericErrorMessage;
        if (rob instanceof TransactionResponse) {
            message = extractTransactionErrorMessage((TransactionResponse) rob);
            if ("success".equalsIgnoreCase(message)) {
                message = genericErrorMessage;
            }
            return message;
        }
        if (rob != null) {
            if (rob.getErrorMessage() != null && rob.getErrorMessage().length() > 0) {
                message = rob.getErrorMessage();
            } else if (rob.getResponseMessage() != null && rob.getResponseMessage().length() > 0) {
                message = rob.getResponseMessage();
            }
            if (BMBConstants.SUCCESS.equalsIgnoreCase(message)) {
                message = genericErrorMessage;
            }
            if (AppConstants.BMB99999_ERROR.equals(rob.getResponseCode()) ||
                    BMBConstants.FUNCTION_NOT_DEFINED.equals(rob.getResponseCode())) {
                BMBLogger.d(AppConstants.BMB99999_ERROR, message);
                message = genericErrorMessage;
            }
        }

        if (GENERIC_ERROR_MSG.equals(message)) {
            if (rob != null) {
                new MonitoringInteractor().logTechnicalEvent(ResponseObject.class.getSimpleName(), rob.opCode, String.format("errorMessage: %s | responseMessage: %s", rob.errorMessage, rob.responseMessage));
            } else {
                new MonitoringInteractor().logTechnicalEvent(ResponseObject.class.getSimpleName(), "", "Null Response Object");
            }
        }

        return message;
    }

    @NonNull
    private static String getGenericErrorMessage() {
        String genericErrorMessage;
        Locale locale = BMBApplication.getApplicationLocale();
        if (Locale.ENGLISH.equals(locale)) {
            genericErrorMessage = GENERIC_ERROR_MSG;
        } else {
            genericErrorMessage = GENERIC_ERROR_MSG_AF;
        }
        return genericErrorMessage;
    }

    private static String extractTransactionErrorMessage(TransactionResponse transactionResponse) {
        String message = getGenericErrorMessage();
        if (transactionResponse != null) {
            String transactionMessage = transactionResponse.getTransactionMessage();
            if (BMBConstants.FAILURE.equalsIgnoreCase(transactionResponse.getTransactionStatus()) && transactionMessage != null) {
                message = transactionMessage;
            }
        }
        return message;
    }

}