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
import com.barclays.absa.banking.cashSend.services.dto.AddCashSendBeneficiaryConfirmationRequest;
import com.barclays.absa.banking.cashSend.services.dto.AddCashSendBeneficiaryResultRequest;
import com.barclays.absa.banking.cashSend.services.dto.CancelCashSendTransactionRequest;
import com.barclays.absa.banking.cashSend.services.dto.CashSendBeneficiaryListRequest;
import com.barclays.absa.banking.cashSend.services.dto.CashSendBeneficiaryResultRequest;
import com.barclays.absa.banking.cashSend.services.dto.CashSendConfirmationRequest;
import com.barclays.absa.banking.cashSend.services.dto.CashSendExistingBeneficiaryConfirmationRequest;
import com.barclays.absa.banking.cashSend.services.dto.CashSendPinEncryptionRequest;
import com.barclays.absa.banking.cashSend.services.dto.CashSendToSelfConfirmationRequest;
import com.barclays.absa.banking.cashSend.services.dto.CashSendUnredeemedTransactionsRequest;
import com.barclays.absa.banking.cashSend.services.dto.ConfirmEditCashSendBeneficiaryRequest;
import com.barclays.absa.banking.cashSend.services.dto.OnceOffCashSendConfirmationRequest;
import com.barclays.absa.banking.cashSend.services.dto.OnceOffCashSendResultRequest;
import com.barclays.absa.banking.cashSend.services.dto.ResendWithdrawlSmsRequest;
import com.barclays.absa.banking.cashSend.services.dto.UnredeemedTransactionsListRequest;
import com.barclays.absa.banking.cashSend.services.dto.UpdateCashSendPinRequest;
import com.barclays.absa.banking.framework.AbstractInteractor;
import com.barclays.absa.banking.framework.BuildConfigHelper;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;

public class CashSendInteractor extends AbstractInteractor implements CashSendService {

    @Override
    public void getCashSendBeneficiariesList(ExtendedResponseListener<BeneficiaryListObject> beneficiariesListResponseListener) {
        CashSendBeneficiaryListRequest<BeneficiaryListObject> cashSendBeneficiaryListRequest = new CashSendBeneficiaryListRequest<>(beneficiariesListResponseListener);
        submitRequest(cashSendBeneficiaryListRequest);
    }

    @Override
    public void requestCashSendPinEncryption(String pin, ExtendedResponseListener<PINObject> pinEncryptionResponseListener) {
        CashSendPinEncryptionRequest<PINObject> cashSendPinEncryptionRequest = new CashSendPinEncryptionRequest<>(pin, pinEncryptionResponseListener);
        ServiceClient serviceClient = new ServiceClient(BuildConfigHelper.INSTANCE.getPinEncryptServerPath(), cashSendPinEncryptionRequest);
        serviceClient.submitRequest();
    }

    @Override
    public void validateCashSendToSelf(CashSendBeneficiaryConfirmation cashSendBeneficiaryConfirmation, PINObject pinObject, boolean termsAccepted, String cellNumber, boolean isCashSendPlus, ExtendedResponseListener<CashSendBeneficiaryConfirmation> validateCashSendToSelfResponseListener) {
        CashSendToSelfConfirmationRequest<CashSendBeneficiaryConfirmation> cashSendToSelfConfirmationRequest = new CashSendToSelfConfirmationRequest<>(cashSendBeneficiaryConfirmation, pinObject, termsAccepted, cellNumber, isCashSendPlus, validateCashSendToSelfResponseListener);
        submitRequest(cashSendToSelfConfirmationRequest);
    }

    public void performCashSendToBeneficiary(boolean isCashSendPlus, String transactionReference, boolean shouldUseResultStub, ExtendedResponseListener<CashSendBeneficiaryResult> performCashSendResponseListener) {
        CashSendBeneficiaryResultRequest<CashSendBeneficiaryResult> cashSendBeneficiaryResultRequest = new CashSendBeneficiaryResultRequest<>(isCashSendPlus, transactionReference, shouldUseResultStub, performCashSendResponseListener);
        submitRequest(cashSendBeneficiaryResultRequest);
    }

    @Override
    public void requestAddCashSendBeneficiary(String beneficiaryId, String beneficiaryName, String beneficiaryNickname, String beneficiarySurname, String cellphoneNumber, String myReference, Boolean isBeneficiaryFavourite, ExtendedResponseListener<AddBeneficiaryCashSendConfirmationObject> addCashSendBeneficiaryResponseListener) {
        AddCashSendBeneficiaryConfirmationRequest<AddBeneficiaryCashSendConfirmationObject> addCashSendBeneficiaryConfirmationRequest = new AddCashSendBeneficiaryConfirmationRequest<>(beneficiaryId, beneficiaryName, beneficiaryNickname, beneficiarySurname, cellphoneNumber, myReference, isBeneficiaryFavourite, addCashSendBeneficiaryResponseListener);
        submitRequest(addCashSendBeneficiaryConfirmationRequest);
    }

    @Override
    public void performAddCashSendBeneficiary(boolean isEditRequest, String transactionReference, String hasImage, ExtendedResponseListener<AddBeneficiaryCashSendConfirmationObject> performAddCashSendBeneficiaryResponseListener) {
        AddCashSendBeneficiaryResultRequest<AddBeneficiaryCashSendConfirmationObject> addCashSendBeneficiaryResultRequest = new AddCashSendBeneficiaryResultRequest<>(isEditRequest, transactionReference, hasImage, performAddCashSendBeneficiaryResponseListener);
        submitRequest(addCashSendBeneficiaryResultRequest);
    }

    @Override
    public void validateOnceOffCashSend(CashSendOnceOffConfirmation onceOffCashSendConfirmationObject, PINObject pinObject, boolean termsAccepted, boolean isCashSendPlus, ExtendedResponseListener<CashSendOnceOffConfirmation> validateOnceOffCashSendResponseListener) {
        OnceOffCashSendConfirmationRequest<CashSendOnceOffConfirmation> onceOffCashSendConfirmationRequest = new OnceOffCashSendConfirmationRequest<>(onceOffCashSendConfirmationObject, pinObject, termsAccepted, isCashSendPlus, validateOnceOffCashSendResponseListener);
        submitRequest(onceOffCashSendConfirmationRequest);
    }

    @Override
    public void performOnceOffCashSend(String transactionReference, ExtendedResponseListener<CashSendOnceOffResult> performOnceOffCashSendResponseListener) {
        OnceOffCashSendResultRequest<CashSendOnceOffResult> onceOffCashSendResultRequest = new OnceOffCashSendResultRequest<>(transactionReference, performOnceOffCashSendResponseListener);
        submitRequest(onceOffCashSendResultRequest);
    }

    @Override
    public void fetchUnredeemedCashSendList(boolean isCashSendPlus, ExtendedResponseListener<CashSendUnredeemedAccounts> unredeemedCashSendResponseListener) {
        UnredeemedTransactionsListRequest<CashSendUnredeemedAccounts> unredeemedTransactionsListRequest = new UnredeemedTransactionsListRequest<>(isCashSendPlus, unredeemedCashSendResponseListener);
        submitRequest(unredeemedTransactionsListRequest);
    }

    @Override
    public void updateATMAccessPin(boolean isCashSendPlus, PINObject pinObject, Bundle passParam, ExtendedResponseListener<ATMAccessPINConfirmObject> updatePinResponseListener) {
        UpdateCashSendPinRequest<ATMAccessPINConfirmObject> updateCashSendPinRequest = new UpdateCashSendPinRequest<>(isCashSendPlus, pinObject, passParam, updatePinResponseListener);
        submitRequest(updateCashSendPinRequest);
    }

    @Override
    public void requestToCancelCashSendTransaction(boolean isCashSendPlus, TransactionUnredeem transactionUnredeem, ExtendedResponseListener<CancelCashSendResponse> cancelCashSendResponseListener) {
        CancelCashSendTransactionRequest<CancelCashSendResponse> cancelCashSendTransactionRequest = new CancelCashSendTransactionRequest<>(isCashSendPlus, transactionUnredeem, cancelCashSendResponseListener);
        submitRequest(cancelCashSendTransactionRequest);
    }

    @Override
    public void requestWithdrawalSms(boolean isCashSendPlus, String fromAccount, TransactionUnredeem transactionUnredeemObject, ExtendedResponseListener<ResendWithdrawalSMSObject> smsWithdrawalResponseListener) {
        ResendWithdrawlSmsRequest<ResendWithdrawalSMSObject> resendWithdrawlSmsRequest = new ResendWithdrawlSmsRequest<>(isCashSendPlus, fromAccount, transactionUnredeemObject, smsWithdrawalResponseListener);
        submitRequest(resendWithdrawlSmsRequest);
    }

    @Override
    public void requestCashSendConfirmation(CashSendBeneficiaryConfirmation cashSendBeneficiaryConfirmation, PINObject pinObject, boolean termsAccepted, boolean isCashSendPlus, ExtendedResponseListener<CashSendBeneficiaryConfirmation> cashSendConfirmationResponseListener) {
        CashSendConfirmationRequest<CashSendBeneficiaryConfirmation> cashSendConfirmationRequest = new CashSendConfirmationRequest<>(cashSendBeneficiaryConfirmation, pinObject, termsAccepted, isCashSendPlus, cashSendConfirmationResponseListener);
        submitRequest(cashSendConfirmationRequest);
    }

    @Override
    public void requestCashSendExistingBeneficiaryConfirmation(BeneficiaryDetailObject beneficiaryDetails, CashSendBeneficiaryConfirmation cashSendBeneficiaryConfirmation, PINObject pinObject, boolean termsAccepted, boolean isCashSendPlus, ExtendedResponseListener<CashSendBeneficiaryConfirmation> cashSendExistingBeneficiaryConfirmationResponseListener) {
        CashSendExistingBeneficiaryConfirmationRequest<CashSendBeneficiaryConfirmation> cashSendExistingBeneficiaryConfirmationRequest = new CashSendExistingBeneficiaryConfirmationRequest<>(beneficiaryDetails, cashSendBeneficiaryConfirmation, pinObject, termsAccepted, isCashSendPlus, cashSendExistingBeneficiaryConfirmationResponseListener);
        submitRequest(cashSendExistingBeneficiaryConfirmationRequest);
    }

    @Override
    public void confirmEditCashSendBeneficiary(BeneficiaryDetailObject beneficiaryDetailObject, ExtendedResponseListener<AddBeneficiaryCashSendConfirmationObject> confirmBeneficiaryResponseListener) {
        ConfirmEditCashSendBeneficiaryRequest<AddBeneficiaryCashSendConfirmationObject> confirmEditCashSendBeneficiaryRequest = new ConfirmEditCashSendBeneficiaryRequest<>(beneficiaryDetailObject, confirmBeneficiaryResponseListener);
        submitRequest(confirmEditCashSendBeneficiaryRequest);
    }

    @Override
    public void requestUnredeemedTransactions(ExtendedResponseListener<CashsendUnredeemTransactions> responseListener, String accountNumber, boolean isCashSendPlus) {
        CashSendUnredeemedTransactionsRequest<CashsendUnredeemTransactions> request = new CashSendUnredeemedTransactionsRequest<>(responseListener, accountNumber, isCashSendPlus);
        submitRequest(request);
    }
}