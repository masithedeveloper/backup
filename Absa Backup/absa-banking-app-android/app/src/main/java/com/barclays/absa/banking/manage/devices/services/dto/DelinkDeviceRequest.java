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

import com.barclays.absa.banking.boundary.model.ManageDeviceResult;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.services.contracts.IAppCacheService;

import static com.barclays.absa.banking.manage.devices.services.ManageDevicesService.OP0824_DELINK_DEVICE;

public class DelinkDeviceRequest<T> extends ExtendedRequest<T> {
    private final Device deviceDetails;
    private String primaryDeviceDelinkReason;

    private final IAppCacheService appCacheService = DaggerHelperKt.getServiceInterface(IAppCacheService.class);

    public DelinkDeviceRequest(Device deviceDetails, ExtendedResponseListener<T> responseListener) {
        super(responseListener);
        this.deviceDetails = deviceDetails;
        primaryDeviceDelinkReason = null;
        setMockResponseFile("manage_devices/op0966_device_delink.json");
    }

    public DelinkDeviceRequest(String delinkReason, ExtendedResponseListener<T> responseListener) {
        super(responseListener);
        deviceDetails = null;
        primaryDeviceDelinkReason = delinkReason;
        setMockResponseFile("manage_devices/op0966_device_delink.json");
    }

    @Override
    public RequestParams getRequestParams() {
        final String DELINK_ACTION_TYPE = "D";
        final String DELINK_TYPE = "delinkDevice";

        String deviceIdentifier;
        if (deviceDetails != null) {
            deviceIdentifier = deviceDetails.getSerialNumber() != null ?
                    deviceDetails.getSerialNumber() : deviceDetails.getImei();
        } else {
            deviceIdentifier = "PRIMARY_DEVICE";
        }

        RequestParams.Builder builder = new RequestParams.Builder()
                .put(OP0824_DELINK_DEVICE)
                .put("imie", deviceIdentifier)
                .put(TransactionParams.Transaction.DECOUPLE_ACTION_TYPE, DELINK_ACTION_TYPE)
                .put(TransactionParams.Transaction.DECOUPLE_TYPE, DELINK_TYPE);
        if (deviceDetails != null) {
            builder.put(TransactionParams.Transaction.MANUFACTURER, deviceDetails.getManufacturer());
            builder.put(TransactionParams.Transaction.DEVICE_NICKNAME, deviceDetails.getNickname());
        } else {
            builder.put(TransactionParams.Transaction.MANUFACTURER, "");
            builder.put(TransactionParams.Transaction.DEVICE_NICKNAME, "");
            primaryDeviceDelinkReason = deriveDelinkReason();
            builder.put("reason", primaryDeviceDelinkReason);
        }

        if (!appCacheService.shouldRevertToOldLinkingFlow() && !appCacheService.isIdentityAndVerificationPostLogin()) {
            builder.put("enterpriseSessionID", appCacheService.getEnterpriseSessionId());
        }
        return builder.build();
    }

    private String deriveDelinkReason() {
        switch (primaryDeviceDelinkReason) {
            case "lost":
                return "LOST_DEVICE";
            case "stolen":
                return "STOLEN_DEVICE";
            case "broken":
                return "BROKEN_DEVICE";
            case "old":
                return "NEW_DEVICE";
            case "deleted app":
            default:
                return "DELETED_MOBILE_APP";
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getResponseClass() {
        return (Class<T>) ManageDeviceResult.class;
    }

    @Override
    public Boolean isEncrypted() {
        return true;
    }
}