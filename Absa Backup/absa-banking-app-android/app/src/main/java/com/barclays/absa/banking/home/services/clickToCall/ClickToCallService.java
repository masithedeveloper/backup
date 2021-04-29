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

package com.barclays.absa.banking.home.services.clickToCall;

import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.home.services.clickToCall.dto.CallBackResponse;
import com.barclays.absa.banking.home.services.clickToCall.dto.CallBackVerificationDataModel;

public interface ClickToCallService {
    String OP2049_REQUEST_CALLBACK_OPCODE = "OP2049";
    String OP2050_VERIFY_SECRET_CODE_OPCODE = "OP2050";

    String SECRET_CODE = "secretCode";
    String SECRET_CODE_VERIFIED = "secretCodeVerified";
    String UNIQUE_REFERENCE_NUMBER = "uniqueRefNo";
    String CALL_BACK_DATE = "callBackDateTime";

    void requestCallBack(String secretCode, String callBackDateTime, ExtendedResponseListener<CallBackResponse> callBackRequestExtendedResponseListener);
    void verifySecretCode(CallBackVerificationDataModel callBackVerificationDataModel, ExtendedResponseListener<CallBackResponse> callBackRequestExtendedResponseListener);
}