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

import com.barclays.absa.banking.boundary.shared.dto.TransactionVerificationType;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;

import static com.barclays.absa.banking.manage.devices.services.ManageDevicesService.OP0818_CHANGE_PRIMARY_DEVICE;

public class ChangePrimaryDeviceRequest<T> extends ExtendedRequest<T> {
    private static final String ID_AND_V_CHANGE_PRIMARY_FROM_MANAGE_DEVICES = "42";
    private static final String ID_AND_V_CHANGE_PRIMARY_FROM_TRANSACTION = "43";
    private static final String ID_AND_V_CHANGE_PRIMARY_WHILE_LINKING = "44";
    private Device newPrimaryDevice;
    private String deviceImeiValue;
    private String requestId;
    private String referenceNumber;
    private boolean isDeviceAvailable;

    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    public ChangePrimaryDeviceRequest(Device device, ExtendedResponseListener<T> responseListener) {
        super(responseListener);
        newPrimaryDevice = device;
        isDeviceAvailable = true;
        setMockResponseFile("alias/op0818_change_primary_device.json");
        printRequest();
    }

    public ChangePrimaryDeviceRequest(String deviceImei, ExtendedResponseListener<T> responseListener) {
        super(responseListener);
        deviceImeiValue = deviceImei;
        setMockResponseFile("alias/op0818_change_primary_device.json");
        isDeviceAvailable = false;
        printRequest();
    }

    public ChangePrimaryDeviceRequest(String deviceImei, String requestId, String referenceNumber, ExtendedResponseListener<T> responseListener) {
        super(responseListener);
        deviceImeiValue = deviceImei;
        this.requestId = requestId;
        this.referenceNumber = referenceNumber;
        setMockResponseFile("alias/op0818_change_primary_device.json");
        isDeviceAvailable = false;
        printRequest();
    }

    @Override
    public RequestParams getRequestParams() {
        final String deviceId;
        if (isDeviceAvailable && newPrimaryDevice != null) {
            deviceId = newPrimaryDevice.getImei() != null ? newPrimaryDevice.getImei() : newPrimaryDevice.getSerialNumber();
        } else {
            deviceId = deviceImeiValue;
        }

        RequestParams.Builder requestParams = new RequestParams.Builder()
                .put(OP0818_CHANGE_PRIMARY_DEVICE)
                .put(TransactionParams.Transaction.NEW_PRIMARY_DEVICE_ID, deviceId);

        if (appCacheService.isChangePrimaryDeviceFlow()) {
            requestParams.put("transactionType", ID_AND_V_CHANGE_PRIMARY_FROM_MANAGE_DEVICES);
        } else if (appCacheService.isChangePrimaryDeviceFlowFromSureCheck() || appCacheService.isChangePrimaryDeviceFromNoPrimaryDeviceScreen() || appCacheService.isIdentityAndVerificationPostLogin() || appCacheService.hasPrimaryDevice()) {
            requestParams.put("transactionType", ID_AND_V_CHANGE_PRIMARY_FROM_TRANSACTION);
        } else if (TransactionVerificationType.SURECHECKV2.getKey().equals(appCacheService.getOriginalSureCheckType()) && appCacheService.isIdentificationAndVerificationFlow()) {
            requestParams.put("transactionType", ID_AND_V_CHANGE_PRIMARY_WHILE_LINKING);
        }

        if (TransactionVerificationType.SURECHECKV2.getKey().equals(appCacheService.getOriginalSureCheckType()) && appCacheService.isIdentificationAndVerificationFlow()) {
            requestParams.put("referenceNumber", referenceNumber);
            requestParams.put("requestID", requestId);
        }

        if (!appCacheService.isIdentityAndVerificationPostLogin() && appCacheService.isChangePrimaryDeviceFlow()) {
            requestParams.put("enterpriseSessionID", appCacheService.getEnterpriseSessionId());
        }
        return requestParams.build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getResponseClass() {
        return (Class<T>) ChangePrimaryDeviceResponse.class;
    }

    @Override
    public Boolean isEncrypted() {
        return true;
    }
}