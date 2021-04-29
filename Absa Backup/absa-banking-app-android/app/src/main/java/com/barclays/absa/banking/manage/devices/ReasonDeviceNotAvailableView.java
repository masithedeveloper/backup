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

package com.barclays.absa.banking.manage.devices;

import com.barclays.absa.banking.framework.BaseView;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.manage.devices.services.dto.Device;

interface ReasonDeviceNotAvailableView extends BaseView {

    void navigateToDelinkPrimaryDeviceConfirmationScreen(Device replacementDevice, int delinkReason, String fillerText);

    void navigateToSecurityCodeActivity();

    String[] fillReasonsDeviceNotAvailable();

    void onPrimaryDeviceChanged(ResponseObject response);

    void onReasonSelected(int reasonPosition);

    void showSecurityCodeRequiredScreen();
}
