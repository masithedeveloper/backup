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

import com.barclays.absa.banking.framework.BaseView;
import com.barclays.absa.banking.framework.data.ResponseObject;

interface RegisterCreatePasswordView extends BaseView {

    void launchRegistrationResultScreen(ResponseObject responseObject);

    void markAllPasswordRulesInvalid();

    void markSequenceValidationRule(boolean isValid);

    void markNameOfUserValidationRule(boolean isValid);

    void markSpacesAndSpecialCharactersValidationRule(boolean isValid);

    void markLengthValidationRule(boolean isValid);

    void markAlphanumericValidationRule(boolean isValid);

    void requestDeviceStatePermissions();

    void showInvalidPasswordMessage();

    void showPasswordsDoNotMatch();

    void goToCreateNicknameScreen();

    void goToHomeScreen();

    void goToLoginScreen();

    void returnValidity(boolean isPasswordValid);
}
