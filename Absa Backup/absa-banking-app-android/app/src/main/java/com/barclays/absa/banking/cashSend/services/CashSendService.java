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

package com.barclays.absa.banking.cashSend.services;

import android.os.Bundle;

import com.barclays.absa.banking.boundary.model.ATMAccessPINConfirmObject;
import com.barclays.absa.banking.boundary.model.AddBeneficiaryCashSendConfirmationObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryDetailObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryListObject;
import com.barclays.absa.banking.boundary.model.CancelCashSendResponse;
import com.barclays.absa.banking.boundary.model.PINObject;
import com.barclays.absa.banking.boundary.model.ResendWithdrawalSMSObject;
import com.barclays.absa.banking.boundary.model.TransactionUnredeem;
import com.barclays.absa.banking.boundary.model.cashSend.CashSendBeneficiaryConfirmation;
import com.barclays.absa.banking.boundary.model.cashSend.CashSendBeneficiaryResult;
import com.barclays.absa.banking.boundary.model.cashSend.CashSendOnceOffConfirmation;
import com.barclays.absa.banking.boundary.model.cashSend.CashSendOnceOffResult;
import com.barclays.absa.banking.boundary.model.cashSend.CashSendUnredeemedAccounts;
import com.barclays.absa.banking.boundary.model.cashSend.CashsendUnredeemTransactions;
import com.barclays.absa.banking.framework.ExtendedResponseListener;

public interface CashSendService {
    String OP0299_UNREDEEMED_CASHSEND = "OP0299";
    String OP0327_CASHSEND_UPDATE_ATM_PIN = "OP0327";
    String OP0451_CASHSEND_RESEND_WITHDRAWAL_SMS = "OP0451";
    String OP0520_ONCE_OFF_CASHSEND = "OP0520";
    String OP0610_ONCE_OFF_CASHSEND_CONFIRM = "OP0610";
    String OP0611_ONCE_OFF_CASHSEND_RESULT = "OP0611";
    String OP0612_SEND_BENEFICIARY_CASHSEND = "OP0612";
    String OP0614_SEND_BENEFICIARY_CASHSEND_RESULT = "OP0614";
    String OP0808_CANCEL_TRANSACTION = "OP0808";
    String CASHSEND_LIMIT_EXCEEDED_CODE = "A40608";
    String CASH_SEND_PLUS = "cashSendPlus";

    void getCashSendBeneficiariesList(ExtendedResponseListener<BeneficiaryListObject> beneficiariesListResponseListener);

    void requestCashSendPinEncryption(String pin, ExtendedResponseListener<PINObject> pinEncryptionResponseListener);

    void validateCashSendToSelf(CashSendBeneficiaryConfirmation cashSendBeneficiaryConfirmation, PINObject pinObject, boolean termsAccepted, String cellNumber, boolean isCashSendPlus, ExtendedResponseListener<CashSendBeneficiaryConfirmation> validateCashSendToSelfResponseListener);

    void performCashSendToBeneficiary(boolean isCashSendPlus, String transactionReference, boolean shouldUseResultStub, ExtendedResponseListener<CashSendBeneficiaryResult> performCashSendResponseListener);

    void requestAddCashSendBeneficiary(String beneficiaryId, String beneficiaryName, String beneficiaryNickname, String beneficiarySurname, String cellphoneNumber, String myReference, Boolean isBeneficiaryFavourite, ExtendedResponseListener<AddBeneficiaryCashSendConfirmationObject> addCashSendBeneficiaryResponseListener);

    void performAddCashSendBeneficiary(boolean isEditRequest, String transactionReference, String hasImage, ExtendedResponseListener<AddBeneficiaryCashSendConfirmationObject> performAddCashSendBeneficiaryResponseListener);

    void validateOnceOffCashSend(CashSendOnceOffConfirmation onceOffCashSendConfirmationObject, PINObject pinObject, boolean termsAccepted, boolean isCashSendPlus, ExtendedResponseListener<CashSendOnceOffConfirmation> validateOnceOffCashSendResponseListener);

    void performOnceOffCashSend(String transactionReference, ExtendedResponseListener<CashSendOnceOffResult> performOnceOffCashSendResponseListener);

    void fetchUnredeemedCashSendList(boolean isCashSendPlus, ExtendedResponseListener<CashSendUnredeemedAccounts> unredeemedCashSendResponseListener);

    void updateATMAccessPin(boolean isCashSendPlus, PINObject pinObject, Bundle passParam, ExtendedResponseListener<ATMAccessPINConfirmObject> updatePinResponseListener);

    void requestToCancelCashSendTransaction(boolean isCashSendPlus, TransactionUnredeem transactionUnredeem, ExtendedResponseListener<CancelCashSendResponse> cancelCashSendResponseListener);

    void requestWithdrawalSms(boolean isCashSendPlus, String fromAccount, TransactionUnredeem transactionUnredeemObject, ExtendedResponseListener<ResendWithdrawalSMSObject> smsWithdrawlResponseListener);

    void requestCashSendConfirmation(CashSendBeneficiaryConfirmation cashSendBeneficiaryConfirmation, PINObject pinObject, boolean termsAccepted, boolean isCashSendPlus, ExtendedResponseListener<CashSendBeneficiaryConfirmation> cashSendConfirmationResponseListener);

    void requestCashSendExistingBeneficiaryConfirmation(BeneficiaryDetailObject beneficiaryDetails, CashSendBeneficiaryConfirmation cashSendBeneficiaryConfirmation, PINObject pinObject, boolean termsAccepted, boolean isCashSendPlus, ExtendedResponseListener<CashSendBeneficiaryConfirmation> cashSendExistingBeneficiaryConfirmationResponseListener);

    void confirmEditCashSendBeneficiary(BeneficiaryDetailObject beneficiaryDetailObject, ExtendedResponseListener<AddBeneficiaryCashSendConfirmationObject> confirmBeneficiaryResponseListner);

    void requestUnredeemedTransactions(ExtendedResponseListener<CashsendUnredeemTransactions> responseListener, String accountNumber, boolean isCashSendPlus);
}