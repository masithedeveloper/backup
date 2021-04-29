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
package com.barclays.absa.banking.manage.devices.services.dto;

import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.crypto.SecureUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Device extends ResponseObject implements Serializable {

    @JsonProperty("imie")
    private String imei;
    @JsonProperty("liveProvingBuild")
    private boolean liveProvingBuild;
    @JsonProperty("model")
    private String model;
    @JsonProperty("manufacturer")
    private String manufacturer;
    @JsonProperty("nickname")
    private String nickname;
    @JsonProperty("status")
    private String authPending;
    @JsonProperty("secondFactorEnabled")
    private boolean secondFactorEnabled;
    @JsonProperty("aliasID")
    private String aliasID;
    @JsonProperty("primarySecondFactorDevice")
    private boolean primarySecondFactorDevice;
    @JsonProperty("serialnumber")
    private String serialNumber;

    public Device() {
        this.liveProvingBuild = false;
        this.imei = "";
        this.model = "";
        this.manufacturer = "";
        this.secondFactorEnabled = false;
        this.nickname = "";
        this.authPending = "";
        this.aliasID = "";
        this.primarySecondFactorDevice = false;
    }


    public static Device createDevice() {
        Device device = new Device();
        device.setImei(SecureUtils.INSTANCE.getDeviceID());
        return device;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isLiveProvingBuild() {
        return liveProvingBuild;
    }

    public void setLiveProvingBuild(boolean liveProvingBuild) {
        this.liveProvingBuild = liveProvingBuild;
    }

    public String getAuthPending() {
        return authPending;
    }

    public void setAuthPending(String authPending) {
        this.authPending = authPending;
    }

    public boolean isSecondFactorEnabled() {
        return secondFactorEnabled;
    }

    public void setSecondFactorEnabled(boolean secondFactorEnabled) {
        this.secondFactorEnabled = secondFactorEnabled;
    }

    public String getAliasID() {
        return aliasID;
    }

    public void setAliasID(String aliasID) {
        this.aliasID = aliasID;
    }

    public boolean isPrimarySecondFactorDevice() {
        return primarySecondFactorDevice;
    }

    public void setPrimarySecondFactorDevice(boolean primarySecondFactorDevice) {
        this.primarySecondFactorDevice = primarySecondFactorDevice;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

}