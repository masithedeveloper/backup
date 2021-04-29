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
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.ServiceClient;
import com.barclays.absa.banking.manage.devices.services.dto.ChangePrimaryDeviceRequest;
import com.barclays.absa.banking.manage.devices.services.dto.ChangePrimaryDeviceResponse;
import com.barclays.absa.banking.manage.devices.services.dto.DelinkDeviceRequest;
import com.barclays.absa.banking.manage.devices.services.dto.Device;
import com.barclays.absa.banking.manage.devices.services.dto.DeviceListRequest;
import com.barclays.absa.banking.manage.devices.services.dto.DeviceListResponse;
import com.barclays.absa.banking.manage.devices.services.dto.GenericValidationResponse;
import com.barclays.absa.banking.manage.devices.services.dto.UpdateDeviceRequest;
import com.barclays.absa.banking.manage.devices.services.dto.ValidatePrimaryDevicePasscodeAndAtmPinRequest;
import com.barclays.absa.banking.manage.devices.services.dto.ValidateSecurityCodeRequest;

public class ManageDevicesInteractor implements ManageDevicesService {

    @Override
    public void delinkDevice(Device deviceDetails, ExtendedResponseListener<ManageDeviceResult> responseListener) {
        DelinkDeviceRequest<ManageDeviceResult> delinkDeviceRequest = new DelinkDeviceRequest<>(deviceDetails, responseListener);
        sendRequest(delinkDeviceRequest);
    }

    @Override
    public void pullDeviceList(ExtendedResponseListener<DeviceListResponse> responseListener) {
        DeviceListRequest<DeviceListResponse> deviceListRequest = new DeviceListRequest<>(responseListener);
        sendRequest(deviceListRequest);
    }

    @Override
    public void editDeviceNickname(Device deviceDetails, String deviceNickname, ExtendedResponseListener<ManageDeviceResult> responseListener) {
        UpdateDeviceRequest<ManageDeviceResult> editNicknameRequest = new UpdateDeviceRequest<>(deviceDetails, deviceNickname, responseListener);
        sendRequest(editNicknameRequest);
    }

    @Override
    public void validatePrimaryDevicePasscodeAndAtmPin(String primaryDevicePasscode, String atmCardNumber, PINObject atmPinBlock, ExtendedResponseListener<GenericValidationResponse> responseListener) {
        ValidatePrimaryDevicePasscodeAndAtmPinRequest<GenericValidationResponse> validatePinAndPasswordRequest = new ValidatePrimaryDevicePasscodeAndAtmPinRequest<>(primaryDevicePasscode, atmCardNumber, atmPinBlock, responseListener);
        sendRequest(validatePinAndPasswordRequest);
    }

    @Override
    public void changePrimaryDevice(Device newPrimaryDevice, ExtendedResponseListener<ChangePrimaryDeviceResponse> responseListener) {
        ChangePrimaryDeviceRequest<ChangePrimaryDeviceResponse> changePrimaryDeviceRequest = new ChangePrimaryDeviceRequest<>(newPrimaryDevice, responseListener);
        sendRequest(changePrimaryDeviceRequest);
    }

    @Override
    public void changePrimaryDevice(String newPrimaryDevice, ExtendedResponseListener<ChangePrimaryDeviceResponse> responseListener) {
        ChangePrimaryDeviceRequest<ChangePrimaryDeviceResponse> changePrimaryDeviceRequest = new ChangePrimaryDeviceRequest<>(newPrimaryDevice, responseListener);
        sendRequest(changePrimaryDeviceRequest);
    }

    @Override
    public void validateSecurityCode(String securityCode, ExtendedResponseListener<GenericValidationResponse> responseListener) {
        ValidateSecurityCodeRequest<GenericValidationResponse> validateSecurityCodeRequest = new ValidateSecurityCodeRequest<>(securityCode, responseListener);
        sendRequest(validateSecurityCodeRequest);
    }

    @Override
    public void delinkCurrentPrimaryDevice(String delinkReason, ExtendedResponseListener<ManageDeviceResult> delinkDeviceListener) {
        DelinkDeviceRequest<ManageDeviceResult> delinkDeviceRequest = new DelinkDeviceRequest<>(delinkReason, delinkDeviceListener);
        sendRequest(delinkDeviceRequest);
    }

    @Override
    public void changePrimaryDevice(String deviceID, String requestId, String referenceNumber, ExtendedResponseListener<ChangePrimaryDeviceResponse> responseListener) {
        ChangePrimaryDeviceRequest<ChangePrimaryDeviceResponse> changePrimaryDeviceRequest = new ChangePrimaryDeviceRequest<>(deviceID, requestId, referenceNumber, responseListener);
        sendRequest(changePrimaryDeviceRequest);
    }

    private void sendRequest(ExtendedRequest delinkDeviceRequest) {
        ServiceClient serviceClient = new ServiceClient(delinkDeviceRequest);
        serviceClient.submitRequest();
    }
}