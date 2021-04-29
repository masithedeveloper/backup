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
package com.barclays.absa.banking.registration.services.dto;

import android.os.Build;

import com.barclays.absa.banking.boundary.model.RegisterProfileDetail;
import com.barclays.absa.banking.framework.ExtendedRequest;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.api.request.params.RequestParams;
import com.barclays.absa.banking.framework.api.request.params.TransactionParams;
import com.barclays.absa.crypto.SecureUtils;
import com.barclays.absa.utils.DeviceUtils;

import static com.barclays.absa.banking.framework.api.request.params.OpCodeParams.OP0991_REGISTRATION_RESULT;

public class ExtendedRegisterUserRequest<T> extends ExtendedRequest<T> {

    private final String accessAccount;
    private final String billingAccount;
    private final RegisterProfileDetail registerProfileDetail;
    private final String password;


    public ExtendedRegisterUserRequest(RegisterProfileDetail registerProfileDetail, String accessAccount, String billingAccount, String onlinePassword, ExtendedResponseListener<T> responseListener) {
        super(responseListener);
        this.accessAccount = accessAccount;
        this.billingAccount = billingAccount;
        this.registerProfileDetail = registerProfileDetail;
        password = onlinePassword;
        setMockResponseFile("registration/op0991_full_registration.json");
        printRequest();
    }

    @Override
    public RequestParams getRequestParams() {
        RequestParams.Builder requestParamsBuilder = new RequestParams.Builder()
                .put(OP0991_REGISTRATION_RESULT)
                .put(TransactionParams.Transaction.SERVICE_CHANNEL_IND, DeviceUtils.getChannelId())
                .put(TransactionParams.Transaction.SERVICE_ACCESS_ACCOUNT, accessAccount)
                .put(TransactionParams.Transaction.ACCOUNT_TO_CHARGE, billingAccount)
                .put(TransactionParams.Transaction.ATM_CARD_NO, registerProfileDetail.getAtmCardNo())
                .put(TransactionParams.Transaction.EMAIL, registerProfileDetail.getEmail() != null && !registerProfileDetail.getEmail().equalsIgnoreCase("null") ? registerProfileDetail.getEmail() : "")
                .put(TransactionParams.Transaction.TITLE, registerProfileDetail.getTitle() != null && !registerProfileDetail.getTitle().equalsIgnoreCase("null") ? registerProfileDetail.getTitle() : "")
                .put(TransactionParams.Transaction.USER_NUMBER, registerProfileDetail.getUserNumber())
                .put(TransactionParams.Transaction.SERVICE_SURNAME_CASHSEND, registerProfileDetail.getSurname())
                .put(TransactionParams.Transaction.SERVICE_FIRSTNAME_CASHSEND, registerProfileDetail.getFirstname())
                .put(TransactionParams.Transaction.RSA_ID_NUMBER, registerProfileDetail.getRsaIdNumber())
                .put(TransactionParams.Transaction.SERVICE_ACCESS_PIN, registerProfileDetail.getOnlinePin())
                .put(TransactionParams.Transaction.ATM_PIN, registerProfileDetail.getAtmPin())
                .put(TransactionParams.Transaction.CELLPHONE_NUMBER, registerProfileDetail.getCellPhoneNumberActual())
                .put(TransactionParams.Transaction.PASSWORD, password)
                .put(TransactionParams.Transaction.MANUFACTURER, Build.MANUFACTURER)
                .put(TransactionParams.Transaction.ENTERPRISE_SESSION_ID, registerProfileDetail.getEnterpriseSessionID())
                .put(TransactionParams.Transaction.DEVICE_SERIAL_NUMBER, SecureUtils.INSTANCE.getDeviceID())
                .put(TransactionParams.Transaction.SURE_CHECK_PASSED, registerProfileDetail.shouldShowPasswordScreen() ? "true" : "false");
        return requestParamsBuilder.build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getResponseClass() {
        return (Class<T>) RegisterAOLProfileResponse.class;
    }

    @Override
    public Boolean isEncrypted() {
        return false;
    }
}
