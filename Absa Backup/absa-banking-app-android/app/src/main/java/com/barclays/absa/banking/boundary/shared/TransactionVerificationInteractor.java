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
package com.barclays.absa.banking.boundary.shared;

import com.barclays.absa.banking.boundary.shared.dto.DeleteAliasRequest;
import com.barclays.absa.banking.boundary.shared.dto.LinkingTransactionDetails;
import com.barclays.absa.banking.boundary.shared.dto.SecurityCodeVerificationRequest;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationGetVerificationStateResponse;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationMethod;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationOperation;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationRequest;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationResponse;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationValidateCodeResponse;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;

public class TransactionVerificationInteractor implements TransactionVerificationService {

    @Override
    public void checkIfTransactionVerificationIsRequired(TransactionVerificationOperation verificationOperation, ExtendedResponseListener<TransactionVerificationResponse> verificationResponseListener) {
        TransactionVerificationRequest<TransactionVerificationResponse> verificationRequest = new TransactionVerificationRequest<>(TransactionVerificationMethod.IS_TRANSACTION_VERIFICATION_NEEDED, verificationOperation, verificationResponseListener, true);
        ServiceClient serviceClient = new ServiceClient(verificationRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void checkIfTransactionVerificationIsRequiredPostLogon(TransactionVerificationOperation verificationOperation, ExtendedResponseListener<TransactionVerificationResponse> verificationResponseListener) {
        TransactionVerificationRequest<TransactionVerificationResponse> verificationRequest = new TransactionVerificationRequest<>(TransactionVerificationMethod.IS_TRANSACTION_VERIFICATION_NEEDED, verificationOperation, verificationResponseListener, false);
        ServiceClient serviceClient = new ServiceClient(verificationRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void checkTransactionVerificationState(TransactionVerificationOperation verificationOperation, ExtendedResponseListener<TransactionVerificationGetVerificationStateResponse> verificationResponseListener) {
        TransactionVerificationStateRequest<TransactionVerificationGetVerificationStateResponse> verificationRequest = new TransactionVerificationStateRequest<>(TransactionVerificationMethod.IS_TRANSACTION_VERIFICATION_NEEDED, verificationOperation, verificationResponseListener, false);
        ServiceClient serviceClient = new ServiceClient(verificationRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void checkVerificationStatus(String referenceNumber, ExtendedResponseListener<TransactionVerificationGetVerificationStateResponse> responseListener) {
        TransactionVerificationRequest<TransactionVerificationGetVerificationStateResponse> transactionVerificationStatusRequest
                = new TransactionVerificationRequest<>(TransactionVerificationMethod.GET_TRANSACTION_VERIFICATION_STATE, referenceNumber, responseListener, true);
        ServiceClient serviceClient = new ServiceClient(transactionVerificationStatusRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void checkVerificationStatusPostLogon(String referenceNumber, ExtendedResponseListener<TransactionVerificationGetVerificationStateResponse> responseListener) {
        TransactionVerificationRequest<TransactionVerificationGetVerificationStateResponse> transactionVerificationStatusRequest
                = new TransactionVerificationRequest<>(TransactionVerificationMethod.GET_TRANSACTION_VERIFICATION_STATE, referenceNumber, responseListener, false);
        ServiceClient serviceClient = new ServiceClient(transactionVerificationStatusRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void performBiometricAuthentication(LinkingTransactionDetails linkingTransactionDetails, ExtendedResponseListener<TransactionVerificationResponse> responseListener) {
        TransactionVerificationRequest<TransactionVerificationResponse> verificationRequest = new TransactionVerificationRequest<>(TransactionVerificationMethod.IS_TRANSACTION_VERIFICATION_NEEDED, linkingTransactionDetails, responseListener, true);
        ServiceClient serviceClient = new ServiceClient(verificationRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void biometricAuthenticationVerificationStatusPreLogin(String referenceNumber, LinkingTransactionDetails linkingTransactionDetails, ExtendedResponseListener<TransactionVerificationGetVerificationStateResponse> responseListener) {
        TransactionVerificationRequest<TransactionVerificationGetVerificationStateResponse> transactionVerificationStatusRequest
                = new TransactionVerificationRequest<>(TransactionVerificationMethod.GET_TRANSACTION_VERIFICATION_STATE, referenceNumber, linkingTransactionDetails, responseListener, true);
        ServiceClient serviceClient = new ServiceClient(transactionVerificationStatusRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void biometricAuthenticationVerificationStatusPostLogin(String referenceNumber, LinkingTransactionDetails linkingTransactionDetails, ExtendedResponseListener<TransactionVerificationGetVerificationStateResponse> responseListener) {
        TransactionVerificationRequest<TransactionVerificationGetVerificationStateResponse> transactionVerificationStatusRequest
                = new TransactionVerificationRequest<>(TransactionVerificationMethod.GET_TRANSACTION_VERIFICATION_STATE, referenceNumber, linkingTransactionDetails, responseListener, false);
        ServiceClient serviceClient = new ServiceClient(transactionVerificationStatusRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void resendTransactionVerification(String referenceNumber, ExtendedResponseListener<TransactionVerificationResponse> responseListener) {
        TransactionVerificationRequest<TransactionVerificationResponse> transactionVerificationResendRequest
                = new TransactionVerificationRequest<>(TransactionVerificationMethod.RESEND_TRANSACTION_VERIFICATION, referenceNumber, responseListener, true);
        ServiceClient serviceClient = new ServiceClient(transactionVerificationResendRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void resendTransactionVerificationPostLogon(String referenceNumber, ExtendedResponseListener<TransactionVerificationResponse> responseListener) {
        TransactionVerificationRequest<TransactionVerificationResponse> transactionVerificationResendRequest
                = new TransactionVerificationRequest<>(TransactionVerificationMethod.RESEND_TRANSACTION_VERIFICATION, referenceNumber, responseListener, false);
        ServiceClient serviceClient = new ServiceClient(transactionVerificationResendRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void validateOtp(String otp, String referenceNumber, ExtendedResponseListener<TransactionVerificationValidateCodeResponse> verificationResponseListener) {
        TransactionVerificationRequest<TransactionVerificationValidateCodeResponse> verificationRequest = new TransactionVerificationRequest<>(otp, referenceNumber, verificationResponseListener, true);
        ServiceClient serviceClient = new ServiceClient(verificationRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void validateOtpPostLogon(String otp, String referenceNumber, ExtendedResponseListener<TransactionVerificationValidateCodeResponse> verificationResponseListener) {
        TransactionVerificationRequest<TransactionVerificationValidateCodeResponse> verificationRequest = new TransactionVerificationRequest<>(otp, referenceNumber, verificationResponseListener, false);
        ServiceClient serviceClient = new ServiceClient(verificationRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void validateSecurityCode(String securityCode, ExtendedResponseListener<TransactionVerificationValidateCodeResponse> verificationResponseListener) {
        submitRequest(new SecurityCodeVerificationRequest<>(securityCode, verificationResponseListener));
    }

    @Override
    public void deleteAlias(String aliasID, String deviceID, ExtendedResponseListener<TransactionVerificationValidateCodeResponse> verificationResponseListener) {
        submitRequest(new DeleteAliasRequest<>(aliasID, deviceID, verificationResponseListener));
    }

    private void submitRequest(ExtendedRequest request) {
        ServiceClient serviceClient = new ServiceClient(request);
        serviceClient.submitRequest();
    }
}