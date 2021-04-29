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
package com.barclays.absa.banking.boundary.shared.dto;

import android.os.Build;

import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;
import com.barclays.absa.crypto.SecureUtils;
import com.barclays.absa.utils.DeviceUtils;

import static com.barclays.absa.banking.boundary.shared.TransactionVerificationService.OP0812_TRANSACTION_VERIFICATION_STATUS;
import static com.barclays.absa.banking.boundary.shared.TransactionVerificationService.OP0814_PRE_LOGON_TRANSACTION_VERIFICATION_STATUS;
import static com.barclays.absa.banking.boundary.shared.TransactionVerificationService.OP0820_VALIDATE_TRANSACTION_VERIFICATION_CODE;
import static com.barclays.absa.banking.boundary.shared.TransactionVerificationService.OP0830_PRE_LOGON_VALIDATE_TRANSACTION_VERIFICATION_CODE;
import static com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationOperation.REGISTER_CREDENTIALS;

public class TransactionVerificationRequest<T> extends ExtendedRequest<T> {

    private TransactionVerificationMethod transactionVerificationMethod;
    private String verificationCode;
    private static final String IDENTIFICATION_AND_VERIFICATION_TRANSACTION = "42";
    private TransactionVerificationOperation operation;
    private LinkingTransactionDetails linkingTransactionDetails;
    private String referenceNumber;
    private final String opCode;
    private final Class responseClass;
    private final boolean isPreLogonTransaction;
    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    public TransactionVerificationRequest(TransactionVerificationMethod verificationMethod, TransactionVerificationOperation verificationOperation, ExtendedResponseListener<T> responseListener, boolean isPreLogonTransaction) {
        super(responseListener);
        this.isPreLogonTransaction = isPreLogonTransaction;
        setMockResponseFile("transaction_verification/op0812_transaction_verification.json");
        opCode = isPreLogonTransaction ? OP0814_PRE_LOGON_TRANSACTION_VERIFICATION_STATUS : OP0812_TRANSACTION_VERIFICATION_STATUS;
        transactionVerificationMethod = verificationMethod;
        operation = verificationOperation;
        printRequest();
        responseClass = TransactionVerificationResponse.class;
    }

    public TransactionVerificationRequest(TransactionVerificationMethod verificationMethod, String referenceNumber, ExtendedResponseListener<T> responseListener, boolean isPreLogonTransaction) {
        super(responseListener);
        this.isPreLogonTransaction = isPreLogonTransaction;
        setMockResponseFile("transaction_verification/op0812_transaction_verification_success.json");
        opCode = isPreLogonTransaction ? OP0814_PRE_LOGON_TRANSACTION_VERIFICATION_STATUS : OP0812_TRANSACTION_VERIFICATION_STATUS;
        transactionVerificationMethod = verificationMethod;
        this.referenceNumber = referenceNumber;
        printRequest();
        switch (transactionVerificationMethod) {
            case GET_TRANSACTION_VERIFICATION_STATE:
                setMockResponseFile("transaction_verification/op0812_transaction_verification_success.json");
                responseClass = TransactionVerificationGetVerificationStateResponse.class;
                break;
            case RESEND_TRANSACTION_VERIFICATION:
            default:
                setMockResponseFile("transaction_verification/op0820_validate_transaction_verification_code.json");
                responseClass = TransactionVerificationResponse.class;
                break;
        }
    }

    public TransactionVerificationRequest(TransactionVerificationMethod verificationMethod, LinkingTransactionDetails linkingTransactionDetails, ExtendedResponseListener<T> responseListener, boolean isPreLogonTransaction) {
        super(responseListener);
        appCacheService.setIsIdentificationAndVerificationFlow(true);
        appCacheService.setLinkingTransactionDetails(linkingTransactionDetails);

        this.linkingTransactionDetails = linkingTransactionDetails;
        this.isPreLogonTransaction = isPreLogonTransaction;
        setMockResponseFile("transaction_verification/op0814_transaction_verification");
        opCode = OP0814_PRE_LOGON_TRANSACTION_VERIFICATION_STATUS;
        transactionVerificationMethod = verificationMethod;
        this.referenceNumber = appCacheService.getSureCheckReferenceNumber();
        operation = REGISTER_CREDENTIALS;
        printRequest();
        switch (transactionVerificationMethod) {
            case GET_TRANSACTION_VERIFICATION_STATE:
                setMockResponseFile("transaction_verification/op0812_transaction_verification_success.json");
                responseClass = TransactionVerificationGetVerificationStateResponse.class;
                break;
            case RESEND_TRANSACTION_VERIFICATION:
            default:
                setMockResponseFile("transaction_verification/op0820_validate_transaction_verification_code.json");
                responseClass = TransactionVerificationResponse.class;
                break;
        }
    }

    public TransactionVerificationRequest(TransactionVerificationMethod verificationMethod, String referenceNumber, LinkingTransactionDetails linkingTransactionDetails, ExtendedResponseListener<T> responseListener, boolean isPreLogonTransaction) {
        super(responseListener);
        this.linkingTransactionDetails = linkingTransactionDetails;
        this.isPreLogonTransaction = isPreLogonTransaction;
        setMockResponseFile("transaction_verification/op0812_transaction_verification_success.json");
        opCode = isPreLogonTransaction ? OP0814_PRE_LOGON_TRANSACTION_VERIFICATION_STATUS : OP0812_TRANSACTION_VERIFICATION_STATUS;
        transactionVerificationMethod = verificationMethod;
        this.referenceNumber = referenceNumber;
        printRequest();
        switch (transactionVerificationMethod) {
            case GET_TRANSACTION_VERIFICATION_STATE:
                setMockResponseFile("transaction_verification/op0812_transaction_verification_success.json");
                responseClass = TransactionVerificationGetVerificationStateResponse.class;
                break;
            case RESEND_TRANSACTION_VERIFICATION:
            default:
                setMockResponseFile("transaction_verification/op0820_validate_transaction_verification_code.json");
                responseClass = TransactionVerificationResponse.class;
                break;
        }
    }

    public TransactionVerificationRequest(String otp, String referenceNumber, ExtendedResponseListener<T> responseListener, boolean isPreLogonTransaction) {
        super(responseListener);
        this.isPreLogonTransaction = isPreLogonTransaction;
        setMockResponseFile("transaction_verification/op0820_validate_transaction_verification_code.json");
        opCode = isPreLogonTransaction ? OP0830_PRE_LOGON_VALIDATE_TRANSACTION_VERIFICATION_CODE : OP0820_VALIDATE_TRANSACTION_VERIFICATION_CODE;
        this.referenceNumber = referenceNumber;
        verificationCode = otp;
        printRequest();
        responseClass = TransactionVerificationValidateCodeResponse.class;
    }

    @Override
    public RequestParams getRequestParams() {
        String deviceID = SecureUtils.INSTANCE.getDeviceID();
        RequestParams.Builder requestParamsBuilder = new RequestParams.Builder()
                .put(opCode)
                .put(TransactionParams.Transaction.SERVICE_CHANNEL_IND, DeviceUtils.getChannelId())
                .put(TransactionParams.Transaction.MANUFACTURER, Build.MANUFACTURER)
                .put(TransactionParams.Transaction.MODEL, Build.MODEL)
                .put(TransactionParams.Transaction.IMEI, deviceID)
                .put(TransactionParams.Transaction.DEVICE_ID, deviceID);

        if (appCacheService.isIdentificationAndVerificationFlow() && appCacheService.isBioAuthenticated() && appCacheService.isIdentificationAndVerificationLinkingFlow() && !appCacheService.isIdentityAndVerificationPostLogin()) {
            if (linkingTransactionDetails == null) {
                linkingTransactionDetails = appCacheService.getLinkingTransactionDetails();
            }
            requestParamsBuilder.put("accessAccount", linkingTransactionDetails.getAccountNumber());
            requestParamsBuilder.put("userNumber", linkingTransactionDetails.getUserNumber());
            requestParamsBuilder.put("enterpriseSessionID", appCacheService.getEnterpriseSessionId());
        }

        if (appCacheService.isChangePrimaryDeviceFlow() || appCacheService.isChangePrimaryDeviceFlowFromSureCheck() || appCacheService.isBioAuthenticated()) {
            requestParamsBuilder.put("transactionType", IDENTIFICATION_AND_VERIFICATION_TRANSACTION);
        }

        if (transactionVerificationMethod != null) {
            requestParamsBuilder.put(TransactionParams.Transaction.METHOD_NAME, transactionVerificationMethod.getKey());
        }
        if (operation != null) {
            requestParamsBuilder.put(TransactionParams.Transaction.OPERATION, operation.getKey());
        }
        if (referenceNumber != null) {
            requestParamsBuilder.put("referenceNumber", referenceNumber);
        }
        if (verificationCode != null) {
            requestParamsBuilder.put(TransactionParams.Transaction.VERIFICATION_CODE, verificationCode);
        }

        return requestParamsBuilder.build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getResponseClass() {
        return (Class<T>) responseClass;
    }

    @Override
    public Boolean isEncrypted() {
        return !isPreLogonTransaction;
    }
}