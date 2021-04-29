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

import android.util.Base64;

import com.barclays.absa.banking.BuildConfig;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.registration.services.RegistrationInteractor;
import com.barclays.absa.banking.registration.services.dto.TermsAndConditionObject;

class RegisterPersonalDetailsPresenter {
    private RegisterPersonalDetailsView view;
    private ExtendedResponseListener<TermsAndConditionObject> responseListener = new ExtendedResponseListener<TermsAndConditionObject>() {

        @Override
        public void onSuccess(final TermsAndConditionObject responseModel) {
            view.dismissProgressDialog();
            try {
                if (responseModel.getClientAgreementDoc() != null)
                    view.launchTermsAndConditionReader(Base64.decode(responseModel.getClientAgreementDoc(), Base64.URL_SAFE));
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
        }
    };

    RegisterPersonalDetailsPresenter(RegisterPersonalDetailsView personalDetailsView) {
        this.view = personalDetailsView;
        responseListener.setView(personalDetailsView);
    }

    public void onTermsOfUseClicked(String clientType) {
        RegistrationInteractor interactor = new RegistrationInteractor();
        interactor.fetchTermsOfUserDetails(clientType, responseListener);
    }
}