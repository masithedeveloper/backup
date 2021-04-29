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

import com.barclays.absa.banking.boundary.shared.dto.LinkingTransactionDetails;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationGetVerificationStateResponse;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationOperation;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationResponse;
import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationValidateCodeResponse;
import com.barclays.absa.banking.framework.ExtendedResponseListener;

public interface TransactionVerificationService {

    String OP0812_TRANSACTION_VERIFICATION_STATUS = "OP0812";
    String OP0814_PRE_LOGON_TRANSACTION_VERIFICATION_STATUS = "OP0814";
    String OP0820_VALIDATE_TRANSACTION_VERIFICATION_CODE = "OP0820";
    String OP0829_DELETE_ALIAS = "OP0829";
    String OP0830_PRE_LOGON_VALIDATE_TRANSACTION_VERIFICATION_CODE = "OP0830";

    void validateOtpPostLogon(String otp, String referenceNumber, ExtendedResponseListener<TransactionVerificationValidateCodeResponse> verificationResponseListener);

    void checkIfTransactionVerificationIsRequired(TransactionVerificationOperation verificationOperation, ExtendedResponseListener<TransactionVerificationResponse> verificationResponseListener);

    void checkIfTransactionVerificationIsRequiredPostLogon(TransactionVerificationOperation verificationOperation, ExtendedResponseListener<TransactionVerificationResponse> verificationResponseListener);

    void checkTransactionVerificationState(TransactionVerificationOperation verificationOperation, ExtendedResponseListener<TransactionVerificationGetVerificationStateResponse> verificationResponseListener);

    void checkVerificationStatus(String referenceNumber, ExtendedResponseListener<TransactionVerificationGetVerificationStateResponse> responseListener);

    void resendTransactionVerification(String referenceNumber, ExtendedResponseListener<TransactionVerificationResponse> responseListener);

    void resendTransactionVerificationPostLogon(String referenceNumber, ExtendedResponseListener<TransactionVerificationResponse> responseListener);

    void validateOtp(String otp, String referenceNumber, ExtendedResponseListener<TransactionVerificationValidateCodeResponse> verificationResponseListener);

    void validateSecurityCode(String securityCode, ExtendedResponseListener<TransactionVerificationValidateCodeResponse> verificationResponseListener);

    void deleteAlias(String aliasID, String deviceID, ExtendedResponseListener<TransactionVerificationValidateCodeResponse> verificationResponseListener);

    void checkVerificationStatusPostLogon(String referenceNumber, ExtendedResponseListener<TransactionVerificationGetVerificationStateResponse> responseListener);

    void performBiometricAuthentication(LinkingTransactionDetails linkingTransactionDetails, ExtendedResponseListener<TransactionVerificationResponse> responseListener);

    void biometricAuthenticationVerificationStatusPreLogin(String referenceNumber, LinkingTransactionDetails linkingTransactionDetails, ExtendedResponseListener<TransactionVerificationGetVerificationStateResponse> responseListener);

    void biometricAuthenticationVerificationStatusPostLogin(String referenceNumber, LinkingTransactionDetails linkingTransactionDetails, ExtendedResponseListener<TransactionVerificationGetVerificationStateResponse> responseListener);
}