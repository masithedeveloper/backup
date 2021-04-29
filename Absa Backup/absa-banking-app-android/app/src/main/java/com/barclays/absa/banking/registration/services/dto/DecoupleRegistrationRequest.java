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

public class DecoupleRegistrationRequest<T> extends ExtendedRequest<T> {

    private final RegisterProfileDetail registrationProfile;

    public DecoupleRegistrationRequest(RegisterProfileDetail registrationProfileDetail, ExtendedResponseListener<T> responseListener) {
        super(responseListener);
        registrationProfile = registrationProfileDetail;
        setMockResponseFile("registration/op0991_full_registration.json");
        printRequest();
    }

    @Override
    public RequestParams getRequestParams() {
        assert registrationProfile.getSelectedAccessAccountNo() != null;
        String[] accessAccount = registrationProfile.getSelectedAccessAccountNo().split("\n");
        assert registrationProfile.getSelectedBillingAccountNo() != null;
        String[] billingAccount = registrationProfile.getSelectedBillingAccountNo().split("\n");
        RequestParams.Builder requestParamsBuilder = new RequestParams.Builder()
                .put(OP0991_REGISTRATION_RESULT)
                .put(TransactionParams.Transaction.SERVICE_CHANNEL_IND, DeviceUtils.getChannelId())
                .put(TransactionParams.Transaction.SERVICE_ACCESS_ACCOUNT, accessAccount[1])
                .put(TransactionParams.Transaction.ACCOUNT_TO_CHARGE, billingAccount[1])
                .put(TransactionParams.Transaction.ATM_CARD_NO, registrationProfile.getAtmCardNo())
                .put(TransactionParams.Transaction.EMAIL, registrationProfile.getEmail() != null && !registrationProfile.getEmail().equalsIgnoreCase("null") ? registrationProfile.getEmail() : "")
                .put(TransactionParams.Transaction.TITLE, registrationProfile.getTitle() != null && !registrationProfile.getTitle().equalsIgnoreCase("null") ? registrationProfile.getTitle() : "")
                .put(TransactionParams.Transaction.USER_NUMBER, registrationProfile.getUserNumber())
                .put(TransactionParams.Transaction.SERVICE_SURNAME_CASHSEND, registrationProfile.getSurname())
                .put(TransactionParams.Transaction.SERVICE_FIRSTNAME_CASHSEND, registrationProfile.getFirstname())
                .put(TransactionParams.Transaction.RSA_ID_NUMBER, registrationProfile.getRsaIdNumber())
                .put(TransactionParams.Transaction.DECOUPLE_REGISTRATION_ATM_PIN, registrationProfile.getOnlinePin())
                .put(TransactionParams.Transaction.ATM_PIN, registrationProfile.getAtmPin())
                .put(TransactionParams.Transaction.CELLPHONE_NUMBER, registrationProfile.getCellPhoneNumberActual())
                .put(TransactionParams.Transaction.PASSWORD, "")
                .put(TransactionParams.Transaction.MANUFACTURER, Build.MANUFACTURER)
                .put(TransactionParams.Transaction.SESSION_ID, registrationProfile.getEnterpriseSessionID())
                .put(TransactionParams.Transaction.DEVICE_SERIAL_NUMBER, SecureUtils.INSTANCE.getDeviceID())
                .put(TransactionParams.Transaction.SURE_CHECK_PASSED, registrationProfile.shouldShowPasswordScreen() ? "true" : "false");

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