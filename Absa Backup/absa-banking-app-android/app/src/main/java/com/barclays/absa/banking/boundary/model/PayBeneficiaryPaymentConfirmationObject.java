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
package com.barclays.absa.banking.boundary.model;

import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PayBeneficiaryPaymentConfirmationObject extends SureCheckResponse {

    @JsonProperty("txnRef")
    private String transactionRefNo = "";
    private String msg;
    @JsonProperty("frmActNo")
    private String fromAccountNumber = "";
    @JsonProperty("maskedFrmActNo")
    private String maskedFromAccountNumber = "";
    private String fromAccountType;
    @JsonProperty("frmActDesc")
    private String description;
    @JsonProperty("benNam")
    private String beneficiaryName = "";
    private String acctAtInst = "";
    @JsonProperty("instNam")
    private String institutionName;
    @JsonProperty("benStatusType")
    private String beneficiaryType = "";
    private String bankName;
    private String branchName;
    private String branchCode;
    @JsonProperty("actNo")
    private String accountNumber = "";
    @JsonProperty("actTyp")
    private String accountType = "";
    @JsonProperty("nowFlg")
    private String paymentDate;
    @JsonProperty("imidPay")
    private String immediatePay = "";
    @JsonProperty("myRef")
    private String myReference = "";
    private String myNotice = "";
    private String myMethod = "";
    @JsonProperty("myDetl")
    private String myMethodDetails = "";
    @JsonProperty("thirRef")
    private String beneficiaryReference = "";
    @JsonProperty("thirNotice")
    private String beneficiaryNotice = "";
    @JsonProperty("thirMethod")
    private String beneficiaryMethod = "";
    @JsonProperty("thirDetl")
    private String beneficiaryMethodDetails = "";
    @JsonProperty("futureTxDate")
    private String futureDate = "";
    private String beneficiaryId;
    private String myFaxCode;
    private String benFaxCode;
    @JsonProperty("iipReferenceNumber")
    private String iipTrackingNo;
    @JsonProperty("txnAmt")
    private Amount transactionAmount;
    private String tiebNumber;
    private String popUpFlag;

    public String getInstitutionName() {
        return this.institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getBankName() {
        return this.bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBranchName() {
        return this.branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchCode() {
        return this.branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMaskedFromAccountNumber() {
        return this.maskedFromAccountNumber;
    }

    public void setMaskedFromAccountNumber(String maskedFromAccountNumber) {
        this.maskedFromAccountNumber = maskedFromAccountNumber;
    }

    public String getFromAccountNumber() {
        return this.fromAccountNumber;
    }

    public void setFromAccountNumber(String fromAccountNumber) {
        this.fromAccountNumber = fromAccountNumber;
    }

    public String getTransactionRefNo() {
        return this.transactionRefNo;
    }

    public void setTransactionRefNo(String transactionRefNo) {
        this.transactionRefNo = transactionRefNo;
    }

    public String getBeneficiaryName() {
        return this.beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getBeneficiaryType() {
        return this.beneficiaryType;
    }

    public void setBeneficiaryType(String beneficiaryType) {
        this.beneficiaryType = beneficiaryType;
    }

    public String getPaymentDate() {
        return this.paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getImmediatePay() {
        return this.immediatePay;
    }

    public void setImmediatePay(String immediatePay) {
        this.immediatePay = immediatePay;
    }

    public String getMyReference() {
        return this.myReference;
    }

    public void setMyReference(String myReference) {
        this.myReference = myReference;
    }

    public String getMyNotice() {
        return this.myNotice;
    }

    public void setMyNotice(String myNotice) {
        this.myNotice = myNotice;
    }

    public String getMyMethod() {
        return this.myMethod;
    }

    public void setMyMethod(String myMethod) {
        this.myMethod = myMethod;
    }

    public String getMyMethodDetails() {
        return this.myMethodDetails;
    }

    public void setMyMethodDetails(String myMethodDetails) {
        this.myMethodDetails = myMethodDetails;
    }

    public String getBeneficiaryNotice() {
        return this.beneficiaryNotice;
    }

    public void setBeneficiaryNotice(String beneficiaryNotice) {
        this.beneficiaryNotice = beneficiaryNotice;
    }

    public String getBeneficiaryMethod() {
        return this.beneficiaryMethod;
    }

    public void setBeneficiaryMethod(String beneficiaryMethod) {
        this.beneficiaryMethod = beneficiaryMethod;
    }

    public String getBeneficiaryMethodDetails() {
        return this.beneficiaryMethodDetails;
    }

    public void setBeneficiaryMethodDetails(String beneficiaryMethodDetails) {
        this.beneficiaryMethodDetails = beneficiaryMethodDetails;
    }

    public Amount getTransactionAmount() {
        return this.transactionAmount;
    }

    public void setTransactionAmount(Amount transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public void setBeneficiaryReference(String beneficiaryReference) {
        this.beneficiaryReference = beneficiaryReference;
    }

    public String getBeneficiaryReference() {
        return this.beneficiaryReference;
    }

    public String getBeneficiaryId() {
        return this.beneficiaryId;
    }

    public void setBeneficiaryId(String beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

    public String getFutureDate() {
        return this.futureDate;
    }

    public void setFutureDate(String futureDate) {
        this.futureDate = futureDate;
    }

    public String getFromAccountType() {
        return this.fromAccountType;
    }

    public void setFromAccountType(String fromAccountType) {
        this.fromAccountType = fromAccountType;
    }

    public String getAccountType() {
        return this.accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAcctAtInst() {
        return this.acctAtInst;
    }

    public void setAcctAtInst(String acctAtInst) {
        this.acctAtInst = acctAtInst;
    }

    public String getMyFaxCode() {
        return this.myFaxCode;
    }

    public void setMyFaxCode(String myFaxCode) {
        this.myFaxCode = myFaxCode;
    }

    public String getBenFaxCode() {
        return this.benFaxCode;
    }

    public void setBenFaxCode(String benFaxCode) {
        this.benFaxCode = benFaxCode;
    }

    public String getIipTrackingNo() {
        return iipTrackingNo;
    }

    public void setIipTrackingNo(String iipTrackingNo) {
        this.iipTrackingNo = iipTrackingNo;
    }

    public String getTiebNumber() {
        return tiebNumber;
    }

    public void setTiebNumber(String tiebNumber) {
        this.tiebNumber = tiebNumber;
    }

    public String getPopUpFlag() {
        return popUpFlag;
    }

    public void setPopUpFlag(String popUpFlag) {
        this.popUpFlag = popUpFlag;
    }

}
