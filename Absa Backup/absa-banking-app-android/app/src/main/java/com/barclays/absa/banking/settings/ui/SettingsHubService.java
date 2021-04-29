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

package com.barclays.absa.banking.settings.ui;

import android.graphics.Bitmap;

import com.barclays.absa.banking.boundary.model.ProfileSetupResult;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.framework.ExtendedResponseListener;

public interface SettingsHubService {

    String OP0202_SHP_LANGUAGE_CHANGE = "OP0202";

    String SERVICE_LANGUAGE_CODE = "langCode";
    String IS_CUST_ACTS_REQ = "isCustActsReq";

    void requestLanguageUpdate(char languageCode, ExtendedResponseListener<SecureHomePageObject> responseListener);

    void requestProfileSetup(boolean isPhotoRemove,
                             boolean isUpdateProfile,
                             Bitmap profileBitmap,
                             String languageCode,
                             String setupProfileName,
                             String backgroundImageId,
                             ExtendedResponseListener<ProfileSetupResult> extendedResponseListener);
}