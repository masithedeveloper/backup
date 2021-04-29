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
package com.barclays.absa.banking.boundary.model.airtime;

import com.barclays.absa.banking.boundary.model.Amount;
import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AirtimeBuyBeneficiaryConfirmation extends SureCheckResponse {

    @JsonProperty("benNam")
    private String beneficiaryName;
    @JsonProperty("txnAmt")
    private Amount amount;
    @JsonProperty("txnRefNo")
    private String txnReferenceNumber;
    @JsonProperty("frmAcctNo")
    private String fromAccountNumber;
    @JsonProperty("frmAcctTyp")
    private String fromAccountType;
    @JsonProperty("cellNo")
    private String cellNumber;
    @JsonProperty("networkProvider")
    private String networkProvider;
    @JsonProperty("airtimeTyp")
    private String rechargeType;
    @JsonProperty("networkProviderName")
    private String networkProviderName;

    private String voucherDescription;

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public Amount getAmount() {
        return amount == null ? new Amount() : amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public String getTxnReferenceNumber() {
        return txnReferenceNumber;
    }

    public void setTxnReferenceNumber(String txnReferenceNumber) {
        this.txnReferenceNumber = txnReferenceNumber;
    }

    public String getFromAccountNumber() {
        return fromAccountNumber;
    }

    public void setFromAccountNumber(String fromAccountNumber) {
        this.fromAccountNumber = fromAccountNumber;
    }

    private String getFromAccountType() {
        return fromAccountType;
    }

    private void setFromAccountType(String fromAccountType) {
        this.fromAccountType = fromAccountType;
    }

    public String getDescription() {
        return getFromAccountType();
    }

    public void setDescription(String description) {
        setFromAccountType(description);
    }

    public String getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
    }

    public String getNetworkProvider() {
        return networkProvider;
    }

    public void setNetworkProvider(String networkProvider) {
        this.networkProvider = networkProvider;
    }

    public String getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(String rechargeType) {
        this.rechargeType = rechargeType;
    }

    public String getNetworkProviderName() {
        return networkProviderName;
    }

    public void setNetworkProviderName(String networkProviderName) {
        this.networkProviderName = networkProviderName;
    }

    // Non (backend response / model) variables and methods
    private String beneficiaryID;
    private String networkProviderCode;

    public String getBeneficiaryID() {
        return beneficiaryID;
    }

    public void setBeneficiaryID(String beneficiaryID) {
        this.beneficiaryID = beneficiaryID;
    }

    public String getNetworkProviderCode() {
        return networkProviderCode;
    }

    public void setNetworkProviderCode(String networkProviderCode) {
        this.networkProviderCode = networkProviderCode;
    }

    public String getVoucherDescription() {
        return voucherDescription;
    }

    public void setVoucherDescription(String voucherDescription) {
        this.voucherDescription = voucherDescription;
    }
}
