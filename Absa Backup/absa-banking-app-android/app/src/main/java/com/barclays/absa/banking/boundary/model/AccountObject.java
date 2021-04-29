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

import com.barclays.absa.utils.TextFormatUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import styleguide.utils.extensions.StringExtensions;

public class AccountObject implements Serializable, Entry {
    public static final String VIEW_ACCOUNT_HISTORY_ALLOWED = "viewAcctHistoryAllowed";
    public static final String MASK_BALANCE = "R *.**";

    // This class needs to be checked thoroughly as the backend response will differ for what
    // needs to be set

    private String isBclEligible;

    @JsonProperty("curBal")
    private Amount currentBalance;

    @JsonProperty("balance")
    private Amount balance;
    private boolean isRewardsBalanceRetrieved;
    private String favorite;

    @JsonProperty("monthly")
    private Amount monthlyAmount;

    public boolean isBclEligible() {
        return "true".equalsIgnoreCase(isBclEligible);
    }

    public void setIsBclEligible(String isBclEligible) {
        this.isBclEligible = isBclEligible;
    }

    public Amount getCurrentBalance() {
        if (monthlyAmount != null) {
            return monthlyAmount;
        } else if (currentBalance == null) {
            currentBalance = new Amount();
        }
        return currentBalance;
    }

    public Amount getBalance() {
        return balance;
    }

    public void setCurrentBalance(Amount currentBalance) {
        this.currentBalance = currentBalance;
    }

    @JsonProperty("unclearedAmount")
    private Amount unclearedBalance;

    public Amount getUnclearedBalance() {
        if (unclearedBalance == null) {
            unclearedBalance = new Amount();
        }
        return unclearedBalance;
    }

    public void setUnclearedBalance(Amount unclearedBalance) {
        this.unclearedBalance = unclearedBalance;
    }

    @JsonProperty("avblBal")
    private Amount availableBalance;

    public Amount getAvailableBalance() {
        if (availableBalance == null) {
            availableBalance = new Amount();
        }
        return availableBalance;
    }

    public String getAvailableBalanceFormated() {
        return TextFormatUtils.formatBasicAmount(getAvailableBalance());
    }

    public void setAvailableBalance(Amount availableBalance) {
        this.availableBalance = availableBalance;
    }

    @JsonProperty("mkdActNo")
    private String maskedAccountNumber;

    public String getMaskedAccountNumber() {
        return maskedAccountNumber;
    }

    public void setMaskedAccountNumber(String maskedAccountNumber) {
        this.maskedAccountNumber = maskedAccountNumber;
    }

    @JsonProperty("actNo")
    private String accountNumber;

    public String getAccountNumber() {
        if (accountNumber == null) {
            accountNumber = "";
        }
        return accountNumber;
    }

    public String getAccountNumberFormatted() {
        return StringExtensions.toFormattedAccountNumber(getAccountNumber());
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @JsonProperty("desc")
    private String description;

    public String getDescription() {
        return description;
    }

    public String getAccountInformation() {
        return TextFormatUtils.formatAccountNumberAndDescription(description, getAccountNumber());
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("curr")
    private String currency = "R";

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @JsonProperty("typ")
    private String accountType;

    public String getAccountType() {
        if (accountType == null) {
            accountType = "";
        }
        return accountType;
    }

    public boolean isCurrentAccount() {
        return "currentAccount".equalsIgnoreCase(getAccountType());
    }

    public boolean isChequeAccount() {
        return "chequeAccount".equalsIgnoreCase(getAccountType());
    }

    public boolean isSavingAccount() {
        return "savingsAccount".equalsIgnoreCase(getAccountType());
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public boolean isAccountDetailAllowed() {
        return "Y".equalsIgnoreCase(viewAcctHistoryAllowed);
    }

    @JsonProperty("viewAcctHistoryAllowed")
    private String viewAcctHistoryAllowed;

    private String getViewAcctHistoryAllowed() {
        return viewAcctHistoryAllowed;
    }

    public void setViewAcctHistoryAllowed(String viewAcctHistoryAllowed) {
        this.viewAcctHistoryAllowed = viewAcctHistoryAllowed;
    }

    @JsonProperty("overDraftLimit")
    private Amount overDraftLimitBalance;

    public Amount getOverDraftLimitBalance() {
        return overDraftLimitBalance;
    }

    public void setOverDraftLimitBalance(Amount overDraftLimitBalance) {
        this.overDraftLimitBalance = overDraftLimitBalance;
    }

    @JsonProperty("acctAccessBits")
    private String accessBits;

    public String getAccessBits() {
        return (accessBits == null || accessBits.isEmpty()) ? "-2" : accessBits;
    }

    public void setAccessBits(String accessBits) {
        this.accessBits = accessBits;
    }

    @JsonProperty("accessTypeBits")
    private String accessTypeBit;

    public String getAccessTypeBit() {
        return (accessTypeBit == null) ? "0" : accessTypeBit;
    }

    public void setAccessTypeBit(String accessTypeBit) {
        this.accessTypeBit = accessTypeBit;
    }

    @JsonProperty("actHlds")
    private String displayName;

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonProperty("image")
    private String accountImageURL;

    public void setAccountImageURL(String accountImageURL) {
        this.accountImageURL = accountImageURL;
    }

    public String getAccountImageURL() {
        return this.accountImageURL;
    }

    @JsonProperty("isBalanceMasked")
    private String isBalanceMasked;

    public void setBalanceMasked(String isBalanceMasked) {
        this.isBalanceMasked = isBalanceMasked;
    }

    public String isBalanceMasked() {
        return isBalanceMasked;
    }

    @JsonProperty("selected") // Issue where "favourite" is also used as serializedName
    private boolean isSelected;

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @NotNull
    @Override
    public String toString() {
        return "AccountType :" + accountType +
                "AccountDescription :" + description +
                " AccountNumber :" + accountNumber +
                " AvailableBalance : " + (availableBalance == null ? "" : availableBalance.toString()) +
                " CurrentBalance : " + (currentBalance == null ? "" : currentBalance.toString());
    }

    @Override
    public int getEntryType() {
        return Entry.ACCOUNT;
    }

    public void setRewardsBalanceRetrieved(boolean rewardsBalanceRetrieved) {
        this.isRewardsBalanceRetrieved = rewardsBalanceRetrieved;
    }

    public boolean isRewardsBalanceRetrieved() {
        return isRewardsBalanceRetrieved;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public String getFavorite() {
        return favorite;
    }

    public Amount getMonthlyAmount() {
        return monthlyAmount;
    }

    public void setMonthlyAmount(Amount monthlyAmount) {
        this.monthlyAmount = monthlyAmount;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AccountObject)) {
            return false;
        }
        AccountObject accountObject = (AccountObject) obj;
        return accountObject.getAccountNumber() != null && getAccountNumber() != null && accountObject.getAccountNumber().contentEquals(this.getAccountNumber());
    }
}