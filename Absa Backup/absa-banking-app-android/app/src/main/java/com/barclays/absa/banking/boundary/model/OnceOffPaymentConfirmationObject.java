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

public class OnceOffPaymentConfirmationObject extends OnceOffPaymentObject {

    private String txnStatus;
    private String txnRef;
    private String msg;
    private String fromAccountNumber;
    private String fromAccountType;
    private String description;
    private String beneficiaryName;
    private String acctAtInst;
    private String institutionName;
    private String instCode;
    private String beneficiaryType;
    private String bankName;
    private String branchName;
    private String branchCode;
    private String accountNumber;
    private String accountType;
    private String paymentDate;
    private String immediatePay;
    private String myReference;
    private String myNotice;
    private String myMethod;
    private String myMethodDetails;
    private String myEmail;
    private String myMobile;
    private String myFaxNum;
    private String beneficiaryReference;
    private String beneficiaryNotice;
    private String beneficiaryMethod;
    private String beneficiaryMethodDetails;
    private String benMobile;
    private String benEmail;
    private String benFaxNum;
    private String futureDate;
    private String myFaxCode;
    private String benFaxCode;
    private String maskedFromAccountNumber;
    private String accept_terms;
    private String bankAccountNumber;
    private String bankAccountHolder;
    private String institutionCode;
	private String iipTrackingNo;
    private Amount transactionAmount = null;
    private String popUpFlag;

    public String getInstitutionCode() {
        return institutionCode;
    }
    public void setInstitutionCode(String institutionCode) {
        this.institutionCode = institutionCode;
    }

    public String getInstCode() {
        return instCode;
    }
    public void setInstCode(String instCode) {
        this.instCode = instCode;
    }

    public String getTxnStatus() {
        return this.txnStatus;
    }
    public void setTxnStatus(String txnStatus) {
        this.txnStatus = txnStatus;
    }

    public String getMsg() {
        return this.msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getAccountType() {
        return this.accountType;
    }
    public void setAccountType(String accountType) {
        this.accountType = accountType;
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

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }
    public String getInstitutionName() {
        return this.institutionName;
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

    public String getTxnRef() {
        return this.txnRef;
    }
    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
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

    public void setAccept_terms(String accept_terms) {
        this.accept_terms = accept_terms;
    }
    public String getAccept_terms() {
        return accept_terms;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }
    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getBankAccountHolder() {
        return bankAccountHolder;
    }
    public void setBankAccountHolder(String bankAccountHolder) {
        this.bankAccountHolder = bankAccountHolder;
    }

    public void setMyEmail(String myEmail) {
        this.myEmail = myEmail;
    }
    public String getMyEmail() {
        return myEmail;
    }

    public void setMyMobile(String myMobile) {
        this.myMobile = myMobile;
    }
    public String getMyMobile() {
        return myMobile;
    }

    public void setMyFaxNum(String myFaxNum) {
        this.myFaxNum = myFaxNum;
    }
    public String getMyFaxNum() {
        return myFaxNum;
    }

    public void setBenMobile(String benMobile) {
        this.benMobile = benMobile;
    }
    public String getBenMobile() {
        return benMobile;
    }

    public void setBenEmail(String benEmail) {
        this.benEmail = benEmail;
    }
    public String getBenEmail() {
        return benEmail;
    }

    public String getIipTrackingNo() {
		return iipTrackingNo;
	}
	public void setIipTrackingNo(String iipTrackingNo) {
		this.iipTrackingNo = iipTrackingNo;
	}

    public void setBenFaxNum(String benFaxNum) {
        this.benFaxNum = benFaxNum;
    }
    public String getBenFaxNum() {
        return benFaxNum;
    }

	public String getPopUpFlag() {
		return popUpFlag;
	}
	public void setPopUpFlag(String popUpFlag) {
		this.popUpFlag = popUpFlag;
	}

}
