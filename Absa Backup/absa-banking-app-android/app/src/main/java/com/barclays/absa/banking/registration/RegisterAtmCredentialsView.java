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

package com.barclays.absa.banking.registration;

import com.barclays.absa.banking.boundary.model.RegisterProfileDetail;
import com.barclays.absa.banking.framework.BaseView;

interface RegisterAtmCredentialsView extends BaseView {
    void onInvalidCardNumber(boolean isInvalidLength);

    void onInvalidPin(boolean isInvalidLength);

    void showAlreadyRegisterDialog();

    void showCardNumberAndPinFailureDialog(RegisterProfileDetail registerProfileDetailObj);

    void onMobileRecordNotFound(RegisterProfileDetail registerProfileDetailObj);

    void goToConfirmContactDetailScreen(RegisterProfileDetail registerProfileDetailObj);

    void showInvalidCardNumberDialog(String failureMessage);

    void showErrorDialog(String error);

    void showSolePropErrorMessage();

    void showBusinessBankingProfileErrorMessage();
}
