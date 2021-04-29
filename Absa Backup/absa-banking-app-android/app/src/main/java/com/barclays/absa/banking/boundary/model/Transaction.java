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

import com.barclays.absa.banking.framework.utils.BMBLogger;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class Transaction implements Serializable, Cloneable {

    @JsonProperty("transactionCategory")
    private String transactionCategory;
    @JsonProperty("txnStatus")
    private String transactionStatus;
    @JsonProperty("txnPrt")
    private String description;
    @JsonProperty("drAmt")
    private Amount debitAmount;
    @JsonProperty("crAmt")
    private Amount creditAmount;
    @JsonProperty("dt")
    private String transactionDate;
    @JsonProperty("bal")
    private Amount balance;
    @JsonProperty("refNo")
    private String referenceNumber;

    private boolean isUnclearedTransaction;

    public String getTransactionCategory() {
        return transactionCategory;
    }

    public void setTransactionCategory(String transactionCategory) {
        this.transactionCategory = transactionCategory;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Amount getDebitAmount() {
        if (debitAmount == null) {
            debitAmount = new Amount();
        }
        return debitAmount;
    }

    public void setDebitAmount(Amount debitAmount) {
        this.debitAmount = debitAmount;
    }

    public Amount getCreditAmount() {
        if (creditAmount == null) {
            creditAmount = new Amount();
        }
        return creditAmount;
    }

    public void setCreditAmount(Amount creditAmount) {
        this.creditAmount = creditAmount;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Amount getBalance() {
        if (balance == null) {
            balance = new Amount();
        }
        return balance;
    }

    public void setBalance(Amount balance) {
        this.balance = balance;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public boolean isUnclearedTransaction() {
        return isUnclearedTransaction;
    }

    public void setUnclearedTransaction(boolean isUnclearedTransaction) {
        this.isUnclearedTransaction = isUnclearedTransaction;
    }

    public boolean isTransactionCreditType() {
        if (creditAmount != null && creditAmount.getAmount() != null && creditAmount.getAmount().trim().length() > 0) {
            final String creditStr = creditAmount.getAmount().replaceAll(",", "");
            final float creditVal = Float.parseFloat(creditStr);
            return creditVal != 0;
        }
        return false;
    }

    @NotNull
    @Override
    public String toString() {
        return "[" + getTransactionDate() + ", " + getBalance() + "]";
    }

    public Transaction updateDate(String date) {
        this.setTransactionDate(date);
        return this;
    }

    @NotNull
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            BMBLogger.e(e.toString());
        }
        return new Transaction();
    }
}
