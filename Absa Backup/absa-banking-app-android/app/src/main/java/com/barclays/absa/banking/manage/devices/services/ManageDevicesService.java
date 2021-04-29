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
package com.barclays.absa.banking.manage.devices.services;

import com.barclays.absa.banking.boundary.model.ManageDeviceResult;
import com.barclays.absa.banking.boundary.model.PINObject;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.manage.devices.services.dto.ChangePrimaryDeviceResponse;
import com.barclays.absa.banking.manage.devices.services.dto.Device;
import com.barclays.absa.banking.manage.devices.services.dto.DeviceListResponse;
import com.barclays.absa.banking.manage.devices.services.dto.GenericValidationResponse;

public interface ManageDevicesService {
    String OP0816_VALIDATE_PRIMARY_DEVICE_PASSCODE_AND_ATM_PIN = "OP0816";
    String OP0818_CHANGE_PRIMARY_DEVICE = "OP0818";
    String OP0822_LIST_DEVICES = "OP0822";
    String OP0823_EDIT_NICKNAME_RESULT = "OP0823";
    String OP0824_DELINK_DEVICE = "OP0824";

    void validatePrimaryDevicePasscodeAndAtmPin(String primaryDevicePasscode, String atmCardNumber, PINObject atmPinBlock, ExtendedResponseListener<GenericValidationResponse> responseListener);
    void changePrimaryDevice (Device newPrimaryDevice, ExtendedResponseListener<ChangePrimaryDeviceResponse> responseListener);
    void changePrimaryDevice(String newPrimaryDevice, ExtendedResponseListener<ChangePrimaryDeviceResponse> responseListener);
    void validateSecurityCode (String securityCode, ExtendedResponseListener<GenericValidationResponse> responseListener);
    void delinkDevice(Device deviceDetails, ExtendedResponseListener<ManageDeviceResult> responseListener);
    void pullDeviceList(ExtendedResponseListener<DeviceListResponse> responseListener);
    void editDeviceNickname(Device deviceDetails, String deviceNickname, ExtendedResponseListener<ManageDeviceResult> responseListener);
    void delinkCurrentPrimaryDevice(String delinkReason, ExtendedResponseListener<ManageDeviceResult> delinkDeviceListener);
    void changePrimaryDevice(String deviceID, String requestId, String referenceNumber, ExtendedResponseListener<ChangePrimaryDeviceResponse> responseListener);
}