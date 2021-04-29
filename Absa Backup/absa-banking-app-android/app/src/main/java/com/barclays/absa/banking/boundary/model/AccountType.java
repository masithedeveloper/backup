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

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

public class AccountType implements Parcelable {
    private double accountNo;
    private String name;
    private String backendAccountType;

    public AccountType() {

    }

    protected AccountType(Parcel in) {
        accountNo = in.readDouble();
        name = in.readString();
        backendAccountType = in.readString();
    }

    public static final Creator<AccountType> CREATOR = new Creator<AccountType>() {
        @Override
        public AccountType createFromParcel(Parcel in) {
            return new AccountType(in);
        }

        @Override
        public AccountType[] newArray(int size) {
            return new AccountType[size];
        }
    };

    public double getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(double accountNo) {
        this.accountNo = accountNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackendAccountType() {
        return backendAccountType;
    }

    public void setBackendAccountType(String backendAccountType) {
        this.backendAccountType = backendAccountType;
    }

    @NotNull
    @Override
    public String toString() {
        return "AccountType{" +
                "accountNo=" + accountNo +
                ", name='" + name + '\'' +
                ", backendAccountType='" + backendAccountType +
                "'}'";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(accountNo);
        dest.writeString(name);
        dest.writeString(backendAccountType);
    }
}
